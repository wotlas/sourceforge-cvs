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

package wotlas.common.message.chat;

import wotlas.common.universe.WotlasLocation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.libs.net.NetMessage;

/** 
 * To write a message in a ChatRoom. (Message sent by Client or Server)
 *
 * @author Petrus, Aldiss
 */

public class SendTextMessage extends NetMessage
{
 
 /*------------------------------------------------------------------------------------*/

  /** Id of the sender
   */
  protected String senderPrimaryKey;
 
  /** Sender Full Player Name
   */
  protected String senderFullName;
 
  /** Id of the ChatRoom
   */
  protected String chatRoomPrimaryKey;
  
  /** The message to send
   */
  protected String message;

  /** Voice Sound Level, see wotlas.common.chat.ChatRoom
   */
  protected byte voiceSoundLevel;
  
 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
  public SendTextMessage() {
    super();        
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with parameters.
   */
  public SendTextMessage(String senderPrimaryKey, String senderFullName, String chatRoomPrimaryKey, String message, byte voiceSoundLevel ) {
    super();
    this.senderPrimaryKey = senderPrimaryKey;
    this.senderFullName = senderFullName;
    this.chatRoomPrimaryKey = chatRoomPrimaryKey;
    this.message = message;
    this.voiceSoundLevel = voiceSoundLevel;
  }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
  public void encode( DataOutputStream ostream ) throws IOException {
    ostream.writeUTF( senderPrimaryKey );
    ostream.writeUTF( senderFullName );
    ostream.writeUTF( chatRoomPrimaryKey );
    ostream.writeUTF( message );
    ostream.writeByte( voiceSoundLevel );
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
  public void decode( DataInputStream istream ) throws IOException {
    senderPrimaryKey = istream.readUTF();
    senderFullName = istream.readUTF();
    chatRoomPrimaryKey = istream.readUTF();
    message = istream.readUTF();
    voiceSoundLevel = istream.readByte();
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the message for this chat message...
   */
   public void setMessage( String message ) {
      this.message = message;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the message for this chat message...
   */
   public String getMessage() {
      return message;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get chatRoomPrimaryKey for this chat message...
   */
   public String getChatRoomPrimaryKey() {
      return chatRoomPrimaryKey;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** To get the voice sound level of this message.
   */
   public byte getVoiceSoundLevel() {
     return voiceSoundLevel;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** To set the voice sound level of this message.
   *  @param voiceSoundLevel as defined in ChatRoom.
   */
   public void setVoiceSoundLevel( byte voiceSoundLevel ) {
        this.voiceSoundLevel=voiceSoundLevel;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}