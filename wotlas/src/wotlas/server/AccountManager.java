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
<PERSISTENCE CORRECT>
<LOTS OF METHODS TO ADD: REMOVEACCOUNT, CREATEACCOUNT, >
<DOC REVIEW>

import java.util.HashMap;

/** 
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 */

class AccountManager
{
 /*------------------------------------------------------------------------------------*/

   /** Client Login ( equals to the client's directory name )
    */
      private HashMap accounts;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Attemps to load the client accounts. Any error at this step
   *  will stop the program.
   */
   public AccountManager() {

       // we use the PersistenceManager to load the worlds.
          if( !loadAccounts() ) {
              Debug.signal( Debug.NOTICE, null, "Exiting..." );
              System.exit(1);
          }

   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load the client Accounts
   *
   * @return true in case of success, false otherwise.
   */

   public boolean loadAccounts()
   {
       // Call to the PersistenceManager to load the accounts from the dataBase.
          try{
               PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
               GameAccount account_list[] = pm.loadAccounts();

               // HashMap init. ( initial size is 1.5*Nb Accounts +10 )
               // The HashMap Load Factor is 75%. The size doubles after that.
                  accounts = new HashMap( (int) (1.5*account.length + 10) );

               // we fill the hash-map...
                  for( int i=0; i<account_list.length; i++ )
                  {
                    // 1 - We load the player's own data from his account.
                       PlayerImpl player = pm.loadPlayer( account_list[i].getAccountName() );

                    // 2 - We associate this data (PlayerImpl) with the Account...
                    // There will be another association made by the DataManager
                    // with the WorldManager.
                       account[i].setPlayer( player );

                    // 3 - We create a new HashMap Entry, The account name is the key.
                    // The account name is the login + IDs
                       accounts.put( account_list[i].getAccountName(), account_list[i] );
                  }
          }
          catch( PersistenceException pe ) {
              Debug.signal( Debug.FAILURE, this, pe );
              return false;
          }

      return true;
   }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To save to disk a modified account.
   *
   * @return true in case of success, false otherwise.
   */

   public boolean saveAccount( GameAccount account ) {
       // Call to the PersistenceManager to save a new account in the dataBase.
          try{
              PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
              pm.saveAccount( account );
          }
          catch( PersistenceException pe ) {
              Debug.signal( Debug.FAILURE, this, pe );
              return false;
          }

      return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To iterate over the game accounts.
   *
   * @return an iterator that review all the GameAccounts.
   */
     public Iterator getIterator() {
         return accounts.entrySet().iterator();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To create a new acount.
   *
   * @param account a game account (client part)
   * @param player player data (player part)
   */
     public synchronized boolean createAccount( GameAccount account, PlayerImpl player ) {
        // we create the persistence entry
        
        // we insert the account in our table

     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To check if an account exists.
   *
   * @param accountName account name.
   * @return true if the account exists, false otherwise.
   */
     public synchronized boolean checkAccountName( String accountName ) {
         return accounts.containsKey( accountName );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an account from its name.
   *
   * @param accountName the account name...
   * @return the wanted account, or null if the account doesnot exist...
   */
     public synchronized GameAccount getAccount( String accountName ) {
         return accounts.get( accountName );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To delete an account from its name.
   *
   * @param accountName the account name...
   * @return true if the account was deleted.
   */
     public synchronized boolean deleteAccount( String accountName ) {
        // we delete the account

        // we remove the file entry

     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

