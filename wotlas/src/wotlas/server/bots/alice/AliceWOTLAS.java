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

package wotlas.server.bots.alice;

import org.alicebot.server.net.listener.AliceChatListener;
import org.alicebot.server.core.ActiveMultiplexor;
import org.alicebot.server.core.Multiplexor;
import org.alicebot.server.core.logging.Log;
import org.alicebot.server.core.util.Trace;
import org.alicebot.server.core.responder.Responder;
import org.alicebot.server.core.responder.TextResponder;

import java.net.*;
import java.util.Properties;

/**
 *  Alice Chat Listener for the wotlas protocol. More information on
 *  alicebot at http://alicebot.org .
 *
 *  This class uses a pool of threads to process incoming requests, we also
 *  implement a simple load-balancing algorithm to dispatch the requests.
 *
 *  This listener is loaded at start-up by the org.alicebot.server.net.AliceServer
 *
 * @author aldiss
 */

public class AliceWOTLAS implements AliceChatListener {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Standard AliceWOTLAS port.
   */
    public static final int ALICE_WOTLAS_PORT = 21121;

  /** Maximum number of connections we accept from other servers.
   */
    public static final int MAX_ALICE_WOTLAS_CONNECTIONS = 20;

  /** Number of threads in our pool to handle Alice requests
   */
    public static final int POOL_THREADS_NUMBER = 5;

  /** Max number of requests a thread in our pool can accept at the same time.
   */
    public static final int MAX_POOL_THREAD_REQUEST = 20;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Our associated server which listens on the ALICE_WOTLAS_PORT and forward requests
   *  to us.
   */
    protected AliceWotlasServer aliceWotlasServer;

  /** A pool of threads (using FIFOs) on which we will dispatch our requests.
   */
    protected AliceRequestFIFO fifoPool[];

