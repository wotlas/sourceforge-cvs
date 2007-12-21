/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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

package wotlas.editor;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import wotlas.client.ClientDirector;
import wotlas.client.TileMapData;
import wotlas.common.ImageLibRef;
import wotlas.common.ResourceManager;
import wotlas.common.Tickable;
import wotlas.common.WorldManager;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.aswing.AProgressMonitor;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.EnhancedGraphicsDirector;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.GroupOfGraphics;
import wotlas.libs.graphics2d.ImageLibrary;
import wotlas.libs.graphics2d.WindowPolicy;
import wotlas.libs.graphics2d.drawable.CircleDrawable;
import wotlas.libs.graphics2d.drawable.PathDrawable;
import wotlas.libs.graphics2d.menu.Menu2DEvent;
import wotlas.libs.graphics2d.menu.Menu2DListener;
import wotlas.libs.graphics2d.policy.CenterWindowPolicy;
import wotlas.utils.Debug;
import wotlas.utils.Tools;

/** A EditorDataManager manages Game Data to be used by editor
 * It possesses a WorldManager
 *
 * @author Petrus, Aldiss
 * @see wotlas.common.NetConnectionListener
 */

public class EditorDataManager extends Thread implements Tickable, Menu2DListener {

    /*------------------------------------------------------------------------------------*/

    /** Image Library
     */
    public final static String IMAGE_LIBRARY = "graphics/imagelib";

    /** size of a mask's cell (in pixels)
     */
    public final static int TILE_SIZE = 5;

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

    /** Our TileMapData : data of the current map displayed on screen.
     */
    public TileMapData myMapData;

    /** Our ImageLibrary.
     */
    private ImageLibrary imageLib;

    /** Our Graphics Director.
     */
    private GraphicsDirector gDirector;

    private JScreen screen;

    private EditMenu menuManager;

    /*------------------------------------------------------------------------------------*/

    /*** DATA ACCESS CONTROLLER ***/

    /** Tick Thread Lock.
     */
    private Object pauseTickThreadLock = new Object();

    /** Do we have to pause the tick thread ?
     */
    private boolean pauseTickThread;

