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

package wotlas.libs.graphics2d;

/** An animation is just an ImageIdentifier with a current state for the 
 *  image index.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2d.ImageLibrary
 * @see wotlas.libs.graphics2d.ImageIdentifier
 */

public class Animation {

    /*------------------------------------------------------------------------------------*/

    /** An image identifier that points out our animation.
     */
    private ImageIdentifier animBase;

    /** Animation length.
     */
    private short animLength;

    /** Number of ticks before next image
     */
    private byte nbTicksBeforeNextImage;

    /** Ticks counter before next image
     */
    private byte tickCounter;

    /*------------------------------------------------------------------------------------*/

    /** Empty constructor.
     */
    public Animation() {
        this.nbTicksBeforeNextImage = 1;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with specified image identifier to use as a base for the animation.
     *  We change the image for display at each tick (nbTicksBeforeNextImage=1).
     * @param animBase image identifier of the images to use for the animation.
     * @param imLib image library from which the animation images are taken.
     */
    public Animation(ImageIdentifier animBase, ImageLibrary imLib) {
        this(animBase, imLib, (byte) 1);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with specified image identifier to use as a base for the animation
     *  and the number of ticks before we select the next image for display.
     * @param animBase image identifier of the images to use for the animation.
     * @param imLib image library from which the animation images are taken.
     * @param nbTicksBeforeNextImage number of ticks before next image.
     */
    public Animation(ImageIdentifier animBase, ImageLibrary imLib, byte nbTicksBeforeNextImage) {

        this.nbTicksBeforeNextImage = nbTicksBeforeNextImage;
        setAnimBase(animBase, imLib);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the current image identifier of the animation.
     * @return current image identifier of the animation.
     */
    public ImageIdentifier getCurrentImage() {
        return this.animBase;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** We reset the animation.
     */
    public void reset() {
        this.animBase.imageId = 0;
        this.tickCounter = 0;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Updates the animation state.
     */
    public void tick() {
        this.tickCounter++;

        if ((this.tickCounter % this.nbTicksBeforeNextImage) == 0) {
            this.animBase.imageId = (short) ((this.animBase.imageId + 1) % this.animLength);
            this.tickCounter = 0;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the animation base.
     * @return animation base.
     */
    public ImageIdentifier getAnimBase() {
        ImageIdentifier animBase = new ImageIdentifier(this.animBase);
        animBase.imageId = 0;
        return animBase;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the animation base.
     * @param animBase animation base to set.
     * @param imLib image library from which the animation images are taken.
     */
    public void setAnimBase(ImageIdentifier animBase, ImageLibrary imLib) {
        this.animLength = (short) imLib.getAnimationLength(animBase);
        this.animBase = new ImageIdentifier(animBase);
        this.animBase.imageId = 0;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}