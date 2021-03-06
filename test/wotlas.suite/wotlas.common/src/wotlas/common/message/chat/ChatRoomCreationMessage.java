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
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetMessage;

/**
 * To notice the server that a ChatRoom should be created now. (Message Sent by
 * Client)
 * 
 * @author Petrus
 */
public class ChatRoomCreationMessage extends NetMessage {

    /*------------------------------------------------------------------------------------*/
    /**
     * Name of the ChatRoom
     */
    protected String name;
    /**
     * ID of the player who created the ChatRoom
     */
    protected String creatorPrimaryKey;

    /*------------------------------------------------------------------------------------*/
    /**
     * Constructor. Just initializes the message category and type.
     */
    public ChatRoomCreationMessage() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /**
     * Constructor with parameters.
     * 
     * @deprecated SHOULD BE UPDATED ON THE CLIENT'S SIDE
     */
    @Deprecated
    public ChatRoomCreationMessage(String name, String creatorPrimaryKey, WotlasLocation location) {
        super();
        this.name = name;
        this.creatorPrimaryKey = creatorPrimaryKey;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /**
     * Constructor with parameters.
     */
    public ChatRoomCreationMessage(String name, String creatorPrimaryKey) {
        super();
        this.name = name;
        this.creatorPrimaryKey = creatorPrimaryKey;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /**
     * This is where we put your message data on the stream. You don't need to
     * invoke this method yourself, it's done automatically.
     * 
     * @param ostream
     *            data stream where to put your data (see
     *            java.io.DataOutputStream)
     * @exception IOException
     *                if the stream has been closed or is corrupted.
     */
    @Override
    public void encode(DataOutputStream ostream) throws IOException {
        ostream.writeUTF(this.name);
        ostream.writeUTF(this.creatorPrimaryKey);

        // DEPRECATED : TO REMOVE FOR THE NEXT CLIENT VERSION
        ostream.writeInt(0);
        ostream.writeInt(0);
        ostream.writeInt(0);
        ostream.writeInt(0);
        ostream.writeInt(0);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /**
     * This is where we retrieve our message data from the stream. You don't
     * need to invoke this method yourself, it's done automatically.
     * 
     * @param istream
     *            data stream where you retrieve your data (see
     *            java.io.DataInputStream)
     * @exception IOException
     *                if the stream has been closed or is corrupted.
     */
    @Override
    public void decode(DataInputStream istream) throws IOException {
        this.name = istream.readUTF();
        this.creatorPrimaryKey = istream.readUTF();

        // DEPRECATED : TO REMOVE FOR THE NEXT CLIENT VERSION
        istream.readInt();
        istream.readInt();
        istream.readInt();
        istream.readInt();
        istream.readInt();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
}
