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
import java.awt.image.*;
import java.awt.geom.*;
import java.lang.Math;

/** The DoorDrawable will represent the action of closing and opening a door.
 *  4 style of doors are considered.
 *
 *  This drawable can have an owner ( use the getOwner/setOwner methods ).
 *
 * @author MasterBob, Aldiss
 */

public class DoorDrawable extends Drawable implements DrawableOwner {

 /*------------------------------------------------------------------------------------*/

   /**
    * The anchor point for the rotation will depent of the type of the door
    */

    /** Door's Rotation set to     *
     *                           |---|
     *                           |   |
     *                           |   |
     *                           |   |
     *                           |---|
     */
     public final static byte VERTICAL_TOP_PIVOT = 0;

   /** Door's Rotation set to    |---|
     *                           |   |
     *                           |   |
     *                           |   |
     *                           |---|
     *                             *
     */
     public final static byte VERTICAL_BOTTOM_PIVOT = 1;

   /** Door's Rotation set to    |-----------------|
    *                           *|                 |
    *                            |-----------------|
    */
     public final static byte HORIZONTAL_LEFT_PIVOT = 2;

   /** Door's Rotation set to    |-----------------|
     *                           |                 |*
     *                           |-----------------|
     */
     public final static byte HORIZONTAL_RIGHT_PIVOT = 3;


 /*------------------------------------------------------------------------------------*/

  /** Owner of this door drawable.
   */
     private Object owner;

  /** Current Image Identifier.
   */
     private ImageIdentifier image;

  /** the initial angle if needed to represent a closing door
   */
     private float iniAngle = 0f;

  /** the variationAngle
   */
     private float variationAngle = 0f;

  /** the current angle
   *  we don't take into account the iniAngle to define the currentAngle
   *  we will just do the correction due to the iniAngle on the paint method
   */
     private float currentAngle = 0f;

  /** Door's type
   */
     private byte doorType;

  /** indicate if we are opening the door
   */
    private boolean isOpening = false;

  /** indicate if we are closing the door
   */
    private boolean isClosing = false;

  /** our current Rectangle when the door is closed.
   */
    private Rectangle rDoorClosed;

