/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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

package wotlas.common.message.action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetMessage;

/** 
 * To send action reqest (Message Sent by Client).
 *
 * @author Diego
 */

abstract public class ActionWithLocationMessage extends NetMessage {
    protected int idOfAction;
    protected WotlasLocation location;

    /** Constructor. Just initializes the message category and type.
     */
    public ActionWithLocationMessage() {
        super();
    }

    public ActionWithLocationMessage(int idOfAction, WotlasLocation location) {
        super();
        this.idOfAction = idOfAction;
        this.location = location;
        //      this.location = location;
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
        ostream.writeInt(this.idOfAction);

        // SEND wotlas location
        ostream.writeInt(this.location.getWorldMapID());
        ostream.writeInt(this.location.getTownMapID());
        ostream.writeInt(this.location.getBuildingID());
        ostream.writeInt(this.location.getInteriorMapID());
        ostream.writeInt(this.location.getRoomID());
        ostream.writeInt(this.location.getTileMapID());
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
        this.idOfAction = istream.readInt();

        // Wotlas Location
        this.location = new WotlasLocation();

        this.location.setWorldMapID(istream.readInt());
        this.location.setTownMapID(istream.readInt());
        this.location.setBuildingID(istream.readInt());
        this.location.setInteriorMapID(istream.readInt());
        this.location.setRoomID(istream.readInt());
        this.location.setTileMapID(istream.readInt());
    }
}