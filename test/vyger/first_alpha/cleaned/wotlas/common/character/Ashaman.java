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
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.drawable.AuraEffect;
import wotlas.libs.graphics2d.drawable.FakeSprite;
import wotlas.libs.graphics2d.drawable.FakeSpriteDataSupplier;
import wotlas.libs.graphics2d.drawable.ShadowSprite;
import wotlas.libs.graphics2d.drawable.Sprite;
import wotlas.libs.graphics2d.drawable.SpriteDataSupplier;
import wotlas.libs.graphics2d.filter.ColorImageFilter;

/** A Ashaman character.
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.common.character.Male
 */

public class Ashaman extends Male {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /*------------------------------------------------------------------------------------*/

    /** Ashaman rank
     */
    public final static String ashamanRank[][] = {
    //        Rank Name              Rank Symbol
    { "Soldier", "soldier-0", }, { "Dedicated", "dedicated-1", }, { "Asha'man", "ashaman-2", }, { "M'Hael", "mhael-3", }, };

    /** Ashaman rank
     */
    public final static Color ashamanColor[] = {
    //        Rank Color
    Color.white, new Color(184, 184, 184), new Color(100, 100, 100), new Color(10, 10, 10), };

    /*------------------------------------------------------------------------------------*/

    /** Ashaman status ( soldier, ... ). [PUBLIC INFO]
     */
    private String characterRank;

    /*------------------------------------------------------------------------------------*/

    /** Current Sprite.
     */
    transient private Sprite ashamanSprite;

    /** Current Shadow.
     */
    transient private ShadowSprite ashamanShadowSprite;

    /** Current Aura.
     */
    transient private AuraEffect ashamanAuraEffect;

    /** ColorImageFilter for InteriorMap Sprites.
     */
    transient private ColorImageFilter filter;

    /*------------------------------------------------------------------------------------*/

    /** Constructor
     */
    public Ashaman() {
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
     * @return a Drawable for this character.
     */
    @Override
    public Drawable getDrawable(Player player) {

        if (!getLocation().isTileMap()) {
            if (this.ashamanSprite != null)
                return this.ashamanSprite;

            // 1 - Sprite Creation + Filter
            this.ashamanSprite = new Sprite((SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY);
            this.ashamanSprite.useAntialiasing(true);
            updateColorFilter();
            return this.ashamanSprite;
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
        if (this.ashamanSprite == null)
            return;

        this.filter = new ColorImageFilter();

        // 2 - Hair Color
        if (this.hairColor.equals("brown")) {
            this.filter.addColorChangeKey(ColorImageFilter.lightYellow, ColorImageFilter.brown);
        } else if (this.hairColor.equals("black")) {
            this.filter.addColorChangeKey(ColorImageFilter.lightYellow, ColorImageFilter.darkgray);
        } else if (this.hairColor.equals("gray")) {
            this.filter.addColorChangeKey(ColorImageFilter.lightYellow, ColorImageFilter.gray);
        } else if (this.hairColor.equals("white")) {
            this.filter.addColorChangeKey(ColorImageFilter.lightYellow, ColorImageFilter.lightgray);
        } else if (this.hairColor.equals("reddish")) {
            this.filter.addColorChangeKey(ColorImageFilter.lightYellow, ColorImageFilter.red);
        } else if (this.hairColor.equals("golden")) {
            this.filter.addColorChangeKey(ColorImageFilter.lightYellow, ColorImageFilter.yellow);
        }

        // 3 - Set Filter
        this.ashamanSprite.setDynamicImageFilter(this.filter);
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

        if (this.ashamanShadowSprite != null)
            return this.ashamanShadowSprite;

        // Shadow Creation
        String path = null;

        path = "players-0/shadows-3/guard-walking-2"; // same shadow as the tower guard

        this.ashamanShadowSprite = new ShadowSprite(this.ashamanSprite.getDataSupplier(), new ImageIdentifier(path), ImageLibRef.SHADOW_PRIORITY, 4, 4);
        return this.ashamanShadowSprite;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's aura.
     *  @return character's Aura Drawable.
     */
    @Override
    public Drawable getAura() {

        if (this.ashamanAuraEffect != null) {
            if (this.ashamanAuraEffect.isLive()) {
                return null; // aura still displayed on screen
            }

            this.ashamanAuraEffect.reset();
            return this.ashamanAuraEffect;
        }

        // Aura Creation
        this.ashamanAuraEffect = new AuraEffect(this.ashamanSprite.getDataSupplier(), getAuraImage(), ImageLibRef.AURA_PRIORITY, 5000);
        this.ashamanAuraEffect.useAntialiasing(true);
        this.ashamanAuraEffect.setAuraMaxAlpha(0.65f);

        if (this.characterRank.equals("Asha'man"))
            this.ashamanAuraEffect.setAmplitudeLimit(1.6f);
        else if (this.characterRank.equals("M'Hael"))
            this.ashamanAuraEffect.setAmplitudeLimit(1.4f);

        return this.ashamanAuraEffect;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Aura Image Identifier.
     */
    private ImageIdentifier getAuraImage() {
        // symbol selection
        String symbolName = null;

        for (int i = 0; i < Ashaman.ashamanRank.length; i++)
            if (this.characterRank.equals(Ashaman.ashamanRank[i][0])) {
                symbolName = Ashaman.ashamanRank[i][1];
                break;
            }

        if (symbolName == null)
            symbolName = Ashaman.ashamanRank[0][0]; // default if not found

        // Aura Creation
        return new ImageIdentifier("players-0/symbols-2/ashaman-symbols-4/" + symbolName + ".gif");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's color.
     *  @return character's color
     */
    @Override
    public Color getColor() {
        for (int i = 0; i < Ashaman.ashamanRank.length; i++)
            if (this.characterRank.equals(Ashaman.ashamanRank[i][0]))
                return Ashaman.ashamanColor[i];

        return Color.black;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the WotCharacter community name.
     * @return the name of the community.
     */
    @Override
    public String getCommunityName() {
        return "Asha'man";
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
            for (int i = 0; i < Ashaman.ashamanRank.length; i++)
                if (rank.equals(Ashaman.ashamanRank[i][0])) {
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
            if (this.ashamanSprite != null && this.filter != null)
                this.ashamanSprite.setDynamicImageFilter(this.filter);

            // We return the default Ashaman Image...
            String path = null;

            path = "players-0/ashaman-8/ashaman-walking-0";

            return new ImageIdentifier(path);
        }

        if (this.ashamanSprite != null)
            this.ashamanSprite.setDynamicImageFilter(null); // no filter for player small image

        return imID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the fanfare sound of this character class.
     *  @return fanfare sound file name
     */
    @Override
    public String getFanfareSound() {
        if (this.characterRank.equals("M'Hael"))
            return "fanfare-special.wav";

        return "fanfare-asha.wav";
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

        setCharClass(CharData.CLASSES_WOT_ASHAMAN);

        this.setCharAttr(CharData.ATTR_STR, 10);
        this.setCharAttr(CharData.ATTR_INT, 10);
        this.setCharAttr(CharData.ATTR_WIS, 10);
        this.setCharAttr(CharData.ATTR_CON, 10);
        this.setCharAttr(CharData.ATTR_DEX, 10);
        this.setCharAttr(CharData.ATTR_CHA, 10);
    }
}