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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import wotlas.libs.graphics2d.Animation;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.DrawableOwner;
import wotlas.libs.graphics2d.GroupOfGraphics;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.ImageLibrary;

/** A MotionlessSprite is a sprite that has no DataSupplier. It is used to just display
 *  an image on the GraphicsDirector. The image can be an animation
 *  but you can not change its (x,y) cordinates once they are set in the constructor.
 *
 *  A MotionlessSprite is especially useful for background images or static images.
 *
 * @author MasterBob, Aldiss, Diego
 */

public class MotionlessSprite extends Drawable implements DrawableOwner {

    /*------------------------------------------------------------------------------------*/

    /** it's a tilemap image and must show only a part of it?
      */
    private boolean isTileMapImage;

    /** internal tile number of tilemap image
      */
    private int internalTile;

    /** Eventual TilePosition, null if isTileMapImage=false
      */
    transient private GroupOfGraphics tileGraphic;

    /** do we have to maintain an animation ?
      */
    private boolean hasAnimation;

    /** Eventual Animation, null if hasAnimation=false
     */
    private Animation sprAnim;

    /** Current Image Identifier.
     */
    private ImageIdentifier image;

    /** Owner name of this sprite.
     */
    private String owner;

    /** Background cordinates if true, otherwise Screen Cordinates.
     */
    private boolean isBackgroundCordinates;

    /*------------------------------------------------------------------------------------*/

    /** Constructor with no Owner ( see the DrawableOwner interface ) for this sprite.
     *  The cordinates x,y are supposed to be background cordinates. The image given can
     *  represent an animation.
     *
     * @param x sprite's x cordinate
     * @param y sprite's y cordinate
     * @param image image identifier to use for this sprite.
     * @param priority sprite's priority
     */
    public MotionlessSprite(int x, int y, ImageIdentifier image, short priority) {
        this(x, y, image, priority, null, true);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with owner and cordinates reference. The owner is just given as
     *  a string that should represent its name. The image given is a Tilemap
     *  
     *
     * @param x sprite's x cordinate
     * @param y sprite's y cordinate
     * @param priority sprite's priority
     * @param owner the owner's name
     * @param isBackgroundCordinates set to true if x, y are background cordinates, to false if they are
     *        screen cordinates.
     */
    public MotionlessSprite(int x, int y, GroupOfGraphics tileGraphic, int internalTile, short priority) {
        super();
        this.r.x = x;
        this.r.y = y;

        this.tileGraphic = tileGraphic;
        this.internalTile = internalTile;
        this.image = tileGraphic.getImage();
        this.priority = priority;
        this.isBackgroundCordinates = true;
        this.owner = null;

        this.isTileMapImage = true;
        this.hasAnimation = false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with owner and cordinates reference. The owner is just given as
     *  a string that should represent its name. The image given can
     *  represent an animation.
     *
     * @param x sprite's x cordinate
     * @param y sprite's y cordinate
     * @param image image identifier to use for this sprite.
     * @param priority sprite's priority
     * @param owner the owner's name
     * @param isBackgroundCordinates set to true if x, y are background cordinates, to false if they are
     *        screen cordinates.
     */
    public MotionlessSprite(int x, int y, ImageIdentifier image, short priority, String owner, boolean isBackgroundCordinates) {
        super();
        this.r.x = x;
        this.r.y = y;

        this.image = image;
        this.priority = priority;
        this.isBackgroundCordinates = isBackgroundCordinates;
        this.owner = owner;

        this.isTileMapImage = false;
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

        if (this.isTileMapImage) {
            this.r.width = 50;
            this.r.height = 50;
        } else {
            this.r.width = getImageLibrary().getWidth(this.image);
            this.r.height = getImageLibrary().getHeight(this.image);

            if (this.image.getIsAnimation()) {
                this.sprAnim = new Animation(this.image, imageLib);
                this.hasAnimation = true;
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the animation object if the given image represented an Animation.
     */
    public Animation getAnimation() {
        return this.sprAnim;
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

        int myX = this.r.x, myY = this.r.y;

        if (this.isBackgroundCordinates) {
            myX -= screen.x;
            myY -= screen.y;
        }

        if (this.isTileMapImage) {
            BufferedImage theTile = getImageLibrary().getImage(this.image);
            this.tileGraphic.drawMe(gc, myX, myY, this.internalTile, theTile);
        } else if (this.hasAnimation) {
            gc.drawImage(getImageLibrary().getImage(this.sprAnim.getCurrentImage()), myX, myY, null);
        } else
            gc.drawImage(getImageLibrary().getImage(this.image), myX, myY, null);
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
        if (this.hasAnimation)
            this.sprAnim.tick();

        return true; // no update needed and a MotionlessSprite is always "live" by default.
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the owner of this drawable. By 'owner' we mean here a name linked to the
     *  object which this MotionlessSprite is the graphical representation.
     *
     * @return Object owner of this drawable : the owner name
     */
    public Object getOwner() {
        return this.owner;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}