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
import java.awt.*;

import wotlas.utils.Debug;

import wotlas.libs.sound.*;
import wotlas.libs.net.NetMessageBehaviour;

import wotlas.common.message.description.*;
import wotlas.common.universe.*;
import wotlas.common.Player;

import wotlas.client.*;
import wotlas.client.screen.*;

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
   * @param sessionContext an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object sessionContext ) {

        // The sessionContext is here a DataManager
           DataManager dataManager = (DataManager) sessionContext;
           Player searchedPlayer = (Player) dataManager.getPlayers().get(primaryKey);

        // 1 - Control
           if( searchedPlayer==null ) {
               Debug.signal( Debug.WARNING, this, "Player not found :"+primaryKey );
               return;
           }

        // 2 - Update of the player
           searchedPlayer.setPlayerPast( playerPast );

        // 3 - Update of the panel
           Component c_info = dataManager.getPlayerPanel().getTab("-info-");
           
           if( c_info==null || !(c_info instanceof InfoPanel) ) {
               Debug.signal( Debug.ERROR, this, "InfoPanel not found !");
               return;
           }

           InfoPanel infoPanel = (InfoPanel) c_info;
           infoPanel.setPlayerInfo( searchedPlayer );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

