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

import java.io.IOException;
import java.net.Socket;
import wotlas.libs.net.message.EndOfConnectionMessage;
import wotlas.libs.net.message.PingMessage;
import wotlas.utils.Debug;

/** 
 * A NetConnection brings together a NetSender and NetReceiver to send and receive
 * messages. This class is abstract because there are many different types of
 * NetSender and NetReceiver. You have to create your own ones by redefining
 * the init() method. Default personalities are provided in the
 * wotlas.libs.net.connection package.
 *<br>
 * Useful methods you can invoke to send messages :
 *<br>
 *    - queueMessage( message );
 *      To queue a message you want to send. 
 *      Works with every connection type ( but with different behaviour ).
 *<br>
 *    - sendAllMessages();
 *      Asks the NetSender to send all his queued messages now. It's typically the method
 *      you use in case of a USER_AGGREGATION NetSender.
 *
 *<br><p>
 * Useful methods you can invoke to receive messages :
 *<br>
 *    - receiveAllMessages();
 *      Asks the NetReceiver to process all received messages now. This method
 *      does nothing if the NetReceiver is in the asynchronous mode. In the
 *      asynchronous mode you have no method to call : everything is automated.
 *<br>
 *    - waitForMessage();
 *      Waits for a message to arrive. Useful in some cases when the NetReceiver
 *      is synchronous. This method does nothing if the NetReceiver is asynchronous.
 *</p>
 * @author Aldiss
 * @see wotlas.libs.net.NetSender
 * @see wotlas.libs.net.NetReceiver
 */

public abstract class NetConnection {

    /*------------------------------------------------------------------------------------*/

    /** Our NetSender
     */
    protected NetSender myNetsender;

    /** Our NetReceiver
     */
    protected NetReceiver myNetreceiver;

    /** Our connection listeners (objects that will be told when the connection is created or closed).
     */
    private NetConnectionListener listeners[];

    /** An eventual PingThread ( internal class )
     */
    private PingThread pingThread;

    /** A lock for the pingThread
     */
    private Object pingLock;

    /*------------------------------------------------------------------------------------*/

