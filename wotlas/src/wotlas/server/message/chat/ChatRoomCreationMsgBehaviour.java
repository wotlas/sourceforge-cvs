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
import wotlas.common.universe.*;
import wotlas.common.message.account.*;

import wotlas.server.*;

import wotlas.utils.Debug;

/**
 * Associated behaviour to the ChatRoomCreationMessage...
 *
 * @author Petrus
 */

public class ChatRoomCreationMsgBehaviour extends ChatRoomCreationMessage implements NetMessageBehaviour
{

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  public ChatRoomCreationMsgBehaviour() {
    super();
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

    // 0 - We check the length of the chat room name
       if( name.length() > ChatRoom.MAXIMUM_NAME_SIZE )
           name = name.substring(0,ChatRoom.MAXIMUM_NAME_SIZE-1);

    // 1 - We get the list of players of the current room/town/world 
       Hashtable players = null;
    
       if ( location.isWorld() ) {
            WorldMap world = ServerDirector.getDataManager().getWorldManager().getWorldMap(location);
            if(world!=null)
                players = world.getPlayers();
       } else if ( location.isTown() ) {
            TownMap town = ServerDirector.getDataManager().getWorldManager().getTownMap(location);
            if (town!=null)
                players = town.getPlayers();
       } else if ( location.isRoom() ) {
            Room currentRoom = player.getMyRoom();    
            if (currentRoom!=null)
                players = currentRoom.getPlayers();
       }

       if( players==null ) {
           Debug.signal( Debug.ERROR, this, "Error could not get current players in "+player.getLocation() );
           player.sendMessage( new WarningMessage("Error could not find your location! please report the bug ! - "+player.getLocation()) );
           return;
       }

    // 2 - Do we have to delete the previous chatroom ?
       if( !player.getCurrentChatPrimaryKey().equals(ChatRoom.DEFAULT_CHAT) ) {
            RemPlayerFromChatRoomMsgBehaviour remPlayerFromChat =
                   new RemPlayerFromChatRoomMsgBehaviour( player.getPrimaryKey(), player.getCurrentChatPrimaryKey() );

         // We Send the message to ourselves & the others...
            try{
                remPlayerFromChat.doBehaviour( player );
            }catch( Exception e ) {
                Debug.signal( Debug.ERROR, this, e );
                player.setCurrentChatPrimaryKey(ChatRoom.DEFAULT_CHAT);
            }
       }

    // 3 - We try to create the new chatroom
       ChatRoom chatRoom = new ChatRoom();
       chatRoom.setPrimaryKey( ChatRoom.getNewChatPrimaryKey() );
       chatRoom.setName(name);
       chatRoom.setCreatorPrimaryKey(creatorPrimaryKey);
       chatRoom.addPlayer(player);

       synchronized( players ) {
           ChatList chatList = player.getChatList();
           
           if(chatList==null) {
           	chatList = (ChatList) new ChatListImpl();
           	
             // We set the chatList to all the players in the chat room...
           	Iterator it = players.values().iterator();
           	
           	while( it.hasNext() ) {
           	    PlayerImpl p = (PlayerImpl) it.next();
           	    p.setChatList( chatList );
           	}
           }

           if(chatList.getNumberOfChatRooms() > ChatRoom.MAXIMUM_CHATROOMS_PER_ROOM )
              return; // can't add ChatRoom : too many already !

           chatList.addChatRoom( chatRoom );
       }

       player.setCurrentChatPrimaryKey( chatRoom.getPrimaryKey() );
       player.setIsChatMember(true);

    // 4 - We advertise the newly created chatroom
    // We send the information to all players of the same room or town or world
    // ( we are one of them, that's why we don't test if p!=player )
       ChatRoomCreatedMessage crcMsg = new ChatRoomCreatedMessage( chatRoom.getPrimaryKey(),
                                                                   name, creatorPrimaryKey );

       synchronized(players) {
          Iterator it = players.values().iterator();
          PlayerImpl p;

          while ( it.hasNext() ) {
              p = (PlayerImpl)it.next();
              p.sendMessage( crcMsg );
          }
       }
  }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}
  
