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

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.description.*;
import wotlas.common.Player;
import wotlas.common.universe.*;
import wotlas.server.PlayerImpl;

/**
 * Associated behaviour to the AllDataLeftPleaseMessage...
 *
 * @author Aldiss
 */

public class AllDataLeftPleaseMsgBehaviour extends AllDataLeftPleaseMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public AllDataLeftPleaseMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to this Message...
   *
   * @param sessionContext an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object sessionContext ) {

        // The sessionContext is here a PlayerImpl.
           PlayerImpl player = (PlayerImpl) sessionContext;

        // We send the player's fake names
           player.sendMessage( new YourFakeNamesMessage( player.getLieManager().getFakeNames(), player.getLieManager().getCurrentFakeName() ) );
           
        // We send the all the data left

        // 1 - PLAYER DATA
          if( player.getLocation().isRoom() ) {

              Room myRoom = player.getMyRoom();

           // Current Room
              player.sendMessage( new RoomPlayerDataMessage( player.getLocation(),
                                  player, myRoom.getPlayers() ) );

           // Other rooms
              if(myRoom.getRoomLinks()!=null)
                for( int i=0; i<myRoom.getRoomLinks().length; i++ ) {
                     Room otherRoom = myRoom.getRoomLinks()[i].getRoom1();
                    
                     if( otherRoom==myRoom )
                         otherRoom = myRoom.getRoomLinks()[i].getRoom2();

                     WotlasLocation roomLoc = new WotlasLocation( player.getLocation() );
                     roomLoc.setRoomID( otherRoom.getRoomID() );

                     player.sendMessage( new RoomPlayerDataMessage( roomLoc,
                                         player, otherRoom.getPlayers() ) );
                }
          }

       // 2 - OBJECT DATA (release 2)

     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

