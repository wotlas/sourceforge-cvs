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

package wotlas.libs.graphics2D.drawable;

import wotlas.libs.graphics2D.*;

import java.awt.*;
import java.awt.geom.*;

/** Represents the shadow of a Sprite. This is an animated shadow. It must have the
 *  same number of images in its animation as its associated Sprite. The shadow alpha
 *  is set to 25%.
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
    public ShadowSprite(SpriteDataSupplier dataSupplier, ImageIdentifier shadowImage, short priority,
    int deltaX, int deltaY ) {
    	super();
        this.dataSupplier = dataSupplier;
        this.priority = priority;
        this.shadowImage = shadowImage;
        this.deltaX = deltaX;
        this.deltaY = deltaY;

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
    public void paint( Graphics2D gc, Rectangle screen ) {

      // 1 - Need to display this sprite ?
         if( !r.intersects(screen) )
             return;

      // 2 - any affine transform ?
         AffineTransform affTr = null;

         if( dataSupplier.getAngle()!=0.0 ) {
           // Rotation Transformation
              affTr = new AffineTransform();
              int anchorX = r.x + r.width/2;
              int anchorY = r.y + r.height/2;

              affTr.rotate( dataSupplier.getAngle(), anchorX-screen.x, anchorY-screen.y );
         }

         if( dataSupplier.getScaleX()!=1.0 || dataSupplier.getScaleY()!=1.0) {
           // Scale Transformation
              if(affTr==null)
                 affTr = new AffineTransform();

              affTr.scale( dataSupplier.getScaleX(), dataSupplier.getScaleY() );
         }

      // 3 - Alpha
         gc.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f ) );

      // 4 - image display
         if( affTr==null ) {
             gc.drawImage( ImageLibrary.getDefaultImageLibrary().getImage( shadowImage ),
                           r.x-screen.x, r.y-screen.y, null );
         }
         else {
             affTr.translate( r.x-screen.x, r.y-screen.y );

             gc.drawImage( ImageLibrary.getDefaultImageLibrary().getImage( shadowImage ),
                           affTr, null );
         }

      // 5 - alpha cleaning
         gc.setComposite( AlphaComposite.SrcOver );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tick method called by the GraphicsDirector. This tick method has a returned value
   *  that indicates if the drawable is still living or must be deleted. Some Drawables
   *  always return "still living", it is then the task of the program that uses
   *  the GraphicsDirector to manage the destruction of drawables.
   *
   *  @return true if the drawable is "live", false if it must be deleted.
   */
     public boolean tick() {
    
       // 1 - Current Image Index
          shadowImage.imageIndex = dataSupplier.getImageIdentifier().imageIndex;

       // 2 - We update our rectangle
          r.x = dataSupplier.getX()+deltaX;
          r.y = dataSupplier.getY()+deltaY;

          r.width = ImageLibrary.getDefaultImageLibrary().getWidth( shadowImage );
          r.height = ImageLibrary.getDefaultImageLibrary().getHeight( shadowImage );

         return true; // no update needed, a sprite is always "live" by default.
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
