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
import wotlas.libs.net.NetMessage;

/** 
 * The GameServer sends to client his fake names (Message Sent by Server).
 *
 * @author Petrus
 */

public class YourFakeNamesMessage extends NetMessage {
    /*------------------------------------------------------------------------------------*/

    // client fake names
    protected String[] fakeNames;

    // current fake name
    protected short currentFakeNameIndex;

    // number of fake names
    protected int fakeNamesLength;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. Just initializes the message category and type.
     */
    public YourFakeNamesMessage() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor.
     *
     * @param fakeNames array of player's fake names
     */
    public YourFakeNamesMessage(String[] fakeNames, short currentFakeNameIndex) {
        super();
        this.fakeNames = fakeNames;
        this.currentFakeNameIndex = currentFakeNameIndex;
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
        this.fakeNamesLength = this.fakeNames.length;
        ostream.writeInt(this.fakeNamesLength);
        for (int i = 0; i < this.fakeNamesLength; i++) {
            ostream.writeUTF(this.fakeNames[i]);
        }
        ostream.writeShort(this.currentFakeNameIndex);
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
        this.fakeNamesLength = istream.readInt();
        this.fakeNames = new String[this.fakeNamesLength];
        for (int i = 0; i < this.fakeNamesLength; i++) {
            this.fakeNames[i] = istream.readUTF();
        }
        this.currentFakeNameIndex = istream.readShort();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
