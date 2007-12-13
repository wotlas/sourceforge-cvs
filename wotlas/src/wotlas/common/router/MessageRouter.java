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
package wotlas.common.router;

import java.util.Hashtable;
import wotlas.common.Player;
import wotlas.common.WorldManager;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetMessage;
import wotlas.utils.Debug;

/** A router of NetMessages. We manage messages that are shared between a group of
 *  players.
 *
 * @author Aldiss
 */
public abstract class MessageRouter {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /*** Options available when sending messages ***/
    /** Available send options. Send message(s) to local group only (default).
     */
    public final static byte LOCAL_GROUP = 0;

    /** Available send options. Send message(s) to local group and eventual other groups
     *  near this one. The implementation of the MessageRouter is not obliged to
     *  implement this option.
     */
    public final static byte EXTENDED_GROUP = 1;

    /** Available send options. Send message(s) to eventual other groups near this one
     *  but NOT to the local group. The implementation of the MessageRouter is not obliged
     *  to implement this option.
     */
    public final static byte EXC_EXTENDED_GROUP = 2;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Our players.
     */
    protected Hashtable<String, Player> players;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Constructor. Just creates internals.
     */
    public MessageRouter() {
        this.players = new Hashtable<String, Player>(5);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Inititializes this MessageRouter.
     *
     * @param location location this MessageRouter is linked to.
     * @param wManager WorldManager of the application.
     */
    abstract public void init(WotlasLocation location, WorldManager wManager);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To add a player to this group. The default implementation of this method just
     *  add the player to our list WITHOUT sending any messages.
     *
     * @param player player to add
     * @return true if the player was added successfully, false if an error occured.
     */
    public boolean addPlayer(Player player) {
        if (this.players.containsKey(player.getPrimaryKey())) {
            Debug.signal(Debug.WARNING, this, "Add failed : player already exists.");
            return false;
        }

        this.players.put(player.getPrimaryKey(), player);
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To remove a player from this group. The default implementation of this method just
     *  removes the player of our list WITHOUT sending any messages.
     *
     * @param player player to remove
     * @return true if the player was removed successfully, false if an error occured.
     */
    public boolean removePlayer(Player player) {
        if (!this.players.containsKey(player.getPrimaryKey())) {
            Debug.signal(Debug.WARNING, this, "Remove failed : player not found.");
            return false;
        }

        this.players.remove(player.getPrimaryKey());
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To remove all the players of this group. The default implementation of this method
     *  just removes all the players WITHOUT sending any messages.
     */
    public void removeAllPlayers() {
        this.players.clear();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To move a player from this group to another.
     *
     * @param player player to move
     * @return true if the player was moved successfully, false if an error occured.
     */
    abstract public boolean movePlayer(Player player, WotlasLocation targetLocation);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To get the list of all the players managed by this router.
     *  IMPORTANT: before ANY process on this list, synchronize your code on it :<br>
     *
     *<pre>
     *   Hashtable players = msgRouter.getPlayers();
     *   
     *   synchronized( players ) {
     *       ... some SIMPLE and SHORT processes...
     *   }
     *
     * @return our players hashtable, the player.getPrimaryKey() is the key.
     */
    public Hashtable<String, Player> getPlayers() {
        return this.players;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To find a player by its primary key. We search in our local group. MessageRouters
     *  may extend this search.
     * @param primaryKey player to find
     * @return null if not found, the player otherwise
     */
    public Player getPlayer(String primaryKey) {
        return this.players.get(primaryKey);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To send a message to the local group.
     *  @param msg message to send to the group
     */
    public void sendMessage(NetMessage msg) {
        NetMessage list[] = {msg};
        sendMessages(list, null, MessageRouter.LOCAL_GROUP);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To send a list of messages to the local group.
     *  @param msg messages to send to the group
     */
    public void sendMessages(NetMessage msg[]) {
        sendMessages(msg, null, MessageRouter.LOCAL_GROUP);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To send a message to the local group with the exception of a player.
     *  @param msg message to send to the group
     *  @param exceptThisPlayer player to except from the send of messages, if the
     *         given player is null the message will be sent to everyone in the local
     *         group. 
     */
    public void sendMessage(NetMessage msg, Player exceptThisPlayer) {
        NetMessage list[] = {msg};
        sendMessages(list, exceptThisPlayer, MessageRouter.LOCAL_GROUP);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To send a list of messages to the local group with the exception of a player.
     *  @param msg messages to send to the group
     *  @param exceptThisPlayer player to except from the send of messages, if the
     *         given player is null the message will be sent to everyone in the local
     *         group. 
     */
    public void sendMessages(NetMessage msg[], Player exceptThisPlayer) {
        sendMessages(msg, exceptThisPlayer, MessageRouter.LOCAL_GROUP);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To send a message to the specified group with the exception of a player.
     *  @param msg message to send to the group
     *  @param exceptThisPlayer player to except from the send of messages, if the
     *         given player is null the message will be sent to everyone in the selected
     *         groups. 
     *  @param groupOption gives the groups to send the message to. See the constants
     *         defined in this class : LOCAL_GROUP, EXTENDED_GROUP, EXC_EXTENDED_GROUP
     */
    public void sendMessage(NetMessage msg, Player exceptThisPlayer, byte groupOption) {
        NetMessage list[] = {msg};
        sendMessages(list, exceptThisPlayer, groupOption);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To send a list of messages to the specified group with the exception of a player.
     *  @param msg message to send to the group
     *  @param exceptThisPlayer player to except from the send of messages, if the
     *         given player is null the message will be sent to everyone in the selected
     *         groups. 
     *  @param groupOption gives the groups to send the message to. See the constants
     *         defined in this class : LOCAL_GROUP, EXTENDED_GROUP, EXC_EXTENDED_GROUP
     */
    abstract public void sendMessages(NetMessage msg[], Player exceptThisPlayer, byte groupOption);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
