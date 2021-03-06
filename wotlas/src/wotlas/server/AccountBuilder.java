/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package wotlas.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import wotlas.common.character.AesSedai;
import wotlas.common.character.Ashaman;
import wotlas.common.character.DarkOne;
import wotlas.common.character.Human;
import wotlas.common.character.Warder;
import wotlas.common.character.WotCharacter;
import wotlas.common.message.account.AccountCreationEndedMessage;
import wotlas.common.message.account.AccountStepMessage;
import wotlas.common.message.account.StepErrorMessage;
import wotlas.common.objects.inventories.Inventory;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.wizard.JWizardStepParameters;
import wotlas.utils.Debug;
import wotlas.utils.Tools;

/** An AccountBuilder helps the creation of a GameAccount for a client. Here is
 *  how it works :<p><br>
 *
 *  1 - The client connects to the AccountServer.<br>
 *
 *  2 - The AccountServer creates a new AccountBuilder and sets it as the client's
 *      NetMessage context.<br>
 *
 *  3 - The client discusses with the AccountBuilder to build his GameAccount and
 *      his PlayerImpl.<br>
 *
 *  4 - When the account is ready it is saved to disk via the PersistenceManager
 *      and added to the current running game.<br>
 *
 *  5 - The client connection is then closed and the AccountBuilder handled to the
 *      garbage collector. The client can now connect to the GameServer.<br><p>
 *
 *
 * If the creation is successful, the accountServer sends a AccountCreationEndedMessage
 * containing the player's IDs.
 *
 * @author Aldiss
 * @see wotlas.server.AccountServer
 */

public class AccountBuilder implements NetConnectionListener {
    /*------------------------------------------------------------------------------------*/

    /** The Game Account we are building
     */
    private GameAccount account;

    /** The Player Data associated to this GameAccount
     */
    private PlayerImpl player;

    /** Connection of our client.
     */
    private NetConnection connection;

    /** Our Account Server
     */
    private AccountServer accountServer;

    /** Our current step.
     */
    private JWizardStepParameters currentParameters;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public AccountBuilder(AccountServer accountServer) {
        this.accountServer = accountServer;
        this.currentParameters = null;

        // the account is empty for now...
        this.account = new GameAccount();
        this.player = new PlayerImpl();
        this.player.setDefaultPlayerLocation();
    }

    /*------------------------------------------------------------------------------------*/

