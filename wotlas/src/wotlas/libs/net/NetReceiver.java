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
import java.net.SocketException;
import java.io.DataInputStream;
import java.io.IOException;

import wotlas.utils.Debug;
import wotlas.libs.net.message.PingMessage;
import wotlas.libs.net.message.EndOfConnectionMessage;

/** A NetReceiver waits for NetMessage to arrive on an opened socket.
 *  It decodes them and execute their associated code.
 *<br>
 * There is two types of NetReceiver : synchronous (sync=true),
 *                                     asynchronous (sync=false)
 *<p>
 * In the first case a thread is automatically started by the constructor.
 *<p>
 * In the second case, no thread is started. The user has to call
 * the pleaseReceiveAllMessagesNow() method regularly to allow the
 * NetReceiver to extract messages.
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

    /** A link to our NetPersonality
     */
        private NetPersonality personality;

    /** Session Object to give to messages when they arrive.
     */
        private Object sessionContext;

    /** For the synchronous NetReceiver : 
     *  maximum number of messages to process per user call ( pleaseReceiveAllMessagesNow() ).
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
     * @param personality a NetPersonality linked to the specified socket.
     * @param sync do we have to work synchronously or asynchronously.
     * @param sessionContext session object to give to messages when they arrive.
     * @param bufferSize buffer size (in bytes) for the buffered input stream.
     * @exception IOException if the socket wasn't already connected.
     */
      public NetReceiver( Socket socket, NetPersonality personality, boolean sync,
                          Object sessionContext, int bufferSize ) throws IOException
      {
          super( socket );
          this.sync = sync;
          this.sessionContext = sessionContext;
          this.personality = personality;
          sendBackPingMessages = false;

          if(sync)  maxMsg = 15; // default value

       // we retrieve & construct some useful handles
          inStream = new DataInputStream( getBufferedInputStream( bufferSize ) );
          factory = NetMessageFactory.getMessageFactory();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Do we have to send back ping messages ?
   * @param sendBack set to true to automatically send back ping messages.
   */
      public void sendBackPingMessages( boolean sendBack ) {
          sendBackPingMessages = sendBack;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Method for the async NetReceiver.
   *  Never call this method it's done automatically.
   */
    public void run()
    {
        if(sync)
           return;   // we have nothing to do here

        try
        {
           do
           {
             // we wait for message super class name.
                String msgSuperClassName = inStream.readUTF();

             // we reconstruct the message and execute its associated code.
                try{
                     NetMessageBehaviour msg = factory.getNewMessageInstance( msgSuperClassName );
                     ( (NetMessage)msg ).decode( inStream );

                     if(msg instanceof PingMessage) {
                     	if(sendBackPingMessages)
                           personality.queueMessage( (NetMessage)msg ); // send back the PingMessage
                        else
                           personality.receivedPingMessage( ((PingMessage) msg).getSeqID() );
                     }
                     else if(msg instanceof EndOfConnectionMessage)
                        break; // end of connection
                     else
                        msg.doBehaviour( sessionContext );
                }
                catch(ClassNotFoundException e) {
                      Debug.signal( Debug.WARNING, this, e );
                      inStream.skipBytes( inStream.available() );  // cleanse the source ;)
                }
           }
           while( !shouldStopThread() );

        }
        catch(Exception e){
           if(e instanceof IOException) {
             // Socket error, connection was probably closed a little roughly...
               Debug.signal( Debug.WARNING, this, "Connection closed : "+e.toString() );
           }
           else if(e instanceof SocketException) {
             // Socket closed a bit roughly
               Debug.signal( Debug.WARNING, this, "Socket closed a bit roughly : "+e.toString() );
           }
           else
               Debug.signal( Debug.ERROR, this, e ); // serious error while processing message
        }

     // we ask the NetPersonality to perform some cleanup
     // and signal that the connection was closed ( connectionListener )
        personality.closeConnection();
        inStream=null;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Method to call in a case of a sync NetReceiver. We stop to process messages in 
   *  either cases : (1) there is no more messages,
   *                 (2) the maxMsg limit has been reached.
   */
     public void pleaseReceiveAllMessagesNow()
     {
        if(!sync)
           return;   // we have nothing to do here

        int msgCounter=0;

        try
        {
           while( inStream.available()!=0 && msgCounter<maxMsg )
           {
             // we wait for message super class name.
                String msgSuperClassName = inStream.readUTF();

             // we reconstruct the message and execute its associated code.
                try{
                     NetMessageBehaviour msg = factory.getNewMessageInstance( msgSuperClassName );
                     ( (NetMessage)msg ).decode( inStream );

                     if(msg instanceof PingMessage) {
                     	if(sendBackPingMessages)
                           personality.queueMessage( (NetMessage)msg ); // send back the PingMessage
                        else
                           personality.receivedPingMessage( ((PingMessage) msg).getSeqID() );
                     }
                     else
                        msg.doBehaviour( sessionContext );
                }
                catch(ClassNotFoundException e) {                     
                      Debug.signal( Debug.WARNING, this, e );
                      inStream.skipBytes( inStream.available() );  // cleanse the source ;)
                      return;
                }

              msgCounter++;
           }

        }
        catch(Exception e){
           if(e instanceof IOException) {
             // Socket error, connection was probably closed a little roughly...
               Debug.signal( Debug.WARNING, this, e );
           }
           else
               Debug.signal( Debug.ERROR, this, e ); // serious error while processing message

         // we ask the NetPersonality to perform some cleanup
         // and signal that the connection was closed ( connectionListener )
            personality.closeConnection();
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the maximum number of messages to process per user call.
   *
   * @param maxMsg maximum number of messages
   */
     public void setMaxMessageLimitPerUserCall( int maxMsg ) {
         this.maxMsg = maxMsg;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the maximum number of messages to process per user call.
   *
   * @return the maximum number of messages
   */
     public int getMaxMessageLimitPerUserCall() {
         return maxMsg;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the current sessionContext.
   *
   * @param sessionContext the new sessionContext.
   */
     public void setContext( Object sessionContext ) {
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
     public void waitForAMessageToArrive() throws IOException{
        if(!sync)
           return;

      // We mark the stream
         inStream.mark( 1000 );

      // Wait for data...
         inStream.readByte();

      // Reset Stream state
         inStream.reset();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** is the NetReceiver synchronous ?
   *
   * @return true if synchronous, false if asynchronous
   */
     public boolean isSynchronous() {
         return sync;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

