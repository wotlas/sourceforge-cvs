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
import wotlas.common.message.movement.CanLeaveWorldMapMessage;
import wotlas.common.message.movement.RedirectConnectionMessage;
import wotlas.common.message.movement.RedirectErrorMessage;
import wotlas.common.message.movement.ResetPositionMessage;
import wotlas.common.message.movement.YouCanLeaveMapMessage;
import wotlas.common.universe.Building;
import wotlas.common.universe.MapExit;
import wotlas.common.universe.Room;
import wotlas.common.universe.TileMap;
import wotlas.common.universe.TownMap;
import wotlas.common.universe.WorldMap;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.server.GatewayServer;
import wotlas.server.LieManager;
import wotlas.server.PlayerImpl;
import wotlas.server.ServerDirector;
import wotlas.utils.Debug;
import wotlas.utils.ScreenPoint;

/**
 * Associated behaviour to the CanLeaveWorldMapMessage...
 *
 * @author Aldiss, Diego
 */

public class CanLeaveWorldMapMsgBehaviour extends CanLeaveWorldMapMessage implements NetMessageBehaviour {

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public CanLeaveWorldMapMsgBehaviour() {
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
        if (this.primaryKey == null) {
            Debug.signal(Debug.ERROR, this, "No primary key specified !");
            return;
        }

        if (!player.getPrimaryKey().equals(this.primaryKey)) {
            Debug.signal(Debug.ERROR, this, "The specified primary Key is not our player one's !");
            return;
        }

        if (!player.getLocation().isWorld()) {
            sendError(player, "Current Player Location is not a World !! " + player.getLocation());
            return;
        }

        // Is the movement possible ?
        WorldManager wManager = ServerDirector.getDataManager().getWorldManager();
        WorldMap currentWorld = wManager.getWorldMap(player.getLocation());

        if (currentWorld == null) {
            sendError(player, "Failed to get world !! " + player.getLocation());
            return;
        }

        // I - GOING IN A TILEMAP ?
        if (this.location.isTileMap() && currentWorld.getTileMaps() != null) {

            boolean found = false;

            // Search of the MapExit among TileMaps
            for (int i = 0; i < currentWorld.getTileMaps().length; i++) {
                TileMap tileMap = currentWorld.getTileMaps()[i];

                // diego:buttare:solo per farlo partire
                found = true;
                if (found) {
                    break;
                }
            }

            if (!found) {
                sendError(player, "Target Map not found !" + this.location);
                return;
            }

            // We update the player's location
            currentWorld.getMessageRouter().removePlayer(player);

            player.setLocation(this.location);
            player.updateSyncID();
            player.getMovementComposer().resetMovement();
            player.setX(this.x + 100);
            player.setY(this.y + 100);
            player.setOrientation(this.orientation);
            player.getLieManager().removeMeet(LieManager.FORGET_WORLDMAP);
            player.getLieManager().forget(LieManager.MEET_CHANGEWORLDMAP);

            // We validate the update...
            player.sendMessage(new YouCanLeaveMapMessage(this.primaryKey, this.location, this.x, this.y, this.orientation, player.getSyncID()));
            return;
        }

        // II - GOING IN TOWN ?
        if (this.location.isTown() && currentWorld.getTownMaps() != null) {

            boolean found = false;

            // Search of the MapExit among Towns
            for (int i = 0; i < currentWorld.getTownMaps().length; i++) {
                TownMap townMap = currentWorld.getTownMaps()[i];

                if (townMap.getMapExits() == null) {
                    continue;
                }

                for (int j = 0; j < townMap.getMapExits().length; j++) {
                    MapExit mapExit = townMap.getMapExits()[j];

                    if (mapExit.getMapExitLocation().equals(this.location)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    break;
                }
            }

            if (!found) {
                sendError(player, "Target Map not found !" + this.location);
                return;
            }

            // We update the player's location
            currentWorld.getMessageRouter().removePlayer(player);

            player.setLocation(this.location);
            player.updateSyncID();
            player.getMovementComposer().resetMovement();
            player.setX(this.x);
            player.setY(this.y);
            player.setOrientation(this.orientation);
            player.getLieManager().removeMeet(LieManager.FORGET_WORLDMAP);
            player.getLieManager().forget(LieManager.MEET_CHANGEWORLDMAP);

            // We validate the update...
            player.sendMessage(new YouCanLeaveMapMessage(this.primaryKey, this.location, this.x, this.y, this.orientation, player.getSyncID()));
            return;
        }

        // III - GOING TO A ROOM ?
        if (this.location.isRoom() && currentWorld.getTownMaps() != null) {

            // we get our new room
            Room targetRoom = wManager.getRoom(this.location);

            if (targetRoom == null) {
                sendError(player, "Target Town not found ! " + this.location);
                return;
            }

            // Building on the same server ?
            int targetServerID = targetRoom.getMyInteriorMap().getMyBuilding().getServerID();

            if (targetServerID != Building.SERVER_ID_NONE && targetServerID != ServerDirector.getServerID()) {
                // ok ! we must transfer this account to another server !!   
                GatewayServer gateway = ServerDirector.getServerManager().getGatewayServer();

                WotlasLocation oldLocation = player.getLocation();
                int oldX = player.getX();
                int oldY = player.getY();
                float oldOrientation = player.getOrientation();

                // We update the player's location
                player.setLocation(this.location);
                player.getMovementComposer().resetMovement();
                player.setX(this.x);
                player.setY(this.y);
                player.setOrientation(this.orientation);
                player.getLieManager().removeMeet(LieManager.FORGET_WORLDMAP);
                player.getLieManager().forget(LieManager.MEET_CHANGEWORLDMAP);

                if (gateway.transfertAccount(this.primaryKey, targetServerID)) {
                    Debug.signal(Debug.NOTICE, null, "Account Transaction " + this.primaryKey + " succeeded... sending redirection message.");
                    player.updateSyncID();

                    // We remove our player from the world
                    currentWorld.getMessageRouter().removePlayer(player);

                    // ... and send a redirection message
                    player.sendMessage(new RedirectConnectionMessage(this.primaryKey, targetServerID)); // success
                    return;
                } else {
                    // we revert to previous position
                    Debug.signal(Debug.NOTICE, null, "Account Transaction " + this.primaryKey + " failed... reverting to previous state.");
                    player.setLocation(oldLocation);
                    player.setX(oldX);
                    player.setY(oldY);
                    player.setOrientation(oldOrientation);

                    // and send an error to the client
                    player.sendMessage(new RedirectErrorMessage("Movement Failed. Retry later.\nTarget server (" + targetServerID + ") is not running.")); // failed
                    return;
                }
            }

            // LOCATION UPDATE
            // We remove our player from the world
            currentWorld.getMessageRouter().removePlayer(player);

            player.setLocation(this.location);
            player.updateSyncID();
            player.getMovementComposer().resetMovement();
            player.setX(this.x);
            player.setY(this.y);
            player.setOrientation(this.orientation);
            player.getLieManager().removeMeet(LieManager.FORGET_WORLDMAP);
            player.getLieManager().forget(LieManager.MEET_CHANGEWORLDMAP);

            player.sendMessage(new YouCanLeaveMapMessage(this.primaryKey, this.location, this.x, this.y, this.orientation, player.getSyncID()));
            return;
        }

        // MapExit not found...
        sendError(player, "Target Map not found !" + this.location);
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
