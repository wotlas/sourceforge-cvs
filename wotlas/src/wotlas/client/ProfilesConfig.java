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

/** ProfilesConfig contains the list of the client's profiles:<br>
 * The ProfileConfig class is saved by the PersistenceManager in config/profile.cfg.
 *  
 * @author Petrus
 *
 * @see wotlas.client.ProfileConfig
 */

public class ProfilesConfig
{
  /** List of client's profiles
   */
  private Profile[] profiles;
  
  /** Index of the last client's profile
   */
  private int currentProfileIndex;

  /*------------------------------------------------------------------------------------*/

  /** Empty Constructor for persistence.
   *  Data is loaded by the PersistenceManager.
   */
  public ProfilesConfig() {
    currentProfileIndex = 0;
  }
  
  /*------------------------------------------------------------------------------------*/

  /** To get the profiles
   */
  public Profile[] getProfiles() {
    return profiles;
  }
  
  /** To set the profiles
   */
  public void setProfiles(Profile[] profiles) {
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
  public Profile addProfile()
  {
    Profile myProfile = new Profile();
    
    if (profiles == null) {
      profiles = new Profile[1];
      profiles[0] = myProfile;
    } else {
      Profile[] myProfiles = new Profile[profiles.length+1];
      System.arraycopy(profiles, 0, myProfiles, 0, profiles.length);
      myProfiles[profiles.length] = myProfile;
      profiles = myProfiles;
    }
    return myProfile;
  }
  
  /** Remove a profile of the array profiles
   *
   * @param index of the profile to be removed
   */
  public int removeProfile(int profileIndex)
  {
    return 0;
  }
}
