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

import wotlas.common.message.account.*;
import wotlas.utils.Debug;
import wotlas.libs.net.*;


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
 *      garbage collector. The client can now connect to the GameServer.
 *
 * @author Aldiss
 * @see wotlas.server.AccountServer
 */


public class AccountBuilder implements NetConnectionListener
{
 /*------------------------------------------------------------------------------------*/

    /** Account Empty State (not initialized)
     */
       static final public byte ACCOUNT_EMPTY_STATE = 0;

    /** ADD HERE OTHER ACCOUNT STATE (player quizz, quizz done ... )
     */

    /** Account Empty State (not initialized)
     */
       static final public byte ACCOUNT_LOGIN_PASSWD_STATE = 1;

    /** Account Empty State (not initialized)
     */
       static final public byte ACCOUNT_PLAYER_NAMES_STATE = 2;


    /** Account Ready State (account ready to be created)
     */
       static final public byte ACCOUNT_READY_STATE = 124;

    /** Account Created State (account has been created)
     */
       static final public byte ACCOUNT_CREATED_STATE = 125;

 /*------------------------------------------------------------------------------------*/

   /** Actual State of the creation process. The possible values are described above.
    */
     private byte state;

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
 
 /*------------------------------------------------------------------------------------*/

   /** Constructor.
    */
      public AccountBuilder( AccountServer accountServer ) {
           this.accountServer = accountServer;

      	// the account is empty for now...
           account = new GameAccount();
           player = new PlayerImpl();
           player.setPlayerLocationToWorld();

           state = ACCOUNT_LOGIN_PASSWD_STATE;   // first state...
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
 
   /** Method called by NetMessages to set login and password.
    */
     public void setLoginAndPassword( String login, String password ) {
     	if(state!=ACCOUNT_LOGIN_PASSWD_STATE) {
           stateError();
           return;
        }

     	account.setLogin(login);
     	account.setPassword(password);
        state = ACCOUNT_READY_STATE;    // TMP: change to ACCOUNT_PLAYER_NAMES_STATE
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called by NetMessages to set player names.
    */
     public void setPlayerNames( String playerName, String fullPlayerName ) {
     	if(state!=ACCOUNT_PLAYER_NAMES_STATE) {
           stateError();
           return;
        }

     	player.setPlayerName(playerName);
     	player.setFullPlayerName(fullPlayerName);                
        state = ACCOUNT_READY_STATE;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Method called by NetMessages to create the account
    */
     public void createAccount() {
     	if(state!=ACCOUNT_READY_STATE) {
           stateError();
           return;
        }

        PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
        AccountManager accountManager = DataManager.getDefaultDataManager().getAccountManager();

     // final inits
        account.setPlayer( player );
        account.setLocalClientID( accountServer.getNewLocalClientID() );
        account.setOriginalServerID( ServerManager.getDefaultServerManager().getServerConfig().getServerID() );
        account.setLastConnectionTimeNow();

        player.setPrimaryKey( account.getAccountName() );

     // account creation
        if( accountManager.checkAccountName( account.getAccountName() ) ) {
              personality.queueMessage( new AccountCreationFailedMessage( "Account already exists. Please change your login." ) );
              return;
        }

        if( pm.createAccount( account ) ) {
           // we add the account to the AccountManager & the player to the world...
              accountManager.addAccount( account );
              player.init();
              
              DataManager.getDefaultDataManager().getWorldManager().addNewPlayer( account.getPlayer() );
              Debug.signal( Debug.NOTICE, this, "Added new client account to the game." );

           // we send a Success Message
              personality.queueMessage( new AccountCreatedMessage(account.getLocalClientID(),
                                                account.getOriginalServerID() ) );

           // And close the connection
              personality.closeConnection();
        }
        else {
           // Account not created for some reason
           // we announce the bad news to the client
           // but we don't close the connection...
              personality.queueMessage( new AccountCreationFailedMessage( "internal failure" ) );
        }

     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
   /** A small method to report a state error and cancel the account creation
    */
     public void stateError() {
        state=ACCOUNT_EMPTY_STATE;
        Debug.signal( Debug.ERROR, this, "Bad State Detected During Account Creation" );
        personality.closeConnection();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

