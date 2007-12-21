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

package wotlas.libs.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import wotlas.libs.net.message.EndOfConnectionMessage;
import wotlas.libs.net.message.PingMessage;
import wotlas.utils.Debug;

/** A NetReceiver waits for NetMessage to arrive on an opened socket.
 *  It decodes them and execute their associated NetMessageBehaviour.
 *<br>
 * There are two types of NetReceiver : asynchronous (sync=false), synchronous (sync=true).
 * In the first case a thread is automatically started and will automatically process
 * received messages. In the second case, no thread is started. The user'll have to call
 * the receiveAllMessages() method regularly to allow the NetReceiver to process messages.
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetMessageFactory
 * @see wotlas.libs.net.NetThread
 */

public class NetReceiver extends NetThread {

    /*------------------------------------------------------------------------------------*/

    /** Communication stream to receive data
     */
    private DataInputStream inStream;

    /** Do we have to wait for a user signal to execute messages ?
     *  Determine if we work synchronously or asynchronously.
     */
    private boolean sync;

    /** Our message factory
     */
    private NetMessageFactory factory;

    /** A link to our NetConnection
     */
    private NetConnection connection;

    /** Session Object to give to messages when they arrive.
     */
    private Object sessionContext;

    /** For the synchronous NetReceiver : 
     *  maximum number of messages to process per user call ( receiveAllMessages() ).
     */
    private int maxMsg;

    /** Do we have to send back ping messages ?
     */
    private boolean sendBackPingMessages;

    /*------------------------------------------------------------------------------------*/

