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

package wotlas.common.message.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import wotlas.common.Player;
import wotlas.libs.net.NetMessage;

/** 
 * To set the current ChatRoom of the player. (Message sent by Server)
 *
 * @author Petrus, Aldiss
 */

public class SetCurrentChatRoomMessage extends NetMessage {

    /*------------------------------------------------------------------------------------*/

    /** Id of the ChatRoom
     */
    protected String chatRoomPrimaryKey;

    /** ChatRoom Players primaryKey
     */
    protected String playersPrimaryKey[];

    /** ChatRoom Players full player name
     */
    protected String fullPlayerNames[];

    /*------------------------------------------------------------------------------------*/

    /** Constructor. Just initializes the message category and type.
     */
    public SetCurrentChatRoomMessage() {
        super();
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with parameters.
     */
    public SetCurrentChatRoomMessage(String chatRoomPrimaryKey, Hashtable players) {
        super();
        this.chatRoomPrimaryKey = chatRoomPrimaryKey;

        if (players == null) {
            this.playersPrimaryKey = new String[0];
            this.fullPlayerNames = new String[0];
            return;
        }

        synchronized (players) {
            Iterator it = players.values().iterator();
            int i = 0;

            this.playersPrimaryKey = new String[players.size()];
            this.fullPlayerNames = new String[players.size()];

            while (it.hasNext()) {
                Player p = (Player) it.next();
                this.playersPrimaryKey[i] = p.getPrimaryKey();
                this.fullPlayerNames[i] = p.getPlayerName();
                i++;
            }
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
        ostream.writeUTF(this.chatRoomPrimaryKey);

        ostream.writeInt(this.playersPrimaryKey.length);

        for (int i = 0; i < this.playersPrimaryKey.length; i++)
            ostream.writeUTF(this.playersPrimaryKey[i]);

        ostream.writeInt(this.fullPlayerNames.length);

        for (int i = 0; i < this.fullPlayerNames.length; i++)
            ostream.writeUTF(this.fullPlayerNames[i]);
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

        this.chatRoomPrimaryKey = istream.readUTF();

        this.playersPrimaryKey = new String[istream.readInt()];

        for (int i = 0; i < this.playersPrimaryKey.length; i++)
            this.playersPrimaryKey[i] = istream.readUTF();

        this.fullPlayerNames = new String[istream.readInt()];

        for (int i = 0; i < this.fullPlayerNames.length; i++)
            this.fullPlayerNames[i] = istream.readUTF();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}