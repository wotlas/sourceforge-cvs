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
 
 /** WorldMap class
  *
  * @author Petrus
  * @see wotlas.common.universe.ServerProcess
  * @see wotlas.common.universe.TownMap
  */
 
public class WorldMap
{
 /*------------------------------------------------------------------------------------*/
 
  /** ID of the World (index in the array {@link ServerProcess#worldMaps ServerProcess.worldMaps})
   */
   private int WorldMapID;
     
  /** Full name of the World
   */
   private String fullName;
   
  /** Short name of the World
   */
   private String shortName;
   
  /** Array of TownMap
   */
   private transient TownMap[] townMaps;
  
  /** List of players in the WorldMap
   */
   private transient PlayerImpl[] playerImpls;
  
 /*------------------------------------------------------------------------------------*/
  
  /**
   * Constructor
   */
   public WorldMap() {}
  
 /*------------------------------------------------------------------------------------*/
  /*
   * List of setter and getter used for persistence
   */

  public void setWorldMapID(int myWorldMapID) {
    this.WorldMapID = myWorldMapID;
  }
  public int getWorldMapID() {
    return WorldMapID;
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
  public void setTownMaps(TownMap[] myTownMaps) {
    this.townMaps = myTownMaps;
  }
  public TownMap[] getTownMaps() {
    return townMaps;
  }
  public void setPlayerImpls(PlayerImpl[] myPlayerImpls) {
    this.playerImpls = myPlayerImpls;
  }
  public PlayerImpl[] getPlayerImpls() {
    return playerImpls;
  }

 /*------------------------------------------------------------------------------------*/

  /** Add a new TownMap object to the array {@link #townMaps townMaps}
   *
   * @return a new TownMap object
   */
  public TownMap addTownMap() {
    if (townMaps == null) {
      townMaps = new TownMap[1];
    }
    
    TownMap[] myTownMaps = new TownMap[townMaps.length+1];
    
    TownMap myTownMap = new TownMap();
    myTownMap.setTownMapID(townMaps.length);
    myTownMap.setFromWorldMapID(this.WorldMapID);
    
    System.arraycopy(townMaps, 0, myTownMaps, 0, townMaps.length);
    myTownMaps[townMaps.length] = myTownMap;
    townMaps = myTownMaps;
    
    return myTownMap;
  }

}