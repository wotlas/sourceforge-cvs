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

package wotlas.client;

import wotlas.client.gui.*;
import wotlas.client.screen.*;
import wotlas.client.screen.plugin.InfoPlugIn;

import wotlas.common.character.*;
import wotlas.common.*;
import wotlas.common.message.account.*;
import wotlas.common.message.description.*;
import wotlas.common.universe.*;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.policy.*;
import wotlas.libs.net.*;
import wotlas.libs.net.utils.NetQueue;
import wotlas.libs.persistence.*;
import wotlas.libs.sound.SoundLibrary;

import wotlas.utils.*;

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

public class DataManager extends Thread implements NetConnectionListener, Tickable {

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

  /*** THE MAIN DATA WE MANAGE ***/

  /** Our World Manager
   */
    private WorldManager worldManager;

  /** Our MapData : data of the current map displayed on screen.
   */
    private MapData myMapData;

  /** Our NetPersonality, represents the connection with the server.
   */
    private NetPersonality personality;

  /** Our player's profile ( serverID, login, etc... ).
   */
    private ProfileConfig currentProfileConfig;

  /** Our ImageLibrary.
   */
    private ImageLibrary imageLib;

  /** Our Graphics Director.
   */
    private GraphicsDirector gDirector;

  /** Our client interface frame.
   */
    private JClientScreen clientScreen;

  /** NetQueue for synchronous messages. Messages that want to be run after the current
   *  tick should call a queueMessage() on this NetQueue.
   *  NetMessageBehaviours should use the invokeLater() method to queue a message.
   */
    private NetQueue syncMessageQueue;

  /** Our player data.
   */
    private PlayerImpl myPlayer;
  
  /** The selected player on screen.
   */
    private PlayerImpl selectedPlayer;

  /** List of all the players displayed on screen.
   */
    private Hashtable players;

 /*------------------------------------------------------------------------------------*/

  /*** DATA ACCESS CONTROLLER ***/

  /** Personality Lock
   */
    private byte personalityLock[] = new byte[1];

  /** Game Lock (unlocked by client.message.description.YourPlayerDataMsgBehaviour)
   */
    private Object startGameLock = new Object();

  /** Tick Thread Lock.
   */
    private Object pauseTickThreadLock = new Object();

  /** Do we have to pause the tick thread ?
   */
    private boolean pauseTickThread;

  /** Are we changing the MapData ?
   */
    private boolean updatingMapData = false;

  /** True if player was diconnected end resumed the game
   */
    private boolean isResuming = false;

  /** Ghost orientation (to limit the update massages sent)
   */
    private double ghostOrientation;

  /** Reference orientation
   */
    private double refOrientation;

 /*------------------------------------------------------------------------------------*/

  /*** SELECTION CIRCLE ***/

  /** Circle selection
   */
    private CircleDrawable circle;

  /** Number of tick since circle creation
   */
    private int circleLife = 0;

  /** Circle Lock
   */
    private byte circleLock[] = new byte[1];


 /*------------------------------------------------------------------------------------*/

