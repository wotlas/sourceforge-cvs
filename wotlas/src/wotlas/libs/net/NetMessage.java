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
 
package wotlas.libs.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/** 
 * A NetMessage is a simple message that you give to your NetSender so
 * it transmits it to a foreign NetReceiver.
 *
 * To create a new message simply extends the NetMessage class : <pre>
 *
 *    class PasswordMsg extends NetMessage {
 *         protected int pswd; // my message data
 *
 *         HelloMsg() {
 *              super( MyNetCategory.AUTHENTICATION_MESSAGE, MyNetAuth.PASSWORD_MESSAGE );
 *         }
 *
 *         HelloMsg( int password ) {
 *              this();
 *              pswd = password;
 *         }
 *
 *         void encode( DataOutputStream ostream ) throws IOException {
 *              ostream.writeInt();
 *         }
 *
 *         void decode( DataInputStream istream ) throws IOException {
 *              pswd = istream.readInt();
 *         }
 *    }
 * </pre>
 * The empty constructor is mandatory and must initialize the message's
 * category and type.
 *
 * Note that a NetMessage comes along with a NetMessageBehaviour
 * which contains the associated code to execute on the remote side.
 *
 * The Message's Category must be declared in your NetMessageRegistry interface
 * and it's type in one of your associated NetMessageCategory interface.
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetSender
 * @see wotlas.libs.net.NetMessageBehaviour
 * @see wotlas.libs.net.NetMessageRegistry
 * @see wotlas.libs.net.NetMessageCategory
 * @see java.io.DataOutputStream
 */

public abstract class NetMessage
{

 /*------------------------------------------------------------------------------------*/

  /** Message category.
   */
      private byte msg_category;

  /** Message type.
   */
      private byte msg_type;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   *
   * @param msg_category message's category in your NetRegistry.
   * @param msg_type message's type in the associated NetCategory.
   */
     public NetMessage( byte msg_category, byte msg_type ) {
         this.msg_category = msg_category;
         this.msg_type = msg_type;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where you put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     abstract public void encode( DataOutputStream ostream ) throws IOException;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where you retrieve your message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     abstract public void decode( DataInputStream istream ) throws IOException;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the message's category.
   * 
   * @return the message's category.
   */
     public byte getMessageCategory() {
           return msg_category;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the message's type.
   * 
   * @return the message's type.
   */
     public byte getMessageType() {
           return msg_type;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To read a string written to this stream by a call to the writeString() below.
   *
   * @param istream the stream from which we take the data.
   * @return the read String
   * @exception IOException in case of IO error
   */
     static protected String readString( DataInputStream istream ) throws IOException
     {
       int nb = istream.readInt();
       char s[] = new char[nb];

           for(int i=0; i<nb; i++)
              s[i] = istream.readChar();

       return new String(s,0,nb);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To write a string o a stream. Use the readString() method to read it on the other
   *  side of the stream.
   *
   * @param s the string to write.
   * @param ostream the stream where we put the data.
   * @exception IOException in case of IO error
   */
     static protected void writeString( String s, DataOutputStream ostream ) throws IOException
     {
       ostream.writeInt( s.length() );

           for(int i=0; i<s.length(); i++)
              ostream.writeChar(s.charAt(i));
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

