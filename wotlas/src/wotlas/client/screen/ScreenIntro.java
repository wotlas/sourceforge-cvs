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

/** The first screen of wotlas client's interface
 *
 * @author Petrus
 */

public class ScreenIntro extends JFrame
{
  private JPanel leftPanel;
  
  private JPanel rightPanel;
  
  private int leftWidth = 300;
  
  private int rightWidth = 150;
  
  private int height = 400;  
  
 /*------------------------------------------------------------------------------------*/
  
  /** Constructor
   */
  public ScreenIntro() {
    super("Wotlas client");    
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    
    //JOptionPane.showMessageDialog(this, "Welcome to WOTLAS Client v1.0 (08-2001)\n\nsee http://wotlas.sf.net for latest version", "WOTLAS-Client", JOptionPane.INFORMATION_MESSAGE);    
  }
  
 /*------------------------------------------------------------------------------------*/
  
  /** Set the left JPanel of the interface
   *
   * @param leftPanel left panel to be added
   */  
  public void setLeftPanel(JPanel leftPanel) {
    removeLeftPanel();
    leftPanel.setPreferredSize( new Dimension(leftWidth, height) );
    this.leftPanel = leftPanel;
    getContentPane().add(this.leftPanel, BorderLayout.WEST);
  }
  
  /** Set the right JPanel of the interface
   *
   * @param rightPanel right panel to be added
   */
  public void setRightPanel(JPanel rightPanel) {
    removeRightPanel();   
    rightPanel.setPreferredSize( new Dimension(rightWidth, height) );
    this.rightPanel = rightPanel;
    getContentPane().add(this.rightPanel, BorderLayout.EAST);
  }
  
  public void removeLeftPanel() {
    if (this.leftPanel != null) {
      remove(this.leftPanel);
      //leftPanel = null;
    }
  }
  
  public void removeRightPanel() {
    if (this.rightPanel != null) {
      remove(this.rightPanel);
      rightPanel = null;
    }
  }  
  
 /*------------------------------------------------------------------------------------*/
  
  /** To get left panel width
   */
  public int getLeftWidth() {
    return leftWidth;
  }
  
  /** To set left panel width
   */
  public void setLeftWidth(int width) {
    leftWidth = width;
  }
  
  /** To get left panel width
   */
  public int getRightWidth() {
    return rightWidth;
  }
  
  /** To set left panel width
   */
  public void setRightWidth(int width) {
    rightWidth = width;
  }  
  
 /*------------------------------------------------------------------------------------*/
  
  /** Show the interface
   */
  public void showScreen() {            
    pack();
    show();
  }
  
  /** Close the interface
   */
  public void closeScreen() {
    if (leftPanel != null) {
      remove(leftPanel);
    }
    if (rightPanel != null) {
      remove(rightPanel);
    }
    dispose();
  }    
  
 /*------------------------------------------------------------------------------------*/ 
  
}  
    
    
    
    