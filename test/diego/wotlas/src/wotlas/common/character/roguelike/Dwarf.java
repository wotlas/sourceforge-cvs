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

import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.common.character.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.common.environment.*;

import java.io.*;

/** A Human RLike Character.
 *
 * @author Diego
 * @see wotlas.common.character.RLikeCharacter
 */

public class Dwarf extends RLikeCharacter {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

  /** Speed [RECONSTRUCTED INFO - NOT REPLICATED]
   */
    transient protected float speed;

 /*------------------------------------------------------------------------------------*/

    public Dwarf() {
        InitCharData();
        InitRLikeData();
//        classes[0] = CLASSES_RLIKE_DWARF;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the speed of this character.
   *
   *  @param playerLocation player current location
   *  @return speed in pixel/s
   */
     public float getSpeed( WotlasLocation playerLocation ) {
         if ( playerLocation.isRoom() )
              return 60.0f;  // Default human speed ( 60pixel/s = 2m/s )
         else if ( playerLocation.isTown() )
              return 10.0f;
         else if ( playerLocation.isTileMap() )
              return 35.0f;  // Default human speed ( 60pixel/s = 2m/s )
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
    public Drawable getDrawable( Player player) {
        if( !player.getLocation().isTileMap() ){
            if(sprite!=null)
                return (Drawable) sprite;

            // 1 - Sprite Creation + Filter
            sprite = new Sprite( (SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY );
            sprite.useAntialiasing(true);
            return sprite;
        }
        else {
            if(fakeSprite!=null)
                return (Drawable) fakeSprite;
            int imageNr = 0;
            switch( EnvironmentManager.whtGraphicSetIs() ){
                case EnvironmentManager.GRAPHICS_SET_ROGUE:
                    imageNr = 0;
                    break;
                default:
                    imageNr = EnvironmentManager.getDefaultNpcImageNr();
            }

            fakeSprite = new FakeSprite( (FakeSpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY
            , EnvironmentManager.getServerEnvironment().getGraphics(EnvironmentManager.SET_OF_NPC
            )[ EnvironmentManager.getDefaultPlayerImage() ], imageNr  );
            return fakeSprite;
        }
    }}