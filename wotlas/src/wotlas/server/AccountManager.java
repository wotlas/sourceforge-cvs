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

import wotlas.utils.Debug;

import java.util.HashMap;
import java.util.Iterator;


/** The AccountManager posseses all the client's game accounts. Note that all the
 *  methods of the AccountManager are not synchronized... so use the AccountManager with
 *  care ! It should only be used as server start-up or for daily persistence save in
 *  maintenance mode.
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 */

public class AccountManager
{
 /*------------------------------------------------------------------------------------*/

   /** Client Login ( equals to the client's directory name )
    */
      private HashMap accounts;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Attemps to load the client accounts.
   */
   public AccountManager() {

          loadAccounts();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load the client Accounts.
   */
   public void loadAccounts()
   {
       int nbAccounts=0,nbAccountsLoaded=0;

       // Call to the PersistenceManager to load the accounts from the dataBase.
          PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
          GameAccount account_list[] = pm.loadAccounts();

            if( account_list!=null )
                  nbAccounts = account_list.length;

            // HashMap init. ( initial size is 1.5*Nb Accounts +10 )
            // The HashMap Load Factor is 75%. The size doubles after that.
               accounts = new HashMap( (int) (1.5*nbAccounts + 10) );

            // we fill the hash-map...
               for( int i=0; i<nbAccounts; i++ )
                   if( account_list[i]!=null ) {
                       accounts.put( account_list[i].getAccountName(), account_list[i] );
                       nbAccountsLoaded++;
                   }

          Debug.signal( Debug.NOTICE, this, "AccountManager loaded "+nbAccountsLoaded+" accounts." );
   }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To save to disk a modified account. The account MUST ALREADY EXIST.
   *
   * @return true in case of success, false otherwise.
   */

   public boolean saveAccount( GameAccount account ) {
       // Call to the PersistenceManager to save a new account in the dataBase.
          PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();

          return pm.saveAccount( account );
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To iterate over the game accounts.
   *
   * @return an iterator that review all the GameAccounts.
   */
     public Iterator getIterator() {
         return accounts.values().iterator();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To create a new acount. The account must have been fully initialized ( don't forget
   *  the setPlayer() ! even if it's a transient field we use it to save Player data in
   *  a separate file ).
   *
   * @param account a game account
   * @return true means success, false usually means that the account name already exists.
   */
     public synchronized boolean createAccount( GameAccount account ) {
        // we create the persistence entry
           PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();

           if( !pm.createAccount( account ) )
               return false;

        // we insert the account in our table
           accounts.put( account.getAccountName(), account );
           return true;
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

  /** To add a new account (note that we don't check if it already exists).
   *
   * @param account account to add.
   */
     public synchronized void addAccount( GameAccount account ) {
         accounts.put( account.getAccountName(), account );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an account from its name.
   *
   * @param accountName the account name...
   * @return the wanted account, or null if the account doesnot exist...
   */
     public synchronized GameAccount getAccount( String accountName ) {
         return (GameAccount) accounts.get( accountName );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To delete an account from its name.
   *
   * @param accountName the account name...
   * @return true if the account was deleted.
   */
     public synchronized boolean deleteAccount( String accountName ) {
           PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
           WorldManager wManager = DataManager.getDefaultDataManager().getWorldManager();

        // We get the account
           GameAccount account = (GameAccount) accounts.get( accountName );
           
           if( account==null ) {
               Debug.signal( Debug.ERROR, this, "Account "+accountName+" not found" );
               return false;
           }

        // we remove the account hashmap entry
           accounts.remove( accountName );

           if( account.getPlayer().isConnectedToGame() ) {
               account.getPlayer().closeConnection();
               Debug.signal( Debug.WARNING, this, "Client connection was closed during the delete request.");
           }

        // we remove the character from the game...
           wManager.removePlayer( account.getPlayer() );

        // we delete the files
           if( !pm.deleteAccount( accountName ) ) {
               Debug.signal( Debug.ERROR, this, "Failed to delete Account files: "+accountName );
               return true; // account erased but some files remain !
           }           

           return true;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a list of online players.
   *
   * @return a list of online players
   */
   public synchronized HashMap getOnlinePlayers() {
     // HashMap init. ( initial size is Nb Accounts/2 )
     HashMap onlinePlayers = new HashMap((int)accounts.size()/2);
     
     Iterator it = accounts.values().iterator();
     PlayerImpl player;
     
     while ( it.hasNext() ) {
       player = ( (GameAccount) it.next() ).getPlayer();
       if (player.isConnectedToGame()) {
         onlinePlayers.put(player.getPrimaryKey(), player);
       }
     }
     
     return onlinePlayers;
   }
   
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

