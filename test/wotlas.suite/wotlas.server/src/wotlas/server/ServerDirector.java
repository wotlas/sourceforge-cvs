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

import java.awt.Frame;
import java.io.File;
import java.util.Properties;
import wotlas.common.RemoteServersPropertiesFile;
import wotlas.common.ResourceManager;
import wotlas.libs.aswing.ALoginDialog;
import wotlas.libs.log.DaemonLogStream;
import wotlas.libs.log.JLogStream;
import wotlas.libs.log.ServerLogStream;
import wotlas.libs.net.NetServerListener;
import wotlas.libs.sound.SoundLibrary;
import wotlas.server.setup.ServerAdminGUI;
import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.utils.Tools;
import wotlas.utils.WotlasDefaultGameDefinition;
import wotlas.utils.WotlasGameDefinition;

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
    public final static String SERVER_COMMAND_LINE_HELP = "Usage: ServerDirector -[debug|admin|erroronly|daemon|help] -[base <path>]\n\n" + "Examples : \n" + "  ServerDirector -admin        : will display the admin GUI only.\n" + "  ServerDirector -daemon       : the server will display nothing.\n" + "  ServerDirector -erroronly    : the server will only print errors.\n" + "  ServerDirector -base ../base : sets the data location.\n\n" + "If the -base option is not set we search for data in " + ResourceManager.DEFAULT_BASE_PATH + "\n\n";

    /** Format of the server log name.
     */
    public final static String SERVER_LOG_PREFIX = "wot-server-";
    public final static String SERVER_LOG_SUFFIX = ".log";

    /*------------------------------------------------------------------------------------*/

    /** Our server properties.
     */
    private ServerPropertiesFile serverProperties;

    /** Our remote server properties.
     */
    private RemoteServersPropertiesFile remoteServersProperties;

    /** Our resource manager
     */
    private ResourceManager resourceManager;

    /*------------------------------------------------------------------------------------*/

    /** Our Server Manager.
     */
    private ServerManager serverManager;

    /** Our Data Manager.
     */
    private DataManager dataManager;

    /** Our default ServerDirector (Peristence Thread).
     */
    private static ServerDirector _serverDirector;

    /*------------------------------------------------------------------------------------*/

    /** Shutdown Thread.
     */
    public static Thread shutdownThread;

    /** Period for changing the keys.
     */
    public byte updateKeysPeriod = 0;

    /** Immediate stop of persistence thread ?
     */
    public boolean immediatePersistenceThreadStop = false;

    /** Default password for transfer
     */
    private String password;

    /** To stop the persistence thread.
     */
    private boolean mustStop = false;

    /** To tell if the server is enabled or not (network interfaces available).
     */
    private boolean serverEnabled = true;

    /** Show debug information ?
     */
    public static boolean SHOW_DEBUG = false;

    /*------------------------------------------------------------------------------------*/

    /** Main Class. Starts the Wotlas Server.
     * @param argv enter -help to get some help info.
     */
    public static void main(String argv[]) {

        // STEP 0 - We parse the command line options
        boolean isDaemon = false;
        boolean displayAdminGUI = false;
        String basePath = ResourceManager.DEFAULT_BASE_PATH;
        Debug.displayExceptionStack(false);

        for (int i = 0; i < argv.length; i++) {

            if (!argv[i].startsWith("-"))
                continue;

            if (argv[i].equals("-debug")) { // -- TO SET THE DEBUG MODE --
                if (isDaemon) {
                    System.out.println("Incompatible options.");
                    System.out.println(ServerDirector.SERVER_COMMAND_LINE_HELP);
                    return;
                }

                System.out.println("mode DEBUG on");
                ServerDirector.SHOW_DEBUG = true;
                Debug.displayExceptionStack(true);
            } else if (argv[i].equals("-erroronly")) { // -- TO ONLY DISPLAY ERRORS --
                if (ServerDirector.SHOW_DEBUG) {
                    System.out.println("Incompatible options.");
                    System.out.println(ServerDirector.SERVER_COMMAND_LINE_HELP);
                    return;
                }

                Debug.displayExceptionStack(false);
                Debug.setLevel(Debug.ERROR);
            } else if (argv[i].equals("-admin")) { // -- TO ONLY DISPLAY THE ADMIN GUI --
                if (isDaemon) {
                    System.out.println("Incompatible options.");
                    System.out.println(ServerDirector.SERVER_COMMAND_LINE_HELP);
                    return;
                }

                displayAdminGUI = true;
            } else if (argv[i].equals("-daemon")) { // -- DAEMON MODE --
                //if (ServerDirector.SHOW_DEBUG) {
                //    System.out.println("Incompatible options.");
                //    System.out.println(ServerDirector.SERVER_COMMAND_LINE_HELP);
                //    return;
                //}

                isDaemon = true;
                Debug.displayExceptionStack(true);
                Debug.setLevel(Debug.NOTICE);
            } else if (argv[i].equals("-base")) { // -- TO SET THE CONFIG FILES LOCATION --

                if (i == argv.length - 1) {
                    System.out.println("Location missing.");
                    System.out.println(ServerDirector.SERVER_COMMAND_LINE_HELP);
                    return;
                }

                basePath = argv[i + 1];
            } else if (argv[i].equals("-help")) { // -- TO DISPLAY THE HELP --

                System.out.println(ServerDirector.SERVER_COMMAND_LINE_HELP);
                return;
            }
        }

        // TODO STEP 0 - Define the game context. 
        WotlasGameDefinition wgd = new WotlasDefaultGameDefinition(WotlasGameDefinition.ID_WOTLAS_SERVER, new String[] { "client", "Client" }, new String[] { "server", "Server" });

        ServerDirector.runServer(isDaemon, displayAdminGUI, basePath, wgd, true, ServerPropertiesFile.SERVER_CONFIG);
    }

    /**
     * Create and run the serverDirector as described by the WotlasGameDefinition.
     * 
     * @param isDaemon
     * @param displayAdminGUI
     * @param basePath
     * @param wgd
     * @param shutdownJreOnClose shutdown the current JRE with a call of System.exit(x).
     */
    public static ServerDirector runServer(boolean isDaemon, boolean displayAdminGUI, String basePath, WotlasGameDefinition wgd, boolean shutdownJreOnClose, String serverCfgFile) {

        final ServerDirector serverDirector = new ServerDirector();
        ServerDirector._serverDirector = serverDirector;

        // STEP 1 - Creation of the ResourceManager
        serverDirector.resourceManager = new ResourceManager(basePath, true, wgd);

        // STEP 2 - We create a LogStream to save our Debug messages to disk.
        try {
            if (isDaemon) {
                // We don't print the Debug messages on System.err
                Debug.setPrintStream(new DaemonLogStream(serverDirector.resourceManager.getExternalLogsDir() + ServerDirector.SERVER_LOG_PREFIX + System.currentTimeMillis() + ServerDirector.SERVER_LOG_SUFFIX));
            } else if (displayAdminGUI) {
                Debug.setPrintStream(new JLogStream(new javax.swing.JFrame(), serverDirector.resourceManager.getExternalLogsDir() + "server-setup.log", "log-title-dark.jpg", serverDirector.resourceManager));
            } else {
                // We also print the Debug messages on System.err
                Debug.setPrintStream(new ServerLogStream(serverDirector.resourceManager.getExternalLogsDir() + ServerDirector.SERVER_LOG_PREFIX + System.currentTimeMillis() + ServerDirector.SERVER_LOG_SUFFIX));
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        // STEP 3 - We control the VM version and load our vital config files.
        if (!Tools.javaVersionHigherThan("1.3.0"))
            Debug.exit();

        Debug.signal(Debug.NOTICE, null, "*-------------------------------------*");
        Debug.signal(Debug.NOTICE, null, "|    Wheel Of Time - Light & Shadow   |");
        Debug.signal(Debug.NOTICE, null, "| Copyright (C) 2001-2002 WOTLAS Team |");
        Debug.signal(Debug.NOTICE, null, "*-------------------------------------*\n");

        Debug.signal(Debug.NOTICE, null, "Code version       : " + ResourceManager.WOTLAS_VERSION);

        if (!serverDirector.resourceManager.inJar())
            Debug.signal(Debug.NOTICE, null, "Data directory     : " + basePath);
        else
            Debug.signal(Debug.NOTICE, null, "Data directory     : JAR File");

        serverDirector.serverProperties = new ServerPropertiesFile(serverDirector.resourceManager, serverCfgFile);
        serverDirector.remoteServersProperties = new RemoteServersPropertiesFile(serverDirector.resourceManager);

        if (displayAdminGUI) {
            ServerAdminGUI.create();
            return null; // we just display the admin GUI, we don't start the server.
        }

        // STEP 4 - We ask the ServerManager to get ready
        serverDirector.serverManager = new ServerManager(serverDirector.resourceManager);
        Debug.signal(Debug.NOTICE, null, "Server Manager created...");

        // STEP 5 - We ask the DataManager to load the worlds & client accounts
        serverDirector.dataManager = new DataManager(serverDirector.resourceManager);
        serverDirector.dataManager.init(serverDirector.serverProperties);

        // STEP 6 - Sound Library for alerts... (we only create a sound player)
        SoundLibrary.createSoundLibrary(serverDirector.serverProperties, null, serverDirector.resourceManager);

        // STEP 7 - Start of the GameServer, AccountServer & GatewayServer !
        Debug.signal(Debug.NOTICE, null, "Starting Game server, Account server & Gateway server...");

        serverDirector.serverManager.getGameServer().addServerListener(serverDirector);
        serverDirector.serverManager.start();

        // STEP 8 - We generate new keys for special characters
        serverDirector.updateKeys();

        // STEP 9 - Adding Shutdown Hook
        ServerDirector.shutdownThread = new Thread() {
            @Override
            public void run() {
                serverDirector.immediatePersistenceThreadStop = true;
                Debug.signal(Debug.CRITICAL, null, "Received VM Shutdown Signal.");

                // 1 - Lock servers...
                serverDirector.serverManager.lockServers();

                // 2 - We warn connected clients
                serverDirector.serverManager.sendWarningMessage("Your server has been stopped.\n" + "Try to reconnect in a few minutes.");

                // 3 - We close all remaining connections & save the data
                serverDirector.serverManager.closeAllConnections();
                serverDirector.dataManager.shutdown(true);
                serverDirector.serverManager.shutdown();
                SoundLibrary.clear();

                Debug.signal(Debug.CRITICAL, null, "Data Saved. Exiting.");
                Debug.flushPrintStream();
            }
        };

        Runtime.getRuntime().addShutdownHook(ServerDirector.shutdownThread);

        // STEP 10 - Everything is ok ! we enter the persistence loop
        Debug.signal(Debug.NOTICE, null, "Starting persistence thread...");

        Thread persistenceThread = new Thread(serverDirector);
        persistenceThread.start();

        // If we are in "daemon" mode the only way to stop the server is via signals.
        // Otherwise we wait 2s and wait for a key to be pressed to shutdown...
        if (!isDaemon) {
            Tools.waitTime(2000); // 2s
            Debug.signal(Debug.NOTICE, null, "Press <ENTER> if you want to shutdown this server.");

            try {
                System.in.read();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Debug.signal(Debug.NOTICE, null, "Leaving in 30s...");

            try {
                Runtime.getRuntime().removeShutdownHook(ServerDirector.shutdownThread);
            } catch (Exception e) {
                return null; // we couldn't remove the hook, it means the VM is already exiting
            }

            serverDirector.shutdown();
        }
        return ServerDirector.getCurrentServerDirector();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Run method for Thread. 
     */
    public void run() {
        do {
            // 1 - we wait the persistence period minus 2 minutes
            synchronized (this) {
                try {
                    wait(this.serverProperties.getIntegerProperty("init.persistencePeriod") * 1000 * 3600 - 1000 * 120);
                } catch (Exception e) {
                }
            }

            if (this.immediatePersistenceThreadStop)
                return;

            // 2 - Start persistence action
            persistenceAction(!mustStopThread());
        } while (!mustStopThread());

        // We shutdown the server
        this.dataManager.shutdown(false);
        this.serverManager.shutdown();

        Tools.waitTime(1000 * 10); // 10s
        Debug.signal(Debug.NOTICE, null, "Leaving Persistence Thread...");
        Debug.exit();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Saves the world & players.
     *
     * @param maintenance if true we advertise clients with a "entering maintenance mode"
     *        message. If false we send a "server is shuting down" message.
     */
    protected void persistenceAction(boolean maintenance) {

        // 1 - We warn all the clients that the server is going to enter
        // maintenance mode for 2-3 minutes, in 2 minutes.
        // If we are not in maintenance mode ( maintenance=false )
        // the message is a "server shuting down" message.
        this.serverManager.lockServers();

        if (maintenance) {
            Debug.signal(Debug.NOTICE, null, "Server will enter maintenance mode in 2 minutes...");
            this.serverManager.sendWarningMessage("Your server will enter maintenance mode in 2 minutes.\n" + "Please disconnect and reconnect in 5 minutes");
            Tools.waitTime(1000 * 120); // 2mn 
            Debug.signal(Debug.WARNING, null, "Server enters maintenance mode now... (" + Tools.getLexicalTime() + ")");
        } else {
            Debug.signal(Debug.NOTICE, null, "Sending warning messages to connected clients...");
            this.serverManager.sendWarningMessage("Your server is about to shutdown in 30s.\n" + "Please disconnect and reconnect later.");
            Tools.waitTime(1000 * 30); // 30s
            Debug.signal(Debug.NOTICE, null, "Saving world & player data... (" + Tools.getLexicalTime() + ")");
        }

        // 2 - We close all remaining connections
        //     and save the data
        this.serverManager.closeAllConnections();
        this.dataManager.save();

        // 3 - Leaving Maintenance Mode...
        if (maintenance) {
            this.updateKeys(); // we update the keys...
            this.serverManager.unlockServers();
            Debug.signal(Debug.NOTICE, null, "Leaving maintenance mode... (" + Tools.getLexicalTime() + ")");
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Must stop the persistence thread ?
     */
    private synchronized boolean mustStopThread() {
        return this.mustStop;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To shutdown the server director (stops the persistence thread ).
     */
    private synchronized void shutdown() {
        this.mustStop = true;
        notify();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the server ID of this server.
     *
     * @return serverID
     */
    public static int getServerID() {
        return ServerDirector.getCurrentServerDirector().serverProperties.getIntegerProperty("init.serverID");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the URL where are stored the remote server configs. This URL can also contain
     *  a news.html file to display some news.
     *
     * @return remoteServerConfigHomeURL
     */
    public static String getRemoteServerConfigHomeURL() {
        return ServerDirector.getCurrentServerDirector().remoteServersProperties.getProperty("info.remoteServerHomeURL");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get server properties.
     * @return server properties
     */
    public static Properties getServerProperties() {
        return ServerDirector.getCurrentServerDirector().serverProperties;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get remote servers properties.
     * @return remote servers properties
     */
    public static Properties getRemoteServersProperties() {
        return ServerDirector.getCurrentServerDirector().remoteServersProperties;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To change the keys used for special players & the bot's controlKey
     */
    protected void updateKeys() {

        int period = this.serverProperties.getIntegerProperty("init.persistencePeriod");
        int nbIteration = 24 / period;

        if (period < 24) {
            if ((this.updateKeysPeriod % nbIteration) == 0)
                this.updateKeysPeriod = 1;
            else {
                this.updateKeysPeriod++;
                return;
            }
        }

        this.serverProperties.setProperty("key.shaitan", Tools.keyGenerator(23, ServerDirector.getServerID() + 1));
        this.serverProperties.setProperty("key.amyrlin", Tools.keyGenerator(23, ServerDirector.getServerID() + 2));
        this.serverProperties.setProperty("key.chronicles", Tools.keyGenerator(23, ServerDirector.getServerID() + 3));
        this.serverProperties.setProperty("key.mhael", Tools.keyGenerator(23, ServerDirector.getServerID() + 4));
        Debug.signal(Debug.NOTICE, null, "Generated new keys for special characters...");

        this.serverProperties.setProperty("bots.controlKey", Tools.keyGenerator(23, ServerDirector.getServerID() + 5));
        Debug.signal(Debug.NOTICE, null, "Generated new control key for bots...");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called when the server network interface is down.
     * @param itf the network interface we tried which is NOT available.
     */
    public void serverInterfaceIsDown(String itf) {
        Debug.signal(Debug.NOTICE, null, "Network interface " + itf + " is down... we'll retry a connection in three minutes.");
        this.serverEnabled = false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called when the server network interface is Up.
     * @param ipAddress currently used IP address
     * @param stateChanged if true it means the interface has been re-created ( ip changed or
     *        serverSocket has just been created ). If false it means that the interface is Up
     *        and its state has not changed (since last check).
     */
    public void serverInterfaceIsUp(String ipAddress, boolean stateChanged) {
        if (!stateChanged) {
            if (!this.serverEnabled) {
                this.serverEnabled = true;
                Debug.signal(Debug.NOTICE, null, "Network interface " + ipAddress + " is up... IP has not changed.");
            }

            return;
        }

        Debug.signal(Debug.NOTICE, null, "Network interface " + ipAddress + " is up...");

        // We save the new IP
        if (!this.serverManager.getServerConfigManager().updateServerConfig(null, ipAddress, this.serverManager.getServerConfig())) {
            Debug.signal(Debug.CRITICAL, this, "Failed to save new IP (" + ipAddress + ") to the local server-" + ServerDirector.getServerID() + ".cfg.adr file ! Please perform a manual update!" + "Your server's IP has changed !");
            return;
        } else
            Debug.signal(Debug.NOTICE, null, "IP saved to server-" + ServerDirector.getServerID() + ".cfg.adr file");

        if (ServerDirector.getServerID() == 0)
            return; // local server

        // Manual update ?
        if (!this.serverProperties.getBooleanProperty("init.automaticUpdate")) {
            String publishAddress = this.serverProperties.getProperty("init.publishAddress");

            if (publishAddress == null || publishAddress.length() == 0)
                Debug.signal(Debug.NOTICE, null, "Your server's IP has changed, you should do a manual update on the wotlas web server!" + "Your server will be unreachable until then.");
            else
                Debug.signal(Debug.NOTICE, null, "Your server's local IP has changed, you should update your NAT table if you are using" + "it. Your server will be unreachable until then.");
            return;
        }

        // Automatic Update via a Thread (we don't want to make our server wait)
        Thread updateThread = new Thread() {
            @Override
            public void run() {
                // We get the login
                String login = ServerDirector.this.remoteServersProperties.getProperty("transfer.serverHomeLogin");

                // We load the script we are about to modify with login & password.
                String cmd = ServerDirector.this.resourceManager.getExternalTransferScript();
                File wDir = new File(ServerDirector.this.resourceManager.getExternalScriptsDir());
                String script = ServerDirector.this.resourceManager.loadText(cmd);
                boolean editScript = true;

                if (script.indexOf("SET WEB_LOGIN") < 0 || script.indexOf("SET WEB_PASSWORD") < 0)
                    editScript = false; // the script doesn't use any login & password, no need to edit it

                // Did the user already entered the password ?
                if (ServerDirector.this.password == null && editScript) {
                    ALoginDialog dialog = new ALoginDialog(new Frame(), "File Transfer Login (asked once):", login, ServerDirector.this.resourceManager);

                    if (dialog.okWasClicked()) {
                        login = dialog.getLogin();
                        ServerDirector.this.remoteServersProperties.setProperty("transfer.serverHomeLogin", login);
                        ServerDirector.this.password = dialog.getPassword();
                    } else {
                        Debug.signal(Debug.ERROR, null, "No password set. Transfer aborted");
                        return; // no transfer
                    }
                }

                // Runtime... we execute the transfert command
                int result = 1;

                try {
                    cmd = new File(cmd).getCanonicalPath();
                } catch (Exception ex) {
                    Debug.signal(Debug.ERROR, this, "Failed to find scripts ! Err: " + ex + " Cmd:" + cmd);
                    return;
                }

                // We replace the login & password values
                if (script == null) {
                    Debug.signal(Debug.ERROR, this, "Failed to load " + script + " !");
                    return;
                } else if (editScript) {
                    script = FileTools.updateProperty("SET WEB_LOGIN", login, script);
                    script = FileTools.updateProperty("SET WEB_PASSWORD", ServerDirector.this.password, script);

                    if (!ServerDirector.this.resourceManager.saveText(cmd, script)) {
                        Debug.signal(Debug.ERROR, this, "Failed to save " + script + " !");
                        return;
                    }
                }

                // We run the script...
                Debug.signal(Debug.NOTICE, null, "Launching transfer script...");

                try {
                    Process pr = Runtime.getRuntime().exec(cmd, null, wDir);
                    result = pr.waitFor();
                } catch (Exception ex) {
                    Debug.signal(Debug.ERROR, this, "Command Line Failed : " + ex.getMessage());
                    return;
                }

                Debug.signal(Debug.NOTICE, null, "Transfer script ended.");

                if (editScript) {
                    // We clean what we have modified in the script
                    script = FileTools.updateProperty("SET WEB_LOGIN", "You will be prompted for your login.", script);
                    script = FileTools.updateProperty("SET WEB_PASSWORD", "You will be prompted for your passsword.", script);

                    if (!ServerDirector.this.resourceManager.saveText(cmd, script)) {
                        Debug.signal(Debug.ERROR, this, "Failed to save " + script + " to clean entries !");
                        return;
                    }
                }
            }
        };

        // We start the thread that will take care of the update
        updateThread.start();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    private static ServerDirector getCurrentServerDirector() {
        return ServerDirector._serverDirector;
    }

    /** To get our resource manager.
     *  @return our resource manager.
     */
    public static ResourceManager getResourceManager() {
        return ServerDirector.getCurrentServerDirector().resourceManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our server manager.
     *  @return our server manager.
     */
    public static ServerManager getServerManager() {
        return ServerDirector.getCurrentServerDirector().serverManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our data manager.
     *  @return our data manager.
     */
    public static DataManager getDataManager() {
        return ServerDirector.getCurrentServerDirector().dataManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
