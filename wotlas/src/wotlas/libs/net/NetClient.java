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

package wotlas.libs.net;

import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;

import wotlas.libs.net.connection.TormConnection;
import wotlas.libs.net.message.ClientRegisterMessage;
import wotlas.libs.net.message.ClientRegisterMsgBehaviour;

import wotlas.utils.Debug;
import wotlas.utils.Tools;

/** A NetClient provides methods to initiate a connection with a server.
 *  The default NetConnection it provides is a TormConnection.
 *
 *  IMPORTANT: The NetClient object is only a tool that can be erased after its use.
 *             If must also be re-created if it fails to connect to the server.
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetConnection
 */

public class NetClient implements NetErrorCodeList
{
 /*------------------------------------------------------------------------------------*/

   /** Timeout for our server connection.
    */
       private static final int CONNECTION_TIMEOUT = 10000;

 /*------------------------------------------------------------------------------------*/

   /** Latest error message generated during the NetConnection creation.
    */
       private String errorMessage;

   /** Latest error code generated during the NetConnection creation.
    *  see NetError interface to see system error codes.
    */
       private short errorCode;

   /** So we have to stop the connection process ( "cancel action" )
    */
       private boolean stop;

   /** Connection validated by server ?
    */
       private boolean validConnection;

   /** SessionContext to set.
    */
       private Object sessionContext;

   /** NetConnection created for this client
    */
       private NetConnection connection;


 /*------------------------------------------------------------------------------------*/

   /** Constructor.
    */
       public NetClient() {
             stop = false;
             validConnection = false;
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates a connection object for the new connection.
    *  Override this method if you don't want to use the LoparConnection.
    *
    * @return a new LoparConnection associated to this socket.
    * @exception IOException IO error.
    */
      protected NetConnection getNewDefaultConnection( Socket socket )
      throws IOException {
              return new TormConnection( socket, null );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We try to build a connection with a server. On success we create a
   *  default NetConnection and return it. If an error occurs, we return null
   *  and an error message is set ( use getCurrentErrorMessage() to get it ).<p>
   *
   *  You have to give the name of the packages where we'll be able to find
   *  the NetMessageBehaviour classes.<p>
   *
   *  If the connection is validated the context is immediately set and, if the
   *  given context object implements the NetConnectionListener, we sets it as the
   *  default NetConnection connection observer ( connection.setConnectionListener() )...
   *
   * @param serverName complete server name
   * @param serverPort port to reach
   * @param key key identifying this client on the server side.
   * @param sessionContext an optional session context object (see NetConnection for more details).
   * @param msgPackages a list of packages where we can find NetMsgBehaviour Classes.
   * @return a NetConnection on success, null on failure.
   */
     public NetConnection connectToServer( String serverName, int serverPort,
                                            String key, Object sessionContext,
                                            String msgPackages[] )
     {
       Socket socket;
       errorMessage = null;
       errorCode = ERR_NONE;
       this.sessionContext = sessionContext;

       // to load the eventually new packages of messages...
          NetMessageFactory.getMessageFactory().addMessagePackages( msgPackages );

          try
          {
            // We try a connection with specified server.
               try{
                    socket = new Socket( serverName, serverPort );
               }
               catch(UnknownHostException e){
                    errorCode = ERR_CONNECT_FAILED;
                    errorMessage = new String("Unknown Server - "+serverName+":"+serverPort);
                    return null;
               }

               if( stop() ) {
                   clean();
                   return null;
               }

            // We create a new connection and send our key.
            // This client is the temporary context of the first
            // incoming message sent by the server.
               connection = getNewDefaultConnection( socket );
               connection.setContext( (Object) this );

               connection.queueMessage( new ClientRegisterMessage( key ) );
               connection.pleaseSendAllMessagesNow();

               if( stop() ) {
                   clean();
                   return null;
               }

            // We wait for the answer... and process it when it's there.
            // If something went wrong on the server the received message will
            // set an error_message. Otherwise "error_message" will remain null.
            
               if( connection.getNetReceiver().isSynchronous() )
               {
                  // easier case: the synchronous NetReceiver
                  // yeah I know we don't use any timeout here...
                  // the waitForAMessage should be modified to
                  // support a timeout. Easy to say...
                     connection.waitForAMessageToArrive();
                     connection.pleaseReceiveAllMessagesNow();
               }
               else {
                 // we cope with the asynchronous NetReceiver
                    synchronized( this ) {
                       connection.start();

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
                  if( errorCode==ERR_NONE ) {
                      errorCode = ERR_CONNECT_FAILED;
                      errorMessage = "Failed to reach server...";
                  }

                  Debug.signal(Debug.ERROR, this, "Connection Failed... code="
                                            +errorCode+" msg="+errorMessage );
                  clean();
                  return null;                   
               }

               return connection;
          }
          catch(IOException e){
           // Hum ! this server doesn't want to hear from us...
              if(errorCode==ERR_NONE) {
                 errorCode = ERR_CONNECT_FAILED;
                 errorMessage = "Failed to reach server...";
              }

              Debug.signal(Debug.ERROR, this, "Connection Failed... code="
                                            +errorCode+" msg="+errorMessage
                                            +" exception="+e.getMessage() );
              clean();
              return null;
 	  }
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the latest error message generated during the NetConnection creation
    *
    * @return the error message.
    */
      public String getErrorMessage() {
             return errorMessage;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the latest error code generated during the NetConnection creation
    *
    * @return the error code.
    */
      public short getErrorCode() {
             return errorCode;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set an error message for the NetConnection creation.
    *
    * @param errorMessage the new error message.
    */
      public synchronized void setErrorMessage( String errorMessage) {
             this.errorMessage = errorMessage;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set an error code for the NetConnection creation.
    *
    * @param errorCode the new error code.
    */
      public synchronized void setErrorCode( short errorCode ) {
             this.errorCode = errorCode;
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
             connection.setContext( sessionContext );

          // NetConnectionListener
             if(sessionContext instanceof NetConnectionListener)
                connection.setConnectionListener( (NetConnectionListener) sessionContext );
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
              if(connection!=null)
                 connection.closeConnection();

              connection=null;
              sessionContext=null;

              if(errorCode==ERR_NONE) {
                 errorMessage = new String("Connect operation canceled");
                 errorCode = ERR_CONNECT_CANCELED;
              }
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}





