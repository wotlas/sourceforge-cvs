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
import wotlas.common.ResourceManager;
import wotlas.libs.persistence.*;
import wotlas.utils.Debug;

import java.io.*;


 /** A WorldManager provides all the methods needed to handle & manage the game world
  *  from its root.<p><br>
  *
  *  This class IS NOT directly persistent. The WorldMap instances are made persistent
  *  by calling save() & load() methods. The files are saved separatly.
  *  This is why a WorldManager is not directly persistent : we don't want to save
  *  all its data in one huge file.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.WorldMap
  * @see wotlas.libs.persistence.PropertiesConverter
  */
 
public class WorldManager
{
 /*------------------------------------------------------------------------------------*/

  /** Game Universe Name Format
   */
    public final static String UNIVERSE_HOME    = "universe";
    public final static String DEFAULT_UNIVERSE = "default";
    public final static String UNIVERSE_PREFIX  = "universe-save-";
    public final static String UNIVERSE_SUFFIX  = "";

    public final static String WORLD_FILE       = "world.cfg";
    public final static String TOWN_FILE        = "town.cfg";
    public final static String BUILDING_FILE    = "building.cfg";
    public final static String MAP_SUFFIX       = "-map.cfg";

 /*------------------------------------------------------------------------------------*/
 
  /** array of WorldMap
   */
    protected WorldMap[] worldMaps;

