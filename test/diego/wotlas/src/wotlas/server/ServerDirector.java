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

import wotlas.server.setup.ServerAdminGUI;
import wotlas.utils.Debug;
import wotlas.utils.Tools;
import wotlas.utils.FileTools;

import wotlas.libs.log.*;
import wotlas.libs.net.*;
import wotlas.libs.sound.SoundLibrary;

import wotlas.common.*;
import wotlas.common.RemoteServersPropertiesFile;

import wotlas.libs.aswing.ALoginDialog;

import wotlas.common.environment.*;
import wotlas.common.universe.*;
import wotlas.common.action.*;

import wotlas.libs.npc.*;

import java.awt.Frame;
import java.io.File;
import java.util.Properties;
import java.net.*;
import java.util.*;

import org.python.util.PythonInterpreter;
import org.python.core.*; 

/** The MAIN server class. It starts the PersistenceManager, the ServerManager
 *  and the DataManager. So got it ? yeah, it's the boss on the server side...
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 * @see wotlas.server.PersistenceManager
 */

public class ServerDirector implements Runnable, NetServerListener {

 /*------------------------------------------------------------------------------------*/

    /** Server Command Line Help
    */
    public final static String SERVER_COMMAND_LINE_HELP =
            "Usage: ServerDirector -[debug|admin|erroronly|daemon|help] -[base <path>]\n\n"
           +"Examples : \n"
           +"  ServerDirector -admin        : will display the admin GUI only.\n"
           +"  ServerDirector -daemon       : the server will display nothing.\n"
           +"  ServerDirector -erroronly    : the server will only print errors.\n"
           +"  ServerDirector -base ../base : sets the data location.\n\n"
           +"If the -base option is not set we search for data in "
           +ResourceManager.DEFAULT_BASE_PATH
           +"\n\n";

    /** Format of the server log name.
    */
    public final static String SERVER_LOG_PREFIX = "wot-server-";
    public final static String SERVER_LOG_SUFFIX = ".log";

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
      
    /**  our defult Npc Manager
    *   
    */
    private static NpcManager npcManager;

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

    /** Default password for transfer
    */
    private static String password;

    /** To stop the persistence thread.
    */
    private boolean mustStop = false;

    /** To tell if the server is enabled or not (network interfaces available).
    */
    private boolean serverEnabled = true;

    /** Show debug information ?
    */
    public static boolean SHOW_DEBUG = false;
      
    static private long genUniqueKeyId;
      
    static public PythonInterpreter interp;

 /*------------------------------------------------------------------------------------*/

