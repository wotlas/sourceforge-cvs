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
import wotlas.libs.net.utils.NetInterface;
import wotlas.libs.net.connection.AsynchronousNetConnection;
import wotlas.libs.net.message.ServerErrorMessage;
import wotlas.libs.net.message.ServerWelcomeMessage;
import wotlas.libs.net.message.ServerErrorMsgBehaviour;
import wotlas.libs.net.message.ServerWelcomeMsgBehaviour;


/** A NetServer awaits client connections. There are many types of Server depending on what
 *  you want to do :
 *<br>
 *    - servers that have no predefined user connection list, i.e. we
 *      don't know the clients that are going to connect (this is our case here).
 *<br>
 *    - server that maintains client accounts. To create this type of server
 *      extend this class and override the accessControl() method.
 *<br>
 *<p>
 * This server creates and uses AsynchronousNetConnection. If you want to use another
 * connection type, override the getNewConnection() method. Note also that when we create
 * our new connection we don't assign any "context" object. To assign one do it in
 * the accessControl() method with the "connection.setContext()" call.
 *</p>
 * @author Aldiss
 * @see wotlas.libs.net.NetConnection
 */

public class NetServer extends Thread implements NetConnectionListener, NetErrorCodeList {

 /*------------------------------------------------------------------------------------*/

   /** Period between two bind() tries if the network interface is not ready.
    */
     public static final long INTERFACE_BIND_PERIOD = 1000*60*3; // 3 min

   /** To prevent servers from accessing to netwok info at the same time
    */
     private static final byte systemNetLock[] = new byte[0];

 /*------------------------------------------------------------------------------------*/

   /** Server Socket
    */
     protected ServerSocket server;

   /** Server Net Interface. We accept three format : an IP address, a DNS name or a network interface
    *  name followed by a ',' with an integer indicating the IP index for that network interface.
    *  If you are not sure just set the index to 0, it will point out the first available IP for
    *  the given interface.<br>
    *  Example : "wotlas.dynds.org", "192.168.0.2", "lan1,0".
    */
     protected String serverInterface;

   /** Server Port.
    */
     protected int serverPort;

   /** Maximum number of opened sockets for this server.
    */
     private int maxOpenedSockets;

   /** Our listeners (objects that will be informed of our state ).
    */
     private NetServerListener listeners[];

   /** Our connections with clients
    */
     private NetConnection connections[];

   /** Stop server ?
    */
     protected boolean stopServer;

   /** User lock to temporarily forbid new connections
    */
     private boolean serverLock;

 /*------------------------------------------------------------------------------------*/

