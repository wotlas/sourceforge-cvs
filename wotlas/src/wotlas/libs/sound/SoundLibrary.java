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

package wotlas.libs.sound;

import wotlas.utils.Debug;

import javax.sound.midi.*;
import javax.sound.sampled.*;

import java.io.*;


/** A very simple Sound Library that enables you to play WAV sounds and midi musics.
 *
 * @author Aldiss
 */

public class SoundLibrary implements MetaEventListener, ControllerEventListener
{
 /*------------------------------------------------------------------------------------*/

   /** Max Volume
    */
      public static final short MAX_VOLUME   = 100;

 /*------------------------------------------------------------------------------------*/

   /** Default SoundLibrary.
    */
      static private SoundLibrary sLibrary;

 /*------------------------------------------------------------------------------------*/

   /** Midi Sequencer.
    */
      private Sequencer sequencer;

   /** Current Music.
    */
      private BufferedInputStream currentMusic;


   /** Midi Synthesizer
    */
      private Synthesizer synthesizer;

   /** Midi channels
    */
      private MidiChannel channels[];

 /*------------------------------------------------------------------------------------*/

    /** Tells if a sound device is present
     */
       private boolean noSoundDevice;

    /** No sound option
     */
       private boolean noSounds;

    /** No music option
     */
       private boolean noMusic;

    /** Current Volume.
     */
       private short volume;

 /*------------------------------------------------------------------------------------*/

    /** DataBase Path...
     */
       private String dataBasePath;

 /*------------------------------------------------------------------------------------*/

    /** To create this SoundLibrary. If there is already one we don't create a new one.
     * @param dataBasePath database location
     * @return default SoundLibrary.
     */
      static public SoundLibrary createSoundLibrary( String dataBasePath ) {
          if( sLibrary != null )
              return sLibrary;
       
          sLibrary = new SoundLibrary( dataBasePath );
          return sLibrary;
      }

 /*------------------------------------------------------------------------------------*/

   /** To get the default SoundLibrary
    * @return null if none, SoundLibrary otherwise
    */
      static public SoundLibrary getSoundLibrary() {
      	  return sLibrary;
      }

 /*------------------------------------------------------------------------------------*/

    /** Constructor.
     * @param dataBasePath database location
     */
      protected SoundLibrary( String dataBasePath ) {
          this.dataBasePath = dataBasePath;

       // We open the Sound Device
          openDevice();
          volume = MAX_VOLUME;
          noSounds = false;
          noMusic = false;
      }

 /*------------------------------------------------------------------------------------*/

    /** Open Sound Device.
     */
      protected void openDevice() {
        try{
            sequencer = MidiSystem.getSequencer();

              if (sequencer == null) {
                Debug.signal( Debug.WARNING, null, "no valid MIDI sequencers");
                noSoundDevice = true;
                return;
              } else {
                if (sequencer instanceof Synthesizer) {
                  synthesizer = (Synthesizer)sequencer;
                  channels = synthesizer.getChannels();
                }              
                sequencer.open();
                sequencer.addMetaEventListener(this);
                int[] controllers = {7};
                sequencer.addControllerEventListener(this, controllers);
             }
        }
        catch (Exception ex) {
          // Failed to init Sequencer
             Debug.signal(Debug.ERROR, this, "Failed to open Sound Device..." );
             noSoundDevice = true;
        }
     }

 /*------------------------------------------------------------------------------------*/

   /** Closes this sound library.
    */
     public void close() {
         if (sequencer != null)
             sequencer.close();
     }

 /*------------------------------------------------------------------------------------*/

   /** To play a sound of the sound library.
    * @param soundName sound file name in the sound database.
    *        we search in the databasePath/sounds.
    */
     public void playSound( String soundName ) {
         if(noSoundDevice || noSounds)
             return;

         Clip sound = loadSound( dataBasePath+File.separator+"sounds"+File.separator+soundName);
         setGain( sound, volume );
         sound.setFramePosition(0);         
         sound.start();
     }

 /*------------------------------------------------------------------------------------*/

   /** To play a music of the music library.
    * @param musicName music file name in the music database.
    *        we search in the databasePath/music.
    */
     public void playMusic( String musicName ) { 
         if(noSoundDevice || noMusic)
             return;

         if(currentMusic!=null)
             sequencer.stop();

         if( !loadMidiMusic( dataBasePath+File.separator+"music"+File.separator+musicName ) ) {
             Debug.signal( Debug.ERROR, this, "Failed to load music "+musicName);
             return;
         }

         int value = (int) ( ( ((double)volume) / 100.0) * 127.0 );

         for (int i = 0; i < channels.length; i++)
                channels[i].controlChange(7, value );

        sequencer.start();
     }

