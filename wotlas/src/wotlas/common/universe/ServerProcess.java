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
 
 /** ServerProcess class
  *
  * @author Petrus
  * @see wotlas.common.universe.WorldMap
  */
 
public class ServerProcess
{
 /*------------------------------------------------------------------------------------*/
 
  /** array of WorldMap
   */
   private WorldMap[] worldMaps;

 /*------------------------------------------------------------------------------------*/
  
  /** Constructor
   */
   public ServerProcess() {}

 /*------------------------------------------------------------------------------------*/
  /*
   * List of setter and getter used for persistence
   */

  public void setWorldMaps(WorldMap[] myWorldMaps) {
    this.worldMaps = myWorldMaps;
  }
  public WorldMap[] getWorldMaps() {
    return worldMaps;
  }
  
 /*------------------------------------------------------------------------------------*/
  
  /** Add a new WorldMap object to the array {@link #worldMaps worldMaps}
   *
   * @return a new WorldMap object
   */
  public WorldMap addWorldMap()
  {
    WorldMap myWorldMap = new WorldMap();
    
    if (worldMaps == null) {
      worldMaps = new WorldMap[1];      
      myWorldMap.setWorldMapID(0);
      worldMaps[0] = myWorldMap;
    } else {
      WorldMap[] myWorldMaps = new WorldMap[worldMaps.length+1];
      myWorldMap.setWorldMapID(worldMaps.length);
      System.arraycopy(worldMaps, 0, myWorldMaps, 0, worldMaps.length);
      myWorldMaps[worldMaps.length] = myWorldMap; 
      worldMaps = myWorldMaps;      
    }    
    return myWorldMap;
  }
   
}