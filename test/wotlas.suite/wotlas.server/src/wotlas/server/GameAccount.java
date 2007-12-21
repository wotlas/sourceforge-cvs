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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import wotlas.common.message.description.PlayerDataMessage;

/** A game account represents information about a player account.
 *  It is used for access control when connecting to a GameServer, and for
 *  data access when the connection has been approved.<p>
 *
 * A GameAccount is also used to store client basic information when entering
 * the game for the first time via the AccountServer.<p>
 *
 * GameAccounts are persistent : they are loaded from the "profile.cfg" file
 * found in the player account. They stay in memory as long as the GameServer
 * lives.<p><br>
 *
 * A client is identified by two IDs :<p>
 *
 *  - Client Original Server ID ( where this game account was first created )<br>
 *  - Local Client ID ( given by the server that was the first to create the client account )
 *<p><br>
 *  Note that the association { originalServerID, localClientID} identifies
 *  plainly this client in the whole virtual world. These IDs are never changed even
 *  when the client moves to another server.<p>
 *
 * Also note that the account name has the following structure :<p><br>
 *
 *      login + "-" + originalServerID + "-" + localClientID
 *
 * @author Aldiss
 * @see wotlas.server.GameServer
 */

public class GameAccount {
    /*------------------------------------------------------------------------------------*/

    /** Client Login ( NOT equal to the client's directory name, see  getAccountName() )
     */
    private String login;

    /** Client Password ( previously hashed with a one-way algorithm )
     */
    private String password;

    /** Client Email
     */
    private String email;

    /** Client Original Server ID ( where this game account was first created )
     */
    private int originalServerID;

    /** Local Client ID ( given by the server that was the first to create the client account )
     */
    private int localClientID;

    /** is a dead account ?
     */
    private boolean isDeadAccount;

    /** Last Connection Time.
     */
    private long lastConnectionTime;

    /** Last Connection Try. ( The last time someone tried to login but entered a 
     *  bad password)
     */
    transient private long lastConnectionTry;

    /** Bad Password Counter. After 3 tries we lock the access for 30s.
     */
    transient private byte badPasswordCounter;

    /** Our PlayerImpl link. This is the object that will be given as a message context.
     *  It is a kind of bookmark for incoming messages ( and a player data repository ).
     */
    transient private PlayerImpl player;

    /*------------------------------------------------------------------------------------*/

