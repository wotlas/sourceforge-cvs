/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
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
import wotlas.common.router.*;

import wotlas.utils.Debug;

import java.io.File;


 /** A WorldManager provides all the methods needed to handle & manage the game world
  *  from its root.<p><br>
  *
  *  This class IS NOT directly persistent. The WorldMap instances are made persistent
  *  by calling saveUniverse() & loadUniverse() methods. The files are saved separatly.
  *  This is why a WorldManager is not directly persistent : we don't want to save
  *  all its data in one huge file.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.WorldMap
  * @see wotlas.libs.persistence.PropertiesConverter
  */
 
public class WorldManager {

 /*------------------------------------------------------------------------------------*/

  /** Game Universe Name Format
   */
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

  /** Our resource manager.
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

         if(worldMaps==null) {
            Debug.signal( Debug.FAILURE, this, "No universe data available !" );
            Debug.exit();
         }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with a pre-loaded list of World Maps.
   *  @param worldMaps list of worlds...
   */
    public WorldManager( WorldMap worldMaps[], ResourceManager rManager ) {
         this.rManager = rManager;
         this.worldMaps = worldMaps;
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

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init this worldmap for message routing. We create an appropriate message router
   *  for the world/towns/rooms via the provided factory. This method should be called once
   *  on the WorldManager at start-up. It's your job to create the factory.
   *
   *  If you don't call this method you won't be able to manage message routing among
   *  the different locations.
   *
   * @param msgRouterFactory our router factory for MessageRouter creation.
   */
    public void initMessageRouting( MessageRouterFactory msgRouterFactory ){
     // 1 - any data ?
        if(worldMaps==null) {
           Debug.signal(Debug.WARNING, this, "Universe routing inits failed: No WorldMaps.");
           return;
        }

     // 2 - we transmit the initMessageRouting() call
        for( int i=0; i<worldMaps.length; i++ )
             if( worldMaps[i]!=null )
                 worldMaps[i].initMessageRouting( msgRouterFactory, this );
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
 
  /** Add a player to this universe.
   * @param player player to add to this world.
   */
   public void addPlayerToUniverse( Player player ) {
       editPlayer( player, true ); // no control on server location, we assume locality
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Remove a player from the universe.
   * @param player the player to remove.
   */
   public void removePlayerFromUniverse( Player player ) {
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
                world.getMessageRouter().addPlayer( player );
             else
                world.getMessageRouter().removePlayer( player );
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
                    town.getMessageRouter().addPlayer( player );
                 else
                    town.getMessageRouter().removePlayer( player );
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
                      room.getMessageRouter().addPlayer( player );
                   else
                      room.getMessageRouter().removePlayer( player );                   
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

 /*------------------------------------------------------------------------------------*/

  /** To load the local game universe.
   *
   *  @param loadDefault do we have to load default data ?
   */
   public void loadUniverse( boolean loadDefault ) {
      int worldCount=0, townCount=0, buildingCount=0, mapCount=0;

      String universeHome =  rManager.getUniverseDataDir()+DEFAULT_UNIVERSE+"/";

    /*** STEP 1 - WE LOAD LOCATIONS (default data) ***/

      String worldList[] = rManager.listUniverseDirectories( universeHome );
      Debug.signal( Debug.NOTICE, null, "Loading Universe Data from :"+universeHome );
      worldMaps = null;

     // ok, here we go ! we load all the worlds we can find...
        for( int w=0; w<worldList.length; w++ ) {

          // we load the world object
             if(worldList[w].endsWith("/CVS/") || worldList[w].endsWith("\\CVS\\"))
                continue; // this is a CVS directory

             WorldMap world = (WorldMap) rManager.loadObject( worldList[w] + WORLD_FILE );

             if( world==null ) {
                 Debug.signal(Debug.WARNING, this, "Failed to load World : "+worldList[w]);
                 continue;
             }

             if(worldMaps == null) {
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
             String townList[] = rManager.listUniverseDirectories( worldList[w] );

             for( int t=0; t<townList.length; t++ ) {

               // we load the town objects
                  TownMap town = (TownMap) rManager.loadObject( townList[t] + TOWN_FILE );

                  if( town==null ) {
                      Debug.signal(Debug.WARNING, this, "Failed to load Town : "+townList[t]);
                      continue;
                  }

                  world.addTownMap( town );
                  townCount++;

               // we load all this town's buildings
                  String buildingList[] = rManager.listUniverseDirectories( townList[t] );

                  for( int b=0; b<buildingList.length; b++ ) {

                     // we load the building objects
                        Building building = (Building) rManager.loadObject( buildingList[b] + BUILDING_FILE );

                        if( building==null ) {
                            Debug.signal(Debug.WARNING, this, "Failed to load building : "+buildingList[b]);
                            continue;
                        }

                        town.addBuilding( building );
                        buildingCount++;

                     // we load all this building's maps
                        String mapList[] = rManager.listUniverseFiles( buildingList[b], MAP_SUFFIX );

                        for( int m=0; m<mapList.length; m++ ) {
                            if( mapList[m].equals( buildingList[b]+BUILDING_FILE ) )
                                continue;

                         // we load the map objects
                            InteriorMap map = (InteriorMap) rManager.loadObject( mapList[m] );

                            if( map==null ) {
                                Debug.signal(Debug.WARNING, this, "Failed to load map : "+mapList[m]);
                                continue;
                            }

                            building.addInteriorMap( map );
                            mapCount++;
                        }
                  }
             }
        }

       Debug.signal( Debug.NOTICE, null, "World Manager loaded "+worldCount+" worlds, "+townCount+" towns, "
                    +buildingCount+" buildings, "+mapCount+" maps." );

    /*** STEP 2 - WE LOAD OBJECTS (latest data) ***/
     //   String latest = FileTools.findSave( universeHome+File.separator+OBJECTS_HOME,
     //                                       OBJECTS_PREFIX, OBJECTS_SUFFIX, true );
                    
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

   /*** STEP 1 - We only have to save location data if they are to be saved as default ***/

     if( isDefault ) {

        String universeHome = rManager.getUniverseDataDir()+DEFAULT_UNIVERSE+"/";

     // We create this directory... (we expect that the directory is outside the JAR)
        new File(universeHome).mkdirs();

     // ok, here we go ! we save all our world data...
        Debug.signal( Debug.NOTICE, this, "Saving Universe Data to :"+universeHome );

        for( int w=0; w<worldMaps.length; w++ ) {
             if( worldMaps[w]==null ) continue;
    
          // we create the world directory
             String worldHome =  universeHome + worldMaps[w].getShortName() + "/";
             new File(worldHome).mkdir();

          // we save the world object
             if( !rManager.saveObject( worldMaps[w], worldHome + WORLD_FILE ) ) {
                 Debug.signal(Debug.ERROR,this,"Failed to save world : "+worldHome );
                 continue;
             }

             worldCount++;

          // we save all the towns of this world
             TownMap towns[] = worldMaps[w].getTownMaps();
             if( towns == null ) continue;

             for( int t=0; t<towns.length; t++ ) {
                  if( towns[t]==null ) continue;

               // we save the town object
                  String townHome =  worldHome + towns[t].getShortName()+"/";
                  new File(townHome).mkdir();

                  if( !rManager.saveObject( towns[t], townHome + TOWN_FILE ) ) {
                      Debug.signal(Debug.ERROR,this,"Failed to save town : "+townHome );
                      continue;
                  }

                  townCount++;

               // we save all this town's buildings
                  Building buildings[] = towns[t].getBuildings();
                  if( buildings==null ) continue;

                  for( int b=0; b<buildings.length; b++ ) {
                       if( buildings[b]==null ) continue;

                    // we save the building objects
                       String buildingHome =  townHome + buildings[b].getShortName() + "/";
                       new File(buildingHome).mkdir();

                       if( !rManager.saveObject( buildings[b], buildingHome + BUILDING_FILE ) ) {
                           Debug.signal(Debug.ERROR,this,"Failed to save building : "+buildingHome );
                           continue;
                       }

                       buildingCount++;

                    // we save all this building's maps
                       InteriorMap interiorMaps[] = buildings[b].getInteriorMaps();
                       if( interiorMaps==null ) continue;

                       for( int m=0; m<interiorMaps.length; m++ ) {
                            if( interiorMaps[m]==null ) continue;

                         // we save the maps
                            String mapName =  buildingHome + interiorMaps[m].getShortName() + MAP_SUFFIX;

                            if( !rManager.saveObject( interiorMaps[m], mapName ) ) {
                                Debug.signal(Debug.ERROR,this,"Failed to save map : "+mapName );
                                continue;
                            }

                            mapCount++;
                       }
                  }
             }
        }
     }

    /*** STEP 2 - WE SAVE OBJECTS DATA ***/



    /*** STEP 3 - Print some stats for control ***/
     if(isDefault)
         Debug.signal( Debug.NOTICE, this, "Saved "+worldCount+" worlds, "+townCount+" towns, "
                       +buildingCount+" buildings, "+mapCount+" maps." );

     return true;
  }

 /*------------------------------------------------------------------------------------*/

}
