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
 
package wotlas.client;

import wotlas.libs.log.*;

import wotlas.libs.persistence.*;

import wotlas.libs.sound.SoundLibrary;

import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.utils.Tools;

import java.util.Properties;
import java.util.Iterator;

/** The MAIN client class. It starts the PersistenceManager, the ClientManager
 * and the DataManager.
 *
 * @author Petrus
 * @see wotlas.client.PersistenceManager;
 * @see wotlas.client.ClientManager; 
 */

public class ClientDirector
{

 /*------------------------------------------------------------------------------------*/
  
  /** Static Link to Database Config File.
   */
  public final static String DATABASE_CONFIG = "../src/config/client.cfg";

  /** Static Link to Log File.
   */
  public final static String CLIENT_LOG = "../log/wot-client.log";

  /** Static Link to Remote Servers Config File.
   */
  public final static String REMOTE_SERVER_CONFIG = "../src/config/remote-servers.cfg";
  
  /** Client options and configuration
   */
  public final static String CLIENT_OPTIONS = "../src/config/client-options.cfg";
  
  /** Complete Path to the database where are stored the client's profiles
   */
  private static String databasePath;

  /** Remote server home URL : where the server list is stored on the internet.
   */
  private static String remoteServerConfigHomeURL;
  
  /** Other eventual properties.
   */
  private static Properties properties;
      
  /** Our Persistence Manager.
   */
  private static PersistenceManager persistenceManager;
  
  /** Our Client Manager.
   */
  private static ClientManager clientManager;
  
  /** Our Data Manager.
   */
  private static DataManager dataManager;
  
  /** True if we show debug informations
   */
  public static boolean SHOW_DEBUG = false;
  
 /*------------------------------------------------------------------------------------*/

  /** Main Class. Starts the WHOLE Client<br>
   * Use flag -debug to display debug informations.
   */
  public static void main(String argv[])
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
      }
      /*if (arg.equals("-nosound")) {
        System.out.println("sound off");
        wotlas.libs.sound.SoundLibrary.setNoSoundDevice();
      }*/
    }
    
    if (SHOW_DEBUG)
      System.out.println("Log started");
      
    // STEP 0 - Start a JLogStream to display our Debug messages
    try {
      Debug.setPrintStream( new JLogStream( new javax.swing.JFrame(), CLIENT_LOG, "../base/gui/log-title.jpg" ) );
    } catch( java.io.FileNotFoundException e ) {
      e.printStackTrace();
      return;
    }    
    if (SHOW_DEBUG)
      System.out.println("Log created");
    
    Debug.signal( Debug.NOTICE, null, "*-----------------------------------*" );
    Debug.signal( Debug.NOTICE, null, "|   Wheel Of Time - Light & Shadow  |" );
    Debug.signal( Debug.NOTICE, null, "|  Copyright (C) 2001 - WOTLAS Team |" );
    Debug.signal( Debug.NOTICE, null, "*-----------------------------------*\n");
    
    // STEP 1 - We load the database path. Where is the data ?
    properties = FileTools.loadPropertiesFile( DATABASE_CONFIG );

    if (properties==null) {
      Debug.signal( Debug.FAILURE, null, "No valid client.cfg file found !" );
      Debug.exit();
    }
    
    databasePath = properties.getProperty( "DATABASE_PATH","" );

    if (databasePath.length()==0) {
      Debug.signal( Debug.FAILURE, null, "No Database Path specified in config file !" );
      Debug.exit();
    }
    
    Debug.signal( Debug.NOTICE, null, "DataBase Path Found : "+databasePath );

    // STEP 2 - We load the remote servers config file to get the admin email.
    Properties remoteProps = FileTools.loadPropertiesFile( REMOTE_SERVER_CONFIG );

    if( remoteProps==null ) {
        Debug.signal( Debug.CRITICAL, null, "No valid remote-servers.cfg file found !" );
        Debug.exit();
    }
    else {
        remoteServerConfigHomeURL = remoteProps.getProperty( "REMOTE_SERVER_CONFIG_HOME_URL","" );

        if( remoteServerConfigHomeURL.length()==0 ) {
            Debug.signal( Debug.CRITICAL, null, "No URL for remote server config home !" );
            Debug.exit();
        }
        
        if( !remoteServerConfigHomeURL.endsWith("/") )
             remoteServerConfigHomeURL += "/";
    }

    // STEP 3 - Creation of the PersistenceManager
    persistenceManager = PersistenceManager.createPersistenceManager(databasePath);
    Debug.signal( Debug.NOTICE, null, "Persistence Manager Created..." );
                
    // STEP 3 - Creation of Sound Library
    SoundLibrary.createSoundLibrary(databasePath);    
    
    // STEP 4 - We ask the ClientManager to get ready
    clientManager = ClientManager.createClientManager(databasePath);
    Debug.signal( Debug.NOTICE, null, "Client Created (but not started)..." );

    // STEP 5 - We ask the DataManager to get ready
    dataManager = DataManager.createDataManager(databasePath);
    dataManager.showDebug(SHOW_DEBUG);
    Debug.signal( Debug.NOTICE, null, "DataManager created..." );
    
    // STEP 6 - Start the ClientManager
    clientManager.start(-1);
    Debug.signal( Debug.NOTICE, null, "WOTLAS Client started with success..." );
        
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

  /** To get the URL where are stored the remote server configs. This URL can also contain
   *  a news.html file to display some news.
   *
   * @return remoteServerConfigHomeURL
   */
   public static String getRemoteServerConfigHomeURL() {
      return remoteServerConfigHomeURL;
   }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/


}

   