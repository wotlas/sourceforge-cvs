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

// TODO : implémenter removeProfile

package wotlas.client;

/** Profile Config List contains the list of the client's profiles:<br>
 * The ProfileConfigList class is saved by the PersistenceManager in config/client-profiles.cfg.
 *  
 * @author Petrus
 * @see wotlas.client.ProfileConfig
 */

public class ProfileConfigList
{

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

  /** Add a new Profile to the array profiles
   */
  public ProfileConfig addProfile()
  {
    ProfileConfig myProfile = new ProfileConfig();
    
    if (profiles == null) {
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
    if (profiles==null) {
      profiles = new ProfileConfig[1];
      profiles[0] = profile;
    } else {
      ProfileConfig[] myProfiles = new ProfileConfig[profiles.length+1];
      System.arraycopy(profiles, 0, myProfiles, 0, profiles.length);
      myProfiles[profiles.length] = profile;
      profiles = myProfiles;
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

 /*------------------------------------------------------------------------------------*/

  /** To get the number of accounts
   */
  public int size() {
    if (profiles==null)
      return 0;
    return profiles.length;
  }
  
 /*------------------------------------------------------------------------------------*/

}
