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

import wotlas.utils.Debug;

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

  private int leftWidth = 290;

  private int rightWidth = 110;

  private int height = 300;

  final private ImageIcon backgroundImg = new ImageIcon("D:\\projects\\wotlas\\wotlas\\src\\wotlas\\client\\screen\\bg-400x300.jpg");

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
  public ScreenIntro() {
    super("Wotlas client");
    //setDefaultCloseOperation(EXIT_ON_CLOSE);
    addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        Debug.exit();
      }
    });
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
    //this.leftPanel.setOpaque(false);
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
    //this.rightPanel.setOpaque(false);
    getContentPane().add(this.rightPanel, BorderLayout.EAST);
  }

  public void removeLeftPanel() {
    if (this.leftPanel != null) {
      remove(this.leftPanel);
      leftPanel = null;
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
    JPanel middlePanel = new JPanel() {
      {setOpaque(false);}
      public void paintComponent(Graphics g) {
        ImageIcon img = new ImageIcon("bg-400x300.jpg");
        g.drawImage(img.getImage(), 0, 0, null);
        super.paintComponent(g);
      }
    };
    getContentPane().add(middlePanel, BorderLayout.NORTH);
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

  public void paint(Graphics g) {
    //ImageIcon image = new ImageIcon("FIREFALL.gif");
    /*Dimension d = getSize();
    for( int x = 0; x < d.width; x += backgroundImg.getIconWidth() )
    for( int y = 0; y < d.height; y += backgroundImg.getIconHeight() )*/

    g.drawImage( backgroundImg.getImage(), 100, 100, null, null );
    super.paint(g);
    // Now let the paint do its usual work

  }

  /*public void paintComponent (Graphics g) {
    g.drawImage(backgroundImg, 0, 0, null);
    super.paintComponent(g);
  }*/
 /*------------------------------------------------------------------------------------*/

}



    