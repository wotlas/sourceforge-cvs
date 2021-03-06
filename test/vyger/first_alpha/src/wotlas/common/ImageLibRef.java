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

package wotlas.common;

import wotlas.libs.graphics2D.ImageLibraryReference;

/**
 * Ids for an easier use of the ImageLibrary...
 *
 * @author aldiss, diego
 * @see wotlas.libs.graphics2D.ImageLibrary
 */

public interface ImageLibRef extends ImageLibraryReference
{

 /*------------------------------------------------------------------------------------*/

  /** DRAWABLE PRIORITIES
   */
    public final static short MAP_PRIORITY      = 0;        // lowest priority, drawn first
    public final static short SECONDARY_MAP_PRIORITY        = 3;
    public final static short HOUSE_FLOOR_PRIORITY          = 5;
    public final static short CARPET_FLOOR_PRIORITY         = 7;
    public final static short PRIMARY_WALL_ANGLE_PRIORITY   = 9;
    public final static short PRIMARY_WALL_POS1_PRIORITY    = 11;
    public final static short PRIMARY_WALL_POS2_PRIORITY    = 13;
    public final static short PRIMARY_WALL_END_PRIORITY     = 15;
    public final static short CENTRAL_FLOOR_PRIORITY        = 17;
    public final static short SHADOW_PRIORITY   = 30;       // shadows
    public final static short AURA_PRIORITY     = 50;       // small player auras
    public final static short OBJECT_PRIORITY   = 70;       // wotlas objects
    public final static short PLAYER_PRIORITY   = 90;      // players
    public final static short ONEPOWER_PRIORITY = 110;       // one power effects
    public final static short DOOR_PRIORITY     = 130;      // doors
    public final static short WAVE_PRIORITY     = 150;      // waves when the player emits sounds
    public final static short SECONDARY_WALL_ANGLE_PRIORITY = 170;
    public final static short SECONDARY_WALL_POS1_PRIORITY  = 172;
    public final static short SECONDARY_WALL_POS2_PRIORITY  = 174;
    public final static short SECONDARY_WALL_END_PRIORITY   = 176;
    public final static short TEXT_PRIORITY     = 200;

 /*------------------------------------------------------------------------------------*/

}