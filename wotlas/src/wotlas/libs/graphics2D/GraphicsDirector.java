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


/** A GraphicsDirector is the root class of this graphics2D engine. It manages
 *  Drawables and has a WindowPolicy for scrollings.
 *
 *  the only synchronized methods in GraphicsDirector are paint() and tick()
 *  so if you happen to handle events or change parameters do it with care !
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.ImageLibrary
 * @see wotlas.libs.graphics2D.Drawable
 * @see wotlas.libs.graphics2D.DrawableIterator
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GraphicsDirector extends JPanel {

 /*------------------------------------------------------------------------------------*/

  /** Represents the visble part of the JPanel (it has the JPanel's size)
   *  and is expressed in the background's coordinate (we use the background as a
   *  reference here, because all Drawables should be expressed in background
   *  coordinates).
   */
    private Rectangle screen;
 
  /** Background's Dimension. The background can be any Drawable,
   *  we don't have to possess a handle it, we only need its dimension.
   */
    private Dimension background;

  /** Our Drawable reference, we need to know which drawable our windowPolicy
   *  is going to refer to center the screen.
   */
    private Drawable refDrawable;

  /** Our drawables. They are sorted by priority.
   */
    DrawableIterator drawables;

  /** Our WindowPolicy. It tells us how to move the screen on the background.
   */
    WindowPolicy windowPolicy;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. The window policy is not supposed to change during the life of the
   *  GraphicsDirector, but you can still change it by invoking the setWindowPolicy()
   *  method.
   *
   * @param windowPolicy a policy that manages window scrolling.
   */
    public GraphicsDirector( WindowPolicy windowPolicy ) {
      super(true);
      drawables = new DrawableIterator();
      setWindowPolicy( windowPolicy );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To initialize the GraphicsDirector. A call to this method suppresses all the
   *  possessed Drawable Objects.
   *
   * @param background dimension of the biggest drawable that you use as a reference
   *        for your 2D cordinates.
   * @param screen initial dimension for this JPanel
   * @param refDrawable Drawable that will always be the center of the screen. This
   *        drawable is not automatically added to our drawables. You also have to call
   *        the addDrawable method to add it to the GraphicsDirector.
   */
    public void init( Dimension background, Dimension screen, Drawable refDrawable ) {
      this.background = background;
      this.screen = new Rectangle( screen );
      this.refDrawable = refDrawable;

      drawables.clear();
      windowPolicy.tick();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the window policy.
   */
    public void setWindowPolicy( WindowPolicy windowPolicy ) {
       this.windowPolicy = windowPolicy;
       windowPolicy.init( this );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** To paint this JPanel.
   *
   * @param gc graphics object.
   */
    public synchronized void paint(Graphics gc) {

        Graphics2D gc2D = (Graphics2D) gc;
        drawables.resetIterator();
        
        while( drawables.hasNext() )
           drawables.next().paint( gc2D, screen );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To add a drawable.
   *
   * @param dr drawable to add.
   */
    public void addDrawable( Drawable dr ) {
        drawables.resetIterator();

        while( drawables.hasNext() ) {
          Drawable current = drawables.next();
          
          if( current.getPriority() > dr.getPriority() ) {
              drawables.insert( dr );
              return;
          }
        }

        drawables.add( dr );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To remove a drawable.
   *
   * @param dr drawable to remove.
   */
   public void removeDrawable( Drawable dr ) {
        drawables.resetIterator();

        while( drawables.hasNext() ) {
          Drawable current = drawables.next();

          if( current == dr ) {
              drawables.insert( dr );
              return;
          }
        }

        drawables.add( dr );

   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To remove all the drawables.
   */
    public void removeAllDrawables() {
       drawables.clear();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** The tick method updates our screen position, drawables and repaint the whole thing.
   *  Never call repaint on the graphics director, call tick() !
   */
    public synchronized void tick() {

      // 1 - We update our screen dimension.
         screen.width = getWidth();
         screen.width = getHeight();

      // 2 - We update our WindowPolicy
         windowPolicy.tick();
      
      // 3 - We tick all our sprites
         drawables.resetIterator();

         while( drawables.hasNext() )
            if( !drawables.next().tick() )
                 drawables.remove();

      // 4 - We repaint all our prites
         repaint();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set a new drawable reference.
    *
    * @param newRefDrawable new drawable reference
    */
     public void setRefDrawable( Drawable newRefDrawable ) {
        refDrawable = newRefDrawable;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the current drawable reference.
    *
    * @return drawable reference
    */
     public Drawable getRefDrawable() {
        return refDrawable;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the screen rectangle. Any changes to the returned rectangle are
    *  affected to the original.
    */
     public Rectangle getScreenRectangle() {
        return screen;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the background dimension.
    */
     public Dimension getBackgroundDimension() {
        return background;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
