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
import wotlas.libs.net.NetReceiver;
import wotlas.libs.net.NetSender;
import wotlas.utils.Debug;

/**
 * This is a synchronous user-dependent NetConnection. It waits for your signal before
 * sending messages or processing received messages. It has io buffers of 128kB.
 *<p>
 * Methods to use with this connection type :
 *<br>
 *   - queueMessage() to queue a message.<br>
 *   - sendAllMessages() to send all the queued messages.<br>
 *   - receiveAllMessages() to process all the received messages (within a certain limit, default:20).<br>
 *<br>
 * It's typically what you want for a synchronous tick() action.
 * This connection has only one active thread (NetSender).
 *</p>
 * @author Aldiss
 * @see wotlas.libs.net.NetConnection
 * @see wotlas.libs.net.NetSender
 * @see wotlas.libs.net.NetReceiver
 */

public class SynchronousNetConnection extends NetConnection {

    /*------------------------------------------------------------------------------------*/

    /** Constructor with an already opened socket.
     *
     * @param socket an already opened socket
     * @exception IOException if the socket wasn't already connected.
     */
    public SynchronousNetConnection(Socket socket) throws IOException {
        super(socket, null);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with an already opened socket and session object.
     *
     * @param socket an already opened socket
     * @param sessionContext object to give to messages when they arrive.
     * @exception IOException if the socket wasn't already connected.
     */
    public SynchronousNetConnection(Socket socket, Object sessionContext) throws IOException {
        super(socket, sessionContext);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** In this method we create our own NetSender and NetReceiver :
     *  a USER_AGGREGATION NetSender and synchronous NetReceiver.
     *
     * @param socket an already opened socket
     * @param sessionContext an object to give to messages as they arrive.
     * @exception IOException if the socket wasn't already connected.
     */
    @Override
    protected void init(Socket socket, Object sessionContext) throws IOException {
        // We change both buffer size to 128k for this socket (default size was 64k)
        try {
            socket.setReceiveBufferSize(128 * 1024);
            socket.setSendBufferSize(128 * 1024);
        } catch (SocketException e) {
            Debug.signal(Debug.NOTICE, this, e);
        }

        // NetSender.
        this.myNetsender = new NetSender(socket, this, NetSender.USER_AGGREGATION, 128 * 1024);

        // NetReceiver
        this.myNetreceiver = new NetReceiver(socket, this, true, sessionContext, 128 * 1024);
        this.myNetreceiver.setMaxMessageLimitPerUserCall(20);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
