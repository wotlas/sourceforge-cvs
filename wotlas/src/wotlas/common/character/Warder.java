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
import wotlas.common.objects.inventories.WarderInventory;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2D.Drawable;
import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.libs.graphics2D.drawable.AuraEffect;
import wotlas.libs.graphics2D.drawable.ShadowSprite;
import wotlas.libs.graphics2D.drawable.Sprite;
import wotlas.libs.graphics2D.drawable.SpriteDataSupplier;
import wotlas.libs.graphics2D.filter.ColorImageFilter;

/** A Warder character.
 *
 * @author Aldiss, Elann
 * @see wotlas.common.character.Male
 */

public class Warder extends Male {

    /*------------------------------------------------------------------------------------*/

    /** Warder rank
     */
    public final static String warderRank[][] = {
    //        Rank Name                Rank Symbol
    { "Youngling", "youngling-0", }, { "Tower Guard", "guard-1", }, { "Warder", "warder-2", }, { "Blade Master", "blade-3", }, };

    /** Warder rank
     */
    public final static Color warderColor[] = {
    //        Rank Color
    Color.white, new Color(184, 184, 184), new Color(160, 160, 180), new Color(140, 140, 160), };

    /** Warder Cloak Color
     */
    public final static String warderCloakColor[] = {
    //        Cloak Color
    "gray", "blue", "yellow", "red", "green", "brown", "black", };

    /*------------------------------------------------------------------------------------*/

    /** Warder status ( youngling, guard, ... ). [PUBLIC INFO]
     */
    private String characterRank;

    /** Warder cloak color ( for warders only ). [PUBLIC INFO]
     *  possible values are listed in the static array WarderCloakColor.
     */
    private String cloakColor;

    /*------------------------------------------------------------------------------------*/

    /** Current Sprite.
     */
    transient private Sprite warderSprite;

    /** Current Shadow.
     */
    transient private ShadowSprite warderShadowSprite;

    /** Current Aura.
     */
    transient private AuraEffect warderAuraEffect;

    /** ColorImageFilter for InteriorMap Sprites.
     */
    transient private ColorImageFilter filter;

    /*------------------------------------------------------------------------------------*/

