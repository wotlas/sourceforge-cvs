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
    public void doBehaviour( Object sessionContext ) {
        try{
        // The sessionContext is here a PlayerImpl.
        PlayerImpl caster = (PlayerImpl) sessionContext;
        
        // we should cast <id>, at the <x,y> of <player>
        System.out.println("casting action id:"+idOfAction+" at x,y:"+x+","+y+" at "
        +caster.getLocation().toString()+" range:"+targetRange );

        ScreenObject target = null;
        if( targetKey != "" )
            target = (ScreenObject) caster.getMyTileMap().getMessageRouter().getScreenObject( targetKey );
        
        // checking range
        if( target == null ) {
            // not so good, but good now at the moment, when we cant check for
            // different world and tilemaps
            // targetRange = UserAction.TARGET_RANGE_SAME_MAP;
            System.out.println(" error? : range = the same map "
            +" I leave the range set from the player .");
            // THAT's a GREAT PROBLEM :
            // i dont know the EXACTLY distance of the Target from the caster
            // ? we must be sure a PLAYER and a NPC cant MOVE while CASTING ?
            // but even doing this we can prevent the target from moving.....
            // however we trust the player actually for this.
        }
        else if( target.getLocation().getWorldMapID() != caster.getLocation().getWorldMapID() ) {
            targetRange = UserAction.TARGET_RANGE_ANY;
            System.out.println(" error : range = out of world");
            return;
        }
        else if( target.getLocation().getTileMapID() != caster.getLocation().getTileMapID() ) {
            targetRange = UserAction.TARGET_RANGE_MAP_ON_SAME_WORLD;
            System.out.println(" error : range = same world but different tilemap");
            return;
        }
        else {
            // THAT's a GREAT PROBLEM :
            // i dont know the EXACTLY distance of the Target from the caster
            // ? we must be sure a PLAYER and a NPC cant MOVE while CASTING ?
            // but even doing this we can prevent the target from moving.....
            // however we trust the player actually for this.
            System.out.println(" error? : range = X? on the same map "
            +" I leave the range set from the player .");
            ;
        }
        
        // checking the targetType
        byte targetType = 0;
        if( target == null ){
            targetType = UserAction.TARGET_TYPE_GROUND;
            System.out.println(" targeting the ground ");
        }
        else {
            if( target instanceof NpcOnTheScreen ){
                targetType = UserAction.TARGET_TYPE_NPC;
                System.out.println(" targeting an npc ");
            }
            else if( target instanceof ItemOnTheScreen ){
                targetType = UserAction.TARGET_TYPE_ITEM;
                System.out.println(" targeting an item ");
            }
            else {
                if( target.getPrimaryKey() == caster.getPrimaryKey() ) {
                    targetType = UserAction.TARGET_TYPE_SELF;
                    System.out.println(" targeting self ");
                }
                else {
                    targetType = UserAction.TARGET_TYPE_PLAYER;
                    System.out.println(" targeting a player ");
                }
            }
        }

        // let's try to cast
        if( CastAction.getCastAction(idOfAction).CanExecute( targetType, targetRange ) ) {
            System.out.println("yeah CAST !");
            CastAction.getCastAction(idOfAction).ExecuteToMap( caster.getLocation(), x, y );
            
            ; // do cast magic : return action id, return it works, return mana lose
        }
        else
            // do cast magic : return action id, return dont work, return mana lose
            System.out.println("uhm not CAST .....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}