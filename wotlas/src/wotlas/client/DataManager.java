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
 
// TODO : 
// - remplacer currentProfile par playerImpl

package wotlas.client;

import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.net.NetPersonality;
import wotlas.libs.net.NetMessage;

import wotlas.common.message.account.*;

import wotlas.utils.Debug;

/** A DataManager manages Game Data and client's connection.
 * It possesses a WorldManager
 *
 * @author Petrus  
 * @see wotlas.common.NetConnectionListener
 */

public class DataManager implements NetConnectionListener
{
  
 /*------------------------------------------------------------------------------------*/

  /** Our Default Data Manager
   */
  static private DataManager dataManager;

 /*------------------------------------------------------------------------------------*/

  /** Our World Manager
   */
  private WorldManager worldManager;

 /*------------------------------------------------------------------------------------*/

  /** Personality Lock
   */
  private byte personalityLock[] = new byte[1];

  /** Our NetPersonality, useful if we want to send messages !
   */
  private NetPersonality personality;

 /*------------------------------------------------------------------------------------*/
  
  /** Our current player
   */
  private ProfileConfig currentProfileConfig;

 /*------------------------------------------------------------------------------------*/
  
  /** Constructor.
   */
  private DataManager() {
    ;
  }

 /*------------------------------------------------------------------------------------*/

  /** Creates a new DataManager.
   *
   * @return the created (or previously created) data manager.
   */
  public static DataManager createDataManager() {
    if (dataManager == null)
      dataManager = new DataManager();
    return dataManager;
  }

 /*------------------------------------------------------------------------------------*/
  
  /** To get the default data manager.
   *
   * @return the default data manager.
   */
  public static DataManager getDefaultDataManager() {
    return dataManager;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the world manager.
   *
   * @return the world manager.
   */
  public WorldManager getWorldManager() {
    return worldManager;
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the current profileConfig.
   */
  public void setCurrentProfileConfig(ProfileConfig currentProfileConfig) {
    this.currentProfileConfig = currentProfileConfig;
  }
  
  /** To get the current profileConfig.
   */
  public ProfileConfig getCurrentProfileConfig() {
    return currentProfileConfig;
  }

 /*------------------------------------------------------------------------------------*/

  /** This method is called when a new network connection is created
   *
   * @param personality the NetPersonality object associated to this connection.
   */
  public void connectionCreated( NetPersonality personality )
  {
    synchronized( personalityLock ) {
      this.personality = personality;
    }        
    
    if (currentProfileConfig.getLocalClientID() == -1) {
      Debug.signal( Debug.NOTICE, null, "no valid key found => request a new account to AccountServer");
      Debug.signal( Debug.NOTICE, null, "sending login & password");
      
      personality.queueMessage( new PasswordAndLoginMessage( currentProfileConfig.getLogin(),
              currentProfileConfig.getPassword() ) );
     
      personality.setContext(this);

      try {
        wait( 2000 );
      } catch(Exception e) {
        ; // Do nothing
      }

      if (personality==null) {
        Debug.signal( Debug.ERROR, this, "Connection closed by AccountServer" );      
        return;
      }

      Debug.signal( Debug.NOTICE, null, "OK, now we create a new account" );     
      personality.queueMessage( new AccountCreationMessage() );

      try {
        wait();
      } catch(Exception e){
        ; // Do nothing
      }
          
      return;
      
    } else {
      // The key is valid, we are connected to the GameServer
    }
    
    Debug.signal( Debug.NOTICE, null, "client.DataManager connected to GameServer" );
    
    System.out.println("Connection opened");
  }
  
 /*------------------------------------------------------------------------------------*/

  /** This method is called when the network connection of the client is closed   
   *
   * @param personality the NetPersonality object associated to this connection.
   */
  public void connectionClosed( NetPersonality personality ) {        
    synchronized( personalityLock ) {
      this.personality = null;
    }
    
    System.out.println("Connection closed");
  }  
  
 /*------------------------------------------------------------------------------------*/

  /** To close the network connection if any.
   */
  public void closeConnection() {
    synchronized( personalityLock ) {
      if ( personality!=null )
        personality.closeConnection();
    }
  }
  
 /*------------------------------------------------------------------------------------*/

  /** To set the ID of currentProfileConfig
   */
  public void setCurrentProfileConfigID(int clientID, int serverID) {    
    currentProfileConfig.setLocalClientID(clientID);
    currentProfileConfig.setOriginalServerID(serverID);
    closeConnection();        
    ClientManager.getDefaultClientManager().start(11);
  }    
}
