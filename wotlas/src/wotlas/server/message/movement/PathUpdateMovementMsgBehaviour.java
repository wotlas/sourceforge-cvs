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

import wotlas.utils.Debug;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.movement.*;
import wotlas.common.message.chat.*;
import wotlas.common.universe.*;
import wotlas.common.Player;
import wotlas.common.chat.*;
import wotlas.server.*;
import wotlas.server.message.chat.*;

/**
 * Associated behaviour to the PathUpdateMovementMessage...
 *
 * @author Aldiss
 */

public class PathUpdateMovementMsgBehaviour extends PathUpdateMovementMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public PathUpdateMovementMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object context ) {
System.out.println("RECEVIED BEHAVIOUR for "+primaryKey);

        // 0 - The context is here a PlayerImpl.
           PlayerImpl player = (PlayerImpl) context;

           if(primaryKey==null) {
              Debug.signal( Debug.ERROR, this, "No primary key for movement !" );
              return;
           }

           if( !player.getPrimaryKey().equals( primaryKey ) ) {
              Debug.signal( Debug.ERROR, this, "The specified primary Key is not our player one's !" );
              return;
           }

       // 1 - We update our player
          player.getMovementComposer().setUpdate( (MovementUpdateMessage)this );
       
       // 2 - We send the update to other players
          if( !player.getLocation().isRoom() )
              return; // nothing to do for world & towns...

          Room room = player.getMyRoom();

          if( room==null ) {
              Debug.signal( Debug.CRITICAL, this, "No current Room for player"+primaryKey+"! "+player.getLocation() );
              return;
          }

       // 2.1 - ... in the current Room
          Hashtable players = room.getPlayers();

          synchronized( players ) {
               Iterator it = players.values().iterator();
                 
                while( it.hasNext() ) {
                    PlayerImpl p = (PlayerImpl)it.next();
                    if(p!=player)
                       p.sendMessage( this );
                }
          }

       // 2.2 - ... and other rooms
          if(room.getRoomLinks()!=null)              
             for( int i=0; i<room.getRoomLinks().length; i++ ) {
                  Room otherRoom = room.getRoomLinks()[i].getRoom1();
                   
                  if( otherRoom==room )
                      otherRoom = room.getRoomLinks()[i].getRoom2();

                  players = otherRoom.getPlayers();

                  synchronized( players ) {
                      Iterator it = players.values().iterator();
                 
                      while( it.hasNext() ) {
                          PlayerImpl p = (PlayerImpl)it.next();
                          p.sendMessage( this );
                      }
                  }
             }

       // 3 - Chat selection/quit
          ChatList chatList = player.getChatList();
                
          if( chatList==null )
              return; // nothing to do

          if( isMoving ) {
             // chat reset
                if( player.getCurrentChatPrimaryKey().equals( ChatRoom.DEFAULT_CHAT ) )
                    return; // nothing to do

             // we leave the current chatroom IF there are still chatters in it
             // and if we are not its creator...
                ChatRoom chatRoom = chatList.getChatRoom( player.getCurrentChatPrimaryKey() );
             
                if( chatRoom!=null && ( chatRoom.getPlayers().size()>1
                    || !chatRoom.getCreatorPrimaryKey().equals(primaryKey) ) ) {
                    RemPlayerFromChatRoomMsgBehaviour remPlayerFromChat =
                        new RemPlayerFromChatRoomMsgBehaviour( primaryKey, player.getCurrentChatPrimaryKey() );

                 // We Send the message to ourselves & the others...
                    try{
                      remPlayerFromChat.doBehaviour( player );
                    }catch( Exception e ) {
                       Debug.signal( Debug.ERROR, this, e );
                       player.setCurrentChatPrimaryKey(ChatRoom.DEFAULT_CHAT);
                    }
                }
          }
          else {
             // chat selection, we search for the closest player
                players = room.getPlayers();
                ChatRoom chatRoom = null;

                synchronized( players ) {
                    Iterator it = players.values().iterator();
                 
                    while( it.hasNext() ) {
                       PlayerImpl p = (PlayerImpl)it.next();
                       if( p!=player && p.isConnectedToGame() ) {
                         int dx = p.getX()-player.getX();
                         int dy = p.getY()-player.getY();
                         
                          if( dx*dx+dy*dy < ChatRoom.MIN_CHAT_DISTANCE ) {
                              chatRoom = chatList.getChatRoom( p.getCurrentChatPrimaryKey() );                              
                              if( chatRoom==null ) continue;
                              
                              chatRoom.addPlayer( player );
                              player.setCurrentChatPrimaryKey( chatRoom.getPrimaryKey() );
                              player.setIsChatMember( false );
                              break;
                          }
                       }
                    }
                }

                if( chatRoom==null )
                    return; // no player near us, or no chat...

             // New chat selected !
                player.sendMessage( new SetCurrentChatRoomMessage( chatRoom.getPrimaryKey(), chatRoom.getPlayers() ) );

             // We advertise our presence
                players = chatRoom.getPlayers();
                AddPlayerToChatRoomMessage aMsg = new AddPlayerToChatRoomMessage( primaryKey, chatRoom.getPrimaryKey() );
             
                synchronized( players ) {
                    Iterator it = players.values().iterator();
                    
                    while( it.hasNext() ) {
                    	PlayerImpl p = (PlayerImpl) it.next();
                    	p.sendMessage( aMsg );
                    }
                }
          }

       // end !
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

