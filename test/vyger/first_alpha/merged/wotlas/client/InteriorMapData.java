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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import wotlas.client.screen.JClientScreen;
import wotlas.common.ImageLibRef;
import wotlas.common.message.description.AllDataLeftPleaseMessage;
import wotlas.common.message.movement.CanLeaveIntMapMessage;
import wotlas.common.message.movement.EnteringRoomMessage;
import wotlas.common.universe.Door;
import wotlas.common.universe.InteriorMap;
import wotlas.common.universe.MapExit;
import wotlas.common.universe.Room;
import wotlas.common.universe.RoomLink;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2d.BinaryMask;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.GrayMask;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.ImageLibraryException;
import wotlas.libs.graphics2d.drawable.MultiLineText;
import wotlas.libs.graphics2d.drawable.MultiRegionImage;
import wotlas.libs.graphics2d.filter.BrightnessFilter;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;
import wotlas.utils.ScreenPoint;

public class InteriorMapData implements MapData {

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
        InteriorMapData.SHOW_DEBUG = value;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set isNotMovingToAnotherMap
     */
    public void setIsNotMovingToAnotherMap(boolean value) {
        this.isNotMovingToAnotherMap = value;
    }

    /*------------------------------------------------------------------------------------*/

    /** To init the display<br>
     * - load background and mask images<br>
     * - init the AStar algorithm
     * - init the Graphics Director
     * - show the other images (shadows, buildings, towns...)
     */
    public void initDisplay(PlayerImpl myPlayer, DataManager dataManager) {

        this.dataManager = dataManager;

        if (DataManager.SHOW_DEBUG)
            System.out.println("-- initDisplay in InteriorMapData --");

        ImageIdentifier backgroundImageID = null; // background image identifier
        Drawable background = null; // background image

        GraphicsDirector gDirector = dataManager.getGraphicsDirector();

        // 0 - Some inits...
        myPlayer.init();

        // 1 - We load the InteriorMap
        WotlasLocation location = myPlayer.getLocation();
        this.currentInteriorMapID = location.getInteriorMapID();
        this.currentRoomID = location.getRoomID();

        this.imap = dataManager.getWorldManager().getInteriorMap(location);

        if (InteriorMapData.SHOW_DEBUG) {
            System.out.println("InteriorMap");
            System.out.println("\tfullName = " + this.imap.getFullName());
            System.out.println("\tshortName = " + this.imap.getShortName());
        }

        // 2 - We load the room ...
        //     ... and set the player's position (if his position is incorrect)
        Room room = dataManager.getWorldManager().getRoom(location);

        if (InteriorMapData.SHOW_DEBUG) {
            System.out.println("Room");
            System.out.println("\tfullName = " + room.getFullName());
            System.out.println("\tshortName = " + room.getShortName());
        }

        dataManager.getClientScreen().getChatPanel().changeMainJChatRoom(room.getShortName());

        if (InteriorMapData.SHOW_DEBUG)
            System.out.println("Adding a new player : " + myPlayer + "to dataManager");

        dataManager.addPlayer(myPlayer);

        if (myPlayer.getX() == -1) {
            ScreenPoint insertionPoint = room.getInsertionPoint();

            if (InteriorMapData.SHOW_DEBUG)
                System.out.println("\tinsertionPoint = " + insertionPoint);

            myPlayer.setX(insertionPoint.x);
            myPlayer.setY(insertionPoint.y);
            myPlayer.setPosition(insertionPoint);
        }

        // 3 - We load the image
        backgroundImageID = this.imap.getInteriorMapImage();

        if (InteriorMapData.SHOW_DEBUG)
            System.out.println("\tbackgroundImageID = " + backgroundImageID);

        background = new MultiRegionImage(myPlayer.getDrawable(), // our reference for image loading
        500, // perception radius
        this.imap.getImageRegionWidth(), // grid deltax
        this.imap.getImageRegionHeight(), // grid deltay
        this.imap.getImageWidth(), // image's total width
        this.imap.getImageHeight(), // image's total height
        this.imap.getInteriorMapImage() // base image identifier
        );

        // 4 - We load the mask
        BufferedImage bufIm = null;

        try {
            ImageIdentifier mapMaskID = gDirector.getImageLibrary().getImageIdentifier(backgroundImageID, "mask");

            if (mapMaskID != null) {
                String maskFile = gDirector.getImageLibrary().getImageFile(mapMaskID);

                if (maskFile != null)
                    bufIm = gDirector.getImageLibrary().loadBufferedImage(maskFile, BufferedImage.TYPE_INT_ARGB);
            }

            if (bufIm == null) {
                Debug.signal(Debug.CRITICAL, this, "Mask not found");
                Debug.exit();
            }
        } catch (ImageLibraryException e) {
            Debug.signal(Debug.CRITICAL, this, "Image Library Corrupted: " + e);
            Debug.exit();
        }

        // 4.1 - We load the mask brightness
        BufferedImage bufIm2 = null;

        try {
            ImageIdentifier brightnessMaskID = gDirector.getImageLibrary().getImageIdentifier(backgroundImageID, "brightness");

            if (brightnessMaskID != null) {
                String brightnessMaskFile = gDirector.getImageLibrary().getImageFile(brightnessMaskID);

                if (brightnessMaskFile != null)
                    bufIm2 = gDirector.getImageLibrary().loadBufferedImage(brightnessMaskFile, BufferedImage.TYPE_INT_ARGB);
            }

            if (bufIm2 == null) {
                Debug.signal(Debug.WARNING, this, "Brightness mask not found");
                BrightnessFilter.setBrightnessMask(null, 10);
            } else {
                BrightnessFilter.setBrightnessMask(GrayMask.create(bufIm2), 10);
                Debug.signal(Debug.NOTICE, this, "Brightness mask found...");
            }
        } catch (ImageLibraryException e) {
            Debug.signal(Debug.CRITICAL, this, "Brightness mask not found: " + e);
            BrightnessFilter.setBrightnessMask(null, 10);
        }

        // 5 - We initialize the AStar algo
        myPlayer.getMovementComposer().setMovementMask(BinaryMask.create(bufIm), 5, 4);
        myPlayer.getMovementComposer().resetMovement();
        bufIm.flush(); // free image resource

        // 6 - We init the GraphicsDirector
        gDirector.init(background, // background drawable
        myPlayer.getDrawable(), // reference for screen movements
        new Dimension(JClientScreen.leftWidth, JClientScreen.mapHeight) // screen default dimension
        );

        // 7 - We show the roomLinks
        if (InteriorMapData.SHOW_DEBUG) {
            RoomLink[] roomLinks = room.getRoomLinks();
            if (roomLinks != null) {
                System.out.println("\tDrawing RoomLink");
                for (int i = 0; i < roomLinks.length; i++) {
                    //System.out.println("\t\troomLinks["+i+"] = " + roomLinks[i]);
                    dataManager.drawScreenRectangle(roomLinks[i].toRectangle(), Color.green);
                }
                roomLinks = null;
            }
        }

        // 8 - We show the mapExits
        if (InteriorMapData.SHOW_DEBUG) {
            MapExit[] mapExits = room.getMapExits();
            if (mapExits != null) {
                System.out.println("\tDrawing MapExit");
                for (int i = 0; i < mapExits.length; i++) {
                    //System.out.println("\t\tmapExits["+i+"] = " + mapExits[i]);
                    dataManager.drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
                }
                mapExits = null;
            }
        }

        // 9 - We add visual properties to the player (shadows...)
        if (InteriorMapData.SHOW_DEBUG)
            System.out.println("Player init visual properties");

        myPlayer.initVisualProperties(gDirector);

        // 10 - We show some informations on the screen
        gDirector.addDrawable(myPlayer.getGameScreenFullPlayerName());

        String[] strTemp2 = { room.getFullName() };
        this.mltLocationName = new MultiLineText(strTemp2, 10, 10, Color.black, 15.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, MultiLineText.RIGHT_ALIGNMENT);
        gDirector.addDrawable(this.mltLocationName);

        // 11 - We add eventual doors...
        Room rooms[] = this.imap.getRooms();

        // Init doors state
        for (int r = 0; r < rooms.length; r++) {
            if (rooms[r] == null)
                continue;

            Door doors[] = rooms[r].getDoors();

            if (doors == null)
                continue;

            for (int d = 0; d < doors.length; d++)
                doors[d].clean();
        }

        // Display doors
        for (int r = 0; r < rooms.length; r++) {
            if (rooms[r] == null)
                continue;

            Door doors[] = rooms[r].getDoors();

            if (doors == null)
                continue;

            for (int d = 0; d < doors.length; d++)
                if (!doors[d].isDisplayed()) {
                    gDirector.addDrawable(doors[d].getDoorDrawable());
                    doors[d].setIsDisplayed(true);
                }
        }

        // 12 - We play the map's music
        String midiFile = this.imap.getMusicName();

        if (midiFile != null)
            SoundLibrary.getMusicPlayer().playMusic(midiFile);

        //  13 - We retrieve non-local data ( door state, players, chat info, etc... )
        if (InteriorMapData.SHOW_DEBUG)
            System.out.println("Sending final AllDataLeftMessage...");

        dataManager.sendMessage(new AllDataLeftPleaseMessage());
    }

