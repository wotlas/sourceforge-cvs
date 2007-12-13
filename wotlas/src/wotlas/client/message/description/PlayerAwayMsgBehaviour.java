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
package wotlas.client.message.description;

import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;
import wotlas.client.screen.JChatRoom;
import wotlas.common.Player;
import wotlas.common.message.description.PlayerAwayMessage;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the PlayerAwayMessage...
 *
 * @author Aldiss
 */
public class PlayerAwayMsgBehaviour extends PlayerAwayMessage implements NetMessageBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public PlayerAwayMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {

        // The sessionContext is here a DataManager
        DataManager dataManager = (DataManager) sessionContext;
        Player searchedPlayer = dataManager.getPlayers().get(this.primaryKey);

        // 1 - Control
        if (searchedPlayer == null) {
            Debug.signal(Debug.WARNING, this, "Player not found :" + this.primaryKey);
            return;
        }

        // 2 - Update of the player
        searchedPlayer.setPlayerAwayMessage(this.playerAwayMessage);

        // 3 - Display of the message
        JChatRoom chatRoom = dataManager.getClientScreen().getChatPanel().getCurrentJChatRoom();
        chatRoom.appendText("<font color='gray'> " + ((PlayerImpl) searchedPlayer).getFullPlayerName() + " (disconnected) says:<i> " + this.playerAwayMessage + " </i></font>");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
