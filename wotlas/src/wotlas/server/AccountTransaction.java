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

import wotlas.common.character.*;
import wotlas.utils.*;
import wotlas.libs.net.*;

import wotlas.server.message.gateway.*;

/** An AccountTransaction to manage an account transaction with a remote GatewayServer.
 *  There are two different ways to use this class depending on the role you want to
 *  assume : account receiver or account sender. 
 *
 *  An instance of this class assumes one and only one role in its life ( it is
 *  represented by an internal field you set in the constructor ).
 *
 * @author Aldiss
 * @see wotlas.server.GatewayServer
 */


public class AccountTransaction implements NetConnectionListener
{
 /*------------------------------------------------------------------------------------*/

    /** Tells we are the account receiver in the transaction.
     */
       static final public byte TRANSACTION_ACCOUNT_RECEIVER = 0;

    /** Tells we are the account sender in the transaction.
     */
       static final public byte TRANSACTION_ACCOUNT_SENDER = 1;

 /*------------------------------------------------------------------------------------*/

   /** Role assumed by this instance
    */
     private byte transactionRole;

   /** Network Personality for message exchange
    */
     private NetPersonality personality;

   /** Lock for transaction response
    */
     private Object transactionLock;

   /** Did the transaction succeeded ?
    */
     private boolean transactionSucceeded;

 /*------------------------------------------------------------------------------------*/

   /** Constructor with transaction role.
    */
      public AccountTransaction( byte transactionRole ) {
             this.transactionRole = transactionRole;
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

   /** To create an account on this server.
    */
     public void createAccount( GameAccount account, int serverID ) {

          if(transactionRole!=TRANSACTION_ACCOUNT_RECEIVER)
             return;

       // 0 - We print some info
          Debug.signal( Debug.NOTICE, null, "Server "+serverID+" sent us "+account.getPrimaryKey()+"'s account." );

       // 1 - Account already exists ?
          AccountManager manager = ServerDirector.getDataManager().getAccountManager();

          if( manager.checkAccountName( account.getPrimaryKey() ) ) {
              reportError( "Account "+account.getPrimaryKey()+" already exists !" );
              return;
          }

       // 2 - Account Creation
          if( !manager.createAccount(account) ) {
              reportError( "Account "+account.getPrimaryKey()+" creation failed !" );
              return;
          }

       // 3 - Success !     	
          if(personality!=null) {
             Debug.signal( Debug.NOTICE, null, account.getPrimaryKey()+" account transaction succeeded." );
             account.getPlayer().init();

             Debug.signal( Debug.NOTICE, this, "Created an account for the received client..." );

             personality.queueMessage( new AccountTrSuccessMessage() );
             personality.closeConnection();
          }
          else {
             Debug.signal( Debug.ERROR, this, "Could'nt finish "+account.getPrimaryKey()+" account transaction." );
             manager.deleteAccount(account.getPrimaryKey(),true); // we clean our mess
          }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
   /** To transfert the account on the other side. This methods waits for an answer until
    *  the timeout (in ms) is reached. The serverID parameter is the ID of our server.
    *
    *  IMPORTANT : if the account transfert succeeds the local account is DELETED.
    *
    *  @return true if the tranfert succeeded.
    */
     public boolean transfertAccount( String accountPrimaryKey, int timeout, int thisServerID ) {

          if(transactionRole!=TRANSACTION_ACCOUNT_SENDER)
             return false;

        // 0 - Some inits
          transactionLock = new Object();
          transactionSucceeded = false;

          AccountManager manager = ServerDirector.getDataManager().getAccountManager();
          GameAccount account = manager.getAccount(accountPrimaryKey);

          if(account==null) {
             Debug.signal( Debug.ERROR, this, "No account to send ! "+accountPrimaryKey+" not found !" );
             return false; // no account to send !!!
          }

          synchronized( transactionLock ) {
            // 1 - We send the account.
               if(personality!=null) {
                  personality.queueMessage( new AccountTransactionMessage( account, thisServerID ) );
               }
               else {
                  Debug.signal( Debug.ERROR, this, "No network personality set !" );
                  return false;
               }

            // 2 - We wait for the answer.
               try{
                 transactionLock.wait(timeout);
               }catch(Exception e ) {}
          }

        // 3 - Result
          if(transactionSucceeded) {
            // we remove the account hashmap entry
               manager.removeAccount( account.getPrimaryKey() );

            // and delete the account...
               if( manager.deleteAccountFiles( account.getPrimaryKey() ) ) // we delete the original account
                   Debug.signal( Debug.NOTICE, null, account.getPrimaryKey()+" account transaction succeeded." );
               else
                   Debug.signal( Debug.CRITICAL, this, "Failed to delete account "
                                 + account.getPrimaryKey()
                                 + " that has been moved to another server.");

               return true; // success
          }

          return false;  // failed
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
   /** A small method to report a state error and cancel the account creation
    */
     public void reportError(String errorMsg) {

         if(transactionRole!=TRANSACTION_ACCOUNT_RECEIVER)
            return;

         Debug.signal( Debug.ERROR, this, errorMsg );
     	
     	 if(personality!=null) {
            personality.queueMessage( new AccountTrFailedMessage( errorMsg ) );
            personality.closeConnection();
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To tell that the transaction suceeded.
    */
     public void transactionSucceeded() {

       if(transactionRole!=TRANSACTION_ACCOUNT_SENDER)
          return;

        synchronized( transactionLock ) {
           transactionSucceeded = true;
           transactionLock.notify();
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To tell that the transaction failed.
    */
     public void transactionFailed( String errorMessage ) {

       if(transactionRole!=TRANSACTION_ACCOUNT_SENDER)
          return;

        synchronized( transactionLock ) {
           Debug.signal( Debug.ERROR, this, errorMessage );
           transactionSucceeded = false;
           transactionLock.notify();
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

