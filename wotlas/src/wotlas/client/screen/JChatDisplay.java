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

/** JEditorPane where messages are displayed...<br>
 * Messages are logged in a HTML file
 *
 * @author Petrus
 */

public class JChatDisplay extends LogStream
{

 /*------------------------------------------------------------------------------------*/

  /** Static Link to Chat Log File.
   */
  public final static String CHAT_LOG_PREFIX = "../log/";
  public final static String CHAT_LOG_SUFFIX = ".htm";

  /** max number of messages to display on screen at the same time
   */
  static final private int MAX_DISPLAYED_MESSAGES = 25;

 /*------------------------------------------------------------------------------------*/

  /** messages number.
   */
  private int msg_number;

  /** Where messages appear.
   */
  private JEditorPane messagesPane;

  /** string buffer.
   */
  private String strBuffer;
  
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   *
   * @param chatRoom chatRoom associated to this JChatDisplay
   */
  JChatDisplay(ChatRoom chatRoom) throws FileNotFoundException {
    super( CHAT_LOG_PREFIX + chatRoom.getPrimaryKey() + CHAT_LOG_SUFFIX, true, 60*1000 );
    
    msg_number = 0;
    strBuffer = "";
    
    messagesPane = new JEditorPane();
    messagesPane.setEditable(false);
    
    MyHTMLEditorKit kit = new MyHTMLEditorKit();
    messagesPane.setEditorKit(kit);
    
    print("<font color='green'><i>Entering " + chatRoom.getName() + " chat room</i></font><br>\n");

  }

  /** To get the scroll pane
   */
  public JScrollPane getPanel() {
    JScrollPane displayScroller = new JScrollPane(messagesPane);
    return displayScroller;
  }
  
 /*------------------------------------------------------------------------------------*/

  /** To append some text
   * 
   * @param text string to append
   */
  synchronized public void appendText(String text) {
    
    if ( (text.toLowerCase().indexOf("<html")>-1)
         || (text.toLowerCase().indexOf("</html")>-1)
         || (text.toLowerCase().indexOf("<pre")>-1) ) {
      return;
    }      
      
    // Search for smileys
    text = Tools.subString(text, "0:)",  "<img width=16 height=20 src='file:../base/gui/chat/angel.gif'>");      
    text = Tools.subString(text, ":,(", "<img width=15 height=15 src='file:../base/gui/chat/cry.gif'>");
    text = Tools.subString(text, ":o",  "<img width=15 height=15 src='file:../base/gui/chat/eek.gif'>");
    text = Tools.subString(text, ":D",  "<img width=15 height=15 src='file:../base/gui/chat/laugh.gif'>");
    text = Tools.subString(text, ":(",  "<img width=15 height=15 src='file:../base/gui/chat/mad.gif'>");
    text = Tools.subString(text, ">0",  "<img width=15 height=15 src='file:../base/gui/chat/rant.gif'>");
    text = Tools.subString(text, "|I",  "<img width=15 height=24 src='file:../base/gui/chat/sleep.gif'>");
    text = Tools.subString(text, ":)",  "<img width=15 height=15 src='file:../base/gui/chat/smile.gif'>");
    text = Tools.subString(text, ":-)",  "<img width=15 height=15 src='file:../base/gui/chat/smile.gif'>");
    text = Tools.subString(text, ":|",  "<img width=15 height=15 src='file:../base/gui/chat/squint.gif'>");
    text = Tools.subString(text, ";)",  "<img width=15 height=15 src='file:../base/gui/chat/wink.gif'>");

    text = Tools.subString(text, "<|",  "<img width=15 height=15 src='file:../base/gui/chat/rolleyes.gif'>");
    text = Tools.subString(text, "://", "£//"); // to protect http://
    text = Tools.subString(text, ":/",  "<img width=15 height=22 src='file:../base/gui/chat/confused.gif'>");
    text = Tools.subString(text, "£//", "://");
    
    text = Tools.subString(text, ">|",  "<img width=15 height=15 src='file:../base/gui/chat/shake.gif'>");
    text = Tools.subString(text, ">)",  "<img width=15 height=15 src='file:../base/gui/chat/devil.gif'>");
    text = Tools.subString(text, ">D ",  "<img width=15 height=15 src='file:../base/gui/chat/evilgrin.gif'>");
    text = Tools.subString(text, ">(",  "<img width=16 height=16 src='file:../base/gui/chat/madfire.gif'>");

    text = Tools.subString(text, ";P",  "<img width=15 height=15 src='file:../base/gui/chat/flirt.gif'>");
    text = Tools.subString(text, "8D",  "<img width=15 height=15 src='file:../base/gui/chat/horny.gif'>");
    text = Tools.subString(text, ">#",  "<img width=15 height=15 src='file:../base/gui/chat/nono.gif'>");
    text = Tools.subString(text, "|O",  "<img width=15 height=15 src='file:../base/gui/chat/yawn.gif'>");

    text += "<br>";
    println(text);   
  }

 /*------------------------------------------------------------------------------------*/
  
  /** To print some text to the screen
   *
   * @param text string to print
   */  
  protected void printedText( final String text ) {

    if(messagesPane==null || text==null || text.length()==0)
       return; // nothing to print... or constructor not fully initialized
    
    // too much messages displayed ?
    msg_number++;

    if (DataManager.SHOW_DEBUG)
      System.out.println("msg_number = " + msg_number);

    if ( msg_number>MAX_DISPLAYED_MESSAGES ) {
      int pos = strBuffer.indexOf("\n");
      strBuffer = strBuffer.substring(pos+1);
      msg_number--;
    }
    
    strBuffer += text + "\n";

  	Runnable runnable = new Runnable() {
           public void run() {
              messagesPane.setText(strBuffer);
              messagesPane.repaint();
           }
        };

    SwingUtilities.invokeLater( runnable );
    
  }
}

  