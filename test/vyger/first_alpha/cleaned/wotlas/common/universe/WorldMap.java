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

package wotlas.common.universe;

import java.awt.Rectangle;
import wotlas.common.WorldManager;
import wotlas.common.router.MessageRouter;
import wotlas.common.router.MessageRouterFactory;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.utils.Debug;
import wotlas.utils.ScreenPoint;

/** A WorldMap represents the root class of a whole world of our Game Universe.
 *
 * @author Petrus, Aldiss, Diego
 * @see wotlas.common.universe.TownMap
 */

public class WorldMap implements WotlasMap {

    /*------------------------------------------------------------------------------------*/

    /** ID of the World (index in the worldmap array in the worldmanager)
     */
    private int worldMapID;

    /** Full name of the World
     */
    private String fullName;

    /** Short name of the World
     */
    private String shortName;

    /** Full Image (identifier) of this world
     */
    private ImageIdentifier worldImage;

    /** Point of insertion (teleportation, arrival)
     */
    private ScreenPoint insertionPoint;

    /** Music Name
     */
    private String musicName;

    /*------------------------------------------------------------------------------------*/

    /** Array of TownMap
     */
    private transient TownMap[] townMaps;

    /** Array of TileMap
     */
    private transient TileMap[] tileMaps;

    /** Our message router. Owns the list of players of this map (not in Towns).
     */
    private transient MessageRouter messageRouter;

    /*------------------------------------------------------------------------------------*/

