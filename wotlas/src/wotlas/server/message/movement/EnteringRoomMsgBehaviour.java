/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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
import wotlas.common.message.chat.*;
import wotlas.common.universe.*;
import wotlas.common.chat.*;
import wotlas.common.router.MessageRouter;
import wotlas.common.*;
import wotlas.server.*;
import wotlas.common.message.description.*;

/**
 * Associated behaviour to the EnteringRoomMessage...
 *
 * @author Aldiss
 */

public class EnteringRoomMsgBehaviour extends EnteringRoomMessage implements NetMessageBehaviour {

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
     public void doBehaviour( Object sessionContext ) {
        // The sessionContext is here a PlayerImpl.
           PlayerImpl player = (PlayerImpl) sessionContext;

        // 1 - CONTROL
           if( primaryKey==null || !player.getPrimaryKey().equals( primaryKey ) ) {
              Debug.signal( Debug.ERROR, this, "The specified primary Key is not our player one's ! "+primaryKey );
              return;
           }

           if( !player.getLocation().isRoom() ) {
              sendError( player, "Current Player Location is not a Room !! "+player.getLocation());
              return;
           }
  
           if( location.getWorldMapID()!=player.getLocation().getWorldMapID() ||
               location.getTownMapID()!=player.getLocation().getTownMapID() ||
               location.getBuildingID()!=player.getLocation().getBuildingID() ||
               location.getInteriorMapID()!=player.getLocation().getInteriorMapID() ) {
               sendError( player, "Specified target location is not on our map !! "+location );
               return;
           }

       // Is the movement possible ?
          Room currentRoom = player.getMyRoom();
          Room targetRoom = null;

          if( currentRoom.getRoomLinks()==null ) {
              sendError( player, "No update possible ! "+location );
              return;
          }

          for( int i=0; i<currentRoom.getRoomLinks().length; i++ ) {
              Room otherRoom = currentRoom.getRoomLinks()[i].getRoom1();
                   
              if( otherRoom==currentRoom )
                  otherRoom = currentRoom.getRoomLinks()[i].getRoom2();

              if( otherRoom.getRoomID()==location.getRoomID() ) {
                  targetRoom = otherRoom;
                  break;
              }
          }

          if( targetRoom==null ) {
              sendError( player, "Target Room not found ! " +location );
              return;
          }

       // Move to target room
          player.setOrientation( orientation );
          
          if( !currentRoom.getMessageRouter().movePlayer( player, location ) ) {
              sendError( player, "Movement failed ! " +location );
              return;
          }
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
                                                       pReset.x, pReset.y,
                                                       player.getOrientation(), player.getSyncID() ) );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

