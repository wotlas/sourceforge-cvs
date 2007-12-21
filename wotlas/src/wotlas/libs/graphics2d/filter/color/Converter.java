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

/** Tools to manipulate colors
 *
 * @author Petrus
 */

public class Converter {

    /*------------------------------------------------------------------------------------*/

    /** To get the blue component of a argb pixel.
     * @param argb pixel of the DirectColorModel type.
     * @return the blue component in the [0,255] range
     */
    static public short getBlue(int argb) {
        return (short) (argb & 0xff);
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the green component of a argb pixel.
     * @param argb pixel of the DirectColorModel type.
     * @return the green component in the [0,255] range
     */
    static public short getGreen(int argb) {
        return (short) ((argb & 0xff00) >> 8);
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the red component of a argb pixel.
     * @param argb pixel of the DirectColorModel type.
     * @return the red component in the [0,255] range
     */
    static public short getRed(int argb) {
        return (short) ((argb & 0xff0000) >> 16);
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the alpha component of a argb pixel.
     * @param argb pixel of the DirectColorModel type.
     * @return the alpha component in the [0,255] range
     */
    static public short getAlpha(int argb) {
        return (short) ((argb & 0xff000000) >> 24);
    }

    /*------------------------------------------------------------------------------------*/

}
