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

import wotlas.common.*;
import wotlas.utils.Debug;
import wotlas.common.message.account.WarningMessage;

import java.util.Iterator;

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
   public ServerManager( ResourceManager rManager ) {

       // 1 - we load the ServerConfig files...
          Debug.signal( Debug.NOTICE, null, "Updating server config files from Internet home... please wait...");
       
          serverConfigManager = new ServerConfigManager( rManager );
          serverConfigManager.setRemoteServerConfigHomeURL( ServerDirector.getRemoteServerConfigHomeURL() );
          serverConfigManager.setLocalServerID( ServerDirector.getServerID() );

          if(ServerDirector.getServerID()!=0)
             serverConfigManager.getLatestConfigFiles(null);  // we retrieve all the server files

          ourConfig = serverConfigManager.getServerConfig( ServerDirector.getServerID() );

          if( ourConfig == null ) {
              Debug.signal( Debug.FAILURE, this, "Can't init servers without a ServerConfig !" );
              Debug.exit();
          }

       // 2 - We create the AccountServer
          String account_packages[] = { "wotlas.server.message.account" };

          accountServer = new AccountServer( ServerDirector.getServerProperties().getProperty("init.serverItf"),
                                             ourConfig.getAccountServerPort(),
                                             account_packages,
                                             ourConfig.getMaxNumberOfAccountConnections() );

       // 3 - We create the GameServer
          String game_packages[] = { "wotlas.server.message.description",
                                     "wotlas.server.message.movement",
                                     "wotlas.server.message.chat",
                                     "wotlas.server.message.action"};

          gameServer = new GameServer( ServerDirector.getServerProperties().getProperty("init.serverItf"),
                                       ourConfig.getGameServerPort(),
                                       game_packages,
                                       ourConfig.getMaxNumberOfGameConnections() );

       // 4 - We create the GatewayServer
          String gateway_packages[] = { "wotlas.server.message.gateway" };

          gatewayServer = new GatewayServer( ServerDirector.getServerProperties().getProperty("init.serverItf"),
                                             ourConfig.getGatewayServerPort(),
                                             gateway_packages,
                                             ourConfig.getMaxNumberOfGatewayConnections(),
                                             serverConfigManager );

       // Everything is ready on the network side...
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Starts the 3 servers.
   */
   public void start() {
       gameServer.start();
       accountServer.start();
       gatewayServer.start();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Shutdown the 3 servers.
   */
   public synchronized void shutdown() {

     // 1 - We stop the servers
       Debug.signal( Debug.NOTICE, null, "Shuting down servers..." );
       gameServer.stopServer();
       accountServer.stopServer();
       gatewayServer.stopServer();

     // 2 - We perform some clean up
       gameServer = null;
       accountServer = null;
       gatewayServer = null;
       ourConfig = null;
       serverConfigManager = null;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To lock the access to servers.
   */
   public void lockServers() {
       gameServer.setServerLock( true );
       accountServer.setServerLock( true );
       gatewayServer.setServerLock( true );

       Debug.signal(Debug.NOTICE,null,"ServerManager locked access to local servers...");
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To unlock the access to servers.
   */
   public void unlockServers() {
       gameServer.setServerLock( false );
       accountServer.setServerLock( false );
       gatewayServer.setServerLock( false );

       Debug.signal(Debug.NOTICE,null,"ServerManager unlocked access to local servers...");
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To send a warning message to all connected players.
   */
    public void sendWarningMessage( String text ) {
        WarningMessage msg = new WarningMessage( text );
        gameServer.sendMessageToOpenedConnections( msg );
        accountServer.sendMessageToOpenedConnections( msg );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To close all the connections on the servers.
   */
    public void closeAllConnections() {
        gameServer.closeConnections();
        accountServer.closeConnections();
        gatewayServer.closeConnections();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the GameServer.
   *
   * @return the game server.
   */
   public GameServer getGameServer() {
         return gameServer;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the AccountServer.
   *
   * @return the account server.
   */
    public AccountServer getAccountServer() {
         return accountServer;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the GatewayServer.
   *
   * @return the gateway server.
   */
   public GatewayServer getGatewayServer() {
         return gatewayServer;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the ServerConfig of this server.
   *
   * @return the serverConfig
   */
    public ServerConfig getServerConfig() {
         return ourConfig;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the ServerConfigManager of this server.
   * @return the ServerConfigManager
   */
    public ServerConfigManager getServerConfigManager() {
         return serverConfigManager;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
