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
import wotlas.utils.Tools;
import wotlas.utils.FileTools;

import wotlas.libs.log.*;
import wotlas.common.message.account.WarningMessage;

import java.util.Properties;
import java.util.Iterator;

/** The MAIN server class. It starts the PersistenceManager, the ServerManager
 *  and the DataManager. So got it ? yeah, it's the boss on the server side...
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 * @see wotlas.server.PersistenceManager
 */

class ServerDirector implements Runnable
{
 /*------------------------------------------------------------------------------------*/

   /** Static Link to Server Config File.
    */
    public final static String SERVER_CONFIG = "../src/config/server.cfg";

   /** Static Link to Server Log File.
    */
    public final static String SERVER_LOG_PREFIX = "../log/wot-server-";
    public final static String SERVER_LOG_SUFFIX = ".log";

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

   /** Our default ServerDirector.
    */
      private static ServerDirector serverDirector;

   /** Show debug information ?
    */
      public static boolean SHOW_DEBUG = false;

 /*------------------------------------------------------------------------------------*/

   /** To stop the persistence thread.
    */
      private boolean mustStop = false;

 /*------------------------------------------------------------------------------------*/

  /** Main Class. Starts the WHOLE Server from the latest database version.
   *  Yeah, some kind of magic is in work there.
   *
   * @param argv useless... sorry but we don't like command line options... if you
   *             want to set some options take a look at config/server.cfg & database.cfg
   */
     public static void main( String argv[] )
     {
        // Parse command line arguments
           int i=0;
           String arg;
           while (i<argv.length && argv[i].startsWith("-")) {
              arg = argv[i];
              i++;
                 if (arg.equals("-debug")) {
                     System.out.println("mode DEBUG on");
                     SHOW_DEBUG = true;
                     Debug.displayExceptionStack( true );
                 }
           }

        // STEP 0 - Start a ServerLogStream to save our Debug messages           
           try{
               Debug.setPrintStream( new ServerLogStream( SERVER_LOG_PREFIX
                                +System.currentTimeMillis()+SERVER_LOG_SUFFIX ) );
           }catch( java.io.FileNotFoundException e ) {
               e.printStackTrace();
               return;
           }

           if( !Tools.javaVersionHigherThan( "1.3.0" ) )
               Debug.exit();

           Debug.signal( Debug.NOTICE, null, "*-----------------------------------*" );
           Debug.signal( Debug.NOTICE, null, "|   Wheel Of Time - Light & Shadow  |" );
           Debug.signal( Debug.NOTICE, null, "|  Copyright (C) 2001 - WOTLAS Team |" );
           Debug.signal( Debug.NOTICE, null, "*-----------------------------------*\n");

        // STEP 1 - We load the database path. Where is the data ?
           properties = FileTools.loadPropertiesFile( SERVER_CONFIG );

             if( properties==null ) {
                Debug.signal( Debug.FAILURE, null, "No valid server.cfg file found !" );
                Debug.exit();
             }

           databasePath = properties.getProperty( "DATABASE_PATH" );

             if( databasePath==null ) {
                Debug.signal( Debug.FAILURE, null, "No Database Path specified in config file !" );
                Debug.exit();
             }

           Debug.signal( Debug.NOTICE, null, "DataBase Path Found : "+databasePath );

           String s_serverID = properties.getProperty( "SERVER_ID" );

           if( s_serverID==null ) {
               Debug.signal( Debug.FAILURE, null, "No ServerID specified in config file !" );
               Debug.exit();
           }

           try{
              serverID = Integer.parseInt( s_serverID );
           }catch( Exception e ) {
                Debug.signal( Debug.FAILURE, null, "Bad ServerID specified in config file !" );
                Debug.exit();
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
           Debug.signal( Debug.NOTICE, null, "Everything is Ok. Creating persistence thread..." );

           serverDirector = new ServerDirector();
           Thread persistenceThread = new Thread( serverDirector );
           persistenceThread.start();

           Tools.waitTime( 2000 ); // 2s
           Debug.signal( Debug.NOTICE, null, "Press <ENTER> if you want to shutdown this server." );

           try{
              System.in.read();
           }catch( Exception e ) {
           }

           Debug.signal( Debug.NOTICE, null, "Leaving in 30s..." );
           serverDirector.stopThread();
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

   /** Run method for Thread. 
    */
      public void run(){
           do{
             // 1 - we wait the persistence period minus 5 minutes
                synchronized( this ) {
                  try{
                     wait( PERSISTENCE_PERIOD-1000*300 );
                  }catch(Exception e) {}
                }

                if(mustStopThread())
                   break;

             // 2 - Start persistence action
                persistenceAction(true);
           }
           while( mustStopThread() );

        // We save data before closing
           persistenceAction(false);

        // We close the different servers
           Debug.signal( Debug.NOTICE, null, "Shuting down servers..." );
           serverManager.getGameServer().stopServer();
           serverManager.getAccountServer().stopServer();
           /* serverManager.getGatewayServer().stopServer(); */

           Tools.waitTime( 1000*10 ); // 10s
           Debug.signal( Debug.NOTICE, null, "Leaving Persistence Thread..." );
           Debug.exit();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Saves the world & players.
    *
    * @param maintenance if true we advertise clients with a "entering maintenance mode"
    *        message. If false we send a "server is shuting down" message.
    */
     private static void persistenceAction( boolean maintenance )
     {
           // We warn all the clients that the server is going to enter
           // maintenance mode for 5 minutes, in 5 minutes.
           // If we are not in maintenance mode ( maintenance=false )
           // the message is a "server shuting down" message.

              if( maintenance )
                  Debug.signal( Debug.NOTICE, null, "Server will enter maintenance mode in 5 minutes...");
              else
                  Debug.signal( Debug.NOTICE, null, "Sending warning messages to connected clients...");

              synchronized( dataManager.getAccountManager() )
              {
                 Iterator it = dataManager.getAccountManager().getIterator();

                 WarningMessage msg = null;
              
                 if( maintenance )
                    msg =new WarningMessage( "Your server will enter maintenance mode in 5 minutes.\n"
                                           +"Please disconnect and reconnect in 10 minutes");
                 else
                    msg =new WarningMessage( "Your server is about to shutdown in 30s.\n"
                                           +"Please disconnect and reconnect later.");

                 while( it.hasNext() )
                    ( (GameAccount) it.next() ).getPlayer().sendMessage( msg );
              }
 
           // 2 - We wait five more minutes ( or 30s if maintenance=false )
              if( maintenance ) {
                  Tools.waitTime( 1000*300 ); // 5mn
                  Debug.signal( Debug.WARNING, null, "Server enters maintenance mode now...");
              }
              else {
                  Tools.waitTime( 1000*30 );  // 30s
                  Debug.signal( Debug.NOTICE, null, "Saving world & player data...");
              }


           // 3 - We close all remaining connections on the GameServer
           //     and enter maintenance mode
              serverManager.getGameServer().setServerLock( true );
              serverManager.getAccountServer().setServerLock( true );
              /* serverManager.getGatewayServer().setServerLock( true ); */

              synchronized( dataManager.getAccountManager() ) {
                 Iterator it = dataManager.getAccountManager().getIterator();

                 while( it.hasNext() )
                     ( (GameAccount) it.next() ).getPlayer().closeConnection();
              }

           // Saving Accounts
              synchronized( dataManager.getAccountManager() ) {
                 Iterator it = dataManager.getAccountManager().getIterator();

                 while( it.hasNext() )
                     dataManager.getAccountManager().saveAccount( (GameAccount) it.next() );
              }

              Debug.signal( Debug.NOTICE, null, "Saved player data..." );


           // 4 - We save the world data
              if( !dataManager.getWorldManager().saveLocalUniverse() )
                  Debug.signal( Debug.WARNING, null, "Failed to save world data..." );
              else
                  Debug.signal( Debug.NOTICE, null, "Saved world data..." );


           // 5 - Leaving Maintenance Mode
              if( maintenance ) {
                  serverManager.getGameServer().setServerLock( true );
                  serverManager.getAccountServer().setServerLock( true );
               /* serverManager.getGatewayServer().setServerLock( true ); */

                  Debug.signal( Debug.NOTICE, null, "Leaving maintenance mode..." );
              }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Must stop persistence thread ?
    */
      private synchronized boolean mustStopThread() {
      	 return mustStop;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To stop the persistence thread.
    */
      private synchronized void stopThread() {
      	 mustStop = true;
      	 notify();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

