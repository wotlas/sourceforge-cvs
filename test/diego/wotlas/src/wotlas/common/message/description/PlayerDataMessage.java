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

package wotlas.common.message.description;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.libs.net.NetMessage;
import wotlas.common.message.movement.*;
import wotlas.common.*;
import wotlas.common.character.WotCharacter;
import wotlas.common.universe.WotlasLocation;
import wotlas.common.movement.*;

import wotlas.utils.Tools;

/** 
 * To send player data (Message Sent by Server).
 *
 * @author Aldiss, Petrus, Diego
 */

public class PlayerDataMessage extends NetMessage
{
 /*------------------------------------------------------------------------------------*/

  /** Player interface.
   */
      protected Player player;
  
  /** key of destinated player.
   */
      protected Player otherPlayer;

  /** Do we have to write/read public info or all the player's data ?
   */
      protected boolean publicInfoOnly;

  /** Player Class to use when building the Player object.
   *  Default is client implementation. Use the appropriate constructor to change that.
   */
      private String playerClass = "wotlas.client.PlayerImpl";

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public PlayerDataMessage() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with the Player object.
   *
   * @param player Player object to send.
   * @param publicInfoOnly tells if we have to write/read public info or all the player's data
   */
     public PlayerDataMessage( Player player, boolean publicInfoOnly ) {
         super();
         this.player = player;
         this.publicInfoOnly = publicInfoOnly;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with the Player object and playerClass to use.
   *
   * @param player Player object to send.
   * @param publicInfoOnly tells if we have to write/read public info or all the player's data
   * @param playerClass to use when building the Player object.
   */
     public PlayerDataMessage( Player player, boolean publicInfoOnly, String playerClass ) {
         super();
         this.player = player;
         this.publicInfoOnly = publicInfoOnly;
         this.playerClass = playerClass;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    
   /** To set otherPlayerKey
    *
    * @param otherPlayerKey key of player this message is sent to
    */
     public void setOtherPlayer(Player otherPlayer) {
         this.otherPlayer = otherPlayer;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void encode( DataOutputStream ostream ) throws IOException {
         ostream.writeBoolean( publicInfoOnly );

      // Wotlas Location
         ostream.writeInt( player.getLocation().getWorldMapID() );
         ostream.writeInt( player.getLocation().getTownMapID() );
         ostream.writeInt( player.getLocation().getBuildingID() );
         ostream.writeInt( player.getLocation().getInteriorMapID() );
         ostream.writeInt( player.getLocation().getRoomID() );
         ostream.writeInt( player.getLocation().getTileMapID() );

      // Player Data
         ostream.writeUTF( player.getPlayerName() );
         ostream.writeUTF( player.getFullPlayerName(otherPlayer) );
         ostream.writeUTF( player.getPrimaryKey() );

         if(!publicInfoOnly) {
            ostream.writeUTF( player.getPlayerPast() );
            ostream.writeUTF( player.getPlayerAwayMessage() );
         }

         ostream.writeBoolean( player.isConnectedToGame() );
         ostream.writeByte( player.getPlayerState().value );

      // Sync ID
         if(!publicInfoOnly)
            ostream.writeByte( player.getSyncID() );

      // Movement Composer
         ostream.writeUTF( player.getMovementComposer().getClass().getName() );

         MovementUpdateMessage updateMsg = player.getMovementComposer().getUpdate();

         ostream.writeUTF( updateMsg.getClass().getName() );
         updateMsg.encode( ostream );

      // Wotlas Character Data
         ostream.writeUTF( player.getWotCharacter().getClass().getName() );
         player.getWotCharacter().encode( ostream, publicInfoOnly ); // call to encode character's data
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void decode( DataInputStream istream ) throws IOException {
         publicInfoOnly = istream.readBoolean();

      // Player Client Instance creation ( no direct call to "server"
      // or "client" packages are issued from the "common" package )
         player = (Player) Tools.getInstance( playerClass );

      // Wotlas Location
         WotlasLocation wotLoc = new WotlasLocation();

         wotLoc.setWorldMapID( istream.readInt() );
         wotLoc.setTownMapID( istream.readInt() );
         wotLoc.setBuildingID( istream.readInt() );
         wotLoc.setInteriorMapID( istream.readInt() );
         wotLoc.setRoomID( istream.readInt() );
         wotLoc.setTileMapID( istream.readInt() );

         player.setLocation( wotLoc );

      // Player Data
         player.setPlayerName( istream.readUTF() );
         player.setFullPlayerName( istream.readUTF() );
         player.setPrimaryKey( istream.readUTF() );

         if(!publicInfoOnly){
             player.setPlayerPast( istream.readUTF() );
             player.setPlayerAwayMessage( istream.readUTF() );
         }

         player.setIsConnectedToGame( istream.readBoolean() );
         player.getPlayerState().value = istream.readByte();

      // Sync ID
         if( !publicInfoOnly )
            player.setSyncID( istream.readByte() );

      // Movement Composer
         MovementComposer mvComposer = (MovementComposer) Tools.getInstance( istream.readUTF() );
         MovementUpdateMessage uMsg = (MovementUpdateMessage) Tools.getInstance( istream.readUTF() );
         uMsg.decode( istream );

      // Wotlas Character
         WotCharacter wotChar = (WotCharacter) Tools.getInstance( istream.readUTF() );
         
         wotChar.decode( istream, publicInfoOnly );
         player.setWotCharacter( wotChar );

      // Movement Composer init
         mvComposer.init( player );
         mvComposer.setUpdate( uMsg ); // in this order, because the player must have been fully initialized
         player.setMovementComposer( mvComposer );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player subject of this message.
    */
     public Player getPlayer() {
     	return player;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

