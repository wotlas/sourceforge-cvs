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

package wotlas.server.bots;

import wotlas.common.Player;
import wotlas.utils.Debug;
import wotlas.server.*;

import java.util.Properties;

/** A BotChatService that returns to the user the message he sent to the bot.
 *  This is a simple class you can use to test the BotChatService.
 *
 * @author Aldiss
 * @see wotlas.server.bots.BotPlayer
 */

public class LoopBackBotChatService implements BotChatService {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Empty constructor for dynamic construction.
    */
      public LoopBackBotChatService() {
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To init this chat service.
    *  @param serverProperties server properties giving some information for this service
    *  @return true if the initialization succeeded, false if it failed
    */
      public boolean init( Properties serverProperties ) {
          Debug.signal(Debug.NOTICE, null, "Bot Chat Service is a LoopBack service.");
          return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To initialize the connection with the remote chat service. The first connect()
    *  is called just after the init() call. It's your job to eventually a thread to manage
    *  the state of the connection.<br>
    *
    *  When the connection succeeds or fails you should refresh the bots state :<br>
    *
    *     ServerDirector.getDataManager().getBotManager().refreshBotState();
    *
    *  @return true if the connection was successfully established, false otherwise
    */
      public boolean connect() {
          Debug.signal(Debug.NOTICE, null, "Bot Chat Service : Connect received.");
          ServerDirector.getDataManager().getBotManager().refreshBotState();
          return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To shut down the connection with the remote chat service. When this method is called
    *  it means the system is about to shutdown. You should free resources and advertise
    *  the shut.
    *  @return true if the connection was successfully shutdown, false otherwise
    */
      public boolean shutdown() {
      	  Debug.signal(Debug.NOTICE, null, "Bot Chat Service shutting down...");
          return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the state of this chat service (usually represents the connection state).
   *  @return true if this BotChatService is available, false if it's not working at
   *          the moment.
   */
      public boolean isAvailable() {
           return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Opens a chat session for the given player with the given bot. This method is
    *  called each time a player arrives in the bot's area.
    *
    *  @param bot bot who's the target of the session.
    *  @param player player arriving near our bot.
    *  @return true if the session was opened successfully
    */
      public boolean openSession(  BotPlayer bot, Player player ) {
      	  Debug.signal(Debug.NOTICE, null, "OPEN SESSION CALLED on "+player.getPrimaryKey()
      	                +" and bot "+((PlayerImpl)bot).getPrimaryKey() );
      	  bot.sendChatAnswer("Welcome to you "+
      	                   ((PlayerImpl) player).getFullPlayerName( (PlayerImpl) bot ) );
          return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Player 'fromPlayer' sent a 'message' to 'toBot'. We ask to this service the
    *  answer 'toBot' must send to 'fromPlayer'. This method SHOULD BE ASYNCHRONOUS.
    *  I.E. we ask for an answer and return. Later, when the result is received we
    *  call back the bot's sendChatAnswer method.
    *
    *  @param message message sent by 'fromPlayer'
    *  @param fromPlayer the player that sent the chat 'message'.
    *  @param toBot the bot which is supposed to answer the 'fromPlayer''s message.
    */
      public void askForAnswer( String message, Player fromPlayer, BotPlayer toBot ) {
      	  Debug.signal(Debug.NOTICE, null, "BOT ASKED FOR AN ANSWER FOR "+fromPlayer.getPrimaryKey());
      	  toBot.sendChatAnswer("I can repeat that : <i> "+ message +" </i>" );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Closes a chat session for the given player with the given bot. This method is
    *  called each time a player leaves the bot's area.
    *
    *  @param bot bot who's the target of the session.
    *  @param player player leaving our bot.
    *  @return true if the session was closed successfully
    */
      public boolean closeSession(  BotPlayer bot, Player player ) {
      	  Debug.signal(Debug.NOTICE, null, "CLOSE SESSION CALLED on "+player.getPrimaryKey()
      	                +" and bot "+((PlayerImpl)bot).getPrimaryKey() );
      	  return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
