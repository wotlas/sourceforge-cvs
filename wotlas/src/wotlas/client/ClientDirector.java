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

package wotlas.client;

import wotlas.common.RemoteServersPropertiesFile;
import wotlas.common.ResourceManager;
import wotlas.libs.graphics2D.FontFactory;
import wotlas.libs.log.JLogStream;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;
import wotlas.utils.Tools;

/** The MAIN client class. It starts the PersistenceManager, the ClientManager
 * and the DataManager.
 *
 * @author Petrus
 * @see wotlas.client.PersistenceManager;
 * @see wotlas.client.ClientManager; 
 */

public class ClientDirector {

    /*------------------------------------------------------------------------------------*/

    /** Server Command Line Help
     */
    public final static String CLIENT_COMMAND_LINE_HELP = "Usage: ClientDirector -[debug|classic|help] -[base <path>]\n\n" + "Examples : \n" + "  ClientDirector -classic      : displays the classic log window.\n" + "  ClientDirector -base ../base : sets the data location.\n\n" + "If the -base option is not set we search for configs in " + ResourceManager.DEFAULT_BASE_PATH + "\n\n";

    /** Name of the client log file.
     */
    public final static String CLIENT_LOG_NAME = "wot-client.log";

    /*------------------------------------------------------------------------------------*/

    /** Our client properties.
     */
    private static ClientPropertiesFile clientProperties;

    /** Our remote server properties.
     */
    private static RemoteServersPropertiesFile remoteServersProperties;

    /** Our resource manager
     */
    private static ResourceManager resourceManager;

    /*------------------------------------------------------------------------------------*/

    /** Our Client Manager.
     */
    private static ClientManager clientManager;

    /** Our Data Manager.
     */
    private static DataManager dataManager;

    /** Client configuration (window size, sound volume, etc... )
     */
    private static ClientConfiguration clientConfiguration;

    /** Our JLogStream
     */
    private static JLogStream logStream;

    /** True if we show debug informations
     */
    public static boolean SHOW_DEBUG = false;

    /*------------------------------------------------------------------------------------*/

