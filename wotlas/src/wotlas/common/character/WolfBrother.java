/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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

package wotlas.common.character;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import wotlas.common.ImageLibRef;
import wotlas.common.Player;
import wotlas.common.objects.inventories.Inventory;
import wotlas.common.objects.inventories.WolfBrotherInventory;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2D.Drawable;
import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.libs.graphics2D.drawable.AuraEffect;
import wotlas.libs.graphics2D.drawable.ShadowSprite;
import wotlas.libs.graphics2D.drawable.Sprite;
import wotlas.libs.graphics2D.drawable.SpriteDataSupplier;
import wotlas.libs.graphics2D.filter.ColorImageFilter;

/** A Wolf Brother character.
 *
 * @author Aldiss, Elann
 * @see wotlas.common.character.Male
 */

public class WolfBrother extends Male {

    /*------------------------------------------------------------------------------------*/

    /** Wolf Brother rank
     */
    public final static String wolfRank[][] = {
    //        Rank Name                Rank Symbol
    { "Wolf Friend", "wolf-0", }, };

    /** Wolf Brother rank
     */
    public final static Color wolfColor[] = {
    //        Rank Color
    new Color(140, 180, 120), };

    /*------------------------------------------------------------------------------------*/

    /** Wolf status ( wolf friend, ... ). [PUBLIC INFO]
     */
    private String characterRank;

    /*------------------------------------------------------------------------------------*/

    /** Current Sprite.
     */
    transient private Sprite wolfSprite;

    /** Current Shadow.
     */
    transient private ShadowSprite wolfShadowSprite;

    /** Current Aura.
     */
    transient private AuraEffect wolfAuraEffect;

    /** ColorImageFilter for InteriorMap Sprites.
     */
    transient private ColorImageFilter filter;

    /*------------------------------------------------------------------------------------*/

    /** Constructor
     */
    public WolfBrother() {
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
    public Drawable getDrawable(Player player) {

        if (this.wolfSprite != null)
            return this.wolfSprite;

        // 1 - Sprite Creation + Filter
        this.wolfSprite = new Sprite((SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY);
        this.wolfSprite.useAntialiasing(true);
        updateColorFilter();
        return this.wolfSprite;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Updates the color filter that is used for the AesSedai sprite.
     */
    private void updateColorFilter() {
        if (this.wolfSprite == null)
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
        } else {
            this.filter.addColorChangeKey(ColorImageFilter.lightYellow, ColorImageFilter.brown);
        }

        // 3 - Set Filter
        this.wolfSprite.setDynamicImageFilter(this.filter);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's shadow. Important: a character Drawable MUST have been created
     *  previously ( via a getDrawable call ). You don't want to create a shadow with no
     *  character, do you ?
     *
     *  @return character's Shadow Drawable.
     */
    public Drawable getShadow() {

        if (this.wolfShadowSprite != null)
            return this.wolfShadowSprite;

        // Shadow Creation
        String path = null;

        path = "players-0/shadows-3/wolf-walking-6";

        this.wolfShadowSprite = new ShadowSprite(this.wolfSprite.getDataSupplier(), new ImageIdentifier(path), ImageLibRef.SHADOW_PRIORITY, 4, 4);
        return this.wolfShadowSprite;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's aura.
     *  @return character's Aura Drawable.
     */
    public Drawable getAura() {

        if (this.wolfAuraEffect != null) {
            if (this.wolfAuraEffect.isLive()) {
                return null; // aura still displayed on screen
            }

            this.wolfAuraEffect.reset();
            return this.wolfAuraEffect;
        }

        // Aura Creation
        this.wolfAuraEffect = new AuraEffect(this.wolfSprite.getDataSupplier(), getAuraImage(), ImageLibRef.AURA_PRIORITY, 5000);
        this.wolfAuraEffect.useAntialiasing(true);
        this.wolfAuraEffect.setAuraMaxAlpha(0.75f);
        this.wolfAuraEffect.setAmplitudeLimit(0.3f);
        return this.wolfAuraEffect;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Aura Image Identifier.
     */
    private ImageIdentifier getAuraImage() {
        // symbol selection
        String symbolName = null;

        for (int i = 0; i < WolfBrother.wolfRank.length; i++)
            if (this.characterRank.equals(WolfBrother.wolfRank[i][0])) {
                symbolName = WolfBrother.wolfRank[i][1];
                break;
            }

        if (symbolName == null)
            symbolName = WolfBrother.wolfRank[0][1]; // default if not found

        // Aura Creation
        return new ImageIdentifier("players-0/symbols-2/wolf-symbols-3/" + symbolName + ".gif");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's color.
     *  @return character's color
     */
    public Color getColor() {
        for (int i = 0; i < WolfBrother.wolfRank.length; i++)
            if (this.characterRank.equals(WolfBrother.wolfRank[i][0]))
                return WolfBrother.wolfColor[i];

        return Color.black;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the WotCharacter community name.
     * @return the name of the community.
     */
    public String getCommunityName() {
        return "Wolf Brother";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the rank of this WotCharacter in his/her community.
     * @return the rank of this wotcharacter in his/her community.
     */
    public String getCharacterRank() {
        return this.characterRank;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the rank of this WotCharacter in his/her community.
     *  IMPORTANT : if the rank doesnot exist it is  set to "unknown".
     *
     * @param rank the rank of this wotcharacter in his/her community.
     */
    public void setCharacterRank(String rank) {

        if (rank != null)
            for (int i = 0; i < WolfBrother.wolfRank.length; i++)
                if (rank.equals(WolfBrother.wolfRank[i][0])) {
                    this.characterRank = rank;
                    return; // success
                }

        this.characterRank = "unknown"; // not found
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns an image for this character.
     *
     *  @param playerLocation player current location
     *  @return image identifier of this character.
     */
    @Override
    public ImageIdentifier getImage(WotlasLocation playerLocation) {

        ImageIdentifier imID = super.getImage(playerLocation);

        if (imID == null) {
            if (this.wolfSprite != null && this.filter != null)
                this.wolfSprite.setDynamicImageFilter(this.filter);

            // We return the default Wolf Brother Image...
            String path = null;

            path = "players-0/wolf-7/wolf-walking-0";

            return new ImageIdentifier(path);
        }

        if (this.wolfSprite != null)
            this.wolfSprite.setDynamicImageFilter(null); // no filter for player small image

        return imID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the fanfare sound of this character class.
     *  @return fanfare sound file name
     */
    public String getFanfareSound() {
        return "fanfare-wolf.wav";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a new Inventory for this WotCharacter.<br>
     * In this case, it is a WolfBrotherInventory.
     * @return a new inventory for this char
     */
    public Inventory createInventory() {
        return new WolfBrotherInventory();
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
    @Override
    public void encode(DataOutputStream ostream, boolean publicInfoOnly) throws IOException {
        super.encode(ostream, publicInfoOnly);
        ostream.writeUTF(this.characterRank);
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
    @Override
    public void decode(DataInputStream istream, boolean publicInfoOnly) throws IOException {
        super.decode(istream, publicInfoOnly);
        this.characterRank = istream.readUTF();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