    /** Empty Constructor for persistence.
     *  Data is loaded by the PersistenceManager.
     */
    public GameAccount() {
        this.isDeadAccount = false;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor used to create the account on this server.
     *  The last connection time is set to now.
     *
     * @param login the client login
     * @param password the hashed password
     * @param originalServerID the original server ID of this client account.
     * @param localClientID the client ID given by its first server.
     */
    public GameAccount(String login, String password, int originalServerID, int localClientID) {
        this.login = login;
        this.password = password;
        this.originalServerID = originalServerID;
        this.localClientID = localClientID;

        this.lastConnectionTime = System.currentTimeMillis();
        this.lastConnectionTry = this.lastConnectionTime;
        this.isDeadAccount = false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the client login name.
     *
     * @return client's login
     */
    public String getLogin() {
        return this.login;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the client login name ( for persistence only ).
     *
     * @param login client's login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the client's account name in the database home/ directory.
     * This is also the key of the GameAccount in the AccountManager.<p>
     *
     * It's structure is : login + "-" + originalServerID + "-" + localClientID
     *
     * @return client's complete account name.
     */
    public String getAccountName() {
        return this.login + "-" + this.originalServerID + "-" + this.localClientID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the client login password ( for persistence only ).
     *
     * @return client's password
     */
    public String getPassword() {
        return this.password;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the client password ( for persistence only ). 
     *
     * @param password client's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To compare the client's password.
     *
     * @param password to compare to our own one
     * @return true if password are equals, false otherwise.
     */
    public boolean isRightPassword(String password) {
        if (this.password.equals(password))
            return true;

        this.lastConnectionTry = System.currentTimeMillis();
        this.badPasswordCounter++;
        return false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Tests if two much bad password have been entered in the last 30 seconds.
     *
     * @return true if the client can enter his password, false otherwise...
     */
    public boolean tooMuchBadPasswordEntered() {
        long delta = System.currentTimeMillis() - this.lastConnectionTry;

        if (delta >= 30000) {
            this.badPasswordCounter = 0; // more than  30s elapsed
            return false;
        }

        if (this.badPasswordCounter < 3)
            return false; // client can try again

        return true; // nope...
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the client email.
     *
     * @return client's email
     */
    public String getEmail() {
        return this.email;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the client email ( for persistence only ).
     *
     * @param email client's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the client local ID.
     *
     * @return client's local ID
     */
    public int getLocalClientID() {
        return this.localClientID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the client local ID ( for persistence only ). 
     *
     * @param localClientID client's local ID
     */
    public void setLocalClientID(int localClientID) {
        this.localClientID = localClientID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the client original server ID.
     *
     * @return client's original server ID
     */
    public int getOriginalServerID() {
        return this.originalServerID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the client original server ID ( for persistence only ). 
     *
     * @param originalServerID client's original server ID
     */
    public void setOriginalServerID(int originalServerID) {
        this.originalServerID = originalServerID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the last connection time
     *
     * @return last connection time
     */
    public long getLastConnectionTime() {
        return this.lastConnectionTime;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the last connection time. 
     *
     * @param lastConnectionTime client's the last connection time
     */
    public void setLastConnectionTime(long lastConnectionTime) {
        this.lastConnectionTime = lastConnectionTime;
        this.lastConnectionTry = lastConnectionTime;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the last connection time. Time is set to current.
     */
    public void setLastConnectionTimeNow() {
        this.lastConnectionTime = System.currentTimeMillis();
        this.lastConnectionTry = this.lastConnectionTime;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** is the account dead ?
     *
     * @return true if the account is dead
     */
    public boolean getIsDeadAccount() {
        return this.isDeadAccount;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** is the account dead ?
     *
     * @param isDeadAccount
     */
    public void setIsDeadAccount(boolean isDeadAccount) {
        this.isDeadAccount = isDeadAccount;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the PlayerImpl instance of this client account.
     *  (corresponds to a transient field)
     *
     * @return player implementation linked to this account.
     */
    public PlayerImpl getPlayer() {
        return this.player;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the PlayerImpl instance for this client account.
     *  (corresponds to a transient field)
     *
     * @param player player implementation linked to this account.
     */
    public void setPlayer(PlayerImpl player) {
        this.player = player;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This is where we put your message data on the stream. You don't need
     * to invoke this method yourself, it's done automatically.
     *
     * @param ostream data stream where to put your data (see java.io.DataOutputStream)
     * @exception IOException if the stream has been closed or is corrupted.
     */
    public void encode(DataOutputStream ostream) throws IOException {
        ostream.writeUTF(this.login);
        ostream.writeUTF(this.password);
        ostream.writeUTF(this.email);
        ostream.writeInt(this.originalServerID);
        ostream.writeInt(this.localClientID);
        ostream.writeLong(this.lastConnectionTime);

        PlayerDataMessage playerEncoder = new PlayerDataMessage(this.player, false);
        playerEncoder.encode(ostream);

        // Lie Manager is sent separately...
        this.player.getLieManager().encode(ostream);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This is where we put your message data on the stream. You don't need
     * to invoke this method yourself, it's done automatically.
     *
     * @param istream data stream where to retrieve our data (see java.io.DataInputStream)
     * @exception IOException if the stream has been closed or is corrupted.
     */
    public void decode(DataInputStream istream) throws IOException {
        this.login = istream.readUTF();
        this.password = istream.readUTF();
        this.email = istream.readUTF();
        this.originalServerID = istream.readInt();
        this.localClientID = istream.readInt();
        this.lastConnectionTime = istream.readLong();
        setLastConnectionTimeNow();
        this.badPasswordCounter = 0;

        PlayerDataMessage playerDecoder = new PlayerDataMessage(this.player, false, "wotlas.server.PlayerImpl");
        playerDecoder.decode(istream);
        this.player = (PlayerImpl) playerDecoder.getPlayer();

        // LieManager re-creation. The LieManager is associated to the player via the decode method
        LieManager lieManager = this.player.getLieManager();
        lieManager.decode(istream, this.player);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the full account name (aka primary key).
     */
    public String getPrimaryKey() {
        return this.login + "-" + this.originalServerID + "-" + this.localClientID;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** prints account info.
     */
    @Override
    public String toString() {
        return "Account Info:" + "\nlogin: " + this.login + "\naccountName: " + getAccountName() + "\nlastConnection: " + getLastConnectionTime() + "\nemail:" + getEmail() + "\n";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
