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

import wotlas.client.DataManager;

import wotlas.libs.graphics2D.*;

import wotlas.utils.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** JPanel to show the current map
 *
 * @author Petrus
 */

public class JMapPanel extends JPanel implements MouseListener
{

 /*------------------------------------------------------------------------------------*/

  /** Our Graphics Director
   */
  private GraphicsDirector gDirector;

  /** Our DataManager
   */
  private DataManager dataManager;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   * @param gDirector Graphics Director
   */
  public JMapPanel(GraphicsDirector gDirector, DataManager dataManager) {
    //super(new FlowLayout(FlowLayout.LEFT,0,0));
    super(new GridLayout(1,1,0,0));

    this.gDirector = gDirector;
    this.dataManager = dataManager;

    add(gDirector);

    // Listen to Mouse clics
    addMouseListener(this);
  }

 /*------------------------------------------------------------------------------------*/

  /** To update the Graphics Director used.
   */
   public void updateGraphicsDirector(GraphicsDirector gDirector) {
       remove(this.gDirector);
       this.gDirector = gDirector;

       add(gDirector);
       validate();
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
  public void mouseReleased(MouseEvent e) {
    
    if (DataManager.SHOW_DEBUG)
      System.out.println("[JMapPanel] : clic sur (" + e.getX() + "," + e.getY() + ")");

    if (SwingUtilities.isRightMouseButton(e)) {
       if (DataManager.SHOW_DEBUG)
          System.out.println("\tright clic");

       dataManager.onRightClicJMapPanel(e);
//      dataManager.tick();
    }
    else {
       if (DataManager.SHOW_DEBUG)
          System.out.println("\tleft clic");

       dataManager.onLeftClicJMapPanel(e);
//      dataManager.tick();
    }
  }

 /*------------------------------------------------------------------------------------*/

}