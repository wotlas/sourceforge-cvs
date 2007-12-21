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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import wotlas.libs.graphics2d.Animation;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.ImageLibrary;

/** An AuraEffect is a sprite that rely on a SpriteDataSupplier. It is used to just display
 *  a rotating image on the GraphicsDirector. The image can be an animation.<br>
 *
 *  It's especially useful when you need to display that a sprite is selected.
 *
 * @author MasterBob, Aldiss
 */

public class AuraEffect extends Drawable {

    /*------------------------------------------------------------------------------------*/

    /** do we have to maintain an animation ?
     */
    private boolean hasAnimation;

    /** Eventual Animation, null if hasAnimation=false
     */
    private Animation sprAnim;

    /** Current Image Identifier.
     */
    private ImageIdentifier image;

    /** The SpriteDataSupplier we take our data from.
     */
    private SpriteDataSupplier dataSupplier;

    /**
     * The transparency of the image
     */
    private float alpha = 0.0f;

    /**
     * The angle of rotation of the image
     */
    private double angle = 0;

    /**
     * The maximum alpha of this aura effect (default is 40%)
     */
    private float auraMaxAlpha;

    /** Step Alpha when the alpha is increasing or decreasing.
     */
    private double alphaStep;

    /** TimeStamp indicating when we'll need to remove our drawable from screen.
     *  If -1 we have infinite life. The TextDrawable must be removed manually.
     */
    private long timeLimit;

    /** Display duration in ms...
     */
    private int lifeTime;

    /** To tell if the aura is disappearing or not ...
     */
    private boolean isDisappearing;

    /** To limit the rotation of this aura (in radians). If -1 there is no
     *  amplitude limit.
     */
    private float amplitudeLimit;

    /** Direction in which we turn... (+1 or -1)
     */
    private byte direction;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. IF you want the aura to be animated, just set the
     *  call the ImageIdentifier.setIsAnimation() on image and set it to true.
     *
     *  The image changes at each tick. If you want to change this behaviour use the
     *  getAnimation() method to retrieve the Animation object that was created.
     *
     * @param dataSupplier ou r reference
     * @param image image identifier to use for this sprite.
     * @param priority sprite's priority
     * @param lifeTime display duration
     */
    public AuraEffect(SpriteDataSupplier dataSupplier, ImageIdentifier image, short priority, int lifeTime) {
        super();

        this.image = image;
        this.priority = priority;
        this.dataSupplier = dataSupplier;
        this.lifeTime = lifeTime;
        this.timeLimit = System.currentTimeMillis() + lifeTime;
        this.isDisappearing = false;
        this.alpha = 0.0f;
        setAuraMaxAlpha(0.4f);
        this.amplitudeLimit = -1.0f;
        this.direction = -1;
        this.hasAnimation = false;
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

        this.r.width = getImageLibrary().getWidth(this.image);
        this.r.height = getImageLibrary().getHeight(this.image);

        if (this.image.getIsAnimation()) {
            this.sprAnim = new Animation(this.image, imageLib);
            this.hasAnimation = true;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To change the aura image.
     * @param image new image identifier
     */
    public void setImage(ImageIdentifier image) {
        this.image = image;

        if (this.imageLib != null) {
            this.r.width = getImageLibrary().getWidth(image);
            this.r.height = getImageLibrary().getHeight(image);

            if (image.getIsAnimation()) {
                this.sprAnim = new Animation(image, this.imageLib);
                this.hasAnimation = true;
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To limit the rotation of this aura (in radians). If -1 there is no
     *  amplitude limit.
     * @param amplitudeLimit amplitude limit
     */
    public void setAmplitudeLimit(float amplitudeLimit) {
        this.amplitudeLimit = amplitudeLimit;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the animation object if the given image represented an Animation.
     */
    public Animation getAnimation() {
        return this.sprAnim;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To reset the animation.
     */
    public void reset() {
        setAlpha(0.0f);
        this.timeLimit = System.currentTimeMillis() + this.lifeTime;
        this.isDisappearing = false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * set alpha;
     */
    public void setAlpha(float f) {
        this.alpha = f;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * set angle;
     */
    public void setAngle(double a) {
        this.angle = a;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the direction in which we turn...
     */
    public void setDirection(byte direction) {
        this.direction = direction;
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

        if (!this.r.intersects(screen))
            return;

        gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha));

        AffineTransform affTr = new AffineTransform();
        int anchorX = this.r.x + this.r.width / 2;
        int anchorY = this.r.y + this.r.height / 2;
        affTr.rotate(this.angle, anchorX - screen.x, anchorY - screen.y);

        BufferedImage bufIm = getImageLibrary().getImage(this.image);

        if (this.hasAnimation) {
            gc.drawImage(getImageLibrary().getImage(this.sprAnim.getCurrentImage()), this.r.x, this.r.y, null);
        } else {
            affTr.translate(this.r.x - screen.x, this.r.y - screen.y);
            gc.drawImage(bufIm, affTr, null);
        }

        gc.setComposite(AlphaComposite.SrcOver); // cleaning
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

        // Animation Update.
        if (this.hasAnimation) {
            this.sprAnim.tick();
            this.r.width = getImageLibrary().getWidth(this.sprAnim.getCurrentImage());
            this.r.height = getImageLibrary().getHeight(this.sprAnim.getCurrentImage());
        }

        if (this.dataSupplier != null) {
            int w = getImageLibrary().getWidth(this.dataSupplier.getImageIdentifier());
            int h = getImageLibrary().getHeight(this.dataSupplier.getImageIdentifier());
            this.r.x = this.dataSupplier.getX() + w / 2 - this.r.width / 2;
            this.r.y = this.dataSupplier.getY() + h / 2 - this.r.height / 2;
        }

        if (this.alpha < this.auraMaxAlpha && !this.isDisappearing)
            this.alpha += this.alphaStep;
        else if (this.alpha > this.alphaStep && this.isDisappearing)
            this.alpha -= this.alphaStep;

        if (this.amplitudeLimit < 0)
            this.angle -= 0.15;
        else if (this.amplitudeLimit > 0) {
            this.angle += this.direction * 0.15;

            if (this.angle >= this.amplitudeLimit)
                this.direction = -1;
            else if (this.angle <= -this.amplitudeLimit)
                this.direction = +1;
        }

        if (this.timeLimit < 0) {
            return true;
        }

        if (!this.isDisappearing && this.timeLimit - System.currentTimeMillis() < 0) {
            this.isDisappearing = true;
            return true;
        } else if (this.isDisappearing && this.alpha <= this.alphaStep) {
            return false;
        }
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns true if the Aura is still displayed on screen
     */
    public boolean isLive() {
        if ((this.isDisappearing && this.alpha <= this.alphaStep) || this.timeLimit + 5000 - System.currentTimeMillis() < 0)
            return false;
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** The maximum alpha of this aura effect (default is 40%)
     */
    public void setAuraMaxAlpha(float auraMaxAlpha) {
        this.auraMaxAlpha = auraMaxAlpha;
        this.alphaStep = auraMaxAlpha / 40;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}