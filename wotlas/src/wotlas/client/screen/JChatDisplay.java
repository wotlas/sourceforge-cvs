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

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLDocument;
import wotlas.client.ClientDirector;
import wotlas.client.DataManager;
import wotlas.common.chat.ChatRoom;
import wotlas.libs.log.LogStream;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.MyHTMLEditorKit;

/** JEditorPane where messages are displayed...<br>
 *  Messages are logged in a HTML file
 *
 * @author Petrus
 */
public class JChatDisplay extends LogStream {

    /*------------------------------------------------------------------------------------*/
    /** Chat Log Name Format
     */
    public final static String CHAT_LOG_SUFFIX = ".html";

    /** The smileys we recognize :
     */
    public final static String SMILEYS[][] = {
        /**   smiley        width   height  file name         **/
        {"0:)", "16", "20", "angel.gif"}, {":,(", "15", "15", "cry.gif"}, {"<,(", "16", "16", "cry2.gif"}, {":o", "15", "15", "eek.gif"}, {":D", "15", "15", "laugh.gif"}, {":(", "15", "15", "mad.gif"}, {">o", "15", "15", "rant.gif"}, {":)", "15", "15", "smile.gif"}, {":-)", "15", "15", "smile.gif"}, {":|", "15", "15", "squint.gif"}, {";)", "15", "15", "wink.gif"}, {"<|", "15", "15", "rolleyes.gif"}, {":/", "15", "22", "confused.gif"}, {">)", "15", "15", "devil.gif"}, {">D", "15", "15", "evilgrin.gif"}, {">(", "16", "16", "madfire.gif"}, {">|", "15", "15", "hum.gif"}, {";P", "15", "15", "flirt.gif"}, {">#", "15", "15", "nono.gif"}, {"|o", "15", "15", "yawn.gif"}, {"8D", "15", "15", "horny.gif"}, {"8)", "15", "15", "cool.gif"}, {"8(", "15", "15", "argh.gif"}, {";D", "120", "15", "roll.gif"}, {"%(", "15", "15", "arg.gif"}, {":yes:", "15", "15", "yes.gif"}, {":no:", "15", "15", "shake.gif"}, {":sleep:", "15", "24", "sleep.gif"}, {":storm:", "31", "34", "storm.gif"}, {":agree:", "15", "15", "agree.gif"}, {":coffee:", "15", "15", "coffee.gif"}, {":disagree:", "15", "15", "disagree.gif"}, {":help:", "23", "15", "help.gif"}, {":warning:", "15", "15", "warning.gif"}, {":idea:", "60", "60", "idea.gif"}, {":music:", "18", "22", "note.gif"}, {":phone:", "25", "22", "phone.gif"},     };

    /** The sounds we recognize :
     */
    public final static String SOUNDS[][] = {
    /**   smiley        file name      **/
    { "*doh*", "doh.wav"}, {"*toh*", "toh.wav"},     };

    /** max number of messages to display on screen at the same time
     */
    public final static int MAX_DISPLAYED_MESSAGES = 25;

    /*------------------------------------------------------------------------------------*/
    /** messages number.
     */
    private int msg_number;

    /** Where messages appear.
     */
    private final JEditorPane messagesPane;

    /** string buffer.
     */
    private String strBuffer;

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     *
     * @param chatRoom chatRoom associated to this JChatDisplay
     */
    public JChatDisplay(ChatRoom chatRoom) throws FileNotFoundException {
        super(ClientDirector.getResourceManager().getExternalLogsDir() + chatRoom.getPrimaryKey() + JChatDisplay.CHAT_LOG_SUFFIX, true, 60 * 1000);

        msg_number = 0;
        strBuffer = "";

        URL url = null;
        String smileysHome = ClientDirector.getResourceManager().getGuiSmileysDir();

        if (ClientDirector.getResourceManager().inJar()) {

            url = this.getClass().getResource(smileysHome + "smileys.html");

            if (url != null) {
                String urlName = url.toString();
                urlName = urlName.substring(0, urlName.indexOf("smileys.html"));

                try {
                    url = new URL(urlName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                url = new File(smileysHome).toURL();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        messagesPane = new JEditorPane();

        messagesPane.setEditable(false);

        MyHTMLEditorKit kit = new MyHTMLEditorKit();
        messagesPane.setEditorKit(kit);

        ((HTMLDocument) messagesPane.getDocument()).setBase(url);

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

        if (text == null || text.length() == 0 || (text.toLowerCase().indexOf("<html") > -1) || (text.toLowerCase().indexOf("</html") > -1) || (text.toLowerCase().indexOf("<pre") > -1)) {
            return;
        }

        // Search for smileys

        for (int i = 0; i < JChatDisplay.SMILEYS.length; i++) {
            int pos = 0, posD = 0;
            StringBuffer buf = new StringBuffer("");

            while ((pos = text.indexOf(JChatDisplay.SMILEYS[i][0], posD)) >= 0) {
                buf.append(text.substring(posD, pos));

                if ((pos == 0 && JChatDisplay.SMILEYS[i][0].length() == text.length()) || (pos == 0 && text.charAt(JChatDisplay.SMILEYS[i][0].length()) == ' ') || (pos != 0 && text.charAt(pos - 1) == ' ' && (pos + JChatDisplay.SMILEYS[i][0].length()) == text.length()) || (pos != 0 && text.charAt(pos - 1) == ' ' && text.charAt(pos + JChatDisplay.SMILEYS[i][0].length()) == ' ')) {
                    buf.append(" <img width='");
                    buf.append(JChatDisplay.SMILEYS[i][1]);
                    buf.append("' height='");
                    buf.append(JChatDisplay.SMILEYS[i][2]);
                    buf.append("' src='");
                    buf.append(JChatDisplay.SMILEYS[i][3]);
                    buf.append("'> ");
                } else {
                    buf.append(JChatDisplay.SMILEYS[i][0]);
                }

                posD = pos + JChatDisplay.SMILEYS[i][0].length();
            }

            buf.append(text.substring(posD, text.length()));
            text = buf.toString();
        }

        for (int i = 0; i < JChatDisplay.SOUNDS.length; i++) {
            if (text.indexOf(JChatDisplay.SOUNDS[i][0]) >= 0) {
                SoundLibrary.getSoundPlayer().playSound(JChatDisplay.SOUNDS[i][1]);
                break;
            }
        }

        text += "<br>";
        println(text);
    }

    /*------------------------------------------------------------------------------------*/
    /** To print some text to the screen
     *
     * @param text string to print
     */
    @Override
    protected void printedText(final String text) {

        if (messagesPane == null || text == null || text.length() == 0) {
            return;
        } // nothing to print... or constructor not fully initialized

        // too much messages displayed ?
        msg_number++;

        if (DataManager.SHOW_DEBUG) {
            System.out.println("msg_number = " + msg_number);
        }

        if (msg_number > JChatDisplay.MAX_DISPLAYED_MESSAGES) {
            int pos = strBuffer.indexOf("\n");
            strBuffer = strBuffer.substring(pos + 1);
            msg_number--;
        }

        strBuffer += text + "\n";

        Runnable runnable = new Runnable() {

            public void run() {
                messagesPane.setText(strBuffer);
                messagesPane.repaint();
            }

        };

        SwingUtilities.invokeLater(runnable);
    }

    /*------------------------------------------------------------------------------------*/
    /** Finalize this chat display.
     */
    @Override
    protected void finalize() throws Throwable {
        flush();
        super.finalize();
    }

    /*------------------------------------------------------------------------------------*/
}
