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
import wotlas.common.ImageLibRef;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/** An AuraEffect is a sprite that rely on a SpriteDataSupplier. It is used to just display
 *  a rotating image on the GraphicsDirector. The image can change ( hasAnimation=true in constructor)
 *  but you can not change its (x,y) cordinates once set in the constructor.
 *
 *  A MotionlessSprite is especially useful for background images.
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
    * The type of the aura (blue,...)
    */
    private short auraType;

   /** TimeStamp indicating when we'll need to remove our drawable from screen.
    *  If -1 we have infinite life. The TextDrawable must be removed manually.
    */
     private long timeLimit;

   /** Display duration in ms...
    */
     private int lifeTime;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   *
   * @param dataSupplier ou r reference
   * @param image image identifier to use for this sprite.
   * @param priority sprite's priority
   * @param hasAnimation if set to true we use the given identifier as a base for an animation,
   *        if set to false, we let the ImageIdentifier imageIndex set to 0.
   * @param lifeTime display duration
   */
    public AuraEffect(SpriteDataSupplier dataSupplier, ImageIdentifier image, short priority,
                      boolean hasAnimation, int lifeTime) {
     super();

     this.image = image;
     r.width = ImageLibrary.getDefaultImageLibrary().getWidth( image );
     r.height = ImageLibrary.getDefaultImageLibrary().getHeight( image );

     this.auraType = auraType;
     this.priority = priority;
     this.dataSupplier = dataSupplier;
     this.lifeTime = lifeTime;
     this.timeLimit = System.currentTimeMillis()+lifeTime;

     if(hasAnimation)
        sprAnim = new Animation( image );

     useAntialiasing(true);
    }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * set alpha;
  */
 public void setAlpha(float f )
  {
   this.alpha = f;
   this.timeLimit = System.currentTimeMillis()+lifeTime;
  }


  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * set angle;
  */
 public void setAngle(double a )
  {
   this.angle = a;
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

        if( !r.intersects(screen) )
            return;

        gc.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha ) );

        AffineTransform affTr = new AffineTransform();
        int anchorX = r.x + r.width/2;
        int anchorY = r.y + r.height/2;
        affTr.rotate( angle, anchorX-screen.x, anchorY-screen.y );

        BufferedImage bufIm = ImageLibrary.getDefaultImageLibrary().getImage( image );

        if( hasAnimation ) {
            gc.drawImage( ImageLibrary.getDefaultImageLibrary().getImage( sprAnim.getCurrentImage() ),
                          r.x, r.y, null );
        }
        else
          {
           affTr.translate( r.x-screen.x, r.y-screen.y );
           gc.drawImage( bufIm, affTr, null );
          }

       gc.setComposite( AlphaComposite.SrcOver ); // cleaning
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

       // Animation Update.
          if( hasAnimation ) {
            sprAnim.tick();
            r.width = ImageLibrary.getDefaultImageLibrary().getWidth( sprAnim.getCurrentImage() );
            r.height = ImageLibrary.getDefaultImageLibrary().getHeight( sprAnim.getCurrentImage() );
           }

       if( dataSupplier != null ) {
          int w = ImageLibrary.getDefaultImageLibrary().getWidth( dataSupplier.getImageIdentifier() );
          int h = ImageLibrary.getDefaultImageLibrary().getHeight( dataSupplier.getImageIdentifier() );
          r.x = dataSupplier.getX()+w/2-r.width/2;
          r.y = dataSupplier.getY()+h/2-r.height/2;
        }

       if(alpha<0.4f) alpha+=0.01f;

       angle -= 0.15;

       if( timeLimit<0 ) {
            return true;
        }

        if( timeLimit-System.currentTimeMillis() <0 )
            return false;
        return true;

     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}