/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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

package wotlas.common.objects;

/** used to define what position an object can take:
 * on the map, in a container, on a player, on my shoulded.
 * @author Diego
 */

public class ItemPosition {

    static public final int MAP = 0;
    static public final int CONTAINER = 1;
    static public final int PLAYER = 2;
    static public final int NPC = 3;
    static public final int SHOP = 4;
    static public final int HEAD = 5;
    static public final int NECK = 6;
    static public final int BODY = 7;
    static public final int AROUND_BODY = 8;
    static public final int HOLD = 9;
    static public final int SHIELD = 10;
    static public final int ARMS = 11;
    static public final int WRIST = 12;
    static public final int HANDS = 13;
    static public final int FINGER = 14;
    static public final int WAIST = 15;
    static public final int LEGS = 16;
    static public final int FEET = 17;
    static public final int FACE = 18;

    static public final int DEFAULT_ITEM_STAT = ItemPosition.MAP | ItemPosition.CONTAINER | ItemPosition.PLAYER | ItemPosition.NPC | ItemPosition.SHOP;
}