    /** Constructor
     */
    public Warder() {
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

        if (this.warderSprite != null)
            return this.warderSprite;

        // 1 - Sprite Creation + Filter
        this.warderSprite = new Sprite((SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY);
        this.warderSprite.useAntialiasing(true);
        updateColorFilter();
        return this.warderSprite;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Updates the color filter that is used for the AesSedai sprite.
     */
    private void updateColorFilter() {
        if (this.warderSprite == null)
            return;

        this.filter = new ColorImageFilter();

        // 1 - Cloak Color
        if (this.cloakColor != null) {
            if (this.cloakColor.equals("brown")) {
                this.filter.addColorChangeKey(ColorImageFilter.blue, ColorImageFilter.brown);
            } else if (this.cloakColor.equals("gray")) {
                this.filter.addColorChangeKey(ColorImageFilter.blue, ColorImageFilter.gray);
            } else if (this.cloakColor.equals("green")) {
                this.filter.addColorChangeKey(ColorImageFilter.blue, ColorImageFilter.green);
            } else if (this.cloakColor.equals("yellow")) {
                this.filter.addColorChangeKey(ColorImageFilter.blue, ColorImageFilter.yellow);
            } else if (this.cloakColor.equals("red")) {
                this.filter.addColorChangeKey(ColorImageFilter.blue, ColorImageFilter.red);
            } else if (this.cloakColor.equals("black")) {
                this.filter.addColorChangeKey(ColorImageFilter.blue, ColorImageFilter.darkgray);
            }
        }

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
        this.warderSprite.setDynamicImageFilter(this.filter);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's shadow. Important: a character Drawable MUST have been created
     *  previously ( via a getDrawable call ). You don't want to create a shadow with no
     *  character, do you ?
     *
     *  @return character's Shadow Drawable.
     */
    public Drawable getShadow() {

        if (this.warderShadowSprite != null)
            return this.warderShadowSprite;

        // Shadow Creation
        String path = null;

        if (this.characterRank.equals("Youngling")) {
            path = "players-0/shadows-3/youngling-walking-1";
        } else if (this.characterRank.equals("Tower Guard")) {
            path = "players-0/shadows-3/guard-walking-2";
        } else {
            path = "players-0/shadows-3/warder-walking-3";
        }

        this.warderShadowSprite = new ShadowSprite(this.warderSprite.getDataSupplier(), new ImageIdentifier(path), ImageLibRef.SHADOW_PRIORITY, 4, 4);
        return this.warderShadowSprite;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's aura.
     *  @return character's Aura Drawable.
     */
    public Drawable getAura() {

        if (this.warderAuraEffect != null) {
            if (this.warderAuraEffect.isLive()) {
                return null; // aura still displayed on screen
            }

            this.warderAuraEffect.reset();
            return this.warderAuraEffect;
        }

        // Aura Creation
        this.warderAuraEffect = new AuraEffect(this.warderSprite.getDataSupplier(), getAuraImage(), ImageLibRef.AURA_PRIORITY, 5000);
        this.warderAuraEffect.useAntialiasing(true);
        this.warderAuraEffect.setAuraMaxAlpha(0.75f);

        if (this.characterRank.equals("Tower Guard"))
            this.warderAuraEffect.setAmplitudeLimit(2.6f);
        else if (this.characterRank.equals("Youngling"))
            this.warderAuraEffect.setAmplitudeLimit(3.1f);

        return this.warderAuraEffect;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Aura Image Identifier.
     */
    private ImageIdentifier getAuraImage() {
        // symbol selection
        String symbolName = null;

        for (int i = 0; i < Warder.warderRank.length; i++)
            if (this.characterRank.equals(Warder.warderRank[i][0])) {
                symbolName = Warder.warderRank[i][1];
                break;
            }

        if (symbolName == null)
            symbolName = Warder.warderRank[0][1]; // default if not found

        // Aura Creation
        return new ImageIdentifier("players-0/symbols-2/warder-symbols-1/" + symbolName + ".gif");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Return the character's color.
     *  @return character's color
     */
    public Color getColor() {
        for (int i = 0; i < Warder.warderRank.length; i++)
            if (this.characterRank.equals(Warder.warderRank[i][0]))
                return Warder.warderColor[i];

        return Color.black;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the WotCharacter community name.
     * @return the name of the community.
     */
    public String getCommunityName() {
        if (this.characterRank.equals("Tower Guard"))
            return "Tar Valon Army";
        return "Warder";
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
            for (int i = 0; i < Warder.warderRank.length; i++)
                if (rank.equals(Warder.warderRank[i][0])) {
                    this.characterRank = rank;
                    return; // success
                }

        this.characterRank = "unknown"; // not found
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the cloak color of the warder. If there is no cloak we return null.
     * @return color name.
     */
    public String getCloakColor() {
        return this.cloakColor;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the cloak color of the warder. If the warder is a Tower Guard or a
     *  youngling it won't be set.
     *
     * @param cloakColor cloak color name
     */
    public void setCloakColor(String cloakColor) {

        if (this.characterRank == null || (!this.characterRank.equals(Warder.warderRank[2][0]) && !this.characterRank.equals(Warder.warderRank[3][0])))
            return;

        for (int i = 0; i < Warder.warderCloakColor.length; i++)
            if (cloakColor.equals(Warder.warderCloakColor[i])) {
                this.cloakColor = cloakColor;
                return; // success
            }

        // not found
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
            if (this.warderSprite != null && this.filter != null)
                this.warderSprite.setDynamicImageFilter(this.filter);

            // We return the default Warder Image...
            String path = null;

            if (this.characterRank.equals("Youngling")) {
                path = "players-0/warder-4/youngling-walking-0";
            } else if (this.characterRank.equals("Tower Guard")) {
                path = "players-0/warder-4/guard-walking-1";
            } else {
                path = "players-0/warder-4/warder-walking-2";
            }

            return new ImageIdentifier(path);
        }

        if (this.warderSprite != null)
            this.warderSprite.setDynamicImageFilter(null); // no filter for player small image

        return imID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the fanfare sound of this character class.
     *  @return fanfare sound file name
     */
    public String getFanfareSound() {
        if (this.characterRank.equals("Tower Guard"))
            return "fanfare-tower.wav";

        return "fanfare.wav";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a new Inventory for this WotCharacter.<br>
     * In this case, it is a WarderInventory.
     * @return a new inventory for this char
     */
    public Inventory createInventory() {
        return new WarderInventory();
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

        ostream.writeBoolean(this.cloakColor != null);

        if (this.cloakColor != null)
            ostream.writeUTF(this.cloakColor);
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

        if (istream.readBoolean())
            this.cloakColor = istream.readUTF();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
