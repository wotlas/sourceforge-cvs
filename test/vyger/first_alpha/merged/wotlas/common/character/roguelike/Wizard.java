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

import wotlas.common.character.CharData;
import wotlas.common.character.RLikeCharacter;

/** Super class of Rogue Like Class character, it's added into a RLikeCharacter
 *
 * @author Diego
 */
public class Wizard extends RLikeClass {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    public Wizard() {
    }

    @Override
    public void init(RLikeCharacter myChar) {
        super.init(myChar);
        myChar.setCharClass(CharData.CLASSES_RL_WIZARD);
        myChar.setCharAttr(CharData.ATTR_MANA, myChar.getCharAttrActual(CharData.ATTR_MANA) + 10);
        RollStat();
    }

    @Override
    public void RollStat() {
        short[] stats;
        stats = RLikeClass.rollStat();

        this.myChar.setCharAttr(CharData.ATTR_STR, stats[3]);
        this.myChar.setCharAttr(CharData.ATTR_INT, stats[0]);
        this.myChar.setCharAttr(CharData.ATTR_WIS, stats[4]);
        this.myChar.setCharAttr(CharData.ATTR_CON, stats[2]);
        this.myChar.setCharAttr(CharData.ATTR_DEX, stats[1]);
        this.myChar.setCharAttr(CharData.ATTR_CHA, stats[5]);
    }

    @Override
    public int getHitDice() {
        return 4;
    }
}