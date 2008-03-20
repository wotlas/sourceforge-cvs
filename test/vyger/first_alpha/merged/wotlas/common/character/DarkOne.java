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
import wotlas.common.ImageLibRef;
import wotlas.common.Player;
import wotlas.common.environment.EnvironmentManager;
import wotlas.common.objects.inventories.Inventory;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.drawable.FakeSprite;
import wotlas.libs.graphics2d.drawable.FakeSpriteDataSupplier;
import wotlas.libs.graphics2d.drawable.ShadowSprite;
import wotlas.libs.graphics2d.drawable.Sprite;
import wotlas.libs.graphics2d.drawable.SpriteDataSupplier;

/** The Dark One.
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.common.Player
 * @see wotlas.libs.graphics2d.Drawable
 */

public class DarkOne extends WotCharacter {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Current Sprite.
     */
    transient private Sprite doSprite;

    /** Current Shadow.
     */
    transient private ShadowSprite doShadowSprite;

    /** Constructor
    */
    public DarkOne() {
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a Drawable for this character. This should not be used on the
     *  server side.
     *
     * @param player the player to chain the drawable to. If a XXXDataSupplier is needed
     *               we sets it to this player object.
     * @return a Drawable for this character
     */
    @Override
    public Drawable getDrawable(Player player) {
        if (!getLocation().isTileMap()) {
            if (this.doSprite != null)
                return this.doSprite;

            this.doSprite = new Sprite((SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY);
            this.doSprite.useAntialiasing(true);
            return this.doSprite;
        } else {
            if (this.fakeSprite != null)
                return this.fakeSprite;
            int imageNr = 0;
            switch (EnvironmentManager.whtGraphicSetIs()) {
                case EnvironmentManager.GRAPHICS_SET_ROGUE:
                    imageNr = 0;
                    break;
                default:
                    imageNr = EnvironmentManager.getDefaultNpcImageNr();
            }

            this.fakeSprite = new FakeSprite((FakeSpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY, EnvironmentManager.getGraphics(EnvironmentManager.SET_OF_NPC)[EnvironmentManager.getDefaultPlayerImage()], imageNr);
            return this.fakeSprite;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns an image for this character, depending on the Map type.
    *
    *  @return image identifier of this character.
    */
    @Override
    public ImageIdentifier getImage() {
        // We return the default DO Image...
        String path = "players-0/dark-one-5";
        return new ImageIdentifier(path);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's shadow. Important: a character Drawable MUST have been created
     *  previously ( via a getDrawable call ). You don't want to create a shadow with no
     *  character, do you ?
     *  @return character's Shadow Drawable.
     */
    @Override
    public Drawable getShadow() {
        if (this.doShadowSprite != null)
            return this.doShadowSprite;
        // Shadow Creation
        String path = "players-0/shadows-3/dark-one-4";

        this.doShadowSprite = new ShadowSprite(this.doSprite.getDataSupplier(), new ImageIdentifier(path), ImageLibRef.SHADOW_PRIORITY, 0, 0);
        return this.doShadowSprite;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's aura.
     *  @return character's Aura Drawable.
     */
    @Override
    public Drawable getAura() {
        return null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's representative color.
    *  @return character's color.
    */
    @Override
    public Color getColor() {
        return Color.black;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the WotCharacter community name.
    * @return the name of the community.
    */
    @Override
    public String getCommunityName() {
        return "Darkness";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the rank of this WotCharacter in his/her community.
     * @return the rank of this wotcharacter in his/her community.
     */
    @Override
    public String getCharacterRank() {
        return "Great Lord";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the rank of this WotCharacter in his/her community.
     *  IMPORTANT : if the rank doesnot exist it is set to "unknown".
     *
     * @param rank the rank of this wotcharacter in his/her community.
     */
    @Override
    public void setCharacterRank(String rank) {
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the speed of this character.
    *
    *  @return speed in pixel/s
    */
    @Override
    public float getSpeed() {
        return 30.0f;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the fanfare sound of this character class.
     *  @return fanfare sound file name
     */
    @Override
    public String getFanfareSound() {
        return "fanfare-dark.wav";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * To get a new Inventory for this WotCharacter.<br>
     * In this case, it is null. The Dark One has no Inventory.
     * 
     * @return null
     */
    @Override
    public Inventory createInventory() {
	return null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    /**
     * write object data with serialize.
     */
    @Override
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        super.writeExternal(objectOutput);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    @Override
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            super.readExternal(objectInput);
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

    /** write object data to a stream to send data.
     */
    @Override
    public void writeObject(java.io.ObjectOutputStream objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        super.writeExternal(objectOutput);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data from a stream to recive data.
     */
    @Override
    public void readObject(java.io.ObjectInputStream objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            super.readExternal(objectInput);
        } else {
            // to do.... when new version
        }
    }

    /** used to store the drawable for tilemaps after creating it.
     */
    transient private FakeSprite fakeSprite;

    /** used to init vars
     */
    @Override
    public void init() {
        InitCharData();
        InitWotData();

        setCharClass(CharData.CLASSES_WOT_DARK_ONE);

        this.setCharAttr(CharData.ATTR_STR, 10);
        this.setCharAttr(CharData.ATTR_INT, 10);
        this.setCharAttr(CharData.ATTR_WIS, 10);
        this.setCharAttr(CharData.ATTR_CON, 10);
        this.setCharAttr(CharData.ATTR_DEX, 10);
        this.setCharAttr(CharData.ATTR_CHA, 10);
    }
}