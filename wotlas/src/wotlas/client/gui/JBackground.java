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

package wotlas.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** Shows a background image on a JPanel
 *
 * @author Petrus
 */

public class JBackground extends JPanel
{ 
  /** Background Image
   */ 
  private Image grayImage;
  
 /*------------------------------------------------------------------------------------*/

  public JBackground() {}
  
  /** Constructor
   *
   * @param imageIcon the background image
   */
  public JBackground (ImageIcon imageIcon)
  {    
    setLayout(new BorderLayout(0,0));
    grayImage = GrayFilter.createDisabledImage(imageIcon.getImage());
    setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
  }

 /*------------------------------------------------------------------------------------*/
  
  /** To paint the background image
   */
  
  public void paintComponent(Graphics g)
  {
    if (grayImage != null) {
      g.drawImage(grayImage, 0, 0, this);
    }
  }
  
  
 /*------------------------------------------------------------------------------------*/

}
