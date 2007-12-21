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

package wotlas.editor;

import java.io.File;
import javax.swing.JOptionPane;
import wotlas.common.ResourceManager;
import wotlas.common.WorldManager;
import wotlas.common.universe.PreloaderEnabled;
import wotlas.common.universe.TileMap;
import wotlas.libs.graphics2d.FontFactory;
import wotlas.libs.log.JLogStream;
import wotlas.utils.Debug;
import wotlas.utils.Tools;

/** The MAIN EditTile class. 
 *
 * @author Diego
 */

public class EditTile {

    /*------------------------------------------------------------------------------------*/

    transient static public TileMap workingOnThisTileMap;
    transient static public boolean autoImport = false;
    transient static public boolean autoExport = false;

    /** Server Command Line Help
    */
    public final static String EditTile_COMMAND_LINE_HELP = "Usage: EditTile -[debug|help] -[base <path>] -[export|import]\n\n" + "Examples : \n" + "  EditTile -base ../base : sets the data location.\n" + "  EditTile -export  : backups all graphic part of the maps to backup directory.\n" + "  EditTile -import  : recreate all maptils from backup directory.\n\n" + "If the -base option is not set we search for configs in " + ResourceManager.DEFAULT_BASE_PATH + "\n\n";

    /** Name of the EditTile log file.
     */
    public final static String EditTile_LOG_NAME = "wot-EditTile.log";

    /*------------------------------------------------------------------------------------*/

    /** Our resource manager
     */
    private static ResourceManager resourceManager;

    /*------------------------------------------------------------------------------------*/

    /** Our Editor Data Manager.
     */
    private static EditorDataManager editorDataManager;

    /** Our JLogStream
     */
    private static JLogStream logStream;

    /** True if we show debug informations
     */
    public static boolean SHOW_DEBUG = false;

    /*------------------------------------------------------------------------------------*/

    /** Main Class. Starts the Wotlas EditTile.
     */
    public static void main(String argv[]) {

        /*  first of all Manage the Preloader for EditTile*/
        WorldManager.PRELOADER_STATUS = PreloaderEnabled.LOAD_ALL;

        // STEP 0 - We parse the command line options
        boolean classicLogWindow = false;
        String basePath = ResourceManager.DEFAULT_BASE_PATH;
        Debug.displayExceptionStack(true);

        for (int i = 0; i < argv.length; i++) {

            if (!argv[i].startsWith("-"))
                continue;

            if (argv[i].equals("-debug")) { // -- TO SET THE DEBUG MODE --
                System.out.println("mode DEBUG on");
                EditTile.SHOW_DEBUG = true;
            } else if (argv[i].equals("-base")) { // -- TO SET THE CONFIG FILES LOCATION --

                if (i == argv.length - 1) {
                    System.out.println("Location missing.");
                    System.out.println(EditTile.EditTile_COMMAND_LINE_HELP);
                    return;
                }

                basePath = argv[i + 1];
            } else if (argv[i].equals("-export")) { // -- TO auto export all
                EditTile.autoExport = true;
            } else if (argv[i].equals("-import")) { // -- TO auto import all
                EditTile.autoImport = true;
            } else if (argv[i].equals("-help")) { // -- TO DISPLAY THE HELP --

                System.out.println(EditTile.EditTile_COMMAND_LINE_HELP);
                return;
            }
        }

        // STEP 1 - Creation of the ResourceManager
        EditTile.resourceManager = new ResourceManager();

        if (!EditTile.resourceManager.inJar())
            EditTile.resourceManager.setBasePath(basePath);

        // STEP 2 - Start a JLogStream to display our Debug messages
        try {
            if (!classicLogWindow)
                EditTile.logStream = new JLogStream(new javax.swing.JFrame(), EditTile.resourceManager.getExternalLogsDir() + EditTile.EditTile_LOG_NAME, "log-title.jpg", EditTile.resourceManager);

            // DIEGO : I CAN SEE DEBUG DURING TILEMAP DEBUGGING
            // Debug.setPrintStream( logStream );
            // System.setOut( logStream );
            // System.setErr( logStream );
        } catch (Exception e) {
            e.printStackTrace();
            Tools.displayDebugMessage("Start-up Error", "" + e);
            Debug.exit();
        }

        if (EditTile.SHOW_DEBUG)
            System.out.println("Log created.");

        // STEP 3 - We control the VM version and load our vital config files.
        Debug.signal(Debug.NOTICE, null, "*-------------------------------------*");
        Debug.signal(Debug.NOTICE, null, "|    Wheel Of Time - Light & Shadow   |");
        Debug.signal(Debug.NOTICE, null, "| Copyright (C) 2001-2003 WOTLAS Team |");
        Debug.signal(Debug.NOTICE, null, "*-------------------------------------*\n");

        Debug.signal(Debug.NOTICE, null, "Code version       : " + ResourceManager.WOTLAS_VERSION);

        if (!EditTile.resourceManager.inJar())
            Debug.signal(Debug.NOTICE, null, "Data directory     : " + basePath);
        else
            Debug.signal(Debug.NOTICE, null, "Data directory     : JAR File");

        // STEP 4 - Creation of our Font Factory
        FontFactory.DEBUG_MODE = EditTile.SHOW_DEBUG;
        FontFactory.createDefaultFontFactory(EditTile.resourceManager);
        Debug.signal(Debug.NOTICE, null, "Font Factory created...");

        // STEP 8 - We ask the editorDataManager to get ready
        EditTile.editorDataManager = new EditorDataManager(EditTile.resourceManager);
        EditTile.editorDataManager.showDebug(EditTile.SHOW_DEBUG);
        Debug.signal(Debug.NOTICE, null, "EditorDataManager created...");

        Thread heavyProcessThread = new Thread() {
            @Override
            public void run() {
                EditTile.editorDataManager.showInterface();
            }
        };

        heavyProcessThread.start();
        EditTile.logStream.setVisible(false);

        if (EditTile.autoExport) {
            new File(EditTile.getResourceManager().getEditorBackupDataDir()).mkdirs();
            StoreTileMapBackground onlyBackground;
            TileMap[] mapTiles;
            mapTiles = EditTile.getDataManager().getWorldManager().getWorldMapFromID(0).getTileMaps();
            for (int i = 0; i < mapTiles.length; i++) {
                onlyBackground = mapTiles[i].getStoreBackground();
                EditTile.getResourceManager().BackupObject(onlyBackground, EditTile.getResourceManager().getEditorBackupDataDir() + File.separator + mapTiles[i].getShortName() + EditorPlugIn.GRAPHIC_DATA_EXPORTED_EXT);
            }
            System.exit(0);
        }

        if (EditTile.autoImport) {
            System.exit(0);
        }
    }

