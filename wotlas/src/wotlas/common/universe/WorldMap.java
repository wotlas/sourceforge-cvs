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
import wotlas.utils.Debug;

import java.util.Hashtable;

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

  /** List of players in the WorldMap (not players in towns)
   */
   private transient Hashtable players;

 /*------------------------------------------------------------------------------------*/
  
  /**
   * Constructor
   */
   public WorldMap() {
       players = new Hashtable(10);
   }
  
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

 /*------------------------------------------------------------------------------------*/

  /** To get the list of all the players on this map.
   * IMPORTANT: before ANY process on this list synchronize your code on the "players"
   * object :
   *<pre>
   *   Hashtable players = world.getPlayers();
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

 /*------------------------------------------------------------------------------------*/

  /** Add a player to this world. The player must have been previously initialized.
   *  We suppose that the player.getLocation() points out to this World.
   *
   * @param player player to add
   * @return false if the player already exists on this WorldMap, true otherwise
   */
   public boolean addPlayer( Player player ) {
       if( players.contains( player.getPrimaryKey() ) ) {
           Debug.signal( Debug.CRITICAL, this, "addPlayer failed: key "+player.getPrimaryKey()
                         +" already in this world "+worldMapID );
           return false;
       }

       players.put( player.getPrimaryKey(), player );
       return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Removes a player from this world.
   *  We suppose that the player.getLocation() points out to this World.
   *
   * @param player player to remove
   * @return false if the player doesn't exists on this WorldMap, true otherwise
   */
   public boolean removePlayer( Player player ) {
       if( !players.contains( player.getPrimaryKey() ) ) {
           Debug.signal( Debug.CRITICAL, this, "removePlayer failed: key "+player.getPrimaryKey()
                         +" not found in this world "+worldMapID );
           return false;
       }

       players.remove( player.getPrimaryKey() );
       return true;
   }

 /*------------------------------------------------------------------------------------*/

  /** To Get a Town by its ID.
   *
   * @param id townMapID
   * @return corresponding townMap, null if ID does not exist.
   */
   public TownMap getTownMapByID( int id ) {
   	if(id>=townMaps.length || id<0) {
           Debug.signal( Debug.ERROR, this, "getTownMapByID : Bad town ID "+id );
   	   return null;
   	}
   	
        return townMaps[id];
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

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

  /** To init this world ( it rebuilds shortcuts ). This method calls the init() method
   *  of the TownMaps. You must only call this method when ALL the world data has been
   *  loaded.
   */
   public void init(){

    // 1 - any data ?
       if(townMaps==null) {
          Debug.signal(Debug.WARNING, this, "WorldMap init failed: No Towns.");
          return;
       }

    // 2 - we transmit the init() call
       for( int i=0; i<townMaps.length; i++ )
            if( townMaps[i]!=null )
                townMaps[i].init();
   }

 /*------------------------------------------------------------------------------------*/

}
