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


// TODO :
// - gameHeight

package wotlas.client.screen;

import wotlas.client.ClientManager;
import wotlas.client.DataManager;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.policy.*;

import wotlas.utils.Debug;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** The main frame of wotlas client interface.<br>
 * It contains :<br>
 * <ul>
 *  <li>JPreviewPanel
 *  <li>JMapPanel
 *  <li>JChatPanel
 *  <li>JPreviewPanel
 *  <li>JPlayerPanel
 *  <li>JLogPanel
 * </ul>
 * @author Petrus
 */

public class JClientScreen extends JFrame
{

 /*------------------------------------------------------------------------------------*/

  public final static int mainWidth = 800;
  public final static int mainHeight = 600;

  //private final static int northHeight = 50;
  private final static int northHeight = 0;
  public final static int leftWidth = 600;

  private final static int gameHeight = 350; // JMapPanel
  public final static int mapHeight = 350;  // Same as above
  private final static int gameMinHeight = 200; // JMapPanel

  private final static int thumbHeight = 100;

  //private final static int playerHeight = 300;
  private final static int playerHeight = mainHeight-thumbHeight;      

  private final static int chatMinHeight = 150;

 /*------------------------------------------------------------------------------------*/

  private JInfosPanel infosPanel;
  private JMapPanel mapPanel;
  private GraphicsDirector gDirector;
  private JChatPanel chatPanel;
  private JOptionsPanel optionsPanel;
  private JPlayerPanel playerPanel;
  private JLogPanel logPanel;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
  public JClientScreen() {
    super("Wotlas client");
  }

  public JClientScreen(JInfosPanel infosPanel, GraphicsDirector gDirector, JChatPanel chatPanel,
                       JOptionsPanel optionsPanel, JPlayerPanel playerPanel, JLogPanel logPanel) {
    super("Wotlas client");
    this.infosPanel = infosPanel;
    this.gDirector = gDirector;
    this.chatPanel = chatPanel;
    this.optionsPanel = optionsPanel;
    this.playerPanel = playerPanel;
    this.logPanel = logPanel;
    addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        //System.out.println("See you soon!");
        //DataManager.getDefaultDataManager().closeConnection();
        //Debug.exit();
        ClientManager.getDefaultClientManager().start(0);
      }
    });
  }

  public JClientScreen(JInfosPanel infosPanel, JMapPanel myMapPanel, JChatPanel chatPanel,
                       JOptionsPanel optionsPanel, JPlayerPanel playerPanel, JLogPanel logPanel) {
    super("Wotlas client");
    this.infosPanel = infosPanel;
    this.mapPanel = myMapPanel;
    this.chatPanel = chatPanel;
    this.optionsPanel = optionsPanel;
    this.playerPanel = playerPanel;
    this.logPanel = logPanel;
    addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        //System.out.println("See you soon!");
        //DataManager.getDefaultDataManager().exit();
        hide();
        ClientManager.getDefaultClientManager().start(0);
      }
    });
  }

 /*------------------------------------------------------------------------------------*/

  /** To init the different panels
   */
  public void init()
  {
    // *** North panel ***

      infosPanel.setPreferredSize(new Dimension(mainWidth, northHeight));
      infosPanel.setBackground(Color.red);
      getContentPane().add(infosPanel, BorderLayout.NORTH);

    // *** Right Panel ***

      JPanel rightPanel = new JPanel();
      rightPanel.setPreferredSize(new Dimension(mainWidth-leftWidth, mainHeight-northHeight));
      rightPanel.setBackground(Color.black);

      // *** Preview Panel ***

      optionsPanel.setPreferredSize(new Dimension(mainWidth-leftWidth, thumbHeight));
      rightPanel.add(optionsPanel);

      // *** Player Panel ***

      playerPanel.setPreferredSize(new Dimension(mainWidth-leftWidth, playerHeight));
      rightPanel.add(playerPanel);

      // *** Log Panel ***

      logPanel.setPreferredSize(new Dimension(mainWidth-leftWidth, mainHeight-thumbHeight-playerHeight));      
      //rightPanel.add(logPanel);

    getContentPane().add(rightPanel, BorderLayout.EAST);

    // *** Left panel ***

      // *** Map panel ***

      mapPanel.setMinimumSize(new Dimension(leftWidth, gameMinHeight));
      //gDirector.setMinimumSize(new Dimension(leftWidth, gameMinHeight));

      // *** Chat Panel ***

      /*JScrollPane myChatPanel = new JScrollPane(chatPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      //myChatPanel.setMinimumSize(new Dimension(leftWidth, mainHeight-northHeight-gameHeight));
      myChatPanel.setMinimumSize(new Dimension(leftWidth, chatMinHeight));

      JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapPanel, myChatPanel);
      //JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gDirector, myChatPanel);
      */
      
      chatPanel.setMinimumSize(new Dimension(leftWidth, chatMinHeight));
      
      JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapPanel, chatPanel);
      

      leftPanel.setOneTouchExpandable(true);
      leftPanel.setDividerLocation(gameHeight);

      getContentPane().add(leftPanel, BorderLayout.CENTER);

    // Finalize init
    pack();
  }

 /*------------------------------------------------------------------------------------*/

  /** To set
   */
  public void setInfosPanel(JInfosPanel infosPanel) {
    this.infosPanel = infosPanel;
  }

  /** To get
   */
  public JInfosPanel getInfosPanel() {
    return infosPanel;
  }

  /** To set
   */
  public void setOptionsPanel(JOptionsPanel optionsPanel) {
    this.optionsPanel = optionsPanel;
  }

  /** To get
   */
  public JOptionsPanel getOptionsPanel() {
    return optionsPanel;
  }

  /** To set
   */
  public void setChatPanel(JChatPanel chatPanel) {
    this.chatPanel = chatPanel;
  }

  /** To get
   */
  public JChatPanel getChatPanel() {
    return chatPanel;
  }

  /** To set
   */
  public void setPlayerPanel(JPlayerPanel playerPanel) {
    this.playerPanel = playerPanel;
  }

  /** To get
   */
  public JPlayerPanel getPlayerPanel() {
    return playerPanel;
  }

  /** To set
   */
  public void setMapPanel(JMapPanel mapPanel) {
    this.mapPanel = mapPanel;
  }

  /** To get
   */
  public JMapPanel getMapPanel() {
    return mapPanel;
  }

 /*------------------------------------------------------------------------------------*/

  /*public static void main(String arv[])
  {
    JInfosPanel infosPanel = new JInfosPanel();
    JMapPanel mapPanel = new JMapPanel();
    JChatPanel chatPanel = new JChatPanel();
    JPreviewPanel previewPanel = new JPreviewPanel();
    JPlayerPanel playerPanel = new JPlayerPanel();
    JLogPanel logPanel = new JLogPanel();

    JClientScreen mFrame = new JClientScreen(infosPanel, mapPanel, chatPanel, previewPanel, playerPanel, logPanel);

    mFrame.init();
    mFrame.pack();
    mFrame.show();
  }*/

 /*------------------------------------------------------------------------------------*/

}