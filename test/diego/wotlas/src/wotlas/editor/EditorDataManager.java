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

import wotlas.client.*;
// import wotlas.client.gui.*;
// import wotlas.client.screen.*;
import wotlas.client.screen.extraplugin.*;
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
import wotlas.libs.persistence.*;
import wotlas.libs.sound.SoundLibrary;
import wotlas.libs.aswing.*;

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
    public EditorDataManager( ResourceManager rManager ) {

      // 1 - We create our world Manager. It will load the universe data.
         worldManager = new WorldManager( true, rManager );

      // 2 - Misc inits

         pauseTickThreadLock = new Object();
         pauseTickThread = false;
         updatingMapData = false;

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
    public TileMapData getMapData() {
      return myMapData;
    }
  /** To get JScreen.
   */
    public JScreen getScreen() {
      return screen;
    }

 /*------------------------------------------------------------------------------------*/

  /** Set to true to show debug information
   */
    public void showDebug(boolean value) {
      SHOW_DEBUG = value;
    }

 /*------------------------------------------------------------------------------------*/

  public void showInterface() {

    // 0 - State analysis, progress monitor init...
       Debug.signal( Debug.NOTICE, null, "DataManager call to ShowInterface");

       AProgressMonitor pMonitor = new AProgressMonitor( ClientDirector.getClientManager(), "Wotlas" );
       pMonitor.setProgress("Loading Shared Images...",0);

    // 1 - Create Image Library
       try {
          imageLib = new ImageLibrary( EditTile.getResourceManager() );
          imageLib.setLoadAllJITDirectoryImages(true); // images from JIT directories are loaded together.
       }
       catch( Exception ex ) {
          Debug.signal(Debug.FAILURE, this, ex );
          Debug.exit();
       }

       pMonitor.setProgress("Reading Preferences...",10);

       pMonitor.setProgress("Creating 2D Engine...",15);
    
    // 3 - Create Graphics Director
       WindowPolicy wPolicy = null;
    
           wPolicy = new CenterWindowPolicy();
           gDirector = new EnhancedGraphicsDirector( wPolicy, imageLib );

       pMonitor.setProgress("Creating GUI...",20);

    // 4 - Creation of the GUI components
       screen = new JScreen(gDirector, this );

       if(SHOW_DEBUG)
          System.out.println("JScreen created");

       pMonitor.setProgress("Loading Player Data from PseudoServer...",30);

       screen.init();

       menuManager = new EditMenu( gDirector );
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

       screen.show();
       Debug.signal( Debug.NOTICE, null, "Show screen..." );
       pMonitor.setProgress("Done...",100);
       pMonitor.close();

       if (SHOW_DEBUG)
           System.out.println("Frame displayed on screen...");

    // 9 - Welcome message

       if(SHOW_DEBUG)
          System.out.println("End of DataManager's showInterface !");
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


    // III - Graphics Director update & redraw
       if( screen.getState()==Frame.ICONIFIED )
          Tools.waitTime(400); // we reduce our tick rate... and don't refresh the screen
       else
          gDirector.tick(); // game screen update

    // IV - Sync Messages Execution
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
                if( !pauseTickThread )
                    return true;
                return false;
          }
    }

 /*------------------------------------------------------------------------------------*/

  /** To show a warning message
   */
    public void showWarningMessage(String warningMsg) {
         JOptionPane.showMessageDialog( screen, warningMsg, "Warning message!", JOptionPane.WARNING_MESSAGE);
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
   }


    public void onLeftButtonMoved( int x, int y ) {
        if( !menuManager.isVisible() )
            return;
        menuManager.mouseMoved( x, y );
    }
    
    public void onRightButtonDragged( int dx, int dy,  boolean startsNow ) {
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
    public boolean mouseSelect( int x, int y, boolean isLeftClick ) {

     // We search for the owner of the object
        Object object = gDirector.findOwner( x, y );

     // We take a look at the selected object the user clicked
     // Is it a player ? a door ?

        if( object instanceof Door ) {
          // We open/close the door IF the player is near enough...
             Door door = (Door) object;

             if (SHOW_DEBUG)
                System.out.println("A door has been clicked...");

          // player near enough the door ?
              /*
             if( door.isPlayerNear( myPlayer.getCurrentRectangle() ) ) {

             }
             else {
              // we go to the door
                 Point doorPoint = door.getPointNearDoor( myPlayer.getCurrentRectangle() );

                 if( doorPoint!=null )
                     // myPlayer.moveTo( doorPoint, worldManager );
             }
               */

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

  /** To change the current MapData ( TownMap, WorldMap, InteriorMap ).
   */
    public void changeMapData() {
        updatingMapData=true;

        if( menuManager.isVisible() )
            menuManager.hide();
        
        myMapData = new TileMapData();
        myMapData.showDebug(SHOW_DEBUG);
        WotlasLocation location = new WotlasLocation();
        location.setTileMapID(0);
        myMapData.initDisplayEditor( this, location );

        updatingMapData=false;
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
    public void menuItemClicked( Menu2DEvent e ) {
        if(SHOW_DEBUG)
            System.out.println("Menu Item Clicked : "+e.toString()); 
    }
 /*------------------------------------------------------------------------------------*/

    public void clickOnATile( int x, int y ) {
        EditTile.workingOnThisTileMap.getManager().getMapBackGroundData(
        )[x][y][0] = (byte) EditorPlugIn.selectedGroup;
        EditTile.workingOnThisTileMap.getManager().getMapBackGroundData(
        )[x][y][1] = (byte) EditorPlugIn.selectedGroupImgNr;
        EditTile.workingOnThisTileMap.getManager().getMapBackGroundData(
        )[x][y][2] = (byte) EditorPlugIn.selectedIsFree;
        EditorPlugIn.AddIt(x,y);
    }
}