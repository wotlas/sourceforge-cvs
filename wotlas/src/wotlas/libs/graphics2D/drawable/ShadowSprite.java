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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import wotlas.libs.graphics2D.Drawable;
import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.libs.graphics2D.ImageLibrary;

/** Represents the shadow of a Sprite. This is an animated shadow. It must have the
 *  SAME number of images in its animation as its associated Sprite. The shadow transparency
 *  (alpha) is initially set to 25%.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.drawable.Sprite
 */

public class ShadowSprite extends Drawable {

    /*------------------------------------------------------------------------------------*/

    /** The SpriteDataSupplier we take our data from.
     */
    private SpriteDataSupplier dataSupplier;

    /** Our shadow Image Identifier.
     */
    private ImageIdentifier shadowImage;

    /** delta from dataSupplier.getX() getY() for shadow's x,y position
     */
    private int deltaX, deltaY;

    /** Shadow Alpha
     */
    private float shadowAlpha;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. The anchor point for rotations is the center of the shadow sprite.
     *  The given imageShadow must have the same number of images ( in its animation )
     *  as the SpriteDataSupplier's one.
     *
     * @param dataSupplier Sprite's data supplier we use to align our shadow.
     * @param shadowImage shadow image
     * @param priority sprite's priority
     * @param deltaX delta from dataSupplier.getX() for shadow's x position 
     * @param deltaY delta from dataSupplier.getY() for shadow's y position 
     */
    public ShadowSprite(SpriteDataSupplier dataSupplier, ImageIdentifier shadowImage, short priority, int deltaX, int deltaY) {
        super();
        this.dataSupplier = dataSupplier;
        this.priority = priority;
        this.shadowImage = shadowImage;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.shadowAlpha = 0.25f;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To change the shadow's alpha component.
     * @param shadowAlpha new shadow alpha component, default was 25% of visibility.
     */
    public void setShadowAlpha(float shadowAlpha) {
        this.shadowAlpha = shadowAlpha;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To initialize this drawable with the ImageLibrary. Don't call it yourself ! it's
     *  done automatically when you call addDrawable on the GraphicsDirector.
     *
     *  IF you need the ImageLib for some special inits just extend this method and don't
     *  forget to call a super.init(imageLib) !
     *
     *  @param imagelib ImageLibrary where you can take the images to display.
     */
    @Override
    protected void init(ImageLibrary imageLib) {
        super.init(imageLib);
        tick();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Paint method called by the GraphicsDirector. The specified rectangle represents
     *  the displayed screen in background cordinates ( see GraphicsDirector ).
     *
     *  @param gc graphics 2D use for display (double buffering is handled by the
     *         GraphicsDirector)
     *  @param screen display zone of the graphicsDirector, in background coordinates.
     */
    @Override
    public void paint(Graphics2D gc, Rectangle screen) {

        // 1 - Need to display this sprite ?
        if (!this.r.intersects(screen))
            return;

        // 2 - any affine transform ?
        AffineTransform affTr = null;

        if (this.dataSupplier.getAngle() != 0.0) {
            // Rotation Transformation
            affTr = new AffineTransform();
            int anchorX = this.r.x + this.r.width / 2;
            int anchorY = this.r.y + this.r.height / 2;

            affTr.rotate(this.dataSupplier.getAngle(), anchorX - screen.x, anchorY - screen.y);
        }

        if (this.dataSupplier.getScaleX() != 1.0 || this.dataSupplier.getScaleY() != 1.0) {
            // Scale Transformation
            if (affTr == null)
                affTr = new AffineTransform();

            affTr.scale(this.dataSupplier.getScaleX(), this.dataSupplier.getScaleY());
        }

        // 3 - Alpha
        gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.shadowAlpha));

        // 4 - image display
        if (affTr == null) {
            gc.drawImage(getImageLibrary().getImage(this.shadowImage), this.r.x - screen.x, this.r.y - screen.y, null);
        } else {
            affTr.translate(this.r.x - screen.x, this.r.y - screen.y);

            gc.drawImage(getImageLibrary().getImage(this.shadowImage), affTr, null);
        }

        // 5 - alpha cleaning
        gc.setComposite(AlphaComposite.SrcOver);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Tick method called by the GraphicsDirector. This tick method has a returned value
     *  that indicates if the drawable is still living or must be deleted. Some Drawables
     *  always return "still living", it is then the task of the program that uses
     *  the GraphicsDirector to manage the destruction of drawables.
     *
     *  @return true if the drawable is "live", false if it must be deleted.
     */
    @Override
    public boolean tick() {

        // 1 - Current Image Index
        this.shadowImage.setImageId(this.dataSupplier.getImageIdentifier().getImageId());

        // 2 - We update our rectangle
        this.r.x = this.dataSupplier.getX() + this.deltaX;
        this.r.y = this.dataSupplier.getY() + this.deltaY;

        BufferedImage bufIm = this.imageLib.getImage(this.shadowImage);

        this.r.width = bufIm.getWidth(null);
        this.r.height = bufIm.getHeight(null);

        return true; // no update needed, a sprite is always "live" by default.
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
