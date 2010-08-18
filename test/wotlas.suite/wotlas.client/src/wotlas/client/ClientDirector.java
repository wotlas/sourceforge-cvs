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
import wotlas.libs.graphics2d.FontFactory;
import wotlas.libs.log.JLogStream;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;
import wotlas.utils.Tools;
import wotlas.utils.WotlasDefaultGameDefinition;
import wotlas.utils.WotlasGameDefinition;

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

    protected static ClientDirector _clientDirector;

    /** Our client properties.
     */
    protected ClientPropertiesFile clientProperties;

    /** Our remote server properties.
     */
    protected RemoteServersPropertiesFile remoteServersProperties;

    /** Our resource manager
     */
    protected ResourceManager resourceManager;

    /*------------------------------------------------------------------------------------*/

    /** Our Client Manager.
     */
    protected ClientManager clientManager;

    /** Our Data Manager.
     */
    protected DataManager dataManager;

    /** Client configuration (window size, sound volume, etc... )
     */
    protected ClientConfiguration clientConfiguration;

    /** Our JLogStream
     */
    protected JLogStream logStream;

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

        // TODO STEP 0 - Define the game context. 
        WotlasGameDefinition wgd = new WotlasDefaultGameDefinition(WotlasGameDefinition.ID_WOTLAS_CLIENT, new String[] { "server", "Server" }, new String[] { "client", "Client", "Standalone" });

        ClientDirector.runClient(classicLogWindow, basePath, wgd);
    }

    /**
     * @param classicLogWindow
     * @param basePath
     * @param wgd
     */
    private static void runClient(boolean classicLogWindow, String basePath, WotlasGameDefinition wgd) {

        ClientDirector clientDirector = new ClientDirector();
        ClientDirector._clientDirector = clientDirector;

        // STEP 1 - Creation of the ResourceManager
        clientDirector.resourceManager = new ResourceManager(basePath, false, wgd);

        // STEP 2 - Start a JLogStream to display our Debug messages
        try {
            if (!classicLogWindow)
                clientDirector.logStream = new JLogStream(new javax.swing.JFrame(), clientDirector.resourceManager.getExternalLogsDir() + ClientDirector.CLIENT_LOG_NAME, "log-title.jpg", clientDirector.resourceManager);
            else
                clientDirector.logStream = new JLogStream(new javax.swing.JFrame(), clientDirector.resourceManager.getExternalLogsDir() + ClientDirector.CLIENT_LOG_NAME, "log-title-dark.jpg", clientDirector.resourceManager);

            // TODO delete comment when alpha release works.            
            //            Debug.setPrintStream(ClientDirector.logStream);
            //             System.setOut(ClientDirector.logStream);
            //             System.setErr(ClientDirector.logStream);
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
        Debug.signal(Debug.NOTICE, null, "| Copyright (C) 2001-2008 WOTLAS Team |");
        Debug.signal(Debug.NOTICE, null, "*-------------------------------------*\n");

        Debug.signal(Debug.NOTICE, null, "Code version       : " + ResourceManager.WOTLAS_VERSION);

        if (!clientDirector.resourceManager.inJar())
            Debug.signal(Debug.NOTICE, null, "Data directory     : " + basePath);
        else
            Debug.signal(Debug.NOTICE, null, "Data directory     : JAR File");

        clientDirector.clientProperties = new ClientPropertiesFile(clientDirector.resourceManager);
        clientDirector.remoteServersProperties = new RemoteServersPropertiesFile(clientDirector.resourceManager);

        // STEP 4 - Creation of Sound Library
        SoundLibrary.createSoundLibrary(clientDirector.clientProperties, clientDirector.resourceManager, clientDirector.resourceManager);

        // STEP 5 - Creation of our Font Factory
        FontFactory.createDefaultFontFactory(clientDirector.resourceManager);
        Debug.signal(Debug.NOTICE, null, "Font Factory created...");

        // STEP 6 - We load the client configuration. There is always a config returned.
        clientDirector.clientConfiguration = ClientConfiguration.load();

        // STEP 7 - We ask the ClientManager to get ready
        clientDirector.clientManager = new ClientManager(clientDirector.resourceManager);
        Debug.signal(Debug.NOTICE, null, "Client Manager created...");

        // STEP 8 - We ask the DataManager to get ready
        clientDirector.dataManager = new DataManager(clientDirector.resourceManager);
        clientDirector.dataManager.showDebug(ClientDirector.SHOW_DEBUG);
        Debug.signal(Debug.NOTICE, null, "DataManager created...");

        // STEP 9 - Start the ClientManager
        clientDirector.clientManager.start(ClientManager.FIRST_INIT);
        Debug.signal(Debug.NOTICE, null, "WOTLAS Client started with success...");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    public static ClientDirector getCurrentClientDirector() {
        return ClientDirector._clientDirector;
    }

    /** To get the URL where are stored the remote server configs. This URL can also contain
     *  a news.html file to display some news.
     *
     * @return remoteServerConfigHomeURL
     */
    public static String getRemoteServerConfigHomeURL() {
        return ClientDirector.getCurrentClientDirector().remoteServersProperties.getProperty("info.remoteServerHomeURL");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the client Configuration and get some user preferences ( window size, etc... )
     *  @return Client Config, you can use the save() method to save it to disk...
     */
    public static ClientConfiguration getClientConfiguration() {
        return ClientDirector.getCurrentClientDirector().clientConfiguration;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our resource manager.
     *  @return our resource manager.
     */
    public static ResourceManager getResourceManager() {
        return ClientDirector.getCurrentClientDirector().resourceManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our Client manager. the client manager possesses the server configs
     *  and client profiles.
     *  @return our ClientManager
     */
    public static ClientManager getClientManager() {
        return ClientDirector.getCurrentClientDirector().clientManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our data manager. The data manager manages the game process.
     *  @return our data manager.
     */
    public static DataManager getDataManager() {
        return ClientDirector.getCurrentClientDirector().dataManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our log window.
     *  @return our log window which is a printStream.
     */
    public static JLogStream getLogStream() {
        return ClientDirector.getCurrentClientDirector().logStream;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
