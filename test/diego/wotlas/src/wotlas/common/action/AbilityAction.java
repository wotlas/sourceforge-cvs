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

import java.io.*;
import wotlas.libs.persistence.*;

/**
 *
 * @author  Diego
 */
public class AbilityAction extends UserAction {
    
    static public final int ABILITY_PICKLOCK    = 0;
    static public final int ABILITY_LAST_ABILITY= 1;
    
    static public AbilityAction[] abilityActions;

    public AbilityAction( int id, String name, String description, byte maskTarget, byte targetRange){
        this.name = name;
        this.description = description;
        this.id = id;
        this.maskTarget = maskTarget;
        this.targetRange = targetRange;
        this.ostileAction = false;
        this.effectRange = EFFECT_RANGE_NONE;
        this.maskInform = 0;
    }

    public boolean CanExecute(byte target, byte range){
        if( !super.isValidTarget(target, range) )
            return false;
        return true;
    }

    static public void InitAbilityAction(){
        abilityActions = new AbilityAction[ABILITY_LAST_ABILITY];
        
        /*
        basicActions[CAST_ADMIN_SUMMON] = new CastAction( CAST_ADMIN_SUMMON
        ,"Summon monster","Summon any monster anywhere!"
        , 1<<TARGET_GROUND
        , TARGET_RANGE_SAME_MAP
        );
         */
    }
}