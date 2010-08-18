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

import wotlas.common.ResourceManager;
import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigManager;
import wotlas.common.message.account.WarningMessage;
import wotlas.common.message.account.WishServerAccountNetMsgBehaviour;
import wotlas.common.message.chat.WishServerChatNetMsgBehaviour;
import wotlas.common.message.description.WishServerDescriptionNetMsgBehaviour;
import wotlas.common.message.movement.WishServerMovementNetMsgBehaviour;
import wotlas.libs.net.NetConfig;
import wotlas.server.message.gateway.WishGatewayNetMsgBehaviour;
import wotlas.utils.Debug;

/** A Server Manager manages three servers : A GameServer, a AccountServer and
 *  a GatewayServer.
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 * @see wotlas.server.AccountServer
 * @see wotlas.server.GatewayServer
 * @see wotlas.common.Serverconfig
 */

public class ServerManager {

    /*------------------------------------------------------------------------------------*/

    /** Our server config files.
     */
    private ServerConfigManager serverConfigManager;

    /** Config of this server.
     */
    private ServerConfig ourConfig;

    /** Our GameServer
     */
    private GameServer gameServer;

    /** Our AccountServer
     */
    private AccountServer accountServer;

    /** Our GatewayServer
     */
    private GatewayServer gatewayServer;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. Attemps to load the server config files... then constructs
     *  the different servers ( but does not start them).
     */
    public ServerManager(ResourceManager rManager) {

        // 1 - we load the ServerConfig files...
        Debug.signal(Debug.NOTICE, null, "Updating server config files from Internet home... please wait...");

        this.serverConfigManager = new ServerConfigManager(rManager);
        this.serverConfigManager.setRemoteServerConfigHomeURL(ServerDirector.getRemoteServerConfigHomeURL());
        this.serverConfigManager.setLocalServerID(ServerDirector.getServerID());

        if (ServerDirector.getServerID() != 0)
            this.serverConfigManager.getLatestConfigFiles(null); // we retrieve all the server files

        this.ourConfig = this.serverConfigManager.getServerConfig(ServerDirector.getServerID());

        if (this.ourConfig == null) {
            Debug.signal(Debug.FAILURE, this, "Can't init servers without a ServerConfig !");
            Debug.exit();
        }

        // 2 - We create the AccountServer
        Class account_msgs[] = { WishServerAccountNetMsgBehaviour.class };
        NetConfig accountNetCfg = new NetConfig(ServerDirector.getServerProperties().getProperty("init.serverItf"), this.ourConfig.getAccountServerPort());
        this.accountServer = new AccountServer(accountNetCfg, account_msgs, this.ourConfig.getMaxNumberOfAccountConnections(), rManager.getGameDefinition());

        // 3 - We create the GameServer
        Class game_msgs[] = { WishServerDescriptionNetMsgBehaviour.class, WishServerMovementNetMsgBehaviour.class, WishServerChatNetMsgBehaviour.class };
        NetConfig gameNetCfg = new NetConfig(ServerDirector.getServerProperties().getProperty("init.serverItf"), this.ourConfig.getGameServerPort());
        this.gameServer = new GameServer(gameNetCfg, game_msgs, this.ourConfig.getMaxNumberOfGameConnections(), rManager.getGameDefinition());

        // 4 - We create the GatewayServer
        Class gateway_msgs[] = { WishGatewayNetMsgBehaviour.class };
        NetConfig gatewayNetCfg = new NetConfig(ServerDirector.getServerProperties().getProperty("init.serverItf"), this.ourConfig.getGatewayServerPort());
        this.gatewayServer = new GatewayServer(gatewayNetCfg, gateway_msgs, this.ourConfig.getMaxNumberOfGatewayConnections(), this.serverConfigManager, rManager.getGameDefinition());

        // Everything is ready on the network side...
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Starts the 3 servers.
     */
    public void start() {
        this.gameServer.start();
        this.accountServer.start();
        this.gatewayServer.start();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Shutdown the 3 servers.
     */
    public synchronized void shutdown() {

        // 1 - We stop the servers
        Debug.signal(Debug.NOTICE, null, "Shuting down servers...");
        this.gameServer.stopServer();
        this.accountServer.stopServer();
        this.gatewayServer.stopServer();

        // 2 - We perform some clean up
        this.gameServer = null;
        this.accountServer = null;
        this.gatewayServer = null;
        this.ourConfig = null;
        this.serverConfigManager = null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To lock the access to servers.
     */
    public void lockServers() {
        this.gameServer.setServerLock(true);
        this.accountServer.setServerLock(true);
        this.gatewayServer.setServerLock(true);

        Debug.signal(Debug.NOTICE, null, "ServerManager locked access to local servers...");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To unlock the access to servers.
     */
    public void unlockServers() {
        this.gameServer.setServerLock(false);
        this.accountServer.setServerLock(false);
        this.gatewayServer.setServerLock(false);

        Debug.signal(Debug.NOTICE, null, "ServerManager unlocked access to local servers...");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To send a warning message to all connected players.
     */
    public void sendWarningMessage(String text) {
        WarningMessage msg = new WarningMessage(text);
        this.gameServer.sendMessageToOpenedConnections(msg);
        this.accountServer.sendMessageToOpenedConnections(msg);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To close all the connections on the servers.
     */
    public void closeAllConnections() {
        this.gameServer.closeConnections();
        this.accountServer.closeConnections();
        this.gatewayServer.closeConnections();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the GameServer.
     *
     * @return the game server.
     */
    public GameServer getGameServer() {
        return this.gameServer;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the AccountServer.
     *
     * @return the account server.
     */
    public AccountServer getAccountServer() {
        return this.accountServer;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the GatewayServer.
     *
     * @return the gateway server.
     */
    public GatewayServer getGatewayServer() {
        return this.gatewayServer;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the ServerConfig of this server.
     *
     * @return the serverConfig
     */
    public ServerConfig getServerConfig() {
        return this.ourConfig;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the ServerConfigManager of this server.
     * @return the ServerConfigManager
     */
    public ServerConfigManager getServerConfigManager() {
        return this.serverConfigManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
