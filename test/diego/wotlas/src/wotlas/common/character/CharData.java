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
import wotlas.libs.persistence.*;
import wotlas.common.objects.inventories.*;

import wotlas.utils.*;

import java.io.*;
import java.awt.Color;

/** Interface for attributes / skill
 *
 * @author Diego
 *
 * Let's make some example on how data should be used:
 *
 * . 0 should usually means data is not set (es for skills)
 * . how can use mask for attributes?
 * if an attributes it's masked as false, it cant be manipulated
 * and/or showed, this means zombie could not have int, and it
 * cant be manipulated (for example), and it means
 * WotCharacter can add new stat attributes, without adding 'em to RLikeChar
 *
 */

public abstract class CharData implements BackupReady {
    
    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /* index for array of data [x][2] */
    final static public int IDX_MAX     = 0;
    final static public int IDX_ACTUAL  = 1;

    /* list of all the classes */
    final static public int CLASSES_WOT_AES_SEDAI   = 1;
    final static public int CLASSES_WOT_WARDER      = 2;
    final static public int CLASSES_WOT_CHILDREN_OF_THE_LIGHT = 3;
    final static public int CLASSES_WOT_ASHAMAN     = 4;
    final static public int CLASSES_WOT_DARK_ONE    = 5;
    final static public int CLASSES_WOT_WOLF_BROTHER= 6;
    final static public int CLASSES_WOT_AIEL_WARRIOR= 7;
    final static public int CLASSES_RL_WARRIOR      = 8;
    
    final static public String[] CLASSES_NAMES = {
        "NULL","Wot:Aes Sedai","Wot:Warder","Wot:Children of the Light","Wot:Ashaman"
        ,"Wot:Dark One","Wot:Wolf Brother","Wot:Aiel Warrior","Rl:Warrior" };
    
    /* spells and items, will point to this constant to maniupulate data flags*/
    final static public int FLAG_INVISIBLE = 0;
    final static public int FLAG_PARALIZED = 1;
    final static public int FLAG_LAST_FLAG = 2;

    /* spells and items, will point to this constant to maniupulate data known*/
    final static public int KNOW_READ       = 0;
    final static public int KNOW_WRITE      = 1;
    final static public int KNOW_LAST_KNOWN = 2;

    /* spells and items, will point to this constant to maniupulate data skill*/
    final static public int SKILL_PICKLOCK     = 0;
    final static public int SKILL_CRAFT_ARMOR  = 1; 
    final static public int SKILL_CRAFT_WEAPON = 2; 
    final static public int SKILL_LAST_SKILL   = 3;

    /* spells and items, will point to this constant to maniupulate data attr.*/
    final static public int ATTR_EXP     = 0; // managed with special proc.
    final static public int ATTR_GOLD    = 1; // managed with special proc.
    final static public int ATTR_CLASSES = 2; // managed with special proc.
    final static public int ATTR_LEVELS  = 3; // managed with special proc.
    final static public int ATTR_HP    = 4;
    final static public int ATTR_STR   = 5;
    final static public int ATTR_INT   = 6;
    final static public int ATTR_CON   = 7;
    final static public int ATTR_WIS   = 8;
    final static public int ATTR_DEX   = 9;
    final static public int ATTR_CHA   = 10;
    final static public int ATTR_MANA  = 11;
    final static public int ATTR_GEMS1 = 12;
    final static public int ATTR_GEMS2 = 13;
    final static public int ATTR_GEMS3 = 14;
    final static public int ATTR_MOVEMENT    = 15;
    final static public int ATTR_AGE         = 16;
    final static public int ATTR_CHAR_WEIGHT = 17;
    final static public int ATTR_CHAR_HEIGHT = 18;
    final static public int ATTR_AC      = 19;
    final static public int ATTR_THAC0   = 20;
    final static public int ATTR_HITROLL = 21;         // special proc?
    final static public int ATTR_DAMROLL = 22;         // special proc?
    final static public int ATTR_SAVINGMIND   = 23;
    final static public int ATTR_SAVINGREFLEX = 24;
    final static public int ATTR_SAVINGCONST  = 25;
    final static public int ATTR_LAST_ATTR = 26;

    public int[] maskCharAttributes;
    /**
    *   list of attribues of a playe/mob with [2] data
    *   [0]=max [1]=actual
    */
    public short[][] charAttributes;

