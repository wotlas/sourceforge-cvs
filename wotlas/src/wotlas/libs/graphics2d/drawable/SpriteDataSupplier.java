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

package wotlas.libs.graphics2d.drawable;

import wotlas.libs.graphics2d.ImageIdentifier;

/** A SpriteDataSupplier defines the data access methods needed by a Sprite Object.
 *  If you plan not to use rotation / scaling / alpha, etc... just return the default
 *  value that is specified in the javadoc header.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2d.drawable.Sprite
 */

public interface SpriteDataSupplier {

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

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the eventual rotation angle. 0 means no rotation.
     *
     * @return angle in radians.
     */
    public double getAngle();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the X factor for scaling... 1.0 means no X scaling
     *
     * @return X scale factor
     */
    public double getScaleX();

    /** To get the Y factor for scaling... 1.0 means no Y scaling
     *
     * @return Y scale factor
     */
    public double getScaleY();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the image's transparency ( 0.0 means invisible, 1.0 means fully visible ). 
     *
     * @return alpha
     */
    public float getAlpha();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}