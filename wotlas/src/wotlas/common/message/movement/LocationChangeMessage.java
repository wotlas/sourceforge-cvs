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

package wotlas.common.message.movement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.libs.net.NetMessage;
import wotlas.common.message.MessageRegistry;
import wotlas.common.universe.*;

/** 
 * To tell the server that we are changing location (Message Sent by Client or Server).
 *
 * @author Aldiss
 */

public class LocationChangeMessage extends NetMessage
{
 /*------------------------------------------------------------------------------------*/

  /** Primary Key
   */
    protected String primaryKey;
  
  /** WotlasLocation
   */
    protected WotlasLocation location;

  /** x position on new location
   */
    protected int x;

  /** y position on new location
   */
    protected int y;

  /** orientation on new location
   */
    protected float orientation;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public LocationChangeMessage() {
          super( MessageRegistry.MOVEMENT_CATEGORY,
                 MovementMessageCategory.LOCATION_CHANGE_MSG );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with message category and type.
   */
     public LocationChangeMessage(byte msg_category, byte msg_type) {
          super( msg_category, msg_type );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with Player's primaryKey & location.
   */
     public LocationChangeMessage(String primaryKey, WotlasLocation location, int x, int y, float orientation) {
          this();
          this.primaryKey = primaryKey;
          this.location = location;
          this.x = x;
          this.y = y;
          this.orientation = orientation;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void encode( DataOutputStream ostream ) throws IOException {

         writeString( primaryKey, ostream );

         ostream.writeInt( location.getWorldMapID() );
         ostream.writeInt( location.getTownMapID() );
         ostream.writeInt( location.getBuildingID() );
         ostream.writeInt( location.getInteriorMapID() );
         ostream.writeInt( location.getRoomID() );

         ostream.writeInt( x );
         ostream.writeInt( y );
         ostream.writeFloat( orientation );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void decode( DataInputStream istream ) throws IOException {

         primaryKey = readString( istream );

         location = new WotlasLocation();

         location.setWorldMapID( istream.readInt() );
         location.setTownMapID( istream.readInt() );
         location.setBuildingID( istream.readInt() );
         location.setInteriorMapID( istream.readInt() );
         location.setRoomID( istream.readInt() );

         x = istream.readInt();
         y = istream.readInt();
         orientation = istream.readFloat();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

