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

import wotlas.common.ErrorCodeList;
import wotlas.libs.net.NetConfig;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetServer;
import wotlas.server.bots.BotPlayer;
import wotlas.utils.Debug;
import wotlas.utils.WotlasGameDefinition;

/** Wotlas Game Server. Its role is to wait client and connect them to the
 *  game. A client must have previously created a GameAccount with the AccountServer.<br>
 *
 *  This server supposes there is a PersistenceManager & DataManager
 *  already created.
 *
 * @author Aldiss
 */

public class GameServer extends NetServer implements ErrorCodeList {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor (see wotlas.libs.net.NetServer for details)
     *
     *  @param serverInterface the host interface to bind to. Example: wotlas.tower.org
     *  @param port port on which the server listens to clients.
     *  @param msgSubInterfaces a list of sub-interfaces where we can find NetMsgBehaviour Classes implemeting them.
     *  @param nbMaxSockets maximum number of sockets that can be opened on this server
     */
    public GameServer(NetConfig netCfg, Class msgSubInterfaces[], int nbMaxSockets, WotlasGameDefinition wgd) {
        super(netCfg, msgSubInterfaces, wgd);
        setMaximumOpenedSockets(nbMaxSockets);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called automatically when a new client establishes a connection
     *  with this server ( the client sends a ClientRegisterMessage ).
     *
     * @param connection a previously created connection for this connection.
     * @param key a string given by the client to identify itself. The key structure
     *        is the following "accountName:password". See wotlas.server.GameAccount
     *        for the accountName structure.
     */
    @Override
    public void accessControl(NetConnection connection, String key) {

        // key sanity check
        if (key.indexOf(':') <= 4 || key.endsWith(":")) {
            Debug.signal(Debug.NOTICE, this, "A client tried to connect with a bad key format.");
            refuseClient(connection, ErrorCodeList.ERR_WRONG_KEY, "You are trying to connect on the wrong server !!!");
            return;
        }

        // we retrieve the key data "accountName:password"
        String accountName = key.substring(0, key.indexOf(':'));
        String password = key.substring(key.indexOf(':') + 1, key.length());

        // does this client exists ?
        AccountManager manager = ServerDirector.getDataManager().getAccountManager();
        GameAccount account = manager.getAccount(accountName);

        if (account == null) {
            Debug.signal(Debug.NOTICE, this, "A client tried to connect on a non-existent account.");
            refuseClient(connection, ErrorCodeList.ERR_UNKNOWN_ACCOUNT, "This account does not exist on this server");
            return;
        }

        if (account.getPlayer().isConnectedToGame() && !(account.getPlayer() instanceof BotPlayer)) {
            Debug.signal(Debug.ERROR, this, accountName + " tried to connect twice to the game server.");
            refuseClient(connection, ErrorCodeList.ERR_ALREADY_CONNECTED, "Someone is already connected on this account !");
            return;
        }

        // Password Crack Detection ( dictionnary attack )
        if (account.tooMuchBadPasswordEntered()) {
            Debug.signal(Debug.WARNING, this, accountName + " has entered 3 bad passwords! account locked for 30s");
            refuseClient(connection, ErrorCodeList.ERR_BAD_PASSWORD, "Sorry, you entered 3 bad passwords ! your account is locked for 30s.");
            return;
        }

        // The account exists... but do we have the right password ?
        if (account.isRightPassword(password)) {
            // account alive ?
            if (account.getIsDeadAccount()) {
                refuseClient(connection, ErrorCodeList.ERR_DEAD_ACCOUNT, "Sorry, your character has been killed !");
                return;
            }

            // ok, client accepted...
            account.setLastConnectionTimeNow();

            // we set his message context to his player...
            connection.setContext(account.getPlayer());
            connection.addConnectionListener(account.getPlayer());

            // We will send back ping messages if we receive them
            connection.sendBackPingMessages(true);

            // welcome on board...
            acceptClient(connection);
            Debug.signal(Debug.NOTICE, null, accountName + " entered the game...");
        } else {
            Debug.signal(Debug.NOTICE, this, accountName + " entered a bad password");
            refuseClient(connection, ErrorCodeList.ERR_BAD_PASSWORD, "Wrong password !");
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
