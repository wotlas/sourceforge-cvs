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

/** A Sprite is mainly an image displayed on the GraphicsDirector. The sprite data is 
 *  given by an object implementing the SpriteDataSupplier interface. Because a sprite
 *  has no Image field but an ImageIdentifier, it can then represent an animation if
 *  the imageIdentifier changes on each tick.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.drawable.SpriteDataSupplier
 */

public class Sprite extends Drawable {

 /*------------------------------------------------------------------------------------*/

  /** Our SpriteDataSupplier.
   */
     private SpriteDataSupplier dataSupplier;

  /** Current Image Identifier.
   */
     private ImageIdentifier image;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   *
   * @param dataSupplier 
   */
    public Sprite(SpriteDataSupplier dataSupplier, short priority) {
    	super();
        this.dataSupplier = dataSupplier;
        this.priority = priority;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To change the data supplier.
   *
   * @param dataSupplier new data Supplier.
   */
    public void setDataSupplier(SpriteDataSupplier dataSupplier) {
        this.dataSupplier = dataSupplier;
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

        gc.drawImage( ImageLibrary.getDefaultImageLibrary().getImage( image ), r.x, r.y, null );
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

        r.width = ImageLibrary.getDefaultImageLibrary().getWidth( image );
        r.height = ImageLibrary.getDefaultImageLibrary().getHeight( image );

        return true; // no update needed and a sprite is always "live" by default.
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
