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

import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import wotlas.client.screen.JChatRoom;
import wotlas.client.screen.JClientScreen;
import wotlas.client.screen.JMapPanel;
import wotlas.client.screen.plugin.InfoPlugIn;
import wotlas.common.ImageLibRef;
import wotlas.common.Player;
import wotlas.common.PlayerState;
import wotlas.common.ResourceManager;
import wotlas.common.Tickable;
import wotlas.common.WorldManager;
import wotlas.common.message.description.DoorStateMessage;
import wotlas.common.message.description.MyPlayerDataPleaseMessage;
import wotlas.common.message.description.WelcomeMessage;
import wotlas.common.universe.Door;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.aswing.AProgressMonitor;
import wotlas.libs.graphics2D.Drawable;
import wotlas.libs.graphics2D.EnhancedGraphicsDirector;
import wotlas.libs.graphics2D.GraphicsDirector;
import wotlas.libs.graphics2D.ImageLibrary;
import wotlas.libs.graphics2D.WindowPolicy;
import wotlas.libs.graphics2D.drawable.CircleDrawable;
import wotlas.libs.graphics2D.drawable.PathDrawable;
import wotlas.libs.graphics2D.menu.Menu2DEvent;
import wotlas.libs.graphics2D.menu.Menu2DListener;
import wotlas.libs.graphics2D.menu.SimpleMenu2D;
import wotlas.libs.graphics2D.policy.CenterWindowPolicy;
import wotlas.libs.graphics2D.policy.LimitWindowPolicy;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.net.NetMessage;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.libs.net.utils.NetQueue;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;
import wotlas.utils.Tools;

/** A DataManager manages Game Data and client's connection.
 * It possesses a WorldManager
 *
 * @author Petrus, Aldiss
 * @see wotlas.common.NetConnectionListener
 */
public class DataManager extends Thread implements NetConnectionListener, Tickable, Menu2DListener {

    /*------------------------------------------------------------------------------------*/
    /** Image Library
     */
    public static final String IMAGE_LIBRARY = "graphics/imagelib";

    /** size of a mask's cell (in pixels)
     */
    public static final int TILE_SIZE = 5;

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
    private final WorldManager worldManager;

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
    private final NetQueue syncMessageQueue;

    /** Our player data.
     */
    private PlayerImpl myPlayer;

    /** The selected player on screen.
     */
    private PlayerImpl selectedPlayer;

    /** List of all the players displayed on screen.
     */
    private final Hashtable<String, PlayerImpl> players;

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
     * @param rManager resources for WorldManager datas.
     */
    public DataManager(final ResourceManager rManager) {

        // 1 - We create our world Manager. It will load the universe data.
        this.worldManager = new WorldManager(rManager, false);

        // 2 - Misc inits
        this.syncMessageQueue = new NetQueue(1, 3);
        this.players = new Hashtable<String, PlayerImpl>();
        this.connectionLock = new byte[1];
        this.startGameLock = new Object();

        this.pauseTickThreadLock = new Object();
        this.pauseTickThread = false;
        this.updatingMapData = false;
        this.isResuming = false;

        this.circleLife = 0;
        this.circleLock = new byte[1];
    }

    /*------------------------------------------------------------------------------------*/
    /** To get the world manager.
     *
     * @return the world manager.
     */
    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    /*------------------------------------------------------------------------------------*/
    /** To get the graphicsDirector
     *
     * @return the graphicsDirector
     */
    public GraphicsDirector getGraphicsDirector() {
        return this.gDirector;
    }

    /*------------------------------------------------------------------------------------*/
    /** To get the image Library
     *
     * @return the image library
     */
    public ImageLibrary getImageLibrary() {
        return this.imageLib;
    }

    /*------------------------------------------------------------------------------------*/
    /*** GETTERS ***/
    /** To get MapData
     */
    public MapData getMapData() {
        return this.myMapData;
    }

    /** To get JClientScreen.
     */
    public JClientScreen getClientScreen() {
        return this.clientScreen;
    }

    /*------------------------------------------------------------------------------------*/
    /** Set to true to show debug information
     */
    public void showDebug(boolean value) {
        DataManager.SHOW_DEBUG = value;
    }

