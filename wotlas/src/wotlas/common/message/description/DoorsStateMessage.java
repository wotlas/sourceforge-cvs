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

package wotlas.common.message.description;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import wotlas.common.universe.Door;
import wotlas.common.universe.Room;
import wotlas.common.universe.RoomLink;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetMessage;

/** 
 * To send a new state for the doors of a room...
 * (Message Sent by Server/Client).
 *
 * @author Aldiss
 */

public class DoorsStateMessage extends NetMessage {
    /*------------------------------------------------------------------------------------*/

    /** Doors state.
     */
    protected boolean isOpened[];

    /** RoomLinkIDs that posseses doors...
     */
    protected int roomLinkIDs[];

    /** WotlasLocation of the room.
     */
    protected WotlasLocation location;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. Just initializes the message category and type.
     */
    public DoorsStateMessage() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with Player's primaryKey & location.
     */
    public DoorsStateMessage(Room room) {
        this();
        this.location = room.getLocation();

        Door doors[] = room.getDoors();
        RoomLink rl[] = room.getRoomLinks();

        this.roomLinkIDs = new int[doors.length];
        this.isOpened = new boolean[doors.length];

        int nb = 0;

        if (rl != null && doors.length != 0)
            for (int i = 0; i < rl.length; i++)
                if (rl[i].getDoor() != null) {
                    this.roomLinkIDs[nb] = rl[i].getRoomLinkID();
                    this.isOpened[nb] = rl[i].getDoor().isOpened();
                    nb++;
                }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This is where we put your message data on the stream. You don't need
     * to invoke this method yourself, it's done automatically.
     *
     * @param ostream data stream where to put your data (see java.io.DataOutputStream)
     * @exception IOException if the stream has been closed or is corrupted.
     */
    @Override
    public void encode(DataOutputStream ostream) throws IOException {

        ostream.writeInt(this.location.getWorldMapID());
        ostream.writeInt(this.location.getTownMapID());
        ostream.writeInt(this.location.getBuildingID());
        ostream.writeInt(this.location.getInteriorMapID());
        ostream.writeInt(this.location.getRoomID());

        ostream.writeInt(this.roomLinkIDs.length);

        for (int i = 0; i < this.roomLinkIDs.length; i++) {
            ostream.writeInt(this.roomLinkIDs[i]);
            ostream.writeBoolean(this.isOpened[i]);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This is where we retrieve our message data from the stream. You don't need
     * to invoke this method yourself, it's done automatically.
     *
     * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
     * @exception IOException if the stream has been closed or is corrupted.
     */
    @Override
    public void decode(DataInputStream istream) throws IOException {

        this.location = new WotlasLocation();

        this.location.setWorldMapID(istream.readInt());
        this.location.setTownMapID(istream.readInt());
        this.location.setBuildingID(istream.readInt());
        this.location.setInteriorMapID(istream.readInt());
        this.location.setRoomID(istream.readInt());

        int size = istream.readInt();
        this.roomLinkIDs = new int[size];
        this.isOpened = new boolean[size];

        for (int i = 0; i < size; i++) {
            this.roomLinkIDs[i] = istream.readInt();
            this.isOpened[i] = istream.readBoolean();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
