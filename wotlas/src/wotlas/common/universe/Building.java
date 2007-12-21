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

import java.awt.Point;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.utils.Debug;
import wotlas.utils.ScreenRectangle;

/** A Building of a town in our World. A building always belongs to a townMap.
 *
 * @author Petrus, Aldiss
 * @see wotlas.common.universe.TownMap
 * @see wotlas.common.universe.MapExit
 */

public class Building extends ScreenRectangle {
    /*------------------------------------------------------------------------------------*/

    /** True if we show debug informations
     */
    public static boolean SHOW_DEBUG = false;
    
    /** Server ID not initialized : no gateway to remote server */
    public static int SERVER_ID_NONE = 0;

    /*------------------------------------------------------------------------------------*/

    /** ID of the Building
     */
    private int buildingID;

    /** Full name of the Building
     */
    private String fullName;

    /** Short name of the Building
     */
    private String shortName;

    /** Server ID of the server that possesses this Building
     */
    private int serverID;

    /** is true if the Building has some TownExit
     */
    private boolean hasTownExits;

    /** is true if the Building has some BuildingExit
     */
    private boolean hasBuildingExits;

    /** Small Image (identifier) of this building for TownMaps.
     */
    private ImageIdentifier smallBuildingImage;

    /*------------------------------------------------------------------------------------*/

    /** A link to our father town...
     */
    private transient TownMap myTownMap;

    /** Our interior maps.
     */
    private transient InteriorMap[] interiorMaps;

    /** Map exits that are building exits...
     */
    private transient MapExit[] buildingExits;

    /** Map exits that are town exits.
     */
    private transient MapExit[] townExits;

    /*------------------------------------------------------------------------------------*/

