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

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.filter.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

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
    	this(dataSupplier,priority,CENTER_ANCHOR_POINT);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with anchor mode ( see public static fields ). 
   *
   * @param dataSupplier Sprite's data supplier
   * @param priority sprite's priority
   * @param anchorMode tells which anchor point to use for rotations ( CENTER_ANCHOR_POINT, ... )
   */
    public Sprite(SpriteDataSupplier dataSupplier, short priority, byte anchorMode ) {
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
     protected void init( ImageLibrary imageLib ) {
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
        return dataSupplier;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To add a DynamicImageFilter to this Sprite. The filter will be called to create
   *  a new filtered BufferedImage before rendering.
   *  @param imageFilter a DynamicImageFilter.
   */
    public void setDynamicImageFilter( DynamicImageFilter imageFilter ) {
        if(imageFilter==null)
           return;

        if (imageFilters==null) {
            imageFilters = new DynamicImageFilter[1];            
            imageFilters[0] = imageFilter;            
            return;
        }
        
        // Search for an existing filter of same class        
        Class filterClass = imageFilter.getClass();
        for (int i=0; i<imageFilters.length; i++) {
            if ( filterClass.isInstance(imageFilters[i]) ) {                
                imageFilters[i] = imageFilter;
                return;
            }
        }
        
        // No existing similar filter found : create a new one        
        DynamicImageFilter[] myImageFilters = new DynamicImageFilter[imageFilters.length+1];
        System.arraycopy(imageFilters, 0, myImageFilters, 0, imageFilters.length);
        myImageFilters[imageFilters.length] = imageFilter;
        imageFilters = myImageFilters;    		
    }
    
    /** To remove a previously set dynamic image filter
     *
     * @param filterClass class name of filter to remove
     */
    public void unsetDynamicImageFilter(String filterClass) {        
        int i=0;        
        DynamicImageFilter imageFilter = imageFilters[0];
        while ( !filterClass.equals(imageFilter.getClass().getName()) && i<imageFilters.length) {            
            i++;
            imageFilter = imageFilters[i];
        }        
        if (i<imageFilters.length) {
            DynamicImageFilter[] myImageFilters = new DynamicImageFilter[imageFilters.length-1];
            System.arraycopy(imageFilters, 0, myImageFilters, 0, i);
            System.arraycopy(imageFilters, i+1, myImageFilters, i, imageFilters.length-1-i);
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
    public void paint( Graphics2D gc, Rectangle screen ) {

      // 1 - Need to display this sprite ?
         if( !r.intersects(screen) )
             return;

      // 2 - any affine transform ?
         AffineTransform affTr = null;

         if( dataSupplier.getAngle()!=0.0 ) {
           // Rotation Transformation
              affTr = new AffineTransform();
              int anchorX=0, anchorY=0;

              switch( anchorMode ) {
                  default:
                  case CENTER_ANCHOR_POINT:
                       anchorX = r.x + r.width/2;
                       anchorY = r.y + r.height/2;
                       break;

                  case UPPER_LEFT_ANCHOR_POINT:
                       anchorX = r.x;
                       anchorY = r.y;
                       break;

                  case UPPER_RIGHT_ANCHOR_POINT:
                       anchorX = r.x + r.width;
                       anchorY = r.y;
                       break;

                  case LOWER_RIGHT_ANCHOR_POINT:
                       anchorX = r.x + r.width;
                       anchorY = r.y + r.height;
                       break;

                  case LOWER_LEFT_ANCHOR_POINT:
                       anchorX = r.x;
                       anchorY = r.y + r.height;
                       break;
              }

              affTr.rotate( dataSupplier.getAngle(), anchorX-screen.x, anchorY-screen.y );
         }

         if( dataSupplier.getScaleX()!=1.0 || dataSupplier.getScaleY()!=1.0) {
           // Scale Transformation
              if(affTr==null)
                 affTr = new AffineTransform();

              affTr.scale( dataSupplier.getScaleX(), dataSupplier.getScaleY() );
              r.x += (int)( ( r.width - r.width*dataSupplier.getScaleX() )/2 );
              r.y += (int)( ( r.height - r.height*dataSupplier.getScaleY() )/2 );
              r.width = (int) (r.width*dataSupplier.getScaleX() );
              r.height = (int) ( r.height*dataSupplier.getScaleY() );
         }

      // 3 - Any alpha ?
         if( dataSupplier.getAlpha()!=1.0f )
             gc.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, dataSupplier.getAlpha() ) );


      // 4 - image display
         BufferedImage bufIm = imageLib.getImage( image );
         
         /*if( imageFilter!=null )
             bufIm = imageFilter.filterImage( bufIm );
         
         BrightnessFilter brightnessFilter = new BrightnessFilter();
         brightnessFilter.setBrightness((short) 230);
         bufIm = brightnessFilter.filterImage( bufIm );*/
         
         if (imageFilters!=null) {
             for (int i=0; i<imageFilters.length; i++) {
                bufIm = imageFilters[i].filterImage( bufIm );
             }
         }

         if( affTr==null )
             gc.drawImage( bufIm, r.x-screen.x, r.y-screen.y, null );
         else {
             affTr.translate( r.x-screen.x, r.y-screen.y );
             gc.drawImage( bufIm, affTr, null );
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

        r.x = dataSupplier.getX();
        r.y = dataSupplier.getY();

        image = dataSupplier.getImageIdentifier();

        BufferedImage bufIm = imageLib.getImage( image );

        r.width = bufIm.getWidth( null );
        r.height = bufIm.getHeight( null );

        return true; // no update needed and a sprite is always "live" by default.
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the owner of this drawable. By 'owner' we mean the object which this
    *  Sprite is the graphical representation, i.e the SpriteDataSupplier.
    *
    * @return Object owner of this drawable : the given SpriteDataSupplier.
    */
     public Object getOwner() {
        return (Object) dataSupplier;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
