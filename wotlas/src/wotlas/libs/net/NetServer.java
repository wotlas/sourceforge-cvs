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

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.BindException;
import java.io.IOException;
import java.io.InterruptedIOException;

import wotlas.utils.Debug;
import wotlas.utils.Tools;
import wotlas.libs.net.connection.TormConnection;
import wotlas.libs.net.message.ServerErrorMessage;
import wotlas.libs.net.message.ServerWelcomeMessage;

import wotlas.libs.net.message.ServerErrorMsgBehaviour;
import wotlas.libs.net.message.ServerWelcomeMsgBehaviour;


/** A NetServer awaits client connections. There are two types of Server :
 *<br>
 *    - server that have no predefined connection list. That means we
 *      don't know the clients that we are connected to (default).
 *<br>
 *    - server that maintains client accounts. To create this type of server
 *      extend this class and override the accessControl() method.
 *<br><p>
 *
 * This server uses a TormConnection as default. If you want to use another
 * one override the getNewConnection() method. Note also that when we create
 * our new connection we don't assign any "context" object. To assign one do it in
 * the initializeConnection() method with the "connection.setContext()" call.
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetConnection
 */


public class NetServer extends Thread implements NetErrorCodeList
{
 /*------------------------------------------------------------------------------------*/

  /** Server counter. We count how many servers we have on this JVM.
   *  It helps set the serverLocalID.
   */
     private static byte serverCounter;

 /*------------------------------------------------------------------------------------*/

  /** Server Socket
   */
     protected ServerSocket server;

  /** Stop server ?
   */
     private boolean stopServer;

  /** User lock to temporarily forbid new connections
   */
     private boolean serverLock;

  /** Maximum number of opened sockets for this server.
   */
     private int maxOpenedSockets;

  /** Server Local ID. Identifies this server on this JVM.
   */
     private byte serverLocalID;

  /** Server Port
   */
     private int serverPort;

  /** Server Host Name
   */
     private String host;

  /** An eventual error listener.
   */
     private NetServerErrorListener errorListener;

 /*------------------------------------------------------------------------------------*/

     /**  Constructs a NetServer but does not starts it. Call the start()
      *   method to start the server. You have to give the name of the packages
      *   where we'll be able to find the NetMessageBehaviour classes.<p>
      *
      *   By default we accept a maximum of 200 opened socket connections for
      *   this server.
      *
      *  @param serverPort port on which the server listens to clients.
      *  @param msgPackages a list of packages where we can find NetMsgBehaviour Classes.
      */
        public NetServer( int serverPort, String msgPackages[] )
        {
              super("Server");
              this.serverPort = serverPort;
              stopServer = false;
              serverLock = false;

           // server local ID
              serverLocalID = serverCounter;
              serverCounter++;

           // default maximum number of opened sockets
              maxOpenedSockets = 200;

           // we add the new message packages to the message factory
              int nb = NetMessageFactory.getMessageFactory().addMessagePackages( msgPackages );

              Debug.signal(Debug.NOTICE,null,"Loaded "+nb+" network message behaviours...");

           // ServerSocket inits
              server = getServerSocket();
        }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

