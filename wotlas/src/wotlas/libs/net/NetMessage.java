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
 * A NetMessage is a simple message that you give to your NetPersonality ( which handles it
 * to the NetSender) so it transmits it to a foreign ( i.e. NetReceiver ).
 *
 * To create a new message simply extends the NetMessage class : <pre>
 *
 *    class PasswordMsg extends NetMessage {
 *         protected int mySecretID;
 *
 *         public PasswordMsg() {
 *              super(); // mandatory call to NetMessage constructor
 *         }
 *
 *         public PasswordMsg( int mySecretID ) {
 *              super();
 *              this.mySecretID = mySecretID;
 *         }
 *
 *         void encode( DataOutputStream ostream ) throws IOException {
 *              ostream.writeInt( mySecretID );
 *         }
 *
 *         void decode( DataInputStream istream ) throws IOException {
 *              mySecretID = istream.readInt();
 *         }
 *    }
 * </pre><br>
 *
 * The empty constructor is mandatory. The 'mySecretID' fiels is protected
 * for an easy access from the child classes.
 *
 * <p>Note that a NetMessage comes along with a NetMessageBehaviour
 * which contains the associated code to execute on the remote side.</p>
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetPersonality
 * @see wotlas.libs.net.NetMessageBehaviour
 * @see java.io.DataOutputStream
 */

public abstract class NetMessage {

 /*------------------------------------------------------------------------------------*/

  /** Message ClassName (fully qualified class name).
   */
     private String messageClassName;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message's class name field.
   */
     public NetMessage() {     	
       // we search for the first non-NetMessageBehaviour
     	if( NetMessageBehaviour.class.isAssignableFrom( getClass() ) )
            messageClassName = getClass().getSuperclass().getName();
        else
            messageClassName = getClass().getName(); // ok, we can get this class name
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the message's class name.
   * @return the message's class name.
   */
     public String getMessageClassName() {
         return messageClassName;
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

}

