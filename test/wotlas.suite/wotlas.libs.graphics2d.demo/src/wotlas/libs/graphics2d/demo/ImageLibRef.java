/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - 2002 WOTLAS Team
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
package wotlas.libs.graphics2d.demo;

/** Priorities of the objects to draw... you are free to put them wherever you want.
 */
interface ImageLibRef {

    /*------------------------------------------------------------------------------------*/
    /** DRAWABLE PRIORITIES
     */
    /** lowest priority, drawn first */
    public static final short MAP_PRIORITY = 0;
    /** shadows */
    public static final short SHADOW_PRIORITY = 5;
    /** small player auras */
    public static final short AURA_PRIORITY = 20;
    /** players */
    public static final short PLAYER_PRIORITY = 50;
    /** text displayed */
    public static final short TEXT_PRIORITY = 150;

    /*------------------------------------------------------------------------------------*/
}