     /**  Constructs a NetServer on the specified host, but does not starts it.
      *   Call the start() method to start the server. You have to give the name of
      *   the packages where we'll be able to find the NetMessageBehaviour classes.<p>
      *
      *   By default we accept a maximum of 200 opened socket connections for
      *   this server.
      *
      *  @param host the host interface to bind to. Example: wotlas.tower.org
      *  @param serverPort port on which the server listens to clients.
      *  @param msgPackages a list of packages where we can find NetMsgBehaviour Classes.
      */
        public NetServer( String host, int serverPort, String msgPackages[] )
        {
              super("Server");
              this.host = host;
              this.serverPort = serverPort;
              stopServer = false;
              serverLock = false;

           // server local ID
              serverLocalID = serverCounter;
              serverCounter++;

           // default maximum number of opened sockets
              maxOpenedSockets = 200;

           // we add the new message packages to the message factory
              int nb = NetMessageFactory.getMessageFactory().addMessagePackages( msgPackages );

              Debug.signal(Debug.NOTICE,null,"Loaded "+nb+" network message behaviours...");

              server = getServerSocket(); 
        }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get a valid ServerSocket. We use the host & serverPort to init the
    *  server socket. ANY ERROR at this level will cause a DIRECT EXIT.
    *
    *  @return server socket
    */
     public ServerSocket getServerSocket() {
           // We get the ip address of the specified host
              InetAddress hostIP = null;
              ServerSocket server = null;
           
               if(host!=null)
                  try{
                     hostIP = InetAddress.getByName( host );
                  }
                  catch(UnknownHostException ue) {
                     Debug.signal( Debug.FAILURE, this, ue );
                     Debug.exit();
                  }

           // ServerSocket inits
              try{
                  server = new ServerSocket(serverPort, 50, hostIP ); 
                  server.setSoTimeout(5000);
              }
              catch (IOException e){
                  Debug.signal( Debug.FAILURE, this, e );
                  Debug.exit();
              }
               
              return server;
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
    *  2) initialize the client session context ( connection.setContext() ). The context can be
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
    * @param connection a previously created connection for this connection.
    * @param key a string given by the client to identify itself.
    */
      public void accessControl( NetConnection connection, String key ) {
           // we accept every client
              acceptClient( connection );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates a connection object for this new connection.
    *  Override this method if you don't want to use the TormConnection.
    *
    * @return a new TormConnection associated to this socket.
    */
      protected NetConnection getNewConnection( Socket socket ) throws IOException{
              return new TormConnection( socket, null, serverLocalID );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Sends a message to tell a client that he is welcome on this server.
    *  Should be called by initializeConnection() if you decide to accept the client.
    *
    * @param connection a previously created connection for this connection.
    */
      protected void acceptClient( NetConnection connection ) {
              connection.queueMessage( new ServerWelcomeMessage() );
              connection.pleaseSendAllMessagesNow();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Sends a message to tell a client that he is refused on this server.
    *  Should be called by initializeConnection() if you decide to refuse the client.
    *
    * @param connection a previously created connection for this connection.
    * @param errorMessage error message to send
    */
      protected void refuseClient( NetConnection connection, short errorCode, String errorMessage ) {
              connection.queueMessage( new ServerErrorMessage( errorCode, errorMessage ) );
              connection.closeConnection();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  Server Thread Runtime.
    *  Never call this method it's done automatically.
    */
      public void run()
      {
        Socket client_socket;
        NetConnection connection;

        // We print some info about this server.
           Debug.signal( Debug.NOTICE, null, "Starting Server #local-"+serverLocalID
                         +" on IP:"+server.getInetAddress()+" Port:"+server.getLocalPort() );

        // We wait for clients.
        // We catch exceptions at different levels because
        // their importance is linked to where they are generated.
           while( !shouldStopServer() )
           {
               client_socket = null;
               connection = null;

               try{
                  // we wait 5s for clients (InterruptedIOException after)
                     client_socket = server.accept();
               }
               catch( IOException e ){
                  // TimeOut exception
                     if( e instanceof InterruptedIOException )
                        continue;

                  // bad IOException generated ?
                     if( e instanceof BindException ) {
                         Debug.signal( Debug.CRITICAL, this, e );

                      // We'll retry our connection in 3 minutes
                         Debug.signal(Debug.WARNING, null,"Server #"+serverLocalID+" will be restarted in 3m.");

                         if(errorListener!=null)
                            errorListener.errorOccured(e);

                         Tools.waitTime(1000*60*3); // we wait 3 minutes
                         Debug.signal(Debug.WARNING, null,"Attempt to restart server #"+serverLocalID+"...");
                         server = getServerSocket();
                         continue;
                     }
                     else {
                         Debug.signal( Debug.FAILURE, this, e );
                         Debug.exit();
                     }
               }

            // Ok, a client has arrived.
               try{                           
                  // We creates a connection object to take care of him...
                     connection = getNewConnection( client_socket );

                  // we inspect our server state... can we really accept him ?
                     if( NetThread.getOpenedSocketNumber( serverLocalID ) >= maxOpenedSockets ) {
                       // we have reached the server's connections limit
                          refuseClient( connection, ERR_MAX_CONN_REACHED, "Server has reached its maximum number of connections for the moment." );
                          Debug.signal(Debug.NOTICE,this,"Err:"+ERR_MAX_CONN_REACHED+" - Server has reached its max number of connections");
                     }
                     else if(serverLock) {
                       // we don't accept new connections for the moment
                          refuseClient( connection, ERR_ACCESS_LOCKED, "Server does not accept connections for the moment." );
                          Debug.signal(Debug.NOTICE,this,"Err:"+ERR_ACCESS_LOCKED+" - Server Locked - just refused incoming connection");
                     }
                     else {
                       // we can start this connection and inspect the client connection.
                       // the context provided is an helper for the NetClientRegisterMessage
                       // behaviour.
                          connection.setContext( (Object) new NetServerEntry( this, connection ) );
                          connection.start();
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
                Debug.signal( Debug.NOTICE, this, "Server Stopped ("+serverLocalID+")." );
          }
          catch(IOException e) {
                Debug.signal( Debug.WARNING, this, e );
          }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set or unset the server lock.
    *
    *  @param lock server lock new value
    */
      public void setServerLock( boolean lock ) {
          serverLock = lock;
      }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To stop this server
    */
      synchronized public void stopServer(){
           stopServer = true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** should we stop ?
    *
    *  @return true if the server must stop.
    */

      synchronized private boolean shouldStopServer(){
           return stopServer;
      }  

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To change the maximum number of opened sockets.
    *
    *  @param maxOpenedSockets maximum number of opened sockets
    */
      protected void setMaximumOpenedSockets( int maxOpenedSockets ) {
          this.maxOpenedSockets = maxOpenedSockets;
      }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set an ErrorListener for this server.
    *  @param nsl listener
    */
      public void setErrorListener( NetServerErrorListener nsl ) {
      	 errorListener = nsl;
      }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}





