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
 * Associated behaviour to the PathUpdateMovementMessage...
 *
 * @author Aldiss
 */

public class RoomPlayerDataMsgBehaviour extends RoomPlayerDataMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public RoomPlayerDataMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object context ) {
           if (DataManager.SHOW_DEBUG)
             System.out.println("ROOM PLAYER DATA MESSAGE "+location);

        // The context is here a DataManager.
           DataManager dataManager = (DataManager) context;
           PlayerImpl myPlayer = dataManager.getMyPlayer();

           if(myPlayer.getLocation()==null) {
              Debug.signal( Debug.ERROR, this, "No location set !" );
              return;
           }


       // We search for the location specified...
          if( myPlayer.getLocation().isRoom() )
          {
              Room myRoom = myPlayer.getMyRoom();       
              if( myRoom==null ) {
if (DataManager.SHOW_DEBUG)
System.out.println("ROOM IS NULLLLLLLLLLLLLLLLL !!!!!");
              	return;
              }

           // is this Room on the same map as ours ?
              WotlasLocation myLocation = myPlayer.getLocation();
              
              if( myLocation.getWorldMapID()!=location.getWorldMapID() ||
                  myLocation.getTownMapID()!=location.getTownMapID() ||
                  myLocation.getBuildingID()!=location.getBuildingID() ||
                  myLocation.getInteriorMapID()!=location.getInteriorMapID() )
              {
                 Debug.signal( Debug.WARNING, this, "Received message with far location" );
                 return;
              }

           // Search in Current Room
              if( myRoom.getRoomID() == location.getRoomID() ) {
                  merge( dataManager );
if (DataManager.SHOW_DEBUG)
System.out.println("END OF ROOM MESSAGE");
                  return;  // success
              }

           // Search in other rooms
              if(myRoom.getRoomLinks()==null) {
if (DataManager.SHOW_DEBUG)
System.out.println("END OF ROOM MESSAGE");
              	  return; // not found
              }

              for( int i=0; i<myRoom.getRoomLinks().length; i++ ) {
                   Room otherRoom = myRoom.getRoomLinks()[i].getRoom1();
                   
                   if( otherRoom==myRoom )
                       otherRoom = myRoom.getRoomLinks()[i].getRoom2();

                   if( otherRoom.getRoomID() == location.getRoomID() ) {
                       merge( dataManager );
if (DataManager.SHOW_DEBUG)                       
System.out.println("END OF ROOM MESSAGE");
                       return;  // success
                   }
              }
if (DataManager.SHOW_DEBUG)
System.out.println("END OF ROOM MESSAGE");

             return; // the room was not found near us...
          }
if (DataManager.SHOW_DEBUG)
System.out.println("END OF ROOM MESSAGE");

     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To merge our players the DataManager's hashtable...
   */
    private void merge( DataManager dataManager ) {
    	Hashtable dest = dataManager.getPlayers();

    	synchronized( dest ) {
            Iterator it = players.values().iterator();

            while( it.hasNext() ) {
                PlayerImpl playerImpl = (PlayerImpl) it.next();
            	
            	if( dest.containsKey( playerImpl.getPrimaryKey() ) )
            	    continue;

                dest.put( playerImpl.getPrimaryKey(), playerImpl );
                playerImpl.init();
                playerImpl.initVisualProperties(dataManager.getGraphicsDirector());
            }
    	}
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
