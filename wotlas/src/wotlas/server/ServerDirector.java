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

import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.common.message.account.WarningMessage;
import wotlas.utils.Tools;

import java.util.Properties;
import java.util.Iterator;

/** The MAIN server class. It starts the PersistenceManager, the ServerManager
 *  and the DataManager. So got it ? yeah, it's the boss on the server side...
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 * @see wotlas.server.PersistenceManager
 */

class ServerDirector
{
 /*------------------------------------------------------------------------------------*/

   /** Static Link to Database Config File.
    */
    public final static String DATABASE_CONFIG = "../src/config/server.cfg";

   /** Persistence period in ms.
    */
    public final static int PERSISTENCE_PERIOD = 1000*3600*12; // 12h

 /*------------------------------------------------------------------------------------*/

   /** Complete Path to the database where are stored the universe and the client
    *  accounts.
    */
      private static String databasePath;

   /** Our Server ID
    */
      private static int serverID;

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
           Debug.displayExceptionStack( false );

        // STEP 0 - Print some info...
           Debug.signal( Debug.NOTICE, null, "*-----------------------------------*" );
           Debug.signal( Debug.NOTICE, null, "|   Wheel Of Time - Light & Shadow  |" );
           Debug.signal( Debug.NOTICE, null, "|  Copyright (C) 2001 - WOTLAS Team |" );
           Debug.signal( Debug.NOTICE, null, "*-----------------------------------*\n");

        // STEP 1 - We load the database path. Where is the data ?
           properties = FileTools.loadPropertiesFile( DATABASE_CONFIG );

             if( properties==null ) {
                Debug.signal( Debug.FAILURE, null, "No valid server-database.cfg file found !" );
                System.exit(1);
             }

           databasePath = properties.getProperty( "DATABASE_PATH" );

             if( databasePath==null ) {
                Debug.signal( Debug.FAILURE, null, "No Database Path specified in config file !" );
                System.exit(1);
             }

           Debug.signal( Debug.NOTICE, null, "DataBase Path Found : "+databasePath );

           String s_serverID = properties.getProperty( "SERVER_ID" );

           if( s_serverID==null ) {
               Debug.signal( Debug.FAILURE, null, "No ServerID specified in config file !" );
               System.exit(1);
           }

           try{
              serverID = Integer.parseInt( s_serverID );
           }catch( Exception e ) {
                Debug.signal( Debug.FAILURE, null, "Bad ServerID specified in config file !" );
                System.exit(1);
           }

           Debug.signal( Debug.NOTICE, null, "Server ID set to : "+serverID );

        // STEP 2 - Creation of the PersistenceManager
           persistenceManager = PersistenceManager.createPersistenceManager( databasePath );
           Debug.signal( Debug.NOTICE, null, "Persistence Manager Created..." );


        // STEP 3 - We ask the ServerManager to get ready
           serverManager = ServerManager.createServerManager();
           Debug.signal( Debug.NOTICE, null, "Servers Created (but not started)..." );


        // STEP 4 - We ask the DataManager to load the worlds & client accounts
           dataManager = DataManager.createDataManager();
           Debug.signal( Debug.NOTICE, null, "World Data Loaded..." );
        
        // STEP 5 - Start of the GameServer, AccountServer & GatewayServer !
           serverManager.start();
           Debug.signal( Debug.NOTICE, null, "WOTLAS Servers started with success..." );

        // Everything is ok ! we enter the persistence loop
           Debug.signal( Debug.NOTICE, null, "Everything is Ok. Entering persistence loop..." );
           persistenceLoop();
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

   /** To get the server ID of this server.
    *
    * @return serverID
    */
      public static int getServerID() {
         return serverID;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Infinite loop saving the world & accounts periodically.
    */

     private static void persistenceLoop()
     {
     	while( true )
     	{
           // 1 - we wait the persistence period minus 5 minutes
              Tools.waitTime( PERSISTENCE_PERIOD-1000*300 );

           // We warn all the clients that the server is going to enter
           // maintenance mode for 5 minutes, in 5 minutes.
              Debug.signal( Debug.NOTICE, null, "Server will enter maintenance mode in 5 minutes...");
              Iterator it = dataManager.getAccountManager().getIterator();

              WarningMessage msg = new WarningMessage(
                       "Your server will enter maintenance mode in 5 minutes.\n"
                       +"Please disconnect and reconnect in 10 minutes");

              while( it.hasNext() )
                  ( (GameAccount) it.next() ).getPlayer().sendMessage( msg );
 
           // 2 - We wait five more minutes
              Tools.waitTime( PERSISTENCE_PERIOD-1000*300 );
              Debug.signal( Debug.NOTICE, null, "Server enters maintenance mode now...");

           // 3 - We close all remaining connections on the GameServer
           //     and enter maintenance mode
              serverManager.getGameServer().setServerLock( true );
              serverManager.getAccountServer().setServerLock( true );
              /* serverManager.getGatewayServer().setServerLock( true ); */

              it = dataManager.getAccountManager().getIterator();

              while( it.hasNext() )
                  ( (GameAccount) it.next() ).getPlayer().closeConnection();


           // Saving Accounts
              it = dataManager.getAccountManager().getIterator();

              while( it.hasNext() )
                  dataManager.getAccountManager().saveAccount( (GameAccount) it.next() );

              Debug.signal( Debug.NOTICE, null, "Saved player data..." );

           // 4 - We save the world data
              if( !dataManager.getWorldManager().saveLocalUniverse() )
                  Debug.signal( Debug.WARNING, null, "Failed to save world data..." );
              else
                  Debug.signal( Debug.NOTICE, null, "Saved world data..." );

           // 5 - Leaving Maintenance Mode
              serverManager.getGameServer().setServerLock( true );
              serverManager.getAccountServer().setServerLock( true );
              /* serverManager.getGatewayServer().setServerLock( true ); */
              Debug.signal( Debug.NOTICE, null, "Leaving maintenance mode..." );     
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

