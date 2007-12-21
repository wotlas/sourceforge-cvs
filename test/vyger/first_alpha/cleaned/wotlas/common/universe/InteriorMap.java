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

import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.utils.Debug;

/** An InteriorMap represents any maps where players can walk. It usually belongs
 *  to buildings. It does not implement the WotlasMap interface because it doesn't
 *  possess players. Players are possessed by the InteriorMap's rooms.
 *
 * @author Petrus, Aldiss
 */

public class InteriorMap {

    /*------------------------------------------------------------------------------------*/

    /** ID of the InteriorMap (index in the array Building.interiorMaps)
     */
    private int interiorMapID;

    /** Full name of the InteriorMap
     */
    private String fullName;

    /** Short name of the InteriorMap
     */
    private String shortName;

    /** Full Image (identifier) of this interiorMap
     */
    private ImageIdentifier interiorMapImage;

    /** Image's Total Width
     */
    private int imageWidth;

    /** Image's Total Height
     */
    private int imageHeight;

    /** Our image is a MultiRegionImage. This is the width of a region.
     */
    private int imageRegionWidth;

    /** Our image is a MultiRegionImage. This is the height of a region.
     */
    private int imageRegionHeight;

    /** Music Name
     */
    private String musicName;

    /** List of the rooms of the Building
     * non transient (rooms in the same file of the building)
     */
    private Room[] rooms;

    /*------------------------------------------------------------------------------------*/

    /** A link to our father building...
     */
    private transient Building myBuilding;

    /*------------------------------------------------------------------------------------*/

    /** Constructor
     */
    public InteriorMap() {
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /*
     * List of setter and getter used for persistence
     */

    public void setInteriorMapID(int myInteriorMapID) {
        this.interiorMapID = myInteriorMapID;
    }

    public int getInteriorMapID() {
        return this.interiorMapID;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageWidth() {
        return this.imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageHeight() {
        return this.imageHeight;
    }

    public void setImageRegionWidth(int imageRegionWidth) {
        this.imageRegionWidth = imageRegionWidth;
    }

    public int getImageRegionWidth() {
        return this.imageRegionWidth;
    }

    public void setImageRegionHeight(int imageRegionHeight) {
        this.imageRegionHeight = imageRegionHeight;
    }

    public int getImageRegionHeight() {
        return this.imageRegionHeight;
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

    public void setRooms(Room[] myRooms) {
        this.rooms = myRooms;
    }

    public Room[] getRooms() {
        return this.rooms;
    }

    public void setInteriorMapImage(ImageIdentifier interiorMapImage) {
        this.interiorMapImage = interiorMapImage;
    }

    public ImageIdentifier getInteriorMapImage() {
        return this.interiorMapImage;
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
    public Building getMyBuilding() {
        return this.myBuilding;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get a room by its ID.
     *
     * @param id roomID
     * @return corresponding room, null if ID does not exist.
     */
    public Room getRoomFromID(int id) {
        if (id >= this.rooms.length || id < 0) {
            Debug.signal(Debug.ERROR, this, "getRoomByID : Bad room ID " + id);
            return null;
        }

        return this.rooms[id];
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new Room object to the array rooms
     *
     * @return a new Room object
     */
    public Room addRoom() {
        Room myRoom = new Room();

        if (this.rooms == null) {
            this.rooms = new Room[1];
            myRoom.setRoomID(0);
            this.rooms[0] = myRoom;
        } else {
            Room[] myRooms = new Room[this.rooms.length + 1];
            myRoom.setRoomID(this.rooms.length);
            System.arraycopy(this.rooms, 0, myRooms, 0, this.rooms.length);
            myRooms[this.rooms.length] = myRoom;
            this.rooms = myRooms;
        }

        return myRoom;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this interiorMap ( it rebuilds shortcuts ). DON'T CALL this method directly,
     *  use the init() method of the associated world.
     *
     * @param myBuilding our father building
     */
    public void init(Building myBuilding) {

        this.myBuilding = myBuilding;

        // 1 - any data ?
        if (this.rooms == null) {
            Debug.signal(Debug.WARNING, this, "InteriorMap has no rooms: " + this);
            return;
        }

        // 2 - we transmit the init() call
        for (int i = 0; i < this.rooms.length; i++)
            if (this.rooms[i] != null)
                this.rooms[i].init(this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** String Info.
     */
    @Override
    public String toString() {
        return "Interior - " + this.fullName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
