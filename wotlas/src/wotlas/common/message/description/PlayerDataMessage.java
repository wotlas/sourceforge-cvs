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

package wotlas.common.message.description;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.libs.net.NetMessage;
import wotlas.common.message.MessageRegistry;
import wotlas.common.message.movement.*;
import wotlas.common.*;
import wotlas.common.character.WotCharacter;
import wotlas.common.universe.WotlasLocation;

import wotlas.utils.Tools;

/** 
 * To send player data (Message Sent by Server).
 *
 * @author Aldiss
 */

public class PlayerDataMessage extends NetMessage
{
 /*------------------------------------------------------------------------------------*/

  /** Player interface.
   */
      protected Player player;

  /** Do we have to write/read public info or all the player's data ?
   */
      protected boolean publicInfoOnly;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public PlayerDataMessage() {
          super( MessageRegistry.DESCRIPTION_CATEGORY,
                 DescriptionMessageCategory.PLAYER_DATA_MSG );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor for eventual subclasses. Just initializes the message category and type.
   *
   * @param msg_category message's category in your NetRegistry.
   * @param msg_type message's type in the associated NetCategory.
   */
     public PlayerDataMessage( byte msg_category, byte msg_type) {
          super( msg_category, msg_type );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with the Player object.
   *
   * @param player Player object to send.
   * @param publicInfoOnly tells if we have to write/read public info or all the player's data
   */
     public PlayerDataMessage( Player player, boolean publicInfoOnly ) {
         this();
         this.player = player;
         this.publicInfoOnly = publicInfoOnly;
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

      // Player Data
         writeString( player.getPlayerName(), ostream );
         writeString( player.getFullPlayerName(), ostream );
         writeString( player.getPrimaryKey(), ostream );

         if(!publicInfoOnly)
            writeString( player.getPlayerPast(), ostream );

         ostream.writeBoolean( player.isConnectedToGame() );

      // Movement Composer
         writeString( player.getMovementComposer().getClass().getName(), ostream );

         MovementUpdateMessage updateMsg = player.getMovementComposer().getUpdate();

         writeString( updateMsg.getClass().getName(), ostream );
         updateMsg.encode( ostream );

      // Wotlas Character Data
         writeString( player.getWotCharacter().getClass().getName(), ostream );
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
         player = (Player) Tools.getInstance( "wotlas.client.PlayerImpl" );

      // Wotlas Location
         WotlasLocation wotLoc = new WotlasLocation();

         wotLoc.setWorldMapID( istream.readInt() );
         wotLoc.setTownMapID( istream.readInt() );
         wotLoc.setBuildingID( istream.readInt() );
         wotLoc.setInteriorMapID( istream.readInt() );
         wotLoc.setRoomID( istream.readInt() );

         player.setLocation( wotLoc );

      // Player Data
         player.setPlayerName( readString( istream ) );
         player.setFullPlayerName( readString( istream ) );
         player.setPrimaryKey( readString( istream ) );

         if(!publicInfoOnly)
             player.setPlayerPast(  readString( istream ) );

         player.setIsConnectedToGame( istream.readBoolean() );

      // Movement Composer
         MovementComposer mvComposer = (MovementComposer) Tools.getInstance( readString( istream ) );
         MovementUpdateMessage uMsg = (MovementUpdateMessage) Tools.getInstance( readString( istream ) );
         uMsg.decode( istream );

      // Wotlas Character
         WotCharacter wotChar = (WotCharacter) Tools.getInstance( readString( istream ) );
         
         wotChar.decode( istream, publicInfoOnly );
         player.setWotCharacter( wotChar );

      // Movement Composer init
         mvComposer.init( player );
         mvComposer.setUpdate( uMsg ); // in this order, because the player must have been fully initialized
         player.setMovementComposer( mvComposer );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

