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

package wotlas.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/** Displays an image in a JWindow during a certain amount of time.
 *
 * @author Aldiss
 */

public class IntroductionWindow extends Window implements ActionListener {
    /** image to display.
     */
    private Image back;

    /** Timer
     */
    private Timer timer;

    /*------------------------------------------------------------------------------------*/

    /** Creates a Window with the specified image in background.
     *
     * @param frame parent frame. 
     * @param image_path an image path...
     * @param duration display duration
     */

    public IntroductionWindow(Frame frame, String image_path, int duration) {
        super(frame);

        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

        // We load the image...
        MediaTracker mediaTracker = new MediaTracker(this);
        this.back = getToolkit().getImage(image_path);
        mediaTracker.addImage(this.back, 0);

        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
            Debug.signal(Debug.WARNING, this, e);
        }

        // We center the windows on the screen
        int XO = (screensize.width - this.back.getWidth(this)) / 2;
        int YO = (screensize.height - this.back.getHeight(this)) / 2;

        setLayout(null);
        setBackground(Color.black);
        setBounds(XO, YO, this.back.getWidth(this), this.back.getHeight(this));

        setVisible(true);
        repaint();

        // Timer init
        this.timer = new Timer(duration, this);
        this.timer.start();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Timer Event interception
     *
     * @param e supposed timer event
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != this.timer)
            return;

        dispose();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Paint Method. We draw the background image.
     * @param g graphics
     */
    @Override
    public void paint(Graphics g) {
        g.drawImage(this.back, 0, 0, this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To avoid any flicks we redefine this method...
     */
    @Override
    public void repaint() {
        paint(getGraphics());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To avoid any flicks we redefine this method...
     * @param g graphics
     */
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
