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

import wotlas.client.*;
import wotlas.client.screen.plugin.MacroPlugIn;

import wotlas.common.chat.*;
import wotlas.common.character.*;
import wotlas.common.message.chat.*;
import wotlas.common.Player;
import wotlas.common.universe.WotlasLocation;

import wotlas.utils.*;
import wotlas.libs.aswing.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.net.*;

import java.util.Enumeration;
import java.util.Hashtable;

/** JPanel to show the chat engine
 *
 * @author Petrus, MasterBob
 */

public class JChatPanel extends JPanel implements MouseListener, ActionListener {

 /*------------------------------------------------------------------------------------*/

   private ImageIcon iconUp = ClientDirector.getResourceManager().getImageIcon("pin.gif");

  /** Our tabbedPane
   */
   private JTabbedPane tabbedPane;

  /** Button to create a new chatRoom
   */
   private JButton b_createChatRoom;

  /** Button to leave the chatRoom
   */
   private JButton b_leaveChatRoom;

  /** Button to get help on chat commands
   */
   private JButton b_helpChat;

  /** Button to insert an image in the chat.
   */
   private JButton b_imageChat;

  /** TextField where player writes messages
   */
   private JTextField inputBox;

  /** Voice Sound Level
   */
   private JSlider chatVoiceLevel;

  /** Primary key of current ChatRoom
   */
   private String currentPrimaryKey;
 
  /** Last messages in input box
   */
   private ChatMessageHistory messageHistory;
   
   /** To iterate the players hashtable
    */   
   private Enumeration e = null;
   
   /** the name we want to autocomplete
    */
   private String autoName = "";
   
