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

import java.io.IOException;
import java.util.*;

import wotlas.utils.*;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.account.*;
import wotlas.common.message.movement.*;
import wotlas.common.message.description.*;
import wotlas.common.universe.*;
import wotlas.common.router.MessageRouter;
import wotlas.common.*;
import wotlas.server.*;

/**
 * Associated behaviour to the CanLeaveIntMapMessage...
 *
 * @author Aldiss
 */

public class CanLeaveIntMapMsgBehaviour extends CanLeaveIntMapMessage implements NetMessageBehaviour {

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public CanLeaveIntMapMsgBehaviour() {
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

        // 1 - PrimaryKey & Loation check
           if(primaryKey==null) {
              Debug.signal( Debug.ERROR, this, "No primary key specified !" );
              return;
           }

           if( !player.getPrimaryKey().equals( primaryKey ) ) {
              Debug.signal( Debug.ERROR, this, "The specified primary Key is not our player one's !" );
              return;
           }
       
           if( !player.getLocation().isRoom() ) {
              sendError( player, "Current Player Location is not a Room !! "+player.getLocation() );
              return;
           }

       // 2 - Is the movement possible ?
          WorldManager wManager = ServerDirector.getDataManager().getWorldManager();
          Room currentRoom = player.getMyRoom();

           if( currentRoom==null || currentRoom.getMapExits()==null) {
              sendError( player, "This room has no map exits !! "+player.getLocation() );
              return;
           }

          boolean found = false;

           for( int i=0; i<currentRoom.getMapExits().length; i++ ) {
               MapExit mapExit = currentRoom.getMapExits()[i];
              
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

       // 3 - Search for target location AND eventual account transfer
          boolean error = false;
       
          if( location.isRoom() ) {
              Room targetRoom = wManager.getRoom( location );
              if( targetRoom==null ) error = true;

           // LEAVING BUILDING ? ACCOUNT TRANSFER ?
              int targetServerID = targetRoom.getMyInteriorMap().getMyBuilding().getServerID();

                if( !error && targetServerID!=ServerDirector.getServerID() ) {

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
                      player.getLieManager().removeMeet(LieManager.FORGET_INTERIORMAP);
                      player.getLieManager().forget(LieManager.MEET_CHANGEINTERIORMAP);

                        if( gateway.transfertAccount( primaryKey, targetServerID ) ) {
                            Debug.signal(Debug.NOTICE,null,"Account Transaction "+primaryKey+" succeeded... sending redirection message.");
                            player.updateSyncID();

                          // We remove our player from our previous Message Router
                             currentRoom.getMessageRouter().removePlayer( player );

                          // We ask the client to reconnect on the target server
                             player.sendMessage(new RedirectConnectionMessage(primaryKey,targetServerID) ); // success
                             return;
                        }
                        else {
                             Debug.signal(Debug.NOTICE,null,"Account Transaction "+primaryKey+" failed... reverting to previous state.");

                          // we revert to previous position
                             player.setLocation( oldLocation );
                             player.setX( oldX );
                             player.setY( oldY );
                             player.setOrientation( oldOrientation );

                          // ... and signal that the transfer failed...
                             player.sendMessage( new RedirectErrorMessage("Movement Failed. Retry later.\nTarget server ("
                                                +targetServerID+") is not running.") ); // failed
                             return;
                        }
                }
          }
          else if( location.isTown() ) {
           // move to our new town
              TownMap targetTown = wManager.getTownMap( location );
              if( targetTown==null ) error=true;
          }
          else if( location.isWorld() ) {
           // move to our new world
              WorldMap targetWorld = wManager.getWorldMap( location );
              if( targetWorld==null ) error = true;
          }
          else
              error = true; // Bad MapExit location !!

          if( error ) {
              sendError( player, "Target Map not found ! "+location );
              return;
          }

       // 4 - LOCATION UPDATE
       // We remove our player from our previous Message Router
       // We will only add our player to the target MessageRouter when the player
       // will send the AllDataLeftMessage
          currentRoom.getMessageRouter().removePlayer( player );

          player.setLocation( location );
          player.updateSyncID();
          player.getMovementComposer().resetMovement();
          player.setX( x );
          player.setY( y );
          player.setOrientation( orientation );
          player.getLieManager().removeMeet(LieManager.FORGET_INTERIORMAP);
          player.getLieManager().forget(LieManager.MEET_CHANGEINTERIORMAP);

          player.sendMessage( new YouCanLeaveMapMessage( primaryKey, location, x, y,
                                                         orientation, player.getSyncID() ) );
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

             if( player.getLocation().isTown() ) {
                 TownMap myTown = wManager.getTownMap( location );
                 if(myTown!=null) 
                    pReset = myTown.getInsertionPoint();
             }
             else if( player.getLocation().isWorld() ) {
                 WorldMap myWorld = wManager.getWorldMap( location );
                 if(myWorld!=null) 
                    pReset = myWorld.getInsertionPoint();
             }
         }

      // Have we found a valid insertion point ?
         if(pReset==null) {
            Debug.signal(Debug.CRITICAL,this,"NO VALID LOCATION FOR PLAYER: "+player.getLocation());
            pReset = new ScreenPoint(0, 0);
         }

      // We send the message...
         player.sendMessage( new ResetPositionMessage( player.getPrimaryKey(), player.getLocation(),
                                                       pReset.x, pReset.y, player.getOrientation(),
                                                       player.getSyncID() ) );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

