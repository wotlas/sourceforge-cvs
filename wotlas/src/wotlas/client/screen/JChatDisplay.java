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
 *  Messages are logged in a HTML file
 *
 * @author Petrus
 */

public class JChatDisplay extends LogStream {

 /*------------------------------------------------------------------------------------*/

  /** Chat Log NAme Format
   */
    public final static String CHAT_LOG_SUFFIX = ".html";

  /** Smiley Home.
   */
    public final static String SMILEYS_HOME = "gui"+File.separator+"chat"+File.separator;

  /** The smileys we recognize :
   */
    public final static String SMILEYS[][] = {
     /**   smiley   width   height  file name         **/
        {  "0:)",   "16",   "20",   "angel.gif"     },
        {  ":,(",   "15",   "15",   "cry.gif"       },
        {  ":o",    "15",   "15",   "eek.gif"       },
        {  ":D",    "15",   "15",   "laugh.gif"     },
        {  ":(",    "15",   "15",   "mad.gif"       },
        {  ">0",    "15",   "15",   "rant.gif"      },
        {  "|I",    "15",   "24",   "sleep.gif"     },
        {  ":)",    "15",   "15",   "smile.gif"     },
        {  ":-)",   "15",   "15",   "smile.gif"     },
        {  ":|",    "15",   "15",   "squint.gif"    },
        {  ";)",    "15",   "15",   "wink.gif"      },
        {  "<|",    "15",   "15",   "rolleyes.gif"  },
        {  ":/",    "15",   "22",   "confused.gif"  },
        {  ">|",    "15",   "15",   "shake.gif"     },
        {  ">)",    "15",   "15",   "devil.gif"     },
        {  ">D ",   "15",   "15",   "evilgrin.gif"  },
        {  ">(",    "16",   "16",   "madfire.gif"   },
        {  ";P",    "15",   "15",   "flirt.gif"     },
        {  "8D",    "15",   "15",   "horny.gif"     },
        {  ">#",    "15",   "15",   "nono.gif"      },
        {  "|O",    "15",   "15",   "yawn.gif"      },
     };


  /** max number of messages to display on screen at the same time
   */
    public final static int MAX_DISPLAYED_MESSAGES = 25;

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
    public JChatDisplay(ChatRoom chatRoom) throws FileNotFoundException {
         super( ClientDirector.getResourceManager().getLog( chatRoom.getPrimaryKey() + CHAT_LOG_SUFFIX),
                true, 60*1000 );

         msg_number = 0;
         strBuffer = "";

         messagesPane = new JEditorPane();
         messagesPane.setEditable(false);
    
         MyHTMLEditorKit kit = new MyHTMLEditorKit();
         messagesPane.setEditorKit(kit);
    
         print("<font color='green'><i>Entering " + chatRoom.getName() + " chat room</i></font><br>\n");
    }

 /*------------------------------------------------------------------------------------*/

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

       if ( text==null || text.length()==0
            || (text.toLowerCase().indexOf("<html")>-1)
            || (text.toLowerCase().indexOf("</html")>-1)
            || (text.toLowerCase().indexOf("<pre")>-1) ) {
         return;
       }

    // Search for smileys
       String smileysHome = ClientDirector.getResourceManager().getBase(SMILEYS_HOME);

       for( int i=0; i<SMILEYS.length; i++ ) {
       	  int pos=0, posD=0;
       	  StringBuffer buf = new StringBuffer("");

          while( (pos=text.indexOf(SMILEYS[i][0], posD ))>=0 ) {
              buf.append( text.substring(posD,pos) );

              if( (pos==0 && SMILEYS[i][0].length()==text.length() )
                  || (pos==0 && text.charAt(SMILEYS[i][0].length())==' ' )
                  || (pos!=0 && text.charAt(pos-1)==' ' && (pos+SMILEYS[i][0].length())==text.length())
                  || (pos!=0 && text.charAt(pos-1)==' ' && text.charAt(pos+SMILEYS[i][0].length())==' ') ) {
                  buf.append(" <img width='");
                  buf.append(SMILEYS[i][1]);
                  buf.append("' height='");
                  buf.append(SMILEYS[i][2]);
                  buf.append("' src='file:");
                  buf.append(smileysHome);
                  buf.append(SMILEYS[i][3]);
                  buf.append("'> ");
              }
              else
                  buf.append(SMILEYS[i][0]);

              posD = pos + SMILEYS[i][0].length();
          }

          buf.append( text.substring( posD, text.length() ) );
          text = buf.toString();
       }

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

 /*------------------------------------------------------------------------------------*/

   /** Finalize this chat display.
    */
    protected void finalize() {
        flush();
    }

 /*------------------------------------------------------------------------------------*/

}

