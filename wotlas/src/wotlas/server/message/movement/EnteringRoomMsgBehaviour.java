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
import wotlas.common.Player;
import wotlas.server.PlayerImpl;
import wotlas.common.message.description.*;

/**
 * Associated behaviour to the EnteringRoomMessage...
 *
 * @author Aldiss
 */

public class EnteringRoomMsgBehaviour extends EnteringRoomMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public EnteringRoomMsgBehaviour() {
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
           boolean mapEnter = false; // to tell if it's an entry on the map, or just an move on the map

        // 1 - CONTROL
           if(primaryKey==null) {
              Debug.signal( Debug.ERROR, this, "No primary key specified !" );
              return;
           }

           if( !player.getPrimaryKey().equals( primaryKey ) ) {
              Debug.signal( Debug.ERROR, this, "The specified primary Key is not our player one's !" );
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

          if( currentRoom.getRoomID()==location.getRoomID() )
              mapEnter=true; // we have just entered the map

          if( !mapEnter && currentRoom.getRoomLinks()==null ) {
              sendError( player, "No update possible ! "+location );
              return;
          }

          if(!mapEnter) {
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
          }
          else
              targetRoom = currentRoom;
           
           
       // 2 - We send REMOVE player messages
          if(!mapEnter) {
             player.sendMessageToNearRooms( currentRoom,
                         new RemovePlayerFromRoomMessage(primaryKey, player.getLocation() ),
                         false );

             Hashtable players = currentRoom.getPlayers();
 
             synchronized( players ) {
                 players.remove( primaryKey );
             }

           // We also send a CleanGhost to our client
              player.sendMessage( new CleanGhostsMessage( primaryKey, location ) );
          }

       // 3 - We change our location & send ADD messages or ChangeLocation Messages
          Hashtable players = targetRoom.getPlayers();
          player.setLocation( location );

          AddPlayerToRoomMessage aMsg = new AddPlayerToRoomMessage( player );
          LocationChangeMessage lMsg = new LocationChangeMessage( player.getPrimaryKey(),
                                           player.getLocation(), 0, 0 );

          synchronized( players ) {
              players.put( primaryKey, player );
              Iterator it = players.values().iterator();
              	 
                 while( it.hasNext() ) {
                     PlayerImpl p = (PlayerImpl)it.next();
                     if(p!=player) {
                        if(mapEnter)
                           p.sendMessage( aMsg );
                        else
                           p.sendMessage( lMsg );
                     }
              	 }
          }

       // 4 - We send ADD player messages to neighbour rooms & RoomPlayerDataMessages
       // to ourseleves
          WotlasLocation destLocation = new WotlasLocation( player.getLocation() );
       
          if(targetRoom.getRoomLinks()!=null)
            for( int i=0; i<targetRoom.getRoomLinks().length; i++ ) {
               Room otherRoom = targetRoom.getRoomLinks()[i].getRoom1();

               if( otherRoom==targetRoom )
                   otherRoom = targetRoom.getRoomLinks()[i].getRoom2();

            // Doors state
               if( mapEnter || otherRoom!=currentRoom ) {
                   destLocation.setRoomID( otherRoom.getRoomID() );
             
                   DoorsStateMessage dMsg = new DoorsStateMessage( destLocation, otherRoom );
                   player.sendMessage( dMsg );
               }

            // AddPlayerDataMessages
               players = otherRoom.getPlayers();

               synchronized( players ) {
                 Iterator it = players.values().iterator();
              	 
                     while( it.hasNext() ) {
              	          PlayerImpl p = (PlayerImpl)it.next();

                          if(otherRoom!=currentRoom)
                             p.sendMessage( aMsg ); // new room seen, AddPlayer...
                          else
                             p.sendMessage( lMsg ); // if it's our old current room we send an changeLocMsg
              	     }
               }

            // RoomPlayerDataMessages ( already received if mapEnter state... ( via AllDataLeftMsg))
               if( !mapEnter && otherRoom!=currentRoom ) {
                  WotlasLocation roomLoc = new WotlasLocation( player.getLocation() );
                  roomLoc.setRoomID( otherRoom.getRoomID() );

                  player.sendMessage( new RoomPlayerDataMessage( roomLoc,
                                      player, otherRoom.getPlayers() ) );
               }
            }

       // 5 - We send a RemovePlayerFromChatMsg if not in the mapEnter state
          if( !mapEnter )
              player.sendMessageToRoom( currentRoom,
                   new RemPlayerFromChatRoomMessage( player.getPrimaryKey(), ChatRoom.DEFAULT_CHAT ),
                   false );

       // 5(bis) - Send the player of the default chat room to our player
          player.sendMessage( new SetCurrentChatRoomMessage( ChatRoom.DEFAULT_CHAT, targetRoom.getPlayers() ) );


       // 6 - We seek for a valid chatList if any...
          players = player.getMyRoom().getPlayers();

          synchronized( players ) {
             Iterator it = players.values().iterator();
              	 
             while( it.hasNext() ) {
             	 PlayerImpl p = (PlayerImpl)it.next();

                 if(p!=player && p.isConnectedToGame() ) {
                    ChatList chatList = p.getChatList();
                    if( chatList!=null )
                    	player.setChatList( chatList );
                 }
             }
          }

          ChatList myChatList = player.getChatList();
          
          if( myChatList == null )
              return; // no chats in the room

       // 7 - We send the chats available to our client...
          Hashtable chatRooms = myChatList.getChatRooms();

          synchronized( chatRooms ) {
             Iterator it = chatRooms.values().iterator();
              	 
             while( it.hasNext() ) {
             	 ChatRoom cRoom = (ChatRoom)it.next();
                 ChatRoomCreatedMessage crcMsg =
                         new ChatRoomCreatedMessage( cRoom.getPrimaryKey(),
                                                     cRoom.getName(), cRoom.getCreatorPrimaryKey() );

                 player.sendMessage( crcMsg );
             }
          }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To send an error message to the client.
    */
     public void sendError( PlayerImpl player, String message ) {
         Debug.signal( Debug.ERROR, this, message );

         ScreenPoint pReset = null;

         if( player.getLocation().isRoom() && player.getMyRoom()!=null )
             pReset = player.getMyRoom().getInsertionPoint();
         else
             pReset = new ScreenPoint(-1, -1);

         player.sendMessage( new ResetPositionMessage( primaryKey, player.getLocation(),
                                                       pReset.x, pReset.y ));
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

