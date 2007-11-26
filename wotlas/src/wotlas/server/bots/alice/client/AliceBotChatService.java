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

package wotlas.server.bots.alice.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.Timer;
import wotlas.common.Player;
import wotlas.common.PropertiesConfigFile;
import wotlas.libs.net.NetClient;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetConnectionListener;
import wotlas.server.PlayerImpl;
import wotlas.server.ServerDirector;
import wotlas.server.bots.BotChatService;
import wotlas.server.bots.BotPlayer;
import wotlas.server.bots.alice.AliceWotlasMessage;
import wotlas.utils.Debug;

/** A BotChatService which connects to an AliceBot server using the AliceWOTLAS
 *  AliceChatListener.
 *
 * @author Aldiss
 * @see wotlas.server.bots.BotPlayer
 */

public class AliceBotChatService implements BotChatService, ActionListener, NetConnectionListener {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Period between two connection attempts. (beware it's an int !)
     */
    public static final int CONNECT_PERIOD = 1000 * 60 * 10; // 10 minutes

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Alice Host Name
     */
    protected String aliceHost;

    /** Alice Host Port
     */
    protected int alicePort;

    /** NetConnection that represents the connection with the Alice server
     */
    protected NetConnection connection;

    /** Timer to retry a connection if the previous attempt failed.
     */
    protected Timer timer;

    /** Connection Lock
     */
    protected byte connectionLock[] = new byte[0];

    /** To tell that we are shutting down...
     */
    protected boolean shutdown;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Empty constructor for dynamic construction.
     */
    public AliceBotChatService() {
        this.alicePort = -1;
        this.shutdown = false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this chat service.
     *  @param serverProperties server properties giving some information for this service
     *  @return true if the initialization succeeded, false if it failed
     */
    public boolean init(Properties serverProperties) {

        PropertiesConfigFile serverProps = (PropertiesConfigFile) serverProperties;
        Debug.signal(Debug.NOTICE, null, "Bot Chat Service used : alicebot");

        // 1 - We retrieve the alicebot server's address & port
        if (!serverProps.isValid("bots.aliceHost")) {
            Debug.signal(Debug.FAILURE, this, "No alice host property set !");
            return false;
        }

        if (!serverProps.isValidInteger("bots.alicePort")) {
            Debug.signal(Debug.FAILURE, this, "The given alice port is not a valid integer !");
            return false;
        }

        this.aliceHost = serverProps.getProperty("bots.aliceHost");
        this.alicePort = serverProps.getIntegerProperty("bots.alicePort");

        this.shutdown = false;
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To initialize the connection with the remote chat service. The first connect()
     *  is called just after the init() call. It's your job to eventually a thread to manage
     *  the state of the connection.<br>
     *
     *  When the connection succeeds or fails you should refresh the bots state :<br>
     *
     *     ServerDirector.getDataManager().getBotManager().refreshBotState();
     *
     *  @return true if the connection was successfully established, false otherwise
     */
    public boolean connect() {

        if (this.alicePort == -1 || this.aliceHost == null || this.shutdown == true)
            return false; // no alice info

        // 1 - We try a connection...
        NetClient client = new NetClient();

        String messagePackages[] = { "wotlas.server.bots.alice.client" };

        this.connection = client.connectToServer(this.aliceHost, this.alicePort, "alicebot-access:" + ServerDirector.getServerID(), // access key for that server (password)
        ServerDirector.getDataManager().getAccountManager(), // context for NetMessageBehaviour
        messagePackages); // Message packages to load

        synchronized (this.connectionLock) {
            if (this.connection == null) {
                // We failed to connect... we create a timer to retry later
                Debug.signal(Debug.WARNING, null, "Bot Chat Service connect : failed to reach AliceBot server. We'll retry later.\n" + client.getErrorMessage());

                if (this.timer == null) {
                    // we create a timer to retry a connection attempt in CONNECT_PERIOD
                    this.timer = new Timer(AliceBotChatService.CONNECT_PERIOD, this);
                    this.timer.start();
                }

                return false;
            } else {
                this.connection.addConnectionListener(this); // WE will monitor the state of the connection

                // Connection succeeded !
                if (this.timer != null) {
                    this.timer.stop();
                    this.timer = null;
                }
            }
        }

        Debug.signal(Debug.NOTICE, null, "Bot Chat Service connect : alice server reached !");

        // 3 - We refresh the bots state
        ServerDirector.getDataManager().getBotManager().refreshBotState();
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To shut down the connection with the remote chat service. When this method is called
     *  it means the system is about to shutdown. You should free resources and advertise
     *  the shut.
     *  @return true if the connection was successfully shutdown, false otherwise
     */
    public boolean shutdown() {
        Debug.signal(Debug.NOTICE, null, "Bot Chat Service shutting down...");

        synchronized (this.connectionLock) {
            this.shutdown = true;

            if (this.connection != null) {
                this.connection.close();
                this.connection = null;
            }

            if (this.timer != null) {
                this.timer.stop();
                this.timer = null;
            }
        }

        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the state of this chat service (usually represents the connection state).
     *  @return true if this BotChatService is available, false if it's not working at
     *          the moment.
     */
    public boolean isAvailable() {
        synchronized (this.connectionLock) {
            return this.connection != null;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Opens a chat session for the given player with the given bot. This method is
     *  called each time a player arrives in the bot's area.
     *
     *  @param bot bot who's the target of the session.
     *  @param player player arriving near our bot.
     *  @return true if the session was opened successfully
     */
    public boolean openSession(BotPlayer bot, Player player) {
        // nothing special to do, alicebot manages its session itself...
        // We just send a hi! from the client
        synchronized (this.connectionLock) {
            if (this.connection == null)
                return false;

            //  String message = "Hi, my name is "+((PlayerImpl) player).getFullPlayerName( (PlayerImpl) bot );
            String message = "Hi!";

            this.connection.queueMessage(new AliceWotlasMessage(player.getPrimaryKey(), ((PlayerImpl) bot).getPrimaryKey(), message, ServerDirector.getServerID()));
        }

        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Player 'fromPlayer' sent a 'message' to 'toBot'. We ask to this service the
     *  answer 'toBot' must send to 'fromPlayer'. This method SHOULD BE ASYNCHRONOUS.
     *  I.E. we ask for an answer and return. Later, when the result is received we
     *  call back the bot's sendChatAnswer method.
     *
     *  @param message message sent by 'fromPlayer'
     *  @param fromPlayer the player that sent the chat 'message'.
     *  @param toBot the bot which is supposed to answer the 'fromPlayer''s message.
     */
    public void askForAnswer(String message, Player fromPlayer, BotPlayer toBot) {

        // We send the request to the Alice Server
        synchronized (this.connectionLock) {
            if (this.connection == null)
                return;

            this.connection.queueMessage(new AliceWotlasMessage(fromPlayer.getPrimaryKey(), ((PlayerImpl) toBot).getPrimaryKey(), message, ServerDirector.getServerID()));
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Closes a chat session for the given player with the given bot. This method is
     *  called each time a player leaves the bot's area.
     *
     *  @param bot bot who's the target of the session.
     *  @param player player leaving our bot.
     *  @return true if the session was closed successfully
     */
    public boolean closeSession(BotPlayer bot, Player player) {
        // nothing to do, alicebot manages its session itself...
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called when a new network connection is created on this player.
     *
     * @param connection the NetConnection object associated to this connection.
     */
    public void connectionCreated(NetConnection connection) {
        // nothing to do...
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called when the network connection of the client is no longer
     * of this world.
     *
     * @param connection the NetConnection object associated to this connection.
     */
    public void connectionClosed(NetConnection connection) {

        // 1 - no more messages will be sent...
        synchronized (this.connectionLock) {
            connection = null;

            if (!this.shutdown)
                ServerDirector.getDataManager().getBotManager().refreshBotState();

            if (this.timer == null) {
                // we create a timer to retry a connection attempt in CONNECT_PERIOD
                this.timer = new Timer(AliceBotChatService.CONNECT_PERIOD, this);
                this.timer.start();
            }
        }

        Debug.signal(Debug.NOTICE, null, "Bot Chat Service : connection closed with alice server.");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Timer Event interception
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != this.timer)
            return;

        // New connection attempt
        connect();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
