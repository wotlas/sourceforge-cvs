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
// - remplacer currentProfile par playerImpl

package wotlas.client;

import wotlas.client.gui.*;
import wotlas.client.screen.*;

import wotlas.common.ImageLibRef;
import wotlas.common.message.account.*;
import wotlas.common.Tickable;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.policy.*;

import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.net.NetMessage;
import wotlas.libs.net.NetPersonality;

import wotlas.libs.pathfinding.AStarDouble;

import wotlas.utils.Debug;
import wotlas.utils.Tools;

import java.awt.*;
import java.awt.event.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.MediaTracker;

import java.io.File;

import javax.swing.*;

/** A DataManager manages Game Data and client's connection.
 * It possesses a WorldManager
 *
 * @author Petrus
 * @see wotlas.common.NetConnectionListener
 */

public class DataManager extends Thread implements NetConnectionListener, Tickable
{
 /*------------------------------------------------------------------------------------*/

  /** Image Library
   */
  public final static String IMAGE_LIBRARY = "graphics/imagelib";
  
  /** size of a mask's cell (in pixels)
   */
  public final static int TILE_SIZE = 10;

 /*------------------------------------------------------------------------------------*/

  /** Path to the local server database.
   */
  private String databasePath;

 /*------------------------------------------------------------------------------------*/

  /** Our Default Data Manager
   */
  static private DataManager dataManager;

  /** Our World Manager
   */
  private WorldManager worldManager;
  
  
 /*------------------------------------------------------------------------------------*/

  /** Personality Lock
   */
  private byte personalityLock[] = new byte[1];

  /** Our NetPersonality, useful if we want to send messages !
   */
  private NetPersonality personality;

 /*------------------------------------------------------------------------------------*/

  /** Our current player.
   */
  private ProfileConfig currentProfileConfig;

  /** Our current playerImpl.
   */
  private PlayerImpl myPlayerImpl;

  /** Our ImageLibrary.
   */
  private ImageLibrary imageLib;

  /** Our Graphics Director.
   */
  private GraphicsDirector gDirector;

  /** Our AStar object.
   */
  public AStarDouble aStar;

  /** Our client interface frame.
   */
  private JClientScreen mFrame;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  private DataManager(String databasePath) {
    this.databasePath = databasePath;
    worldManager = new WorldManager();
  }

 /*------------------------------------------------------------------------------------*/

