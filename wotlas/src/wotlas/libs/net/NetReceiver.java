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
import java.io.DataInputStream;
import java.io.IOException;

import wotlas.utils.Debug;
import wotlas.utils.Tools;

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

public class NetReceiver extends NetThread
{

 /*------------------------------------------------------------------------------------*/

    /** Communication stream to receive data
     */
        private DataInputStream in_stream;

    /** Do we have to wait for a user signal to execute messages ?
     *  Determine if we work synchronously or asynchronously.
     */
        private boolean sync;

    /** Our message factory
     */
        private NetMessageFactory factory;
    
    /** Object to give to messages when they arrive.
     */
        private Object context;

    /** For the synchronous NetReceiver : 
     *  maximum number of messages to process per user call ( pleaseReceiveAllMessagesNow() ).
     */
        private int max_msg;

 /*------------------------------------------------------------------------------------*/

    /**  Constructor. Should be called only by the NetServer & NetClient classes.
     *   We assume that a NetMessageFactory has already been created.
     *
     * @param socket a previously created & connected socket.
     * @param personality a NetPersonality linked to the specified socket.
     * @param sync do we have to work synchronously or asynchronously.
     * @param context object to give to messages when they arrive.
     * @param buffer_size buffer size (in bytes) for the buffered input stream.
     * @exception IOException if the socket wasn't already connected.
     */
      public NetReceiver( Socket socket, NetPersonality personality, boolean sync,
                          Object context, int buffer_size ) throws IOException
      {
          super( socket, personality );
          this.sync = sync;
          this.context = context;

          if(sync)
             max_msg = 15; // default value

       // we retrieve & construct some useful handles
          in_stream = new DataInputStream( getBufferedInputStream( buffer_size ) );
          factory = NetMessageFactory.getDefaultMessageFactory();
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
             // we wait for message category & type.
                byte msg_cat = in_stream.readByte();
                byte msg_typ = in_stream.readByte();

             // we reconstruct the message and execute its associated code.
                try{
                     NetMessageBehaviour msg = factory.getNewMessageInstance( msg_cat, msg_typ );
                     ((NetMessage) msg ).decode( in_stream );
                     msg.doBehaviour( context );
                }
                catch(ClassNotFoundException e) {
                      Debug.signal( Debug.WARNING, this, e );
                      in_stream.skipBytes( in_stream.available() );  // cleanse the source ;)
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
               Debug.signal( Debug.ERROR, this, e ); // serious error while processing message
        }

       closeConnection();
       in_stream=null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Method to call in a case of a sync NetReceiver. We stop to process messages in 
   *  either cases : (1) there is no more messages,
   *                 (2) the max_msg limit has been reached.
   */
     public void pleaseReceiveAllMessagesNow()
     {
        if(!sync)
           return;   // we have nothing to do here

        int msg_counter=0;

        try
        {

           while( in_stream.available()!=0 && msg_counter<max_msg )
           {
             // we read the message category & type.
                byte msg_cat = in_stream.readByte();
                byte msg_typ = in_stream.readByte();

             // we reconstruct the message and execute its associated code.
                try{
                     NetMessageBehaviour msg = factory.getNewMessageInstance( msg_cat, msg_typ );
                     ((NetMessage) msg ).decode( in_stream );
                     msg.doBehaviour( context );
                }
                catch(ClassNotFoundException e) {                     
                      Debug.signal( Debug.WARNING, this, e );
                      in_stream.skipBytes( in_stream.available() );  // cleanse the source ;)
                      return;
                }

              msg_counter++;
           }

        }
        catch(Exception e){
           if(e instanceof IOException) {
             // Socket error, connection was probably closed a little roughly...
               Debug.signal( Debug.WARNING, this, e );
           }
           else
               Debug.signal( Debug.ERROR, this, e ); // serious error while processing message

           closeConnection();
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the maximum number of messages to process per user call.
   *
   * @param max_msg maximum number of messages
   */
     public void setMaxMessageLimitPerUserCall( int max_msg ) {
         this.max_msg = max_msg;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the maximum number of messages to process per user call.
   *
   * @return the maximum number of messages
   */
     public int getMaxMessageLimitPerUserCall() {
         return max_msg;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the current context.
   *
   * @param context the new context.
   */
     public void setContext( Object context ) {
         this.context = context;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Waits for a message to arrive. Useful in some cases when the NetReceiver
   *  is synchronous. This method does nothing if the NetReceiver is asynchronous.
   *
   * @exception IOException if something goes wrong
   */
     public void waitForAMessageToArrive() throws IOException{
        if(!sync)
           return;

      // We mark the stream
         in_stream.mark( 1000 );
 
      // Wait for data...
         in_stream.readByte();

      // Reset Stream state
         in_stream.reset();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

