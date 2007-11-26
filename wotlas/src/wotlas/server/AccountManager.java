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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import wotlas.common.ResourceManager;
import wotlas.common.WorldManager;
import wotlas.common.objects.inventories.Inventory;
import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.utils.Tools;

/** The AccountManager posseses all the client's game accounts. Note that all the
 *  methods of the AccountManager are not synchronized... so use the AccountManager with
 *  care ! It should only be used as server start-up or for daily persistence save in
 *  maintenance mode.
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 */

public class AccountManager {

    /*------------------------------------------------------------------------------------*/

    /** Format of the account file names.
     */
    public final static String CLIENT_PROFILE = "profile.cfg";
    public final static String PLAYER_PREFIX = "player-save-";
    public final static String PLAYER_SUFFIX = ".cfg";

    /** Inventory file name format ( I changed the suffix to be sure to don't mess with the player saves )
     */
    public final static String INVENTORY_PREFIX = "inventory-save-";
    public final static String INVENTORY_SUFFIX = ".ifg";

    /** Maximum number of save in a client account. If this number is reached we delete
     *  the oldest entry.
     */
    public final static int MAX_NUMBER_OF_SAVE = 3;

    /*------------------------------------------------------------------------------------*/

    /** Client Login ( equals to the client's directory name )
     */
    private HashMap accounts;