    /**
     * Constructor
     */
    public WorldMap() {
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /*
     * List of setter and getter used for persistence
     */

    public void setWorldMapID(int myWorldMapID) {
        this.worldMapID = myWorldMapID;
    }

    public int getWorldMapID() {
        return this.worldMapID;
    }

    public void setFullName(String myFullName) {
        this.fullName = myFullName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setShortName(String myShortName) {
        this.shortName = myShortName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setInsertionPoint(ScreenPoint myInsertionPoint) {
        this.insertionPoint = myInsertionPoint;
    }

    public ScreenPoint getInsertionPoint() {
        return new ScreenPoint(this.insertionPoint);
    }

    public void setWorldImage(ImageIdentifier worldImage) {
        this.worldImage = worldImage;
    }

    public ImageIdentifier getWorldImage() {
        return this.worldImage;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicName() {
        return this.musicName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Transient fields getter & setter
     */
    public void setTownMaps(TownMap[] myTownMaps) {
        this.townMaps = myTownMaps;
    }

    public TownMap[] getTownMaps() {
        return this.townMaps;
    }

    public MessageRouter getMessageRouter() {
        return this.messageRouter;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get a TileMap by its ID.
     *
     * @param id tileMapID
     * @return corresponding tileMap, null if ID does not exist.
     */
    public TileMap getTileMapFromID(int id) {
        if (id >= this.tileMaps.length || id < 0) {
            Debug.signal(Debug.ERROR, this, "getTileMapFromID : Bad tileMap ID " + id);
            return null;
        }

        return this.tileMaps[id];
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get a Town by its ID.
     *
     * @param id townMapID
     * @return corresponding townMap, null if ID does not exist.
     */
    public TownMap getTownMapFromID(int id) {
        if (id >= this.townMaps.length || id < 0) {
            Debug.signal(Debug.ERROR, this, "getTownMapFromID : Bad town ID " + id);
            return null;
        }

        return this.townMaps[id];
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new TownMap object to the array {@link #townMaps townMaps}
     *
     * @param town TownMap object to add
     */
    public void addTownMap(TownMap town) {
        if (this.townMaps == null) {
            this.townMaps = new TownMap[town.getTownMapID() + 1];
        } else if (this.townMaps.length <= town.getTownMapID()) {
            TownMap[] myTownMaps = new TownMap[town.getTownMapID() + 1];
            System.arraycopy(this.townMaps, 0, myTownMaps, 0, this.townMaps.length);
            this.townMaps = myTownMaps;
        }

        this.townMaps[town.getTownMapID()] = town;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new TownMap object to the array {@link #townMaps townMaps}
     *
     * @return a new TownMap object
     */
    public TownMap addNewTownMap() {
        TownMap myTownMap = new TownMap();

        if (this.townMaps == null) {
            this.townMaps = new TownMap[1];
            myTownMap.setTownMapID(0);
            this.townMaps[0] = myTownMap;
        } else {
            TownMap[] myTownMaps = new TownMap[this.townMaps.length + 1];
            myTownMap.setTownMapID(this.townMaps.length);
            System.arraycopy(this.townMaps, 0, myTownMaps, 0, this.townMaps.length);
            myTownMaps[this.townMaps.length] = myTownMap;
            this.townMaps = myTownMaps;
        }

        return myTownMap;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the wotlas location associated to this Map.
     *  @return associated Wotlas Location
     */
    public WotlasLocation getLocation() {
        return new WotlasLocation(this.worldMapID);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this world ( it rebuilds shortcuts ). This method calls the init() method
     *  of the TownMaps. You must only call this method when ALL the world data has been
     *  loaded.
     */
    public void init() {

        // 1 - any data ?
        if (this.townMaps == null) {
            Debug.signal(Debug.WARNING, this, "WorldMap init failed: No Towns.");
        }
        if (this.tileMaps == null) {
            Debug.signal(Debug.WARNING, this, "WorldMap init failed: No TileMap.");
        }
        if (this.townMaps == null && this.tileMaps == null) {
            return;
        }

        // 2 - we transmit the init() call
        for (int i = 0; i < this.townMaps.length; i++)
            if (this.townMaps[i] != null)
                this.townMaps[i].init(this);
        for (int i = 0; i < this.tileMaps.length; i++)
            if (this.tileMaps[i] != null)
                this.tileMaps[i].init(this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this worldmap for message routing. We create an appropriate message router
     *  for the world via the provided factory.
     *
     *  Don't call this method yourself it's called from the WorldManager !
     *
     * @param msgRouterFactory our router factory
     */
    public void initMessageRouting(MessageRouterFactory msgRouterFactory, WorldManager wManager) {
        // build/get our router
        this.messageRouter = msgRouterFactory.createMsgRouterForWorldMap(this, wManager);

        // we transmit the call to other layers
        for (int i = 0; i < this.townMaps.length; i++)
            if (this.townMaps[i] != null)
                this.townMaps[i].initMessageRouting(msgRouterFactory, wManager);
        for (int i = 0; i < this.tileMaps.length; i++)
            if (this.tileMaps[i] != null)
                this.tileMaps[i].initMessageRouting(msgRouterFactory, wManager);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** String Info.
     */
    @Override
    public String toString() {
        return "World - " + this.fullName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Tests if the given player rectangle has its x,y cordinates in a TileMapRectangle
     *
     * @param destX destination x position of the player movement ( endPoint of path )
     * @param destY destination y position of the player movement ( endPoint of path )
     * @param rCurrent rectangle containing the player's current position, width & height
     * @return the TileMap the player is heading to (if he has reached it, or if there
     *         are any), null if none.
     */
    public TileMap isEnteringTileMap(int destX, int destY, Rectangle rCurrent) {
        if (this.tileMaps == null)
            return null;

        for (int i = 0; i < this.tileMaps.length; i++) {
            Rectangle tileMapRect = this.tileMaps[i].toRectangle();

            if (tileMapRect.contains(destX, destY) && tileMapRect.intersects(rCurrent))
                return this.tileMaps[i]; // ileMap reached
        }

        return null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Tests if the given player rectangle has its x,y cordinates in a TownRectangle
     *
     * @param destX destination x position of the player movement ( endPoint of path )
     * @param destY destination y position of the player movement ( endPoint of path )
     * @param rCurrent rectangle containing the player's current position, width & height
     * @return the TownMap the player is heading to (if he has reached it, or if there
     *         are any), null if none.
     */
    public TownMap isEnteringTown(int destX, int destY, Rectangle rCurrent) {
        if (this.townMaps == null)
            return null;

        for (int i = 0; i < this.townMaps.length; i++) {
            Rectangle townRect = this.townMaps[i].toRectangle();

            if (townRect.contains(destX, destY) && townRect.intersects(rCurrent))
                return this.townMaps[i]; // town reached
        }

        return null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new TileMap object to the array {@link #tileMaps tileMaps}
     *
     * @param tileMap TileMap object to add
     */
    public void addTileMap(TileMap tileMap) {
        if (this.tileMaps == null) {
            this.tileMaps = new TileMap[tileMap.getTileMapID() + 1];
        } else if (this.tileMaps.length <= tileMap.getTileMapID()) {
            TileMap[] myTileMaps = new TileMap[tileMap.getTileMapID() + 1];
            System.arraycopy(this.tileMaps, 0, myTileMaps, 0, this.tileMaps.length);
            this.tileMaps = myTileMaps;
        }

        this.tileMaps[tileMap.getTileMapID()] = tileMap;
    }

    /** Add a new TileMap object to the array {@link #tileMaps tileMaps}
     *
     * @return a new TileMap object
     */
    public TileMap addNewTileMap() {
        TileMap myTileMap = new TileMap();

        if (this.tileMaps == null) {
            this.tileMaps = new TileMap[1];
            myTileMap.setTileMapID(0);
            this.tileMaps[0] = myTileMap;
        } else {
            TileMap[] myTileMaps = new TileMap[this.tileMaps.length + 1];
            myTileMap.setTileMapID(this.tileMaps.length);
            System.arraycopy(this.tileMaps, 0, myTileMaps, 0, this.tileMaps.length);
            myTileMaps[this.tileMaps.length] = myTileMap;
            this.tileMaps = myTileMaps;
        }

        return myTileMap;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Transient fields getter & setter
     */
    public void setTileMaps(TileMap[] myTileMaps) {
        this.tileMaps = myTileMaps;
    }

    public TileMap[] getTileMaps() {
        return this.tileMaps;
    }

}
