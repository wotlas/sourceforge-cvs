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

package wotlas.server;

import wotlas.common.character.*;
import wotlas.common.chat.*;
import wotlas.server.message.chat.*;

import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.net.NetPersonality;
import wotlas.libs.net.NetMessage;

import wotlas.libs.pathfinding.*;

import wotlas.common.*;
import wotlas.common.chat.*;
import wotlas.common.message.movement.*;
import wotlas.common.message.description.*;
import wotlas.common.universe.*;
import wotlas.utils.*;

import java.util.*;

/** Class of a Wotlas Player. It is the class that, in certain way, a client gets connected to.
 *  All the client messages have a server PlayerImpl context.
 *
 * @author Aldiss
 * @see wotlas.common.Player
 * @see wotlas.common.NetConnectionListener
 */

public class PlayerImpl implements Player, NetConnectionListener
{
 /*------------------------------------------------------------------------------------*/

   /** Player's primary key (usually the client account name)
    */
       private String primaryKey;

   /** Player location
    */
       private WotlasLocation location;

   /** Player name
    */
       private String playerName;

   /** Player full name
    */
       private String fullPlayerName;

   /** Player character's past
    */
       private String playerPast;

   /** WotCharacter Class
    */
       private WotCharacter wotCharacter;

   /** Movement Composer
    */
       private MovementComposer movementComposer = (MovementComposer) new PathFollower();

 /*------------------------------------------------------------------------------------*/

   /** Our NetPersonality, useful if we want to send messages !
    */
       transient private NetPersonality personality;

   /** Our current Room ( if we are in a Room, null otherwise )
    */
       transient private Room myRoom;

 /*------------------------------------------------------------------------------------*/

   /** Player ChatRooms : is the list of the current room.
    */
       transient private ChatList chatList;

   /** Current Chat PrimaryKey : the chat we are currently looking.
    */
       transient private String currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT; // everlasting chat set as default

   /** are we a member of this chat ? or just eavesdropping ?
    */
       transient private boolean isChatMember = true; //always member on default chat.

 /*------------------------------------------------------------------------------------*/

   /** Personality Lock
    */
       transient private byte personalityLock[] = new byte[0];

   /** ChatList Lock
    */
       transient private byte chatListLock[] = new byte[0];

 /*------------------------------------------------------------------------------------*/

