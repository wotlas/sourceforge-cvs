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

package wotlas.libs.graphics2D;


/** An animation is just an ImageIdentifier with a current state for the 
 *  image index.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.ImageLibrary
 * @see wotlas.libs.graphics2D.ImageIdentifier
 */

public class Animation {

 /*------------------------------------------------------------------------------------*/

  /** An image identifier that points out our animation.
   */
    private ImageIdentifier animBase;

  /** Animation length.
   */
    private short animLength;

 /*------------------------------------------------------------------------------------*/

  /** Empty constructor.
   */
    public Animation() {
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with specified image identifier to use as a base for the animation.
   *
   * @param animBase image identifier of the images to use for the animation.
   */
    public Animation( ImageIdentifier animBase ) {
       setAnimBase( animBase );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the current image identifier of the animation.
   *
   * @return current image identifier of the animation.
   */
    public ImageIdentifier getCurrentImage() {
       return animBase;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We reset the animation.
   */
    public void reset() {
   	animBase.imageIndex = 0;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Updates the animation state.
   */
    public void tick() {
       animBase.imageIndex = (short) ( (animBase.imageIndex+1)%animLength );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the animation base.
   *
   * @return animation base.
   */
    public ImageIdentifier getAnimBase() {
       ImageIdentifier animBase = new ImageIdentifier( this.animBase );
       animBase.imageIndex = 0;
       return animBase;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the animation base.
   *
   * @param animBase animation base to set.
   */
    public void setAnimBase( ImageIdentifier animBase ) {
       this.animBase = new ImageIdentifier( animBase );
       this.animBase.imageIndex = 0;
       animLength = (short) ImageLibrary.getDefaultImageLibrary().getIndexLength( animBase );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}