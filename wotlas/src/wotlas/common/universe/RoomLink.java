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

import wotlas.utils.*;

 /** RoomLink class. Represents the screen zone which links two rooms.
  *  A RoomLink MUST not have a width equal to its height... A square form
  *  would not help us to determine the limit between the two rooms.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.Door
  */

public class RoomLink extends ScreenRectangle
{ 
 /*------------------------------------------------------------------------------------*/
  
  /** ID of the RoomLink
   */
   private int roomLinkID;
   
  /** ID of the first Room ( the one on the west or north )
   */
   private int room1ID;
  
  /** ID of the second Room ( the one on the east or south )
   */
   private int room2ID;
  
  /** door of the Room
   */
   private Door door;

 /*------------------------------------------------------------------------------------*/

  /** first Room of this link ( the one on the west or north )
   */
   transient private Room room1;

  /** second Room of this link ( the one on the west or north )
   */
   transient private Room room2;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
   public RoomLink() {}
 
 /*------------------------------------------------------------------------------------*/

  /** Constructor with ScreenRectangle.
   */
   public RoomLink(ScreenRectangle r) {
       super(r);
       
       if(r.width==r.height)
          Debug.signal( Debug.ERROR, this, "INVALID ROOMLINK: height is equal to width !!!");
   }
 
 /*------------------------------------------------------------------------------------*/

  /*
   * List of setter and getter used for persistence
   */

  public void setRoomLinkID(int myRoomLinkID) {
    this.roomLinkID = myRoomLinkID;
  }
  public int getRoomLinkID() {
    return roomLinkID;
  }
  public void setRoom1ID(int myRoom1ID) {
    this.room1ID = myRoom1ID;
  }
  public int getRoom1ID() {
    return room1ID;
  }
  public void setRoom2ID(int myRoom2ID) {
    this.room2ID = myRoom2ID;
  }
  public int getRoom2ID() {
    return room2ID;
  }
  public void setDoor(Door myDoor) {
    this.door = myDoor;
  }
  public Door getDoor() {
    return door;
  }

 /*------------------------------------------------------------------------------------*/

  // transient fields getters & setters

  public void setRoom1( Room room1) {
    this.room1 = room1;
  }

  public Room getRoom1() {
    return room1;
  }

  public void setRoom2( Room room2) {
    this.room2 = room2;
  }

  public Room getRoom2() {
    return room2;
  }

 /*------------------------------------------------------------------------------------*/

   /** Are the two RoomLinks equal ?
    *  Important : the two RoomLinks must belong to the same InteriorMap.
    */
    public boolean equals( RoomLink other ) {
        return toRectangle().equals( other.toRectangle() );
    }

 /*------------------------------------------------------------------------------------*/

   /** To print this roomLink
    */
    public String toString() {
        return "RoomLink between room "+room1ID+" <-> "+room2ID;
    }

 /*------------------------------------------------------------------------------------*/
}