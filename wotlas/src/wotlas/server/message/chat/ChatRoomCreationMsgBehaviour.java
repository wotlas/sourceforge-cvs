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

package wotlas.server.message.chat;

import java.io.IOException;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.chat.*;
import wotlas.common.Player;
import wotlas.server.PlayerImpl;

/**
 * Associated behaviour to the ChatRoomCreationMessage...
 *
 * @author Petrus
 */

public class ChatRoomCreationMsgBehaviour extends ChatRoomCreationMessage implements NetMessageBehaviour
{

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  public ChatRoomCreationMsgBehaviour() {
    super();
  }
  
  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
  public void doBehaviour( Object context ) {
    // The context is here a PlayerImpl.
    // PlayerImpl player = (PlayerImpl) context;

    // We send the player's data
    //player.sendMessage(  );
    System.out.println("public ChatRoomCreationMsgBehaviour()");
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}
  