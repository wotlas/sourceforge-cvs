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
import wotlas.common.character.*;
import wotlas.common.message.chat.*;
import wotlas.common.Player;
import wotlas.common.universe.WotlasLocation;

import wotlas.utils.*;
import wotlas.utils.aswing.*;

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
   
  ImageIcon iconUp = new ImageIcon("../base/gui/pin.gif");
  
  /** Our tabbedPane
   */
  JTabbedPane tabbedPane;
  
  /** Button to create a new chatRoom
   */
  JButton b_createChatRoom;
  
  
  /** Button to leave the chatRoom
   */
  JButton b_leaveChatRoom;
  
  /** TextField where player writes messages
   */
  JTextField inputBox;
  
  /** Voice Sound Level
   */
  JSlider chatVoiceLevel;

  /** Primary key of current ChatRoom
   */
  String currentPrimaryKey;
   
 /*------------------------------------------------------------------------------------*/  
 
  /** Constructor.
   */ 
  public JChatPanel() {
    super();

    tabbedPane = new JTabbedPane();

    // NORTH
    JToolBar chatToolbar = new JToolBar();
    chatToolbar.setFloatable(false);
        
    b_createChatRoom = new JButton(new ImageIcon("../base/gui/chat-new.gif"));
    b_createChatRoom.setActionCommand("createChatRoom");
    b_createChatRoom.addActionListener(this);
    b_createChatRoom.setToolTipText("Create a new chat room");
    chatToolbar.add(b_createChatRoom);
    
    b_leaveChatRoom = new JButton(new ImageIcon("../base/gui/chat-leave.gif"));
    b_leaveChatRoom.setActionCommand("leaveChatRoom");
    b_leaveChatRoom.addActionListener(this);
    b_leaveChatRoom.setToolTipText("Leave the current chat room");
    chatToolbar.add(b_leaveChatRoom);
        
    // SOUTH
    JPanel bottomChat = new JPanel(false);
//    bottomChat.setLayout(new BorderLayout());
      bottomChat.setLayout(new BoxLayout(bottomChat,BoxLayout.X_AXIS)); // MasterBob revision


    inputBox = new JTextField();
    inputBox.getCaret().setVisible(true);
    inputBox.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          if ( e.getKeyCode()==KeyEvent.VK_ENTER )
            okAction();
          }
    });

//    bottomChat.add("Center", inputBox);

    chatVoiceLevel = new JSlider(JSlider.HORIZONTAL, 0, 2, ChatRoom.NORMAL_VOICE_LEVEL);
