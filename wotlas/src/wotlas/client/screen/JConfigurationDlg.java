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
import wotlas.utils.*;
import wotlas.utils.SwingTools;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

/** JDialog to configure client options.
 *
 * @author Petrus
 * @see wotlas.client.ClientConfiguration
 */

public class JConfigurationDlg extends JDialog {

 /*------------------------------------------------------------------------------------*/

  protected JSlider soundVolLevel, musicVolLevel;
  protected JCheckBox cButton;
  protected JCheckBox savePassButton;
  protected JCheckBox policyButton;
  protected JCheckBox hardwareButton;

 /*------------------------------------------------------------------------------------*/

  /** To get the music volume.
   */
    public short getMusicVolume() {
        return SoundLibrary.getSoundLibrary().getMusicVolume();
    }
  
  /** To set the music volume.
   */
    public void setMusicVolume(short musicVolume) {
       if ((musicVolume>-1) && (musicVolume<SoundLibrary.MAX_MUSIC_VOLUME))
           musicVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_MUSIC_VOLUME, musicVolume);
       else
           musicVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_MUSIC_VOLUME, SoundLibrary.MAX_MUSIC_VOLUME/2);
    }
  
  /** To get the sound volume.
   */
    public short getSoundVolume() {
       return SoundLibrary.getSoundLibrary().getSoundVolume();
    }
  
  /** To set the sound volume.
   */
    public void setSoundVolume(short soundVolume) {    
      if ((soundVolume>-1) && (soundVolume<=SoundLibrary.MAX_SOUND_VOLUME))
         soundVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_SOUND_VOLUME, soundVolume);
      else
         soundVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_SOUND_VOLUME, SoundLibrary.MAX_SOUND_VOLUME/2);
    }
    
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public JConfigurationDlg(JFrame frame) {
      super(frame, "Options", true);
      setSize(400,300);

      ClientConfiguration clientConfiguration = ClientDirector.getClientConfiguration();

      musicVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_MUSIC_VOLUME,
                                  (short)clientConfiguration.getMusicVolume());

      soundVolLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_SOUND_VOLUME,
                                  (short)clientConfiguration.getSoundVolume());

      cButton = new JCheckBox("High details for player name display.");
      cButton.setSelected(clientConfiguration.getHighDetails());
      TextDrawable.setHighQualityTextDisplay(clientConfiguration.getHighDetails());

      savePassButton = new JCheckBox("Remember passwords (saved to disk).");
      savePassButton.setSelected( clientConfiguration.getRememberPasswords() );
      ClientManager.setRememberPasswords( clientConfiguration.getRememberPasswords() );

      policyButton = new JCheckBox("Always center the game screen on the player.");
      policyButton.setSelected( clientConfiguration.getCenterScreenPolicy() );

      hardwareButton = new JCheckBox("Use hardware acceleration for 2D graphics (Java 1.4).");
      hardwareButton.setSelected( clientConfiguration.getUseHardwareAcceleration() );

      if(!Tools.javaVersionHigherThan("1.4.0"))
         hardwareButton.setEnabled(false);

      JPanel pane = (JPanel) getContentPane();
    
   // JDialog properties
      pane.setLayout(new BorderLayout());
      pane.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

      getContentPane().setBackground(Color.white);

   // We load the images
      ImageIcon im_okup = new ImageIcon("../base/gui/ok-up.gif");
      ImageIcon im_okdo = new ImageIcon("../base/gui/ok-do.gif");

   // JPanel Tabs
      JGeneralTab generalTab = new JGeneralTab();
      JVolumeTab volumeTab = new JVolumeTab();
    
      JTabbedPane tabbedPane = new JTabbedPane();
      tabbedPane.addTab( "General configuration",
                       null, 
                       generalTab,   
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

   // Save the configuration
     b_ok.addActionListener(new ActionListener() {
       public void actionPerformed (ActionEvent e) {

        ClientConfiguration clientConfiguration = ClientDirector.getClientConfiguration();

        clientConfiguration.setMusicVolume((short) musicVolLevel.getValue());
        clientConfiguration.setSoundVolume((short) soundVolLevel.getValue());
        SoundLibrary.getSoundLibrary().setSoundVolume((short) soundVolLevel.getValue());

        clientConfiguration.setNoMusic((musicVolLevel.getValue()==0));
        SoundLibrary.getSoundLibrary().setNoMusic(clientConfiguration.getNoMusic());

        clientConfiguration.setNoSound((soundVolLevel.getValue()==0));
        SoundLibrary.getSoundLibrary().setNoSound(clientConfiguration.getNoSound());

        clientConfiguration.setHighDetails(cButton.isSelected());
        clientConfiguration.setRememberPasswords(savePassButton.isSelected());

        if( clientConfiguration.getCenterScreenPolicy()!=policyButton.isSelected() 
            || clientConfiguration.getUseHardwareAcceleration()!=hardwareButton.isSelected() )
            JOptionPane.showMessageDialog( null, "You've modified the behaviour of the 2D graphics engine.\n"
                                                +"If you are connected to the game you'll need to reconnect\n"
                                                +"to let the changes apply.",
                                                 "Information", JOptionPane.INFORMATION_MESSAGE);

        clientConfiguration.setCenterScreenPolicy(policyButton.isSelected());
        clientConfiguration.setUseHardwareAcceleration(hardwareButton.isSelected());

        ClientDirector.saveClientConfiguration();
        dispose();
      }
    });

    getContentPane().add(b_ok, BorderLayout.SOUTH);

   // Display
    SwingTools.centerComponent(this);
    show();
  }

 /*------------------------------------------------------------------------------------*/

  /** Graphics Tab Configuration.
   */
  private class JGeneralTab extends JPanel implements ItemListener {        
    /** Constructor.
     */
    public JGeneralTab() {
      super();
      setBackground(Color.white);
      setLayout(new FlowLayout(FlowLayout.LEFT));
      //setAlignmentX(Component.CENTER_ALIGNMENT);
      setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

      /*ALabel qTextTitle = new ALabel("Graphics");
      qTextTitle.setForeground(Color.white);
      add(qTextTitle, BorderLayout.NORTH);*/

      JPanel innerPanel = new JPanel(new GridLayout(4,1,10,10));
      innerPanel.setBackground(Color.white);
      add(innerPanel);
      
//      ALabel lbl_details = new ALabel("High details for text details.");
//      innerPanel.add(lbl_details);
       
      cButton.setBackground(Color.white);      
      cButton.addItemListener(this);
      innerPanel.add(cButton);

//      ALabel lbl_passw = new ALabel("Remember Passwords");
//      innerPanel.add(lbl_passw);

      savePassButton.setBackground(Color.white);      
      savePassButton.addItemListener(this);
      innerPanel.add(savePassButton);

      policyButton.setBackground(Color.white);      
      innerPanel.add(policyButton);

      hardwareButton.setBackground(Color.white);      
      innerPanel.add(hardwareButton);
    }
    
    /** Invoked when check box state is changed
     */
    public void itemStateChanged(ItemEvent e) {
      Object source = e.getItemSelectable();

      if(cButton==source)
          TextDrawable.setHighQualityTextDisplay( (e.getStateChange() == ItemEvent.SELECTED) );
      else if(savePassButton==source)
          ClientManager.setRememberPasswords( (e.getStateChange() == ItemEvent.SELECTED) );
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
      setLayout(new FlowLayout(FlowLayout.LEFT));
      //setAlignmentX(Component.CENTER_ALIGNMENT);
      setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
      
      ImageIcon minVolIcon = new ImageIcon("../base/gui/volume16.gif");
      ImageIcon maxVolIcon = new ImageIcon("../base/gui/volume24.gif");
      
      /*ALabel lbl_title = new ALabel("Sound");      
      lbl_title.setForeground(Color.white);
      add(lbl_title, BorderLayout.NORTH);*/
      
      JPanel innerPanel = new JPanel(new GridLayout(2,2,10,10));
      innerPanel.setBackground(Color.white);           
      add(innerPanel);
      
      ALabel lbl_sound = new ALabel("Sound Volume");
      lbl_sound.setAlignmentY(Component.CENTER_ALIGNMENT);
      innerPanel.add(lbl_sound);
      
      JPanel soundPanel = new JPanel();
      //soundPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        soundPanel.setBackground(Color.white);
        JLabel volMin = new JLabel(minVolIcon);
        volMin.setAlignmentY(Component.CENTER_ALIGNMENT);
        soundPanel.add(volMin);  

        //soundVolLevel.setPaintTrack(false);
        soundVolLevel.setOpaque(false);
        soundVolLevel.setPreferredSize(new Dimension(100,30));
        //soundVolLevel.addChangeListener(this);
        soundVolLevel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        soundPanel.add(soundVolLevel);

        JLabel volMax = new JLabel(maxVolIcon);
        volMax.setAlignmentY(Component.CENTER_ALIGNMENT);
        soundPanel.add(volMax);
      innerPanel.add(soundPanel);
      
      ALabel lbl_music = new ALabel("Music Volume");
      lbl_music.setAlignmentY(Component.CENTER_ALIGNMENT);
      innerPanel.add(lbl_music);
        
      JPanel musicPanel = new JPanel();
      
        musicPanel.setBackground(Color.white);
        musicPanel.add(new JLabel(minVolIcon));
               
        //musicVolLevel.setPaintTrack(false);
        musicVolLevel.setOpaque(false);
        musicVolLevel.setPreferredSize(new Dimension(100,30));
        musicVolLevel.addChangeListener(this);
        musicVolLevel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        musicPanel.add(musicVolLevel);
        
        musicPanel.add(new JLabel(maxVolIcon));
        innerPanel.add(musicPanel);
    }
  
    /** Invoked when volume is changed.
     */
    public void stateChanged(ChangeEvent e) {
      SoundLibrary.getSoundLibrary().setMusicVolume((short) musicVolLevel.getValue());
    }
  }

 /*------------------------------------------------------------------------------------*/

}
  