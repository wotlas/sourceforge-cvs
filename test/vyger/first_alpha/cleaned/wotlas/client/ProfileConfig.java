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

package wotlas.client;

/** ProfileConfig contains all the basic information of a client to
 * connect to a server : login, server
 *
 * @author Petrus
 * @see wotlas.client.PersistenceManager
 */

public class ProfileConfig {
    /*------------------------------------------------------------------------------------*/

    /** To forbid access to passwords (default is false).
     */
    private static boolean forbidPasswordAccess = false;

    /*------------------------------------------------------------------------------------*/

    /** client's login
     */
    private String login;

    /** client's password
     */
    private String password;

    /** client's name
     */
    private String playerName;

    /** serverID where the client last connected
     */
    private int serverID;

    /** serverName where the client last connected
     */
    private transient String serverName;

    /** serverPort where the client last connected
     */
    private transient int serverPort;

    /** serverID where the client was first created
     */
    private int originalServerID;

    /** local clientID of the server where the client was first created
     */
    private int localClientID;

    /*------------------------------------------------------------------------------------*/

    /** To set if we can access to passwords store in this class.
     * @param forbid set to true to forbid the access
     */
    static void setPasswordAccess(boolean forbid) {
        ProfileConfig.forbidPasswordAccess = forbid;
    }

    /*------------------------------------------------------------------------------------*/

    /** Empty Constructor for persitence.
     * Data is loaded by the PersistenceManager.
     */
    public ProfileConfig() {
        this.login = new String("nobody");
        this.password = null;
        this.playerName = new String("nobody");
        this.serverID = -1;
        this.serverName = new String("nope");
        this.serverPort = -1;
        this.originalServerID = -1;
        this.localClientID = -1;
    }

    /** To get the client's Login
     */
    public String getLogin() {
        return this.login;
    }

    /** To set the client's Login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /** To get the client's password. Beware of the state of forbidPasswordAccess.
     */
    public String getPassword() {
        if (ProfileConfig.forbidPasswordAccess)
            return null;

        return this.password;
    }

    /** To set the client's password. Beware of the state of forbidPasswordAccess.
     */
    public void setPassword(String password) {
        if (!ProfileConfig.forbidPasswordAccess)
            this.password = password;
    }

    /** To get the client's Name
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /** To set client's Name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /** To get the serverID where the client last connected
     */
    public int getServerID() {
        return this.serverID;
    }

    /** To set the serverID where the client last connected
     */
    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    /** To get the serverName where the client last connected
     */
    public String getServerName() {
        return this.serverName;
    }

    /** To set the serverName where the client last connected
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /** To get the serverPort where the client last connected
     */
    public int getServerPort() {
        return this.serverPort;
    }

    /** To set the serverPort where the client last connected
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /** To get the serverID of the server where the client was first created
     */
    public int getOriginalServerID() {
        return this.originalServerID;
    }

    /** To set the serverID of the server where the client was first created
     */
    public void setOriginalServerID(int originalServerID) {
        this.originalServerID = originalServerID;
    }

    /** To get the localClientID
     */
    public int getLocalClientID() {
        return this.localClientID;
    }

    /** To set the localClientID
     */
    public void setLocalClientID(int localClientID) {
        this.localClientID = localClientID;
    }

    /** To get the client's identification key
     */
    public String getKey() {
        return "" + this.originalServerID + "-" + this.localClientID;
    }

    /*------------------------------------------------------------------------------------*/

}