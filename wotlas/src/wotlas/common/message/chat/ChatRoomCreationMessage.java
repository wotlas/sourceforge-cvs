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

package wotlas.common.message.chat;

import wotlas.common.universe.WotlasLocation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.libs.net.NetMessage;

/** 
 * To notice the server that a ChatRoom should be created now. (Message Sent by Client)
 *
 * @author Petrus
 */

public class ChatRoomCreationMessage extends NetMessage
{
 
 /*------------------------------------------------------------------------------------*/
 
  /** Name of the ChatRoom
   */
  protected String name;
  
  /** ID of the player who created the ChatRoom
   */
  protected String creatorPrimaryKey;
  
  /** Location where the ChatRoom was created
   */
  protected WotlasLocation location;
  
 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
  public ChatRoomCreationMessage() {
    super();
  }
  
  /** Constructor with parameters.
   */
  public ChatRoomCreationMessage(String name, String creatorPrimaryKey, WotlasLocation location) {
    super();
    this.name = name;
    this.creatorPrimaryKey = creatorPrimaryKey;
    this.location = location;
  }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
  public void encode( DataOutputStream ostream ) throws IOException {
    ostream.writeUTF( name );
    
    ostream.writeUTF( creatorPrimaryKey );
    
    ostream.writeInt( location.getWorldMapID() );
    ostream.writeInt( location.getTownMapID() );
    ostream.writeInt( location.getBuildingID() );
    ostream.writeInt( location.getInteriorMapID() );
    ostream.writeInt( location.getRoomID() );
    
  }  
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
  public void decode( DataInputStream istream ) throws IOException {
    name = istream.readUTF();
    
    creatorPrimaryKey = istream.readUTF();
    
    WotlasLocation wotLoc = new WotlasLocation();
    wotLoc.setWorldMapID( istream.readInt() );
    wotLoc.setTownMapID( istream.readInt() );
    wotLoc.setBuildingID( istream.readInt() );
    wotLoc.setInteriorMapID( istream.readInt() );
    wotLoc.setRoomID( istream.readInt() );

  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}