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

import wotlas.common.environment.EnvironmentManager;
import wotlas.utils.MaskTools;

/** basic Interface of a Character.Each Player object possess
 * one Character object, that should (actually) be a wotChar or a rlikeChar.
 * rLikeChar = rogueLike character
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.common.Player
 * @see wotlas.libs.graphics2d.Drawable
 */

public abstract class WotCharacter extends BasicChar {

    /** return enviroment type : Actually are RogueLike or Wheel of Time
     *
     */
    @Override
    public byte getEnvironment() {
        return EnvironmentManager.ENVIRONMENT_WOT;
    }

    public void InitWotData() {
        this.setLevel(1);
        this.setGold(100);
        this.setExp(1);
        this.setCharAttr(CharData.ATTR_HUNGER, 100);
        this.setCharAttr(CharData.ATTR_THIRSTY, 100);
        this.setCharAttr(CharData.ATTR_HP, 10);
        this.setCharAttr(CharData.ATTR_MOVEMENT, 100);
    }

    /**
    * return data to show in plugin panel attributesPlugin
    * it's the same for all wotlas classes, 
    * change for Rogue Like classes, and 
    * any other diffent environment class.
    */
    @Override
    public int[] showMaskCharAttributes() {
        int[] tmp = new int[CharData.ATTR_LAST_ATTR];
        tmp = MaskTools.set(tmp, CharData.ATTR_STR);
        tmp = MaskTools.set(tmp, CharData.ATTR_INT);
        tmp = MaskTools.set(tmp, CharData.ATTR_WIS);
        tmp = MaskTools.set(tmp, CharData.ATTR_CON);
        tmp = MaskTools.set(tmp, CharData.ATTR_DEX);
        tmp = MaskTools.set(tmp, CharData.ATTR_CHA);
        tmp = MaskTools.set(tmp, CharData.ATTR_HUNGER);
        tmp = MaskTools.set(tmp, CharData.ATTR_THIRSTY);
        tmp = MaskTools.set(tmp, CharData.ATTR_MANA);
        tmp = MaskTools.set(tmp, CharData.ATTR_HP);
        return tmp;
    }

    /** used to manage level gain
     */
    @Override
    public void gainLevel() {
        // add hp
        // add mana
    }

    @Override
    public void clone(BasicChar value) throws Exception {
        super.clone((CharData) value);
    }
}