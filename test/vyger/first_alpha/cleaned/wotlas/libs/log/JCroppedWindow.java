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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.JWindow;

/** A small utility to display a cropped window.
 *
 * @author Aldiss
 */

public class JCroppedWindow extends JWindow {

    /*------------------------------------------------------------------------------------*/

    // navigation bar images
    private Image leftBar, middleBar, rightBar;

    // Title
    private String title;

    // Font  
    private Font titleFont;

    // User Mouse Listener
    private MouseListener userMouseListener;

    // User Mouse Listener
    private MouseMotionListener userMouseMotionListener;

    // left mouse button pressed ?
    private boolean leftButtonPressed;

    // resizing window ? moving window ?
    private boolean resizingFromTop, resizingFromBottom, resizingFromLeft, resizingFromRight;
    private boolean movingWindow;

    // initial mouse position for window move
    private int iniX, iniY;

    private boolean updatedSize, updatedLocation;
    private int newX, newY, newW, newH;

    // resizable window ?
    private boolean resizable;

    // use the dark menu bar ?
    private boolean useDarkMenuBar;

    /*------------------------------------------------------------------------------------*/

    // Our user ContentPane... yes it's a JPanel...
    private JPanel userContentPane;

    /*------------------------------------------------------------------------------------*/

    /** Constructor with owner Frame and title.
     *
     * @param owner frame owner
     * @param title window title, set to "" if you want none
     * @param rLocator interface giving access to the images to use for our window.
     */
    public JCroppedWindow(Frame owner, String title, LogResourceLocator rLocator) {
        this(owner, title, false, rLocator);
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with owner Frame and title.
     *
     * @param owner frame owner
     * @param title window title, set to "" if you want none
     * @param useDarkMenuBar which images do we use for the menu bar : light images or
     *        dark images.
     * @param rLocator interface giving access to the images to use for our window.
     */
    public JCroppedWindow(Frame owner, String title, boolean useDarkMenuBar, LogResourceLocator rLocator) {
        super(owner);
        this.title = title;
        this.useDarkMenuBar = useDarkMenuBar;

        // We load the images
        if (useDarkMenuBar) {
            this.leftBar = rLocator.getGuiImage("left-bar-dark.gif");
            this.middleBar = rLocator.getGuiImage("middle-bar-dark.gif");
            this.rightBar = rLocator.getGuiImage("right-bar-dark.gif");
        } else {
            this.leftBar = rLocator.getGuiImage("left-bar.gif");
            this.middleBar = rLocator.getGuiImage("middle-bar.gif");
            this.rightBar = rLocator.getGuiImage("right-bar.gif");
        }

        MediaTracker tracker = new MediaTracker(this);

        tracker.addImage(this.leftBar, 0);
        tracker.addImage(this.middleBar, 1);
        tracker.addImage(this.rightBar, 2);

        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Font
        this.titleFont = new Font("Dialog", Font.PLAIN, 11);

        // State inits
        this.leftButtonPressed = false;
        this.resizingFromTop = false;
        this.resizingFromBottom = false;
        this.resizingFromLeft = false;
        this.resizingFromRight = false;
        this.movingWindow = false;

        this.resizable = true;

        // Our Mouse Adapter        
        super.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getY() < JCroppedWindow.this.rightBar.getHeight(null) && e.getX() > JCroppedWindow.this.getWidth() - JCroppedWindow.this.rightBar.getWidth(null))
                    dispose();
                else if (JCroppedWindow.this.userMouseListener != null)
                    JCroppedWindow.this.userMouseListener.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (JCroppedWindow.this.userMouseListener != null)
                    JCroppedWindow.this.userMouseListener.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (JCroppedWindow.this.userMouseListener != null)
                    JCroppedWindow.this.userMouseListener.mouseExited(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {

                if (JCroppedWindow.this.leftButtonPressed) { // if button already pressed
                    if (JCroppedWindow.this.userMouseListener != null)
                        JCroppedWindow.this.userMouseListener.mousePressed(e);
                    return;
                }

                JCroppedWindow.this.leftButtonPressed = true;
                JCroppedWindow.this.iniX = e.getX();
                JCroppedWindow.this.iniY = e.getY();
                JCroppedWindow.this.updatedSize = false;
                JCroppedWindow.this.updatedLocation = false;

                JCroppedWindow.this.newX = JCroppedWindow.this.getX();
                JCroppedWindow.this.newY = JCroppedWindow.this.getY();
                JCroppedWindow.this.newW = JCroppedWindow.this.getWidth();
                JCroppedWindow.this.newH = JCroppedWindow.this.getHeight();

                JCroppedWindow.this.movingWindow = false;
                JCroppedWindow.this.resizingFromTop = false;
                JCroppedWindow.this.resizingFromBottom = false;
                JCroppedWindow.this.resizingFromLeft = false;
                JCroppedWindow.this.resizingFromRight = false;

                if ((e.getY() < 3) && JCroppedWindow.this.resizable)
                    JCroppedWindow.this.resizingFromTop = true;
                else if ((e.getY() > JCroppedWindow.this.getHeight() - 5) && JCroppedWindow.this.resizable)
                    JCroppedWindow.this.resizingFromBottom = true;
                //                else if( e.getY() < middleBar.getHeight(null)+2 )
                //                    movingWindow = true;

                if (!JCroppedWindow.this.movingWindow && e.getX() < 5 && JCroppedWindow.this.resizable)
                    JCroppedWindow.this.resizingFromLeft = true;
                else if (!JCroppedWindow.this.movingWindow && (e.getX() > JCroppedWindow.this.getWidth() - 5) && JCroppedWindow.this.resizable)
                    JCroppedWindow.this.resizingFromRight = true;

                if (JCroppedWindow.this.userMouseListener != null)
                    JCroppedWindow.this.userMouseListener.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (JCroppedWindow.this.movingWindow) {
                    JCroppedWindow.this.newX += e.getX() - JCroppedWindow.this.iniX;
                    JCroppedWindow.this.newY += e.getY() - JCroppedWindow.this.iniY;
                    //                    updatedLocation=true;
                } else {
                    if (JCroppedWindow.this.resizingFromTop) {
                        JCroppedWindow.this.newY += e.getY() - JCroppedWindow.this.iniY;
                        JCroppedWindow.this.updatedLocation = true;

                        JCroppedWindow.this.newH += JCroppedWindow.this.iniY - e.getY();
                        JCroppedWindow.this.updatedSize = true;
                    } else if (JCroppedWindow.this.resizingFromBottom) {
                        JCroppedWindow.this.newH += -JCroppedWindow.this.iniY + e.getY();
                        JCroppedWindow.this.updatedSize = true;
                    }

                    if (JCroppedWindow.this.resizingFromLeft) {
                        JCroppedWindow.this.newX += e.getX() - JCroppedWindow.this.iniX;
                        JCroppedWindow.this.updatedLocation = true;

                        JCroppedWindow.this.newW += JCroppedWindow.this.iniX - e.getX();
                        JCroppedWindow.this.updatedSize = true;
                    } else if (JCroppedWindow.this.resizingFromRight) {
                        JCroppedWindow.this.newW += -JCroppedWindow.this.iniX + e.getX();
                        JCroppedWindow.this.updatedSize = true;
                    }
                }

                if (JCroppedWindow.this.newH < JCroppedWindow.this.middleBar.getHeight(null))
                    JCroppedWindow.this.newH = JCroppedWindow.this.middleBar.getHeight(null);

                if (JCroppedWindow.this.newW < JCroppedWindow.this.leftBar.getWidth(null) + JCroppedWindow.this.rightBar.getWidth(null))
                    JCroppedWindow.this.newW = JCroppedWindow.this.leftBar.getWidth(null) + JCroppedWindow.this.rightBar.getWidth(null);

                if (JCroppedWindow.this.updatedLocation && JCroppedWindow.this.updatedSize) {
                    JCroppedWindow.this.setSize(new Dimension(JCroppedWindow.this.newW, JCroppedWindow.this.newH));
                    JCroppedWindow.this.userContentPane.setPreferredSize(new Dimension(JCroppedWindow.this.newW, JCroppedWindow.this.newH - JCroppedWindow.this.middleBar.getHeight(null)));
                    JCroppedWindow.this.setSize(new Dimension(JCroppedWindow.this.newW, JCroppedWindow.this.newH));
                    JCroppedWindow.this.pack();
                    JCroppedWindow.this.setLocation(JCroppedWindow.this.newX, JCroppedWindow.this.newY);
                } else if (JCroppedWindow.this.updatedSize) {
                    JCroppedWindow.this.userContentPane.setPreferredSize(new Dimension(JCroppedWindow.this.newW, JCroppedWindow.this.newH - JCroppedWindow.this.middleBar.getHeight(null)));
                    JCroppedWindow.this.setSize(new Dimension(JCroppedWindow.this.newW, JCroppedWindow.this.newH));
                    JCroppedWindow.this.pack();
                } else if (JCroppedWindow.this.updatedLocation) {
                    JCroppedWindow.this.setLocation(JCroppedWindow.this.newX, JCroppedWindow.this.newY);
                    JCroppedWindow.this.pack();
                } else if (JCroppedWindow.this.userMouseListener != null)
                    JCroppedWindow.this.userMouseListener.mouseReleased(e);

                JCroppedWindow.this.updatedLocation = false;
                JCroppedWindow.this.updatedSize = false;
                JCroppedWindow.this.leftButtonPressed = false;
                JCroppedWindow.this.movingWindow = false;
                JCroppedWindow.this.resizingFromTop = false;
                JCroppedWindow.this.resizingFromBottom = false;
                JCroppedWindow.this.resizingFromLeft = false;
                JCroppedWindow.this.resizingFromRight = false;
            }
        });

