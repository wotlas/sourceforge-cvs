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
// - modifier le point d'insertion dans InteriorMap : récupérer les coordonnées du joueur

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

import wotlas.libs.sound.SoundLibrary;

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

  /** Path to the local images database.
   */
  private String imageDBHome;

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
  private JInfosPanel infosPanel;
  private JMapPanel mapPanel;
  private JChatPanel chatPanel;
  private JPreviewPanel previewPanel;
  private JPlayerPanel playerPanel;
  private JLogPanel logPanel;

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

    // 0 - Create Image Library
    imageDBHome = databasePath + File.separator + IMAGE_LIBRARY;
    try {
      imageLib = ImageLibrary.createImageLibrary(imageDBHome);
    } catch( java.io.IOException ioe ) {
      ioe.printStackTrace();
      Debug.exit();
    }

    // 0 - Create Sound Library
    SoundLibrary.createSoundLibrary( databasePath);

    // 1 - Create Graphics Director
    gDirector = new GraphicsDirector( new LimitWindowPolicy() );

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

    // 4 - Create AStar
    aStar = new AStarDouble();

  //*** World
    if (location.isWorld()) {
      System.out.println("\tWorld");
      initWorldMapDisplay(location);
    }

  //*** Town
    if (location.isTown()) {
      System.out.println("Town");
      initTownMapDisplay(location);
    }

  //*** Room
    if (location.isRoom()) {
      System.out.println("InterioMap");
      initInteriorMapDisplay(location);
    }

    // 6 - Create the panels
    infosPanel = new JInfosPanel(myPlayer);

    Room room = worldManager.getRoom(myPlayer.getLocation());
    infosPanel.setLocation(room.getFullName());
    mapPanel = new JMapPanel(gDirector, this);
    chatPanel = new JChatPanel();
    previewPanel = new JPreviewPanel();
    playerPanel = new JPlayerPanel();
    logPanel = new JLogPanel();

    // Create main Frame
    mFrame = new JClientScreen(infosPanel, mapPanel, chatPanel, previewPanel, playerPanel, logPanel);
    mFrame.init();
    mFrame.show();

    // Start main loop tick
    Debug.signal( Debug.NOTICE, null, "Beginning to tick Graphics Director" );
    this.start();

    // Retreive other players informations
    players = new HashMap();
    //personality.queueMessage(new AllDataLeftPleaseMessage());

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

    myPlayer.tick();
    locationUpdate();

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


    /*
    Iterator it = players.values().iterator();
    //System.out.println("tick players");
    while( it.hasNext() ) {
      ( (PlayerImpl) it.next() ).tick();
    }
    //System.out.println("end tick");
    */
    
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
      //System.out.println("endPosition = ("+newX+","+newY+")");
      myPlayer.setEndPosition(newX, newY);

      // Create the trajectory
      wotlas.utils.List path = aStar.findPath( new Point( myPlayer.getX()/TILE_SIZE, myPlayer.getY()/TILE_SIZE),
                                           new Point(newX/TILE_SIZE, newY/TILE_SIZE));

      if (path!=null) {
        Point p0[] = new Point[path.size()];
        for (int i=0; i<path.size(); i++) {
          Point p = (Point) path.elementAt(i);
          //System.out.println("path[" + i + "] = " + p.x  + "," + p.y);
          p0[i] = new Point(p.x*TILE_SIZE, p.y*TILE_SIZE);
        }
        Drawable pathDrawable = (Drawable) new PathDrawable( p0, Color.red, (short) ImageLibRef.AURA_PRIORITY );
        gDirector.addDrawable( pathDrawable );
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

      WotlasLocation location = myPlayer.getLocation();
      if ( myPlayer.getLocation().isRoom() ) {
        // Room -> we use rotations
        wotlas.utils.List smoothPath = aStar.smoothPath(path);
        
        if (smoothPath!=null) {
          Point p1[] = new Point[smoothPath.size()];
          for (int i=0; i<smoothPath.size(); i++) {
            Point p = (Point) smoothPath.elementAt(i);
            //System.out.println("smoothPath[" + i + "] = " + p.x  + "," + p.y);
            p1[i] = new Point(p.x*TILE_SIZE, p.y*TILE_SIZE);
          }
          Drawable pathDrawable1 = (Drawable) new PathDrawable( p1, Color.blue, (short) ImageLibRef.AURA_PRIORITY );
          gDirector.addDrawable( pathDrawable1 );
        }        
        
        if (smoothPath!=null) {
          for (int i=0; i<smoothPath.size(); i++) {
            Point p = (Point) smoothPath.elementAt(i);
            p.x *= TILE_SIZE;
            p.y *= TILE_SIZE;
            //System.out.println("smoothPath["+i+"] = ("+p.x+","+p.y+")");
          }          
        }
        myPlayer.initMovement(smoothPath);
      } else {
        // World or Town -> we don't use rotations
        myPlayer.setTrajectory(path);
      }          
      
      

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

 /*------------------------------------------------------------------------------------*/

  public void roomLocationUpdate() {

    Room myRoom = worldManager.getRoom( myPlayer.getLocation() );

    // I - ROOMLINK INTERSECTION UPDATE ( is the player moving to another room ? )
    RoomLink rl = myRoom.isIntersectingRoomLink( myPlayer.getCurrentRectangle() );

      if ( rl!=null && !couldBeMovingToAnotherRoom ) {
        // Player is intersecting a RoomLink
        System.out.println("Insersecting a RoomLink");
        latestRoomLink = rl;
        couldBeMovingToAnotherRoom = true;

        // is there a Door ?
        if ( rl.getDoor()!=null ) {
          // nothing for now
        }
      } else if ( rl==null && couldBeMovingToAnotherRoom ) {
        // ok, no intersection now, are we in an another room ?
        System.out.println("ok, no intersection now, are we in an another room ?");
        couldBeMovingToAnotherRoom = false;

        int newRoomID = myRoom.isInOtherRoom( latestRoomLink, myPlayer.getCurrentRectangle() );

        if ( newRoomID>=0 ) {
          // Ok, we move to this new Room
          myRoom.removePlayer( myPlayer );
          myPlayer.getLocation().setRoomID( newRoomID );
// Not sure : update myRoom ??
          myRoom.addPlayer( myPlayer );

          System.out.print("Move to another room : " + newRoomID);
          Room room = worldManager.getRoom(myPlayer.getLocation());
          System.out.println(room.getFullName());
          infosPanel.setLocation(room.getFullName());

          RoomLink[] roomLinks = room.getRoomLinks();
          System.out.println("\tRoomLink");
          for (int i=0; i<roomLinks.length; i++) {
            System.out.println("\t\troomLinks["+i+"] = " + roomLinks[i]);
            drawScreenRectangle(roomLinks[i].toRectangle(), Color.green);
          }

          MapExit[] mapExits = room.getMapExits();
          if (mapExits!= null) {
            System.out.println("\tMapExit");
            for (int i=0; i<mapExits.length; i++) {
              System.out.println("\t\tmapExits["+i+"] = " + mapExits[i]);
              drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
            }
          }


        } else {
          System.out.println("We are still in the same room" + newRoomID);
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
        System.out.println("Ok, we are going to a new map...");

        myPlayer.stopMoving();
        myRoom.removePlayer( myPlayer );
        myPlayer.setLocation( mapExit.getTargetWotlasLocation() );
        cleanInteriorMapData(); // suppress drawables, shadows, data

        ScreenPoint targetPoint = mapExit.getTargetPosition();
        myPlayer.setX(targetPoint.x);
        myPlayer.setY(targetPoint.y);
        myPlayer.setPosition(targetPoint);

        switch( mapExit.getType() ) {
          case MapExit.INTERIOR_MAP_EXIT :
            System.out.println("Move to another InteriorMap");
            initInteriorMapDisplay(myPlayer.getLocation()); // init new map
            break;

          case MapExit.TOWN_EXIT :
            System.out.println("Move to TownMap");
            initTownMapDisplay(myPlayer.getLocation()); // init new map
            break;

          case MapExit.BUILDING_EXIT :
            System.out.println("Move to Building");
            initTownMapDisplay(myPlayer.getLocation()); // init new map
            break;

          default:
            Debug.signal( Debug.CRITICAL, this, "Unknown mapExit : " + mapExit.getType() );
        }
      }
    } // End of part II
  }

 /*------------------------------------------------------------------------------------*/

  public void townLocationUpdate() {
    
    TownMap townMap = worldManager.getTownMap( myPlayer.getLocation() );
   
    Point destination = myPlayer.getEndPosition();
    
    // MAPEXIT INTERSECTION UPDATE ( is the player moving to another map ? )
    MapExit mapExit = townMap.isIntersectingMapExit( destination.x,
                                                     destination.y,
                                                     myPlayer.getCurrentRectangle() );
    
    if ( mapExit!=null ) {
      // Ok, we are going to a world map...
      System.out.println("Ok, we are going to a world map...");

      myPlayer.stopMoving();
      myPlayer.setLocation( mapExit.getTargetWotlasLocation() );            
      cleanInteriorMapData(); // suppress drawables, shadows, data

      ScreenPoint targetPoint = mapExit.getTargetPosition();
      System.out.println("targetPoint = " + targetPoint);
      myPlayer.setX(targetPoint.x);
      myPlayer.setY(targetPoint.y);
      myPlayer.setPosition(targetPoint);  

      if (mapExit.getType() == MapExit.TOWN_EXIT) {
        System.out.println("Move to WorldMap");
        initWorldMapDisplay(myPlayer.getLocation());
      } else {
        Debug.signal( Debug.CRITICAL, this, "Unknown mapExit : " + mapExit.getType() );
      }
    }
    
    /*Building buildingMap = townMap.isEnteringBuilding( destination.x,
                                                       destination.y,
                                                       myPlayer.getCurrentRectangle() );*/
    Building buildingMap = townMap.isEnteringBuilding( myPlayer.getX(),
                                                       myPlayer.getY(),
                                                       myPlayer.getCurrentRectangle() );
   
    if ( buildingMap != null ) {
      // intersection with a Building, which MapExit are we using ?
      System.out.println("Ok, we are going to a new building...");
      
      myPlayer.stopMoving();
      
      System.out.println("\tbuildingMap.getFullName() = " + buildingMap.getFullName());
      System.out.println("\tbuildingMap.getShortName() = " + buildingMap.getShortName());
                  
      System.out.print("\tmyPlayer.getAngle() = ");
      System.out.println(myPlayer.getAngle()*Math.PI/180);
      
      mapExit = buildingMap.findTownMapExit( myPlayer.getAngle() );      
      
      System.out.println("\tmapExit.getType() = " + mapExit.getType());
      System.out.println("\tmapExit.getMapExitSide() = " + mapExit.getMapExitSide());
      System.out.println("\tmapExit.getTargetWotlasLocation() = " + mapExit.getTargetWotlasLocation());      
      System.out.println("\tmapExit.getMapExitLocation() = " + mapExit.getMapExitLocation());      
            
      myPlayer.setLocation(mapExit.getMapExitLocation());
      
      System.out.println(mapExit);
      cleanInteriorMapData();
   
      myPlayer.setX( mapExit.getX() + mapExit.getWidth()/2 );
      myPlayer.setY( mapExit.getY() + mapExit.getHeight()/2 );
   
      initInteriorMapDisplay(myPlayer.getLocation()); // init new map
    }
  }

 /*------------------------------------------------------------------------------------*/
  
  /** To update World location
   */
  public void worldLocationUpdate() {

    WorldMap worldMap = worldManager.getWorldMap( myPlayer.getLocation() );

    Point destination = myPlayer.getEndPosition();
    TownMap townMap = worldMap.isEnteringTown( destination.x,
                                               destination.y,
                                               myPlayer.getCurrentRectangle() );

    if( townMap != null ) {
      // intersection with a TownMap, which MapExit are we using ?
      System.out.println("Ok, we are going to a new town...");

      myPlayer.stopMoving();
      MapExit mapExit = townMap.findTownMapExit( myPlayer.getCurrentRectangle() );
      myPlayer.setLocation( mapExit.getTargetWotlasLocation() );
      cleanInteriorMapData(); // suppress drawables, shadows, data

      // We set our player on the middle of the MapExit
      myPlayer.setX( mapExit.getX() + mapExit.getWidth()/2 );
      myPlayer.setY( mapExit.getY() + mapExit.getHeight()/2 );

      initTownMapDisplay(myPlayer.getLocation()); // init new map
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** To init InteriorMap
   */
  public void initInteriorMapDisplay(WotlasLocation location) {
    myPlayer.init();

    ImageIdentifier backgroundImageID = null;   // background image identifier
    Drawable background = null;                 // background image

    // 1 - We load the InteriorMap
    InteriorMap imap = worldManager.getInteriorMap(location);
    System.out.println("InteriorMap");
    System.out.println("\tfullName = "  + imap.getFullName());
    System.out.println("\tshortName = " + imap.getShortName());

    // 2 - We load the room
    Room room = worldManager.getRoom(location);
    System.out.println("Room");
    System.out.println("\tfullName = "       + room.getFullName());
    System.out.println("\tshortName = "      + room.getShortName());

    ScreenPoint insertionPoint = room.getInsertionPoint();
    System.out.println("\tinsertionPoint = " + insertionPoint);



    backgroundImageID = imap.getInteriorMapImage();
    System.out.println("\tbackgroundImageID = " + backgroundImageID);

    background = (Drawable) new MultiRegionImage(myPlayer.getDrawable(),              // our reference for image loading
                                                 650,                                 // perception radius
                                                 imap.getImageRegionWidth(),          // grid deltax
                                                 imap.getImageRegionHeight(),         // grid deltay
                                                 imap.getImageWidth(),                // image's total width
                                                 imap.getImageHeight(),               // image's total height
                                                 imap.getInteriorMapImage()           // base image identifier
                                                );

    // 3 - We set player's position

    myPlayer.setX(insertionPoint.x);
    myPlayer.setY(insertionPoint.y);
    myPlayer.setPosition(insertionPoint);


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

    // 5 - We load the mask image and initialize the Astar algo.
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

    aStar.setMask( BinaryMask.create( bufIm ) );
    aStar.setSpriteSize(4);
    bufIm.flush(); // free image resource

    // 6 - Init the GraphicsDirector
    gDirector.init( background,               // background drawable
                    myPlayer.getDrawable(),   // reference for screen movements
                    new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight )   // screen default dimension
                   );

    RoomLink[] roomLinks = room.getRoomLinks();
    System.out.println("\tRoomLink");
    for (int i=0; i<roomLinks.length; i++) {
      System.out.println("\t\troomLinks["+i+"] = " + roomLinks[i]);
      drawScreenRectangle(roomLinks[i].toRectangle(), Color.green);
    }

    MapExit[] mapExits = room.getMapExits();
    if (mapExits!= null) {
      System.out.println("\tMapExit");
      for (int i=0; i<mapExits.length; i++) {
        System.out.println("\t\tmapExits["+i+"] = " + mapExits[i]);
        drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
      }
    }
    
    // 7 - We add visual properties to the player (shadows...)
    myPlayer.initVisualProperties(gDirector);

    // 8 - We play music
    SoundLibrary.getSoundLibrary().playMusic( "tar-valon-01.mid" );
  }

 /*------------------------------------------------------------------------------------*/

  public void initTownMapDisplay(WotlasLocation location) {
    myPlayer.init();

    ImageIdentifier backgroundImageID = null;   // background image identifier
    Drawable background = null;                 // background image

    // 1 - We load the TownMap
    TownMap townMap = worldManager.getTownMap(location);
    System.out.println("TownMap");
    System.out.println("\tfullName = "  + townMap.getFullName());
    System.out.println("\tshortName = " + townMap.getShortName());
    infosPanel.setLocation(townMap.getFullName());

    backgroundImageID = townMap.getTownImage();
    System.out.println("\tbackgroundImageID = " + backgroundImageID);

    background = (Drawable) new MotionlessSprite(
                                            0,                        // ground x=0
                                            0,                        // ground y=0
                                            backgroundImageID,        // image
                                            ImageLibRef.MAP_PRIORITY, // priority
                                            false                     // no animation
                                        );

    // 3 - We set player's position

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

    // 5 - We load the mask image and initialize the Astar algo.
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

    aStar.setMask( BinaryMask.create( bufIm ) );
    aStar.setSpriteSize(1);
    bufIm.flush(); // free image resource

    // 6 - Init the GraphicsDirector
    gDirector.init( background,               // background drawable
                    myPlayer.getDrawable(),   // reference for screen movements
                    new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight )   // screen default dimension
                   );

    // 7 - We add buildings' images
    Building buildings[] = townMap.getBuildings();
    if (buildings!=null) {
      System.out.println("\tBuildings");
      ImageIdentifier buildingImageID = null;   // building image identifier
      Drawable buildingImage = null;            // building image
      for (int i=0; i<buildings.length; i++) {
        System.out.println("\t\tbuildings["+i+"] = " + buildings[i]);
        buildingImageID = buildings[i].getSmallBuildingImage();
        Rectangle position = buildings[i].toRectangle();
        buildingImage = (Drawable) new MotionlessSprite(
                                            position.x,
                                            position.y,
                                            buildingImageID,          // image
                                            ImageLibRef.MAP_PRIORITY, // priority
                                            false                     // no animation
                                        );
        gDirector.addDrawable(buildingImage);
      }
    }

    // 8 - We add MapExits' images
    MapExit[] mapExits = townMap.getMapExits();
    if (mapExits!= null) {
      System.out.println("\tMapExit");
      for (int i=0; i<mapExits.length; i++) {
        System.out.println("\t\tmapExits["+i+"] = " + mapExits[i]);
        drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
      }
    }
  
  }

 /*------------------------------------------------------------------------------------*/

  public void initWorldMapDisplay(WotlasLocation location) {
    myPlayer.init();

    ImageIdentifier backgroundImageID = null;   // background image identifier
    Drawable background = null;                 // background image

    // 1 - We load the WorldMap
    WorldMap worldMap = worldManager.getWorldMap(location);
    System.out.println("WorldMap");
    System.out.println("\tfullName = "  + worldMap.getFullName());
    System.out.println("\tshortName = " + worldMap.getShortName());
    infosPanel.setLocation(worldMap.getFullName());

    backgroundImageID = worldMap.getWorldImage();
    System.out.println("\tImageIdentifier = " + backgroundImageID);

    background = (Drawable) new MotionlessSprite(
                                            0,                        // ground x=0
                                            0,                        // ground y=0
                                            backgroundImageID,        // image
                                            ImageLibRef.MAP_PRIORITY, // priority
                                            false                     // no animation
                                        );

    // 3 - We set player's position

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

    // 5 - We load the mask image and initialize the Astar algo.
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

    aStar.setMask( BinaryMask.create( bufIm ) );
    aStar.setSpriteSize(1);
    bufIm.flush(); // free image resource

    // 6 - Init the GraphicsDirector
    gDirector.init( background,               // background drawable
                    myPlayer.getDrawable(),   // reference for screen movements
                    new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight )   // screen default dimension
                   );
    
    // 7 - We add towns' images
    TownMap towns[] = worldMap.getTownMaps();
    if (towns!=null) {
      System.out.println("\tTowns");
      ImageIdentifier townImageID = null;   // town image identifier
      Drawable townImage = null;            // town image
      for (int i=0; i<towns.length; i++) {
        System.out.println("\t\ttowns["+i+"] = " + towns[i]);
        townImageID = towns[i].getSmallTownImage();
        Rectangle position = towns[i].toRectangle();
        townImage = (Drawable) new MotionlessSprite(
                                            position.x,
                                            position.y,
                                            townImageID,              // image
                                            ImageLibRef.MAP_PRIORITY, // priority
                                            false                     // no animation
                                        );
        gDirector.addDrawable(townImage);
      }
    }

  }

 /*------------------------------------------------------------------------------------*/

  /** suppress drawables, shadows, data
   */
  public void cleanInteriorMapData() {
    gDirector.removeAllDrawables();
  }

 /*------------------------------------------------------------------------------------*/

  /** To draw a rectangle on the screen
   *
   * @param rect the rectangle to display
   */
  private void drawScreenRectangle(Rectangle rect, Color color) {
    Point p[] = new Point[5];
    int x = (int) rect.getX();
    int y = (int) rect.getY();
    int width = (int) rect.getWidth();
    int height = (int) rect.getHeight();
    p[0] = new Point(x,y);
    p[1] = new Point(x+width, y);
    p[2] = new Point(x+width, y+height);
    p[3] = new Point(x, y+height);
    p[4] = new Point(x,y);
    Drawable pathDrawable = (Drawable) new PathDrawable( p, color, (short) ImageLibRef.AURA_PRIORITY );
    gDirector.addDrawable( pathDrawable);
  }

 /*------------------------------------------------------------------------------------*/

}
