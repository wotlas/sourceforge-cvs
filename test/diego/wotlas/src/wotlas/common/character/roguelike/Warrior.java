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

package wotlas.common.character.roguelike;

import wotlas.common.*;
import wotlas.common.character.*;
import wotlas.libs.persistence.*;

import wotlas.utils.*;

import java.io.*;
import java.awt.Color;

/** Super class of Rogue Like Class character, it's added into a RLikeCharacter
 *
 * @author Diego
 */
public class Warrior extends RLikeClass {
    
    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;
    
    public void init(RLikeCharacter myChar){
        super.init(myChar);
        myChar.classes = new short[1];
        myChar.classes[CharData.IDX_MAX] = 1;
        myChar.classes[CharData.IDX_ACTUAL] = 1;
        myChar.charAttributes[CharData.ATTR_HP][CharData.IDX_MAX] += 10;
        myChar.charAttributes[CharData.ATTR_HP][CharData.IDX_ACTUAL] += 10;        
        
        short[] stats;
        stats = rollStat();
        
        myChar.charAttributes[CharData.ATTR_STR][CharData.IDX_ACTUAL] = stats[0];
        myChar.charAttributes[CharData.ATTR_STR][CharData.IDX_MAX]    = stats[0];
        myChar.charAttributes[CharData.ATTR_INT][CharData.IDX_ACTUAL] = stats[4];
        myChar.charAttributes[CharData.ATTR_INT][CharData.IDX_MAX]    = stats[4];
        myChar.charAttributes[CharData.ATTR_WIS][CharData.IDX_ACTUAL] = stats[5];
        myChar.charAttributes[CharData.ATTR_WIS][CharData.IDX_MAX]    = stats[5];
        myChar.charAttributes[CharData.ATTR_CON][CharData.IDX_ACTUAL] = stats[1];
        myChar.charAttributes[CharData.ATTR_CON][CharData.IDX_MAX]    = stats[1];
        myChar.charAttributes[CharData.ATTR_DEX][CharData.IDX_ACTUAL] = stats[2];
        myChar.charAttributes[CharData.ATTR_DEX][CharData.IDX_MAX]    = stats[2];
        myChar.charAttributes[CharData.ATTR_CHA][CharData.IDX_ACTUAL] = stats[3];
        myChar.charAttributes[CharData.ATTR_CHA][CharData.IDX_MAX]    = stats[3];
        
        //        classes[0] = CLASSES_RLIKE_WARRIOR;
    }
}