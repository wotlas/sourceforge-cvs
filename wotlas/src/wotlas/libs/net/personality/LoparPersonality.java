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
 
package wotlas.libs.net.personality;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import wotlas.libs.net.NetPersonality;
import wotlas.libs.net.NetSender;
import wotlas.libs.net.NetReceiver;
import wotlas.utils.Debug;

/**
 * Lopar has a user-dependent NetPersonality. It waits for your signal before
 * sending messages or processing received messages. It has buffers of 128k.
 *<p>
 * Methods to use with Lopar :
 *<br>
 *   - queueMessage() to prepare the list of messages to send.<p>
 *   - pleaseSendAllMessagesNow() to send all the queued messages.<p>
 *   - pleaseReceiveAllMessagesNow() to process all the received messages within
 *     a certain limit.<p>
 *<br>
 * It's typically what you want for a synchronous client.
 * This personality has only one active thread (NetSender).
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetPersonality
 * @see wotlas.libs.net.NetSender
 * @see wotlas.libs.net.NetReceiver
 */

public class LoparPersonality extends NetPersonality
{

 /*------------------------------------------------------------------------------------*/

  /** Constructor with an already opened socket.
   *
   * @param socket an already opened socket
   * @param context object to give to messages when they arrive.
   * @exception IOException if the socket wasn't already connected.
   */
     public LoparPersonality( Socket socket, Object context ) throws IOException {
           super( socket, context );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with an already opened socket and a local ID to identify
   *  a set of threads ( see NetServer ).
   *
   * @param socket an already opened socket
   * @param context object to give to messages when they arrive.
   * @param localID an ID that identifies a set of threads.
   * @exception IOException if the socket wasn't already connected.
   */
     public LoparPersonality( Socket socket, Object context, byte localID ) throws IOException {
           super( socket, context, localID );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** In this method we create our own NetSender and NetReceiver :
   *  a USER_AGGREGATION NetSender and synchronous NetReceiver.
   *
   * @param socket an already opened socket
   * @param context an object to give to messages as they arrive.
   * @exception IOException if the socket wasn't already connected.
   */
     protected void generatePersonality( Socket socket, Object context )
     throws IOException
     {
       // We change both buffer size to 128k for this socket (default size was 64k)
	  try{
              socket.setReceiveBufferSize(128*1024);
              socket.setSendBufferSize(128*1024);
          }
          catch(SocketException e){
         	Debug.signal( Debug.NOTICE, this, e );
          }

       // NetSender.
          my_netsender = new NetSender( socket, this, NetSender.USER_AGGREGATION, 128*1024 );

       // NetReceiver
          my_netreceiver = new NetReceiver( socket, this, true, context, 128*1024 );
          my_netreceiver.setMaxMessageLimitPerUserCall( 20 );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

