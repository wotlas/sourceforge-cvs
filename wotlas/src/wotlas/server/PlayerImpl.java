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

import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.net.NetPersonality;
import wotlas.libs.net.NetMessage;

import wotlas.libs.pathfinding.*;

import wotlas.common.*;
import wotlas.common.message.movement.MovementUpdateMessage;
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

   /** WotCharacter Class
    */
       private WotCharacter wotCharacter;

 /*------------------------------------------------------------------------------------*/

   /** Player ChatRooms
    */
       //transient private Hashtable chatRooms;
       transient private ChatListImpl chatList;
       
   /** Number of ChatRooms
    */
       private static int chatCounter = 0;
       
 /*------------------------------------------------------------------------------------*/

   /** Personality Lock
    */
       transient private byte personalityLock[] = new byte[1];

 /*------------------------------------------------------------------------------------*/

   /** Movement Composer
    */
       private MovementComposer movementComposer = (MovementComposer) new PathFollower();

   /** Our NetPersonality, useful if we want to send messages !
    */
       transient private NetPersonality personality;

   /** Our current Room ( if we are in a Room, null otherwise )
    */
       transient private Room myRoom;

 /*------------------------------------------------------------------------------------*/

   /** Constructor for persistence.
    */
      public PlayerImpl() {
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

   /** To initialize the player location to the first existent worlds found.
    *  WARNING : the player is NOT moved in the world... that means this method
    *  is for player creation ONLY.
    */
      public void setPlayerLocationToWorld() {
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

System.out.println("POSITION set to x:"+getX()+" y:"+getY()+" location is "+location);
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** When this method is called, the player can intialize its own fields safely : all
    *  the game data has been loaded.
    */
      public void init() {
         movementComposer.init( this );
         setLocation( location );
System.out.println("Player Init Done: "+location+" myRoom:"+myRoom);
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
              else
                 myRoom = null;
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

  /** This method is called when a new network connection is created on this player.
   *
   * @param personality the NetPersonality object associated to this connection.
   */
     public void connectionCreated( NetPersonality personality ) {

             synchronized( personalityLock ) {
                 this.personality = personality;
             }

          // great we do nothing
             System.out.println("Connection opened for player "+playerName);
        System.out.println( "OPEN isMoving:"+movementComposer.isMoving()+", ref:"+this);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when the network connection of the client is no longer
   * of this world.
   *
   * @param personality the NetPersonality object associated to this connection.
   */
     public void connectionClosed( NetPersonality personality ) {

             synchronized( personalityLock ) {
                 this.personality = null;
             }

          // great we do nothing
             Debug.signal(Debug.NOTICE, this, "Connection closed on player: "+playerName);
System.out.println( "CLOSE isMoving:"+movementComposer.isMoving()+", ref:"+this);
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
System.out.println("SENDING MESSAGE "+message);
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

             if(location.isRoom())
             {
              // no movement saved on rooms...
                 movementComposer.resetMovement();

              // We send an update to players near us...
                 MovementUpdateMessage uMsg = movementComposer.getUpdate();

                 if(myRoom==null) {
                    Debug.signal( Debug.ERROR, this, "Player "+primaryKey+" has an incoherent location state");
                 }

                 Hashtable players = myRoom.getPlayers();

                 synchronized( players ) {
                    Iterator it = players.values().iterator();
                 
                    while( it.hasNext() ) {
                        PlayerImpl p = (PlayerImpl)it.next();
                        if(p!=this)
                           p.sendMessage( uMsg );
                    }
                 }

                 if(myRoom.getRoomLinks()!=null)
                    for( int j=0; j<myRoom.getRoomLinks().length; j++ ) {
                         Room otherRoom = myRoom.getRoomLinks()[j].getRoom1();
  
                         if( otherRoom==myRoom )
                             otherRoom = myRoom.getRoomLinks()[j].getRoom2();

                         players = otherRoom.getPlayers();

                         synchronized( players ) {
                            Iterator it = players.values().iterator();
                 
                            while( it.hasNext() ) {
                               PlayerImpl p = (PlayerImpl)it.next();
                               p.sendMessage( uMsg );
                            }
                         }
                    }
             }
             else if(location.isTown()) {
              // no movement saved on towns...
                 movementComposer.resetMovement();
             }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a valid ChatRoom primaryKey
   */
  synchronized static public String getNewChatRoomID() {
    chatCounter++;
    return "chat-"+chatCounter;
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}