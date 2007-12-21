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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import wotlas.client.ClientDirector;
import wotlas.client.DataManager;
import wotlas.libs.graphics2d.FontFactory;
import wotlas.libs.net.NetPingListener;
import wotlas.utils.SwingTools;

/** A graphic panel to show ping info.
 *
 *  @author Aldiss
 */

public class GraphicPingPanel extends JPanel implements NetPingListener {

    /*------------------------------------------------------------------------------------*/

    // The different ping background images red, yellow, green,
    private Image red, green, yellow;

    // Panel j_drawzone size
    private static int TEXT_X = 40;
    private static int TEXT_Y = 24;

    // Font for ping text
    private Font f_text;

    // double-buffer.
    private Image offScreenImage;

    // current ping value
    private int currentPing;

    // eventual "Please Wait" dialog
    private JPleaseWait pleaseWait;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     *
     */
    public GraphicPingPanel() {
        setFont("Lucida Blackletter");

        MediaTracker mediaTracker = new MediaTracker(this);
        this.red = ClientDirector.getResourceManager().getGuiImage("ping-red.jpg");
        this.green = ClientDirector.getResourceManager().getGuiImage("ping-green.jpg");
        this.yellow = ClientDirector.getResourceManager().getGuiImage("ping-yellow.jpg");
        mediaTracker.addImage(this.red, 0);
        mediaTracker.addImage(this.green, 1);
        mediaTracker.addImage(this.yellow, 2);

        try {
            mediaTracker.waitForAll(); // wait for all images to be in memory
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Panel properties
        setBackground(Color.black);
        setPreferredSize(new Dimension(160, 40));
        setMinimumSize(new Dimension(160, 40));
        setMaximumSize(new Dimension(160, 40));
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To paint our panel...
     */
    @Override
    public void paint(Graphics g) {
        try {
            // off screen image eventual creation
            if (this.offScreenImage == null)
                this.offScreenImage = createImage(160, 40);

            Graphics2D offScreen = (Graphics2D) this.offScreenImage.getGraphics();

            // we erase the previous content by redrawing the backgound
            if (this.currentPing < 400 && this.currentPing >= 0)
                offScreen.drawImage(this.green, 0, 0, this);
            else if (this.currentPing < 1000 && this.currentPing >= 0)
                offScreen.drawImage(this.yellow, 0, 0, this);
            else
                offScreen.drawImage(this.red, 0, 0, this);

            // Anti aliasing
            RenderingHints savedRenderHints = offScreen.getRenderingHints(); // save
            RenderingHints antiARenderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            antiARenderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            offScreen.setRenderingHints(antiARenderHints);

            offScreen.setFont(this.f_text);
            offScreen.setColor(Color.black);

            if (this.currentPing < 0)
                offScreen.drawString("No Response !", GraphicPingPanel.TEXT_X, GraphicPingPanel.TEXT_Y);
            else if (this.currentPing < 100)
                offScreen.drawString("Excellent (" + this.currentPing + " ms)", GraphicPingPanel.TEXT_X, GraphicPingPanel.TEXT_Y);
            else if (this.currentPing < 200)
                offScreen.drawString("Good (" + this.currentPing + " ms)", GraphicPingPanel.TEXT_X, GraphicPingPanel.TEXT_Y);
            else if (this.currentPing < 400)
                offScreen.drawString("Medium (" + this.currentPing + " ms)", GraphicPingPanel.TEXT_X, GraphicPingPanel.TEXT_Y);
            else if (this.currentPing < 1000)
                offScreen.drawString("Low (" + this.currentPing + " ms)", GraphicPingPanel.TEXT_X, GraphicPingPanel.TEXT_Y);
            else
                offScreen.drawString("Very Low (" + this.currentPing + " ms)", GraphicPingPanel.TEXT_X, GraphicPingPanel.TEXT_Y);

            // clean anti-aliasing
            offScreen.setRenderingHints(savedRenderHints);

            // we can now draw the whole result image on screen
            g.drawImage(this.offScreenImage, 0, 0, this);
        } catch (Exception e) {
        }
    }

    @Override
    public void repaint() {
        paint(getGraphics());
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * To define the font for the title and the text
     */
    public void setFont(String fontName) {
        this.f_text = FontFactory.getDefaultFontFactory().getFont(fontName);
        this.f_text = this.f_text.deriveFont(Font.PLAIN, 10f);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called when some ping information is available.
     *
     * @param ping if >=0 it's a valid ping value, if == PING_FAILED it means the
     *        last ping failed, if == PING_CONNECTION_CLOSED it means the connection
     *        has been closed.
     */
    public void pingComputed(int ping) {
        this.currentPing = ping;
        repaint();

        if (ping == NetPingListener.PING_FAILED && this.pleaseWait == null) {
            DataManager dManager = ClientDirector.getDataManager();

            if (dManager.getMyPlayer() == null || dManager.getMyPlayer().getMovementComposer() == null)
                return;

            dManager.getMyPlayer().getMovementComposer().resetMovement();
            this.pleaseWait = new JPleaseWait(dManager.getClientScreen());
        } else if (ping != NetPingListener.PING_FAILED && this.pleaseWait != null) {
            this.pleaseWait.dispose();
            this.pleaseWait = null;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Internal Class : "Please Wait Window"
     */
    class JPleaseWait extends JDialog {
        public JPleaseWait(Frame frame) {
            super(frame, "Network Connection", false);
            getContentPane().add(new JLabel("No response from server. Please Wait..."), BorderLayout.CENTER);
            pack();
            SwingTools.centerComponent(this);
            this.show();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