    /*------------------------------------------------------------------------------------*/
    /** To get the hashtable players
     * @return table of (String, PlayerImpl)
     */
    public Hashtable<String, PlayerImpl> getPlayers() {
        return this.players;
    }

    /** To get selected player
     * @return his primaryKey.
     */
    public String getSelectedPlayerKey() {
        if (this.selectedPlayer != null) {
            return this.selectedPlayer.getPrimaryKey();
        }
        return null;
    }

    /** To remove the circle
     */
    public void removeCircle() {
        this.gDirector.removeDrawable(this.circle);
        this.circle = null;
    }

    /*------------------------------------------------------------------------------------*/
    /** To set the current profileConfig<br>
     * (called by client.message.account.AccountCreatedMsgBehaviour)
     * @param currentProfileConfig current config.
     */
    public void setCurrentProfileConfig(ProfileConfig currentProfileConfig) {
        this.currentProfileConfig = currentProfileConfig;
    }

    /** To get the current profileConfig.
     * @return current config.
     */
    public ProfileConfig getCurrentProfileConfig() {
        return this.currentProfileConfig;
    }

    /*------------------------------------------------------------------------------------*/
    /** To test if player was diconnected
     *
     * @return true if player was disconnected
     */
    public boolean isResuming() {
        return this.isResuming;
    }

    /** To set whether player has finished resuming the game
     * @param value true if finished.
     */
    public void setIsResuming(boolean value) {
        this.isResuming = value;
    }

    /*------------------------------------------------------------------------------------*/
    /** This method is called when a new network connection is created
     *
     * @param connection the NetConnection object associated to this connection.
     */
    public void connectionCreated(NetConnection connection) {

        synchronized (this.connectionLock) {
            this.connection = connection;
            this.connectionLock.notifyAll();
        }

        connection.setContext(this);

        if (this.currentProfileConfig.getLocalClientID() == -1) {
            if (connection == null) {
                Debug.signal(Debug.ERROR, this, "Connection closed by AccountServer");
                return;
            }

            Debug.signal(Debug.NOTICE, null, "New account created !");
            return;
        }

        // The key is valid, we are connected to the GameServer
        Debug.signal(Debug.NOTICE, null, "DataManager connected to GameServer");
    }

