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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** JPanel to show the informations of the player
 *
 * @author Petrus
 */

public class JPlayerPanel extends JPanel implements MouseListener
{
  JTabbedPane playerTabbedPane;
  
 /*------------------------------------------------------------------------------------*/ 
  
  /** Consctructor.
   */
  public JPlayerPanel() {
    super();
    this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    
    playerTabbedPane = new JTabbedPane();
    //playerTabbedPane.setAlignmentX(0.5f);
    
    playerTabbedPane.addTab("Info", null, new InfoPanel() );
    playerTabbedPane.getComponentAt(0).setName("Info");
        
    add(playerTabbedPane);
  }
  
  
  
 public Component getTab(String nom)
  {
   for (int i=0; i<playerTabbedPane.getTabCount();i++) {
    if ( playerTabbedPane.getComponentAt(i).getName().equals(nom) ) {
      return playerTabbedPane.getComponentAt(i);
    }
   } 
   return null;
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

}