  /** Our resource manager/
   */
    protected ResourceManager rManager;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with resource Manager. We do not load universe data.
   * @param rManager resource manager to get the data from.
   */
    public WorldManager( ResourceManager rManager ) {
         this.rManager = rManager;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with resource manager. We attempt to load the local universe data.
   * Any error at this step will stop the program.
   *
   * @param rManager resource manager to get the data from.
   * @param loadDefault do we have to load the default universe data (true) or the
   *                    current one ?
   */
    public WorldManager( ResourceManager rManager, boolean loadDefault ) {
         this.rManager = rManager;
         loadUniverse(loadDefault);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with a pre-loaded list of World Maps.
   *  @param worldMaps list of worlds...
   */
    public WorldManager( WorldMap worldMaps[], ResourceManager rManager ) {
         this.rManager = rManager;
         this.worldMaps = worldMaps;
    }

 /*------------------------------------------------------------------------------------*/

  /** To Get a World by its ID.
   *
   * @param id worldMapID
   * @return corresponding worldMap, null if ID does not exist.
   */
    public WorldMap getWorldMapFromID( int id ) {
        if(worldMaps==null) {
           Debug.signal( Debug.ERROR, this, "No World data available." );
   	   return null;
   	}

   	if(id>=worldMaps.length || id<0) {
           Debug.signal( Debug.ERROR, this, "getWorldMapFromID : Bad world ID "+id );
   	   return null;
   	}
   	
        return worldMaps[id];
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a World list (some entries might be null).
   *  @return worldMaps possessed by this WorldManager.
   */
   public WorldMap[] getWorldMaps() {
        return worldMaps;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a World from a Wotlas location.
   *
   * @param location wotlas location
   * @return corresponding worldMap, null if ID does not exist.
   */
   public WorldMap getWorldMap( WotlasLocation location ) {
        return getWorldMapFromID( location.getWorldMapID() );
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a Town from a WotlasLocation. IMPORTANT: we assume the WotlasLocation object
   *  points out at least a townMap...
   *
   * @param location wotlas location
   * @return corresponding townMap, null if the map does not exist.
   */
   public TownMap getTownMap( WotlasLocation location ) {

        WorldMap wMap = getWorldMapFromID( location.getWorldMapID() );

        if(wMap==null)
    	   return null;

        return wMap.getTownMapFromID( location.getTownMapID() );
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a Building from a WotlasLocation. IMPORTANT: we assume the
   *  WotlasLocation object points out at least a building...
   *
   * @param location wotlas location
   * @return corresponding building, null if the map does not exist.
   */
   public Building getBuilding( WotlasLocation location ) {

        TownMap tMap = getTownMap( location );

        if(tMap==null)
    	   return null;

        return tMap.getBuildingFromID( location.getBuildingID() );
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get an InteriorMap from a WotlasLocation. IMPORTANT: we assume the
   *  WotlasLocation object points out at least an interiorMap...
   *
   * @param location wotlas location
   * @return corresponding interiorMap, null if the map does not exist.
   */
   public InteriorMap getInteriorMap( WotlasLocation location ) {

        Building bMap = getBuilding( location );

        if(bMap==null)
    	   return null;

        return bMap.getInteriorMapFromID( location.getInteriorMapID() );
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a Room from a WotlasLocation. IMPORTANT: we assume the
   *  WotlasLocation object points out a room...
   *
   * @param location wotlas location
   * @return corresponding Room, null if the map does not exist.
   */
   public Room getRoom( WotlasLocation location ) {

        InteriorMap iMap = getInteriorMap( location );

        if(iMap==null)
    	   return null;

        return iMap.getRoomFromID( location.getRoomID() );
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Add a new WorldMap object to the array {@link #worldMaps worldMaps}
   *
   * IMPORTANT: WorldManager methods are not synchronized. Handle this method
   * with care ( Server locked and no connected clients ).
   *
   * @return a new WorldMap object
   */
   public WorldMap addWorldMap() {

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

  /** To remove a player from the universe.
   * @param player the player to remove.
   */
   public void removePlayer( Player player ) {
        editPlayer( player, false );
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
   protected void editPlayer( Player player, boolean addButNotRemove ) {

      // Get Location & location type
         WotlasLocation location = player.getLocation();

         if( location==null ) {
             Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has no WotlasLocation.");
             return;
         }

      // does this world exists ?
         WorldMap world = getWorldMapFromID( location.getWorldMapID() );

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
             TownMap town = world.getTownMapFromID( location.getTownMapID() );

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
                   Building building = town.getBuildingFromID( location.getBuildingID() );

                   if(building==null)  {
                      Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has bad location." );
                      return;
                   }

                // does this interiorMap exists ?
                   InteriorMap map = building.getInteriorMapFromID( location.getInteriorMapID() );

                   if(map==null)  {
                      Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has bad location." );
                      return;
                   }
         
                // does this room exists ?
                   Room room = map.getRoomFromID( location.getRoomID() );

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
   *  loaded...
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

  /** To load the local game universe.
   *
   *  @param loadDefault do we have to load default data ?
   */
   public void loadUniverse( boolean loadDefault ) {
      int worldCount=0, townCount=0, buildingCount=0, mapCount=0;

      String universeHome =  rManager.getBase(UNIVERSE_HOME);

    /*** STEP 1 - WE LOAD LOCATIONS (default data) ***/

     // We search for the latest save...
     //   String latest = FileTools.findSave( universeHome, UNIVERSE_PREFIX, UNIVERSE_SUFFIX, true );
        String latest = null;

        if( latest==null )
            latest = DEFAULT_UNIVERSE;

        universeHome += File.separator + latest;
        File worldSaveList[] = new File( universeHome ).listFiles();

        if( worldSaveList==null ) {
            Debug.signal( Debug.ERROR, null, "No universe data found in: " + universeHome );
            return;
        }

     // ok, here we go ! we load all the worlds we can find...
        Debug.signal( Debug.NOTICE, null, "Loading Universe Data from :"+universeHome );
        worldMaps = null;

        for( int w=0; w<worldSaveList.length; w++ )
        {
           if( !worldSaveList[w].isDirectory() )
               continue;

           try
           {
             // we load the world object
                String worldHome =  universeHome + File.separator + worldSaveList[w].getName();

                if( ! new File(worldHome + File.separator + WORLD_FILE).exists() ) {
                    Debug.signal(Debug.WARNING, this, "Found Empty World directory : "+worldHome);
                    continue;
                }

                WorldMap world = (WorldMap) PropertiesConverter.load( worldHome + File.separator
                                                                 + WORLD_FILE );

                if (worldMaps == null) {
                    worldMaps = new WorldMap[world.getWorldMapID()+1];
                }
                else if( worldMaps.length <= world.getWorldMapID() ) {
                    WorldMap[] myWorldMaps = new WorldMap[world.getWorldMapID()+1];
                    System.arraycopy( worldMaps, 0, myWorldMaps, 0, worldMaps.length );
                    worldMaps = myWorldMaps;
                }

                worldMaps[world.getWorldMapID()] = world;
                worldCount++;

             // we load all the towns of this world
                File townSaveList[] = new File( worldHome ).listFiles();

                for( int t=0; t<townSaveList.length; t++ )
                {
                    if( townSaveList[t].getName().equals(WORLD_FILE) || !townSaveList[t].isDirectory())
                        continue;

                 // we load the town objects
                    String townHome =  worldHome + File.separator + townSaveList[t].getName();

                    TownMap town = (TownMap) PropertiesConverter.load( townHome + File.separator
                                                                       + TOWN_FILE );
                    world.addTownMap( town );
                    townCount++;

                 // we load all this town's buildings
                    File buildingSaveList[] = new File( townHome ).listFiles();

                    for( int b=0; b<buildingSaveList.length; b++ )
                    {
                        if( buildingSaveList[b].getName().equals(TOWN_FILE) || !buildingSaveList[b].isDirectory() )
                            continue;

                     // we load the building objects
                        String buildingHome =  townHome + File.separator + buildingSaveList[b].getName();

                        Building building = (Building) PropertiesConverter.load( buildingHome + File.separator
                                                                         + BUILDING_FILE );
                        town.addBuilding( building );
                        buildingCount++;

                     // we load all this building's maps
                        File mapsSaveList[] = new File( buildingHome ).listFiles();

                        for( int m=0; m<mapsSaveList.length; m++ )
                        {
                          if( mapsSaveList[m].getName().equals(BUILDING_FILE)
                              || mapsSaveList[m].isDirectory()
                              || !mapsSaveList[m].getName().endsWith(MAP_SUFFIX) )
                            continue;

                         // we load the building objects
                            String mapName =  buildingHome + File.separator + mapsSaveList[m].getName();

                            InteriorMap map = (InteriorMap) PropertiesConverter.load( mapName );
                            building.addInteriorMap( map );
                            mapCount++;
                        }
                    }
                }
           }
           catch( PersistenceException pe ) {
              Debug.signal( Debug.FAILURE, this, "Failed to load world: "
                            + universeHome + File.separator
                            + worldSaveList[w].getName() +"\n Message:"+pe.getMessage() );
              Debug.exit();
           }
        }

    /*** STEP 2 - WE LOAD OBJECTS (latest data) ***/
     //   String latest = FileTools.findSave( universeHome+File.separator+OBJECTS_HOME,
     //                                       OBJECTS_PREFIX, OBJECTS_SUFFIX, true );

      Debug.signal( Debug.NOTICE, null, "World Manager loaded "+worldCount+" worlds, "+townCount+" towns, "
                    +buildingCount+" buildings, "+mapCount+" maps." );
                    
    /*** STEP 3 - WE INIT THE WORLD ***/
      init();
   }

 /*------------------------------------------------------------------------------------*/

  /** To save the local game universe.
   *
   *  @param isDefault if true we save all data in their DEFAULT directory.
   *  @return true in case of success, false otherwise
   */
   public boolean saveUniverse( boolean isDefault ) {

     int worldCount=0, townCount=0, buildingCount=0, mapCount=0;
     boolean failed = false;

   /*** STEP 1 - We only have to save location data if they are to be saved as default ***/
   	
   // which home ?
      String universeHome = null;
     
      if( isDefault ) {
        universeHome = rManager.getBase(UNIVERSE_HOME+File.separator+DEFAULT_UNIVERSE);
        //  else
        //    universeHome = databasePath+File.separator+UNIVERSE_HOME+File.separator
        //                    +UNIVERSE_PREFIX+Tools.getLexicalDate()+UNIVERSE_SUFFIX;

     // We create this directory...
        new File(universeHome).mkdir();

     // ok, here we go ! we load all the worlds we can find...
        Debug.signal( Debug.NOTICE, this, "Saving Universe Data to :"+universeHome );

        for( int w=0; w<worldMaps.length; w++ )
        {
           if( worldMaps[w]==null )
               continue;

           try
           {
             // we create the world directory
                String worldHome =  universeHome + File.separator + worldMaps[w].getShortName();
                new File(worldHome).mkdir();

             // we save the world object
                PropertiesConverter.save( worldMaps[w], worldHome + File.separator + WORLD_FILE );
                worldCount++;

             // we save all the towns of this world
                TownMap towns[] = worldMaps[w].getTownMaps();

                if( towns == null )
                    continue;

                for( int t=0; t<towns.length; t++ )
                {
                    if( towns[t]==null )
                        continue;

                 // we load the town objects
                    String townHome =  worldHome + File.separator + towns[t].getShortName();
                    new File(townHome).mkdir();

                    PropertiesConverter.save( towns[t], townHome + File.separator + TOWN_FILE );
                    townCount++;

                 // we save all this town's buildings
                    Building buildings[] = towns[t].getBuildings();

                    if( buildings==null )
                        continue;

                    for( int b=0; b<buildings.length; b++ )
                    {
                        if( buildings[b]==null )
                            continue;

                     // we load the building objects
                        String buildingHome =  townHome + File.separator + buildings[b].getShortName();
                        new File(buildingHome).mkdir();

                        PropertiesConverter.save( buildings[b], buildingHome + File.separator + BUILDING_FILE );
                        buildingCount++;

                     // we save all this building's maps
                        InteriorMap interiorMaps[] = buildings[b].getInteriorMaps();

                        if( interiorMaps==null )
                            continue;

                        for( int m=0; m<interiorMaps.length; m++ )
                        {
                          if( interiorMaps[m]==null )
                             continue;

                         // we load the building objects
                            String mapName =  buildingHome + File.separator
                                              + interiorMaps[m].getShortName() + MAP_SUFFIX;

                            PropertiesConverter.save( interiorMaps[m], mapName );
                            mapCount++;
                        }
                    }
                }
           }
           catch( PersistenceException pe ) {
              failed = true;

              Debug.signal( Debug.CRITICAL, this, "Failed to save world: "
                            + universeHome + File.separator
                            + worldMaps[w].getShortName() +"\n Message:"+pe.getMessage() );
           }
        }

      }

    /*** STEP 2 - WE SAVE OBJECTS DATA ***/

      if(isDefault) {
      }


    /*** STEP 3 - Print some stats for control ***/
      if(isDefault)
         Debug.signal( Debug.NOTICE, this, "Saved "+worldCount+" worlds, "+townCount+" towns, "
                       +buildingCount+" buildings, "+mapCount+" maps." );

      return !failed;
   }

 /*------------------------------------------------------------------------------------*/

}