//    chatVoiceLevel = new JSlider(JSlider.VERTICAL, 0, 2, ChatRoom.NORMAL_VOICE_LEVEL);
    chatVoiceLevel.setMajorTickSpacing(1);
    chatVoiceLevel.setMinorTickSpacing(1);
    chatVoiceLevel.setSnapToTicks(true);
    chatVoiceLevel.setPaintTicks(true);
    chatVoiceLevel.setMaximumSize(new Dimension(80,30)); // MasterBob revision
    chatVoiceLevel.setMinimumSize(new Dimension(80,30));  // MasterBob revision
    chatVoiceLevel.setPreferredSize(new Dimension(80,30));  // MasterBob revision

    /**MB**/ //bottomChat.add("West", chatLevel);

    bottomChat.add( new ALabel( new ImageIcon("../base/gui/chat-sound-level.gif")) );
    bottomChat.add(chatVoiceLevel); // MasterBob revision
    bottomChat.add(inputBox); // MasterBob revision

    
    setLayout(new BorderLayout());
    add("North", chatToolbar);
    add("Center", tabbedPane);
    add("South", bottomChat);
    
    // Create main ChatRoom
    ChatRoom mainChat = new ChatRoom();
    mainChat.setPrimaryKey(ChatRoom.DEFAULT_CHAT);
    mainChat.setName("");
    JChatRoom jchatRoom = addJChatRoom(mainChat);
    currentPrimaryKey = ChatRoom.DEFAULT_CHAT;
    
    jchatRoom.addPlayer(DataManager.getDefaultDataManager().getMyPlayer().getPrimaryKey(), DataManager.getDefaultDataManager().getMyPlayer().getFullPlayerName());
  }

 /*------------------------------------------------------------------------------------*/

  /** To get current ChatRoom primaryKey
   */
  public String getMyCurrentChatPrimaryKey() {
       return currentPrimaryKey;
  }

 /*------------------------------------------------------------------------------------*/  

  /** To enable/disable a chatRoom
   *
   * @param primaryKey the ChatRoom primary key
   * @param value true to enable/false to disable
   */
  public void setEnabledAt(String primaryKey, boolean value) {
    for (int i=0; i<tabbedPane.getTabCount();i++) {
      if ( tabbedPane.getComponentAt(i).getName().equals(primaryKey) ) {
        tabbedPane.setEnabledAt(i, value);
        return;
      }
    }
  }

 /*------------------------------------------------------------------------------------*/  

  /** To reset the state of the JChatPanel
   */
   public void reset() {
       tabbedPane.setEnabledAt(0, true);
       tabbedPane.setSelectedIndex(0);
       currentPrimaryKey = ChatRoom.DEFAULT_CHAT;
       b_createChatRoom.setEnabled(true);

       if (DataManager.SHOW_DEBUG)
           System.out.println("TAB number:"+tabbedPane.getTabCount());
       for (int i=tabbedPane.getTabCount()-1; i>=0;i--) {
            if (DataManager.SHOW_DEBUG)
                System.out.println(""+tabbedPane.getComponentAt(i).getName());

            if( !tabbedPane.getComponentAt(i).getName().equals(ChatRoom.DEFAULT_CHAT)  ) {
                tabbedPane.remove(i);
                if (DataManager.SHOW_DEBUG)                
                   System.out.println("tab removed");
            }
            else {
                ( (JChatRoom) tabbedPane.getComponentAt(i) ).removeAllPlayers();
                if (DataManager.SHOW_DEBUG)
                System.out.println("DEFAULT CHAT player list reseted");
            }
        }


//       addPlayer( ChatRoom.DEFAULT_CHAT, DataManager.getDefaultDataManager().getMyPlayer() );
   }

 /*------------------------------------------------------------------------------------*/  

  /** To set the current ChatRoom.
   */
   public boolean setCurrentJChatRoom(String primaryKey) {
    boolean found = false;

    if ( primaryKey.equals(ChatRoom.DEFAULT_CHAT) ) {
      tabbedPane.setEnabledAt(0, true);
      tabbedPane.setSelectedIndex(0);
      this.currentPrimaryKey = primaryKey;
      found = true;
    } else {
      tabbedPane.setEnabledAt(0, false);
    }
      
    for (int i=1; i<tabbedPane.getTabCount();i++) {
      if ( tabbedPane.getComponentAt(i).getName().equals(primaryKey) ) {
           tabbedPane.setEnabledAt(i, true);
           tabbedPane.setSelectedIndex(i);
           this.currentPrimaryKey = primaryKey;
           found = true;
      }
      else {
           tabbedPane.setEnabledAt(i, false);           
           JChatRoom jchatRoom = (JChatRoom) tabbedPane.getComponentAt(i);
           jchatRoom.removeAllPlayers(); // we remove all the players of disabled chats...
      }
    }

    return found;
  }

 /*------------------------------------------------------------------------------------*/  

  /** To add a JChatRoom.<br>
   * called by wotlas.client.message.chat.ChatRoomCreatedMessage
   */
  public JChatRoom addJChatRoom(ChatRoom chatRoom) {
    JChatRoom jchatRoom = new JChatRoom(chatRoom);
    if (DataManager.SHOW_DEBUG)
      System.out.println("JChatRoom::addJChatRoom "+jchatRoom.getName()+" !!!!!!!!");
    if (DataManager.SHOW_DEBUG)
      System.out.println("\tcreatorPrimaryKey = " + chatRoom.getCreatorPrimaryKey());
    tabbedPane.addTab(chatRoom.getName(), iconUp, jchatRoom, chatRoom.getName() + " channel");

      if (tabbedPane.getTabCount()>=ChatRoom.MAXIMUM_CHATROOMS_PER_ROOM)
          b_createChatRoom.setEnabled(false);
      else
          b_createChatRoom.setEnabled(true);

    return jchatRoom;
  }
  
  /** To remove a JChatRoom.
   *
   * @param primaryKey ChatRoom primary key
   */
  public boolean removeJChatRoom(String primaryKey) {
    // We can't remove the first ChatRoom
    for (int i=1; i<tabbedPane.getTabCount();i++) {
      if ( tabbedPane.getComponentAt(i).getName().equals(primaryKey) ) {
        if (DataManager.SHOW_DEBUG)
          System.out.println("removeChatRoom");
        tabbedPane.remove(i);

        if(primaryKey.equals(currentPrimaryKey))
           setCurrentJChatRoom(ChatRoom.DEFAULT_CHAT);

        if (tabbedPane.getTabCount()>=ChatRoom.MAXIMUM_CHATROOMS_PER_ROOM)
              b_createChatRoom.setEnabled(false);
        else
              b_createChatRoom.setEnabled(true);

        return true;
      }
    }
    if (DataManager.SHOW_DEBUG)
      System.out.println("ERROR : Couldn't removeJChatRoom");
    return false;
  }
  
  /** To remove currentChatRoom
   *
  public void removeCurrentChatRoom() {
    int chatTabIndex = tabbedPane.getSelectedIndex();
    // We can't remove first ChatRoom
     if (chatTabIndex == 0)
         return;

     tabbedPane.remove(chatTabIndex);

     if (tabbedPane.getTabCount()>=ChatRoom.MAXIMUM_CHATROOMS_PER_ROOM)
           b_createChatRoom.setEnabled(false);
     else
           b_createChatRoom.setEnabled(true);
  }
*/  
  /** To change the title of main JChatRoom associated to the room (first index)
   */
  public void changeMainJChatRoom(String roomName) {    
    tabbedPane.setTitleAt(0, roomName);
    JChatRoom jchatRoom = (JChatRoom) tabbedPane.getComponentAt(0);
    jchatRoom.removeAllPlayers();
  }
  
  /** To get a JChatRoom.
   *
   * @param primaryKey primary key of JChatRoom we want to get
   */
  public JChatRoom getJChatRoom(String primaryKey) {
    for (int i=0; i<tabbedPane.getTabCount();i++) {
      if ( tabbedPane.getComponentAt(i).getName().equals(primaryKey) ) {
        if (DataManager.SHOW_DEBUG)
          System.out.println("getJChatRoom");
        return (JChatRoom) tabbedPane.getComponentAt(i);
      }
    }
    if (DataManager.SHOW_DEBUG)
      System.out.println("ERROR : Couldn't getJChatRoom");
    return null;
  }
  
  /** To get current JChatRoom.
   */
  public JChatRoom getCurrentJChatRoom() {
    return getJChatRoom(currentPrimaryKey);
  }

  /** To set the current active window.
   *
   * @param primaryKey primary key of current ChatRoom
   *
  public boolean setCurrentJChatRoom(String primaryKey) {
    this.currentPrimaryKey = primaryKey;
    for (int i=0; i<tabbedPane.getTabCount();i++) {
      if ( tabbedPane.getComponentAt(i).getName().equals(primaryKey) ) {
        tabbedPane.setEnabledAt(i, true);
        tabbedPane.setSelectedIndex(i);
        return true;
      }
    }
    System.out.println("ERROR : Couldn't setCurrentJChatRoom");
    return false;
  }
*/
 /*------------------------------------------------------------------------------------*/  
  
  /** To add a player to a JChatRoom.
   *
   * @param primaryKey primary key of ChatRoom to modify
   * @param playerPrimaryKey primary key of Player to add
   */
  public boolean addPlayer(String primaryKey, PlayerImpl player) {
    for (int i=0; i<tabbedPane.getTabCount();i++) {
      if ( tabbedPane.getComponentAt(i).getName().equals(primaryKey) ) {
        JChatRoom jchatRoom = (JChatRoom) tabbedPane.getComponentAt(i);
        jchatRoom.addPlayer(player.getPrimaryKey(), player.getFullPlayerName());
        return true;
      }
    }
    if (DataManager.SHOW_DEBUG)
      System.out.println("ERROR : Couldn't addPlayer");
    return false;
  }
  
  /** To remove a player from a ChatRoom.
   *
   * @param primaryKey primary key of ChatRoom to modify
   * @param playerPrimaryKey primary key of Player to remove
   */
  public boolean removePlayer(String primaryKey, PlayerImpl player) {
    for (int i=0; i<tabbedPane.getTabCount();i++) {
      if ( tabbedPane.getComponentAt(i).getName().equals(primaryKey) ) {
        JChatRoom jchatRoom = (JChatRoom) tabbedPane.getComponentAt(i);
        jchatRoom.removePlayer(player.getPrimaryKey());
        return true;
      }
    }
    if (DataManager.SHOW_DEBUG)
      System.out.println("ERROR : Couldn't removePlayer");
    return false;
  }
  
  /** To get the list of players of a ChatRoom
   *
   * @param primaryKey primary key of the ChatRoom
   */
  public Hashtable getPlayers(String primaryKey) {

    for (int i=0; i<tabbedPane.getTabCount();i++) {
      if ( tabbedPane.getComponentAt(i).getName().equals(primaryKey) ) {
        JChatRoom jchatRoom = (JChatRoom) tabbedPane.getComponentAt(i);
        return jchatRoom.getPlayers();
      }
    }
    if (DataManager.SHOW_DEBUG)
      System.out.println("ERROR : Couldn't get players");
    return null;
  }
   
 /*------------------------------------------------------------------------------------*/ 
  
  /** To write some text in client's window
   */
  
 /*------------------------------------------------------------------------------------*/ 

  /** action when the user wants to send a message
   */
  private void okAction() {
    String message = inputBox.getText();

    if(message.length()==0)
      return;

    DataManager dManager = DataManager.getDefaultDataManager();
    
    // Shortcuts
    if (message.startsWith("/whisper")) {
      chatVoiceLevel.setValue(ChatRoom.WHISPERING_VOICE_LEVEL);
      message = message.substring(9);
    } else if (message.startsWith("/shout")) {
      chatVoiceLevel.setValue(ChatRoom.SHOUTING_VOICE_LEVEL);
      message = message.substring(7);
    }
    
    dManager.sendMessage( new SendTextMessage( dManager.getMyPlayer().getPrimaryKey(),
                                               dManager.getMyPlayer().getFullPlayerName(),
                                               getMyCurrentChatPrimaryKey(),
                                               message,
                                               (byte)chatVoiceLevel.getValue() ));

    // entry reset
    inputBox.setText("");
    chatVoiceLevel.setValue(ChatRoom.NORMAL_VOICE_LEVEL);
  }

 /*------------------------------------------------------------------------------------*/ 

