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

import wotlas.common.ImageLibRef;
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

public class InteriorMapData implements MapData
{

 /*------------------------------------------------------------------------------------*/

  /** True if we show debug informations
   */
  public static boolean SHOW_DEBUG = true;

  DataManager dataManager;

  /** tells if the player could be moving to another room
   */
  private boolean couldBeMovingToAnotherRoom = false;

  /** current RoomLink considered for intersection
   */
  private RoomLink latestRoomLink;

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

    // 1 - We load the InteriorMap
    WotlasLocation location = myPlayer.getLocation();
    InteriorMap imap = dataManager.getWorldManager().getInteriorMap(location);
    if (SHOW_DEBUG) {
      System.out.println("InteriorMap");
      System.out.println("\tfullName = "  + imap.getFullName());
      System.out.println("\tshortName = " + imap.getShortName());
    }

    //   - We load the room
    Room room = dataManager.getWorldManager().getRoom(location);
    if (SHOW_DEBUG) {
      System.out.println("Room");
      System.out.println("\tfullName = "       + room.getFullName());
      System.out.println("\tshortName = "      + room.getShortName());
    }
    dataManager.getInfosPanel().setLocation(room.getFullName());

    if (SHOW_DEBUG)
      System.out.println("Adding a new player : " + myPlayer + "to room : " + room);
    room.addPlayer(myPlayer);

    // 2 - We set player's position if his position is incorrect
    if (myPlayer.getX() == -1) {
      ScreenPoint insertionPoint = room.getInsertionPoint();
      if (SHOW_DEBUG)
        System.out.println("\tinsertionPoint = " + insertionPoint);
      myPlayer.setX(insertionPoint.x);
      myPlayer.setY(insertionPoint.y);
      myPlayer.setPosition(insertionPoint);
    }

    // 3 - We load the image
    backgroundImageID = imap.getInteriorMapImage();
    if (SHOW_DEBUG)
      System.out.println("\tbackgroundImageID = " + backgroundImageID);
    background = (Drawable) new MultiRegionImage( myPlayer.getDrawable(),              // our reference for image loading
                                                  650,                                 // perception radius
                                                  imap.getImageRegionWidth(),          // grid deltax
                                                  imap.getImageRegionHeight(),         // grid deltay
                                                  imap.getImageWidth(),                // image's total width
                                                  imap.getImageHeight(),               // image's total height
                                                  imap.getInteriorMapImage()           // base image identifier
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

    // 5 - We initialize the AStar algo
    dataManager.getAStar().setMask( BinaryMask.create( bufIm ) );
    dataManager.getAStar().setSpriteSize(4);
    bufIm.flush(); // free image resource

    // 6 - We init the GraphicsDirector
    GraphicsDirector gDirector = dataManager.getGraphicsDirector();
    gDirector.init( background,               // background drawable
                    myPlayer.getDrawable(),   // reference for screen movements
                    new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight )   // screen default dimension
                   );

    //   - We show the roomLinks
    if (SHOW_DEBUG) {
      RoomLink[] roomLinks = room.getRoomLinks();
      if (roomLinks != null) {
        System.out.println("\tRoomLink");
        for (int i=0; i<roomLinks.length; i++) {
          System.out.println("\t\troomLinks["+i+"] = " + roomLinks[i]);
          dataManager.drawScreenRectangle(roomLinks[i].toRectangle(), Color.green);
        }
        roomLinks = null;
      }
    }

    //   - We show the mapExits
    if (SHOW_DEBUG) {
      MapExit[] mapExits = room.getMapExits();
      if (mapExits!= null) {
        System.out.println("\tMapExit");
        for (int i=0; i<mapExits.length; i++) {
          System.out.println("\t\tmapExits["+i+"] = " + mapExits[i]);
          dataManager.drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
        }
        mapExits = null;
      }
    }

    //   - We add visual properties to the player (shadows...)
    myPlayer.initVisualProperties(gDirector);

    //   - We play music
    String midiFile = imap.getMusicName();
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
    Room myRoom = dataManager.getWorldManager().getRoom( myPlayer.getLocation() );

    // I - ROOMLINK INTERSECTION UPDATE ( is the player moving to another room ? )
    RoomLink rl = myRoom.isIntersectingRoomLink( myPlayer.getCurrentRectangle() );

