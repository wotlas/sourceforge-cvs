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

import wotlas.utils.*;

import java.util.Properties;


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
           TownMap townMap = new TownMap(758,280,12,11);
           townMaps[0] = townMap;

           worldMap.setTownMaps( townMaps );

           townMap.setTownMapID(0);
           townMap.setFullName("Tar Valon");
           townMap.setShortName("tarvalon");

           townMap.setSmallTownImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                           ImageLibRef.TOWN_SMALL_SET,
                                                           ImageLibRef.TARVALON_SMALL_IM_ACTION ) );

           townMap.setTownImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                      ImageLibRef.UNIVERSE_SET ,
                                                      ImageLibRef.TARVALON_MAP_ACTION ) );

           MapExit mapExit = null;

               mapExit = townMap.addMapExit( new ScreenRectangle(32,313,14,20) );
               mapExit.setType( MapExit.TOWN_EXIT );
               mapExit.setMapExitSide( MapExit.WEST );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0) );
               mapExit.setTargetPosition( new ScreenPoint(745,280) );

               mapExit = townMap.addMapExit( new ScreenRectangle(29,715,14,20) );
               mapExit.setType( MapExit.TOWN_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0) );
               mapExit.setTargetPosition( new ScreenPoint(763,300) );

               mapExit = townMap.addMapExit( new ScreenRectangle(30,760,13,15) );
               mapExit.setType( MapExit.TOWN_EXIT );
               mapExit.setMapExitSide( MapExit.SOUTH );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0) );
               mapExit.setTargetPosition( new ScreenPoint(763,300) );

               mapExit = townMap.addMapExit( new ScreenRectangle(556,724,11,15) );
               mapExit.setType( MapExit.TOWN_EXIT );
               mapExit.setMapExitSide( MapExit.EAST );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0) );
               mapExit.setTargetPosition( new ScreenPoint(774,284) );

               mapExit = townMap.addMapExit( new ScreenRectangle(561,386, 10, 13) );
               mapExit.setType( MapExit.TOWN_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0) );
               mapExit.setTargetPosition( new ScreenPoint(769,273) );

               mapExit = townMap.addMapExit( new ScreenRectangle(562, 181, 10, 15) );
               mapExit.setType( MapExit.TOWN_EXIT );
               mapExit.setMapExitSide( MapExit.NORTH );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0) );
               mapExit.setTargetPosition( new ScreenPoint(758,264) );


        // STEP 4 - Tar Valon West Gate Building
           Building buildings[] = new Building[1];
           Building building = new Building(208,493,10,18);
           buildings[0] = building;

           townMap.setBuildings( buildings );

           building.setBuildingID(0);

           building.setFullName("Tar Valon West Gate");
           building.setShortName("tarvalWeGate");
           building.setServerID(0);
           building.setHasTownExits(false);
           building.setHasBuildingExits(true);

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

           RoomLink roomLink = null;

           for(int i=0; i<18; i++ ) {
               rooms[i] = new Room();
               rooms[i].setRoomID(i);
               rooms[i].setMaxPlayers(30);
           }

           rooms[0].setFullName("West Bridge");
           rooms[0].setShortName("bridge1");
           rooms[0].setInsertionPoint( new ScreenPoint(70,640) );

             roomLink = rooms[0].addRoomLink( new ScreenRectangle( 170, 450, 30, 400) );
             roomLink.setRoom1ID(0);
             roomLink.setRoom2ID(1);  

               mapExit = rooms[0].addMapExit( new ScreenRectangle(0,450,30,400) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.WEST );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(200,500) );


           rooms[1].setFullName("West Bridge");
           rooms[1].setShortName("bridge2");
           rooms[1].setInsertionPoint( new ScreenPoint(320,640) );

             roomLink = rooms[1].addRoomLink( new ScreenRectangle( 450, 450, 30, 400) );
             roomLink.setRoom1ID(1);
             roomLink.setRoom2ID(2);

             rooms[1].addRoomLink( rooms[0].getRoomLink(0) );

           rooms[2].setFullName("West Gate Entry");
           rooms[2].setShortName("entry");
           rooms[2].setInsertionPoint( new ScreenPoint(640,640) );

             roomLink = rooms[2].addRoomLink( new ScreenRectangle( 790, 520, 30, 260 ) );
             roomLink.setRoom1ID(2);
             roomLink.setRoom2ID(3);  

             roomLink = rooms[2].addRoomLink( new ScreenRectangle( 695, 770, 60, 25 ) );
             roomLink.setRoom1ID(2);
             roomLink.setRoom2ID(14);  

             roomLink = rooms[2].addRoomLink( new ScreenRectangle( 700, 505, 50, 25) );
             roomLink.setRoom1ID(5);
             roomLink.setRoom2ID(2);  

             rooms[2].addRoomLink( rooms[1].getRoomLink(0) );

           rooms[3].setFullName("Tar Valon West Entry");
           rooms[3].setShortName("tarval-entry");
           rooms[3].setInsertionPoint( new ScreenPoint(960,640) );

             roomLink = rooms[3].addRoomLink( new ScreenRectangle( 800, 460, 250, 25) );
             roomLink.setRoom1ID(4);
             roomLink.setRoom2ID(3);  

             roomLink = rooms[3].addRoomLink( new ScreenRectangle( 800, 815, 250, 25) );
             roomLink.setRoom1ID(3);
             roomLink.setRoom2ID(10);  

             rooms[3].addRoomLink( rooms[2].getRoomLink(0) );

               mapExit = rooms[3].addMapExit( new ScreenRectangle(1020,485,30,330) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.EAST );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(230,500) );


           rooms[4].setFullName("North-West Street");
           rooms[4].setShortName("nw-street");
           rooms[4].setInsertionPoint( new ScreenPoint(930,40) );

             roomLink = rooms[4].addRoomLink( new ScreenRectangle( 780, 150, 20, 40) );
             roomLink.setRoom1ID(8);
             roomLink.setRoom2ID(4);  

             rooms[4].addRoomLink( rooms[3].getRoomLink(0) );

               mapExit = rooms[4].addMapExit( new ScreenRectangle(1020,0,30,460) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.NORTH );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(220,485) );

               mapExit = rooms[4].addMapExit( new ScreenRectangle(790, 0, 230, 25) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(220,485) );


           rooms[5].setFullName("West Gate - North Tower");
           rooms[5].setShortName("build-north-entry");
           rooms[5].setInsertionPoint( new ScreenPoint(620,450) );

             roomLink = rooms[5].addRoomLink( new ScreenRectangle( 450, 385, 125, 20) );
             roomLink.setRoom1ID(6);
             roomLink.setRoom2ID(5);  

             rooms[5].addRoomLink( rooms[2].getRoomLink(2) );


           rooms[6].setFullName("West Gate - North Tower");
           rooms[6].setShortName("build-north-middle");
           rooms[6].setInsertionPoint( new ScreenPoint(480,310) );

             roomLink = rooms[6].addRoomLink( new ScreenRectangle( 540, 270, 20, 65) );
             roomLink.setRoom1ID(6);
             roomLink.setRoom2ID(7);  

             roomLink = rooms[6].addRoomLink( new ScreenRectangle( 460, 190, 120, 20) );
             roomLink.setRoom1ID(8);
             roomLink.setRoom2ID(6);  

             rooms[6].addRoomLink( rooms[5].getRoomLink(0) );

           rooms[7].setFullName("West Gate - North Tower - Meeting Room");
           rooms[7].setShortName("build-north-central");
           rooms[7].setInsertionPoint( new ScreenPoint(670,290) );

             rooms[7].addRoomLink( rooms[6].getRoomLink(0) );

           rooms[8].setFullName("West Gate - North Tower");
           rooms[8].setShortName("build-north-corridor");
           rooms[8].setInsertionPoint( new ScreenPoint(660,170) );

             roomLink = rooms[8].addRoomLink( new ScreenRectangle( 680, 120, 40, 20) );
             roomLink.setRoom1ID(9);
             roomLink.setRoom2ID(8);

             rooms[8].addRoomLink( rooms[6].getRoomLink(1) );
             rooms[8].addRoomLink( rooms[4].getRoomLink(0) );

           rooms[9].setFullName("West Gate - North Tower's Store");
           rooms[9].setShortName("build-north-store");
           rooms[9].setInsertionPoint( new ScreenPoint(630,80) );

             rooms[9].addRoomLink( rooms[8].getRoomLink(0) );

           rooms[10].setFullName("South-West Street");
           rooms[10].setShortName("sw-street");
           rooms[10].setInsertionPoint( new ScreenPoint(960,1150) );

             roomLink = rooms[10].addRoomLink( new ScreenRectangle( 780, 1110, 25, 40) );
             roomLink.setRoom1ID(14);
             roomLink.setRoom2ID(10);  

             rooms[10].addRoomLink( rooms[3].getRoomLink(1) );

               mapExit = rooms[10].addMapExit( new ScreenRectangle(1020,840,30,360) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.SOUTH );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(223,510) );

               mapExit = rooms[10].addMapExit( new ScreenRectangle(790,1180,230,20) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(223,510) );

           rooms[11].setFullName("West Gate - South Tower - Room");
           rooms[11].setShortName("bsouth-room2");
           rooms[11].setInsertionPoint( new ScreenPoint(580,1140) );

             roomLink = rooms[11].addRoomLink( new ScreenRectangle( 570, 1110, 40, 20) );
             roomLink.setRoom1ID(16);
             roomLink.setRoom2ID(11);  

           rooms[12].setFullName("West Gate - South Tower - Room");
           rooms[12].setShortName("bsouth-room1");
           rooms[12].setInsertionPoint( new ScreenPoint(600,1020) );

             roomLink = rooms[12].addRoomLink( new ScreenRectangle( 670, 990, 20, 40) );
             roomLink.setRoom1ID(12);
             roomLink.setRoom2ID(14);  

           rooms[13].setFullName("West Gate - South Tower's Store");
           rooms[13].setShortName("bsouth-store");
           rooms[13].setInsertionPoint( new ScreenPoint(600,850) );

             roomLink = rooms[13].addRoomLink( new ScreenRectangle( 670, 850, 20, 40) );
             roomLink.setRoom1ID(13);
             roomLink.setRoom2ID(14);  

           rooms[14].setFullName("West Gate - South Tower Hall");
           rooms[14].setShortName("bsouth-entry");
           rooms[14].setInsertionPoint( new ScreenPoint( 730, 980 ) );

             roomLink = rooms[14].addRoomLink( new ScreenRectangle( 670, 920, 20, 40) );
             roomLink.setRoom1ID(15);
             roomLink.setRoom2ID(14);  

             roomLink = rooms[14].addRoomLink( new ScreenRectangle( 670, 1070, 20, 40) );
             roomLink.setRoom1ID(16);
             roomLink.setRoom2ID(14);

             rooms[14].addRoomLink( rooms[2].getRoomLink(1) );
             rooms[14].addRoomLink( rooms[10].getRoomLink(0) );
             rooms[14].addRoomLink( rooms[12].getRoomLink(0) );
             rooms[14].addRoomLink( rooms[13].getRoomLink(0) );

           rooms[15].setFullName("West Gate - South Tower");
           rooms[15].setShortName("bsouth-corridor1");
           rooms[15].setInsertionPoint( new ScreenPoint(600,940) );

             roomLink = rooms[15].addRoomLink( new ScreenRectangle( 520, 910, 20, 40) );
             roomLink.setRoom1ID(17);
             roomLink.setRoom2ID(15);  

             rooms[15].addRoomLink( rooms[14].getRoomLink(0) );

           rooms[16].setFullName("West Gate - South Tower");
           rooms[16].setShortName("bsouth-corridor2");
           rooms[16].setInsertionPoint( new ScreenPoint(600,1080) );

             roomLink = rooms[16].addRoomLink( new ScreenRectangle( 520, 1060, 20, 40) );
             roomLink.setRoom1ID(17);
             roomLink.setRoom2ID(16);

             rooms[16].addRoomLink( rooms[11].getRoomLink(0) );
             rooms[16].addRoomLink( rooms[14].getRoomLink(1) );

           rooms[17].setFullName("West Gate - South Tower's Office");
           rooms[17].setShortName("bsouth-office");
           rooms[17].setInsertionPoint( new ScreenPoint(480,980) );

             rooms[17].addRoomLink( rooms[15].getRoomLink(0) );
             rooms[17].addRoomLink( rooms[16].getRoomLink(0) );

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