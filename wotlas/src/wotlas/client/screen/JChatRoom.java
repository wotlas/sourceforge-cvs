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

import wotlas.client.*;

import wotlas.common.chat.ChatRoom;
import wotlas.common.Player;

import wotlas.libs.log.*;

import wotlas.utils.MyHTMLEditorKit;
import wotlas.utils.Tools;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.util.*;


/** Swing Chat Room where messages are displayed...
 *
 * @author Petrus, Aldiss
 */

public class JChatRoom extends JPanel implements MouseListener {

 /*------------------------------------------------------------------------------------*/

  /** ChatRoom display.
   */
  private JPanel chatTab;
  
  /** Panel where messages appear.
   */
  private JChatDisplay chatDisplay;

  /** JList of ChatRoom players.
   */
  private JList playersJList;
  private DefaultListModel playersListModel;

  /** Array of players.
   */
  private Hashtable players;
  
  /** Key of selected player.
   */
  static public String selectedPlayerKey;
  
 /*------------------------------------------------------------------------------------*/

  /** Constructor.<br>
   *  To get ChatRoom component to display in player's interface
   *
   * @param chatRoom the chatRoom to display
   */
  JChatRoom(ChatRoom chatRoom) {
    //super(false);
    super(true);
    setName(chatRoom.getPrimaryKey());
    setLayout(new BorderLayout());
    players = new Hashtable(2);


    // EAST (List of ChatRoom players)
    playersListModel = new DefaultListModel();
    
    playersJList = new JList(playersListModel);
    
    playersJList.addMouseListener(this);
    
    playersJList.setFixedCellWidth(100);

    playersJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    PlayersListRenderer playersListRenderer = new PlayersListRenderer();
//    if( selectedPlayer.getPlayerAwayMessage()!=null && !selectedPlayer.isConnectedToGame() ) {
    playersJList.setCellRenderer(playersListRenderer);
    
    JScrollPane listScroller = new JScrollPane(playersJList,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    try {
      chatDisplay = new JChatDisplay(chatRoom);
    } catch( java.io.FileNotFoundException e ) {
      e.printStackTrace();
      return;
    }

    add("Center", chatDisplay.getPanel());

    add("East", listScroller);
    
    appendText("<font color='green'><i>New chat created.</i></font>");

  }

 /*------------------------------------------------------------------------------------*/

  /** To add some players to the JList.
   */
  /*synchronized public void addPlayers(PlayerImpl players[]) {
    for (int i=0; i<players.length; i++) {
      // we detect non valid entries
     	 if(players[i]==null ||  this.players.containsKey( players[i].getPrimaryKey() ))
    	    continue;

      // ok, we add this one...
         playersListModel.addElement(players[i].getFullPlayerName());
         this.players.put( players[i].getPrimaryKey(), players[i] );
    }
  }*/

  /** To add a player to the JList.
   */
  synchronized public void addPlayer(String primaryKey, String senderFullName) {
    if( players.containsKey( primaryKey ) )
        return; // already in this chat
    if (DataManager.SHOW_DEBUG)
      System.out.println("ADDING PLAYER "+primaryKey);
    
    Hashtable playersTable = ClientDirector.getDataManager().getPlayers();
    Player newPlayer = (Player) playersTable.get(primaryKey);

    final PlayerState newPlayerItem = new PlayerState(senderFullName, newPlayer.isConnectedToGame());
    players.put( primaryKey, newPlayerItem);  

    if ( (newPlayer!=null) && newPlayer.isConnectedToGame() ) {
      Runnable runnable = new Runnable() {
        public void run() {
          if (!newPlayerItem.fullName.equals(ClientDirector.getDataManager().getMyPlayer().getFullPlayerName()))
            appendText("<font color='green'>" + newPlayerItem.fullName + " entered the chat...</font>");
          playersListModel.addElement(newPlayerItem);
          revalidate();
          repaint();
        }
      };
      SwingUtilities.invokeLater( runnable );
    } else {
      Runnable runnable = new Runnable() {
        public void run() {          
          playersListModel.addElement(newPlayerItem);       
          revalidate();
          repaint();
        }
      };
      SwingUtilities.invokeLater( runnable );
    }
  }

  /** To remove a player from the JList.
   */
  synchronized public void removePlayer(String primaryKey) {
    if( !players.containsKey( primaryKey ) )
        return; // not in this chat
    if (DataManager.SHOW_DEBUG)
      System.out.println("REMOVING PLAYER "+primaryKey);
     
    final PlayerState oldPlayerItem = (PlayerState) players.get(primaryKey);
    players.remove(primaryKey);

    Runnable runnable = new Runnable() {
      public void run() {
        if (!oldPlayerItem.fullName.equals(ClientDirector.getDataManager().getMyPlayer().getFullPlayerName()))
          appendText("<font color='green'><i>" + oldPlayerItem.fullName + " left the chat...</i></font>");
        playersListModel.removeElement(oldPlayerItem);
        revalidate();
        repaint();
      }
    };
    SwingUtilities.invokeLater( runnable );
  }

  /** To update a player's full name from the JList.
   */
  synchronized public void updatePlayer(String primaryKey, String newName) {
    if( !players.containsKey( primaryKey ) )
        return; // not in this chat
    if (DataManager.SHOW_DEBUG)
      System.out.println("UPDATING PLAYER "+primaryKey);

    final PlayerState oldPlayerItem = (PlayerState) players.get(primaryKey);
    final PlayerState newPlayerItem = new PlayerState(newName, oldPlayerItem.isNotAway);
    players.put(primaryKey, newPlayerItem);

    Runnable runnable = new Runnable() {
      public void run() {
        playersListModel.removeElement(oldPlayerItem);
        playersListModel.addElement(newPlayerItem);
        revalidate();
        repaint();
      }
    };
    SwingUtilities.invokeLater( runnable );
  }
  
  /** To update a player's state from the JList.
   */
  synchronized public void updatePlayer(String primaryKey, boolean isNotAway) {
    if( !players.containsKey( primaryKey ) )
        return; // not in this chat
    if (DataManager.SHOW_DEBUG)
      System.out.println("UPDATING PLAYER "+primaryKey);

    final PlayerState oldPlayerItem = (PlayerState) players.get(primaryKey);
    final PlayerState newPlayerItem = new PlayerState(oldPlayerItem.fullName, isNotAway);
    players.put(primaryKey, newPlayerItem);

    Runnable runnable = new Runnable() {
      public void run() {
        playersListModel.removeElement(oldPlayerItem);
        playersListModel.addElement(newPlayerItem);
        revalidate();
        repaint();
      }
    };
    SwingUtilities.invokeLater( runnable );
  }

  /** To remove all players from JList.
   */
  synchronized public void removeAllPlayers() {
    players.clear();

    Runnable runnable = new Runnable() {
      public void run() {
        playersListModel.removeAllElements();
        revalidate();
        repaint();
      }
    };
    SwingUtilities.invokeLater( runnable );

  }

  public Hashtable getPlayers() {
    return players;
  }

 /*------------------------------------------------------------------------------------*/
  
  public void appendText(String s) {
    chatDisplay.appendText(s);
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
  public void mouseReleased(MouseEvent e) {
    
    if (DataManager.SHOW_DEBUG)
      System.out.println("[JChatRoom] : clic sur (" + e.getX() + "," + e.getY() + ")");
    if (SwingUtilities.isRightMouseButton(e)) {
      
    } else {
      if (DataManager.SHOW_DEBUG)
        System.out.println("\tleft clic");
      String selectedPlayerName = (String) playersJList.getSelectedValue();      
      ClientDirector.getDataManager().getClientScreen().getChatPanel().setInputBoxText("/to:"+selectedPlayerName+":");
    }
  }

  class PlayersListRenderer extends JLabel implements ListCellRenderer {
     // This is the only method defined by ListCellRenderer.
     // We just reconfigure the JLabel each time we're called.

     public Component getListCellRendererComponent(
       JList list,
       Object value,            // value to display
       int index,               // cell index
       boolean isSelected,      // is the cell selected
       boolean cellHasFocus)    // the list and the cell have the focus
     {   
        String s = ((PlayerState) value).fullName;
        setText(s);
         
        if (isSelected) {
          setBackground(list.getSelectionBackground());
	        setForeground(list.getSelectionForeground());
	      } else {
          setBackground(list.getBackground());
          if ( ((PlayerState) value).isNotAway ) {
	          setForeground(list.getForeground());
	        } else {
	          setForeground(Color.gray);
	        }
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        return this;
     }
  }

}

  