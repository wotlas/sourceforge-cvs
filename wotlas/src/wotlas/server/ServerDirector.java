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

package wotlas.server;

import wotlas.utils.Debug;
import wotlas.utils.Tools;
import wotlas.utils.FileTools;

import wotlas.libs.log.*;
import wotlas.libs.net.*;

import wotlas.common.*;
import wotlas.common.RemoteServersPropertiesFile;

import java.io.File;
import java.util.Properties;
import java.net.*;

/** The MAIN server class. It starts the PersistenceManager, the ServerManager
 *  and the DataManager. So got it ? yeah, it's the boss on the server side...
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 * @see wotlas.server.PersistenceManager
 */

public class ServerDirector implements Runnable, NetServerErrorListener {

 /*------------------------------------------------------------------------------------*/

   /** Default location where are stored base data.
    */
      public final static String DEFAULT_BASE_PATH = "../base";

   /** Server Command Line Help
    */
      public final static String SERVER_COMMAND_LINE_HELP =
            "Usage: ServerDirector -[debug|erroronly|daemon|help] -[base <path>]\n\n"
           +"Examples : \n"
           +"  ServerDirector -daemon       : the server will display nothing.\n"
           +"  ServerDirector -erroronly    : the server will only print errors.\n"
           +"  ServerDirector -base ../base : sets the data location.\n\n"
           +"If the -base option is not set we search for data in "+DEFAULT_BASE_PATH
           +"\n\n";

   /** Format of the server log name.
    */
      public final static String SERVER_LOGS = "logs";
      public final static String SERVER_LOG_PREFIX = "wot-server-";
      public final static String SERVER_LOG_SUFFIX = ".log";

   /** Format of the configs path name
    */
      public final static String SERVER_CONFIGS = "configs";

 /*------------------------------------------------------------------------------------*/

   /** Our server properties.
    */
      private static ServerPropertiesFile serverProperties;

   /** Our remote server properties.
    */
      private static RemoteServersPropertiesFile remoteServersProperties;

   /** Our resource manager
    */
      private static ResourceManager resourceManager;

 /*------------------------------------------------------------------------------------*/

   /** Our Server Manager.
    */
      private static ServerManager serverManager;

   /** Our Data Manager.
    */
      private static DataManager dataManager;

   /** Our default ServerDirector (Peristence Thread).
    */
      private static ServerDirector serverDirector;

 /*------------------------------------------------------------------------------------*/

   /** Shutdown Thread.
    */
      public static Thread shutdownThread;

   /** Period for changing the keys.
    */
      public static byte updateKeysPeriod =0;

   /** Immediate stop of persistence thread ?
    */
      public static boolean immediatePersistenceThreadStop = false;

   /** To stop the persistence thread.
    */
      private boolean mustStop = false;

   /** Show debug information ?
    */
      public static boolean SHOW_DEBUG = false;

 /*------------------------------------------------------------------------------------*/