   /** content of textField
    */
   private String input;
    

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  public JChatPanel() {
    super();
    messageHistory = new ChatMessageHistory();
    tabbedPane = new JTabbedPane();

    // NORTH
    JToolBar chatToolbar = new JToolBar();
    chatToolbar.setFloatable(false);

    b_createChatRoom = new JButton(ClientDirector.getResourceManager().getImageIcon("chat-new.gif"));
    b_createChatRoom.setActionCommand("createChatRoom");
    b_createChatRoom.addActionListener(this);
    b_createChatRoom.setToolTipText("Create a new chat room");
    chatToolbar.add(b_createChatRoom);

    b_leaveChatRoom = new JButton(ClientDirector.getResourceManager().getImageIcon("chat-leave.gif"));
    b_leaveChatRoom.setActionCommand("leaveChatRoom");
    b_leaveChatRoom.addActionListener(this);
    b_leaveChatRoom.setToolTipText("Leave the current chat room");
    chatToolbar.add(b_leaveChatRoom);

    chatToolbar.add( new JToolBar.Separator(new Dimension(30,14)) );

    b_helpChat = new JButton(ClientDirector.getResourceManager().getImageIcon("chat-help.gif"));
    b_helpChat.setActionCommand("helpChat");
    b_helpChat.addActionListener(this);
    b_helpChat.setToolTipText("To display the available chat commands");
    chatToolbar.add(b_helpChat);

    b_imageChat = new JButton(ClientDirector.getResourceManager().getImageIcon("chat-image.gif"));
    b_imageChat.setActionCommand("imageChat");
    b_imageChat.addActionListener(this);
    b_imageChat.setToolTipText("To insert an image in the chat");
    chatToolbar.add(b_imageChat);

    // SOUTH
    JPanel bottomChat = new JPanel(false);
    bottomChat.setLayout(new BoxLayout(bottomChat,BoxLayout.X_AXIS));

    inputBox = new NoFocusJTextField();
    inputBox.getCaret().setVisible(true);
    //inputBox.setInputVerifier(new NoFocusInputVerifier());
    inputBox.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent keyEvent) {
          // Tab key
          if ((keyEvent.getKeyCode() == KeyEvent.VK_TAB) || (keyEvent.getKeyChar() == '\t')) {
            inputBox.setText( nameCompletion() );
            keyEvent.consume();
            return;
          }
          // Up key
          if ( keyEvent.getKeyCode()==KeyEvent.VK_UP )  {
            inputBox.setText( messageHistory.getPrevious(inputBox.getText()) );
            return;
          }
          // Down key
          if ( keyEvent.getKeyCode()==KeyEvent.VK_DOWN )  {
            inputBox.setText( messageHistory.getNext(inputBox.getText()) );
            return;
          }
          // Enter key
          if ( keyEvent.getKeyCode() == KeyEvent.VK_ENTER ) {
            okAction();
            return;
          }
          // Default
          e = null;
        }      
    });

    chatVoiceLevel = new JSlider(JSlider.HORIZONTAL, 0, 2, ChatRoom.NORMAL_VOICE_LEVEL);
    chatVoiceLevel.setMajorTickSpacing(1);
    chatVoiceLevel.setMinorTickSpacing(1);
    chatVoiceLevel.setSnapToTicks(true);
    chatVoiceLevel.setPaintTicks(true);
    chatVoiceLevel.setMaximumSize(new Dimension(80,30)); // MasterBob revision
    chatVoiceLevel.setMinimumSize(new Dimension(80,30));  // MasterBob revision
    chatVoiceLevel.setPreferredSize(new Dimension(80,30));  // MasterBob revision

    bottomChat.add( new ALabel(ClientDirector.getResourceManager().getImageIcon("chat-sound-level.gif")) );
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
  }

 /*------------------------------------------------------------------------------------*/

  /** To get current ChatRoom primaryKey
   */
  public String getMyCurrentChatPrimaryKey() {
       return currentPrimaryKey;
  }

  /** To set input box text
   *
   * @param text the new text of input box
   */
  public void setInputBoxText(String text) {
    inputBox.setText(text);
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the input box text and send the message
   *
   * @param text the new text of input box
   */
  public void sendChatMessage(String text) {
    inputBox.setText(text);
    okAction();
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
      System.out.println("JChatRoom::addJChatRoom "+jchatRoom.getName()+" created !");

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
   * @param player player to add
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
   * @param player player to remove
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


  /** To remove a player from all the ChatRooms.
   *
   * @param primaryKey primary key of Player to remove
   */
  public void removePlayerFromAllchatRooms(String primaryKey) {
    for (int i=0; i<tabbedPane.getTabCount();i++) {
        JChatRoom jchatRoom = (JChatRoom) tabbedPane.getComponentAt(i);
        jchatRoom.removePlayer(primaryKey);
    }
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

  /** action when the user wants to send a message
   */
  private void okAction() {
    String message;

    message = inputBox.getText();

    // I - We control the length of the message the user wants to send.
    if(message.length()==0)
      return;

    messageHistory.add(message);

    // We get the MacroPlugIn and process the chat message with it.
       MacroPlugIn macroPlugIn = (MacroPlugIn) ClientDirector.getDataManager().getClientScreen().getPlayerPanel().getPlugIn("Macro");

       if(macroPlugIn!=null)
          message = macroPlugIn.processMacros( message );

       if(message.length()>ChatRoom.MAXIMUM_MESSAGE_SIZE)
          message = message.substring(0,ChatRoom.MAXIMUM_MESSAGE_SIZE-4) +"...";

    // II - Any Shortcuts ?
    if (message.startsWith("/whisper")) {
      chatVoiceLevel.setValue(ChatRoom.WHISPERING_VOICE_LEVEL);
      message = message.substring(8);
      inputBox.setText(message);
      if(message.length()==0) return;
    }
    else if (message.startsWith("/shout")) {
      chatVoiceLevel.setValue(ChatRoom.SHOUTING_VOICE_LEVEL);
      message = message.substring(6);
      inputBox.setText(message);
      if(message.length()==0) return;
    }


    // III - We send the message
    DataManager dManager = ClientDirector.getDataManager();

    dManager.sendMessage( new SendTextMessage( dManager.getMyPlayer().getPrimaryKey(),
                                               dManager.getMyPlayer().getPlayerName(),
                                               getMyCurrentChatPrimaryKey(),
                                               message,
                                               (byte)chatVoiceLevel.getValue() ));

    // IV - entry reset
    inputBox.setText("");
    chatVoiceLevel.setValue(ChatRoom.NORMAL_VOICE_LEVEL);
  }

 /*------------------------------------------------------------------------------------*/

  /** To auto complete the name of a player
   */
  private String nameCompletion() {                      
    if (e==null) {     
      input = inputBox.getText(); 
      if (input.length()==0)
        return input;    
      int lastIndex = input.lastIndexOf(' ');      
      autoName = input.substring(lastIndex+1, input.length());
      input = input.substring(0, lastIndex+1);
      e = getPlayers(currentPrimaryKey).elements();
    }
/*    
    String playerKey;
    for (; e.hasMoreElements() ;) {
      playerKey = ((PlayerState) e.nextElement()).fullName;
      if ( playerKey.startsWith(autoName)) {  
        // player found      
        return input + playerKey;
      }
    }
  */  
    // no player found
    e = getPlayers(currentPrimaryKey).elements();
    return input + autoName;
  }

 /*------------------------------------------------------------------------------------*/

/** ActionListener Implementation **/

  /** Called when an action is performed.
   */
  public void actionPerformed(ActionEvent e) {
      String actionCommand = e.getActionCommand();

    // 0 - Command Control
      if (actionCommand == null)
          return;

      if (DataManager.SHOW_DEBUG)
        System.out.println("Action command : " + actionCommand);

      DataManager dataManager = ClientDirector.getDataManager();
      PlayerImpl myPlayer = dataManager.getMyPlayer();

      if( !myPlayer.getLocation().isRoom() && actionCommand.equals("createChatRoom")) {
          JOptionPane.showMessageDialog(null, "Sorry, but you can not create/leave chat channels\n"
                                        +"on World/Town Maps.", "INFORMATION", JOptionPane.INFORMATION_MESSAGE);
          return;
      }

    // 1 - Get Button
      if (actionCommand.equals("createChatRoom")) {

         WotlasLocation chatRoomLocation = myPlayer.getLocation();

         String chatRoomName = JOptionPane.showInputDialog("Please enter a Name:");

         if( chatRoomName==null || chatRoomName.length()==0 )
             return;

         if (tabbedPane.getTabCount()>=ChatRoom.MAXIMUM_CHATROOMS_PER_ROOM-1)
             b_createChatRoom.setEnabled(false);
         else
             b_createChatRoom.setEnabled(true);

         myPlayer.sendMessage( new ChatRoomCreationMessage( chatRoomName,
                                                            myPlayer.getPrimaryKey() ) );
      }
      else if (actionCommand.equals("leaveChatRoom")) {
          //removeCurrentChatRoom();
       // Sending Message
          if( !currentPrimaryKey.equals( ChatRoom.DEFAULT_CHAT ) )
              myPlayer.sendMessage( new RemPlayerFromChatRoomMessage( myPlayer.getPrimaryKey(),
                                                                     currentPrimaryKey) );
      }
      else if (actionCommand.equals("helpChat")) {
           DataManager dManager = ClientDirector.getDataManager();

           dManager.sendMessage( new SendTextMessage( dManager.getMyPlayer().getPrimaryKey(),
                                               dManager.getMyPlayer().getPlayerName(),
                                               getMyCurrentChatPrimaryKey(),
                                               "/help",
                                               ChatRoom.NORMAL_VOICE_LEVEL ));
      }
      else if (actionCommand.equals("imageChat")) {

        // ask for an image URL
           String imageURL = JOptionPane.showInputDialog("Please enter your image's URL:\nExample: http://wotlas.sf.net/images/wotlas.gif");

           if( imageURL==null || imageURL.length()==0 )
               return;

        // control the URL & image
           try {
              URL url = new URL(imageURL);
              URLConnection urlC = url.openConnection();
              urlC.connect();

              String ctype = urlC.getContentType();

              if( !ctype.startsWith("image/") ) {
                  JOptionPane.showMessageDialog(null, "The specified URL does not refer to an image !",
                                                      "Information", JOptionPane.INFORMATION_MESSAGE);
                  return;
              }

              if( urlC.getContentLength()> 50*1024 ) {
                  JOptionPane.showMessageDialog(null, "The specified image is too big (above 50kB).",
                                                      "Information", JOptionPane.INFORMATION_MESSAGE);
                  return;
              }

           }catch(Exception ex) {
              Debug.signal(Debug.ERROR,this,"Failed to get image: "+ex);
              JOptionPane.showMessageDialog(null, "Failed to get the specified image...\nCheck your URL.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
              return;
           }

        // send the Image URL to other players...
           DataManager dManager = ClientDirector.getDataManager();

           dManager.sendMessage( new SendTextMessage( dManager.getMyPlayer().getPrimaryKey(),
                                               dManager.getMyPlayer().getPlayerName(),
                                               getMyCurrentChatPrimaryKey(),
                                               "<img src='"+imageURL+"'>",
                                               ChatRoom.NORMAL_VOICE_LEVEL ));
      }
      else {
        if(DataManager.SHOW_DEBUG) {
          System.out.println("Err : unknown actionCommand");
          System.out.println("No action command found!");
        }
      }
  }
  
  /** To update players lists of all chat rooms
   *
   * @param searchedPlayer player we want to update the state
   */
  public void updateAllChatRooms(Player searchedPlayer) {
    for (int i=0; i<tabbedPane.getTabCount();i++) {      
      JChatRoom jchatRoom = (JChatRoom) tabbedPane.getComponentAt(i);
      //jchatRoom.updatePlayer(searchedPlayer.getPrimaryKey(), searchedPlayer.isConnectedToGame());
      jchatRoom.updatePlayer(searchedPlayer.getPrimaryKey(), searchedPlayer.getPlayerState().value);
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

 /*------------------------------------------------------------------------------------*/

/**
 * Private class to prevent loss of focus of a JComponent
 */
class NoFocusInputVerifier extends InputVerifier {
  /** Checks whether the JComponent's input is valid.
   */
  public boolean verify(JComponent input) {      
    // returning false prevents loss of focus
    return false;
  }
}

 /*------------------------------------------------------------------------------------*/

/**
 * Private class to prevent loss of focus of a JComponent
 */
class NoFocusJTextField extends JTextField {
  /** Override this method and return true if your JComponent manages focus
   */
  public boolean isManagingFocus() {
    // return true to inform focus manager that component is managing focus changes
    return true;
  }
}

 /*------------------------------------------------------------------------------------*/
