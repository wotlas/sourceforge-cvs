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
// - changer la boucle du thread
// - mettre un lock sur le circle
// - modifier le point d'insertion dans InteriorMap : récupérer les coordonnées du joueur

// - remettre players

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

import java.util.Hashtable;
import java.util.Iterator;

/** A DataManager manages Game Data and client's connection.
 * It possesses a WorldManager
 *
 * @author Petrus, Aldiss
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

  /** True if we show debug informations
   */
  public static boolean SHOW_DEBUG = false;

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
  private Hashtable players;

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

  /** Our MapData.
   */
  private MapData myMapData;

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

  /** player's name
   */
  private MultiLineText mltPlayerName;

  /** Wotlas location name
   */
  private MultiLineText mltLocationName;

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

  /** To get the world manager.
   *
   * @return the world manager.
   */
  public WorldManager getWorldManager() {
    return worldManager;
  } 

  /** To get the graphicsDirector
   *
   * @return the graphicsDirector
   */
  public GraphicsDirector getGraphicsDirector() {
    return gDirector;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get startGameLock
   */
  public Object getStartGameLock() {
    return startGameLock;
  }

  /** To get MapData
   */
  public MapData getMapData() {
    return myMapData;
  }
  
  public JInfosPanel getInfosPanel() {
    return infosPanel;
  }

  public JMapPanel getMapPanel() {
    return mapPanel;
  }

  public JChatPanel getChatPanel() {
    return chatPanel;
  }

  public JPreviewPanel getPreviewPanel() {
    return previewPanel;
  }

  public JPlayerPanel getPlayerPanel() {
    return playerPanel;
  }

  public JLogPanel getLogPanel() {
    return logPanel;
  }

 /*------------------------------------------------------------------------------------*/
  
  /** To get the hashtable players
   */
  public Hashtable getPlayers() {
    return players;
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

  public String getDatabasePath() {
    return databasePath;
  }

  public String getImageDBHome() {
    return imageDBHome;
  }

 /*------------------------------------------------------------------------------------*/

  public AStarDouble getAStar() {
    return aStar;
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

    Debug.signal( Debug.NOTICE, null, "DataManager connected to GameServer" );

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

    Debug.signal( Debug.NOTICE, null, "DataManager not connected anymore to GameServer" );

  }

  /** Use this method to send a NetMessage to the server.
   *
   * @param message message to send to the player.
   */
  public void sendMessage( NetMessage message ) {
    synchronized( personalityLock ) {
      if ( personality!=null ) {
        personality.queueMessage( message );
      }
    }
  }

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
    Debug.signal( Debug.NOTICE, null, "DataManager::ShowInterface");

    // 0 - Create Image Library
    imageDBHome = databasePath + File.separator + IMAGE_LIBRARY;
    try {
      imageLib = ImageLibrary.createImageLibrary(imageDBHome);
    } catch( java.io.IOException ioe ) {
      ioe.printStackTrace();
      Debug.exit();
    }

    // 0 - Create Sound Library
    SoundLibrary.createSoundLibrary(databasePath);

    // 1 - Create Graphics Director
    gDirector = new GraphicsDirector( new LimitWindowPolicy() );

    // 2 - Retrieve player's informations
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

// RAJOUTER UNE ERREUR CRITIQUE SI PAS DE DONNEES

    myPlayer.setIsMaster( true );   // this player is controlled by the user.

    // Retreive player's location
    WotlasLocation location = myPlayer.getLocation();
//    if (SHOW_DEBUG)
        System.out.println("POSITION set to x:"+myPlayer.getX()+" y:"+myPlayer.getY()+" location is "+location);
// ALDISS
    // 3 - Create the drawable reference
/*    location.setWorldMapID(0);
    location.setTownMapID(0);
    location.setInteriorMapID(-1);
    location.setBuildingID(-1);
    location.setRoomID(-1);
    //myPlayer.setX(70);
    myPlayer.setX(31);
    //myPlayer.setY(640);
    myPlayer.setY(759);
    //myPlayer.setPosition(new ScreenPoint(70,640));
    myPlayer.setPosition(new ScreenPoint(31,759));
*/
    players = new Hashtable();
    
    // 4 - Create AStar
    //aStar = new AStarDouble();

    // 5 - Create the panels
    infosPanel = new JInfosPanel(myPlayer);
    mapPanel = new JMapPanel(gDirector, this);
    chatPanel = new JChatPanel();
    previewPanel = new JPreviewPanel();
    playerPanel = new JPlayerPanel();
    logPanel = new JLogPanel();
System.out.println("Displaying window");
    // 8 - Create main Frame
    mFrame = new JClientScreen(infosPanel, mapPanel, chatPanel, previewPanel, playerPanel, logPanel);
System.out.println("JCLient created");
    mFrame.init();
System.out.println("End of init");    

System.out.println("Changing map data");
    // 6 - Init map display
    changeMapData();

/*Object obj = new Object();
synchronized( obj ) {
try{
System.out.println("Waiaintg 4s");
  obj.wait(2000);
System.out.println("done, tick");
}
catch(Exception e) {e.printStackTrace();}
}*/

    // 7 - Start main loop tick
    Debug.signal( Debug.NOTICE, null, "Beginning to tick Graphics Director" );
    this.start();
System.out.println("tick thread started");

    mFrame.show();
System.out.println("Frame show");

    // 9 - Retreive other players informations    
    //personality.queueMessage(new AllDataLeftPleaseMessage());    
      addPlayer(myPlayer);

    // 10 - We can now ask for eventual remaining data
    // This step should have been done in the current MapData.init() but it was not
    // the cas because our DataManager thread was not started...
       sendMessage(new AllDataLeftPleaseMessage());
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

    delay = 20;
    if ( os.equals("Windows 2000") ) {
      delay = 25;
    }

    Object lock = new Object();
    while( true ) {
      now = System.currentTimeMillis();
      tick();

      deltaT = (int) (System.currentTimeMillis()-now);
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

    // Update myPlayer's location
    myMapData.locationUpdate(myPlayer);
    
    // Update players drawings    
    synchronized(players) {
      Iterator it = players.values().iterator();
      while( it.hasNext() ) {
        ( (PlayerImpl) it.next() ).tick();
      }
    }

    if (circle != null) {
      if (circleLife < CIRCLE_LIFETIME) {
        circleLife++;
      } else {
        Debug.signal( Debug.NOTICE, this, "destroy circle");
        gDirector.removeDrawable(circle);
        circle = null;
        circleLife = 0;
      }
    }

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

    if (SHOW_DEBUG)
      System.out.println("DataManager::onLeftClicJMapPanel");

    Rectangle screen = gDirector.getScreenRectangle();

    Object object = gDirector.findOwner( e.getX(), e.getY());

    if (object == null) {
      int newX = e.getX() + (int)screen.getX();
      int newY = e.getY() + (int)screen.getY();
      myPlayer.moveTo( new Point(newX,newY) );
    } else {
      if (SHOW_DEBUG)
        System.out.println("object.getClass().getName() = " + object.getClass().getName());

      if ( object.getClass().getName().equals("wotlas.client.PlayerImpl") ) {
        PlayerImpl selectedPlayer = (PlayerImpl) object;
        //if (circle!=null) {
          //gDirector.removeDrawable(circle);
          //circle = null;
        //}
        //circle = new CircleDrawable(myPlayer.getDrawable(), 20, Color.yellow, (short) ImageLibRef.AURA_PRIORITY);
        //gDirector.addDrawable(circle);

           TextDrawable textDrawable = new TextDrawable( selectedPlayer.getFullPlayerName(),
                                                         selectedPlayer.getDrawable(), Color.black,
                                                         12.0f, "Lblack.ttf",
                                                         ImageLibRef.TEXT_PRIORITY, 5000 );
           gDirector.addDrawable(textDrawable);

        // Aura
           gDirector.addDrawable( selectedPlayer.getWotCharacter().getAura() );
      }
    }
System.out.println("END JCLICK");
  }

 /*------------------------------------------------------------------------------------*/

 /** Called when user right-clic on JMapPanel
   */
  public void onRightClicJMapPanel(MouseEvent e) {
    if (SHOW_DEBUG) {
      System.out.println("Hiding debug informations");
    } else {
      System.out.println("Showing debug informations");
    }
    SHOW_DEBUG = !SHOW_DEBUG;
    
    /*
    NetMessage toto = new TotoMessage();
       
    if ( myPlayer.getLocation().isRoom() ) {
      // Current Room    
      Room room = player.getMyRoom();                    
      if ( room==null ) return;     
      Hashtable players = room.getPlayers();            
      synchronized( players ) {
        Iterator it = players.values().iterator();               
        PlayerImpl p;
        while ( it.hasNext() ) {
          p = (PlayerImpl) it.next();
          p.sendMessage( toto );          
        }
      }

      // Other rooms      
      if ( room.getRoomLinks()==null ) return;
      Room otherRoom;
      for( int i=0; i<room.getRoomLinks().length; i++ ) {
        otherRoom = room.getRoomLinks()[i].getRoom1();
        if ( otherRoom==room )
          otherRoom = room.getRoomLinks()[i].getRoom2();
          
        players = otherRoom.getPlayers();
        synchronized( players ) {
          Iterator it = players.values().iterator();
          PlayerImpl p;
          while ( it.hasNext() ) {
            p = (PlayerImpl)it.next();
            p.sendMessage( toto );
          }
        }
      }
    }
    */
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

  /** To change type of MapData
   */
  public void changeMapData() {
    if ( myPlayer.getLocation().isRoom() ) {
      myMapData = new InteriorMapData();
    }
    else if ( myPlayer.getLocation().isTown() ) {
      System.out.println("town");
      myMapData = new TownMapData();
    }
    else if ( myPlayer.getLocation().isWorld() ) {
      myMapData = new WorldMapData();
    }
    myMapData.initDisplay(myPlayer);
  }

 /*------------------------------------------------------------------------------------*/

  /** To suppress drawables, shadows, data
   */
  public void cleanInteriorMapData() {
    gDirector.removeAllDrawables();
  }

 /*------------------------------------------------------------------------------------*/

  /** To draw a rectangle on the screen
   *
   * @param rect the rectangle to display
   */
  public void drawScreenRectangle(Rectangle rect, Color color) {
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

  /** To close the client
   */
  public void exit() {
    if (gDirector!=null)
      gDirector.removeAllDrawables();
    //closeConnection();
    Debug.exit();
  }


 /*------------------------------------------------------------------------------------*/
  //////////////// ALDISS ajout d'une méthod pour récupérer le joueur courant.
   /** To get the master player.
    */
    public PlayerImpl getMyPlayer() {
       return myPlayer;
    }

  ////////////////// FIN ALDISS
}
