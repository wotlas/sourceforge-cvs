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

import wotlas.common.message.description.*;
import wotlas.common.message.movement.*;
import wotlas.common.universe.*;
import wotlas.common.*;

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

import java.util.Hashtable;

public class WorldMapData implements MapData
{

 /*------------------------------------------------------------------------------------*/

  /** True if we show debug informations
   */
  public static boolean SHOW_DEBUG = false;

  /** if true, the player can change its MapData
   * otherwise, the server didn't send a message to do so => player stay where he is
   */
  public boolean canChangeMap;

  /** Our default dataManager
   */
  private DataManager dataManager;

  /** tells if the player is going to another map
   */
  private boolean isNotMovingToAnotherMap = true;
  
  /** previous location
   */
  private int currentWorldMapID = -1;

 /*------------------------------------------------------------------------------------*/

  /** Set to true to show debug information
   */
  public void showDebug(boolean value) {
    SHOW_DEBUG = value;
  }

 /*------------------------------------------------------------------------------------*/

  /** To set isNotMovingToAnotherMap
   */
  public void setIsNotMovingToAnotherMap(boolean value) {
    isNotMovingToAnotherMap = value;
  }

 /*------------------------------------------------------------------------------------*/

  /** To init the display<br>
   * - load background and mask images<br>
   * - init the AStar algorithm
   * - init the Graphics Director
   * - show the other images (shadows, buildings, towns...)
   */
  public void initDisplay(PlayerImpl myPlayer, DataManager dataManager ) {

    if (DataManager.SHOW_DEBUG)
      System.out.println("-- initDisplay in WorldMapData --");

    this.dataManager = dataManager;

    ImageIdentifier backgroundImageID = null;   // background image identifier
    Drawable background = null;                 // background image

    GraphicsDirector gDirector = dataManager.getGraphicsDirector();
    String imageDBHome = dataManager.getImageDBHome();

    // 0 - some inits...
    myPlayer.init();

    // 1 - We load the WorldMap
    WotlasLocation location = myPlayer.getLocation();

    currentWorldMapID = location.getWorldMapID();

    WorldMap worldMap = dataManager.getWorldManager().getWorldMap(location);
    if (SHOW_DEBUG) {
      System.out.println("WorldMap");
      System.out.println("\tfullName = "  + worldMap.getFullName());
      System.out.println("\tshortName = " + worldMap.getShortName());
    }
    dataManager.getChatPanel().changeMainJChatRoom(worldMap.getShortName());

    dataManager.addPlayer(myPlayer);

    // 2 - We set player's position if his position is incorrect

    // 3 - We load the image
    backgroundImageID = worldMap.getWorldImage();
    gDirector.getImageLibrary().loadImage( backgroundImageID );

    if (SHOW_DEBUG)
      System.out.println("\tImageIdentifier = " + backgroundImageID);

    background = (Drawable) new MotionlessSprite( 0,                        // ground x=0
                                                  0,                        // ground y=0
                                                  backgroundImageID,        // image
                                                  ImageLibRef.MAP_PRIORITY  // priority
                                                 );

    // 4 - We load the mask
    BufferedImage bufIm = null;

    try {
       ImageIdentifier mapMaskID = gDirector.getImageLibrary().getImageIdentifier( backgroundImageID, "mask" );

       if(mapMaskID!=null) {
          File maskFile = gDirector.getImageLibrary().getImageFile( mapMaskID );
          
          if(maskFile!=null)
             bufIm = ImageLibrary.loadBufferedImage( maskFile.getPath(), BufferedImage.TYPE_INT_ARGB );
       }

       if(bufIm==null) {
          Debug.signal( Debug.CRITICAL, this, "Mask not found" );
          Debug.exit();
       }
    }
    catch( ImageLibraryException e ) {
      Debug.signal( Debug.CRITICAL, this, "Image Library Corrupted: "+e );
      Debug.exit();
    }

    // 5 - We initialize the AStar algo
    myPlayer.getMovementComposer().setMovementMask( BinaryMask.create( bufIm ), 5, 1 );
    myPlayer.getMovementComposer().resetMovement();
    bufIm.flush(); // free image resource

    // 6 - We init the GraphicsDirector
    gDirector.init( background,               // background drawable
                    myPlayer.getDrawable(),   // reference for screen movements
                    new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight )   // screen default dimension
                   );

