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

package wotlas.libs.net.connection;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetSender;
import wotlas.libs.net.NetReceiver;
import wotlas.utils.Debug;

/**
 * Torm has a very independent NetConnection. It sends messages (with aggregation)
 * and processes received messages without any user intervention. It has standard
 * buffers of 64k.
 *<br>
 * The only method to use with Torm is :
 *<br>
 *   - queueMessage() to queue the messages to send.
 *<p><br>
 * All the work of sending, processing messages is done automatically.
 * It's typically what you want for a server that deals with many clients.
 * This connection has two active thread (NetSender & NetReceiver).
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetConnection
 * @see wotlas.libs.net.NetSender
 * @see wotlas.libs.net.NetReceiver
 */

public class TormConnection extends NetConnection
{

 /*------------------------------------------------------------------------------------*/

  /** Constructor with an already opened socket.
   *
   * @param socket an already opened socket
   * @param sessionContext an object to give to messages as they arrive.
   * @exception IOException if the socket wasn't already connected.
   */
     public TormConnection( Socket socket, Object sessionContext ) throws IOException {
           super( socket, sessionContext );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with an already opened socket and a local ID to identify
   *  a set of threads ( see NetServer ).
   *
   * @param socket an already opened socket
   * @param sessionContext object to give to messages when they arrive.
   * @param localID an ID that identifies a set of threads.
   * @exception IOException if the socket wasn't already connected.
   */
     public TormConnection( Socket socket, Object sessionContext, byte localID ) throws IOException {
           super( socket, sessionContext, localID );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** In this method we create our own NetSender and NetReceiver :
   *  a AGGREGATE_MESSAGES NetSender and asynchronous NetReceiver.
   *
   * @param socket an already opened socket
   * @param sessionContext an object to give to messages as they arrive.
   * @exception IOException if the socket wasn't already connected.
   */
     protected void generateConnection( Socket socket, Object sessionContext )
     throws IOException
     {
       // We change both buffer size to 64k for this socket (default is normally 64k)
	  try{
              socket.setReceiveBufferSize(64*1024);
              socket.setSendBufferSize(64*1024);
          }
          catch(SocketException e){
         	Debug.signal( Debug.NOTICE, this, e );
          }

       // NetSender with default aggregation limit & timeout.
          myNetsender = new NetSender( socket, this, NetSender.AGGREGATE_MESSAGES, 128*1024 );

       // NetReceiver, asynchronous. It processes messages as they arrive.
          myNetreceiver = new NetReceiver( socket, this, false, sessionContext, 128*1024 );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

