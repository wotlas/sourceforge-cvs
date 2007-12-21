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
package wotlas.common.action;

import wotlas.common.character.CharData;
import wotlas.common.screenobject.ScreenObject;

/**
 *   NEAR ALL ACTION HAVE ONLY TO CHOOSE BETWEEN 2 TARGET : SELF AND NPC/PLAYER/ITEM
 *   No area target, no another map target, no world target.
 *
 *
 *
 * @author  Diego
 */
public class BasicAction extends UserAction {

    static public final int BASIC_MOVE_ITEM = 1; // ?
    static public final int BASIC_OPEN_ITEM = 2;
    static public final int BASIC_SLEEP = 3;
    static public final int BASIC_EAT = 4;
    static public final int BASIC_DRINK = 5;
    static public final int BASIC_ENABLE_CAST = 6;
    static public final int BASIC_ENABLE_ABILITY = 7;
    static public final int BASIC_ATTACK = 8;
    static public final int BASIC_LAST_BASIC = 9;

    static public BasicAction[] basicActions;

    public BasicAction(int id, String name, String description, byte maskTarget, byte targetRange) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.maskTarget = maskTarget;
        this.targetRange = targetRange;
        this.ostileAction = false;
        this.effectRange = UserAction.EFFECT_RANGE_NONE;
        this.maskInform = 0;
    }

    @Override
    public boolean CanExecute(ScreenObject user, byte targetType, byte range) {
        // if( user.getCharData() == null )
        //    return false;

        // valid target?
        return isValidTarget(targetType, range);
    }

    static public void InitBasicActions(boolean loadByServer) {
        BasicAction.basicActions = new BasicAction[BasicAction.BASIC_LAST_BASIC];

        BasicAction.basicActions[BasicAction.BASIC_ATTACK] = new BasicAction(BasicAction.BASIC_ATTACK, "Attack", "Attack the target", new Integer((1 << UserAction.TARGET_TYPE_NPC) + (1 << UserAction.TARGET_TYPE_PLAYER)).byteValue(), UserAction.TARGET_RANGE_SHORT
        //        , TARGET_RANGE_TOUCH
        );

    }

    static public BasicAction getBasicAction(int id) {
        return BasicAction.basicActions[id];
    }

    /** used by server execute the action
     *
     */
    public void ExecuteToMap(CharData caster, int casterX, int casterY, int targetX, int targetY) {
        // spell.CastToMap(caster,casterX,casterY,targetX,targetY);
    }

    public void ExecuteToTarget(CharData caster, CharData target) {
        // if action is ostile should set the attack of the 
        // target and set WHO start it : he and his group
    }

    public void ExecuteToSelf(CharData caster) {
        // if action is ostile should set the attack of the 
        // target and set WHO start it : he and his group
    }
}