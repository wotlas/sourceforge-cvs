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
import wotlas.libs.net.NetMessage;

/** 
 * To send action reqest (Message Sent by Client).
 *
 * @author Diego
 */

abstract public class ActionWithPositionMessage extends NetMessage {

    protected int idOfAction;
    protected int x, y;
    protected byte targetRange;

    /** Constructor. Just initializes the message category and type.
     */
    public ActionWithPositionMessage() {
        super();
    }

    public ActionWithPositionMessage(int idOfAction, int x, int y, byte targetRange) {
        super();
        this.idOfAction = idOfAction;
        this.x = x;
        this.y = y;
        this.targetRange = targetRange;
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
        ostream.writeInt(this.x);
        ostream.writeInt(this.y);
        ostream.writeByte(this.targetRange);
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
        this.x = istream.readInt();
        this.y = istream.readInt();
        this.targetRange = istream.readByte();
    }
}