    /** Are we changing the MapData ?
     */
    private boolean updatingMapData = false;

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
    public EditorDataManager(ResourceManager rManager) {

        // 1 - We create our world Manager. It will load the universe data.
        this.worldManager = new WorldManager(true, rManager);

        // 2 - Misc inits

        this.pauseTickThreadLock = new Object();
        this.pauseTickThread = false;
        this.updatingMapData = false;

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
    public TileMapData getMapData() {
        return this.myMapData;
    }

    /** To get JScreen.
     */
    public JScreen getScreen() {
        return this.screen;
    }

    /*------------------------------------------------------------------------------------*/

    /** Set to true to show debug information
     */
    public void showDebug(boolean value) {
        EditorDataManager.SHOW_DEBUG = value;
    }

    /*------------------------------------------------------------------------------------*/

    public void showInterface() {

        // 0 - State analysis, progress monitor init...
        Debug.signal(Debug.NOTICE, null, "DataManager call to ShowInterface");

        AProgressMonitor pMonitor = new AProgressMonitor(ClientDirector.getClientManager(), "Wotlas");
        pMonitor.setProgress("Loading Shared Images...", 0);

        // 1 - Create Image Library
        try {
            this.imageLib = new ImageLibrary(EditTile.getResourceManager());
            this.imageLib.setLoadAllJITDirectoryImages(true); // images from JIT directories are loaded together.
        } catch (Exception ex) {
            Debug.signal(Debug.FAILURE, this, ex);
            Debug.exit();
        }

        pMonitor.setProgress("Reading Preferences...", 10);

        pMonitor.setProgress("Creating 2D Engine...", 15);

        // 3 - Create Graphics Director
        WindowPolicy wPolicy = null;

        wPolicy = new CenterWindowPolicy();
        this.gDirector = new EnhancedGraphicsDirector(wPolicy, this.imageLib);

        pMonitor.setProgress("Creating GUI...", 20);

        // 4 - Creation of the GUI components
        this.screen = new JScreen(this.gDirector, this);

        if (EditorDataManager.SHOW_DEBUG)
            System.out.println("JScreen created");

        pMonitor.setProgress("Loading Player Data from PseudoServer...", 30);

        this.screen.init();

        this.menuManager = new EditMenu(this.gDirector);
        this.menuManager.addMenu2DListener(this);

        pMonitor.setProgress("Loading Map Data...", 85);

        // 7 - Init the map display...
        changeMapData();

        if (EditorDataManager.SHOW_DEBUG)
            System.out.println("Changed map data !");

        pMonitor.setProgress("Starting Game...", 95);

        // 8 - Start the tick thread.
        start();
        Debug.signal(Debug.NOTICE, null, "Started the tick thread...");

        this.screen.show();
        Debug.signal(Debug.NOTICE, null, "Show screen...");
        pMonitor.setProgress("Done...", 100);
        pMonitor.close();

        if (EditorDataManager.SHOW_DEBUG)
            System.out.println("Frame displayed on screen...");

        // 9 - Welcome message

        if (EditorDataManager.SHOW_DEBUG)
            System.out.println("End of DataManager's showInterface !");
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
                if (this.pauseTickThread)
                    try {
                        this.pauseTickThreadLock.wait();
                    } catch (Exception e) {
                    }
            }

            // Tick
            tick();

            deltaT = (int) (System.currentTimeMillis() - now);

            if (deltaT < delay)
                Tools.waitTime(delay - deltaT);
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** Tick Action. We propagate the tick on the players & GraphicsDirector.
     */
    public void tick() {

        // III - Graphics Director update & redraw
        if (this.screen.getState() == Frame.ICONIFIED)
            Tools.waitTime(400); // we reduce our tick rate... and don't refresh the screen
        else
            this.gDirector.tick(); // game screen update

        // IV - Sync Messages Execution
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
            if (!this.pauseTickThread)
                return true;
            return false;
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** To show a warning message
     */
    public void showWarningMessage(String warningMsg) {
        JOptionPane.showMessageDialog(this.screen, warningMsg, "Warning message!", JOptionPane.WARNING_MESSAGE);
    }

    /*------------------------------------------------------------------------------------*/

    /** Called when user left-clic on JMapPanel
     */
    public void onLeftClicJMapPanel(MouseEvent e) {

        if (EditorDataManager.SHOW_DEBUG)
            System.out.println("DataManager::onLeftClicJMapPanel");

        if (this.updatingMapData)
            return; // updating Map Location

        // Menu clicked ?
        if (this.menuManager.isVisible())
            if (!this.menuManager.mouseClicked(e))
                this.menuManager.hide();
            else
                return;

        // Object/Player selected ?
        if (mouseSelect(e.getX(), e.getY(), true))
            return;

        if (EditorDataManager.SHOW_DEBUG)
            System.out.println("END of DataManager::onLeftClicJMapPanel");
    }

    /*------------------------------------------------------------------------------------*/

    /** Called when user right-clic on JMapPanel
     */
    public void onRightClicJMapPanel(MouseEvent e) {
        if (EditorDataManager.SHOW_DEBUG)
            System.out.println("DataManager::onRightClicJMapPanel");

        if (this.menuManager.isVisible())
            this.menuManager.hide();
        else {
            // Menu selection & display
            mouseSelect(e.getX(), e.getY(), false);
            this.menuManager.initNoContent();
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
    }

    public void onLeftButtonMoved(int x, int y) {
        if (!this.menuManager.isVisible())
            return;
        this.menuManager.mouseMoved(x, y);
    }

    public void onRightButtonDragged(int dx, int dy, boolean startsNow) {
        /*        if( !menuManager.isVisible() )
                    return;

                menuManager.mouseDragged( dx, dy, startsNow );*/
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

        /*
        if ( object instanceof ScreenObject ) {

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
         */

        return false; // event not for us
    }

    /*------------------------------------------------------------------------------------*/

    /** To change the current MapData ( TownMap, WorldMap, InteriorMap ).
     */
    public void changeMapData() {
        this.updatingMapData = true;

        if (this.menuManager.isVisible())
            this.menuManager.hide();

        this.myMapData = new TileMapData();
        this.myMapData.showDebug(EditorDataManager.SHOW_DEBUG);
        WotlasLocation location = new WotlasLocation();
        location.setTileMapID(0);
        this.myMapData.initDisplayEditor(this, location);

        this.updatingMapData = false;
    }

    /*------------------------------------------------------------------------------------*/

    /** To suppress drawables, shadows, data
     */
    public void cleanInteriorMapData() {
        this.gDirector.removeAllDrawables();
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
        // SoundLibrary.clear();
        Debug.exit();
        System.exit(0);
    }

    /*------------------------------------------------------------------------------------*/

    /** Method called when an item has been clicked on an item who is not a menu link.
     *  @param e menu event generated.
     */
    public void menuItemClicked(Menu2DEvent e) {
        if (EditorDataManager.SHOW_DEBUG)
            System.out.println("Menu Item Clicked : " + e.toString());
    }

    /*------------------------------------------------------------------------------------*/

    public void clickOnATile(int x, int y) {
        if (EditorPlugIn.itSelf.MainTabb.getSelectedIndex() == 2) {
            EditorPlugIn.manageAddExit(x, y);
            System.out.println("x,y per primo exit : " + x + " " + y);
        } else if (EditorPlugIn.itSelf.MainTabb.getSelectedIndex() == 1) {
            EditTile.workingOnThisTileMap.getManager().getMapBackGroundData()[x][y][0] = (byte) EditorPlugIn.selectedGroup;
            EditTile.workingOnThisTileMap.getManager().getMapBackGroundData()[x][y][1] = (byte) EditorPlugIn.selectedGroupImgNr;
            EditTile.workingOnThisTileMap.getManager().getMapMask()[x][y] = GroupOfGraphics.ROGUE_SET[EditorPlugIn.selectedGroup].getFreeStatus();
            //            EditTile.workingOnThisTileMap.getManager().getMapMask(
            //            )[x][y] = EditorPlugIn.selectedIsFree;
            EditorPlugIn.AddIt(x, y);
        }
    }
}