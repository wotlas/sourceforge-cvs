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

import java.util.Hashtable;
import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;
import wotlas.common.message.description.RemovePlayerFromRoomMessage;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the RemovePlayerFromRoomMessage...
 *
 * @author Aldiss
 */

public class RemovePlayerFromRoomMsgBehaviour extends RemovePlayerFromRoomMessage implements NetMessageBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public RemovePlayerFromRoomMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {
        if (DataManager.SHOW_DEBUG)
            System.out.println("REMOVE PLAYER MESSAGE p:" + this.primaryKey);

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl myPlayer = dataManager.getMyPlayer();

        // 1 - Control
        if (!myPlayer.getLocation().isRoom()) {
            Debug.signal(Debug.ERROR, this, "Master player is not on an InteriorMap");
            return;
        }

        if (myPlayer.getPrimaryKey().equals(this.primaryKey)) {
            Debug.signal(Debug.ERROR, this, "ATTEMPT TO REMOVE MASTER PLAYER !!");
            return;
        }

        // 2 - We remove the player
        Hashtable players = dataManager.getPlayers();
        PlayerImpl playerImpl = null;

        synchronized (players) {
            playerImpl = (PlayerImpl) players.get(this.primaryKey);

            if (playerImpl == null)
                return;

            if (DataManager.SHOW_DEBUG)
                System.out.println("REMOVING PLAYER " + this.primaryKey);

            players.remove(this.primaryKey);
            dataManager.getClientScreen().getChatPanel().removePlayerFromAllchatRooms(this.primaryKey);
        }

        playerImpl.cleanVisualProperties(dataManager.getGraphicsDirector());

        if (dataManager.getSelectedPlayerKey() != null && this.primaryKey.equals(dataManager.getSelectedPlayerKey()))
            dataManager.removeCircle();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
