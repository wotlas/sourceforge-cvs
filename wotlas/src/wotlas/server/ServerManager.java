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
<COMPLETE>
<PACKAGE NAMES (FOR SERVERS) TO COMPLETE>
<PERSISTENCE CORRECT>
<SET MAXSOCKS AS SERVERCONFIG>
<GETTER & SETTER FOR SERVER>

 /** A Server Manager manages three servers : A GameServer, a AccountServer and
  *  a GatewayServer. The parameters for these servers are gathered in config/server.cfg.
  *
  * @author Aldiss
  * @see wotlas.common.server.GameServer
  * @see wotlas.common.server.AccountServer
  * @see wotlas.common.server.GatewayServer
  * @see wotlas.common.server.Serverconfig
  */
 
public class ServerManager
{
 /*------------------------------------------------------------------------------------*/
 
   /** Our Default ServerManager.
    */
      private static ServerManager serverManager;

 /*------------------------------------------------------------------------------------*/
 
   /** Our ServerConfig file.
    */
      private ServerConfig config;

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

       // 1 - we load the ServerConfig file...
          config = new ServerConfig();

          PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
          
           try{
              pm.loadObject( config, "config/server.cfg" );
           }
           catch( PersistenceException pe ) {
               Debug.signal( Debug.FAILURE, this, pe );
               return false;
           }

       // 2 - We create the AccountServer
          String account_packages[] = { "wotlas.server.message.account" };

          accountServer = new AccountServer( config.getServerName(),
                                             config.getAccountServerPort(),
                                             account_packages );

          accountServer.setMaximumOpenedSockets( 20 );

       // 3 - We create the GameServer
          String game_packages[] = { "wotlas.server.message.description",
                                     "wotlas.server.message.movement" };

          gameServer = new GameServer( config.getServerName(),
                                       config.getGameServerPort(),
                                       game_packages );

          gameServer.setMaximumOpenedSockets( 110 );

       // 4 - We create the GatewayServer
          String gateway_packages[] = { "wotlas.server.message.gateway" };

          gatewayServer = new GatewayServer( config.getServerName(),
                                             config.getGatewayServerPort(),
                                             gateway_packages );

          gatewayServer.setMaximumOpenedSockets( 20 );

       // Everything is ready on the network side...
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

}
