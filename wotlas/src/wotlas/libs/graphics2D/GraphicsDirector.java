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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** A GraphicsDirector is the root class of this graphics2D engine. It manages
 *  Drawables and has a WindowPolicy for scrollings.
 *
 *  the only synchronized methods in GraphicsDirector are paint() and tick()
 *  so if you happen to handle events or change parameters do it with care !
 *
 * @author MasterBob, Aldiss, Petrus
 * @see wotlas.libs.graphics2D.ImageLibrary
 * @see wotlas.libs.graphics2D.Drawable
 * @see wotlas.libs.graphics2D.DrawableIterator
 */

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

  /** Can we display our drawables ?
   */
    private boolean display;

  /** Reset the background ? if true we repaint the background with a white color.
   */
    private boolean resetBackground;

  /** Lock for repaint...
   */
    private Object lockPaint = new Object();

  /** OffScreen image for the GraphicsDirector. 
   */
    private Image backBufferImage;

  /** To repaint the screen.
   */
    private Thread paintThread;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. The window policy is not supposed to change during the life of the
   *  GraphicsDirector, but you can still change it by invoking the setWindowPolicy()
   *  method.
   *
   * @param windowPolicy a policy that manages window scrolling.
   */
    public GraphicsDirector( WindowPolicy windowPolicy ) {
      super(false); // we don't use the default JPanel double-buffering
      display = false;
      drawables = new DrawableIterator();
      setWindowPolicy( windowPolicy );
      setBackground( Color.white );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To initialize the GraphicsDirector. A call to this method suppresses all the
   *  possessed Drawable Objects. The backDrawable & refDrawable are automatically
   *  added to the GraphicsDirector Drawable list.
   *
   * @param backDrawable the drawable that you will use as a reference for your 2D cordinates.
   * @param refDrawable reference Drawable for screen movements. The way the screen moves
   *        is dictated by the WindowPolicy and refers to this drawable.
   * @param screen initial dimension for this JPanel
   */
    public void init( Drawable backDrawable,  Drawable refDrawable, Dimension screen) {

      // Background dims
         display = false;
         background = new Dimension( backDrawable.getWidth(), backDrawable.getHeight() );

      // Screen defaults
         this.screen = new Rectangle( screen );
         setPreferredSize( screen );
         setMaximumSize( background );
         setMinimumSize( new Dimension(10,10) );


      // we reset the GraphicsDirector's drawables
         drawables.clear();
         addDrawable( backDrawable );
         addDrawable( refDrawable );

      // We set the new drawable reference an tick our WindowPolicy.
         this.refDrawable = refDrawable;
         windowPolicy.tick();
         resetBackground = true;
         display = true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the window policy.
   */
    public void setWindowPolicy( WindowPolicy windowPolicy ) {
       this.windowPolicy = windowPolicy;
       windowPolicy.init( this );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Our customized repaint method
   */
    public void repaint() {      
       if(lockPaint==null) return;
       
       synchronized( lockPaint ) {

          if(paintThread!=null)
              try{
                 lockPaint.wait( 100 );
              }catch( Exception e ) {}

          paintThread =new Thread() {
             public void run() {
                try{                  
                    GraphicsDirector.this.paint( GraphicsDirector.this.getGraphics() );

                    synchronized( lockPaint ) {
                    	paintThread = null;
                    	lockPaint.notify();
                    }
                }catch( Exception e ) {
                   System.out.println("Exception in repaint() : "+e);
                }
            }
          };

       }

       paintThread.start();       
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To avoid flickering.
   */
    public void update( Graphics g ) {      
       paint( g );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To paint this JPanel.
   *
   * @param gc graphics object.
   */
    public void paint(Graphics gc) {      
         if(gc==null || getHeight()<=0 || getWidth()<=0) return;

       // double-buffer init
         if (backBufferImage == null  || getWidth() != backBufferImage.getWidth(this) || getHeight() != backBufferImage.getHeight(this))
             backBufferImage = createImage(getWidth(),getHeight());

         Graphics backBufferGraphics = backBufferImage.getGraphics();

         if(!display) {
            backBufferGraphics.setColor( Color.white );
            backBufferGraphics.fillRect( 0, 0, getWidth(), getHeight() );
            gc.drawImage(backBufferImage, 0, 0, this);
            return;
         }


         Graphics2D gc2D = (Graphics2D) backBufferGraphics;

       // Anti-aliasing init
          RenderingHints savedRenderHints = gc2D.getRenderingHints(); // save    
          RenderingHints antiARenderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                              RenderingHints.VALUE_ANTIALIAS_ON);
          antiARenderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

          boolean previousHadAntiA = false;

          synchronized( drawables ) {
             if( resetBackground ) {
               // We repaint the background
                 backBufferGraphics.setColor( Color.white );
                 backBufferGraphics.fillRect( 0, 0, getWidth(), getHeight() );
                 resetBackground = false;
             }

             drawables.resetIterator();
        
             while( drawables.hasNext() ) {
                    Drawable d = drawables.next();

                 // Set Anti-aliasing or not ?
                    if( d.wantAntialiasing() && !previousHadAntiA ) {
                        previousHadAntiA = true;
                        gc2D.setRenderingHints( antiARenderHints );
                    }
                    else if( !d.wantAntialiasing() && previousHadAntiA ) {
                        previousHadAntiA = false;
                        gc2D.setRenderingHints( savedRenderHints );
                    }

                 // paint ?
                    d.paint( gc2D, new Rectangle( screen ) );
             }
          }

       // Rendering Hints restore...
          gc2D.setRenderingHints( savedRenderHints );

       // double-buffer print
         gc.drawImage(backBufferImage, 0, 0, this);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To add a drawable.
   *
   * @param dr drawable to add.
   */
    public void addDrawable( Drawable dr ) {
    	if(dr==null) return;
    	
        synchronized( drawables ) {
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
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To remove a drawable.
   *
   * @param dr drawable to remove.
   */
   public void removeDrawable( Drawable dr ) {
        synchronized( drawables ) {
            drawables.resetIterator();

            while( drawables.hasNext() )
                if( drawables.next() == dr ) {
                    drawables.remove();
                    return;
                }
        }
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To remove all the drawables.
   */
    public void removeAllDrawables() {
        synchronized( drawables ) {
           drawables.clear();
           refDrawable = null;
           display = false;
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** The tick method updates our screen position, drawables and repaint the whole thing.
   *  Never call repaint on the graphics director, call tick() !
   */
    public void tick() {
      
      // 1 - We update our screen dimension.
         synchronized( drawables ) {
             screen.width = getWidth();
             screen.height = getHeight();
         }

      // 2 - We update our WindowPolicy
         if( getWidth()>0 && getHeight()>0 )
             windowPolicy.tick();
      
      // 3 - We tick all our sprites
         synchronized( drawables ) {
            drawables.resetIterator();

            while( drawables.hasNext() )
                if( !drawables.next().tick() )
                    drawables.remove();
         }

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

   /** Given a point we search for the first drawable that implements the DrawableOwner
    *  interface AND contains the point. We then return the owner of the drawable.
    *
    *  The search in the Drawable list is performed backward : we inspect top sprites
    *  ( high priority ) first.
    *
    *  @param x x cordinate
    *  @param y y cordinate
    *  @return the owner of the targeted drawable, null if none or not found.
    */
     public Object findOwner( int x, int y ) {

        synchronized( drawables ) {
            drawables.resetIteratorToEnd();
        
            while( drawables.hasPrev() ) {
               Drawable d = drawables.prev();

               if( d instanceof DrawableOwner )
                   if ( d.contains( x+screen.x, y+screen.y )
                        && ((DrawableOwner)d).getOwner()!=null )
                      return ( (DrawableOwner)d ).getOwner();
            }
        }

        return null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Given a point we search for the first drawable that implements the DrawableOwner
    *  interface AND contains the point. We then return the owner of the drawable.
    *
    *  @param p Point
    *  @return the owner of the targeted drawable, null if none or not found.
    */
     public Object findOwner( Point p ) {
         return findOwner( p.x, p.y );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
