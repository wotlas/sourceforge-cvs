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

/** A MotionlessSprite is a sprite that has no DataSupplier. It is used to just display
 *  an image on the GraphicsDirector. The image can change ( hasAnimation=true in constructor)
 *  but you can not change its (x,y) cordinates once set in the constructor.
 *
 *  A MotionlessSprite is especially useful for background images.
 *
 * @author MasterBob, Aldiss
 */

public class MotionlessSprite extends Drawable implements DrawableOwner {

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

  /** Owner name of this sprite.
   */
     private String owner;

  /** Background cordinates if true, otherwise Screen Cordinates.
   */
     private boolean isBackgroundCordinates;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with no Owner ( see the DrawableOwner interface ) for this sprite.
   *  The cordinates x,y are supposed to be background cordinates.
   *
   * @param x sprite's x cordinate
   * @param y sprite's y cordinate
   * @param image image identifier to use for this sprite.
   * @param priority sprite's priority
   * @param hasAnimation if set to true we use the given identifier as a base for an animation,
   *        if set to false, we let the ImageIdentifier imageIndex set to 0.
   */
    public MotionlessSprite( int x, int y, ImageIdentifier image, short priority, boolean hasAnimation ) {
        this( x, y, image, priority, hasAnimation, null, true );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with owner and cordinates reference. The owner is just given as
   *  a string that should represent its name.
   *
   * @param x sprite's x cordinate
   * @param y sprite's y cordinate
   * @param image image identifier to use for this sprite.
   * @param priority sprite's priority
   * @param hasAnimation if set to true we use the given identifier as a base for an animation,
   *        if set to false, we let the ImageIdentifier imageIndex set to 0.
   * @param owner the owner's name
   * @param isBackgroundCordinates set to true if x, y are background cordinates, to false if they are
   *        screen cordinates.
   */
    public MotionlessSprite( int x, int y, ImageIdentifier image, short priority, boolean hasAnimation,
                             String owner, boolean isBackgroundCordinates ) {
    	super();
    	r.x = x;
    	r.y = y;
        r.width = ImageLibrary.getDefaultImageLibrary().getWidth( image );
        r.height = ImageLibrary.getDefaultImageLibrary().getHeight( image );
    	
    	this.image = image;    	
        this.priority = priority;
        this.hasAnimation = hasAnimation;
        this.isBackgroundCordinates = isBackgroundCordinates;

        if(hasAnimation)
           sprAnim = new Animation( image );

        this.owner = owner;
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

        int myX = r.x, myY = r.y;

        if(isBackgroundCordinates) {
           myX -= screen.x;
           myY -= screen.y;
        }

        if( hasAnimation ) {
            gc.drawImage( ImageLibrary.getDefaultImageLibrary().getImage( sprAnim.getCurrentImage() ),
                          myX, myY, null );
        }
        else
            gc.drawImage( ImageLibrary.getDefaultImageLibrary().getImage( image ), myX, myY, null );
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
          if( hasAnimation )
             sprAnim.tick();

        return true; // no update needed and a MotionlessSprite is always "live" by default.
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the owner of this drawable. By 'owner' we mean here a name linked to the
    *  object which this MotionlessSprite is the graphical representation.
    *
    * @return Object owner of this drawable : the owner name
    */
     public Object getOwner() {
        return (Object) owner;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
