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
import wotlas.common.message.description.AddPlayerToRoomMessage;
import wotlas.common.message.description.WishClientDescriptionNetMsgBehaviour;
import wotlas.common.universe.Room;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the AddPlayerToRoomMessage...
 *
 * @author Aldiss
 */
public class AddPlayerToRoomMsgBehaviour extends AddPlayerToRoomMessage implements WishClientDescriptionNetMsgBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public AddPlayerToRoomMsgBehaviour() {
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
            System.out.println("ADD PLAYER TO ROOM MESSAGE player: " + this.player.getPrimaryKey());
        }

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl myPlayer = dataManager.getMyPlayer();

        // 1 - Control
        if (!myPlayer.getLocation().isRoom()) {
            Debug.signal(Debug.ERROR, this, "Master player is not on an InteriorMap");
            return;
        }

        WotlasLocation myLocation = myPlayer.getLocation();
        Room myRoom = myPlayer.getMyRoom();

        if (myLocation.getWorldMapID() != this.player.getLocation().getWorldMapID() || myLocation.getTownMapID() != this.player.getLocation().getTownMapID() || myLocation.getBuildingID() != this.player.getLocation().getBuildingID() || myLocation.getInteriorMapID() != this.player.getLocation().getInteriorMapID()) {
            Debug.signal(Debug.WARNING, this, "Received message with far location");
            return;
        }

        // Search in Current Room
        if (myRoom.getRoomID() == this.player.getLocation().getRoomID()) {
            Hashtable<String, PlayerImpl> players = dataManager.getPlayers();

            synchronized (players) {
                if (!players.containsKey(this.player.getPrimaryKey())) {
                    players.put(this.player.getPrimaryKey(), (PlayerImpl) this.player);
                    ((PlayerImpl) this.player).init();
                    ((PlayerImpl) this.player).initVisualProperties(dataManager.getGraphicsDirector());
                    SoundLibrary.getSoundPlayer().playSound("human-steps.wav");
                }
            }

            return; // success
        }

        // Search in other rooms
        if (myRoom.getRoomLinks() == null) {
            return;
        } // not found

        for (int i = 0; i < myRoom.getRoomLinks().length; i++) {
            Room otherRoom = myRoom.getRoomLinks()[i].getRoom1();

            if (otherRoom == myRoom) {
                otherRoom = myRoom.getRoomLinks()[i].getRoom2();
            }

            if (otherRoom.getRoomID() == this.player.getLocation().getRoomID()) {
                Hashtable<String, PlayerImpl> players = dataManager.getPlayers();

                synchronized (players) {
                    if (!players.containsKey(this.player.getPrimaryKey())) {
                        players.put(this.player.getPrimaryKey(), (PlayerImpl) this.player);
                        ((PlayerImpl) this.player).init();
                        ((PlayerImpl) this.player).initVisualProperties(dataManager.getGraphicsDirector());
                        SoundLibrary.getSoundPlayer().playSound("human-steps.wav");
                    }
                }

                return; // success
            }
        }

        // not found
        Debug.signal(Debug.NOTICE, this, "Player's Room is not near master's");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
