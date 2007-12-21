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

package wotlas.libs.graphics2d.filter.color;

import wotlas.libs.graphics2d.filter.ColorType;

/** Represents the "light gray" colors.
 *
 * @author Aldiss
 */

public class WhiteColor implements ColorType {

    /*------------------------------------------------------------------------------------*/

    /** Return true if the given color is of our Color Type.
     * @param r red component
     * @param b blue component
     * @param g green component
     * @return true if it's of this color type.
     */
    public boolean isFromThisColorType(short r, short g, short b) {
        if (Math.abs(g - r) < 5 && Math.abs(b - r) < 5 && r >= 220)
            return true;
        return false;
    }

    /*------------------------------------------------------------------------------------*/

    /** Given three level of luminosity we return a color of our color type.
     * @param min min luminosity.
     * @param mid medium luminosity.
     * @param max maximum luminosity.
     * @return a rgb integer with an alpha set to 0.
     */
    public int setToColorType(short min, short mid, short max) {

        if (mid * 2 > 255)
            return 0xffffff;

        return (mid * 2) << 16 | (mid * 2) << 8 | mid * 2;
    }

    /*------------------------------------------------------------------------------------*/

}
