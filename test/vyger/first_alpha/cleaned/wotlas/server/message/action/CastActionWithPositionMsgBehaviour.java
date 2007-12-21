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

import wotlas.common.action.CastAction;
import wotlas.common.action.UserAction;
import wotlas.common.message.action.CastActionWithPositionMessage;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.server.PlayerImpl;

/**
 * Associated behaviour to the ActionWithPositionMessage... 
 * we check if we can do it
 *
 * @author Diego
 */
public class CastActionWithPositionMsgBehaviour extends CastActionWithPositionMessage implements NetMessageBehaviour {

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
    */
    public CastActionWithPositionMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Associated code to this Message...
    *
    * @param sessionContext an object giving specific access to other objects needed to process
    *        this message.
    */
    public void doBehaviour(Object sessionContext) {
        // try{
        // The sessionContext is here a PlayerImpl.
        PlayerImpl caster = (PlayerImpl) sessionContext;

        // we should cast <id>, at the <x,y> of <player>
        // !?!?

        // checking the targetType
        byte targetType = UserAction.TARGET_TYPE_GROUND;

        // let's try to cast
        if (CastAction.getCastAction(this.idOfAction).CanExecute(targetType, this.targetRange)) {
            CastAction.getCastAction(this.idOfAction).ExecuteToMap(caster.getBasicChar(), caster.getX(), caster.getY(), this.x, this.y);
            // do cast magic : return action id, return it works, return mana lose
        } else
            // do cast magic : return action id, return dont work, return mana lose
            //System.out.println("uhm not CAST .....");
            ;
        // } catch (Exception e) {
        //    e.printStackTrace();
        // }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}