  /** Creates a new DataManager.
   *
   * @return the created (or previously created) data manager.
   */
  public static DataManager createDataManager(String databasePath) {
    if (dataManager == null)
      dataManager = new DataManager(databasePath);
    return dataManager;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the default data manager.
   *
   * @return the default data manager.
   */
  public static DataManager getDefaultDataManager() {
    return dataManager;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the world manager.
   *
   * @return the world manager.
   */
  public WorldManager getWorldManager() {
    return worldManager;
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the current profileConfig.
   */
  public void setCurrentProfileConfig(ProfileConfig currentProfileConfig) {
    this.currentProfileConfig = currentProfileConfig;
  }

  /** To get the current profileConfig.
   */
  public ProfileConfig getCurrentProfileConfig() {
    return currentProfileConfig;
  }

 /*------------------------------------------------------------------------------------*/

  /** This method is called when a new network connection is created
   *
   * @param personality the NetPersonality object associated to this connection.
   */
  public void connectionCreated( NetPersonality personality )
  {
    synchronized( personalityLock ) {
      this.personality = personality;
    }

    if (currentProfileConfig.getLocalClientID() == -1) {
      Debug.signal( Debug.NOTICE, null, "no valid key found => request a new account to AccountServer");
      Debug.signal( Debug.NOTICE, null, "sending login & password");

      personality.queueMessage( new PasswordAndLoginMessage( currentProfileConfig.getLogin(),
              currentProfileConfig.getPassword() ) );

      personality.setContext(this);

      try {
        wait( 2000 );
      } catch(Exception e) {
        ; // Do nothing
      }

      if (personality==null) {
        Debug.signal( Debug.ERROR, this, "Connection closed by AccountServer" );
        return;
      }

      Debug.signal( Debug.NOTICE, null, "OK, now we create a new account" );
      personality.queueMessage( new AccountCreationMessage() );

      try {
        wait( 1000 );
      } catch(Exception e){
        ; // Do nothing
      }

      return;

    } else {
      // The key is valid, we are connected to the GameServer
    }

    Debug.signal( Debug.NOTICE, null, "client.DataManager connected to GameServer" );

    System.out.println("Connection opened");
  }

 /*------------------------------------------------------------------------------------*/

  /** This method is called when the network connection of the client is closed
   *
   * @param personality the NetPersonality object associated to this connection.
   */
  public void connectionClosed( NetPersonality personality ) {
    synchronized( personalityLock ) {
      this.personality = null;
    }

    System.out.println("Connection closed");
  }

 /*------------------------------------------------------------------------------------*/

  /** To close the network connection if any.
   */
  public void closeConnection() {
    synchronized( personalityLock ) {
      if ( personality!=null )
        personality.closeConnection();
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the ID of currentProfileConfig
   */
  public void setCurrentProfileConfigID(int clientID, int serverID) {
    currentProfileConfig.setLocalClientID(clientID);
    currentProfileConfig.setOriginalServerID(serverID);
    closeConnection();
    ClientManager.getDefaultClientManager().start(11);
  }

 /*------------------------------------------------------------------------------------*/

  /** To show the client's interface
   */
  public void showInterface() {
    
    gDirector = new GraphicsDirector( new LimitWindowPolicy() );

    // Background
    ImageIdentifier groundImId = new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                           (short) 2,
                                           (short) 0);
    // Image Library Creation
    try {
      imageLib = ImageLibrary.createImageLibrary(databasePath+File.separator+IMAGE_LIBRARY);
      imageLib.loadImageAction( groundImId );
    } catch( java.io.IOException ioe ) {
      ioe.printStackTrace();
      Debug.exit();
    }

    // We create a "MotionlessSprite" that will represent our ground image in the GraphicsDirector.
    MotionlessSprite groundSpr = new MotionlessSprite(
                                            0,                        // ground x=0
                                            0,                        // ground y=0
                                            groundImId,               // image
                                            ImageLibRef.MAP_PRIORITY, // priority
                                            false                     // no animation
                                        );

    // Creation of AStar
    aStar = new AStarDouble();

    // Loading mask image
    /*Image maskImg    = Toolkit.getDefaultToolkit().getImage("../base/mask.gif");
    MediaTracker myTracker = new MediaTracker(new Label());
    myTracker.addImage(maskImg, 0);
    try {
      myTracker.waitForAll();
    } catch (InterruptedException ie) {
      System.err.println("InterruptedException: " + ie);
      System.exit(1);
    }    
    BufferedImage maskBuffImg = new BufferedImage(80, 56, BufferedImage.TYPE_INT_RGB);
    */
    BufferedImage maskBuffImg = ImageLibrary.loadBufferedImage("../base/mask.gif");

    aStar.initMask(maskBuffImg, 80, 56);

    // Creation of the drawable reference
    myPlayerImpl = new PlayerImpl();
    myPlayerImpl.init();
    myPlayerImpl.setX(groundSpr.getWidth()/2);
    myPlayerImpl.setY(groundSpr.getHeight()/2);

    // Init of the GraphicsDirector
     gDirector.init(
                      (Drawable) groundSpr,         // background drawable
                      myPlayerImpl.getDrawable(),   // reference for screen movements
                      new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight )   // screen default dimension
                    );

    // Create the panels
    JInfosPanel infosPanel = new JInfosPanel();
    JMapPanel mapPanel = new JMapPanel(gDirector, this);
    JChatPanel chatPanel = new JChatPanel();
    JPreviewPanel previewPanel = new JPreviewPanel();
    JPlayerPanel playerPanel = new JPlayerPanel();
    JLogPanel logPanel = new JLogPanel();

    // Create main Frame
    mFrame = new JClientScreen(infosPanel, mapPanel, chatPanel, previewPanel, playerPanel, logPanel);
    mFrame.init();
    mFrame.show();

    // Start main loop tick
    Debug.signal( Debug.NOTICE, null, "Beginning to tick Graphics Director" );
    this.start();
  }

 /*------------------------------------------------------------------------------------*/

  /** Main loop to tick the graphics director every 100ms
   */
  public void run() {
    Object lock = new Object();
    while( true ) {
      tick();
      Tools.waitTime(10);
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** Tick
   */
  public void tick() {
    myPlayerImpl.tick();
    gDirector.tick();
  }

 /*------------------------------------------------------------------------------------*/

  /** To show a warning message
   */
  public void showWarningMessage(String warningMsg) {
    JOptionPane.showMessageDialog( mFrame, warningMsg, "Warning message!", JOptionPane.WARNING_MESSAGE);
  }

 /*------------------------------------------------------------------------------------*/

  /** Called when user clic on JMapPanel
   */
  public void onClicJMapPanel(MouseEvent e) {
    Rectangle screen = gDirector.getScreenRectangle();
    
    Object object = gDirector.findOwner( e.getX(), e.getY());
    if (object != null) {
      System.out.println(object.getClass());
    }
    
    int newX = e.getX() + (int)screen.getX();
    int newY = e.getY() + (int)screen.getY();
    System.out.println("newPosition : " + newX + ", " + newY);
    myPlayerImpl.setEndPosition(newX, newY);
    
    // Create the trajectory
    myPlayerImpl.setTrajectory(aStar.findPath(new Point(myPlayerImpl.getX()/TILE_SIZE, myPlayerImpl.getY()/TILE_SIZE), new Point(newX/TILE_SIZE, newY/TILE_SIZE)));

  }

 /*------------------------------------------------------------------------------------*/

}
