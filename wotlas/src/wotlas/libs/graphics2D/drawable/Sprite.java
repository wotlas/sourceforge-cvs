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
import wotlas.libs.graphics2D.DrawableOwner;
import wotlas.libs.graphics2D.DynamicImageFilter;
import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.libs.graphics2D.ImageLibrary;

/** A Sprite is mainly an image displayed on the GraphicsDirector. The sprite data is 
 *  given by an object implementing the SpriteDataSupplier interface. Because a sprite
 *  has no Image field but an ImageIdentifier, it can then represent an animation if
 *  the imageIdentifier changes on each tick. You can use an Animation object to manage
 *  the animation.
 *
 * @author MasterBob, Aldiss, Petrus
 * @see wotlas.libs.graphics2D.drawable.SpriteDataSupplier
 */

public class Sprite extends Drawable implements DrawableOwner {

    /*------------------------------------------------------------------------------------*/

    /** Rotation Anchor Point set to sprite's center.
     */
    public final static byte CENTER_ANCHOR_POINT = 0;

    /** Rotation Anchor Point set to left upper corner.
     */
    public final static byte UPPER_LEFT_ANCHOR_POINT = 1;

    /** Rotation Anchor Point set to right upper corner.
     */
    public final static byte UPPER_RIGHT_ANCHOR_POINT = 2;

    /** Rotation Anchor Point set to right lower corner.
     */
    public final static byte LOWER_RIGHT_ANCHOR_POINT = 3;

    /** Rotation Anchor Point set to left upper corner.
     */
    public final static byte LOWER_LEFT_ANCHOR_POINT = 4;

    /*------------------------------------------------------------------------------------*/

    /** Our SpriteDataSupplier.
     */
    private SpriteDataSupplier dataSupplier;

    /** Current Image Identifier.
     */
    private ImageIdentifier image;

    /** Our anchor mode for rotations ( see static fields : CENTER_ANCHOR_POINT ).
     */
    private byte anchorMode;

