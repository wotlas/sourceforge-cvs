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


/** 
 * A warning message that should display to the client in a pop-up window.
 * (Message Sent by Server). It means the account of the user should have
 * been sent to another server but  the operation failed.
 *
 * @author Aldiss
 */

public class RedirectErrorMessage extends NetMessage
{
 /*------------------------------------------------------------------------------------*/

   // information
      protected String errorMsg;

   // optional reset position
      protected int xReset;
      protected int yReset;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public RedirectErrorMessage() {
         super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with IDs and no special reset position.
   *
   */
     public RedirectErrorMessage(String errorMsg) {
         super();
         this.errorMsg = errorMsg;
         xReset=-1;
         yReset=-1;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with IDs and a special reset position. The player will be set to this
   *  new position to avoid conflicts.
   */
     public RedirectErrorMessage(String errorMsg, int xReset, int yReset ) {
         super();
         this.errorMsg = errorMsg;
         this.xReset=xReset;
         this.yReset=yReset;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void encode( DataOutputStream ostream ) throws IOException {
         ostream.writeUTF( errorMsg );
         ostream.writeInt(xReset);
         ostream.writeInt(yReset);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void decode( DataInputStream istream ) throws IOException {
          errorMsg = istream.readUTF();
          xReset = istream.readInt();
          yReset = istream.readInt();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

