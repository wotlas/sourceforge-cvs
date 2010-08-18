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
import wotlas.common.Player;
import wotlas.common.message.description.RoomPlayerDataMessage;
import wotlas.common.message.description.WishClientDescriptionNetMsgBehaviour;
import wotlas.common.universe.Room;
import wotlas.common.universe.WotlasLocation;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the PathUpdateMovementMessage...
 *
 * @author Aldiss
 */
public class RoomPlayerDataMsgBehaviour extends RoomPlayerDataMessage implements WishClientDescriptionNetMsgBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public RoomPlayerDataMsgBehaviour() {
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
            System.out.println("ROOM PLAYER DATA MESSAGE " + this.location);
        }

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl myPlayer = dataManager.getMyPlayer();

        if (myPlayer.getLocation() == null) {
            Debug.signal(Debug.ERROR, this, "No location set !");
            return;
        }

        // We search for the location specified...
        if (myPlayer.getLocation().isRoom()) {
            Room myRoom = myPlayer.getMyRoom();
            if (myRoom == null) {
                Debug.signal(Debug.ERROR, this, "Null Room for " + myPlayer.getPrimaryKey());
                return;
            }

            // is this Room on the same map as ours ?
            WotlasLocation myLocation = myPlayer.getLocation();

            if (myLocation.getWorldMapID() != this.location.getWorldMapID() || myLocation.getTownMapID() != this.location.getTownMapID() || myLocation.getBuildingID() != this.location.getBuildingID() || myLocation.getInteriorMapID() != this.location.getInteriorMapID()) {
                Debug.signal(Debug.WARNING, this, "Received message with far location");
                return;
            }

            // Search in Current Room
            if (myRoom.getRoomID() == this.location.getRoomID()) {
                merge(dataManager);
                return; // success
            }

            // Search in other rooms
            if (myRoom.getRoomLinks() == null) {
                return; // not found
            }

            for (int i = 0; i < myRoom.getRoomLinks().length; i++) {
                Room otherRoom = myRoom.getRoomLinks()[i].getRoom1();

                if (otherRoom == myRoom) {
                    otherRoom = myRoom.getRoomLinks()[i].getRoom2();
                }

                if (otherRoom.getRoomID() == this.location.getRoomID()) {
                    merge(dataManager);
                    return; // success
                }
            }

            return; // the room was not found near us...
        }

    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To merge our players the DataManager's hashtable...
     */
    private void merge(DataManager dataManager) {
        Hashtable<String, PlayerImpl> dest = dataManager.getPlayers();

        synchronized (dest) {
            Iterator<Player> it = this.players.values().iterator();

            while (it.hasNext()) {
                PlayerImpl playerImpl = (PlayerImpl) it.next();

                if (dest.containsKey(playerImpl.getPrimaryKey())) {
                    continue;
                }

                dest.put(playerImpl.getPrimaryKey(), playerImpl);
                playerImpl.init();
                playerImpl.initVisualProperties(dataManager.getGraphicsDirector());
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
