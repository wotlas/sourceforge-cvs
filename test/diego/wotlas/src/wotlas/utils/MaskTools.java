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

package wotlas.utils;

/** used to manipulate flags in int[]
 * where an index such as 32 means to set int[2] bit 0 to something
 *
 * @author Diego
 */

public class MaskTools {
    
    static public int[] set(int[] mask, int index){
        if( (mask.length * 4) >= index){
            System.out.println("Error making mask, index too high.");
            return mask;
        }
        int bitIndex = index;
        int arrayIndex = 0;
        while(true){
            if( ( bitIndex-32 ) < 0 )
                break;
            bitIndex -= 32;
            arrayIndex++;
        }
        mask[arrayIndex] = mask[arrayIndex] | ( 1 << bitIndex );
        return mask;
    }

    static public int[] reset(int[] mask, int index){
        if( (mask.length * 4) >= index){
            System.out.println("Error making mask, index too high.");
            return mask;
        }
        int bitIndex = index;
        int arrayIndex = 0;
        while(true){
            if( ( bitIndex-32 ) < 0 )
                break;
            bitIndex -= 32;
            arrayIndex++;
        }
        mask[arrayIndex] = mask[arrayIndex] & (~( 1 << bitIndex ));
        return mask;
    }
}

