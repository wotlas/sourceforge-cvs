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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

/** JPanel to configure the interface
 *
 * @author Petrus
 */

public class JOptionsPanel extends JPanel implements MouseListener, ChangeListener, ItemListener 
{

 /*------------------------------------------------------------------------------------*/
  
  /** Slider to change volume level.
   */
  private JSlider volumeLevel;
  
  /** Consctructor.
   */
  public JOptionsPanel() {
    super();
    
    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
    innerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    
    ALabel label1 = new ALabel("Configuration");
    label1.setAlignmentX(Component.CENTER_ALIGNMENT);

// Volume Panel
    JPanel vPanel = new JPanel();
    vPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    vPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    
    ALabel volTitle = new ALabel("volume : ");
    vPanel.add(volTitle);

    JLabel soundMin = new JLabel(new ImageIcon("../base/gui/volume16.gif"));
    //soundMin.setAlignmentY(Component.CENTER_ALIGNMENT);
    vPanel.add(soundMin);
    
    volumeLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_VOLUME, SoundLibrary.MAX_VOLUME);
    volumeLevel.setPreferredSize(new Dimension(50,30));
    volumeLevel.addChangeListener(this);
    volumeLevel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
    vPanel.add(volumeLevel);
    
    JLabel soundMax = new JLabel(new ImageIcon("../base/gui/volume24.gif"));
    //soundMax.setAlignmentY(Component.CENTER_ALIGNMENT);
    vPanel.add(soundMax);

// Text Quality
    JPanel qTextPanel = new JPanel();
    qTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    qTextPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    
    ALabel qTextTitle = new ALabel("high quality text : ");
    qTextPanel.add(qTextTitle);
    
    JCheckBox cButton = new JCheckBox();
    cButton.setSelected(false);
    cButton.addItemListener(this);
    qTextPanel.add(cButton);
    
    innerPanel.add(label1);
    innerPanel.add(vPanel);
    innerPanel.add(qTextPanel);
        
    add(innerPanel);
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
  public void stateChanged(ChangeEvent e) {
    SoundLibrary.getSoundLibrary().setVolume((short) volumeLevel.getValue());
  }
  
  /** Invoked when check box state is changed
   */
  public void itemStateChanged(ItemEvent e) {        
    Object source = e.getItemSelectable(); 
    TextDrawable.setHighQualityTextDisplay( (e.getStateChange() == ItemEvent.SELECTED) );
  }

}