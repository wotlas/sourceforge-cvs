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

  /*** IMAGE CATEGORIES ***/

  /** Image category for players.
   */
    public final static short PLAYERS_CATEGORY = 0;

  /** Image category for maps.
   */
    public final static short MAPS_CATEGORY = 1;

 /*------------------------------------------------------------------------------------*/

  /*** PLAYERS CATEGORY ***/

  /** Aes Sedai Set
   */
    public final static short AES_SEDAI_SET = 0;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Aes Sedai Set - ACTION LIST */

   /** Blue ajah Aes Sedai with golden hair, walking.
    */
     public final static short AES_BLUE_GOLDH_WALKING_ACTION = 0;


 /*------------------------------------------------------------------------------------*/

  /*** MAPS CATEGORY ***/

  /** Universe Set
   */
    public final static short UNIVERSE_SET = 0;

  /** Towns small images for world maps.
   */
    public final static short TOWN_SMALL_SET = 1;

  /** Buildings small images for town maps.
   */
    public final static short BUILDING_SMALL_SET = 2;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /*** Universe set - maps ***/

   /** RandLand
    */
     public final static short RANDLAND_MAP_ACTION = 0;

   /** Tar Valon
    */
     public final static short TARVALON_MAP_ACTION = 1;

   /** Tar Valon - White tower - level 0 (hall of the tower)
    */
     public final static short TARVALON_WHTOW_LEV0_MAP_ACTION = 2;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*** Towns small images set - ACTION LIST ***/

   /** Tar valon small image
    */
     public final static short TARVALON_SMALL_IM_ACTION = 0;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*** Building small images set - ACTION LIST ***/

   /** Tar valon - White Tower small image.
    */
     public final static short TARVALON_WHTOW_SMALL_IM_ACTION = 0;

 /*------------------------------------------------------------------------------------*/

  /** DRAWABLE PRIORITIES
   */
    public final static short MAP_PRIORITY      = 0;      // lowest priority, drawn first
    public final static short SHADOW_PRIORITY   = 5;      // shadows or small player auras
    public final static short OBJECT_PRIORITY   = 20;     // wotlas objects
    public final static short ONEPOWER_PRIORITY = 30;     // one power effects
    public final static short PLAYER_PRIORITY   = 50;     // players
    public final static short DOOR_PRIORITY     = 100;    // doors

 /*------------------------------------------------------------------------------------*/

}