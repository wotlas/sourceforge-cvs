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

import wotlas.libs.net.personality.TormPersonality;
import wotlas.libs.net.message.ClientRegisterMessage;
import wotlas.libs.net.message.ClientRegisterMsgBehaviour;

import wotlas.utils.Debug;
import wotlas.utils.Tools;

/** A NetClient provides methods to initiate a connection with a server.
 *  The default NetPersonality it provides is a TormPersonality.
 *
 *  IMPORTANT: The NetClient object is only a tool that can be erased after its use.
 *             If must also be re-created if it fails to connect to the server.
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetPersonality
 */

public class NetClient
{
 /*------------------------------------------------------------------------------------*/

   /** Timeout for our server connection.
    */
       private static final int CONNECTION_TIMEOUT = 10000;

 /*------------------------------------------------------------------------------------*/

   /** Latest error message generated during the NetPersonality creation.
    */
       private String errorMessage;

   /** So we have to stop the connection process ( "cancel action" )
    */
       private boolean stop;

   /** Connection validated by server ?
    */
       private boolean validConnection;

   /** SessionContext to set.
    */
       private Object sessionContext;

   /** NetPersonality created for this client
    */
       private NetPersonality personality;


 /*------------------------------------------------------------------------------------*/

   /** Constructor.
    */
       public NetClient() {
             stop = false;
             validConnection = false;
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
              return new TormPersonality( socket, null );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We try to build a connection with a server. On success we create a
   *  default NetPersonality and return it. If an error occurs, we return null
   *  and an error message is set ( use getCurrentErrorMessage() to get it ).<p>
   *
   *  You have to give the name of the packages where we'll be able to find
   *  the NetMessageBehaviour classes.<p>
   *
   *  If the connection is validated the context is immediately set and, if the
   *  given context object implements the NetConnectionListener, we sets it as the
   *  default NetPersonality connection observer ( personality.setConnectionListener() )...
   *
   * @param serverName complete server name
   * @param serverPort port to reach
   * @param key key identifying this client on the server side.
   * @param sessionContext an optional session context object (see NetPersonality for more details).
   * @param msgPackages a list of packages where we can find NetMsgBehaviour Classes.
   * @return a NetPersonality on success, null on failure.
   */
     public NetPersonality connectToServer( String serverName, int serverPort,
                                            String key, Object sessionContext,
                                            String msgPackages[] )
     {
       Socket socket;
       errorMessage = null;
       this.sessionContext = sessionContext;

       // to make sure there is one...
          NetMessageFactory.getMessageFactory().addMessagePackages( msgPackages );

          try
          {
            // We try a connection with specified server.
               try{
                    socket = new Socket( serverName, serverPort );
               }
               catch(UnknownHostException e){
                    errorMessage = new String("Unknown Server - "+serverName+":"+serverPort);
                    return null;
               }

               if( stop() ) {
                   clean();
                   return null;
               }

            // We create a new personality and send our key.
            // This client is the temporary context of the first
            // incoming message sent by the server.
               personality = getNewDefaultPersonality( socket );
               personality.setContext( (Object) this );

               personality.queueMessage( new ClientRegisterMessage( key ) );
               personality.pleaseSendAllMessagesNow();

               if( stop() ) {
                   clean();
                   return null;
               }

            // We wait for the answer... and process it when it's there.
            // If something went wrong on the server the received message will
            // set an error_message. Otherwise "error_message" will remain null.
            
               if( personality.getNetReceiver().isSynchronous() )
               {
                  // easier case: the synchronous NetReceiver
                  // yeah I know we don't use any timeout here...
                  // the waitForAMessage should be modified to
                  // support a timeout. Easy to say...
                     personality.waitForAMessageToArrive();
                     personality.pleaseReceiveAllMessagesNow();
               }
               else {
                 // we cope with the asynchronous NetReceiver
                    synchronized( this )
                    {
                       personality.start();

                       try{
                          wait( CONNECTION_TIMEOUT );
                       } catch(InterruptedException ie){}
                    }
               }

               if(stop()) {
                  clean();
                  return null;
               }

            // Success ? let's see if there is an error message
            // (see the messages in the wotlas.libs.net.message package)
               if( !validConnection() ) {

                  if(getErrorMessage()!=null)
                      Debug.signal(Debug.ERROR, this, "Server returned an Error");
                  else {
                      setErrorMessage( "The server is not running at the moment." );
                      Debug.signal(Debug.ERROR, this, "Connection Timeout");
                  }

                  clean();
                  return null;                   
               }

               return personality;
          }
          catch(IOException e){
           // Hum ! this server doesn't want to hear from us...
              errorMessage = "Failed to join server: "+e.getMessage();

              clean();
              return null;
 	  }
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the latest error_message generated during the NetPersonality creation
    *
    * @return the error message.
    */
      public String getErrorMessage() {
             return errorMessage;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set an error message for the NetPersonality creation.
    *
    * @param errorMessage the new error message.
    */
      public synchronized void setErrorMessage( String errorMessage) {
             this.errorMessage = errorMessage;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To stop the connection process...
     */
       public synchronized void stopConnectionProcess() {
             stop=true;
             notify();
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Do we have to stop the connection process ?
     *
     * @return true if we must stop;
     */
       public synchronized boolean stop() {
             return stop;
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is for the ServerWelcomeMsgBehaviour only: it validates the
     *  connection with the server and sets the NetMessage Context.
     */
       public synchronized void validateConnection() {
             validConnection=true;
             personality.setContext( sessionContext );

          // NetConnectionListener
             if(sessionContext instanceof NetConnectionListener)
                personality.setConnectionListener( (NetConnectionListener) sessionContext );
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Do we have a valid connection ?
     *
     * @return true if it's the case...
     */
       public synchronized boolean validConnection() {
             return validConnection;
       }


 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To clean this NetClient
     */
       private synchronized void clean(){
              if(personality!=null)
                 personality.closeConnection();

              personality=null;
              sessionContext=null;
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}





