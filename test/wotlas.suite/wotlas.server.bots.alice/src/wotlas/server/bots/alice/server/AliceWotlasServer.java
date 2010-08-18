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
package wotlas.server.bots.alice.server;

import java.util.Hashtable;
import java.util.Iterator;
import wotlas.common.ErrorCodeList;
import wotlas.libs.net.NetConfig;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.net.NetServer;
import wotlas.server.bots.alice.AliceWotlasMessage;
import wotlas.utils.Debug;

/** Alice Wotlas Server. Its role is to wait alice message request from remote
 *  wotlas servers and transfer them to the the AliceWOTLAS listener.
 *
 * @author Aldiss
 */
public class AliceWotlasServer extends NetServer implements NetConnectionListener, ErrorCodeList {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Our WOTLAS alice chat listener. We use this class to handle our alicebot answer
     *  requests.
     */
    protected AliceWotlasListener aliceWotlas;
    /** List of the remote wotlas servers we are connected to. The key of the hashtable
     *  is the server's id.
     */
    protected Hashtable<String, NetConnection> serverLinks;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Constructor (see wotlas.libs.net.NetServer for details)
     *
     *  @param netCfg (the host interface to bind to. Example: wotlas.tower.org; and the port on which the server listens to clients).
     *  @param msgSubInterfaces a list of sub-interfaces where we can find NetMsgBehaviour Classes implemeting them.
     *  @param nbMaxSockets maximum number of sockets that can be opened on this server
     *  @param aliceWotlas wotlas alice chat listener
     */
    public AliceWotlasServer(NetConfig netCfg, Class msgSubInterfaces[], int nbMaxSockets, AliceWotlasListener aliceWotlas) {
        super(netCfg, msgSubInterfaces, aliceWotlas.getGameDefinition());
        this.aliceWotlas = aliceWotlas;
        this.serverLinks = new Hashtable<String, NetConnection>(10);

        setMaximumOpenedSockets(nbMaxSockets);
        Debug.displayExceptionStack(false);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** This method is called automatically when a new client establishes a connection
     *  with this server ( the client sends a ClientRegisterMessage ).
     *
     * @param connection a previously created connection for this connection.
     * @param key a string given by the client to identify itself. The key structure
     *        is the following "accountName:password". See wotlas.server.GameAccount
     *        for the accountName structure.
     */
    @Override
    public void accessControl(NetConnection connection, String key) {

        // 1 - We check the key
        if (!key.startsWith("alicebot-access:") || key.endsWith(":")) {
            Debug.signal(Debug.NOTICE, this, "A client tried to connect with a bad key format : " + key);
            refuseClient(connection, ErrorCodeList.ERR_WRONG_KEY, "You are trying to connect with a bad key !!!");
            return;
        }

        // 2 - We extract the server ID and add its NetConnection to our list
        try {
            int serverID = Integer.parseInt(key.substring(key.indexOf(':') + 1));
            this.serverLinks.put("" + serverID, connection);
            Debug.signal(Debug.NOTICE, null, "AliceWotlasServer connection created for server " + serverID);
        } catch (Exception e) {
            Debug.signal(Debug.NOTICE, this, "A client tried to connect with a bad key : " + key);
            refuseClient(connection, ErrorCodeList.ERR_WRONG_KEY, "The key you sent is not correct !!!");
            return;
        }

        // 3 - we set the message context to our alice chat listener...
        connection.setContext(this.aliceWotlas); // requests will go to our AliceWOTLAS class
        connection.addConnectionListener(this); // we will listen to the connection's state

        // 4 - Final step, all inits have been done, we welcome our new client...
        acceptClient(connection);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To send back Alice's answer to the 'botPrimaryKey' player on server 'serverID'.
     *
     *  @param playerPrimaryKey pk of the player whose the answer is for
     *  @param botPrimaryKey pk of the bot
     *  @param answer sent by alice
     *  @param serverID wotlas server to send the answer to
     */
    public void sendAnswer(String playerPrimaryKey, String botPrimaryKey, String answer, int serverID) {

        // 1 - Search for the server's connection
        NetConnection connection = this.serverLinks.get("" + serverID);

        // 2 - Send the message...
        if (connection != null) {
            connection.queueMessage(new AliceWotlasMessage(playerPrimaryKey, botPrimaryKey, answer, serverID));
        } else {
            Debug.signal(Debug.ERROR, this, "AliceWotlasServer server " + serverID + " connection not found...");
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** This method is called when a new network connection is created.
     *
     * @param connection the NetConnection object associated to this connection.
     */
    @Override
    public void connectionCreated(NetConnection connection) {
        // well nothing to do here...
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** This method is called when a network connection is no longer of this world.
     *
     * @param connection the NetConnection object associated to this connection.
     */
    @Override
    public void connectionClosed(NetConnection connection) {

        // We perform some cleaning...
        // We search the server NetConnection entry in our table
        synchronized (this.serverLinks) {
            Iterator<NetConnection> it = this.serverLinks.values().iterator();

            while (it.hasNext()) {
                NetConnection np = it.next();

                if (np == connection) {
                    // ok, we found it... we can remove it...
                    it.remove();
                    Debug.signal(Debug.NOTICE, null, "AliceWotlasServer asked to close a connection.");
                    return;
                }
            }
        }

        Debug.signal(Debug.WARNING, this, "AliceWotlasServer server connection not found... " + "this message could mean that the connection was just shut and re-opened.");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /**
     *  Shuts down the process.
     */
    public void shutdown() {
        // 1 - Stop Server
        stopServer();

        // 2 - Shuts down all connections
        synchronized (this.serverLinks) {
            Iterator<NetConnection> it = this.serverLinks.values().iterator();

            while (it.hasNext()) {
                NetConnection np = it.next();
                np.close();
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
