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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import wotlas.libs.net.NetMessage;
import wotlas.common.environment.*;

/** 
 * To send the Server Environment
 * (Message Sent by Server).
 *
 * @author Diego
 */

public class ServerEnvironmentMessage extends NetMessage
{

    protected EnvironmentManager data;
    
 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public ServerEnvironmentMessage() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with the server environment
   */
/*
     public ServerEnvironmentMessage(EnvironmentManager data) {
          super();
          this.data = new EnvironmentManager( data );
     }
*/

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void encode( DataOutputStream ostream ) throws IOException {
         try{
             new ObjectOutputStream(ostream).writeObject( new EnvironmentManager(EnvironmentManager.getServerEnvironment()) );
         } catch (Exception e) {
             e.printStackTrace();
             System.out.println(" diego(2): error, should still decide how to manage this error");
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void decode( DataInputStream istream ) throws IOException {
         try {
             EnvironmentManager.setServerEnvironment( (EnvironmentManager) new ObjectInputStream(istream).readObject() );
         } catch (Exception e) {
             e.printStackTrace();
             System.out.println(" diego(1): error, should still decide how to manage this error");
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}