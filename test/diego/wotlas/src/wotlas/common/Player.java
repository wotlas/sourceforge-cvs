/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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

package wotlas.common;

import wotlas.common.character.*;
import wotlas.common.universe.*;
import wotlas.common.movement.*;
import wotlas.common.objects.*;

import wotlas.libs.net.NetMessage;

import java.io.*;

/** Interface of a Wotlas Player.
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.server.PlayerImpl
 * @see wotlas.client.PlayerImpl
 */

public interface Player extends PreciseLocationOwner
{
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Is this player a Master player ? ( directly controlled  by the client )
    * @return true if this is a Master player, false otherwise.
    */
      public boolean isMaster();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player location.
    *
    *  @param wotlasLocation
    */
      public void setLocation( WotlasLocation wotlasLocation );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the player's X position.
   *
   *  @param x
   */
      public void setX( int x );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the player's Y position.
   *
   *  @return y
   */
      public void setY( int y );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player name ( short name )
    *
    *  @return player name
    */
      public String getPlayerName();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player name ( short name )
    *
    *  @param playerName
    */
      public void setPlayerName( String playerName );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's full name.
    *
    *  @param otherPlayerKey the key of player who requested player's full name
    *  @return player full name ( should contain the player name )
    */
      public String getFullPlayerName( Player otherPlayer );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's full name ( short name )
    *
    *  @param fullPlayerName
    */
      public void setFullPlayerName( String fullPlayerName );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player primary Key ( account name or any unique ID )
    *
    *  @return player primary key
    */
      public String getPrimaryKey();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's primary Key ( account name or any unique ID )
    *
    *  @param primaryKey player primary key
    */
      public void setPrimaryKey( String primaryKey );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player character past.
    *
    *  @return player past
    */
      public String getPlayerPast();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's past.
    *
    *  @param playerPast past
    */
      public void setPlayerPast( String playerPast );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player away message.
    *
    *  @return player away Message
    */
      public String getPlayerAwayMessage();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's away message.
    *
    *  @param playerAwayMessage msg
    */
      public void setPlayerAwayMessage( String playerAwayMessage );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's character.
    *
    *  @return player character
    */
      // public WotCharacter getWotCharacter();
      public BasicChar getBasicChar();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's character.
    *
    *  @param wotCharacter player character
    */
      public void setBasicChar( BasicChar basicChar );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's object manager
    *
    *  @return player object manager
    */
      public ObjectManager getObjectManager();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's object manager.
    *
    *  @param objectManager player object manager
    */
      public void setObjectManager( ObjectManager objectManager );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's movement Composer.
    *
    *  @return player MovementComposer
    */
      public MovementComposer getMovementComposer();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's movement Composer.
    *
    *  @param movement MovementComposer.
    */
      public void setMovementComposer( MovementComposer movementComposer );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's current Room ( if we are in a Room ).
    * @return current Room, null if we are not in a room.
    */
      public Room getMyRoom();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Is this player connected to the game ?
    * @return true if the player is in the game, false if the client is not connected.
    */
      public boolean isConnectedToGame();
    
    /** To get the player's state (disconnected/connected/away)
     *
     * @return player state
     */  
      public PlayerState getPlayerState();

    /** To set the player's state (disconnected/connected/away)
     *
     * @param value player state
     */        
      public void setPlayerState(PlayerState playerState);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set if this player is connected to the game.
    * @param true if the player is in the game, false if the client is not connected.
    */
      public void setIsConnectedToGame( boolean isConnected );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To send a message to this player on the client or server side. If called on
   *  the client side it sends the message to the server. If called on the server
   *  side it sends the message to the client.
   *
   * @param message message to send to the player.
   */
     public void sendMessage( NetMessage message );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the synchronization ID. This ID is used to synchronize this player on the
    *  client & server side. The ID is incremented only when the player changes its map.
    *  Movement messages that have a bad syncID are discarded.
    * @return sync ID
    */
     public byte getSyncID();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the synchronization ID. See the getter for an explanation on this ID.
    *  @param syncID new syncID
    */
     public void setSyncID(byte syncID);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
