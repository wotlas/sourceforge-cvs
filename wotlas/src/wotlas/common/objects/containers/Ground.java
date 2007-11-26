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

package wotlas.common.objects.containers;

import wotlas.common.universe.Room;

/** 
 * The ground object.
 * 
 * @author Elann
 * @see wotlas.common.objects.BaseObject
 * @see wotlas.common.objects.interfaces.ContainerInterface
 * @see wotlas.common.objects.containers.ContainerObject
 */
public class Ground extends ContainerObject {

    /*------------------------------------------------------------------------------------*/

    /** The owner of the ground.
    */
    protected transient Room ownerRoom;

    /*------------------------------------------------------------------------------------*/

    /** Default constructor. Calls ContainerObject's constructor.
     */
    public Ground() {
        super();

        this.className = "Ground";
        this.objectName = "standard ground"; // to modify -> name of the room
    }

    /** Parametric constructor. Calls ContainerObject's constructor.
     * @param ownerRoom the ground's owner
     */
    public Ground(Room ownerRoom) {
        super();

        this.ownerRoom = ownerRoom;
        this.className = "Ground";

        // set the objectName
        updateName();
    }

    /** Parametric constructor. Calls ContainerObject's constructor.
     * @param capacity the number of objects that can be laid on the ground
     */
    public Ground(short capacity) {
        super(capacity);

        this.className = "Ground";
        this.objectName = "standard ground"; // to modify -> name of the room
    }

    /** Full parametric constructor. Calls ContainerObject's constructor.
     * @param ownerRoom the ground's owner
     * @param capacity the number of objects that can be laid on the ground
     */
    public Ground(Room ownerRoom, short capacity) {
        super(capacity);

        this.ownerRoom = ownerRoom;
        this.className = "Ground";

        // set the objectName
        updateName();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Set the room owner of the ground. 
     * @param ownerRoom the new room owner
     */
    public void setOwnerRoom(Room ownerRoom) {
        this.ownerRoom = ownerRoom;
        updateName();
    }

    /** Get the room owner of the ground. 
     * @return ownerRoom
     */
    public Room getOwnerRoom() {
        return this.ownerRoom;
    }

    /** Set ground's name based on its owner 
     */
    public void updateName() {
        this.objectName = this.ownerRoom.getShortName() + "-ground";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
