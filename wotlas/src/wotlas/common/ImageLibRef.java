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
 /*------------------------------------------------------------------------------------*/

  /*** IMAGE CATEGORIES ***/

  /** Image category for players.
   */
    public final static short PLAYERS_CATEGORY = 0;

  /** Image category for maps.
   */
    public final static short MAPS_CATEGORY = 1;

 /*------------------------------------------------------------------------------------*/
 /*------------------------------------------------------------------------------------*/

  /*** PLAYERS CATEGORY ***/

  /** Aes Sedai Set
   */
    public final static short AES_SEDAI_SET = 0;

  /** Player's small images.
   */
    public final static short PLAYER_SMALL_IMAGES_SET = 1;

  /** Player's symbol images.
   */
    public final static short PLAYER_SYMBOL_IMAGES_SET = 2;

  /** Player's shadow images.
   */
    public final static short PLAYER_SHADOW_IMAGES_SET = 3;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Aes Sedai Set - ACTION LIST */

   /** Blue ajah Aes Sedai with golden hair, walking.
    *  Other images are obtained by color filters.
    */
     public final static short AES_BLUE_GOLDH_WALKING_ACTION = 0;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*** Player small images set - ACTION LIST ***/

   /** Player small image on town maps (& WorldMap)...
    */
     public final static short PLAYER_SMALL_IM_ACTION = 0;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*** Player symbol images set - ACTION LIST ***/

   /** Aes Sedai symbol images for small Auras...
    */
     public final static short AES_SEDAI_SYMBOL_ACTION  = 0;

     public final static short AES_AMYRLIN_SYMBOL_INDEX = 0;
     public final static short AES_YELLOW_SYMBOL_INDEX  = 1;
     public final static short AES_BROWN_SYMBOL_INDEX   = 2;
     public final static short AES_RED_SYMBOL_INDEX     = 3;
     public final static short AES_BLUE_SYMBOL_INDEX    = 4;
     public final static short AES_GREEN_SYMBOL_INDEX   = 5;
     public final static short AES_WHITE_SYMBOL_INDEX   = 6;
     public final static short AES_GREY_SYMBOL_INDEX    = 7;
     public final static short AES_ACCEPTED_SYMBOL_INDEX= 8;
     public final static short AES_NOVICE_SYMBOL_INDEX  = 9;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*** Player shadow images set - ACTION LIST ***/

   /** Aes Sedai shadow images when walking...
    */
     public final static short AES_SEDAI_WALK_SHADOW_ACTION  = 0;

 /*------------------------------------------------------------------------------------*/
 /*------------------------------------------------------------------------------------*/

  /*** MAPS CATEGORY ***/

  /** Buildings small images for town maps.
   */
    public final static short BUILDING_SMALL_SET = 0;

  /** Towns small images for world maps.
   */
    public final static short TOWN_SMALL_SET = 1;

  /** Universe Set
   */
    public final static short UNIVERSE_SET = 2;

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

   /** Tar Valon - West Gate
    */
     public final static short TARVALON_WEST_GATE_MAP_ACTION = 3;

   /** Tar Valon - North West Clearing
    */
     public final static short TARVALON_NW_CLEARING_ACTION = 4;

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

   /** Tar valon - West Gate small image.
    */
     public final static short TARVALON_WEGATE_SMALL_IM_ACTION = 1;

   /** Tar valon - NW Clearing Small image.
    */
     public final static short TARVALON_NWCLNG_SMALL_IM_ACTION = 2;

 /*------------------------------------------------------------------------------------*/
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

 /*------------------------------------------------------------------------------------*/

}