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
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import wotlas.utils.Debug;


/** A NetThread is an abstract class representing a thread that manages
 *  a socket connection.
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetReceiver
 * @see wotlas.libs.net.NetSender
 */

abstract public class NetThread extends Thread
{
 /*------------------------------------------------------------------------------------*/

   /** Thread Counters. ( This JVM may have different servers for example, each of them
    *  owning a set of threads ).
    */
       private static int threadCounter[] = new int[1];

 /*------------------------------------------------------------------------------------*/

   /** Our socket.
    */
       private Socket socket;

   /** tells the thread if it must stop.
    */ 
       private boolean stopThread;

   /** An ID representing our owner. This is also our index in the threadCounter array.
    */
       private byte localID;

 /*------------------------------------------------------------------------------------*/

   /** NetThread constructor.
    *
    * @param socket an already opened socket. 
    */
      protected NetThread( Socket socket ){
         super("NetThread");

         this.socket = socket;
         stopThread = false;
         localID = -1;       // no owner for now, see attachTo() method
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  Use this method to stop this thread.
    *   This method does nothing if the thread has already been stopped.
    */
      synchronized public void stopThread(){
           stopThread = true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To see if this thread should stop...
    *
    * @return true if the thread should stop.
    */
      synchronized protected boolean shouldStopThread(){
           return stopThread;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  Method to close the socket connection.
    */
      public void closeSocket(){
           try{
                 socket.getInputStream().close();
                 socket.getOutputStream().close();
                 socket.close();
           }
           catch(IOException e) {
              // socket probably already closed...
              // be just to be sure...
                 try{
                      socket.close();
                 }
                 catch(IOException ioe ) {}
           }


        // we decrease the number of threads for our set...
           if( localID!=-1 )
               synchronized( threadCounter ) {
                   threadCounter[localID] -= 2; // this method is called once
               }                                // and there are 2 threads.
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To get a buffered socket input stream.
    *
    * @param buffer_size buffer size for the BufferedInputStream.
    * @return a buffered input stream linked to the socket input stream.
    * @exception IOException if the socket has not already been opened.
    */
      protected BufferedInputStream getBufferedInputStream( int buffer_size )
      throws IOException {
           return new BufferedInputStream( socket.getInputStream(), buffer_size );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To get a buffered socket output stream.
    *
    * @param buffer_size buffer size for the BufferedOutputStream.
    * @return a buffered output stream linked to the socket output stream.
    * @exception IOException if the socket has not already been opened.
    */
      protected BufferedOutputStream getBufferedOutputStream( int buffer_size )
      throws IOException {
           return new BufferedOutputStream( socket.getOutputStream(), buffer_size );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To get the number of opened socket for the given owner ID.
    *   This method should only be used for resource management (NetServer).
    *
    * @param localID the ID of the sockets owner
    * @return the current number of opened sockets for this owner.
    */
      public static int getOpenedSocketNumber( byte localID ) { 
             return getThreadsNumber(localID)/2;  // two threads per socket
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To get the number of threads for the given owner ID.
    *   This method should only be used for resource management (NetServer).
    *
    * @param localID the ID of the sockets owner (ID>0).
    * @return the current number of working threads for this owner.
    */
      public static int getThreadsNumber( byte localID ) { 
           synchronized (threadCounter) {
            // if no socket were opened it is possible that the
            // array length was not increased yet.
               if( localID>=threadCounter.length )
                   return 0;
           
               return threadCounter[localID];
           }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Attach this thread to the given owner ID.<p>
    *
    *  The purpose of this method is mainly to keep trace of the number
    *  of opened sockets. It's optional and currently only used by the
    *  NetServer class.
    * 
    *  @param localID an ID identifying an entity that owns this thread.
    */
      public void attachTo( byte localID ) {
          this.localID = localID;

          synchronized(threadCounter)
          {
            // new localID ? array not big enough ?
              if( localID>=threadCounter.length ) {
                 int tmp[] = new int[localID+1];
                
                 for( int i=0; i<threadCounter.length; i++ )
                      tmp[i] = threadCounter[i];
                
                 threadCounter = tmp;
              }
          
            // counter incr
               threadCounter[localID]++;
          }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
