/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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

import wotlas.common.character.*;
import wotlas.common.universe.*;
import wotlas.common.message.account.*;
import wotlas.utils.*;

import wotlas.libs.net.*;
import wotlas.libs.wizard.*;

import java.lang.reflect.*;
import java.util.Properties;

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


public class AccountBuilder implements NetConnectionListener
{
 /*------------------------------------------------------------------------------------*/

   /** The Game Account we are building
    */
     private GameAccount account;

   /** The Player Data associated to this GameAccount
    */
     private PlayerImpl player;

   /** Personality of our client.
    */
     private NetPersonality personality;

   /** Our Account Server
    */
     private AccountServer accountServer;
 
   /** Our current step.
    */
     private JWizardStepParameters currentParameters;

 /*------------------------------------------------------------------------------------*/

   /** Constructor.
    */
      public AccountBuilder( AccountServer accountServer ) {
           this.accountServer = accountServer;
           currentParameters=null;

      	// the account is empty for now...
           account = new GameAccount();
           player = new PlayerImpl();
           player.setDefaultPlayerLocation();
      }

 /*------------------------------------------------------------------------------------*/

   /** Method called when the connection with the client is established.
    *
    * @param personality 
    */
     public void connectionCreated( NetPersonality personality ) {
         this.personality = personality;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called when the connection with the client is established.
    *
    * @param personality 
    */
     public void connectionClosed( NetPersonality personality ) {
       // clean-up
          personality = null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Called to start the account build.
    */
     public void startFirstStep() {
     	 if(currentParameters!=null)
     	    return;  // can only call this method once

     	 currentParameters = accountServer.getStepFactory().getStep( AccountStepFactory.FIRST_STEP );

         personalizeParameters(currentParameters);
         personality.queueMessage( new AccountStepMessage(
                                           currentParameters.getCopyWithNoServerProps() ) );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To return to the previous step.
    */
     public void returnToPreviousStep() {

        // A - Do we have a previous step to call ?
           if( !currentParameters.getIsPrevButtonEnabled() ) {
               sendStepError("Previous command is not enabled for this step !!");
               return;
           }

           String previous = currentParameters.getProperty("server.previous");

           if(previous==null) {
              sendStepError("Previous step not found !");
              return;
           }

        // B - We load the previous step and send it to the client
           currentParameters = accountServer.getStepFactory().getStep( previous );

           if(currentParameters==null) {
              sendStepError("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #stpNotFnd)");
              return;
           }

           personalizeParameters(currentParameters);
           personality.queueMessage( new AccountStepMessage(
                                          currentParameters.getCopyWithNoServerProps() ) );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To parse the result data and move to the next step.
    */
     public void setStepResultData( JWizardStepParameters resultParameters ) {

       try{

       // A - we retrieve the data properties
          String resultPropsKey[] = resultParameters.getStepPropertiesKey();
          if(resultPropsKey==null)
             resultPropsKey = new String[0];

       // B - we call the associated methods of the data properties
          String next = null;
       
          for( int i=0; i<resultPropsKey.length; i++ )
               if( resultPropsKey[i].startsWith("data.") ) {
               	  // 1 - we get the suffix
               	     String suffix = resultPropsKey[i].substring(
               	                                  resultPropsKey[i].indexOf('.')+1,
               	                                  resultPropsKey[i].length() );
                  // 2 - we get the data
               	     String data = resultParameters.getStepPropertiesValue()[i];
               	     
               	     if( suffix.equals("choice") ) {
               	         int ind = -1;

                         try{
                            ind = Integer.parseInt(data);
                         }
                         catch(Exception ex) {
                            sendStepError("Selection not valid !");
                            return;
                         }

                         suffix = "choice"+ind;
                         data = currentParameters.getProperty("init."+suffix);
                         
                         if(data==null) {
                            sendStepError("Selection not valid !");
                            return;
                         }
                     }

                  // 3 - Check for a method to call on this data
                     String method = currentParameters.getProperty("server."+suffix+".method");

                     if(method!=null && !invokeMethod(method, data) )
                     	return;

                  // 4 - Check for default method to call on this data
                     method = currentParameters.getProperty("server.method");

                     if(method!=null && !invokeMethod(method, data) )
                     	return;

                  // 5 - Check for link "next" step
                     next = currentParameters.getProperty("server."+suffix+".next");
               }

        // C - Do we have a next step to call ?
           if(next==null && !currentParameters.getIsLastStep()) {
              // we search for default
                 next = currentParameters.getProperty("server.next");

                 if(next==null) {
                    sendStepError("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #nexStpNon)");
                    return;
                 }
           }
           else if( currentParameters.getIsLastStep() ) {
               // we create the account and return
                  try{
                     createAccount();
                  }catch( Exception ex ) {
                     sendStepError("Failed to create account : "+ex.getMessage());
                  }
                 return;
           }

        // D - We load the next step and send it to the client
           currentParameters = accountServer.getStepFactory().getStep( next );

           if(currentParameters==null) {
              sendStepError("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #nexStpFai)");
              return;
           }

           personalizeParameters(currentParameters);
           personality.queueMessage( new AccountStepMessage(
                                          currentParameters.getCopyWithNoServerProps() ) );
        }
        catch( Exception ex2 ) {
           Debug.signal(Debug.ERROR,this,ex2);
           sendStepError("Internal Error : "+ex2);
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To invoke a method of the 'void XXXX(String data)' type.
     *  @return true if the method call succeeded, false if it failed and an error msg was
     *  sent.
     */
     private boolean invokeMethod( String method, String data ) {
        try{
            Class cparams[] = new Class[1];
            cparams[0] = String.class;
            Method m = getClass().getMethod(method, cparams);

            if(m==null) {
               sendStepError("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #metNofou)");
               return false;
            }

            Object params[] = new Object[1];
            params[0] = data;
            m.invoke(this,params);
            return true;
        }
        catch( InvocationTargetException ite ) {
            sendStepError("Error : "+ite.getTargetException().getMessage());
            return false;
        }
        catch( Exception ex ) {
            Debug.signal(Debug.ERROR,this,ex);
            sendStepError("Internal Error : ("+ex.getMessage()+") Please report the bug.");
            return false;
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To invoke a method of the 'String XXXX()' type.
     *  @return a String if the method call succeeded, null if it fails. In case of
     *  errors no error message is sent.
     */
     private String invokeMethod( String method ) {
        try{
            Class cparams[] = new Class[0];
            Method m = getClass().getMethod(method, cparams);

            if(m==null)
               return null; // method not found

            Object params[] = new Object[0];
            return (String) m.invoke(this,params);
        }
        catch( InvocationTargetException ite ) {
            Debug.signal(Debug.ERROR,this,ite.getTargetException().getMessage());
            return null;
        }
        catch( Exception ex ) {
            Debug.signal(Debug.ERROR,this,ex);
            return null;
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To personalize eventual init properties ( we replace $PATTERN$ if there are any
    *  declared).
    *  @param parameters to personalize
    */
     void personalizeParameters( JWizardStepParameters parameters ) {

       // A - we retrieve the keys
          String propsKey[] = parameters.getStepPropertiesKey();
          if(propsKey==null)
             return; // none

       // B - we personalize the init properties
          for( int i=0; i<propsKey.length; i++ )
               if( propsKey[i].startsWith("server.") && propsKey[i].endsWith("$")) {
               	  // 1 - we get the suffix & pattern
               	     String suffix = propsKey[i].substring(
               	                                  propsKey[i].indexOf('.')+1,
               	                                  propsKey[i].indexOf('$')-1 );
               	     String pattern = propsKey[i].substring(
               	                                  propsKey[i].indexOf('$'),
               	                                  propsKey[i].length() );

                  // 2 - we get the methodName & text to analyze
               	     String methodName = parameters.getStepPropertiesValue()[i];

                     String text = parameters.getProperty("init."+suffix);

                  // 3 - Check what we have
                     if(pattern.length()==0 || text==null || methodName.length()==0)
                        continue; // ignore this bad entry

                  // 4 - Proceed...
                     int ind = text.indexOf(pattern);
                     
                     if(ind<0) continue; // pattern not found

                     StringBuffer buf = new StringBuffer(text.substring(0,ind));
                     
                     String result = invokeMethod(methodName);
                     if(result==null) result="#ERROR#";

                     buf.append(result);
                     buf.append( text.substring(ind+pattern.length(),text.length()) );

                  // 5 - Save our modif
                     parameters.setProperty( "init."+suffix, buf.toString() );
               }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** A small method to report a step error.
    */
     private void sendStepError( String message ) {
        personality.queueMessage( new StepErrorMessage( message ) );
        Debug.signal( Debug.ERROR, this, "An error occured during account creation : "+message );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To cancel the account's creation.
    */
     public void cancelCreation() {
        Debug.signal( Debug.NOTICE, this, "Account Creation cancelled..." );
        personality.closeConnection();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called to create the account
    *  This method sends back to the client a AccountCreationEnded message on success.
    */
     public void createAccount() throws AccountException {

        AccountManager accountManager = DataManager.getDefaultDataManager().getAccountManager();

     // 1 - We finalize inits
        account.setPlayer( player );
        account.setLocalClientID( accountServer.getNewLocalClientID() );
        account.setOriginalServerID( ServerManager.getDefaultServerManager().getServerConfig().getServerID() );
        account.setLastConnectionTimeNow();
        player.setPrimaryKey( account.getAccountName() );

     // 2 - We add the account to the game server
        if( accountManager.checkAccountName( account.getAccountName() ) )
            throw new AccountException("Internal Error. This server was badly configurated.\nPlease mail this server's administrator ! (code: #dupAcID)");

        if( accountManager.createAccount( account ) ) {
           // we add the player to the world...
              player.init();
              
              DataManager.getDefaultDataManager().getWorldManager().addNewPlayer( account.getPlayer() );
              Debug.signal( Debug.NOTICE, this, "Added new client account to the game." );

           // we send a Success Message
              personality.queueMessage( new AccountCreationEndedMessage(account.getLocalClientID(),
                                                                        account.getOriginalServerID(),
                                                                        account.getLogin(),
                                                                        account.getPassword(),
                                                                        player.getFullPlayerName() ) );
           // And close the connection
              personality.closeConnection();
        }
        else {
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
     public void setLogin( String data ) throws AccountException {
     	account.setLogin( data );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method to set the player's password.
    */
     public void setPassword( String data ) throws AccountException {
     	account.setPassword( data );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called to set the WotCharacterClass
    */
     public void setWotCharacterClass( String data ) throws AccountException {

      // 1 - Select Class
        String className="wotlas.common.character.";

        if(data.equals("Aes Sedai"))
           className += "AesSedai";
        else if(data.equals("Warder"))
           className += "Warder";
        else if(data.equals("Children of the Light"))
           className += "ChildrenOfTheLight";
        else if(data.equals("Wolf Brother"))
           className += "WolfBrother";
        else if(data.equals("Special Characters"))
           return;
        else
           throw new AccountException("Unknown character class !");

      // 2 - Create Instance
        Object obj = Tools.getInstance( className );

        if( obj==null || !(obj instanceof WotCharacter) )
           throw new AccountException("Error during character class creation !");

      // 3 - Set the player's character
        WotCharacter wotCharacter = (WotCharacter) obj;
     	player.setWotCharacter( wotCharacter );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called to set the WotCharacterClass rank.
    */
     public void setWotCharacterRank( String data ) throws AccountException {

      // 1 - Set the rank
        WotCharacter wotCharacter = (WotCharacter) player.getWotCharacter();

        if(wotCharacter==null)
           throw new AccountException("No character created !");

        wotCharacter.setCharacterRank(data);

      // 2 - check that it was set        
        if( !data.equals( wotCharacter.getCharacterRank() ) )
            throw new AccountException("Unknown rank for this character class !");
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called to set the player hair color. (for humans only).
    */
     public void setHairColor( String data ) throws AccountException {
     
       // 1 - Get Human character
        WotCharacter wotCharacter = (WotCharacter) player.getWotCharacter(); 

        if(wotCharacter==null)
           throw new AccountException("No character created !");

        if( ! (wotCharacter instanceof Human)  )
           throw new AccountException("Your character is not Human !");

        Human human = (Human) wotCharacter;
        human.setHairColor(data);

      // 2 - check that it was set        
        if( !data.equals( human.getHairColor() ) )
            throw new AccountException("Unknown hair color !");
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called to set the player's name.
    */
     public void setPlayerName( String data )  throws AccountException {
     	if(data.length()>30)
           throw new AccountException("Your nickname should have less than 30 letters !");
     	player.setPlayerName(data);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called to set the player's full name.
    */
     public void setFullPlayerName( String data )  throws AccountException {
     	if(data.length()>30)
           throw new AccountException("Your full name should have less than 30 letters !");

     	if(data.length()<5)
           throw new AccountException("Your full name should have more than 4 letters !");

     	player.setFullPlayerName(data);                
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called to set the player's email.
    */
     public void setEmail( String data ) throws AccountException {
     	account.setEmail(data);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called to set the player's past.
    */
     public void setPlayerPast( String data ) throws AccountException {
     	player.setPlayerPast(data);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called to set the player's past option.
    */
     public void setPlayerPastOption( String data ) throws AccountException {
     	if(data.equals("true"))
           player.setPlayerPast(""); // past will be set later
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called to create special characters
    */
     public void setSpecialCharacter( String data ) throws AccountException {

        Properties props = ServerDirector.getServerProperties();

        if( data.equals( props.getProperty("key.shaitan","shaitan") ) ) {
          // We create a great lord of the dark...
             player.setWotCharacter(new DarkOne());

             setPlayerName("Great Lord Of the Dark");
             setFullPlayerName("Shai'tan");
             setPlayerPast("SERVE ME OR DIE !");

             WotlasLocation prison = new WotlasLocation(0,2,0,1,0);
             player.setLocation(prison);
             player.setX(50);
             player.setY(100);
        }
        else if(data.equals( props.getProperty("key.amyrlin","amyrlin") )) {
          // We create an Amyrlin...
             player.setWotCharacter(new AesSedai());
             player.getWotCharacter().setCharacterRank("Amyrlin");
        }
        else if(data.equals( props.getProperty("key.chronicles","chronicles") )) {
          // We create a Keeper of chronicles...
             player.setWotCharacter(new AesSedai());
             player.getWotCharacter().setCharacterRank("Keeper of the Chronicles");
        }
        else
           throw new AccountException("Wrong Special Character Key !");
     }


 /*------------------------------------------------------------------------------------*/
 /*------------------------------------------------------------------------------------*/

   /** To get the server Name.
    */
     public String getServerName() throws AccountException {
         return ServerManager.getDefaultServerManager().getServerConfig().getServerSymbolicName();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the admin email.
    */
     public String getAdminEmail() throws AccountException {
         return ServerManager.getDefaultServerManager().getServerConfig().getAdminEmail();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player account summary.
    */
     public String getAccountSummary() throws AccountException {

         StringBuffer str = new StringBuffer("");
         str.append("        Player Name  \t:  ");
         str.append( player.getFullPlayerName() );
         str.append("\n        Player Class \t:  ");
         str.append( player.getWotCharacter().getCommunityName() );
         str.append("\n        Player Rank  \t:  ");
         str.append( player.getWotCharacter().getCharacterRank() );

         return str.toString();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
