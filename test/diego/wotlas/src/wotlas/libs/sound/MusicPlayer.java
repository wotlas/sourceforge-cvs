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

package wotlas.libs.sound;

import java.util.Properties;

/** A Music Player for playing music sound files ( Midi, Xm, mp3, etc. ).
 *  Any class implementing this interface must have a constructor with no arguments.
 *
 * @author Aldiss
 */

public interface MusicPlayer {

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** To init the music player. The resource locator can be used to get a stream on
   *  a music file.
   * @param props properties for init.
   * @param resourceLocator to locate music resources.
   */
    public void init( Properties props, MusicResourceLocator resourceLocator );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Closes this music player.
   */
    public void close();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To play a music.
   * @param musicName music file name in the music database.
   *        we'll search the file via the resourceLocator.
   */
    public void playMusic( String musicName );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Stops the current music.
   */
    public void stopMusic();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Resume the current music or restarts the current music.
   */
    public void resumeMusic();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the music volume in [0, 100].
   *  @return the music volume.
   */
    public short getMusicVolume();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the music volume.
   * @param volume new volume in [0,100]
   */
    public void setMusicVolume(short musicVolume);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Tells if we want the player to play music or just ignore music 'play' requests.
   * @return true if we must ignore music play requests
   */
    public boolean getNoMusicState();

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set/unset the "No Music" state option.
   * @param noMusicState true if requests to play music must be ignored, false to play music
   *        when asked to.
   */
    public void setNoMusicState( boolean noMusicState );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the name of this music player.
   */
    public String getMusicPlayerName();
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
