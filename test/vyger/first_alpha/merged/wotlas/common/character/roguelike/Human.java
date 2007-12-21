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

package wotlas.common.character.roguelike;

import wotlas.common.ImageLibRef;
import wotlas.common.Player;
import wotlas.common.character.CharData;
import wotlas.common.character.RLikeCharacter;
import wotlas.common.environment.EnvironmentManager;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.drawable.FakeSprite;
import wotlas.libs.graphics2d.drawable.FakeSpriteDataSupplier;
import wotlas.libs.graphics2d.drawable.Sprite;
import wotlas.libs.graphics2d.drawable.SpriteDataSupplier;

/** A Human RLike Character.
 *
 * @author Diego
 * @see wotlas.common.character.RLikeCharacter
 */

public class Human extends RLikeCharacter {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /** Speed [RECONSTRUCTED INFO - NOT REPLICATED]
     */
    transient protected float speed;

    /*------------------------------------------------------------------------------------*/

    /** empty constructor
     *
     */
    public Human() {
    }

    /** used to init vars
     */
    @Override
    public void init() {
        InitCharData();
        InitRLikeData();
        setCharClass(CharData.CLASSES_RL_HUMAN);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the speed of this character.
     *
     *  @return speed in pixel/s
     */
    @Override
    public float getSpeed() {
        if (getLocation().isRoom())
            return 60.0f; // Default human speed ( 60pixel/s = 2m/s )
        else if (getLocation().isTown())
            return 10.0f;
        else if (getLocation().isTileMap())
            return 35.0f; // Default human speed ( 60pixel/s = 2m/s )
        else
            return 5.0f;
    }

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
            if (this.sprite != null)
                return this.sprite;

            // 1 - Sprite Creation + Filter
            this.sprite = new Sprite((SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY);
            this.sprite.useAntialiasing(true);
            return this.sprite;
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

    @Override
    public void RollStat() {
        this.myClass.RollStat();
    }
}