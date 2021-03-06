/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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
 * Associated behaviour to the RemovePlayerFromTileMapMessage...
 *
 * @author Aldiss
 */

public class RemovePlayerFromTileMapMsgBehaviour extends RemovePlayerFromTileMapMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public RemovePlayerFromTileMapMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to this Message...
   *
   * @param sessionContext an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object sessionContext ) {
           if (DataManager.SHOW_DEBUG)
             System.out.println("REMOVE PLAYER MESSAGE p:"+primaryKey);  

        // The sessionContext is here a DataManager.
           DataManager dataManager = (DataManager) sessionContext;
           PlayerImpl myPlayer = dataManager.getMyPlayer();

        // 1 - Control
           if( !myPlayer.getLocation().isTileMap() ) {
               Debug.signal( Debug.ERROR, this, "Master player is not on a TileMap" );
               return;
           }

           if( myPlayer.getPrimaryKey().equals( primaryKey ) ) {
               Debug.signal( Debug.ERROR, this, "ATTEMPT TO REMOVE MASTER PLAYER !!" );
               return;
           }

        // 2 - We remove the player
           Hashtable players = dataManager.getPlayers();
           PlayerImpl playerImpl = null;
           
           synchronized( players ) {
              playerImpl = (PlayerImpl) players.get( primaryKey );

              if(playerImpl==null)
                 return;

              if (DataManager.SHOW_DEBUG)                 
                  System.out.println("REMOVING PLAYER "+primaryKey);

              players.remove( primaryKey );
              dataManager.getClientScreen().getChatPanel().removePlayerFromAllchatRooms(primaryKey);
           }
                      
           playerImpl.cleanVisualProperties(dataManager.getGraphicsDirector());

           if (dataManager.getSelectedPlayerKey()!=null && primaryKey.equals(dataManager.getSelectedPlayerKey()) )
               dataManager.removeCircle();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}