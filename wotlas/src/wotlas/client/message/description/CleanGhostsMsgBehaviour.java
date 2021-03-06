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
import java.util.Iterator;
import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;
import wotlas.common.message.description.CleanGhostsMessage;
import wotlas.common.universe.Room;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the RemovePlayerFromRoomMessage...
 *
 * @author Aldiss
 */
public class CleanGhostsMsgBehaviour extends CleanGhostsMessage implements NetMessageBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public CleanGhostsMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {
        if (DataManager.SHOW_DEBUG) {
            System.out.println("CLEAN GHOSTS MESSAGE");
        }

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl myPlayer = dataManager.getMyPlayer();

        // 1 - Control
        if (!myPlayer.getLocation().isRoom()) {
            Debug.signal(Debug.ERROR, this, "Master player is not on an InteriorMap");
            return;
        }

        if (!myPlayer.getPrimaryKey().equals(this.primaryKey)) {
            Debug.signal(Debug.ERROR, this, "bad primaryKey !!" + this.primaryKey);
            return;
        }

        if (!myPlayer.getLocation().equals(this.location)) {
            Debug.signal(Debug.ERROR, this, "asked to clean ghosts for wrong base location !!");
            return;
        }

        // 2 - We compute the Rooms ID list of the visible rooms.
        Room myRoom = myPlayer.getMyRoom();
        if (myRoom == null) {
            return;
        }

        int roomIDs[] = null;

        if (myRoom.getRoomLinks() != null) {
            roomIDs = new int[myRoom.getRoomLinks().length + 1];

            for (int i = 0; i < myRoom.getRoomLinks().length; i++) {
                Room otherRoom = myRoom.getRoomLinks()[i].getRoom1();

                if (otherRoom == myRoom) {
                    otherRoom = myRoom.getRoomLinks()[i].getRoom2();
                }

                roomIDs[i] = otherRoom.getRoomID();
            }
        } else {
            roomIDs = new int[1];
        }

        roomIDs[roomIDs.length - 1] = myRoom.getRoomID();

        // 3 - We remove the ghosts players
        Hashtable<String, PlayerImpl> players = dataManager.getPlayers();
        PlayerImpl playerImpl = null;

        synchronized (players) {
            Iterator<PlayerImpl> it = players.values().iterator();

            while (it.hasNext()) {
                playerImpl = it.next();

                // Is this player in our list ?
                boolean isInList = false;
                int roomIDToTest = playerImpl.getLocation().getRoomID();

                for (int i = 0; i < roomIDs.length; i++) {
                    if (roomIDToTest == roomIDs[i]) {
                        isInList = true;
                        break;
                    }
                }

                if (!isInList) {
                    it.remove(); // GHOST !!
                    if (DataManager.SHOW_DEBUG) {
                        System.out.println("REMOVING GHOSTS !!!!" + playerImpl.getPrimaryKey() + " rID" + playerImpl.getLocation().getRoomID());
                    }
                    playerImpl.cleanVisualProperties(dataManager.getGraphicsDirector());

                    if (dataManager.getSelectedPlayerKey() != null && playerImpl.getPrimaryKey().equals(dataManager.getSelectedPlayerKey())) {
                        dataManager.removeCircle();
                    }
                }
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
