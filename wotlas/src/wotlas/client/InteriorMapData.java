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

public class InteriorMapData implements MapData
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

  /** tells if the player could be moving to another room
   */
  private boolean couldBeMovingToAnotherRoom = false;
  
  /** tells if the player is going to another map
   */
  private boolean isNotMovingToAnotherMap = true;

  /** current RoomLink considered for intersection
   */
  private RoomLink latestRoomLink;

  /** Display current location name
   */
  private MultiLineText mltLocationName;

  /** Associated InteriorMap of this InteriorMapData.
   */
  private InteriorMap imap;

  /** true if we must reset the room
   */
  private boolean resetRoom = false;

  /** previous location
   */
  private int currentInteriorMapID = -1;
  private int currentRoomID = -1;

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

    this.dataManager = dataManager;

    if (DataManager.SHOW_DEBUG)
      System.out.println("-- initDisplay in InteriorMapData --");
    
    ImageIdentifier backgroundImageID = null;   // background image identifier
    Drawable background = null;                 // background image

    GraphicsDirector gDirector = dataManager.getGraphicsDirector();
    String imageDBHome = dataManager.getImageDBHome();

    // 0 - Some inits...
    myPlayer.init();

    // 1 - We load the InteriorMap
    WotlasLocation location = myPlayer.getLocation();

    currentInteriorMapID = location.getInteriorMapID();
    currentRoomID = location.getRoomID();

    imap = dataManager.getWorldManager().getInteriorMap(location);
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
    dataManager.getChatPanel().changeMainJChatRoom(room.getShortName());

