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

public class JChatRoom extends JPanel implements MouseListener
{

 /*------------------------------------------------------------------------------------*/

  /** ChatRoom display.
   */
  private JPanel chatTab;
  
  /** Panel where messages appear.
   */
  private JChatDisplay chatDisplay;

  /** List of ChatRoom players.
   */
  private JList playersJList;
  private DefaultListModel playersListModel;

  /** Player list
   */
  private Hashtable players;
  
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
    
//    PlayersListRenderer playersListRenderer = new PlayersListRenderer();
//    if( selectedPlayer.getPlayerAwayMessage()!=null && !selectedPlayer.isConnectedToGame() ) {
//    playersJList.setRenderer(playersListRenderer);
    
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
    
    appendText("<font color='green'><i>new chat created</i></font>");

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

    final String strNewName = senderFullName;
    players.put( primaryKey, senderFullName );

    Runnable runnable = new Runnable() {
      public void run() {
        if (!strNewName.equals(DataManager.getDefaultDataManager().getMyPlayer().getFullPlayerName()))
          appendText("<font color='green'>" + strNewName + " entered the chat...</font>");
        playersListModel.addElement(strNewName);       
        revalidate();
        repaint();
      }
    };
    SwingUtilities.invokeLater( runnable );

  }

  /** To remove a player from the JList.
   */
  synchronized public void removePlayer(String primaryKey) {
    if( !players.containsKey( primaryKey ) )
        return; // not in this chat
    if (DataManager.SHOW_DEBUG)
      System.out.println("REMOVING PLAYER "+primaryKey);

    final String strOldName = (String) players.get(primaryKey);
    players.remove(primaryKey);

    Runnable runnable = new Runnable() {
      public void run() {
        if (!strOldName.equals(DataManager.getDefaultDataManager().getMyPlayer().getFullPlayerName()))
          appendText("<font color='green'><i>" + strOldName + " left the chat...</i></font>");
        playersListModel.removeElement(strOldName);
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
      if (DataManager.SHOW_DEBUG)
        System.out.println("\tright clic");

    } else {
      if (DataManager.SHOW_DEBUG)
        System.out.println("\tleft clic");
      String selectedPlayerName = (String) playersJList.getSelectedValue();      
      DataManager.getDefaultDataManager().getChatPanel().setInputBoxText("/to:"+selectedPlayerName+":");
      
    }
  }
}

  