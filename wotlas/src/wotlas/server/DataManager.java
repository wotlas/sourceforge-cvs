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

import java.util.Iterator;
import java.util.Properties;
import wotlas.common.ResourceManager;
import wotlas.common.WorldManager;
import wotlas.server.bots.BotManager;
import wotlas.server.chat.ChatCommandProcessor;
import wotlas.server.router.ServerMessageRouterFactory;
import wotlas.utils.Debug;

/** A DataManager manages Game Data. It possesses a WorldManager & AccountManager.
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 */

public class DataManager {

    /*------------------------------------------------------------------------------------*/

    /** Our World Manager
     */
    private WorldManager worldManager;

    /** Our Account Manager
     */
    private AccountManager accountManager;

    /** Our chat command processor
     */
    private ChatCommandProcessor chatCommandProcessor;

    /** Our BotManager
     */
    private BotManager botManager;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. Loads the world data and player accounts.
     */
    public DataManager(ResourceManager rManager) {

        // 1 - We load all the WotlasObjects, Knowledges, etc...
        /** Not for wotlas release 1 **/

        // 2 - We create a WorldManager. Worlds data is automatically loaded.
        this.worldManager = new WorldManager(rManager, false);
        this.worldManager.initMessageRouting(new ServerMessageRouterFactory());

        // 3 - We create an AccountManager. Player Accounts are not loaded yet.
        this.accountManager = new AccountManager(rManager);

        // 4 - Creation of the Chat Command Processor.
        this.chatCommandProcessor = new ChatCommandProcessor();

        // 5 - Creation of the Bot Manager
        this.botManager = new BotManager(this.accountManager);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Final init. We add our players to the world when everything's ready.
     */
    public void init(Properties serverProperties) {

        // 1 - Load accounts
        this.accountManager.init();

        // 2 - We initialize the WorldManager with the Players ( Players are
        // located on Maps, it's our game organization... ).
        Iterator it = this.accountManager.getIterator();

        while (it.hasNext()) {
            GameAccount account = (GameAccount) it.next();
            if (!account.getIsDeadAccount())
                this.worldManager.addPlayerToUniverse(account.getPlayer());
        }

        // 3 - We initialize the player objects that we just placed in the world.
        it = this.accountManager.getIterator();

        while (it.hasNext())
            ((GameAccount) it.next()).getPlayer().init();

        // 4 - Init of the Chat Command Processor.
        this.chatCommandProcessor.init();

        // 5 - We initialize our bot manager
        if (!this.botManager.init(serverProperties)) {
            Debug.signal(Debug.CRITICAL, this, "Failed to init Bot Manager...");
            Debug.exit();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To shutdown this datamanager.
     * @param saveData do we have to save all the persistent data before shutting down ?
     */
    public synchronized void shutdown(boolean saveData) {
        // 1 - save data ?
        if (saveData)
            save();

        // 2 - Clean - up
        this.botManager.shutdown();
        this.botManager = null;
        this.worldManager = null;

        this.accountManager.clear();
        this.accountManager = null;

        this.chatCommandProcessor = null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To save all the persistent data of this datamanager.
     */
    public void save() {
        // 1 - We save the accounts
        synchronized (this.accountManager) {
            Iterator it = this.accountManager.getIterator();

            while (it.hasNext())
                this.accountManager.saveAccount((GameAccount) it.next());
        }

        Debug.signal(Debug.NOTICE, null, "Saved player data...");

        // 2 - We save the world data
        if (!this.worldManager.saveUniverse(false))
            Debug.signal(Debug.WARNING, null, "Failed to save world data...");
        else
            Debug.signal(Debug.NOTICE, null, "Saved world data...");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the world manager.
     *
     * @return the world manager.
     */
    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the account manager.
     *
     * @return the account manager.
     */
    public AccountManager getAccountManager() {
        return this.accountManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the chat command processor.
     *
     * @return the chat command processor.
     */
    public ChatCommandProcessor getChatCommandProcessor() {
        return this.chatCommandProcessor;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the bot manager.
     *
     * @return the bot manager.
     */
    public BotManager getBotManager() {
        return this.botManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
