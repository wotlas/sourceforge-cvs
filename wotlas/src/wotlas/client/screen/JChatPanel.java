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

/** JPanel to show the chat engine
 *
 * @author Petrus
 */

public class JChatPanel extends JPanel implements MouseListener
{

  /*------------------------------------------------------------------------------------*/  
  
  static final int CHAT_WHISPER = 0;
  static final int CHAT_SPEAK = 1;
  static final int CHAT_SHOUT = 2;
 
  ImageIcon iconUp = new ImageIcon("images/th_up.gif");
  ImageIcon iconDown = new ImageIcon("images/th_dn.gif");
  
  /** Our tabbedPane
   */
  JTabbedPane tabbedPane;
 
 /*------------------------------------------------------------------------------------*/  
 
  /** Constructor.
   */ 
  public JChatPanel() {
    super();

    tabbedPane = new JTabbedPane();

    /*Component panel1 = makeTextPanel("Blah");
    tabbedPane.addTab("#Salon", iconUp, panel1, "Main room");
    tabbedPane.setSelectedIndex(0);

    Component panel2 = makeTextPanel("Blah blah");
    tabbedPane.addTab("#Philo", iconUp, panel2, "Room for philosophs");

    Component panel3 = makeTextPanel("Blah blah blah");
    tabbedPane.addTab("#Science", null, panel3, "Room for scientists");
    
    Component panel4 = makeTextPanel("Blah blah blah");
    tabbedPane.addTab("#Bio", null, panel4, "Room for biologists");
    */
    
    addChatRoom("Current Room");

    //tabbedPane.setEnabledAt(3, false);

    // NORTH
    JToolBar chatToolbar = new JToolBar();
    
    //chatToolbar.setPreferredSize(new Dimension(10,10));
    JButton createGroupButton = new JButton(new ImageIcon("images/th_up.gif"));
    createGroupButton.setToolTipText("Create a new group");
    chatToolbar.add(createGroupButton);
    JButton leaveGroupButton = new JButton(new ImageIcon("images/th_dn.gif"));
    leaveGroupButton.setToolTipText("Leave the current new group");
    chatToolbar.add(leaveGroupButton);
    
    //Add the tabbed pane to this panel.
    setLayout(new GridLayout(1, 1, 0, 0)); 
    //setLayout(new BorderLayout());
    //add("Center", chatToolbar);
    add("South", tabbedPane);
      
  }

  protected Component makeTextPanel(String text) {
    JPanel panelChat = new JPanel(false);
    panelChat.setLayout(new BorderLayout());

    // CENTER
    JTextPane displayPane = new JTextPane();
    displayPane.setEditable(false);
    JScrollPane displayScroller = new JScrollPane(displayPane);
    
    // SOUTH
    JPanel bottomChat = new JPanel(false);
    bottomChat.setLayout(new BorderLayout());
    JTextField inputBox = new JTextField(30);
    bottomChat.add("East", inputBox);
    JSlider chatLevel = new JSlider(JSlider.HORIZONTAL, 0, 2, CHAT_SPEAK);
    chatLevel.setMajorTickSpacing(1);
    chatLevel.setMinorTickSpacing(1);
    chatLevel.setSnapToTicks(true);
    chatLevel.setPaintTicks(true);
    //chatLevel.setPreferredSize(new Dimension(60,10));
    bottomChat.add("Center", chatLevel);
  
    // EAST
    String[] data = {"bernie","petrus","thierry","valere"};
    JList clientsList = new JList(data);
    //clientsList.setPreferredSize(new Dimension(80, 50));
    clientsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane listScroller = new JScrollPane(clientsList, 
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    panelChat.add("Center", displayScroller);
    panelChat.add("South", bottomChat);
    panelChat.add("East", listScroller);
    
    return panelChat;
  }
  
  public void addChatRoom(String roomName) {
    Component panelRoom = makeTextPanel(roomName);
    tabbedPane.addTab("#" + roomName, iconUp, panelRoom, roomName + " channel");    
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