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

    /* Main Class. only to check the functions.
    public static void main(String argv[]) {
       int[] test1,test2;
       test1 = new int[1];
       test2 = new int[5];
       System.out.println( "check on the 1 "+isSet(test1,3) );
       test1 = set(test1,3);
       System.out.println( "check on the 1 "+isSet(test1,3) );

       test2 = set(test2,3);
       test2 = set(test2,5);
       test2 = set(test2,12);
       test2 = set(test2,40);
       test2 = set(test2,100);
       test2 = set(test2,500);
       System.out.println( "check on the 2 "+isSet(test2,3) );
       System.out.println( "check on the 2 "+isSet(test2,5) );
       System.out.println( "check on the 2 "+isSet(test2,12) );
       System.out.println( "check on the 2 "+isSet(test2,40) );
       System.out.println( "check on the 2 "+isSet(test2,100) );
       System.out.println( "check on the 2 "+isSet(test2,500) );
       
       System.out.println( "check on the 2 ! "+isSet(test2,0) );
       System.out.println( "check on the 2 ! "+isSet(test2,1) );
       System.out.println( "check on the 2 ! "+isSet(test2,41) );

       test2 = reset(test2,100);
       System.out.println( "check on the 2 ! "+isSet(test2,100) );
    }
    */

    static public int[] set(int[] mask, int index) {
        if ((mask.length * 32) <= index) {
            System.out.println("set:Error making mask, index too high [" + index + "].");
            return mask;
        }
        int bitIndex = index;
        int arrayIndex = 0;
        while (true) {
            if ((bitIndex - 32) < 0)
                break;
            bitIndex -= 32;
            arrayIndex++;
        }
        mask[arrayIndex] = mask[arrayIndex] | (1 << bitIndex);
        return mask;
    }

    static public int[] reset(int[] mask, int index) {
        if ((mask.length * 32) <= index) {
            System.out.println("reset:Error making mask, index too high [" + index + "].");
            return mask;
        }
        int bitIndex = index;
        int arrayIndex = 0;
        while (true) {
            if ((bitIndex - 32) < 0)
                break;
            bitIndex -= 32;
            arrayIndex++;
        }
        mask[arrayIndex] = mask[arrayIndex] & (~(1 << bitIndex));
        return mask;
    }

    static public boolean isSet(int[] mask, int index) {
        if ((mask.length * 32) <= index) {
            System.out.println("isSet:Error making mask, index too high [" + index + "].");
            return false;
        }
        int bitIndex = index;
        int arrayIndex = 0;
        while (true) {
            if ((bitIndex - 32) < 0)
                break;
            bitIndex -= 32;
            arrayIndex++;
        }
        if ((mask[arrayIndex] & (1 << bitIndex)) != 0)
            return true;
        else
            return false;
    }

    static public byte set(byte mask, byte index) {
        if (8 <= index) {
            System.out.println("set:Error making mask, index too high [" + index + "].");
            return mask;
        }
        mask = (byte) (mask | (1 << index));
        return mask;
    }

    static public byte reset(byte mask, byte index) {
        if (8 <= index) {
            System.out.println("reset:Error making mask, index too high [" + index + "].");
            return mask;
        }
        mask = (byte) (mask & (~(1 << index)));
        return mask;
    }

    static public boolean isSet(byte mask, byte index) {
        if (8 <= index) {
            System.out.println("isSet:Error making mask, index too high [" + index + "].");
            return false;
        }
        if ((mask & (1 << index)) != 0)
            return true;
        else
            return false;
    }
}
