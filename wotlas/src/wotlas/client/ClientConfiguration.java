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

import wotlas.libs.sound.*;
import wotlas.libs.persistence.*;
import wotlas.utils.Debug;

import java.io.*;


/** ClientConfiguration contains the configuration and options of the client
 *
 * @author Petrus
 * @see wotlas.client.PersistenceManager
 */

public class ClientConfiguration {

 /*------------------------------------------------------------------------------------*/

  /** The file name in which we store the client options.
   */
   public static final String CLIENT_CONFIG_FILENAME = "client-options.cfg";

 /*------------------------------------------------------------------------------------*/

  /** music volume.
   */
  private short musicVolume = SoundLibrary.MAX_MUSIC_VOLUME/2;

  /** sound volume.
   */
  private short soundVolume = SoundLibrary.MAX_SOUND_VOLUME;

  /** true if no music.
   */
  private boolean noMusic = false;
  
  /** true if no sounds.
   */
  private boolean noSound = false;
  
  /** true if high details.
   */
  private boolean highDetails = false;

  /** Remember passwords
   */
  private boolean rememberPasswords = true;
  
  /** client screen width.
   */
  private int clientWidth = 800;
  
  /** client screeen height.
   */
  private int clientHeight = 600;

  /** To tell the WindowPolicy we want the GraphicsDirector to use.
   */
  private boolean centerScreenPolicy = false;

  /** To tell if we want to create a GraphicsDirector that uses hardware acceleration.
   */
  private boolean useHardwareAcceleration = false;

 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor for persitence.
   * Data is loaded by the PersistenceManager.
   */
  public ClientConfiguration() {
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the music volume.
   */
  public short getMusicVolume() {
    return musicVolume;
  }

  /** To set the music volume.
   */
  public void setMusicVolume(short musicVolume) {
    this.musicVolume = musicVolume;
  }

 /*------------------------------------------------------------------------------------*/
  
  /** To get the sound volume.
   */
  public short getSoundVolume() {
    return soundVolume;
  }

  /** To set the sound volume.
   */
  public void setSoundVolume(short soundVolume) {
    this.soundVolume = soundVolume;
  }
  
 /*------------------------------------------------------------------------------------*/

  /** Getter of noMusic.
   */
  public boolean getNoMusic() {
    return noMusic;
  }
  
  /** Setter of noMusic.
   */
  public void setNoMusic(boolean value) {
    this.noMusic = value;
  }
  
 /*------------------------------------------------------------------------------------*/

  /** Getter of noSound.
   */
  public boolean getNoSound() {
    return noSound;
  }
  
  /** Setter of noSound.
   */
  public void setNoSound(boolean value) {
    this.noSound = value;
  }
  
 /*------------------------------------------------------------------------------------*/
  
  /** Getter of highDetails.
   */
  public boolean getHighDetails() {
    return highDetails;
  }
  
  /** Setter of highDetails.
   */
  public void setHighDetails(boolean value) {
    this.highDetails = value;
  }
  
 /*------------------------------------------------------------------------------------*/ 

  /** Getter of clientWidth.
   */
  public int getClientWidth() {
    return clientWidth;
  }
  
  /** Setter of clientWidth.
   */
  public void setClientWidth(int width) {
    this.clientWidth = width;
  }
  
  /** Getter of clientHeight.
   */
  public int getClientHeight() {
    return clientHeight;
  }
  
  /** Setter of clientHeight.
   */
  public void setClientHeight(int height) {
    this.clientHeight = height;
  }

 /*------------------------------------------------------------------------------------*/ 

  /** Getter of rememberPassword.
   */
  public boolean getRememberPasswords() {
    return rememberPasswords;
  }
  
  /** Setter of rememberPassword.
   */
  public void setRememberPasswords(boolean rememberPasswords) {
    this.rememberPasswords = rememberPasswords;
  }

 /*------------------------------------------------------------------------------------*/ 

  /** Getter of centerScreenPolicy.
   */
  public boolean getCenterScreenPolicy() {
    return centerScreenPolicy;
  }
  
  /** Setter of CenterScreenPolicy.
   */
  public void setCenterScreenPolicy(boolean centerScreenPolicy) {
    this.centerScreenPolicy = centerScreenPolicy;
  }

 /*------------------------------------------------------------------------------------*/ 

  /** Getter of useHardwareAcceleration.
   */
  public boolean getUseHardwareAcceleration() {
    return useHardwareAcceleration;
  }
  
  /** Setter of useHardwareAcceleration.
   */
  public void setUseHardwareAcceleration(boolean useHardwareAcceleration) {
    this.useHardwareAcceleration = useHardwareAcceleration;
  }

 /*------------------------------------------------------------------------------------*/ 

  /** To save this client configuration.
   */
     public void save() {
        try{
           PropertiesConverter.save(this, ClientDirector.getResourceManager().getConfig(CLIENT_CONFIG_FILENAME) );
        }
        catch (PersistenceException pe) {
           Debug.signal( Debug.ERROR, null, "Failed to save client configuration : " + pe.getMessage() );
        }
     }

 /*------------------------------------------------------------------------------------*/ 

  /** To load the default client configuration.
   */
     public static ClientConfiguration load() {
       String fileName = ClientDirector.getResourceManager().getConfig(CLIENT_CONFIG_FILENAME);

       try {
         if(new File(fileName).exists())
            return (ClientConfiguration) PropertiesConverter.load(fileName);
         else {
            Debug.signal( Debug.ERROR, null, "Failed to load client configuration. Creating a new one." );
            return new ClientConfiguration();
         }
       }
       catch (PersistenceException pe) {
           Debug.signal( Debug.ERROR, null, "Failed to load client configuration : " + pe.getMessage()+". Creating a new one." );
           return new ClientConfiguration();
       }
    }

 /*------------------------------------------------------------------------------------*/ 

}
