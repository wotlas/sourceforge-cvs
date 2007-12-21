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
 * To send CastAction reqest (Message Sent by Client).
 *
 * @author Diego
 */
public class BasicActionWithPositionMessage extends ActionWithPositionMessage
{

  /** Constructor. Just initializes the message category and type.
   */
    public BasicActionWithPositionMessage() {
        super();
    }

    public BasicActionWithPositionMessage( int idOfAction, int x, int y
    , byte targetRange ) {
        super();
        this.idOfAction = idOfAction;
        this.x = x;
        this.y = y;
        this.targetRange = targetRange;
    }
}