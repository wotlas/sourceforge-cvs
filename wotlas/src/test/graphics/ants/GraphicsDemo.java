
package test.graphics.ants;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.policy.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Main Class.
 *
 * @author Bertrand Le Nistour
 */

public class GraphicsDemo extends JFrame implements AWTEventListener {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  // The Ant the user is going to control
     private Ant myAnt;

  // Background
     private ImageIdentifier groundImId;

  // Our graphics director
     GraphicsDirector gDirector;

  // Our ImageLibrary
     ImageLibrary imageLib;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Constructor
    */
      public GraphicsDemo() {
          super("Wotlas 2DGraphics Engine Demo");
         
       // 1 - Image Library Creation
          try{
             imageLib = ImageLibrary.createImageLibrary("../src/test/graphics/ants/graphics");
          }catch( java.io.IOException ioe ) {
              ioe.printStackTrace();
              System.exit(1);
          }

       // 2 - Graphics Director Creation
          gDirector = new GraphicsDirector( new CenterWindowPolicy() );
          getContentPane().add( gDirector, BorderLayout.CENTER );

       // 3 - Creation of the background image for the Graphicsdirector

          // 3.1 - Creation of the image identifier
             groundImId = new ImageIdentifier( ImLibRef.GROUND_CATEGORY,
                                               ImLibRef.MUDDY_GROUNDS_SET,
                                               ImLibRef.MUDDY_GROUND_ACTION );

          // 3.2 - we ask the Imagelibrary to load the image now
          //     ( due to the "-exc" Imagelibrary option, see the 
          //     Imagelibrary.createImageLibrary javadoc for details ).
             try{
                 imageLib.loadImageAction( groundImId );
             }catch( java.io.IOException ioe ) {
                ioe.printStackTrace();
                System.exit(1);
             }

          // 3.3 - we create a "MotionlessSprite" that will represent our
          //     ground image in the GraphicsDirector.
             MotionlessSprite groundSpr = new MotionlessSprite(
                                                0,                        // ground x=0
                                                0,                        // ground y=0
                                                groundImId,               // image
                                                ImLibRef.GROUND_PRIORITY, // priority
                                                false                     // no animation
                                            );

       // 4 - Creation of the drawable reference : our ant.
          myAnt = new Ant( groundSpr.getWidth()/2, groundSpr.getHeight()/2 );

       // 5 - Init of the GraphicsDirector
          gDirector.init(
                          (Drawable) groundSpr,       // background drawable
                          myAnt.getDrawable(),        // reference for screen movements
                          new Dimension( 400, 300 )   // screen default dimension
                        );

       // 6 - We add key listeners via an AWTEventListener on the GraphicsDirector.
       //     Since the javax.swing.JFrame is a little buggy on addKeyListener...
          Toolkit.getDefaultToolkit().addAWTEventListener(this, KeyEvent.KEY_EVENT_MASK);

          addWindowListener( new WindowAdapter() {
             public void windowClosing(WindowEvent e) {
               gDirector.removeAllDrawables();
               System.out.println("\nSee you in September for Wotlas Release 1.\n");
               System.exit(0);
             }
          });

       // 7 - we add 50 ants on the map...       
          for( int i=0; i<50; i++ ) {
              int x = (int)(Math.random()*(groundSpr.getWidth()-100) )+50;
              int y = (int)(Math.random()*(groundSpr.getHeight()-100) )+50;
              double angleDeg = Math.random()*360;

              Ant ant = new Ant( x, y );
              ant.setAngle( angleDeg );

              gDirector.addDrawable( ant.getDrawable() );
          }

          System.out.println("Added 50 ants...");

       // 8 - We display the whole...
          pack();
          setEnabled(true);
          show();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To receive Key events.
   */
  public void eventDispatched(AWTEvent e) {
    if( !(e instanceof KeyEvent) )
       return;

    KeyEvent key = (KeyEvent)e;

    int x=0, y=0;
    double angle=0;

       switch( key.getKeyCode() ) {
            case KeyEvent.VK_UP:
                     angle = myAnt.getAngle();
                     x = (int)( myAnt.getX() + 2*Math.cos( angle ) );
                     y = (int)( myAnt.getY() + 2*Math.sin( angle ) );
                     myAnt.setX( x );
                     myAnt.setY( y );
                     break;

            case KeyEvent.VK_DOWN:
                     angle = myAnt.getAngle();
                     x = (int)( myAnt.getX() - 1.5*Math.cos( angle ) );
                     y = (int)( myAnt.getY() - 1.5*Math.sin( angle ) );
                     myAnt.setX( x );
                     myAnt.setY( y );
                     break;

            case KeyEvent.VK_LEFT:
                     angle = myAnt.getAngle()*180/Math.PI -5;
                     myAnt.setAngle( angle );
                     break;

            case KeyEvent.VK_RIGHT:
                     angle = myAnt.getAngle()*180/Math.PI +5;
                     myAnt.setAngle( angle );
                     break;

            case KeyEvent.VK_X:
                     gDirector.removeAllDrawables();
                     System.out.println("\nSee you in September for Wotlas Release 1.\n");
                     System.exit(1);
       }
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Our tick method
    */
      public void tick() {
        // We tick our GraphicsDirector
          gDirector.tick();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   public static void main( String argv[] ) {
     
     // 1 - We create our GraphicsDemo Frame
        GraphicsDemo gDemo = new GraphicsDemo();
        
     // 2 - we tick it regularly
        Object lock = new Object();

        while( true ) {
         // 1 - tick graphicsDirector
          gDemo.tick();

         // 2 - MANDATORY : we wait 10ms to let the KeyEvents be processed            
            synchronized( lock ) {
               try{
                 lock.wait( 10 );
               }catch( Exception e ){}
            }
        }
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}