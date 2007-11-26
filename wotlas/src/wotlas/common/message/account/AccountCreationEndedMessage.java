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

package wotlas.common.message.account;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import wotlas.libs.net.NetMessage;

/** 
 * To notice the client that the account creation has ended ()successfully).
 * (Message Sent by Server)
 *
 * @author Aldiss
 */

public class AccountCreationEndedMessage extends NetMessage {
    /*------------------------------------------------------------------------------------*/

    // client ID part
    protected int clientID;

    // server ID part
    protected int serverID;

    // player login
    protected String login;

    // player password
    protected String password;

    // player (full) name
    protected String playerName;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public AccountCreationEndedMessage() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with IDs.
     *
     * @param clientID client local ID
     * @param serverID server ID who created the client account
     * @param login player login
     * @param playerName player Name
     */
    public AccountCreationEndedMessage(int clientID, int serverID, String login, String password, String playerName) {
        super();
        this.clientID = clientID;
        this.serverID = serverID;
        this.login = login;
        this.password = password;
        this.playerName = playerName;
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
        ostream.writeInt(this.clientID);
        ostream.writeInt(this.serverID);
        ostream.writeUTF(this.login);
        ostream.writeUTF(this.password);
        ostream.writeUTF(this.playerName);
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
        this.clientID = istream.readInt();
        this.serverID = istream.readInt();
        this.login = istream.readUTF();
        this.password = istream.readUTF();
        this.playerName = istream.readUTF();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