 /*------------------------------------------------------------------------------------*/

    /** To load a Sound Clip. We only read Wave PCM & Wave ALAW/ULAW.
     * @param soudPath complete path to the sound file
     * @return Sound Clip
     */
     protected Clip loadSound( String soundPath )
     {
        File f = new File(soundPath);
    	AudioInputStream stream = null;
        Clip clip = null;
    	
        try {
              stream = AudioSystem.getAudioInputStream(f);
        }
        catch(Exception e1) {
            Debug.signal( Debug.ERROR, this, "Failed to load sound "+soundPath+": "+e1);
            return null;
        }

        try
        {
             AudioFormat format = stream.getFormat();

           /**
            * we can't yet open the device for ALAW/ULAW playback,
            * convert ALAW/ULAW to PCM
            */
             if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
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
           Debug.signal( Debug.ERROR, this, "Failed to read sound "+soundPath+": "+ex);
           return null;
        }

      return clip;
    }

 /*------------------------------------------------------------------------------------*/

   /** To load a Midi Music
    * @param musicPath complete music path
    * @return true if successful, false if loading failed.
    */
     protected boolean loadMidiMusic( String musicPath )
     {
        File f = new File(musicPath);
        Clip clip = null;

           try{ 
                FileInputStream is = new FileInputStream(f);
                currentMusic = new BufferedInputStream(is, 1024);
           }
           catch (Exception e) { 
                Debug.signal( Debug.ERROR, this, "Failed to load music "+musicPath+": "+e);
                return false;
           }

           try{
                //sequencer.open();                
                sequencer.setSequence(currentMusic);
           }
           catch (InvalidMidiDataException imde) { 
                Debug.signal( Debug.ERROR, this, "Unsupported audio file "+musicPath+": "+imde);
		return false;
           }
           catch (Exception ex) { 
                Debug.signal( Debug.ERROR, this, "Failed to read music "+musicPath+": "+ex);
                return false;
	   }

        return true;
     }

 /*------------------------------------------------------------------------------------*/

   /** To set/unset the "No Music" option.
    * @param noMusic true for no music, false for music...
    */
     public void setNoMusic( boolean noMusic ) {
        this.noMusic = noMusic;
     	if( noMusic )
     	    stopMusic();
     }

 /*------------------------------------------------------------------------------------*/

   /** To set/unset the "No Sound" option.
    * @param noSound true for no sounds, false for sounds...
    */
     public void setNoSounds( boolean noSounds ) {
        this.noSounds = noSounds;
     }

 /*------------------------------------------------------------------------------------*/

   /** To set the volume.
    * @param volume new volume [0,MAX_VOLUME]
    */
     public void setVolume( short volume ) {
        this.volume = volume;

      // We change the volume for the current used channels
        if(channels==null)
           return;

        int value = (int) ( ( ((double)volume) / 100.0) * 127.0 );

        for (int i = 0; i < channels.length; i++)
             channels[i].controlChange(7, value );
     }

 /*------------------------------------------------------------------------------------*/

   /** Stops the current music.
    */
     public void stopMusic() { 
        if(noSoundDevice || sequencer==null)
             return;
 
        sequencer.stop();
        currentMusic=null;
     }

 /*------------------------------------------------------------------------------------*/

  /** To set the gain for sounds. The volume range is [0..100].
   * @param clip clip to adjust.
   * @param volume volume to set.
   */
    protected void setGain( Clip clip, int volume ) {
        double value = volume / 100.0;

         try
         {
            FloatControl gainControl = (FloatControl) clip.getControl( FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(value==0.0?0.0001:value)/Math.log(10.0)*20.0);
            gainControl.setValue(dB); 
         }
         catch (Exception ex) {
            Debug.signal( Debug.WARNING, this, "Failed to change sound volume :"+ex);
         }
    }

 /*------------------------------------------------------------------------------------*/

  /** Midi Events intercepted. We use it for automatic music loopback.
   */
    public void meta( MetaMessage message ) {
        if (message.getType() == 47) {  // 47 means end of track
            if(noSoundDevice || sequencer==null)
               return;

            if(currentMusic!=null) {              
               //sequencer.stop();
               sequencer.start();
            }
        }       
    }

 /** Control Change Events intercepted. We use it for automatic adjust volume.
   */
    public void controlChange( ShortMessage message ) {
       
          //System.out.println("ShortMessage type = " + message.getCommand());
          
        channels[message.getChannel()].controlChange(7, (int) ( ( ((double)volume) / 100.0) * 127.0 ) );
        
    }

 /*------------------------------------------------------------------------------------*/
 
}
