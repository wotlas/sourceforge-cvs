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

package wotlas.client.gui;

import wotlas.client.*;

import wotlas.utils.SwingTools;
import wotlas.libs.graphics2D.FontFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;


/** A wizard that displays a content (left panel) and navigation buttons (right panel).
 *
 * @author Petrus
 */

public class JIntroWizard extends JFrame {

 /*------------------------------------------------------------------------------------*/  

  /** Left Panel ( content of the window )
   */
    private JPanel leftPanel;

  /** Right Panel ( buttons for navigation )
   */
    private JPanel rightPanel;

  /** Some Dimensions
   */
    private int width = 500;
    private int rightWidth = 120;
    private int leftWidth = 0;
    private int height = 300;
    private Dimension rightDimension = new Dimension(rightWidth, 0);

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public JIntroWizard() {
        super("Wotlas Client");
        setBackground(Color.white);
        setIconImage(ClientDirector.getResourceManager().getBaseImage("gui/icon.gif"));
        setGUI();

        setSize( width, height );
        SwingTools.centerComponent(this);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

 /*------------------------------------------------------------------------------------*/

  /** Set the left JPanel of the interface
   *
   * @param leftPanel left panel to be added
   */
    protected void setLeftPanel(JPanel leftPanel) {
       removeLeftPanel();
       this.leftPanel = leftPanel;
       leftPanel.setBackground(Color.white);
       getContentPane().add(this.leftPanel, BorderLayout.CENTER);        
    }

 /*------------------------------------------------------------------------------------*/

  /** Set the right JPanel of the interface
   *
   * @param rightPanel right panel to be added
   */
    protected void setRightPanel(JPanel rightPanel) {
       removeRightPanel();
       rightPanel.setPreferredSize(rightDimension);
       this.rightPanel = rightPanel;
       rightPanel.setBackground(Color.white);
       getContentPane().add(this.rightPanel, BorderLayout.EAST);        
    }

 /*------------------------------------------------------------------------------------*/

  /** Remove the left panel
   */
    protected void removeLeftPanel() {
       if(leftPanel != null) {
          getContentPane().remove(leftPanel);
          leftPanel = null;
       }
    }

 /*------------------------------------------------------------------------------------*/

  /** Remove the right panel
   */
    protected void removeRightPanel() {
      if(rightPanel != null) {
         getContentPane().remove(rightPanel);
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
       validate();
       pack();    
       show();
    }

 /*--------------------------------------------------------------------------*/

  /** Close the interface and remove the panels
   */
    public void closeScreen() {
       if (leftPanel != null)
          getContentPane().remove(leftPanel);

       if (rightPanel != null)
          getContentPane().remove(rightPanel);

       dispose();
    }

 /*--------------------------------------------------------------------------*/

  /** Set the colors and fonts
   */
    static public void setGUI() {
        Font f;

        f = new Font("Monospaced", Font.PLAIN, 10);
        UIManager.put("Button.font", f);

        f = FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter");
    
        UIManager.put("ComboBox.font", f.deriveFont(14f));
        UIManager.put("ComboBox.foreground", Color.black);

        UIManager.put("Label.font", f.deriveFont(14f));
        UIManager.put("Label.foreground", Color.black);
    
        UIManager.put("PasswordField.font", f.deriveFont(14f));
        UIManager.put("PasswordField.foreground", Color.black);
    
        UIManager.put("RadioButton.font", f.deriveFont(14f));
        UIManager.put("RadioButton.foreground", Color.black);
            
        UIManager.put("Table.font", f.deriveFont(14f));
        UIManager.put("Table.foreground", Color.black);    
    
        UIManager.put("TableHeader.font", f.deriveFont(16f));
        UIManager.put("TableHeader.foreground", Color.black);
    
        UIManager.put("TextArea.font", f.deriveFont(14f));
        UIManager.put("TextArea.foreground", Color.black);
    
        UIManager.put("TextField.font", f.deriveFont(14f));
        UIManager.put("TextField.foreground", Color.black);    
    
        UIManager.put("CheckBox.font", f.deriveFont(14f));
        UIManager.put("CheckBox.foreground", Color.black);
    }
  
 /*--------------------------------------------------------------------------*/

}

