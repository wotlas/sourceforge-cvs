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
import wotlas.common.universe.*;
import wotlas.common.chat.*;
import wotlas.common.Player;
import wotlas.server.PlayerImpl;
import wotlas.common.message.description.*;

/**
 * Associated behaviour to the PlayerPastMessage...
 *
 * @author Aldiss
 */

public class PlayerPastMsgBehaviour extends PlayerPastMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public PlayerPastMsgBehaviour() {
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

        // is our player the dest of this message
           if( primaryKey.equals(player.getPrimaryKey()) ) {
             // do we have to save the past for our player
              if( player.getPlayerPast()==null || player.getPlayerPast().length()==0 )
                  player.setPlayerPast( playerPast ); // we save the past...

              return;
           }


       // no, it's another player we want...
           if( !player.getLocation().isRoom() ) {
               Debug.signal( Debug.ERROR, this, "Location is not a room ! "+player.getLocation() );
               return;
           }

       // we search for the player in our current room
          Room currentRoom = player.getMyRoom();

          Hashtable players = currentRoom.getPlayers();
          PlayerImpl searchedPlayer = null;

          searchedPlayer = (PlayerImpl) players.get( primaryKey );

            if( searchedPlayer!=null ) {
              // player found !
                 player.sendMessage( new PlayerPastMessage( primaryKey, searchedPlayer.getPlayerPast() ) );
                 return;
            }

       // We search in rooms near us
          if( currentRoom.getRoomLinks()==null ) {
              Debug.signal( Debug.WARNING, this, "Could not find player : "+primaryKey );
              return; // not found...
          }
          
          for( int i=0; i<currentRoom.getRoomLinks().length; i++ ) {
               Room otherRoom = currentRoom.getRoomLinks()[i].getRoom1();

               if( otherRoom==currentRoom )
                   otherRoom = currentRoom.getRoomLinks()[i].getRoom2();

               players = otherRoom.getPlayers();
               searchedPlayer = (PlayerImpl) players.get( primaryKey );

               if( searchedPlayer!=null ) {
              	 // player found !
              	    player.sendMessage( new PlayerPastMessage( primaryKey, searchedPlayer.getPlayerPast() ) );
              	    return;
               }
          }

       Debug.signal( Debug.WARNING, this, "Could not find player : "+primaryKey );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