    public static ResourceManager getResourceManager() {
        return EditTile.resourceManager;
    }

    public static EditorDataManager getDataManager() {
        return EditTile.editorDataManager;
    }

    static public void letsTryToSave(int index) {
        TileMap tileMaps[] = EditTile.getDataManager().getWorldManager().getWorldMapFromID(0).getTileMaps();
        if (tileMaps == null)
            return;
        if (tileMaps[index] == null)
            return;
        String universeHome = EditTile.getResourceManager().getUniverseDataDir() + WorldManager.DEFAULT_UNIVERSE + File.separator;
        String worldHome = universeHome + EditTile.getDataManager().getWorldManager().getWorldMapFromID(0).getShortName() + File.separator;
        if (tileMaps[index].getAreaName().length() <= 0) {
            String tileMapHome = worldHome + tileMaps[index].getShortName() + WorldManager.TILEMAP_DIR_EXT + File.separator;
            new File(tileMapHome).mkdir();
            if (!EditTile.getResourceManager().BackupObject(tileMaps[index], tileMapHome + WorldManager.TILEMAP_FILE)) {
                Debug.signal(Debug.ERROR, null, "Failed to save tileMap : " + tileMapHome);
                JOptionPane.showMessageDialog(EditTile.getDataManager().getScreen(), "Map not Saved", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            String areaOfTile = worldHome + tileMaps[index].getAreaName() + WorldManager.AREA_EXT + File.separator;
            new File(areaOfTile).mkdir();
            if (!EditTile.getResourceManager().BackupObject(tileMaps[index], areaOfTile + tileMaps[index].getShortName() + WorldManager.TILEMAP_EXT)) {
                Debug.signal(Debug.ERROR, null, "Failed to save tileMap : " + areaOfTile);
                JOptionPane.showMessageDialog(EditTile.getDataManager().getScreen(), "Map not Saved", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
        JOptionPane.showMessageDialog(EditTile.getDataManager().getScreen(), "Map Saved");
    }
}