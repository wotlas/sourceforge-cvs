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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import wotlas.libs.graphics2d.Drawable;

/** Represents an anti-aliased circle that can be associated to any Drawable.
 *  It's an easy way to display selections on screen.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2d.Drawable
 */

public class CircleDrawable extends Drawable {

    /*------------------------------------------------------------------------------------*/

    /** The SpriteDataSupplier we take our data from.
     */
    private Drawable refDrawable;

    /** Our radius.
     */
    private int radius;

    /** Our Circle's Color.
     */
    private Color color;

    /** do we have to center this circle ?
     */
    private boolean centerCircle;

    /** Circle Alpha
     */
    private float alpha;

    /*------------------------------------------------------------------------------------*/

    /** Constructor with our Reference Drawable. There is no alpha and the circle is centered.
     *
     * @param refDrawable the drawable we are associated to.
     * @param radius circle's radius.
     * @param color circle's color.
     * @param priority circle's priority
     */
    public CircleDrawable(Drawable refDrawable, int radius, Color color, short priority) {
        this(refDrawable, radius, color, 1.0f, true, priority);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with our Reference Drawable and center mode. There is no alpha.
     *
     * @param refDrawable the drawable we are associated to.
     * @param radius circle's radius.
     * @param color circle's color.
     * @param centerCircle if true we center the circle on the given refDrawable, if false we use
     *        the refDrawable's upper-left cordinates as the center of our circle.
     * @param priority circle's priority
     */
    public CircleDrawable(Drawable refDrawable, int radius, Color color, boolean centerCircle, short priority) {
        this(refDrawable, radius, color, 1.0f, centerCircle, priority);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with our Reference Drawable, alpha and center mode.
     *
     * @param refDrawable the drawable we are associated to.
     * @param radius circle's radius.
     * @param color circle's color.
     * @param alpha circle's alpha. With a value of 0.0 the circle is invisble, with 1.0 it's fully visble.
     * @param centerCircle if true we center the circle on the given refDrawable, if false we use
     *        the refDrawable's upper-left cordinates as the center of our circle.
     * @param priority circle's priority
     */
    public CircleDrawable(Drawable refDrawable, int radius, Color color, float alpha, boolean centerCircle, short priority) {
        super();
        this.refDrawable = refDrawable;
        this.radius = radius;
        this.color = color;
        this.priority = priority;
        this.alpha = alpha;
        this.centerCircle = centerCircle;

        this.r.width = 2 * radius;
        this.r.height = 2 * radius;

        useAntialiasing(true);
        tick();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To change the circle's radius.
     * @param radius new radius
     */
    public void setRadius(int radius) {
        this.radius = radius;
        this.r.width = 2 * radius;
        this.r.height = 2 * radius;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the radius.
     *  @return radius
     */
    public int getRadius() {
        return this.radius;
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

        gc.setColor(this.color);

        // 2 - Alpha
        if (this.alpha != 1.0)
            gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha));

        // 3 - circle display
        gc.drawOval(this.r.x - screen.x, this.r.y - screen.y, this.r.width, this.r.height);

        // 4 - alpha cleaning
        if (this.alpha != 1.0)
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
        // 1 - We update our rectangle
        if (this.centerCircle) {
            this.r.x = this.refDrawable.getX() + this.refDrawable.getWidth() / 2 - this.radius;
            this.r.y = this.refDrawable.getY() + this.refDrawable.getHeight() / 2 - this.radius;
        } else {
            this.r.x = this.refDrawable.getX() - this.radius;
            this.r.y = this.refDrawable.getY() - this.radius;
        }

        return true; // no update needed, a sprite is always "live" by default.
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
