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

import java.io.DataOutputStream;
import java.io.IOException;
import wotlas.utils.Debug;

/** A NetSender sends NetMessages on an opened socket.
 * A NetSender has three different modes :
 *<br>
 *   (1) simply send messages as they arrive. <br>
 *   (2) aggregate messages with a timeout and max message limit and send them. <br>
 *   (3) wait for a user signal to send the messages (user aggregation of messages)<br>
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetThread
 */
public class NetSender extends NetThread {

    /*------------------------------------------------------------------------------------*/
    /** Mode 1 : we send messages as they arrive.
     */
    public static final byte SEND_IMMEDIATELY = 1;

    /** Mode 2 : we aggregate messages.
     */
    public static final byte AGGREGATE_MESSAGES = 2;

    /** Mode 3 : we wait for a user signal to send messages.
     */
    public static final byte USER_AGGREGATION = 3;

    /*------------------------------------------------------------------------------------*/
    /** A link to our NetConnection
     */
    private NetConnection connection;

    /** Communication stream to send data
     */
    private DataOutputStream outStream;

    /** NetSender type ( SEND_IMMEDIATELY, AGGREGATE_MESSAGES or USER_AGGREGATION )
     */
    private byte senderType;

    /** Aggregation timeout (ms)
     */
    private short aggregationTimeout;

    /** Aggregation maximum message limit
     */
    private short aggregationMsgLimit;

    /** Stop aggregation for this time. (useful only for the AGGREGATE_MESSAGES
     *  type and the sendAllMessages() call )
     */
    private boolean stopAggregation;

    /** To signal that we a thread is locked on sendAllMessages()
     */
    private boolean locked;

    /*------------------------------------------------------------------------------------*/
    /** NetMessages to send.
     */
    private NetMessage messageList[];

    /** Current number of messages to send.
     */
    private int nbMessages;

    /*------------------------------------------------------------------------------------*/
    /** Constructor. Should be called only by the NetConnection implementations.
     *  Default values :
     *<br>
     *      - aggregation_timeout = 20ms<br>
     *      - aggregation_msg_limit = 10 messages
     *<p>
     * @param socket a previously created & connected socket.
     * @param connection a NetConnection linked to the specified socket.
     * @param senderType NetSender type ( SEND_IMMEDIATELY, AGGREGATE_MESSAGES or USER_AGGREGATION )
     * @param bufferSize buffer size (in bytes) for the buffered output stream.
     * @exception IOException if the socket wasn't already connected.
     */
    public NetSender(IOChannel socket, NetConnection connection, byte senderType, int bufferSize) throws IOException {
        super(socket);
        this.connection = connection;

        if (senderType < 1 || 3 < senderType) {
            this.senderType = NetSender.SEND_IMMEDIATELY;
        } else {
            this.senderType = senderType;
        }

        // default values
        this.aggregationTimeout = 20; // 20 ms
        this.aggregationMsgLimit = 10; // 10 messages max per aggregation
        this.stopAggregation = false;

        // other inits
        this.messageList = new NetMessage[this.aggregationMsgLimit];
        this.outStream = new DataOutputStream(getBufferedOutputStream(bufferSize));

        start();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** NetSender Thread action.
     *  Never call this method it's done automatically.
     */
    @Override
    public void run() {
        if (this.senderType == NetSender.USER_AGGREGATION) {
            return; // // we have nothing to do here
        }

        try {
            do {
                synchronized (this) {
                    // 1 - we wait for some action...
                    while (this.nbMessages == 0 && !shouldStopThread()) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }
                    }

                    // 2 - ok, we have at least one message... what do we do ?
                    if (this.senderType == NetSender.AGGREGATE_MESSAGES) {
                        if (!this.stopAggregation) {
                            // aggregation start
                            long t0 = System.currentTimeMillis();
                            long tr = this.aggregationTimeout;

                            while (this.nbMessages < this.aggregationMsgLimit && !shouldStopThread()) {
                                try {
                                    wait(tr);
                                } catch (InterruptedException e) {
                                }

                                tr = this.aggregationTimeout - System.currentTimeMillis() - t0;
                                if (tr < 3) {
                                    break; // aggregation end, we are not going to loop again for 3ms
                                }
                            }
                        } else {
                            this.stopAggregation = false;
                        }
                    }

                    // we send all the messages
                    sendQueuedMessages();
                }
            } while (!shouldStopThread());
        } catch (IOException ioe) {
            // Socket error, connection was probably closed a little roughly...
            Debug.signal(Debug.WARNING, this, ioe);
        } catch (Exception e) {
            // serious error while sending message
            Debug.signal(Debug.ERROR, this, e);
        }

