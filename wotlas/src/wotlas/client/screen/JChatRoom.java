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

import wotlas.utils.MyHTMLEditorKit;
import wotlas.utils.MyImageView;
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

public class JChatRoom extends JPanel
{

 /*------------------------------------------------------------------------------------*/

  /** max number of messages to display on screen at the same time
   */
  static final private int MAX_DISPLAYED_MESSAGES = 25;

 /*------------------------------------------------------------------------------------*/

  /** messages number.
   */
  private int msg_number;

  /** ChatRoom display.
   */
  private JPanel chatTab;

  /** Where messages appear.
   */
  private JEditorPane messagesPane;

  /** Chat document.
   */
  private DefaultStyledDocument doc_chat;
  private SimpleAttributeSet attribut;
  private String strBuffer;

  /** List of ChatRoom players.
   */
  private JList playersJList;
  private DefaultListModel playersListModel;

  /** Player list
   */
  private Hashtable players;

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

    msg_number = 0;

    // CENTER (JPanel where messages appear)
    /*HTMLEditorKit kit = new HTMLEditorKit();
   	HTMLDocument doc_chat = (HTMLDocument) kit.createDefaultDocument();
    */
//    doc_chat = new DefaultStyledDocument();

//    messagesPane = new JTextPane(doc_chat); ALDISS
    messagesPane = new JEditorPane();
    messagesPane.setEditable(false);
    /*
    messagesPane.setContentType("text/html");
    messagesPane.setEditable(false);
    */
    
    MyHTMLEditorKit kit = new MyHTMLEditorKit();
    messagesPane.setEditorKit(kit);
    
    /*Document doc = messagesPane.getDocument();
    
    strBuffer = "<font color='green'><i>new chat created</i> <img src='file:../base/gui/chat/cry.gif'></font>";
    StringReader reader = new StringReader(strBuffer);
    try {
      kit.read(reader, doc, 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("doc.getLength() = " + doc.getLength());
    try {
      System.out.println("doc = " + doc.getText(0, doc.getLength()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    reader = new StringReader("<br>toto <i>est</i> beau!!<br>Eh <b>oui</b>");
    try {
      kit.read(reader, doc, doc.getLength());
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("doc.getLength() = " + doc.getLength());
    try {
      System.out.println("doc = " + doc.getText(0, doc.getLength()));
    } catch (Exception e) {
      e.printStackTrace();
    }*/
    
    JScrollPane displayScroller = new JScrollPane(messagesPane);
    
    attribut = new SimpleAttributeSet();
    StyleConstants.setFontSize(attribut,12);

    /*
    strBuffer = "<font color='green'><i>new chat created</i></font><br>\n";;
    messagesPane.setText("<html><body>" + strBuffer + "</body></html>");
    */

    //messagesPane.setCaretPosition(messagesPane.getText().length());

    // EAST (List of ChatRoom players)
    playersListModel = new DefaultListModel();
    
    playersJList = new JList(playersListModel);
    playersJList.setFixedCellWidth(100);

    playersJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    
    JScrollPane listScroller = new JScrollPane(playersJList,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    //playersJList.setPreferredSize(new Dimension(100,0));
    //playersJList.setMinimumSize(new Dimension(100,0));
    //playersJList.setMaximumSize(new Dimension(100,0));
    
    add("Center", displayScroller);
    add("East", listScroller);

    strBuffer = "";
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
          ;//appendText("<font color='green'>" + strNewName + " entered the chat...</font>");
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

  synchronized public void appendText(String text) {
    
    if ( (text.toLowerCase().indexOf("<html")>-1)
         || (text.toLowerCase().indexOf("</html")>-1)
         || (text.toLowerCase().indexOf("<pre")>-1) ) {
      return;
    }
    
    // too much messages displayed ?
    msg_number++;

    /*StringBuffer buffer = messagesPane.getText();
    System.out.println("buffer = " + buffer);
    buffer = buffer + text + "<br>";
    System.out.println("buffer after = " + buffer);
    messagesPane.setText(buffer);*/

    if (DataManager.SHOW_DEBUG)
      System.out.println("msg_number = " + msg_number);
    /*try {
      System.out.println("dochat = " + doc_chat.getText(0,doc_chat.getLength()));
      System.out.println("messagesPane.getText() = " + messagesPane.getText());
    } catch (BadLocationException e) {
        System.out.println("Chat Error:"+e.getMessage());
    }*/

    if ( msg_number>MAX_DISPLAYED_MESSAGES )
      //try
      {
        /*int pos = doc_chat.getText(0,doc_chat.getLength()).indexOf("\n");
        doc_chat.remove(0,pos+1);*/

        int pos = strBuffer.indexOf("\n");
        strBuffer = strBuffer.substring(pos+1);
        msg_number--;
	    /*} catch(BadLocationException e) {
        System.out.println("Chat Error:"+e.getMessage());*/
      }

      // Search for commands

      
      
      // Search for smileys
      text = Tools.subString(text, "0:)",  "<img width=16 height=20 src='file:../base/gui/chat/angel.gif'>");      
      text = Tools.subString(text, ":,(", "<img width=15 height=15 src='file:../base/gui/chat/cry.gif'>");
      text = Tools.subString(text, ":o",  "<img width=15 height=15 src='file:../base/gui/chat/eek.gif'>");
      text = Tools.subString(text, ":D",  "<img width=15 height=15 src='file:../base/gui/chat/laugh.gif'>");
      text = Tools.subString(text, ":(",  "<img width=15 height=15 src='file:../base/gui/chat/mad.gif'>");
      text = Tools.subString(text, ">0",  "<img width=15 height=15 src='file:../base/gui/chat/rant.gif'>");
      text = Tools.subString(text, "|I",  "<img width=15 height=24 src='file:../base/gui/chat/sleep.gif'>");
      text = Tools.subString(text, ":)",  "<img width=15 height=15 src='file:../base/gui/chat/smile.gif'>");
      text = Tools.subString(text, ":|",  "<img width=15 height=15 src='file:../base/gui/chat/squint.gif'>");
      text = Tools.subString(text, ";)",  "<img width=15 height=15 src='file:../base/gui/chat/wink.gif'>");

      text = Tools.subString(text, "<|",  "<img width=15 height=15 src='file:../base/gui/chat/rolleyes.gif'>");
      text = Tools.subString(text, ":?",  "<img width=15 height=22 src='file:../base/gui/chat/confused.gif'>");
      text = Tools.subString(text, ">|",  "<img width=15 height=15 src='file:../base/gui/chat/shake.gif'>");
      text = Tools.subString(text, ">)",  "<img width=15 height=15 src='file:../base/gui/chat/devil.gif'>");
      text = Tools.subString(text, ">D",  "<img width=15 height=15 src='file:../base/gui/chat/evilgrin.gif'>");
      text = Tools.subString(text, ">(",  "<img width=16 height=16 src='file:../base/gui/chat/madfire.gif'>");

      text = Tools.subString(text, ";P",  "<img width=15 height=15 src='file:../base/gui/chat/flirt.gif'>");
      text = Tools.subString(text, "8D",  "<img width=15 height=15 src='file:../base/gui/chat/horny.gif'>");
      text = Tools.subString(text, ">#",  "<img width=15 height=15 src='file:../base/gui/chat/nono.gif'>");
      text = Tools.subString(text, "|O",  "<img width=15 height=15 src='file:../base/gui/chat/yawn.gif'>");

    //try {
      if (DataManager.SHOW_DEBUG)
        System.out.println("insertString");
      //doc_chat.insertString (doc_chat.getLength(), text+"<br>\n", attribut );

      strBuffer += text + "<br>\n";

      Runnable runnable = new Runnable() {
        public void run() {
          messagesPane.setText(strBuffer);
          messagesPane.repaint();
        }
      };
      SwingUtilities.invokeLater( runnable );

    /*} catch(BadLocationException e) {
      e.printStackTrace();
      return;
    }*/

    // TRICK TRICK TRICK TRICK TRICK
    //System.out.println( "LENGTH str:"+strBuffer.length()+" mTxt:"+messagesPane.getText().length());
    // we want the scrollbars to move when some text is added...
    //if (isShowing())
    //  messagesPane.setCaretPosition( strBuffer.length() );
    // TRICK TRICK TRICK TRICK TRICK
  }

 /*------------------------------------------------------------------------------------*/

}

  