    /** Eventual Dynamic Image Filters
     */
    private DynamicImageFilter[] imageFilters;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. The anchor mode for rotations is set to CENTER_ANCHOR_POINT.
     *
     * @param dataSupplier Sprite's data supplier
     * @param priority sprite's priority
     */
    public Sprite(SpriteDataSupplier dataSupplier, short priority) {
        this(dataSupplier, priority, Sprite.CENTER_ANCHOR_POINT);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with anchor mode ( see public static fields ). 
     *
     * @param dataSupplier Sprite's data supplier
     * @param priority sprite's priority
     * @param anchorMode tells which anchor point to use for rotations ( CENTER_ANCHOR_POINT, ... )
     */
    public Sprite(SpriteDataSupplier dataSupplier, short priority, byte anchorMode) {
        super();
        this.dataSupplier = dataSupplier;
        this.priority = priority;
        this.anchorMode = anchorMode;
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

    /** To change the data supplier.
     * @param dataSupplier new data Supplier.
     */
    public void setDataSupplier(SpriteDataSupplier dataSupplier) {
        this.dataSupplier = dataSupplier;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the data supplier.
     * @return the sprite's data Supplier.
     */
    public SpriteDataSupplier getDataSupplier() {
        return this.dataSupplier;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To add a DynamicImageFilter to this Sprite. The filter will be called to create
     *  a new filtered BufferedImage before rendering.
     *  @param imageFilter a DynamicImageFilter.
     */
    public void setDynamicImageFilter(DynamicImageFilter imageFilter) {
        if (imageFilter == null)
            return;

        if (this.imageFilters == null) {
            this.imageFilters = new DynamicImageFilter[1];
            this.imageFilters[0] = imageFilter;
            return;
        }

        // Search for an existing filter of same class        
        Class filterClass = imageFilter.getClass();
        for (int i = 0; i < this.imageFilters.length; i++) {
            if (filterClass.isInstance(this.imageFilters[i])) {
                this.imageFilters[i] = imageFilter;
                return;
            }
        }

        // No existing similar filter found : create a new one        
        DynamicImageFilter[] myImageFilters = new DynamicImageFilter[this.imageFilters.length + 1];
        System.arraycopy(this.imageFilters, 0, myImageFilters, 0, this.imageFilters.length);
        myImageFilters[this.imageFilters.length] = imageFilter;
        this.imageFilters = myImageFilters;
    }

    /** To remove a previously set dynamic image filter
     *
     * @param filterClass class name of filter to remove
     */
    public void unsetDynamicImageFilter(String filterClass) {
        int i = 0;
        DynamicImageFilter imageFilter = this.imageFilters[0];
        while (!filterClass.equals(imageFilter.getClass().getName()) && i < this.imageFilters.length) {
            i++;
            imageFilter = this.imageFilters[i];
        }
        if (i < this.imageFilters.length) {
            DynamicImageFilter[] myImageFilters = new DynamicImageFilter[this.imageFilters.length - 1];
            System.arraycopy(this.imageFilters, 0, myImageFilters, 0, i);
            System.arraycopy(this.imageFilters, i + 1, myImageFilters, i, this.imageFilters.length - 1 - i);
        }
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
            int anchorX = 0, anchorY = 0;

            switch (this.anchorMode) {
                default:
                case CENTER_ANCHOR_POINT:
                    anchorX = this.r.x + this.r.width / 2;
                    anchorY = this.r.y + this.r.height / 2;
                    break;

                case UPPER_LEFT_ANCHOR_POINT:
                    anchorX = this.r.x;
                    anchorY = this.r.y;
                    break;

                case UPPER_RIGHT_ANCHOR_POINT:
                    anchorX = this.r.x + this.r.width;
                    anchorY = this.r.y;
                    break;

                case LOWER_RIGHT_ANCHOR_POINT:
                    anchorX = this.r.x + this.r.width;
                    anchorY = this.r.y + this.r.height;
                    break;

                case LOWER_LEFT_ANCHOR_POINT:
                    anchorX = this.r.x;
                    anchorY = this.r.y + this.r.height;
                    break;
            }

            affTr.rotate(this.dataSupplier.getAngle(), anchorX - screen.x, anchorY - screen.y);
        }

        if (this.dataSupplier.getScaleX() != 1.0 || this.dataSupplier.getScaleY() != 1.0) {
            // Scale Transformation
            if (affTr == null)
                affTr = new AffineTransform();

            affTr.scale(this.dataSupplier.getScaleX(), this.dataSupplier.getScaleY());
            this.r.x += (int) ((this.r.width - this.r.width * this.dataSupplier.getScaleX()) / 2);
            this.r.y += (int) ((this.r.height - this.r.height * this.dataSupplier.getScaleY()) / 2);
            this.r.width = (int) (this.r.width * this.dataSupplier.getScaleX());
            this.r.height = (int) (this.r.height * this.dataSupplier.getScaleY());
        }

        // 3 - Any alpha ?
        if (this.dataSupplier.getAlpha() != 1.0f)
            gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.dataSupplier.getAlpha()));

        // 4 - image display
        BufferedImage bufIm = this.imageLib.getImage(this.image);

        /*if( imageFilter!=null )
            bufIm = imageFilter.filterImage( bufIm );
        
        BrightnessFilter brightnessFilter = new BrightnessFilter();
        brightnessFilter.setBrightness((short) 230);
        bufIm = brightnessFilter.filterImage( bufIm );*/

        if (this.imageFilters != null) {
            for (int i = 0; i < this.imageFilters.length; i++) {
                bufIm = this.imageFilters[i].filterImage(bufIm);
            }
        }

        if (affTr == null)
            gc.drawImage(bufIm, this.r.x - screen.x, this.r.y - screen.y, null);
        else {
            affTr.translate(this.r.x - screen.x, this.r.y - screen.y);
            gc.drawImage(bufIm, affTr, null);
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

        this.r.x = this.dataSupplier.getX();
        this.r.y = this.dataSupplier.getY();

        this.image = this.dataSupplier.getImageIdentifier();

        BufferedImage bufIm = this.imageLib.getImage(this.image);

        this.r.width = bufIm.getWidth(null);
        this.r.height = bufIm.getHeight(null);

        return true; // no update needed and a sprite is always "live" by default.
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the owner of this drawable. By 'owner' we mean the object which this
     *  Sprite is the graphical representation, i.e the SpriteDataSupplier.
     *
     * @return Object owner of this drawable : the given SpriteDataSupplier.
     */
    public Object getOwner() {
        return this.dataSupplier;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