    /**  Constructor. Should be called only by the NetServer & NetClient classes.
     *   We assume that a NetMessageFactory has already been created.
     *
     *   By default we don't send PingMessages back... use the setSendBackPingMessages
     *   method to change that.
     *
     * @param socket a previously created & connected socket.
     * @param connection a NetConnection linked to the specified socket.
     * @param sync do we have to work synchronously or asynchronously.
     * @param sessionContext session object to give to messages when they arrive.
     * @param bufferSize buffer size (in bytes) for the buffered input stream.
     * @exception IOException if the socket wasn't already connected.
     */
    public NetReceiver(Socket socket, NetConnection connection, boolean sync, Object sessionContext, int bufferSize) throws IOException {
        super(socket);
        this.sync = sync;
        this.sessionContext = sessionContext;
        this.connection = connection;
        this.sendBackPingMessages = false;

        if (sync)
            this.maxMsg = 15; // default value

        // we retrieve & construct some useful handles
        this.inStream = new DataInputStream(getBufferedInputStream(bufferSize));
        this.factory = NetMessageFactory.getMessageFactory();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Method for the async NetReceiver.
     *  Never call this method it's done automatically.
     */
    @Override
    public void run() {
        if (this.sync)
            return; // we have nothing to do here

        try {
            do {
                // we wait for message to arrive and read its message class name.
                String msgSuperClassName = this.inStream.readUTF();

                // we reconstruct the message and execute its associated code.
                try {
                    NetMessageBehaviour msg = this.factory.getNewMessageInstance(msgSuperClassName);
                    ((NetMessage) msg).decode(this.inStream); // decode data in the message behaviour class

                    // what kind of message do we have here ?
                    if (msg instanceof PingMessage) {
                        if (this.sendBackPingMessages)
                            this.connection.queueMessage((NetMessage) msg); // send back the PingMessage
                        else
                            this.connection.receivedPingMessage(((PingMessage) msg).getSeqID());
                    } else if (msg instanceof EndOfConnectionMessage) {
                        break; // end of connection
                    } else
                        msg.doBehaviour(this.sessionContext); // execute the message's behaviour code...
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Debug.signal(Debug.WARNING, this, e);
                    this.inStream.skipBytes(this.inStream.available()); // cleanse the source ;)
                }
            } while (!shouldStopThread());
        } catch (SocketException se) {
            // Socket closed a bit roughly
            Debug.signal(Debug.WARNING, this, "Connection closed a bit roughly : " + se.toString());
        } catch (IOException ioe) {
            // Socket error, connection was probably closed a little roughly...
            Debug.signal(Debug.WARNING, this, "Connection closed : " + ioe.toString());
        } catch (Exception e) {
            // Real error !
            // e.printStackTrace();
            Debug.signal(Debug.ERROR, this, e); // serious error while processing message
        }

        // we ask the NetConnection to perform some cleanup
        // and signal that the connection was closed ( connectionListener )
        this.connection.close();
        this.inStream = null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To process all the received messages.
     *  This is the method to call in a case of a sync NetReceiver. We stop to process messages in 
     *  either cases : (1) there is no more messages,
     *                 (2) the maxMsg limit has been reached.
     */
    public void receiveAllMessages() {
        if (!this.sync)
            return; // we have nothing to do here

        int msgCounter = 0;

        try {
            while (this.inStream.available() != 0 && msgCounter < this.maxMsg) {
                // 1 - we wait for message to arrive and read its message class name.
                String msgSuperClassName = this.inStream.readUTF();

                // 2 - we reconstruct the message and execute its associated code.
                try {
                    NetMessageBehaviour msg = this.factory.getNewMessageInstance(msgSuperClassName);
                    ((NetMessage) msg).decode(this.inStream); // decode data in the message behaviour class

                    // what kind of message do we have here ?
                    if (msg instanceof PingMessage) {
                        if (this.sendBackPingMessages)
                            this.connection.queueMessage((NetMessage) msg); // send back the PingMessage
                        else
                            this.connection.receivedPingMessage(((PingMessage) msg).getSeqID());
                    } else if (msg instanceof EndOfConnectionMessage) {
                        this.connection.close();
                        break; // end of connection
                    } else
                        msg.doBehaviour(this.sessionContext); // execute the message's behaviour code...
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Debug.signal(Debug.WARNING, this, e);
                    this.inStream.skipBytes(this.inStream.available()); // cleanse the source ;)
                    return;
                }

                msgCounter++;
            }
        } catch (IOException ioe) {
            // Socket error, connection was probably closed a little roughly...
            Debug.signal(Debug.WARNING, this, ioe);

            // we ask the NetConnection to perform some cleanup
            // and signal that the connection was closed ( connectionListener )
            this.connection.close();
        } catch (Exception e) {
            // serious error while processing message
            Debug.signal(Debug.ERROR, this, e);

            // we ask the NetConnection to perform some cleanup
            // and signal that the connection was closed ( connectionListener )
            this.connection.close();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the maximum number of messages to process per user call.
     * @param maxMsg maximum number of messages
     */
    public void setMaxMessageLimitPerUserCall(int maxMsg) {
        this.maxMsg = maxMsg;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the maximum number of messages to process per user call.
     * @return the maximum number of messages
     */
    public int getMaxMessageLimitPerUserCall() {
        return this.maxMsg;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the current sessionContext. This is the object that we'll give to message
     *  behaviours when we'll have to call the doBehaviour() method on them.
     *
     * @param sessionContext the new sessionContext.
     */
    public void setContext(Object sessionContext) {
        this.sessionContext = sessionContext;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Waits for a message to arrive. Useful in some cases when the NetReceiver
     *  is synchronous. This method does nothing if the NetReceiver is asynchronous.
     *
     * IMPORTANT: this method locks forever if there is no incoming message...
     *            sorry... no timeout.
     *
     * @exception IOException if something goes wrong
     */
    public void waitForMessage() throws IOException {
        if (!this.sync)
            return;

        // We mark the stream
        this.inStream.mark(1000);

        // Wait for data...
        this.inStream.readByte();

        // Reset Stream state
        this.inStream.reset();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** is the NetReceiver synchronous ?
     *
     * @return true if synchronous, false if asynchronous
     */
    public boolean isSynchronous() {
        return this.sync;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Do we have to send back ping messages ?
     * @param sendBack set to true to automatically send back ping messages.
     */
    public void sendBackPingMessages(boolean sendBack) {
        this.sendBackPingMessages = sendBack;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