    /** Constructor with an already opened socket.
     *
     * @param socket an already opened socket
     * @exception IOException if the socket wasn't already connected.
     */
    public NetConnection(Socket socket) throws IOException {
        this(socket, null);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with an already opened socket and a session context to use.
     *
     * @param socket an already opened socket
     * @param sessionContext object to give to messages when they arrive.
     * @exception IOException if the socket wasn't already connected.
     */
    public NetConnection(Socket socket, Object sessionContext) throws IOException {
        init(socket, sessionContext);
        this.listeners = new NetConnectionListener[0]; // no listeners for now
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** In this method you have to create your own NetSender and NetReceiver and
     *  save them in the "myNetsender" and "my_net_receiver" class attributes.
     *  If you want to create a new Connection model you can take the classes
     *  in wotlas.libs.net.connection as examples.
     *
     * @param socket an already opened socket
     * @param sessionContext an object to give to messages as they arrive.
     * @exception IOException if the socket wasn't already connected.
     */
    protected abstract void init(Socket socket, Object sessionContext) throws IOException;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Starts this connection. When you create a new connection you can just
     * send messages but not receives ones. By calling this start() method you
     * launch your NetReceiver and thus can process incoming messages.
     */
    public void start() {
        this.myNetreceiver.start();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the current sessionContext. It can be any wanted object and it will be given
     *  to NetMessageBehaviour in their doBehaviour() method.
     *
     * @param sessionContext the new sessionContext.
     */
    public void setContext(Object sessionContext) {
        this.myNetreceiver.setContext(sessionContext);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Are we still connected ?
     * @return true if we are, false otherwise.
     */
    public boolean isConnected() {
        if (this.myNetsender == null || this.myNetreceiver == null)
            return false;
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the NetSender.
     *
     * @return the connection's NetSender
     */
    public NetSender getNetSender() {
        return this.myNetsender;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the NetReceiver.
     *
     * @return the connection's NetReceiver
     */
    public NetReceiver getNetReceiver() {
        return this.myNetreceiver;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To add a NetConnectionListener on this connection.
     *  The listener will receive information on the life on this connection : when it's
     *  created and when it's closed.
     *
     *  If this NetConnection is still alive we automatically call connectionCreated() on
     *  the new listener.
     *
     * @param listener an object implementing the NetConnectionListener interface.
     */
    public void addConnectionListener(NetConnectionListener listener) {

        NetConnectionListener tmp[] = new NetConnectionListener[this.listeners.length + 1];

        for (int i = 0; i < this.listeners.length; i++)
            tmp[i] = this.listeners[i];

        tmp[tmp.length - 1] = listener;
        this.listeners = tmp;

        // We warn the listener that this connection has been created
        if (this.myNetsender != null && this.myNetreceiver != null)
            listener.connectionCreated(this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To remove the specified network listener. We don't call any method on the listener.
     * @param listener listener to remove.
     * @return true if the listener was removed, false if it was nor found
     */
    public boolean removeConnectionListener(NetConnectionListener listener) {

        // does the listener exists ?
        boolean found = false;

        for (int i = 0; i < this.listeners.length; i++)
            if (this.listeners[i] == listener) {
                found = true;
                break;
            }

        if (!found)
            return false; // not found

        // We remove the connection listener
        NetConnectionListener tmp[] = new NetConnectionListener[this.listeners.length - 1];
        int nb = 0;

        for (int i = 0; i < this.listeners.length; i++)
            if (this.listeners[i] != listener) {
                tmp[nb] = this.listeners[i];
                nb++;
            }

        this.listeners = tmp;
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Asks the NetSender to send all his queued messages now. It's typically the method
     *  you use in case of a USER_AGGREGATION NetSender when the messages are not sent
     *  automatically, waiting for you to call this method to send them.
     *  If the NetSender is not in the USER_AGGREGATION it will still try to send currently
     *  queued messages (asynchronous, we just fire a "send!" signal).
     */
    public void sendAllMessages() {
        this.myNetsender.sendAllMessages();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Asks the NetReceiver to process all received messages now. This method
     *  does nothing if the NetReceiver is in the asynchronous mode. If in synchronous mode
     *  messages are queued when they arrive and wait for this method call to be processed.
     */
    public void receiveAllMessages() {
        this.myNetreceiver.receiveAllMessages();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To queue a message you want to send.
     *
     *  This method works with every connection type but has different behaviours. If your
     *  connection type is asynchronous this method will just leave the message on a queue
     *  and the message will be sent automatically later. If the connection type is synchronous
     *  you'll have to call the sendAllMessages() method to signal that queued messages can now
     *  be sent.<br>
     *
     *  The connection type depends on the implementation of this abstract NetConnection class
     *  Take a look at the provided implementation in wotlas.libs.net.connection .
     *
     * @param message message you want to send.
     */
    public void queueMessage(NetMessage message) {
        if (this.myNetsender != null)
            this.myNetsender.queueMessage(message);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Waits for a message to arrive. Useful in some cases when the NetReceiver
     *  is synchronous. This method does nothing if the NetReceiver is asynchronous.
     *
     * @exception IOException if an IO error occur.
     */
    public void waitForMessage() throws IOException {
        this.myNetreceiver.waitForMessage();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To close this connection. Erases all the allocated resources. This method will return
     *  immediately but the remaining queued messages will still be sent. This method performs
     *  some clean-up on the NetConnection object and does nothing when called twice.
     */
    synchronized public void close() {

        if (this.myNetsender == null || this.myNetreceiver == null)
            return;

        // We send a EndOfConnection message to the other side
        if (this.myNetsender != null)
            this.myNetsender.queueMessage(new EndOfConnectionMessage());

        // no more message handling
        this.myNetreceiver.stopThread();

        // we wait for the remaining messages to be sent
        this.myNetsender.sendAllMessages();

        // massive destruction
        this.myNetsender.stopThread();
        this.myNetsender.closeSocket(); // only lets the NetReceiver finish its work
        // before closing

        synchronized (this.myNetsender) {
            this.myNetsender.notify(); // we don't forget to wake up the thread !
        } // (the NetSender is locked when there are no msgs )

        if (this.pingLock != null)
            synchronized (this.pingLock) {
                this.pingThread.stopThread();
            }

        for (int i = 0; i < this.listeners.length; i++)
            this.listeners[i].connectionClosed(this);

        this.myNetsender = null;
        this.myNetreceiver = null;
        this.listeners = null;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set a ping listener for this network connection. Ping messages will be added to
     *  this network connection and if the remote peer supports it, Ping info will be sent
     *  regularly to this listener.
     * @param pListener the object that will receive ping information.
     */
    public void setPingListener(NetPingListener pListener) {

        if (pListener == null)
            return;

        if (this.pingLock != null)
            synchronized (this.pingLock) {
                if (this.pingThread == null)
                    return; // No longer living net connnection

                // just swap the listener
                this.pingThread.pListener = pListener;
                return;
            }

        // Ping Thread Creation
        this.pingLock = new Object();
        this.pingThread = new PingThread(pListener);
        this.pingThread.start();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To remove the current ping listener (if any) for this network connection.
     */
    public void removePingListener() {
        if (this.pingLock != null)
            synchronized (this.pingLock) {
                if (this.pingThread == null)
                    return; // No longer living net connnection

                this.pingThread.pListener = null;
            }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Called by the NetReceiver when it receives a PingMessage (if not in SendBack mode).
     * @param sequenceID ping message's sequence ID.
     */
    protected void receivedPingMessage(int sequenceID) {

        if (this.pingLock == null)
            return;

        synchronized (this.pingLock) {
            if (this.pingThread == null || this.pingThread.sequenceID != sequenceID)
                return;

            this.pingThread.lastPingValue = (int) (System.currentTimeMillis() - this.pingThread.lastPingT0);
            this.pingThread.pingReceivedBack = true;

            if (this.pingThread.pListener != null)
                this.pingThread.pListener.pingComputed(this.pingThread.lastPingValue);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set that this network connection must answer or not to ping messages.
     *  This option is not compatible with the setPingListener() method.
     *  You must choose between send back ping messages and compute the ping info.
     *  @param sendBack set to true if you want to send back ping messages.
     */
    public synchronized void sendBackPingMessages(boolean sendBack) {

        if (this.pingLock != null && sendBack) {
            Debug.signal(Debug.ERROR, this, "Conflict detected: ping thread exists ! Can't send back ping messages");
        }

        if (this.myNetreceiver != null)
            this.myNetreceiver.sendBackPingMessages(sendBack);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** A Thread that send PingMessages and manages the ping info.
     *  It is created when a NetPingListener is first set and lives as long as the network
     *  connection does.
     */
    private class PingThread extends Thread {

        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

        protected byte sequenceID; // current ping sequence ID

        protected boolean pingReceivedBack; // has the last ping been received ?

        protected int lastPingValue; // last ping value

        protected long lastPingT0; // last ping sent t0

        protected NetPingListener pListener; // our ping listener

        protected boolean shouldStopPingThread;

        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

        /** Constructor with ping listener.
         */
        public PingThread(NetPingListener pListener) {
            this.sequenceID = 0;
            this.pingReceivedBack = false;
            this.shouldStopPingThread = false;
            this.pListener = pListener;
        }

        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

        /** Our Ping Loop
         */
        @Override
        public void run() {

            while (!this.shouldStopPingThread) {

                // 1 - We send a PingMessage to the other side
                queueMessage(new PingMessage(this.sequenceID));
                this.lastPingT0 = System.currentTimeMillis();

                // 2 - We wait 2 seconds between two Ping.
                synchronized (this) {
                    try {
                        this.wait(2000);
                    } catch (Exception e) {
                    }

                    if (this.shouldStopPingThread)
                        break;
                }

                // 3 - Have we received the answer ?
                synchronized (NetConnection.this.pingLock) {
                    if (!this.pingReceivedBack) {
                        // we wait two more seconds
                        try {
                            this.wait(2000);
                        } catch (Exception e) {
                        }

                        // if there is still no answer we declare the message lost
                        if (!this.pingReceivedBack) {
                            this.lastPingValue = NetPingListener.PING_FAILED;
                            if (this.pListener != null)
                                this.pListener.pingComputed(this.lastPingValue);
                        }
                    }

                    this.sequenceID = (byte) ((this.sequenceID + 1) % 120);
                    this.pingReceivedBack = false;
                }
            }

            // Advertise the end of this connection
            synchronized (NetConnection.this.pingLock) {
                if (this.pListener != null) {
                    this.pListener.pingComputed(NetPingListener.PING_CONNECTION_CLOSED);
                    this.pListener = null;
                }

                NetConnection.this.pingThread = null;
            }
        }

        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

        public synchronized void stopThread() {
            this.shouldStopPingThread = true;
            this.notify();
        }

        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    };

    /*------------------------------------------------------------------------------------*/

}
