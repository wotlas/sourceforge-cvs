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

public class TownMapData implements MapData
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
  private int currentTownMapID=-1;

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
  public void initDisplay(PlayerImpl myPlayer) {
    if (DataManager.SHOW_DEBUG)
      System.out.println("-- initDisplay in TownMapData --");
      
    myPlayer.init();

    ImageIdentifier backgroundImageID = null;   // background image identifier
    Drawable background = null;                 // background image

    dataManager = DataManager.getDefaultDataManager();
    String imageDBHome = dataManager.getImageDBHome();

    // 1 - We load the TownMap
    WotlasLocation location = myPlayer.getLocation();

    currentTownMapID = location.getTownMapID();

    TownMap townMap = dataManager.getWorldManager().getTownMap(location);
    if (SHOW_DEBUG) {
      System.out.println("TownMap");
      System.out.println("\tfullName = "  + townMap.getFullName());
      System.out.println("\tshortName = " + townMap.getShortName());
    }
    dataManager.getChatPanel().changeMainJChatRoom(townMap.getShortName());

    dataManager.addPlayer(myPlayer);

    // 2 - We set player's position if his position is incorrect

    // 3 - We load the image
    backgroundImageID = townMap.getTownImage();
    if (SHOW_DEBUG)
      System.out.println("\tbackgroundImageID = " + backgroundImageID);
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

    // 5 - We initialize the AStar algo
    myPlayer.getMovementComposer().setMovementMask( BinaryMask.create( bufIm ), 5, 1 );
    myPlayer.getMovementComposer().resetMovement();
    bufIm.flush(); // free image resource

    // 6 - Init the GraphicsDirector
    GraphicsDirector gDirector = dataManager.getGraphicsDirector();
    gDirector.init( background,               // background drawable
                    myPlayer.getDrawable(),   // reference for screen movements
                    new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight )   // screen default dimension
                   );

    //   - We add buildings' images
    Building buildings[] = townMap.getBuildings();
    if (buildings!=null) {
      if (SHOW_DEBUG)
        System.out.println("\tDrawing Buildings");
      ImageIdentifier buildingImageID = null;   // building image identifier
      Drawable buildingImage = null;            // building image
      for (int i=0; i<buildings.length; i++) {        
        buildingImageID = buildings[i].getSmallBuildingImage();
        Rectangle position = buildings[i].toRectangle();
        buildingImage = (Drawable) new MotionlessSprite( position.x,
                                                         position.y,
                                                         buildingImageID,          // image
                                                         ImageLibRef.MAP_PRIORITY, // priority
                                                         false                     // no animation
                                                        );
        gDirector.addDrawable(buildingImage);
      }
    }

    //   - We add MapExits' images
    if (SHOW_DEBUG) {
      MapExit[] mapExits = townMap.getMapExits();
      if (mapExits!= null) {
        if (SHOW_DEBUG)
          System.out.println("\tDrawing MapExits");
        for (int i=0; i<mapExits.length; i++) {
          //System.out.println("\t\tmapExits["+i+"] = " + mapExits[i]);
          dataManager.drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
        }
      }
    }

    //   - We show some informations on the screen
    String[] strTemp = { myPlayer.getFullPlayerName() };
    MultiLineText mltPlayerName = new MultiLineText(strTemp, 10, 10, Color.black, 15.0f, "Lblack.ttf", ImageLibRef.TEXT_PRIORITY, MultiLineText.LEFT_ALIGNMENT);
    gDirector.addDrawable(mltPlayerName);

    String[] strTemp2 = { townMap.getFullName() };
    MultiLineText mltLocationName = new MultiLineText(strTemp2, 10, 10, Color.black, 15.0f, "Lblack.ttf", ImageLibRef.TEXT_PRIORITY, MultiLineText.RIGHT_ALIGNMENT);
    gDirector.addDrawable(mltLocationName);

    //   - We play music
    String midiFile = townMap.getMusicName();
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

    // Has the currentLocation changed ?

    if ( (currentTownMapID != myPlayer.getLocation().getTownMapID())
          || (myPlayer.getLocation().getBuildingID()>-1) ) {
      if (DataManager.SHOW_DEBUG)
        System.out.println("LOCATION HAS CHANGED in TownMapData");
        
      Debug.signal( Debug.NOTICE, null, "LOCATION HAS CHANGED in TownMapData");

      dataManager.getPlayers().clear();
      dataManager.cleanInteriorMapData();
      dataManager.getChatPanel().reset();
      
      //myPlayer.setPosition( new ScreenPoint(myPlayer.getX(), myPlayer.getY()) );
      
      dataManager.changeMapData();
      return;
    }

    TownMap townMap = dataManager.getWorldManager().getTownMap( myPlayer.getLocation() );

    // I - MAPEXIT INTERSECTION UPDATE ( is the player moving to a world map ? )
    Point destination = myPlayer.getEndPosition();
    MapExit mapExit = townMap.isIntersectingMapExit( destination.x,
                                                     destination.y,
                                                     myPlayer.getCurrentRectangle()
                                                    );

    if ( mapExit!=null ) {
      // Ok, we are going to a world map...
      if (SHOW_DEBUG)
        System.out.println("We are going to a world map...");

      myPlayer.getMovementComposer().resetMovement();

/* NETMESSAGE */
      if (isNotMovingToAnotherMap) {
        isNotMovingToAnotherMap = false;
        myPlayer.sendMessage( new CanLeaveTownMapMessage(myPlayer.getPrimaryKey(),
                                  mapExit.getTargetWotlasLocation(),
                                  mapExit.getTargetPosition().x, mapExit.getTargetPosition().y,
                                  mapExit.getTargetOrientation() ) );
      }
    }

    // II - BUILDING INTERSECTION UPDATE ( is the player entering a building ? )
    /*Building buildingMap = townMap.isEnteringBuilding( destination.x,
                                                       destination.y,
                                                       myPlayer.getCurrentRectangle() );*/
    Building buildingMap = townMap.isEnteringBuilding( myPlayer.getX(),
                                                       myPlayer.getY(),
                                                       myPlayer.getCurrentRectangle()
                                                      );

    if ( buildingMap != null ) {
      // intersection with a Building, which MapExit are we using ?
      if (SHOW_DEBUG)
        System.out.println("We are entering a building...");

///////////////////////////// ALDISS : avant stopMoving()
      myPlayer.getMovementComposer().resetMovement();
///////////////////////////// FIN ALDISS

      if (SHOW_DEBUG) {
        System.out.println("\t\tbuildingMap.getFullName() = " + buildingMap.getFullName());
        System.out.println("\t\tbuildingMap.getShortName() = " + buildingMap.getShortName());
        //System.out.print("\t\tmyPlayer.getAngle() = ");
        //System.out.println(myPlayer.getAngle()*180/Math.PI);
        //System.out.println("cosinus = " + Math.cos(myPlayer.getAngle()));
        //System.out.println("sinus = " + Math.sin(myPlayer.getAngle()));
      }

      mapExit = buildingMap.findTownMapExit( myPlayer.getAngle() );
      
        if (SHOW_DEBUG) {
          System.out.println("Which MapExit are we using ?");
          System.out.println("\t\tmapExit.getType() = " + (int) mapExit.getType());
          System.out.print("\t\tmapExit.getMapExitSide() = ");
        }
        
        if (SHOW_DEBUG) {
          System.out.println("\t\tmapExit.getTargetWotlasLocation() = " + mapExit.getTargetWotlasLocation());
          System.out.println("\t\tmapExit.getMapExitLocation() = " + mapExit.getMapExitLocation());
        }

      if (isNotMovingToAnotherMap) {
        isNotMovingToAnotherMap = false;

      // New Position
         ScreenPoint newPos = mapExit.getInsertionPoint();
         
         myPlayer.sendMessage( new CanLeaveTownMapMessage(myPlayer.getPrimaryKey(),
                               mapExit.getMapExitLocation(), newPos.x, newPos.y,
                               mapExit.getLocalOrientation() ) );
      }
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** To update the graphicsDirector's drawables
   */
  public void tick() {}

 /*------------------------------------------------------------------------------------*/

}


  