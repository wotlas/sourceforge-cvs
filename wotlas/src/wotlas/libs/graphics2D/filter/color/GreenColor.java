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

package wotlas.libs.graphics2D.filter.color;

import wotlas.libs.graphics2D.filter.ColorType;

/** Represents the "green" colors.
 *
 * @author Aldiss
 */

public class GreenColor implements ColorType {

    /*------------------------------------------------------------------------------------*/

    /** Return true if the given color is of our Color Type.
     * @param r red component
     * @param b blue component
     * @param g green component
     * @return true if it's of this color type.
     */
    public boolean isFromThisColorType(short r, short g, short b) {
        if (g > r && g > b)
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
        return ((short) (mid / 1.3f) << 16) | ((short) (max / 1.3f) << 8) | (short) (min / 1.3f);
    }

    /*------------------------------------------------------------------------------------*/

}
