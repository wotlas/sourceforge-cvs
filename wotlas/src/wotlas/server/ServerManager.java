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
 
package wotlas.server;

import wotlas.common.*;
import wotlas.utils.Debug;

 /** A Server Manager manages three servers : A GameServer, a AccountServer and
  *  a GatewayServer. The parameters for these servers are gathered in config/server.cfg.
  *
  * @author Aldiss
  * @see wotlas.server.GameServer
  * @see wotlas.server.AccountServer
  * @see wotlas.server.GatewayServer
  * @see wotlas.common.Serverconfig
  */
 
public class ServerManager
{
 /*------------------------------------------------------------------------------------*/
 
   /** Our Default ServerManager.
    */
      private static ServerManager serverManager;

 /*------------------------------------------------------------------------------------*/
 
   /** Our server config files.
    */
      private ServerConfigList configs;

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
  
  /** Constructor. Attemps to load the config/server.cfg file... and then constructs
   *  the different servers ( but doesnot start them).
   */
   private ServerManager() {

       // 1 - we load the ServerConfig files...
          Debug.signal( Debug.NOTICE, null, "Updating server config files from Internet home... please wait...");
       
          PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();          

          configs = new ServerConfigList( pm );
          configs.setRemoteServerConfigHomeURL( ServerDirector.getRemoteServerConfigHomeURL() );
          configs.setLocalServerID( ServerDirector.getServerID() );

          configs.getLatestConfigFiles(null);  // we retrieve all the server files

          ourConfig = configs.getServerConfig( ServerDirector.getServerID() );

          if( ourConfig == null ) {
               Debug.signal( Debug.FAILURE, this, "Can't init servers without a ServerConfig !" );
               Debug.exit();
          }

       // 2 - We create the AccountServer
          String account_packages[] = { "wotlas.server.message.account" };

          accountServer = new AccountServer( ourConfig.getServerName(),
                                             ourConfig.getAccountServerPort(),
                                             account_packages,
                                             ourConfig.getMaxNumberOfAccountConnections() );

       // 3 - We create the GameServer
          String game_packages[] = { "wotlas.server.message.description",
                                     "wotlas.server.message.movement",
                                     "wotlas.server.message.chat" };

          gameServer = new GameServer( ourConfig.getServerName(),
                                       ourConfig.getGameServerPort(),
                                       game_packages,
                                       ourConfig.getMaxNumberOfGameConnections() );

       // 4 - We create the GatewayServer
          String gateway_packages[] = { "wotlas.server.message.gateway" };

          gatewayServer = new GatewayServer( ourConfig.getServerName(),
                                             ourConfig.getGatewayServerPort(),
                                             gateway_packages,
                                             ourConfig.getMaxNumberOfGatewayConnections(),
                                             configs );

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

  
  /** Creates a server manager. Attemps to load the config/server.cfg file... and then
   * constructs the different servers ( but doesnot start them ).
   *
   * @return the created (or previously created) server manager.
   */
   public static ServerManager createServerManager() {
         if( serverManager == null )
             serverManager = new ServerManager();
         
         return serverManager;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the default server manager.
   *
   * @return the default server manager.
   */
   public static ServerManager getDefaultServerManager() {
         return serverManager;
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

  /** To get the ServerConfig.
   *
   * @return the serverConfig
   */
    public ServerConfig getServerConfig() {
         return ourConfig;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