    /** Constructor for persistence.
     */
    public Building() {
        this.serverID = Building.SERVER_ID_NONE; // Not initialized.
        this.hasBuildingExits = false; // default
        this.hasTownExits = false; // default
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with x,y positions & width,height dimension on TownMaps.
     * @param x x position of this building on a townMap.
     * @param y y position of this building on a townMap.
     * @param width width dimension of this building on a townMap.
     * @param height height dimension of this building on a townMap.
     */
    public Building(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.serverID = Building.SERVER_ID_NONE; // Not initialized.
        this.hasBuildingExits = false; // default
        this.hasTownExits = false; // default
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /*
     * List of setter and getter used for persistence
     */

    public void setBuildingID(int myBuildingID) {
        this.buildingID = myBuildingID;
    }

    public int getBuildingID() {
        return this.buildingID;
    }

    public void setServerID(int myServerID) {
        this.serverID = myServerID;
    }

    public int getServerID() {
        return this.serverID;
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

    public void setHasTownExits(boolean myHasTownExits) {
        this.hasTownExits = myHasTownExits;
    }

    public boolean getHasTownExits() {
        return this.hasTownExits;
    }

    public void setHasBuildingExits(boolean myHasBuildingExits) {
        this.hasBuildingExits = myHasBuildingExits;
    }

    public boolean getHasBuildingExits() {
        return this.hasBuildingExits;
    }

    public void setSmallBuildingImage(ImageIdentifier smallBuildingImage) {
        this.smallBuildingImage = smallBuildingImage;
    }

    public ImageIdentifier getSmallBuildingImage() {
        return this.smallBuildingImage;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Transient fields getter & setter
     */

    public TownMap getMyTownMap() {
        return this.myTownMap;
    }

    public void setInteriorMaps(InteriorMap[] myInteriorMaps) {
        this.interiorMaps = myInteriorMaps;
    }

    public InteriorMap[] getInteriorMaps() {
        return this.interiorMaps;
    }

    public MapExit[] getBuildingExits() {
        return this.buildingExits;
    }

    public MapExit[] getTownExits() {
        return this.townExits;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get a interiorMap by its ID.
     *
     * @param id interiorMapID
     * @return corresponding interiorMap, null if ID does not exist.
     */
    public InteriorMap getInteriorMapFromID(int id) {
        if (id >= this.interiorMaps.length || id < 0) {
            Debug.signal(Debug.ERROR, this, "getInteriorMapByID : Bad interiorMap ID " + id + ". " + this);
            return null;
        }

        return this.interiorMaps[id];
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new InteriorMap object to the array interiorMaps
     *
     * @return a new InteriorMap object
     */
    public InteriorMap addNewInteriorMap() {
        InteriorMap myInteriorMap = new InteriorMap();

        if (this.interiorMaps == null) {
            this.interiorMaps = new InteriorMap[1];
            myInteriorMap.setInteriorMapID(0);
            this.interiorMaps[0] = myInteriorMap;
        } else {
            InteriorMap[] myInteriorMaps = new InteriorMap[this.interiorMaps.length + 1];
            myInteriorMap.setInteriorMapID(this.interiorMaps.length);
            System.arraycopy(this.interiorMaps, 0, myInteriorMaps, 0, this.interiorMaps.length);
            myInteriorMaps[this.interiorMaps.length] = myInteriorMap;
            this.interiorMaps = myInteriorMaps;
        }

        return myInteriorMap;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a InteriorMap to our array interiorMaps {@link #buildings buildings})
     *
     * @param building Building object to add
     */
    public void addInteriorMap(InteriorMap map) {
        if (this.interiorMaps == null) {
            this.interiorMaps = new InteriorMap[map.getInteriorMapID() + 1];
        } else if (this.interiorMaps.length <= map.getInteriorMapID()) {
            InteriorMap[] myInteriorMap = new InteriorMap[map.getInteriorMapID() + 1];
            System.arraycopy(this.interiorMaps, 0, myInteriorMap, 0, this.interiorMaps.length);
            this.interiorMaps = myInteriorMap;
        }

        this.interiorMaps[map.getInteriorMapID()] = map;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this building ( it rebuilds shortcuts ). DON'T CALL this method directly, use
     *  the init() method of the associated world.
     *
     * @param townMap our father townMap
     */
    public void init(TownMap myTownMap) {

        this.myTownMap = myTownMap;

        // 1 - any data ?
        if (this.interiorMaps == null) {
            Debug.signal(Debug.NOTICE, this, "Building has no interior maps: " + this);
            return;
        }

        // 2 - we transmit the init() call
        for (int i = 0; i < this.interiorMaps.length; i++)
            if (this.interiorMaps[i] != null)
                this.interiorMaps[i].init(this);

        // 3 - we reconstruct the shortcuts (now that interiorMaps shortcuts have been rebuild)
        for (int i = 0; i < this.interiorMaps.length; i++)
            if (this.interiorMaps[i] != null) {
                Room rooms[] = this.interiorMaps[i].getRooms();

                if (rooms == null)
                    continue;

                for (int j = 0; j < rooms.length; j++)
                    if (rooms[j] != null) {
                        MapExit exits[] = rooms[j].getMapExits();

                        if (exits == null)
                            continue;

                        for (int k = 0; k < exits.length; k++)
                            if (exits[k] != null && exits[k].getType() == MapExit.BUILDING_EXIT) {
                                if (this.buildingExits == null) {
                                    this.buildingExits = new MapExit[1];
                                    this.hasBuildingExits = true;
                                } else {
                                    MapExit tmp[] = new MapExit[this.buildingExits.length + 1];
                                    System.arraycopy(this.buildingExits, 0, tmp, 0, this.buildingExits.length);
                                    this.buildingExits = tmp;
                                }

                                this.buildingExits[this.buildingExits.length - 1] = exits[k];
                            } else if (exits[k] != null && exits[k].getType() == MapExit.TOWN_EXIT) {
                                if (this.townExits == null) {
                                    this.townExits = new MapExit[1];
                                    this.hasTownExits = true;
                                } else {
                                    MapExit tmp[] = new MapExit[this.townExits.length + 1];
                                    System.arraycopy(this.townExits, 0, tmp, 0, this.townExits.length);
                                    this.townExits = tmp;
                                }

                                this.townExits[this.townExits.length - 1] = exits[k];
                            }
                    }
            }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the buildingExit (MapExit) that is on the side given by the specified point.
     * @param a point which is out of the MapExit ScreenZone and should represent
     *        the direction by which the player hits this TownMap zone.
     * @return the appropriate MapExit, null if there are no MapExits.
     */
    public MapExit findTownMapExit(Point fromPosition) {

        if (this.buildingExits == null)
            return null;

        if (this.buildingExits.length == 1)
            return this.buildingExits[0];

        for (int i = 0; i < this.buildingExits.length; i++) {
            if (this.buildingExits[i].getMapExitSide() == MapExit.WEST && fromPosition.x <= this.x)
                return this.buildingExits[i];

            if (this.buildingExits[i].getMapExitSide() == MapExit.EAST && fromPosition.x >= this.x + this.width)
                return this.buildingExits[i];

            if (this.buildingExits[i].getMapExitSide() == MapExit.NORTH && fromPosition.y <= this.y)
                return this.buildingExits[i];

            if (this.buildingExits[i].getMapExitSide() == MapExit.SOUTH && fromPosition.y >= this.y + this.height)
                return this.buildingExits[i];
        }

        return this.buildingExits[0]; // default
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the buildingExit (MapExit) that is on the side given by the specified angle.
     * @param a angle which should represent the direction by which the player hits this TownMap zone.
     * @return the appropriate MapExit, null if there are no MapExits.
     */
    public MapExit findTownMapExit(double fromAngle) {

        if (this.buildingExits == null)
            return null;

        if (this.buildingExits.length == 1)
            return this.buildingExits[0];

        if (Building.SHOW_DEBUG) {
            System.out.print("fromAngle = ");
            System.out.println(fromAngle * 180 / Math.PI);
            System.out.print("cosinus = " + Math.cos(fromAngle));
            System.out.println(" , sinus = " + Math.sin(fromAngle));
        }

        for (int i = 0; i < this.buildingExits.length; i++) {
            if (Math.cos(fromAngle) > 0.708)
                // West
                if (this.buildingExits[i].getMapExitSide() == MapExit.WEST)
                    return this.buildingExits[i];

            if (Math.cos(fromAngle) < -0.708)
                // East
                if (this.buildingExits[i].getMapExitSide() == MapExit.EAST)
                    return this.buildingExits[i];

            if (Math.sin(fromAngle) > 0.7)
                // North
                if (this.buildingExits[i].getMapExitSide() == MapExit.NORTH)
                    return this.buildingExits[i];

            if (Math.sin(fromAngle) < 0.7)
                // South
                if (this.buildingExits[i].getMapExitSide() == MapExit.SOUTH)
                    return this.buildingExits[i];

        }

        if (Building.SHOW_DEBUG) {
            System.out.print("default ");
            System.out.println(Math.cos(fromAngle) + 0.7);
        }
        return this.buildingExits[0]; // default
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** String Info.
     */
    @Override
    public String toString() {
        return "Building - " + this.fullName + " (serverID:" + this.serverID + ")";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}