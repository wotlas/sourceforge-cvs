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

import java.awt.*;
import java.awt.geom.*;


/** Represents an anti-aliased path for showing trajectory on screen...
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.Drawable
 */

public class PathDrawable extends Drawable {

 /*------------------------------------------------------------------------------------*/

  /** Our points
   */
     private Point p[];

  /** Our Path's Color.
   */
     private Color color;

  /** Path Alpha
   */
     private float alpha;

  /** Our Stroke
   */
     private Stroke stroke;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with an array of Points. There is no alpha.
   *
   * @param p list of points forming the path to display.
   * @param color path's color.
   * @param priority path's priority
   */
    public PathDrawable( Point p[], Color color, short priority ) {
    	this(p, color, 1.0f, priority );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with an array of Points. We use the specified alpha.
   *
   * @param p list of points forming the path to display.
   * @param color path's color.
   * @param alpha path's alpha. With a value of 0.0 the path is invisible, with 1.0 it's fully visible.
   * @param priority path's priority
   */
    public PathDrawable( Point p[], Color color, float alpha, short priority ) {
    	super();
        this.color = color;
        this.priority = priority;
        this.alpha = alpha;

         if(p==null || p.length<=1)
            return;

        this.p = p;

        float dash1[] = {5.0f};
        stroke = (Stroke) new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                                          BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

      // Rectangle init
        int xmin=p[0].x, xmax=p[0].x, ymin=p[0].y, ymax=p[0].y;

         for( int i=1; i<p.length; i++ ) {
           if( p[i].x>xmax ) xmax = p[i].x;
           else if( p[i].x<xmin ) xmin = p[i].x;

           if( p[i].y>ymax ) ymax = p[i].y;
           else if( p[i].y<ymin ) ymin = p[i].y;
         }

         r.x = xmin;
         r.y = ymin;
         r.width = xmax-xmin;     
         r.height = ymax-ymin;
         useAntialiasing(true);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To change the path's default stroke.
    * @param stroke Stroke to use for rendering
    */
    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
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
         RenderingHints saveRenderHints = gc.getRenderingHints(); // save
         Stroke strokeSave = gc.getStroke(); // save
    
         RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                         RenderingHints.VALUE_ANTIALIAS_ON);
         renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    
         gc.setRenderingHints( renderHints );
         gc.setStroke( stroke );

        GeneralPath polyline = new GeneralPath( GeneralPath.WIND_EVEN_ODD, p.length );
        polyline.moveTo( p[0].x-screen.x, p[0].y-screen.y );

     // We generate our path
        for( int i=1; i<p.length; i++ ) {
           polyline.lineTo( p[i].x-screen.x, p[i].y-screen.y );
        }
         
           gc.draw( polyline );

         gc.setStroke(strokeSave);
         gc.setRenderingHints( saveRenderHints ); // restore

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
         return true; // no update needed
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

