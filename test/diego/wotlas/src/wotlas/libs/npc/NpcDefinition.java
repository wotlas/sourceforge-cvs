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

package wotlas.libs.npc;

import wotlas.common.universe.*;
import wotlas.common.*;
import wotlas.libs.persistence.*;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.utils.Debug;
import wotlas.common.environment.*;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**  Npc Definitions
  *
  * @author Diego
 */
public abstract class NpcDefinition implements BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;
    
 /*------------------------------------------------------------------------------------*/

    String name;
    
    /** Returns the speed of this npc
    *
    *  @param playerLocation player current location
    *  @return speed in pixel/s
    */
    abstract public float getSpeed( WotlasLocation playerLocation );

     /** return enviroment type : Actually are RogueLike or Wheel of Time
      *
      */
    abstract public byte getEnvironment();

    /** To get a Drawable for this character. This should not be used on the
    *  server side. This drawable is only used in tilemaps and is still in beta.
    * it doesnt support animations actually, however it will support the
    * change of the image from the datasupplier, to let the users change their
    * images (polymorth, disguise and so on)
    *
    * @param player the player to chain the drawable to. If a XXXDataSupplier is needed
    *               we sets it to this player object.
    * @return a Drawable for this character.
    */
    abstract public Drawable getDrawableForTileMaps( Player player );

}