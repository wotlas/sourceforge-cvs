/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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

package wotlas.common.message.movement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.libs.net.NetMessage;
import wotlas.common.message.MessageRegistry;
import wotlas.common.Player;
import wotlas.common.character.WotCharacter;
import wotlas.common.universe.WotlasLocation;

import wotlas.utils.Tools;

/** 
 * To send movement data (Message Sent by Client or Server).
 *
 * @author Aldiss
 */

abstract public class MovementUpdateMessage extends NetMessage
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public MovementUpdateMessage() {
          super( MessageRegistry.MOVEMENT_CATEGORY,
                 MovementMessageCategory.MOVEMENT_UPDATE_MSG );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor for eventual subclasses. Just initializes the message category and type.
   *
   * @param msg_category message's category in your NetRegistry.
   * @param msg_type message's type in the associated NetCategory.
   */
     public MovementUpdateMessage( byte msg_category, byte msg_type) {
          super( msg_category, msg_type );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
