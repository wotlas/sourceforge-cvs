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

package wotlas.common;

/** A ServerConfig contains all the basic information of a server : name, ports, etc...
 *<p>
 *  It has two purposes :<p><br>
 *
 *   - provide start-up information for the ServerManager or for the client side.<br>
 *
 *   - provide server information to export to a central web server
 *     ( http://wotlas.net for instance ). This central server registers
 *     game servers and publish a up-to-date list of active servers. Wotlas Client Software
 *     can then download this list and display it to the user.<br><p>
 *
 * The ServerConfig class is saved in $base$/servers/server-<id>.cfg. and
 * $base$/servers/server-<id>-adr.cfg
 *
 * When you install a wotlas server you need to create this file. To help you a setup tool
 * is provided in the package : wotlas.server.setup.ServerSetup
 *
 * @author Aldiss
 * @see wotlas.server.setup.ServerSetup
 */

public class ServerConfig {
    /*------------------------------------------------------------------------------------*/

    /** Name used for the standalone server : the one used by a client that use memory buffers to exchange with a server instantiated in memory. */
    public static final String STANDALONE_SERVERNAME = "standalone";

    /** Server Symbolic Name.
     */
    private String serverSymbolicName;

    /** Server Name, normally the host name. Example: "tatoo.wotlas.org"
     *  Since wotlas v1.2 the serverName is transient and saved separately in an other file.
     *  see javadoc header of this class.
     */
    transient private String serverName;

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

    /** Maximum Number of Connections on the Game Server.
     */
    private int maxNumberOfGameConnections;

    /** Maximum Number of Connections on the Account Server.
     */
    private int maxNumberOfAccountConnections;

    /** Maximum Number of Connections on the Gateway Server.
     */
    private int maxNumberOfGatewayConnections;

    /** A description of the content of this server : towns, guilds, etc ...
     */
    private String description;

    /** Server Physical Location. Is it in France, England, USA, Australia ?
     */
    private String location;

    /** Email of the Wotlas Server Administrator.
     */
    private String adminEmail;

    /** ServerConfig Version.
     */
    private String configVersion;

    /** The first x position in the world.
     */
    private int worldFirstXPosition;

    /** The first y position in the world.
     */
    private int worldFirstYPosition;

    /*------------------------------------------------------------------------------------*/

    /** Last time we updated the server data.
     */
    transient private long lastUpdateTime;

    /*------------------------------------------------------------------------------------*/

