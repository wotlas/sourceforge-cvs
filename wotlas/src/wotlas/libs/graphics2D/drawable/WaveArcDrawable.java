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

/** Represents an anti-aliased animated arc representing a wave.
 *  The wave is associated to a sprite.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.Drawable
 */

public class WaveArcDrawable extends Drawable {

 /*------------------------------------------------------------------------------------*/

  /** The Sprite we take our data from.
   */
     private Sprite sprite;

  /** Our radius.
   */
     private int radius;

  /** Our Arc's Color.
   */
     private Color color;

  /** Wave Max Alpha (t=0)
   */
     private float maxAlpha;

  /** Wave Current Alpha
   */
     private float alpha;

  /** Orientation angle for the wave (in degrees)
   */
     private int orientation;

  /** Circle max radius
   */
     private int maxRadius;

  /** Max Number of cycles for the wave (max 100).
   */
     private byte maxCycle;

  /** Current cycle number for the wave.
   */
     private byte cycle;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with our Sprite. Maximum alpha is 1.0 and there is only one
   *  cycle for the wave.
   *
   * @param sprite the drawable we are associated to.
   * @param maxRadius circle's radius.
   * @param color circle's color.
   * @param priority circle's priority
   */
    public WaveArcDrawable( Sprite sprite, int maxRadius, Color color, short priority ) {
    	this(sprite, maxRadius, color, priority, 1.0f, (byte)1 );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with our Sprite, maximum alpha and number of cycles.
   *
   * @param sprite the drawable we are associated to.
   * @param maxRadius circle's radius.
   * @param color circle's color.
   * @param priority circle's priority
   * @param maxAlpha maximum alpha for the wave
   * @param maxCycle max number of cycles for the wave
   */
    public WaveArcDrawable( Sprite sprite, int maxRadius, Color color, short priority,
                            float maxAlpha, byte maxCycle ) {
        super();
        this.sprite = sprite;
        this.maxRadius = maxRadius;
        this.color = color;
        this.priority = priority;
        this.maxAlpha = maxAlpha;
        this.maxCycle = maxCycle;

        useAntialiasing(true);
        reset();
        tick();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To reset this animation
    */
    public void reset() {
        alpha = maxAlpha;
        radius = 8;
        cycle=0;
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

         gc.setColor( color );

      // 2 - Alpha
         if(alpha!=1.0)
            gc.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha ) );

      // 3 - circle display
         gc.drawArc( r.x-screen.x, r.y-screen.y, r.width, r.height, orientation-45, 90 );

      // 4 - alpha cleaning
         if(alpha!=1.0)
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

       // 1 - orientation, radius, alpha update
          orientation =  (int) ( -(sprite.getDataSupplier().getAngle()*180)/Math.PI );
          radius += (maxRadius-8)/10;
          
          alpha -= (maxAlpha/10);

          if(radius>maxRadius) {
             cycle++;

             if(cycle==maxCycle)
                return false; // end of animation

             alpha = maxAlpha;
             radius = 8;
          }
          else if(alpha<0)
             alpha =0.0f;

       // 2 - We update our rectangle
          r.x = sprite.getX() + sprite.getWidth()/2 - radius ;
          r.y = sprite.getY() + sprite.getHeight()/2 - radius ;
          r.width = radius*2;
          r.height = radius*2;

         return true;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Is the wave drawable still alive ?
   *  @return true id alive
   */
     public boolean isLive() {
          return !(cycle==maxCycle);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
