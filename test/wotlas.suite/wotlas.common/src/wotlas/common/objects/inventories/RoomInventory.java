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

package wotlas.common.objects.inventories;

import wotlas.common.objects.containers.Ground;
import wotlas.common.universe.Room;
import wotlas.common.universe.WotlasLocation;

/** 
 * This is the base class for all RoomInventories.<br>
 * It can also be considered as a standard Room with only the Ground for Container.
 *
 * The WotlasLocation field is needed for persistance purpose ( see WorldManager.load 
 * and save )
 * @author Elann, Aldiss
 */

public class RoomInventory {

    /*------------------------------------------------------------------------------------*/

    /** The ground of the room. Used to manage the objects disposed on the ground.
     */
    protected Ground ground;

    /** Our location.
     */
    private WotlasLocation location;

    /*------------------------------------------------------------------------------------*/

    /** Default constructor
     */
    public RoomInventory() {
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the ground.
     * @return ground
     */
    public Ground getGround() {
        return this.ground;
    }

    /** Set the ground.
     * @param ground the new ground
     */
    public void setGround(Ground ground) {
        this.ground = ground;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the Location of this room inventory
     * @return location
     */
    public WotlasLocation getLocation() {
        return this.location;
    }

    /** Set the Location of this room inventory
     * @param location Location of this room inventory
     */
    public void setLocation(WotlasLocation location) {
        this.location = location;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Set the room owner of the RoomInventory. 
     * @param ownerRoom the new room owner
     */
    public void setOwnerRoom(Room ownerRoom) {
        this.ground.setOwnerRoom(ownerRoom);
        this.location = new WotlasLocation(ownerRoom.getLocation());
    }

    /** Get the room owner of the RoomInventory. 
     * @return ownerRoom
     */
    public Room getOwnerRoom() {
        return this.ground.getOwnerRoom();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
