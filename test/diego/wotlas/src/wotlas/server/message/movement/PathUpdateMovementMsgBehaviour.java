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

import wotlas.utils.Debug;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.movement.*;
import wotlas.common.message.chat.*;
import wotlas.common.router.MessageRouter;
import wotlas.common.universe.*;
import wotlas.common.Player;
import wotlas.common.chat.*;
import wotlas.server.*;
import wotlas.server.message.chat.*;

/**
 * Associated behaviour to the PathUpdateMovementMessage...
 *
 * @author Aldiss, Diego
 *
 *  the movement updates are sended to everyone in a room
 *  i need it too for tilemaps, and i will need it too for npc movements.
 *
 */

public class PathUpdateMovementMsgBehaviour extends PathUpdateMovementMessage implements NetMessageBehaviour {

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

          if( primaryKey==null || !player.getPrimaryKey().equals( primaryKey ) ) {
             Debug.signal( Debug.ERROR, this, "The specified primary Key is not our player one's ! "+primaryKey );
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
       // ... in the current room/tilemap & other rooms near me
          MessageRouter mRouter = player.getMessageRouter();
          if( player.getLocation().isRoom())
              mRouter.sendMessage( this, player, MessageRouter.EXTENDED_GROUP );
          else if (player.getLocation().isTileMap())
              mRouter.sendMessage( new ScreenObjectPathUpdateMovementMessage(this)
              , player, MessageRouter.EXTENDED_GROUP );
          else
              return; // nothing to do for worlds & towns...

          PlayerImpl nearPlayer = null;

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
                Hashtable players = mRouter.getPlayers();
                ChatRoom chatRoom = null;

                synchronized( players ) {
                    Iterator it = players.values().iterator();
                 
                    while( it.hasNext() ) {
                       PlayerImpl p = (PlayerImpl)it.next();
                       nearPlayer=null;

                       if( p!=player && p.isConnectedToGame() ) {
                         int dx = p.getX()-player.getX();
                         int dy = p.getY()-player.getY();
                         
                          if( dx*dx+dy*dy < ChatRoom.MIN_CHAT_DISTANCE ) {

                              boolean weAreInDefaultChat = player.getCurrentChatPrimaryKey().equals( ChatRoom.DEFAULT_CHAT );
                              boolean heIsInDefaultChat = p.getCurrentChatPrimaryKey().equals( ChatRoom.DEFAULT_CHAT );

                              if( !weAreInDefaultChat && heIsInDefaultChat ) {
                                // we add the near player to our chat
                                   chatRoom = chatList.getChatRoom( player.getCurrentChatPrimaryKey() );
                                   nearPlayer = p;
                              }
                              else if( weAreInDefaultChat && !heIsInDefaultChat ) {
                                // we add the near player to our chat
                                   chatRoom = chatList.getChatRoom( p.getCurrentChatPrimaryKey() );
                              }

                              if( chatRoom==null )
                                  continue; // no near chat room change

                              if(nearPlayer==null) {
                                // we are influenced by the nearest player, we do not influence him
                                 chatRoom.addPlayer( player );
                                 player.setCurrentChatPrimaryKey( chatRoom.getPrimaryKey() );
                                 player.setIsChatMember( false );
                              }
                              else {
                                // we influence the nearest player, he does not influence us
                                 chatRoom.addPlayer( nearPlayer );
                                 nearPlayer.setCurrentChatPrimaryKey( chatRoom.getPrimaryKey() );
                                 nearPlayer.setIsChatMember( false );
                              }

                              break; // END OF THIS LOOP ! we successfully found a near player
                          }
                       }
                    }
                }

                if( chatRoom==null )
                    return; // no player near us, or no chat...

             // New chat selected !
                if(nearPlayer==null)
                   player.sendMessage( new SetCurrentChatRoomMessage( chatRoom.getPrimaryKey(), chatRoom.getPlayers() ) );
                else
                   nearPlayer.sendMessage( new SetCurrentChatRoomMessage( chatRoom.getPrimaryKey(), chatRoom.getPlayers() ) );

             // A player is a plain member of a chat only
             // when he speaks the first time, but his/her name appear
             // in the chatters's list.
             // So, we advertise our presence
                if(nearPlayer==null)
                   chatRoom.sendMessageToChatRoom(
                       new AddPlayerToChatRoomMessage( primaryKey, chatRoom.getPrimaryKey() ) );
                else
                   chatRoom.sendMessageToChatRoom(
                       new AddPlayerToChatRoomMessage( nearPlayer.getPrimaryKey(), chatRoom.getPrimaryKey() ) );
          }

       // end !
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}