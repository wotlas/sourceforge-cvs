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

 /** InteriorMap class
  *
  * @author Petrus
  */

public class InteriorMap
{
 /*------------------------------------------------------------------------------------*/

  /** ID of the InteriorMap (index in the array Building.interiorMaps)
   */
   private int InteriorMapID;
  
  /** Full name of the InteriorMap
   */
   private String fullName;
   
  /** Short name of the InteriorMap
   */
   private String shortName;
   
  /** ID of WorldMap the InteriorMap belongs to
   */
   private int fromWorldMapID;
   
  /** ID of TownMap the InteriorMap belongs to
   */
   private int fromTownMapID;
  
  /** ID of Building the InteriorMap belongs to
   */
   private int fromBuildingID;
  
  /** List of the rooms of the Building
   * non transient (rooms in the same file of the building)
   */
   private Room[] rooms;

 /*------------------------------------------------------------------------------------*/
  
  /** Constructor
   */
   public InteriorMap() {}

 /*------------------------------------------------------------------------------------*/
  /*
   * List of setter and getter used for persistence
   */  

  public void setInteriorMapID(int myInteriorMapID) {
    this.InteriorMapID = myInteriorMapID;
  }
  public int getInteriorMapID() {
    return InteriorMapID;
  }
  public void setFullName(String myFullName) {
    this.fullName = myFullName;
  }
  public String getFullName() {
    return fullName;
  }
  public void setShortName(String myShortName) {
    this.shortName = myShortName;
  }
  public String getShortName() {
    return shortName;
  }  
  public void setFromWorldMapID(int myFromWorldMapID) {
    this.fromWorldMapID = myFromWorldMapID;
  }
  public int getFromWorldMapID() {
    return fromWorldMapID;
  }
  public void setFromTownMapID(int myTownMapID) {
    this.fromTownMapID = myTownMapID;
  }
  public int getFromTownMapID() {
    return fromTownMapID;
  }
  public void setFromBuildingID(int myBuildingID) {
    this.fromBuildingID = myBuildingID;
  }
  public int getFromBuildingID() {
    return fromBuildingID;
  }
  public void setRooms(Room[] myRooms) {
    this.rooms = myRooms;
  }
  public Room[] getRooms() {
    return rooms;
  }
  
 /*------------------------------------------------------------------------------------*/

  /** Add a new Room object to the array rooms
   *
   * @return a new Room object
   */
  public Room addRoom() {
    if (rooms == null) {
      rooms = new Room[1];
    }
    
    Room[] myRooms = new Room[rooms.length+1];
    
    Room myRoom = new Room();
    myRoom.setRoomID(rooms.length);
    
    System.arraycopy(rooms, 0, myRooms, 0, rooms.length);
    myRooms[rooms.length] = myRoom;
    rooms = myRooms;
    
    return myRoom;
  }

} 
  
   