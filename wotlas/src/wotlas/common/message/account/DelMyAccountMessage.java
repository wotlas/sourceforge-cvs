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

package wotlas.common.message.account;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.libs.net.NetMessage;
import wotlas.common.message.MessageRegistry;

/** 
 * To delete an account. (Message Sent by Client)
 *
 * @author Petrus
 */

public class DelMyAccountMessage extends NetMessage
{
 /*------------------------------------------------------------------------------------*/

  /** serverID where the client last connected
   */
      protected int serverID;

  /** serverID where the client was first created
   */
      private int originalServerID;      

 /** local clientID of the server where the client was first created
   */
      private int localClientID;
  
  /** Password
   */
      protected String password;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public DelMyAccountMessage() {
          super( MessageRegistry.ACCOUNT_CATEGORY, AccountMessageCategory.DELETE_MY_ACCOUNT_MSG );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with the client's login & password.
   *
   * @param login login
   * @param password password
   */
     public DelMyAccountMessage( int serverID, int originalServerID, int localClientID, String password ) {
         this();
         this.serverID = serverID;
         this.originalServerID = originalServerID;
         this.localClientID = localClientID;
         this.password = password;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void encode( DataOutputStream ostream ) throws IOException {
         ostream.writeInt( serverID );
         ostream.writeInt( originalServerID );
         ostream.writeInt( localClientID );
         writeString( password, ostream );         
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void decode( DataInputStream istream ) throws IOException {
          serverID = istream.readInt();
          originalServerID = istream.readInt();
          localClientID = istream.readInt();
          password = readString( istream );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}