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

package wotlas.server.bots;

import wotlas.server.*;

import wotlas.common.character.*;
import wotlas.common.universe.*;
import wotlas.common.Player;

import java.util.Properties;

/** Proposes a set of methods to create a PlayerBot given some properties.
 *
 * @author Aldiss
 * @see wotlas.server.bots.BotPlayer
 */

public class BotFactory {

 /*------------------------------------------------------------------------------------*/

   /** Empty Constructor.
    */
      public BotFactory() {
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Call this method to initialize the factory with the server properties.
    * @param serverProperties server properties as given by the ServerDirector
    * @return true if the initialization succeeded, false if it failed
    */
      public boolean init( Properties serverProperties ) {
      	return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To create a bot from a player's data. The given player remains unchanged.
    *  @param player the player we'll take the data from to create a bot.
    *  @return a fully initialized bot, this BotPlayer can be cast into a server
    *          PlayerImpl and added to a standard player account.
    */
    public BotPlayer createBot(PlayerImpl player) {

      // Creation of a BotPlayer object
      // & Transfer of the player data to the bot
         BotPlayerImpl botPlayer = new BotPlayerImpl();
         botPlayer.clone( player );
         botPlayer.init();

         return (BotPlayer) botPlayer;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To transform a player into a bot.
    *  @param player the player we'll transform into a bot.
    *  @return true if the operation succeeded, false otherwise.
    */
    public boolean transformIntoBot(PlayerImpl player,AccountManager accountManager) {

      // 1 - Creation of a BotPlayer object
         BotPlayerImpl botPlayer = new BotPlayerImpl();
         botPlayer.clone( player );

      // 2 - We cut the old player from the world
         botPlayer.getMessageRouter().removePlayer( botPlayer );
         player.removeConnectionListener();
         player.closeConnection();

      // 3 - Modify the game account and add the bot...
         GameAccount account = accountManager.getAccount( player.getPrimaryKey() );
         account.setPlayer( botPlayer );
         accountManager.saveAccount(account);

         botPlayer.getMessageRouter().addPlayer( botPlayer );
         botPlayer.init(); // finalize the init...
         return true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