/* NETMESSAGE */
    if (SHOW_DEBUG)
      System.out.println("dataManager.sendMessage( new EnteringRoomMessage(...) )");

    if (SHOW_DEBUG)
      System.out.println("Adding a new player : " + myPlayer + "to dataManager");
    dataManager.addPlayer(myPlayer);

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
                                                  450,                                 // perception radius
                                                  imap.getImageRegionWidth(),          // grid deltax
                                                  imap.getImageRegionHeight(),         // grid deltay
                                                  imap.getImageWidth(),                // image's total width
                                                  imap.getImageHeight(),               // image's total height
                                                  imap.getInteriorMapImage()           // base image identifier
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
    myPlayer.getMovementComposer().setMovementMask( BinaryMask.create( bufIm ), 5, 4 );
    myPlayer.getMovementComposer().resetMovement();
    bufIm.flush(); // free image resource

    // 6 - We init the GraphicsDirector
    gDirector.init( background,               // background drawable
                    myPlayer.getDrawable(),   // reference for screen movements
                    new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight )   // screen default dimension
                   );

    //   - We show the roomLinks
    if (SHOW_DEBUG) {
      RoomLink[] roomLinks = room.getRoomLinks();
      if (roomLinks != null) {
        System.out.println("\tDrawing RoomLink");
        for (int i=0; i<roomLinks.length; i++) {
          //System.out.println("\t\troomLinks["+i+"] = " + roomLinks[i]);
          dataManager.drawScreenRectangle(roomLinks[i].toRectangle(), Color.green);
        }
        roomLinks = null;
      }
    }

    //   - We show the mapExits
    if (SHOW_DEBUG) {
      MapExit[] mapExits = room.getMapExits();
      if (mapExits!= null) {
        System.out.println("\tDrawing MapExit");
        for (int i=0; i<mapExits.length; i++) {
          //System.out.println("\t\tmapExits["+i+"] = " + mapExits[i]);
          dataManager.drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
        }
        mapExits = null;
      }
    }

    //   - We add visual properties to the player (shadows...)
    if (SHOW_DEBUG)
      System.out.println("Player init visual properties");
    myPlayer.initVisualProperties(gDirector);

    //   - We show some informations on the screen
    String[] strTemp = { myPlayer.getFullPlayerName() };
    MultiLineText mltPlayerName = new MultiLineText(strTemp, 10, 10, Color.black, 15.0f, "Lblack.ttf", ImageLibRef.TEXT_PRIORITY, MultiLineText.LEFT_ALIGNMENT);
    gDirector.addDrawable(mltPlayerName);

    String[] strTemp2 = { room.getFullName() };
    mltLocationName = new MultiLineText(strTemp2, 10, 10, Color.black, 15.0f, "Lblack.ttf", ImageLibRef.TEXT_PRIORITY, MultiLineText.RIGHT_ALIGNMENT);
    gDirector.addDrawable(mltLocationName);

    //  - We add eventual doors...
      Room rooms[] = imap.getRooms();

        // Init doors state
        for( int r=0; r<rooms.length; r++ ) {
             Door doors[] = rooms[r].getDoors();
             for( int d=0; d<doors.length; d++ )
                doors[d].clean();
        }

        // Display doors
        for( int r=0; r<rooms.length; r++ ) {
             Door doors[] = rooms[r].getDoors();

             for( int d=0; d<doors.length; d++ )
                  if( !doors[d].isDisplayed() ) {
                      gDirector.addDrawable( doors[d].getDoorDrawable() );
                      doors[d].setIsDisplayed(true);
                  }
        }

    // - We declare ourselves to other players...
    dataManager.sendMessage( new EnteringRoomMessage(myPlayer.getPrimaryKey(), myPlayer.getLocation(),
                                 myPlayer.getX(), myPlayer.getY(), (float)myPlayer.getAngle() ) );

    //   - We play music
    String midiFile = imap.getMusicName();
    if (midiFile != null)
      SoundLibrary.getSoundLibrary().playMusic( midiFile );
    
    //   - We retreive other players informations
    if( dataManager.isAlive() ) {
      if (SHOW_DEBUG)
    	  System.out.println("DATAMANAGER ALIVE !!!");
      if (SHOW_DEBUG)
        System.out.println("Sending final AllDataLeftMessage");    	  
      dataManager.sendMessage(new AllDataLeftPleaseMessage());
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** canChangeMap is set to true if player can change its MapData<br>
   * called by wotlas.client.message.YouCanLeaveMapMessage
   */
  /*public void canChangeMapLocation( boolean canChangeMap ) {
    synchronized( changeMapLock ) {
      if (SHOW_DEBUG)
        System.out.println("NOTIFYING");
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

    if ( currentInteriorMapID != myPlayer.getLocation().getInteriorMapID() ) {
      if (DataManager.SHOW_DEBUG)
        System.out.println("LOCATION HAS CHANGED in InteriorMapData");
        
      Debug.signal( Debug.NOTICE, null, "LOCATION HAS CHANGED in InteriorMapData");

      dataManager.getPlayers().clear();
      dataManager.cleanInteriorMapData(); // suppress drawables, shadows, data
      dataManager.getChatPanel().reset();

      //myPlayer.setPosition( new ScreenPoint(myPlayer.getX(), myPlayer.getY()) );
      
      //  - We clean eventual doors data...
      Room rooms[] = imap.getRooms();

      for( int r=0; r<rooms.length; r++ ) {
        Door doors[] = rooms[r].getDoors();
        for( int d=0; d<doors.length; d++ )
          doors[d].clean();
      }

      dataManager.changeMapData();
      return;
    }

    if (currentRoomID != myPlayer.getLocation().getRoomID() ) {
      Debug.signal( Debug.NOTICE, null, "ROOM HAS CHANGED in InteriorMapData");
      currentRoomID = myPlayer.getLocation().getRoomID();
      Room room = myPlayer.getMyRoom();

      couldBeMovingToAnotherRoom = true;

      // We must reset the room
      resetRoom = true;
    }

    Room myRoom = dataManager.getWorldManager().getRoom( myPlayer.getLocation() );

    // I - ROOMLINK INTERSECTION UPDATE ( is the player moving to another room ? )
    RoomLink rl = myRoom.isIntersectingRoomLink( myPlayer.getCurrentRectangle() );

    // is there a Door ?
    if ( rl!=null && rl.getDoor()!=null ) {
      if ( !rl.getDoor().isOpened()
            && !rl.getDoor().canMove(myPlayer.getCurrentRectangle(),
                                        myPlayer.getEndPosition() ) ) {
        myPlayer.stopMovement();
      }
    }

    // Moving to another Room ?
    if ( rl!=null && !couldBeMovingToAnotherRoom ) {
      // Player is intersecting a RoomLink
         latestRoomLink = rl;
         couldBeMovingToAnotherRoom = true;
    } else if ( rl==null && couldBeMovingToAnotherRoom ) {
      // ok, no intersection now, are we in an another room ?
      couldBeMovingToAnotherRoom = false;

      int newRoomID;
      if (!resetRoom) {
        newRoomID = myRoom.isInOtherRoom( latestRoomLink, myPlayer.getCurrentRectangle() );
      } else {
        newRoomID = myRoom.getRoomID();
        //System.out.println("Net congestion => resetting the room");
        resetRoom = false;
      }

      if ( newRoomID>=0 ) {
        // Ok, we move to this new Room
        WotlasLocation location = myPlayer.getLocation();
        location.setRoomID( newRoomID );
        currentRoomID = newRoomID;
        myPlayer.setLocation(location);
        Room room = myPlayer.getMyRoom();

/* NETMESSAGE */
        if (SHOW_DEBUG)
          System.out.println("dataManager.sendMessage( new EnteringRoomMessage(...) )");
        dataManager.sendMessage( new EnteringRoomMessage(myPlayer.getPrimaryKey(), myPlayer.getLocation(),
                                                         myPlayer.getX(), myPlayer.getY(),
                                                         (float)myPlayer.getAngle() ) );

        if (SHOW_DEBUG)
          System.out.println("Changing main ChatRoom");

        dataManager.getChatPanel().reset();
        dataManager.getChatPanel().changeMainJChatRoom(room.getShortName());

        String[] strTemp = { room.getFullName() };
        mltLocationName.setText(strTemp);

        if (SHOW_DEBUG)
          System.out.print("Move to another room : " + newRoomID + " -> " + room.getFullName());

        if (SHOW_DEBUG) {
          RoomLink[] roomLinks = room.getRoomLinks();
          if (roomLinks != null) {
            for (int i=0; i<roomLinks.length; i++) {
              dataManager.drawScreenRectangle(roomLinks[i].toRectangle(), Color.green);
            }
            roomLinks = null;
          }
        }

        if (SHOW_DEBUG) {
          MapExit[] mapExits = room.getMapExits();
          if (mapExits!= null) {
            for (int i=0; i<mapExits.length; i++) {
              dataManager.drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
            }
            mapExits = null;
          }
        }

      } else {
        /*if (SHOW_DEBUG)
          System.out.println("We are still in the same room" + newRoomID);
          */
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

        myPlayer.getMovementComposer().resetMovement();

/* NETMESSAGE */
        if (isNotMovingToAnotherMap) {
          isNotMovingToAnotherMap = false;
          myPlayer.sendMessage( new CanLeaveIntMapMessage( myPlayer.getPrimaryKey(),
                                        mapExit.getTargetWotlasLocation(),
                                        mapExit.getTargetPosition().x, mapExit.getTargetPosition().y,
                                        mapExit.getTargetOrientation() ) );
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


  