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
 
package wotlas.common.message.description;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.*;

import wotlas.libs.net.NetMessage;
import wotlas.common.screenobject.*;
import wotlas.common.Player;


/** 
 * The message the GameServer sends to tell us to add a screenObjects... (Message Sent by Server).
 *
 * @author Aldiss, Petrus, Diego
 */

public class AddScreenObjectToTileMapMessage extends NetMessage
{
    
    protected ScreenObject item;
    
 /*------------------------------------------------------------------------------------*/

    /** Constructor. Just initializes the message category and type.
    */
    public AddScreenObjectToTileMapMessage() {
        super();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with the ScreenObject object.
    *
    * @param item ScreenObject to send
    */
    public AddScreenObjectToTileMapMessage( ScreenObject item) {
        super();
        this.item = item;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void encode( DataOutputStream ostream ) throws IOException {
         try{
            new ObjectOutputStream(ostream).writeObject( item ); 
         } catch (Exception e) {
            System.out.println("diego: error, should still decide how to manage this error");
            e.printStackTrace();
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void decode( DataInputStream istream ) throws IOException {
         try {
             item = (ScreenObject) new ObjectInputStream(istream).readObject();             
         } catch (Exception e) {
             System.out.println(" diego: error, should still decide how to manage this error");
            e.printStackTrace();
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}