    /** Main Class. Starts the Wotlas Server.
    * @param argv enter -help to get some help info.
    */
    public static void main( String argv[] ) {

        /*  first of all Manage the Preloader for WorldGenerator*/
        WorldManager.PRELOADER_STATUS = PreloaderEnabled.LOAD_SERVER_DATA;
        // set random variable to roll dices
        initRoll();
        
        // STEP 0 - We parse the command line options
        boolean isDaemon = false;
        boolean displayAdminGUI = false;
        String basePath = ResourceManager.DEFAULT_BASE_PATH;
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
            else if (argv[i].equals("-admin")) {  // -- TO ONLY DISPLAY THE ADMIN GUI --
                  if(isDaemon) {
                     System.out.println("Incompatible options.");
                     System.out.println(SERVER_COMMAND_LINE_HELP);
                     return;
                   }

                   displayAdminGUI = true;
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

        // STEP 1 - Creation of the ResourceManager
        resourceManager = new ResourceManager();

        if( !resourceManager.inJar() )
            resourceManager.setBasePath(basePath);


        // STEP 2 - We create a LogStream to save our Debug messages to disk.
        try{
            if(isDaemon) {
                // We don't print the Debug messages on System.err
                Debug.setPrintStream( new DaemonLogStream( resourceManager.getExternalLogsDir()
                +SERVER_LOG_PREFIX+System.currentTimeMillis()+SERVER_LOG_SUFFIX ) );
            }
            else if(displayAdminGUI) {
                Debug.setPrintStream( new JLogStream( new javax.swing.JFrame(),
                                    resourceManager.getExternalLogsDir()+"server-setup.log",
                                    "log-title-dark.jpg", resourceManager ) );
            }
            else{
               	// We also print the Debug messages on System.err
                Debug.setPrintStream( new ServerLogStream( resourceManager.getExternalLogsDir()
                           +SERVER_LOG_PREFIX+System.currentTimeMillis()+SERVER_LOG_SUFFIX ) );
            }
        }
        catch( java.io.FileNotFoundException e ) {
            e.printStackTrace();
            return;
        }

        // STEP 3 - We control the VM version and load our vital config files.
        if( !Tools.javaVersionHigherThan( "1.3.0" ) )
            Debug.exit();

        Debug.signal( Debug.NOTICE, null, "*----------------------------------------*" );
        Debug.signal( Debug.NOTICE, null, "|    Wheel Of Time - Light & Shadow     |" );
        Debug.signal( Debug.NOTICE, null, "| Copyright (C) 2001-2003 WOTLAS Team   |" );
        Debug.signal( Debug.NOTICE, null, "*---------------------------------------*\n");
        Debug.signal( Debug.NOTICE, null, "|     ver 2.0 is an alpha:              |" );
        Debug.signal( Debug.NOTICE, null, "|                                       |" );
        Debug.signal( Debug.NOTICE, null, "| Every times a new comes up u need     |" );
        Debug.signal( Debug.NOTICE, null, "| to delete universe and home directory |" );
        Debug.signal( Debug.NOTICE, null, "| and reload server-world-generator     |" );
        Debug.signal( Debug.NOTICE, null, "|                            Shra       |" );
        Debug.signal( Debug.NOTICE, null, "*---------------------------------------*" );

        Debug.signal( Debug.NOTICE, null, "Code version       : "+resourceManager.WOTLAS_VERSION );

        if( !resourceManager.inJar() )
            Debug.signal( Debug.NOTICE, null, "Data directory     : "+basePath );
        else
            Debug.signal( Debug.NOTICE, null, "Data directory     : JAR File" );

        serverProperties = new ServerPropertiesFile(resourceManager);
        remoteServersProperties = new RemoteServersPropertiesFile(resourceManager);

        if(displayAdminGUI) {
            ServerAdminGUI.create();
            return; // we just display the admin GUI, we don't start the server.
        }

        // STEP 4 - Loading Jython
        try {
            interp = new PythonInterpreter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Debug.signal( Debug.NOTICE, null, "Jython loaded..." );
           
        // STEP 5 - We ask the ServerManager to get ready
        serverManager = new ServerManager(resourceManager);
        Debug.signal( Debug.NOTICE, null, "Server Manager created..." );

        // STEP 6 - We ask the DataManager to load the worlds & client accounts
        dataManager = new DataManager(resourceManager);
        dataManager.init( serverProperties );

        // STEP 7 - Loading Npc Definition
        npcManager = new NpcManager();
        npcManager.init();
        UserAction.InitAllActions(true);
        Debug.signal( Debug.NOTICE, null, "Npc Definition,Action and Spells loaded..." );

        // STEP 8 - Sound Library for alerts... (we only create a sound player)
        SoundLibrary.createSoundLibrary( serverProperties, null, resourceManager );

        // STEP 9 - Start of the GameServer, AccountServer & GatewayServer !
        Debug.signal( Debug.NOTICE, null, "Starting Game server, Account server & Gateway server..." );

        serverDirector = new ServerDirector();
        serverManager.getGameServer().addServerListener( serverDirector );
        serverManager.start();

        // STEP 10 - We generate new keys for special characters
        updateKeys();

        // STEP 11 - Adding Shutdown Hook
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
                SoundLibrary.clear();

                Debug.signal(Debug.CRITICAL,null,"Data Saved. Exiting.");
                Debug.flushPrintStream();
            }
        };

        Runtime.getRuntime().addShutdownHook(shutdownThread);

        // STEP 12 - Everything is ok ! we enter the persistence loop
        Debug.signal( Debug.NOTICE, null, "Starting persistence thread..." );
           
        Thread persistenceThread = new Thread( serverDirector );
        persistenceThread.start();

        // STEP 13 - Show environment type
        Debug.signal( Debug.NOTICE, null, "Server environment name : "+EnvironmentManager.getEnvironmentName()+" ." );
        EnvironmentManager.getEnvironmentHour();
        
        //  STEP 14 - Running a thread to manage maps and npc inside maps.
        Debug.signal( Debug.NOTICE, null, "Starting npc/map(encounterSchedule) thread..." );
        npcManager.start();
           
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

            npcManager.shouldQuit(true);
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

  /** To get remote servers properties.
   * @return remote servers properties
   */
   public static Properties getRemoteServersProperties() {
      return (Properties)remoteServersProperties;
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

   /** This method is called when the server network interface is down.
    * @param itf the network interface we tried which is NOT available.
    */
     public void serverInterfaceIsDown( String itf ) {
          Debug.signal( Debug.NOTICE, null, "Network interface "+itf+" is down... we'll retry a connection in three minutes.");
          serverEnabled = false;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** This method is called when the server network interface is Up.
    * @param ipAddress currently used IP address
    * @param stateChanged if true it means the interface has been re-created ( ip changed or
    *        serverSocket has just been created ). If false it means that the interface is Up
    *        and its state has not changed (since last check).
    */
     public void serverInterfaceIsUp( String ipAddress, boolean stateChanged ) {
          if(!stateChanged) {
             if(!serverEnabled) {
                serverEnabled = true;
                Debug.signal( Debug.NOTICE, null, "Network interface "+ipAddress+" is up... IP has not changed.");
             }

             return;
          }

          Debug.signal( Debug.NOTICE, null, "Network interface "+ipAddress+" is up...");

       // We save the new IP
          if( !serverManager.getServerConfigManager().updateServerConfig(
                              null, ipAddress, serverManager.getServerConfig() ) ) {
              Debug.signal( Debug.CRITICAL, this, "Failed to save new IP ("+ipAddress+") to the local server-"
                                                  +getServerID()+".cfg.adr file ! Please perform a manual update!"
                                                  +"Your server's IP has changed !" );
              return;
          }
          else
              Debug.signal( Debug.NOTICE, null, "IP saved to server-"+getServerID()+".cfg.adr file");

          if( getServerID()==0 )
              return; // local server

       // Manual update ?
          if( !serverProperties.getBooleanProperty("init.automaticUpdate") ) {
              String publishAddress = serverProperties.getProperty("init.publishAddress");

              if(publishAddress==null || publishAddress.length()==0)
                 Debug.signal( Debug.NOTICE, null, "Your server's IP has changed, you should do a manual update on the wotlas web server!"
                            +"Your server will be unreachable until then.");
              else
                 Debug.signal( Debug.NOTICE, null, "Your server's local IP has changed, you should update your NAT table if you are using"
                            +"it. Your server will be unreachable until then.");
              return;
          }

       // Automatic Update via a Thread (we don't want to make our server wait)
          Thread updateThread = new Thread() {
             public void run() {
              // We get the login
                 String login = remoteServersProperties.getProperty("transfer.serverHomeLogin");

              // We load the script we are about to modify with login & password.
                 String cmd = resourceManager.getExternalTransferScript();
                 File wDir = new File(resourceManager.getExternalScriptsDir());
                 String script = resourceManager.loadText( cmd );
                 boolean editScript = true;
                 
                 if( script.indexOf("SET WEB_LOGIN")<0 || script.indexOf("SET WEB_PASSWORD")<0 )
                     editScript = false; // the script doesn't use any login & password, no need to edit it

              // Did the user already entered the password ?
                 if(password==null && editScript) {
                    ALoginDialog dialog = new ALoginDialog( new Frame(), "File Transfer Login (asked once):", login, resourceManager );
                    
                    if( dialog.okWasClicked() ) {
                        login = dialog.getLogin();
                        remoteServersProperties.setProperty("transfer.serverHomeLogin",login);
                        password = dialog.getPassword();
                    }
                    else {
                       Debug.signal( Debug.ERROR, null, "No password set. Transfer aborted");
                       return; // no transfer
                    }
                 }

              // Runtime... we execute the transfert command
                 int result=1;

                    try{
                      cmd = new File( cmd ).getCanonicalPath();
                    }catch(Exception ex ) {
                      Debug.signal( Debug.ERROR, this, "Failed to find scripts ! Err: "+ex+" Cmd:"+cmd);
                      return;
                    }

                 // We replace the login & password values
                    if( script==null ) {
                        Debug.signal( Debug.ERROR, this, "Failed to load "+script+" !" );
                        return;
                    }
                    else if( editScript ) {
                        script = FileTools.updateProperty( "SET WEB_LOGIN", login, script );
                        script = FileTools.updateProperty( "SET WEB_PASSWORD", password, script );

                        if( !resourceManager.saveText( cmd, script ) ) {
                            Debug.signal( Debug.ERROR, this, "Failed to save "+script+" !" );
                            return;
                        }
                    }

                 // We run the script...
                    Debug.signal(Debug.NOTICE,null,"Launching transfer script...");

                    try{
                       Process pr = Runtime.getRuntime().exec( cmd, null, wDir );
                       result = pr.waitFor();
                    }
                    catch( Exception ex ) {
                       Debug.signal( Debug.ERROR, this, "Command Line Failed : "+ex.getMessage() );
                       return;
                    }

                 Debug.signal(Debug.NOTICE,null,"Transfer script ended.");

                 if( editScript ) {
                   // We clean what we have modified in the script
                     script = FileTools.updateProperty( "SET WEB_LOGIN", "You will be prompted for your login.", script );
                     script = FileTools.updateProperty( "SET WEB_PASSWORD", "You will be prompted for your passsword.", script );

                     if( !resourceManager.saveText( cmd, script ) ) {
                         Debug.signal( Debug.ERROR, this, "Failed to save "+script+" to clean entries !" );
                         return;
                     }
                 }
             }
          };

        // We start the thread that will take care of the update
          updateThread.start();
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

    static synchronized public String GenUniqueKeyId() {
        return System.currentTimeMillis()+""+genUniqueKeyId++;
    }

  /* - - - - - - - - ROLL DICE SECTION - - - - - - - - - - - - - - - - -*/

    static private Random Dice;
    static private boolean needInit = true;
    
    static public final short roll(int dices, int diceSize) {
        short value = 0;
        for(int i=0; i < dices ; i++){
            value += new Double( 1+(Dice.nextDouble()*(diceSize) ) ).shortValue() ;;
        }
        return value;
    }
    
    static private void initRoll() {
        if(!needInit)
            return;
        Dice = new Random( System.currentTimeMillis() ) ;
        needInit = false;        
    }
}