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
 *  a socket connection and is a part of a Network Personality ( NetPersonality ).
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetReceiver
 * @see wotlas.libs.net.NetSender
 * @see wotlas.libs.net.NetPersonality
 */

abstract class NetThread extends Thread
{
 /*------------------------------------------------------------------------------------*/

   /** NetThread counter.
    */
       private static int count = 0;

 /*------------------------------------------------------------------------------------*/

   /** Our socket.
    */
       private Socket socket;

   /** A link to our NetPersonality
    */
       private NetPersonality personality;

   /** tells the thread if it must stop.
    */ 
       private boolean stop_thread;

 /*------------------------------------------------------------------------------------*/

   /** NetThread constructor.
    *
    * @param socket an already opened socket. 
    * @param personality a NetPersonality linked to this socket.
    */
      protected NetThread( Socket socket, NetPersonality personality ){
         super("NetThread"+count);
         count++;

         this.socket = socket;
         this.personality = personality;
         stop_thread = false;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  Use this method to stop this thread.
    *   This method does nothing if the thread has already been stopped.
    */
      synchronized public void stopThread(){
           stop_thread = true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To see if this thread should stop...
    *
    * @return true if the thread should stop.
    */
      synchronized protected boolean shouldStopThread(){
           return stop_thread;
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
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  Asks the NetPersonality to close the socket connection.
    */
      protected void closeConnection() {
        // we ask the NetPersonality to perform some cleanup
        // and signal that the connection was closed ( connectionListener )
           personality.closeConnection();
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


}
