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

/** A NetClient...
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetPersonality
 */

public class NetClient
{
 /*------------------------------------------------------------------------------------*/

   /** Latest error message...
    */
       static private String error_message;

 /*------------------------------------------------------------------------------------*/

   /** NetClient personality;
    */
      private NetPersonality personality;

 /*------------------------------------------------------------------------------------*/

     /**  Private Constructor.
      *
      *  @param personality a NetPersonality created during the connectToServer() call.
      */
        private NetClient( NetPersonality personality ) {
           this.personality = personality;
        }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates a personality object for the new connection.
    *  Override this method if you don't want to use the LoparPersonality.
    *
    * @return a new LoparPersonality associated to this socket.
    * @exception IOException IO error.
    */
      protected static NetPersonality getNewDefaultPersonality( Socket socket )
      throws IOException {
              return new LoparPersonality( socket );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the default personality of this NetClient.
    */
      public NetPersonality getPersonality() {
              return personality;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We try to build a connection with a server. On success we create a
   *  default NetPersonality and return it. If an error occurs, we return null
   *  and an error message is set ( use getCurrentErrorMessage() to get it ).
   *
   * @param server_name complete server name
   * @param server_port port to reach
   * @param key key identifying this client on the server side.
   * @param context an optional context object (see NetReceiver for more details).
   * @return a NetClient on success, null on failure.
   */
     public static NetClient connectToServer( String server_name, int server_port,
                                              String key, Object context )
     {
       Socket socket;
       NetPersonality personality=null;

       // to make sure there is one...
          NetMessageFactory.createMessageFactory();

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

               personality.queueMessage( new ClientRegisterMessage( key ) );
               personality.pleaseSendAllMessagesNow();

            // We wait for the answer... and process it when it's there.
            // If something went wrong on the server the received message will
            // probably generate an IOException and tell us more.
               personality.waitForAMessageToArrive();
               personality.pleaseReceiveAllMessagesNow();

            // Success !
               personality.setContext( context );
               return new NetClient( personality );
          }
          catch(IOException e){
              error_message = e.getMessage();
              return null;
 	  }
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}





