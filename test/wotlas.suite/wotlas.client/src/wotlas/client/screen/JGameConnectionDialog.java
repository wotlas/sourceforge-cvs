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
package wotlas.client.screen;

import java.awt.Frame;
import wotlas.client.gui.JConnectionDialog;
import wotlas.common.message.account.WishClientAccountNetMsgBehaviour;
import wotlas.common.message.chat.WishClientChatNetMsgBehaviour;
import wotlas.common.message.description.WishClientDescriptionNetMsgBehaviour;
import wotlas.common.message.movement.WishClientMovementNetMsgBehaviour;
import wotlas.libs.net.NetConfig;
import wotlas.utils.WotlasGameDefinition;

/** A small utility to connect to an account server using a JDialog.
 * <pre>
 *  Example : here is the case of a player of login "masterBob" whose secret password is "hulo"
 *            and player IDs are : 1 (server part) and 22 (client part)
 *
 *   NetConnection myNetConnection;
 *
 *   JGameConnectionDialog jconnect = new JGameConnectionDialog( frame, "myServer", 26000
 *                                                  "masterBob", "hulo", 1, 22, myDataManager );
 *   if( jconnect.hasSucceeded() ) 
 *       myNetConnection = jconnect.getConnection();
 *
 * </pre>
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetClient
 */
public class JGameConnectionDialog extends JConnectionDialog {
    /*------------------------------------------------------------------------------------*/

    /** Constructor. Displays the JDialog and immediately tries to connect to the specified
     *  server. It displays eventual error messages in pop-ups.
     *  The detail of the parameters is the following :
     * 
     * @param frame frame owner of this JDialog
     * @param server server name (DNS or IP address)
     * @param port server port
     * @param serverID Id of the server we want to join
     * @param login client login
     * @param password client password
     * @param localClientID ID given by the account server when the client created his player
     * @param originalServerID ID given by the account server when the client created his player
     * @param context context to set to messages ( see NetConnection ).
     */
    public JGameConnectionDialog(Frame frame, NetConfig netCfg, String login, String password, int localClientID, int originalServerID,
            Object context, WotlasGameDefinition wgd) {
        super(frame, netCfg, login + "-" + originalServerID + "-" + localClientID + ":" + password, context, wgd);
    }

    /*------------------------------------------------------------------------------------*/
    /** To retrieve a list of the NetMessage packages to use with this server.
     */
    @Override
    protected Class[] getMsgSubInterfaces() {
        //String list[] = null; // no packages for now
        Class list[] = { WishClientAccountNetMsgBehaviour.class, WishClientDescriptionNetMsgBehaviour.class, WishClientMovementNetMsgBehaviour.class, WishClientChatNetMsgBehaviour.class };

        return list;
    }

    /*------------------------------------------------------------------------------------*/
}
