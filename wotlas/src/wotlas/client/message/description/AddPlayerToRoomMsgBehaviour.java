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

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.description.*;
import wotlas.common.universe.*;
import wotlas.common.Player;
import wotlas.client.*;

/**
 * Associated behaviour to the AddPlayerToRoomMessage...
 *
 * @author Aldiss
 */

public class AddPlayerToRoomMsgBehaviour extends AddPlayerToRoomMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public AddPlayerToRoomMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object context ) {

        // The context is here a DataManager.
           DataManager dataManager = (DataManager) context;
           PlayerImpl myPlayer = dataManager.getMyPlayer();


        // 1 - Control
           if( !myPlayer.getLocation().isRoom() ) {
               Debug.signal( Debug.ERROR, this, "Master player is not on an InteriorMap" );
               return;
           }

           WotlasLocation myLocation = myPlayer.getLocation();
           Room myRoom = myPlayer.getMyRoom();

           if( myLocation.getWorldMapID()!=player.getLocation().getWorldMapID() ||
               myLocation.getTownMapID()!=player.getLocation().getTownMapID() ||
               myLocation.getBuildingID()!=player.getLocation().getBuildingID() ||
               myLocation.getInteriorMapID()!=player.getLocation().getInteriorMapID() )
           {
               Debug.signal( Debug.WARNING, this, "Received message with far location" );
               return;
           }

        // Search in Current Room
           if( myRoom.getRoomID() == player.getLocation().getRoomID() ) {
               Hashtable players = dataManager.getPlayers();
               
               synchronized( players ) {
               	  if( !players.containsKey( player.getPrimaryKey() ) ) {
               	       players.put( player.getPrimaryKey(), player );
               	       ((PlayerImpl)player).init();
               	       ((PlayerImpl)player).initVisualProperties(dataManager.getGraphicsDirector());
               	  }
               }

               return;  // success
           }

        // Search in other rooms
           if(myRoom.getRoomLinks()==null) return; // not found
              
           for( int i=0; i<myRoom.getRoomLinks().length; i++ ) {
                Room otherRoom = myRoom.getRoomLinks()[i].getRoom1();
                   
                if( otherRoom==myRoom )
                    otherRoom = myRoom.getRoomLinks()[i].getRoom2();

                if( otherRoom.getRoomID() == player.getLocation().getRoomID() ) {
                    Hashtable players = dataManager.getPlayers();

                    synchronized( players ) {
                       if( !players.containsKey( player.getPrimaryKey() ) ) {
               	           players.put( player.getPrimaryKey(), player );
                           ((PlayerImpl)player).init();
                           ((PlayerImpl)player).initVisualProperties(dataManager.getGraphicsDirector());
                       }
                    }

                   return;  // success
                }
           }

        // not found
           Debug.signal( Debug.NOTICE, this, "Player's Room is not near master's" );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

