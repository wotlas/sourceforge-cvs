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

package wotlas.client.message.description;

import java.io.IOException;
import java.util.*;

import wotlas.utils.Debug;

import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.description.*;
import wotlas.common.universe.*;
import wotlas.common.Player;
import wotlas.client.*;

/**
 * Associated behaviour to the DoorsStateMessage...
 *
 * @author Aldiss
 */

public class DoorsStateMsgBehaviour extends DoorsStateMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public DoorsStateMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object context ) {

        // The context is here a DataManager
           DataManager dataManager = (DataManager) context;
           PlayerImpl myPlayer = dataManager.getMyPlayer();

        // 1 - Control
           if( !myPlayer.getLocation().isRoom() ) {
               Debug.signal( Debug.ERROR, this, "Master player is not on an InteriorMap" );
               return;
           }

           Room room = dataManager.getWorldManager().getRoom( location );

           if( room==null || room.getRoomLinks()==null ) {
               Debug.signal( Debug.WARNING, this, "Room or RoomLink not found..." );
               return;
           }

        // 2 - Update
           for( int i=0; i<roomLinkIDs.length; i++ ) {
                RoomLink roomLink = room.getRoomLink( roomLinkIDs[i] );
                Door door = null;
                
                if( roomLink!=null)
                    door = roomLink.getDoor();

                if( door==null) {
                    Debug.signal( Debug.WARNING, this, "Door not found ! "+location );
                    continue;
                }

            // Can we set directly the state of the door, without any animation ?
                if( door.getDoorDrawable()==null ) {
                    if( isOpened[i] )
                        door.open();
                    else
                        door.close();
                }
                else{
                 // set no animation
                    if( isOpened[i] )
                        ( (DoorDrawable)door.getDoorDrawable() ).setOpened();
                    else
                        ( (DoorDrawable)door.getDoorDrawable() ).setClosed();
                }
           }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

