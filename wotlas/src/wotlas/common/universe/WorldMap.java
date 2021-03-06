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
 * @author Petrus, Aldiss
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
            return;
        }

        // 2 - we transmit the init() call
        for (int i = 0; i < this.townMaps.length; i++)
            if (this.townMaps[i] != null)
                this.townMaps[i].init(this);
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
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** String Info.
     */
    @Override
    public String toString() {
        return "World - " + this.fullName;
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
}
