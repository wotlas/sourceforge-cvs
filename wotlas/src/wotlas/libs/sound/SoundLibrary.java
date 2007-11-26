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
import wotlas.utils.Debug;

/** A Sound Library that enables you to play musics (via a Music Player)
 *  and sounds (via a SoundPlayer). The music & sounds players are created at
 *  start-up.
 *
 * @author Aldiss, Petrus
 */

public class SoundLibrary {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Our Music Player
     */
    static private MusicPlayer musicPlayer;

    /** Our Sound Player
     */
    static private SoundPlayer soundPlayer;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To create this SoundLibrary. We just create the MusicPlayer and SoundPlayer.
     *  If they already exist we do nothing.
     *
     *  the start-up properties MUST possess a "init.musicPlayerClass" and
     *  "init.soundPlayerClass" indicating the classes to use for the MusicPlayer
     *  and SoundPlayer. This props object is also used to initialize the player.
     *
     * @param props start-up properties
     * @param mrLocator music resource locator, if null we don't create any MusicPlayer
     * @param srLocator sound resource locator, if null we don't create any SoundPlayer
     */
    static public void createSoundLibrary(Properties props, MusicResourceLocator mrLocator, SoundResourceLocator srLocator) {

        String sClass = props.getProperty("init.soundPlayerClass", "");
        String mClass = props.getProperty("init.musicPlayerClass", "");

        if (sClass != null && SoundLibrary.soundPlayer == null && srLocator != null)
            try {
                Class plClass = Class.forName(sClass);
                SoundLibrary.soundPlayer = (SoundPlayer) plClass.newInstance();
                SoundLibrary.soundPlayer.init(props, srLocator);
                Debug.signal(Debug.NOTICE, null, "Sound Player       : " + SoundLibrary.soundPlayer.getSoundPlayerName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        if (mClass != null && SoundLibrary.musicPlayer == null && mrLocator != null)
            try {
                Class plClass = Class.forName(mClass);
                SoundLibrary.musicPlayer = (MusicPlayer) plClass.newInstance();
                SoundLibrary.musicPlayer.init(props, mrLocator);
                Debug.signal(Debug.NOTICE, null, "Music Player       : " + SoundLibrary.musicPlayer.getMusicPlayerName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To close and erase the opened players.
     */
    static public void clear() {
        if (SoundLibrary.musicPlayer != null)
            SoundLibrary.musicPlayer.close();

        if (SoundLibrary.soundPlayer != null)
            SoundLibrary.soundPlayer.close();

        SoundLibrary.musicPlayer = null;
        SoundLibrary.soundPlayer = null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Music Player
     * @return music player, null if none
     */
    static public MusicPlayer getMusicPlayer() {
        if (SoundLibrary.musicPlayer == null)
            return new NullMusicPlayer();

        return SoundLibrary.musicPlayer;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Sound Player
     * @return sound player, null if none
     */
    static public SoundPlayer getSoundPlayer() {
        if (SoundLibrary.soundPlayer == null)
            return new NullSoundPlayer();

        return SoundLibrary.soundPlayer;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
