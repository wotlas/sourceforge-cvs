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

import wotlas.common.Player;

 
 /** TownMap class
  *
  * @author Petrus
  * @see wotlas.common.universe.WorldMap
  * @see wotlas.common.universe.Building
  */
 
public class TownMap
{
 /*------------------------------------------------------------------------------------*/
 
  /** ID of the TownMap (index in the array {@link WorldMap#towns WorldMap.towns})
   */
   private int TownMapID;
     
  /** Full name of the Town
   */
   private String fullName;
   
  /** Short name of the Town
   */
   private String shortName;
  
  /** ID of WorldMap the TownMap belongs to
   */
   private int fromWorldMapID;
  
  /** Position and Dimension of the WorldMap
   * to enter the town
   */
   private int worldMapEnter_X;
   private int worldMapEnter_Y;
   private int worldMapEnter_width;
   private int worldMapEnter_height;
   
  /** Array of Building
   */
   private Building[] buildings;
  
  /** List of players in the Town
   */
   private transient Player[] players;

  /** List of buildings to enter the town
   * first  element : BuildingID
   * second element : KnowledgeID
   */
   private int[][] buildingsEnter;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
   public TownMap() {}
   
 /*------------------------------------------------------------------------------------*/
  /*
   * List of setter and getter used for persistence
   */

  public void setTownMapID(int myTownMapID) {
    this.TownMapID = myTownMapID;
  }
  public int getTownMapID() {
    return TownMapID;
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
  public void setBuildings(Building[] myBuildings) {
    this.buildings = myBuildings;
  }
  public Building[] getBuildings() {
    return buildings;
  }
  public void setPlayerImpls(Player[] myPlayers) {
    this.players = myPlayers;
  }
  public Player[] getPlayers() {
    return players;
  }
  public void setBuildingsEnter(int[][] myBuildingsEnter) {
    this.buildingsEnter = myBuildingsEnter;
  }
  public int[][] getBuildingsEnter() {
    return buildingsEnter;
  }
  
 /*------------------------------------------------------------------------------------*/

  /** Adda new Building object to the array {@link #buildings buildings})
   *
   * @return a new Building object
   */
  public Building addBuilding()
  {
    Building myBuilding = new Building();
    
    if (buildings == null) {
      buildings = new Building[1];      
      myBuilding.setBuildingID(0);
      buildings[0] = myBuilding;
    } else {
      Building[] myBuildings = new Building[buildings.length+1];
      myBuilding.setBuildingID(buildings.length);
      System.arraycopy(buildings, 0, myBuildings, 0, buildings.length);
      myBuildings[buildings.length] = myBuilding; 
      buildings = myBuildings;      
    }    
    return myBuilding;
  }
  
}

        
        
 