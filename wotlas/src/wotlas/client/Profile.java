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

/** Profile contains all the basic information of a client to
 * connect to a server : login, server, password...
 *
 * @author Petrus
 */

public class Profile
{
  /** client's login
   */
  private String login;
  
  /** client's password
   */
  private String password;
  
  /** server name where the client was created
   */
  private String serverName;
   
  /** client's serial number : #s-cc where :<br>
   * <li>s is 
   * <li>cc is the clientID
   */
  
  /** serverID of the server where the client was first created
   */
  private int originalServerID;  
  
  /** local clientID in the server where the client was first created
   */
  private int localClientID;  

 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor for persitence.
   * Data is loaded by the PersistenceManager.
   */
  public Profile() {
    login = new String("nobody");
    password = new String("toto");
    serverName = new String("nope");
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
  public void setLogin(String clientLogin) {
    this.login = clientLogin;
  }
  
  /** To get the client's password
   */
  public String getPassword() {
    return password;
  }
  
  /** To set the client's password
   */
  public void setPassword(String clientPassword) {
    this.password = clientPassword;
  }
  
  /** To get the serverName where the client was created
   */
  public String getServerName() {
    return serverName;
  }
  
  /** To set the serverName where the client was created
   */
  public void setServerName(String serverName) {
    this.serverName = serverName;
  }
  
  /** To get the serverID of the server where the client was created
   */
  public int getOriginalServerID() {
    return originalServerID;
  }
  
  /** To set the serverID of the server where the client was created
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
  
  /** To get the client's serial number
   */
  public String getSerial() {
    return ""+originalServerID+"-"+localClientID;
  }  
  
}