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
import java.util.HashMap;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.chat.*;
import wotlas.common.message.chat.SendTextMessage;
import wotlas.common.message.description.*;
import wotlas.common.Player;
import wotlas.common.universe.*;
import wotlas.server.DataManager;
import wotlas.server.PlayerImpl;

/**
 * Associated behaviour to the WelcomeMessage...
 *
 * @author Petrus
 */

public class WelcomeMsgBehaviour extends WelcomeMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public WelcomeMsgBehaviour() {
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

        // We create a welcome message
           String message = "/cmd:<b>Welcome to Wotlas !</b><br> ";
        
        // Get the list of online players
           HashMap onlinePlayers = DataManager.getDefaultDataManager().getAccountManager().getOnlinePlayers();
           message += "There are currently " + onlinePlayers.size() + " online players...";
        
           player.sendMessage( new SendTextMessage( player.getPrimaryKey(),
                                               player.getFullPlayerName(),
                                               ChatRoom.DEFAULT_CHAT,
                                               message,
                                               ChatRoom.NORMAL_VOICE_LEVEL ));
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

