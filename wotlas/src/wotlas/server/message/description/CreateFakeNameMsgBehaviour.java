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

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.description.*;
import wotlas.common.Player;
import wotlas.common.universe.*;
import wotlas.server.LieManager;
import wotlas.server.PlayerImpl;

/**
 * Associated behaviour to the CreateFakeNameMessage...
 *
 * @author Petrus
 */

public class CreateFakeNameMsgBehaviour extends CreateFakeNameMessage implements NetMessageBehaviour {

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public CreateFakeNameMsgBehaviour() {
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
       LieManager lieManager = player.getLieManager();

       short currentFakeName = lieManager.createFakeName(fakeName);    

       if (currentFakeName > 0)
          player.sendMessage(new SetFakeNameMessage(currentFakeName, fakeName));
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