   /** Constructor for persistence.
    */
      public PlayerImpl() {
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** When this method is called, the player can intialize its own fields safely : all
    *  the game data has been loaded.
    */
      public void init() {
         movementComposer.init( this );
         setLocation( location );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To initialize the player location to the first existent town found.
    *  WARNING : the player is NOT moved in the town... that means this method
    *  is for player creation ONLY.
    */
      public void setDefaultPlayerLocation() {
          // 1 - player initial location : a World...
             WorldManager worldManager = DataManager.getDefaultDataManager().getWorldManager();

             int worldID = worldManager.getAValidWorldID();
             
             if( worldID<0 )
                 Debug.signal( Debug.WARNING, this, "No world data given to initialize player." );

             location = new WotlasLocation();
             location.setWorldMapID( worldID );
             location.setTownMapID( 0 );
             location.setBuildingID( -1 );
             location.setInteriorMapID( -1 );
             location.setRoomID( -1 );

             TownMap tMap = worldManager.getTownMap( location );
             
             if(tMap==null) {
                Debug.signal( Debug.CRITICAL, this, "No towns available." );
                return;
             }

             MapExit mExits[] = tMap.getMapExits();
             
             if( mExits==null || mExits[0]==null ) {
                Debug.signal( Debug.CRITICAL, this, "No mapExits on town 0..." );
                return;
             }

             setX( mExits[0].x+mExits[0].width/2 );
             setY( mExits[0].y+mExits[0].height/2 );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Is this player a Master player ? ( directly controlled  by the client )
    * @return true if this is a Master player, false otherwise.
    */
      public boolean isMaster() {
        return false; // Server PlayerImpl is only a slave player implementation
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the player's X position.
   *
   *  @return x
   */
      public int getX() {
          return (int)movementComposer.getXPosition();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the player's Y position.
   *
   *  @return y
   */
      public int getY() {
          return (int)movementComposer.getYPosition();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the player's X position.
   *
   *  @param x
   */
      public void setX( int x ) {
          movementComposer.setXPosition( (float)x );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the player's Y position.
   *
   *  @return y
   */
      public void setY( int y ) {
          movementComposer.setYPosition( (float)y );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player location.
    *
    *  @return player WotlasLocation
    */
      public WotlasLocation getLocation(){
          return new WotlasLocation(location);
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player location.
    *
    *  @param new player WotlasLocation
    */
      public void setLocation( WotlasLocation myLocation ){
             location = myLocation;

             if( location.isRoom() && DataManager.getDefaultDataManager()!=null )
                 myRoom = DataManager.getDefaultDataManager().getWorldManager().getRoom( location );
             else {
             	 if( location.isRoom() )
              	     Debug.signal( Debug.CRITICAL, this, "Room not found !!! location is:"+location );
                 myRoom = null;
             }

          // Current Chat set to default
             currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT;
             isChatMember = false;
             
             synchronized( chatListLock ) {
             	chatList = null;
             }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player name ( short name )
    *
    *  @return player name
    */
      public String getPlayerName() {
         return playerName;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's full name.
    *
    *  @return player full name ( should contain the player name )
    */
      public String getFullPlayerName() {
         return fullPlayerName;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player primary Key ( account name )
    *
    *  @return player primary key
    */
      public String getPrimaryKey() {
         return primaryKey;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player character past.
    *
    *  @return player past
    */
      public String getPlayerPast() {
         return playerPast;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's name ( short name )
    *
    *  @param player name
    */
      public void setPlayerName( String playerName ) {
           this.playerName = playerName;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's primary key.
    *
    *  @param primary key
    */
      public void setPrimaryKey( String primaryKey ) {
           this.primaryKey = primaryKey;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's past.
    *
    *  @param playerPast past
    */
      public void setPlayerPast( String playerPast ) {
           this.playerPast = playerPast;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's full name.
    *
    *  @param player full name ( should contain the player name )
    */
      public void setFullPlayerName( String fullPlayerName ) {
          this.fullPlayerName = fullPlayerName;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's character.
    *
    *  @return player character
    */
      public WotCharacter getWotCharacter() {
        return wotCharacter; 
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's character.
    *
    *  @param wotCharacter new player character
    */
      public void setWotCharacter( WotCharacter wotCharacter ) {
         this.wotCharacter = wotCharacter;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's movement Composer.
    *
    *  @return player MovementComposer
    */
      public MovementComposer getMovementComposer() {
          return movementComposer;
      }
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's movement Composer.
    *
    *  @param movement MovementComposer.
    */
      public void setMovementComposer( MovementComposer movementComposer ) {
          this.movementComposer = movementComposer;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's current Room ( if we are in a Room ).
    */
      public Room getMyRoom() {
        return myRoom;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's chatList.
    */
      public void setChatList( ChatList chatList ) {
      	 synchronized( chatListLock ) {
            this.chatList = chatList;
         }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's chatList.
    */
      public ChatList getChatList() {
      	 synchronized( chatListLock ) {
            return chatList;
         }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the primary key of the chat the player is now using.
    * @return currentChatPrimaryKey
    */
      public String getCurrentChatPrimaryKey() {
          return currentChatPrimaryKey;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the current chat used
    * @param currentChatPrimaryKey
    */
      public void setCurrentChatPrimaryKey( String currentChatPrimaryKey ) {
      	this.currentChatPrimaryKey = currentChatPrimaryKey;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
      
   /** are we a member of this chat ? or just eavesdropping ?
    * @return true if we are a real member of the current chat
    */
      public boolean isChatMember() {
      	   return isChatMember;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** are we a member of this chat ? or just eavesdropping ?
    * @param isChatMember if we are a real member of the current chat set it to true.
    */
      public void setIsChatMember( boolean isChatMember ) {
      	   this.isChatMember = isChatMember;
      }

 /*------------------------------------------------------------------------------------*/

   /** Is this player connected to the game ? ( not synchronized )
    * @return true if the player is in the game, false if the client is not connected.
    */
      public boolean isConnectedToGame() {
            if(personality==null)
               return false;
            return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set if this player is connected to the game. (not used on this server side )
    * @param true if the player is in the game, false if the client is not connected.
    */
      public void setIsConnectedToGame( boolean isConnected ) {
      	// no external update possible on the server side !
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when a new network connection is created on this player.
   *
   * @param personality the NetPersonality object associated to this connection.
   */
     public void connectionCreated( NetPersonality personality ) {

             synchronized( personalityLock ) {
                 this.personality = personality;
             }

          // We signal our connection to players in the game
          // ... and players in the rooms near us
             if(location.isRoom()) {
              // We send an update to players near us...
                 PlayerConnectedToGameMessage pMsg = new PlayerConnectedToGameMessage(primaryKey,true);

                 if(myRoom==null) {
                    Debug.signal( Debug.ERROR, this, "Player "+primaryKey+" has an incoherent location state");
                    return;
                 }

                 sendMessageToRoom( myRoom, pMsg, true );
                 sendMessageToNearRooms( myRoom, pMsg, false );
             }

             Debug.signal(Debug.NOTICE,null,"Connection opened for player "+playerName);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when the network connection of the client is no longer
   * of this world.
   *
   * @param personality the NetPersonality object associated to this connection.
   */
     public void connectionClosed( NetPersonality personality ) {

         // 0 - no more messages will be sent...
             synchronized( personalityLock ) {
                 this.personality = null;
             }

             Debug.signal(Debug.NOTICE, null, "Connection closed on player: "+playerName);

         // 1 - Leave any current chat...
            if( !currentChatPrimaryKey.equals( ChatRoom.DEFAULT_CHAT ) ) {
                RemPlayerFromChatRoomMsgBehaviour remPlayerFromChat
                     = new RemPlayerFromChatRoomMsgBehaviour( primaryKey, currentChatPrimaryKey );

                try{
                   remPlayerFromChat.doBehaviour( this );
                }catch( Exception e ) {
                   Debug.signal( Debug.ERROR, this, e );
                   currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT;
                }
            }

            synchronized( chatListLock ) {
                chatList = null;
            }

         // 2 - Stop any current movement
            if(location.isRoom())
            {
              // no movement saved on rooms...
                 movementComposer.resetMovement();

              // We send an update to players near us...
              // ... and players in other rooms
                 NetMessage msg[] = new NetMessage[2];
                 msg[0] = (NetMessage) movementComposer.getUpdate();
                 msg[1] = (NetMessage) new PlayerConnectedToGameMessage(primaryKey,false);

                 if(myRoom==null) {
                    Debug.signal( Debug.ERROR, this, "Player "+primaryKey+" has an incoherent location state");
                    return;
                 }

                 sendMessageToRoom( myRoom, msg, true );
                 sendMessageToNearRooms( myRoom, msg, false );
                 return;
            }
            else if(location.isTown()) {
              // no movement saved on towns...
                 movementComposer.resetMovement();
                 return;
            }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Use this method to send a NetMessage to this player. You can use it directly :
   *  it does not lock, does not wait for the message to be sent before returning
   *  AND checks that the player is connected.
   *
   * @param message message to send to the player.
   */
     public void sendMessage( NetMessage message ) {
             synchronized( personalityLock ) {
                if( personality!=null ) {
                    if( ServerDirector.SHOW_DEBUG )
                        System.out.println("Player "+primaryKey+" sending msg: "+message);
                    personality.queueMessage( message );
                }
             }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To close the network connection if any.
   */
     public void closeConnection() {
             synchronized( personalityLock ) {
                if( personality!=null )
                    personality.closeConnection();
             }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To send a message to the players that are in the specified room.
   * @param room target room
   * @param msg message to send
   * @param exceptMe do we have to send the message to us ?
   */
    public void sendMessageToRoom( Room room, NetMessage msg, boolean exceptMe ) {
          Hashtable players = room.getPlayers();

          synchronized( players ) {
               Iterator it = players.values().iterator();
               
               if( exceptMe )
                   while( it.hasNext() ) {
                       PlayerImpl p = (PlayerImpl)it.next();
                       if(p!=this)
                          p.sendMessage( msg );
                   }
               else
                   while( it.hasNext() )
                       ( (PlayerImpl)it.next() ).sendMessage( msg );
          }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To send some messages to the players that are in the specified room.
   * @param room target room
   * @param msg message to send
   * @param exceptMe do we have to send the message to us ?
   */
    public void sendMessageToRoom( Room room, NetMessage msg[], boolean exceptMe ) {
          Hashtable players = room.getPlayers();

          synchronized( players ) {
               Iterator it = players.values().iterator();
               
               if( exceptMe )
                   while( it.hasNext() ) {
                       PlayerImpl p = (PlayerImpl)it.next();
                       if(p!=this)
                          for( int i=0; i< msg.length; i++ )
                               p.sendMessage( msg[i] );
                   }
               else
                   while( it.hasNext() ) {
                       PlayerImpl p = (PlayerImpl)it.next();
                       for( int i=0; i< msg.length; i++ )
                            p.sendMessage( msg[i] );
                   }
          }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To send a message to the players that are in the rooms near the specified room.
   * @param room reference room
   * @param msg message to send
   * @param exceptMe do we have to send the message to us ?
   */
    public void sendMessageToNearRooms( Room room, NetMessage msg[], boolean exceptMe ) {
        if(room.getRoomLinks()!=null)
           for( int j=0; j<room.getRoomLinks().length; j++ ) {
                Room otherRoom = room.getRoomLinks()[j].getRoom1();
  
                if( otherRoom==room )
                    otherRoom = room.getRoomLinks()[j].getRoom2();

                Hashtable players = otherRoom.getPlayers();

                synchronized( players ) {
                    Iterator it = players.values().iterator();
                 
                    if( exceptMe )
                       while( it.hasNext() ) {
                           PlayerImpl p = (PlayerImpl)it.next();
                           if(p!=this)
                              for( int i=0; i< msg.length; i++ )
                                   p.sendMessage( msg[i] );
                       }
                    else
                       while( it.hasNext() ) {
                           PlayerImpl p = (PlayerImpl)it.next();
                           for( int i=0; i< msg.length; i++ )
                                p.sendMessage( msg[i] );
                       }
                }
           }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To send a message to the players that are in the rooms near the specified room.
   * @param room reference room
   * @param msg message to send
   * @param exceptMe do we have to send the message to us ?
   */
    public void sendMessageToNearRooms( Room room, NetMessage msg, boolean exceptMe ) {
        if(room.getRoomLinks()!=null)
           for( int j=0; j<room.getRoomLinks().length; j++ ) {
                Room otherRoom = room.getRoomLinks()[j].getRoom1();
  
                if( otherRoom==room )
                    otherRoom = room.getRoomLinks()[j].getRoom2();

                Hashtable players = otherRoom.getPlayers();

                synchronized( players ) {
                    Iterator it = players.values().iterator();
                 
                    if( exceptMe )
                       while( it.hasNext() ) {
                           PlayerImpl p = (PlayerImpl)it.next();
                           if(p!=this)
                              p.sendMessage( msg );
                       }
                    else
                       while( it.hasNext() )
                           ( (PlayerImpl)it.next() ).sendMessage( msg );
                }
           }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}