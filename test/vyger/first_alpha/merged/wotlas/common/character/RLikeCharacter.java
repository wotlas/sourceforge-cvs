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

import java.awt.Color;
import wotlas.common.character.roguelike.RLikeClass;
import wotlas.common.environment.EnvironmentManager;
import wotlas.common.objects.inventories.Inventory;
import wotlas.common.objects.inventories.MaleInventory;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.drawable.FakeSprite;
import wotlas.libs.graphics2d.drawable.Sprite;
import wotlas.utils.MaskTools;

/** basic Interface for a rogue like Character
 *
 * @author Diego
 * @see wotlas.common.Player
 * @see wotlas.common.BasicChar
 * @see wotlas.common.CharData
 * @see wotlas.libs.graphics2d.Drawable
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
    @Override
    public byte getEnvironment() {
        return EnvironmentManager.ENVIRONMENT_ROGUE_LIKE;
    }

    public void InitRLikeData() {
        this.setLevel(-1);
        this.setGold(100);
        this.setExp(1);
        this.setCharAttr(CharData.ATTR_HUNGER, 100);
        this.setCharAttr(CharData.ATTR_THIRSTY, 100);
        this.setCharAttr(CharData.ATTR_HP, 10);
        this.setCharAttr(CharData.ATTR_MOVEMENT, 100);
    }

    /** Returns an image for this character.
    *
    *  @return image identifier of this character.
    */
    @Override
    public ImageIdentifier getImage() {
        // Default image for towns & worlds
        if (getLocation().isTown() || getLocation().isWorld())
            return new ImageIdentifier("players-0/players-small-images-1/player-small-0");
        if (!getLocation().isTileMap()) {
            String path[] = { "players-0", "aes-sedai-0", "aes-sedai-walking-0" };
            return new ImageIdentifier(path);
        }
        return null; // null otherwise, we let sub-classes redefine the rest...
    }

    /**
    * return data to show in plugin panel attributesPlugin
    * it's the same for all wotlas classes, 
    * change for Rogue Like classes, and 
    * any other diffent environment class.
    */
    @Override
    public int[] showMaskCharAttributes() {
        int[] tmp = new int[CharData.ATTR_LAST_ATTR / 4];
        tmp = MaskTools.set(tmp, CharData.ATTR_STR);
        tmp = MaskTools.set(tmp, CharData.ATTR_INT);
        tmp = MaskTools.set(tmp, CharData.ATTR_WIS);
        tmp = MaskTools.set(tmp, CharData.ATTR_CON);
        tmp = MaskTools.set(tmp, CharData.ATTR_DEX);
        tmp = MaskTools.set(tmp, CharData.ATTR_CHA);
        tmp = MaskTools.set(tmp, CharData.ATTR_HUNGER);
        tmp = MaskTools.set(tmp, CharData.ATTR_THIRSTY);
        tmp = MaskTools.set(tmp, CharData.ATTR_MANA);
        tmp = MaskTools.set(tmp, CharData.ATTR_HP);
        tmp = MaskTools.set(tmp, CharData.ATTR_MOVEMENT);
        return tmp;
    }

    public void setClass(RLikeClass myClass) {
        this.myClass = myClass;
        this.myClass.init(this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** write object data with serialize.
     */
    @Override
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        super.writeExternal(objectOutput);
        objectOutput.writeObject(this.myClass);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    @Override
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            super.readExternal(objectInput);
            this.myClass = (RLikeClass) objectInput.readObject();
            this.myClass.setMyChar(this);
        } else {
            // to do.... when new version
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** id version of data, used in serialized persistance.
     */
    @Override
    public int ExternalizeGetVersion() {
        return 1;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    @Override
    public void writeObject(java.io.ObjectOutputStream objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        super.writeExternal(objectOutput);
        objectOutput.writeObject(this.myClass);
    }

    @Override
    public void readObject(java.io.ObjectInputStream objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            super.readExternal(objectInput);
            this.myClass = (RLikeClass) objectInput.readObject();
            this.myClass.setMyChar(this);
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
    public void setHairColor(String hairColor) {
    }

    @Override
    public void setCharacterRank(String value) {
    }

    @Override
    public String getCharacterRank() {
        return null;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public Drawable getAura() {
        return null;
    }

    @Override
    public Drawable getShadow() {
        return null;
    }

    @Override
    public String getCommunityName() {
        return null;
    }

    /** Returns the fanfare sound of this character class.
    *  @return fanfare sound file name
    */
    @Override
    public String getFanfareSound() {
        return "fanfare-special.wav";
    }

    /** used to manage level gain
     * to add hp, mana ; to add skills and spells and knowledge
     */
    @Override
    public void gainLevel() {
        this.myClass.gainLevel();
    }

    @Override
    public void clone(BasicChar value) throws Exception {
        super.clone((CharData) value);
        this.myClass = ((RLikeCharacter) value).myClass.getClass().newInstance();
        this.myClass.clone(((RLikeCharacter) value).myClass);
        this.myClass.setMyChar(this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    /**
     * To get a new Inventory for this WotCharacter.<br>
     * In this case, it is an MaleInventory.
     * 
     * @return a new inventory for this char
     */
    @Override
    public Inventory createInventory() {
	// TODO inventory
	return new MaleInventory();
    }
}