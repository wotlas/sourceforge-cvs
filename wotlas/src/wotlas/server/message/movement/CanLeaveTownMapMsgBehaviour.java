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
import wotlas.common.universe.*;
import wotlas.common.Player;
import wotlas.server.*;
import wotlas.common.message.description.*;

/**
 * Associated behaviour to the CanLeaveTownMapMessage...
 *
 * @author Aldiss
 */

public class CanLeaveTownMapMsgBehaviour extends CanLeaveTownMapMessage implements NetMessageBehaviour
{
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
       
           if( !player.getLocation().isTown() ) {
              sendError( player, "Current Player Location is not a Town !! "+player.getLocation() );
              return;
           }

       // Is the movement possible ?
          WorldManager wManager = DataManager.getDefaultDataManager().getWorldManager();
          TownMap currentTown = wManager.getTownMap( player.getLocation() );

       // which of them is the right mapExit ?
          if( currentTown==null ) {
             sendError( player, "Failed to get town !! " +player.getLocation() );
             return;
          }

          if( location.isWorld() && currentTown.getMapExits()!=null )
              for( int i=0; i<currentTown.getMapExits().length; i++ )
              {
                 MapExit mapExit = currentTown.getMapExits()[i];
              
                 if( !mapExit.getTargetWotlasLocation().equals( location ) )
                     continue;

              // MapExit Found !!!
              // 1 - ADD MORE PRECISE DESTINATION CHECK HERE

              // 2 - PREPARE LOCATION CHANGE
                 Hashtable players = currentTown.getPlayers();

                 synchronized( players ) {
                     players.remove( primaryKey );
                 }

              // move to our new world
                 WorldMap targetWorld = wManager.getWorldMap( location );
  
                 if( targetWorld==null  ) {
                     sendError( player, "Target World not found !"+location );

                   // reverting to old location
                      synchronized( players ) {
                          players.put( primaryKey, player );
                      }
                    return;
                 }

                 players = targetWorld.getPlayers();

              // 3  - LOCATION UPDATE
                 player.setLocation( location );
                 player.updateSyncID();
                 player.getMovementComposer().resetMovement();
                 player.setX( x );
                 player.setY( y );
                 player.setOrientation( orientation );

                 synchronized( players ) {
                     players.put( primaryKey, player );
                 }

              // 4 - SEND MESSAGE TO PLAYER
                 player.sendMessage( new YouCanLeaveMapMessage( primaryKey, location, x ,y,
                                                                orientation, player.getSyncID() ) );
                 return;
              }

          if( location.isRoom() && currentTown.getBuildings()!=null )
              for( int i=0; i<currentTown.getBuildings().length; i++ )
              {
                Building building = currentTown.getBuildings()[i];
              
                if( building.getBuildingExits() == null )
                    continue;
              
                for( int j=0; j<building.getBuildingExits().length; j++ )
                {
                   MapExit mapExit = building.getBuildingExits()[j];

                   if( !mapExit.getMapExitLocation().equals( location ) )
                       continue;

                // MapExit Found !!!
                // 1 - ADD MORE PRECISE DESTINATION CHECK HERE

                // 2 - PREPARE LOCATION CHANGE
                   Hashtable players = currentTown.getPlayers();

                   synchronized( players ) {
                      players.remove( primaryKey );
                   }

                // move to our new room
                   Room targetRoom = wManager.getRoom( location );
                   if( targetRoom==null ) {
                       sendError( player, "Target Room not found ! " +location );

                     // reverting to old location
                        synchronized( players ) {
                             players.put( primaryKey, player );
                        }
                       return;
                   }

                   players = targetRoom.getPlayers();

                // 3  - LOCATION UPDATE
                   player.setLocation( location );
                   player.updateSyncID();
                   player.getMovementComposer().resetMovement();
                   player.setX( x );
                   player.setY( y );
                   player.setOrientation( orientation );

                   synchronized( players ) {
                      players.put( primaryKey, player );
                   }

                // 4 - SEND MESSAGE TO PLAYER
                   player.sendMessage( new YouCanLeaveMapMessage( primaryKey, location,
                                                                  x, y, orientation, player.getSyncID() ) );
                   return;
                }
              }

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
             WorldManager wManager = DataManager.getDefaultDataManager().getWorldManager();

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
         player.sendMessage( new ResetPositionMessage( primaryKey, player.getLocation(),
                                                       pReset.x, pReset.y,
                                                       player.getOrientation(), player.getSyncID() ) );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

