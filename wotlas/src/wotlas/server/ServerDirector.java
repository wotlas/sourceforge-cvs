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
import wotlas.libs.net.*;
import wotlas.common.message.account.WarningMessage;

import java.util.Properties;
import java.util.Iterator;
import java.net.*;

/** The MAIN server class. It starts the PersistenceManager, the ServerManager
 *  and the DataManager. So got it ? yeah, it's the boss on the server side...
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 * @see wotlas.server.PersistenceManager
 */

public class ServerDirector implements Runnable, NetServerErrorListener
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
    public final static long PERSISTENCE_PERIOD = 1000*3600*6; // 6h

   /** Static Link to Remote Servers Config File.
    */
    public final static String REMOTE_SERVER_CONFIG = "../src/config/remote-servers.cfg";

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

   /** Remote server home URL : where the server list is stored on the internet.
    */
      private static String remoteServerConfigHomeURL;

   /** Show debug information ?
    */
      public static boolean SHOW_DEBUG = false;

   /** Period for changing the keys.
    */
      public static byte updateKeysPeriod =0;

   /** Shutdown Thread.
    */
      public static Thread shutdownThread;

   /** Immediate stop of persistence thread ?
    */
      public static boolean immediatePersistenceThreadStop = false;

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
     public static void main( String argv[] ) {
           boolean isDaemon = false;

           Debug.displayExceptionStack( false );

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
                 else if (arg.equals("-erroronly")) {
                     Debug.displayExceptionStack( false );
                     Debug.setLevel(Debug.ERROR);
                 }
                 else if (arg.equals("-daemon")) {
                     isDaemon = true;
                     Debug.displayExceptionStack( true );
                     Debug.setLevel(Debug.NOTICE);
                 }
           }

        // STEP 0 - Start a ServerLogStream to save our Debug messages           
           try{
               if(isDaemon)
                  Debug.setPrintStream( new DaemonLogStream( SERVER_LOG_PREFIX
                                +System.currentTimeMillis()+SERVER_LOG_SUFFIX ) );
               else
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
           Debug.signal( Debug.NOTICE, null, "|            Server v1.2            |" );
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


        // STEP 1.1 - We load remote props (server table base URL)
           Properties remoteProps = FileTools.loadPropertiesFile( REMOTE_SERVER_CONFIG );

           if( remoteProps==null ) {
               Debug.signal( Debug.FAILURE, null, "No valid remote-servers.cfg file found !" );
               Debug.exit();
           }
           else {
               remoteServerConfigHomeURL = remoteProps.getProperty( "REMOTE_SERVER_CONFIG_HOME_URL","" );

               if( remoteServerConfigHomeURL.length()==0 ) {
                   Debug.signal( Debug.FAILURE, null, "No URL for remote server config home !" );
                   Debug.exit();
               }
        
               if( !remoteServerConfigHomeURL.endsWith("/") )
                   remoteServerConfigHomeURL += "/";
           }


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

        // STEP 6 - We generate new keys for special characters
           updateKeys();

        // STEP 7 - Adding Shutdown Hook           
           shutdownThread = new Thread() {
           	public void run() {
           	   immediatePersistenceThreadStop = true;
                   Debug.signal(Debug.CRITICAL,null,"Received VM Shutdown Signal.");
                   ServerDirector.immediatePersistenceAction();
                   Debug.signal(Debug.CRITICAL,null,"Data Saved.");
                   Debug.flushPrintStream();
           	}
           };

           Runtime.getRuntime().addShutdownHook(shutdownThread);

        // Everything is ok ! we enter the persistence loop
           Debug.signal( Debug.NOTICE, null, "Everything is Ok. Creating persistence thread..." );

           serverDirector = new ServerDirector();
           serverManager.getGameServer().setErrorListener( serverDirector );
           
           Thread persistenceThread = new Thread( serverDirector );
           persistenceThread.start();

           if(!isDaemon) {
              Tools.waitTime( 2000 ); // 2s
              Debug.signal( Debug.NOTICE, null, "Press <ENTER> if you want to shutdown this server." );

              try{
                 System.in.read();
              }catch( Exception e ) {
              }

              Debug.signal( Debug.NOTICE, null, "Leaving in 30s..." );
              Runtime.getRuntime().removeShutdownHook(shutdownThread);
              serverDirector.stopThread();
           }
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
                if(immediatePersistenceThreadStop) return;

             // 1 - we wait the persistence period minus 2 minutes
                synchronized( this ) {
                  try{
                     wait( PERSISTENCE_PERIOD-1000*120 );
                  }catch(Exception e) {}
                }

                if(immediatePersistenceThreadStop) return;
                if(mustStopThread()) break;

             // 2 - Start persistence action
                persistenceAction(true);
           }
           while( !mustStopThread() );

        // We save data before closing
           persistenceAction(false);

        // We close the different servers
           if(immediatePersistenceThreadStop) return;
           Debug.signal( Debug.NOTICE, null, "Shuting down servers..." );
           serverManager.getGameServer().stopServer();
           serverManager.getAccountServer().stopServer();
           serverManager.getGatewayServer().stopServer();

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
     private static void persistenceAction( boolean maintenance ) {

              if(immediatePersistenceThreadStop) return;

           // We warn all the clients that the server is going to enter
           // maintenance mode for 2-3 minutes, in 2 minutes.
           // If we are not in maintenance mode ( maintenance=false )
           // the message is a "server shuting down" message.
              serverManager.getGameServer().setServerLock( true );
              serverManager.getAccountServer().setServerLock( true );
              serverManager.getGatewayServer().setServerLock( true );

              if( maintenance )
                  Debug.signal( Debug.NOTICE, null, "Server will enter maintenance mode in 2 minutes...");
              else
                  Debug.signal( Debug.NOTICE, null, "Sending warning messages to connected clients...");

              synchronized( dataManager.getAccountManager() ) {
                 Iterator it = dataManager.getAccountManager().getIterator();

                 WarningMessage msg = null;
              
                 if( maintenance )
                    msg =new WarningMessage( "Your server will enter maintenance mode in 2 minutes.\n"
                                           +"Please disconnect and reconnect in 5 minutes");
                 else
                    msg =new WarningMessage( "Your server is about to shutdown in 30s.\n"
                                           +"Please disconnect and reconnect later.");

                 while( it.hasNext() )
                    ( (GameAccount) it.next() ).getPlayer().sendMessage( msg );
              }
 
           // 2 - We wait two more minutes ( or 30s if maintenance=false )
              if( maintenance ) {
                  Tools.waitTime( 1000*120 ); // 2mn
                  Debug.signal( Debug.WARNING, null, "Server enters maintenance mode now... ("+Tools.getLexicalTime()+")");
              }
              else {
                  Tools.waitTime( 1000*30 );  // 30s
                  Debug.signal( Debug.NOTICE, null, "Saving world & player data... ("+Tools.getLexicalTime()+")");
              }

           // 3 - We close all remaining connections on the GameServer
           //     and enter maintenance mode

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
                  serverManager.getGameServer().setServerLock( false );
                  serverManager.getAccountServer().setServerLock( false );
                  serverManager.getGatewayServer().setServerLock( false );

                  Debug.signal( Debug.NOTICE, null, "Leaving maintenance mode... ("+Tools.getLexicalTime()+")" );

                  updateKeys(); // we update the keys...
              }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Saves the world & players immediately, closes all connection and stops server.
    */
     private static void immediatePersistenceAction() {
     	  // 1 - Lock servers...
              serverManager.getGameServer().setServerLock( true );
              serverManager.getAccountServer().setServerLock( true );
              serverManager.getGatewayServer().setServerLock( true );

          // 2 - We warn connected clients
              synchronized( dataManager.getAccountManager() ) {
                 Iterator it = dataManager.getAccountManager().getIterator();

                 WarningMessage msg = new WarningMessage( "Your server has been stopped.\n"
                                           +"Try to reconnect in a few minutes.");

                 while( it.hasNext() )
                    ( (GameAccount) it.next() ).getPlayer().sendMessage( msg );
              }

           // 3 - We close all remaining connections on the GameServer
              synchronized( dataManager.getAccountManager() ) {
                 Iterator it = dataManager.getAccountManager().getIterator();

                 while( it.hasNext() )
                     ( (GameAccount) it.next() ).getPlayer().closeConnection();
              }

              serverManager.getGameServer().stopServer();
              serverManager.getAccountServer().stopServer();
              serverManager.getGatewayServer().stopServer();

           // 4 - Saving Accounts
              synchronized( dataManager.getAccountManager() ) {
                 Iterator it = dataManager.getAccountManager().getIterator();

                 while( it.hasNext() )
                     dataManager.getAccountManager().saveAccount( (GameAccount) it.next() );
              }

           // 5 - We save the world data
              if( !dataManager.getWorldManager().saveLocalUniverse() )
                  Debug.signal( Debug.WARNING, null, "Failed to save world data..." );
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

   /** To awake the persistence thread and perform a save of the data (world, player...).
    */
      private synchronized void awakePersistenceThread() {
      	 notify();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when an error occurs in one of the NetServer.
   *
   * @param e the exception that occured.
   */
     public void errorOccured( Exception e ) {
     	if(e instanceof BindException) {
     	   Debug.signal( Debug.NOTICE, this, "Trying to awake Persistence Thread..." );
     	   awakePersistenceThread(); // returns immediately
     	}
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the URL where are stored the remote server configs. This URL can also contain
   *  a news.html file to display some news.
   *
   * @return remoteServerConfigHomeURL
   */
   public static String getRemoteServerConfigHomeURL() {
      return remoteServerConfigHomeURL;
   }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get server properties.
   * @return server properties
   */
   public static Properties getServerProperties() {
      return properties;
   }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To change the keys used for special players.
   */
   public static void updateKeys() {

        if(updateKeysPeriod!=0) {
           updateKeysPeriod++;
           if(updateKeysPeriod==4) updateKeysPeriod=0;
           return;
        }
        else
           updateKeysPeriod++;

        String oldConfig = FileTools.loadTextFromFile( SERVER_CONFIG );

        if( oldConfig!=null ) {

            properties.setProperty( "key.shaitan", Tools.keyGenerator(23, serverID+1) );
            properties.setProperty( "key.amyrlin", Tools.keyGenerator(23, serverID+2) );
            properties.setProperty( "key.chronicles", Tools.keyGenerator(23, serverID+3) );
            properties.setProperty( "key.mhael", Tools.keyGenerator(23, serverID+4) );

            oldConfig = FileTools.updateProperty( "key.shaitan", properties.getProperty( "key.shaitan"), oldConfig);
            oldConfig = FileTools.updateProperty( "key.amyrlin", properties.getProperty( "key.amyrlin"), oldConfig);
            oldConfig = FileTools.updateProperty( "key.chronicles", properties.getProperty( "key.chronicles"), oldConfig);
            oldConfig = FileTools.updateProperty( "key.mhael", properties.getProperty( "key.mhael"), oldConfig);

            if( !FileTools.saveTextToFile( SERVER_CONFIG, oldConfig ) )
                Debug.signal(Debug.ERROR,null,"Failed to save characters keys in "+SERVER_CONFIG);
            else
                Debug.signal( Debug.NOTICE, null, "Generated new keys for special characters..." );
        }
        else
            Debug.signal(Debug.ERROR,null,"Failed to open "+SERVER_CONFIG);
   }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