    /** Our resource Manager
     */
    private ResourceManager rManager;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. Attempts to load the client accounts.
     */
    public AccountManager(ResourceManager rManager) {
        this.rManager = rManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init & load the client Accounts.
     */
    public void init() {
        int nbAccounts = 0, nbAccountsLoaded = 0;

        // Call to the PersistenceManager to load the accounts from the dataBase.
        GameAccount account_list[] = loadAccounts();

        if (account_list != null)
            nbAccounts = account_list.length;

        // HashMap init. ( initial size is 1.5*Nb Accounts +10 )
        // The HashMap Load Factor is 75%. The size doubles after that.
        this.accounts = new HashMap((int) (1.5 * nbAccounts + 10));

        // we fill the hash-map...
        for (int i = 0; i < nbAccounts; i++)
            if (account_list[i] != null) {

                if (account_list[i].getPlayer() == null) {
                    Debug.signal(Debug.ERROR, this, "Failed to load account " + account_list[i].getPrimaryKey() + "! No player data found !");
                    continue;
                }

                this.accounts.put(account_list[i].getAccountName(), account_list[i]);
                nbAccountsLoaded++;
            }

        Debug.signal(Debug.NOTICE, null, "AccountManager loaded " + nbAccountsLoaded + " accounts.");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Erases all the possessed data.
     */
    public void clear() {
        this.accounts.clear();
        this.accounts = null;
        this.rManager = null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To iterate over the game accounts.
     *
     * @return an iterator that review all the GameAccounts.
     */
    public Iterator getIterator() {
        return this.accounts.values().iterator();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To create a new acount. The account must have been fully initialized ( don't forget
     *  the setPlayer() ! even if it's a transient field we use it to save Player data in
     *  a separate file ).
     *
     * @param account a game account
     * @return true means success, false usually means that the account name already exists.
     */
    public synchronized boolean createAccount(GameAccount account) {
        if (!createAccountFiles(account))
            return false;

        // we insert the account in our table
        this.accounts.put(account.getAccountName(), account);
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To check if an account exists.
     *
     * @param accountName account name.
     * @return true if the account exists, false otherwise.
     */
    public synchronized boolean checkAccountName(String accountName) {
        return this.accounts.containsKey(accountName);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To add a new account (note that we don't check if it already exists).
     *
     * @param account account to add.
     */
    public synchronized void addAccount(GameAccount account) {
        this.accounts.put(account.getAccountName(), account);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To remove an account (note that we don't check if it already exists nor we delete
     *  associated resources). Use with care !
     *
     * @param accountName name of the account to remove
     */
    public synchronized void removeAccount(String accountName) {
        this.accounts.remove(accountName);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get an account from its name.
     *
     * @param accountName the account name...
     * @return the wanted account, or null if the account doesnot exist...
     */
    public synchronized GameAccount getAccount(String accountName) {
        return (GameAccount) this.accounts.get(accountName);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To delete an account from its name.
     *
     * @param accountName the account name...
     * @param closeIfConnected if true we close the player's connection if he is connected.
     * @return true if the account was deleted.
     */
    public synchronized boolean deleteAccount(String accountName, boolean closeIfConnected) {
        WorldManager wManager = ServerDirector.getDataManager().getWorldManager();

        // We get the account
        GameAccount account = (GameAccount) this.accounts.get(accountName);

        if (account == null) {
            Debug.signal(Debug.ERROR, this, "Account " + accountName + " not found");
            return false;
        }

        // we remove the account hashmap entry
        this.accounts.remove(accountName);

        if (closeIfConnected && account.getPlayer().isConnectedToGame()) {
            account.getPlayer().closeConnection();
            Debug.signal(Debug.WARNING, this, "Client connection was closed during the delete request.");
        }

        // we remove the character from the game...
        wManager.removePlayerFromUniverse(account.getPlayer());

        // we delete the files
        if (!deleteAccountFiles(accountName)) {
            Debug.signal(Debug.ERROR, this, "Failed to delete Account files: " + accountName);
            return true; // account erased but some files remain !
        }

        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To change the status of an account : a 'dead' account means its character has been
     *  killed in the game.
     *
     * @param account the account...
     * @return true if the account was deleted.
     */
    public synchronized boolean changeToDeadAccount(GameAccount account) {
        WorldManager wManager = ServerDirector.getDataManager().getWorldManager();

        // We get the account           
        if (account == null || account.getIsDeadAccount())
            return false;

        if (account.getPlayer().isConnectedToGame())
            account.getPlayer().closeConnection();

        // we remove the character from the game...
        account.setIsDeadAccount(true);
        wManager.removePlayerFromUniverse(account.getPlayer());
        Debug.signal(Debug.WARNING, this, "Account " + account.getAccountName() + " is dead...");
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a list of online players.
     *
     * @return a list of online players
     */
    public synchronized HashMap getOnlinePlayers() {
        // HashMap init. ( initial size is Nb Accounts/2 )
        HashMap onlinePlayers = new HashMap(this.accounts.size() / 2);

        Iterator it = this.accounts.values().iterator();
        PlayerImpl player;

        while (it.hasNext()) {
            player = ((GameAccount) it.next()).getPlayer();
            if (player.isConnectedToGame()) {
                onlinePlayers.put(player.getPrimaryKey(), player);
            }
        }

        return onlinePlayers;
    }

    /*------------------------------------------------------------------------------------*/

    /** To load all the client Accounts. Warning: the returned array can have null entries.
     *
     * @return all the client accounts found in the databasePath+"/"+ACCOUNTS_HOME
     */
    public GameAccount[] loadAccounts() {

        String accountHome = this.rManager.getExternalPlayersHomeDir();
        String accountList[] = this.rManager.listDirectories(accountHome);

        // no accounts ?
        if (accountList == null || accountList.length == 0) {
            Debug.signal(Debug.WARNING, this, "No player accounts found in: " + accountHome);
            return null;
        }

        // We load the different accounts...
        GameAccount accounts[] = new GameAccount[accountList.length];

        for (int i = 0; i < accountList.length; i++) {

            if (accountList[i].indexOf('-') < 0)
                continue;

            // we load the client's profile
            accounts[i] = (GameAccount) this.rManager.loadObject(accountList[i] + AccountManager.CLIENT_PROFILE);

            if (accounts[i] == null) {
                Debug.signal(Debug.ERROR, this, "Failed to load " + accountList[i]);
                continue;
            }

            // we now search for the latest saved player file.
            String latest = FileTools.findSave(this.rManager.listFiles(accountList[i], AccountManager.PLAYER_SUFFIX), AccountManager.PLAYER_PREFIX, AccountManager.PLAYER_SUFFIX, true);

            // have we found the latest saved file ?
            if (latest == null) {
                // nope, then it's an invalid account, we ignore it...
                Debug.signal(Debug.ERROR, this, "Failed to load account: " + accountList[i]);
                accounts[i] = null;
                continue;
            }

            PlayerImpl player = (PlayerImpl) this.rManager.loadObject(latest);

            if (player != null)
                accounts[i].setPlayer(player);
            else {
                Debug.signal(Debug.ERROR, this, "Failed to load " + latest + " for player " + accounts[i].getPrimaryKey());
                accounts[i] = null;
            }

            // we now search for the latest saved inventory file.
            latest = FileTools.findSave(this.rManager.listFiles(accountList[i], AccountManager.INVENTORY_SUFFIX), AccountManager.INVENTORY_PREFIX, AccountManager.INVENTORY_SUFFIX, true);

            // have we found the latest saved inventory file ?
            if (latest == null) {
                // nope, then it's an invalid account, we ignore it...
                Debug.signal(Debug.ERROR, this, "Failed to load account's inventory: " + accountList[i]);
                accounts[i] = null;
                continue;
            }

            Inventory inventory = (Inventory) this.rManager.loadObject(latest);

            if (inventory != null) {
                // Aldiss : we create a new ServerObjectManager and give it its inventory
                ServerObjectManager objManager = new ServerObjectManager();
                player.setObjectManager(objManager);
                objManager.setInventory(inventory);
            } else {
                Debug.signal(Debug.ERROR, this, "Failed to load " + latest + " inventory for player " + accounts[i].getPrimaryKey());
                //                accounts[i] = null;
                // Petrus : if no inventory found : create a new one
                ServerObjectManager objManager = new ServerObjectManager();
                player.setObjectManager(objManager);
                objManager.setInventory(new Inventory());
            }
        }

        return accounts;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To save a client Account. Deletes the oldest entry if the account has too many.
     *
     * @param account client account
     * @return true if the account has been saved succesfully.
     */
    public boolean saveAccount(GameAccount account) {

        String accountHome = this.rManager.getExternalPlayersHomeDir();

        if (!this.rManager.saveObject(account.getPlayer(), accountHome + account.getAccountName() + File.separator + AccountManager.PLAYER_PREFIX + Tools.getLexicalDate() + AccountManager.PLAYER_SUFFIX)) {
            Debug.signal(Debug.ERROR, this, "Failed to save account: " + accountHome + account.getAccountName());
            return false;
        }

        // Aldiss : we save the player's inventory
        if (!this.rManager.saveObject(account.getPlayer().getObjectManager().getInventory(), accountHome + account.getAccountName() + File.separator + AccountManager.INVENTORY_PREFIX + Tools.getLexicalDate() + AccountManager.INVENTORY_SUFFIX)) {
            Debug.signal(Debug.ERROR, this, "Failed to save account inventory: " + accountHome + account.getAccountName());
            return false;
        }

        // ok, the save went ok... do we have to erase the oldest file ?
        String accountFiles[] = this.rManager.listFiles(accountHome + account.getAccountName(), AccountManager.PLAYER_SUFFIX);

        if (accountFiles.length > AccountManager.MAX_NUMBER_OF_SAVE + 1) {
            // ok, let's delete the oldest save	
            String oldest = FileTools.findSave(accountFiles, AccountManager.PLAYER_PREFIX, AccountManager.PLAYER_SUFFIX, false);

            // have we found the latest saved file ?
            if (oldest != null) {
                // is it the file we've just saved, (if so we won't delete it, otherwise we will)
                if (oldest.endsWith(AccountManager.PLAYER_PREFIX + Tools.getLexicalDate() + AccountManager.PLAYER_SUFFIX) || new File(oldest).delete()) {

                    accountFiles = this.rManager.listFiles(accountHome + account.getAccountName(), AccountManager.INVENTORY_SUFFIX);

                    oldest = FileTools.findSave(accountFiles, AccountManager.INVENTORY_PREFIX, AccountManager.INVENTORY_SUFFIX, false);

                    // have we found the latest saved inventory ?
                    if (oldest != null) {
                        // is it the file we've just saved, (if so we won't delete it, otherwise we will)
                        if (oldest.endsWith(AccountManager.INVENTORY_PREFIX + Tools.getLexicalDate() + AccountManager.INVENTORY_SUFFIX) || new File(oldest).delete()) {
                            return true;
                        }
                    }
                }
            }

            Debug.signal(Debug.WARNING, this, "Failed to find old account entry : " + oldest);
        }

        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To create a client Account.
     *
     * @param account client account
     * @return true if the account has been created succesfully.
     */
    public boolean createAccountFiles(GameAccount account) {

        String accountHome = this.rManager.getExternalPlayersHomeDir();
        File accountDir = new File(accountHome + account.getAccountName());

        if (!accountDir.mkdir()) {
            Debug.signal(Debug.ERROR, this, "Failed to create account: " + accountDir.getName());
            return false;
        }

        // we create the client's "profile.cfg"
        if (!this.rManager.saveObject(account, accountHome + account.getAccountName() + File.separator + AccountManager.CLIENT_PROFILE)) {
            Debug.signal(Debug.ERROR, this, "Failed to save account's profile: " + accountHome + account.getAccountName());

            if (!deleteAccountFiles(account.getAccountName()))
                Debug.signal(Debug.WARNING, this, "Failed to delete bad account");

            return false;
        }

        // we save the player data in something like "player-save-2001-09-23.cfg"
        if (!saveAccount(account)) {
            if (!deleteAccountFiles(account.getAccountName()))
                Debug.signal(Debug.WARNING, this, "Failed to delete bad account");

            return false;
        }

        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To delete a client Account.
     *
     * @param accountName account name, use GameAccount.getAccountName().
     * @return true if the account has been deleted succesfully.
     */
    public boolean deleteAccountFiles(String accountName) {

        String accountHome = this.rManager.getExternalPlayersHomeDir();
        File accountDir = new File(accountHome + accountName);

        // account doesnot exists
        if (!accountDir.exists())
            return true;

        File list[] = accountDir.listFiles();

        // we delete the account's file
        if (list != null)
            for (int i = 0; i < list.length; i++)
                if (!list[i].delete())
                    return false;

        return accountDir.delete();
    }

    /*------------------------------------------------------------------------------------*/
}
