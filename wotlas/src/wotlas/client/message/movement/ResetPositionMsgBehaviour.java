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
 * Associated behaviour to the ResetPositionMessage...
 *
 * @author Aldiss
 */

public class ResetPositionMsgBehaviour extends ResetPositionMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

   /** To tell if this message is to be invoked later or not.
    */
     private boolean invokeLater = true;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public ResetPositionMsgBehaviour() {
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

        // Direct Change
           if( invokeLater ) {
             if(DataManager.SHOW_DEBUG)      
                System.out.println("RESET POSITION MESSAGE");
             
             if(primaryKey==null) {
                Debug.signal( Debug.ERROR, this, "No primary key to identify player !" );
                return;
             }

             if(!myPlayer.getPrimaryKey().equals( primaryKey ) ) {
                Debug.signal( Debug.ERROR, this, "This message is not for master player !" );
                return;
             }

             invokeLater = false;
             dataManager.invokeLater( this );
             return;
           }

       // code to invoke after the current tick :
          if (DataManager.SHOW_DEBUG)
            System.out.println("Position reseted by server !");
          Debug.signal( Debug.WARNING, this, "Position reseted by server !" );          
          myPlayer.getMovementComposer().resetMovement();
          myPlayer.setX(x);
          myPlayer.setY(y);
          myPlayer.setLocation( location );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

