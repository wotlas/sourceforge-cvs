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
  /** music volume
   */
  private short musicVolume;

  /** sound volume
   */
  private short soundVolume;

 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor for persitence.
   * Data is loaded by the PersistenceManager.
   */
  public ClientConfiguration() {
    musicVolume = -1;
    soundVolume = -1;
  }

  /** To get the music volume
   */
  public short getMusicVolume() {
    return musicVolume;
  }

  /** To set the music volume
   */
  public void setMusicVolume(short musicVolume) {
    this.musicVolume = musicVolume;
  }
  
  /** To get the sound volume
   */
  public short getSoundVolume() {
    return soundVolume;
  }

  /** To set the sound volume
   */
  public void setSoundVolume(short soundVolume) {
    this.soundVolume = soundVolume;
  }
  
 /*------------------------------------------------------------------------------------*/

}