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
 
 /** Building class
  *
  * @author Petrus
  * @see wotlas.common.universe.TownMap
  * @see wotlas.common.universe.TownExit
  * @see wotlas.common.universe.BuildingExit
  */
 
public class Building
{
 /*------------------------------------------------------------------------------------*/
  /*
   * properties in all servers
   */
   
  /** ID of the Building
   */
   private int BuildingID;
     
  /** Full name of the Building
   */
   private String fullName;
   
  /** Short name of the Building
   */
   private String shortName;
  
  /** name of the Server the Building belongs to
   */
   private String fromServerName;
   
  /** ID of WorldMap the Building belongs to
   */
   private int fromWorldMapID;
  
  /** ID of TownMap the Building belongs to
   */
   private int fromTownMapID;
  
  /** is true if the Building has some TownExit
   */
   private boolean hasTownExits;
  
  /** is true if the Building has some BuildingExit
   */
   private boolean hasBuildingExits;
   
  /** ScreenZone to clic in the TownMap to enter the building
   */
   private ScreenZone townMapEnter;

 /*------------------------------------------------------------------------------------*/
  /*
   * properties only in the server the Building belongs to
   */
   
  /**
   */
   private transient InteriorMap[] interiorMaps;
   
  /**
   * 1 element : InteriorMapID
   * 2 element : Room
   * 3 element : MapExit
   */
   //private transient ?? buildingExits;
   
   /**
    * 1 element : InteriorMapID
    * 2 element : Room
    * 3 element : MapExit
    */
   //private transient ?? townExits;
   
 /*------------------------------------------------------------------------------------*/
  
  /** Constructor
   */
   public Building() {}
    
 /*------------------------------------------------------------------------------------*/
  /*
   * List of setter and getter used for persistence
   */

  public void setBuildingID(int myBuildingID) {
    this.BuildingID = myBuildingID;
  }
  public int getBuildingID() {
    return BuildingID;
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
  public void setHasTownExits(boolean myHasTownExits) {
    this.hasTownExits = myHasTownExits;
  }
  public boolean getHasTownExits() {
    return hasTownExits;
  }
  public void setHasBuildingExits(boolean myHasBuildingExits) {
    this.hasBuildingExits = myHasBuildingExits;
  }
  public boolean getHasBuildingExits() {
    return hasBuildingExits;
  }
  public void setInteriorMaps(InteriorMap[] myInteriorMaps) {
    this.interiorMaps = myInteriorMaps;
  }
  public InteriorMap[] getInteriorMaps() {
    return interiorMaps;
  }
    
 /*------------------------------------------------------------------------------------*/

  /** Add a new InteriorMap object to the array interiorMaps
   *
   * @return a new InteriorMap object
   */
  public InteriorMap addInteriorMap()
  {
    InteriorMap myInteriorMap = new InteriorMap();
    
    if (interiorMaps == null) {
      interiorMaps = new InteriorMap[1];
      myInteriorMap.setInteriorMapID(0);
      interiorMaps[0] = myInteriorMap;
    } else {
      InteriorMap[] myInteriorMaps = new InteriorMap[interiorMaps.length+1];
      myInteriorMap.setInteriorMapID(interiorMaps.length);
      System.arraycopy(interiorMaps, 0, myInteriorMaps, 0, interiorMaps.length);
      myInteriorMaps[interiorMaps.length] = myInteriorMap;
      interiorMaps = myInteriorMaps;
    }
    return myInteriorMap;
  }

}