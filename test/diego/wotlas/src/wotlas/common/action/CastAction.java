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
import wotlas.common.screenobject.*;
import wotlas.common.action.spell.*;
import wotlas.common.universe.*;
import wotlas.server.*;
import wotlas.common.*;
import wotlas.common.router.*;

/**
 *
 * @author  Diego
 */
public class CastAction extends UserAction {

    static public final int CAST_ADMIN_SUMMON   = 0;
    static public final int CAST_ADMIN_CREATE   = 1;    
    static public final int CAST_LAST_CAST      = 2;
    
    static protected CastAction[] castActions;

    protected int manaCost;
    protected int minimumLevel;
    protected Spell spell;
    
    public CastAction( int id, String name, String description
    , byte maskTarget, byte targetRange
    , int manaCost, int minimumLevel
    , Spell spell){
        this.name = name;
        this.description = description;
        this.id = id;
        this.maskTarget = maskTarget;
        this.targetRange = targetRange;
        this.manaCost = manaCost;
        this.minimumLevel = minimumLevel;
        this.ostileAction = false;
        this.effectRange = EFFECT_RANGE_NONE;
        this.maskInform = 0;
        this.spell = spell;
    }

    public boolean CanExecute(ScreenObject user, byte targetType, byte range){
    // da attivare ma non ora....
    //    if( user.getCharData() == null )
    //            return false;
        
        // i can cast this ID? : use chardata to check it.....
        
        // i have mana? : use chardata to check it.....

        // valid target?
        return isValidTarget(targetType, range);
    }
    
    /* -------------static functions--------------------------------------- */
    static public void InitCastActions(boolean loadByServer){
        if( castActions != null )
            return;
        Spell.loadByServer = loadByServer;
        
        castActions = new CastAction[CAST_LAST_CAST];
        
        castActions[CAST_ADMIN_SUMMON] = new CastAction( CAST_ADMIN_SUMMON
        ,"Summon monster","Summon any monster anywhere!"
        , (byte)(1<<TARGET_TYPE_GROUND), TARGET_RANGE_SAME_MAP
        , 0, 0 
        , new Summon( "dwarf berserk" ) );
        
        castActions[CAST_ADMIN_CREATE] = new CastAction( CAST_ADMIN_CREATE
        ,"Create item","Create item anywhere!"
        , (byte)(1<<TARGET_TYPE_GROUND), TARGET_RANGE_SAME_MAP
        , 0, 0 
        , new Create( "oggdef1-name" ) );
        
    }

    static public CastAction getCastAction(int id){
        return castActions[id];
    }
    
    /** used by server execute the action
     *
     */
    public void ExecuteToMap(WotlasLocation loc, int x, int y) {
        spell.CastToMap(loc, x, y);
    }

}