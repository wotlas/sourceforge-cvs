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

import java.io.*;

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
    public final static byte REDDISH_HAIR= 6;

 /*------------------------------------------------------------------------------------*/

  /** Hair color [PUBLIC INFO]
   */
    protected byte hairColor;

  /** TO ADD : other common human fields ( force, dexterity, speed, etc ... ) */

 /*------------------------------------------------------------------------------------*/

  /** Getters & Setters for persistence
   */
    public byte getHairColor() {
       return hairColor; 
    }

    public void setHairColor( byte hairColor ) {
       this.hairColor = hairColor;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Tests if the given ID is a valid hair color
    *  @param hairColor hairColor
    *  @return true if the hairColor is valid, false otherwise.
    */
    public static boolean isValidHairColor( byte hairColor ) {
    	if( hairColor>=0 && hairColor<=6 )
    	    return true;
    	return false;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /** To put the WotCharacter's data on the network stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @param publicInfoOnly if false we write the player's full description, if true
   *                     we only write public info
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void encode( DataOutputStream ostream, boolean publicInfoOnly ) throws IOException {
     	ostream.writeByte( hairColor );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To retrieve your WotCharacter's data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @param publicInfoOnly if false it means the available data is the player's full description,
   *                     if true it means we only have public info here.
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void decode( DataInputStream istream, boolean publicInfoOnly ) throws IOException {
     	hairColor = istream.readByte();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
