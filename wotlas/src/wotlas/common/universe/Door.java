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
 
package wotlas.common.universe;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
 
 /** A Door on an InteriorMap... Doors are possessed by RoomLinks.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.RoomLink
  */

public class Door
{
 /*------------------------------------------------------------------------------------*/

   /** Does this Door has a Lock ?
    */
     private boolean hasLock;

   /** X position of the door ( top-left corner )
    */
     private int x;

   /** Y position of the door ( top-left corner )
    */
     private int y;

   /** Door Type ( indicating which pivot point to use )
    */
     private byte doorType;

   /** Points out which Door image to use.
    */
     private ImageIdentifier doorImage;

 /*------------------------------------------------------------------------------------*/

   /** Door drawable to use...
    */
   // transient private DoorDrawable doorDrawable;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
   public Door() {
         hasLock = false;
   }

 /*------------------------------------------------------------------------------------*/  

  /** To construct a Door with no lock.
   */
   public Door(int x, int y, byte doorType, ImageIdentifier doorImage ) {
         hasLock = false;
         this.x =x;
         this.y =y;
         this.doorType =doorType;
         this.doorImage = doorImage;
   }

 /*------------------------------------------------------------------------------------*/  

  /** Getter / Setter for persistence
   */
    public boolean getHasLock() {
    	return hasLock;
    }

    public void setHasLock( boolean hasLock ) {
    	this.hasLock = hasLock;
    }

     public int getX(){
     	return x;
     }

     public void setX( int x ) {
     	this.x = x;
     }

     public int getY(){
     	return y;
     }

     public void setY( int y ) {
     	this.y = y;
     }

     public byte getDoorType(){
     	return doorType;
     }

     public void setDoorType( byte doorType ) {
     	this.doorType = doorType;
     }

     public ImageIdentifier getDoorImage() {
     	return doorImage;
     }

     public void setDoorImage( ImageIdentifier doorImage ){
     	this.doorImage = doorImage;
     }

 /*------------------------------------------------------------------------------------*/  

   /** To get the Door Drawable to add to the GraphicsDirector.
    * @return DoorDrawable corresponding to this room.
    */
     public Drawable getDoorDrawable() {
      	 if( ImageLibrary.getDefaultImageLibrary() == null )
      	     return null;

       //  if(doorDrawable!=null)
       //     return (Drawable) doorDrawable;

       // Door Drawable creation
       // doorDrawable = new DoorDrawable( x, y, doorType,
       //                                  doorImage, ImageLibRef.DOOR_PRIORITY );
       // doorDrawable.useAntialiasing(true);
          return null; // tmp
     }

 /*------------------------------------------------------------------------------------*/

   /** To open the door...
    * @param from direction from where the door is opened (see DoorDrawable).
    */
     public void openDoor( byte from ) {
      /*
         if( getDoorDrawable()==null )
             return;

         doorDrawable.openDoor( from );
       */
     }

 /*------------------------------------------------------------------------------------*/  

   /** To test if the door is opened...
    */
     public boolean isDoorOpened() {
     	// if( getDoorDrawable()!=null )
     	//     return doorDrawable.isDoorOpened();
        // else
               return true;
     }

 /*------------------------------------------------------------------------------------*/
}

        
        
 