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

import java.io.IOException;
import java.net.Socket;

/** 
 * A NetPersonality brings together a NetSender and NetReceiver to send and receive
 * messages. This class is abstract because there are many different types of
 * NetSender and NetReceiver. You have to create your own ones by redefining
 * the generatePersonality() method. Default personalities are provided in the
 * wotlas.libs.net.personality package.
 *
 * Useful methods you can invoke to send messages :
 *
 *    - queueMessage( message );
 *      To queue a message you want to send. 
 *      Works with every personality type ( but with different behaviour ).
 *
 *    - pleaseSendAllMessagesNow();
 *      Asks the NetSender to send all his queued messages now. It's typically the method
 *      you use in case of a USER_AGGREGATION NetSender or when you want to send all the
 *      remaining messages before closing a connection with the AGGREGATION_MESSAGES NetSender.
 *
 *
 * Useful methods you can invoke to receive messages :
 *
 *    - pleaseReceiveAllMessagesNow();
 *      Asks the NetReceiver to process all received messages now. This method
 *      does nothing if the NetReceiver is in the asynchronous mode. In the
 *      asynchronous mode you have no method to call : everything is automated.
 *
 *    - waitForAMessageToArrive();
 *      Waits for a message to arrive. Useful in some cases when the NetReceiver
 *      is synchronous. This method does nothing if the NetReceiver is asynchronous.
 *
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetSender
 * @see wotlas.libs.net.NetReceiver
 */

public abstract class NetPersonality
{
 /*------------------------------------------------------------------------------------*/

   /** Our NetSender
    */
      protected NetSender my_netsender;

   /** Our NetReceiver
    */
      protected NetReceiver my_netreceiver;

   /** An eventual connection listener
    */
      private NetConnectionListener listener;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with an already opened socket.
   *
   * @param socket an already opened socket
   * @param context object to give to messages when they arrive.
   * @exception IOException if the socket wasn't already connected.
   */
     public NetPersonality( Socket socket, Object context ) throws IOException {
           generatePersonality( socket, context );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** In this method you have to create your own NetSender and NetReceiver and
   *  save them in the "my_netsender" and "my_net_receiver" class attributes.
   *  If you want to create a new Personality model you can take the classes
   *  in wotlas.libs.net.personality as examples.
   *
   * @param socket an already opened socket
   * @param context an object to give to messages as they arrive.
   * @exception IOException if the socket wasn't already connected.
   */
     protected abstract void generatePersonality( Socket socket, Object context )
     throws IOException;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Starts this personality. When you create a new personality you can just
   * send messages but not receives ones. By calling this start() method you
   * launch your NetReceiver and thus can process incoming messages.
   */
     public void start() {
          my_netreceiver.start();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the current context.
   *
   * @param context the new context.
   */
     public void setContext( Object context ) {
         my_netreceiver.setContext( context );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the NetSender.
   *
   * @return the personality's NetSender
   */
     public NetSender getNetSender() {
         return my_netsender;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the NetReceiver.
   *
   * @return the personality's NetReceiver
   */
     public NetReceiver getNetReceiver() {
         return my_netreceiver;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the NetConnectionListener destination for this personality.
   *  The destination will receive information about the current NetSender,
   *  NetReceiver... and will know when the connection will be closed.
   *
   *  IMPORTANT : this is not an "add", the "set" means that you can only set
   *              one listener for this personality.
   *
   * @param listener an object implementing the NetConnectionListener interface.
   */
     public void setConnectionListener( NetConnectionListener listener ) {
         if( listener==null )
             return;

         this.listener = listener;

         if( my_netsender!=null && my_netreceiver!=null )
             listener.connectionCreated( this );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Asks the NetSender to send all his queued messages now. It's typically the method
   *  you use in case of a USER_AGGREGATION NetSender or when you want to send all the
   *  remaining messages before closing a connection with the AGGREGATION_MESSAGES NetSender.
   */
     public void pleaseSendAllMessagesNow() {
           my_netsender.pleaseSendAllMessagesNow();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Asks the NetReceiver to process all received messages now. This method
   *  does nothing if the NetReceiver is in the asynchronous mode.
   */
     public void pleaseReceiveAllMessagesNow() {
           my_netreceiver.pleaseReceiveAllMessagesNow();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To queue a message you want to send. 
   *  Works with every personality type ( but with different behaviour ).
   *
   * @param message message you want to send.
   */
     public void queueMessage( NetMessage message ) {
          my_netsender.queueMessage( message );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Waits for a message to arrive. Useful in some cases when the NetReceiver
   *  is synchronous. This method does nothing if the NetReceiver is asynchronous.
   *
   * @exception IOException if an IO error occur.
   */
     public void waitForAMessageToArrive() throws IOException{
     	my_netreceiver.waitForAMessageToArrive();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To close this connection. Erases all the allocated resources.
   */
     synchronized public void closeConnection() {
     	  if( my_netsender==null ||  my_netreceiver==null )
     	      return;
     	  
     	  if( listener!=null )
              listener.connectionClosed();

          my_netsender.stopThread();
          my_netreceiver.stopThread();

          my_netreceiver.closeSocket();

          my_netsender = null;
          my_netreceiver = null;
          listener=null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

