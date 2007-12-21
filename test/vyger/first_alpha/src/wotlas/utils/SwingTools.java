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

package wotlas.utils;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/** Various useful tools to manipulate Swing components
 *
 * @author Petrus
 */

public class SwingTools
{
 
 /*------------------------------------------------------------------------------------*/

  /** Center a component on the screen
   *
   * @param component the component to be centered
   */
  static public void centerComponent(Component component) {
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((d.getWidth() - component.getWidth()) / 2);
    int y = (int) ((d.getHeight() - component.getHeight()) / 2);
    component.setLocation(x, y);
  }

 /*------------------------------------------------------------------------------------*/
  
  /** Load a font
   *
   * @param filename path of the font
   * @param size font size
   */
  static public Font loadFont(String filename, float size) {
    try {
      File file = new File(filename);
      FileInputStream fis = new FileInputStream(file);
      Font f = Font.createFont(Font.TRUETYPE_FONT, fis);      
      f = f.deriveFont(size);
      return f;
    } catch (Exception e) {
      e.printStackTrace();
      Font f = new Font("Dialog", Font.PLAIN, (int)size);
      return f;
    }
  }

 /*------------------------------------------------------------------------------------*/
  
  /** Load a font
   *
   * @param filename path of the font
   * @param size font size
   */
  static public Font loadFont(String filename) {
    try {
      File file = new File(filename);
      FileInputStream fis = new FileInputStream(file);
      Font f = Font.createFont(Font.TRUETYPE_FONT, fis);            
      return f;
    } catch (Exception e) {
      e.printStackTrace();
      Font f = new Font("Dialog", Font.PLAIN, 10);
      return f;
    }
  }

 /*------------------------------------------------------------------------------------*/ 

}