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

package wotlas.server.message.action;

import java.io.IOException;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.description.*;
import wotlas.common.Player;
import wotlas.common.screenobject.*;
import wotlas.common.router.MessageRouter;
import wotlas.common.universe.*;
import wotlas.server.PlayerImpl;

import wotlas.common.message.action.*;
import wotlas.common.action.*;

/**
 * Associated behaviour to the ActionWithPositionMessage... 
 * we check if we can do it
 *
 * @author Diego
 */
public class BasicActionWithPositionMsgBehaviour extends BasicActionWithPositionMessage implements NetMessageBehaviour {

 /*------------------------------------------------------------------------------------*/

    /** Constructor.
    */
    public BasicActionWithPositionMsgBehaviour() {
        super();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Associated code to this Message...
    *
    * @param sessionContext an object giving specific access to other objects needed to process
    *        this message.
    */
    public void doBehaviour( Object sessionContext ) {
        // try{
        // The sessionContext is here a PlayerImpl.
        PlayerImpl caster = (PlayerImpl) sessionContext;
        
        // we should cast <id>, at the <x,y> of <player>
        
//            targetRange = UserAction.TARGET_RANGE_ANY;
        
        // checking the targetType
        byte targetType = UserAction.TARGET_TYPE_GROUND;

        // let's try to cast
        if( BasicAction.getBasicAction(idOfAction).CanExecute( targetType, targetRange ) ) {
            BasicAction.getBasicAction(idOfAction).ExecuteToMap( caster.getBasicChar()
            , caster.getX(), caster.getY()
            , x, y );        }
        else
            // do cast magic : return action id, return dont work, return mana lose
            // System.out.println("uhm nothing done .....");
            ;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}