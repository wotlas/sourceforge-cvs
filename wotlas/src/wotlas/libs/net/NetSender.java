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
 * @see wotlas.libs.net.NetMessageFactory
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

    /** A link to our NetPersonality
     */
        private NetPersonality personality;

    /** Communication stream to send data
     */
        private DataOutputStream out_stream;

    /** NetSender type ( SEND_IMMEDIATELY, AGGREGATE_MESSAGES or USER_AGGREGATION )
     */
        private byte sender_type;

    /** Aggregation timeout (ms)
     */
        private short aggregation_timeout;

    /** Aggregation maximum message limit
     */
        private short aggregation_msg_limit;

    /** Stop aggregation for this time. (useful only for the AGGREGATE_MESSAGES
     *  type and the pleaseSendAllMessagesNow() call )
     */
        private boolean stop_aggregation;

    /** To signal that we a thread is locked on pleaseSendAllMessagesNow()
     */
        private boolean locked;

 /*------------------------------------------------------------------------------------*/

    /** NetMessages to send.
     */
        private NetMessage message_list[];

    /** Current number of messages to send.
     */
        private int nb_messages;

 /*------------------------------------------------------------------------------------*/

    /**  Constructor. Should be called only by the NetServer & NetClient classes.
     *   Default values :
     *<br>
     *      - aggregation_timeout = 20ms<br>
     *      - aggregation_msg_limit = 10 messages
     *<p>
     * @param socket a previously created & connected socket.
     * @param personality a NetPersonality linked to the specified socket.
     * @param sender_type NetSender type ( SEND_IMMEDIATELY, AGGREGATE_MESSAGES or USER_AGGREGATION )
     * @param buffer_size buffer size (in bytes) for the buffered output stream.
     * @exception IOException if the socket wasn't already connected.
     */
      public NetSender( Socket socket, NetPersonality personality,
                        byte sender_type, int buffer_size ) throws IOException
      {
          super(socket);
          this.personality = personality;

          if(sender_type<1 || 3<sender_type)
              this.sender_type = SEND_IMMEDIATELY;
           else
              this.sender_type = sender_type;

       // default values
          aggregation_timeout   = 20;  // 20 ms
          aggregation_msg_limit = 10;  // 10 messages max per aggregation
          stop_aggregation = false;

       // other inits
          message_list = new NetMessage[aggregation_msg_limit];
          out_stream = new DataOutputStream( getBufferedOutputStream( buffer_size ) );

          start();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** NetSender Thread action.
   *  Never call this method it's done automatically.
   */
    public void run()
    {
     	if( sender_type == USER_AGGREGATION )  // we have nothing to do here
     	  return;

        try
        {
           do
           {
               synchronized( this )
               {
                  // we wait for some action...
                     while( nb_messages==0 && !shouldStopThread() )
                         try{
                               wait();
                         }
                         catch( InterruptedException e )
                         {}

                  // ok, we have at least one message... what do we do ?
                     if( sender_type==AGGREGATE_MESSAGES )
                     {
                       if( !stop_aggregation )
                       {
                        // aggregation start
                           long t0 = System.currentTimeMillis();
                           long tr = aggregation_timeout;

                            while( nb_messages<aggregation_msg_limit && !shouldStopThread())
                            {
                               try{
                                        wait( tr );
                               }
                               catch( InterruptedException e )
                               {}

                               tr = aggregation_timeout-System.currentTimeMillis()-t0;

                               if(tr<3) break; // aggregation end
                            }
                       }
                       else
                           stop_aggregation = false;
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

     // we ask the NetPersonality to perform some cleanup
     // and signal that the connection was closed ( connectionListener )
        personality.closeConnection();
        out_stream=null;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To queue a message. With the SEND_IMMEDIATELY & AGGREGATE_MESSAGES NetSender
   *  we signal the new message to the thread. For the USER_AGGREGATION NetSender
   *  use the pleaseSendMessagesNow() after your queueMessage() calls.
   *
   * @param message message to queue.
   */
     synchronized public void queueMessage( NetMessage message )
     {
          if( nb_messages==aggregation_msg_limit ) {
                // the user has not tunned his aggregation limit very well...
                setAggregationMessageLimit( (short) (aggregation_msg_limit+5) );
          }

          message_list[nb_messages] = message;
          nb_messages++;

          if( sender_type!=USER_AGGREGATION )
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
            if( sender_type == USER_AGGREGATION )
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

                // we ask the NetPersonality to perform some cleanup
                // and signal that the connection was closed ( connectionListener )
                   personality.closeConnection();
                   out_stream=null;
                }
            }
            else if( sender_type == AGGREGATE_MESSAGES ) {
                stop_aggregation = true;
                notify();
            }

        // we wait until the last message is sent. The sendQueuedMessages() will notify us.
        // (max 10s to avoid a deadlock if an Exception has been thrown in sendQueuedMessages)
           if(nb_messages!=0) {
              locked = true;
 
              try{
                   wait( 10000 );
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
     synchronized private void sendQueuedMessages() throws IOException
     {
        if(shouldStopThread())
            return;

        for( short i=0; i<nb_messages; i++) {
     	    if(message_list[i]==null) continue;

            out_stream.writeByte( message_list[i].getMessageCategory() );
            out_stream.writeByte( message_list[i].getMessageType() );
     	    message_list[i].encode( out_stream );
     	    message_list[i] = null;
     	}

        out_stream.flush();
     	nb_messages = 0;

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
   * @param new_msg_limit the new value for the aggregation_msg_limit
   */
     synchronized public void setAggregationMessageLimit( short new_msg_limit ) {
        if( new_msg_limit<nb_messages ) {
            Debug.signal( Debug.NOTICE, this, "setAggregationMessageLimit refused: "+nb_messages );
            return;
        }

        NetMessage list_tmp[] = new NetMessage[new_msg_limit];
        System.arraycopy( message_list, 0, list_tmp, 0, nb_messages );

        message_list = list_tmp;
        aggregation_msg_limit=new_msg_limit;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To change the aggregation timeout.
   *
   *  IMPORTANT: this change takes its effects only after the current aggregation.
   *
   * @param timeout new aggregation timeout
   */
     synchronized public void setAggregationTimeout( short timeout ) {
         aggregation_timeout = timeout;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the aggregation timeout.
   *
   * @return aggregation timeout
   */
     public short getAggregationTimeout() {
         return aggregation_timeout;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the aggregation message limit.
   *
   * @return aggregation message limit
   */
     public short getAggregationMessageLimit() {
         return aggregation_msg_limit;
     }

 /*------------------------------------------------------------------------------------*/

}