  /** Main Class. Starts the Wotlas Server.
   * @param argv enter -help to get some help info.
   */
     public static void main( String argv[] ) {

        // STEP 0 - We parse the command line options
           boolean isDaemon = false;
           String basePath = DEFAULT_BASE_PATH;
           Debug.displayExceptionStack( false );

           for( int i=0; i<argv.length; i++ ) {

              if( !argv[i].startsWith("-") )
                  continue;

              if (argv[i].equals("-debug")) {    // -- TO SET THE DEBUG MODE --
                  if(isDaemon) {
                      System.out.println("Incompatible options.");
                      System.out.println(SERVER_COMMAND_LINE_HELP);
                      return;
                  }

                  System.out.println("mode DEBUG on");
                  SHOW_DEBUG = true;
                  Debug.displayExceptionStack( true );
              }
              else if (argv[i].equals("-erroronly")) {  // -- TO ONLY DISPLAY ERRORS --
                  if(SHOW_DEBUG) {
                     System.out.println("Incompatible options.");
                     System.out.println(SERVER_COMMAND_LINE_HELP);
                     return;
                   }

                   Debug.displayExceptionStack( false );
                   Debug.setLevel(Debug.ERROR);
              }
              else if (argv[i].equals("-daemon")) {   // -- DAEMON MODE --
                   if(SHOW_DEBUG) {
                      System.out.println("Incompatible options.");
                      System.out.println(SERVER_COMMAND_LINE_HELP);
                      return;
                   }

                   isDaemon = true;
                   Debug.displayExceptionStack( true );
                   Debug.setLevel(Debug.NOTICE);
              }
              else if(argv[i].equals("-base")) {   // -- TO SET THE CONFIG FILES LOCATION --

                   if(i==argv.length-1) {
                      System.out.println("Location missing.");
                      System.out.println(SERVER_COMMAND_LINE_HELP);
                      return;
                   }

                   basePath = argv[i+1];
              }
              else if(argv[i].equals("-help")) {   // -- TO DISPLAY THE HELP --

                   System.out.println(SERVER_COMMAND_LINE_HELP);
                   return;
              }
           }

        // STEP 1 - We create a LogStream to save our Debug messages to disk.
           try{
               if(isDaemon) {
               	// We don't print the Debug messages on System.err
                  Debug.setPrintStream( new DaemonLogStream( basePath+File.separator+SERVER_LOGS
                           +File.separator+SERVER_LOG_PREFIX+System.currentTimeMillis()+SERVER_LOG_SUFFIX ) );
               }
               else {
               	// We also print the Debug messages on System.err
                  Debug.setPrintStream( new ServerLogStream( basePath+File.separator+SERVER_LOGS
                           +File.separator+SERVER_LOG_PREFIX+System.currentTimeMillis()+SERVER_LOG_SUFFIX ) );
               }
           }
           catch( java.io.FileNotFoundException e ) {
               e.printStackTrace();
               return;
           }


        // STEP 2 - We control the VM version and load our vital config files.

           if( !Tools.javaVersionHigherThan( "1.3.0" ) )
               Debug.exit();

           Debug.signal( Debug.NOTICE, null, "*-----------------------------------*" );
           Debug.signal( Debug.NOTICE, null, "|   Wheel Of Time - Light & Shadow  |" );
           Debug.signal( Debug.NOTICE, null, "|  Copyright (C) 2001 - WOTLAS Team |" );
           Debug.signal( Debug.NOTICE, null, "|           Server v1.2.3           |" );
           Debug.signal( Debug.NOTICE, null, "*-----------------------------------*\n");


           serverProperties = new ServerPropertiesFile(basePath+File.separator+SERVER_CONFIGS);
           Debug.signal( Debug.NOTICE, null, "Data directory     : "+basePath );

           remoteServersProperties = new RemoteServersPropertiesFile(basePath+File.separator+SERVER_CONFIGS);


        // STEP 3 - Creation of the ResourceManager
           resourceManager = new ResourceManager( basePath,
                                             basePath+File.separator+SERVER_CONFIGS,
                                             serverProperties.getProperty("init.helpPath"),
                                             basePath+File.separator+SERVER_LOGS
                                         );

        // STEP 4 - We ask the ServerManager to get ready
           serverManager = new ServerManager(resourceManager);
           Debug.signal( Debug.NOTICE, null, "Server Manager created..." );

        // STEP 5 - We ask the DataManager to load the worlds & client accounts
           dataManager = new DataManager(resourceManager);
           dataManager.init( serverProperties );

        // STEP 6 - Start of the GameServer, AccountServer & GatewayServer !
           Debug.signal( Debug.NOTICE, null, "Starting Game server, Account server & Gateway server..." );
           serverManager.start();

        // STEP 7 - We generate new keys for special characters
           updateKeys();

        // STEP 8 - Adding Shutdown Hook
           shutdownThread = new Thread() {
           	public void run() {
           	   immediatePersistenceThreadStop = true;
                   Debug.signal(Debug.CRITICAL,null,"Received VM Shutdown Signal.");

                // 1 - Lock servers...
                   serverManager.lockServers();

                // 2 - We warn connected clients
                   serverManager.sendWarningMessage( "Your server has been stopped.\n"
                                           +"Try to reconnect in a few minutes.");

                // 3 - We close all remaining connections & save the data
                   serverManager.closeAllConnections();
                   dataManager.shutdown(true);
                   serverManager.shutdown();

                   Debug.signal(Debug.CRITICAL,null,"Data Saved. Exiting.");
                   Debug.flushPrintStream();
           	}
           };

           Runtime.getRuntime().addShutdownHook(shutdownThread);


        // STEP 9 - Everything is ok ! we enter the persistence loop
           Debug.signal( Debug.NOTICE, null, "Starting persistence thread..." );

           serverDirector = new ServerDirector();
           serverManager.getGameServer().setErrorListener( serverDirector );
           
           Thread persistenceThread = new Thread( serverDirector );
           persistenceThread.start();


        // If we are in "daemon" mode the only way to stop the server is via signals.
        // Otherwise we wait 2s and wait for a key to be pressed to shutdown...
           if( !isDaemon ) {
               Tools.waitTime( 2000 ); // 2s
               Debug.signal( Debug.NOTICE, null, "Press <ENTER> if you want to shutdown this server." );
 
                 try{
                     System.in.read();
                 }catch( Exception e ) {
                     e.printStackTrace();
                 }

               Debug.signal( Debug.NOTICE, null, "Leaving in 30s..." );

                 try{
                     Runtime.getRuntime().removeShutdownHook(shutdownThread);
                 }catch(Exception e) {
               	     return; // we couldn't remove the hook, it means the VM is already exiting
                 }

               serverDirector.shutdown();
           }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Run method for Thread. 
    */
      public void run(){
           do{
             // 1 - we wait the persistence period minus 2 minutes
                synchronized( this ) {
                  try{
                     wait( serverProperties.getIntegerProperty("init.persistencePeriod")*1000*3600-1000*120 );
                  }catch(Exception e) {}
                }

                if(immediatePersistenceThreadStop)
                   return;

             // 2 - Start persistence action
                persistenceAction( !mustStopThread() );
           }
           while( !mustStopThread() );

        // We shutdown the server
           dataManager.shutdown(false);
           serverManager.shutdown();

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
     protected void persistenceAction( boolean maintenance ) {

           // 1 - We warn all the clients that the server is going to enter
           // maintenance mode for 2-3 minutes, in 2 minutes.
           // If we are not in maintenance mode ( maintenance=false )
           // the message is a "server shuting down" message.
              serverManager.lockServers();

              if( maintenance ) {
                  Debug.signal( Debug.NOTICE, null, "Server will enter maintenance mode in 2 minutes...");
                  serverManager.sendWarningMessage( "Your server will enter maintenance mode in 2 minutes.\n"
                                                    +"Please disconnect and reconnect in 5 minutes");
                  Tools.waitTime( 1000*120 ); // 2mn 
                  Debug.signal( Debug.WARNING, null, "Server enters maintenance mode now... ("+Tools.getLexicalTime()+")");
              }
              else {
                  Debug.signal( Debug.NOTICE, null, "Sending warning messages to connected clients...");
                  serverManager.sendWarningMessage( "Your server is about to shutdown in 30s.\n"
                                                                    +"Please disconnect and reconnect later.");
                  Tools.waitTime( 1000*30 );  // 30s
                  Debug.signal( Debug.NOTICE, null, "Saving world & player data... ("+Tools.getLexicalTime()+")");
              }
 

           // 2 - We close all remaining connections
           //     and save the data
              serverManager.closeAllConnections();
              dataManager.save();

           // 3 - Leaving Maintenance Mode...
              if( maintenance ) {
                  updateKeys(); // we update the keys...
                  serverManager.unlockServers();
                  Debug.signal( Debug.NOTICE, null, "Leaving maintenance mode... ("+Tools.getLexicalTime()+")" );
              }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Must stop the persistence thread ?
    */
      private synchronized boolean mustStopThread() {
      	 return mustStop;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To shutdown the server director (stops the persistence thread ).
    */
      private synchronized void shutdown() {
      	 mustStop = true;
      	 notify();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when an error occurs in one of the NetServer.
   *  DEPRECATED - WILL BE REMOVED SOON
   * @param e the exception that occured.
   */
     public void errorOccured( Exception e ) {
     	if(e instanceof BindException) {
     	   Debug.signal( Debug.NOTICE, this, "Trying to awake Persistence Thread..." );
     	   
     	   synchronized( this ) {
     	     notify(); // returns immediately
     	   }
     	}
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the server ID of this server.
    *
    * @return serverID
    */
      public static int getServerID() {
         return serverProperties.getIntegerProperty("init.serverID");
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the URL where are stored the remote server configs. This URL can also contain
   *  a news.html file to display some news.
   *
   * @return remoteServerConfigHomeURL
   */
   public static String getRemoteServerConfigHomeURL() {
      return remoteServersProperties.getProperty("info.remoteServerHomeURL");
   }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get server properties.
   * @return server properties
   */
   public static Properties getServerProperties() {
      return (Properties)serverProperties;
   }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To change the keys used for special players & the bot's controlKey
   */
   public static void updateKeys() {

       int period = serverProperties.getIntegerProperty("init.persistencePeriod");
       int nbIteration = 24/period;

       if(period<24) {
          if( (updateKeysPeriod%nbIteration)==0 )
              updateKeysPeriod=1;
          else {
              updateKeysPeriod++;
              return;
          }
       }

       serverProperties.setProperty( "key.shaitan", Tools.keyGenerator(23, getServerID()+1) );
       serverProperties.setProperty( "key.amyrlin", Tools.keyGenerator(23, getServerID()+2) );
       serverProperties.setProperty( "key.chronicles", Tools.keyGenerator(23, getServerID()+3) );
       serverProperties.setProperty( "key.mhael", Tools.keyGenerator(23, getServerID()+4) );
       Debug.signal( Debug.NOTICE, null, "Generated new keys for special characters..." );

       serverProperties.setProperty( "bots.controlKey", Tools.keyGenerator(23, getServerID()+5) );
       Debug.signal( Debug.NOTICE, null, "Generated new control key for bots..." );
   }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get our resource manager.
   *  @return our resource manager.
   */
     public static ResourceManager getResourceManager() {
         return resourceManager;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get our server manager.
   *  @return our server manager.
   */
     public static ServerManager getServerManager() {
         return serverManager;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get our data manager.
   *  @return our data manager.
   */
     public static DataManager getDataManager() {
         return dataManager;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

