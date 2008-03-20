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
import wotlas.common.objects.inventories.RoomInventory;
import wotlas.common.router.MessageRouter;
import wotlas.common.router.MessageRouterFactory;
import wotlas.utils.Debug;
import wotlas.utils.ScreenPoint;
import wotlas.utils.ScreenRectangle;

/** A Room of an interiorMap. 
 *
 * @author Petrus, Aldiss, Elann
 * @see wotlas.common.universe.RoomLink
 */

public class Room implements WotlasMap {

    /*------------------------------------------------------------------------------------*/

    /** ID of the Room (index in the array {@link InteriorMap#rooms InteriorMap.rooms})
     */
    private int roomID;

    /** Full name of the Room
     */
    private String fullName;

    /** Short name of the World
     */
    private String shortName;

    /** Point of insertion (teleportation, arrival)
     */
    private ScreenPoint insertionPoint;

    /** Number maximum of players
     */
    private int maxPlayers;

    /** Room links...
     */
    private RoomLink[] roomLinks;

    /** Map exits...
     */
    private MapExit[] mapExits;

    /*------------------------------------------------------------------------------------*/

    /** Our interiorMap where this room is.
     */
    private transient InteriorMap myInteriorMap;

    /** Room Location
     */
    private transient WotlasLocation thisLocation;

    /** Our message router. Owns the list of players of this map.
     */
    private transient MessageRouter messageRouter;

    /**
     * RoomInventory used to get objects here.<br>
     * Transient because there are saved elsewhere.
     */
    private transient RoomInventory inventory;

    /*------------------------------------------------------------------------------------*/