/** ActionListener Implementation **/

  /** Called when an action is performed.
   */
  public void actionPerformed(ActionEvent e) {
      String actionCommand = e.getActionCommand();
 
      if (actionCommand == null)
          return;

      if (DataManager.SHOW_DEBUG)
        System.out.println("Action command : " + actionCommand);
      DataManager dataManager = DataManager.getDefaultDataManager();
      PlayerImpl myPlayer = dataManager.getMyPlayer();

      if( !myPlayer.getLocation().isRoom() ) {
          JOptionPane.showMessageDialog(null, "Sorry, but you can not create/leave chat channels\n"
                                        +"on World/Town Maps.", "INFORMATION", JOptionPane.INFORMATION_MESSAGE); 
          return;
      }
    
    // 1 - Get Button
      if (actionCommand.equals("createChatRoom")) {
        
         WotlasLocation chatRoomLocation = myPlayer.getLocation();

         String chatRoomName = JOptionPane.showInputDialog("Please enter a Name:"); 

         if( chatRoomName.length()==0 )
             return;

         if (tabbedPane.getTabCount()>=ChatRoom.MAXIMUM_CHATROOMS_PER_ROOM-1)
             b_createChatRoom.setEnabled(false);
         else
             b_createChatRoom.setEnabled(true);

         myPlayer.sendMessage( new ChatRoomCreationMessage( chatRoomName,
                                                            myPlayer.getPrimaryKey(),
                                                            chatRoomLocation ));
      }
      else if (actionCommand.equals("leaveChatRoom")) {
          //removeCurrentChatRoom();
       // Sending Message
          if( !currentPrimaryKey.equals( ChatRoom.DEFAULT_CHAT ) )
              myPlayer.sendMessage( new RemPlayerFromChatRoomMessage( myPlayer.getPrimaryKey(),
                                                                     currentPrimaryKey) );
      }
      else {
        if (DataManager.SHOW_DEBUG) {
          System.out.println("Err : unknown actionCommand");      
          System.out.println("No action command found!");
        }
      }    
  }

 /*------------------------------------------------------------------------------------*/ 

/** MouseListener Implementation **/

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