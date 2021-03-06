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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import wotlas.client.screen.JClientScreen;
import wotlas.common.ImageLibRef;
import wotlas.common.message.description.AllDataLeftPleaseMessage;
import wotlas.common.message.movement.CanLeaveTownMapMessage;
import wotlas.common.universe.Building;
import wotlas.common.universe.MapExit;
import wotlas.common.universe.TownMap;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2d.BinaryMask;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.ImageLibraryException;
import wotlas.libs.graphics2d.drawable.MotionlessSprite;
import wotlas.libs.graphics2d.drawable.MultiLineText;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;
import wotlas.utils.ScreenPoint;

public class TownMapData implements MapData {

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
    private int currentTownMapID = -1;

    /*------------------------------------------------------------------------------------*/

    /** Set to true to show debug information
     */
    public void showDebug(boolean value) {
        TownMapData.SHOW_DEBUG = value;
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
            System.out.println("-- initDisplay in TownMapData --");

        ImageIdentifier backgroundImageID = null; // background image identifier
        Drawable background = null; // background image

        GraphicsDirector gDirector = dataManager.getGraphicsDirector();

        // 0 - Some inits...
        myPlayer.init();

        // 1 - We load the TownMap
        WotlasLocation location = myPlayer.getLocation();

        this.currentTownMapID = location.getTownMapID();

        TownMap townMap = dataManager.getWorldManager().getTownMap(location);

        if (TownMapData.SHOW_DEBUG) {
            System.out.println("TownMap");
            System.out.println("\tfullName = " + townMap.getFullName());
            System.out.println("\tshortName = " + townMap.getShortName());
        }

        dataManager.getClientScreen().getChatPanel().changeMainJChatRoom(townMap.getShortName());

        dataManager.addPlayer(myPlayer);

        // 2 - We set player's position if his position is incorrect

        // 3 - We load the image
        backgroundImageID = townMap.getTownImage();
        gDirector.getImageLibrary().loadImage(backgroundImageID);

        if (TownMapData.SHOW_DEBUG)
            System.out.println("\tbackgroundImageID = " + backgroundImageID);

        background = new MotionlessSprite(0, // ground x=0
        0, // ground y=0
        backgroundImageID, // image
        ImageLibRef.MAP_PRIORITY // priority
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

        // 5 - We initialize the AStar algo
        myPlayer.getMovementComposer().setMovementMask(BinaryMask.create(bufIm), 5, 1);
        myPlayer.getMovementComposer().resetMovement();
        bufIm.flush(); // free image resource

        // 6 - Init the GraphicsDirector
        gDirector.init(background, // background drawable
        myPlayer.getDrawable(), // reference for screen movements
        new Dimension(JClientScreen.leftWidth, JClientScreen.mapHeight) // screen default dimension
        );

        // 7 - We add buildings' images
        Building buildings[] = townMap.getBuildings();

        if (buildings != null) {

            if (TownMapData.SHOW_DEBUG)
                System.out.println("\tDrawing Buildings");

            ImageIdentifier buildingImageID = null; // building image identifier
            Drawable buildingImage = null; // building image

            for (int i = 0; i < buildings.length; i++) {
                buildingImageID = buildings[i].getSmallBuildingImage();

                if (buildings[i].getX() < -5)
                    continue; // we don't display buildings that are out of the screen

                Rectangle position = buildings[i].toRectangle();
                buildingImage = new MotionlessSprite(position.x, position.y, buildingImageID, // image
                ImageLibRef.MAP_PRIORITY // priority
                );
                gDirector.addDrawable(buildingImage);
            }
        }

        // 8 - We add MapExits' images
        if (TownMapData.SHOW_DEBUG) {
            MapExit[] mapExits = townMap.getMapExits();
            if (mapExits != null) {
                if (TownMapData.SHOW_DEBUG)
                    System.out.println("\tDrawing MapExits");
                for (int i = 0; i < mapExits.length; i++) {
                    //System.out.println("\t\tmapExits["+i+"] = " + mapExits[i]);
                    dataManager.drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
                }
            }
        }

        // 9 - We show some informations on the screen
        gDirector.addDrawable(myPlayer.getGameScreenFullPlayerName());

        String[] strTemp2 = { townMap.getFullName() };
        MultiLineText mltLocationName = new MultiLineText(strTemp2, 10, 10, Color.black, 15.0f, "Lucida Blackletter Regular", ImageLibRef.TEXT_PRIORITY, MultiLineText.RIGHT_ALIGNMENT);
        gDirector.addDrawable(mltLocationName);

        // 10 - We play music
        String midiFile = townMap.getMusicName();
        if (midiFile != null)
            SoundLibrary.getMusicPlayer().playMusic(midiFile);

        // 11 - We retrieve eventual remaining data
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

        if ((this.currentTownMapID != myPlayer.getLocation().getTownMapID()) || (myPlayer.getLocation().getBuildingID() > -1)) {
            if (DataManager.SHOW_DEBUG)
                System.out.println("LOCATION HAS CHANGED in TownMapData");

            Debug.signal(Debug.NOTICE, null, "LOCATION HAS CHANGED in TownMapData");

            this.dataManager.getPlayers().clear();
            this.dataManager.cleanInteriorMapData();
            this.dataManager.getClientScreen().getChatPanel().reset();
            this.dataManager.changeMapData();
            return;
        }

        TownMap townMap = this.dataManager.getWorldManager().getTownMap(myPlayer.getLocation());

        // I - MAPEXIT INTERSECTION UPDATE ( is the player moving to a world map ? )
        Point destination = myPlayer.getEndPosition();
        MapExit mapExit = townMap.isIntersectingMapExit(destination.x, destination.y, myPlayer.getCurrentRectangle());

        if (mapExit != null) {
            // Ok, we are going to a world map...
            if (TownMapData.SHOW_DEBUG)
                System.out.println("We are going to a world map...");

            myPlayer.getMovementComposer().resetMovement();

            if (this.isNotMovingToAnotherMap) {
                this.isNotMovingToAnotherMap = false;
                myPlayer.sendMessage(new CanLeaveTownMapMessage(myPlayer.getPrimaryKey(), mapExit.getTargetWotlasLocation(), mapExit.getTargetPosition().x, mapExit.getTargetPosition().y, mapExit.getTargetOrientation()));
            }
        }

        // II - BUILDING INTERSECTION UPDATE ( is the player entering a building ? )
        /*Building buildingMap = townMap.isEnteringBuilding( destination.x,
                                                           destination.y,
                                                           myPlayer.getCurrentRectangle() );*/
        Building buildingMap = townMap.isEnteringBuilding(myPlayer.getX(), myPlayer.getY(), myPlayer.getCurrentRectangle());

        if (buildingMap != null) {
            // intersection with a Building, which MapExit are we using ?
            if (TownMapData.SHOW_DEBUG)
                System.out.println("We are entering a building...");

            myPlayer.getMovementComposer().resetMovement();

            if (TownMapData.SHOW_DEBUG) {
                System.out.println("\t\tbuildingMap.getFullName() = " + buildingMap.getFullName());
                System.out.println("\t\tbuildingMap.getShortName() = " + buildingMap.getShortName());
                //System.out.print("\t\tmyPlayer.getAngle() = ");
                //System.out.println(myPlayer.getAngle()*180/Math.PI);
                //System.out.println("cosinus = " + Math.cos(myPlayer.getAngle()));
                //System.out.println("sinus = " + Math.sin(myPlayer.getAngle()));
            }

            mapExit = buildingMap.findTownMapExit(myPlayer.getAngle());

            if (TownMapData.SHOW_DEBUG) {
                System.out.println("Which MapExit are we using ?");
                System.out.println("\t\tmapExit.getType() = " + (int) mapExit.getType());
                System.out.print("\t\tmapExit.getMapExitSide() = ");
            }

            if (TownMapData.SHOW_DEBUG) {
                System.out.println("\t\tmapExit.getTargetWotlasLocation() = " + mapExit.getTargetWotlasLocation());
                System.out.println("\t\tmapExit.getMapExitLocation() = " + mapExit.getMapExitLocation());
            }

            if (this.isNotMovingToAnotherMap) {
                this.isNotMovingToAnotherMap = false;

                // New Position
                ScreenPoint newPos = mapExit.getInsertionPoint();

                myPlayer.sendMessage(new CanLeaveTownMapMessage(myPlayer.getPrimaryKey(), mapExit.getMapExitLocation(), newPos.x, newPos.y, mapExit.getLocalOrientation()));
            }
        }
    }

    /*------------------------------------------------------------------------------------*/

}
