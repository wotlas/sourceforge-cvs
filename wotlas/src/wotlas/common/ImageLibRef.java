/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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
 * @author aldiss
 * @see wotlas.libs.graphics2D.ImageLibrary
 */

public interface ImageLibRef extends ImageLibraryReference
{

 /*------------------------------------------------------------------------------------*/

  /** DRAWABLE PRIORITIES
   */
    public final static short MAP_PRIORITY      = 0;      // lowest priority, drawn first
    public final static short SHADOW_PRIORITY   = 5;      // shadows
    public final static short AURA_PRIORITY     = 20;     // small player auras
    public final static short OBJECT_PRIORITY   = 25;     // wotlas objects
    public final static short ONEPOWER_PRIORITY = 30;     // one power effects
    public final static short PLAYER_PRIORITY   = 50;     // players
    public final static short DOOR_PRIORITY     = 100;    // doors
    public final static short TEXT_PRIORITY     = 150;    // doors

 /*------------------------------------------------------------------------------------*/

}