    /** Method called when the connection with the client is established.
     *
     * @param connection 
     */
    public void connectionCreated(NetConnection connection) {
        this.connection = connection;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called when the connection with the client is established.
     *
     * @param connection 
     */
    public void connectionClosed(NetConnection connection) {
        // clean-up
        connection = null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Called to start the account build.
     */
    public void startFirstStep() {
        if (this.currentParameters != null)
            return; // can only call this method once

        this.currentParameters = this.accountServer.getStepFactory().getStep(AccountStepFactory.FIRST_STEP);

        JWizardStepParameters clientParams = this.currentParameters.getCopyWithNoServerProps();
        personalizeParameters(clientParams, this.currentParameters);

        this.connection.queueMessage(new AccountStepMessage(clientParams));
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To return to the previous step.
     */
    public void returnToPreviousStep() {

        // A - Do we have a previous step to call ?
        if (!this.currentParameters.getIsPrevButtonEnabled()) {
            sendStepError("Previous command is not enabled for this step !!");
            return;
        }

        String previous = this.currentParameters.getProperty("server.previous");

        if (previous == null) {
            sendStepError("Previous step not found !");
            return;
        }

        // B - We load the previous step and send it to the client
        this.currentParameters = this.accountServer.getStepFactory().getStep(previous);

        if (this.currentParameters == null) {
            sendStepError("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #stpNotFnd)");
            return;
        }

        JWizardStepParameters clientParams = this.currentParameters.getCopyWithNoServerProps();
        personalizeParameters(clientParams, this.currentParameters);

        this.connection.queueMessage(new AccountStepMessage(clientParams));
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To parse the result data and move to the next step.
     */
    public void setStepResultData(JWizardStepParameters resultParameters) {

        try {

            // A - we retrieve the data properties
            String resultPropsKey[] = resultParameters.getStepPropertiesKey();
            if (resultPropsKey == null)
                resultPropsKey = new String[0];

            // B - we call the associated methods of the data properties
            String next = null;

            for (int i = 0; i < resultPropsKey.length; i++)
                if (resultPropsKey[i].startsWith("data.")) {
                    // 1 - we get the suffix
                    String suffix = resultPropsKey[i].substring(resultPropsKey[i].indexOf('.') + 1, resultPropsKey[i].length());
                    // 2 - we get the data
                    String data = resultParameters.getStepPropertiesValue()[i];

                    if (suffix.equals("choice")) {
                        int ind = -1;

                        try {
                            ind = Integer.parseInt(data);
                        } catch (Exception ex) {
                            sendStepError("Selection not valid !");
                            return;
                        }

                        suffix = "choice" + ind;
                        data = this.currentParameters.getProperty("init." + suffix);

                        if (data == null) {
                            sendStepError("Selection not valid !");
                            return;
                        }
                    }

                    // 3 - Check for a method to call on this data
                    String method = this.currentParameters.getProperty("server." + suffix + ".method");

                    if (method != null && !invokeMethod(method, data))
                        return;

                    // 4 - Check for default method to call on this data
                    method = this.currentParameters.getProperty("server.method");

                    if (method != null && !invokeMethod(method, data))
                        return;

                    // 5 - Check for link "next" step
                    next = this.currentParameters.getProperty("server." + suffix + ".next");
                }

            // C - Do we have a next step to call ?
            if (next == null && !this.currentParameters.getIsLastStep()) {
                // we search for default
                next = this.currentParameters.getProperty("server.next");

                if (next == null) {
                    sendStepError("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #nexStpNon)");
                    return;
                }
            } else if (this.currentParameters.getIsLastStep()) {
                // we create the account and return
                try {
                    createAccount();
                } catch (Exception ex) {
                    sendStepError("Failed to create account : " + ex.getMessage());
                }
                return;
            }

            // D - We load the next step and send it to the client
            this.currentParameters = this.accountServer.getStepFactory().getStep(next);

            if (this.currentParameters == null) {
                sendStepError("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #nexStpFai)");
                return;
            }

            JWizardStepParameters clientParams = this.currentParameters.getCopyWithNoServerProps();
            personalizeParameters(clientParams, this.currentParameters);

            this.connection.queueMessage(new AccountStepMessage(clientParams));
        } catch (Exception ex2) {
            Debug.signal(Debug.ERROR, this, ex2);
            sendStepError("Internal Error : " + ex2);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To invoke a method of the 'void XXXX(String data)' type.
     *  @return true if the method call succeeded, false if it failed and an error msg was
     *  sent.
     */
    private boolean invokeMethod(String method, String data) {
        try {
            Class cparams[] = new Class[1];
            cparams[0] = String.class;
            Method m = getClass().getMethod(method, cparams);

            if (m == null) {
                sendStepError("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #metNofou)");
                return false;
            }

            Object params[] = new Object[1];
            params[0] = data;
            m.invoke(this, params);
            return true;
        } catch (InvocationTargetException ite) {
            sendStepError("Error : " + ite.getTargetException().getMessage());
            return false;
        } catch (Exception ex) {
            Debug.signal(Debug.ERROR, this, ex);
            sendStepError("Internal Error : (" + ex.getMessage() + ") Please report the bug.");
            return false;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To invoke a method of the 'String XXXX()' type.
     *  @return a String if the method call succeeded, null if it fails. In case of
     *  errors no error message is sent.
     */
    private String invokeMethod(String method) {
        try {
            Class cparams[] = new Class[0];
            Method m = getClass().getMethod(method, cparams);

            if (m == null)
                return null; // method not found

            Object params[] = new Object[0];
            return (String) m.invoke(this, params);
        } catch (InvocationTargetException ite) {
            Debug.signal(Debug.ERROR, this, ite.getTargetException().getMessage());
            return null;
        } catch (Exception ex) {
            Debug.signal(Debug.ERROR, this, ex);
            return null;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To personalize eventual init properties ( we replace $PATTERN$ if there are any
     *  declared) of parameters that are going to be sent to a client.
     *  @param clientParameters params to personalize
     *  @param serverParameters server params to use to personalize client params.
     */
    void personalizeParameters(JWizardStepParameters clientParameters, JWizardStepParameters serverParameters) {

        // A - we retrieve the keys
        String propsKey[] = serverParameters.getStepPropertiesKey();
        if (propsKey == null)
            return; // none

        // B - we personalize the init properties
        for (int i = 0; i < propsKey.length; i++)
            if (propsKey[i].startsWith("server.") && propsKey[i].endsWith("$")) {
                // 1 - we get the suffix & pattern
                String suffix = propsKey[i].substring(propsKey[i].indexOf('.') + 1, propsKey[i].indexOf('$') - 1);
                String pattern = propsKey[i].substring(propsKey[i].indexOf('$'), propsKey[i].length());

                // 2 - we get the methodName & text to analyze
                String methodName = serverParameters.getStepPropertiesValue()[i];

                String text = clientParameters.getProperty("init." + suffix);

                // 3 - Check what we have
                if (pattern.length() == 0 || text == null || methodName.length() == 0)
                    continue; // ignore this bad entry

                // 4 - Proceed...
                int ind = text.indexOf(pattern);

                if (ind < 0)
                    continue; // pattern not found

                StringBuffer buf = new StringBuffer(text.substring(0, ind));

                String result = invokeMethod(methodName);
                if (result == null)
                    result = "#ERROR#";

                buf.append(result);
                buf.append(text.substring(ind + pattern.length(), text.length()));

                // 5 - Save our modif
                clientParameters.setProperty("init." + suffix, buf.toString());
            }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** A small method to report a step error.
     */
    private void sendStepError(String message) {
        this.connection.queueMessage(new StepErrorMessage(message));
        Debug.signal(Debug.ERROR, this, "An error occured during account creation : " + message);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To cancel the account's creation.
     */
    public void cancelCreation() {
        Debug.signal(Debug.NOTICE, this, "Account Creation cancelled...");
        this.connection.close();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to create the account
     *  This method sends back to the client a AccountCreationEnded message on success.
     */
    public void createAccount() throws AccountException {

        AccountManager accountManager = ServerDirector.getDataManager().getAccountManager();

        // 1 - We finalize inits
        this.account.setPlayer(this.player);
        this.account.setLocalClientID(this.accountServer.getNewLocalClientID());
        this.account.setOriginalServerID(ServerDirector.getServerID());
        this.account.setLastConnectionTimeNow();
        this.player.setPrimaryKey(this.account.getAccountName());

        Inventory inventory = this.player.getWotCharacter().createInventory();
        ServerObjectManager objectManager = new ServerObjectManager();
        objectManager.setInventory(inventory);
        this.player.setObjectManager(objectManager);

        // 2 - We add the account to the game server
        if (accountManager.checkAccountName(this.account.getAccountName()))
            throw new AccountException("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #dupAcID)");

        if (accountManager.createAccount(this.account)) {
            // we add the player to the world...
            this.player.init();
            Debug.signal(Debug.NOTICE, this, "Client account created... (" + this.player.getPrimaryKey() + ").");

            // we send a Success Message
            this.connection.queueMessage(new AccountCreationEndedMessage(this.account.getLocalClientID(), this.account.getOriginalServerID(), this.account.getLogin(), this.account.getPassword(), this.player.getFullPlayerName()));
            // And close the connection
            this.connection.close();
        } else {
            // Account not created for some reason
            // we announce the bad news to the client
            // but we don't close the connection...
            throw new AccountException("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #creFaiDisk)");
        }
    }

    /*------------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------------*/

    /***
     ***  METHODS THAT CAN BE INVOKED DYNAMICALY
     ***
     ***  Their prototype must be : public void setXXXX( String data ) throws AccountException
     ***
     ***/

    /** Method to set the player's login.
     */
    public void setLogin(String data) throws AccountException {
        this.account.setLogin(data);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method to set the player's password.
     */
    public void setPassword(String data) throws AccountException {
        this.account.setPassword(data);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to set the WotCharacterClass
     */
    public void setWotCharacterClass(String data) throws AccountException {

        // 1 - Select Class
        String className = "wotlas.common.character.";

        if (data.equals("Aes Sedai"))
            className += "AesSedai";
        else if (data.equals("Warder"))
            className += "Warder";
        else if (data.equals("Children of the Light"))
            className += "ChildrenOfTheLight";
        else if (data.equals("Wolf Brother"))
            className += "WolfBrother";
        else if (data.equals("Asha'man"))
            className += "Ashaman";
        else if (data.equals("Aiel"))
            className += "AielWarrior";
        else if (data.equals("Darkfriend"))
            return;
        else if (data.equals("Special Characters"))
            return;
        else
            throw new AccountException("Unknown character class !");

        // 2 - Create Instance
        Object obj = Tools.getInstance(className);

        if (obj == null || !(obj instanceof WotCharacter))
            throw new AccountException("Error during character class creation !");

        // 3 - Set the player's character
        WotCharacter wotCharacter = (WotCharacter) obj;
        this.player.setWotCharacter(wotCharacter);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to set the WotCharacterClass rank.
     */
    public void setWotCharacterRank(String data) throws AccountException {

        // 1 - Set the rank
        WotCharacter wotCharacter = this.player.getWotCharacter();

        if (wotCharacter == null)
            throw new AccountException("No character created !");

        wotCharacter.setCharacterRank(data);

        // 2 - check that it was set        
        if (!data.equals(wotCharacter.getCharacterRank()))
            throw new AccountException("Unknown rank for this character class !");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to set the player hair color. (for humans only).
     */
    public void setHairColor(String data) throws AccountException {

        // 1 - Get Human character
        WotCharacter wotCharacter = this.player.getWotCharacter();

        if (wotCharacter == null)
            throw new AccountException("No character created !");

        if (!(wotCharacter instanceof Human))
            throw new AccountException("Your character is not Human !");

        Human human = (Human) wotCharacter;
        human.setHairColor(data);

        // 2 - check that it was set        
        if (!data.equals(human.getHairColor()))
            throw new AccountException("Unknown hair color !");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to set the player's name.
     */
    public void setPlayerName(String data) throws AccountException {
        data = data.trim();

        if (data.length() > 30)
            throw new AccountException("Your nickname should have less than 30 letters !");
        this.player.setPlayerName(data);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to set the player's full name.
     */
    public void setFullPlayerName(String data) throws AccountException {
        data = data.trim();

        if (data.length() > 30)
            throw new AccountException("Your full name should have less than 30 letters !");

        if (data.length() < 5)
            throw new AccountException("Your full name should have more than 4 letters !");

        this.player.setFullPlayerName(data);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to set the player's email.
     */
    public void setEmail(String data) throws AccountException {
        this.account.setEmail(data);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to set the player's past.
     */
    public void setPlayerPast(String data) throws AccountException {
        this.player.setPlayerPast(data);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to set the player's past option.
     */
    public void setPlayerPastOption(String data) throws AccountException {
        if (data.equals("true"))
            this.player.setPlayerPast(""); // past will be set later
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to create special characters
     */
    public void setSpecialCharacter(String data) throws AccountException {

        Properties props = ServerDirector.getServerProperties();

        if (data.equals(props.getProperty("key.shaitan", "shaitan"))) {
            // We create a great lord of the dark...
            this.player.setWotCharacter(new DarkOne());

            setPlayerName("Great Lord Of the Dark");
            setFullPlayerName("Shai'tan");
            setPlayerPast("SERVE ME OR DIE !");

            WotlasLocation prison = new WotlasLocation(0, 2, 0, 1, 0);
            this.player.setLocation(prison);
            this.player.setX(50);
            this.player.setY(100);
        } else if (data.equals(props.getProperty("key.amyrlin", "amyrlin"))) {
            // We create an Amyrlin...
            this.player.setWotCharacter(new AesSedai());
            this.player.getWotCharacter().setCharacterRank("Amyrlin");
        } else if (data.equals(props.getProperty("key.chronicles", "chronicles"))) {
            // We create a Keeper of chronicles...
            this.player.setWotCharacter(new AesSedai());
            this.player.getWotCharacter().setCharacterRank("Keeper of the Chronicles");
        } else if (data.equals(props.getProperty("key.mhael", "mhael"))) {
            // We create a M'Hael...
            this.player.setWotCharacter(new Ashaman());
            this.player.getWotCharacter().setCharacterRank("M'Hael");
        } else
            throw new AccountException("Wrong Special Character Key !");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method called to set a warder's cloak color. (for warders and blade masters only).
     */
    public void setCloakColor(String data) throws AccountException {

        // 1 - Get Human character
        WotCharacter wotCharacter = this.player.getWotCharacter();

        if (wotCharacter == null)
            throw new AccountException("No character created !");

        if (!(wotCharacter instanceof Warder))
            throw new AccountException("Your character is not a Warder !");

        Warder warder = (Warder) wotCharacter;
        warder.setCloakColor(data);

        // 2 - check that it was set
        if (warder.getCloakColor() == null)
            throw new AccountException("Failed to set cloak color : " + data);
    }

    /*------------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------------*/

    /** To get the server Name.
     */
    public String getServerName() throws AccountException {
        return ServerDirector.getServerManager().getServerConfig().getServerSymbolicName();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the admin email.
     */
    public String getAdminEmail() throws AccountException {
        return ServerDirector.getServerManager().getServerConfig().getAdminEmail();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player account summary.
     */
    public String getAccountSummary() throws AccountException {

        StringBuffer str = new StringBuffer("");
        str.append("        Player Name  \t:  ");
        str.append(this.player.getFullPlayerName());
        str.append("\n        Player Class \t:  ");
        str.append(this.player.getWotCharacter().getCommunityName());
        str.append("\n        Player Rank  \t:  ");
        str.append(this.player.getWotCharacter().getCharacterRank());

        return str.toString();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
