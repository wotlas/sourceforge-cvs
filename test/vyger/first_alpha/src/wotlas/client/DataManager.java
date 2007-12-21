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

package wotlas.client;

import wotlas.client.gui.*;
import wotlas.client.screen.*;
//import wotlas.client.screen.extraplugin.*;
import wotlas.client.screen.JPanelPlugIn;
import wotlas.client.screen.plugin.InfoPlugIn;

import wotlas.common.character.*;
import wotlas.common.*;
import wotlas.common.message.account.*;
import wotlas.common.message.description.*;
import wotlas.common.PlayerState;
import wotlas.common.universe.*;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.policy.*;
import wotlas.libs.graphics2D.menu.*;
import wotlas.libs.net.*;
import wotlas.libs.net.utils.NetQueue;
import wotlas.libs.persistence.*;
import wotlas.libs.sound.SoundLibrary;
import wotlas.libs.aswing.*;

import wotlas.utils.*;
import wotlas.common.action.*;
import wotlas.common.message.action.*;
import wotlas.common.screenobject.*;

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
public class DataManager extends Thread implements NetConnectionListener, Tickable,
                                                   Menu2DListener {

    public final static byte COMMAND_NOTHING  = 0;
    public final static byte COMMAND_CAST     = 1;
    public final static byte COMMAND_ABILITY  = 2;
    public final static byte COMMAND_BASIC    = 3;

    public byte commandRequest = COMMAND_NOTHING;
    public UserAction commandAction = null;

 /*------------------------------------------------------------------------------------*/

    /** Image Library
    */
    public final static String IMAGE_LIBRARY = "graphics/imagelib";

    /** size of a mask's cell (in pixels)
    */
    public final static int TILE_SIZE = 5;

    /** TIMEOUT to the Account Server
    */
    private static final int CONNECTION_TIMEOUT = 30000;

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

  /** Our NetConnection, represents the connection with the server.
   */
    private NetConnection connection;

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

  /** List of all the ScreenObjects displayed on screen.
   */
    private Hashtable screenObjects;
    
  /** Our menu manager.
   */
    private MenuManager menuManager;

 /*------------------------------------------------------------------------------------*/

  /*** DATA ACCESS CONTROLLER ***/

  /** Connection Lock
   */
    private byte connectionLock[] = new byte[1];

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
         screenObjects = new Hashtable();
         connectionLock = new byte[1];
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
 
  /** To get the hashtable screenObjects
   */
    public Hashtable getScreenObjects() {
      return screenObjects;
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
   * @param connection the NetConnection object associated to this connection.
   */
    public void connectionCreated( NetConnection connection ) {

      synchronized( connectionLock ) {
        this.connection = connection;
        connectionLock.notifyAll();
      }

      connection.setContext(this);

      if (currentProfileConfig.getLocalClientID() == -1) {  
          if (connection==null) {
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

      long t0 = System.currentTimeMillis();

      synchronized( connectionLock ) {
        do{
         long now = System.currentTimeMillis();

           if( connection==null && timeout>(now-t0) )
              try{
                 connectionLock.wait(timeout-(now-t0));
              }catch(Exception e ) {}
           else
              return;
        }
        while( true );
      }
   }

 /*------------------------------------------------------------------------------------*/

  /** This method is called when the network connection of the client is closed
   *
   * @param connection the NetConnection object associated to this connection.
   */
  public void connectionClosed( NetConnection connection ) {
    synchronized( connectionLock ) {
      this.connection = null;
    }

    Debug.signal( Debug.NOTICE, null, "DataManager not connected anymore to GameServer" );

    pauseTickThread();

     if ( clientScreen!=null && clientScreen.isShowing() ) {

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
     else if( clientScreen!=null ) {
        Runnable runnable = new Runnable() {
           public void run() {
              ClientDirector.getClientManager().start(ClientManager.MAIN_SCREEN);
           }
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
    synchronized( connectionLock ) {
      if ( connection!=null ) {
        connection.queueMessage( message );
      }
    }
  }

 /*------------------------------------------------------------------------------------*/

    /** To close the network connection if any.
    */
    public void closeConnection() {
        synchronized( connectionLock ) {
            if ( connection!=null )
                connection.close();
        }
    }

 /*------------------------------------------------------------------------------------*/

  /** To show the client's interface.
   */
  public void showInterface() {

    // 0 - State analysis, progress monitor init...
       Debug.signal( Debug.NOTICE, null, "DataManager call to ShowInterface");

       if (imageLib !=null) {
          // All data have already been initialized
          // => there was a disconnection and player has resumed the game
          resumeInterface();
          return;
       }

       AProgressMonitor pMonitor = new AProgressMonitor( ClientDirector.getClientManager(), "Wotlas" );
       pMonitor.setProgress("Loading Shared Images...",0);

    // 1 - Create Image Library
       try {
          imageLib = new ImageLibrary( ClientDirector.getResourceManager() );
          imageLib.setLoadAllJITDirectoryImages(true); // images from JIT directories are loaded together.
       }
       catch( Exception ex ) {
          Debug.signal(Debug.FAILURE, this, ex );
          Debug.exit();
       }

       pMonitor.setProgress("Reading Preferences...",10);

    // 2 - Set Client Configuration Choices
       ClientConfiguration clientConfiguration = ClientDirector.getClientConfiguration();
    
       SoundLibrary.getMusicPlayer().setNoMusicState(clientConfiguration.getNoMusic());

       if( clientConfiguration.getMusicVolume()>0 )
          SoundLibrary.getMusicPlayer().setMusicVolume((short) clientConfiguration.getMusicVolume());

       SoundLibrary.getSoundPlayer().setNoSoundState(clientConfiguration.getNoSound());

       if(clientConfiguration.getSoundVolume()>0)
         SoundLibrary.getSoundPlayer().setSoundVolume((short) clientConfiguration.getSoundVolume());

       pMonitor.setProgress("Creating 2D Engine...",15);
    
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

       pMonitor.setProgress("Creating GUI...",20);

    // 4 - Creation of the GUI components
       clientScreen = new JClientScreen(gDirector, this );

       if(SHOW_DEBUG)
          System.out.println("JClientScreen created");

       pMonitor.setProgress("Loading Player Data from Server...",30);

    // 5 - We retrieve our player's own data
       myPlayer = null;

       waitForConnection(30000); // 30s max...

       try {
         synchronized(startGameLock) {
            connection.queueMessage(new MyPlayerDataPleaseMessage());
            startGameLock.wait(CONNECTION_TIMEOUT);
         }
       } catch (InterruptedException ie) {
       }

       if(myPlayer==null) {
          pMonitor.close();
          showWarningMessage("Failed to retrieve your player data from the Game Server !\nPlease retry later...");
          imageLib = null;
          closeConnection();
          return;
       }

       myPlayer.setIsMaster( true );   // this player is controlled by the user.
       myPlayer.tick();                // we tick the player to validate data recreation
       addPlayer(myPlayer);

       if (SHOW_DEBUG)
          System.out.println("POSITION set to x:"+myPlayer.getX()+" y:"+myPlayer.getY()+" location is "+myPlayer.getLocation());

       pMonitor.setProgress("Setting Preferences...",80);

    // 6 - Final GUI inits
       connection.setPingListener( (NetPingListener) clientScreen.getPingPanel() );

       clientScreen.init();

       if ( (clientConfiguration.getClientWidth()>0) && (clientConfiguration.getClientHeight()>0) )
          clientScreen.setSize(clientConfiguration.getClientWidth(),clientConfiguration.getClientHeight());

       menuManager = new MenuManager( myPlayer, gDirector );
       menuManager.addMenu2DListener(this);       

       pMonitor.setProgress("Loading Map Data...",85);

    // 7 - Init the map display...
       changeMapData();

       if(SHOW_DEBUG)
         System.out.println("Changed map data !");

       pMonitor.setProgress("Starting Game...",95);

    // 8 - Start the tick thread.
       start();
       Debug.signal( Debug.NOTICE, null, "Started the tick thread..." );

       clientScreen.show();
       Debug.signal( Debug.NOTICE, null, "Show clientScreen..." );
       pMonitor.setProgress("Done...",100);
       pMonitor.close();

       if (SHOW_DEBUG)
           System.out.println("Frame displayed on screen...");

    // 9 - Welcome message
       sendMessage(new WelcomeMessage());

    // 10 - ask for server environment
    // diego : at least i'm trying to ask for it.....
       sendMessage(new TheServerEnvironmentPleaseMessage());
       
       if(SHOW_DEBUG)
          System.out.println("End of DataManager's showInterface !");
                 
    // 11 - Add extra plugin
          //clientScreen.getPlayerPanel().addPlugIn((JPanelPlugIn) new ChangeAspectPlugIn(), -1);

// Test Petrus
      String empty[] = { "head", "body", "left hand", "right hand" };
      SimpleMenu2D emptyMenu = new SimpleMenu2D("emptyMenu",empty);
      emptyMenu.setItemEnabled( "head", true );
      emptyMenu.setItemEnabled( "body", true );
      emptyMenu.setItemEnabled( "left hand", true );
      emptyMenu.setItemEnabled( "right hand", true );
      ((SimpleMenu2D) menuManager.getRootMenu()).addItemLink(MenuManager.OBJECT_ITEM_NAME, emptyMenu );
      
      /*SimpleMenu2D objectMenu = (SimpleMenu2D) menuManager.findByName(MenuManager.OBJECT_ITEM_NAME);
      objectMenu.addItem("head");
      */
// end Test Petrus
     
  }

 /*------------------------------------------------------------------------------------*/

  /** Resumes the game screen in case of server connection shut.
   */
    public void resumeInterface() {
       Debug.signal( Debug.NOTICE, null, "DataManager::ResumeInterface");

       AProgressMonitor pMonitor = new AProgressMonitor( ClientDirector.getClientManager(), "Wotlas" );
       pMonitor.setProgress("Creating 2D Engine...",15);

    // 1 - We recreate the graphics director...
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

       if(menuManager!=null)
          menuManager.clear();

    // 2 - Retrieve player's informations
       pMonitor.setProgress("Loading Player Data from Server...",30);
       myPlayer = null;

       waitForConnection(30000); // 30s max...

       try {
         synchronized(startGameLock) {
             connection.queueMessage(new MyPlayerDataPleaseMessage());
             startGameLock.wait(CONNECTION_TIMEOUT);
         }
       } catch (InterruptedException ie) {
       }

       if(myPlayer==null) {
          pMonitor.close();
          showWarningMessage("Failed to retrieve your player data from the Game Server !\nPlease retry later...");
          closeConnection();
          return;
       }

       myPlayer.setIsMaster( true );   // this player is controlled by the user.
       myPlayer.tick();
       addPlayer(myPlayer);

       if(SHOW_DEBUG)
          System.out.println("POSITION set to x:"+myPlayer.getX()+" y:"+myPlayer.getY()+" location is "+myPlayer.getLocation());

    // 3 - Reset previous the data
       pMonitor.setProgress("Setting Preferences...",80);

       clientScreen.getChatPanel().reset();
       clientScreen.getPlayerPanel().reset();
       players.clear();
       screenObjects.clear();
       connection.setPingListener( (NetPingListener) clientScreen.getPingPanel() );

       menuManager = new MenuManager( myPlayer, gDirector );
       menuManager.addMenu2DListener(this);

    // 4 - Init map display, resume tick thread & show screen...
       pMonitor.setProgress("Loading Map Data...",85);

       changeMapData();
       resumeTickThread();

       pMonitor.setProgress("Starting Game...",95);
       clientScreen.show();

       pMonitor.setProgress("Done...",100);
       pMonitor.close();

    // 5 - Welcome message
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
                    } catch(Exception e) {}
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
        
        ScreenObject item = null;
        synchronized(screenObjects) {
            Iterator it = screenObjects.values().iterator();
            while( it.hasNext() )
                item = (ScreenObject) it.next();
                if( item instanceof PlayerOnTheScreen 
                || item instanceof ItemOnTheScreen
                || item instanceof SpellOnTheScreen
                || item instanceof NpcOnTheScreen )
                    item.tick();
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

  /** To tell if the DataManager's tick thread is running.
   * @return true if it's running, false otherwise
   */
    public boolean isRunning() {
          synchronized( pauseTickThreadLock ) {
                if( !pauseTickThread && myPlayer!=null )
                    return true;
                return false;
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
        SoundLibrary.getSoundPlayer().playSound("bell.wav");
    }

 /*------------------------------------------------------------------------------------*/

    /** Called when user left-clic on JMapPanel
    */
    public void onLeftClicJMapPanel(MouseEvent e) {

      if(SHOW_DEBUG)
         System.out.println("DataManager::onLeftClicJMapPanel");

        if(updatingMapData)
            return; // updating Map Location
        // Menu clicked ?
        if( menuManager.isVisible() )
            if( !menuManager.mouseClicked( e ) )
                menuManager.hide();
            else
                return;
        // Object/Player selected ?
        if( mouseSelect( e.getX(), e.getY(), true ) )
            return;
        // Clicked object is the game screen...
        // We move the player to that location.
        Rectangle screen = gDirector.getScreenRectangle();
        synchronized( players ) { //!?!?!? why? : wahy syncr playERS to move playER ?
            myPlayer.moveTo( new Point( e.getX() + (int)screen.getX()
            ,e.getY() + (int)screen.getY() ), worldManager );
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
        
        if( menuManager.isVisible() )
            menuManager.hide();
        else {
            // Menu selection & display
            mouseSelect( e.getX(), e.getY(), false );
            if(selectedPlayer!=null)
                menuManager.initContent( selectedPlayer );      
                //        else if(selectedObject!=null)
                //             ADD menuManager initContent here
            else
                menuManager.initNoContent();
            menuManager.show( new Point( e.getX(), e.getY() ) );
        }
    }

 /*------------------------------------------------------------------------------------*/

  /** Called when the mouse cursor is dragged with the left button.
   * @param e mouse event
   * @param dx delta x since mouse pressed
   * @param dy delta y since mouse pressed
   * @param finalMov movement type as describe in JMapPanel, INIT_MOUSE_MOVEMENT, etc...
   */
   public void onLeftButtonDragged( MouseEvent e, int dx, int dy, byte movementType ) {

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

  /** Called when the mouse cursor is moved.
   * @param x mouse's x
   * @param y mouse's y
   */
    public void onLeftButtonMoved( int x, int y ) {
        if( !menuManager.isVisible() )
            return;

        menuManager.mouseMoved( x, y );
    }

 /*------------------------------------------------------------------------------------*/

  /** Called when the mouse cursor is dragged with the left button.
   * @param dx delta x since mouse pressed
   * @param dy delta y since mouse pressed
   * @param startsNow tells if the drag movement is just about to start
   */
    public void onRightButtonDragged( int dx, int dy,  boolean startsNow ) {
        if( !menuManager.isVisible() )
            return;

        menuManager.mouseDragged( dx, dy, startsNow );
    }

 /*------------------------------------------------------------------------------------*/

  /** To select an object/player on screen via a mouse click
   * @param screen game screen dimension
   * @param x x position of the mouse
   * @param y y position of the mouse
   * @param isLeftClick true if the left button was clicked, false if it's the right.
   * @return true if we processed the mouse event, false if it was not for us.
   */
    public boolean mouseSelect( int x, int y, boolean isLeftClick ) {

        // We search for the owner of the object
        Object object = gDirector.findOwner( x, y );

        // We take a look at the selected object the user clicked
        // Is it a player ? a door ? an objecOnTheScreen
        if( myPlayer.getLocation().isTileMap() ) {
            Rectangle screen = gDirector.getScreenRectangle();
            int tmpX = new Integer( (x+screen.x)/32 ).intValue();
            int tmpY = new Integer( (y+screen.y)/32 ).intValue();
            
            if(object instanceof ScreenObject) {
                ScreenObject item = ((ScreenObject) object);
                int a = new Integer( item.getX()/32 ).intValue();
                int b = new Integer( item.getY()/32 ).intValue();
                System.out.println(" sObj video pos="+tmpX+","+tmpY+" real pos "+a+","+b );
            }
            
            // ITS NOT GOOD FOR DOORS, HOWEVER I CAN USE IT FOR THE MOMENT
            // THEN DOORS COMES, WE CAN GET ANOTHER.
            try {
                if( myPlayer.getMyTileMap().getManager().getMapMask()[tmpX][tmpY] == TileMap.TILE_NOT_FREE ) {
                    getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    commandRequest = COMMAND_NOTHING;
                    return true;
                }
            } catch (Exception e) {
                /*
                System.out.println("Error got : look at this. The error should(?)"
                +" comes out 'because empty space (out of map) was clicked(?).");
                e.printStackTrace();
                 */
                getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                commandRequest = COMMAND_NOTHING;
                return true;
            }
            x = new Integer( tmpX*32 ).intValue();
            y = new Integer( tmpY*32 ).intValue();
        }

        if( commandRequest != COMMAND_NOTHING ) {
            // init vars
            byte indexForMaskTarget = 0;
            byte targetRange = 0;
            WotlasLocation loc = new WotlasLocation(myPlayer.getLocation());
            String targetKey = "";
            
            // checking what's the target
            if( object==null ) {
                if (SHOW_DEBUG)
                    ; //System.out.println("The ground has been clicked...");
                indexForMaskTarget = UserAction.TARGET_TYPE_GROUND;
                targetKey = "";
            }
            else if ( object instanceof ScreenObject ) {
                if (SHOW_DEBUG)
                    ; //System.out.println("A screenobject has been clicked...");
                indexForMaskTarget = ((ScreenObject) object).getTargetType();
                targetKey = ((ScreenObject) object).getPrimaryKey();
            }
            else  {
                commandRequest = COMMAND_NOTHING;
                if (SHOW_DEBUG) {
                    ; //System.out.println("Action aborting: wrong target class.....");
                }
            } 

            
            /*
            // player near enough the door ?
            if( door.isPlayerNear( myPlayer.getCurrentRectangle() ) ) {
            }
            */
//            targetRange = UserAction.TARGET_RANGE_SAME_MAP;
            int range = new Double( java.lang.Math.sqrt( java.lang.Math.pow( x-myPlayer.getX(), 2)
            +java.lang.Math.pow( y-myPlayer.getY(), 2) )/32 ).intValue();
            if(range <= 1)
                targetRange = UserAction.TARGET_RANGE_TOUCH;
            else if(range <= 3)
                targetRange = UserAction.TARGET_RANGE_SHORT;
            else if(range <= 5)
                targetRange = UserAction.TARGET_RANGE_MEDIUM;
            else if(range <= 8)
                targetRange = UserAction.TARGET_RANGE_LONG;
            else
                targetRange = UserAction.TARGET_RANGE_SAME_MAP;
            /*
                case UserAction.TARGET_RANGE_NONE:
                case UserAction.TARGET_RANGE_SAME_MAP:
                case UserAction.TARGET_RANGE_ONE_MAP:
                case UserAction.TARGET_RANGE_MAP_ON_SAME_WORLD:
                case UserAction.TARGET_RANGE_ANY:
            */
            
            // checking if the target and the range are right
            if( !commandAction.isValidTarget(indexForMaskTarget, targetRange) ){
                if (SHOW_DEBUG) {
                    System.out.println("Action aborting: invalid target/range.....");
                }
                loc = null;
                commandRequest = COMMAND_NOTHING;
                getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return true;
            }
            
            switch(indexForMaskTarget) {
                
                case UserAction.TARGET_TYPE_SELF:
                case UserAction.TARGET_TYPE_ITEM:
                case UserAction.TARGET_TYPE_NPC:
                case UserAction.TARGET_TYPE_SPELL:
                case UserAction.TARGET_TYPE_PLAYER:
                    switch(commandRequest) {
                        case COMMAND_CAST:
                            sendMessage( 
                            new CastActionWithTargetMessage( commandAction.getId()
                            ,((ScreenObject) object).getPrimaryKey(),targetRange ) );
                            break;
                        case COMMAND_BASIC:
                            sendMessage( 
                            new BasicActionWithTargetMessage( commandAction.getId()
                            ,((ScreenObject) object).getPrimaryKey(),targetRange ) );
                            break;
                    }
                    break;
                    
                case UserAction.TARGET_TYPE_INVENTORY:
                    break;
                    
                case UserAction.TARGET_TYPE_GROUND:
                    switch(commandRequest) {
                        case COMMAND_CAST:
                            sendMessage( 
                            new CastActionWithPositionMessage( commandAction.getId()
                            , x, y,targetRange ) );
                            break;
                        case COMMAND_BASIC:
                            sendMessage( 
                            new BasicActionWithPositionMessage( commandAction.getId()
                            , x, y,targetRange ) );
                            break;
                    }
                    break;
                    
                case UserAction.TARGET_RANGE_ANY:
                    switch(commandRequest) {
                        case COMMAND_CAST:
                            sendMessage( new CastActionWithLocationMessage( commandAction.getId(), loc ) );
                            break;
                        case COMMAND_BASIC:
                            break;
                    }
                    break;

            }
            getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            commandRequest = COMMAND_NOTHING;
            loc = null;
            return true;
            
            // FIXME ??? reset vars and screen cursor
            /*
            loc = null;
            commandRequest = COMMAND_NOTHING;
            getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            if (SHOW_DEBUG) {
                System.out.println("Action aborted.....");
            }
             */
        }
        else if( commandRequest == COMMAND_NOTHING 
        && myPlayer.getLocation().isTileMap() 
        && object instanceof PlayerImpl ) {
            // We get the InfoPlugIn
            InfoPlugIn infoPanel = (InfoPlugIn) clientScreen.getPlayerPanel().getPlugIn("Info");

            if(infoPanel==null) {
                Debug.signal(Debug.ERROR,this,"InfoPlugIn not found !");
       	        return true;
            }

            // We erase the previous selection circle
            if (circle!=null) {
                gDirector.removeDrawable(circle);
                circle=null;
            }

            // Deselect ?
            if ( isLeftClick ) {
                gDirector.addDrawable(myPlayer.getTextDrawable());
//              gDirector.addDrawable(selectedPlayer.getBasicChar().getAura());
                selectedPlayer=null;
                if(infoPanel!=null) infoPanel.reset();
                return true;
            }
            // Select
            circle = new CircleDrawable( myPlayer.getDrawable(), 20,
                                       myPlayer.getBasicChar().getColor(), true,
                                       ImageLibRef.AURA_PRIORITY );
            gDirector.addDrawable(circle);
            gDirector.addDrawable(myPlayer.getTextDrawable());
            if(!selectedPlayer.getLocation().isTileMap())
                gDirector.addDrawable(selectedPlayer.getBasicChar().getAura());
            if(infoPanel!=null)
                infoPanel.setPlayerInfo( myPlayer );
             return true;
        }
        else if( object instanceof PlayerImpl ) {
          // We display selection and player info
             String previouslySelectedPlayerKey = "";

             if (selectedPlayer!=null)
                 previouslySelectedPlayerKey = selectedPlayer.getPrimaryKey();

             selectedPlayer = (PlayerImpl) object; // new player selected

          // We get the InfoPlugIn
             InfoPlugIn infoPanel = (InfoPlugIn) clientScreen.getPlayerPanel().getPlugIn("Info");

             if(infoPanel==null) {
                Debug.signal(Debug.ERROR,this,"InfoPlugIn not found !");
       	        return true;
             }

          // We erase the previous selection circle
             if (circle!=null) {
                 gDirector.removeDrawable(circle);
                 circle=null;
             }

          // Deselect ?
             if ( previouslySelectedPlayerKey.equals(selectedPlayer.getPrimaryKey()) && isLeftClick ) {
                gDirector.addDrawable(selectedPlayer.getTextDrawable());
                gDirector.addDrawable(selectedPlayer.getBasicChar().getAura());
                selectedPlayer=null;

                if(infoPanel!=null) infoPanel.reset();
                return true;
             }

          // Select
             circle = new CircleDrawable( selectedPlayer.getDrawable(), 20,
                                       selectedPlayer.getBasicChar().getColor(), true,
                                       ImageLibRef.AURA_PRIORITY );
             gDirector.addDrawable(circle);
             gDirector.addDrawable(selectedPlayer.getTextDrawable());
             if(!selectedPlayer.getLocation().isTileMap())
                gDirector.addDrawable(selectedPlayer.getBasicChar().getAura());

             if(infoPanel!=null)
                infoPanel.setPlayerInfo( selectedPlayer );

          // Connection state
             if( selectedPlayer.getPlayerState().value == PlayerState.CONNECTED )
                return true;

             if( !isLeftClick )
                return true; // no away message displayed if right click

          // Away Message
             String awayMessage = selectedPlayer.getPlayerAwayMessage();

             if( !selectedPlayer.canDisplayAwayMessage() )  return true;

             if( awayMessage!=null ) {
                 JChatRoom chatRoom = clientScreen.getChatPanel().getCurrentJChatRoom();
                 if ( selectedPlayer.getPlayerState().value==PlayerState.DISCONNECTED ) {
                      chatRoom.appendText("<font color='gray'> "+selectedPlayer.getFullPlayerName()+" (disconnected) says: <i> "
                                                     +selectedPlayer.getPlayerAwayMessage()+" </i></font>");
                 } else
                     chatRoom.appendText("<font color='gray'> "+selectedPlayer.getFullPlayerName()+" (away) says: <i> "
                                                     +selectedPlayer.getPlayerAwayMessage()+" </i></font>");
             }

             return true;
        }
        else if( object instanceof Door ) {
          // We open/close the door IF the player is near enough...
             Door door = (Door) object;

             if (SHOW_DEBUG)
                System.out.println("A door has been clicked...");

          // player near enough the door ?
             if( door.isPlayerNear( myPlayer.getCurrentRectangle() ) ) {

                 if( isLeftClick ) {
                     WotlasLocation location = new WotlasLocation(myPlayer.getLocation());
                    
                    // ADD HERE lock test, does the player has the key if the door is locked ?
                    
                     sendMessage( new DoorStateMessage(location, door.getMyRoomLinkID(), !door.isOpened()) );
                 }
                 else {
                    // Door Selection ?

                 }
             }
             else {
              // we go to the door
                 Point doorPoint = door.getPointNearDoor( myPlayer.getCurrentRectangle() );

                 if( doorPoint!=null )
                     myPlayer.moveTo( doorPoint, worldManager );
             }

             return true;
        }
//      else if( object instanceof BaseObject ) {
//
//             ADD HERE SELECTION CODE FOR OBJECTS (use the player selection as example)
//      }
        else if( object!=null ) {
           // Unknown Object !
              Debug.signal(Debug.WARNING,this,"Unknown Object Clicked : "+object);
              return true;
        }

       return false; // event not for us
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

    /** To add a new screenObject to the screen<br>
   * (called by client.message.description.MsgBehaviour)
   *
   * @screenObject  to add to the player screen
   */
    public synchronized void addScreenObject(ScreenObject item) {
       screenObjects.put( item.getPrimaryKey(), item );
    }

    /** To remove a screenObject from player screen
    *
    * @screeenObject the player no more see, cause it's no more there
    */
    public synchronized boolean removeScreenObject(ScreenObject item) {
        screenObjects.remove( item.getPrimaryKey() );
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

      if( menuManager.isVisible() )
          menuManager.hide();

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
        else if ( myPlayer.getLocation().isTileMap() ) {
            myMapData = new TileMapData();
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
       SoundLibrary.clear();
       Debug.exit();
    }

 /*------------------------------------------------------------------------------------*/

    /** To get the master player.
    */
    public PlayerImpl getMyPlayer() {
        return myPlayer;
    }

 /*------------------------------------------------------------------------------------*/

    /** Method called when an item has been clicked on an item who is not a menu link.
    *  @param e menu event generated.
    */
    public void menuItemClicked( Menu2DEvent e ) {
        if(SHOW_DEBUG)
            System.out.println("Menu Item Clicked : "+e.toString()); 
// Test petrus
        if (e.getItemName().equals("test inventory plugin")) {
            System.out.println("ok");
        } else {
            System.out.println("not a test");
        }
    }

 /*------------------------------------------------------------------------------------*/

}