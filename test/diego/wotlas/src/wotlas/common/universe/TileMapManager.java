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

 /** Group of graphics represents an Id+ the size of the Tiles inside the image, and the name of the image
  *
  * @author Diego
  * @see wotlas.common.universe.TileMap
  * @see wotlas.client.TileMapData
  */
 
public abstract class TileMapManager implements BackupReady {

    abstract public byte getMapType();

    abstract public byte[][][] getMapBackGroundData();

    abstract public void drawAllLayer( GraphicsDirector gDirector );
    
    abstract public void setTileMap( TileMap tileMap );
    
    abstract public byte getBasicFloorId();

    abstract public void setBasicFloorId( byte value );

    abstract public byte getBasicFloorNr();

    abstract public void setBasicFloorNr( byte value );
}