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

import java.awt.*;

import wotlas.common.*;
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

   /** Max square distance from which the player can open/close this door.
    */
     public static final int MAX_ACTION_DIST = 2500;

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

   /** Door Variation Angle (in radians).
    */
     private float variationAngle;

   /** Points out which Door image to use.
    */
     private ImageIdentifier doorImage;

 /*------------------------------------------------------------------------------------*/

   /** Door drawable to use...
    */
     transient private DoorDrawable doorDrawable;

   /** For the server side : door opened ?
    */
     transient private boolean isOpened;

   /** Door RoomID.
    */
     transient private int myRoomID;

   /** Door RoomLinkID.
    */
     transient private int myRoomLinkID;

   /** Is the door displayed on screen ?
    */
     transient private boolean isDisplayed;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
   public Door() {
         hasLock = false;
         isOpened = false;
         isDisplayed = false;
   }

 /*------------------------------------------------------------------------------------*/  

  /** To construct a Door with no lock.
   */
   public Door(int x, int y, float variationAngle, byte doorType, ImageIdentifier doorImage ) {
         this();
         this.x =x;
         this.y =y;
         this.doorType =doorType;
         this.doorImage = doorImage;
         this.variationAngle = variationAngle;
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

     public void setVariationAngle (float varAngle) {
     	this.variationAngle = varAngle;
     }

     public float getVariationAngle() {
        return variationAngle;
     }

 /*------------------------------------------------------------------------------------*/

   /** To get one of my RoomID.
    */
     public int getMyRoomID() {
     	return myRoomID;
     }

   /** To set one of my RoomID.
    */
     public void setMyRoomID( int myRoomID ) {
     	this.myRoomID = myRoomID;
     }

   /** To get my RoomLinkID.
    */
     public int getMyRoomLinkID() {
     	return myRoomLinkID;
     }

   /** To set my RoomLinkID.
    */
     public void setMyRoomLinkID( int myRoomLinkID ) {
     	this.myRoomLinkID = myRoomLinkID;
     }

 /*------------------------------------------------------------------------------------*/

   /** To clean this door : erases the associated DoorDrawable.
    */
     public void clean(){
     	doorDrawable = null;
     	isDisplayed = false;
     }

 /*------------------------------------------------------------------------------------*/  

   /** To get the Door Drawable to add to the GraphicsDirector.
    * @return DoorDrawable corresponding to this room.
    */
     public Drawable getDoorDrawable() {
           if( ImageLibrary.getDefaultImageLibrary() == null )
      	       return null;

           if(doorDrawable!=null)
              return (Drawable) doorDrawable;

       //  Door Drawable creation
           doorDrawable = new DoorDrawable( x, y, 0.0f, variationAngle, doorType,
                                            doorImage, ImageLibRef.DOOR_PRIORITY );
           doorDrawable.useAntialiasing(true);
           doorDrawable.setOwner( this ); // we are the owner of this door draw...

           if(isOpened)
              doorDrawable.setOpened();
           else
              doorDrawable.setClosed();
           
           return doorDrawable;
     }

 /*------------------------------------------------------------------------------------*/

   /** To open the door...
    * @param from direction from where the door is opened (see DoorDrawable).
    */
     public synchronized void open() {
         if( getDoorDrawable()==null ) {
             isOpened = true;
             return;
         }

         doorDrawable.open();
     }

 /*------------------------------------------------------------------------------------*/  

   /** To close the door...
    * @param from direction from where the door is opened (see DoorDrawable).
    */
     public synchronized void close() {
         if( getDoorDrawable()==null ) {
             isOpened = false;
             return;
         }

         doorDrawable.close();
     }

 /*------------------------------------------------------------------------------------*/  

   /** To test if the door is opened...
    */
     public boolean isOpened() {
     	   if( getDoorDrawable()!=null )
     	       return doorDrawable.isOpened();
           else
               return isOpened;
     }

 /*------------------------------------------------------------------------------------*/

   /** To test if the door is displayed on screen...
    */
     public boolean isDisplayed() {
           return isDisplayed;
     }

 /*------------------------------------------------------------------------------------*/

   /** To set that the door is displayed on screen...
    */
     public void setIsDisplayed( boolean isDisplayed) {
           this.isDisplayed = isDisplayed;
     }

 /*------------------------------------------------------------------------------------*/

   /** To check if the player is near this door...
    * @param true if the player is near the door.
    */
     public boolean isPlayerNear( Rectangle playerRectangle ) {
       if( doorDrawable==null )
           return false;

       int xpc = playerRectangle.x + playerRectangle.width/2;
       int ypc = playerRectangle.y + playerRectangle.height/2;

       int xdc = doorDrawable.getX() + doorDrawable.getWidth()/2;
       int ydc = doorDrawable.getY() + doorDrawable.getHeight()/2;

       if( (xdc-xpc)*(xdc-xpc)+(ydc-ypc)*(ydc-ypc) <= MAX_ACTION_DIST )
           return true;

       return false;
     }

 /*------------------------------------------------------------------------------------*/

   /** To check if the player can go to the specified direction...
    * @param true if the player can move...
    */
     public boolean canMove( Rectangle playerRectangle, Point destPoint ) {
       if( doorDrawable==null || destPoint.x<0 || destPoint.y<0 )
           return true;

       int xpc = playerRectangle.x + playerRectangle.width/2;
       int ypc = playerRectangle.y + playerRectangle.height/2;

       int xdc = doorDrawable.getX() + doorDrawable.getWidth()/2;
       int ydc = doorDrawable.getY() + doorDrawable.getHeight()/2;

       if( doorType == DoorDrawable.VERTICAL_TOP_PIVOT ||
           doorType == DoorDrawable.VERTICAL_TOP_PIVOT ) {
           if( (xdc<xpc && xdc<destPoint.x) ||  (xdc>xpc && xdc>destPoint.x) )
               return true;
           return false;
       }
       else if( (ydc<ypc && ydc<destPoint.y) || (ydc>ypc && ydc>destPoint.y) )
               return true;

       return false;
     }

 /*------------------------------------------------------------------------------------*/

}

        
        
 