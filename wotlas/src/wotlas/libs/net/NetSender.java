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


import java.net.Socket;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.utils.Debug;


/** A NetSender sends NetMessage on an opened socket.
 *
 * A NetSender has three different modes :
 *<br>
 *   (1) simply send messages as they arrive. <p>
 *   (2) aggregate messages with a timeout and max message limit. <p>
 *   (3) wait for a user signal to send the message aggregation <p>
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetThread
 */

public class NetSender extends NetThread
{
 /*------------------------------------------------------------------------------------*/

    /** Mode 1 : we send messages as they arrive.
     */
        public static final byte SEND_IMMEDIATELY   = 1;

    /** Mode 2 : we aggregate messages.
     */
        public static final byte AGGREGATE_MESSAGES = 2;

    /** Mode 3 : we wait for a user signal to send messages.
     */
        public static final byte USER_AGGREGATION   = 3;

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
     *  type and the pleaseSendAllMessagesNow() call )
     */
        private boolean stopAggregation;

    /** To signal that we a thread is locked on pleaseSendAllMessagesNow()
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

    /**  Constructor. Should be called only by the NetServer & NetClient classes.
     *   Default values :
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
      public NetSender( Socket socket, NetConnection connection,
                        byte senderType, int bufferSize ) throws IOException
      {
          super(socket);
          this.connection = connection;

          if( senderType<1 || 3<senderType )
              this.senderType = SEND_IMMEDIATELY;
           else
              this.senderType = senderType;

       // default values
          aggregationTimeout   = 20;     // 20 ms
          aggregationMsgLimit  = 10;     // 10 messages max per aggregation
          stopAggregation      = false; 

       // other inits
          messageList = new NetMessage[aggregationMsgLimit];
          outStream = new DataOutputStream( getBufferedOutputStream( bufferSize ) );

          start();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** NetSender Thread action.
   *  Never call this method it's done automatically.
   */
    public void run()
    {
     	if( senderType==USER_AGGREGATION )  // we have nothing to do here
     	  return;

        try
        {
           do
           {
               synchronized( this )
               {
                  // we wait for some action...
                     while( nbMessages==0 && !shouldStopThread() )
                         try{
                           wait();
                         } catch( InterruptedException e ) {}

                  // ok, we have at least one message... what do we do ?
                     if( senderType==AGGREGATE_MESSAGES )
                     {
                       if( !stopAggregation )
                       {
                        // aggregation start
                           long t0 = System.currentTimeMillis();
                           long tr = aggregationTimeout;

                            while( nbMessages<aggregationMsgLimit && !shouldStopThread())
                            {
                               try{
                                        wait( tr );
                               } catch( InterruptedException e ) {}

                               tr = aggregationTimeout-System.currentTimeMillis()-t0;

                               if(tr<3) break; // aggregation end
                            }
                       }
                       else
                           stopAggregation = false;
                    }

                 // we send all the messages
                    sendQueuedMessages();
               }
           }
           while( !shouldStopThread() );

        }
        catch(Exception e){
           if(e instanceof IOException) {
             // Socket error, connection was probably closed a little roughly...
               Debug.signal( Debug.WARNING, this, e );
           }
           else
               Debug.signal( Debug.ERROR, this, e ); // serious error while sending message
        }

     // we ask the NetConnection to perform some cleanup
     // and signal that the connection was closed ( connectionListener )
        connection.closeConnection();
        outStream=null;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To queue a message. With the SEND_IMMEDIATELY & AGGREGATE_MESSAGES NetSender
   *  we signal the new message to the thread. For the USER_AGGREGATION NetSender
   *  use the pleaseSendMessagesNow() after your queueMessage() calls.
   *
   * @param message message to queue.
   */
     synchronized public void queueMessage( NetMessage message ) {
          if( nbMessages==aggregationMsgLimit ) {
                // the user has not tunned his aggregation limit very well...
                setAggregationMessageLimit( (short) (aggregationMsgLimit+5) );
          }

          messageList[nbMessages] = message;
          nbMessages++;

          if( senderType!=USER_AGGREGATION )
               notify();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Method to use for the USER_AGGREGATION NetSender when you want it to send
   *  the queued messages. For the AGGREGATION_MESSAGES it asks for the immediate
   *  send of the queued messages. For the SEND_IMMEDIATELY type we do nothing but
   *  make sure that the message has been sent.
   */
     synchronized public void pleaseSendAllMessagesNow()
     {
            if( senderType==USER_AGGREGATION )
            {
                try{
                    sendQueuedMessages();
                    return;
                }
                catch( Exception e )
                {
                    if(e instanceof IOException) {
                       // Socket error, connection was probably closed a little roughly...
                          Debug.signal( Debug.WARNING, this, e );
                    }
                    else
                          Debug.signal( Debug.ERROR, this, e ); // serious error

                // we ask the NetConnection to perform some cleanup
                // and signal that the connection was closed ( connectionListener )
                   connection.closeConnection();
                   outStream=null;
                }
            }
            else if( senderType==AGGREGATE_MESSAGES ) {
                stopAggregation = true;
                notify();
            }

        // we wait until the last message is sent. The sendQueuedMessages() will notify us.
        // (max 15s to avoid a deadlock if an Exception has been thrown in sendQueuedMessages)
           if(nbMessages!=0) {
              locked = true;
 
              try{
                   wait( 15000 );
              }
              catch( InterruptedException e )
              {}
           }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Sends all queued messages.
   * 
   * @exception IOException if something goes wrong while sending this message
   */
     synchronized private void sendQueuedMessages() throws IOException {
        if(shouldStopThread())
            return;

        for( short i=0; i<nbMessages; i++) {
     	    if(messageList[i]==null) continue;

            outStream.writeUTF( messageList[i].getMessageClassName() );
     	    messageList[i].encode( outStream );
     	    messageList[i] = null;
     	}

        outStream.flush();
     	nbMessages = 0;

      // A notifyAll if there are threads locked on pleaseSendAllMessagesNow()
         if(locked) {
            locked=false;
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
     synchronized public void setAggregationMessageLimit( short newMsgLimit ) {
        if( newMsgLimit<nbMessages ) {
            Debug.signal( Debug.NOTICE, this, "setAggregationMessageLimit refused: "+nbMessages );
            return;
        }

        NetMessage listTmp[] = new NetMessage[newMsgLimit];
        System.arraycopy( messageList, 0, listTmp, 0, nbMessages );

        messageList = listTmp;
        aggregationMsgLimit=newMsgLimit;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To change the aggregation timeout.
   *  IMPORTANT: this change takes its effects only after the current aggregation.
   *
   * @param timeout new aggregation timeout
   */
     synchronized public void setAggregationTimeout( short timeout ) {
         aggregationTimeout = timeout;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the aggregation timeout.
   *
   * @return aggregation timeout
   */
     public short getAggregationTimeout() {
         return aggregationTimeout;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the aggregation message limit.
   *
   * @return aggregation message limit
   */
     public short getAggregationMessageLimit() {
         return aggregationMsgLimit;
     }

 /*------------------------------------------------------------------------------------*/

}

