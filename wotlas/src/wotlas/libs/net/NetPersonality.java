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
 * the generatePersonality() method.
 * 
 * Default personalities are provided in the wotlas.libs.net.personality package.
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

   /** A key identifying our client.
    */
      private String key;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with an already opened socket. The context given to the first received
   *  message will be this NetPersonality object.
   *
   * @param socket an already opened socket
   * @exception IOException if the socket wasn't already connected.
   */
     public NetPersonality( Socket socket ) throws IOException {
           generatePersonality( socket, this );
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

  /** To close this connection. Erases all the allocated resources.
   */
     public void closeConnection() {
          my_netsender.stopThread();
          my_netreceiver.stopThread();

          my_netreceiver.closeSocket();
          my_netsender = null;
          my_netreceiver = null;
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

  /** To set the personality's key.
   *
   * @param key the personality's key.
   */
     public void setKey( String key ) {
         this.key = key;
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
}

