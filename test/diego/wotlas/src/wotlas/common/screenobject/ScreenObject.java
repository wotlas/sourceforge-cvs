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

package wotlas.common.screenobject;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.filter.*;

import java.awt.Rectangle;

/**  ScreenObject
  *
  * @author Diego
 */
public abstract class ScreenObject {

 /*------------------------------------------------------------------------------------*/
    /** To get the player primary Key ( account name or any unique ID )
    *
    *  @return player primary key
    */
    abstract public String getPrimaryKey();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's primary Key ( account name or any unique ID )
    *
    *  @param primaryKey player primary key
    */
    abstract public void setPrimaryKey( String primaryKey );

    /*
    public void ScreenObject() {

    }
     */
    
    /** To get the player's drawable
    *  @return player sprite
    */
    abstract public Drawable getDrawable();
    
    /** To get player's rectangle (to test intersection)
    * it's used in world/town/interior/rooms/tilemaps sprites
    */
    abstract public Rectangle getCurrentRectangle();
    
}