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

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InterruptedIOException;

import wotlas.utils.Debug;
import wotlas.libs.net.personality.TormPersonality;
import wotlas.libs.net.message.ServerErrorMessage;
import wotlas.libs.net.message.ServerWelcomeMessage;

import wotlas.libs.net.message.ServerErrorMsgBehaviour;
import wotlas.libs.net.message.ServerWelcomeMsgBehaviour;


/** A NetServer awaits client connections. There are two types of Server :
 *
 *    - server that have no predefined connection list. That means we
 *      don't know the clients that we are connected to (default).
 *
 *    - server that maintains client accounts. To create this type of server
 *      extend this class and override the accessControl() method.
 *
 *
 * This server uses a TormPersonality as default. If you want to use another
 * one override the getNewDefaultPersonality() method. Note also that when we create
 * our new personality we don't assign any "context" object. To assign one do it in
 * the initializeConnection() method with the "personality.setContext()" call.
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetPersonality
 */


abstract public class NetServer extends Thread
{
 /*------------------------------------------------------------------------------------*/

  /** Server Socket
   */
     private ServerSocket server;

  /** Current number of clients
   */
     private int current_nb_clients;

  /** Stop server ?
   */
     private boolean stop_server;

  /** Maximum number of clients to accept at the same time.
   */
     private int max_clients;

  /** User lock to temporarily forbid new connections
   */
     private boolean server_lock;


 /*------------------------------------------------------------------------------------*/

     /**  Constructs a NetServer but does not starts it. Call the start()
      *   method to start the server. You have to give the name of the packages
      *   where we'll be able to find the NetMessageBehaviour classes.
      *
      *  @param server_port port on which the server listens to clients.
      *  @param max_clients maximum number of clients to accept at the same time.
      *  @param msg_packages a list of packages where we can find NetMsgBehaviour Classes.
      */
        public NetServer( int server_port, int max_clients, String msg_packages[] )
        {
              super("Server");
              stop_server = false;
              server_lock = false;
              this.max_clients = max_clients;

           // we create a message factory
              NetMessageFactory.createMessageFactory( msg_packages );

           // ServerSocket inits
              try{
                  server = new ServerSocket(server_port); 
                  server.setSoTimeout(5000);
               }
               catch (IOException e){
                  Debug.signal( Debug.FAILURE, this, e );
                  System.exit(1);
               }
        }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** This method is called automatically when a new client establishes a connection
    *  with this server ( the client sends a ClientRegisterMessage ). We are supposed
    *  to provide here some basic access control.
    *
    *  The default implementation contains the STRICT MINIMUM : we accept every
    *  client without considering the key they provide.
    *
    *  You can redefine this method (recommended) to :
    *
    *  1) consider if the key ( key parameter ) provided by the client is correct for your
    *     application. For example for a chat Server a key could be a chat channel name. In
    *     the case of a repository server, a key would be a client login. You can then create
    *     your own set of messages to ask for a password.
    *
    *  2) initialize the client context ( personality.setContext() ). The context can be
    *     any type of object and should be client dependent. It will be given to the messages
    *     coming from the client. For example, in a ChatServer the context could be the Chat
    *     chanel object the client wants to register to. Messages would have then a direct
    *     access to their right chat channel.
    *
    *  3) MANDATORY : if you decide to accept this client, call the acceptClient() method.
    *     it will send a "Welcome!" to the client. If you decide to refuse the client, call
    *     the refuseClient() method with an appropriate error message. It will immediately
    *     close the connection.
    *
    *
    * @param personality a previously created personality for this connection.
    * @param key a string given by the client to identify itself.
    */
      public void accessControl( NetPersonality personality, String key ) {
            try{
                acceptClient( personality ); // we accept every client
            }
            catch(IOException e) {
                Debug.signal( Debug.WARNING, this, e );
            }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates a personality object for this new connection.
    *  Override this method if you don't want to use the TormPersonality.
    *
    * @return a new TormPersonality associated to this socket.
    */
      protected NetPersonality getNewDefaultPersonality( Socket socket ) throws IOException{
              return new TormPersonality( socket, null );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Sends a message to tell a client that he is welcome on this server.
    *  Should be called by initializeConnection() if you decide to accept the client.
    *
    * @param personality a previously created personality for this connection.
    */
      protected void acceptClient( NetPersonality personality ) throws IOException{
              personality.queueMessage( new ServerWelcomeMessage() );
              personality.pleaseSendAllMessagesNow();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Sends a message to tell a client that he is refused on this server.
    *  Should be called by initializeConnection() if you decide to refuse the client.
    *
    * @param personality a previously created personality for this connection.
    */
      protected void refuseClient( NetPersonality personality, String error_message )
      throws IOException{
              personality.queueMessage( new ServerErrorMessage( error_message ) );
              personality.pleaseSendAllMessagesNow();       
              personality.closeConnection();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  Server Thread Runtime.
    *  Never call this method it's done automatically.
    */
      public void run()
      {
        Socket client_socket;
        NetPersonality personality;

        // We wait for clients.
        // We catch exceptions at different levels because
        // their importance is linked to where they are generated.
           while( !shouldStopServer() )
           {
               client_socket = null;
               personality = null;

               try{
                  // we wait 5s for clients (InterruptedIOException after)
                     client_socket = server.accept();
               }
               catch( IOException e ){
                  // TimeOut exception
                     if( e instanceof InterruptedIOException )
                        continue;

                  // bad IOException generated...
                     Debug.signal( Debug.FAILURE, this, e );
                     System.exit(1);
               }

            // Ok, a client has arrived.
               try{                           
                  // We creates a personality object to take care of him...
                     personality = getNewDefaultPersonality( client_socket );
                     current_nb_clients++;

                  // we inspect our server state... do we really accept him ?
                     if( current_nb_clients>=max_clients ) {
                       // maximum clients reached...
                          refuseClient( personality, "maximum number of clients reached for the moment." );
                          Debug.signal(Debug.NOTICE,this,"maximum number of clients reached");
                     }
                     else if(server_lock) {
                       // we don't accept new connections for the moment
                          refuseClient( personality, "Server does not accept connections for the moment." );
                          Debug.signal(Debug.NOTICE,this,"Server Locked - just refused incoming connection");
                     }
                     else {
                       // we can start this personality and inspect the client connection.
                       // the context provided is an helper for the NetClientRegisterMessage
                       // behaviour.
                          personality.setContext( (Object) new NetServerEntry( this, personality ) );
                          personality.start();
                     }
               }
               catch(IOException ioe) {
                 // there was an error while dealing with this new client
                 // we continue as if nothing had happened
                    Debug.signal( Debug.ERROR, this, ioe );
               }
           }

       // We close the server connection
          try{
                server.close();
                Debug.signal( Debug.NOTICE, this, "Server Stopped" );
          }
          catch(IOException e) {
                Debug.signal( Debug.WARNING, this, e );
          }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** The number of connected clients... 
    *
    *  @return the current clients number 
    */
      public int getCurrentClientsNumber(){
             return current_nb_clients;
      }


 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set or unset the server lock.
    *
    *  @param lock server lock new value
    */
      public void setServerLock( boolean lock ) {
          server_lock = lock;
      }

  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To stop this server
    */
      synchronized public void stopServer(){
           stop_server = true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** should we stop ?
    *
    *  @return true if the server must stop.
    */

      synchronized private boolean shouldStopServer(){
           return stop_server;
      }  

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}





