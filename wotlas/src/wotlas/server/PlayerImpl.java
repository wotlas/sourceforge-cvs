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
import wotlas.common.universe.*;
import wotlas.utils.*;

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

   /** Player's current x position
    */
       private int x;

   /** Player's current y position
    */
       private int y;

   /** WotCharacter Class
    */
       private WotCharacter wotCharacter;

 /*------------------------------------------------------------------------------------*/

   /** is this player moving ? 
    */
       private boolean isMoving = false;

   /** End Point of movement.
    */
       private ScreenPoint endPoint;

   /** Time when we initialized this movement on the Server.
    */
       private long movementTimeStamp;

 /*------------------------------------------------------------------------------------*/

   /** Personality Lock
    */
       transient private byte personalityLock[] = new byte[1];

 /*------------------------------------------------------------------------------------*/

   /** Movement Composer
    */
       transient private MovementComposer movementComposer = (MovementComposer) new PathFollower();

   /** Our NetPersonality, useful if we want to send messages !
    */
       transient private NetPersonality personality;

   /** Our current Room ( if we are a Room, null otherwise )
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
      	  return x;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the player's Y position.
   *
   *  @return y
   */
      public int getY() {
      	  return y;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the player's X position.
   *
   *  @param x
   */
      public void setX( int x ) {
      	this.x = x;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the player's Y position.
   *
   *  @return y
   */
      public void setY( int y ) {
      	this.y = y;
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
             location.setTownMapID( -1 );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** When this method is called, the player can intialize its own fields safely : all
    *  the game data has been loaded.
    */
      public void init() {
         // nothing to do here for now...
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player location.
    *
    *  @return player WotlasLocation
    */
      public WotlasLocation getLocation(){
          return location;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player location.
    *
    *  @param new player WotlasLocation
    */
      public void setLocation( WotlasLocation myLocation ){
             location = myLocation;
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

   // other getters & setters for persistence

      public void setIsMoving( boolean isMoving ) {
      	this.isMoving = isMoving;
      }

      public boolean getIsMoving() {
      	return isMoving;
      }

      public void setEndPoint( ScreenPoint endPoint ) {
      	this.endPoint = endPoint;
      }

      public ScreenPoint getEndPoint() {
      	return endPoint;
      }

      public void setMovementTimeStamp( long movementTimeStamp ) {
      	this.movementTimeStamp = movementTimeStamp;
      }

      public long getMovementTimeStamp() {
      	return movementTimeStamp;
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
             System.out.println("Connection opened on this player");
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
             System.out.println("Connection closed on this player");
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Use this method to send a NetMessage to this player. You can use it directly :
   *  it does not lock, does not wait for the message to be sent before returning
   *  AND checks that the player is connected.
   *
   * @param message message to send to the player.
   * @return true if the message was sent, false if the client was not connected.
   */
     public boolean sendMessage( NetMessage message ) {

             synchronized( personalityLock ) {
             	if( personality!=null ) {
                    personality.queueMessage( message );
                    return true;
                }
             }

         return false;
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

}