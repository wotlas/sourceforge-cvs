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

import wotlas.common.PropertiesConfigFile;
import wotlas.common.ResourceManager;
import wotlas.utils.Debug;

/** Represents the 'server.cfg' properties file. We check that its content is valid.
 *
 * @author Aldiss
 * @see wotlas.server.ServerDirector
 */

public class ServerPropertiesFile extends PropertiesConfigFile {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Server Properties Config File Name
     */
    public final static String SERVER_CONFIG = "server.cfg";

    /** Server Properties Config File Name for the standalone server.
     */
    public final static String SERVER_STANDALONE_CONFIG = "server-standalone.cfg";

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with our resource manager.
     *
     * @param rManager our resource manager
     */
    public ServerPropertiesFile(ResourceManager rManager, String serverCfgFile) {
        super(rManager, serverCfgFile);

        if (!isValidInteger("init.persistencePeriod")) {
            Debug.signal(Debug.FAILURE, this, "The given persistence period is not a valid integer ! (in " + ServerPropertiesFile.SERVER_CONFIG + ")");
            Debug.exit();
        }

        if (!isValid("init.serverItf")) {
            Debug.signal(Debug.FAILURE, this, "init.serverItf property not set in " + ServerPropertiesFile.SERVER_CONFIG + " !");
            Debug.exit();
        }

        if (!isValidBoolean("init.automaticUpdate")) {
            Debug.signal(Debug.FAILURE, this, "init.automaticUpdate boolean property not set in " + ServerPropertiesFile.SERVER_CONFIG + " !");
            Debug.exit();
        }

        if (!isValidInteger("init.serverID")) {
            Debug.signal(Debug.FAILURE, this, "The given serverID is not a valid integer ! (in " + ServerPropertiesFile.SERVER_CONFIG + ")");
            Debug.exit();
        }

        if (!isValid("init.botChatServiceClass")) {
            Debug.signal(Debug.FAILURE, this, "init.botChatServiceClass property not set in " + ServerPropertiesFile.SERVER_CONFIG + " !");
            Debug.exit();
        }

        Debug.signal(Debug.NOTICE, null, "Server properties loaded successfully :");
        Debug.signal(Debug.NOTICE, null, "Server ID set to   : " + getProperty("init.serverID"));
        Debug.signal(Debug.NOTICE, null, "Persistence period : " + getProperty("init.persistencePeriod") + " hours");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
