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
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.libs.net.NetPersonality;
import wotlas.libs.net.NetPingListener;
import wotlas.libs.net.utils.NetQueue;

import wotlas.libs.persistence.*;

import wotlas.libs.sound.SoundLibrary;

import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.utils.List;
import wotlas.utils.ScreenPoint;
import wotlas.utils.ScreenRectangle;
import wotlas.utils.Tools;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.swing.*;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

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

  /** Tick Thread Lock.
   */
  private Object pauseTickThreadLock = new Object();

  /** Do we have to pause the tick thread ?
   */
  private boolean pauseTickThread;

 /*------------------------------------------------------------------------------------*/

  /** Our current player.
   */
  private ProfileConfig currentProfileConfig;

  /** Our playerImpl.
   */
  private PlayerImpl myPlayer;
  
  /** Selected player.
   */
  private PlayerImpl selectedPlayer;

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
  
  /** True if player was diconnected end resumed the game
   */
  private boolean isResuming = false;

  /** Our MapData.
   */
  private MapData myMapData;

  /** Our client interface frame.
   */
  private JClientScreen mFrame;
  private JInfosPanel infosPanel;
  private JMapPanel mapPanel;
  private JChatPanel chatPanel;
  private JOptionsPanel optionsPanel;
  private JPlayerPanel playerPanel;
  private JLogPanel logPanel;
  private GraphicPingPanel pingPanel;

 /*------------------------------------------------------------------------------------*/

   /** NetQueue for synchronous messages. Messages that want to be run after the current
    *  tick should call a queueMessage() on this NetQueue.
    *  NetMessageBehaviours should use the invokeLater() method to queue a message.
    */
      private NetQueue syncMessageQueue = new NetQueue(1,3);

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
  
  /** To get JInfosPanel.
   */
  public JInfosPanel getInfosPanel() {
    return infosPanel;
  }

  /** To get JClientScreen.
   */
  public JClientScreen getClientScreen() {
    return mFrame;
  }

  /** To get JMapPanel.
   */
  public JMapPanel getMapPanel() {
    return mapPanel;
  }

  /** To get JChatPanel.
   */
  public JChatPanel getChatPanel() {
    return chatPanel;
  }

  /** To get JPreviewPanel.
   */
  public JOptionsPanel getOptionsPanel() {
    return optionsPanel;
  }

  /** To get JPlayerPanel.
   */
  public JPlayerPanel getPlayerPanel() {
    return playerPanel;
  }

  /** To get JLogPanel.
   */
  public JLogPanel getLogPanel() {
    return logPanel;
  }

 /*------------------------------------------------------------------------------------*/

  /** Set to true to show debug information
   */
  public void showDebug(boolean value) {
    SHOW_DEBUG = value;
  }

 /*------------------------------------------------------------------------------------*/
  
  /** To get the hashtable players
   */
  public Hashtable getPlayers() {
    return players;
  }
 
  /** To get selected player
   */
  public String getSelectedPlayerKey() {
    if (selectedPlayer!=null)
      return selectedPlayer.getPrimaryKey();
    return null;
  }
  
  /** To remove the circle
   */
  public void removeCircle() {
    gDirector.removeDrawable(circle);
    circle = null;
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

  /** To test if player was diconnected
   *
   * @return true if player was disconnected
   */
  public boolean isResuming() {
    return isResuming;
  }
  
  /** To set whether player has finished resuming the game
   */
  public void setIsResuming(boolean value) {
    this.isResuming = value;
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


      JAccountWizard host = new JAccountWizard(personality);
      wotlas.utils.SwingTools.centerComponent(host);
      host.init();
      host.start();

      if (personality==null) {
        Debug.signal( Debug.ERROR, this, "Connection closed by AccountServer" );
        return;
      }

      Debug.signal( Debug.NOTICE, null, "New account created !" );
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

    pauseTickThread();

     if ( (mFrame!=null) && (mFrame.isShowing()) ) {

        if( !ClientManager.getDefaultClientManager().getAutomaticLogin() ) {
           gDirector.removeAllDrawables();
           showWarningMessage("Connection to Server lost ! Re-connect to the game...");
        }

        Runnable runnable = new Runnable() {
           public void run() {
              ClientManager.getDefaultClientManager().start(1);  // we restart the ClientManager
           }                                                     // on the Login entry
        };

        SwingUtilities.invokeLater( runnable );
     }
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

    if (imageLib !=null) {
      // All data have already been initialized
      // => there was a disconnexion and player has resumed the game
      resumeInterface();
      return;
    }
    
    // 0 - Create Image Library
    imageDBHome = databasePath + File.separator + IMAGE_LIBRARY;
    try {
      imageLib = ImageLibrary.createImageLibrary( imageDBHome, databasePath+File.separator+"fonts");
    } catch( Exception ex ) {
      ex.printStackTrace();
      Debug.exit();
    }
    
    // 0 - Load Client Configuration
    ClientConfiguration clientConfiguration;
    try {
      clientConfiguration = (ClientConfiguration) PropertiesConverter.load(ClientDirector.CLIENT_OPTIONS);
    } catch (PersistenceException pe) {
      Debug.signal( Debug.ERROR, this, "Failed to load client configuration : " + pe.getMessage() );
      clientConfiguration = new ClientConfiguration();    
    }
    
    //
    SoundLibrary.getSoundLibrary().setNoMusic(clientConfiguration.getNoMusic());
    if (clientConfiguration.getMusicVolume()>0)
      SoundLibrary.getSoundLibrary().setMusicVolume((short) clientConfiguration.getMusicVolume());
    SoundLibrary.getSoundLibrary().setNoSound(clientConfiguration.getNoSound());
    if (clientConfiguration.getSoundVolume()>0)
      SoundLibrary.getSoundLibrary().setSoundVolume((short) clientConfiguration.getSoundVolume());
    
    // 1 - Create Graphics Director
    gDirector = new GraphicsDirector( new LimitWindowPolicy(), imageLib );

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

    myPlayer.setIsMaster( true );   // this player is controlled by the user.

    // 3 - Retreive player's location
    WotlasLocation location = myPlayer.getLocation();
    if (SHOW_DEBUG)
        System.out.println("POSITION set to x:"+myPlayer.getX()+" y:"+myPlayer.getY()+" location is "+location);

    players = new Hashtable();
    
    // 4 - Create the panels
    infosPanel = new JInfosPanel(myPlayer);
    mapPanel = new JMapPanel(gDirector, this);
    chatPanel = new JChatPanel();
    optionsPanel = new JOptionsPanel();
    playerPanel = new JPlayerPanel();
    logPanel = new JLogPanel();    
    pingPanel = new GraphicPingPanel();
    personality.setPingListener( (NetPingListener) pingPanel );

    if (SHOW_DEBUG)
        System.out.println("Displaying window");
    
    
    // Welcome message
    sendMessage(new WelcomeMessage());

    // 5 - Create main Frame
    mFrame = new JClientScreen(infosPanel, mapPanel, chatPanel, optionsPanel, playerPanel, logPanel, pingPanel);

    if (SHOW_DEBUG)
       System.out.println("JClient created");
    mFrame.init();
    
    // 6 - Client configuration
    if ( (clientConfiguration.getClientWidth()>0) && (clientConfiguration.getClientHeight()>0) )
      mFrame.setSize(clientConfiguration.getClientWidth(),clientConfiguration.getClientHeight());

    // 7 - Init map display
    if (SHOW_DEBUG)
       System.out.println("Changing map data");
    changeMapData();


    // 8 - Start main loop tick
    Debug.signal( Debug.NOTICE, null, "Beginning to tick Graphics Director" );
    this.start();

    if (SHOW_DEBUG)
        System.out.println("Frame show");
    mFrame.show();
    
    // 9 - Retrieve other players informations        
    addPlayer(myPlayer);

    // 10 - We can now ask for eventual remaining data
    // This step should have been done in the current MapData.init() but it was not
    // the cas because our DataManager thread was not started...
    sendMessage(new AllDataLeftPleaseMessage());
    
    // Free memory
    try {
      PropertiesConverter.save(clientConfiguration, ClientDirector.CLIENT_OPTIONS);
    } catch (PersistenceException pe) {
      Debug.signal( Debug.ERROR, this, "Failed to save client configuration : " + pe.getMessage() );       
    }
    clientConfiguration = null;
  }

 /*------------------------------------------------------------------------------------*/

  /** Resume play in case of server deconnection
   */
  public void resumeInterface() {
    Debug.signal( Debug.NOTICE, null, "DataManager::ResumeInterface");

    // Reset the data
    chatPanel.reset();
    personality.setPingListener( (NetPingListener) pingPanel );
    
    mFrame.show();

    // Retrieve player's informations
    try {
      synchronized(startGameLock) {
        personality.queueMessage(new MyPlayerDataPleaseMessage());
        startGameLock.wait(CONNECTION_TIMEOUT);
      }
    } catch (InterruptedException ie) {
      showWarningMessage("Cannot retreive your informations from the Game Server !");
      Debug.exit();
    }

    myPlayer.setIsMaster( true );   // this player is controlled by the user.

    // Retreive player's location
    WotlasLocation location = myPlayer.getLocation();
    if (SHOW_DEBUG)
        System.out.println("POSITION set to x:"+myPlayer.getX()+" y:"+myPlayer.getY()+" location is "+location);

    players = new Hashtable();
    
     // Init map display
    changeMapData();

    addPlayer(myPlayer);
    playerPanel.reset();

    // Resume Tick Thread
    resumeTickThread();

    // We can now ask for eventual remaining data
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

    if ( os.equals("Windows 2000") || os.equals("Windows XP") )
      delay = 35;

    pauseTickThread = false;

      while( true ) {
          now = System.currentTimeMillis();

       // Pause Thread ?
          synchronized( pauseTickThreadLock ) {
             if(pauseTickThread)
                try{
                   pauseTickThreadLock.wait();
                }catch(Exception e){}
          }

       // Tick
          tick();

          deltaT = (int) (System.currentTimeMillis()-now);

          if (deltaT<delay)
              Tools.waitTime(delay-deltaT);
      }
  }

 /*------------------------------------------------------------------------------------*/

  /** Tick
   */
  public void tick() {

    // I - Update myPlayer's location
       myMapData.locationUpdate(myPlayer);
   
     
    // II - Update players drawings    
       synchronized(players) {
          Iterator it = players.values().iterator();

          while( it.hasNext() )
              ( (PlayerImpl) it.next() ).tick();
       }

       if ( circle!=null ) {
          circle.tick();
       }
          
    // III - Graphics Director update & redraw
       gDirector.tick();


    // IV - Sync Messages Execution
       NetMessageBehaviour syncMessages[] = syncMessageQueue.pullMessages();
       
       for( int i=0; i<syncMessages.length; i++)
            syncMessages[i].doBehaviour( this );
  }

 /*------------------------------------------------------------------------------------*/

  /** To pause the tick thread.
   */
   private void pauseTickThread() {
          synchronized( pauseTickThreadLock ) {
                   pauseTickThread=true;
          }
   }

 /*------------------------------------------------------------------------------------*/

  /** To resume the tick thread.
   */
   private void resumeTickThread() {
          synchronized( pauseTickThreadLock ) {
                   pauseTickThread=false;
                   pauseTickThreadLock.notify();
          }
   }

 /*------------------------------------------------------------------------------------*/

  /** To invoke the code of the specified message just after the current tick.
   *  This method can be called multiple times and is synchronized.
   */
   public void invokeLater( NetMessageBehaviour msg ) {
      syncMessageQueue.queueMessage( msg );
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
    Object object = gDirector.findOwner( e.getX(), e.getY() );

    if ( object instanceof PlayerImpl ) {
      	// We display selection and player info
           String previouslySelectedPlayerKey = "";

           if (selectedPlayer!=null)
               previouslySelectedPlayerKey = selectedPlayer.getPrimaryKey();

           selectedPlayer = (PlayerImpl) object; // new player selected      

       // We get the InfoPanel
          Component c_info = dataManager.getPlayerPanel().getTab("-info-");
           
          if( c_info==null || !(c_info instanceof InfoPanel) ) {
              Debug.signal( Debug.ERROR, this, "InfoPanel not found !");
              return;
          }

          InfoPanel infoPanel = (InfoPanel) c_info;

       // We erase the previous selection circle
          if (circle!=null) {
              gDirector.removeDrawable(circle);
              circle=null;
          }
                      
       // Deselect ?
          if ( previouslySelectedPlayerKey.equals(selectedPlayer.getPrimaryKey()) ) {
               gDirector.addDrawable(selectedPlayer.getTextDrawable());
               gDirector.addDrawable(selectedPlayer.getWotCharacter().getAura());
               selectedPlayer=null;
               infoPanel.reset();
               return;
          }

       // Select
          circle = new CircleDrawable( selectedPlayer.getDrawable(),
                                       15,
                                       selectedPlayer.getWotCharacter().getColor(),
                                       true,
                                       ImageLibRef.AURA_PRIORITY);
          gDirector.addDrawable(circle);
          gDirector.addDrawable(selectedPlayer.getTextDrawable());
          gDirector.addDrawable(selectedPlayer.getWotCharacter().getAura());
          infoPanel.setPlayerInfo( selectedPlayer );

       // Away Message
          String awayMessage = selectedPlayer.getPlayerAwayMessage();

          if( selectedPlayer.isConnectedToGame() )  return;
          if( !selectedPlayer.canDisplayAwayMessage() )  return;

          if( awayMessage!=null ) {
              JChatRoom chatRoom = chatPanel.getCurrentJChatRoom();
              chatRoom.appendText("<font color='gray'> "+selectedPlayer.getPlayerName()+" (away) says: <i> "
                                                    +selectedPlayer.getPlayerAwayMessage()+" </i></font>");
          }

          return;
    }
    else if( object instanceof Door ) {
        // We open/close the door IF the player is near enough...
           Door door = (Door) object;
           
           if (SHOW_DEBUG)
              System.out.println("A door has been clicked...");

        // player near enough the door ?
           if( door.isPlayerNear( myPlayer.getCurrentRectangle() ) ) {
               WotlasLocation location = new WotlasLocation(myPlayer.getLocation());
               sendMessage( new DoorStateMessage(location, door.getMyRoomLinkID(), !door.isOpened()) );
           }
           else {
             // we go to the door
                Point doorPoint = door.getPointNearDoor( myPlayer.getCurrentRectangle() );

                if( doorPoint!=null )
                    myPlayer.moveTo( doorPoint );
           }

           return;
    }

    synchronized( players ) {
       if (object == null) {
          int newX = e.getX() + (int)screen.getX();
          int newY = e.getY() + (int)screen.getY();
          myPlayer.moveTo( new Point(newX,newY) );
       }
    }
    
    if (SHOW_DEBUG)
      System.out.println("END of DataManager::onLeftClicJMapPanel");
  }

 /*------------------------------------------------------------------------------------*/

 /** Called when user right-clic on JMapPanel
   */
  public void onRightClicJMapPanel(MouseEvent e) {
    if (SHOW_DEBUG)
      System.out.println("DataManager::onRightClicJMapPanel");
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
      myMapData = new TownMapData();
    }
    else if ( myPlayer.getLocation().isWorld() ) {
      myMapData = new WorldMapData();
    }
    myMapData.showDebug(SHOW_DEBUG);
    myMapData.initDisplay(myPlayer, this);
  }

 /*------------------------------------------------------------------------------------*/

  /** To suppress drawables, shadows, data
   */
  public void cleanInteriorMapData() {
    gDirector.removeAllDrawables();
    circle = null;
    selectedPlayer = null;
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

  /** To close the client.
   */
  public void exit() {
    
    if (mFrame!=null) {
      int mFrameWidth =  mFrame.getWidth();
      int mFrameHeight = mFrame.getHeight();
      try {
        ClientConfiguration clientConfiguration = (ClientConfiguration) PropertiesConverter.load(ClientDirector.CLIENT_OPTIONS);
        if (mFrameWidth>100)
          clientConfiguration.setClientWidth(mFrameWidth);
        if (mFrameHeight>100)
          clientConfiguration.setClientHeight(mFrameHeight);
        PropertiesConverter.save(clientConfiguration, "../src/config/client-options.cfg");
      } catch (PersistenceException pe) {
        Debug.signal( Debug.ERROR, this, "Failed to save client configuration : " + pe.getMessage() );
      }
    }
    
    if (gDirector!=null)
      gDirector.removeAllDrawables();
    Debug.exit();
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the master player.
   */
  public PlayerImpl getMyPlayer() {
    return myPlayer;
  }

 /*------------------------------------------------------------------------------------*/

}
