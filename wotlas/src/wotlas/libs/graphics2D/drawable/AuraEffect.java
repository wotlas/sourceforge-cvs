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
import java.awt.image.*;

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
    public AuraEffect( SpriteDataSupplier dataSupplier, ImageIdentifier image, short priority,
                       int lifeTime) {
     super();

     this.image = image;
     this.auraType = auraType;
     this.priority = priority;
     this.dataSupplier = dataSupplier;
     this.lifeTime = lifeTime;
     this.timeLimit = System.currentTimeMillis()+lifeTime;
     isDisappearing = false;
     alpha=0.0f;
     amplitudeLimit = -1.0f;
     direction = -1;
     hasAnimation = false;
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
     protected void init( ImageLibrary imageLib ) {
     	super.init(imageLib);

         r.width = getImageLibrary().getWidth( image );
         r.height = getImageLibrary().getHeight( image );

         if( image.getIsAnimation() ) {
             sprAnim = new Animation( image, imageLib );
             hasAnimation = true;
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To change the aura image.
    * @param image new image identifier
    */
     public void setImage( ImageIdentifier image ) {
          this.image = image;
          
          if(imageLib!=null) {
             r.width = getImageLibrary().getWidth( image );
             r.height = getImageLibrary().getHeight( image );
          }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To limit the rotation of this aura (in radians). If -1 there is no
    *  amplitude limit.
    * @param amplitudeLimit amplitude limit
    */
     public void setAmplitudeLimit( float amplitudeLimit ) {
          this.amplitudeLimit = amplitudeLimit;
     }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the animation object if the given image represented an Animation.
    */
     public Animation getAnimation() {
     	return sprAnim;
     }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To reset the animation.
    */
     public void reset() {
        setAlpha( 0.0f );
        this.timeLimit = System.currentTimeMillis()+lifeTime;
        isDisappearing = false;
     }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * set alpha;
  */
   public void setAlpha( float f ) {
     this.alpha = f;
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   * set angle;
   */
   public void setAngle(double a ) {
      this.angle = a;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the direction in which we turn...
   */
   public void setDirection( byte direction ) {
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
    public void paint( Graphics2D gc, Rectangle screen ) {

        if( !r.intersects(screen) )
            return;

        gc.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha ) );

        AffineTransform affTr = new AffineTransform();
        int anchorX = r.x + r.width/2;
        int anchorY = r.y + r.height/2;
        affTr.rotate( angle, anchorX-screen.x, anchorY-screen.y );

        BufferedImage bufIm = getImageLibrary().getImage( image );

        if( hasAnimation ) {
            gc.drawImage( getImageLibrary().getImage( sprAnim.getCurrentImage() ),
                          r.x, r.y, null );
        }
        else {
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
       if(hasAnimation) {
            sprAnim.tick();
            r.width = getImageLibrary().getWidth( sprAnim.getCurrentImage() );
            r.height = getImageLibrary().getHeight( sprAnim.getCurrentImage() );
       }

       if( dataSupplier != null ) {
          int w = getImageLibrary().getWidth( dataSupplier.getImageIdentifier() );
          int h = getImageLibrary().getHeight( dataSupplier.getImageIdentifier() );
          r.x = dataSupplier.getX()+w/2-r.width/2;
          r.y = dataSupplier.getY()+h/2-r.height/2;
       }

       if(alpha<0.4f && !isDisappearing)
          alpha+=0.01f;
       else if(alpha>0.01 && isDisappearing)
          alpha-=0.01f;

       if( amplitudeLimit<0 )
           angle -= 0.15;
       else {
       	   angle += direction*0.15;
       	
           if( angle >= amplitudeLimit )
               direction = -1;
           else if( angle <= -amplitudeLimit )
               direction = +1;
       }

       if( timeLimit<0 ) {
            return true;
        }

        if( !isDisappearing && timeLimit-System.currentTimeMillis() <0) {
            isDisappearing = true;
            return true;
        }else if( isDisappearing && alpha<=0.01 ) {
            return false;
        }
        return true;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns true if the Aura is still displayed on screen
    */
   public boolean isLive() {
        if( (isDisappearing && alpha<=0.01) || timeLimit+5000-System.currentTimeMillis() <0)
            return false;
        return true;   	
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}