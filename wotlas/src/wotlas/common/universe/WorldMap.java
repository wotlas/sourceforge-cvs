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
   private int worldMapID;
     
  /** Full name of the World
   */
   private String fullName;
   
  /** Short name of the World
   */
   private String shortName;
   
  /** Array of TownMap
   */
   private transient TownMap[] townMaps;
  
  /** List of players in the WorldMap   // CHANGE TO HASHMAP !!! AND manage hashmap size !!
   */
   private transient Player[] players;

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
    this.worldMapID = myWorldMapID;
  }
  public int getWorldMapID() {
    return worldMapID;
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
  public void setPlayers(Player[] myPlayers) {
    this.players = myPlayers;
  }
  public Player[] getPlayers() {
    return players;
  }

 /*------------------------------------------------------------------------------------*/

  /** Add a player to this world. The player must have been previously initialized.
   *  We suppose that the player.getLocation() points out our World location.
   *
   * @param player player to add
   */
   public void addPlayer( Player player )
   {
/* To change or suppress PlayerImpl is server/client specific and cannot be used here
 * directly, replace with interface player.
 * 
     if (playerImpl == null) {
        playerImpl = new PlayerImpl[1];
        playerImpl[0] = player;
     } else {
    	PlayerImpl[] myPlayerImpl = new PlayerImpl[playerImpl.length+1];
    	System.arraycopy( playerImpl, 0, myPlayerImpl, 0, playerImpl.length );
    	myPlayerImpl[playerImpl.length] = player;
    	playerImpl = myPlayerImpl;
     }
 */
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

/** removePlayer TO ADD **/

 /*------------------------------------------------------------------------------------*/

  /** Add a new TownMap object to the array {@link #townMaps townMaps}
   *
   * @param town TownMap object to add
   */
   public void addTownMap( TownMap town )
   {
      if (townMaps == null) {
         townMaps = new TownMap[town.getTownMapID()+1];
      }
      else if( townMaps.length <= town.getTownMapID() ) {
         TownMap[] myTownMaps = new TownMap[town.getTownMapID()+1];
         System.arraycopy( townMaps, 0, myTownMaps, 0, townMaps.length );
         townMaps = myTownMaps;
      }

      townMaps[town.getTownMapID()] = town;        
   }

 /*------------------------------------------------------------------------------------*/

  /** Add a new TownMap object to the array {@@link #townMaps townMaps}
   *
   * @return a new TownMap object
   */
  public TownMap addNewTownMap()
  {
    TownMap myTownMap = new TownMap();
  		
    if (townMaps == null) {
      townMaps = new TownMap[1];
      myTownMap.setTownMapID(0);
      townMaps[0] = myTownMap;
    } else {
    	TownMap[] myTownMaps = new TownMap[townMaps.length+1];
    	myTownMap.setTownMapID(townMaps.length);
    	myTownMap.setFromWorldMapID(this.worldMapID);
    	System.arraycopy(townMaps, 0, myTownMaps, 0, townMaps.length);
    	myTownMaps[townMaps.length] = myTownMap;
    	townMaps = myTownMaps;
    }
    return myTownMap;
  }

 /*------------------------------------------------------------------------------------*/

}
