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

import wotlas.libs.persistence.*;

import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.utils.Tools;

 /** Persistence Manager for Wotlas Clients. The persistence manager is the central
  * class where are saved/loaded data for the game. Mainly, it deals with client's profiles
  * and World data ( wotlas.common.universe ).
  *
  * @author Petrus
  * @see wotlas.libs.persistence.PropertiesConverter
  */
  
public class PersistenceManager
{
 /*------------------------------------------------------------------------------------*/

  public final static String CLIENT_PROFILES = "../src/config/client-profiles.cfg";
  public final static String CLIENTCONFIG = "../src/config/client.cfg";

 /*------------------------------------------------------------------------------------*/

  /** Our Default PersistenceManager.
   */
  private static PersistenceManager persistenceManager;

  /** Path to the local server database.
   */
  private String databasePath;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   *
   * @param databasePath path to the local server database
   */  
  private PersistenceManager(String databasePath) {
    this.databasePath = databasePath;
  }

 /*------------------------------------------------------------------------------------*/

  /** Creates a persistence manager.
   *
   * @param databasePath path to the local server database
   * @return the created (or previously created) persistence manager.
   */
  public static PersistenceManager createPersistenceManager( String databasePath ) {
    if (persistenceManager == null)
      persistenceManager = new PersistenceManager(databasePath);
    return persistenceManager;
   }

 /*------------------------------------------------------------------------------------*/

  /** To get the default persistence manager.
   *
   * @return the default persistence manager.
   */
  public static PersistenceManager getDefaultPersistenceManager() {
    return persistenceManager;
  }

 /*------------------------------------------------------------------------------------*/

  /** Loads the client's profiles config located in config/client-profiles.cfg
   *
   * @return the client's profiles config found in CLIENT_PROFILES
   */
  public ProfilesConfig loadProfilesConfig()
  {
    try {
      return (ProfilesConfig) PropertiesConverter.load(CLIENT_PROFILES);
    } catch (PersistenceException pe) {
      Debug.signal( Debug.ERROR, this, "Failed to load client's profiles config: " + pe.getMessage() );
      return null;
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** Saves the client's profiles config to config/client-profiles.cfg
   *
   * @param profilesConfig client's profiles config
   * @return true in case of success, false if an error occured.
   */
  public boolean saveProfilesConfig(ProfilesConfig profilesConfig)
  {
    try {
      PropertiesConverter.save(profilesConfig, CLIENT_PROFILES);
      return true;
    } catch (PersistenceException pe) {
      Debug.signal( Debug.ERROR, this, "Failed to save client's profiles config:" + pe.getMessage() );
      return false;
    }
  }
  
 /*------------------------------------------------------------------------------------*/

  /** Loads the client's profiles located in config/client-profiles.cfg
   *
   * @return all the client's profiles found in CLIENT_PROFILES
   */
  public Profile[] loadProfiles()
  {
    try {
      ProfilesConfig profilesConfig = (ProfilesConfig) PropertiesConverter.load(CLIENT_PROFILES);
      return profilesConfig.getProfiles();
    } catch (PersistenceException pe) {
      Debug.signal( Debug.ERROR, this, "Failed to load client's profiles: " + pe.getMessage() );
      return null;
    }
  }
}