    /*------------------------------------------------------------------------------------*/
    /** To wait (timeout max) for the connection to be established.
     */
    public void waitForConnection(long timeout) {

        long t0 = System.currentTimeMillis();

        synchronized (this.connectionLock) {
            do {
                long now = System.currentTimeMillis();

                if (this.connection == null && timeout > (now - t0)) {
                    try {
                        this.connectionLock.wait(timeout - (now - t0));
                    } catch (Exception e) {
                    }
                } else {
                    return;
                }
            } while (true);
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** This method is called when the network connection of the client is closed
     *
     * @param connection the NetConnection object associated to this connection.
     */
    public void connectionClosed(NetConnection connection) {
        synchronized (this.connectionLock) {
            this.connection = null;
        }

        Debug.signal(Debug.NOTICE, null, "DataManager not connected anymore to GameServer");

        pauseTickThread();

        if (this.clientScreen != null && this.clientScreen.isShowing()) {

            if (!ClientDirector.getClientManager().getAutomaticLogin()) {
                this.gDirector.removeAllDrawables();
                showWarningMessage("Connection to Server lost ! Re-connect to the game...");
            }

            Runnable runnable = new Runnable() {

                public void run() {
                    ClientDirector.getClientManager().start(ClientManager.ACCOUNT_LOGIN_SCREEN); // we restart the ClientManager
                } // on the Login entry

            };

            SwingUtilities.invokeLater(runnable);
        } else if (this.clientScreen != null) {
            Runnable runnable = new Runnable() {

                public void run() {
                    ClientDirector.getClientManager().start(ClientManager.MAIN_SCREEN);
                }

            };

            SwingUtilities.invokeLater(runnable);
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** Use this method to send a NetMessage to the server.
     *
     * @param message message to send to the player.
     */
    public void sendMessage(NetMessage message) {
        synchronized (this.connectionLock) {
            if (this.connection != null) {
                this.connection.queueMessage(message);
            }
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To close the network connection if any.
     */
    public void closeConnection() {
        synchronized (this.connectionLock) {
            if (this.connection != null) {
                this.connection.close();
            }
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To show the client's interface.
     */
    public void showInterface() {

        // 0 - State analysis, progress monitor init...
        Debug.signal(Debug.NOTICE, null, "DataManager call to ShowInterface");

        if (this.imageLib != null) {
            // All data have already been initialized
            // => there was a disconnection and player has resumed the game
            resumeInterface();
            return;
        }

        AProgressMonitor pMonitor = new AProgressMonitor(ClientDirector.getClientManager(), "Wotlas");
        pMonitor.setProgress("Loading Shared Images...", 0);

        // 1 - Create Image Library
        try {
            this.imageLib = new ImageLibrary(ClientDirector.getResourceManager());
            this.imageLib.setLoadAllJITDirectoryImages(true); // images from JIT directories are loaded together.
        } catch (Exception ex) {
            Debug.signal(Debug.FAILURE, this, ex);
            Debug.exit();
        }

        pMonitor.setProgress("Reading Preferences...", 10);

        // 2 - Set Client Configuration Choices
        ClientConfiguration clientConfiguration = ClientDirector.getClientConfiguration();

        SoundLibrary.getMusicPlayer().setNoMusicState(clientConfiguration.getNoMusic());

        if (clientConfiguration.getMusicVolume() > 0) {
            SoundLibrary.getMusicPlayer().setMusicVolume(clientConfiguration.getMusicVolume());
        }

        SoundLibrary.getSoundPlayer().setNoSoundState(clientConfiguration.getNoSound());

        if (clientConfiguration.getSoundVolume() > 0) {
            SoundLibrary.getSoundPlayer().setSoundVolume(clientConfiguration.getSoundVolume());
        }

        pMonitor.setProgress("Creating 2D Engine...", 15);

        // 3 - Create Graphics Director
        WindowPolicy wPolicy = null;

        if (clientConfiguration.getCenterScreenPolicy()) {
            wPolicy = new CenterWindowPolicy();
        } else {
            wPolicy = new LimitWindowPolicy();
        }

        if (clientConfiguration.getUseHardwareAcceleration()) {
            this.gDirector = new EnhancedGraphicsDirector(wPolicy, this.imageLib);
        } else {
            this.gDirector = new GraphicsDirector(wPolicy, this.imageLib);
        }

        Debug.signal(Debug.NOTICE, null, "Graphics Engine is using hardware mode : " + clientConfiguration.getUseHardwareAcceleration());

        pMonitor.setProgress("Creating GUI...", 20);

        // 4 - Creation of the GUI components
        this.clientScreen = new JClientScreen(this.gDirector, this);

        if (DataManager.SHOW_DEBUG) {
            System.out.println("JClientScreen created");
        }

        pMonitor.setProgress("Loading Player Data from Server...", 30);

        // 5 - We retrieve our player's own data
        this.myPlayer = null;

        waitForConnection(30000); // 30s max...

        try {
            synchronized (this.startGameLock) {
                this.connection.queueMessage(new MyPlayerDataPleaseMessage());
                this.startGameLock.wait(DataManager.CONNECTION_TIMEOUT);
            }
        } catch (InterruptedException ie) {
        }

        if (this.myPlayer == null) {
            pMonitor.close();
            showWarningMessage("Failed to retrieve your player data from the Game Server !\nPlease retry later...");
            this.imageLib = null;
            closeConnection();
            return;
        }

        this.myPlayer.setIsMaster(true); // this player is controlled by the user.
        this.myPlayer.tick(); // we tick the player to validate data recreation
        addPlayer(this.myPlayer);

        if (DataManager.SHOW_DEBUG) {
            System.out.println("POSITION set to x:" + this.myPlayer.getX() + " y:" + this.myPlayer.getY() + " location is " + this.myPlayer.getLocation());
        }

        pMonitor.setProgress("Setting Preferences...", 80);

        // 6 - Final GUI inits
        this.connection.setPingListener(this.clientScreen.getPingPanel());

        this.clientScreen.init();

        if ((clientConfiguration.getClientWidth() > 0) && (clientConfiguration.getClientHeight() > 0)) {
            this.clientScreen.setSize(clientConfiguration.getClientWidth(), clientConfiguration.getClientHeight());
        }

        this.menuManager = new MenuManager(this.myPlayer, this.gDirector);
        this.menuManager.addMenu2DListener(this);

        pMonitor.setProgress("Loading Map Data...", 85);

        // 7 - Init the map display...
        changeMapData();

        if (DataManager.SHOW_DEBUG) {
            System.out.println("Changed map data !");
        }

        pMonitor.setProgress("Starting Game...", 95);

        // 8 - Start the tick thread.
        start();
        Debug.signal(Debug.NOTICE, null, "Started the tick thread...");

        this.clientScreen.show();
        pMonitor.setProgress("Done...", 100);
        pMonitor.close();

        if (DataManager.SHOW_DEBUG) {
            System.out.println("Frame displayed on screen...");
        }

        // 9 - Welcome message
        sendMessage(new WelcomeMessage());

        if (DataManager.SHOW_DEBUG) {
            System.out.println("End of DataManager's showInterface !");
        }

        // 10 - Add extra plugin
        //clientScreen.getPlayerPanel().addPlugIn((JPanelPlugIn) new ChangeAspectPlugIn(), -1);

        // Test Petrus
        String empty[] = {"head", "body", "left hand", "right hand"};
        SimpleMenu2D emptyMenu = new SimpleMenu2D("emptyMenu", empty);
        emptyMenu.setItemEnabled("head", true);
        emptyMenu.setItemEnabled("body", true);
        emptyMenu.setItemEnabled("left hand", true);
        emptyMenu.setItemEnabled("right hand", true);
        ((SimpleMenu2D) this.menuManager.getRootMenu()).addItemLink(MenuManager.OBJECT_ITEM_NAME, emptyMenu);

    /*SimpleMenu2D objectMenu = (SimpleMenu2D) menuManager.findByName(MenuManager.OBJECT_ITEM_NAME);
    objectMenu.addItem("head");
     */
    // end Test Petrus
    }

    /*------------------------------------------------------------------------------------*/
    /** Resumes the game screen in case of server connection shut.
     */
    public void resumeInterface() {
        Debug.signal(Debug.NOTICE, null, "DataManager::ResumeInterface");

        AProgressMonitor pMonitor = new AProgressMonitor(ClientDirector.getClientManager(), "Wotlas");
        pMonitor.setProgress("Creating 2D Engine...", 15);

        // 1 - We recreate the graphics director...
        WindowPolicy wPolicy = null;

        if (ClientDirector.getClientConfiguration().getCenterScreenPolicy()) {
            wPolicy = new CenterWindowPolicy();
        } else {
            wPolicy = new LimitWindowPolicy();
        }

        if (ClientDirector.getClientConfiguration().getUseHardwareAcceleration()) {
            this.gDirector = new EnhancedGraphicsDirector(wPolicy, this.imageLib);
        } else {
            this.gDirector = new GraphicsDirector(wPolicy, this.imageLib);
        }

        Debug.signal(Debug.NOTICE, null, "Graphics Engine is using hardware mode : " + ClientDirector.getClientConfiguration().getUseHardwareAcceleration());

        this.clientScreen.getMapPanel().updateGraphicsDirector(this.gDirector);

        if (this.menuManager != null) {
            this.menuManager.clear();
        }

        // 2 - Retrieve player's informations
        pMonitor.setProgress("Loading Player Data from Server...", 30);
        this.myPlayer = null;

        waitForConnection(30000); // 30s max...

        try {
            synchronized (this.startGameLock) {
                this.connection.queueMessage(new MyPlayerDataPleaseMessage());
                this.startGameLock.wait(DataManager.CONNECTION_TIMEOUT);
            }
        } catch (InterruptedException ie) {
        }

        if (this.myPlayer == null) {
            pMonitor.close();
            showWarningMessage("Failed to retrieve your player data from the Game Server !\nPlease retry later...");
            closeConnection();
            return;
        }

        this.myPlayer.setIsMaster(true); // this player is controlled by the user.
        this.myPlayer.tick();
        addPlayer(this.myPlayer);

        if (DataManager.SHOW_DEBUG) {
            System.out.println("POSITION set to x:" + this.myPlayer.getX() + " y:" + this.myPlayer.getY() + " location is " + this.myPlayer.getLocation());
        }

        // 3 - Reset previous the data
        pMonitor.setProgress("Setting Preferences...", 80);

        this.clientScreen.getChatPanel().reset();
        this.clientScreen.getPlayerPanel().reset();
        this.players.clear();
        this.connection.setPingListener(this.clientScreen.getPingPanel());

        this.menuManager = new MenuManager(this.myPlayer, this.gDirector);
        this.menuManager.addMenu2DListener(this);

        // 4 - Init map display, resume tick thread & show screen...
        pMonitor.setProgress("Loading Map Data...", 85);

        changeMapData();
        resumeTickThread();

        pMonitor.setProgress("Starting Game...", 95);
        this.clientScreen.show();

        pMonitor.setProgress("Done...", 100);
        pMonitor.close();

        // 5 - Welcome message
        sendMessage(new WelcomeMessage());
    }

    /*------------------------------------------------------------------------------------*/
    /** Main loop to tick the graphics director every 50ms.
     */
    @Override
    public void run() {
        long now;
        int deltaT;
        int delay;

        String os = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");
        String vers = System.getProperty("os.version");

        Debug.signal(Debug.NOTICE, this, "OS INFO :\n\nOS NAME : <" + os + ">\nOS ARCH: <" + arch + ">\nOS VERSION: <" + vers + ">\n");

        delay = 50;

        //if ( os.equals("Windows 2000") || os.equals("Windows XP") )
        //  delay = 40;

        this.pauseTickThread = false;

        while (true) {
            now = System.currentTimeMillis();

            // Pause Thread ?
            synchronized (this.pauseTickThreadLock) {
                if (this.pauseTickThread) {
                    try {
                        this.pauseTickThreadLock.wait();
                    } catch (Exception e) {
                    }
                }
            }

            // Tick
            tick();

            deltaT = (int) (System.currentTimeMillis() - now);

            if (deltaT < delay) {
                Tools.waitTime(delay - deltaT);
            }
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** Tick Action. We propagate the tick on the players & GraphicsDirector.
     */
    public void tick() {

        // I - Update myPlayer's location
        this.myMapData.locationUpdate(this.myPlayer);

        // II - Update players drawings    
        synchronized (this.players) {
            Iterator<PlayerImpl> it = this.players.values().iterator();

            while (it.hasNext()) {
                it.next().tick();
            }
        }

        if (this.circle != null) {
            this.circle.tick();
        }

        // III - Graphics Director update & redraw
        if (this.clientScreen.getState() == Frame.ICONIFIED) {
            Tools.waitTime(400);
        } // we reduce our tick rate... and don't refresh the screen
        else {
            this.gDirector.tick();
        } // game screen update

        // IV - Sync Messages Execution
        NetMessageBehaviour syncMessages[] = this.syncMessageQueue.pullMessages();

        for (int i = 0; i < syncMessages.length; i++) {
            syncMessages[i].doBehaviour(this);
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To pause the tick thread.
     */
    private void pauseTickThread() {
        synchronized (this.pauseTickThreadLock) {
            this.pauseTickThread = true;
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To resume the tick thread.
     */
    private void resumeTickThread() {
        synchronized (this.pauseTickThreadLock) {
            this.pauseTickThread = false;
            this.pauseTickThreadLock.notify();
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To tell if the DataManager's tick thread is running.
     * @return true if it's running, false otherwise
     */
    public boolean isRunning() {
        synchronized (this.pauseTickThreadLock) {
            if (!this.pauseTickThread && this.myPlayer != null) {
                return true;
            }
            return false;
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To invoke the code of the specified message just after the current tick.
     *  This method can be called multiple times and is synchronized.
     */
    public void invokeLater(NetMessageBehaviour msg) {
        this.syncMessageQueue.queueMessage(msg);
    }

    /*------------------------------------------------------------------------------------*/
    /** To show a warning message
     */
    public void showWarningMessage(String warningMsg) {
        JOptionPane.showMessageDialog(this.clientScreen, warningMsg, "Warning message!", JOptionPane.WARNING_MESSAGE);
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

        if (DataManager.SHOW_DEBUG) {
            System.out.println("DataManager::onLeftClicJMapPanel");
        }

        if (this.updatingMapData) {
            return;
        } // updating Map Location

        // Menu clicked ?
        if (this.menuManager.isVisible()) {
            if (!this.menuManager.mouseClicked(e)) {
                this.menuManager.hide();
            } else {
                return;
            }
        }

        // Object/Player selected ?
        if (mouseSelect(e.getX(), e.getY(), true)) {
            return;
        }

        // Clicked object is the game screen...
        // We move the player to that location.
        Rectangle screen = this.gDirector.getScreenRectangle();

        synchronized (this.players) {
            this.myPlayer.moveTo(new Point(e.getX() + (int) screen.getX(), e.getY() + (int) screen.getY()), this.worldManager);
        }

        if (DataManager.SHOW_DEBUG) {
            System.out.println("END of DataManager::onLeftClicJMapPanel");
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** Called when user right-clic on JMapPanel
     */
    public void onRightClicJMapPanel(MouseEvent e) {
        if (DataManager.SHOW_DEBUG) {
            System.out.println("DataManager::onRightClicJMapPanel");
        }

        if (this.menuManager.isVisible()) {
            this.menuManager.hide();
        } else {
            // Menu selection & display
            mouseSelect(e.getX(), e.getY(), false);

            if (this.selectedPlayer != null) {
                this.menuManager.initContent(this.selectedPlayer);
            } //        else if(selectedObject!=null)
            //             ADD menuManager initContent here
            else {
                this.menuManager.initNoContent();
            }

            this.menuManager.show(new Point(e.getX(), e.getY()));
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** Called when the mouse cursor is dragged with the left button.
     * @param e mouse event
     * @param dx delta x since mouse pressed
     * @param dy delta y since mouse pressed
     * @param finalMov movement type as describe in JMapPanel, INIT_MOUSE_MOVEMENT, etc...
     */
    public void onLeftButtonDragged(MouseEvent e, int dx, int dy, byte movementType) {

        // if the player is moving we return
        if (this.myPlayer.getMovementComposer().isMoving()) {
            return;
        }

        double orientation = this.myPlayer.getMovementComposer().getOrientationAngle();

        // init the rotation ?
        if (movementType == JMapPanel.INIT_MOUSE_MOVEMENT) {
            this.refOrientation = orientation;
            this.ghostOrientation = orientation;
            return;
        }

        if (Math.abs((double) dx / 100) > 3.4) {
            return;
        }

        this.myPlayer.getMovementComposer().setOrientationAngle(this.refOrientation - (double) dx / 100);
        orientation = this.myPlayer.getMovementComposer().getOrientationAngle();

        // send an update message ?
        if (Math.abs(orientation - this.ghostOrientation) > 1.0 || (movementType == JMapPanel.END_MOUSE_MOVEMENT && Math.abs(orientation - this.ghostOrientation) >= 0.05)) {
            this.myPlayer.getMovementComposer().rotateTo(orientation);
            this.ghostOrientation = orientation;
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** Called when the mouse cursor is moved.
     * @param x mouse's x
     * @param y mouse's y
     */
    public void onLeftButtonMoved(int x, int y) {
        if (!this.menuManager.isVisible()) {
            return;
        }

        this.menuManager.mouseMoved(x, y);
    }

    /*------------------------------------------------------------------------------------*/
    /** Called when the mouse cursor is dragged with the left button.
     * @param dx delta x since mouse pressed
     * @param dy delta y since mouse pressed
     * @param startsNow tells if the drag movement is just about to start
     */
    public void onRightButtonDragged(int dx, int dy, boolean startsNow) {
        if (!this.menuManager.isVisible()) {
            return;
        }

        this.menuManager.mouseDragged(dx, dy, startsNow);
    }

    /*------------------------------------------------------------------------------------*/
    /** To select an object/player on screen via a mouse click
     * @param screen game screen dimension
     * @param x x position of the mouse
     * @param y y position of the mouse
     * @param isLeftClick true if the left button was clicked, false if it's the right.
     * @return true if we processed the mouse event, false if it was not for us.
     */
    public boolean mouseSelect(int x, int y, boolean isLeftClick) {

        // We search for the owner of the object
        Object object = this.gDirector.findOwner(x, y);

        // We take a look at the selected object the user clicked
        // Is it a player ? a door ?

        if (object instanceof PlayerImpl) {
            // We display selection and player info
            String previouslySelectedPlayerKey = "";

            if (this.selectedPlayer != null) {
                previouslySelectedPlayerKey = this.selectedPlayer.getPrimaryKey();
            }

            this.selectedPlayer = (PlayerImpl) object; // new player selected

            // We get the InfoPlugIn
            InfoPlugIn infoPanel = (InfoPlugIn) this.clientScreen.getPlayerPanel().getPlugIn("Info");

            if (infoPanel == null) {
                Debug.signal(Debug.ERROR, this, "InfoPlugIn not found !");
                return true;
            }

            // We erase the previous selection circle
            if (this.circle != null) {
                this.gDirector.removeDrawable(this.circle);
                this.circle = null;
            }

            // Deselect ?
            if (previouslySelectedPlayerKey.equals(this.selectedPlayer.getPrimaryKey()) && isLeftClick) {
                this.gDirector.addDrawable(this.selectedPlayer.getTextDrawable());
                this.gDirector.addDrawable(this.selectedPlayer.getWotCharacter().getAura());
                this.selectedPlayer = null;

                if (infoPanel != null) {
                    infoPanel.reset();
                }
                return true;
            }

            // Select
            this.circle = new CircleDrawable(this.selectedPlayer.getDrawable(), 20, this.selectedPlayer.getWotCharacter().getColor(), true, ImageLibRef.AURA_PRIORITY);
            this.gDirector.addDrawable(this.circle);
            this.gDirector.addDrawable(this.selectedPlayer.getTextDrawable());
            this.gDirector.addDrawable(this.selectedPlayer.getWotCharacter().getAura());

            if (infoPanel != null) {
                infoPanel.setPlayerInfo(this.selectedPlayer);
            }

            // Connection state
            if (this.selectedPlayer.getPlayerState().value == PlayerState.CONNECTED) {
                return true;
            }

            if (!isLeftClick) {
                return true;
            } // no away message displayed if right click

            // Away Message
            String awayMessage = this.selectedPlayer.getPlayerAwayMessage();

            if (!this.selectedPlayer.canDisplayAwayMessage()) {
                return true;
            }

            if (awayMessage != null) {
                JChatRoom chatRoom = this.clientScreen.getChatPanel().getCurrentJChatRoom();
                if (this.selectedPlayer.getPlayerState().value == PlayerState.DISCONNECTED) {
                    chatRoom.appendText("<font color='gray'> " + this.selectedPlayer.getFullPlayerName() + " (disconnected) says: <i> " + this.selectedPlayer.getPlayerAwayMessage() + " </i></font>");
                } else {
                    chatRoom.appendText("<font color='gray'> " + this.selectedPlayer.getFullPlayerName() + " (away) says: <i> " + this.selectedPlayer.getPlayerAwayMessage() + " </i></font>");
                }
            }

            return true;
        } else if (object instanceof Door) {
            // We open/close the door IF the player is near enough...
            Door door = (Door) object;

            if (DataManager.SHOW_DEBUG) {
                System.out.println("A door has been clicked...");
            }

            // player near enough the door ?
            if (door.isPlayerNear(this.myPlayer.getCurrentRectangle())) {

                if (isLeftClick) {
                    WotlasLocation location = new WotlasLocation(this.myPlayer.getLocation());

                    // ADD HERE lock test, does the player has the key if the door is locked ?

                    sendMessage(new DoorStateMessage(location, door.getMyRoomLinkID(), !door.isOpened()));
                } else {
                // Door Selection ?

                }
            } else {
                // we go to the door
                Point doorPoint = door.getPointNearDoor(this.myPlayer.getCurrentRectangle());

                if (doorPoint != null) {
                    this.myPlayer.moveTo(doorPoint, this.worldManager);
                }
            }

            return true;
        } //      else if( object instanceof BaseObject ) {
        //
        //             ADD HERE SELECTION CODE FOR OBJECTS (use the player selection as example)
        //      }
        else if (object != null) {
            // Unknown Object !
            Debug.signal(Debug.WARNING, this, "Unknown Object Clicked : " + object);
            return true;
        }

        return false; // event not for us
    }

    /*------------------------------------------------------------------------------------*/
    /** To add a wave arc effect on the player.
     */
    public void addWaveDrawable(PlayerImpl player) {
        if (this.gDirector != null) {
            this.gDirector.addDrawable(player.getWaveArcDrawable());
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To add a new player to the screen<br>
     * (called by client.message.description.PlayerDataMsgBehaviour)
     *
     * @player the player to add
     */
    public synchronized void addPlayer(PlayerImpl player) {
        this.players.put(player.getPrimaryKey(), player);
    }

    /** To remove a player
     *
     * @player the player to remove
     */
    public synchronized boolean removePlayer(PlayerImpl player) {
        this.players.remove(player.getPrimaryKey());
        return true;
    }

    /** To set our player<br>
     * (called by client.message.description.YourPlayerDataMsgBehaviour)
     *
     * @param player Our player
     */
    public void setCurrentPlayer(Player player) {
        this.myPlayer = (PlayerImpl) player;

        synchronized (this.startGameLock) {
            this.startGameLock.notify();
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To change the current MapData ( TownMap, WorldMap, InteriorMap ).
     */
    public void changeMapData() {
        this.updatingMapData = true;

        if (this.menuManager.isVisible()) {
            this.menuManager.hide();
        }

        try {
            if (this.myPlayer.getLocation().isRoom()) {
                this.myMapData = new InteriorMapData();
            } else if (this.myPlayer.getLocation().isTown()) {
                this.myMapData = new TownMapData();
            } else if (this.myPlayer.getLocation().isWorld()) {
                this.myMapData = new WorldMapData();
            }

            this.myMapData.showDebug(DataManager.SHOW_DEBUG);
            this.myMapData.initDisplay(this.myPlayer, this);
        } catch (Exception e) {
            Debug.signal(Debug.ERROR, this, e);
        }

        this.updatingMapData = false;
    }

    /*------------------------------------------------------------------------------------*/
    /** To suppress drawables, shadows, data
     */
    public void cleanInteriorMapData() {
        this.gDirector.removeAllDrawables();
        this.circle = null;
        this.selectedPlayer = null;
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
        p[0] = new Point(x, y);
        p[1] = new Point(x + width, y);
        p[2] = new Point(x + width, y + height);
        p[3] = new Point(x, y + height);
        p[4] = new Point(x, y);

        Drawable pathDrawable = new PathDrawable(p, color, ImageLibRef.AURA_PRIORITY);
        this.gDirector.addDrawable(pathDrawable);
    }

    /*------------------------------------------------------------------------------------*/
    /** To exit wotlas.
     */
    public void exit() {

        if (this.clientScreen != null) {
            int clientScreenWidth = this.clientScreen.getWidth();
            int clientScreenHeight = this.clientScreen.getHeight();

            if (clientScreenWidth > 100) {
                ClientDirector.getClientConfiguration().setClientWidth(clientScreenWidth);
            }

            if (clientScreenHeight > 100) {
                ClientDirector.getClientConfiguration().setClientHeight(clientScreenHeight);
            }
        }

        ClientDirector.getClientConfiguration().save();
        SoundLibrary.clear();
        Debug.exit();
    }

    /*------------------------------------------------------------------------------------*/
    /** To get the master player.
     */
    public PlayerImpl getMyPlayer() {
        return this.myPlayer;
    }

    /*------------------------------------------------------------------------------------*/
    /** Method called when an item has been clicked on an item who is not a menu link.
     *  @param e menu event generated.
     */
    public void menuItemClicked(Menu2DEvent e) {
        if (DataManager.SHOW_DEBUG) {
            System.out.println("Menu Item Clicked : " + e.toString());
        }
        // Test petrus
        if (e.getItemName().equals("test inventory plugin")) {
            System.out.println("ok");
        } else {
            System.out.println("not a test");
        }
    }

    /*------------------------------------------------------------------------------------*/
}
