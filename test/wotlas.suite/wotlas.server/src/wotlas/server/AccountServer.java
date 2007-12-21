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

import java.io.File;
import java.util.Properties;
import wotlas.common.ErrorCodeList;
import wotlas.common.ResourceManager;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetServer;
import wotlas.utils.Debug;

/** Wotlas Account Server. Its role is to wait clients and connect them to a new
 *  AccountBuilder. An AccountBuilder will help the client to create a GameAccount.<p>
 *
 *  For more information on how it works see {@see AccountBuilder AccountBuilder }.
 *
 *  This server supposes there is a PersistenceManager & DataManager
 *  already created.
 *
 * @author Aldiss
 * @see wotlas.server.AccountBuilder
 */

public class AccountServer extends NetServer implements ErrorCodeList {

    /*------------------------------------------------------------------------------------*/

    /** Static Link to Account Server Config File.
     */
    public final static String ACCOUNT_CONFIG = "account-server.cfg";

    /*------------------------------------------------------------------------------------*/

    /** Client Counter
     */
    private int clientCounter;

    /** Factory for building step parameters.
     */
    private AccountStepFactory stepFactory;

    /*------------------------------------------------------------------------------------*/

    /** Constructor (see wotlas.libs.net.NetServer for details).
     *
     *  @param serverInterface the host interface to bind to. Example: wotlas.tower.org
     *  @param port port on which the server listens to clients.
     *  @param packages a list of packages where we can find NetMsgBehaviour Classes.
     *  @param nbMaxSockets maximum number of sockets that can be opened on this server
     */
    public AccountServer(String serverInterface, int port, String packages[], int nbMaxSockets) {
        super(serverInterface, port, packages);
        setMaximumOpenedSockets(nbMaxSockets);

        // we create our wizars step factory
        this.stepFactory = new AccountStepFactory(ServerDirector.getResourceManager());

        if (this.stepFactory.getStep(AccountStepFactory.FIRST_STEP) == null) {
            Debug.signal(Debug.FAILURE, this, "First step missing in the account wizard !");
            Debug.exit();
        }

        ResourceManager rManager = ServerDirector.getResourceManager();

        // we load the clientCounter from the ACCOUNT_CONFIG

        Properties props = null;

        if (new File(rManager.getExternalConfigsDir() + AccountServer.ACCOUNT_CONFIG).exists())
            props = rManager.loadProperties(rManager.getExternalConfigsDir() + AccountServer.ACCOUNT_CONFIG);

        if (props == null) {
            // file not found, we create one...
            Debug.signal(Debug.WARNING, null, "Could not find file: " + rManager.getExternalConfigsDir() + AccountServer.ACCOUNT_CONFIG + "\n   Creating a new one...");

            props = new Properties();
            props.setProperty("clientCounter", "0");
            this.clientCounter = 0;

            if (!rManager.saveProperties(props, rManager.getExternalConfigsDir() + AccountServer.ACCOUNT_CONFIG, "Do not remove or modify this file !")) {
                Debug.signal(Debug.FAILURE, this, "Cannot create or get " + AccountServer.ACCOUNT_CONFIG + " file!");
                Debug.exit();
            }
        } else {
            try {
                this.clientCounter = Integer.parseInt(props.getProperty("clientCounter")) + 1;
                Debug.signal(Debug.NOTICE, null, "AccountServer Client Counter set to " + this.clientCounter + ".");
            } catch (Exception e) {
                Debug.signal(Debug.FAILURE, this, "Bad " + AccountServer.ACCOUNT_CONFIG + " clientCounter property!");
                Debug.exit();
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called automatically when a new client establishes a connection
     *  with this server ( the client sends a ClientRegisterMessage ).
     *
     * @param connection a previously created connection for this connection.
     * @param key a string given by the client to identify itself. The key should be
     *        equal to "AccountServerPlease!".
     */
    @Override
    public void accessControl(NetConnection connection, String key) {

        // The key is there to prevent wrong connections
        if (key.equals("AccountServerPlease!")) {

            // ok, let's create an AccountBuilder for this future client.
            AccountBuilder accountBuilder = new AccountBuilder(this);

            // we set his message context to his player...
            connection.setContext(accountBuilder);
            connection.addConnectionListener(accountBuilder);

            // welcome on board...
            acceptClient(connection);
            Debug.signal(Debug.NOTICE, this, "A new client is building a GameAccount...");
        } else if (key.startsWith("deleteAccount:") && !key.endsWith(":")) {
            // we retrieve the account name & password
            int basepos = key.indexOf(':');
            int basepos2 = key.indexOf(':', basepos + 1);

            if (basepos2 < 0) {
                Debug.signal(Debug.NOTICE, this, "A client tried to delete an account without giving a password.");
                refuseClient(connection, ErrorCodeList.ERR_BAD_REQUEST, "Invalid request !");
                return;
            }

            String accountName = key.substring(basepos + 1, basepos2);
            String password = key.substring(basepos2 + 1, key.length());

            // We try to erase the account
            AccountManager manager = ServerDirector.getDataManager().getAccountManager();
            GameAccount account = manager.getAccount(accountName);

            if (account == null) {
                Debug.signal(Debug.NOTICE, this, "A client tried to delete a non-existent account.");
                refuseClient(connection, ErrorCodeList.ERR_UNKNOWN_ACCOUNT, "This account does not exist on this server");
                return;
            }

            // Password Crack Detection ( dictionnary attack )
            if (account.tooMuchBadPasswordEntered()) {
                Debug.signal(Debug.WARNING, this, accountName + " already entered 3 bad passwords! account locked for 30s");
                refuseClient(connection, ErrorCodeList.ERR_BAD_PASSWORD, "Sorry, you entered 3 bad passwords ! your account is locked for 30s.");
                return;
            }

            // The account exists... but do we have the right password ?
            if (account.isRightPassword(password)) {
                // ok we delete the account...
                if (manager.deleteAccount(accountName, true)) {
                    Debug.signal(Debug.NOTICE, this, "Account " + accountName + " deleted successfully...");
                    refuseClient(connection, ErrorCodeList.ERR_ACCOUNT_DELETED, "Account deleted successfully...");
                    return;
                }

                // we set his message context to his player...
                Debug.signal(Debug.NOTICE, this, "Failed to delete Account " + accountName + "...");
                refuseClient(connection, ErrorCodeList.ERR_DELETE_FAILED, "Failed to delete your account. Please Report the problem.");
                return;
            } else {
                Debug.signal(Debug.NOTICE, this, accountName + " entered a bad password to delete an account.");
                refuseClient(connection, ErrorCodeList.ERR_BAD_PASSWORD, "Wrong password !");
                return;
            }
        } else {
            Debug.signal(Debug.NOTICE, this, "A client tried to enter the AccountServer with a wrong key :" + key);
            refuseClient(connection, ErrorCodeList.ERR_WRONG_KEY, "Wrong key for this server :" + key);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a valid ID for a new client. The new ID is saved on disk.
     */
    synchronized public int getNewLocalClientID() {
        this.clientCounter++;

        // save this state in config/accountServer.cfg
        Properties props = new Properties();
        props.setProperty("clientCounter", "" + this.clientCounter);

        ResourceManager rManager = ServerDirector.getResourceManager();

        if (!rManager.saveProperties(props, rManager.getExternalConfigsDir() + AccountServer.ACCOUNT_CONFIG, "Do not remove or modify this file !")) {
            Debug.signal(Debug.CRITICAL, this, "Cannot save clientCounter (=" + this.clientCounter + ") to " + AccountServer.ACCOUNT_CONFIG + " file!");
        }

        // we return the new value
        return this.clientCounter;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the factory for building step parameters.
     */
    public AccountStepFactory getStepFactory() {
        return this.stepFactory;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
