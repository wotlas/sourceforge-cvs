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

package wotlas.libs.log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/** A JLogStream prints messages to a log file every three minutes and also prints
 *  messages on a JTextArea in a JDialog.
 *
 * @author Aldiss
 * @see wotlas.libs.log.LogStream
 */

public class JLogStream extends LogStream {
    /*------------------------------------------------------------------------------------*/

    /** Max number of messages we display...
     */
    private static final int MAX_MSG = 40;

    /*------------------------------------------------------------------------------------*/

    /** Our CroppedWindow
     */
    private JCroppedWindow dialog;

    /** Our JTextArea
     */
    private JTextArea logArea;

    /** Our image.
     */
    private Image image;

    /** Number of messages displayed
     */
    private int numberOfMsg;

    /*------------------------------------------------------------------------------------*/

    /** Constructor with file name. The log is saved to disk every 3 minutes.
     *  Example : new JLogStream( frame, "client.log", "back.jpg", rLocator );<br>
     *
     *  The "back.jpg" image given in the example is given by the resource locator.
     *
     * @param owner frame parent
     * @param logFileName log file to create or use if already existing.
     * @param imageName image to display. The image are located using the rLocator.
     * @param rLocator interface giving access to the images to use for the JLogWindow
     *        AND JCroppedWindow.
     * @exception FileNotFoundException if we cannot use or create the given log file.
     */
    public JLogStream(Frame owner, String logFileName, String imageFileName, LogResourceLocator rLocator) throws FileNotFoundException {
        super(logFileName, false, 180 * 1000);

        if (imageFileName.indexOf("dark") < 0)
            this.dialog = new JCroppedWindow(owner, "Wotlas Log Window", false, rLocator);
        else
            this.dialog = new JCroppedWindow(owner, "Wotlas Log Window", true, rLocator);

        // 1 - image panel
        this.image = rLocator.getGuiImage(imageFileName);

        MediaTracker tracker = new MediaTracker(this.dialog);
        tracker.addImage(this.image, 0);

        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JPanel imPanel = new JPanel(true) {
            @Override
            public void paint(Graphics g) {
                g.drawImage(JLogStream.this.image, 0, 0, JLogStream.this.dialog);
            }
        };

        imPanel.setPreferredSize(new Dimension(this.image.getWidth(null), this.image.getHeight(null)));

        this.dialog.getContentPane().add(imPanel, BorderLayout.NORTH);
        this.dialog.setBackground(Color.white);

        // 2 - log text area
        this.logArea = new JTextArea("Starting log timer...\n");
        this.logArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        this.logArea.setForeground(new Color(100, 100, 100));
        this.logArea.setPreferredSize(new Dimension(this.image.getWidth(null), 90));
        this.logArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(this.logArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        this.dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
        scrollPane.setPreferredSize(new Dimension(this.image.getWidth(null), 80));

        // 3 - event management
        this.dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JLogStream.this.flush();
                JLogStream.this.dialog = null;
            }
        });

        // 4 - display
        this.dialog.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.dialog.setLocation((int) ((screenSize.getWidth() - this.dialog.getWidth()) / 2), (int) ((screenSize.getHeight() - this.dialog.getHeight()) / 2));

        this.dialog.show();
        waitTime(1500);
    }

    /*------------------------------------------------------------------------------------*/

    /** Method called each time text is added to the stream.
     *  Useful if you want to display the log somewhere else.
     *
     * @param x text just printed to log.
     */
    @Override
    protected void printedText(final String x) {
        if (this.logArea == null || this.dialog == null || !this.logArea.isShowing() || x == null || x.length() == 0)
            return;

        // how many lines in this message ?
        int nbLines = 0;
        int cur = 0;

        do {
            nbLines++;
            cur = x.indexOf("\n", cur);

            if (cur < 0)
                break;
            cur++;
        } while (cur > 0);

        // too much messages displayed ?
        this.numberOfMsg += nbLines;

        Runnable runnable = new Runnable() {
            public void run() {
                while (JLogStream.this.numberOfMsg > JLogStream.MAX_MSG) {
                    int pos = JLogStream.this.logArea.getText().indexOf("\n");

                    if (pos >= 0) {
                        JLogStream.this.logArea.setText(JLogStream.this.logArea.getText().substring(pos + 1, JLogStream.this.logArea.getText().length()));
                        JLogStream.this.numberOfMsg--;
                    } else
                        break;
                }

                if (JLogStream.this.logArea.isShowing()) {
                    JLogStream.this.logArea.append(x + "\n");
                    JLogStream.this.logArea.setPreferredSize(new Dimension(JLogStream.this.image.getWidth(JLogStream.this.dialog), JLogStream.this.numberOfMsg * 16));
                }

                // we want the scrollbars to move when some text is added...
                if (JLogStream.this.logArea.isShowing())
                    JLogStream.this.logArea.setCaretPosition(JLogStream.this.logArea.getText().length());
            }
        };

        SwingUtilities.invokeLater(runnable);
    }

    /*------------------------------------------------------------------------------------*/

    /** Waits ms milliseconds with a very low CPU use.
     *
     * @param ms number of milliseconds to wait.
     */
    public void waitTime(long ms) {
        Object o = new Object();

        synchronized (o) {
            try {
                o.wait(ms);
            } catch (InterruptedException e) {
            }
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** To show/hide the Log Window. The window is not destroyed, just hidden.
     *
     * @param show true to show the window, false to hide it.
     */
    public void setVisible(boolean show) {
        this.dialog.setVisible(show);
    }

    /*------------------------------------------------------------------------------------*/
}
