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
// - utiliser une hashtable pour les players
// - changer la boucle du thread
// - mettre un lock sur le circle

package wotlas.client;

import wotlas.client.gui.*;
import wotlas.client.screen.*;

import wotlas.common.character.*;
import wotlas.common.ImageLibRef;
import wotlas.common.message.account.*;
import wotlas.common.message.description.*;
import wotlas.common.Player;
import wotlas.common.Tickable;
import wotlas.common.universe.*;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.policy.*;

import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.net.NetMessage;
import wotlas.libs.net.NetPersonality;

import wotlas.libs.pathfinding.AStarDouble;

import wotlas.utils.Debug;
import wotlas.utils.List;
import wotlas.utils.ScreenPoint;
import wotlas.utils.ScreenRectangle;
import wotlas.utils.Tools;

import java.awt.*;
import java.awt.event.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.MediaTracker;

import java.io.File;
import java.io.IOException;

import javax.swing.*;

import java.util.HashMap;
import java.util.Iterator;

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
  public final static int TILE_SIZE = 5;

  /** TIMEOUT to the Account Server
   */
  private static final int CONNECTION_TIMEOUT = 5000;

  /** Number of tick before destroying the circle
   */
  private static final int CIRCLE_LIFETIME = 20;

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

  /** tells if the player could be moving to another room
   */
  private boolean couldBeMovingToAnotherRoom = false;
  
  /** current RoomLink considered for intersection
   */
  private RoomLink latestRoomLink;
  
 /*------------------------------------------------------------------------------------*/

  /** Personality Lock
   */
  private byte personalityLock[] = new byte[1];

  /** Our NetPersonality, useful if we want to send messages !
   */
  private NetPersonality personality;

  /** Game Lock (unlocked by client.message.description.YourPlayerDataMsgBehaviour)
   */
  private Object startGameLock = new Object();

 /*------------------------------------------------------------------------------------*/

  /** Our current player.
   */
  private ProfileConfig currentProfileConfig;

  /** Our playerImpl.
   */
  private PlayerImpl myPlayer;

  /** List of players
   */
  private HashMap players;

  /** Circle selection
   */
  private CircleDrawable circle;

  /** Number of tick since circle creation
   */
  private int circleLife = 0;

  /** Circle Lock
   */
  private byte circleLock[] = new byte[1];

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

  /** To get startGameLock
   */
  public Object getStartGameLock() {
    return startGameLock;
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the current profileConfig<br>
   * (called by client.message.account.AccountCreatedMsgBehaviour)
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

    personality.setContext(this);

    if (currentProfileConfig.getLocalClientID() == -1) {
      Debug.signal( Debug.NOTICE, null, "no valid key found => request a new account to AccountServer");
      Debug.signal( Debug.NOTICE, null, "sending login & password");

      personality.queueMessage( new PasswordAndLoginMessage( currentProfileConfig.getLogin(),
              currentProfileConfig.getPassword() ) );

      /*
      try {
        wait( 1000 );
      } catch(Exception e){
        ; // Do nothing
      }
      */

      JAccountWizard host = new JAccountWizard(personality);
      wotlas.utils.SwingTools.centerComponent(host);
      host.init();
      host.start();

      if (personality==null) {
        Debug.signal( Debug.ERROR, this, "Connection closed by AccountServer" );
        return;
      }

      Debug.signal( Debug.NOTICE, null, "New account created !" );

      /*
      try {
        wait( 1000 );
      } catch(Exception e){
        ; // Do nothing
      }
      */

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

  /** To set the ID of currentProfileConfig<br>
   * called by wotlas.client.message.account.AccountCreatedMsgBehaviour
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
    System.out.println("DataManager::ShowInterface");

    String locationName = "";
    
    // 0 - Create Image Library
    String imageDBHome = databasePath + File.separator + IMAGE_LIBRARY;
    try {
      imageLib = ImageLibrary.createImageLibrary(imageDBHome);
    } catch( java.io.IOException ioe ) {
      ioe.printStackTrace();
      Debug.exit();
    }

    // 1 - Create Graphics Director
    gDirector = new GraphicsDirector( new LimitWindowPolicy() );
    ImageIdentifier backgroundImageID = null;  // background image ( town, interiorMap, etc ... )
    Drawable background = null;

    // 2 - Retreive player's informations
    myPlayer = new PlayerImpl();
    try {
      synchronized(startGameLock) {
        personality.queueMessage(new MyPlayerDataPleaseMessage());
        startGameLock.wait(CONNECTION_TIMEOUT);
      }
    } catch (InterruptedException ie) {
      showWarningMessage("Cannot retreive your informations from the Game Server !");
      Debug.exit();
    }

    // Retreive player's location
    WotlasLocation location = myPlayer.getLocation();
    
    System.out.println("\tmyPlayer.location = "   + location);
    System.out.println("\tlocation.worldMapID = " + location.getWorldMapID());
    System.out.println("\tlocation.townMapID = "  + location.getTownMapID());
    System.out.println("\tlocation.buildingID = " + location.getWorldMapID());

    // 3 - Create the drawable reference
    location.setWorldMapID(0);
    location.setTownMapID(0);
    location.setInteriorMapID(0);
    location.setBuildingID(0);
    location.setRoomID(0);
    myPlayer.init();
    System.out.println("\tmyPlayer = " + myPlayer);
    System.out.println("\tmyPlayer.fullPlayerName = "  + myPlayer.getFullPlayerName());
    System.out.println("\tmyPlayer.PlayerName = "      + myPlayer.getPlayerName());
    System.out.println("\tmyPlayer.WotCharacter = "    + myPlayer.getWotCharacter());
    System.out.println("\tmyPlayer.ImageIdentifier = " + myPlayer.getImageIdentifier());
    System.out.println("\tmyPlayer.Drawable = "        + myPlayer.getDrawable());

    /*
    location.setWorldMapID(0);
    location.setTownMapID(0);
    location.setInteriorMapID(0);
    location.setBuildingID(0);
    location.setRoomID(0);
    */

  //*** World
    if (location.isWorld()) {
      System.out.println("\tWorld");
      backgroundImageID = worldManager.getWorldMap(location).getWorldImage();
      System.out.println("\tImageIdentifier = " + backgroundImageID);

      background = (Drawable) new MotionlessSprite(
                                            0,                        // ground x=0
                                            0,                        // ground y=0
                                            backgroundImageID,        // image
                                            ImageLibRef.MAP_PRIORITY, // priority
                                            false                     // no animation
                                        );

      myPlayer.setX(152*TILE_SIZE);
      myPlayer.setY(16*TILE_SIZE);
    }

  //*** Town
    if (location.isTown()) {
      System.out.println("Town");
      //System.out.println(worldManager.getTownMap(location).getFullName());

    }

  //*** Room
    if (location.isRoom()) {

      InteriorMap imap = worldManager.getInteriorMap(location);     
      System.out.println("InteriorMap");
      System.out.println("\tfullName = "  + imap.getFullName());
      System.out.println("\tshortName = " + imap.getShortName());

      Room room = worldManager.getRoom(location);
      System.out.println("Room");
      System.out.println("\tfullName = "       + room.getFullName());
      locationName = room.getFullName();
      System.out.println("\tshortName = "      + room.getShortName());
      ScreenPoint insertionPoint = room.getInsertionPoint();
      System.out.println("\tinsertionPoint = " + insertionPoint);

      RoomLink[] roomLinks = room.getRoomLinks();
      System.out.println("RoomLink");
      for (int i=0; i<roomLinks.length; i++) {
        System.out.println("roomLinks["+i+"] = " + roomLinks[i]);
      }


      backgroundImageID = imap.getInteriorMapImage();
      System.out.println("\tbackgroundImageID = " + backgroundImageID);

      background = (Drawable) new MultiRegionImage(
                                                myPlayer.getDrawable(),              // our reference for image loading
                                                650,                                 // perception radius
                                                imap.getImageRegionWidth(),          // grid deltax
                                                imap.getImageRegionHeight(),         // grid deltay
                                                imap.getImageWidth(),                // image's total width
                                                imap.getImageHeight(),               // image's total height
                                                imap.getInteriorMapImage()           // base image identifier
                                            );
      
      
      myPlayer.setX(insertionPoint.x);
      myPlayer.setY(insertionPoint.y);
      myPlayer.setPosition(insertionPoint);

    }

    // 4 - given the backgroundImageID we get the mask...
    ImageIdentifier mapMaskID = null;
    try {
      mapMaskID = ImageLibrary.getImageIdentifier( backgroundImageID, imageDBHome, "mask" );
    } catch( IOException e ) {
      Debug.signal( Debug.CRITICAL, this, "Image Library Corrupted" );
      Debug.exit();
    }
    if (mapMaskID==null) {
      Debug.signal( Debug.CRITICAL, this, "Mask not found" );
      Debug.exit();
    }

    // 5 - We load the mask image and create the Astar algo.
    BufferedImage bufIm = null;
    try {
      bufIm = ImageLibrary.loadBufferedImage(new ImageIdentifier( mapMaskID ), imageDBHome, BufferedImage.TYPE_INT_ARGB );
    } catch( IOException e ) {
      e.printStackTrace();
      return;
    }
    System.out.println("\tbufIm.width = " + bufIm.getWidth());
    System.out.println("\tbufIm.height = " + bufIm.getHeight());
    System.out.println("\tbackground.width = " + background.getWidth());
    System.out.println("\tbackground.height = " + background.getHeight());
    aStar = new AStarDouble();
    aStar.setMask( BinaryMask.create( bufIm ) );
    bufIm.flush(); // free image resource

    // 6 - Init the GraphicsDirector
    gDirector.init( background,               // background drawable
                    myPlayer.getDrawable(),   // reference for screen movements
                    new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight )   // screen default dimension
                   );

    // Add visual properties to the player
    myPlayer.initVisualProperties(gDirector);

    // 6 - Create the panels
    JInfosPanel infosPanel = new JInfosPanel(myPlayer);
    infosPanel.setLocation(locationName);
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

    // Retreive other players informations
    players = new HashMap();
    personality.queueMessage(new AllDataLeftPleaseMessage());

    addPlayer(myPlayer);

  }

 /*------------------------------------------------------------------------------------*/

  /** Main loop to tick the graphics director every 10ms
   */
  public void run() {
    long now;
    int deltaT;
    int delay;

    String os   = System.getProperty( "os.name" );
    String arch = System.getProperty( "os.arch" );
    String vers = System.getProperty( "os.version" );
    Debug.signal( Debug.NOTICE, this, "OS INFO :\n\nOS NAME : <"+os+">\nOS ARCH: <"+arch+">\nOS VERSION: <"+vers+">\n" );

    delay = 30;
    if ( os.equals("Windows 2000") ) {
      delay = 35;
    }

    Object lock = new Object();
    while( true ) {
      now = System.currentTimeMillis();
      tick();

      deltaT = (int) (System.currentTimeMillis()-now);
      //System.out.println("deltaT = " + deltaT);
      if (deltaT<delay) {
        Tools.waitTime(delay-deltaT);
      } else {
        Tools.waitTime(delay);
      }
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** Tick
   */
  public void tick() {
    //myPlayer.tick();

    if (circle != null) {
      if (circleLife < CIRCLE_LIFETIME) {
        circleLife++;
      } else {
        System.out.println("destroy circle");
        gDirector.removeDrawable(circle);
        circle = null;
        circleLife = 0;
      }
    }
    Iterator it = players.values().iterator();
    //System.out.println("tick players");
    while( it.hasNext() ) {
      //System.out.println("tick");
      ( (PlayerImpl) it.next() ).tick();
    }
    //System.out.println("end tick");
    gDirector.tick();
  }

 /*------------------------------------------------------------------------------------*/

  /** To show a warning message
   */
  public void showWarningMessage(String warningMsg) {
    JOptionPane.showMessageDialog( mFrame, warningMsg, "Warning message!", JOptionPane.WARNING_MESSAGE);
  }

 /*------------------------------------------------------------------------------------*/

  /** Called when user left-clic on JMapPanel
   */
  public void onLeftClicJMapPanel(MouseEvent e) {
    System.out.println("DataManager::onLeftClicJMapPanel");

    Rectangle screen = gDirector.getScreenRectangle();

    Object object = gDirector.findOwner( e.getX(), e.getY());

    if (object == null) {
      int newX = e.getX() + (int)screen.getX();
      int newY = e.getY() + (int)screen.getY();
      System.out.println("endPosition = ("+newX+","+newY+")");
      myPlayer.setEndPosition(newX, newY);
      // Create the trajectory
      
      wotlas.utils.List path = aStar.findPath( new Point( myPlayer.getX()/TILE_SIZE, myPlayer.getY()/TILE_SIZE),
                                           new Point(newX/TILE_SIZE, newY/TILE_SIZE));                         
      
           
      if (path!=null) {
        Point p0[] = new Point[path.size()];
        for (int i=0; i<path.size(); i++) {
          Point p = (Point) path.elementAt(i);             
          p0[i] = new Point(p.x*TILE_SIZE, p.y*TILE_SIZE);
        }
        Drawable pathDrawable = (Drawable) new PathDrawable( p0, Color.red, (short) ImageLibRef.AURA_PRIORITY ); 
        gDirector.addDrawable( pathDrawable );
      }
      
      wotlas.utils.List smoothPath = aStar.smoothPath(path);            
      if (smoothPath!=null) {
        Point p1[] = new Point[smoothPath.size()];
        for (int i=0; i<smoothPath.size(); i++) {
          Point p = (Point) smoothPath.elementAt(i);                    
          p1[i] = new Point(p.x*TILE_SIZE, p.y*TILE_SIZE);          
        }
        Drawable pathDrawable1 = (Drawable) new PathDrawable( p1, Color.blue, (short) ImageLibRef.AURA_PRIORITY ); 
        gDirector.addDrawable( pathDrawable1 );
      }
      
            
      /*if (path!=null) {
        Point p0[] = new Point[path.size()];
        for (int i=0; i<path.size(); i++) {
          Point p = (Point) path.elementAt(i);      
          p.x *= TILE_SIZE;
          p.y *= TILE_SIZE;
          p0[i] = p;
        }
        Drawable pathDrawable = (Drawable) new PathDrawable( p0, Color.red, (short) ImageLibRef.AURA_PRIORITY ); 
        gDirector.addDrawable( pathDrawable );
      } */   
            
      if (smoothPath!=null) {       
        for (int i=0; i<smoothPath.size(); i++) {
          Point p = (Point) smoothPath.elementAt(i);                    
          p.x *= TILE_SIZE;
          p.y *= TILE_SIZE;
          System.out.println("smoothPath["+i+"] = ("+p.x+","+p.y+")");
        }        
      }
      myPlayer.initMovement(smoothPath);

    } else {
      System.out.println("object.getClass().getName() = " + object.getClass().getName());

      // Test to create multi players
      if ( object.getClass().getName().equals("wotlas.client.PlayerImpl") ) {
        myPlayer = (PlayerImpl) object;
        if (circle!=null) {
          gDirector.removeDrawable(circle);
          circle = null;
        }
        circle = new CircleDrawable(myPlayer.getDrawable(), 20, Color.yellow, (short) ImageLibRef.AURA_PRIORITY);
        gDirector.addDrawable(circle);
      }
    }
  }

 /*------------------------------------------------------------------------------------*/

 /** Called when user right-clic on JMapPanel
   */
  public void onRightClicJMapPanel(MouseEvent e) {
    PlayerImpl newPlayer;
    Rectangle screen = gDirector.getScreenRectangle();

    System.out.println("playerImpl creation : ");

    newPlayer = new PlayerImpl();
    newPlayer.init();
    newPlayer.setX(e.getX() + (int)screen.getX());
    newPlayer.setY(e.getY() + (int)screen.getY());
    gDirector.addDrawable(newPlayer.getDrawable());

    addPlayer(newPlayer);
  }

 /*------------------------------------------------------------------------------------*/

  /** To add a new player to the screen<br>
   * (called by client.message.description.PlayerDataMsgBehaviour)
   *
   * @player the player to add
   */
  public synchronized void addPlayer(PlayerImpl player) {
    players.put( player.getPrimaryKey(), player );
  }

  /** To remove a player
   *
   * @player the player to remove
   */
  public synchronized boolean removePlayer(PlayerImpl player) {
    players.remove(player.getPrimaryKey());
    return true;
  }

  /** To set our player<br>
   * (called by client.message.description.YourPlayerDataMsgBehaviour)
   *
   * @param player Our player
   */
  public void setCurrentPlayer(Player player) {
    myPlayer = (PlayerImpl) player;
  }

 /*------------------------------------------------------------------------------------*/

  public void locationUpdate() {
    // we call the right method whether the player is on
    // a TownMap, a WorldMap or in a Room.
    if ( myPlayer.getLocation().isRoom() )
      roomLocationUpdate();
    else if ( myPlayer.getLocation().isTown() )
      townLocationUpdate();
    else if ( myPlayer.getLocation().isWorld() )
      worldLocationUpdate();
  }
  
  public void roomLocationUpdate() {
    Room myRoom = worldManager.getRoom( myPlayer.getLocation() );
    
    // I - ROOMLINK INTERSECTION UPDATE ( is the player moving to another room ? )
    RoomLink rl = myRoom.isIntersectingRoomLink( myPlayer.getCurrentRectangle() );
  
      if ( rl!=null && !couldBeMovingToAnotherRoom ) {
        // Player is intersecting a RoomLink
        latestRoomLink = rl;
        couldBeMovingToAnotherRoom = true;
  
        // is there a Door ?
        if ( rl.getDoor()!=null ) {
          // nothing for now
        }
      } else if ( couldBeMovingToAnotherRoom ) {
        // ok, no intersection now, are we in an another room ?
        couldBeMovingToAnotherRoom = false;
  
        int newRoomID = myRoom.isInOtherRoom( latestRoomLink, myPlayer.getCurrentRectangle() );
               
        if ( newRoomID>=0 ) {
          // Ok, we move to this new Room
          myRoom.removePlayer( myPlayer );
          myPlayer.getLocation().setRoomID( newRoomID );
          myRoom.addPlayer( myPlayer );
        }
    } // End of part I
  
    // II - MAPEXIT INTERSECTION UPDATE ( is the player moving to another map ? )
    if ( myPlayer.isMoving() ) {
      Point destination = myPlayer.getEndPosition();
      MapExit mapExit = myRoom.isIntersectingMapExit( destination.x,
                                                      destination.y,
                                                      myPlayer.getCurrentRectangle() );
      if ( mapExit!=null ) {
        // Ok, we are going to a new map...
        myRoom.removePlayer( myPlayer );
        myPlayer.setLocation( mapExit.getTargetWotlasLocation() );
        cleanInteriorMapData(); // suppress drawables, shadows, data
        
        switch( mapExit.getType() ) {
          case MapExit.INTERIOR_MAP_EXIT :
            initInteriorMapDisplay(); // init new map
            break;
  
          case MapExit.TOWN_EXIT :
            initTownMapDisplay(); // init new map
            break;
        }
      }
    } // End of part II  
  }
  
  public void townLocationUpdate() {
    ;
  }

  public void worldLocationUpdate() {
    ;
  }
  
  public void initInteriorMapDisplay(  ) {
    // 1 - we load the images & init the graphicsDirector
    // 2 - we init our Player, add a shadow, and start the display
  }
  
  public void initTownMapDisplay(  ) {
    // 1 - we load the images & init the graphicsDirector
    // 2 - we init our Player (no shadow drawable) and start the display
  }
  
  /** suppress drawables, shadows, data
   */
  public void cleanInteriorMapData() {
    ;
  }
}