  /** The index of the current thread we are using in our pool.
   */
    protected int poolThreadIndex;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor.
   */
    public AliceWOTLAS() {
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   *  Given a properties set, initializes the listener if possible.
   *
   *  @param properties   the properties set that may (or may not!)
   *                      contain necessary configuration values
   *
   *  @return <code>true</code> if the initialization was successful, <code>false</code> if not
   */
    public boolean initialize( Properties properties ) {

       // 1 - We retrieve the host name
          String hostName = "localhost";

          try {
             hostName = InetAddress.getLocalHost().getHostName();
          }
          catch (UnknownHostException e) {
              Log.userinfo("AliceWOTLAS: no host name found. Using localhost.", Log.STARTUP);
              e.printStackTrace();
          }


       // 2 - We create some FIFO threads to handle the future incoming requests
          fifoPool = new AliceRequestFIFO[POOL_THREADS_NUMBER];
          poolThreadIndex = 0;

          for( int i=0; i<POOL_THREADS_NUMBER; i++ ) {
               fifoPool[i] = new AliceRequestFIFO( MAX_POOL_THREAD_REQUEST );
               fifoPool[i].start();
          }

          Log.userinfo("AliceWOTLAS: created "+POOL_THREADS_NUMBER+" threads to process incoming requests", Log.STARTUP);

       // 3 - We create our server and start it !
          String messagePackages[] = { "wotlas.server.bots.alice.server" };

          aliceWotlasServer = new AliceWotlasServer( hostName,
                                             ALICE_WOTLAS_PORT,
                                             messagePackages,
                                             MAX_ALICE_WOTLAS_CONNECTIONS,
                                             this );

          Log.userinfo("Started AliceWOTLAS listener on "+hostName+":"+ALICE_WOTLAS_PORT, Log.STARTUP);
          return true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Starts the server of our listener.
    */
     public void run() {
          aliceWotlasServer.start();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**
    *  Shuts down the process.
    */
     public void shutdown() {
          Log.userinfo("Shutting down AliceWOTLAS listener & server.", Log.LISTENERS);
          aliceWotlasServer.shutdown();

            for( int i=0; i<POOL_THREADS_NUMBER; i++ )
                 fifoPool[i].shutdown();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get an answer to a message. This is the root method processes must call
    *  to get an ALICE answer. We dispatch the request call on one of our FIFO threads
    *  in a way to perform load balancing.
    *
    *  If all the threads have reached their MAX_POOL_THREAD_REQUEST the request will
    *  be discarded and an empty answer message will be sent to the client. This way
    *  we ensure a maximum level of CPU use on the host computer.
    *
    * @param playerPrimaryKey primaryKey of the client asking this message (userID)
    * @param botPrimaryKey primaryKey of the wotlas bot target of this message
    * @param message message to ask ALICE
    * @param serverID original server identifier sending this request (will be used
    *        to send back the answer)
    * @return true if the request was processed, false if it wasn't.
    */
     public boolean getAnswer( String playerPrimaryKey, String botPrimaryKey, String message, int serverID ) {

        // 1 - Try to dispatch the request on one of our threads
        //     This is a simple load balancing algo
           AliceRequest request = new AliceRequest( playerPrimaryKey, botPrimaryKey, message, serverID );
           int endLoop = poolThreadIndex; // the thread index we begin with
           boolean succeeded;

           do{
             succeeded = fifoPool[poolThreadIndex].addRequest( request );
             poolThreadIndex = (poolThreadIndex+1)%fifoPool.length; // move to next thread in any case

             if( succeeded )
                 return true; // success, the thread will process the request
                              // also next time we'll begin the loop with the next thread
           }
           while( poolThreadIndex!=endLoop );  // have we tried all the threads in the pool ?

        // 2 - Failed ! All the threads are busy ! We ignore the request...
           aliceWotlasServer.sendAnswer( playerPrimaryKey, botPrimaryKey,
                           "sorry, I didn't hear, can you repeat please ?", serverID );
           return false;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** A Thread that processes alice requests. Requests are stored in a FIFO.
    *  The FIFO has a FIXED length so that the CPU use is strictly controlled.
    */
     class AliceRequestFIFO extends Thread {

      /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

       /** Our Request FIFO. Note it has a fixed length.
        */
         private AliceRequest fifo[];

       /** Current end cursor of the fifo. Gives us the next index (in the fifo)
        *  where we can insert a new incoming request.
        */
         private int end;

       /** To tell the thread to stop.
        */
         private boolean shutdown;

      /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

       /** Constructor with length of the fifo to use.
        */
     	public AliceRequestFIFO( int fifoLength ) {
     	   fifo = new AliceRequest[fifoLength];
     	   end=0;
     	   shutdown=false;
     	}

      /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
       
       /** FIFO process
        */
     	public void run() {

            int index=0;            // our current index in the FIFO
            String answer = null;   // answer given by ALICE
            boolean sleep;          // do we have to sleep ? or is there requests to process ?

         // We process our requests
            while(!shutdown) {

               // 1 - Wait for something to come
                  synchronized(this) {
                     while( fifo[index]==null )
                       try{
                          this.wait();
                       }catch( Exception e ) {}
                  }

                  if(shutdown)
                     return;

               // 2 - Process Available Requests...
                  do{
                    // 2.1 - Get the answer to the request from Alice
                       try{
                         answer = ActiveMultiplexor.getInstance().getResponse(
                                fifo[index].message,
                                fifo[index].playerPrimaryKey+":"+fifo[index].botPrimaryKey );
                       }
                       catch(Exception e) {
                         answer=null;
                         e.printStackTrace();
                       }

                    // 2.2 - Send the answer back to the wotlas server                
                       if(answer!=null)
                          aliceWotlasServer.sendAnswer( fifo[index].playerPrimaryKey,
                                                        fifo[index].botPrimaryKey,
                                                        answer, fifo[index].serverID );

                    // 2.3 - Move to next request in our FIFO
                       synchronized(this) {
                          fifo[index] = null;
                          index = (index+1)%fifo.length;
                          sleep = (fifo[index]==null);
                       }
                  }
                  while(!sleep); // we continue while not told to go to sleep
            }
     	}

      /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
     	
       /** Adds a request to this FIFO
        * @return true if the request was accepted, false if our FIFO is full.
        */
        public synchronized boolean addRequest( AliceRequest request ) {
           if(fifo[end]!=null)
              return false; // our FIFO is full, we can't accept the request
           
           fifo[end] = request;
           end = (end+1)%fifo.length;  //  move the end of the FIFO
           this.notify();              // notification to the thread ! wake up !
           return true;
        }

      /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

       /** To shutdown this FIFO.
        */
        public synchronized void shutdown() {
           shutdown=true;
           this.notify();
        }

      /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** A request for alice... We use this class in a C data structure way.
    */
     class AliceRequest {

      /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
       
       /** Primary Key (userID) of the player asking this message
        */
          public String playerPrimaryKey;

       /** Primary Key of the bot target of this message
        */
          public String botPrimaryKey;
       
       /** Message to send to ALICE
        */
          public String message;

       /** Server ID of the server that asked for the request. We'll use this ID
        *  to send back ALICE's answer.
        */
          public int serverID;

      /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

       /** Constructor.
        */
          public AliceRequest( String playerPrimaryKey, String botPrimaryKey, String message, int serverID ) {
              this.playerPrimaryKey = playerPrimaryKey;
              this.botPrimaryKey = botPrimaryKey;
              this.message = message;
              this.serverID = serverID;
     	  }

      /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