    //   - We add towns' images
    TownMap towns[] = worldMap.getTownMaps();
    if (towns!=null) {
      if (SHOW_DEBUG)
        System.out.println("\tDrawing Towns");
      ImageIdentifier townImageID = null;   // town image identifier
      Drawable townImage = null;            // town image
      for (int i=0; i<towns.length; i++) {        
        townImageID = towns[i].getSmallTownImage();
        Rectangle position = towns[i].toRectangle();
        townImage = (Drawable) new MotionlessSprite( position.x,
                                                     position.y,
                                                     townImageID,              // image
                                                     ImageLibRef.MAP_PRIORITY  // priority
                                                    );
        gDirector.addDrawable(townImage);
      }
    }

    //   - We show some informations on the screen
    String[] strTemp = { myPlayer.getFullPlayerName() };
    MultiLineText mltPlayerName = new MultiLineText(strTemp, 10, 10, Color.black, 15.0f, "Lblack.ttf", ImageLibRef.TEXT_PRIORITY, MultiLineText.LEFT_ALIGNMENT);
    gDirector.addDrawable(mltPlayerName);

    String[] strTemp2 = { worldMap.getFullName() };
    MultiLineText mltLocationName = new MultiLineText(strTemp2, 10, 10, Color.black, 15.0f, "Lblack.ttf", ImageLibRef.TEXT_PRIORITY, MultiLineText.RIGHT_ALIGNMENT);
    gDirector.addDrawable(mltLocationName);

    //   - We play music
    String midiFile = worldMap.getMusicName();
    if (midiFile != null)
      SoundLibrary.getSoundLibrary().playMusic( midiFile );

    //   - We retreive other players informations
    if( dataManager.isAlive() )
        dataManager.sendMessage(new AllDataLeftPleaseMessage());
  }

 /*------------------------------------------------------------------------------------*/

  /** canChangeMap is set to true if player can change its MapData<br>
   * called by wotlas.client.message.YouCanLeaveMapMessage
   */
  /*public void canChangeMapLocation( boolean canChangeMap ) {
    synchronized( changeMapLock ) {
      this.canChangeMap = canChangeMap;
      changeMapLock.notify();
    }
  }*/

 /*------------------------------------------------------------------------------------*/

  /** To update the location<br>
   * - test if player is intersecting a screenZone<br>
   * - test if player is entering a new WotlasLocation<br>
   * - change the current MapData
   */
  public void locationUpdate(PlayerImpl myPlayer) {

    if(dataManager==null)
       return;

    // Has the currentLocation changed ?

    if ( (currentWorldMapID != myPlayer.getLocation().getWorldMapID())
          || (myPlayer.getLocation().getTownMapID()>-1) ) {
      Debug.signal( Debug.NOTICE, null, "LOCATION HAS CHANGED in WorldMapData");

      dataManager.getPlayers().clear();
      dataManager.cleanInteriorMapData(); // suppress drawables, shadows, data
      dataManager.getChatPanel().reset();
      
      //myPlayer.setPosition( new ScreenPoint(myPlayer.getX(), myPlayer.getY()) );
      
      dataManager.changeMapData();
      return;
    }

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

      myPlayer.getMovementComposer().resetMovement();

      MapExit mapExit = townMap.findTownMapExit( myPlayer.getCurrentRectangle() );

      if (isNotMovingToAnotherMap) {
        isNotMovingToAnotherMap = false;

      // New Position
         ScreenPoint newPos = mapExit.getInsertionPoint();

         myPlayer.sendMessage( new CanLeaveWorldMapMessage(myPlayer.getPrimaryKey(),
                                  mapExit.getMapExitLocation(),
                                  newPos.x, newPos.y, mapExit.getLocalOrientation() ) );
      }
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** To update the graphicsDirector's drawables
   */
  public void tick() {}

 /*------------------------------------------------------------------------------------*/

} 