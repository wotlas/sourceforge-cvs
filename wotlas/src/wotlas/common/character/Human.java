/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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


/** A Human Wotlas Character.
 *
 * @author Aldiss
 * @see wotlas.common.character.WotCharacter
 */

public abstract class Human implements WotCharacter {

 /*------------------------------------------------------------------------------------*/

  /** Hair color
   */
    public final static byte BALD        = 0;
    public final static byte GOLDEN_HAIR = 1;
    public final static byte BROWN_HAIR  = 2;
    public final static byte BLACK_HAIR  = 3;
    public final static byte GREY_HAIR   = 4;
    public final static byte WHITE_HAIR  = 5;

 /*------------------------------------------------------------------------------------*/

  /** Hair color
   */
    private byte hairColor;

  /** TO ADD : other common human fields ( force, dexterity, speed, etc ... ) */

 /*------------------------------------------------------------------------------------*/

  /** Getters & Setters for persistence
   */
    private byte getHairColor() {
       return hairColor; 
    }

    private void setHairColor( byte hairColor ) {
       this.hairColor = hairColor;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
