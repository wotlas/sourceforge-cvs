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

           float halfPI = (float)(Math.PI/2);

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
           worldMap.setInsertionPoint( new ScreenPoint(680,455) );
           worldMap.setWorldImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                        ImageLibRef.UNIVERSE_SET,
                                                        ImageLibRef.RANDLAND_MAP_ACTION ) );
           worldMap.setMusicName("tar-valon-01.mid");

        // STEP 3 - TOWNS

        // Tar Valon Creation
           TownMap townMaps[] = new TownMap[2];
           worldMap.setTownMaps( townMaps );

           TownMap townMap = new TownMap(758,280,12,11);
           townMaps[0] = townMap;

           townMap.setTownMapID(0);
           townMap.setFullName("Tar Valon");
           townMap.setShortName("tarvalon");
           townMap.setInsertionPoint( new ScreenPoint(70,340) );

           townMap.setSmallTownImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                           ImageLibRef.TOWN_SMALL_SET,
                                                           ImageLibRef.TARVALON_SMALL_IM_ACTION ) );

           townMap.setTownImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                      ImageLibRef.UNIVERSE_SET ,
                                                      ImageLibRef.TARVALON_MAP_ACTION ) );
           townMap.setMusicName("tar-valon-01.mid");

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

        // Blight Refuge 'Town'
           townMap = new TownMap(774,115,16,15);
           townMaps[1] = townMap;

           townMap.setTownMapID(1);
           townMap.setFullName("Blight Refuge");
           townMap.setShortName("blightrefuge");
           townMap.setInsertionPoint( new ScreenPoint(0,0) );

           townMap.setSmallTownImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                           ImageLibRef.TOWN_SMALL_SET,
                                                           ImageLibRef.BLIGHT_REFUGE_SMALL_IM_ACTION ) );

           townMap.setTownImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                      ImageLibRef.UNIVERSE_SET ,
                                                      (short)-1 ) ); // no town image
           townMap.setMusicName("tar-valon-01.mid");


        // STEP 4 - Tar Valon West Gate Building
           Building buildings[] = new Building[3];
           townMaps[0].setBuildings( buildings );

           buildings[0] = new Building(208,493,10,18);
           buildings[0].setBuildingID(0);
           buildings[0].setFullName("Tar Valon West Gate");
           buildings[0].setShortName("tarvalWeGate");
           buildings[0].setServerID(0);
           buildings[0].setHasTownExits(false);
           buildings[0].setHasBuildingExits(true);
           buildings[0].setSmallBuildingImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                                ImageLibRef.BUILDING_SMALL_SET,
                                                                ImageLibRef.TARVALON_WEGATE_SMALL_IM_ACTION ) );

        // Tar Valon - North-West Clearing (Building)
           buildings[1] = new Building(55,232,18,18);
           buildings[1].setBuildingID(1);
           buildings[1].setFullName("Tar Valon - Forest - North West Clearing");
           buildings[1].setShortName("TvNWClearing");
           buildings[1].setServerID(0);
           buildings[1].setHasTownExits(false);
           buildings[1].setHasBuildingExits(true);
           buildings[1].setSmallBuildingImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                                ImageLibRef.BUILDING_SMALL_SET,
                                                                ImageLibRef.TARVALON_NWCLNG_SMALL_IM_ACTION ) );

        // Tar Valon - North-West Gate (Building)
           buildings[2] = new Building(210,400,13,21);
           buildings[2].setBuildingID(2);
           buildings[2].setFullName("Tar Valon North-West Gate");
           buildings[2].setShortName("tarvalNWGate");
           buildings[2].setServerID(0);
           buildings[2].setHasTownExits(false);
           buildings[2].setHasBuildingExits(true);
           buildings[2].setSmallBuildingImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                                ImageLibRef.BUILDING_SMALL_SET,
                                                                ImageLibRef.TARVALON_NWGATE_SMALL_IM_ACTION ) );

        // STEP 5 - Tar Valon West Gate InteriorMap
           InteriorMap maps[] = new InteriorMap[1];
           InteriorMap map = new InteriorMap();
           maps[0] = map;

           buildings[0].setInteriorMaps( maps );

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

           map.setMusicName("tar-valon-01.mid");


        // STEP 6 - Rooms of TarValon WestGate InteriorMap
           Room rooms[] = new Room[18];
           map.setRooms( rooms );

           RoomLink roomLink = null;

           for(int i=0; i<18; i++ ) {
               rooms[i] = new Room();
               rooms[i].setRoomID(i);
               rooms[i].setMaxPlayers(30);
           }

           rooms[0].setFullName("West Bridge Middle");
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

             rooms[1].addRoomLink( rooms[0].getRoomLinks()[0] );

           rooms[2].setFullName("West Gate Entry");
           rooms[2].setShortName("entry");
           rooms[2].setInsertionPoint( new ScreenPoint(640,640) );

             roomLink = rooms[2].addRoomLink( new ScreenRectangle( 780, 520, 30, 260 ) );
             roomLink.setRoom1ID(2);
             roomLink.setRoom2ID(3);  

             roomLink = rooms[2].addRoomLink( new ScreenRectangle( 695, 770, 60, 25 ) );
             roomLink.setRoom1ID(2);
             roomLink.setRoom2ID(14);  
             roomLink.setDoor( new Door( 705, 777, -halfPI, (byte)ImageLibRef.HORIZONTAL_RIGHT_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)2,ImageLibRef.HORIZONTAL_RIGHT_PIVOT) ) );

             roomLink = rooms[2].addRoomLink( new ScreenRectangle( 700, 505, 50, 25) );
             roomLink.setRoom1ID(5);
             roomLink.setRoom2ID(2);
             roomLink.setDoor( new Door( 705, 514, halfPI, (byte)ImageLibRef.HORIZONTAL_RIGHT_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)2,ImageLibRef.HORIZONTAL_RIGHT_PIVOT) ) );

             rooms[2].addRoomLink( rooms[1].getRoomLinks()[0] );

           rooms[3].setFullName("Tar Valon West Entry");
           rooms[3].setShortName("tarval-entry");
           rooms[3].setInsertionPoint( new ScreenPoint(960,640) );

             roomLink = rooms[3].addRoomLink( new ScreenRectangle( 800, 460, 250, 25) );
             roomLink.setRoom1ID(4);
             roomLink.setRoom2ID(3);  

             roomLink = rooms[3].addRoomLink( new ScreenRectangle( 800, 815, 250, 25) );
             roomLink.setRoom1ID(3);
             roomLink.setRoom2ID(10);  

             rooms[3].addRoomLink( rooms[2].getRoomLinks()[0] );

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
             roomLink.setDoor( new Door( 786, 154, -halfPI,(byte)ImageLibRef.VERTICAL_TOP_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)1,ImageLibRef.VERTICAL_TOP_PIVOT) ) );

             rooms[4].addRoomLink( rooms[3].getRoomLinks()[0] );

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

             rooms[5].addRoomLink( rooms[2].getRoomLinks()[2] );


           rooms[6].setFullName("West Gate - North Tower");
           rooms[6].setShortName("build-north-middle");
           rooms[6].setInsertionPoint( new ScreenPoint(480,310) );

             roomLink = rooms[6].addRoomLink( new ScreenRectangle( 540, 270, 20, 65) );
             roomLink.setRoom1ID(6);
             roomLink.setRoom2ID(7);  

             roomLink = rooms[6].addRoomLink( new ScreenRectangle( 460, 200, 120, 20) );
             roomLink.setRoom1ID(8);
             roomLink.setRoom2ID(6);  

             rooms[6].addRoomLink( rooms[5].getRoomLinks()[0] );

           rooms[7].setFullName("West Gate - North Tower - Meeting Room");
           rooms[7].setShortName("build-north-central");
           rooms[7].setInsertionPoint( new ScreenPoint(670,290) );

             rooms[7].addRoomLink( rooms[6].getRoomLinks()[0] );

           rooms[8].setFullName("West Gate - North Tower");
           rooms[8].setShortName("build-north-corridor");
           rooms[8].setInsertionPoint( new ScreenPoint(660,170) );

             roomLink = rooms[8].addRoomLink( new ScreenRectangle( 680, 120, 40, 20) );
             roomLink.setRoom1ID(9);
             roomLink.setRoom2ID(8);
             roomLink.setDoor( new Door( 685, 127, halfPI,(byte)ImageLibRef.HORIZONTAL_RIGHT_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)0,ImageLibRef.HORIZONTAL_RIGHT_PIVOT) ) );

             rooms[8].addRoomLink( rooms[6].getRoomLinks()[1] );
             rooms[8].addRoomLink( rooms[4].getRoomLinks()[0] );

           rooms[9].setFullName("West Gate - North Tower's Store");
           rooms[9].setShortName("build-north-store");
           rooms[9].setInsertionPoint( new ScreenPoint(630,80) );

             rooms[9].addRoomLink( rooms[8].getRoomLinks()[0] );

           rooms[10].setFullName("South-West Street");
           rooms[10].setShortName("sw-street");
           rooms[10].setInsertionPoint( new ScreenPoint(960,1150) );

             roomLink = rooms[10].addRoomLink( new ScreenRectangle( 780, 1110, 25, 40) );
             roomLink.setRoom1ID(14);
             roomLink.setRoom2ID(10);  
             roomLink.setDoor( new Door( 786, 1114, halfPI, (byte)ImageLibRef.VERTICAL_BOTTOM_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)1,ImageLibRef.VERTICAL_BOTTOM_PIVOT) ) );

             rooms[10].addRoomLink( rooms[3].getRoomLinks()[1] );

               mapExit = rooms[10].addMapExit( new ScreenRectangle(1020,840,30,360) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.SOUTH );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(223,510) );

               mapExit = rooms[10].addMapExit( new ScreenRectangle(790,1170,230,30) );
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
             roomLink.setDoor( new Door( 575, 1118, -halfPI, (byte)ImageLibRef.HORIZONTAL_RIGHT_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)0,ImageLibRef.HORIZONTAL_RIGHT_PIVOT) ) );

           rooms[12].setFullName("West Gate - South Tower - Room");
           rooms[12].setShortName("bsouth-room1");
           rooms[12].setInsertionPoint( new ScreenPoint(600,1020) );

             roomLink = rooms[12].addRoomLink( new ScreenRectangle( 670, 990, 20, 40) );
             roomLink.setRoom1ID(12);
             roomLink.setRoom2ID(14);  
             roomLink.setDoor( new Door( 676, 995, halfPI, (byte)ImageLibRef.VERTICAL_BOTTOM_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)0,ImageLibRef.VERTICAL_BOTTOM_PIVOT) ) );

           rooms[13].setFullName("West Gate - South Tower's Store");
           rooms[13].setShortName("bsouth-store");
           rooms[13].setInsertionPoint( new ScreenPoint(600,850) );

             roomLink = rooms[13].addRoomLink( new ScreenRectangle( 670, 850, 20, 40) );
             roomLink.setRoom1ID(13);
             roomLink.setRoom2ID(14);  
             roomLink.setDoor( new Door( 677, 854, -halfPI, (byte)ImageLibRef.VERTICAL_BOTTOM_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)0,ImageLibRef.VERTICAL_BOTTOM_PIVOT) ) );

           rooms[14].setFullName("West Gate - South Tower Hall");
           rooms[14].setShortName("bsouth-entry");
           rooms[14].setInsertionPoint( new ScreenPoint( 730, 980 ) );

             roomLink = rooms[14].addRoomLink( new ScreenRectangle( 670, 920, 20, 40) );
             roomLink.setRoom1ID(15);
             roomLink.setRoom2ID(14);  

             roomLink = rooms[14].addRoomLink( new ScreenRectangle( 670, 1070, 20, 40) );
             roomLink.setRoom1ID(16);
             roomLink.setRoom2ID(14);

             rooms[14].addRoomLink( rooms[2].getRoomLinks()[1] );
             rooms[14].addRoomLink( rooms[10].getRoomLinks()[0] );
             rooms[14].addRoomLink( rooms[12].getRoomLinks()[0] );
             rooms[14].addRoomLink( rooms[13].getRoomLinks()[0] );

           rooms[15].setFullName("West Gate - South Tower");
           rooms[15].setShortName("bsouth-corridor1");
           rooms[15].setInsertionPoint( new ScreenPoint(600,940) );

             roomLink = rooms[15].addRoomLink( new ScreenRectangle( 520, 910, 20, 40) );
             roomLink.setRoom1ID(17);
             roomLink.setRoom2ID(15);  
             roomLink.setDoor( new Door( 528, 915, -halfPI, (byte)ImageLibRef.VERTICAL_TOP_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)0,ImageLibRef.VERTICAL_TOP_PIVOT) ) );

             rooms[15].addRoomLink( rooms[14].getRoomLinks()[0] );

           rooms[16].setFullName("West Gate - South Tower");
           rooms[16].setShortName("bsouth-corridor2");
           rooms[16].setInsertionPoint( new ScreenPoint(600,1080) );

             roomLink = rooms[16].addRoomLink( new ScreenRectangle( 520, 1060, 20, 40) );
             roomLink.setRoom1ID(17);
             roomLink.setRoom2ID(16);
             roomLink.setDoor( new Door( 528, 1065, -halfPI, (byte)ImageLibRef.VERTICAL_BOTTOM_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)0,ImageLibRef.VERTICAL_BOTTOM_PIVOT) ) );

             rooms[16].addRoomLink( rooms[11].getRoomLinks()[0] );
             rooms[16].addRoomLink( rooms[14].getRoomLinks()[1] );

           rooms[17].setFullName("West Gate - South Tower's Office");
           rooms[17].setShortName("bsouth-office");
           rooms[17].setInsertionPoint( new ScreenPoint(480,980) );

             rooms[17].addRoomLink( rooms[15].getRoomLinks()[0] );
             rooms[17].addRoomLink( rooms[16].getRoomLinks()[0] );


        // STEP 7 - Tar Valon NW Clearing InteriorMap
           maps = new InteriorMap[1];
           maps[0] = new InteriorMap();

           maps[0].setInteriorMapID(0);
           maps[0].setFullName("Tar Valon - Forest - North West Clearing");
           maps[0].setShortName("nw-clearing");
           maps[0].setInteriorMapImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                         ImageLibRef.UNIVERSE_SET ,
                                                         ImageLibRef.TARVALON_NW_CLEARING_ACTION ) );
           maps[0].setImageWidth(550);
           maps[0].setImageHeight(350);
           maps[0].setImageRegionWidth(550);
           maps[0].setImageRegionHeight(350);

           maps[0].setMusicName("tar-valon-01.mid");

           buildings[1].setInteriorMaps( maps );

        // STEP 8 - Rooms of Tar Valon NW Clearing InteriorMap
           rooms = new Room[1];
           maps[0].setRooms( rooms );
           rooms[0] = new Room();
           rooms[0].setRoomID(0);
           rooms[0].setMaxPlayers(30);

           rooms[0].setFullName("Tar Valon - Forest - North West Clearing");
           rooms[0].setShortName("nw-clearing");
           rooms[0].setInsertionPoint( new ScreenPoint(270,200) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(0,260,20,90) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.WEST );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(60,250) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(20,330,400,20) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.SOUTH );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(60,250) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(430,0,120,20) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.NORTH );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(65,225) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(530,20,20,240) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.EAST );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(65,225) );


        // STEP 9 - Tar Valon NW Gate InteriorMap
           maps = new InteriorMap[2];
           maps[0] = new InteriorMap();

           maps[0].setInteriorMapID(0);
           maps[0].setFullName("Tar Valon - North West Gate");
           maps[0].setShortName("nw-gate-lv0");
           maps[0].setInteriorMapImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                         ImageLibRef.UNIVERSE_SET ,
                                                         ImageLibRef.TARVALON_NW_GATE_LV0_MAP_ACTION ) );
           maps[0].setImageWidth(640);
           maps[0].setImageHeight(460);
           maps[0].setImageRegionWidth(640);
           maps[0].setImageRegionHeight(460);

           maps[0].setMusicName("tar-valon-01.mid");

           maps[1] = new InteriorMap();
           maps[1].setInteriorMapID(1);
           maps[1].setFullName("Tar Valon - North West Gate - Terrace");
           maps[1].setShortName("nw-gate-lv1");
           maps[1].setInteriorMapImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                         ImageLibRef.UNIVERSE_SET,
                                                         ImageLibRef.TARVALON_NW_GATE_LV1_MAP_ACTION ) );
           maps[1].setImageWidth(600);
           maps[1].setImageHeight(460);
           maps[1].setImageRegionWidth(600);
           maps[1].setImageRegionHeight(460);

           maps[1].setMusicName("tar-valon-01.mid");

           buildings[2].setInteriorMaps( maps );

        // STEP 10 - Rooms of Tar Valon NW Clearing InteriorMap Level 0
           rooms = new Room[1];
           maps[0].setRooms( rooms );
           rooms[0] = new Room();
           rooms[0].setRoomID(0);
           rooms[0].setMaxPlayers(30);

           rooms[0].setFullName("Tar Valon - North West Gate");
           rooms[0].setShortName("nw-gate-bridge");
           rooms[0].setInsertionPoint( new ScreenPoint(260,240) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(0,25,25,245) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.WEST );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(207,400) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(330,435,310,25) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.SOUTH );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(224,420) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(0,0,230,25) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.NORTH );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(207,400) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(615,335,25,100) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.EAST );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0) );
               mapExit.setTargetPosition( new ScreenPoint(224,420) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(485,200,40,25) );
               mapExit.setType( MapExit.INTERIOR_MAP_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0,2,1,0) );
               mapExit.setTargetPosition( new ScreenPoint(400,40) );

        // STEP 11 - Rooms of Tar Valon NW Terrace InteriorMap Level 1
           rooms = new Room[1];
           maps[1].setRooms( rooms );
           rooms[0] = new Room();
           rooms[0].setRoomID(0);
           rooms[0].setMaxPlayers(30);

           rooms[0].setFullName("Tar Valon - North West Gate - Terrace");
           rooms[0].setShortName("nw-gate-terrace");
           rooms[0].setInsertionPoint( new ScreenPoint(400,300) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(400,25,25,40) );
               mapExit.setType( MapExit.INTERIOR_MAP_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,0,2,0,0) );
               mapExit.setTargetPosition( new ScreenPoint(500,205) );

        // STEP 12 - Blight Refuge Building
           buildings = new Building[1];
           townMaps[1].setBuildings( buildings );

           buildings[0] = new Building(0,0,10,10);
           buildings[0].setBuildingID(0);
           buildings[0].setFullName("Blight Refuge");
           buildings[0].setShortName("blightrefuge");
           buildings[0].setServerID(0);
           buildings[0].setHasTownExits(true);
           buildings[0].setHasBuildingExits(true);
           buildings[0].setSmallBuildingImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                                ImageLibRef.BUILDING_SMALL_SET,
                                                                (short)-1 ) ); // no image
        // STEP 13 - Blight Refuge Exterior InteriorMap
           maps = new InteriorMap[3];
           buildings[0].setInteriorMaps( maps );

           map = new InteriorMap();
           maps[0] = map;

           map.setInteriorMapID(0);
           map.setFullName("Blight Refuge Outside");
           map.setShortName("blightrefugeouside");
           map.setInteriorMapImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                      ImageLibRef.UNIVERSE_SET ,
                                                      ImageLibRef.BLIGHT_REFUGE_EXT_MAP_ACTION ) );
           map.setImageWidth(720);
           map.setImageHeight(400);
           map.setImageRegionWidth(720);
           map.setImageRegionHeight(400);

           map.setMusicName("tar-valon-01.mid");


        // STEP 14 - Rooms of Blight Refuge Ext InteriorMap
           rooms = new Room[2];
           map.setRooms( rooms );

           rooms[0] = new Room();
           rooms[0].setRoomID(0);
           rooms[0].setMaxPlayers(30);

           rooms[0].setFullName("Blight Refuge - Outside");
           rooms[0].setShortName("refuge-ouside");
           rooms[0].setInsertionPoint( new ScreenPoint(610,200) );

             roomLink = rooms[0].addRoomLink( new ScreenRectangle(400,30,5,15) );
             roomLink.setRoom1ID(1);
             roomLink.setRoom2ID(0);

               mapExit = rooms[0].addMapExit( new ScreenRectangle(400,170,20,80) );
               mapExit.setType( MapExit.INTERIOR_MAP_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,1,0,1,0) );
               mapExit.setTargetPosition( new ScreenPoint(370,142) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(700,60,20,300) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.EAST );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0) );
               mapExit.setTargetPosition( new ScreenPoint(779,133) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(630,380,70,20) );
               mapExit.setType( MapExit.BUILDING_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0) );
               mapExit.setTargetPosition( new ScreenPoint(779,133) );

           rooms[1] = new Room();
           rooms[1].setRoomID(1);
           rooms[1].setMaxPlayers(30);

           rooms[1].setFullName("Blight Refuge - Terrace");
           rooms[1].setShortName("refuge-terrace");
           rooms[1].setInsertionPoint( new ScreenPoint(250,200) );

             rooms[1].addRoomLink( rooms[0].getRoomLinks()[0] );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(160,50,20,310) );
               mapExit.setType( MapExit.INTERIOR_MAP_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,1,0,1,2) );
               mapExit.setTargetPosition( new ScreenPoint(350,360) );

        // STEP 15 - Blight Refuge Int0 InteriorMap
           map = new InteriorMap();
           maps[1] = map;

           map.setInteriorMapID(1);
           map.setFullName("Blight Refuge Interior1");
           map.setShortName("blightrefugeInterior1");
           map.setInteriorMapImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                      ImageLibRef.UNIVERSE_SET ,
                                                      ImageLibRef.BLIGHT_REFUGE_INT0_MAP_ACTION ) );
           map.setImageWidth(400);
           map.setImageHeight(500);
           map.setImageRegionWidth(400);
           map.setImageRegionHeight(500);

           map.setMusicName("tar-valon-01.mid");

        // STEP 16 - Rooms of Blight Refuge Int0 InteriorMap
           rooms = new Room[4];
           map.setRooms( rooms );

           rooms[0] = new Room();
           rooms[0].setRoomID(0);
           rooms[0].setMaxPlayers(30);

           rooms[0].setFullName("Blight Refuge - Hall");
           rooms[0].setShortName("refuge-hall");
           rooms[0].setInsertionPoint( new ScreenPoint(246,147) );

             roomLink = rooms[0].addRoomLink( new ScreenRectangle(330,20,10,30) );
             roomLink.setRoom1ID(0);
             roomLink.setRoom2ID(1);
             roomLink.setDoor( new Door( 331, 20, halfPI, (byte)ImageLibRef.VERTICAL_TOP_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)1,ImageLibRef.VERTICAL_TOP_PIVOT) ) );

             roomLink = rooms[0].addRoomLink( new ScreenRectangle(90,290,30,10) );
             roomLink.setRoom1ID(0);
             roomLink.setRoom2ID(2);
             roomLink.setDoor( new Door( 90, 291, halfPI, (byte)ImageLibRef.HORIZONTAL_LEFT_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)1,ImageLibRef.HORIZONTAL_LEFT_PIVOT) ) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(375,130,30,50) );
               mapExit.setType( MapExit.INTERIOR_MAP_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,1,0,0,0) );
               mapExit.setTargetPosition( new ScreenPoint(410,200) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(0,70,20,160) );
               mapExit.setType( MapExit.INTERIOR_MAP_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,1,0,2,0) );
               mapExit.setTargetPosition( new ScreenPoint(500,260) );

           rooms[1] = new Room();
           rooms[1].setRoomID(1);
           rooms[1].setMaxPlayers(30);

           rooms[1].setFullName("Blight Refuge - Room");
           rooms[1].setShortName("refuge-room");
           rooms[1].setInsertionPoint( new ScreenPoint(354,24) );

             rooms[1].addRoomLink( rooms[0].getRoomLinks()[0] );

           rooms[2] = new Room();
           rooms[2].setRoomID(2);
           rooms[2].setMaxPlayers(30);

           rooms[2].setFullName("Blight Refuge - Meeting Room");
           rooms[2].setShortName("refuge-meeting");
           rooms[2].setInsertionPoint( new ScreenPoint(300,380) );

             roomLink = rooms[2].addRoomLink( new ScreenRectangle(350,290,30,10) );
             roomLink.setRoom1ID(3);
             roomLink.setRoom2ID(2);
             roomLink.setDoor( new Door( 350, 291, -halfPI, (byte)ImageLibRef.HORIZONTAL_RIGHT_PIVOT,
                               new ImageIdentifier((short)2,(short)0,(short)1,ImageLibRef.HORIZONTAL_RIGHT_PIVOT) ) );

             rooms[2].addRoomLink( rooms[0].getRoomLinks()[1] );

               mapExit = rooms[2].addMapExit( new ScreenRectangle(355,355,25,55) );
               mapExit.setType( MapExit.INTERIOR_MAP_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,1,0,0,0) );
               mapExit.setTargetPosition( new ScreenPoint(170,150) );

           rooms[3] = new Room();
           rooms[3].setRoomID(3);
           rooms[3].setMaxPlayers(30);

           rooms[3].setFullName("Blight Refuge - Store");
           rooms[3].setShortName("refuge-store");
           rooms[3].setInsertionPoint( new ScreenPoint(360,260) );

             rooms[3].addRoomLink( rooms[2].getRoomLinks()[0] );

        // STEP 17 - Blight Refuge Int1 InteriorMap
           map = new InteriorMap();
           maps[2] = map;

           map.setInteriorMapID(2);
           map.setFullName("Blight Refuge Interior2");
           map.setShortName("blightrefugeInterior2");
           map.setInteriorMapImage( new ImageIdentifier( ImageLibRef.MAPS_CATEGORY,
                                                      ImageLibRef.UNIVERSE_SET ,
                                                      ImageLibRef.BLIGHT_REFUGE_INT1_MAP_ACTION ) );
           map.setImageWidth(525);
           map.setImageHeight(530);
           map.setImageRegionWidth(525);
           map.setImageRegionHeight(530);

           map.setMusicName("tar-valon-01.mid");

        // STEP 18 - Rooms of Blight Refuge Int0 InteriorMap
           rooms = new Room[1];
           map.setRooms( rooms );

           rooms[0] = new Room();
           rooms[0].setRoomID(0);
           rooms[0].setMaxPlayers(30);

           rooms[0].setFullName("Blight Refuge - Main Hall");
           rooms[0].setShortName("refuge-mainhall");
           rooms[0].setInsertionPoint( new ScreenPoint(450,230) );

               mapExit = rooms[0].addMapExit( new ScreenRectangle(500,230,25,70) );
               mapExit.setType( MapExit.INTERIOR_MAP_EXIT );
               mapExit.setMapExitSide( MapExit.NONE );
               mapExit.setTargetWotlasLocation( new WotlasLocation(0,1,0,1,0) );
               mapExit.setTargetPosition( new ScreenPoint(10,140) );


        // STEP XX - We save this simple universe.
           persistenceManager = wotlas.server.PersistenceManager.createPersistenceManager( databasePath );
           Debug.signal( Debug.NOTICE, null, "Persistence Manager Created..." );

           if( persistenceManager.saveLocalUniverse( worldMaps, true ) )
               Debug.signal( Debug.NOTICE, null, "World Save Succeeded..." );
           else
               Debug.signal( Debug.NOTICE, null, "World Save Failed..." );
    }

 /*------------------------------------------------------------------------------------*/
}