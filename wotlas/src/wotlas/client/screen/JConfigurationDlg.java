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

package wotlas.client.screen;

import wotlas.client.*;

import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.persistence.*;
import wotlas.libs.sound.*;

import wotlas.utils.aswing.*;
import wotlas.utils.Debug;
import wotlas.utils.SwingTools;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

public class JConfigurationDlg extends JDialog
{

  protected JSlider soundVolLevel, musicVolLevel;
  
  /** To get the music volume.
   */
  public short getMusicVolume() {
    return SoundLibrary.getSoundLibrary().getMusicVolume();
  }
  
  /** To set the music volume.
   */
  public void setMusicVolume(short musicVolume) {
    if ((musicVolume>-1) && (musicVolume<SoundLibrary.MAX_MUSIC_VOLUME)) {
      musicVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_MUSIC_VOLUME, musicVolume);
    } else {
      musicVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_MUSIC_VOLUME, SoundLibrary.MAX_MUSIC_VOLUME/2);
    }
  }
  
  /** To get the sound volume.
   */
  public short getSoundVolume() {
    return SoundLibrary.getSoundLibrary().getSoundVolume();
  }
  
  /** To set the sound volume.
   */
  public void setSoundVolume(short soundVolume) {    
    if ((soundVolume>-1) && (soundVolume<SoundLibrary.MAX_SOUND_VOLUME)) {
      soundVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_SOUND_VOLUME, soundVolume);
    } else {
      soundVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_SOUND_VOLUME, SoundLibrary.MAX_SOUND_VOLUME);
    }
  } 
  
    
 /*------------------------------------------------------------------------------------*/


  /** Constructor.
   */
  public JConfigurationDlg(JFrame frame) {
    super(frame, "Options", true);
    setSize(500,400);
    
    setMusicVolume((short) 60);
    setSoundVolume((short) 30);
    
    JPanel pane = (JPanel) getContentPane();
    
// JDialog properties
    pane.setLayout(new BorderLayout());
    pane.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
      
    getContentPane().setBackground(Color.white);
    //setBackground(Color.black);
         
// We load the images
    ImageIcon im_okup = new ImageIcon("../base/gui/ok-up.gif");
    ImageIcon im_okdo = new ImageIcon("../base/gui/ok-do.gif");

// JPanel Graphics
    JGraphicsTab graphicsTab = new JGraphicsTab();
    JVolumeTab volumeTab = new JVolumeTab();
    
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab( "Graphics configuration",
                       null, 
                       graphicsTab,   
                       "" ); //tooltip text
    tabbedPane.addTab( "Volume configuration",
                       null, 
                       volumeTab,   
                       "" ); //tooltip text
    getContentPane().add(tabbedPane, BorderLayout.CENTER);
    
// OK Button
    JButton b_ok = new JButton(im_okup);
    b_ok.setRolloverIcon(im_okdo);
    b_ok.setPressedIcon(im_okdo);
    b_ok.setBorderPainted(false);
    b_ok.setContentAreaFilled(false);
    b_ok.setFocusPainted(false);
    b_ok.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSoundVolume((short) soundVolLevel.getValue());
        clientConfig.setMusicVolume((short) musicVolLevel.getValue());
        
        try {
          PropertiesConverter.save(SoundLibrary.getSoundLibrary(), "../src/config/clientOptions.cfg");
        } catch (PersistenceException pe) {
          Debug.signal( Debug.ERROR, this, "Failed to save sound & music configuration : " + pe.getMessage() );
        }
        dispose();
      }
    });
    getContentPane().add(b_ok, BorderLayout.SOUTH);

// Display
    SwingTools.centerComponent(this);
    show();
    
  }

 /*------------------------------------------------------------------------------------*/

  /** Graphics Tab COnfiguration.
   */
  private class JGraphicsTab extends JPanel implements ItemListener {        
    /** Constructor.
     */
    public JGraphicsTab() {
      super();
      setBackground(Color.white);
      setAlignmentX(Component.CENTER_ALIGNMENT);
      setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

      ALabel qTextTitle = new ALabel("Graphics");
      add(qTextTitle);

      JCheckBox cButton = new JCheckBox();
      cButton.setBackground(Color.white);
      cButton.setSelected(false);
      cButton.addItemListener(this);
      add(cButton);
    }
    
    /** Invoked when check box state is changed
     */
    public void itemStateChanged(ItemEvent e) {
      Object source = e.getItemSelectable();
      TextDrawable.setHighQualityTextDisplay( (e.getStateChange() == ItemEvent.SELECTED) );
    }
  }

 /*------------------------------------------------------------------------------------*/
  
  /** Volume Tab Configuration.
   */
  private class JVolumeTab extends JPanel implements ChangeListener {
    
    
    /** Constructor.
     */
    public JVolumeTab() {
      super();      
      setBackground(Color.white);
      setAlignmentX(Component.CENTER_ALIGNMENT);
      setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
      
      ImageIcon minVolIcon = new ImageIcon("../base/gui/volume16.gif");
      ImageIcon maxVolIcon = new ImageIcon("../base/gui/volume24.gif");
      
      
      JPanel innerPanel = new JPanel(new GridLayout(2,2,10,10));
      innerPanel.setBackground(Color.white);           
      add(innerPanel, BorderLayout.WEST);
      
      ALabel lbl_sound = new ALabel("Sound volum");
      innerPanel.add(lbl_sound);
      
      JPanel soundPanel = new JPanel();
        soundPanel.setBackground(Color.white);
        JLabel volMin = new JLabel(minVolIcon);
        volMin.setAlignmentY(Component.CENTER_ALIGNMENT);
        soundPanel.add(volMin);  

        
        SoundLibrary.getSoundLibrary().changeMusicVolume((short) soundVolLevel.getValue());
        //soundVolLevel.setPaintTrack(false);
        soundVolLevel.setOpaque(false);
        soundVolLevel.setPreferredSize(new Dimension(50,30));
        //soundVolLevel.addChangeListener(this);
        soundVolLevel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        soundPanel.add(soundVolLevel);

        JLabel volMax = new JLabel(maxVolIcon);
        volMax.setAlignmentY(Component.CENTER_ALIGNMENT);
        soundPanel.add(volMax);
      innerPanel.add(soundPanel);
      
      ALabel lbl_music = new ALabel("Music volum");
      innerPanel.add(lbl_music);
        
      JPanel musicPanel = new JPanel();
        musicPanel.setBackground(Color.white);
        musicPanel.add(new JLabel(minVolIcon));
        
        musicVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_MUSIC_VOLUME, 30);
        SoundLibrary.getSoundLibrary().changeMusicVolume((short) musicVolLevel.getValue());
        //musicVolLevel.setPaintTrack(false);
        musicVolLevel.setOpaque(false);
        musicVolLevel.setPreferredSize(new Dimension(50,30));
        musicVolLevel.addChangeListener(this);
        musicVolLevel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        musicPanel.add(musicVolLevel);
        
        musicPanel.add(new JLabel(maxVolIcon));
      innerPanel.add(musicPanel);
        
        

    }
  
    /** Invoked when volume is changed.
     */
    public void stateChanged(ChangeEvent e) {
      SoundLibrary.getSoundLibrary().changeMusicVolume((short) musicVolLevel.getValue());       
    }
    
  }

 /*------------------------------------------------------------------------------------*/

    
}
  