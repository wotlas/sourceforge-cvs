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
 
package wotlas.common.universe;

import wotlas.libs.persistence.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.utils.*;

import java.awt.*;

 /** Group of graphics represents an Id+ the size of the Tiles inside the image, and the name of the image
  *
  * @author Diego
  * @see wotlas.common.universe.TileMap
  * @see wotlas.client.TileMapData
  */
public abstract class TileMapManager implements BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    abstract public byte getMapType();

    abstract public byte[][][] getMapBackGroundData();

    abstract public void drawAllLayer( GraphicsDirector gDirector );
    
    abstract public void setTileMap( TileMap tileMap );
    
    abstract public byte getBasicFloorId();

    abstract public void setBasicFloorId( byte value );

    abstract public byte getBasicFloorNr();

    abstract public void setBasicFloorNr( byte value );

  /** Add a new MapExit object to the array {@link #mapExits mapExits}
   *
   * @return a new MapExit object
   */
    abstract public MapExit addMapExit(ScreenRectangle r);

  /** Add a new MapExit object to the array {@link #mapExits mapExits}
   * and set his name.
   * @return a new MapExit object
   */
    abstract public MapExit addMapExit(ScreenRectangle r, String name);

  /** Add a new MapExit object to the array {@link #mapExits mapExits}
   *
   * @param me MapExit object
   */
    abstract public void addMapExit( MapExit me );

  /** Returns the MapExit which is on the side given by the specified rectangle.
   *  It's an helper for you : if your player is on a WorldMap and wants to go inside
   *  a TileMap use this method to retrieve a valid MapExit and get an insertion point.
   *
   *  The MapExit is in fact a ScreenRectangle and the so called "insertion point"
   *  should be the center of this ScreenRectangle.
   * 
   * @param rCurrent rectangle containing the player's current position, width & height
   *        the rectangle position can be anything BUT it should represent in some
   *        way the direction by which the player hits this TileMap zone.
   * @return the appropriate MapExit, null if there are no MapExits.
   */
   abstract public MapExit findTileMapExit( Rectangle fromPosition );

  /** Returns the eventual MapExit the given player is intersecting.
   *
   * @param rCurrent rectangle containing the player's current position, width & height
   * @return the ~Building:others tilemap the player is heading to (if he has reached it, or if there
   *         are any), null if none.
   */
   abstract public MapExit isIntersectingMapExit( int destX, int destY, Rectangle rCurrent );

   abstract public MapExit[] getMapExits();

   abstract public boolean[][] getMapMask();
   
   abstract public void freeMapBackGroundData();
}