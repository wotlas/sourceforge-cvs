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

import wotlas.utils.Debug;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Properties;

/** A Sound Player for reading short WAV, AU, etc. sound files via the JAVA Sound API.
 *
 * @author Aldiss
 */

public class JavaSoundPlayer implements SoundPlayer {

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Our Resource Locator
   */
    private SoundResourceLocator resourceLocator;

  /** No sound option
   */
    private boolean noSoundState;

  /** Current Sound Volume
   */
    private short soundVolume;

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor.
   */
    public JavaSoundPlayer() {
    }
  
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init the sound player. The resource locator can be used to get a stream on
   *  a sound file.
   * @param props properties for init.
   * @param resourceLocator to locate sound resources.
   */
    public void init( Properties props, SoundResourceLocator resourceLocator ) {
        this.resourceLocator = resourceLocator;
        noSoundState=false;
        soundVolume=100;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Closes this sound player. Does Nothing here.
   */
    public void close() {
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To play a sound.
   * @param soundName sound file name in the sound database.
   *        we'll search the file via the resourceLocator.
   */
    public void playSound( String soundName ) {
        if( noSoundState || soundName==null )
           return;

        Clip sound = loadSound( resourceLocator.getSoundStream( soundName ) );

        if(sound==null)
           return;

        setGain( sound, soundVolume );
        sound.setFramePosition(0);
        sound.start();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** To get the sound volume in [0, 100].
   * @return volume new volume in [0,100]
   */
    public short getSoundVolume() {
        return soundVolume;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the sound volume ( wave sounds ) in the [0,100] range.
   * @return volume new volume in [0,100]
   */
    public void setSoundVolume(short soundVolume) {
    	this.soundVolume = soundVolume;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Tells if we want the player to play sounds or just ignore sounds 'play' requests.
   * @return true if we must ignore sound play requests
   */
    public boolean getNoSoundState() {
        return noSoundState;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set/unset the "No Sound" option.
   * @param noSoundState true if requests to play sounds must be ignored, false to play sounds
   *        when asked to.
   */
    public void setNoSoundState( boolean noSoundState ) {
       this.noSoundState = noSoundState;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the name of this sound player.
   */
    public String getSoundPlayerName() {
       return "Java Sound Player";
    }
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load a Sound Clip. We only read Wave PCM & Wave ALAW/ULAW.
   * @param soudStream stream from the sound clip.
   * @return sound clip
   */
    protected Clip loadSound( InputStream soundStream ) {
       AudioInputStream stream = null;
       Clip clip = null;

       if(soundStream==null)
          return null;
    	
       try {
         stream = AudioSystem.getAudioInputStream( new BufferedInputStream( soundStream, 2048 ) );
       }
       catch(Exception ex) {
         Debug.signal( Debug.ERROR, this, "Failed to load sound : "+ex);
         return null;
       }

       try {
          AudioFormat format = stream.getFormat();

         /**
          * we can't yet open the device for ALAW/ULAW playback,
          * convert ALAW/ULAW to PCM
          */
         if ( (format.getEncoding() == AudioFormat.Encoding.ULAW) ||
              (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
            AudioFormat tmp = new javax.sound.sampled.AudioFormat(
                                        AudioFormat.Encoding.PCM_SIGNED, 
                                        format.getSampleRate(),
                                        format.getSampleSizeInBits() * 2,
                                        format.getChannels(),
                                        format.getFrameSize() * 2,
                                        format.getFrameRate(),
                                        true);
            stream = AudioSystem.getAudioInputStream(tmp, stream);
            format = tmp;
         }

         DataLine.Info info = new DataLine.Info(
                                        javax.sound.sampled.Clip.class, 
                                        stream.getFormat(), 
                                        ((int) stream.getFrameLength() *format.getFrameSize()));

         clip = (Clip) AudioSystem.getLine(info);
         clip.open(stream);
       }
       catch (Exception ex) { 
          Debug.signal( Debug.ERROR, this, "Failed to read sound : "+ex);
          return null;
       }

      return clip;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the gain for sounds. The volume range is [0..100].
   * @param clip clip to adjust.
   * @param volume volume to set.
   */
    protected void setGain( Clip clip, int soundVolume ) {
       double value = soundVolume / 100.0;

       try {
          FloatControl gainControl = (FloatControl) clip.getControl( FloatControl.Type.MASTER_GAIN);
          float dB = (float) (Math.log(value==0.0?0.0001:value)/Math.log(10.0)*20.0);
          gainControl.setValue(dB); 
       }
       catch (Exception ex) {
          Debug.signal( Debug.WARNING, this, "Failed to change sound volume :"+ex);
       }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}
