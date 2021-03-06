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
import java.awt.image.BufferedImage;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.DrawableOwner;
import wotlas.libs.graphics2d.GroupOfGraphics;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.ImageLibrary;

/** 
 *  a FakeSprite, comes out from Sprite, i create it, 'cause i need 
 * to display part of an image, a tile in inside a GroupOfGraphics.
 * 
 * Why i need an Owner?because the player can change his form/aspect
 * so i will use the dataSupplier to get the new.
 *
 * @author MasterBob, Aldiss, Petrus, Diego
 */

public class FakeSprite extends Drawable implements DrawableOwner {

    /** Our owner.
     */
    private FakeSpriteDataSupplier dataSupplier;

    /** Current Image Identifier.
     */
    private ImageIdentifier image;

    private int imageNr;
    private GroupOfGraphics groupOfGraphics;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. The anchor mode for rotations is set to CENTER_ANCHOR_POINT.
     *
     * @param owner 
     * @param priority sprite's priority
     */
    public FakeSprite(FakeSpriteDataSupplier dataSupplier, short priority, GroupOfGraphics groupOfGraphics, int imageNr) {
        super();
        this.dataSupplier = dataSupplier;
        this.priority = priority;
        this.groupOfGraphics = groupOfGraphics;
        this.imageNr = imageNr;
    }

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

        // 4 - image display
        //   BufferedImage bufIm = imageLib.getImage( image );
        //   gc.drawImage( bufIm, r.x-screen.x, r.y-screen.y, null );

        // groupOfGraphics.init(gc);
        BufferedImage theTile = getImageLibrary().getImage(this.groupOfGraphics.getImage());
        this.groupOfGraphics.drawMe(gc, this.r.x - screen.x, this.r.y - screen.y, this.imageNr, theTile);

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

        // image = dataSupplier.getImageIdentifier();

        // BufferedImage bufIm = imageLib.getImage( image );

        // r.width = bufIm.getWidth( null );
        // r.height = bufIm.getHeight( null );

        this.r.width = 32;
        this.r.height = 32;

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
        //      return (Object) "diego:should supply getOwner to FakeSprite.";
    }
}