
package test.graphics.color;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.filter.*;

/** An ant...
 *
 * @author Bertrand Le Nistour
 */

public class Perso implements SpriteDataSupplier
{
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  // our ant cordinates
     private int x;
     private int y;

  // our angle (in rads)
     private double angleRad;

  // our image in the Imagelibrary
     private Animation imId;

  // our sprite
     private Sprite sprAnt;

  // Locks
     private byte xLock[] = new byte[0];
     private byte yLock[] = new byte[0];
     private byte angleLock[] = new byte[0];
     private byte imageLock[] = new byte[0];

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  /** Constructor with initial position.
   */
     public Perso( int x, int y ) {
       this.x = x;
       this.y = y;
     
       imId = new Animation( new ImageIdentifier( (short)0,(short)0,(short)0 ),(byte)3 );
       sprAnt = new Sprite( (SpriteDataSupplier) this, (short)10 );
       sprAnt.setDynamicImageFilter( new ColorImageFilter() );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the X image position.
   * @return x image cordinate
   */
    public int getX(){
      synchronized( xLock ) {
         return x;
      }
    }

  /** To get the Y image position.
   * @return y image cordinate
   */
    public int getY(){
      synchronized( yLock ) {
         return y;
      }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the image identifier to use.
   * @return image identifier.
   */
    public synchronized ImageIdentifier getImageIdentifier(){
      synchronized( imageLock ) {
         return imId.getCurrentImage();
      }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the eventual rotation angle. 0 means no rotation.
   * @return angle in radians.
   */
    public double getAngle(){
      synchronized( angleLock ) {
       return angleRad; // not used
      }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the X factor for scaling... 1.0 means no X scaling
   * @return X scale factor
   */
    public double getScaleX(){
       return 1.0; // not used
    }

  /** To get the Y factor for scaling... 1.0 means no Y scaling
   * @return Y scale factor
   */
    public double getScaleY(){
       return 1.0; // not used
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the image's transparency ( 0.0 means invisible, 1.0 means fully visible ). 
   * @return alpha
   */
    public float getAlpha(){
       return 1.0f; // not used
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set X.
   * @param x cordinate
   */
    public void setX( int x ){
      synchronized( xLock ) {
         this.x = x;
      }
    }

  /** To set Y.
   * @param y cordinate
   */
    public void setY( int y ){
      synchronized( yLock ) {
         this.y = y;
      }
    }

  /** To set the rotation angle. The angle is given in deg and is converted in radians.
   * @param angle in deg
   */
    public void setAngle( double angleDeg ){
      synchronized( angleLock ) {
         angleRad = (angleDeg/180)*Math.PI;
      }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the ant's drawable
    *  @return ant sprite casted into a Drawable
    */
    public Drawable getDrawable() {
       return (Drawable) sprAnt;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

public void tick() {
   imId.tick();	
}

}