    /** Constructor
     */
    public Room() {
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /*
     * List of setter and getter used for persistence
     */
    public void setRoomID(int myRoomID) {
        this.roomID = myRoomID;
    }

    public int getRoomID() {
        return this.roomID;
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

    public void setMaxPlayers(int myMaxPlayers) {
        this.maxPlayers = myMaxPlayers;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setRoomLinks(RoomLink[] myRoomLinks) {
        this.roomLinks = myRoomLinks;
    }

    public RoomLink[] getRoomLinks() {
        return this.roomLinks;
    }

    public void setMapExits(MapExit[] myMapExits) {
        this.mapExits = myMapExits;
    }

    public MapExit[] getMapExits() {
        return this.mapExits;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Transient fields getter & setter
     */

    public InteriorMap getMyInteriorMap() {
        return this.myInteriorMap;
    }

    public MessageRouter getMessageRouter() {
        return this.messageRouter;
    }

    public RoomInventory getInventory() {
        return this.inventory;
    }

    public void setInventory(RoomInventory inventory) {
        this.inventory = inventory;
        inventory.setOwnerRoom(this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new RoomLink object to the array {@link #roomLinks roomLinks}
     *
     * @return a new RoomLink object
     */
    public RoomLink addRoomLink(ScreenRectangle r) {
        RoomLink myRoomLink = new RoomLink(r);

        if (this.roomLinks == null) {
            this.roomLinks = new RoomLink[1];
            myRoomLink.setRoomLinkID(RoomLink.getNewRoomLinkID());
            this.roomLinks[0] = myRoomLink;
        } else {
            RoomLink[] myRoomLinks = new RoomLink[this.roomLinks.length + 1];
            myRoomLink.setRoomLinkID(RoomLink.getNewRoomLinkID());
            System.arraycopy(this.roomLinks, 0, myRoomLinks, 0, this.roomLinks.length);
            myRoomLinks[this.roomLinks.length] = myRoomLink;
            this.roomLinks = myRoomLinks;
        }

        return myRoomLink;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a RoomLink object to the array {@link #roomLinks roomLinks}
     *
     * @param rl RoomLink object to add
     */
    public void addRoomLink(RoomLink rl) {
        if (this.roomLinks == null) {
            this.roomLinks = new RoomLink[1];
            this.roomLinks[0] = rl;
        } else {
            RoomLink[] myRoomLinks = new RoomLink[this.roomLinks.length + 1];
            System.arraycopy(this.roomLinks, 0, myRoomLinks, 0, this.roomLinks.length);
            myRoomLinks[this.roomLinks.length] = rl;
            this.roomLinks = myRoomLinks;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns a RoomLink from its id.
     * @return null if not found...
     */
    public RoomLink getRoomLink(int roomLinkID) {
        if (this.roomLinks == null)
            return null;

        for (int i = 0; i < this.roomLinks.length; i++)
            if (this.roomLinks[i].getRoomLinkID() == roomLinkID)
                return this.roomLinks[i];

        return null; // not found
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns a Door from its RoomLink id.
     * @return null if not found...
     */
    public Door getDoor(int roomLinkID) {
        if (this.roomLinks == null)
            return null;

        for (int i = 0; i < this.roomLinks.length; i++)
            if (this.roomLinks[i].getRoomLinkID() == roomLinkID)
                return this.roomLinks[i].getDoor();

        return null; // not found
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new MapExit object to the array {@link #mapExits mapExits}
     *
     * @return a new MapExit object
     */
    public MapExit addMapExit(ScreenRectangle r) {
        MapExit myMapExit = new MapExit(r);

        if (this.mapExits == null) {
            this.mapExits = new MapExit[1];
            myMapExit.setMapExitID(0);
            this.mapExits[0] = myMapExit;
        } else {
            MapExit[] myMapExits = new MapExit[this.mapExits.length + 1];
            myMapExit.setMapExitID(this.mapExits.length);
            System.arraycopy(this.mapExits, 0, myMapExits, 0, this.mapExits.length);
            myMapExits[this.mapExits.length] = myMapExit;
            this.mapExits = myMapExits;
        }
        return myMapExit;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new MapExit object to the array {@link #mapExits mapExits}
     *
     * @param me MapExit object
     */
    public void addMapExit(MapExit me) {
        if (this.mapExits == null) {
            this.mapExits = new MapExit[1];
            this.mapExits[0] = me;
        } else {
            MapExit[] myMapExits = new MapExit[this.mapExits.length + 1];
            System.arraycopy(this.mapExits, 0, myMapExits, 0, this.mapExits.length);
            myMapExits[this.mapExits.length] = me;
            this.mapExits = myMapExits;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the wotlas location associated to this Map.
     *  @return associated Wotlas Location
     */
    public WotlasLocation getLocation() {
        if (this.thisLocation == null) {
            this.thisLocation = new WotlasLocation();
            this.thisLocation.setRoomID(this.roomID);
            this.thisLocation.setInteriorMapID(this.myInteriorMap.getInteriorMapID());
            this.thisLocation.setBuildingID(this.myInteriorMap.getMyBuilding().getBuildingID());
            this.thisLocation.setTownMapID(this.myInteriorMap.getMyBuilding().getMyTownMap().getTownMapID());
            this.thisLocation.setWorldMapID(this.myInteriorMap.getMyBuilding().getMyTownMap().getMyWorldMap().getWorldMapID());
        }

        return this.thisLocation;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this room ( it rebuilds shortcuts ). DON'T CALL this method directly,
     *  use the init() method of the associated world.
     *
     * @param myInteriorMap our father InteriorMap
     */
    public void init(InteriorMap myInteriorMap) {
        // 1 - inits
        this.myInteriorMap = myInteriorMap;

        this.thisLocation = getLocation();

        // 2 - We reconstruct MapExit links...       
        if (this.mapExits != null)
            for (int i = 0; i < this.mapExits.length; i++)
                this.mapExits[i].setMapExitLocation(this.thisLocation);

        // 3 - We reconstruct RoomLinks links...
        if (this.roomLinks == null)
            return;

        for (int i = 0; i < this.roomLinks.length; i++) {

            if (this.roomLinks[i].getRoom1() != null)
                continue; // already done

            if (this.roomLinks[i].getDoor() != null) {
                this.roomLinks[i].getDoor().setMyRoomLinkID(this.roomLinks[i].getRoomLinkID());
                this.roomLinks[i].getDoor().setMyRoomID(this.roomID);
            }

            Room other = null;

            if (this.roomLinks[i].getRoom1ID() == this.roomID) {
                this.roomLinks[i].setRoom1(this);
                other = myInteriorMap.getRoomFromID(this.roomLinks[i].getRoom2ID());
                this.roomLinks[i].setRoom2(other);
            } else if (this.roomLinks[i].getRoom2ID() == this.roomID) {
                this.roomLinks[i].setRoom2(this);
                other = myInteriorMap.getRoomFromID(this.roomLinks[i].getRoom1ID());
                this.roomLinks[i].setRoom1(other);
            } else
                Debug.signal(Debug.ERROR, this, "BAD ROOMLINK DETECTED : " + this.thisLocation);

            // RoomLink - double detection
            RoomLink otherLinks[] = other.getRoomLinks();

            if (otherLinks == null) {
                Debug.signal(Debug.ERROR, this, "BAD ROOMLINK DETECTED : " + this.thisLocation);
                continue;
            }

            for (int j = 0; j < otherLinks.length; j++)
                if (this.roomLinks[i].equals(otherLinks[j]) && otherLinks[j].getRoom1() == null) {
                    otherLinks[j] = this.roomLinks[i];
                    break;
                }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this room for message routing. We create an appropriate message router
     *  for the room via the provided factory.
     *
     *  Don't call this method yourself it's called from the WorldManager !
     *
     * @param msgRouterFactory our router factory
     */
    public void initMessageRouting(MessageRouterFactory msgRouterFactory, WorldManager wManager) {
        // build/get our router
        this.messageRouter = msgRouterFactory.createMsgRouterForRoom(this, wManager);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the eventual RoomLink the given player is intersecting.
     *
     * @param rCurrent rectangle containing the player's current position, width & height
     * @return the Building the player is heading to (if he has reached it, or if there
     *         are any), null if none.
     */
    public RoomLink isIntersectingRoomLink(Rectangle rCurrent) {
        if (this.roomLinks == null)
            return null;

        for (int i = 0; i < this.roomLinks.length; i++)
            if (this.roomLinks[i].toRectangle().intersects(rCurrent))
                return this.roomLinks[i]; // RoomLink reached

        return null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the eventual MapExit the given player is intersecting.
     *
     * @param rCurrent rectangle containing the player's current position, width & height
     * @return the Building the player is heading to (if he has reached it, or if there
     *         are any), null if none.
     */
    public MapExit isIntersectingMapExit(int destX, int destY, Rectangle rCurrent) {
        if (this.mapExits == null)
            return null;

        for (int i = 0; i < this.mapExits.length; i++)
            if ((this.mapExits[i].toRectangle().contains(destX, destY) || this.mapExits[i].toRectangle().contains(destX + rCurrent.width / 2, destY + rCurrent.height / 2) || this.mapExits[i].toRectangle().contains(destX + rCurrent.width, destY + rCurrent.height)) && this.mapExits[i].toRectangle().intersects(rCurrent))
                return this.mapExits[i]; // mapExits reached

        return null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the RoomID of the RoomLink's target Room if the given current player
     *  Rectangle is proved to be in this other room. We return -1 if we are still in our
     *  room.
     *
     *  You should call this method
     *
     * @param rlink a RoomLink the player has intersected recently
     * @param rCurrent current player Rectangle.
     * @return -1 if the player is still in this Room, the other RoomID if he has moved to
     *         the other Room pointed by the given RoomLink.
     */
    public int isInOtherRoom(RoomLink rlink, Rectangle rCurrent) {

        if (rlink.width < rlink.height) {
            if (rlink.getRoom1ID() == this.roomID) {
                // ok, we are the west Room
                if (rlink.x < rCurrent.x + rCurrent.width / 2)
                    return rlink.getRoom2ID(); // we are in the other room
            } else {
                // ok, we are the east Room
                if (rCurrent.x + rCurrent.width / 2 < rlink.x + rlink.width)
                    return rlink.getRoom1ID(); // we are in the other room
            }
        } else if (rlink.width > rlink.height) {
            if (rlink.getRoom1ID() == this.roomID) {
                // ok, we are the north Room
                if (rlink.y <= rCurrent.y)
                    return rlink.getRoom2ID(); // we are in the other room
            } else {
                // ok, we are the south Room
                if (rCurrent.y + rCurrent.height <= rlink.y + rlink.height)
                    return rlink.getRoom1ID(); // we are in the other room
            }
        }

        return -1; // we are still in this room
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a list of the doors of this room.
     * @return array of Doors ( never returns null ).
     */
    public Door[] getDoors() {
        if (this.roomLinks == null)
            return new Door[0];

        // 1 - how many doors are there ?
        int nb = 0;

        for (int i = 0; i < this.roomLinks.length; i++)
            if (this.roomLinks[i].getDoor() != null)
                nb++;

        // 2 - Create our array
        Door doors[] = new Door[nb];

        if (nb == 0)
            return doors;

        nb = 0;

        for (int i = 0; i < this.roomLinks.length; i++)
            if (this.roomLinks[i].getDoor() != null) {
                doors[nb] = this.roomLinks[i].getDoor();
                nb++;
            }

        return doors;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** String Info.
     */
    @Override
    public String toString() {
        return "Room - " + this.fullName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}