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

import wotlas.common.*;
import wotlas.server.router.*;
import wotlas.server.chat.ChatCommandProcessor;

import java.util.Iterator;


/** A DataManager manages Game Data. It possesses a WorldManager & AccountManager.
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 */

public class DataManager {

 /*------------------------------------------------------------------------------------*/

   /** Our World Manager
    */
      private WorldManager worldManager;

   /** Our Account Manager
    */
      private AccountManager accountManager;

   /** Our chat command processor
    */
      private ChatCommandProcessor chatCommandProcessor;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Loads the world data and player accounts.
   */
   public DataManager( ResourceManager rManager ) {

       // 1 - We load all the WotlasObjects, Knowledges, etc...
          /** Not for wotlas release 1 **/

       // 2 - We create a WorldManager. Worlds data is automatically loaded.
          worldManager = new WorldManager( rManager, false );
          worldManager.initMessageRouting( new ServerMessageRouterFactory() );

       // 3 - We create an AccountManager. Player Accounts are not loaded yet.
          accountManager = new AccountManager( rManager );

       // 4 - Creation of the Chat Command Processor.
          chatCommandProcessor = new ChatCommandProcessor();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Final init. We add our players to the world when everything's ready.
   */
   public void init() {

       // 0 - Load accounts
          accountManager.init();

       // 1 - We initialize the WorldManager with the Players ( Players are
       // located on Maps, it's our game organization... ).
          Iterator it = accountManager.getIterator();

          while( it.hasNext() ) {
          	 GameAccount account = (GameAccount) it.next();
          	 if( !account.getIsDeadAccount() )
                     worldManager.addPlayerToUniverse( account.getPlayer() );
          }

       // 2 - We initialize the player objects that we just placed in the world.
          it = accountManager.getIterator();

          while( it.hasNext() )
                 ( (GameAccount) it.next() ).getPlayer().init();

       // 3 - Init of the Chat Command Processor.
          chatCommandProcessor.init();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the world manager.
   *
   * @return the world manager.
   */
   public WorldManager getWorldManager() {
         return worldManager;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the account manager.
   *
   * @return the account manager.
   */
   public AccountManager getAccountManager() {
         return accountManager;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the chat command processor.
   *
   * @return the chat command processor.
   */
   public ChatCommandProcessor getChatCommandProcessor(){
         return chatCommandProcessor;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

