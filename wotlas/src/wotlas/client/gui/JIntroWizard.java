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

/** A wizard to enter wotlas world
 *
 * @author Petrus
 */

public class JIntroWizard extends JFrame
{
  private JBackground bgPanel;
  
  private JPanel leftPanel;
  
  private JPanel rightPanel;
  
  private int leftWidth = 280;
  
  private int rightWidth = 120;
  
  private int height = 300;    
  
 /*------------------------------------------------------------------------------------*/
  
  /** Constructor
   */
  public JIntroWizard() {
    super("Wotlas client"); 
    setDefaultCloseOperation(EXIT_ON_CLOSE);    
    
    ImageIcon bgImg = new ImageIcon("..\\src\\test\\petrus\\bg-400x300.jpg");
    bgPanel = new JBackground(bgImg);
    
    //bgPanel = new JBackground();
    getContentPane().add(bgPanel, BorderLayout.NORTH);
    
    setSize(leftWidth+rightWidth, height);
    setResizable(false);
  }  
  
 /*------------------------------------------------------------------------------------*/
  
  /** Set the left JPanel of the interface
   *
   * @param leftPanel left panel to be added
   */  
  public void setLeftPanel(JPanel leftPanel) {
    removeLeftPanel();
    leftPanel.setPreferredSize( new Dimension(leftWidth-10, height) );
    this.leftPanel = leftPanel;
    this.leftPanel.setOpaque(false);    
    bgPanel.add(this.leftPanel, BorderLayout.CENTER);
  }
  
  /** Set the right JPanel of the interface
   *
   * @param rightPanel right panel to be added
   */
  public void setRightPanel(JPanel rightPanel) {
    removeRightPanel();   
    rightPanel.setPreferredSize( new Dimension(rightWidth-10, height) );
    this.rightPanel = rightPanel;
    this.rightPanel.setOpaque(false);    
    bgPanel.add(this.rightPanel, BorderLayout.EAST);
  }
  
  /** Remove the left panel
   */
  public void removeLeftPanel() {
    if (this.leftPanel != null) {
      
      bgPanel.remove(this.leftPanel);
      leftPanel = null;
    }
  }
  
  /** Remove the right panel
   */
  public void removeRightPanel() {
    if (this.rightPanel != null) {
      bgPanel.remove(this.rightPanel);
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
  
  /** To get right panel width
   */
  public int getRightWidth() {
    return rightWidth;
  }
  
  /** To set right panel width
   */
  public void setRightWidth(int width) {
    rightWidth = width;
  }  
  
 /*------------------------------------------------------------------------------------*/
  
  /** Show the interface
   */
  public void showScreen() {    
    pack();
    setSize(leftWidth+rightWidth, height);
    leftPanel.repaint();
    rightPanel.repaint();
    leftPanel.validate();
    rightPanel.validate();
    show();
  }
  
  /** Close the interface
   */
  public void closeScreen() {    
    if (leftPanel != null) {
      bgPanel.remove(leftPanel);
    }
    if (rightPanel != null) {
      bgPanel.remove(rightPanel);
    }
    remove(bgPanel);
    bgPanel = null;
    dispose();
  }
  
  /** Set the different colors
   */
  static public void setGUI() {
    // Set the differents fonts
    Font f = new Font("Monospaced", Font.PLAIN, 10);
    UIManager.put("Button.font", f);
    
    f = new Font("Dialog", Font.BOLD, 15);
    UIManager.put("Label.font", f);
    UIManager.put("Label.foreground", Color.white);
  }
 /*--------------------------------------------------------------------------*/ 
  
}  
    
    
    
    