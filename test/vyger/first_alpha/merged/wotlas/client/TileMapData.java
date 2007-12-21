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
import java.awt.Point;
import wotlas.common.ImageLibRef;
import wotlas.common.environment.EnvironmentManager;
import wotlas.common.message.description.AllDataLeftPleaseMessage;
import wotlas.common.message.movement.CanLeaveTileMapMessage;
import wotlas.common.universe.MapExit;
import wotlas.common.universe.TileMap;
import wotlas.common.universe.WotlasLocation;
import wotlas.editor.EditorDataManager;
import wotlas.editor.EditorPlugIn;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.drawable.MultiLineText;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;

/**  TileMapData 
  *
  * @author ??? who started TownMapData, Diego
 */
public class TileMapData implements MapData {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

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
    private int currentTileMapID = -1;

    /*------------------------------------------------------------------------------------*/

    /** Set to true to show debug information
     */
    public void showDebug(boolean value) {
        TileMapData.SHOW_DEBUG = value;
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
            System.out.println("-- initDisplay in TileMapData --");

        GraphicsDirector gDirector = dataManager.getGraphicsDirector();

        // 0 - Some inits...
        myPlayer.init();

        // 1 - We load the TileMap
        WotlasLocation location = myPlayer.getLocation();

        this.currentTileMapID = location.getTileMapID();

        TileMap tileMap = dataManager.getWorldManager().getTileMap(location);
        tileMap.initGraphicSet(gDirector);
        EnvironmentManager.initGraphics(gDirector);

        if (TileMapData.SHOW_DEBUG) {
            System.out.println("TileMap");
            System.out.println("\tfullName = " + tileMap.getFullName());
            System.out.println("\tshortName = " + tileMap.getShortName());
        }

        dataManager.getClientScreen().getChatPanel().changeMainJChatRoom(tileMap.getShortName());

        dataManager.addPlayer(myPlayer); // should be changed
        // dataManager.addScreenObjects(myPlayer.getScreenObject()); // should be changed

        // 2 - We set player's position if his position is incorrect

        // 3 - preInit the GraphicsDirector : reset it...
        //    gDirector.preTileMapInitWithPlayer( myPlayer.getBasicChar().getDrawableForTileMaps(myPlayer),tileMap.getMapFullSize() );
        gDirector.preTileMapInitWithPlayer(myPlayer.getDrawable(), tileMap.getMapFullSize());

        // 4 - We load the background tile and create the background
        tileMap.drawAllLayer(gDirector);

        // 5 - We load the mask

        // 6 - We initialize the AStar algo
        // myPlayer.getMovementComposer().setMovementMask( tileMap.getManager().getMapMask()
        // , 5, 1 );
        myPlayer.getMovementComposer().setMovementMask(tileMap.getManager().getMapMask()
        //        , tileMap.getMapTileDim().height, 1 );  
        , tileMap.getMapTileDim().height, 1);
        myPlayer.getMovementComposer().resetMovement();

        gDirector.tileMapInit(tileMap.getMapFullSize());

        // 7 - We add buildings' images

        // 8 - We add MapExits' images
        MapExit[] mapExits = tileMap.getManager().getMapExits();
        if (mapExits != null) {
            if (TileMapData.SHOW_DEBUG)
                System.out.println("\tDrawing MapExits");
            for (int i = 0; i < mapExits.length; i++)
                dataManager.drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
        }

        // 9 - We show some informations on the screen
        gDirector.addDrawable(myPlayer.getGameScreenFullPlayerName());

        String[] strTemp2 = { tileMap.getFullName() };
        MultiLineText mltLocationName = new MultiLineText(strTemp2, 10, 10, Color.black, 15.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, MultiLineText.RIGHT_ALIGNMENT);
        gDirector.addDrawable(mltLocationName);

        // 10 - We play music
        String midiFile = tileMap.getMusicName();
        if (midiFile != null)
            SoundLibrary.getMusicPlayer().playMusic(midiFile);

        // 11 - We retrieve eventual remaining data
        dataManager.sendMessage(new AllDataLeftPleaseMessage());
    }

    /** To init the display editor<br>
     * - load background and mask images<br>
     * - init the AStar algorithm
     * - init the Graphics Director
     * - show the other images (shadows, buildings, towns...)
     */
    public void initDisplayEditor(EditorDataManager editorDataManager, WotlasLocation location) {
        TileMap tileMap = editorDataManager.getWorldManager().getTileMap(location);
        System.out.println("" + location);
        GraphicsDirector gDirector = editorDataManager.getGraphicsDirector();
        EditorPlugIn.rememberTheGDirector(gDirector);
        gDirector.preTileMapInit(tileMap.getMapFullSize());
        tileMap.initGraphicSet(gDirector);
        tileMap.drawAllLayer(gDirector);
        gDirector.tileMapInit(tileMap.getMapFullSize());
        // 8 - We add MapExits' images
        MapExit[] mapExits = tileMap.getManager().getMapExits();
        if (mapExits != null) {
            for (int i = 0; i < mapExits.length; i++)
                editorDataManager.drawScreenRectangle(mapExits[i].toRectangle(), Color.yellow);
        }
        String[] strTemp2 = { tileMap.getFullName() };
        MultiLineText mltLocationName = new MultiLineText(strTemp2, 10, 10, Color.black, 15.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, MultiLineText.RIGHT_ALIGNMENT);
        gDirector.addDrawable(mltLocationName);
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
        if ((this.currentTileMapID != myPlayer.getLocation().getTileMapID()) || (myPlayer.getLocation().getBuildingID() > -1)) {
            if (DataManager.SHOW_DEBUG)
                System.out.println("LOCATION HAS CHANGED in TileMapData");

            Debug.signal(Debug.NOTICE, null, "LOCATION HAS CHANGED in TileMapData");

            this.dataManager.getPlayers().clear();
            this.dataManager.getScreenObjects().clear();
            this.dataManager.cleanInteriorMapData();
            this.dataManager.getClientScreen().getChatPanel().reset();
            this.dataManager.changeMapData();
            return;
        }

        TileMap tileMap = this.dataManager.getWorldManager().getTileMap(myPlayer.getLocation());

        // I - MAPEXIT INTERSECTION UPDATE ( is the player moving to a world map ? )
        Point destination = myPlayer.getEndPosition();
        MapExit mapExit = tileMap.isIntersectingMapExit(destination.x, destination.y, myPlayer.getCurrentRectangle());

        if (mapExit != null) {
            // Ok, we are going to a world map...
            if (TileMapData.SHOW_DEBUG)
                System.out.println("We are going to a world map...");

            myPlayer.getMovementComposer().resetMovement();

            if (this.isNotMovingToAnotherMap) {
                this.isNotMovingToAnotherMap = false;
                myPlayer.sendMessage(new CanLeaveTileMapMessage(myPlayer.getPrimaryKey(), mapExit.getTargetWotlasLocation(), mapExit.getTargetPosition().x, mapExit.getTargetPosition().y, mapExit.getTargetOrientation()));
            }
        }

        // II - BUILDING INTERSECTION UPDATE ( is the player entering a building ? )

    }
}