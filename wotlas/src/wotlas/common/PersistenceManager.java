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

import wotlas.libs.persistence.*;
import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.utils.Tools;

import java.io.File;

 /** Persistence Manager for Wotlas Servers. The persistence manager is the central
  * class where are saved/loaded data for the game. Mainly, this common part deals with 
  * World data ( wotlas.common.universe ) and ServerConfig.
  *
  * @author Aldiss
  * @see wotlas.libs.persistence.PropertiesConverter
  */
 
public class PersistenceManager
{
 /*------------------------------------------------------------------------------------*/

  /** Game Universe
   */
   public final static String UNIVERSE_HOME    = "universe";
   public final static String DEFAULT_UNIVERSE = "default";
   public final static String UNIVERSE_PREFIX  = "universe-save-";
   public final static String UNIVERSE_SUFFIX  = "";

   public final static String WORLD_FILE       = "world.cfg";
   public final static String TOWN_FILE        = "town.cfg";
   public final static String BUILDING_FILE    = "building.cfg";
   public final static String MAP_SUFFIX       = "-map.cfg";

  /** Server Config
   */
   public final static String SERVERS_HOME = "servers";
   public final static String SERVERS_PREFIX = "server-";
   public final static String SERVERS_SUFFIX = ".cfg";

 /*------------------------------------------------------------------------------------*/

   /** Path to the local server database.
    */
      protected String databasePath;

 /*------------------------------------------------------------------------------------*/
  
  /** Constructor.
   *
   * @param databasePath path to the local server database
   */
   protected PersistenceManager( String databasePath ) {
          this.databasePath = databasePath;
   }

 /*------------------------------------------------------------------------------------*/