    public byte[][] levels;
    public short[] classes;
    public long[] gold;
    public long[] exp;

    public int[] maskCharSkills;
    /**
    *   list of skills/knowledge with an int value:
    *   u have it with value of x>0 or u have not it, and it's 0.
    */
    public byte[] charSkills;

    public int[] maskCharKnownledge;
    /**
    *   list of skills/knowledge with a boolean value:
    *   u have it or u have not it.
    */
    public boolean[] charKnownledge;

    public int[] maskCharFlags;
    /**
    *    it's a list of status flags, lile <invisible?>:
    *    if true, the char is invisible
    */
    public boolean[] charFlags;

    protected void InitCharData(){
        maskCharAttributes = new int[2];
        charAttributes = new short[ATTR_LAST_ATTR][2];
        levels = new byte[1][2];
        classes = new short[1];
        gold = new long[2];
        exp = new long[2];
        maskCharSkills = new int[2];
        charSkills = new byte[SKILL_LAST_SKILL];
        maskCharKnownledge = new int[2];
        charKnownledge = new boolean[KNOW_LAST_KNOWN];
        maskCharFlags = new int[2];
        charFlags = new boolean[FLAG_LAST_FLAG];
    }
    
    /**
    *   hold object in the backpack every mob/char have
    */
//    public container BackPackInventory;
    /**
    *   hold object in the body every mob/char have
    */
//     public container BodyInventory;
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns an image for this character/npc/mob, depending on the Map type.
    *
    *  @param playerLocation player current location
    *  @return image identifier of this character.
    */
    public abstract ImageIdentifier getImage( WotlasLocation playerLocation );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeObject( maskCharAttributes );
        objectOutput.writeObject( charAttributes );
        objectOutput.writeObject( levels );
        objectOutput.writeObject( classes );
        objectOutput.writeObject( gold );
        objectOutput.writeObject( exp );
        objectOutput.writeObject( maskCharSkills );
        objectOutput.writeObject( charSkills );
        objectOutput.writeObject( maskCharKnownledge );
        objectOutput.writeObject( charKnownledge );
        objectOutput.writeObject( maskCharFlags );
        objectOutput.writeObject( charFlags );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            maskCharAttributes = ( int[] ) objectInput.readObject();
            charAttributes = ( short[][] ) objectInput.readObject();
            levels = ( byte[][] ) objectInput.readObject();
            classes = ( short[] ) objectInput.readObject();
            gold = ( long[] ) objectInput.readObject();
            exp = ( long[] ) objectInput.readObject();
            maskCharSkills = (  int[]) objectInput.readObject();
            charSkills = ( byte[] ) objectInput.readObject();
            maskCharKnownledge = ( int[] ) objectInput.readObject();
            charKnownledge = ( boolean[] ) objectInput.readObject();
            maskCharFlags = ( int[] ) objectInput.readObject();
            charFlags = ( boolean[] ) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** id version of data, used in serialized persistance.
   */
    public int ExternalizeGetVersion(){
        return 1;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    public void writeObject(java.io.ObjectOutputStream objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeObject( maskCharAttributes );
        objectOutput.writeObject( charAttributes );
        objectOutput.writeObject( levels );
        objectOutput.writeObject( classes );
        objectOutput.writeObject( gold );
        objectOutput.writeObject( exp );
        objectOutput.writeObject( maskCharSkills );
        objectOutput.writeObject( charSkills );
        objectOutput.writeObject( maskCharKnownledge );
        objectOutput.writeObject( charKnownledge );
        objectOutput.writeObject( maskCharFlags );
        objectOutput.writeObject( charFlags );
    }
    
    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            maskCharAttributes = ( int[] ) objectInput.readObject();
            charAttributes = ( short[][] ) objectInput.readObject();
            levels = ( byte[][] ) objectInput.readObject();
            classes = ( short[] ) objectInput.readObject();
            gold = ( long[] ) objectInput.readObject();
            exp = ( long[] ) objectInput.readObject();
            maskCharSkills = (  int[]) objectInput.readObject();
            charSkills = ( byte[] ) objectInput.readObject();
            maskCharKnownledge = ( int[] ) objectInput.readObject();
            charKnownledge = ( boolean[] ) objectInput.readObject();
            maskCharFlags = ( int[] ) objectInput.readObject();
            charFlags = ( boolean[] ) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }
}