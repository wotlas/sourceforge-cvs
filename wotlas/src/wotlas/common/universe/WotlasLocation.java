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

/** Location class. Identifies a precise location in the universe. Normally,
 *  it points out a room of an InteriorMap, building, town, world.<br>
 *  But it can points out :<p><br>
 *
 *  - a World ( worldMapId set, townMapId=-1, buildingID=-1, interiorMapId=-1, roomID=-1)<br>
 *
 *  - a Town ( worldMapId set, townMapId set, buildingID=-1, interiorMapId=-1, roomID=-1)
 *
 * @author Petrus, Aldiss
 */

public class WotlasLocation {

    /*------------------------------------------------------------------------------------*/

    /** worldMapID
     */
    private int worldMapID;

    /** townMapID
     */
    private int townMapID;

    /** buildingID
     */
    private int buildingID;

    /** interiorMapID
     */
    private int interiorMapID;

    /** roomID
     */
    private int roomID;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public WotlasLocation() {
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor from another WotlasLocation.
     */
    public WotlasLocation(WotlasLocation other) {
        this.worldMapID = other.worldMapID;
        this.townMapID = other.townMapID;
        this.buildingID = other.buildingID;
        this.interiorMapID = other.interiorMapID;
        this.roomID = other.roomID;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor for Rooms.
     */
    public WotlasLocation(int worldMapID, int townMapID, int buildingID, int interiorMapID, int roomID) {
        this.worldMapID = worldMapID;
        this.townMapID = townMapID;
        this.buildingID = buildingID;
        this.interiorMapID = interiorMapID;
        this.roomID = roomID;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor for TownMap.
     */
    public WotlasLocation(int worldMapID, int townMapID) {
        this.worldMapID = worldMapID;
        this.townMapID = townMapID;
        this.buildingID = -1;
        this.interiorMapID = -1;
        this.roomID = -1;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor for WorldMap.
     */
    public WotlasLocation(int worldMapID) {
        this.worldMapID = worldMapID;
        this.townMapID = -1;
        this.buildingID = -1;
        this.interiorMapID = -1;
        this.roomID = -1;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the WorldMapID
     *
     * @return WorldMapID
     */
    public int getWorldMapID() {
        return this.worldMapID;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the townMapID
     *
     * @return townMapID
     */
    public int getTownMapID() {
        return this.townMapID;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the buildingID
     *
     * @return buildingID
     */
    public int getBuildingID() {
        return this.buildingID;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the interiorMapID
     *
     * @return interiorMapID
     */
    public int getInteriorMapID() {
        return this.interiorMapID;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the roomID
     *
     * @return roomID
     */
    public int getRoomID() {
        return this.roomID;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the worldMapID.
     *
     * @param worldMapID
     */
    public void setWorldMapID(int worldMapID) {
        this.worldMapID = worldMapID;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the townMapID.
     *
     * @param townMapID
     */
    public void setTownMapID(int townMapID) {
        this.townMapID = townMapID;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the buildingID.
     *
     * @param buildingID
     */
    public void setBuildingID(int buildingID) {
        this.buildingID = buildingID;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the interiorMapID.
     *
     * @param interiorMapID
     */
    public void setInteriorMapID(int interiorMapID) {
        this.interiorMapID = interiorMapID;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the roomID.
     *
     * @param roomID
     */
    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    /*------------------------------------------------------------------------------------*/

    /** Does this location points out a Town ?
     *
     * @return true if it points out a town.
     */
    public boolean isTown() {
        if ((this.townMapID >= 0) && (this.buildingID < 0))
            return true;
        return false;
    }

    /*------------------------------------------------------------------------------------*/

    /** Does this location points out a World ?
     *
     * @return true if it points out a world.
     */
    public boolean isWorld() {
        if (this.townMapID < 0)
            return true;
        return false;
    }

    /*------------------------------------------------------------------------------------*/

    /** Does this location points out a Room ?
     *
     * @return true if it points out a room.
     */
    public boolean isRoom() {
        if (this.buildingID >= 0 && this.townMapID >= 0)
            return true;
        return false;
    }

    /*------------------------------------------------------------------------------------*/

    /** Are this location equal to our ?
     * @param other other location
     * @return true if they are equal...
     */
    public boolean equals(WotlasLocation other) {
        if (isRoom()) {
            if (this.worldMapID == other.getWorldMapID() && this.townMapID == other.getTownMapID() && this.buildingID == other.getBuildingID() && this.interiorMapID == other.getInteriorMapID() && this.roomID == other.getRoomID())
                return true;
            return false;
        } else if (isTown()) {
            if (other.isTown() && this.worldMapID == other.getWorldMapID() && this.townMapID == other.getTownMapID())
                return true;
            return false;
        } else if (isWorld()) {
            if (other.isWorld() && this.worldMapID == other.getWorldMapID())
                return true;
        }

        return false; // we should never arrive here
    }

    /*------------------------------------------------------------------------------------*/

    /** String Info.
     */
    @Override
    public String toString() {
        if (isRoom())
            return "Room: w" + this.worldMapID + ", t" + this.townMapID + ", b" + this.buildingID + ", i" + this.interiorMapID + ", r" + this.roomID;
        else if (isTown())
            return "Town: w" + this.worldMapID + ", t" + this.townMapID;
        else if (isWorld())
            return "World: w" + this.worldMapID;
        else
            return "Bad WotlasLocation: w" + this.worldMapID;
    }

    /*------------------------------------------------------------------------------------*/

}