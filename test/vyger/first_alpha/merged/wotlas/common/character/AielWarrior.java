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
import wotlas.common.objects.inventories.AielWarriorInventory;
import wotlas.common.objects.inventories.Inventory;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.drawable.AuraEffect;
import wotlas.libs.graphics2d.drawable.FakeSprite;
import wotlas.libs.graphics2d.drawable.FakeSpriteDataSupplier;
import wotlas.libs.graphics2d.drawable.ShadowSprite;
import wotlas.libs.graphics2d.drawable.Sprite;
import wotlas.libs.graphics2d.drawable.SpriteDataSupplier;
import wotlas.libs.graphics2d.filter.ColorImageFilter;

/** An Aiel Warrior character.
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.common.character.Male
 */

public class AielWarrior extends Male {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /*------------------------------------------------------------------------------------*/

    /** Aiel rank
     */
    public final static String aielRank[][] = {
    //        Rank Name                Rank Symbol
    { "Aiel Warrior", "warrior-0", }, };

    /** Aiel rank
     */
    public final static Color aielColor[] = {
    //        Rank Color
    new Color(180, 170, 90), };

    /*------------------------------------------------------------------------------------*/

    /** Aiel status ( warrior, ... ). [PUBLIC INFO]
     */
    private String characterRank;

    /*------------------------------------------------------------------------------------*/

    /** Current Sprite.
     */
    transient private Sprite aielSprite;

    /** Current Shadow.
     */
    transient private ShadowSprite aielShadowSprite;

    /** Current Aura.
     */
    transient private AuraEffect aielAuraEffect;

    /** ColorImageFilter for InteriorMap Sprites.
     */
    transient private ColorImageFilter filter;

    /*------------------------------------------------------------------------------------*/

    /** Constructor
    */
    public AielWarrior() {
    }

    /*------------------------------------------------------------------------------------*/

