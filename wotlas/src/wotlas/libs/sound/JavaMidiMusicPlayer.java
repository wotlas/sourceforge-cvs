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
import wotlas.utils.Tools;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.Properties;

/** A Music Player that reads Midi musics files via the JAVA Sound API.
 *
 * @author Aldiss
 */

public class JavaMidiMusicPlayer implements MusicPlayer, MetaEventListener, ControllerEventListener {

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Our Resource Locator
   */
    private MusicResourceLocator resourceLocator;

  /** Tells if a sound device is present
   */
    private boolean noMusicDevice;

  /** No music option
   */
    private boolean noMusicState;

  /** Current Music Volume
   */
    private short musicVolume;

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Midi Sequencer.
   */
    private Sequencer sequencer;

  /** Midi Synthesizer.
   */
    private Synthesizer synthesizer;

  /** Midi channels.
   */
    private MidiChannel channels[];

  /** Current Music.
   */
    private BufferedInputStream currentMusic;

  /** Current Music playing. Null if none...
   */
    private String currentMusicName;

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Lock to avoid events collision
   */
    private byte lockMeta[] = new byte[0];

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor.
   */
    public JavaMidiMusicPlayer() {
    }
  
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init the music player. The resource locator can be used to get a stream on
   *  a music file.
   * @param props properties for init.
   * @param resourceLocator to locate music resources.
   */
    public void init( Properties props, MusicResourceLocator resourceLocator ) {
        this.resourceLocator = resourceLocator;
        musicVolume = 100;
        noMusicState = false;
        noMusicDevice = false;

     // We open the Sound Device
        openDevice();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Closes this music player.
   */
    public void close() {
       if(sequencer!=null)
          sequencer.close();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To play a music.
   * @param musicName music file name in the music database.
   *        we'll search the file via the resourceLocator.
   */
    public void playMusic( String musicName ) {
        if(noMusicDevice || noMusicState || sequencer==null )
          return;

        synchronized( lockMeta ) {

           if(currentMusicName!=null && currentMusicName.equals(musicName) )
              return; // the music is already playing.
           else
              currentMusicName=musicName;

           if(currentMusic!=null) {
              sequencer.stop();
              Tools.waitTime(100);
           }

           if ( !loadMidiMusic( resourceLocator.getMusicStream( musicName ) ) ) {
                Debug.signal( Debug.ERROR, this, "Failed to load music "+musicName);
                return;
           }

           setMusicVolume(getMusicVolume());  // Music Volume
           sequencer.start();
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Stops the current music.
   */
    public void stopMusic() {
       if (noMusicDevice || sequencer==null)
          return;
 
       synchronized( lockMeta ) {
          sequencer.stop();
          currentMusicName=null;
          currentMusic=null;
       }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Resume the current music or restarts the current music.
   */
    public void resumeMusic() {
        if(noMusicDevice || noMusicState || sequencer==null || currentMusic==null)
           return;

        synchronized( lockMeta ) {
          sequencer.start();
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the music volume in [0, 100].
   *  @return the music volume.
   */
    public short getMusicVolume() {
       return musicVolume;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the music volume.
   * @param volume new volume in [0,100]
   */
    public void setMusicVolume(short musicVolume) {
        if( musicVolume>100 )
            musicVolume = 100;

        if( musicVolume<0 )
            musicVolume = 0;

        this.musicVolume = musicVolume;

        synchronized( lockMeta ) {

        // We change the volume for the currently used channels
           if(channels==null)
              return;

           int value = (int) ( ( ((double)musicVolume) / 100.0) * 127.0 );
           if(value<0)  value=0;
           if(value>127) value=127;

           for ( int i = 0; i < channels.length; i++ )
                channels[i].controlChange( 7, value );

           noMusicState = (value==0);
       }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Tells if we want the player to play music or just ignore music 'play' requests.
   * @return true if we must ignore music play requests
   */
    public boolean getNoMusicState() {
       return noMusicState;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set/unset the "No Music" state option.
   * @param noMusicState true if requests to play music must be ignored, false to play music
   *        when asked to.
   */
    public void setNoMusicState( boolean noMusicState ) {
        this.noMusicState = noMusicState;

        synchronized( lockMeta ) {

          if(noMusicState)
             stopMusic();
          else 
             resumeMusic();
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the name of this music player.
   */
    public String getMusicPlayerName() {
        return "Java Sound Midi Player";
    }
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Opens the virtual Midi sound device.
   */
    protected void openDevice() {

       try {
          sequencer = MidiSystem.getSequencer();

          if(sequencer == null) {
             Debug.signal( Debug.WARNING, null, "no valid MIDI sequencers");
             noMusicDevice = true;
             return;
          }
          else {
            if(sequencer instanceof Synthesizer) {
               synthesizer = (Synthesizer)sequencer;
               channels = synthesizer.getChannels();
            }

            sequencer.open();
            sequencer.addMetaEventListener(this);
            int[] controllers = {7};
            sequencer.addControllerEventListener(this, controllers);
          }

          noMusicDevice = false;
       }
       catch (Exception ex) {
         // Failed to init Sequencer
            Debug.signal(Debug.ERROR, this, "Failed to open Sound Device..."+ex );
            noMusicDevice = true;
       }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load a Midi Music
   * @param musicStream music stream to use to load the music file
   * @return true if successful, false if loading failed.
   */
    protected boolean loadMidiMusic( InputStream musicStream ) {

       if( musicStream==null )
           return false;

       Clip clip = null;
       currentMusic = new BufferedInputStream(musicStream, 4096 );

       try {
          sequencer.setSequence(currentMusic);
       }
       catch (InvalidMidiDataException imde) { 
          Debug.signal( Debug.ERROR, this, "Unsupported audio file : "+imde);
          return false;
       }
       catch (Exception ex) { 
          Debug.signal( Debug.ERROR, this, "Failed to read music : "+ex);
          return false;
       }

       return true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Midi Events intercepted. We use it for automatic music loopback.
   */
    public void meta( MetaMessage message ) {

        if(noMusicDevice || sequencer==null || noMusicState || currentMusic==null)
           return;

        if (message.getType() == 47) {  // 47 means end of track      
           if (currentMusic!=null) {
              synchronized( lockMeta ) {
                 sequencer.stop();
                 Tools.waitTime(100);
                 sequencer.start();
              }
           }
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Control Change Events intercepted. We use it for automatic adjust volume.
   */
    public void controlChange( ShortMessage message ) {
      synchronized( lockMeta ) {
      	 if(currentMusic==null)
      	    return;

         if(message.getCommand()!=176 ) return;

         channels[message.getChannel()].controlChange(7, (int) ( ( ((double)musicVolume) / 100.0) * 127.0 ) );
      }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
