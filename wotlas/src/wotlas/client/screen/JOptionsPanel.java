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

public class JOptionsPanel extends JPanel implements MouseListener, ChangeListener
{

 /*------------------------------------------------------------------------------------*/
  
  /** Slider to change volume level.
   */
  private JSlider volumeLevel;
  
  /** Consctructor.
   */
  public JOptionsPanel() {
    super();
    JLabel label1 = new ALabel("Options");
    
    JPanel vPanel = new JPanel();
    vPanel.add(new JLabel(new ImageIcon("../base/gui/volume16.gif")));
    volumeLevel = new JSlider(JSlider.HORIZONTAL, 0, SoundLibrary.MAX_VOLUME, SoundLibrary.MAX_VOLUME);
    volumeLevel.addChangeListener(this);
    vPanel.add(volumeLevel);
    vPanel.add(new JLabel(new ImageIcon("../base/gui/volume24.gif")));
    
    
    add(label1, BorderLayout.NORTH);
    add(vPanel, BorderLayout.CENTER);
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

}