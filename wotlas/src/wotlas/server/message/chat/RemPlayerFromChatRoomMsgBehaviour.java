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

package wotlas.server.message.chat;

import java.io.IOException;
import java.util.*;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.chat.*;

import wotlas.common.chat.*;
import wotlas.common.Player;
import wotlas.common.router.MessageRouter;
import wotlas.common.universe.*;
import wotlas.common.message.account.*;

import wotlas.server.*;

import wotlas.utils.Debug;

/**
 * Associated behaviour to the RemPlayerFromChatRoomMessage...
 *
 * @author Petrus, Aldiss
 */

public class RemPlayerFromChatRoomMsgBehaviour extends RemPlayerFromChatRoomMessage implements NetMessageBehaviour {

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public RemPlayerFromChatRoomMsgBehaviour() {
       super();
    }

 /*------------------------------------------------------------------------------------*/

  /** Constructor with parameters : useful to directly call the behaviour.
   */
    public RemPlayerFromChatRoomMsgBehaviour(String senderPrimaryKey, String chatRoomPrimaryKey) {
       super(senderPrimaryKey,chatRoomPrimaryKey);
    }

 /*------------------------------------------------------------------------------------*/
  
  /** Associated code to this Message...
   *
   * @param sessionContext an object giving specific access to other objects needed to process
   *        this message.
   */
    public void doBehaviour( Object sessionContext ) {
    
    // The sessionContext is here a PlayerImpl.
       PlayerImpl player = (PlayerImpl) sessionContext;
       WotlasLocation location = player.getLocation();

    // 0 - security, is it our primary key ?
       if( !player.getPrimaryKey().equals( senderPrimaryKey ) ) {
           Debug.signal( Debug.ERROR, this, "Received a RemovePlayerFromChatMsg that was for: "+chatRoomPrimaryKey+" and not for us : "+player.getPrimaryKey() );
       	   return;
       }

    // 1 - Set player Chat membership to defaults
       player.setCurrentChatPrimaryKey( ChatRoom.DEFAULT_CHAT );
       player.setIsChatMember(false);

    // 2 - We suppress our entry in the chatList
       ChatList chatList = player.getChatList();

       if( chatList==null ){
           Debug.signal( Debug.ERROR, this, "No Chat List available ! can't remove player" );
       	   return;
       }

       ChatRoom chatRoom = chatList.getChatRoom( chatRoomPrimaryKey );

       if( chatRoom==null ){
           Debug.signal( Debug.ERROR, this, "Chat room "+chatRoomPrimaryKey+" not found ! can't remove player" );
       	   return;
       }

       chatRoom.removePlayer( player );
       player.sendMessage( this );    // validation sent to client

    // 3 - Need to suppress the ChatRoom ?
       Hashtable players = chatRoom.getPlayers();

       synchronized( players ) {
           if( players.size()<=0 )
               chatList.removeChatRoom( chatRoomPrimaryKey ); // ok, we remove the chat room
           else {
             // chatRoom is not empty, we just advertise our departure & quit
                chatRoom.sendMessageToChatRoom( this );
                return;
           }
       }

    // 4 - ok, we've just suppressed our past chatRoom, we advertise the destruction...
       ChatRoomDeletedMessage delMsg = new ChatRoomDeletedMessage( chatRoomPrimaryKey );
       MessageRouter mRouter = player.getMessageRouter();

       if( mRouter!=null )
           mRouter.sendMessage( delMsg );
  }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}
  
