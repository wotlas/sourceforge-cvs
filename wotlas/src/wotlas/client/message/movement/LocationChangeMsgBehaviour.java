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

package wotlas.client.message.movement;

import java.io.IOException;
import java.util.*;

import wotlas.utils.Debug;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.movement.*;
import wotlas.common.universe.*;
import wotlas.common.Player;
import wotlas.client.*;

/**
 * Associated behaviour to the LocationChangeMessage...
 *
 * @author Aldiss
 */

public class LocationChangeMsgBehaviour extends LocationChangeMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public LocationChangeMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object context ) {
System.out.println("LOCATION CHANGED FOR PLAYER "+primaryKey+" !");

        // The context is here a DataManager.
           DataManager dataManager = (DataManager) context;
           PlayerImpl player = dataManager.getMyPlayer();

           if(primaryKey==null) {
              Debug.signal( Debug.ERROR, this, "No primary key to identify player !" );
              return;
           }

           if( player.getPrimaryKey().equals( primaryKey ) ) {
              Debug.signal( Debug.ERROR, this, "This message is for our master player !" );
              return;
           }

        // We seek for this player
           Hashtable players = dataManager.getPlayers();
           Player uPlayer = null;
           
           if(players!=null)
              uPlayer = (Player) players.get(primaryKey);
              
           if(uPlayer==null) {
              Debug.signal( Debug.ERROR, this, "Player "+primaryKey+" not found !" );
              return;
           }
           
        // SUCCESS ! we update the player location !
           uPlayer.setLocation(location);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

