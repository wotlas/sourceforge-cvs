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
import wotlas.common.character.*;

import wotlas.common.message.account.*;

import wotlas.server.*;
import wotlas.server.chat.ChatCommandProcessor;

import wotlas.utils.Debug;

/**
 * Associated behaviour to the SendTextMsgBehaviour...
 *
 * @author Petrus
 */

public class SendTextMsgBehaviour extends SendTextMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  public SendTextMsgBehaviour() {
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

    // 0 - big messages are truncated
       if(message.length()>ChatRoom.MAXIMUM_MESSAGE_SIZE)
          message = message.substring( 0, ChatRoom.MAXIMUM_MESSAGE_SIZE-4)+"...";

       Hashtable players = null;
       WotlasLocation myLocation = player.getLocation();

    // 0.1 - test shortcut/commands...
       if(message.charAt(0)=='/') {
          ChatCommandProcessor processor = DataManager.getDefaultDataManager().getChatCommandProcessor();

          if( processor.processCommand( message, player, this ) )
             return; // end of message process if the command returns true
                     // if the command returns false we continue the message process
       }

    // 1 - We send the message back to the user.
       if( chatRoomPrimaryKey.equals(player.getCurrentChatPrimaryKey()) ) {
           if(voiceSoundLevel==ChatRoom.SHOUTING_VOICE_LEVEL)
              message = message.toUpperCase();
           player.sendMessage(this);
       }
       else if(voiceSoundLevel!=ChatRoom.SHOUTING_VOICE_LEVEL) {
       	// player is trying to speak in a ChatRoom not near to him.
       	   message = "<i>No one can hear you !</i>";
           player.sendMessage(this);
           return;       	   
       }

    // 2 - We analyze who we must receive this message... it depends on location...          
               
    // 2.1 - ROOM CASE
       if ( myLocation.isRoom() ) {
       	 // 2.1.1 - Get Current Room
            Room myRoom = player.getMyRoom();    
            if (myRoom==null) {
                Debug.signal( Debug.ERROR, this, "Error could not get current room ! "+player.getLocation() );
                player.sendMessage( new WarningMessage("Your player has a bad location on Server ! Please report this bug !\nLocation:"+player.getLocation()) );
                return;
            }

         // 2.1.2 - Voice Level
            switch( voiceSoundLevel ) {
                case ChatRoom.WHISPERING_VOICE_LEVEL :
                   // is it the default chat ? or another ?
                     boolean isDefaultChat = chatRoomPrimaryKey.equals(ChatRoom.DEFAULT_CHAT);
                   
                     if(isDefaultChat)
                     	players = myRoom.getPlayers();
                     else {
                        player.setIsChatMember(true);

                        if( player.getChatList()==null ) {
                            Debug.signal( Debug.ERROR, this, "No Chat List for player: "+player.getPrimaryKey() );
                            return;
                        }
                        
                        players = player.getChatList().getPlayers( chatRoomPrimaryKey );
                     }

                     if(players==null) {
                        Debug.signal( Debug.ERROR, this, "No players found for chat: "+chatRoomPrimaryKey );
                        return;
                     }

                  // send the message
                     synchronized(players) {
                        Iterator it = players.values().iterator();
                        PlayerImpl p = null;

                        while ( it.hasNext() ) {
                            p = (PlayerImpl)it.next();
                            if (p!=player && ( p.isChatMember() || isDefaultChat ) )
                                p.sendMessage( this );
                        }
                     }

                     return;

                case ChatRoom.NORMAL_VOICE_LEVEL :
                   // is it the default chat ? or another ?
                     if(chatRoomPrimaryKey.equals(ChatRoom.DEFAULT_CHAT))
                     	players = myRoom.getPlayers();
                     else {
                        player.setIsChatMember(true);
                        if( player.getChatList()==null ) {
                            Debug.signal( Debug.ERROR, this, "No Chat List for player: "+player.getPrimaryKey() );
                            return;
                        }
                        
                        players = player.getChatList().getPlayers( chatRoomPrimaryKey );
                     }

                     if(players==null) {
                        Debug.signal( Debug.ERROR, this, "No players found for chat: "+chatRoomPrimaryKey );
                        return;
                     }

                  // send the message
                     synchronized(players) {
                        Iterator it = players.values().iterator();
                        PlayerImpl p = null;

                        while ( it.hasNext() ) {
                            p = (PlayerImpl)it.next();
                            if (p!=player)
                                p.sendMessage( this );
                        }
                     }

                     return;

                case ChatRoom.SHOUTING_VOICE_LEVEL :
                     players = myRoom.getPlayers();
                
                     if(players==null) {
                        Debug.signal( Debug.ERROR, this, "No players found for room: "+myRoom );
                        return;
                     }

                     message = message.toUpperCase();

                  // send the message to the players of the room
                     synchronized(players) {
                        Iterator it = players.values().iterator();
                        PlayerImpl p = null;

                        while ( it.hasNext() ) {
                            p = (PlayerImpl)it.next();
                            if (p!=player)
                                p.sendMessage( this );
                        }
                     }

                  // And players in other rooms
                     if(myRoom.getRoomLinks()==null)
                        return;

                     for( int j=0; j<myRoom.getRoomLinks().length; j++ ) {
                         Room otherRoom = myRoom.getRoomLinks()[j].getRoom1();
  
                         if( otherRoom==myRoom )
                             otherRoom = myRoom.getRoomLinks()[j].getRoom2();

                         players = otherRoom.getPlayers();

                         synchronized( players ) {
                            Iterator it = players.values().iterator();
                 
                            while( it.hasNext() ) {
                               PlayerImpl p = (PlayerImpl)it.next();
                               p.sendMessage( this );
                            }
                         }
                     }

                     return;
            }  // end of switch

          return; // should never be reached
       }

    // 2.2 - TOWN CASE

       if ( myLocation.isTown() ) {
       	 // 2.2.1 - Get Town
            TownMap town = DataManager.getDefaultDataManager().getWorldManager().getTownMap(myLocation);

            if (town==null) {
            	Debug.signal( Debug.ERROR, this, "Town not Found : "+myLocation+" Player:"+player.getPrimaryKey());
            	return;
            }

         // 2.2.2 - Retrieve list of players to send message to...
         //         ( everybody on the default town chat... )
            players = town.getPlayers();
       }

    // 2.3 - WORLD CASE
       if ( myLocation.isWorld() ) {
       	 // 2.3.1 - Get World
            WorldMap world = DataManager.getDefaultDataManager().getWorldManager().getWorldMap(myLocation);

            if (world==null) {
            	Debug.signal( Debug.ERROR, this, "World not Found : "+myLocation+" Player:"+player.getPrimaryKey());
            	return;
            }

         // 2.2.2 - Retrieve list of players to send message to...
         //         ( everybody on the default world chat... )
            players = world.getPlayers();
       }


    // 2.4 - We send the message for the Town & World Cases
       if(players==null) {
          Debug.signal( Debug.ERROR, this, "No players found for location: "+player.getLocation() );
          return;
       }
    
       synchronized(players) {
          Iterator it = players.values().iterator();
          PlayerImpl p;

            while ( it.hasNext() ) {
               p = (PlayerImpl)it.next();
               if (p!=player)
                   p.sendMessage( this );
            }
       }
  }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}
  
