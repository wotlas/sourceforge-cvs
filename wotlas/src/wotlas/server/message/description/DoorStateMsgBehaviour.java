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

package wotlas.server.message.description;

import java.io.IOException;
import java.util.*;

import wotlas.utils.*;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.description.*;
import wotlas.common.message.chat.*;
import wotlas.common.universe.*;
import wotlas.common.chat.*;
import wotlas.common.Player;
import wotlas.server.PlayerImpl;
import wotlas.common.message.description.*;

/**
 * Associated behaviour to the DoorStateMessage...
 *
 * @author Aldiss
 */

public class DoorStateMsgBehaviour extends DoorStateMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public DoorStateMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object context ) {

        // The context is here a PlayerImpl.
           PlayerImpl player = (PlayerImpl) context;

        // 1 - CONTROL
           if( location.getWorldMapID()!=player.getLocation().getWorldMapID() ||
               location.getTownMapID()!=player.getLocation().getTownMapID() ||
               location.getBuildingID()!=player.getLocation().getBuildingID() ||
               location.getInteriorMapID()!=player.getLocation().getInteriorMapID() ||
               location.getRoomID()!=player.getLocation().getRoomID() ) {
               Debug.signal( Debug.ERROR, this, "Specified door location is not in our room !! "+location );
               return;
           }

       // Is the update possible ?
          Room currentRoom = player.getMyRoom();
          Room targetRoom = null;

       // update Door
          RoomLink roomLink = currentRoom.getRoomLink( roomLinkID );
          Door door = null;
          
          if( roomLink!=null )
              door = roomLink.getDoor();

          if( door==null ) {
               Debug.signal( Debug.ERROR, this, "Specified door was not found !" );
               return;
          }
          
          if( isOpened )
              door.open();
          else
              door.close();

       // We propagate this update
          targetRoom = roomLink.getRoom1();

          if( targetRoom==currentRoom )
              targetRoom = roomLink.getRoom2();

          player.sendMessageToNearRooms( currentRoom, this, false );
          player.sendMessageToNearRooms( targetRoom, this, false );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

