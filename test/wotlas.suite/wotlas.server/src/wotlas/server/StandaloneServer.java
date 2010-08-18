/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2009 WOTLAS Team
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

import wotlas.libs.net.NetConfig;
import wotlas.libs.net.WishNetStandaloneServer;
import wotlas.utils.WotlasDefaultGameDefinition;
import wotlas.utils.WotlasGameDefinition;

/**
 * @author SleepingOwl
 *
 */
public class StandaloneServer implements WishNetStandaloneServer {

    private String basePath;

    private WotlasGameDefinition wgd;

    private ServerDirector currentServerDirector;

    /* 
     * @see wotlas.libs.net.WishNetStandaloneServer#run()
     */
    public void run() {
        // TODO Auto-generated method stub
        System.err.println("Launching standalone server");
        boolean isDaemon = true;
        boolean displayAdminGUI = false;
        boolean shutdownJreOnClose = false;
        String serverCfgFile = ServerPropertiesFile.SERVER_STANDALONE_CONFIG;
        if (this.currentServerDirector == null)
            this.currentServerDirector = ServerDirector.runServer(isDaemon, displayAdminGUI, this.basePath, this.wgd, shutdownJreOnClose, serverCfgFile);
        else
            System.err.println("Standalone server already launched :" + this.currentServerDirector);

    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.WishNetStandaloneServer#init(wotlas.libs.net.NetConfig, wotlas.utils.WotlasGameDefinition)
     */
    public void init(NetConfig netCfg, WotlasGameDefinition gameDefinition) {
        // TODO Auto-generated method stub

        // TODO STEP 0 - Define the game context.
        this.basePath = netCfg.getStandaloneBasePath();
        this.wgd = new WotlasDefaultGameDefinition(WotlasGameDefinition.ID_WOTLAS_SERVER, new String[] { "client", "Client" }, new String[] { "server", "Server" });
    }

}