    /** To get a Drawable for this character. This should not be used on the
     *  server side.
     *
     *  The returned Drawable is unique : we always return the same drawable per
     *  AesSedai instance.
     *
     * @param player the player to chain the drawable to. If a XXXDataSupplier is needed
     *               we sets it to this player object.
     * @return a Drawable for this character
     */
    @Override
    public Drawable getDrawable(Player player) {
        if (!getLocation().isTileMap()) {
            if (this.aielSprite != null)
                return this.aielSprite;

            // 1 - Sprite Creation + Filter
            this.aielSprite = new Sprite((SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY);
            this.aielSprite.useAntialiasing(true);
            updateColorFilter();
            return this.aielSprite;
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

    /** Updates the color filter that is used for the AesSedai sprite.
     */
    private void updateColorFilter() {
        if (this.aielSprite == null)
            return;

        // no color filter to set...
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's shadow. Important: a character Drawable MUST have been created
     *  previously ( via a getDrawable call ). You don't want to create a shadow with no
     *  character, do you ?
     *
     *  @return character's Shadow Drawable.
     */
    @Override
    public Drawable getShadow() {

        if (this.aielShadowSprite != null)
            return this.aielShadowSprite;

        // Shadow Creation
        String path = null;

        path = "players-0/shadows-3/aiel-w-walking-7";

        this.aielShadowSprite = new ShadowSprite(this.aielSprite.getDataSupplier(), new ImageIdentifier(path), ImageLibRef.SHADOW_PRIORITY, 4, 4);
        return this.aielShadowSprite;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's aura.
     *  @return character's Aura Drawable.
     */
    @Override
    public Drawable getAura() {

        if (this.aielAuraEffect != null) {
            if (this.aielAuraEffect.isLive()) {
                return null; // aura still displayed on screen
            }

            this.aielAuraEffect.reset();
            return this.aielAuraEffect;
        }

        // Aura Creation
        this.aielAuraEffect = new AuraEffect(this.aielSprite.getDataSupplier(), getAuraImage(), ImageLibRef.AURA_PRIORITY, 5000);
        this.aielAuraEffect.useAntialiasing(true);
        this.aielAuraEffect.setAuraMaxAlpha(0.5f);
        return this.aielAuraEffect;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Aura Image Identifier.
     */
    private ImageIdentifier getAuraImage() {
        // symbol selection
        String symbolName = null;

        for (int i = 0; i < AielWarrior.aielRank.length; i++)
            if (this.characterRank.equals(AielWarrior.aielRank[i][0])) {
                symbolName = AielWarrior.aielRank[i][1];
                break;
            }

        if (symbolName == null)
            symbolName = AielWarrior.aielRank[0][1]; // default if not found

        // Aura Creation
        return new ImageIdentifier("players-0/symbols-2/aiel-symbols-5/" + symbolName + ".gif");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's color.
     *  @return character's color
     */
    @Override
    public Color getColor() {
        for (int i = 0; i < AielWarrior.aielRank.length; i++)
            if (this.characterRank.equals(AielWarrior.aielRank[i][0]))
                return AielWarrior.aielColor[i];

        return Color.black;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the WotCharacter community name.
     * @return the name of the community.
     */
    @Override
    public String getCommunityName() {
        return "Aiel";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the rank of this WotCharacter in his/her community.
     * @return the rank of this wotcharacter in his/her community.
     */
    @Override
    public String getCharacterRank() {
        return this.characterRank;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the rank of this WotCharacter in his/her community.
     *  IMPORTANT : if the rank doesnot exist it is  set to "unknown".
     *
     * @param rank the rank of this wotcharacter in his/her community.
     */
    @Override
    public void setCharacterRank(String rank) {

        if (rank != null)
            for (int i = 0; i < AielWarrior.aielRank.length; i++)
                if (rank.equals(AielWarrior.aielRank[i][0])) {
                    this.characterRank = rank;
                    return; // success
                }

        this.characterRank = "unknown"; // not found
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns an image for this character.
     *
     *  @return image identifier of this character.
     */
    @Override
    public ImageIdentifier getImage() {

        ImageIdentifier imID = super.getImage();

        if (imID == null) {
            if (this.aielSprite != null && this.filter != null)
                this.aielSprite.setDynamicImageFilter(this.filter);

            // We return the default Aiel Image...
            String path = null;

            path = "players-0/aiel-9/aiel-warrior-walking-0";

            return new ImageIdentifier(path);
        }

        if (this.aielSprite != null)
            this.aielSprite.setDynamicImageFilter(null); // no filter for player small image

        return imID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the fanfare sound of this character class.
     *  @return fanfare sound file name
     */
    @Override
    public String getFanfareSound() {
        return "fanfare-aiel.wav";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    /**
     * To get a new Inventory for this WotCharacter.<br>
     * In this case, it is an AielWarriorInventory.
     * 
     * @return a new inventory for this char
     */
    @Override
    public Inventory createInventory() {
	return new AielWarriorInventory();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** write object data with serialize.
     */
    @Override
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        super.writeExternal(objectOutput);
        objectOutput.writeUTF(this.characterRank);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    @Override
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            super.readExternal(objectInput);
            this.characterRank = objectInput.readUTF();
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
        objectOutput.writeUTF(this.characterRank);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data from a stream to recive data.
     */
    @Override
    public void readObject(java.io.ObjectInputStream objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            super.readExternal(objectInput);
            this.characterRank = objectInput.readUTF();
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

        setCharClass(CharData.CLASSES_WOT_AIEL_WARRIOR);

        this.setCharAttr(CharData.ATTR_STR, 10);
        this.setCharAttr(CharData.ATTR_INT, 10);
        this.setCharAttr(CharData.ATTR_WIS, 10);
        this.setCharAttr(CharData.ATTR_CON, 10);
        this.setCharAttr(CharData.ATTR_DEX, 10);
        this.setCharAttr(CharData.ATTR_CHA, 10);
    }
}