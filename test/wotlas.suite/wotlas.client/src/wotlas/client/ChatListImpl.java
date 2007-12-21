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

package wotlas.client;

import java.util.Hashtable;
import wotlas.common.Player;
import wotlas.common.chat.ChatList;
import wotlas.common.chat.ChatRoom;
import wotlas.utils.Debug;

/** Implementation of a Chat by the server
 *
 * @author Petrus
 * @see wotlas.server.ChatListImpl
 * @see wotlas.client.ChatListImpl
 */

public class ChatListImpl implements ChatList {
    /*------------------------------------------------------------------------------------*/

    /** List of player's ChatRooms
     */
    protected Hashtable<String, ChatRoom> chatRooms = new Hashtable<String, ChatRoom>(2);

    /*------------------------------------------------------------------------------------*/

    /** Constructor
     */
    public ChatListImpl() {
        super();
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the number of existing ChatRooms.
     */
    public int getNumberOfChatRooms() {
        return this.chatRooms.size();
    }

    /*------------------------------------------------------------------------------------*/

    /** To add a chatRoom.
     *
     * @param chatRoom ChatRoom to add
     * @return false if the chatRoom already exists, true otherwise
     */
    public boolean addChatRoom(ChatRoom chatRoom) {
        if (this.chatRooms.containsKey(chatRoom.getPrimaryKey())) {
            Debug.signal(Debug.CRITICAL, this, "addChatRoom failed: key " + chatRoom.getPrimaryKey() + " already in " + this);
            return false;
        }
        this.chatRooms.put(chatRoom.getPrimaryKey(), chatRoom);
        return true;
    }

    /*------------------------------------------------------------------------------------*/

    /** To remove a ChatRoom.
     *
     * @param primaryKey ChatRoom primary key
     */
    public boolean removeChatRoom(String primaryKey) {
        if (!this.chatRooms.containsKey(primaryKey)) {
            Debug.signal(Debug.CRITICAL, this, "removeChatRoom failed: key " + primaryKey + " not found in " + this);
            return false;
        }
        this.chatRooms.remove(primaryKey);
        return true;
    }

    /** To remove a ChatRoom.   
     *
     * @param chatRoom ChatRoom to remove
     * @return false if the chatRoom doesn't exists, true otherwise
     */
    public boolean removeChatRoom(ChatRoom chatRoom) {
        return removeChatRoom(chatRoom.getPrimaryKey());
    }

    /*------------------------------------------------------------------------------------*/

    /** To get a ChatRoom.
     *
     * @param primaryKey primary key of ChatRoom we want to get
     */
    public ChatRoom getChatRoom(String primaryKey) {
        return this.chatRooms.get(primaryKey);
    }

    /*------------------------------------------------------------------------------------*/

    /** To add a player to a ChatRoom.
     *
     * @param primaryKey primary key of ChatRoom to modify
     * @param player Player to add
     */
    public boolean addPlayer(String primaryKey, Player player) {
        ChatRoom chatRoom = this.chatRooms.get(primaryKey);

        if (chatRoom == null) {
            Debug.signal(Debug.ERROR, this, "No chatRoom " + primaryKey + " found. Can't add player.");
            return false;
        }

        return chatRoom.addPlayer(player);
    }

    /*------------------------------------------------------------------------------------*/

    /** To remove a player from a ChatRoom.
     *
     * @param primaryKey primary key of ChatRoom to modify
     * @param player Player to remove
     */
    public boolean removePlayer(String primaryKey, Player player) {
        ChatRoom chatRoom = this.chatRooms.get(primaryKey);

        if (chatRoom == null) {
            Debug.signal(Debug.ERROR, this, "No chatRoom " + primaryKey + " found. Can't remove player.");
            return false;
        }

        return chatRoom.removePlayer(player);
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the list of players of a ChatRoom
     *
     * @param primaryKey primary key of the ChatRoom
     */
    public Hashtable<String, Player> getPlayers(String primaryKey) {
        ChatRoom chatRoom = this.chatRooms.get(primaryKey);

        if (chatRoom == null) {
            Debug.signal(Debug.ERROR, this, "No chatRoom " + primaryKey + " found. Can't get player list.");
            return null;
        }

        return chatRoom.getPlayers();
    }

    /*------------------------------------------------------------------------------------*/

    /** To get all the ChatRooms. Use with : synchronized( ... ) {} please !
     *
     * @param primaryKey primary key of ChatRoom we want to get
     */
    public Hashtable<String, ChatRoom> getChatRooms() {
        return this.chatRooms;
    }

    /*------------------------------------------------------------------------------------*/

}
