/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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
import wotlas.utils.*;
import wotlas.utils.aswing.*;

import wotlas.common.*;
import wotlas.common.message.description.SetFakeNameMessage;
import wotlas.client.*;

/** JPanel where you can lie on your name.
 *
 * @author Petrus
 */

public class LiePanel extends JPanel
{
  //private JList fakeNamesList;
  
  private JTable fakeNamesList;
  
  private AButton saveNameButton;
  
 /*------------------------------------------------------------------------------------*/ 
  
  /** Constructor.
   */
  public LiePanel() {
    super();
    this.setLayout(new BorderLayout());

    ATextArea ta_infos = new ATextArea("You can lie on your name: you can create and use up to 5 fake names... but be careful, once you have created a fake name, you cannot change it");
    ta_infos.setLineWrap(true);
    ta_infos.setWrapStyleWord(true);
    ta_infos.setEditable(false);
    ta_infos.setAlignmentX(0.5f);
    ta_infos.setOpaque(false);
    add(ta_infos, BorderLayout.NORTH);
    
    JPanel centerPanel = new JPanel( new BorderLayout() );
    
    final String[][] data = {{"one"}, {"two"}, {"three"}, {"four"}, {"five yes"}};
    String[] columnNames = {"names"};
    fakeNamesList = new JTable(data, columnNames);
    centerPanel.add(fakeNamesList);
    add(centerPanel, BorderLayout.CENTER);
    
    ImageIcon im_saveup  = new ImageIcon("../base/gui/save-up.gif");
    ImageIcon im_savedo  = new ImageIcon("../base/gui/save-do.gif");
    saveNameButton = new AButton(im_saveup);
    saveNameButton.setRolloverIcon(im_savedo);
    saveNameButton.setPressedIcon(im_savedo);
    saveNameButton.setBorderPainted(false);
    saveNameButton.setContentAreaFilled(false);
    saveNameButton.setFocusPainted(false);
    saveNameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    
      saveNameButton.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {                    	
          PlayerImpl player = DataManager.getDefaultDataManager().getMyPlayer();
          
          int index = 0;
          System.out.println("fakeName 0,0 = " + (String) fakeNamesList.getValueAt(0,0));
          player.sendMessage( new SetFakeNameMessage(index, (String) fakeNamesList.getValueAt(index,0) ) );
        }
      });

    JPanel whitePanel = new JPanel();
    whitePanel.setBackground( Color.white );
    whitePanel.add(saveNameButton);
    add(whitePanel, BorderLayout.SOUTH);
       



  }


  /** To reset this panel.
   */
  public void reset() {
    
  }
}
