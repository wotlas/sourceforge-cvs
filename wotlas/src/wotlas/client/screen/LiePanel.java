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
import wotlas.common.message.description.CreateFakeNameMessage;
import wotlas.common.message.description.ChangeFakeNameMessage;
import wotlas.client.*;

/** JPanel where you can lie on your name.
 *
 * @author Petrus
 */

public class LiePanel extends JPanel
{
  //private JList fakeNamesList;
  
  private final static int fakeNamesLength = 5;
  
  private JTable fakeNamesList;
  
  private AButton saveNameButton;
  
  private ARadioButton[] b_fakeNames;
  
  JPanel whitePanel;
  
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
    
    //final String[][] data = {{"one"}, {"two"}, {"three"}, {"four"}, {"five yes"}};
    //String[] columnNames = {"names"};
    //fakeNamesList = new JTable(data, columnNames);
    //centerPanel.add(fakeNamesList);
        
    JPanel form = new JPanel(new GridLayout(fakeNamesLength, 1));    
    b_fakeNames = new ARadioButton[fakeNamesLength];
    ButtonGroup g_fakeNames = new ButtonGroup();
    RadioListener myListener = new RadioListener();

    for (int i=0; i<fakeNamesLength; i++) {     
      b_fakeNames[i] = new ARadioButton();
      b_fakeNames[i].setVisible(false);
      b_fakeNames[i].setActionCommand("");
      b_fakeNames[i].addActionListener(myListener);
      g_fakeNames.add(b_fakeNames[i]);
      form.add(b_fakeNames[i]);  
    }
    
    centerPanel.add(form);
    
    add(centerPanel, BorderLayout.CENTER);
    
    JPanel form2 = new JPanel(new GridLayout(2, 1));
    form2.setBackground(Color.white);
    ALabel a_infoFakeName = new ALabel("Enter a new fake name:");
    form2.add(a_infoFakeName);
    final ATextField a_newFakeName = new ATextField();
    form2.add(a_newFakeName);
    
    ImageIcon im_saveup  = new ImageIcon("../base/gui/new-up.gif");
    ImageIcon im_savedo  = new ImageIcon("../base/gui/new-do.gif");
    saveNameButton = new AButton(im_saveup);
    saveNameButton.setRolloverIcon(im_savedo);
    saveNameButton.setPressedIcon(im_savedo);
    saveNameButton.setBorderPainted(false);
    saveNameButton.setContentAreaFilled(false);
    saveNameButton.setFocusPainted(false);
    
    
      saveNameButton.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {                    	
          PlayerImpl player = DataManager.getDefaultDataManager().getMyPlayer();                 
          System.out.println("CreateFakeName " + a_newFakeName.getText());
          player.sendMessage( new CreateFakeNameMessage(a_newFakeName.getText()) );
          a_newFakeName.setText("");
        }
      });

    whitePanel = new JPanel(new BorderLayout());
    

    
    whitePanel.add(form2, BorderLayout.NORTH);
    whitePanel.add(saveNameButton, BorderLayout.CENTER);
    
    whitePanel.setBackground( Color.white );
    
    add(whitePanel, BorderLayout.SOUTH);
       



  }

  /** To set a fake name.
   */
  public void setFakeName(int index, String fakeName) {
    if (fakeName.length()==0)
      return;
      
    b_fakeNames[index].setText(fakeName);
    b_fakeNames[index].setActionCommand(""+index);
    b_fakeNames[index].setVisible(true);
    
    // if player has filled all his fake names,
    // we hide the form to create a new fake name
    if (index==(fakeNamesLength-1)) {
      whitePanel.setVisible(false);
    }
  }
  
  /** To set the current fake name.
   * @param index index of the current fake name
   */
  public void setCurrentFakeName(int index) {
    b_fakeNames[index].setSelected(true);
  }
  
  /** To reset this panel.
   */
  public void reset() {
    for (int i=0; i<fakeNamesLength; i++) {         
      b_fakeNames[i].setVisible(false);
      b_fakeNames[i].setActionCommand("");      
    }    
  }
  
  /** Listen to the radio button selection.
   */
  class RadioListener implements ActionListener  {
    public void actionPerformed(ActionEvent e) {
      int currentFakeName = Integer.parseInt(e.getActionCommand());
      PlayerImpl player = DataManager.getDefaultDataManager().getMyPlayer(); 
      player.sendMessage(new ChangeFakeNameMessage((short) currentFakeName));        
    }
  }
}