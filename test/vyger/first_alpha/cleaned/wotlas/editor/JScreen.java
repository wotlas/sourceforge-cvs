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

package wotlas.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import wotlas.libs.graphics2d.GraphicsDirector;

/** The main frame of the wotlas client interface.<br>
 * It contains a :<br>
 * <ul>
 *  <li>JMapPanel     ( contains the graphics director, displays the game map)
 *  <li>EditorPlugIn ( displays edit map tools )
 * </ul>
 * @author Petrus, Diego
 */

public class JScreen extends JFrame {

    /*------------------------------------------------------------------------------------*/

    /** GUI DIMENSION
     */
    public final static int mainWidth = 800;
    public final static int mainHeight = 600;

    public final static int leftWidth = 600;

    public final static int gameHeight = 300; // JMapPanel
    public final static int mapHeight = 300; // Same as above
    public final static int gameMinHeight = 200; // JMapPanel

    /*------------------------------------------------------------------------------------*/

    /** Our GUI components
     */
    private JMPanel mapPanel;
    private GraphicsDirector gDirector;
    private EditorPlugIn editorPlugIn;
    private javax.swing.JScrollPane jScrollPane1;

    /*------------------------------------------------------------------------------------*/

    /** Empty Constructor.
     */
    public JScreen() {
        super("Wotlas editor");
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with graphicsDirector & data Manager
     */
    public JScreen(GraphicsDirector gDirector, EditorDataManager dManager) {
        super("Wotlas editor");

        this.mapPanel = new JMPanel(gDirector, dManager);
        this.editorPlugIn = new EditorPlugIn();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hide();
                EditTile.getDataManager().exit();
                System.exit(0);
            }
        });

        //      setIconImage(ClientDirector.getResourceManager().getGuiImage("icon.gif"));
    }

    /*------------------------------------------------------------------------------------*/

    /** To init the different panels & the display.
     */
    public void init() {

        // *** left Panel ***
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(JScreen.leftWidth, JScreen.mainHeight));
        leftPanel.setLayout(new java.awt.GridLayout(1, 1));
        this.mapPanel.setMinimumSize(new Dimension(400, 500));
        this.jScrollPane1 = new javax.swing.JScrollPane(this.mapPanel);
        leftPanel.add(this.jScrollPane1);

        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(JScreen.mainWidth - JScreen.leftWidth, JScreen.mainHeight));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        rightPanel.setBackground(Color.black);
        rightPanel.setMinimumSize(new Dimension(150, 500));
        //rightPanel.add(Box.createRigidArea(new Dimension(0,2)));

        // *** Map editor plug in ***
        this.editorPlugIn.init();
        this.editorPlugIn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(this.editorPlugIn, BorderLayout.CENTER);
        //rightPanel.add(Box.createRigidArea(new Dimension(0,2)));

        JPanel fillPanel = new JPanel();
        fillPanel.setLayout(new BoxLayout(fillPanel, BoxLayout.X_AXIS)); // MasterBob revision
        ImageIcon im_quitup = EditTile.getResourceManager().getImageIcon("quit-up.jpg");
        ImageIcon im_quitdo = EditTile.getResourceManager().getImageIcon("quit-do.jpg");

        JButton b_quit = new JButton(im_quitup);
        b_quit.setRolloverIcon(im_quitdo);
        b_quit.setPressedIcon(im_quitdo);
        b_quit.setBorderPainted(false);
        b_quit.setContentAreaFilled(false);
        b_quit.setFocusPainted(false);

        b_quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hide();
                EditTile.getDataManager().exit();
            }
        });

        b_quit.setPreferredSize(new Dimension(36, 40));
        b_quit.setMinimumSize(new Dimension(36, 40));
        b_quit.setMaximumSize(new Dimension(36, 40));

        fillPanel.add(b_quit);
        rightPanel.add(fillPanel);

        JSplitPane diegoPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        getContentPane().add(diegoPanel, BorderLayout.CENTER);

        this.setSize(800, 600);

        // Finalize init
        pack();
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the Map Panel
     */
    public void setMapPanel(JMPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    /** To get the Map Panel
     */
    public JMPanel getMapPanel() {
        return this.mapPanel;
    }
}