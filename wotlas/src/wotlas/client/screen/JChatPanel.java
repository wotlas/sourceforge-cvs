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

import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;

import wotlas.common.chat.*;
import wotlas.common.message.chat.*;
import wotlas.common.universe.WotlasLocation;

import wotlas.utils.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Hashtable;

/** JPanel to show the chat engine
 *
 * @author Petrus
 */

public class JChatPanel extends JPanel implements MouseListener, ActionListener
{

  /*------------------------------------------------------------------------------------*/  
  
  static final int CHAT_WHISPER = 0;
  static final int CHAT_SPEAK = 1;
  static final int CHAT_SHOUT = 2;
 
  ImageIcon iconUp = new ImageIcon("..\\base\\graphics\\gui\\chat\\myrddraal.gif");
  ImageIcon iconDown = new ImageIcon("..\\base\\graphics\\gui\\chat\\madmyrddraal.gif");
  
  /** Our tabbedPane
   */
  JTabbedPane tabbedPane;
  
  private Hashtable chatRooms;
 
 /*------------------------------------------------------------------------------------*/  
 
  /** Constructor.
   */ 
  public JChatPanel() {
    super();

    tabbedPane = new JTabbedPane();
    
    addChatRoom("Current Room");

    //tabbedPane.setEnabledAt(3, false);

    // NORTH
    JToolBar chatToolbar = new JToolBar();
    chatToolbar.setFloatable(false);
        
    JButton b_createChatRoom = new JButton(new ImageIcon("..\\base\\graphics\\gui\\chat\\smile.gif"));
    b_createChatRoom.setActionCommand("createChatRoom");
    b_createChatRoom.addActionListener(this);
    b_createChatRoom.setToolTipText("Create a new chat room");
    chatToolbar.add(b_createChatRoom);
    
    JButton b_leaveChatRoom = new JButton(new ImageIcon("..\\base\\graphics\\gui\\chat\\perplexed.gif"));
    b_leaveChatRoom.setActionCommand("leaveChatRoom");
    b_leaveChatRoom.addActionListener(this);
    b_leaveChatRoom.setToolTipText("Leave the current chat room");
    chatToolbar.add(b_leaveChatRoom);
    
    JButton b_deleteChatRoom = new JButton(new ImageIcon("..\\base\\graphics\\gui\\chat\\dead.gif"));
    b_deleteChatRoom.setActionCommand("deleteChatRoom");
    b_deleteChatRoom.addActionListener(this);
    b_deleteChatRoom.setToolTipText("Delete the current chat room");
    chatToolbar.add(b_deleteChatRoom);
    
    //Add the tabbed pane to this panel.
    //setLayout(new GridLayout(1, 1, 0, 0)); 
    setLayout(new BorderLayout());
    add("North", chatToolbar);
    add("Center", tabbedPane);
      
  }

 /*------------------------------------------------------------------------------------*/  
 
  /** To add a chatRoom.
   *
   * @param chatRoom ChatRoom to add
   * @return false if the chatRoom already exists, true otherwise
   */
  public boolean addChatRoom(ChatRoom chatRoom) {
    if ( chatRooms.containsKey(chatRoom.getPrimaryKey()) ) {
      Debug.signal( Debug.CRITICAL, this, "addChatRoom failed: key " + chatRoom.getPrimaryKey()
                      + " already in " + this );
      return false;
    }

    chatRooms.put(chatRoom.getPrimaryKey(), chatRoom);
    return true;    
  }

 /*------------------------------------------------------------------------------------*/  

  /** To remove a chatRoom.   
   *
   * @param chatRoom ChatRoom to remove
   * @return false if the chatRoom doesn't exists, true otherwise
   */
  public boolean removeChatRoom(ChatRoom chatRoom) {
    if ( !chatRooms.containsKey(chatRoom.getPrimaryKey()) ) {
      Debug.signal( Debug.CRITICAL, this, "removeChatRoom failed: key " + chatRoom.getPrimaryKey()
                      + " not found in " + this );
      return false;
    }

    chatRooms.remove(chatRoom.getPrimaryKey() );
    return true;
  }

 /*------------------------------------------------------------------------------------*/  
  
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
 
  /** Called when an action is performed.
   */
  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    if (actionCommand != null) {
      System.out.println("Action command : " + actionCommand);
      if (actionCommand.equals("createChatRoom")) {
        PlayerImpl myPlayer = DataManager.getDefaultDataManager().getMyPlayer();        
        WotlasLocation chatRoomLocation = myPlayer.getLocation();
        String chatRoomName = "";
        if ( chatRoomLocation.isRoom() ) {
          chatRoomName = myPlayer.getMyRoom().getShortName();
        } else if ( chatRoomLocation.isTown() ) {
          chatRoomName = "Town chat room";
        } else if ( chatRoomLocation.isWorld() ) {
          chatRoomName = "World chat room";
        }        
          
        myPlayer.sendMessage( new ChatRoomCreationMessage( chatRoomName,
                                                           myPlayer.getPrimaryKey(),
                                                           chatRoomLocation
                                                          ) );
        
      } else if (actionCommand.equals("leaveChatRoom")) {
        
      } else if (actionCommand.equals("deleteChatRoom")) {
        
      } else {          
        System.out.println("Err : unknown actionCommand");      
      }
    } else {
      System.out.println("No action command found!");
    }    
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