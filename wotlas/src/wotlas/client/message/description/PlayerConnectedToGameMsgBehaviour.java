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
import wotlas.common.Player;
import wotlas.common.PlayerState;
import wotlas.common.message.description.PlayerConnectedToGameMessage;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the PlayerConnectedToGameMessage...
 *
 * @author Aldiss
 */

public class PlayerConnectedToGameMsgBehaviour extends PlayerConnectedToGameMessage implements NetMessageBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public PlayerConnectedToGameMsgBehaviour() {
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
        Player searchedPlayer = (Player) dataManager.getPlayers().get(this.primaryKey);

        // 1 - Control
        if (searchedPlayer == null) {
            Debug.signal(Debug.WARNING, this, "Player not found :" + this.primaryKey);
            return;
        }

        // 2 - Update of the player
        searchedPlayer.setIsConnectedToGame(this.isConnectedToGame);
        if (this.isConnectedToGame) {
            searchedPlayer.getPlayerState().value = PlayerState.CONNECTED;
        } else {
            searchedPlayer.getPlayerState().value = PlayerState.DISCONNECTED;
        }

        // 3 - Update the Chat players list about searchedPlayer's state
        dataManager.getClientScreen().getChatPanel().updateAllChatRooms(searchedPlayer);

        if (this.isConnectedToGame)
            SoundLibrary.getSoundPlayer().playSound("gong.wav");
        else
            SoundLibrary.getSoundPlayer().playSound("man-yawn.wav");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