    /** Main Class. Starts the Wotlas Client.
     *  @param argv enter -help to get some help info.
     */
    public static void main(String argv[]) {

        // STEP 0 - We parse the command line options
        boolean classicLogWindow = false;
        String basePath = ResourceManager.DEFAULT_BASE_PATH;
        Debug.displayExceptionStack(true);

        for (int i = 0; i < argv.length; i++) {

            if (!argv[i].startsWith("-"))
                continue;

            if (argv[i].equals("-debug")) { // -- TO SET THE DEBUG MODE --
                System.out.println("mode DEBUG on");
                ClientDirector.SHOW_DEBUG = true;
            } else if (argv[i].equals("-classic")) {
                classicLogWindow = true;
            } else if (argv[i].equals("-base")) { // -- TO SET THE CONFIG FILES LOCATION --

                if (i == argv.length - 1) {
                    System.out.println("Location missing.");
                    System.out.println(ClientDirector.CLIENT_COMMAND_LINE_HELP);
                    return;
                }

                basePath = argv[i + 1];
            } else if (argv[i].equals("-help")) { // -- TO DISPLAY THE HELP --

                System.out.println(ClientDirector.CLIENT_COMMAND_LINE_HELP);
                return;
            }
        }

        // STEP 1 - Creation of the ResourceManager
        ClientDirector.resourceManager = new ResourceManager();

        if (!ClientDirector.resourceManager.inJar())
            ClientDirector.resourceManager.setBasePath(basePath);

        // STEP 2 - Start a JLogStream to display our Debug messages
        try {
            if (!classicLogWindow)
                ClientDirector.logStream = new JLogStream(new javax.swing.JFrame(), ClientDirector.resourceManager.getExternalLogsDir() + ClientDirector.CLIENT_LOG_NAME, "log-title.jpg", ClientDirector.resourceManager);
            else
                ClientDirector.logStream = new JLogStream(new javax.swing.JFrame(), ClientDirector.resourceManager.getExternalLogsDir() + ClientDirector.CLIENT_LOG_NAME, "log-title-dark.jpg", ClientDirector.resourceManager);

            Debug.setPrintStream(ClientDirector.logStream);
            System.setOut(ClientDirector.logStream);
            System.setErr(ClientDirector.logStream);
        } catch (Exception e) {
            e.printStackTrace();
            Tools.displayDebugMessage("Start-up Error", "" + e);
            Debug.exit();
        }

        if (ClientDirector.SHOW_DEBUG)
            System.out.println("Log created.");

        // STEP 3 - We control the VM version and load our vital config files.
        Debug.signal(Debug.NOTICE, null, "*-------------------------------------*");
        Debug.signal(Debug.NOTICE, null, "|    Wheel Of Time - Light & Shadow   |");
        Debug.signal(Debug.NOTICE, null, "| Copyright (C) 2001-2002 WOTLAS Team |");
        Debug.signal(Debug.NOTICE, null, "*-------------------------------------*\n");

        Debug.signal(Debug.NOTICE, null, "Code version       : " + ResourceManager.WOTLAS_VERSION);

        if (!ClientDirector.resourceManager.inJar())
            Debug.signal(Debug.NOTICE, null, "Data directory     : " + basePath);
        else
            Debug.signal(Debug.NOTICE, null, "Data directory     : JAR File");

        ClientDirector.clientProperties = new ClientPropertiesFile(ClientDirector.resourceManager);
        ClientDirector.remoteServersProperties = new RemoteServersPropertiesFile(ClientDirector.resourceManager);

        // STEP 4 - Creation of Sound Library
        SoundLibrary.createSoundLibrary(ClientDirector.clientProperties, ClientDirector.resourceManager, ClientDirector.resourceManager);

        // STEP 5 - Creation of our Font Factory
        FontFactory.createDefaultFontFactory(ClientDirector.resourceManager);
        Debug.signal(Debug.NOTICE, null, "Font Factory created...");

        // STEP 6 - We load the client configuration. There is always a config returned.
        ClientDirector.clientConfiguration = ClientConfiguration.load();

        // STEP 7 - We ask the ClientManager to get ready
        ClientDirector.clientManager = new ClientManager(ClientDirector.resourceManager);
        Debug.signal(Debug.NOTICE, null, "Client Manager created...");

        // STEP 8 - We ask the DataManager to get ready
        ClientDirector.dataManager = new DataManager(ClientDirector.resourceManager);
        ClientDirector.dataManager.showDebug(ClientDirector.SHOW_DEBUG);
        Debug.signal(Debug.NOTICE, null, "DataManager created...");

        // STEP 9 - Start the ClientManager
        ClientDirector.clientManager.start(ClientManager.FIRST_INIT);
        Debug.signal(Debug.NOTICE, null, "WOTLAS Client started with success...");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the URL where are stored the remote server configs. This URL can also contain
     *  a news.html file to display some news.
     *
     * @return remoteServerConfigHomeURL
     */
    public static String getRemoteServerConfigHomeURL() {
        return ClientDirector.remoteServersProperties.getProperty("info.remoteServerHomeURL");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the client Configuration and get some user preferences ( window size, etc... )
     *  @return Client Config, you can use the save() method to save it to disk...
     */
    public static ClientConfiguration getClientConfiguration() {
        return ClientDirector.clientConfiguration;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our resource manager.
     *  @return our resource manager.
     */
    public static ResourceManager getResourceManager() {
        return ClientDirector.resourceManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our Client manager. the client manager possesses the server configs
     *  and client profiles.
     *  @return our ClientManager
     */
    public static ClientManager getClientManager() {
        return ClientDirector.clientManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our data manager. The data manager manages the game process.
     *  @return our data manager.
     */
    public static DataManager getDataManager() {
        return ClientDirector.dataManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our log window.
     *  @return our log window which is a printStream.
     */
    public static JLogStream getLogStream() {
        return ClientDirector.logStream;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
