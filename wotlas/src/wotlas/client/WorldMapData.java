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

import wotlas.client.screen.JClientScreen;

import wotlas.common.*;
import wotlas.common.universe.*;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;

import wotlas.libs.pathfinding.AStarDouble;

import wotlas.libs.sound.SoundLibrary;

import wotlas.utils.Debug;
import wotlas.utils.ScreenPoint;
import wotlas.utils.ScreenRectangle;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.*;

import java.io.File;
import java.io.IOException;

public class WorldMapData implements MapData
{

 /*------------------------------------------------------------------------------------*/

  /** True if we show debug informations
   */
  public static boolean SHOW_DEBUG = true;

  DataManager dataManager;

 /*------------------------------------------------------------------------------------*/

  /** Set to true to show debug information
   */
  public void showDebug(boolean value) {
    SHOW_DEBUG = value;
  }

 /*------------------------------------------------------------------------------------*/

  /** To init the display<br>
   * - load background and mask images<br>
   * - init the AStar algorithm
   * - init the Graphics Director
   * - show the other images (shadows, buildings, towns...)
   */
  public void initDisplay(PlayerImpl myPlayer) {
    myPlayer.init();

    ImageIdentifier backgroundImageID = null;   // background image identifier
    Drawable background = null;                 // background image

    dataManager = DataManager.getDefaultDataManager();
    String imageDBHome = dataManager.getImageDBHome();

    // 1 - We load the WorldMap
    WotlasLocation location = myPlayer.getLocation();
    WorldMap worldMap = dataManager.getWorldManager().getWorldMap(location);
    if (SHOW_DEBUG) {
      System.out.println("WorldMap");
      System.out.println("\tfullName = "  + worldMap.getFullName());
      System.out.println("\tshortName = " + worldMap.getShortName());
    }
    dataManager.getInfosPanel().setLocation(worldMap.getFullName());

    // 2 - We set player's position if his position is incorrect

    // 3 - We load the image
    backgroundImageID = worldMap.getWorldImage();
    if (SHOW_DEBUG)
      System.out.println("\tImageIdentifier = " + backgroundImageID);
    background = (Drawable) new MotionlessSprite( 0,                        // ground x=0
                                                  0,                        // ground y=0
                                                  backgroundImageID,        // image
                                                  ImageLibRef.MAP_PRIORITY, // priority
                                                  false                     // no animation
                                                 );

    // 4 - We load the mask
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
    BufferedImage bufIm = null;
    try {
      bufIm = ImageLibrary.loadBufferedImage(new ImageIdentifier( mapMaskID ), imageDBHome, BufferedImage.TYPE_INT_ARGB );
    } catch( IOException e ) {
      e.printStackTrace();
      return;
    }
    if (SHOW_DEBUG) {
      System.out.println("\tbufIm.width = " + bufIm.getWidth());
      System.out.println("\tbufIm.height = " + bufIm.getHeight());
      System.out.println("\tbackground.width = " + background.getWidth());
      System.out.println("\tbackground.height = " + background.getHeight());
    }

///////////////////////////// ALDISS : changement de l'initialisation de Astar

    // 5 - We initialize the AStar algo
    myPlayer.getMovementComposer().setMovementMask( BinaryMask.create( bufIm ), 5, 1 );

//    dataManager.getAStar().setMask( BinaryMask.create( bufIm ) );
//    dataManager.getAStar().setSpriteSize(1);
    bufIm.flush(); // free image resource

///////////////////////////// FIN ALDISS 

    // 6 - We init the GraphicsDirector
    GraphicsDirector gDirector = dataManager.getGraphicsDirector();
    gDirector.init( background,               // background drawable
                    myPlayer.getDrawable(),   // reference for screen movements
                    new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight )   // screen default dimension
                   );

    //   - We add towns' images
    TownMap towns[] = worldMap.getTownMaps();
    if (towns!=null) {
      if (SHOW_DEBUG)
        System.out.println("\tTowns");
      ImageIdentifier townImageID = null;   // town image identifier
      Drawable townImage = null;            // town image
      for (int i=0; i<towns.length; i++) {
        if (SHOW_DEBUG)
          System.out.println("\t\ttowns["+i+"] = " + towns[i]);
        townImageID = towns[i].getSmallTownImage();
        Rectangle position = towns[i].toRectangle();
        townImage = (Drawable) new MotionlessSprite( position.x,
                                                     position.y,
                                                     townImageID,              // image
                                                     ImageLibRef.MAP_PRIORITY, // priority
                                                     false                     // no animation
                                                    );
        gDirector.addDrawable(townImage);
      }
    }

    //   - We play music
    String midiFile = worldMap.getMusicName();
    if (midiFile != null)
      SoundLibrary.getSoundLibrary().playMusic( midiFile );
  }

 /*------------------------------------------------------------------------------------*/

  /** To update the location<br>
   * - test if player is intersecting a screenZone<br>
   * - test if player is entering a new WotlasLocation<br>
   * - change the current MapData
   */
  public void locationUpdate(PlayerImpl myPlayer) {
    WorldMap worldMap = dataManager.getWorldManager().getWorldMap( myPlayer.getLocation() );

    // I - TOWN INTERSECTION UPDATE ( is the player entering a town ? )
    Point destination = myPlayer.getEndPosition();
    TownMap townMap = worldMap.isEnteringTown( destination.x,
                                               destination.y,
                                               myPlayer.getCurrentRectangle() );

    if( townMap != null ) {
      // intersection with a TownMap, which MapExit are we using ?
      if (SHOW_DEBUG)
        System.out.println("We are entering a town...");

///////////////////////////// ALDISS : changement de nom de stopMoving

      myPlayer.stopMovement();
///////////////////////////// FIN ALDISS

      MapExit mapExit = townMap.findTownMapExit( myPlayer.getCurrentRectangle() );

      myPlayer.setLocation( mapExit.getMapExitLocation() );
      dataManager.cleanInteriorMapData(); // suppress drawables, shadows, data

      // We set our player on the middle of the MapExit
      myPlayer.setX( mapExit.getX() + mapExit.getWidth()/2 );
      myPlayer.setY( mapExit.getY() + mapExit.getHeight()/2 );
      myPlayer.setPosition( new ScreenPoint(myPlayer.getX(), myPlayer.getY()) );

      //initTownMapDisplay(myPlayer.getLocation()); // init new map
      dataManager.changeMapData();
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** To update the graphicsDirector's drawables
   */
  public void tick() {}

 /*------------------------------------------------------------------------------------*/

} 