    if ( rl!=null && !couldBeMovingToAnotherRoom ) {
      // Player is intersecting a RoomLink
      if (SHOW_DEBUG)
        System.out.println("Insersecting a RoomLink");
      latestRoomLink = rl;
      couldBeMovingToAnotherRoom = true;

      // is there a Door ?
      if ( rl.getDoor()!=null ) {
        // nothing for now
      }
    } else if ( rl==null && couldBeMovingToAnotherRoom ) {
      // ok, no intersection now, are we in an another room ?
      if (SHOW_DEBUG)
        System.out.println("ok, no intersection now, are we in an another room ?");
      couldBeMovingToAnotherRoom = false;

      int newRoomID = myRoom.isInOtherRoom( latestRoomLink, myPlayer.getCurrentRectangle() );

      if ( newRoomID>=0 ) {
        // Ok, we move to this new Room
        if (SHOW_DEBUG)
          System.out.println("Removing an existing player : " + myPlayer + "to room : " + myRoom);
        myRoom.removePlayer( myPlayer );
        myPlayer.getLocation().setRoomID( newRoomID );
        Room room = dataManager.getWorldManager().getRoom(myPlayer.getLocation());
        if (SHOW_DEBUG)
          System.out.println("Adding a new player : " + myPlayer + "to room : " + room);
        room.addPlayer( myPlayer );
        dataManager.getInfosPanel().setLocation(room.getFullName());
        if (SHOW_DEBUG)
          System.out.print("Move to another room : " + newRoomID + " -> " + room.getFullName());

        if (SHOW_DEBUG) {
          RoomLink[] roomLinks = room.getRoomLinks();
          if (roomLinks != null) {
            System.out.println("\tRoomLink");
            for (int i=0; i<roomLinks.length; i++) {
              System.out.println("\t\troomLinks["+i+"] = " + roomLinks[i]);
              dataManager.drawScreenRectangle(roomLinks[i].toRectangle(), Color.green);
            }
            roomLinks = null;
          }
        }

        if (SHOW_DEBUG) {
          MapExit[] mapExits = room.getMapExits();
          if (mapExits!= null) {
            System.out.println("\tMapExit");
            for (int i=0; i<mapExits.length; i++) {
              System.out.println("\t\tmapExits["+i+"] = " + mapExits[i]);
              dataManager.drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
            }
            mapExits = null;
          }
        }

      } else {
        if (SHOW_DEBUG)
          System.out.println("We are still in the same room" + newRoomID);
      }
    } // End of part I

    // II - MAPEXIT INTERSECTION UPDATE ( is the player moving to another map ? )
    if ( myPlayer.isMoving() ) {
      Point destination = myPlayer.getEndPosition();
      MapExit mapExit = myRoom.isIntersectingMapExit( destination.x,
                                                      destination.y,
                                                      myPlayer.getCurrentRectangle()
                                                     );
      if ( mapExit!=null ) {
        // Ok, we are going to a new map...
        if (SHOW_DEBUG)
          System.out.println("We are going to a new map...");

        myPlayer.stopMoving();
        myRoom.removePlayer( myPlayer );
        myPlayer.setLocation( mapExit.getTargetWotlasLocation() );
        dataManager.cleanInteriorMapData(); // suppress drawables, shadows, data

        ScreenPoint targetPoint = mapExit.getTargetPosition();
        myPlayer.setX(targetPoint.x);
        myPlayer.setY(targetPoint.y);
        myPlayer.setPosition(targetPoint);

        switch( mapExit.getType() ) {
          case MapExit.INTERIOR_MAP_EXIT :
            if (SHOW_DEBUG)
              System.out.println("Move to another InteriorMap");
            //initInteriorMapDisplay(myPlayer.getLocation()); // init new map
            dataManager.changeMapData();
            break;

          case MapExit.TOWN_EXIT :
            if (SHOW_DEBUG)
              System.out.println("Move to TownMap");
            //initTownMapDisplay(myPlayer.getLocation()); // init new map
            dataManager.changeMapData();
            break;

          case MapExit.BUILDING_EXIT :
            if (SHOW_DEBUG)
              System.out.println("Move to Building");
            //initTownMapDisplay(myPlayer.getLocation()); // init new map
            dataManager.changeMapData();
            break;

          default:
            Debug.signal( Debug.CRITICAL, this, "Unknown mapExit of type : " + mapExit.getType() );
        }
      }
    } // End of part II
  }

 /*------------------------------------------------------------------------------------*/

  /** To update the graphicsDirector's drawables
   */
  public void tick() {}

 /*------------------------------------------------------------------------------------*/

}


  