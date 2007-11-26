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

package wotlas.common;

import wotlas.utils.Debug;

/** Represents the 'remote-servers.cfg' properties file. We check that its content is valid.
 *
 * @author Aldiss
 * @see wotlas.server.ServerDirector
 * @see wotlas.client.ClientDirector
 */

public class RemoteServersPropertiesFile extends PropertiesConfigFile {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Remote Servers Properties Config File Name
     */
    public final static String REMOTE_SERVERS_CONFIG = "remote-servers.cfg";

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with our resource manager.
     *
     * @param rManager our resource manager
     */
    public RemoteServersPropertiesFile(ResourceManager rManager) {
        super(rManager, RemoteServersPropertiesFile.REMOTE_SERVERS_CONFIG);

        if (!isValid("info.remoteServerHomeURL")) {
            Debug.signal(Debug.FAILURE, this, "info.remoteServerHomeURL property not set in " + RemoteServersPropertiesFile.REMOTE_SERVERS_CONFIG + " !");
            Debug.exit();
        }

        if (!getProperty("info.remoteServerHomeURL").endsWith("/"))
            setProperty("info.remoteServerHomeURL", getProperty("info.remoteServerHomeURL") + "/");

        Debug.signal(Debug.NOTICE, null, "Server home URL    : " + getProperty("info.remoteServerHomeURL"));
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