    /** Empty Constructor for persistence.
     *  Data is loaded by the PersistenceManager.
     */
    public ServerConfig() {
        this.serverSymbolicName = new String("Wotlas Server");
        this.serverName = null;
        this.serverID = 0;
        this.accountServerPort = 25500;
        this.gameServerPort = 26500;
        this.gatewayServerPort = 27500;
        this.maxNumberOfGameConnections = 110;
        this.maxNumberOfAccountConnections = 20;
        this.maxNumberOfGatewayConnections = 20;
        this.description = new String("Enter a description for your server...");
        this.location = new String("France / USA / Germany / England / Italy...");
        this.adminEmail = new String("myAdress@foobar.net");
        this.lastUpdateTime = 0;

        this.worldFirstXPosition = 743;
        this.worldFirstYPosition = 277; // default is Tar Valon
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To update this ServerConfig from another.
     * @param other other ServerConfig.
     */
    public void update(ServerConfig other) {
        this.serverSymbolicName = other.getServerSymbolicName();
        this.serverName = other.getServerName();
        this.serverID = other.getServerID();
        this.accountServerPort = other.getAccountServerPort();
        this.gameServerPort = other.getGameServerPort();
        this.gatewayServerPort = other.getGatewayServerPort();
        this.maxNumberOfGameConnections = other.getMaxNumberOfGameConnections();
        this.maxNumberOfAccountConnections = other.getMaxNumberOfAccountConnections();
        this.maxNumberOfGatewayConnections = other.getMaxNumberOfGatewayConnections();
        this.description = other.getDescription();
        this.location = other.getLocation();
        this.adminEmail = other.getAdminEmail();
        this.configVersion = other.getConfigVersion();
        this.worldFirstXPosition = other.getWorldFirstXPosition();
        this.worldFirstYPosition = other.getWorldFirstYPosition();

        this.lastUpdateTime = System.currentTimeMillis();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Server Name. Normally it's the host name. Example: "tatoo.wotlas.org"
     *
     * @return server name
     */
    public String getServerName() {
        return this.serverName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the Server Name. Normally it's the host name. Example: "tatoo.wotlas.org"
     *
     * @param serverName server name
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Server Symbolic Name. Example: "My Wotlas Server"
     *
     * @return server symbolic name
     */
    public String getServerSymbolicName() {
        return this.serverSymbolicName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the Server Symbolic Name. Example: "My Wotlas Server"
     *
     * @param serverSymbolicName server symbolic name
     */
    public void setServerSymbolicName(String serverSymbolicName) {
        this.serverSymbolicName = serverSymbolicName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Server ID. This ID is given by light-and-shadow.org.
     *  See the wotlas.setup.
     *
     * @return serverID
     */
    public int getServerID() {
        return this.serverID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the Server ID. This ID is given by light-and-shadow.org.
     *  See the wotlas.setup.
     *
     * @param serverID server ID to set
     */
    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the port for the AccountServer.
     *
     * @return accountServerPort
     */
    public int getAccountServerPort() {
        return this.accountServerPort;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the port for the AccountServer. There is no restriction on this number.
     *
     * @param accountServerPort account Server Port to set
     */
    public void setAccountServerPort(int accountServerPort) {
        this.accountServerPort = accountServerPort;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the port for the GameServer.
     *
     * @return gameServerPort
     */
    public int getGameServerPort() {
        return this.gameServerPort;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the port for the GameServer. There is no restriction on this number.
     *
     * @param gameServerPort game Server Port to set
     */
    public void setGameServerPort(int gameServerPort) {
        this.gameServerPort = gameServerPort;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the port for the GatewayServer.
     *
     * @return gatewayServerPort
     */
    public int getGatewayServerPort() {
        return this.gatewayServerPort;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the port for the GatewayServer. There is no restriction on this number.
     *
     * @param gatewayServerPort gateway Server Port to set
     */
    public void setGatewayServerPort(int gatewayServerPort) {
        this.gatewayServerPort = gatewayServerPort;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the maximum number of game connections.
     *
     * @param maxNumberOfGameConnections to set
     */
    public void setMaxNumberOfGameConnections(int maxNumberOfGameConnections) {
        this.maxNumberOfGameConnections = maxNumberOfGameConnections;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the maximum number of account connections.
     *
     * @param maxNumberOfAccountConnections to set
     */
    public void setMaxNumberOfAccountConnections(int maxNumberOfAccountConnections) {
        this.maxNumberOfAccountConnections = maxNumberOfAccountConnections;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the maximum number of gateway connections.
     *
     * @param maxNumberOfGatewayConnections to set
     */
    public void setMaxNumberOfGatewayConnections(int maxNumberOfGatewayConnections) {
        this.maxNumberOfGatewayConnections = maxNumberOfGatewayConnections;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the maximum number of account connections.
     *
     * @return maxNumberOfAccountConnections
     */
    public int getMaxNumberOfAccountConnections() {
        return this.maxNumberOfAccountConnections;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the maximum number of game connections.
     *
     * @return maxNumberOfGameConnections
     */
    public int getMaxNumberOfGameConnections() {
        return this.maxNumberOfGameConnections;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the maximum number of gateway connections.
     *
     * @return maxNumberOfGatewayConnections
     */
    public int getMaxNumberOfGatewayConnections() {
        return this.maxNumberOfGatewayConnections;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a description of the content of this server : towns, guilds, etc ...
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set a description about the content of this server : towns, guilds, etc ...
     *
     * @param description server description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Server Physical Location. Is it in France, England, USA, Australia ?
     *
     * @return location
     */
    public String getLocation() {
        return this.location;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the Server Physical Location. Is it in France, England, USA, Australia ?
     *
     * @param location location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the email of the Wotlas Server Administrator.
     *
     * @return adminEmail
     */
    public String getAdminEmail() {
        return this.adminEmail;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the email of the Wotlas Server Administrator.
     *
     * @param adminEmail wotlas server admin email
     */
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the version of this server config
     *
     * @return configVersion
     */
    public String getConfigVersion() {
        return this.configVersion.trim();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the version of this server config.
     */
    public void setConfigVersion() {
        this.configVersion = "WOT-" + System.currentTimeMillis();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the version of this SErverConfig.
     *
     * @param configVersion version.
     */
    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion.trim();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's first x position in the world.
     * @return worldFirstXPosition
     */
    public int getWorldFirstXPosition() {
        return this.worldFirstXPosition;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's first x position in the world.
     * @param worldFirstXPosition
     */
    public void setWorldFirstXPosition(int worldFirstXPosition) {
        this.worldFirstXPosition = worldFirstXPosition;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's first y position in the world.
     * @return worldFirstYPosition
     */
    public int getWorldFirstYPosition() {
        return this.worldFirstYPosition;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's first y position in the world.
     * @param worldFirstYPosition
     */
    public void setWorldFirstYPosition(int worldFirstYPosition) {
        this.worldFirstYPosition = worldFirstYPosition;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get this ServerConfig as a String.
     *
     * @return a string containing all the information to export.
     */
    @Override
    public String toString() {
        return "Wotlas Server Declaration\n" + "Ref:\n " + this.configVersion + "\n" + "\nServer Symbolic Name:\n   " + this.serverSymbolicName + "\nServer Name:\n   " + this.serverName + "\nServer ID:\n   " + this.serverID + "\n" + "\nAccount Port:\n   " + this.accountServerPort + "\nGame Port:\n   " + this.gameServerPort + "\nGateway Port:\n   " + this.gatewayServerPort + "\n" + "\nLocation:\n   " + this.location + "\nAdmin e-mail:\n   " + this.adminEmail + "\nDescription:\n   " + this.description + "\n";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get this ServerConfig as a HTML String.
     *
     * @return a string containing all the information to export.
     */
    public String toHTML() {
        return "<h2>Wotlas Server</h2><br>" + "<b>Server Symbolic Name :</b> " + this.serverSymbolicName + "<br>" + "<b>Server address:</b> " + this.serverName + "<br>" + "<b>Server ID:</b> " + this.serverID + "<br>" + "<b>Location:</b> " + this.location + "<br>" + "<b>Admin e-mail:</b> <i>" + this.adminEmail + "</i><br>" + "<b>Description:</b> " + this.description + "<br>";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the last time we updated this config.
     */
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To clear the last update timestamp. This operation will force the next call
     *  to this config (via the ServerConfigList) to first fetch a new config on the net.
     */
    public void clearLastUpdateTime() {
        this.lastUpdateTime = 0;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