    /*------------------------------------------------------------------------------------*/

    /** To update the location<br>
     * - test if player is intersecting a screenZone<br>
     * - test if player is entering a new WotlasLocation<br>
     * - change the current MapData
     */
    public void locationUpdate(PlayerImpl myPlayer) {

        if (this.dataManager == null)
            return;

        // Has the currentLocation changed ?

        if (this.currentInteriorMapID != myPlayer.getLocation().getInteriorMapID()) {
            if (DataManager.SHOW_DEBUG)
                System.out.println("LOCATION HAS CHANGED in InteriorMapData");

            Debug.signal(Debug.NOTICE, null, "LOCATION HAS CHANGED in InteriorMapData");

            this.dataManager.getPlayers().clear();
            this.dataManager.cleanInteriorMapData(); // suppress drawables, shadows, data
            this.dataManager.getClientScreen().getChatPanel().reset();

            //  - We clean eventual doors data...
            Room rooms[] = this.imap.getRooms();

            for (int r = 0; r < rooms.length; r++) {
                Door doors[] = rooms[r].getDoors();
                for (int d = 0; d < doors.length; d++)
                    doors[d].clean();
            }

            this.dataManager.changeMapData();
            return;
        }

        if (this.currentRoomID != myPlayer.getLocation().getRoomID()) {
            Debug.signal(Debug.NOTICE, null, "ROOM HAS CHANGED in InteriorMapData");
            this.currentRoomID = myPlayer.getLocation().getRoomID();
            Room room = myPlayer.getMyRoom();

            this.couldBeMovingToAnotherRoom = true;

            // We must reset the room
            this.resetRoom = true;
        }

        Room myRoom = this.dataManager.getWorldManager().getRoom(myPlayer.getLocation());

        // I - ROOMLINK INTERSECTION UPDATE ( is the player moving to another room ? )
        RoomLink rl = myRoom.isIntersectingRoomLink(myPlayer.getCurrentRectangle());

        // is there a Door ?
        if (rl != null && rl.getDoor() != null) {
            if (!rl.getDoor().isOpened() && !rl.getDoor().canMove(myPlayer.getCurrentRectangle(), myPlayer.getEndPosition())) {
                myPlayer.stopMovement();
            }
        }

        // Moving to another Room ?
        if (rl != null && !this.couldBeMovingToAnotherRoom) {
            // Player is intersecting a RoomLink
            this.latestRoomLink = rl;
            this.couldBeMovingToAnotherRoom = true;
        } else if (rl == null && this.couldBeMovingToAnotherRoom) {
            // ok, no intersection now, are we in an another room ?
            this.couldBeMovingToAnotherRoom = false;

            int newRoomID;
            if (!this.resetRoom) {
                newRoomID = myRoom.isInOtherRoom(this.latestRoomLink, myPlayer.getCurrentRectangle());
            } else {
                newRoomID = myRoom.getRoomID();
                //System.out.println("Net congestion => resetting the room");
                this.resetRoom = false;
            }

            if (newRoomID >= 0) {
                // Ok, we move to this new Room
                WotlasLocation location = myPlayer.getLocation();
                location.setRoomID(newRoomID);
                this.currentRoomID = newRoomID;
                myPlayer.setLocation(location);
                Room room = myPlayer.getMyRoom();

                if (InteriorMapData.SHOW_DEBUG)
                    System.out.println("dataManager.sendMessage( new EnteringRoomMessage(...) )");

                this.dataManager.sendMessage(new EnteringRoomMessage(myPlayer.getPrimaryKey(), myPlayer.getLocation(), myPlayer.getX(), myPlayer.getY(), (float) myPlayer.getAngle()));

                if (InteriorMapData.SHOW_DEBUG)
                    System.out.println("Changing main ChatRoom");

                this.dataManager.getClientScreen().getChatPanel().reset();
                this.dataManager.getClientScreen().getChatPanel().changeMainJChatRoom(room.getShortName());

                String[] strTemp = { room.getFullName() };
                this.mltLocationName.setText(strTemp);

                if (InteriorMapData.SHOW_DEBUG)
                    System.out.print("Move to another room : " + newRoomID + " -> " + room.getFullName());

                if (InteriorMapData.SHOW_DEBUG) {
                    RoomLink[] roomLinks = room.getRoomLinks();
                    if (roomLinks != null) {
                        for (int i = 0; i < roomLinks.length; i++) {
                            this.dataManager.drawScreenRectangle(roomLinks[i].toRectangle(), Color.green);
                        }
                        roomLinks = null;
                    }
                }

                if (InteriorMapData.SHOW_DEBUG) {
                    MapExit[] mapExits = room.getMapExits();
                    if (mapExits != null) {
                        for (int i = 0; i < mapExits.length; i++) {
                            this.dataManager.drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
                        }
                        mapExits = null;
                    }
                }

            }
        } // End of part I

        // II - MAPEXIT INTERSECTION UPDATE ( is the player moving to another map ? )
        if (myPlayer.isMoving()) {
            Point destination = myPlayer.getEndPosition();
            MapExit mapExit = myRoom.isIntersectingMapExit(destination.x, destination.y, myPlayer.getCurrentRectangle());
            if (mapExit != null) {
                // Ok, we are going to a new map...
                if (InteriorMapData.SHOW_DEBUG)
                    System.out.println("We are going to a new map...");

                myPlayer.getMovementComposer().resetMovement();

                if (this.isNotMovingToAnotherMap) {
                    this.isNotMovingToAnotherMap = false;
                    myPlayer.sendMessage(new CanLeaveIntMapMessage(myPlayer.getPrimaryKey(), mapExit.getTargetWotlasLocation(), mapExit.getTargetPosition().x, mapExit.getTargetPosition().y, mapExit.getTargetOrientation()));
                }
            }
        } // End of part II
    }

    /*------------------------------------------------------------------------------------*/

}