  /** Constructor with resource manager.
   */
    public DataManager( ResourceManager rManager ) {

      // 1 - We create our world Manager. It will load the universe data.
         worldManager = new WorldManager( rManager, false );

      // 2 - Misc inits
         syncMessageQueue = new NetQueue(1,3);
         players = new Hashtable();
         personalityLock = new byte[1];
         startGameLock = new Object();

         pauseTickThreadLock = new Object();
         pauseTickThread = false;
         updatingMapData = false;
         isResuming = false;

         circleLife = 0;
         circleLock= new byte[1];
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

  /** To get the graphicsDirector
   *
   * @return the graphicsDirector
   */
    public GraphicsDirector getGraphicsDirector() {
       return gDirector;
    }

 /*------------------------------------------------------------------------------------*/

  /** To get the image Library
   *
   * @return the image library
   */
    public ImageLibrary getImageLibrary() {
       return imageLib;
    }

 /*------------------------------------------------------------------------------------*/

  /*** GETTERS ***/

  /** To get MapData
   */
    public MapData getMapData() {
      return myMapData;
    }
  /** To get JClientScreen.
   */
    public JClientScreen getClientScreen() {
      return clientScreen;
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
    public void connectionCreated( NetPersonality personality ) {

      synchronized( personalityLock ) {
        this.personality = personality;
        personalityLock.notifyAll();
      }

      personality.setContext(this);

      if (currentProfileConfig.getLocalClientID() == -1) {  
          if (personality==null) {
              Debug.signal( Debug.ERROR, this, "Connection closed by AccountServer" );
              return;
          }

          Debug.signal( Debug.NOTICE, null, "New account created !" );
          return;
      }

      // The key is valid, we are connected to the GameServer
       Debug.signal( Debug.NOTICE, null, "DataManager connected to GameServer" );
    }

 /*------------------------------------------------------------------------------------*/

  /** To wait (timeout max) for the connection to be established.
   */
   public void waitForConnection(long timeout) {

      synchronized( personalityLock ) {
         if(personality==null)
            try{
               personalityLock.wait(timeout);
            }catch(Exception e ) {}
      }
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

     if ( (clientScreen!=null) && (clientScreen.isShowing()) ) {

        if( !ClientDirector.getClientManager().getAutomaticLogin() ) {
           gDirector.removeAllDrawables();
           showWarningMessage("Connection to Server lost ! Re-connect to the game...");
        }

        Runnable runnable = new Runnable() {
           public void run() {
              ClientDirector.getClientManager().start(ClientManager.ACCOUNT_LOGIN_SCREEN);  // we restart the ClientManager
           }                                                                                // on the Login entry
        };

        SwingUtilities.invokeLater( runnable );
     }
  }

 /*------------------------------------------------------------------------------------*/

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

  /** To show the client's interface.
   */
  public void showInterface() {

       Debug.signal( Debug.NOTICE, null, "DataManager call to ShowInterface");

       if (imageLib !=null) {
          // All data have already been initialized
          // => there was a disconnection and player has resumed the game
          resumeInterface();
          return;
       }
    
    // 1 - Create Image Library
       String imageDBHome = ClientDirector.getResourceManager().getBase( IMAGE_LIBRARY );
       String fontsHome = ClientDirector.getResourceManager().getBase( "fonts" );

       try {
          imageLib = ImageLibrary.createImageLibrary( imageDBHome, fontsHome );
       }
       catch( Exception ex ) {
          Debug.signal(Debug.FAILURE, this, ex );
          Debug.exit();
       }
    
    // 2 - Set Client Configuration Choices
       ClientConfiguration clientConfiguration = ClientDirector.getClientConfiguration();
    
       SoundLibrary.getSoundLibrary().setNoMusic(clientConfiguration.getNoMusic());

       if( clientConfiguration.getMusicVolume()>0 )
          SoundLibrary.getSoundLibrary().setMusicVolume((short) clientConfiguration.getMusicVolume());

       SoundLibrary.getSoundLibrary().setNoSound(clientConfiguration.getNoSound());

       if(clientConfiguration.getSoundVolume()>0)
         SoundLibrary.getSoundLibrary().setSoundVolume((short) clientConfiguration.getSoundVolume());
    
    // 3 - Create Graphics Director
       WindowPolicy wPolicy = null;
    
       if( clientConfiguration.getCenterScreenPolicy() )
           wPolicy = new CenterWindowPolicy();
       else
           wPolicy = new LimitWindowPolicy();

       if( clientConfiguration.getUseHardwareAcceleration() )
           gDirector = new EnhancedGraphicsDirector( wPolicy, imageLib );
       else
           gDirector = new GraphicsDirector( wPolicy, imageLib );

       Debug.signal(Debug.NOTICE, null, "Graphics Engine is using hardware mode : "+
                                         clientConfiguration.getUseHardwareAcceleration() );

    // 4 - Retrieve player's informations
       myPlayer = null;

       waitForConnection(10000); // 10s max...

       try {
         synchronized(startGameLock) {
            personality.queueMessage(new MyPlayerDataPleaseMessage());
            startGameLock.wait(CONNECTION_TIMEOUT);
         }
       } catch (InterruptedException ie) {
       }

       if(myPlayer==null) {
          showWarningMessage("Failed to retrieve your player data from the Game Server !\nPlease retry later...");
          closeConnection();
          return;
       }

       myPlayer.setIsMaster( true );   // this player is controlled by the user.
       myPlayer.tick();                // we tick the player to validate data recreation
       addPlayer(myPlayer);

       WotlasLocation location = myPlayer.getLocation();  // we get the player's location

       if (SHOW_DEBUG)
          System.out.println("POSITION set to x:"+myPlayer.getX()+" y:"+myPlayer.getY()+" location is "+location);

    // 5 - Creation of  the GUI
       clientScreen = new JClientScreen(gDirector, this );
       clientScreen.init();
       personality.setPingListener( (NetPingListener) clientScreen.getPingPanel() );

       if ( (clientConfiguration.getClientWidth()>0) && (clientConfiguration.getClientHeight()>0) )
          clientScreen.setSize(clientConfiguration.getClientWidth(),clientConfiguration.getClientHeight());

       if(SHOW_DEBUG)
          System.out.println("JClientScreen created");

    // 6 - Init the map display
       changeMapData();

       if(SHOW_DEBUG)
         System.out.println("Changed map data !");

    // 7 - Start the tick thread.
       start();
       Debug.signal( Debug.NOTICE, null, "Started the tick thread..." );

       clientScreen.show();

       if (SHOW_DEBUG)
           System.out.println("Frame displayed on screen...");

    // 8 - We can now ask for the remaining data : chat groups, players, doors...
    // This step should have been done in the current MapData.init() but it was not
    // the cas because our DataManager thread was not started...
       sendMessage(new AllDataLeftPleaseMessage());

    // Welcome message
       sendMessage(new WelcomeMessage());

       if(SHOW_DEBUG)
          System.out.println("End of DataManager's showInterface !");
  }

 /*------------------------------------------------------------------------------------*/

  /** Resumes the game screen in case of server connection shut.
   */
    public void resumeInterface() {
       Debug.signal( Debug.NOTICE, null, "DataManager::ResumeInterface");

     // wait for connection
       waitForConnection(10000); // 10s max...

    // Reset the data
       clientScreen.getChatPanel().reset();
       personality.setPingListener( (NetPingListener) clientScreen.getPingPanel() );

    // We recreate the graphics director...
       WindowPolicy wPolicy = null;
    
       if( ClientDirector.getClientConfiguration().getCenterScreenPolicy() )
           wPolicy = new CenterWindowPolicy();
       else
           wPolicy = new LimitWindowPolicy();

       if( ClientDirector.getClientConfiguration().getUseHardwareAcceleration() )
           gDirector = new EnhancedGraphicsDirector( wPolicy, imageLib );
       else
           gDirector = new GraphicsDirector( wPolicy, imageLib );

       Debug.signal(Debug.NOTICE, null, "Graphics Engine is using hardware mode : "+
                    ClientDirector.getClientConfiguration().getUseHardwareAcceleration() );

       clientScreen.getMapPanel().updateGraphicsDirector(gDirector);
       clientScreen.show();

    // Retrieve player's informations
       myPlayer = null;
    
       try {
         synchronized(startGameLock) {
             personality.queueMessage(new MyPlayerDataPleaseMessage());
             startGameLock.wait(CONNECTION_TIMEOUT);
         }
       } catch (InterruptedException ie) {
       }

       if(myPlayer==null) {
          showWarningMessage("Failed to retrieve your player data from the Game Server !\nPlease retry later...");
          closeConnection();
          return;
       }

       myPlayer.setIsMaster( true );   // this player is controlled by the user.
       myPlayer.tick();
       players.clear();
       addPlayer(myPlayer);

    // Retreive player's location
       WotlasLocation location = myPlayer.getLocation();
       if(SHOW_DEBUG)
          System.out.println("POSITION set to x:"+myPlayer.getX()+" y:"+myPlayer.getY()+" location is "+location);

    // Init map display
       changeMapData();
       clientScreen.getPlayerPanel().reset();
       resumeTickThread();

    // We can now ask for eventual remaining data
    // This step should have been done in the current MapData.init() but it was not
    // the cas because our DataManager thread was not started...
       sendMessage(new AllDataLeftPleaseMessage());

    // Welcome message
       sendMessage(new WelcomeMessage());
   }
  
 /*------------------------------------------------------------------------------------*/

  /** Main loop to tick the graphics director every 50ms.
   */
    public void run() {
      long now;
      int deltaT;
      int delay;

      String os   = System.getProperty( "os.name" );
      String arch = System.getProperty( "os.arch" );
      String vers = System.getProperty( "os.version" );

      Debug.signal( Debug.NOTICE, this, "OS INFO :\n\nOS NAME : <"+os+">\nOS ARCH: <"+arch+">\nOS VERSION: <"+vers+">\n" );

      delay = 50;

      //if ( os.equals("Windows 2000") || os.equals("Windows XP") )
      //  delay = 40;

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

  /** Tick Action. We propagate the tick on the players & GraphicsDirector.
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

       if( circle!=null )
           circle.tick();

    // III - Graphics Director update & redraw
       if( clientScreen.getState()==Frame.ICONIFIED )
          Tools.waitTime(400); // we reduce our tick rate... and don't refresh the screen
       else
          gDirector.tick(); // game screen update

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
         JOptionPane.showMessageDialog( clientScreen, warningMsg, "Warning message!", JOptionPane.WARNING_MESSAGE);
    }
    
  /** To ear a warning beep
   */
    public void playerWarningBeep() {
         SoundLibrary.getSoundLibrary().playSound("bell.wav");
    }

 /*------------------------------------------------------------------------------------*/

  /** Called when user left-clic on JMapPanel
   */
    public void onLeftClicJMapPanel(MouseEvent e) {

      if(SHOW_DEBUG)
         System.out.println("DataManager::onLeftClicJMapPanel");

      if(updatingMapData)
         return; // updating Map Location

      Rectangle screen = gDirector.getScreenRectangle();
      Object object = gDirector.findOwner( e.getX(), e.getY() );

   // We take a look at the selected object the user clicked
   // Is it a player ? a door ? or the current map ?

      if ( object instanceof PlayerImpl ) {
      	// We display selection and player info
           String previouslySelectedPlayerKey = "";

           if (selectedPlayer!=null)
               previouslySelectedPlayerKey = selectedPlayer.getPrimaryKey();

           selectedPlayer = (PlayerImpl) object; // new player selected      

        // We get the InfoPlugIn
           InfoPlugIn infoPanel = (InfoPlugIn) clientScreen.getPlayerPanel().getPlugIn("Info");

           if(infoPanel==null) {
              Debug.signal(Debug.ERROR,this,"InfoPlugIn not found !");
       	      return;
           }

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

                if(infoPanel!=null) infoPanel.reset();
                return;
           }

        // Select
           circle = new CircleDrawable( selectedPlayer.getDrawable(),
                                       20,
                                       selectedPlayer.getWotCharacter().getColor(),
                                       true,
                                       ImageLibRef.AURA_PRIORITY);
           gDirector.addDrawable(circle);
           gDirector.addDrawable(selectedPlayer.getTextDrawable());
           gDirector.addDrawable(selectedPlayer.getWotCharacter().getAura());

           if(infoPanel!=null)
              infoPanel.setPlayerInfo( selectedPlayer );

        // Away Message
           String awayMessage = selectedPlayer.getPlayerAwayMessage();

           if( selectedPlayer.isConnectedToGame() )  return; 
           if( !selectedPlayer.canDisplayAwayMessage() )  return;

           if( awayMessage!=null ) {
               JChatRoom chatRoom = clientScreen.getChatPanel().getCurrentJChatRoom();
               chatRoom.appendText("<font color='gray'> "+selectedPlayer.getFullPlayerName()+" (away) says: <i> "
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
                    myPlayer.moveTo( doorPoint, worldManager );
           }

           return;
    }

  // Clicked object is the game screen...
  // We move the player to that location.
     synchronized( players ) {
       if (object == null) {
          int newX = e.getX() + (int)screen.getX();
          int newY = e.getY() + (int)screen.getY();
          myPlayer.moveTo( new Point(newX,newY), worldManager );
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

  /** Called when the mouse cursor is moved.
   * @param e mouse event
   * @param dx delta x since mouse pressed
   * @param dy delta y since mouse pressed
   * @param finalMov movement type as describe in JMapPanel, INIT_MOUSE_MOVEMENT, etc...
   */
   public void onLeftButtonMoved( MouseEvent e, int dx, int dy, byte movementType ) {

     // if the player is moving we return
       if(myPlayer.getMovementComposer().isMoving())
          return;

       double orientation = myPlayer.getMovementComposer().getOrientationAngle();

     // init the rotation ?
       if(movementType==JMapPanel.INIT_MOUSE_MOVEMENT) {
          refOrientation = orientation;
          ghostOrientation = orientation;
          return;
       }

       if( Math.abs((double)dx/100)>3.4 )
           return;

       myPlayer.getMovementComposer().setOrientationAngle(refOrientation-(double)dx/100);
       orientation = myPlayer.getMovementComposer().getOrientationAngle();

     // send an update message ?
       if( Math.abs(orientation-ghostOrientation) > 1.0
           || (movementType==JMapPanel.END_MOUSE_MOVEMENT
               && Math.abs(orientation-ghostOrientation) >= 0.05) ) {
          myPlayer.getMovementComposer().rotateTo( orientation );
          ghostOrientation = orientation;
       }
   }

 /*------------------------------------------------------------------------------------*/

 /** To add a wave arc effect on the player.
  */
   public void addWaveDrawable( PlayerImpl player ) {
      if(gDirector!=null)
         gDirector.addDrawable(player.getWaveArcDrawable());
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

       synchronized(startGameLock) {
           startGameLock.notify();      
       }
    }

 /*------------------------------------------------------------------------------------*/

  /** To change the current MapData ( TownMap, WorldMap, InteriorMap ).
   */
    public void changeMapData() {
      updatingMapData=true;

      try{
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
      catch(Exception e ) {
         Debug.signal(Debug.ERROR, this, e);
      }

      updatingMapData=false;
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

  /** To exit wotlas.
   */
    public void exit() {
    
        if(clientScreen!=null) {
           int clientScreenWidth =  clientScreen.getWidth();
           int clientScreenHeight = clientScreen.getHeight();

            if(clientScreenWidth>100)
               ClientDirector.getClientConfiguration().setClientWidth(clientScreenWidth);

            if(clientScreenHeight>100)
               ClientDirector.getClientConfiguration().setClientHeight(clientScreenHeight);
        }

       ClientDirector.getClientConfiguration().save();
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
