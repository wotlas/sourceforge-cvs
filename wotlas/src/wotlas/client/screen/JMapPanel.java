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

public class JMapPanel extends JPanel implements MouseListener, MouseMotionListener {

 /*------------------------------------------------------------------------------------*/

  /** Different mouse movement steps...
   */
    public static final byte INIT_MOUSE_MOVEMENT = 0;
    public static final byte MOUSE_MOVEMENT      = 1;
    public static final byte END_MOUSE_MOVEMENT  = 2;

 /*------------------------------------------------------------------------------------*/

  /** Our Graphics Director
   */
    private GraphicsDirector gDirector;

  /** Our DataManager
   */
    private DataManager dataManager;

  /** Left mouse button pressed
   */
    private boolean isLeftMouseButtonPressed;

  /** Mouse button dragged ?
   */
    private boolean mouseDragged;

  /** Mouse Position
   */
    private int x, y;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   * @param gDirector Graphics Director
   */
    public JMapPanel(GraphicsDirector gDirector, DataManager dataManager) {
       super(new GridLayout(1,1,0,0));

       this.gDirector = gDirector;
       this.dataManager = dataManager;
       isLeftMouseButtonPressed = false;

       add(gDirector);

    // Listen to Mouse clics
       addMouseListener( this );

    // ... and mouse motion
       addMouseMotionListener( this );
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

 /*------------------------------------------------------------------------------------*/

  /**
   * Invoked when the mouse enters a component
   */
    public void mouseEntered(MouseEvent e) {}

 /*------------------------------------------------------------------------------------*/

  /**
   * Invoked when the mouse exits a component
   */
    public void mouseExited(MouseEvent e) {}

 /*------------------------------------------------------------------------------------*/

  /**
   * Invoked when a mouse button has been pressed on a component
   */
    public void mousePressed(MouseEvent e) {
       mouseDragged = false;

       if (SwingUtilities.isRightMouseButton(e)) {
           isLeftMouseButtonPressed = false;
           x=e.getX();
           y=e.getY();
           dataManager.onRightButtonDragged( 0, 0, true );
       }
       else {
           isLeftMouseButtonPressed = true;
           x=e.getX();
           y=e.getY();
           dataManager.onLeftButtonDragged(e,0,0,INIT_MOUSE_MOVEMENT);
       }
    }

 /*------------------------------------------------------------------------------------*/

  /**
   * Invoked when a mouse button has been released on a component
   */
    public void mouseReleased(MouseEvent e) {
    
       if(DataManager.SHOW_DEBUG)
          System.out.println("[JMapPanel] : clic sur (" + e.getX() + "," + e.getY() + ")");

       if(SwingUtilities.isRightMouseButton(e)) {
          if (DataManager.SHOW_DEBUG)
             System.out.println("\tright clic");

          if( Math.abs(e.getX()-x)<5 && Math.abs(e.getY()-y)<5 )
             dataManager.onRightClicJMapPanel(e);
       }
       else {
          isLeftMouseButtonPressed = false;
          dataManager.onLeftButtonDragged(e,e.getX()-x,e.getY()-y,END_MOUSE_MOVEMENT);

          if (DataManager.SHOW_DEBUG)
             System.out.println("\tleft clic");

          if( Math.abs(e.getX()-x)<5 && Math.abs(e.getY()-y)<5 )
              dataManager.onLeftClicJMapPanel(e);
       }

       mouseDragged = false;
    }

 /*------------------------------------------------------------------------------------*/

   /** Called when the mouse is dragged.
    */
     public void mouseDragged(MouseEvent e) {
     	mouseDragged = true;
     	
        if(isLeftMouseButtonPressed)
           dataManager.onLeftButtonDragged( e, e.getX()-x, e.getY()-y, MOUSE_MOVEMENT );
        else
           dataManager.onRightButtonDragged( e.getX()-x, e.getY()-y, false );
     }

   /** Called when the mouse is moved.
    */
     public void mouseMoved(MouseEvent e) {
        dataManager.onLeftButtonMoved( e.getX(), e.getY() );
     }

 /*------------------------------------------------------------------------------------*/

}