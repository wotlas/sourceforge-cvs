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

package wotlas.libs.graphics2D;

import java.awt.Rectangle;
import java.awt.Graphics2D;


/** A Drawable is the mother class of objects that can be handled by the GraphicsDirector.
 *  It is just a rectangle with a paint method and a draw priority. The draw priority is 
 *  the order that the GraphicsDirector will use to display Drawables. Low priority Drawables
 *  are displayed first.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.GraphicsDirector
 */

public abstract class Drawable {

 /*------------------------------------------------------------------------------------*/

  /** Current rectangle of the Drawable. Its the zone where the Drawable object
   * will display. It should be expressed in backgound cordinates ( i.e. the background
   * drawable is usually the drawable with the lowest priority ).
   */
     protected Rectangle r;

  /** Old rectangle of previous tick.
   */
     protected Rectangle rOld;

  /** Draw priority.
   */
     protected short priority;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     protected Drawable() {
        priority = -1;
        r = new Rectangle();
        rOld = r;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Paint method called by the GraphicsDirector. The specified rectangle represents
   *  the displayed screen in background cordinates ( see GraphicsDirector ).
   *
   *  @param gc graphics 2D use for display (double buffering is handled by the
   *         GraphicsDirector)
   *  @param screen display zone of the graphicsDirector, in background coordinates.
   */
     abstract public void paint( Graphics2D gc, Rectangle screen );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tick method called by the GraphicsDirector. This tick method has a returned value
   *  that indicates if the drawable is still living or must be deleted. Some Drawables
   *  always return "still living", it is then the task of the program that uses
   *  the GraphicsDirector to manage the destruction of drawables.
   *
   *  @return true if the drawable is "live", false if it must be deleted.
   */
     abstract public boolean tick();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the priority.
   *
   * @return priority
   */ 
     public short getPriority() {
         return priority;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the current rectangle. Warning: any modification on the returned object
   *  modifies the original.
   *
   * @return current rectangle
   */
     public Rectangle getRectangle() {
         return r;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the old rectangle. Warning: any modification on the returned object
   *  modifies the original.
   *
   * @return old rectangle
   */
     public Rectangle getOldRectangle() {
         return rOld;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To update the old rectangle with the current one.
   */
     public void updateOldRectangle() {
         rOld = new Rectangle( r );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
