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
<ADD MAXGAMECONNECTIONS,MAXACCOUNTCONNECTIONS,MAXGATEWAYCONNECTIONS>

/** A ServerConfig contains all the basic information of a server : name, ports, etc...
 *<p>
 *  It has two purposes :<p><br>
 *
 *   - provide start-up information for the ServerManager.<br>
 *
 *   - provide server information to export to a central web server
 *     ( http://light-and-shadow.org for instance ). This central server registers
 *     game servers and publish a up-to-date list of active servers. Wotlas Client Software
 *     can then download this list and display it to the user.<br><p>
 *
 * The ServerConfig class is saved by the PersistenceManager in config/server.cfg.
 * When you install a wotlas server you need to create this file. To help you a setup tool
 * is provided in the package : wotlas.server.setup.
 *
 * @author Aldiss
 * @see wotlas.server.setup.ServerSetup
 * @see wotlas.server.ServerManager
 */

class ServerConfig
{
 /*------------------------------------------------------------------------------------*/

   /** Server Name, normally the host name. Example: "tatoo.wotlas.org"
    */
      private String serverName;

   /** Server ID. This ID is given by light-and-shadow.org. See the wotlas.setup package.
    */
      private int serverID;

   /** Port for the AccountServer. There is no restriction on this number.
    */
      private int accountServerPort;

   /** Port for the GameServer. There is no restriction on this number.
    */
      private int gameServerPort;

   /** Port for the GatewayServer. There is no restriction on this number.
    */
      private int gatewayServerPort;

   /** A description of the content of this server : towns, guilds, etc ...
    */
      private String description;

   /** Server Physical Location. Is it in France, England, USA, Australia ?
    */
      private String location;

   /** Email of the Wotlas Server Administrator.
    */
      private String adminEmail;

 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor for persistence.
   *  Data is loaded by the PersistenceManager.
   */
     public ServerConfig() {
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Server Name. Normally it's the host name. Example: "tatoo.wotlas.org"
    *
    * @return server name
    */
      public String getServerName() {
          return serverName;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the Server Name. Normally it's the host name. Example: "tatoo.wotlas.org"
    *
    * @param serverName server name
    */
      public void setServerName( String serverName ) {
          this.serverName = serverName;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Server ID. This ID is given by light-and-shadow.org.
    *  See the wotlas.setup.
    *
    * @return serverID
    */
      public int getServerID() {
         return serverID;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the Server ID. This ID is given by light-and-shadow.org.
    *  See the wotlas.setup.
    *
    * @param serverID server ID to set
    */
      public void setServerID( int serverID ) {
         this.serverID = serverID;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the port for the AccountServer.
    *
    * @return accountServerPort
    */
      public int getAccountServerPort() {
         return accountServerPort;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the port for the AccountServer. There is no restriction on this number.
    *
    * @param accountServerPort account Server Port to set
    */
      public void setAccountServerPort(  int accountServerPort ) {
         this.accountServerPort = accountServerPort;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the port for the GameServer.
    *
    * @return gameServerPort
    */
      public int getGameServerPort() {
         return gameServerPort;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the port for the GameServer. There is no restriction on this number.
    *
    * @param gameServerPort game Server Port to set
    */
      public void setGameServerPort(  int gameServerPort ) {
         this.gameServerPort = gameServerPort;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the port for the GatewayServer.
    *
    * @return gatewayServerPort
    */
      public int getGatewayServerPort() {
         return gatewayServerPort;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the port for the GatewayServer. There is no restriction on this number.
    *
    * @param gatewayServerPort gateway Server Port to set
    */
      public void setGatewayServerPort(  int gatewayServerPort ) {
         this.gatewayServerPort = gatewayServerPort;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get a description of the content of this server : towns, guilds, etc ...
    *
    * @return description
    */
      public String getDescription() {
         return description;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set a description about the content of this server : towns, guilds, etc ...
    *
    * @param description server description
    */
      public void setDescription( String description ) {
         this.description = description;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Server Physical Location. Is it in France, England, USA, Australia ?
    *
    * @return location
    */
      public String getLocation() {
         return location;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the Server Physical Location. Is it in France, England, USA, Australia ?
    *
    * @param location location to set
    */
      public void setLocation( String location ) {
         this.location = location;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the email of the Wotlas Server Administrator.
    *
    * @return adminEmail
    */
      public String getAdminEmail() {
         return adminEmail;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the email of the Wotlas Server Administrator.
    *
    * @param adminEmail wotlas server admin email
    */
      public void setAdminEmail( String adminEmail ) {
         this.adminEmail = adminEmail;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the data to export to the wotlas server registry ( light-and-shadow.org )
    *
    * @return a string containing all the information to export.
    */
      public String toString() {
          return "Wotlas Server Declaration\n"
                 +"Ref:\n\t#WOT"+System.currentTimeMillis()+"\n"
                 +"\nServer Name:\n\t"+serverName
                 +"\nServer ID:\n\t"+serverID+"\n"
                 +"\nAccount Port:\n\t"+accountServerPort
                 +"\nGame Port:\n\t"+gameServerPort
                 +"\nGateway Port:\n\t"+gatewayServerPort+"\n"
                 +"\nLocation:\n\t"+location
                 +"\nAdmin e-mail:\n\t"+adminEmail
                 +"Description:\n\t"+description+"\n";
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

