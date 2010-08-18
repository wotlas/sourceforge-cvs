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
package wotlas.server.message.movement;

import wotlas.common.WorldManager;
import wotlas.common.message.movement.EnteringRoomMessage;
import wotlas.common.message.movement.ResetPositionMessage;
import wotlas.common.message.movement.WishServerMovementNetMsgBehaviour;
import wotlas.common.universe.Room;
import wotlas.common.universe.TownMap;
import wotlas.common.universe.WorldMap;
import wotlas.server.PlayerImpl;
import wotlas.server.ServerDirector;
import wotlas.utils.Debug;
import wotlas.utils.ScreenPoint;

/**
 * Associated behaviour to the EnteringRoomMessage...
 *
 * @author Aldiss
 */
public class EnteringRoomMsgBehaviour extends EnteringRoomMessage implements WishServerMovementNetMsgBehaviour {

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     */
    public EnteringRoomMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {
        // The sessionContext is here a PlayerImpl.
        PlayerImpl player = (PlayerImpl) sessionContext;

        // 1 - CONTROL
        if (this.primaryKey == null || !player.getPrimaryKey().equals(this.primaryKey)) {
            Debug.signal(Debug.ERROR, this, "The specified primary Key is not our player one's ! " + this.primaryKey);
            return;
        }

        if (!player.getLocation().isRoom()) {
            sendError(player, "Current Player Location is not a Room !! " + player.getLocation());
            return;
        }

        if (this.location.getWorldMapID() != player.getLocation().getWorldMapID() || this.location.getTownMapID() != player.getLocation().getTownMapID() || this.location.getBuildingID() != player.getLocation().getBuildingID() || this.location.getInteriorMapID() != player.getLocation().getInteriorMapID()) {
            sendError(player, "Specified target location is not on our map !! " + this.location);
            return;
        }

        // Is the movement possible ?
        Room currentRoom = player.getMyRoom();
        Room targetRoom = null;

        if (currentRoom.getRoomLinks() == null) {
            sendError(player, "No update possible ! " + this.location);
            return;
        }

        for (int i = 0; i < currentRoom.getRoomLinks().length; i++) {
            Room otherRoom = currentRoom.getRoomLinks()[i].getRoom1();

            if (otherRoom == currentRoom) {
                otherRoom = currentRoom.getRoomLinks()[i].getRoom2();
            }

            if (otherRoom.getRoomID() == this.location.getRoomID()) {
                targetRoom = otherRoom;
                break;
            }
        }

        if (targetRoom == null) {
            sendError(player, "Target Room not found ! " + this.location);
            return;
        }

        // Move to target room
        player.setOrientation(this.orientation);

        if (!currentRoom.getMessageRouter().movePlayer(player, this.location)) {
            sendError(player, "Movement failed ! " + this.location);
            return;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To send an error message to the client.
     */
    public void sendError(PlayerImpl player, String message) {
        Debug.signal(Debug.ERROR, this, message);

        // We search for a valid insertion point
        ScreenPoint pReset = null;
        player.updateSyncID();

        if (player.getLocation().isRoom()) {
            pReset = player.getMyRoom().getInsertionPoint();
        } else {
            // We get the world manager
            WorldManager wManager = ServerDirector.getDataManager().getWorldManager();

            if (player.getLocation().isTown()) {
                TownMap myTown = wManager.getTownMap(this.location);
                if (myTown != null) {
                    pReset = myTown.getInsertionPoint();
                }
            } else if (player.getLocation().isWorld()) {
                WorldMap myWorld = wManager.getWorldMap(this.location);
                if (myWorld != null) {
                    pReset = myWorld.getInsertionPoint();
                }
            }
        }

        // Have we found a valid insertion point ?
        if (pReset == null) {
            Debug.signal(Debug.CRITICAL, this, "NO VALID LOCATION FOR PLAYER: " + player.getLocation());
            pReset = new ScreenPoint(0, 0);
        }

        // We send the message...
        player.sendMessage(new ResetPositionMessage(player.getPrimaryKey(), player.getLocation(), pReset.x, pReset.y, player.getOrientation(), player.getSyncID()));
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
