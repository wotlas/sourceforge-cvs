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
 * Associated behaviour to the CanLeaveWorldMapMessage...
 *
 * @author Aldiss
 */

public class CanLeaveWorldMapMsgBehaviour extends CanLeaveWorldMapMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public CanLeaveWorldMapMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object context ) {

        // The context is here a PlayerImpl.
           PlayerImpl player = (PlayerImpl) context;

        // 1 - CONTROL
           if(primaryKey==null) {
              Debug.signal( Debug.ERROR, this, "No primary key specified !" );
              return;
           }

           if( !player.getPrimaryKey().equals( primaryKey ) ) {
              Debug.signal( Debug.ERROR, this, "The specified primary Key is not our player one's !" );
              return;
           }
       
           if( !player.getLocation().isWorld() ) {
              sendError( player, "Current Player Location is not a World !! "+player.getLocation() );
              return;
           }

       // Is the movement possible ?
          WorldManager wManager = DataManager.getDefaultDataManager().getWorldManager();
          WorldMap currentWorld = wManager.getWorldMap( player.getLocation() );

       // which of them is the right mapExit ?
          if( currentWorld==null ) {
             sendError( player, "Failed to get world !! "+player.getLocation() );
             return;
          }

          if( location.isTown() && currentWorld.getTownMaps()!=null )
              for( int i=0; i<currentWorld.getTownMaps().length; i++ )
              {
                TownMap townMap = currentWorld.getTownMaps()[i];
              
                if( townMap.getMapExits() == null )
                    continue;
              
                for( int j=0; j<townMap.getMapExits().length; j++ )
                {
                   MapExit mapExit = townMap.getMapExits()[j];

                   if( !mapExit.getMapExitLocation().equals( location ) )
                       continue;

                // MapExit Found !!!
                // 1 - ADD MORE PRECISE DESTINATION CHECK HERE

                // 2 - PREPARE LOCATION CHANGE
                   Hashtable players = currentWorld.getPlayers();

                   synchronized( players ) {
                      players.remove( primaryKey );
                   }

                // move to our new room
                   TownMap targetTownMap = wManager.getTownMap( location );
                   if( targetTownMap==null ) {
                       sendError( player, "Target Town not found ! "+location );

                     // reverting to old location
                        synchronized( players ) {
                            players.put( primaryKey, player );
                        }
                       return;
                   }

                   players = targetTownMap.getPlayers();

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
                   player.sendMessage( new YouCanLeaveMapMessage( primaryKey, location, x, y,
                                                                  orientation, player.getSyncID() ) );
                   return;
                }
              }
              else if( location.isRoom() && currentWorld.getTownMaps()!=null ) {
              	   WotlasLocation targetTown = new WotlasLocation(location.getWorldMapID(),location.getTownMapID());

                   for( int i=0; i<currentWorld.getTownMaps().length; i++ )
                   {
                      TownMap townMap = currentWorld.getTownMaps()[i];
              
                      if( townMap.getTownMapID()==location.getTownMapID()
                          && townMap.getMapExits()==null ) {                   
                          MapExit mapExit = townMap.findTownMapExit(null);

                          if( mapExit==null || !mapExit.getMapExitLocation().equals( location ) )
                              continue;

                       // MapExit Found !!!
                       // 1 - ADD MORE PRECISE DESTINATION CHECK HERE

                       // 2 - PREPARE LOCATION CHANGE
                          Hashtable players = currentWorld.getPlayers();

                          synchronized( players ) {
                              players.remove( primaryKey );
                          }

                       // move to our new room
                          Room targetRoom = wManager.getRoom( location );
                          if( targetRoom==null ) {
                             sendError( player, "Target Town not found ! "+location );

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
              }

       // MapExit not found...
          sendError( player, "Target Map not found !"+location );
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

