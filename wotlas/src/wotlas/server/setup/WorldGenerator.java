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

package wotlas.server.setup;

import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.server.*;

import wotlas.libs.graphics2D.ImageIdentifier;

import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.utils.Tools;

import java.util.Properties;
import java.awt.Point;
import java.awt.Rectangle;


/** A small utility to generate <<Wotlas release 1>>'s default world.
 *
 * @author Aldiss
 */

public class WorldGenerator {

 /*------------------------------------------------------------------------------------*/

   /** Static Link to Server Config File.
    */
    public final static String SERVER_CONFIG = "../src/config/server.cfg";

 /*------------------------------------------------------------------------------------*/

   /** Complete Path to the database where are stored the universe and the client
    *  accounts.
    */
      private static String databasePath;

   /** Other eventual properties.
    */
      private static Properties properties;

   /** Our Persistence Manager.
    */
      private static wotlas.server.PersistenceManager persistenceManager;

 /*------------------------------------------------------------------------------------*/

  /** Main method.
   *  @param argv not used
   */
    public static void main( String argv[] ) {

        // STEP 1 - We load the database path. Where is the data ?
           properties = FileTools.loadPropertiesFile( SERVER_CONFIG );

             if( properties==null ) {
                Debug.signal( Debug.FAILURE, null, "No valid server.cfg file found !" );
                System.exit(1);
             }

           databasePath = properties.getProperty( "DATABASE_PATH" );

             if( databasePath==null ) {
                Debug.signal( Debug.FAILURE, null, "No Database Path specified in config file !" );
                System.exit(1);
             }

           Debug.signal( Debug.NOTICE, null, "DataBase Path Found : "+databasePath );

        // STEP 2 - WORLD CREATION : RANDLAND
           WorldMap worldMaps[] = new WorldMap[1];

           WorldMap worldMap = new WorldMap();
           worldMaps[0] = worldMap;

           worldMap.setWorldMapID(0);     
           worldMap.setFullName("RandLand");
           worldMap.setShortName("randland");
           worldMap.setWorldImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                        ImageLibRef.UNIVERSE_SET,
                                                        ImageLibRef.RANDLAND_MAP_ACTION ) );

        // STEP 3 - Tar Valon Creation
           TownMap townMaps[] = new TownMap[1];
           TownMap townMap = new TownMap();
           townMaps[0] = townMap;

           worldMap.setTownMaps( townMaps );

           townMap.setTownMapID(0);
           townMap.setFullName("Tar Valon");
           townMap.setShortName("tarvalon");

           townMap.setWorldMapRectangle( new Rectangle(758,280,12,11) );
           townMap.setSmallTownImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                           ImageLibRef.TOWN_SMALL_SET,
                                                           ImageLibRef.TARVALON_SMALL_IM_ACTION ) );

           townMap.setTownImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                      ImageLibRef.UNIVERSE_SET ,
                                                      ImageLibRef.TARVALON_MAP_ACTION ) );

        // STEP 4 - Tar Valon West Gate Building
           Building buildings[] = new Building[1];
           Building building = new Building();
           buildings[0] = building;

           townMap.setBuildings( buildings );

           building.setBuildingID(0);

           building.setFullName("Tar Valon West Gate");
           building.setShortName("tarvalWeGate");
           building.setServerID(0);
           building.setHasTownExits(false);
           building.setHasBuildingExits(true);
           building.setTownMapRectangle( new Rectangle(208,493,10,18) );

           building.setSmallBuildingImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                                ImageLibRef.BUILDING_SMALL_SET,
                                                                ImageLibRef.TARVALON_WEGATE_SMALL_IM_ACTION ) );

        // STEP 5 - Tar Valon West Gate InteriorMap
           InteriorMap maps[] = new InteriorMap[1];
           InteriorMap map = new InteriorMap();
           maps[0] = map;

           building.setInteriorMaps( maps );

           map.setInteriorMapID(0);
           map.setFullName("Tar Valon West Gate - First Level");
           map.setShortName("firstlevel");
           map.setInteriorMapImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                      ImageLibRef.UNIVERSE_SET ,
                                                      ImageLibRef.TARVALON_WEST_GATE_MAP_ACTION ) );
           map.setImageWidth(1050);
           map.setImageHeight(1200);
           map.setImageRegionWidth(350);
           map.setImageRegionHeight(400);


        // STEP 6 - Rooms of TarValon WestGate InteriorMap
           Room rooms[] = new Room[18];
           map.setRooms( rooms );

           for(int i=0; i<18; i++ ) {
               rooms[i] = new Room();
               rooms[i].setRoomID(i);
               rooms[i].setMaxPlayers(30);
           }

           rooms[0].setFullName("West Bridge");
           rooms[0].setShortName("bridge1");
           rooms[0].setInsertionPoint( new Point(70,640) );

           rooms[1].setFullName("West Bridge");
           rooms[1].setShortName("bridge2");
           rooms[1].setInsertionPoint( new Point(320,640) );

           rooms[2].setFullName("West Gate Entry");
           rooms[2].setShortName("entry");
           rooms[2].setInsertionPoint( new Point(640,640) );

           rooms[3].setFullName("Tar Valon West Entry");
           rooms[3].setShortName("tarval-entry");
           rooms[3].setInsertionPoint( new Point(960,640) );

           rooms[4].setFullName("North-West Street");
           rooms[4].setShortName("nw-street");
           rooms[4].setInsertionPoint( new Point(930,40) );

           rooms[5].setFullName("West Gate - North Tower");
           rooms[5].setShortName("build-north-entry");
           rooms[5].setInsertionPoint( new Point(620,450) );

           rooms[6].setFullName("West Gate - North Tower");
           rooms[6].setShortName("build-north-middle");
           rooms[6].setInsertionPoint( new Point(480,310) );

           rooms[7].setFullName("West Gate - North Tower - Meeting Room");
           rooms[7].setShortName("build-north-central");
           rooms[7].setInsertionPoint( new Point(670,290) );

           rooms[8].setFullName("West Gate - North Tower");
           rooms[8].setShortName("build-north-corridor");
           rooms[8].setInsertionPoint( new Point(660,170) );

           rooms[9].setFullName("West Gate - North Tower's Store");
           rooms[9].setShortName("build-north-store");
           rooms[9].setInsertionPoint( new Point(630,80) );

           rooms[10].setFullName("South-West Street");
           rooms[10].setShortName("sw-street");
           rooms[10].setInsertionPoint( new Point(960,1150) );

           rooms[11].setFullName("West Gate - South Tower - Room");
           rooms[11].setShortName("bsouth-room2");
           rooms[11].setInsertionPoint( new Point(580,1140) );

           rooms[12].setFullName("West Gate - South Tower - Room");
           rooms[12].setShortName("bsouth-room1");
           rooms[12].setInsertionPoint( new Point(600,1020) );

           rooms[13].setFullName("West Gate - South Tower's Store");
           rooms[13].setShortName("bsouth-store");
           rooms[13].setInsertionPoint( new Point(600,850) );

           rooms[14].setFullName("West Gate - South Tower Hall");
           rooms[14].setShortName("bsouth-entry");
           rooms[14].setInsertionPoint( new Point( 730, 980 ) );

           rooms[15].setFullName("West Gate - South Tower");
           rooms[15].setShortName("bsouth-corridor1");
           rooms[15].setInsertionPoint( new Point(600,940) );

           rooms[16].setFullName("West Gate - South Tower");
           rooms[16].setShortName("bsouth-corridor2");
           rooms[16].setInsertionPoint( new Point(600,1080) );

           rooms[17].setFullName("West Gate - South Tower's Office");
           rooms[17].setShortName("bsouth-office");
           rooms[17].setInsertionPoint( new Point(480,980) );

        // STEP 3 - We save this simple universe.
           persistenceManager = wotlas.server.PersistenceManager.createPersistenceManager( databasePath );
           Debug.signal( Debug.NOTICE, null, "Persistence Manager Created..." );

           if( persistenceManager.saveLocalUniverse( worldMaps, true ) )
               Debug.signal( Debug.NOTICE, null, "World Save Succeeded..." );
           else
               Debug.signal( Debug.NOTICE, null, "World Save Failed..." );
    }

 /*------------------------------------------------------------------------------------*/
}