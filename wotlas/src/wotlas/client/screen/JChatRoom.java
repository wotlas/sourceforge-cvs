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

import wotlas.common.chat.ChatRoom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JChatRoom extends JPanel
{
  /** ChatRoom display.
   */
  JPanel chatTab; 
  
  /** List of ChatRoom players.
   */
  JList playersJList;

 /*------------------------------------------------------------------------------------*/  
 
  /** Constructor.<br>
   *  To get ChatRoom component to display in player's interface
   *
   * @param chatRoom the chatRoom to display
   */
  JChatRoom(ChatRoom chatRoom) {
    super(false);
    setName(chatRoom.getPrimaryKey());
    setLayout(new BorderLayout());
    
    // CENTER (JPanel where messages appear)
    JTextPane displayPane = new JTextPane();
    displayPane.setEditable(false);
    JScrollPane displayScroller = new JScrollPane(displayPane);
    
    // EAST (List of ChatRoom players)
    //chatPlayers = new String[chatRoom.getPlayers().size()];
    String[] chatPlayers = {"bernie","petrus","thierry","valere"};
    
    playersJList = new JList(chatPlayers);
    playersJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane listScroller = new JScrollPane(playersJList, 
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    add("Center", displayScroller); 
    add("East", listScroller);
    
  }
  
  /** To update the list of players
   */
  public void setPlayers(Object[] players) {
    playersJList = new JList(players);
  }
  
}
  
  