  /** Rectangle representing the door opened.
   */
    private Rectangle rDoorOpened;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with doorType mode ( see public static fields ).
   *
   * @param positionX x position of the top-left corner of the door 
   * @param positionY y position of the top-left corner of the door 
   * @param iniAngle optional initial angle
   * @param variationAngle variation angle for the door (in radians, ex: +pi/2, -pi/2 );
   * @param doorType type of the door
   * @param image door image
   * @param priority sprite's priority
   */
    public DoorDrawable( int positionX, int positionY, float iniAngle, float variationAngle,
                         byte doorType, ImageIdentifier image, short priority) {
    	super();

      // Compute rectangle for door closed
        rDoorClosed = new Rectangle();

        rDoorClosed.x = positionX;
        rDoorClosed.y = positionY;
        rDoorClosed.width = ImageLibrary.getDefaultImageLibrary().getWidth( image );
        rDoorClosed.height = ImageLibrary.getDefaultImageLibrary().getHeight( image );

        
      // Compute rectangle for door opened
         rDoorOpened = new Rectangle();
         if( iniAngle<=1.0 && Math.abs(variationAngle)>=1.55 /*Math.PI/2*/ ) {
            switch( doorType ) {
             //default:
               case HORIZONTAL_LEFT_PIVOT:
                       rDoorOpened.x = rDoorClosed.x;

                       if( variationAngle>0 )
                           rDoorOpened.y = rDoorClosed.y;
                       else
                           rDoorOpened.y = rDoorClosed.y - rDoorClosed.width +rDoorClosed.height;
                       break;

                  case HORIZONTAL_RIGHT_PIVOT:
                       rDoorOpened.x = rDoorClosed.x +rDoorClosed.width-rDoorClosed.height;

                       if( variationAngle>0 )
                           rDoorOpened.y = rDoorClosed.y - rDoorClosed.width + rDoorClosed.height;
                       else
                           rDoorOpened.y = rDoorClosed.y;
                       break;

                  case VERTICAL_BOTTOM_PIVOT:
                       rDoorOpened.y = rDoorClosed.y + rDoorClosed.height - rDoorClosed.width;

                       if( variationAngle>0 )
                           rDoorOpened.x = rDoorClosed.x;
                       else
                           rDoorOpened.x = rDoorClosed.x - rDoorClosed.height +rDoorClosed.width;
                       break;

                  case VERTICAL_TOP_PIVOT:
                       rDoorOpened.y = rDoorClosed.y;

                       if( variationAngle>0 )
                           rDoorOpened.x = rDoorClosed.x - rDoorClosed.height +rDoorClosed.width;
                       else
                           rDoorOpened.x = rDoorClosed.x;
                       break;
            }

            rDoorOpened.width = rDoorClosed.height;
            rDoorOpened.height = rDoorClosed.width;
         }
         else {
           // we create a general rectangle
            switch( doorType ) {
             //default:
               case HORIZONTAL_LEFT_PIVOT:
                       rDoorOpened.x = rDoorClosed.x;
                       rDoorOpened.y = rDoorClosed.y - rDoorClosed.width;
                       rDoorOpened.width = rDoorClosed.width;
                       rDoorOpened.height = rDoorClosed.height+2*rDoorClosed.width;
                       break;

                  case HORIZONTAL_RIGHT_PIVOT:
                       rDoorOpened.x = rDoorClosed.x;
                       rDoorOpened.y = rDoorClosed.y - rDoorClosed.width;
                       rDoorOpened.width = rDoorClosed.width;
                       rDoorOpened.height = rDoorClosed.height+2*rDoorClosed.width;
                       break;

                  case VERTICAL_BOTTOM_PIVOT:
                       rDoorOpened.x = rDoorClosed.x-rDoorClosed.height;
                       rDoorOpened.y = rDoorClosed.y;
                       rDoorOpened.width = rDoorClosed.width+2*rDoorClosed.height;
                       rDoorOpened.height = rDoorClosed.height;
                       break;

                  case VERTICAL_TOP_PIVOT:
                       rDoorOpened.x = rDoorClosed.x-rDoorClosed.height;
                       rDoorOpened.y = rDoorClosed.y;
                       rDoorOpened.width = rDoorClosed.width+2*rDoorClosed.height;
                       rDoorOpened.height = rDoorClosed.height;
                       break;
            }
         }

        this.priority = priority;
        this.iniAngle = iniAngle;
        this.variationAngle = variationAngle;
        this.doorType = doorType;
        this.image = image;
        r = rDoorClosed;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the real door rectangle, not its influence zone.
   * @return strict rectangle representing the door CLOSED.
   */
    public Rectangle getRealDoorRectangle() {
    	  return new Rectangle(rDoorClosed);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the current angle of the doorDrawable
   */
    public float getCurrentAngle() {
    	return this.currentAngle;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the angle of variation of the doorDrawable
   *  @param varAngle the angle of the variation of the rotation
   */
    public void setVariationAngle (float varAngle) {
    	this.variationAngle = varAngle;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the variationAngle of the doorDrawable
   */
    public float getVariationAngle() {
    	return this.variationAngle;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the current angle of the doorDrawable
   *  @param angle the angle of the rotation
   */
    public void setCurrentAngle(float angle) {
    	this.currentAngle = angle;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   *  set/stop the action of closing door
   */
     private void setClosing( boolean closing ) {
      this.isClosing = closing;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   *  set/stop the action of opening door
   */
     private void setOpening( boolean opening ) {
      this.isOpening = opening;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   *  return true if the door is opening
   */
     public boolean isOpening() {
      return this.isOpening;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   *  return true if the door is closing
   */
     public boolean isClosing() {
      return this.isClosing;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   *  set the door to a stable closed view
   */
     public void setClosed() {
      this.isOpening = false;
      this.isClosing = false;
      this.currentAngle = this.iniAngle;
      r = rDoorClosed;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   *  set the door to a stable open view
   */
     public void setOpened() {
      this.isOpening = false;
      this.isClosing = false;
      this.currentAngle = this.iniAngle + this.variationAngle;
      r = rDoorOpened;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   *  return true if the door is in a stable closed view
   */
     public boolean isClosed() {
      if(this.isOpening == false && this.isClosing == false
         && this.currentAngle == this.iniAngle )
            return true;
       else 
          return false;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   *  return true if the door is in a stable opened view
   */
     public boolean isOpened() {
      if( (this.isOpening == false) && (this.isClosing == false)
          && (this.currentAngle == this.iniAngle + this.variationAngle) )
            return true;
       else 
          return false;
     }


  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To open the door
   */
    public void open() {
       setClosing(false);
       setOpening(true);
       r = rDoorOpened;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To close the door
   */
    public void close() {
       setOpening(false);
       setClosing(true);
       r = rDoorClosed;
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

      // 2 - any affine transform ?
         AffineTransform affTr = null;

         //calcul de la rotation eventuelle
         float rotation = iniAngle + currentAngle;

         if( rotation !=0.0 ) {
           // Rotation Transformation
              affTr = new AffineTransform();
              int anchorX=0, anchorY=0;

              switch( doorType ) {
                  //default:
                  case HORIZONTAL_LEFT_PIVOT:
                       anchorX = rDoorClosed.x+rDoorClosed.height/2;
                       anchorY = rDoorClosed.y + rDoorClosed.height/2;
                       break;

                  case HORIZONTAL_RIGHT_PIVOT:
                       anchorX = rDoorClosed.x + rDoorClosed.width - rDoorClosed.height/2;
                       anchorY = rDoorClosed.y + rDoorClosed.height/2;
                       break;

                  case VERTICAL_BOTTOM_PIVOT:
                       anchorX = rDoorClosed.x + rDoorClosed.width/2;
                       anchorY = rDoorClosed.y + rDoorClosed.height -rDoorClosed.width/2;
                       break;

                  case VERTICAL_TOP_PIVOT:
                       anchorX = rDoorClosed.x + rDoorClosed.width/2;
                       anchorY = rDoorClosed.y + rDoorClosed.width/2;
                       break;

              }

              affTr.rotate( rotation, anchorX-screen.x, anchorY-screen.y );
         }


      // 3 - image display
         BufferedImage bufIm = ImageLibrary.getDefaultImageLibrary().getImage( image );

         if( affTr==null )
             gc.drawImage( bufIm, rDoorClosed.x-screen.x, rDoorClosed.y-screen.y, null );
         else {
             affTr.translate( rDoorClosed.x-screen.x, rDoorClosed.y-screen.y );
             gc.drawImage( bufIm, affTr, null );
         }
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

       if(isOpening) {
          currentAngle += variationAngle/10;
          if( Math.abs(currentAngle) >= Math.abs(variationAngle))
              setOpened();
       }

       if(isClosing) {
          currentAngle -= variationAngle/10;
          if( Math.abs(currentAngle-variationAngle) >= Math.abs(variationAngle) )
              setClosed();
       }

       return true; // no update needed and a DoorDrawable is always "live" by default.
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the owner of this drawable. By 'owner' we mean the object which this
    *  Drawable is the graphical representation.
    *
    * @return Object owner of this drawable.
    */
     public Object getOwner() {
        return owner;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the owner of this drawable. By 'owner' we mean the object which this
    *  Drawable is the graphical representation.
    *
    * @param object owner of this drawable.
    */
     public void setOwner( Object owner ) {
        this.owner = owner;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}
