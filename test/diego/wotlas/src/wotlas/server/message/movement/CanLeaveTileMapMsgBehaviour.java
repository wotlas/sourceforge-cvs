/*
 *          TO LieManager should be addes the TILEMAPS
 *
 *                      player.getLieManager().removeMeet(LieManager.FORGET_TOWNMAP);
 *                      player.getLieManager().forget(LieManager.MEET_CHANGETOWNMAP);
 *
 *                      player.getLieManager().removeMeet(LieManager.FORGET_TOWNMAP);
 *                      player.getLieManager().forget(LieManager.MEET_CHANGETOWNMAP);
 *
 *
 *
 *
 *
 *
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

import java.io.IOException;
import java.util.*;

import wotlas.utils.*;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.movement.*;
import wotlas.common.universe.*;
import wotlas.common.router.MessageRouter;
import wotlas.common.*;
import wotlas.server.*;
import wotlas.common.message.description.*;
import wotlas.common.message.account.*;

/**
 * Associated behaviour to the CanLeaveTownMapMessage...
 *
 * @author Aldiss
 */

public class CanLeaveTileMapMsgBehaviour extends CanLeaveTileMapMessage implements NetMessageBehaviour {

 /*------------------------------------------------------------------------------------*/

    /** Constructor.
    */
    public CanLeaveTileMapMsgBehaviour() {
        super();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Associated code to this Message...
    *
    * @param sessionContext an object giving specific access to other objects needed to process
    *        this message.
    */
    public void doBehaviour( Object sessionContext ) {

        // The sessionContext is here a PlayerImpl.
        PlayerImpl player = (PlayerImpl) sessionContext;

        // 1 - CONTROL
        if(primaryKey==null) {
            Debug.signal( Debug.ERROR, this, "No primary key specified !" );
            return;
        }

        if( !player.getPrimaryKey().equals( primaryKey ) ) {
            Debug.signal( Debug.ERROR, this, "The specified primary Key is not our player one's !" );
            return;
        }
       
        if( !player.getLocation().isTileMap() ) {
            sendError( player, "Current Player Location is not a TileMap !! "+player.getLocation() );
            return;
        }

        // Is the movement possible ?
        WorldManager wManager = ServerDirector.getDataManager().getWorldManager();
        TileMap currentTileMap = wManager.getTileMap( player.getLocation() );

        // Going to a WorldMap ?
        if( location.isWorld() && currentTileMap.getMapExits()!=null ) {

            boolean found = false;

            for( int i=0; i<currentTileMap.getMapExits().length; i++ ) {

                MapExit mapExit = currentTileMap.getMapExits()[i];
              
                if( mapExit.getTargetWotlasLocation().equals( location ) ) {
                    found = true;
                    break;
                }
            }

            if(!found) {
                // MapExit not found...
                sendError( player, "Target Map not found !"+location );
                return;
            }

            // move to our brave new world...
            WorldMap targetWorld = wManager.getWorldMap( location );

            if( targetWorld==null  ) {
                sendError( player, "Target World not found !"+location );
                return;
            }

            // Location Update
            currentTileMap.getMessageRouter().removePlayer(player);

            player.setLocation( location );
            player.updateSyncID();
            player.getMovementComposer().resetMovement();
            player.setX( x );
            player.setY( y );
            player.setOrientation( orientation );
            player.getLieManager().removeMeet(LieManager.FORGET_TOWNMAP);
            player.getLieManager().forget(LieManager.MEET_CHANGETOWNMAP);

            player.sendMessage( new YouCanLeaveMapMessage( primaryKey, location, x ,y,
                                                         orientation, player.getSyncID() ) );
            return;
        }

        // Going to a TileMap ?
        if( currentTileMap==null ) {
            sendError( player, "Failed to get tilemap !! " +player.getLocation() );
            return;
        }

        if( location.isTileMap() && currentTileMap.getMapExits()!=null ) {

            boolean found = false;

            for( int i=0; i<currentTileMap.getMapExits().length; i++ ) {

                MapExit mapExit = currentTileMap.getMapExits()[i];
              
                if( mapExit.getTargetWotlasLocation().equals( location ) ) {
                    found = true;
                    break;
                }
            }

            if(!found) {
                // MapExit not found...
                sendError( player, "Target Map not found !"+location );
                return;
            }

            // move to our brave new world...
            TileMap targetTileMap = wManager.getTileMap( location );

            if( targetTileMap==null  ) {
                sendError( player, "Target TileMap not found !"+location );
                return;
            }

            // Location Update
            currentTileMap.getMessageRouter().removePlayer(player);

            player.setLocation( location );
            player.updateSyncID();
            player.getMovementComposer().resetMovement();
            player.setX( x );
            player.setY( y );
            player.setOrientation( orientation );
            player.getLieManager().removeMeet(LieManager.FORGET_TOWNMAP);
            player.getLieManager().forget(LieManager.MEET_CHANGETOWNMAP);

            player.sendMessage( new YouCanLeaveMapMessage( primaryKey, location, x ,y,
                                                         orientation, player.getSyncID() ) );
            return;
        }

/*
          if( location.isTileMap() && currentTileMap.getMapExits()!=null ) {

              boolean found = false;

              for( int i=0; i<currentTileMap.getMapExits().length; i++ ) {

                 MapExit mapExit = currentTileMap.getMapExits()[i];

                 if( !mapExit.getMapExitLocation().equals( location ) )
                     continue;

                 // Ok target mapExit found
                 // is the building on the same server ?
                 int targetServerID = xxxxxxxxxxx.getServerID();

                 if( targetServerID!=ServerDirector.getServerID() ) {
                       
                    // ok ! we must transfer this account to another server !!   
                    GatewayServer gateway = ServerDirector.getServerManager().getGatewayServer();

                    WotlasLocation oldLocation = player.getLocation();
                    int oldX = player.getX();
                    int oldY = player.getY();
                    float oldOrientation = player.getOrientation();

                    // We update the player's location
                    player.setLocation( location );
                    player.getMovementComposer().resetMovement();
                    player.setX( x );
                    player.setY( y );
                    player.setOrientation( orientation );
                    player.getLieManager().removeMeet(LieManager.FORGET_TOWNMAP);
                    player.getLieManager().forget(LieManager.MEET_CHANGETOWNMAP);

                    if( gateway.transfertAccount( primaryKey, targetServerID ) ) {
                        Debug.signal(Debug.NOTICE, null, "Account Transaction "+primaryKey+" succeeded... sending redirection message.");
                        player.updateSyncID();

                        // We remove our player from the world
                        currentTileMap.getMessageRouter().removePlayer(player);

                        // ... and send a redirection message
                        player.sendMessage(new RedirectConnectionMessage(primaryKey,targetServerID) ); // success
                        return;
                    }
                    else {
                        Debug.signal(Debug.NOTICE, null, "Account Transaction "+primaryKey+" failed... reverting to previous state.");

                        // we revert to previous position
                        player.setLocation( oldLocation );
                        player.setX( mapExit.getTargetPosition().getX() );
                        player.setY( mapExit.getTargetPosition().getY() );
                        player.setOrientation( oldOrientation );

                        // and send an error message to the client...
                        player.sendMessage(new RedirectErrorMessage("Movement Failed. Retry later.\nTarget server ("
                                            +targetServerID+") is not running.",
                                            mapExit.getTargetPosition().getX(),
                                            mapExit.getTargetPosition().getY() ) ); // failed
                        return;
                    }
                 }

              }

              // move to our brave new world...
              TileMap targetTileMap = wManager.getTileMap( location );

              if( targetTileMap==null  ) {
                  sendError( player, "Target TileMap not found !"+location );
                  return;
              }

           // Location Update
              currentTileMap.getMessageRouter().removePlayer(player);

              player.setLocation( location );
              player.updateSyncID();
              player.getMovementComposer().resetMovement();
              player.setX( x );
              player.setY( y );
              player.setOrientation( orientation );
              player.getLieManager().removeMeet(LieManager.FORGET_TOWNMAP);
              player.getLieManager().forget(LieManager.MEET_CHANGETOWNMAP);

              player.sendMessage( new YouCanLeaveMapMessage( primaryKey, location, x ,y,
                                                         orientation, player.getSyncID() ) );
              return;
          }
              
 */

        // MapExit not found...
        sendError( player, "Target Map not found ! "+location );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To send an error message to the client.
    */
     public void sendError( PlayerImpl player, String message ) {
         Debug.signal( Debug.ERROR, this, message );

      // We search for a valid insertion point
         ScreenPoint pReset = null;
         player.updateSyncID();

         if( player.getLocation().isRoom() )
             pReset = player.getMyRoom().getInsertionPoint();
         else {
           // We get the world manager
             WorldManager wManager = ServerDirector.getDataManager().getWorldManager();

             if( player.getLocation().isTileMap() ) {
                 TileMap myTileMap = wManager.getTileMap( location );
                 if(myTileMap!=null) 
                    pReset = myTileMap.getInsertionPoint();
             }
             else if( player.getLocation().isWorld() ) {
                 WorldMap myWorld = wManager.getWorldMap( location );
                 if(myWorld!=null) 
                    pReset = myWorld.getInsertionPoint();
             }
             else if( player.getLocation().isTown() ) {
                 TownMap myTown = wManager.getTownMap( location );
                 if(myTown!=null) 
                    pReset = myTown.getInsertionPoint();
             }
             else if( player.getLocation().isTileMap() ) {
                 TileMap myTileMap = wManager.getTileMap( location );
                 if(myTileMap!=null) 
                    pReset = myTileMap.getInsertionPoint();
             }
         }

      // Have we found a valid insertion point ?
         if(pReset==null) {
            Debug.signal(Debug.CRITICAL,this,"NO VALID LOCATION FOR PLAYER: "+player.getLocation());
            pReset = new ScreenPoint(0, 0);
         }

      // We send the message...
         player.sendMessage( new ResetPositionMessage( player.getPrimaryKey(), player.getLocation(),
                                                       pReset.x, pReset.y,
                                                       player.getOrientation(), player.getSyncID() ) );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}