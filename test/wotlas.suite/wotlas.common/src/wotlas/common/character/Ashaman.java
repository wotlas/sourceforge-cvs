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
import wotlas.common.objects.inventories.AshamanInventory;
import wotlas.common.objects.inventories.Inventory;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.drawable.AuraEffect;
import wotlas.libs.graphics2d.drawable.ShadowSprite;
import wotlas.libs.graphics2d.drawable.Sprite;
import wotlas.libs.graphics2d.drawable.SpriteDataSupplier;
import wotlas.libs.graphics2d.filter.ColorImageFilter;

/** A Ashaman character.
 *
 * @author Aldiss, Elann
 * @see wotlas.common.character.Male
 */

public class Ashaman extends Male {

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
    public Drawable getDrawable(Player player) {

        if (this.ashamanSprite != null)
            return this.ashamanSprite;

        // 1 - Sprite Creation + Filter
        this.ashamanSprite = new Sprite((SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY);
        this.ashamanSprite.useAntialiasing(true);
        updateColorFilter();
        return this.ashamanSprite;
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
    public String getCommunityName() {
        return "Asha'man";
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
     *  @param playerLocation player current location
     *  @return image identifier of this character.
     */
    @Override
    public ImageIdentifier getImage(WotlasLocation playerLocation) {

        ImageIdentifier imID = super.getImage(playerLocation);

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
    public String getFanfareSound() {
        if (this.characterRank.equals("M'Hael"))
            return "fanfare-special.wav";

        return "fanfare-asha.wav";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a new Inventory for this WotCharacter.<br>
     * In this case, it is an AshamanInventory.
     * @return a new inventory for this char
     */
    public Inventory createInventory() {
        return new AshamanInventory();
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
