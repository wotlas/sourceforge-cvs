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
   * @param sessionContext an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object sessionContext ) {

        // 0 - The sessionContext is here a PlayerImpl.
           PlayerImpl player = (PlayerImpl) sessionContext;

           if(primaryKey==null) {
              Debug.signal( Debug.ERROR, this, "No primary key for movement !" );
              return;
           }

           if( !player.getPrimaryKey().equals( primaryKey ) ) {
              Debug.signal( Debug.ERROR, this, "The specified primary Key is not our player one's !" );
              return;
           }

       // 0 - Is the syncID of this message the same as ours ?
          if( syncID!=player.getSyncID() ) {
              Debug.signal( Debug.NOTICE, this, "Message discarded: bad sync ID." );
              return;
          }

       // 1 - We update our player
          player.getMovementComposer().setUpdate( (MovementUpdateMessage)this );
       
       // 2 - We send the update to other players
       // ... in the current Room & other rooms near me
          if( !player.getLocation().isRoom() )
              return; // nothing to do for worlds & towns...

          Room room = player.getMyRoom();

          player.sendMessageToRoom( room, this, true );
          player.sendMessageToNearRooms( room, this, false );


       // 3 - Chat selection/quit
          ChatList chatList = player.getChatList();
          if( chatList==null )  return; // nothing to do

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
                Hashtable players = room.getPlayers();
                ChatRoom chatRoom = null;

                synchronized( players ) {
                    Iterator it = players.values().iterator();
                 
                    while( it.hasNext() ) {
                       PlayerImpl p = (PlayerImpl)it.next();
                       if( p!=player && p.isConnectedToGame() ) {
                         int dx = p.getX()-player.getX();
                         int dy = p.getY()-player.getY();
                         
                          if( dx*dx+dy*dy < ChatRoom.MIN_CHAT_DISTANCE ) {

                              if( p.getCurrentChatPrimaryKey().equals( ChatRoom.DEFAULT_CHAT )
                                  && !player.getCurrentChatPrimaryKey().equals( ChatRoom.DEFAULT_CHAT ) ) {
                                 // we change the current chat of THE OTHER player

                                 // ATTENTION PLEASE !! FROM THIS LINE (if reached) THE CURRENT
                                 // PLAYER ( player ) IS NOW THE SELECTED PLAYER HERE ( p )

                                   chatRoom = chatList.getChatRoom( player.getCurrentChatPrimaryKey() );
                                   player = p; // <- change
                              }
                              else
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
                player.sendMessage( new SetCurrentChatRoomMessage( primaryKey, chatRoom.getPrimaryKey(), chatRoom.getPlayers() ) );            
             
             // A player is a plain member of a chat only
             // when he speaks the first time, but his/her name appear
             // in the chatters's list.
             // So, we advertise our presence
                chatRoom.sendMessageToChatRoom(
                   new AddPlayerToChatRoomMessage( primaryKey, player.getFullPlayerName(), chatRoom.getPrimaryKey() ) );
          }

       // end !
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

