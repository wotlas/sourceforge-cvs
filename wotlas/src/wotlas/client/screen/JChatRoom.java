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
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;

public class JChatRoom extends JPanel
{

 /*------------------------------------------------------------------------------------*/  
  
  /** max number of messages to display on screen at the same time
   */
  static final private int MAX_DISPLAYED_MESSAGES = 25;

  /** messages number.
   */
  private int msg_number;

  /** ChatRoom display.
   */
  private JPanel chatTab; 
  
  /** Where messages appear.
   */
  private JTextPane messagesPane;
  
  /** Chat document.
   */
  private DefaultStyledDocument doc_chat;
  private SimpleAttributeSet attribut;
  
  /** List of ChatRoom players.
   */
  private JList playersJList;

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
    
    msg_number = 0;
    
    // CENTER (JPanel where messages appear)
    
    
    /*HTMLEditorKit kit = new HTMLEditorKit();
   	HTMLDocument doc_chat = (HTMLDocument) kit.createDefaultDocument();
    */
    doc_chat = new DefaultStyledDocument();
    
    
    messagesPane = new JTextPane(doc_chat);
    messagesPane.setContentType("text/html");
    messagesPane.setEditable(false);
    
    JScrollPane displayScroller = new JScrollPane(messagesPane);
    
    attribut = new SimpleAttributeSet();
    StyleConstants.setFontSize(attribut,12);
    
    System.out.println("init = " + messagesPane.getText());
    //messagesPane.setText("Welcome!<br>");
    //messagesPane.setCaretPosition(messagesPane.getText().length());
    
    //Element elt = doc_chat.getElement("p");
    //System.out.println("elt = " + elt);
    
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

 /*------------------------------------------------------------------------------------*/  
  
  /** To update the list of players
   */
  synchronized public void setPlayers(Object[] players) {
    playersJList = new JList(players);
  }
  
  synchronized public void addPlayer(String value) {
    System.out.println("JChatRoom::addPlayer " + value);
  }
  
  synchronized public void removePlayer(String value) {
    System.out.println("JChatRoom::removePlayer " + value);
  }
  

 /*------------------------------------------------------------------------------------*/  


   
  synchronized public void appendText(String text) {
    // too much messages displayed ?
    msg_number++;
    


   /* StringBuffer buffer = messagesPane.getText();
    System.out.println("buffer = " + buffer);
    buffer = buffer + text + "<br>";
    System.out.println("buffer after = " + buffer);
    messagesPane.setText(buffer);*/
   

    System.out.println("msg_number = " + msg_number);
    try {
      System.out.println("dochat = " + doc_chat.getText(0,doc_chat.getLength()));
    } catch (BadLocationException e) {
        System.out.println("Chat Error:"+e.getMessage());
      } 
      
    if ( msg_number>MAX_DISPLAYED_MESSAGES )
      try {
        int pos = doc_chat.getText(0,doc_chat.getLength()).indexOf("\n");
        doc_chat.remove(0,pos+1);
        msg_number--;
	    } catch(BadLocationException e) {
        System.out.println("Chat Error:"+e.getMessage());
      } 

    // text color
    StyleConstants.setForeground( attribut, Color.blue );

    try {
      System.out.println("insertString");
      System.out.println("doc_chat.getLength() = " + doc_chat.getLength());
      doc_chat.insertString (doc_chat.getLength(), text+"<br>\n", attribut );
      //messagesPane.setDocument(doc_chat);
    } catch(BadLocationException e) {
      e.printStackTrace();
      return;
    }

    // TRICK TRICK TRICK TRICK TRICK

    // we want the scrollbars to move when some text is added...
    /*if (isShowing())
      messagesPane.setCaretPosition( doc_chat.getLength() );
*/
     // TRICK TRICK TRICK TRICK TRICK
  }
  
  
}
  
  