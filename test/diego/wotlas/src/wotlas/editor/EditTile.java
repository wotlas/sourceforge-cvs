/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2003 WOTLAS Team
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
 
package src.wotlas.editor;

import wotlas.libs.log.*;
import wotlas.libs.persistence.*;
import wotlas.libs.sound.SoundLibrary;
import wotlas.libs.graphics2D.FontFactory;

import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.utils.Tools;

import wotlas.common.*;

import java.io.File;
import java.util.Properties;
import java.util.Iterator;

/** The MAIN TileMapEditor class. 
 *
 * @author Diego
 */

public class EditTile {

 /*------------------------------------------------------------------------------------*/

    /** Server Command Line Help
   */
    public final static String TILEMAPEDITOR_COMMAND_LINE_HELP =
            "Usage: TileMapEditor -[debug|help] -[base <path>]\n\n"
           +"Examples : \n"
           +"  TileMapEditor -base ../base : sets the data location.\n\n"
           +"If the -base option is not set we search for configs in "
           +ResourceManager.DEFAULT_BASE_PATH
           +"\n\n";

  /** Name of the tilemapeditor log file.
   */
    public final static String TILEMAPEDITOR_LOG_NAME = "wot-tilemapeditor.log";

 /*------------------------------------------------------------------------------------*/

  /** Our remote server properties.
   */
    private static RemoteServersPropertiesFile remoteServersProperties;

  /** Our resource manager
   */
    private static ResourceManager resourceManager;

 /*------------------------------------------------------------------------------------*/

  /** Our tilemapeditor Manager.
   */
    private static ClientManager clientManager;
  
  /** Our Data Manager.
   */
    private static DataManager dataManager;

  /** Our JLogStream
   */
    private static JLogStream logStream;

  /** True if we show debug informations
   */
    public static boolean SHOW_DEBUG = false;

 /*------------------------------------------------------------------------------------*/

  /** Main Class. Starts the Wotlas tilemapeditor.
   */
   public static void main(String argv[]) {

    // STEP 0 - We parse the command line options
       boolean classicLogWindow = false;
       String basePath = ResourceManager.DEFAULT_BASE_PATH;
       Debug.displayExceptionStack( true );

       for( int i=0; i<argv.length; i++ ) {

            if( !argv[i].startsWith("-") )
                continue;

            if( argv[i].equals("-debug") ) {    // -- TO SET THE DEBUG MODE --
                System.out.println("mode DEBUG on");
                SHOW_DEBUG = true;
            }
            else if(argv[i].equals("-base")) {   // -- TO SET THE CONFIG FILES LOCATION --

                if(i==argv.length-1) {
                   System.out.println("Location missing.");
                   System.out.println(TILEMAPEDITOR_COMMAND_LINE_HELP);
                   return;
                }

                basePath = argv[i+1];
            }
            else if(argv[i].equals("-help")) {   // -- TO DISPLAY THE HELP --

                System.out.println(TILEMAPEDITOR_COMMAND_LINE_HELP);
                return;
            }
       }

    // STEP 1 - Creation of the ResourceManager
       resourceManager = new ResourceManager();
       
       if( !resourceManager.inJar() )
           resourceManager.setBasePath(basePath);

    // STEP 2 - Start a JLogStream to display our Debug messages
       try {
            if(!classicLogWindow)
               logStream = new JLogStream( new javax.swing.JFrame(),
                                           resourceManager.getExternalLogsDir()+TILEMAPEDITOR_LOG_NAME,
                                           "log-title.jpg", resourceManager );

            // DIEGO : I CAN SEE DEBUG DURING TILEMAP DEBUGGING
            Debug.setPrintStream( logStream );
            System.setOut( logStream );
            System.setErr( logStream );
       }
       catch( Exception e ) {
         e.printStackTrace();
         Tools.displayDebugMessage("Start-up Error",""+e);
         Debug.exit();
       }

       if(SHOW_DEBUG)
          System.out.println("Log created.");


    // STEP 3 - We control the VM version and load our vital config files.
       Debug.signal( Debug.NOTICE, null, "*-------------------------------------*" );
       Debug.signal( Debug.NOTICE, null, "|    Wheel Of Time - Light & Shadow   |" );
       Debug.signal( Debug.NOTICE, null, "| Copyright (C) 2001-2003 WOTLAS Team |" );
       Debug.signal( Debug.NOTICE, null, "*-------------------------------------*\n");

       Debug.signal( Debug.NOTICE, null, "Code version       : "+resourceManager.WOTLAS_VERSION );

       if( !resourceManager.inJar() )
           Debug.signal( Debug.NOTICE, null, "Data directory     : "+basePath );
        else
           Debug.signal( Debug.NOTICE, null, "Data directory     : JAR File" );

    // STEP 4 - Creation of our Font Factory
       FontFactory.createDefaultFontFactory( resourceManager );
       Debug.signal( Debug.NOTICE, null, "Font Factory created..." );

    // STEP 8 - We ask the DataManager to get ready
       dataManager = new DataManager( resourceManager );
       dataManager.showDebug(SHOW_DEBUG);
       Debug.signal( Debug.NOTICE, null, "DataManager created..." );
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

}
