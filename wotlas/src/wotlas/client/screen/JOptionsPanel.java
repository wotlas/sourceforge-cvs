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

import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.sound.*;
import wotlas.utils.*;
import wotlas.utils.aswing.*;
import wotlas.client.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

/** JPanel to configure the interface
 *
 * @author Petrus
 */

public class JOptionsPanel extends JPanel implements MouseListener//, ChangeListener, ItemListener
{

 /*------------------------------------------------------------------------------------*/

  /** Slider to change volume level.
   */
  private JSlider volumeLevel;

  /** Consctructor.
   */
  public JOptionsPanel() {
    super();
    setBackground(Color.white);
    JPanel innerPanel = new JPanel();
    innerPanel.setOpaque(false);
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
    innerPanel.setBorder(BorderFactory.createEmptyBorder(3,3,5,5)); // all 10s

// Title
    ALabel lbl_title = new ALabel("Menu");
    lbl_title.setAlignmentX(Component.CENTER_ALIGNMENT);
    innerPanel.add(lbl_title);

// Space
    innerPanel.add(new JLabel(" "));

// Options button
    ImageIcon im_optionsup = new ImageIcon("../base/gui/options-up.gif");
    ImageIcon im_optionsdo  = new ImageIcon("../base/gui/options-do.gif");
    JButton b_options = new JButton(im_optionsup);
    b_options.setRolloverIcon(im_optionsdo);
    b_options.setPressedIcon(im_optionsdo);
    b_options.setBorderPainted(false);
    b_options.setContentAreaFilled(false);
    b_options.setFocusPainted(false);
    b_options.setAlignmentX(Component.CENTER_ALIGNMENT);
    b_options.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        new JConfigurationDlg(DataManager.getDefaultDataManager().getClientScreen());
      }
    });
    innerPanel.add(b_options);

// Volume Panel
    /*JPanel vPanel = new JPanel();
    vPanel.setOpaque(false);
    vPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    vPanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));

    ALabel volTitle = new ALabel("Volume : ");
    vPanel.add(volTitle);

    JLabel soundMin = new JLabel(new ImageIcon("../base/gui/volume16.gif"));
    //soundMin.setAlignmentY(Component.CENTER_ALIGNMENT);
    vPanel.add(soundMin);

    volumeLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_MUSIC_VOLUME, 30);
    SoundLibrary.getSoundLibrary().changeMusicVolume((short) volumeLevel.getValue());

    volumeLevel.setPaintTrack(false);
    volumeLevel.setOpaque(false);
    volumeLevel.setPreferredSize(new Dimension(50,30));
    volumeLevel.addChangeListener(this);
    volumeLevel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
    vPanel.add(volumeLevel);

    JLabel soundMax = new JLabel(new ImageIcon("../base/gui/volume24.gif"));
    //soundMax.setAlignmentY(Component.CENTER_ALIGNMENT);
    vPanel.add(soundMax);
    */

// Text Quality
    /*
    JPanel qTextPanel = new JPanel();
    qTextPanel.setOpaque(false);
    qTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    qTextPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

    ALabel qTextTitle = new ALabel("high quality text : ");
    qTextPanel.add(qTextTitle);

    JCheckBox cButton = new JCheckBox();
    cButton.setBackground(Color.white);
    cButton.setSelected(false);
    cButton.addItemListener(this);
    qTextPanel.add(cButton);
    */

// Help buttons
    ImageIcon im_helpup  = new ImageIcon("../base/gui/help-up.gif");
    ImageIcon im_helpdo  = new ImageIcon("../base/gui/help-do.gif");
    JButton b_help = new JButton(im_helpup);
    b_help.setRolloverIcon(im_helpdo);
    b_help.setPressedIcon(im_helpdo);
    b_help.setBorderPainted(false);
    b_help.setContentAreaFilled(false);
    b_help.setFocusPainted(false);
    b_help.setAlignmentX(Component.CENTER_ALIGNMENT);
    //b_help.setPreferredSize( new Dimension(90,30) );    
    b_help.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        new JHTMLWindow( DataManager.getDefaultDataManager().getClientScreen(), "Help", "../docs/help/index.html", 340, 450, false );
      }
    });
    innerPanel.add(b_help);

    add(innerPanel);
    
    if (DataManager.SHOW_DEBUG) {
      JMemory memo = new JMemory();
      memo.init();
    }
    
  }

 /*------------------------------------------------------------------------------------*/

  /**
   * Invoked when the mouse button is clicked
   */
  public void mouseClicked(MouseEvent e) {}
  /**
   * Invoked when the mouse enters a component
   */
  public void mouseEntered(MouseEvent e) {}
  /**
   * Invoked when the mouse exits a component
   */
  public void mouseExited(MouseEvent e) {}
  /**
   * Invoked when a mouse button has been pressed on a component
   */
  public void mousePressed(MouseEvent e) {}
  /**
   * Invoked when a mouse button has been released on a component
   */
  public void mouseReleased(MouseEvent e) {}

 /*------------------------------------------------------------------------------------*/

  /** Invoked when volume is changed
   */
  /*
  public void stateChanged(ChangeEvent e) {
    SoundLibrary.getSoundLibrary().changeMusicVolume((short) volumeLevel.getValue());
  }*/

  /** Invoked when check box state is changed
   */
  /*public void itemStateChanged(ItemEvent e) {
    Object source = e.getItemSelectable();
    TextDrawable.setHighQualityTextDisplay( (e.getStateChange() == ItemEvent.SELECTED) );
  }
  */

}