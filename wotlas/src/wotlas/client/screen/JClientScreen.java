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

import wotlas.client.*;

import wotlas.libs.graphics2D.GraphicsDirector;
import wotlas.utils.Debug;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** The main frame of the wotlas client interface.<br>
 * It contains a :<br>
 * <ul>
 *  <li>JMapPanel     ( contains the graphics director, displays the game map)
 *  <li>JChatPanel    ( contains JChatRooms, i.e chat rooms )
 *  <li>JOptionsPanel ( menu on top right with help/options buttons )
 *  <li>JPlayerPanel  ( player infos, away panel, lie manager, plug-ins)
 *  <li>GraphicPingPanel ( displays ping info )
 * </ul>
 * @author Petrus
 */

public class JClientScreen extends JFrame {

 /*------------------------------------------------------------------------------------*/

  /** GUI DIMENSION
   */
   public final static int mainWidth = 800;
   public final static int mainHeight = 600;

   public final static int leftWidth = 600;

   public final static int gameHeight = 300;    // JMapPanel
   public final static int mapHeight = 300;     // Same as above
   public final static int gameMinHeight = 200; // JMapPanel

   public final static int thumbHeight = 145;

   public final static int playerHeight = mainHeight-thumbHeight;

   public final static int chatMinHeight = 150;

 /*------------------------------------------------------------------------------------*/

  /** Our GUI components
   */
   private JMapPanel mapPanel;
   private GraphicsDirector gDirector;
   private JChatPanel chatPanel;
   private JOptionsPanel optionsPanel;
   private JPlayerPanel playerPanel;
   private GraphicPingPanel pingPanel;

 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor.
   */
    public JClientScreen() {
       super("Wotlas client");
    }

 /*------------------------------------------------------------------------------------*/

  /** Constructor with graphicsDirector & data Manager
   */
   public JClientScreen( GraphicsDirector gDirector, DataManager dManager ) {
      super("Wotlas client");

       mapPanel = new JMapPanel(gDirector, dManager);
       chatPanel = new JChatPanel();
       optionsPanel = new JOptionsPanel();
       playerPanel = new JPlayerPanel();
       pingPanel = new GraphicPingPanel();

       addWindowListener( new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
             hide();
             ClientDirector.getDataManager().closeConnection();
             ClientDirector.getClientManager().start(ClientManager.MAIN_SCREEN);
          }
       });

      setIconImage(ClientDirector.getResourceManager().getGuiImage("icon.gif"));
   }

 /*------------------------------------------------------------------------------------*/

  /** To init the different panels & the display.
   */
   public void init() {

    // *** Right Panel ***

      JPanel rightPanel = new JPanel();
      rightPanel.setPreferredSize(new Dimension(mainWidth-leftWidth, mainHeight));
      rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
      rightPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
      rightPanel.setBackground(Color.black);
      
      // *** Preview Panel ***
      optionsPanel.setPreferredSize(new Dimension(mainWidth-leftWidth-4, thumbHeight));
      optionsPanel.setMinimumSize(new Dimension(mainWidth-leftWidth-4, thumbHeight));
      optionsPanel.setMaximumSize(new Dimension(mainWidth-leftWidth-4, thumbHeight));
      optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
      rightPanel.add(optionsPanel, BorderLayout.NORTH);

      rightPanel.add(Box.createRigidArea(new Dimension(0,2)));
      
      // *** Player Panel ***
      playerPanel.init();
      playerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
      rightPanel.add(playerPanel, BorderLayout.CENTER);
      
      rightPanel.add(Box.createRigidArea(new Dimension(0,2)));
      
      // *** Ping Panel ***
      JPanel fillPanel = new JPanel( );
      fillPanel.setLayout(new BoxLayout(fillPanel,BoxLayout.X_AXIS)); // MasterBob revision
      fillPanel.add(pingPanel);

      ImageIcon im_quitup    = ClientDirector.getResourceManager().getImageIcon("quit-up.jpg");
      ImageIcon im_quitdo    = ClientDirector.getResourceManager().getImageIcon("quit-do.jpg");

      JButton b_quit = new JButton(im_quitup);
      b_quit.setRolloverIcon(im_quitdo);
      b_quit.setPressedIcon(im_quitdo);
      b_quit.setBorderPainted(false);
      b_quit.setContentAreaFilled(false);
      b_quit.setFocusPainted(false);

      b_quit.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            hide();
            ClientDirector.getDataManager().closeConnection();
          }
        }
      );

      b_quit.setPreferredSize( new Dimension(36,40) );      
      b_quit.setMinimumSize( new Dimension(36,40) );
      b_quit.setMaximumSize( new Dimension(36,40) );

      fillPanel.add(b_quit);
      rightPanel.add(fillPanel);
      getContentPane().add(rightPanel, BorderLayout.EAST);

    // *** Left panel ***

      // *** Map panel ***
      mapPanel.setMinimumSize(new Dimension(leftWidth, gameMinHeight));      
      chatPanel.setMinimumSize(new Dimension(leftWidth, chatMinHeight));

      chatPanel.getCurrentJChatRoom().addPlayer(
                        ClientDirector.getDataManager().getMyPlayer().getPrimaryKey(),
                        ClientDirector.getDataManager().getMyPlayer().getFullPlayerName());
      
      JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapPanel, chatPanel);
      leftPanel.setOneTouchExpandable(true);
      leftPanel.setDividerLocation(gameHeight);

      getContentPane().add(leftPanel, BorderLayout.CENTER);

    // Finalize init
      pack();
  }

 /*------------------------------------------------------------------------------------*/

  /** GETTERS & SETTERS **/

  /** To set the Options Panel
   */
   public void setOptionsPanel(JOptionsPanel optionsPanel) {
      this.optionsPanel = optionsPanel;
   }

  /** To get tthe Options Panel
   */
   public JOptionsPanel getOptionsPanel() {
      return optionsPanel;
   }

  /** To set the Chat Panel
   */
   public void setChatPanel(JChatPanel chatPanel) {
      this.chatPanel = chatPanel;
   }

  /** To get the Chat panel
   */
   public JChatPanel getChatPanel() {
      return chatPanel;
   }

  /** To set the Player Panel
   */
   public void setPlayerPanel(JPlayerPanel playerPanel) {
      this.playerPanel = playerPanel;
   }

  /** To get the Player Panel
   */
   public JPlayerPanel getPlayerPanel() {
      return playerPanel;
   }

  /** To set the Map Panel
   */
   public void setMapPanel(JMapPanel mapPanel) {
      this.mapPanel = mapPanel;
   }

  /** To get the Map Panel
   */
   public JMapPanel getMapPanel() {
      return mapPanel;
   }

  /** To set the Ping Panel
   */
   public void setPingPanel(GraphicPingPanel pingPanel) {
      this.pingPanel = pingPanel;
   }

  /** To get the Ping Panel
   */
   public GraphicPingPanel getPingPanel() {
      return pingPanel;
   }

 /*------------------------------------------------------------------------------------*/

}