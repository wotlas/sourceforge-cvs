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

package wotlas.common.character;

import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.common.objects.inventories.Inventory;
import wotlas.libs.graphics2D.*;
import wotlas.libs.persistence.*;
import wotlas.utils.*;

import java.io.*;
import java.awt.Color;

/** basic Interface for a rogue like Character
 *
 * @author Diego
 * @see wotlas.common.Player
 * @see wotlas.libs.graphics2D.Drawable
 */

public abstract class RLikeCharacter extends BasicChar {
     
     /** return enviroment type : Actually are RogueLike or Wheel of Time
      *
      */
     public byte getEnvironment() {
         return ENVIRONMENT_ROGUE_LIKE;
     }
     
          /**
      * return data to show in plugin panel attributesPlugin
      * it's the same for all wotlas classes, 
      * change for Rogue Like classes, and 
      * any other diffent environment class.
      */
    public int[] showMaskCharAttributes(){ 
        int[] tmp = new int[ATTR_LAST_ATTR];
        tmp = MaskTools.set( tmp, ATTR_STR );
        tmp = MaskTools.set( tmp, ATTR_INT );
        tmp = MaskTools.set( tmp, ATTR_WIS );
        tmp = MaskTools.set( tmp, ATTR_CON );
        tmp = MaskTools.set( tmp, ATTR_DEX );
        tmp = MaskTools.set( tmp, ATTR_CHA );
        tmp = MaskTools.set( tmp, ATTR_HUNGER );
        tmp = MaskTools.set( tmp, ATTR_THIRSTY );
        tmp = MaskTools.set( tmp, ATTR_MANA );
        tmp = MaskTools.set( tmp, ATTR_HP );
        return tmp;
    }
}