        // we ask the NetConnection to perform some cleanup
        // and signal that the connection was closed ( connectionListener )
        this.connection.close();
        this.outStream = null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To queue a message. With the SEND_IMMEDIATELY & AGGREGATE_MESSAGES NetSender
     *  we signal the new message to the thread. For the USER_AGGREGATION NetSender
     *  use the pleaseSendMessagesNow() after your queueMessage() calls.
     *
     * @param message message to queue.
     */
    synchronized public void queueMessage(NetMessage message) {
        if (this.nbMessages == this.aggregationMsgLimit) {
            // the user has not tunned his aggregation limit very well...
            setAggregationMessageLimit((short) (this.aggregationMsgLimit + 10));
        }

        this.messageList[this.nbMessages] = message;
        this.nbMessages++;

        if (this.senderType != NetSender.USER_AGGREGATION) {
            notify();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Method to use for the USER_AGGREGATION NetSender when you want it to send
     *  the queued messages. For the AGGREGATION_MESSAGES it asks for the immediate
     *  send of the queued messages. For the SEND_IMMEDIATELY type we do nothing but
     *  make sure that the message has been sent.
     */
    synchronized public void sendAllMessages() {
        // Different behaviours depending on the sender's type
        if (this.senderType == NetSender.USER_AGGREGATION) {
            try {
                sendQueuedMessages();
                return;
            } catch (IOException ioe) {
                // Socket error, connection was probably closed a little roughly...
                Debug.signal(Debug.WARNING, this, ioe);

                // we ask the NetConnection to perform some cleanup
                // and signal that the connection was closed ( connectionListener )
                this.connection.close();
                this.outStream = null;
                return;
            } catch (Exception e) {
                // serious error occured
                Debug.signal(Debug.ERROR, this, e);

                // we ask the NetConnection to perform some cleanup
                // and signal that the connection was closed ( connectionListener )
                this.connection.close();
                this.outStream = null;
                return;
            }
        } else if (this.senderType == NetSender.AGGREGATE_MESSAGES) {
            this.stopAggregation = true;
            notify();
        }

        // we wait until the last message is sent. The sendQueuedMessages() will notify us.
        // (max 10s to avoid a deadlock if an Exception has been thrown in sendQueuedMessages)
        if (this.nbMessages != 0) {
            this.locked = true;

            try {
                wait(10000);
            } catch (InterruptedException e) {
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Sends all queued messages.
     * @exception IOException if something goes wrong while sending this message
     */
    synchronized private void sendQueuedMessages() throws IOException {
        if (shouldStopThread()) {
            return;
        }

        for (short i = 0; i < this.nbMessages; i++) {
            if (this.messageList[i] == null) {
                continue;
            }

            // TODO remove system.err (debug mode)
            if (this.messageList[i].getMessageClassName().indexOf("Ping") == -1) {
                System.out.println("NetSender => " + this.messageList[i].getMessageClassName());
            }

            // 1 - We first write the header of the message : the message class name.
            this.outStream.writeUTF(this.messageList[i].getMessageClassName());

            // 2 - We write the user data
            this.messageList[i].encode(this.outStream);
            this.messageList[i] = null;
        }

        this.outStream.flush(); // send the whole
        this.nbMessages = 0;

        // We throw a "notifyAll" signal if there are threads locked on sendAllMessages()
        if (this.locked) {
            this.locked = false;
            notifyAll();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To change the aggregation message limit. 
     *
     *  IMPORTANT: your change can be refused if you want to reduce the
     *  aggregation message limit and if there are M messages to send
     *  with M superior to your new value.
     *
     *  Note also that this method is synchronised and takes a little time...
     *  Use it only to initialize this NetSender.
     *
     * @param newMsgLimit the new value for the aggregationMsgLimit
     */
    synchronized public void setAggregationMessageLimit(short newMsgLimit) {
        if (newMsgLimit < this.nbMessages) {
            Debug.signal(Debug.NOTICE, this, "setAggregationMessageLimit refused: " + this.nbMessages);
            return;
        }

        NetMessage listTmp[] = new NetMessage[newMsgLimit];
        System.arraycopy(this.messageList, 0, listTmp, 0, this.nbMessages);

        this.messageList = listTmp;
        this.aggregationMsgLimit = newMsgLimit;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To change the aggregation timeout.
     *  IMPORTANT: this change takes its effects only after the current aggregation.
     *
     * @param timeout new aggregation timeout
     */
    synchronized public void setAggregationTimeout(short timeout) {
        this.aggregationTimeout = timeout;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To get the aggregation timeout.
     *
     * @return aggregation timeout
     */
    public short getAggregationTimeout() {
        return this.aggregationTimeout;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To get the aggregation message limit.
     *
     * @return aggregation message limit
     */
    public short getAggregationMessageLimit() {
        return this.aggregationMsgLimit;
    }

    /*------------------------------------------------------------------------------------*/
}
