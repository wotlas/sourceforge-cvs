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

import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.common.objects.inventories.Inventory;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;

import java.io.*;
import java.awt.Color;

/** basic class of a Character.Each Player object possess
 * one Character object, that should (actually) be a wotChar or a rlikeChar.
 * rLikeChar = rogueLike character
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.common.Player
 * @see wotlas.libs.graphics2D.Drawable
 */

public abstract class BasicChar extends CharData {
    
    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get a Drawable for this character. This should not be used on the
    *  server side.
    *
    * @param player the player to chain the drawable to. If a XXXDataSupplier is needed
    *               we sets it to this player object.
    * @return a Drawable for this character.
    */
      abstract public Drawable getDrawable( Player player );
      
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's representative color.
   *  @return character's color.
   */     
     abstract public Color getColor();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the WotCharacter community name.
   * @return the name of the community.
   */
     abstract public String getCommunityName();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the rank of this WotCharacter in his/her community.
   * @return the rank of this wotcharacter in his/her community.
   */
     abstract public String getCharacterRank();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the rank of this WotCharacter in his/her community.
   *  IMPORTANT : if the rank doesnot exist it is set to "unknown".
   *
   * @param rank the rank of this wotcharacter in his/her community.
   */
     abstract public void setCharacterRank( String rank );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the speed of this character.
   *
   *  @param playerLocation player current location
   *  @return speed in pixel/s
   */
     abstract public float getSpeed( WotlasLocation playerLocation );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the fanfare sound of this character class.
   *  @return fanfare sound file name
   */
     abstract public String getFanfareSound();
     
  /** To get a new Inventory for this WotCharacter.<br>
   * NB : there is at least one Inventory class for each WotCharacter implementor.
   * @return a new inventory for this char
   */
     public abstract Inventory createInventory();


  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's shadow. Important: a character Drawable MUST have been created
   *  previously ( via a getDrawable call ). You don't want to create a shadow with no
   *  character, do you ?
   *  @return character's Shadow Drawable.
   */
     public abstract Drawable getShadow();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's aura.
   *  @return character's Aura Drawable.
   */     
     public abstract Drawable getAura();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

     /**
      * return data to show in plugin panel attributesPlugin
      * it's the same for all wotlas classes, 
      * change for Rogue Like classes, and 
      * any other diffent environment class.
      */
    abstract public int[] showMaskCharAttributes();

     /** return enviroment type : Actually are RogueLike or Wheel of Time
      *
      */
    abstract public byte getEnvironment();

    /*
    abstract void public gainLevel();

    abstract void public setLevel(int level);
    */
}