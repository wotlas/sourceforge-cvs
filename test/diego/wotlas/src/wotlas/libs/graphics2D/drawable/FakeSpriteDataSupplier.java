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

package wotlas.libs.graphics2D.drawable;

import wotlas.libs.graphics2D.ImageIdentifier;

/** A FakeSpriteDataSupplier defines the data access methods needed by a FakeSprite Object.
 *  If you plan not to use rotation / scaling / alpha, etc... just return the default
 *  value that is specified in the javadoc header.
 *
 * @author MasterBob, Aldiss, Diego
 * @see wotlas.libs.graphics2D.drawable.Sprite
 */

public interface FakeSpriteDataSupplier {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the X image position.
   *
   * @return x image cordinate
   */
    public int getX();

  /** To get the Y image position.
   *
   * @return y image cordinate
   */
    public int getY();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the image identifier to use.
   *
   * @return image identifier.
   */
    public ImageIdentifier getImageIdentifier();

}

