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
import wotlas.common.message.movement.CanLeaveTownMapMessage;
import wotlas.common.message.movement.RedirectConnectionMessage;
import wotlas.common.message.movement.RedirectErrorMessage;
import wotlas.common.message.movement.ResetPositionMessage;
import wotlas.common.message.movement.WishServerMovementNetMsgBehaviour;
import wotlas.common.message.movement.YouCanLeaveMapMessage;
import wotlas.common.universe.Building;
import wotlas.common.universe.MapExit;
import wotlas.common.universe.Room;
import wotlas.common.universe.TownMap;
import wotlas.common.universe.WorldMap;
import wotlas.common.universe.WotlasLocation;
import wotlas.server.GatewayServer;
import wotlas.server.LieManager;
import wotlas.server.PlayerImpl;
import wotlas.server.ServerDirector;
import wotlas.utils.Debug;
import wotlas.utils.ScreenPoint;

/**
 * Associated behaviour to the CanLeaveTownMapMessage...
 *
 * @author Aldiss
 */
public class CanLeaveTownMapMsgBehaviour extends CanLeaveTownMapMessage implements WishServerMovementNetMsgBehaviour {

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     */
    public CanLeaveTownMapMsgBehaviour() {
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

        if (!player.getLocation().isTown()) {
            sendError(player, "Current Player Location is not a Town !! " + player.getLocation());
            return;
        }

        // Is the movement possible ?
        WorldManager wManager = ServerDirector.getDataManager().getWorldManager();
        TownMap currentTown = wManager.getTownMap(player.getLocation());

        if (currentTown == null) {
            sendError(player, "Failed to get town !! " + player.getLocation());
            return;
        }

        // Going to a WorldMap ?
        if (this.location.isWorld() && currentTown.getMapExits() != null) {

            boolean found = false;

            for (int i = 0; i < currentTown.getMapExits().length; i++) {

                MapExit mapExit = currentTown.getMapExits()[i];

                if (mapExit.getTargetWotlasLocation().equals(this.location)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                // MapExit not found...
                sendError(player, "Target Map not found !" + this.location);
                return;
            }

            // move to our brave new world...
            WorldMap targetWorld = wManager.getWorldMap(this.location);

            if (targetWorld == null) {
                sendError(player, "Target World not found !" + this.location);
                return;
            }

            // Location Update
            currentTown.getMessageRouter().removePlayer(player);

            player.setLocation(this.location);
            player.updateSyncID();
            player.getMovementComposer().resetMovement();
            player.setX(this.x);
            player.setY(this.y);
            player.setOrientation(this.orientation);
            player.getLieManager().removeMeet(LieManager.FORGET_TOWNMAP);
            player.getLieManager().forget(LieManager.MEET_CHANGETOWNMAP);

            player.sendMessage(new YouCanLeaveMapMessage(this.primaryKey, this.location, this.x, this.y, this.orientation, player.getSyncID()));
            return;
        }

        // Going to a room ?
        if (this.location.isRoom() && currentTown.getBuildings() != null) {
            for (int i = 0; i < currentTown.getBuildings().length; i++) {
                Building building = currentTown.getBuildings()[i];

                if (building.getBuildingExits() == null) {
                    continue;
                }

                for (int j = 0; j < building.getBuildingExits().length; j++) {
                    MapExit mapExit = building.getBuildingExits()[j];

                    if (!mapExit.getMapExitLocation().equals(this.location)) {
                        continue;
                    }

                    // Ok target mapExit found
                    // is the building on the same server ?
                    int targetServerID = building.getServerID();

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
                        player.getLieManager().removeMeet(LieManager.FORGET_TOWNMAP);
                        player.getLieManager().forget(LieManager.MEET_CHANGETOWNMAP);

                        if (gateway.transfertAccount(this.primaryKey, targetServerID)) {
                            Debug.signal(Debug.NOTICE, null, "Account Transaction " + this.primaryKey + " succeeded... sending redirection message.");
                            player.updateSyncID();

                            // We remove our player from the world
                            currentTown.getMessageRouter().removePlayer(player);

                            // ... and send a redirection message
                            player.sendMessage(new RedirectConnectionMessage(this.primaryKey, targetServerID)); // success
                            return;
                        } else {
                            Debug.signal(Debug.NOTICE, null, "Account Transaction " + this.primaryKey + " failed... reverting to previous state.");

                            // we revert to previous position
                            player.setLocation(oldLocation);
                            player.setX(mapExit.getTargetPosition().getX());
                            player.setY(mapExit.getTargetPosition().getY());
                            player.setOrientation(oldOrientation);

                            // and send an error message to the client...
                            player.sendMessage(new RedirectErrorMessage("Movement Failed. Retry later.\nTarget server (" + targetServerID + ") is not running.", mapExit.getTargetPosition().getX(), mapExit.getTargetPosition().getY())); // failed
                            return;
                        }
                    }

                    // just move to our new room
                    Room targetRoom = wManager.getRoom(this.location);

                    if (targetRoom == null) {
                        sendError(player, "Target Room not found ! " + this.location);
                        return;
                    }

                    // Location update...
                    currentTown.getMessageRouter().removePlayer(player);

                    player.setLocation(this.location);
                    player.updateSyncID();
                    player.getMovementComposer().resetMovement();
                    player.setX(this.x);
                    player.setY(this.y);
                    player.setOrientation(this.orientation);
                    player.getLieManager().removeMeet(LieManager.FORGET_TOWNMAP);
                    player.getLieManager().forget(LieManager.MEET_CHANGETOWNMAP);

                    player.sendMessage(new YouCanLeaveMapMessage(this.primaryKey, this.location, this.x, this.y, this.orientation, player.getSyncID()));
                    return;
                }
            }
        }

        // MapExit not found...
        sendError(player, "Target Map not found ! " + this.location);
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
