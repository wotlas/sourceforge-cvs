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

package wotlas.client;

/** ProfileConfig contains all the basic information of a client to
 * connect to a server : login, server
 *
 * @author Petrus
 * @see wotlas.client.PersistenceManager
 */

public class ProfileConfig
{
  /** client's login
   */
  private String login;
  
  /** client's password
   */
  private transient String password;
  
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

  /** Empty Constructor for persitence.
   * Data is loaded by the PersistenceManager.
   */
  public ProfileConfig() {
    login = new String("nobody");
    password = new String("toto");
    serverID = -1;
    serverName = new String("nope");
    serverPort = -1;
    originalServerID = -1;
    localClientID = -1;
  }
  
  /** To get the client's Login
   */
  public String getLogin() {
    return login;
  }
  
  /** To set the client's Login
   */
  public void setLogin(String login) {
    this.login = login;
  }
  
  /** To get the client's password
   */
  public String getPassword() {
    return password;
  }
  
  /** To set the client's password
   */
  public void setPassword(String password) {
    this.password = password;
  }
  
  /** To get the serverID where the client last connected
   */
  public int getServerID() {
    return serverID;
  } 
  
  /** To set the serverID where the client last connected
   */
  public void setServerID(int serverID) {
    this.serverID = serverID;
  }
  
  /** To get the serverName where the client last connected
   */
  public String getServerName() {
    return serverName;
  }
  
  /** To set the serverName where the client last connected
   */
  public void setServerName(String serverName) {
    this.serverName = serverName;
  }
  
  /** To get the serverPort where the client last connected
   */
  public int getServerPort() {
    return serverPort;
  }
  
  /** To set the serverPort where the client last connected
   */
  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }
  
  /** To get the serverID of the server where the client was first created
   */
  public int getOriginalServerID() {
    return originalServerID;
  }
  
  /** To set the serverID of the server where the client was first created
   */
  public void setOriginalServerID(int originalServerID) {
    this.originalServerID = originalServerID;
  }
  
  /** To get the localClientID
   */
  public int getLocalClientID() {
    return localClientID;
  }
  
  /** To set the localClientID
   */
  public void setLocalClientID(int localClientID) {
    this.localClientID = localClientID;
  }  
  
  /** To get the client's identification key
   */
  public String getKey() {
    return ""+originalServerID+"-"+localClientID;
  }  
  
}