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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import wotlas.libs.net.NetMessage;
import wotlas.common.message.movement.*;
import wotlas.common.*;
// import wotlas.common.character.WotCharacter;
import wotlas.common.character.BasicChar;
import wotlas.common.universe.WotlasLocation;
import wotlas.common.movement.*;

import wotlas.utils.Tools;

/** 
 * To send action reqest (Message Sent by Client).
 *
 * @author Diego
 */

abstract public class ActionWithPositionMessage extends NetMessage
{
    protected String targetKey;
    protected int idOfAction;
    protected int x,y;
    protected byte targetRange;

  /** Constructor. Just initializes the message category and type.
   */
    public ActionWithPositionMessage() {
        super();
    }

    public ActionWithPositionMessage( int idOfAction, int x, int y, String targetKey
    , byte targetRange ) {
        super();
        this.idOfAction = idOfAction;
        this.x = x;
        this.y = y;
        this.targetKey = targetKey;
        this.targetRange = targetRange;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This is where we put your message data on the stream. You don't need
    * to invoke this method yourself, it's done automatically.
    *
    * @param ostream data stream where to put your data (see java.io.DataOutputStream)
    * @exception IOException if the stream has been closed or is corrupted.
    */
    public void encode( DataOutputStream ostream ) throws IOException {
        ostream.writeInt( idOfAction );
        ostream.writeInt( x );
        ostream.writeInt( y );
        ostream.writeUTF( targetKey );
        ostream.writeByte( targetRange );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This is where we retrieve our message data from the stream. You don't need
    * to invoke this method yourself, it's done automatically.
    *
    * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
    * @exception IOException if the stream has been closed or is corrupted.
    */
    public void decode( DataInputStream istream ) throws IOException {
        idOfAction = istream.readInt();
        x = istream.readInt();
        y = istream.readInt();
        targetKey = istream.readUTF();        
        targetRange = istream.readByte();
    }
}