     /** Constructs a NetServer on the specified host/port, but does not starts it.
      *  Call the start() method to start the server. You have to give the name of
      *  the packages where we'll be able to find the NetMessageBehaviour classes to use.<p>
      *
      *  <p>Server Host Name. We accept three format : an IP address, a DNS name or a network interface
      *  name followed by a ',' with an integer indicating the IP index for that network interface.
      *  If you are not sure just set the index to 0, it will point out the first available IP for
      *  the given interface.<br>
      *
      *  Example : "wotlas.dynds.org", "192.168.0.2", "lan1,0".</p>
      *
      *  <p>By default we accept a maximum of 200 opened socket connections for
      *  this server. This number can be changed with setMaximumOpenedSockets().</p>
      *
      *  @param host the interface to monitor and to bind to.
      *  @param serverPort port on which the server listens to clients.
      *  @param msgPackages a list of packages where we can find NetMsgBehaviour Classes.
      */
        public NetServer( String serverInterface, int serverPort, String msgPackages[] ) {
              super("Server");
              this.serverInterface = serverInterface;
              this.serverPort = serverPort;
              stopServer = false;
              serverLock = false;
              maxOpenedSockets = 200;  // default maximum number of opened sockets

              listeners = new NetServerListener[0];
              connections = new NetConnection[maxOpenedSockets];

           // we add the new message packages to the message factory
              int nb = NetMessageFactory.getMessageFactory().addMessagePackages( msgPackages );
              Debug.signal(Debug.NOTICE,null,"Loaded "+nb+" network message behaviours...");
        }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get a valid ServerSocket. If the interface is not ready we will display a warning
    *  message and wait INTERFACE_BIND_PERIOD milliseconds before retrying.
    *  @return a valid server socket
    */
      private ServerSocket getServerSocket() {
           // We get the ip address of the specified host
              InetAddress hostIP = null;
              boolean updateServerSocket = false;

           // We check the format of the "host" field
              if( serverInterface.indexOf(',')<0 ) {
                 // ok this is an IP or DNS Name
                    do{
                       try{
                           hostIP = InetAddress.getByName( serverInterface );
                       }
                       catch(UnknownHostException ue) {
                         // Interface is not ready
                           for( int i=0; i<listeners.length; i++ )
                                listeners[i].serverInterfaceIsDown( serverInterface );

                         // we wait some time before retrying...
                           synchronized( this ) {
                              try{
                                wait( INTERFACE_BIND_PERIOD );
                              }catch( Exception e ) {}
                           }
                       }

                       if(mustStop())
                          return server;
                    }
                    while( hostIP ==null );
              }
              else {
                synchronized( systemNetLock ){     // <-- to prevent servers from accessing net info at the same time
                 // ok, we have an interface name
                    int separatorIndex = serverInterface.indexOf(',');
                    int ipIndex = -1;

                    try{
                         ipIndex = Integer.parseInt( serverInterface.substring( separatorIndex+1, serverInterface.length() ) );
                    }catch(Exception e) {
                        Debug.signal(Debug.FAILURE,this,"Invalid network interface format : "+serverInterface+" should be <itf>,<ip-index> !");
                        Debug.exit();
                    }

                 // We wait for the interface to be up
                    do{
                       String itfIP[] = NetInterface.getInterfaceAddresses( serverInterface.substring(0,separatorIndex) );
                       
                       if(itfIP.length<=ipIndex) {
                           // Interface is not ready
                              if(server==null) {
                                 Debug.signal(Debug.FAILURE,this,"Network Interface MUST be enabled at start-up so that we load appropriate libraries !");
                                 Debug.exit();
                              }

                              for( int i=0; i<listeners.length; i++ )
                                   listeners[i].serverInterfaceIsDown( serverInterface.substring(0,separatorIndex)+" - ip "+ipIndex );

                           // we wait some time before retrying...
                              synchronized( this ) {
                                 try{
                                    wait( INTERFACE_BIND_PERIOD );
                                 }catch( Exception e ) {}
                              }
                       }
                       else {
                          // Interface is up !
                            try{
                               hostIP = InetAddress.getByName( itfIP[ipIndex] );
                            }catch( UnknownHostException uhe ) {
                               Debug.signal( Debug.FAILURE, this, "Could not use IP given by NetworkInterface ! "+uhe ); // FATAL !
                               Debug.exit();
                            }
                            
                            if(server!=null && !server.getInetAddress().equals(hostIP) )
                               updateServerSocket = true;
                       }

                       if(mustStop())
                          return server;
                    }
                    while( hostIP ==null );
                }
              }

           // 2 - Has the state of the network interface changed ?
              if( !updateServerSocket && server!=null ) {
                  for( int i=0; i<listeners.length; i++ )
                       listeners[i].serverInterfaceIsUp( hostIP.getHostAddress(), false ); // state not changed

                  return server;
              }

           // 3 - ServerSocket creation is needed here.
              if(server!=null)
                 try{
                     server.close();
                 }catch( IOException ioe ) {
                     Debug.signal(Debug.ERROR,this,"Error while closing old server socket : "+ioe);
                 }

              try{
                  server = new ServerSocket(serverPort, 50, hostIP );  // new server socket
                  server.setSoTimeout(5000);
              }
              catch (Exception e){
                  Debug.signal( Debug.FAILURE, this, "Could not create server socket ! "+e ); // FATAL !
                  Debug.exit();
              }

              for( int i=0; i<listeners.length; i++ )
                   listeners[i].serverInterfaceIsUp( hostIP.getHostAddress(), true ); // state changed
               
              return server;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** This method is called automatically when a new client establishes a connection
    *  with this server ( the client sends a ClientRegisterMessage ). We are supposed
    *  to provide here some basic access control.<br>
    *
    *  The default implementation here contains the STRICT MINIMUM : we accept every
    *  client connection without considering the content of the key they provide.<br>
    *
    *  You can redefine this method (recommended) to :
    *<p>
    *  1) consider if the key ( key parameter ) provided by the client is correct for your
    *     application. For example for a chat Server a key could be a chat channel name. In
    *     the case of a repository server, a key could be "login:password".
    *</p><p>
    *  2) initialize the client session context ( connection.setContext() ). The context can be
    *     any type of object and should be client dependent. It will be given to the messages
    *     coming from the client. For example, in a ChatServer the context could be the Chat
    *     chanel object the client wants to register to. This way message behaviours would have
    *     a direct access to their right chat channel.
    *</p><p>
    *  3) MANDATORY : if you decide to accept this client, call the acceptClient() method.
    *     it will validate the client connection. If you decide to refuse the client, call
    *     the refuseClient() method with an appropriate error message. It will immediately
    *     close the connection.
    *</p>
    * @param connection a previously created connection for this connection.
    * @param key a string given by the client to identify itself.
    */
      public void accessControl( NetConnection connection, String key ) {
           // we accept every client
              acceptClient( connection );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates a connection object for this new connection.
    *  Override this method if you don't want to use the AsynchronousNetConnection.
    *
    * @return a new AsynchronousNetConnection associated to this socket.
    */
      protected NetConnection getNewConnection( Socket socket ) throws IOException{
          return new AsynchronousNetConnection( socket );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Sends a message to tell a client that he is welcome on this server.
    *  Should be called by initializeConnection() if you decide to accept the client.
    *
    * @param connection a previously created connection for this connection.
    */
      protected void acceptClient( NetConnection connection ) {
              connection.queueMessage( new ServerWelcomeMessage() );
              connection.sendAllMessages();
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
              connection.close();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Server Thread Runtime.
    *  Never call this method it's done automatically.
    */
      public void run() {
           Socket clientSocket;            // current socket
           NetConnection connection;       // current connection object wrapping up the socket
           boolean failureOccured = false; // if a failure already occured during the accept()

        // We retrieve a valid server socket. This method can lock to wait for the itf to get ready.
           server = getServerSocket();

        // We print some info about this server.
           Debug.signal( Debug.NOTICE, null, "Starting Server on "+server.getInetAddress()+":"+server.getLocalPort() );

        // We wait for client connections. We catch exceptions at different levels because
        // their importance is linked to where they are thrown.
           while( !mustStop() ) {
               clientSocket = null;
               connection = null;

               try{
                  // we wait 5s for clients (InterruptedIOException after)
                     clientSocket = server.accept();
               }
               catch(InterruptedIOException iioe) {
                  // This is the normal behaviour : the SOTimeout was fired
                     failureOccured = false;
                     server = getServerSocket(); // we update our server socket if needed
                     continue;
               }
               catch( Exception e ){
                     Debug.signal( Debug.FAILURE, this, e );

                     if(failureOccured)
                        Debug.exit(); // this was our second try, we quit...
                     else {
                        failureOccured = true;
                        continue;  // retry... one more time only
                     }
               }

            // Ok, a client has arrived.
               try{                           
                  // We creates a connection object to take care of him...
                     connection = getNewConnection( clientSocket );

                  // we inspect our server state... can we really accept him ?
                     if( !registerConnection( connection ) ) {
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
                Debug.signal( Debug.NOTICE, this, "Server Stopped." );
           }
           catch(IOException e) {
                Debug.signal( Debug.WARNING, this, e );
           }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set or unset the server lock.
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
           notifyAll();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** should we stop ?
    *  @return true if the server must stop.
    */
      synchronized private boolean mustStop(){
           return stopServer;
      }  

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To change the maximum number of opened sockets. This can be refused if more than
    *  the new 'maxOpenedSockets' are already opened.
    *  @param maxOpenedSockets maximum number of opened sockets
    */
      protected synchronized void setMaximumOpenedSockets( int maxOpenedSockets ) {

          // 1 - can we accept this request ?
             int nb=0;
             
             for(int i=0; i<connections.length; i++)
                 if(connections[i]!=null)
                    nb++;

             if( nb>maxOpenedSockets ) {
                 Debug.signal(Debug.ERROR,this,"setMaximumOpenedSockets() Request refused : more sockets are already opened !");
                 return;
             }

          // 2 - ok, request accepted
             this.maxOpenedSockets = maxOpenedSockets;
             NetConnection tmp[] = new NetConnection[maxOpenedSockets];
             nb=0;

             for(int i=0; i<connections.length; i++)
                 if(connections[i]!=null) {
                    tmp[nb] = connections[i];
                    nb++;
                 }

             connections = tmp; // swap
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To add a NetServerListener on this connection.
    *  The listener will receive information on the life on our network interface.
    * @param listener an object implementing the NetServerListener interface.
    */
     public void addServerListener( NetServerListener listener ) {

         NetServerListener tmp[] = new NetServerListener[listeners.length+1];

         for( int i=0; i<listeners.length; i++ )
              tmp[i] = listeners[i];

         tmp[tmp.length-1]= listener;
         listeners = tmp;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To remove the specified server listener.
    * @param listener listener to remove.
    * @return true if the listener was removed, false if it was nor found
    */
     public boolean removeServerListener( NetServerListener listener ) {

       // does the listener exists ?
          boolean found = false;

          for( int i=0; i<listeners.length; i++ )
               if( listeners[i]==listener ) {
                   found = true;
                   break;
               }

          if( !found )
             return false;  // not found

       // We remove the connection listener
          NetServerListener tmp[] = new NetServerListener[listeners.length-1];
          int nb=0;

          for( int i=0; i<listeners.length; i++ )
               if( listeners[i]!=listener ) {
                   tmp[nb]=listeners[i];
                   nb++;
               }

          listeners = tmp;
          return true;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when a new network connection is created for you.
   * @param connection the NetConnection object associated to this connection.
   */
     public synchronized void connectionCreated( NetConnection connection ) {
        // we add this connection to our list
           for( int i=0; i<connections.length; i++ )
                if(connections[i]==null) {
                   connections[i] = connection;
                   return;
                }

        // ERROR !!! should never happen
           Debug.signal(Debug.ERROR,this,"Failed to find room for established connection !! closing it !");
           connection.close();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when the network connection is no longer of this world.
   * @param connection the NetConnection object associated to this connection.
   */
     public synchronized void connectionClosed( NetConnection connection ) {
        // we remove this connection from our list
           for( int i=0; i<connections.length; i++ )
                if(connections[i]==connection) {
                   connections[i] = null;
                   return;
                }

        // ERROR !!! should never happen !
           Debug.signal(Debug.ERROR,this,"Failed to find connection in our list ! an error must have occured...");
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tries to register a new connection in our list.
   * @param connection the NetConnection object associated to this connection.
   */
     public synchronized boolean registerConnection( NetConnection connection ) {
        // we try to find some room for the new connection
           for( int i=0; i<connections.length; i++ )
                if(connections[i]==null) {
                   connection.addConnectionListener( this ); // this way the registration will not
                   return true;                              // occur if the connection has been closed roughly
                }

           return false; // no room, sorry !
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Closes all the connections on this server.
   */
     public synchronized void closeConnections() {
           for( int i=0; i<connections.length; i++ )
                if(connections[i]!=null)
                   connections[i].close();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Sends a NetMessage to all the connections on this server.
   * @param msg a net message to send to all clients.
   */
     public synchronized void sendMessageToOpenedConnections( NetMessage msg ) {
           for( int i=0; i<connections.length; i++ )
                if(connections[i]!=null)
                   connections[i].queueMessage( msg );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
