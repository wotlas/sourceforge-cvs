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
import java.util.HashMap;

import wotlas.utils.Debug;
import wotlas.libs.net.personality.TormPersonality;
import wotlas.libs.net.message.ServerErrorMessage;


/** A NetServer awaits client connections. There are two types of Server :
 *
 *    - server that have no predefined connection list. That means we
 *      don't know the clients that we are connected to. We erase their
 *      NetServerEntry as they quit.
 *
 *    - server that maintains client accounts.
 *
 *
 * This server uses a TormPersonality as default. If you want to use another
 * one override the getNewDefaultPersonality() method. Note also that when we create
 * our new personality we don't assign any "context" object. To assign one do it in
 * the initializeConnection() method with the "personality.setContext()" call.
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetPersonality
 * @see wotlas.libs.net.NetServerEntry
 */


abstract public class NetServer extends Thread
{
 /*------------------------------------------------------------------------------------*/

  /** Server Socket
   */
     private ServerSocket server;

  /** NetServerEntries representing network connections.
   *  The HashMap entries are NetServerEntry objects.
   *  The key is a string provided by the client when he connects.
   */
     private HashMap connection_list;

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

     /**  Constructs a ServiceServer and starts it.
      *
      *  @param server_port port on which the server listens to clients.
      *  @param max_clients maximum number of clients to accept at the same time.
      */
        public NetServer( int server_port, int max_clients )
        {
              super("Server");
              stop_server = false;
              server_lock = false;
              this.max_clients = max_clients;

           // we create a message factory
              NetMessageFactory.createMessageFactory();

           // we construct or initialize our connection list
              createConnectionList();

           // ServerSocket inits
              try{
                  server = new ServerSocket(server_port); 
                  server.setSoTimeout(5000);
               }
               catch (IOException e){
                  Debug.signal( Debug.FAILURE, this, e );
                  System.exit(1);
               }

           start();
        }


 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Redefine this method to :
    *
    *  
    */
      abstract protected void createConnectionList();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Redefine this method to :
    *
    *  
    */
      abstract protected void initializeConnection( NetPersonality personality );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Redefine this method to :
    *
    *  
    */
      abstract protected void closeConnection();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates a personality object for this new connection.
    *  Override this method if you don't want to use the TormPersonality.
    *
    * @return a new TormPersonality associated to this socket.
    */
      protected NetPersonality getNewDefaultPersonality( Socket socket ) throws IOException{
              return new TormPersonality( socket );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  Starts the server.
    *   Never call this method it's done automatically.
    */
      public void start()
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
                     if( current_nb_clients>=max_clients )
                     {
                       // maximum clients reached...
                          personality.queueMessage( new ServerErrorMessage("maximum number of clients reached for the moment.") );
                          personality.pleaseSendAllMessagesNow();
                          personality.closeConnection();

                          Debug.signal(Debug.NOTICE,this,"maximum number of clients reached");
                     }
                     else if(server_lock)
                     {
                       // we don't accept new connections for the moment
                          personality.queueMessage( new ServerErrorMessage("Server does not accept connections for the moment.") );
                          personality.pleaseSendAllMessagesNow();
                          personality.closeConnection();
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





