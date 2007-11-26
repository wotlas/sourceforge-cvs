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

import java.util.Properties;
import wotlas.common.Player;

/** A BotChatService is a service providing access to a remote AI chat bot.
 *  The service must possess a public empty constructor and is initialized
 *  by giving 'botChatService.xxx' properties in the init() method.
 *
 *  A BotChatService implementation must have an empty constructor with no parameters.
 *
 * @author Aldiss
 * @see wotlas.server.bots.BotPlayer
 */

public interface BotChatService {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this chat service.
     *  @param serverProperties server properties giving some information for this service
     *  @return true if the initialization succeeded, false if it failed
     */
    public boolean init(Properties serverProperties);

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
    public boolean connect();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To shut down the connection with the remote chat service. When this method is called
     *  it means the system is about to shutdown. You should free resources and advertise
     *  the shut.
     *  @return true if the connection was successfully shutdown, false otherwise
     */
    public boolean shutdown();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the state of this chat service (usually represents the connection state).
     *  @return true if this BotChatService is available, false if it's not working at
     *          the moment.
     */
    public boolean isAvailable();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Opens a chat session for the given player with the given bot. This method is
     *  called each time a player arrives in the bot's area.
     *
     *  @param bot bot who's the target of the session.
     *  @param player player arriving near our bot.
     *  @return true if the session was opened successfully
     */
    public boolean openSession(BotPlayer bot, Player player);

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
    public void askForAnswer(String message, Player fromPlayer, BotPlayer toBot);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Closes a chat session for the given player with the given bot. This method is
     *  called each time a player leaves the bot's area.
     *
     *  @param bot bot who's the target of the session.
     *  @param player player leaving our bot.
     *  @return true if the session was closed successfully
     */
    public boolean closeSession(BotPlayer bot, Player player);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
