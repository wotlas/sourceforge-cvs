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

import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.common.Player;
import wotlas.utils.Debug;

import java.util.Hashtable;
import java.awt.Rectangle;

 /** A TownMap represents a town in our Game Universe.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.WorldMap
  * @see wotlas.common.universe.Building
  */
 
public class TownMap
{
 /*------------------------------------------------------------------------------------*/
 
  /** ID of the TownMap (index in the array {@link WorldMap#towns WorldMap.towns})
   */
    private int townMapID;
     
  /** Full name of the Town
   */
    private String fullName;
   
  /** Short name of the Town
   */
    private String shortName;

  /** Rectangle of the town on the WorldMap.
   */
    private Rectangle worldMapRectangle;

  /** Small Image (identifier) of this town for WorldMaps.
   */
    private ImageIdentifier smallTownImage;

  /** Full Image (identifier) of this town
   */
    private ImageIdentifier townImage;

 /*------------------------------------------------------------------------------------*/

  /** Link to the worldMap we belong to...
   */
    private transient WorldMap myWorldMap;

  /** Array of Building
   */
    private transient Building[] buildings;
  
  /** List of players in the Town
   */
    private transient Hashtable players;

  /** List of buildings to enter the town
   * first  element : BuildingID
   */
    private transient int[] buildingsEnter;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
    public TownMap() {
       players = new Hashtable(10);
    }
   
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*
   * List of setter and getter used for persistence
   */

    public void setTownMapID(int myTownMapID) {
      this.townMapID = myTownMapID;
    }

    public int getTownMapID() {
      return townMapID;
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

    public void setWorldMapRectangle(Rectangle r) {
      worldMapRectangle = r;
    }

    public Rectangle getWorldMapRectangle() {
      return worldMapRectangle;
    }

    public void setSmallTownImage(ImageIdentifier smallTownImage) {
      this.smallTownImage = smallTownImage;
    }

    public ImageIdentifier getSmallTownImage() {
      return smallTownImage;
    }

    public void setTownImage(ImageIdentifier townImage) {
      this.townImage = townImage;
    }

    public ImageIdentifier getTownImage() {
      return townImage;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Transient fields getter & setter
   */

    public WorldMap getMyWorldMap() {
      return myWorldMap;
    }

    public void setBuildings(Building[] myBuildings) {
      this.buildings = myBuildings;
    }

    public Building[] getBuildings() {
      return buildings;
    }

    public void setBuildingsEnter(int[] myBuildingsEnter) {
      this.buildingsEnter = myBuildingsEnter;
    }

    public int[] getBuildingsEnter() {
      return buildingsEnter;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a building by its ID.
   *
   * @param id buildingID
   * @return corresponding building, null if ID does not exist.
   */
    public Building getBuildingFromID( int id ) {
   	if(id>=buildings.length || id<0) {
           Debug.signal( Debug.ERROR, this, "getBuildingFromID : Bad building ID "+id );
   	   return null;
   	}
   	
        return buildings[id];
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the list of all the players on this map.
   * IMPORTANT: before ANY process on this list synchronize your code on the "players"
   * object :
   *<pre>
   *   Hashtable players = town.getPlayers();
   *   
   *   synchronized( players ) {
   *       ... some SIMPLE and SHORT processes...
   *   }
   *
   * @return player hashtable, player.getPrimaryKey() is the key.
   */
    public Hashtable getPlayers() {
        return players;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a player to this town. The player must have been previously initialized.
   *  We suppose that the player.getLocation() points out to this town.
   *
   * @param player player to add
   * @return false if the player already exists on this TownMap, true otherwise
   */
    public boolean addPlayer( Player player ) {
       if( players.contains( player.getPrimaryKey() ) ) {
           Debug.signal( Debug.CRITICAL, this, "addPlayer failed: key "+player.getPrimaryKey()
                         +" already in "+this);
           return false;
       }

       players.put( player.getPrimaryKey(), player );
       return true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Removes a player from this town.
   *  We suppose that the player.getLocation() points out to this town.
   *
   * @param player player to remove
   * @return false if the player doesn't exists on this townMap, true otherwise
   */
    public boolean removePlayer( Player player ) {
       if( !players.contains( player.getPrimaryKey() ) ) {
           Debug.signal( Debug.CRITICAL, this, "removePlayer failed: key "+player.getPrimaryKey()
                         +" not found in"+this);
           return false;
       }

       players.remove( player.getPrimaryKey() );
       return true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new Building object to our list {@link #buildings buildings})
   *
   * @param building Building object to add
   */
    public void addBuilding( Building building ) {
        if ( buildings == null ) {
             buildings = new Building[building.getBuildingID()+1];
        }
        else if( buildings.length <= building.getBuildingID() ) {
           Building[] myBuildings = new Building[building.getBuildingID()+1];
           System.arraycopy( buildings, 0, myBuildings, 0, buildings.length );
           buildings = myBuildings;
        }

        buildings[building.getBuildingID()] = building;        
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new Building object to the array {@@link #buildings buildings})
   *
   * @return a new Building object
   */
    public Building addNewBuilding()
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

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init this town ( it rebuilds shortcuts ). DON'T CALL this method directly, use
   *  the init() method of the associated world.
   *
   * @param myWorldMap our parent WorldMap.
   */
    public void init( WorldMap myWorldMap ) {

       this.myWorldMap = myWorldMap;

    // 1 - any data ?
       if(buildings==null) {
          Debug.signal(Debug.WARNING, this, "Town has no buildings ! "+this);
          return;
       }

    // 2 - we transmit the init() call
       for( int i=0; i<buildings.length; i++ )
            if( buildings[i]!=null )
                buildings[i].init( this );

    // 3 - we reconstruct the shortcuts... (here, buildings to enter the town)
       for( int i=0; i<buildings.length; i++ )
            if( buildings[i]!=null && buildings[i].getHasTownExits() )
            {
                if ( buildingsEnter == null )
                     buildingsEnter = new int[1];
                else {
                     int tmp[] = new int[buildingsEnter.length+1];
                     System.arraycopy( buildingsEnter, 0, tmp, 0, buildingsEnter.length );
                     buildingsEnter = tmp;
                }

                buildingsEnter[buildingsEnter.length-1] = buildings[i].getBuildingID();        
            }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** String Info.
   */
    public String toString(){
      if(buildings==null)
         return "TownMap tId:"+townMapID+" Name:"+fullName+" maxIdBuilding: no array";
      else
         return "TownMap tId:"+townMapID+" Name:"+fullName+" maxIdBuilding:"+buildings.length;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

        
        
 