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
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.persistence.*;
import wotlas.utils.*;

import java.io.*;
import java.awt.Color;
import wotlas.common.environment.*;
import wotlas.common.character.roguelike.*;
import wotlas.common.objects.inventories.Inventory;
import wotlas.common.objects.inventories.MaleInventory;

/** basic Interface for a rogue like Character
 *
 * @author Diego
 * @see wotlas.common.Player
 * @see wotlas.common.BasicChar
 * @see wotlas.common.CharData
 * @see wotlas.libs.graphics2D.Drawable
 */
public abstract class RLikeCharacter extends BasicChar {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;
    
    protected RLikeClass myClass;
    transient protected Sprite sprite;
    transient protected FakeSprite fakeSprite;

    abstract public void RollStat();

    /** return enviroment type : Actually are RogueLike or Wheel of Time
    *
    */
    public byte getEnvironment() {
        return EnvironmentManager.ENVIRONMENT_ROGUE_LIKE;
    }
     
    public void InitRLikeData(){        
        this.setLevel(-1);
        this.setGold(100);
        this.setExp(1);
        this.setCharAttr(CharData.ATTR_HUNGER,100);
        this.setCharAttr(CharData.ATTR_THIRSTY,100);
        this.setCharAttr(CharData.ATTR_HP,10);
        this.setCharAttr(CharData.ATTR_MOVEMENT,100);
    }

    /** Returns an image for this character.
    *
    *  @return image identifier of this character.
    */
    public ImageIdentifier getImage() {
        // Default image for towns & worlds
        if( getLocation().isTown() || getLocation().isWorld() )
            return new ImageIdentifier( "players-0/players-small-images-1/player-small-0" );
        if( !getLocation().isTileMap() ){
            String path[] = { "players-0", "aes-sedai-0", "aes-sedai-walking-0" };
            return new ImageIdentifier( path );
        }        
        return null; // null otherwise, we let sub-classes redefine the rest...
    }

    /**
    * return data to show in plugin panel attributesPlugin
    * it's the same for all wotlas classes, 
    * change for Rogue Like classes, and 
    * any other diffent environment class.
    */
    public int[] showMaskCharAttributes() {
        int[] tmp = new int[ATTR_LAST_ATTR/4];
        tmp = MaskTools.set( tmp, ATTR_STR );
        tmp = MaskTools.set( tmp, ATTR_INT );
        tmp = MaskTools.set( tmp, ATTR_WIS );
        tmp = MaskTools.set( tmp, ATTR_CON );
        tmp = MaskTools.set( tmp, ATTR_DEX );
        tmp = MaskTools.set( tmp, ATTR_CHA );
        tmp = MaskTools.set( tmp, ATTR_HUNGER );
        tmp = MaskTools.set( tmp, ATTR_THIRSTY );
        tmp = MaskTools.set( tmp, ATTR_MANA );
        tmp = MaskTools.set( tmp, ATTR_HP );
        tmp = MaskTools.set( tmp, ATTR_MOVEMENT );
        return tmp;
    }
    
    public void setClass( RLikeClass myClass ) {
        this.myClass = myClass;
        this.myClass.init(this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal( objectOutput );
        objectOutput.writeObject( myClass );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal( objectInput );
            myClass = (RLikeClass) objectInput.readObject();
            myClass.setMyChar(this);
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
        super.writeExternal( objectOutput );
        objectOutput.writeObject( myClass );
    }
    
    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal( objectInput );
            myClass = (RLikeClass) objectInput.readObject();
            myClass.setMyChar(this);
        } else {
            // to do.... when new version
        }
    }

  /** To get the hair color of the human player.
   */
    public String getHairColor() {
       return null; 
    }

  /** To set the hair color of the human player. If the hair color given
   *  doesn't exist in our list we set it as "unknown".
   */
    public void setHairColor( String hairColor ) {
    }
    
    public void setCharacterRank(String value) {
    }

    public String getCharacterRank() {
        return null;
    }
    
    public Color getColor() {
        return null;
    }

    public Drawable getAura() {
        return null;
    }
    
    public Drawable getShadow() {
        return null;
    }

    public String getCommunityName() {
        return null;
    }

    /** Returns the fanfare sound of this character class.
    *  @return fanfare sound file name
    */
    public String getFanfareSound() {
        return "fanfare-special.wav";
    }
    
    /** used to manage level gain
     * to add hp, mana ; to add skills and spells and knowledge
     */
    public void gainLevel() {
        this.myClass.gainLevel();
    }
    
    public void clone(BasicChar value) throws Exception {
        super.clone((CharData)value);
        myClass = (RLikeClass) ((RLikeCharacter)value).myClass.getClass().newInstance();
        myClass.clone( ((RLikeCharacter)value).myClass );
        myClass.setMyChar(this);
    }
    
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a new Inventory for this WotCharacter.<br>
   * In this case, it is an AielWarriorInventory.
   * @return a new inventory for this char
   */
   public Inventory createInventory() {
      // TODO inventory
       return new MaleInventory();
   }    
}