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

import wotlas.libs.sound.*;

/** ClientConfiguration contains the configuration and options of the client
 *
 * @author Petrus
 * @see wotlas.client.PersistenceManager
 */

public class ClientConfiguration
{

 /*------------------------------------------------------------------------------------*/

  /** music volume.
   */
  private short musicVolume = SoundLibrary.MAX_MUSIC_VOLUME/3;

  /** sound volume.
   */
  private short soundVolume = SoundLibrary.MAX_SOUND_VOLUME*2/3;

  /** true if no music.
   */
  private boolean noMusic = false;
  
  /** true if no sounds.
   */
  private boolean noSound = false;
  
  /** true if high details.
   */
  private boolean highDetails = false;
  
  /** client screen width.
   */
  private int clientWidth = 810;
  
  /** client screeen height.
   */
  private int clientHeight = 610;
  
 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor for persitence.
   * Data is loaded by the PersistenceManager.
   */
  public ClientConfiguration() {
    ;
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
  
}