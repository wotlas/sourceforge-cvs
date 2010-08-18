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

import java.io.IOException;
import java.io.InterruptedIOException;
import wotlas.libs.net.connection.AsynchronousNetConnection;
import wotlas.libs.net.message.ServerErrorMessage;
import wotlas.libs.net.message.ServerWelcomeMessage;
import wotlas.utils.Debug;
import wotlas.utils.Tools;
import wotlas.utils.WotlasGameDefinition;

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

    /** the configuration of the server (name, port, ...);
     */
    private NetConfig netCfg;

    /** Our listeners (objects that will be informed of our state ).
     */
    private NetServerListener listeners[];

    /** Our connections with clients
     */
    private NetConnection connections[];

    /** User lock to temporarily forbid new connections
     */
    private boolean serverLock;

    /**
     * A dependent factory of the current game definition
     */
    private NetMessageFactory msgFactory;

    protected IOServerChannel server;

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
     *  @param netCfg the configuration of the server (name, port, ...);
     *  @param msgSubInterfaces a list of sub-interfaces where we can find NetMsgBehaviour Classes implemeting them.
     */
    public NetServer(NetConfig netCfg, Class msgSubInterfaces[], WotlasGameDefinition wgd) {
        super("Server");
        this.msgFactory = new NetMessageFactory(wgd);
        this.netCfg = netCfg;
        this.serverLock = false;
        this.listeners = new NetServerListener[0];
        this.connections = new NetConnection[10];

        // we add the new message packages to the message factory
        int nb = this.msgFactory.addMessagePackages(msgSubInterfaces);
        Debug.signal(Debug.NOTICE, null, "Loaded " + nb + " network message behaviours...");
    }

    /**
     * @return
     * @see wotlas.libs.net.NetMessageFactory#getGameDefinition()
     */
    public WotlasGameDefinition getGameDefinition() {
        return this.msgFactory.getGameDefinition();
    }

    /**
     * We load the available socket factory managing the connection to or for the server.
     * @param netCfg the configuration of the server (name, port, ...);
     */
    protected final IOChannelFactory getSocketFactory(NetConfig netCfg) {
        /** We load the available plug-ins (we search everywhere).
         */
        Object[] factories = null;
        try {
            // We get an instance of the factory
            factories = Tools.getImplementorsOf(IOChannelFactory.class, this.msgFactory.getGameDefinition());

        } catch (ClassNotFoundException e) {
            Debug.signal(Debug.CRITICAL, this, e);
            return null;
        } catch (SecurityException e) {
            Debug.signal(Debug.CRITICAL, this, e);
            return null;
        } catch (RuntimeException e) {
            Debug.signal(Debug.ERROR, this, e);
            return null;
        }

        if (factories == null || factories.length == 0 || !(factories[0] instanceof IOChannelFactory)) {
            Debug.signal(Debug.ERROR, this, "No socket factory available in services");
            return null;
        }
        for (int i = 0; i < factories.length; i++) {
            IOChannelFactory fact = (IOChannelFactory) factories[i];
            if (fact.isManaging(netCfg)) {
                return fact;
            }
        }
        Debug.signal(Debug.ERROR, this, "No socket factory available in services : using the first as default");
        return (IOChannelFactory) factories[0];
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a valid ServerSocket. If the interface is not ready we will display a warning
     *  message and wait INTERFACE_BIND_PERIOD milliseconds before retrying.
     *  @return a valid server socket
     */
    private IOServerChannel createNewServerSocket() {

        IOChannelFactory factory = getSocketFactory(this.netCfg);
        if (factory == null) {
            Debug.signal(Debug.FAILURE, this, "Could not create server io socket : factory is null !");
            Debug.exit();
        }

        try {
            if (this.server == null || this.server.mustStop()) {
                IOServerChannel ioServer = factory.createNewServerSocket(this.netCfg, this.listeners);
                this.server = ioServer;
            }

            if (this.server != null) {
                this.server.setMaximumOpenedSockets(this.connections.length);
            }

        } catch (IOException ioe) {
            Debug.signal(Debug.FAILURE, this, ioe);
            Debug.exit();
        }

        // Interface is not ready
        if (this.server == null) {
            Debug.signal(Debug.FAILURE, this, "Could not create server io socket !");
            Debug.exit();
        }

        return this.server;
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
    public void accessControl(NetConnection connection, String key) {
        // we accept every client
        acceptClient(connection);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Creates a connection object for this new connection.
     *  Override this method if you don't want to use the AsynchronousNetConnection.
     *
     * @return a new AsynchronousNetConnection associated to this socket.
     */
    protected NetConnection getNewConnection(IOChannel socket) throws IOException {
        if (socket == null)
            return null; // Not a real connection.
        return new AsynchronousNetConnection(this.msgFactory, socket);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Sends a message to tell a client that he is welcome on this server.
     *  Should be called by initializeConnection() if you decide to accept the client.
     *
     * @param connection a previously created connection for this connection.
     */
    protected void acceptClient(NetConnection connection) {
        connection.queueMessage(new ServerWelcomeMessage());
        connection.sendAllMessages();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Sends a message to tell a client that he is refused on this server.
     *  Should be called by initializeConnection() if you decide to refuse the client.
     *
     * @param connection a previously created connection for this connection.
     * @param errorMessage error message to send
     */
    protected void refuseClient(NetConnection connection, short errorCode, String errorMessage) {
        connection.queueMessage(new ServerErrorMessage(errorCode, errorMessage));
        connection.close();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Server Thread Runtime.
     *  Never call this method it's done automatically.
     */
    @Override
    public void run() {
        IOChannel clientSocket; // current socket
        NetConnection connection; // current connection object wrapping up the socket
        boolean failureOccured = false; // if a failure already occured during the accept()

        // We retrieve a valid server socket. This method can lock to wait for the itf to get ready.
        this.server = createNewServerSocket();

        // We print some info about this server.
        Debug.signal(Debug.NOTICE, null, "Starting Server on " + this.server.getDescription());

        // We wait for client connections. We catch exceptions at different levels because
        // their importance is linked to where they are thrown.
        while (this.server != null && !this.server.mustStop()) {
            clientSocket = null;
            connection = null;

            try {
                // we wait 5s for clients (InterruptedIOException after)
                clientSocket = this.server.accept();

            } catch (InterruptedIOException iioe) {
                // This is the normal behavior : the SOTimeout was fired
                failureOccured = false;
                if (!this.server.mustStop()) {
                    this.server = createNewServerSocket(); // we update our server socket if needed
                }
                continue;

            } catch (Exception e) {
                Debug.signal(Debug.FAILURE, this, e);

                if (failureOccured)
                    Debug.exit(); // this was our second try, we quit...
                else {
                    failureOccured = true;
                    continue; // retry... one more time only
                }
            }

            // Ok, a client has arrived.
            try {
                // We creates a connection object to take care of him...
                connection = getNewConnection(clientSocket);

                // we inspect our server state... can we really accept him ?
                if (connection != null && !registerConnection(connection)) {
                    // we have reached the server's connections limit
                    refuseClient(connection, NetErrorCodeList.ERR_MAX_CONN_REACHED, "Server has reached its maximum number of connections for the moment.");
                    Debug.signal(Debug.NOTICE, this, "Err:" + NetErrorCodeList.ERR_MAX_CONN_REACHED + " - Server has reached its max number of connections");
                } else if (connection != null && this.serverLock) {
                    // we don't accept new connections for the moment
                    refuseClient(connection, NetErrorCodeList.ERR_ACCESS_LOCKED, "Server does not accept connections for the moment.");
                    Debug.signal(Debug.NOTICE, this, "Err:" + NetErrorCodeList.ERR_ACCESS_LOCKED + " - Server Locked - just refused incoming connection");
                } else if (connection != null) {
                    // we can start this connection and inspect the client connection.
                    // the context provided is an helper for the NetClientRegisterMessage
                    // behaviour.
                    connection.setContext(new NetServerEntry(this, connection));
                    connection.start();
                }
            } catch (IOException ioe) {
                // there was an error while dealing with this new client
                // we continue as if nothing had happened
                Debug.signal(Debug.ERROR, this, ioe);
            }
        }

        // We close the server connection
        try {
            this.server.close();
            Debug.signal(Debug.NOTICE, this, "Server Stopped.");
        } catch (IOException e) {
            Debug.signal(Debug.WARNING, this, e);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set or unset the server lock.
     *  @param lock server lock new value
     */
    public void setServerLock(boolean lock) {
        this.serverLock = lock;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To add a NetServerListener on this connection.
     *  The listener will receive information on the life on our network interface.
     * @param listener an object implementing the NetServerListener interface.
     */
    public void addServerListener(NetServerListener listener) {

        NetServerListener tmp[] = new NetServerListener[this.listeners.length + 1];

        for (int i = 0; i < this.listeners.length; i++)
            tmp[i] = this.listeners[i];

        tmp[tmp.length - 1] = listener;
        this.listeners = tmp;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To remove the specified server listener.
     * @param listener listener to remove.
     * @return true if the listener was removed, false if it was nor found
     */
    public boolean removeServerListener(NetServerListener listener) {

        // does the listener exists ?
        boolean found = false;

        for (int i = 0; i < this.listeners.length; i++)
            if (this.listeners[i] == listener) {
                found = true;
                break;
            }

        if (!found)
            return false; // not found

        // We remove the connection listener
        NetServerListener tmp[] = new NetServerListener[this.listeners.length - 1];
        int nb = 0;

        for (int i = 0; i < this.listeners.length; i++)
            if (this.listeners[i] != listener) {
                tmp[nb] = this.listeners[i];
                nb++;
            }

        this.listeners = tmp;
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called when a new network connection is created for you.
     * @param connection the NetConnection object associated to this connection.
     */
    public synchronized void connectionCreated(NetConnection connection) {
        // we add this connection to our list
        for (int i = 0; i < this.connections.length; i++)
            if (this.connections[i] == null) {
                this.connections[i] = connection;
                return;
            }

        // ERROR !!! should never happen
        Debug.signal(Debug.ERROR, this, "Failed to find room for established connection !! closing it !");
        connection.close();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called when the network connection is no longer of this world.
     * @param connection the NetConnection object associated to this connection.
     */
    public synchronized void connectionClosed(NetConnection connection) {
        // we remove this connection from our list
        for (int i = 0; i < this.connections.length; i++)
            if (this.connections[i] == connection) {
                this.connections[i] = null;
                return;
            }

        // ERROR !!! should never happen !
        Debug.signal(Debug.ERROR, this, "Failed to find connection in our list ! an error must have occured...");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Tries to register a new connection in our list.
     * @param connection the NetConnection object associated to this connection.
     */
    public synchronized boolean registerConnection(NetConnection connection) {
        if (connection == null)
            return false; // not a real connection.

        // we try to find some room for the new connection
        for (int i = 0; i < this.connections.length; i++)
            if (this.connections[i] == null) {
                connection.addConnectionListener(this); // this way the registration will not
                return true; // occur if the connection has been closed roughly
            }

        return false; // no room, sorry !
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Closes all the connections on this server.
     */
    public synchronized void closeConnections() {
        for (int i = 0; i < this.connections.length; i++)
            if (this.connections[i] != null) {
                // We do not need to listen to the closed connection (avoid deadlock with an other thread closing the current connection.
                NetConnection connection = this.connections[i];
                this.connections[i] = null;
                connection.removeConnectionListener(this);
                connection.close();
            }
    }

    /** To stop this server
     */
    public void stopServer() {
        if (this.server != null) {
            this.server.stopServer();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To change the maximum number of opened sockets. This can be refused if more than
     *  the new 'maxOpenedSockets' are already opened.
     *  @param maxOpenedSockets maximum number of opened sockets
     */
    protected synchronized void setMaximumOpenedSockets(int maxOpenedSockets) {

        // 1 - can we accept this request ?
        int nb = 0;

        for (int i = 0; i < this.connections.length; i++)
            if (this.connections[i] != null)
                nb++;

        if (nb > maxOpenedSockets) {
            Debug.signal(Debug.ERROR, this, "setMaximumOpenedSockets() Request refused : more sockets are already opened !");
            return;
        }

        // 2 - ok, request accepted
        if (this.server != null) {
            this.server.setMaximumOpenedSockets(maxOpenedSockets);
        }
        NetConnection tmp[] = new NetConnection[maxOpenedSockets];
        nb = 0;

        for (int i = 0; i < this.connections.length; i++)
            if (this.connections[i] != null) {
                tmp[nb] = this.connections[i];
                nb++;
            }

        this.connections = tmp; // swap
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Sends a NetMessage to all the connections on this server.
     * @param msg a net message to send to all clients.
     */
    public synchronized void sendMessageToOpenedConnections(NetMessage msg) {
        for (int i = 0; i < this.connections.length; i++)
            if (this.connections[i] != null)
                this.connections[i].queueMessage(msg);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
