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

package wotlas.server.bots.alice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.libs.net.NetMessage;

/** 
 * A message that can be sent from the wotlas server or the Alice wotlas server.
 * It contains a message request for Alice or an answer sent by Alice (depends who
 * sends the message alice or wotlas... )
 *
 * @author Aldiss
 */

public class AliceWotlasMessage extends NetMessage {
 
 /*------------------------------------------------------------------------------------*/

  /** Primary key of the player that sent the original message.
   */
    protected String playerPrimaryKey;
 
  /** Primary key of the bot who is the target of the message.
   */
    protected String botPrimaryKey;

  /** Message sent to AliceBot or answered by aliceBot.
   */
    protected String message;

  /** Server ID of the wotlas server that started the message transaction
   */
    protected int serverID;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public AliceWotlasMessage() {
      super();        
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with parameters.
   */
    public AliceWotlasMessage(String playerPrimaryKey, String botPrimaryKey, String message, int serverID ) {
       super();
       this.playerPrimaryKey = playerPrimaryKey;
       this.botPrimaryKey = botPrimaryKey;
       this.message = message;
       this.serverID = serverID;
    }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
    public void encode( DataOutputStream ostream ) throws IOException {
       ostream.writeUTF( playerPrimaryKey );
       ostream.writeUTF( botPrimaryKey );
       ostream.writeUTF( message );
       ostream.writeInt( serverID );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
    public void decode( DataInputStream istream ) throws IOException {
       playerPrimaryKey = istream.readUTF();
       botPrimaryKey = istream.readUTF();
       message = istream.readUTF();
       serverID = istream.readInt();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}