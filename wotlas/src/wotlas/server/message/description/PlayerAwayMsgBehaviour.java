/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
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
import wotlas.common.router.MessageRouter;
import wotlas.common.chat.*;
import wotlas.common.Player;
import wotlas.server.PlayerImpl;
import wotlas.common.message.description.*;

/**
 * Associated behaviour to the PlayerAwayMessage...
 *
 * @author Aldiss
 */

public class PlayerAwayMsgBehaviour extends PlayerAwayMessage implements NetMessageBehaviour {

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public PlayerAwayMsgBehaviour() {
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

        // is our player the dest of this message
           if( primaryKey.equals(player.getPrimaryKey()) ) {
             // we save the new away message
                player.setPlayerAwayMessage( playerAwayMessage );
                return;
           }

       // no, it's another player we want...
           if( !player.getLocation().isRoom() ) {
               Debug.signal( Debug.ERROR, this, "Location is not a room ! "+player.getLocation() );
               return;
           }

       // we search for the player via our MessageRouter
          MessageRouter mRouter = player.getMessageRouter();
          if(mRouter==null) return;

          PlayerImpl searchedPlayer = (PlayerImpl) mRouter.getPlayer( primaryKey );

            if( searchedPlayer!=null ) {
                Calendar lastTime = Calendar.getInstance();
                lastTime.setTime(new Date(searchedPlayer.getLastDisconnectedTime()));
                String awayMsg = "I was last connected on " +  Tools.getLexicalDate(lastTime)+ "<br>";
                awayMsg += searchedPlayer.getPlayerAwayMessage();
                player.sendMessage( new PlayerAwayMessage( primaryKey, awayMsg ) );
                return;
            }

          Debug.signal( Debug.WARNING, this, "Could not find player : "+primaryKey );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

