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

class ClientDirector
{

 /*------------------------------------------------------------------------------------*/
  
  /** Static Link to Database Config File.
   */
  public final static String DATABASE_CONFIG = "../src/config/client.cfg";

  /** Static Link to Log File.
   */
  public final static String CLIENT_LOG = "../log/wot-client.log";
  
  /** Complete Path to the database where are stored the client's profiles
   */
  private static String databasePath;
  
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
  
 /*------------------------------------------------------------------------------------*/

  /** Main Class. Starts the WHOLE Client
   */
  public static void main(String argv[])
  {
    
    System.out.println("Log started");
    // STEP 0 - Start a JLogStream to display our Debug messages
    try {
      Debug.setPrintStream( new JLogStream( new javax.swing.JFrame(), CLIENT_LOG, "../base/gui/log-title.jpg" ) );
    } catch( java.io.FileNotFoundException e ) {
      e.printStackTrace();
      return;
    }    
    System.out.println("Log created");
    
    Debug.signal( Debug.NOTICE, null, "*-----------------------------------*" );
    Debug.signal( Debug.NOTICE, null, "|   Wheel Of Time - Light & Shadow  |" );
    Debug.signal( Debug.NOTICE, null, "|  Copyright (C) 2001 - WOTLAS Team |" );
    Debug.signal( Debug.NOTICE, null, "*-----------------------------------*\n");
    
    // STEP 1 - We load the database path. Where is the data ?
    properties = FileTools.loadPropertiesFile( DATABASE_CONFIG );

    if (properties==null) {
      Debug.signal( Debug.FAILURE, null, "No valid client-database.cfg file found !" );
      Debug.exit();
    }
    
    databasePath = properties.getProperty( "DATABASE_PATH" );

    if (databasePath==null) {
      Debug.signal( Debug.FAILURE, null, "No Database Path specified in config file !" );
      Debug.exit();
    }
    
    Debug.signal( Debug.NOTICE, null, "DataBase Path Found : "+databasePath );

    // STEP 2 - Creation of the PersistenceManager
    persistenceManager = PersistenceManager.createPersistenceManager(databasePath);
    Debug.signal( Debug.NOTICE, null, "Persistence Manager Created..." );
                
    // STEP 3 - We ask the ClientManager to get ready
    clientManager = ClientManager.createClientManager(databasePath);
    Debug.signal( Debug.NOTICE, null, "Client Created (but not started)..." );

    // STEP 4 - We ask the DataManager to get ready
    dataManager = DataManager.createDataManager(databasePath);
    Debug.signal( Debug.NOTICE, null, "DataManager created..." );
    
    // STEP 5 - Start the ClientManager
    clientManager.start(0);
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

}

   