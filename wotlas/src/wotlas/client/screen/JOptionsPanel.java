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

import wotlas.utils.*;
import wotlas.libs.aswing.*;
import wotlas.client.*;
import wotlas.client.gui.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

/** JPanel to configure the interface
 *
 * @author Petrus
 */

public class JOptionsPanel extends JPanel implements MouseListener
{

 /*------------------------------------------------------------------------------------*/

   private Image menu;

  /** Consctructor.
   */
  public JOptionsPanel() {
    super();
    setBackground(Color.white);
    JPanel innerPanel = new JPanel();
    innerPanel.setOpaque(false);
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

  // Title
    ALabel lbl_title = new ALabel(" ");
    lbl_title.setAlignmentX(Component.CENTER_ALIGNMENT);
    innerPanel.add(lbl_title);

  // Space
    innerPanel.add(new JLabel(" "));

  // Options button
    ImageIcon im_optionsup = ClientDirector.getResourceManager().getImageIcon("options-up.gif");
    ImageIcon im_optionsdo  = ClientDirector.getResourceManager().getImageIcon("options-do.gif");
    JButton b_options = new JButton(im_optionsup);
    b_options.setRolloverIcon(im_optionsdo);
    
    b_options.setPressedIcon(im_optionsdo);
    b_options.setBorderPainted(false);
    b_options.setContentAreaFilled(false);
    b_options.setFocusPainted(false);
    b_options.setAlignmentX(Component.CENTER_ALIGNMENT);
    b_options.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        new JConfigurationDlg(ClientDirector.getDataManager().getClientScreen());
      }
    });
    innerPanel.add(b_options);


// Help buttons
    ImageIcon im_helpup  = ClientDirector.getResourceManager().getImageIcon("help-up.gif");
    ImageIcon im_helpdo  = ClientDirector.getResourceManager().getImageIcon("help-do.gif");
    JButton b_help = new JButton(im_helpup);
    b_help.setRolloverIcon(im_helpdo);
    b_help.setPressedIcon(im_helpdo);
    b_help.setBorderPainted(false);
    b_help.setContentAreaFilled(false);
    b_help.setFocusPainted(false);
    b_help.setAlignmentX(Component.CENTER_ALIGNMENT);

    b_help.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        new JHTMLWindow( ClientDirector.getDataManager().getClientScreen(), "Help",
                         ClientDirector.getResourceManager().getHelpDocsDir()+"index.html",
                         640, 340, false,
                         ClientDirector.getResourceManager() );
      }
    });
    innerPanel.add(b_help);

    add(innerPanel);

  // We load the image
     MediaTracker mediaTracker = new MediaTracker(this);
     menu  = ClientDirector.getResourceManager().getGuiImage("menu.jpg");
     mediaTracker.addImage(menu,0);

         try{
            mediaTracker.waitForAll(); // wait for all images to be in memory
         }
         catch(InterruptedException e){
            e.printStackTrace();
         }


    if (DataManager.SHOW_DEBUG) {
      JMemory memo = new JMemory();
      memo.init();
    }
    
  }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To paint our panel...
    */
      public void paintComponent(Graphics g) {
      	  if(menu!=null)
             g.drawImage(menu,0,0,this);
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