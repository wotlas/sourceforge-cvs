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

 /** RoomLink class
  *
  * @author Petrus
  * @see wotlas.common.universe.FrontierZone
  * @see wotlas.common.universe.Door
  */
  
public class RoomLink extends wotlas.common.universe.FrontierZone
{ 
 /*------------------------------------------------------------------------------------*/
  
  /** ID of the RoomLink (index in the array {@link Room#roomLinks Room.roomLinks})
   */
   private int RoomLinkID;
   
  /** ID of the first Room
   */
   private int room1ID;
  
  /** ID of the second Room
   */
   private int room2ID;
  
  /** door of the Room
   */
   private Door door;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
   public RoomLink() {}
 
 /*------------------------------------------------------------------------------------*/
  /*
   * List of setter and getter used for persistence
   */

  public void setRoomLinkID(int myRoomLinkID) {
    this.RoomLinkID = myRoomLinkID;
  }
  public int getRoomLinkID() {
    return RoomLinkID;
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
  
}