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
package wotlas.client.message.description;

import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;
import wotlas.common.message.description.DoorStateMessage;
import wotlas.common.message.description.WishClientDescriptionNetMsgBehaviour;
import wotlas.common.universe.Door;
import wotlas.common.universe.Room;
import wotlas.common.universe.RoomLink;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the DoorStateMessage...
 *
 * @author Aldiss
 */
public class DoorStateMsgBehaviour extends DoorStateMessage implements WishClientDescriptionNetMsgBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public DoorStateMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {

        // The sessionContext is here a DataManager
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl myPlayer = dataManager.getMyPlayer();

        // 1 - Control
        if (!myPlayer.getLocation().isRoom()) {
            Debug.signal(Debug.ERROR, this, "Master player is not on an InteriorMap");
            return;
        }

        Room room = dataManager.getWorldManager().getRoom(this.location);

        if (room == null || room.getRoomLinks() == null) {
            Debug.signal(Debug.WARNING, this, "Room or RoomLink not found...");
            return;
        }

        // 2 - Update
        RoomLink roomLink = room.getRoomLink(this.roomLinkID);
        Door door = null;

        if (roomLink != null) {
            door = roomLink.getDoor();
        }

        if (door == null) {
            Debug.signal(Debug.WARNING, this, "RoomLink has no door !" + this.location);
            return;
        }

        if (this.isOpened) {
            SoundLibrary.getSoundPlayer().playSound("door-open.wav");
            door.open();
        } else {
            SoundLibrary.getSoundPlayer().playSound("door-close.wav");
            door.close();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
