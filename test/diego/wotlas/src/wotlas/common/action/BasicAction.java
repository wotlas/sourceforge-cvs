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
public class BasicAction extends UserAction {
    
    static public final int BASIC_MOVE_ITEM= 1; // ?
    static public final int BASIC_OPEN_ITEM= 2;
    static public final int BASIC_SLEEP    = 3;
    static public final int BASIC_EAT      = 4;
    static public final int BASIC_DRINK    = 5;
    static public final int BASIC_ENABLE_CAST   = 6;
    static public final int BASIC_ENABLE_ABILITY= 7;
    static public final int BASIC_LAST_BASIC    = 8;
    
    static public BasicAction[] basicActions;

    public BasicAction( int id, String name, String description, byte maskTarget, byte targetRange){
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

    static public void InitBasicActions(){
        basicActions = new BasicAction[BASIC_LAST_BASIC];
        
        /*
        basicActions[CAST_ADMIN_SUMMON] = new CastAction( CAST_ADMIN_SUMMON
        ,"Summon monster","Summon any monster anywhere!"
        , 1<<TARGET_GROUND
        , TARGET_RANGE_SAME_MAP
        );
         */
    }
}