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

import wotlas.utils.Debug;
import wotlas.libs.net.message.*;

import java.io.IOException;
import java.net.Socket;

/** 
 * A NetPersonality brings together a NetSender and NetReceiver to send and receive
 * messages. This class is abstract because there are many different types of
 * NetSender and NetReceiver. You have to create your own ones by redefining
 * the generatePersonality() method. Default personalities are provided in the
 * wotlas.libs.net.personality package.
 *<br>
 * Useful methods you can invoke to send messages :
 *<br>
 *    - queueMessage( message );
 *      To queue a message you want to send. 
 *      Works with every personality type ( but with different behaviour ).
 *<br>
 *    - pleaseSendAllMessagesNow();
 *      Asks the NetSender to send all his queued messages now. It's typically the method
 *      you use in case of a USER_AGGREGATION NetSender.
 *
 *<br><p>
 * Useful methods you can invoke to receive messages :
 *<br>
 *    - pleaseReceiveAllMessagesNow();
 *      Asks the NetReceiver to process all received messages now. This method
 *      does nothing if the NetReceiver is in the asynchronous mode. In the
 *      asynchronous mode you have no method to call : everything is automated.
 *<br>
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

   /** An eventual PingThread ( internal class )
    */
      private PingThread pingThread;

   /** An eventual lock for the pingThread
    */
      private Object pingLock;

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

  /** Constructor with an already opened socket and a local ID to identify
   *  a set of threads ( see NetServer ).
   *
   * @param socket an already opened socket
   * @param context object to give to messages when they arrive.
   * @param localID an ID that identifies a set of threads.
   * @exception IOException if the socket wasn't already connected.
   */
     public NetPersonality( Socket socket, Object context, byte localID ) throws IOException {
           generatePersonality( socket, context );
        
        // we attach the socket of the NetThread to 
           my_netreceiver.attachTo( localID );
           my_netsender.attachTo( localID );
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
   *  you use in case of a USER_AGGREGATION NetSender.
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
            if( my_netsender!=null )
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
   *  Before closing the connection we wait for the remaining messages
   *  to be sent. We then perform some clean up.
   */
     synchronized public void closeConnection()
     {
     	  if( my_netsender==null ||  my_netreceiver==null )
     	      return;
     	  
          if( listener!=null )
              listener.connectionClosed( this );

       // no more message handling
          my_netreceiver.stopThread();

       // we wait for the remaining messages to be sent
          my_netsender.pleaseSendAllMessagesNow();
            
       // massive destruction
          my_netsender.stopThread();
          my_netsender.closeSocket();  // only lets the NetReceiver finish its work
                                       // before closing
          
          synchronized( my_netsender ) {
                my_netsender.notify();    // we don't forget to wake up the thread !
          }                               // (the NetSender is locked when there are no msgs )

          if( pingLock!=null )
            synchronized( pingLock ){
               pingThread.stopThread();
            }

          my_netsender = null;
          my_netreceiver = null;
          listener=null;
     }

 /*------------------------------------------------------------------------------------*/

  /** To set a ping listener for this network connection.
   *
   * @param pListener the object that will receive ping information.
   */
    public void setPingListener( NetPingListener pListener ) {

    	if( pingLock!=null )
            synchronized( pingLock ) {
                if( pingThread==null ) return; // No longer living net connnection

    	     // just swap the listener
    	        pingThread.pListener = pListener;
                return;
            }
    	
      // Ping Thread Creation
         pingLock = new Object();
         pingThread = new PingThread(pListener);
         pingThread.start();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Called by the NetReceiver when it receives a PingMessage (if not in SendBack mode).
   * @param sequenceID ping message's sequence ID.
   */
    protected void receivedPingMessage( int sequenceID ) {

       if( pingLock==null ) return;

       synchronized( pingLock ) {
           if( pingThread==null || pingThread.sequenceID!=sequenceID)
    	       return;

           pingThread.lastPingValue = (int)(System.currentTimeMillis()-pingThread.lastPingT0);
           pingThread.pingReceivedBack = true;
           pingThread.pListener.pingComputed( pingThread.lastPingValue );
       }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To use 
   */
    public synchronized void sendBackPingMessages( boolean sendBack ) {

        if( pingLock!=null && sendBack ) {
            Debug.signal( Debug.ERROR, this, "Conflict detected: ping thread exists ! Can't send back ping messages");
        }

        if( my_netreceiver!=null )
            my_netreceiver.sendBackPingMessages( sendBack );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** A Thread that send PingMessages and manages the ping info.
   *  It is created when a NetPingListener is set and lives as long as the network
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
         public PingThread( NetPingListener pListener ) {
             sequenceID = 0;
             pingReceivedBack = false;
             shouldStopPingThread = false;
             this.pListener = pListener;
         }

     /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

       /** Our Ping Loop
        */
         public void run(){

            while( !shouldStopPingThread ) {

              // 1 - We send a PingMessage to the other side
                 queueMessage( new PingMessage( sequenceID ) );
                 lastPingT0 = System.currentTimeMillis();

              // 2 - We wait 1 second
                 synchronized( this ) {
                    try{
                       wait( 1000 );
                    }catch( Exception e ) {}
                    
                    if(shouldStopPingThread)
                       break;
                 }

              // 3 - Do we have received the answer ?
                 synchronized( pingLock ) {
                     if(!pingReceivedBack) {
                        // we wait one more second and a half
                           try{
                              wait( 1500 );
                           }catch( Exception e ) {
                           }

                        // if there is still no answer we declare the message lost
                           if( !pingReceivedBack ) {
                               lastPingValue = NetPingListener.PING_FAILED;
                               pListener.pingComputed( lastPingValue );
                           }
                     }

                     sequenceID = (byte) ( (sequenceID+1)%120 );
                     pingReceivedBack = false;
                 }
            }

         // Advertise the end of this connection
            synchronized( pingLock ) {
               pListener.pingComputed( NetPingListener.PING_CONNECTION_CLOSED );
               pListener = null;
               pingThread = null;
            }
         }

     /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

         public synchronized void stopThread() {
             shouldStopPingThread = true;
             notify();
         }

     /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    };

 /*------------------------------------------------------------------------------------*/

}

