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

package wotlas.server.message.movement;

import java.io.IOException;
import java.util.*;

import wotlas.utils.Debug;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.movement.*;
import wotlas.common.Player;
import wotlas.server.PlayerImpl;

/**
 * Associated behaviour to the PathUpdateMovementMessage...
 *
 * @author Aldiss
 */

public class PathUpdateMovementMsgBehaviour extends PathUpdateMovementMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public PathUpdateMovementMsgBehaviour() {
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

           if(primaryKey==null) {
              Debug.signal( Debug.ERROR, this, "No primary key for movement !" );
              return;
           }

           if( !player.getPrimaryKey.equals( primaryKey ) ) {
              Debug.signal( Debug.ERROR, this, "The specified primary Key is not our player one's !" );
              return;
           }

       // We update our player
          player.getMovementComposer().setUpdate( (MovementUpdateMessage)this );
       
       // We send the update to other players
          if( player.getLocation().isRoom() ) {

              Room room = player.getMyRoom();              
              if( room==null ) return;

           // Current Room
              Hashtable players = room.getPlayers();
     
              synchronized( players ) {
              	 Iterator it = players.values().iterator();
              	 
              	 while( it.hasNext() ) {
              	    PlayerImpl p = (PlayerImpl)it.next();
                    p.sendMessage( this );    	    
              	 }
              }

           // Other rooms
              if(room.getRoomLinks()==null) return;
              
              for( int i=0; i<room.getRoomLinks().length; i++ ) {
                   Room otherRoom = room.getRoomLinks()[i].getRoom1();
                   
                   if( otherRoom==room )
                       otherRoom = room.getRoomLinks()[i].getRoom2();

                   players = otherRoom.getPlayers();

                   synchronized( players ) {
              	      Iterator it = players.values().iterator();
              	 
              	      while( it.hasNext() ) {
              	          PlayerImpl p = (PlayerImpl)it.next();
                          p.sendMessage( this );
              	      }
                   }
              }

          }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

