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
  
  static final short CHAT_WHISPER = 0;
  static final short CHAT_SPEAK = 1;
  static final short CHAT_SHOUT = 2;
  
  static final int MAX_CHATROOMS = 5;
 
  ImageIcon iconUp = new ImageIcon("..\\base\\graphics\\gui\\chat\\myrddraal.gif");
  ImageIcon iconDown = new ImageIcon("..\\base\\graphics\\gui\\chat\\madmyrddraal.gif");
  
  /** Our tabbedPane
   */
  JTabbedPane tabbedPane;
  
  /** Button to create a new chatRoom
   */
  JButton b_createChatRoom;
  
  /** Button to delete the chatRoom
   */
  JButton b_deleteChatRoom;
  
  /** Button to leave the chatRoom
   */
  JButton b_leaveChatRoom;
  
  /** TextField where player writes messages
   */
  JTextField inputBox;
 
 /*------------------------------------------------------------------------------------*/  
 
  /** Constructor.
   */ 
  public JChatPanel() {
    super();

    tabbedPane = new JTabbedPane();

    // NORTH
    JToolBar chatToolbar = new JToolBar();
    chatToolbar.setFloatable(false);
        
    b_createChatRoom = new JButton(new ImageIcon("..\\base\\graphics\\gui\\chat\\smile.gif"));
    b_createChatRoom.setActionCommand("createChatRoom");
    b_createChatRoom.addActionListener(this);
    b_createChatRoom.setToolTipText("Create a new chat room");
    chatToolbar.add(b_createChatRoom);
    
    b_leaveChatRoom = new JButton(new ImageIcon("..\\base\\graphics\\gui\\chat\\perplexed.gif"));
    b_leaveChatRoom.setActionCommand("leaveChatRoom");
    b_leaveChatRoom.addActionListener(this);
    b_leaveChatRoom.setToolTipText("Leave the current chat room");
    chatToolbar.add(b_leaveChatRoom);
    
    b_deleteChatRoom = new JButton(new ImageIcon("..\\base\\graphics\\gui\\chat\\dead.gif"));
    b_deleteChatRoom.setActionCommand("deleteChatRoom");
    b_deleteChatRoom.addActionListener(this);
    b_deleteChatRoom.setToolTipText("Delete the current chat room");
    chatToolbar.add(b_deleteChatRoom);
    
    // SOUTH
    JPanel bottomChat = new JPanel(false);
    bottomChat.setLayout(new BorderLayout());
    inputBox = new JTextField();
    inputBox.getCaret().setVisible(true);
    inputBox.addKeyListener(new KeyAdapter()
      {
        public void keyReleased(KeyEvent e) {
          if ( e.getKeyCode()==KeyEvent.VK_ENTER )
            okAction();
          }
	    });
    bottomChat.add("Center", inputBox);
    JSlider chatLevel = new JSlider(JSlider.HORIZONTAL, 0, 2, CHAT_SPEAK);
    chatLevel.setMajorTickSpacing(1);
    chatLevel.setMinorTickSpacing(1);
    chatLevel.setSnapToTicks(true);
    chatLevel.setPaintTicks(true);
    bottomChat.add("West", chatLevel);
    // Add the tabbed pane to this panel.
    //setLayout(new GridLayout(1, 1, 0, 0)); 
    
    setLayout(new BorderLayout());
    add("North", chatToolbar);
    add("Center", tabbedPane);
    add("South", bottomChat);
    
    // Create some ChatRooms
    ChatRoom chat1 = new ChatRoom();
    chat1.setPrimaryKey("chat-1");
    chat1.setName("chatRoom one");
    
    ChatRoom chat2 = new ChatRoom();
    chat2.setPrimaryKey("chat-2");
    chat2.setName("chatRoom two");
    
    ChatRoom chat3 = new ChatRoom();
    chat3.setPrimaryKey("chat-3");
    chat3.setName("chatRoom three");
    
    addChatRoom(chat1);
    addChatRoom(chat2);
    addChatRoom(chat3);
    
    removeChatRoom("chat-2");
  }

 /*------------------------------------------------------------------------------------*/  

  /** To get current ChatRoom primaryKey
   */
  public String getMyChatRoomID() {
    return tabbedPane.getSelectedComponent().getName();
  }

 /*------------------------------------------------------------------------------------*/  
  
  /** To enable/disable a chatRoom
   *
   * @param primaryKey the ChatRoom primary key
   * @param value true to enable/false to disable
   */
  public void setEnabledAt(String primaryKey, boolean value) {
    Hashtable chatRooms;
    tabbedPane.setEnabledAt(3, value);
  }
  
 /*------------------------------------------------------------------------------------*/  

  /** Add a new ChatRoom to the interface.<br>
   * called by wotlas.client.message.chat.ChatRoomCreatedMessage
   */
  public void addChatRoom(ChatRoom chatRoom) {
    Component chatTab = initChatRoom(chatRoom);
    tabbedPane.addTab("#" + chatRoom.getName(), iconUp, chatTab, chatRoom.getName() + " channel");    
  }
  
 /*------------------------------------------------------------------------------------*/  

  /** To remove a chatRoom.   
   *
   * @param chatRoom ChatRoom to remove
   * @return false if the chatRoom doesn't exists, true otherwise
   */
  public boolean removeChatRoom(ChatRoom chatRoom) {
    return removeChatRoom(chatRoom.getPrimaryKey());
  }
  
  /** To remove a chatRoom.
   *
   * @param primaryKey the primary key of the ChatRoom to remove
   * @return false if the chatRoom doesn't exists, true otherwise
   */
  public boolean removeChatRoom(String primaryKey) {
    // We can't remove the first ChatRoom
    for (int i=1; i<tabbedPane.getTabCount();i++) {
      if ( tabbedPane.getComponentAt(i).getName().equals(primaryKey) ) {
        System.out.println("removeChatRoom");
        tabbedPane.remove(i);
        return true;
      }
    }
    
    System.out.println("ERROR : Couldn't removeChatRoom");
    return false;
  }
  
  /** To remove currentChatRoom
   */
  public void removeCurrentChatRoom() {
    int chatTabIndex = tabbedPane.getSelectedIndex();
    // We can't remove first ChatRoom
    if (chatTabIndex == 0)
      return;
    tabbedPane.remove(chatTabIndex);
  }

 /*------------------------------------------------------------------------------------*/  
  
  /** To create a new ChatRoom.
   */
  private Component initChatRoom(ChatRoom chatRoom) {
    JPanel chatTab = new JPanel(false);
    chatTab.setName(chatRoom.getPrimaryKey());
    chatTab.setLayout(new BorderLayout());
    
    // CENTER (JPanel where messages appear)
    JTextPane displayPane = new JTextPane();
    displayPane.setEditable(false);
    JScrollPane displayScroller = new JScrollPane(displayPane);
    
    // EAST (List of ChatRoom players)
    //String[] chatPlayers = new String[chatRoom.getPlayers().size()];
    String[] chatPlayers = {"bernie","petrus","thierry","valere"};
    JList playersList = new JList(chatPlayers);
    playersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane listScroller = new JScrollPane(playersList, 
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    chatTab.add("Center", displayScroller); 
    chatTab.add("East", listScroller);
    
    return chatTab; 
  }

 /*------------------------------------------------------------------------------------*/ 

  /** action when the user wants to send a message
   */
  private void okAction() {
    String message = inputBox.getText();

    if (message.length()==0)
      return;

    DataManager.getDefaultDataManager().sendMessage(new SendPublicTxtMessage( getMyChatRoomID(), message ) );

    // entry reset
    inputBox.setText("");
  }

 /*------------------------------------------------------------------------------------*/ 

  /** Called when an action is performed.
   */
  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    if (actionCommand != null) {
      System.out.println("Action command : " + actionCommand);
      DataManager dataManager = DataManager.getDefaultDataManager();
      PlayerImpl myPlayer = dataManager.getMyPlayer();        
      if (actionCommand.equals("createChatRoom")) {
        if (tabbedPane.getTabCount()==MAX_CHATROOMS) {
          b_createChatRoom.setEnabled(false);
        } else {
          b_createChatRoom.setEnabled(true);
        }
        
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
        removeCurrentChatRoom();
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