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

package wotlas.utils.aswing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.File;

/** A small utility to display an information message
 *
 * @author Aldiss
 */

public class AInfoDialog extends JDialog {
 /*------------------------------------------------------------------------------------*/

   /** Constructor.
    * 
    * @param frame frame owner of this JDialog
    * @param message msg to display
    * @param modal if the dialog is modal or not
    */
   public AInfoDialog(Frame frame, String message, boolean modal, String guiImagesPath) {
         super(frame,"Information", modal);
         guiImagesPath = guiImagesPath + File.separator;
      // some inits
         getContentPane().setLayout( new BorderLayout() );
         getContentPane().setBackground(Color.white);
         
      // Top Label
         ALabel label1 = new ALabel(message,SwingConstants.CENTER );
         getContentPane().add( label1, BorderLayout.CENTER );

      // Ok Button
         ImageIcon im_okup = new ImageIcon(guiImagesPath+"gui/ok-up.gif");
         ImageIcon im_okdo = new ImageIcon(guiImagesPath+"gui/ok-do.gif");
         ImageIcon im_okun = new ImageIcon(guiImagesPath+"gui/ok-un.gif");
         JButton b_ok = new JButton(im_okup);
         b_ok.setRolloverIcon(im_okdo);
         b_ok.setPressedIcon(im_okdo);
         b_ok.setDisabledIcon(im_okun);
         b_ok.setBorderPainted(false);
         b_ok.setContentAreaFilled(false);
         b_ok.setFocusPainted(false);
         getContentPane().add(b_ok, BorderLayout.SOUTH );

          b_ok.addActionListener(new ActionListener()
           {
              public void actionPerformed (ActionEvent e) {
                   dispose();
              }
           });

         pack();

         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         setLocation( (int) ((screenSize.getWidth() - getWidth()) / 2),
                   (int) ((screenSize.getHeight() - getHeight()) / 2) );
         show();
   }

 /*------------------------------------------------------------------------------------*/

}