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


import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.server.*;

import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.utils.Tools;

import java.util.Properties;
import java.awt.Point;


/** A small utility to generate a simple default world.
 *
 * @author Aldiss
 */

public class WorldGenerator {

 /*------------------------------------------------------------------------------------*/

   /** Static Link to Server Config File.
    */
    public final static String SERVER_CONFIG = "config/server.cfg";

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

        // STEP 2 - WORLD CREATION
           WorldMap worldMaps[] = new WorldMap[1];

           WorldMap worldMap = new WorldMap();
           worldMaps[0] = worldMap;

           worldMap.setWorldMapID(0);     
           worldMap.setFullName("RandLand Main Lands");
           worldMap.setShortName("randland");

           TownMap townMaps[] = new TownMap[1];
           TownMap townMap = new TownMap();
           townMaps[0] = townMap;

           worldMap.setTownMaps( townMaps );

           townMap.setTownMapID(0);
           townMap.setFullName("Tar Valon");
           townMap.setShortName("tarvalon");

           townMap.setFromWorldMapID(0);
           townMap.setWorldMapEnterX(200);
           townMap.setWorldMapEnterY(100);
   
           Building buildings[] = new Building[1];
           Building building = new Building();
           buildings[0] = building;

           townMap.setBuildings( buildings );

           building.setBuildingID(0);

           building.setFullName("Tar Valon South Bridge");
           building.setShortName("southbridge");

           building.setServerID(0);

           InteriorMap maps[] = new InteriorMap[1];
           InteriorMap map = new InteriorMap();
           maps[0] = map;

           building.setInteriorMaps( maps );

           map.setInteriorMapID(0);
           map.setFullName("Main Gate Entrance Level");
           map.setShortName("maingate");

           Room rooms[] = new Room[2];
           Room room = new Room();
           rooms[0] = room;

           room.setRoomID(0);
           room.setFullName("Front of the Gate");
           room.setShortName("frontGate");
           room.setMaxPlayers(50);
           room.setInsertionPoint(new Point(100,400));

           Room room2 = new Room();
           rooms[1] = room2;

           room2.setRoomID(1);
           room2.setFullName("Soldier Tower Entrance");
           room2.setShortName("soldiertower");
           room2.setMaxPlayers(30);
           room2.setInsertionPoint(new Point(300,200));

           map.setRooms( rooms );

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