        // Our Mouse Motion Adapter
        super.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                if (!JCroppedWindow.this.resizingFromTop && !JCroppedWindow.this.resizingFromBottom && !JCroppedWindow.this.resizingFromLeft && !JCroppedWindow.this.resizingFromRight)
                    JCroppedWindow.this.setLocation(JCroppedWindow.this.getX() + e.getX() - JCroppedWindow.this.iniX, JCroppedWindow.this.getY() + e.getY() - JCroppedWindow.this.iniY);

                if (JCroppedWindow.this.userMouseMotionListener != null)
                    JCroppedWindow.this.userMouseMotionListener.mouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (JCroppedWindow.this.userMouseMotionListener != null)
                    JCroppedWindow.this.userMouseMotionListener.mouseMoved(e);
            }

        });

        // Default components
        super.setContentPane(new JPanel(true));

        super.getContentPane().setLayout(new BorderLayout());

        super.getContentPane().add(new JBarPanel(), BorderLayout.NORTH);

        this.userContentPane = new JPanel(true);
        this.userContentPane.setLayout(new BorderLayout());
        super.getContentPane().add(this.userContentPane, BorderLayout.CENTER);
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the JWindow's Content Pane
     */
    @Override
    public Container getContentPane() {
        return this.userContentPane;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the JWindow's Content Pane. Only JPanel are accpeted.
     */
    @Override
    public void setContentPane(Container contentPane) {
        if (contentPane instanceof JPanel)
            this.userContentPane = (JPanel) contentPane;
    }

    /*------------------------------------------------------------------------------------*/

    /** To add a MouseListener to this JWindow.
     */
    @Override
    public void addMouseListener(MouseListener l) {
        this.userMouseListener = l;
    }

    /*------------------------------------------------------------------------------------*/

    /** To add a MouseMotionListener to this JWindow.
     */
    @Override
    public void addMouseMotionListener(MouseMotionListener l) {
        this.userMouseMotionListener = l;
    }

    /*------------------------------------------------------------------------------------*/

    /** Our JBarPanel.
     */
    public class JBarPanel extends JPanel {

        public JBarPanel() {
            super(true);
            this.setOpaque(false);
            this.setBackground(Color.white);
            setMinimumSize(new Dimension(0, 0));
            setPreferredSize(new Dimension(100, 0));
            setMaximumSize(new Dimension(3000, 0));
        }

        @Override
        public void setMaximumSize(Dimension maximumSize) {
            maximumSize.height = JCroppedWindow.this.middleBar.getHeight(null);
            super.setMaximumSize(maximumSize);
        }

        @Override
        public void setPreferredSize(Dimension preferredSize) {
            preferredSize.height = JCroppedWindow.this.middleBar.getHeight(null);

            if (preferredSize.width < JCroppedWindow.this.leftBar.getWidth(null) + JCroppedWindow.this.rightBar.getWidth(null))
                preferredSize.width = JCroppedWindow.this.leftBar.getWidth(null) + JCroppedWindow.this.rightBar.getWidth(null);

            super.setPreferredSize(preferredSize);
        }

        @Override
        public void setMinimumSize(Dimension minimumSize) {
            minimumSize.height = JCroppedWindow.this.middleBar.getHeight(null);

            if (minimumSize.width < JCroppedWindow.this.leftBar.getWidth(null) + JCroppedWindow.this.rightBar.getWidth(null))
                minimumSize.width = JCroppedWindow.this.leftBar.getWidth(null) + JCroppedWindow.this.rightBar.getWidth(null);

            super.setMinimumSize(minimumSize);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            // 1 - Left & middle bar images
            g.drawImage(JCroppedWindow.this.leftBar, 0, 0, this);

            for (int i = JCroppedWindow.this.leftBar.getWidth(null); i <= this.getWidth() - JCroppedWindow.this.rightBar.getWidth(null); i += JCroppedWindow.this.middleBar.getWidth(null))
                g.drawImage(JCroppedWindow.this.middleBar, i, 0, this);

            // 2 - Title
            Graphics2D g2D = (Graphics2D) g;
            RenderingHints saveRenderHints = g2D.getRenderingHints(); // save

            RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2D.setRenderingHints(renderHints);

            if (JCroppedWindow.this.useDarkMenuBar)
                g2D.setColor(new Color(160, 146, 130));
            else
                g2D.setColor(Color.black);

            g2D.setFont(JCroppedWindow.this.titleFont);
            g2D.drawString(JCroppedWindow.this.title, JCroppedWindow.this.leftBar.getWidth(null) + 2, 11);
            g2D.setRenderingHints(saveRenderHints); // restore

            // 4 - Right Bar image
            g.drawImage(JCroppedWindow.this.rightBar, this.getWidth() - JCroppedWindow.this.rightBar.getWidth(null), 0, this);
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** To tell if this window is resizable.
     *  @param resizable set to true if you want to be able to resize this window (default).
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    /*------------------------------------------------------------------------------------*/

}
