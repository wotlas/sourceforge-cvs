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

package wotlas.server;

import wotlas.common.ErrorCodeList;
import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigManager;
import wotlas.libs.net.NetClient;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetErrorCodeList;
import wotlas.libs.net.NetServer;
import wotlas.utils.Debug;

/** Wotlas Gateway Server. This is used for account transaction management.
 *  The server awaits for account transferts and also provides a method
 *  to send an account to a remote GatewayServer.
 *
 *  This server supposes there is a PersistenceManager & DataManager
 *  already created.
 *
 * @author Aldiss
 */

public class GatewayServer extends NetServer implements ErrorCodeList {

    /*------------------------------------------------------------------------------------*/

    /** Server config list.
     */
    private ServerConfigManager serverConfigManager;

    /*------------------------------------------------------------------------------------*/

    /** Constructor (see wotlas.libs.net.NetServer for details)
     *
     *  @param serverInterface the host interface to bind to. Example: wotlas.tower.org
     *  @param port port on which the server listens to clients.
     *  @param packages a list of packages where we can find NetMsgBehaviour Classes.
     *  @param nbMaxSockets maximum number of sockets that can be opened on this server
     *  @param serverConfigManager config of all the servers.
     */
    public GatewayServer(String serverInterface, int port, String packages[], int nbMaxSockets, ServerConfigManager serverConfigManager) {
        super(serverInterface, port, packages);
        setMaximumOpenedSockets(nbMaxSockets);

        this.serverConfigManager = serverConfigManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called automatically when a new client establishes a connection
     *  with this server ( the client sends a ClientRegisterMessage ).
     *
     * @param connection a previously created connection for this connection.
     * @param key a string given by the client to identify itself. The key should be
     *        equal to "AccountServerPlease!".
     */
    @Override
    public void accessControl(NetConnection connection, String key) {

        // The key is there to prevent wrong connections
        if (key.equals("GatewayServerPlease!")) {
            // ok, let's create an AccountTransaction for the operation.
            AccountTransaction transaction = new AccountTransaction(AccountTransaction.TRANSACTION_ACCOUNT_RECEIVER);

            // we set his message context to his player...
            connection.setContext(transaction);
            connection.addConnectionListener(transaction);

            // welcome on board...
            acceptClient(connection);
            Debug.signal(Debug.NOTICE, null, "Gateway Server is receiving an account...");
        } else {
            // NO VALID KEY
            Debug.signal(Debug.NOTICE, this, "Someone tried to connect with a bad key : " + key);
            refuseClient(connection, ErrorCodeList.ERR_WRONG_KEY, "Wrong key for this server :" + key);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To transfert an account to a remote server...
     *  @param account account to transfert
     *  @param remoteServerID remote server ID.
     */
    public boolean transfertAccount(String accountPrimaryKey, int remoteServerID) {

        Debug.signal(Debug.NOTICE, null, "Starting " + accountPrimaryKey + " account transfert to server " + remoteServerID + ".");

        // STEP 1 - Get the remote server config
        ServerConfig remoteServer = this.serverConfigManager.getServerConfig(remoteServerID);

        if (remoteServer == null) {
            Debug.signal(Debug.ERROR, this, "Server Config " + remoteServerID + " not found !");
            return false;
        }

        Debug.signal(Debug.NOTICE, null, "" + accountPrimaryKey + " will be sent to " + remoteServer.getServerName() + ":" + remoteServer.getGatewayServerPort());

        // STEP 2 - Creation of an AccountTransaction
        AccountTransaction transaction = new AccountTransaction(AccountTransaction.TRANSACTION_ACCOUNT_SENDER);

        // STEP 3 - Open a connection with the remote server
        NetClient client = new NetClient();
        NetConnection connection = client.connectToServer(remoteServer.getServerName(), remoteServer.getGatewayServerPort(), "GatewayServerPlease!", transaction, null);

        if (connection == null) {
            // We analyze the error returned
            if (client.getErrorCode() == NetErrorCodeList.ERR_CONNECT_FAILED) {
                // we report the deadlink and try the eventualy new address
                String newServerName = this.serverConfigManager.reportDeadServer(remoteServerID);
                if (newServerName != null)
                    Debug.signal(Debug.NOTICE, null, "Server dead link. Trying " + newServerName + ":" + remoteServer.getGatewayServerPort());

                if (this.server != null) {
                    client = new NetClient();
                    connection = client.connectToServer(newServerName, remoteServer.getGatewayServerPort(), "GatewayServerPlease!", transaction, null);
                }
            }

            if (connection == null) {
                Debug.signal(Debug.ERROR, this, client.getErrorMessage());
                return false;
            }
        }

        Debug.signal(Debug.NOTICE, null, "Gateway server reached. Starting transaction for " + accountPrimaryKey + ".");
        connection.addConnectionListener(transaction);

        // STEP 4 - Wait for the result (10s max)
        return transaction.transfertAccount(accountPrimaryKey, 10000, ServerDirector.getServerID());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