  /** To load the local game universe.
   *
   *  @return a WorldMap array, null if an error occured.
   */
   public WorldMap[] loadLocalUniverse()
   {
      int worldCount=0, townCount=0, buildingCount=0, mapCount=0;

      String universeHome =  databasePath+File.separator+UNIVERSE_HOME;

     // We search for the latest save...
        String latest = FileTools.findSave( universeHome, UNIVERSE_PREFIX, UNIVERSE_SUFFIX, true );

        if( latest==null )
            latest = DEFAULT_UNIVERSE;

        universeHome += File.separator + latest;
        File worldSaveList[] = new File( universeHome ).listFiles();

        if( worldSaveList==null ) {
            Debug.signal( Debug.ERROR, this, "No universe data found in: " + universeHome );
            return null;
        }

     // ok, here we go ! we load all the worlds we can find...
        Debug.signal( Debug.NOTICE, this, "Loading Universe Data from :"+universeHome );
        WorldMap worlds[] = null;

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

                if (worlds == null) {
                    worlds = new WorldMap[world.getWorldMapID()+1];
                }
                else if( worlds.length <= world.getWorldMapID() ) {
                    WorldMap[] myWorldMaps = new WorldMap[world.getWorldMapID()+1];
                    System.arraycopy( worlds, 0, myWorldMaps, 0, worlds.length );
                    worlds = myWorldMaps;
                }

                worlds[world.getWorldMapID()] = world;
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
              System.exit(1);
           }
        }

      Debug.signal( Debug.NOTICE, this, "Loaded "+worldCount+" worlds, "+townCount+" towns,"
                    +buildingCount+" buildings, "+mapCount+" maps." );

      return worlds;
   }

 /*------------------------------------------------------------------------------------*/

  /** To save the local game universe.
   *
   *  @param a WorldMap array.
   *  @param isDefault if true we save the worlds in the universe/DEFAULT_UNIVERSE directory.
   *  @return true in case of success, fale otherwise
   */
   public boolean saveLocalUniverse( WorldMap worlds[], boolean isDefault )
   {
       int worldCount=0, townCount=0, buildingCount=0, mapCount=0;
       boolean failed = false;
   	
     // which home ?
        String universeHome = null;
     
        if( isDefault )
            universeHome = databasePath+File.separator+UNIVERSE_HOME+File.separator
                            +DEFAULT_UNIVERSE;
        else
            universeHome = databasePath+File.separator+UNIVERSE_HOME+File.separator
                            +UNIVERSE_PREFIX+Tools.getLexicalDate()+UNIVERSE_SUFFIX;

     // We create this directory...
        new File(universeHome).mkdir();

     // ok, here we go ! we load all the worlds we can find...
        Debug.signal( Debug.NOTICE, this, "Saving Universe Data to :"+universeHome );

        for( int w=0; w<worlds.length; w++ )
        {
           if( worlds[w]==null )
               continue;

           try
           {
             // we create the world directory
                String worldHome =  universeHome + File.separator + worlds[w].getShortName();
                new File(worldHome).mkdir();

             // we save the world object
                PropertiesConverter.save( worlds[w], worldHome + File.separator + WORLD_FILE );
                worldCount++;

             // we save all the towns of this world
                TownMap towns[] = worlds[w].getTownMaps();

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
                        InteriorMap interiorMaps[] = buildings[t].getInteriorMaps();

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
                            + worlds[w].getShortName() +"\n Message:"+pe.getMessage() );
           }
        }

      Debug.signal( Debug.NOTICE, this, "Saved "+worldCount+" worlds, "+townCount+" towns,"
                    +buildingCount+" buildings, "+mapCount+" maps." );

      return !failed;
   }

 /*------------------------------------------------------------------------------------*/

  /** Loads the server config associated to the given serverID.
   *
   * @param serverID id of the server which config is to be loaded.
   * @return server config
   */
   public ServerConfig loadServerConfig( int serverID )
   {
      String serverFile = databasePath+File.separator+SERVERS_HOME+File.separator
                            +SERVERS_PREFIX+serverID+SERVERS_SUFFIX;

      try{
          return (ServerConfig) PropertiesConverter.load( serverFile );
      }
      catch( PersistenceException pe ) {
          Debug.signal( Debug.ERROR, this, "Failed to load server config: "+pe.getMessage() );
          return null;
      }
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Saves the server config to the SERVERS_HOME directory.
   *
   *  @param serverConfig server config
   *  @return true in case of success, false if an error occured.
   */
   public boolean saveServerConfig( ServerConfig serverConfig )
   {
      String serverFile = databasePath+File.separator+SERVERS_HOME+File.separator
                            +SERVERS_PREFIX+serverConfig.getServerID()+SERVERS_SUFFIX;

      try{
          PropertiesConverter.save( serverConfig, serverFile );
          return true;
      }
      catch( PersistenceException pe ) {
          Debug.signal( Debug.ERROR, this, "Failed to save server config: "+pe.getMessage() );
          return false;
      }
   }

 /*------------------------------------------------------------------------------------*/

  /** Loads all the server config files found in SERVERS_HOME.
   *
   * @param serverID id of the server which config is to be loaded.
   * @return server config
   */
   public ServerConfig[] loadServerConfigs()
   {
      String serversHome = databasePath+File.separator+SERVERS_HOME;
      
      File configFileList[] = new File(serversHome).listFiles();

     // how many server files ?
        int nbFiles=0;

        for( int i=0; i<configFileList.length; i++ )
           if(configFileList[i].isFile() && configFileList[i].getName().endsWith(SERVERS_SUFFIX) )
              nbFiles++;
       
     // create ServerConfig array
        if(nbFiles==0)
           return null;

        ServerConfig configList[] = new ServerConfig[nbFiles];
        int index=0;

      try
      {
        for( int i=0; i<configFileList.length; i++ )
           if(configFileList[i].isFile() && configFileList[i].getName().endsWith(SERVERS_SUFFIX) ){
               configList[index] = (ServerConfig) PropertiesConverter.load( serversHome
                                                  +File.separator+configFileList[i].getName() );
               index++;
           }
      }
      catch( PersistenceException pe ) {
          Debug.signal( Debug.ERROR, this, "Failed to load server config: "+pe.getMessage() );
          return null;
      }

     return configList;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
