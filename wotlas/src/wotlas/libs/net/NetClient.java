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
       private String error_message;

   /** So we have to stop the connection process ( "cancel action" )
    */
       private boolean stop;

   /** Connection validated by server ?
    */
       private boolean validConnection;

   /** Context to set (temp attribute)
    */
       private Object context;

   /** NetPersonality created for this client
    */
       private NetPersonality personality;


 /*------------------------------------------------------------------------------------*/

   /** Constructor.
    */
       public NetClient() {
             stop = false;
             validConnection = false;

          // We delete the eventual old NetMessageFactory
             if( NetMessageFactory.getDefaultMessageFactory() != null )
                 NetMessageFactory.getDefaultMessageFactory().deleteFactory();
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
       error_message = null;
       this.context = context;

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
                       }
                       catch(InterruptedException ie){
                       }
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
                      setErrorMessage( "Server reached, but connection timeout" );        
                      Debug.signal(Debug.ERROR, this, "Connection Timeout");
                  }

                  clean();
                  return null;                   
               }

               return personality;
          }
          catch(IOException e){
           // Hum ! this server doesn't want to hear from us...
              error_message = "Error during connection: "+e.getMessage();

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
             return error_message;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set an error_message for the NetPersonality creation.
    *
    * @param error_message the new error message.
    */
      public synchronized void setErrorMessage( String error_message) {
             this.error_message = error_message;
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
             personality.setContext( context );

          // NetConnectionListener
             if(context instanceof NetConnectionListener)
                personality.setConnectionListener( (NetConnectionListener) context );
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
              context=null;
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}





