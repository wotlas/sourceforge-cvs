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
import java.net.UnknownHostException;

import wotlas.libs.net.personality.LoparPersonality;
import wotlas.libs.net.message.ClientRegisterMessage;
import wotlas.libs.net.message.ClientRegisterMsgBehaviour;

import wotlas.utils.Debug;
import wotlas.utils.Tools;

/** A NetClient provides methods to initiate a connection with a server.
 *  The default NetPersonality it provides is a LoparPersonality.
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetPersonality
 */

public class NetClient
{
 /*------------------------------------------------------------------------------------*/

   /** Latest error message generated during the NetPersonality creation.
    */
       private String error_message;

 /*------------------------------------------------------------------------------------*/

   /** Constructor.
    */
       public NetClient() {
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates a personality object for the new connection.
    *  Override this method if you don't want to use the LoparPersonality.
    *
    * @return a new LoparPersonality associated to this socket.
    * @exception IOException IO error.
    */
      protected NetPersonality getNewDefaultPersonality( Socket socket )
      throws IOException {
              return new LoparPersonality( socket, null );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We try to build a connection with a server. On success we create a
   *  default NetPersonality and return it. If an error occurs, we return null
   *  and an error message is set ( use getCurrentErrorMessage() to get it ).
   *
   *  You have to give the name of the packages where we'll be able to find
   *  the NetMessageBehaviour classes.
   *
   * @param server_name complete server name
   * @param server_port port to reach
   * @param key key identifying this client on the server side.
   * @param context an optional context object (see NetReceiver for more details).
   * @param msg_packages a list of packages where we can find NetMsgBehaviour Classes.
   * @return a NetPersonality on success, null on failure.
   */
     public NetPersonality connectToServer( String server_name, int server_port,
                                            String key, Object context,
                                            String msg_packages[] )
     {
       Socket socket;
       NetPersonality personality=null;
       error_message = null;

       // to make sure there is one...
          NetMessageFactory.createMessageFactory( msg_packages );

          try
          {
            // We try a connection with specified server.
               try{
                    socket = new Socket( server_name, server_port );
               }
               catch(UnknownHostException e){
                    error_message = new String("Unknown Server - "+server_name+":"+server_port);
                    return null;
               }

            // We create a new personality and send our key.
               personality = getNewDefaultPersonality( socket );
               personality.setContext( (Object) this );

               personality.queueMessage( new ClientRegisterMessage( key ) );
               personality.pleaseSendAllMessagesNow();

            // We wait for the answer... and process it when it's there.
            // If something went wrong on the server the received message will
            // set an error_message.
               personality.waitForAMessageToArrive();
               personality.start();
               personality.pleaseReceiveAllMessagesNow();

               Tools.waitTime(200); // if the NetReceiver is asynchronous we have returned
                                    // immediately from the previous method call... this is
                                    // a dirty way to wait for the message to be processed.
                                    // the pleaseReceive... method should be extended with
                                    // an extra parameter WAIT, NO_WAIT.

            // Success ?
               if(error_message!=null) {
                  Debug.signal(Debug.ERROR, this, "Server returned an Error");
                  personality.closeConnection();
                  return null;
               }

               personality.setContext( context );
               return personality;
          }
          catch(IOException e){
           // Hum ! this server doesn't want to hear from us...
              error_message = e.getMessage();

              if(personality!=null)
                 personality.closeConnection();
   
              return null;
 	  }
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the latest error_message generated during the NetPersonality creation
    *
    * @return the error message.
    */
      public String getErrorMessage() {
             return error_message;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set an error_message for the NetPersonality creation.
    *
    * @param error_message the new error message.
    */
      public void setErrorMessage( String error_message) {
             this.error_message = error_message;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}





