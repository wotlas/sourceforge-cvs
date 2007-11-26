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

package wotlas.libs.graphics2D;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/** A Drawable is the mother class of objects that can be handled by the GraphicsDirector.
 *  It is just a rectangle with a paint method and a draw priority. The draw priority is 
 *  the order that the GraphicsDirector will use to display Drawables. Low priority Drawables
 *  are displayed first.
 *
 *  <p>A drawable can have antialiasing ( default is false ). The anti-aliasing is managed
 *  by the GraphicsDirector.</p>
 *
 *  <p>Drawables are linked to an ImageLibrary when they are added to a GraphicsDirector.
 *  The ImageLibrary used is the same as the GraphicsDirector.</p>
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

    /** Do we want to use anti-aliasing for this Drawable ?
     */
    protected boolean useAntialiasing;

    /** Our Image Library.
     */
    protected ImageLibrary imageLib;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. Beware ! When you extend this class, in your constructor when you call
     *  super() it will initialize every fields EXCEPT the imageLibrary parameter that will
     *  be set later when you add this drawable to a GraphicsDirector.
     */
    protected Drawable() {
        this.priority = -1;
        this.r = new Rectangle();
        this.rOld = this.r;
        this.useAntialiasing = false;
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
    protected void init(ImageLibrary imageLib) {
        this.imageLib = imageLib;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Paint method called by the GraphicsDirector. The specified rectangle represents
     *  the displayed screen in background cordinates ( see GraphicsDirector ).
     *
     *  @param gc graphics 2D use for display. Double buffering is handled by the
     *         GraphicsDirector as well as antialiasing (see useAntialiasing() method).
     *  @param screen rectangle of the graphicsDirector that is displayed on screen,
     *         expressed in background coordinates.
     */
    abstract public void paint(Graphics2D gc, Rectangle screen);

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

    /** To set if we want or not antialiasing for this drawable.
     * @param useAntialiasing set to true to use antialiasing.
     */
    public void useAntialiasing(boolean useAntialiasing) {
        this.useAntialiasing = useAntialiasing;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Do we want antialiasing for this drawable ?
     * @return true if we want antialiasing.
     */
    public boolean wantAntialiasing() {
        return this.useAntialiasing;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the priority.
     *
     * @return priority
     */
    public short getPriority() {
        return this.priority;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the current rectangle. Warning: any modification on the returned object
     *  modifies the original.
     *
     * @return current rectangle
     */
    public Rectangle getRectangle() {
        return this.r;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the old rectangle. Warning: any modification on the returned object
     *  modifies the original.
     *
     * @return old rectangle
     */
    public Rectangle getOldRectangle() {
        return this.rOld;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To update the old rectangle with the current one.
     */
    public void updateOldRectangle() {
        this.rOld = new Rectangle(this.r);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the current x position of the drawable.
     * @return current x
     */
    public int getX() {
        return this.r.x;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the current y position of the drawable.
     * @return current y
     */
    public int getY() {
        return this.r.y;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the current width of the drawable.
     * @return current width
     */
    public int getWidth() {
        return this.r.width;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the current height of the drawable.
     * @return current height
     */
    public int getHeight() {
        return this.r.height;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Test if the given point is in the rectangle of this drawable.
     *  @return true if the point is in the rectangle.
     */
    public boolean contains(int x, int y) {
        return this.r.contains(x, y);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the ImageLibrary.
     * @return the ImageLibrary associated to this drawable.
     */
    public ImageLibrary getImageLibrary() {
        return this.imageLib;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
