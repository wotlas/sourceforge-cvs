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
 
package wotlas.common;

import wotlas.common.universe.*;

import wotlas.common.Player;

import wotlas.utils.Debug;

 /** A WorldManager provides all the methods needed to handle & manage the game world
  *  from its root.<p><br>
  *
  *  This class IS NOT directly persistent. The WorldMap instances are made persistent
  *  by calling the PersistenceManager BUT the PersistenceManager save them in separated
  *  files. This is why a WorldManager is not directly persistent : we don't want to save
  *  all its data in one huge file.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.WorldMap
  */
 
public class WorldManager
{
 /*------------------------------------------------------------------------------------*/
 
  /** array of WorldMap
   */
   protected WorldMap[] worldMaps;

 /*------------------------------------------------------------------------------------*/
  
  /** Constructor. Attemps to load the local universe data. Any error at this
   * step will stop the program.
   */

   public WorldManager() {
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a World by its ID.
   *
   * @param id worldMapID
   * @return corresponding worldMap, null if ID does not exist.
   */
   public WorldMap getWorldMapByID( int id ) {
        if(worldMaps==null) {
           Debug.signal( Debug.ERROR, this, "No World data available." );
   	   return null;
   	}

   	if(id>=worldMaps.length || id<0) {
           Debug.signal( Debug.ERROR, this, "getWorldMapByID : Bad world ID "+id );
   	   return null;
   	}
   	
        return worldMaps[id];
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Add a new WorldMap object to the array {@link #worldMaps worldMaps}
   *
   * IMPORTANT: WorldManager methods are not synchronized. Handle this method
   * with care ( Server locked and no connected clients ).
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

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Add a player to this universe. This method is called for inits and should NOT
   *  be used in any other cases. Use movePlayer instead.
   *
   * @param player player to add to this world.
   */
   public void addNewPlayer( Player player ) {
       editPlayer( player, true ); // no control on server location, we assume locality
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a player to this universe (addButNotRemove=true) or removes a player from
   *  this universe (addButNotRemove=false). The player must have been previously initialized.
   *  IMPORTANT: if the location points out a room we assume that the room is LOCAL, i.e.
   *             local to this server.
   *
   * @param player player to add/remove
   * @param addButNotRemove set to true if tou want to add this player, set to false
   *        if you want to remove the player.
   */
   protected void editPlayer( Player player, boolean addButNotRemove )
   {
      // Get Location & location type
         WotlasLocation location = player.getLocation();

         if( location==null ) {
             Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has no WotlasLocation.");
             return;
         }

      // does this world exists ?
         WorldMap world = getWorldMapByID( location.getWorldMapID() );

         if( world==null ) {
             Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has bad location.");
             return;
         }

      // add/remove player
         if( location.isWorld() ) {
             if(addButNotRemove)
                world.addPlayer( player );
             else
                world.removePlayer( player );
         }
         else{
          // does this town exists ?
             TownMap town = world.getTownMapByID( location.getTownMapID() );

             if(town==null)  {
                Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has bad location." );
                return;
             }
         
             if( location.isTown() ) {
                 if(addButNotRemove)
                    town.addPlayer( player );
                 else
                    town.removePlayer( player );
             }
             else if( location.isRoom() )
             {
                // does this building exists ?
                   Building building = town.getBuildingByID( location.getBuildingID() );

                   if(building==null)  {
                      Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has bad location." );
                      return;
                   }

                // does this interiorMap exists ?
                   InteriorMap map = building.getInteriorMapByID( location.getInteriorMapID() );

                   if(map==null)  {
                      Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has bad location." );
                      return;
                   }
         
                // does this room exists ?
                   Room room = map.getRoomByID( location.getRoomID() );

                   if(room==null)  {
                      Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has bad location." );
                      return;
                   }

                // pheewww... ok, we add/remove this player...
                   if(addButNotRemove)
                      room.addPlayer( player );
                   else
                      room.removePlayer( player );                   
             }
             else
                Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has strange location." );        
        }
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a valid WorldID ( for player inits ).
   *
   * @return a valid worldMap ID, -1 if there are none
   */
   public int getAValidWorldID() {
   	if(worldMaps==null)
   	   return -1;
   	   
   	for(  int i=0; i<worldMaps.length; i++ )
   	      if( worldMaps[i]!=null )
   	          return i;
   	
        return -1;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To initialize this whole universe ( it rebuilds shortcuts ). This method calls
   *  recursively the init() method of the WorldMaps, TownMaps, buildings, interiorMaps
   *  and rooms.
   *
   *  IMPORTANT: You must ONLY call this method ONE time when ALL the world data has been
   *  loaded by the persistence manger...
   */
   protected void init() {

    // 1 - any data ?
       if(worldMaps==null) {
          Debug.signal(Debug.WARNING, this, "Universe inits failed: No WorldMaps.");
          return;
       }

    // 2 - we transmit the init() call
       for( int i=0; i<worldMaps.length; i++ )
            if( worldMaps[i]!=null )
                worldMaps[i].init();
   }

 /*------------------------------------------------------------------------------------*/

}
