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

package wotlas.server.message.account;

import java.io.IOException;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.description.*;
import wotlas.client.*;

/**
 * Associated behaviour to the YourPlayerDataMessage...
 *
 * @author Aldiss
 */

public class YourPlayerDataMsgBehaviour extends YourPlayerDataMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public YourPlayerDataMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object context ) {

        // The context is a DataManager

/** Soluce 1 : all is done in a method call.

           DataManager dataManager = (DataManager) context;

           dataManager.setCurrentPlayer( player );
 **/


/** Soluce 2 : The dataManager is awaiting on a lock. We set the data and awake him.

           DataManager dataManager = (DataManager) context;

           dataManager.setCurrentPlayer( player );
           
           dataManager.getStartGameLock().notify();
 **/

// My comment : the Soluce 2 seems better as we quit as soon as the data is set...
//              ( in soluce 1 we have to wait for the server to finish its process )
//              also the DataManager can be waiting on a startGameLock.wait( TIMEOUT );
//              that allows us to display an error message after some time if the
//              server doesn't respond.
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

