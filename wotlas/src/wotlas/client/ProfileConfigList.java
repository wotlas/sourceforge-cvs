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

import wotlas.common.*;
import wotlas.utils.Debug;

import java.io.File;

/** Profile Config List contains the list of the client's profiles:<br>
 * The ProfileConfigList class is saved by the PersistenceManager in config/client-profiles.cfg.
 *
 * @author Petrus
 * @see wotlas.client.ProfileConfig
 */

public class ProfileConfigList {

 /*------------------------------------------------------------------------------------*/

  /** File Name we are stored the client profiles
   */
    public final static String CLIENT_PROFILES = "client-profiles.cfg";

 /*------------------------------------------------------------------------------------*/

  /** List of client's profiles
   */
    private ProfileConfig[] profiles;

  /** Index of the latest used client's profile
   */
    private int currentProfileIndex;

 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor for persistence.
   *  Data is loaded by the PersistenceManager.
   */
  public ProfileConfigList() {
    currentProfileIndex = 0;
    profiles = new ProfileConfig[0];
  }

  /*------------------------------------------------------------------------------------*/

  /** To get the profiles
   */
  public ProfileConfig[] getProfiles() {
    return profiles;
  }

  /** To set the profiles
   */
  public void setProfiles(ProfileConfig[] profiles) {
    this.profiles = profiles;
  }

  /** To get the currentProfileIndex
   */
  public int getCurrentProfileIndex() {
    return currentProfileIndex;
  }

  /** To set currentProfileIndex
   */
  public void setCurrentProfileIndex(int currentProfileIndex) {
    this.currentProfileIndex = currentProfileIndex;
  }

 /*------------------------------------------------------------------------------------*/

  /** If we want to delete passwords to make sure they are not saved to disk.
   */
   public void deletePasswords() {
   	if(profiles==null)
   	   return;

   	for( int i=0; i<profiles.length; i++ )
           if(profiles[i]!=null)
              profiles[i].setPassword(null);
   }

 /*------------------------------------------------------------------------------------*/

  /** Add a new Profile to the array profiles
   */
  public ProfileConfig addProfile()
  {
    ProfileConfig myProfile = new ProfileConfig();

    if (profiles == null || profiles.length==0 ) {
      profiles = new ProfileConfig[1];
      profiles[0] = myProfile;
    } else {
      ProfileConfig[] myProfiles = new ProfileConfig[profiles.length+1];
      System.arraycopy(profiles, 0, myProfiles, 0, profiles.length);
      myProfiles[profiles.length] = myProfile;
      profiles = myProfiles;
    }
    return myProfile;
  }

  /** Add a new Profile to the array <b>profiles</b>
   */
  public void addProfile(ProfileConfig profile) {
    if (profiles==null || profiles.length==0) {
      profiles = new ProfileConfig[1];
      profiles[0] = profile;
    } else {
      ProfileConfig[] myProfiles = new ProfileConfig[profiles.length+1];
      System.arraycopy(profiles, 0, myProfiles, 0, profiles.length);
      myProfiles[profiles.length] = profile;
      profiles = myProfiles;
    }
  }
  
  /** Update a profile or create a new one.
   */
  public void updateProfile(ProfileConfig profile) {
    if (profiles==null || profiles.length==0) {
      profiles = new ProfileConfig[1];
      profiles[0] = profile;
    } else {
      // search for an existing profiles
      for (int i=0; i<profiles.length; i++) {
        if (profiles[i].getKey().equals(profile.getKey())) {          
          profiles[i] = profile;
          return;
        }
      }
      // no existing profile found
      addProfile(profile);
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** Remove a profile of the array profiles
   *
   * @param index of the profile to be removed
   */
   public int removeProfile(int profileIndex) {
      return 0;
   }

  /** Remove a profile of the array profiles
   *
   * @param profileConfig to remove
   * @return true if removed
   */
   public boolean removeProfile(ProfileConfig pf) {
      if(profiles==null)
         return false;
      
      int index=-1;
      
      for( int i=0; i<profiles.length; i++ )
         if(profiles[i]==pf) {
            index=i;
            break;
         }

      if(index==-1)
        return false; // not found

      if(profiles.length==1) {
      	profiles=new ProfileConfig[0]; // no profile remaining...
      	return true;
      }

      ProfileConfig[] myProfiles = new ProfileConfig[profiles.length-1];
      int j=0;

      for( int i=0; i<profiles.length; i++ )
      	if(i!=index) {
           myProfiles[j] = profiles[i];
           j++;
        }

      profiles = myProfiles; // swap
      return true;
   }


 /*------------------------------------------------------------------------------------*/

  /** To get the number of accounts
   */
  public int size() {
    if (profiles==null)
      return 0;
    return profiles.length;
  }

 /*------------------------------------------------------------------------------------*/

  /** Loads all the client's profile config files located in CLIENT_PROFILES
   *
   * @return client's ProfileConfigList
   */
    public static ProfileConfigList load() {

     	ResourceManager rManager = ClientDirector.getResourceManager();
        String fileName = rManager.getExternalConfigsDir()+CLIENT_PROFILES;

        return (ProfileConfigList) rManager.loadObject(fileName);
    }

 /*------------------------------------------------------------------------------------*/

  /** Saves the client's profiles config to "client-profiles.cfg"
   * @return true in case of success, false if an error occured.
   */
    public boolean save() {
       if(!ClientManager.getRememberPasswords())
           ProfileConfig.setPasswordAccess(false);

     	ResourceManager rManager = ClientDirector.getResourceManager();
     	
        if( !rManager.saveObject(this, rManager.getExternalConfigsDir()+CLIENT_PROFILES ) ) {
            Debug.signal( Debug.ERROR, null, "Failed to save client's profiles.");

            if(!ClientManager.getRememberPasswords())
                ProfileConfig.setPasswordAccess(true);

            return false;
        }

        if(!ClientManager.getRememberPasswords())
           ProfileConfig.setPasswordAccess(true);

      return true;
  }

 /*------------------------------------------------------------------------------------*/

}
