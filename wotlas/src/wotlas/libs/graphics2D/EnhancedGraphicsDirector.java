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
import java.awt.image.*;
import javax.swing.*;

/** A EnhancedGraphicsDirector is a GraphicsDirector that uses Java 1.4 VolatileImage
 *  to store the offscreen image in the graphics adapter memory, thus enabling faster
 *  rendering.<br>
 *
 *  JAVA 1.4 or better required.
 *
 * @author Aldiss
 * @see wotlas.libs.graphics2D.GraphicsDirector
 */

public class EnhancedGraphicsDirector extends GraphicsDirector {

 /*------------------------------------------------------------------------------------*/

  /** OffScreen image for the GraphicsDirector. 
   */
    protected VolatileImage vOffscreenImage;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. The window policy is not supposed to change during the life of the
   *  GraphicsDirector, but you can still change it by invoking the setWindowPolicy()
   *  method. The ImageLibrary is set to the default one : ImageLibrary.getDefault...
   *
   * @param windowPolicy a policy that manages window scrolling.
   * @exception ImageLibraryException if no ImageLibrary is found.
   */
    public EnhancedGraphicsDirector( WindowPolicy windowPolicy )
    throws ImageLibraryException {
    	this( windowPolicy, null );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor. The window policy is not supposed to change during the life of the
   *  GraphicsDirector, but you can still change it by invoking the setWindowPolicy()
   *  method. If the imageLibrary is set to null we seek for a default one.
   *
   * @param windowPolicy a policy that manages window scrolling.
   * @param imageLib ImageLibrary to use for this GraphicsDirector.
   * @exception ImageLibraryException if no ImageLibrary is found.
   */
    public EnhancedGraphicsDirector( WindowPolicy windowPolicy, ImageLibrary imageLib )
    throws ImageLibraryException {
        super(windowPolicy, imageLib);

     // image creation - we don't know its size yet...
//        vOffscreenImage = createVolatileImage(100, 100);

//        System.out.println("acc: "+vOffscreenImage.getCapabilities().isAccelerated() );
//        System.out.println("vol: "+vOffscreenImage.getCapabilities().isTrueVolatile() );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Renders the image off screen...
    */
    private void renderOffscreen() {
        do {
            if (vOffscreenImage.validate(getGraphicsConfiguration()) ==
                                                   VolatileImage.IMAGE_INCOMPATIBLE) {
              // old vImg doesn't work with new GraphicsConfig; re-create it
                 vOffscreenImage = createVolatileImage( getWidth(), getHeight() );
            }

            Graphics2D gc2D = vOffscreenImage.createGraphics();

         // Anti-aliasing init
            RenderingHints savedRenderHints = gc2D.getRenderingHints(); // save    
            RenderingHints antiARenderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                                 RenderingHints.VALUE_ANTIALIAS_ON);
            antiARenderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            boolean previousHadAntiA = false;
            final Rectangle r_screen =  new Rectangle( screen );

            gc2D.setColor( Color.white );
            gc2D.fillRect( 0, 0, getWidth(), getHeight() );

            if(display)
              synchronized( drawables ) {

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

                        d.paint( gc2D, r_screen );
                 }
              }

         // Rendering Hints restore...
            gc2D.setRenderingHints( savedRenderHints );
            gc2D.dispose();

        }
        while(vOffscreenImage.contentsLost());
    }


 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To paint this JPanel.
   *
   * @param gc graphics object.
   */
    public void paint(Graphics gc) {
         if(gc==null || screen==null || getHeight()<=0 || getWidth()<=0) return;

       // Volatile Image created ?
         if(vOffscreenImage==null) {
            vOffscreenImage = createVolatileImage( getWidth(), getHeight() );
            if(vOffscreenImage==null) return;
         }

       // Rendering Call
         do{
            int returnCode = vOffscreenImage.validate(getGraphicsConfiguration());

           /** We check the returned image state code
            */
            if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE
                || getWidth() != vOffscreenImage.getWidth()
                || getHeight() != vOffscreenImage.getHeight()) {
               // old vImg doesn't work with new GraphicsConfig; re-create it
                  vOffscreenImage = createVolatileImage( getWidth(), getHeight() );
                  renderOffscreen();
            }
            else
                  renderOffscreen(); // we refresh the image content

          // Double buffer print.
             gc.drawImage(vOffscreenImage, 0, 0, this);
         }
         while (vOffscreenImage.contentsLost());
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
