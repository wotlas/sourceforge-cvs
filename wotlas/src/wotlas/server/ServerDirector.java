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

package wotlas.server;
<COMPLETE>
<REPLACE FILETOOLS CALL WITH A PROPERTIES FILE>

import wotlas.utils.FileTools;

/** The MAIN server class. It starts the PersistenceManager, the ServerManager
 *  and the DataManager. So got it ? yeah, it's the boss...
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 */

class ServerDirector
{
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
      private static PersistenceManager persistenceManager;

   /** Our Server Manager.
    */
      private static ServerManager serverManager;

   /** Our Data Manager.
    */
      private static DataManager dataManager;


 /*------------------------------------------------------------------------------------*/

  /** Main Class. Starts the WHOLE Server from the latest database version.
   *  Yeah, some kind of magic is in work there.
   *
   * @param argv useless... sorry but we don't like command line options... if you
   *             want to set some options take a look at config/server.cfg & database.cfg
   */
     public static void main( String argv[] )
     {
        // STEP 1 - We load the database path. Where is the data ?
           databasePath = FileTools.readConfigFile( "config/database.cfg", "DATABASE_PATH" );

             if( databasePath==null ) {
                Debug.signal( Debug.FAILURE, null, "No Database Path available !" );
                System.exit(1);
             }

           Debug.signal( Debug.NOTICE, null, "DataBase Path Found..." );


        // STEP 2 - Creation of the PersistenceManager
           persistenceManager = createPersistenceManager( databasePath );

             if( persistenceManager==null ) {
                Debug.signal( Debug.FAILURE, null, "Failed to create PersistenceManager !" );
                System.exit(1);
             }

           Debug.signal( Debug.NOTICE, null, "Persistence Manager Created..." );


        // STEP 3 - We ask the ServerManager to get ready
           serverManager = new ServerManager();

           Debug.signal( Debug.NOTICE, null, "Servers Created..." );


        // STEP 4 - We ask the DataManager to load the worlds & client accounts
           dataManager = new DataManager();

           Debug.signal( Debug.NOTICE, null, "World Data Loaded..." );
        
        // STEP 5 - Start of the GameServer, AccountServer & GatewayServer !
           serverManager.start();

           Debug.signal( Debug.NOTICE, null, "WOTLAS Servers started with success..." );

        // Everything is ok !
           Debug.signal( Debug.NOTICE, null, "Awaiting clients..." );

     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the complete path to the database where are stored the universe and the client
    *  accounts.
    *
    * @return databasePath
    */
      public static String getDatabasePath() {
         return databasePath;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

