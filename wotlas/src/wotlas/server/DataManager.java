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
<COMPLETE>
<GETTER SETTER ACCOUNT MANAGER & WORLDMANAGER>

/** A DataManager manages Game Data. It possesses a WorldManager & AccountManager.
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 */

class DataManager
{
 /*------------------------------------------------------------------------------------*/
 
   /** Our Default Data Manager
    */
      static private DataManager dataManager;

 /*------------------------------------------------------------------------------------*/

   /** Our World Manager
    */
      private WorldManager worldManager;

   /** Our Account Manager
    */
      private AccountManager accountManager;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Loads the world data and player accounts.
   */
   private DataManager() {

       // 0 - We load all the WotlasObjects, Knowledges, etc...


       // 1 - We create an AccountManager. Player Accounts are automatically loaded.
              accountManager = new AccountManager();

       // 2 - We create a WorldManager. Worlds data is automatically loaded.
              worldManager = new WorldManager();
       
       // 3 - We initialize the WorldManager with the Players ( Players are
       //     located on Maps ).
              Iterator it = accountManager.getIterator();

              while( it.hasNext() )
                   worldManager.addPlayer( ( (GameAccount) it.next() ).getPlayer() );

       // 4 - We initialize the player objects that we just placed in the world.
              it = accountManager.getIterator();

              while( it.hasNext() )
                   ( (GameAccount) it.next() ).getPlayer().init();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Creates a new DataManager.
   *
   * @return the created (or previously created) data manager.
   */
   public static DataManager createDataManager() {
     if( dataManager==null )
            dataManager = new DataManager();

     return dataManager;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the default data manager.
   *
   * @return the default data manager.
   */
   public static DataManager getDefaultDataManager() {
         return dataManager;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
