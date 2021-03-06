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
package wotlas.server.router;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import wotlas.common.Player;
import wotlas.common.WorldManager;
import wotlas.common.chat.ChatList;
import wotlas.common.chat.ChatRoom;
import wotlas.common.message.chat.AddPlayerToChatRoomMessage;
import wotlas.common.message.chat.ChatRoomCreatedMessage;
import wotlas.common.message.chat.RemPlayerFromChatRoomMessage;
import wotlas.common.message.chat.SetCurrentChatRoomMessage;
import wotlas.common.message.description.AddPlayerToRoomMessage;
import wotlas.common.message.description.CleanGhostsMessage;
import wotlas.common.message.description.DoorsStateMessage;
import wotlas.common.message.description.RemovePlayerFromRoomMessage;
import wotlas.common.message.description.RoomPlayerDataMessage;
import wotlas.common.message.movement.LocationChangeMessage;
import wotlas.common.router.MessageRouter;
import wotlas.common.universe.Room;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetMessage;
import wotlas.server.PlayerImpl;
import wotlas.server.message.chat.RemPlayerFromChatRoomMsgBehaviour;
import wotlas.utils.Debug;

/** A message router for Rooms which follows a 1-near step policy.
 *
 * @author Aldiss
 */
public class MultiGroupMessageRouter extends MessageRouter {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Our near Rooms (the list does not contain our Room).
     */
    protected Room nearRooms[];

    /** Our Room.
     */
    protected Room thisRoom;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Constructor. Just creates internals.
     */
    public MultiGroupMessageRouter() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Inititializes this MessageRouter.
     *
     * @param location location this MessageRouter is linked to.
     * @param wManager WorldManager of the application.
     */
    @Override
    public void init(WotlasLocation location, WorldManager wManager) {

        // 1 - We get our Room.
        if (!location.isRoom()) {
            Debug.signal(Debug.FAILURE, this, "Location is not a Room ! Can't init router !");
            return;
        }

        this.thisRoom = wManager.getRoom(location);

        if (this.thisRoom == null) {
            Debug.signal(Debug.FAILURE, this, "Room not found ! Can't init router !");
            return;
        }

        // 2 - We create a list of the rooms near ours.
        if (this.thisRoom.getRoomLinks() == null || this.thisRoom.getRoomLinks().length == 0) {
            this.nearRooms = new Room[0]; // this way no need to test for nearRooms nullity...
            return;
        }

        HashMap<String, Room> tempRoomList = new HashMap<String, Room>(10);

        for (int i = 0; i < this.thisRoom.getRoomLinks().length; i++) {
            Room otherRoom = this.thisRoom.getRoomLinks()[i].getRoom1();

            if (otherRoom == this.thisRoom) {
                otherRoom = this.thisRoom.getRoomLinks()[i].getRoom2();
                if (otherRoom == this.thisRoom) {
                    continue;
                }
            }

            if (otherRoom == null) {
                continue;
            }

            // ok do we have this room in our list ?
            if (!tempRoomList.containsKey("" + otherRoom.getRoomID())) {
                tempRoomList.put("" + otherRoom.getRoomID(), otherRoom);
            }
        }

        this.nearRooms = new Room[tempRoomList.size()];

        Iterator<Room> it = tempRoomList.values().iterator();
        int counter = 0;

        while (it.hasNext()) {
            this.nearRooms[counter] = it.next();
            counter++;
        }

        tempRoomList.clear(); // let's help the garbage collector...
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To add a player to this group. We update its location.
     *
     *  Call this method when a player is added to the map. If the player is arriving from
     *  another room call movePlayer.
     *
     * @param player player to add
     * @return true if the player was added successfully, false if an error occured.
     */
    @Override
    public boolean addPlayer(Player player) {

        // 1 - We add this player to our list & don't care if it's already in there
        this.players.put(player.getPrimaryKey(), player);
        player.setLocation(this.thisRoom.getLocation()); // update player location

        if (!player.isConnectedToGame()) {
            return true;
        } // no need to advertise if the player is not connected

        // 2 - We advertise our presence to other players in the LOCAL room
        AddPlayerToRoomMessage aMsg = new AddPlayerToRoomMessage(null, player);

        synchronized (this.players) {
            Iterator<Player> it = this.players.values().iterator();

            while (it.hasNext()) {
                Player p = it.next();

                if (p != player) {
                    aMsg.setOtherPlayer(p); // needed for the LieManager to know who is
                    p.sendMessage(aMsg); // asking for the player's name....
                }
            }
        }

        // 3 - We advertise our presence to other players of the 1 step-NEAR rooms
        for (int i = 0; i < this.nearRooms.length; i++) {
            Hashtable<String, Player> otherPlayers = this.nearRooms[i].getMessageRouter().getPlayers();

            synchronized (otherPlayers) {
                Iterator<Player> it = otherPlayers.values().iterator();

                while (it.hasNext()) {
                    Player p = it.next();
                    aMsg.setOtherPlayer(p); // needed for the LieManager to know who is
                    p.sendMessage(aMsg); // asking for the player's name....
                }
            }
        }

        // 4 - We send DOORS & PLAYERS data to the added player
        player.sendMessage(new RoomPlayerDataMessage(this.thisRoom, player));

        for (int i = 0; i < this.nearRooms.length; i++) {
            player.sendMessage(new DoorsStateMessage(this.nearRooms[i]));
            player.sendMessage(new RoomPlayerDataMessage(this.nearRooms[i], player));
        }

        // 5 - We send CHAT DATA to the added player
        updateChatInformation((PlayerImpl) player);

        return true;

    /*** END OF ADDPLAYER ***/
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To update the local chat information for a player. We update the state of the
     *  player
     */
    protected void updateChatInformation(PlayerImpl player) {

        // 1 - and signal our player to default chat room...
        sendMessage(new AddPlayerToChatRoomMessage(player.getPrimaryKey(), ChatRoom.DEFAULT_CHAT), player);

        // 2 - We send CHAT data to the added player
        //     ( list of the players of the default chat room )
        player.sendMessage(new SetCurrentChatRoomMessage(ChatRoom.DEFAULT_CHAT, this.players));

        // 3 - We seek for a valid chatList if any...
        synchronized (this.players) {
            Iterator<Player> it = this.players.values().iterator();

            while (it.hasNext()) {
                PlayerImpl p = (PlayerImpl) it.next();

                if (p != player && p.isConnectedToGame()) {
                    ChatList chatList = p.getChatList();

                    if (chatList != null) {
                        player.setChatList(chatList);
                        break;
                    }
                }
            }
        }

        ChatList myChatList = player.getChatList();

        if (myChatList == null) {
            return;
        } // no chats in the room

        // 4 - We send the CHAT ROOMS available to our client...
        Hashtable<String, ChatRoom> chatRooms = myChatList.getChatRooms();

        synchronized (chatRooms) {
            Iterator<ChatRoom> it = chatRooms.values().iterator();

            while (it.hasNext()) {
                ChatRoom cRoom = it.next();
                player.sendMessage(new ChatRoomCreatedMessage(cRoom.getPrimaryKey(), cRoom.getName(), cRoom.getCreatorPrimaryKey()));
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To remove a player from this group.
     *
     *  Call this method when a player is removed from the map. If the player is arriving
     *  from another room call movePlayer.
     *
     * @param player player to remove
     * @return true if the player was removed successfully, false if an error occured.
     */
    @Override
    public boolean removePlayer(Player player) {

        // 1 - We remove this player from our list
        if (!super.removePlayer(player)) {
            return false;
        } // non-existent player

        // 2 - We send remove messages to local & near players
        sendMessage(new RemovePlayerFromRoomMessage(player.getPrimaryKey(), this.thisRoom.getLocation()), null, MessageRouter.EXTENDED_GROUP);

        // 3 - Remove from the chat
        PlayerImpl playerImpl = (PlayerImpl) player;

        if (!playerImpl.getCurrentChatPrimaryKey().equals(ChatRoom.DEFAULT_CHAT)) {
            RemPlayerFromChatRoomMsgBehaviour remPlayerFromChat = new RemPlayerFromChatRoomMsgBehaviour(player.getPrimaryKey(), playerImpl.getCurrentChatPrimaryKey());

            try {
                remPlayerFromChat.doBehaviour(player);
            } catch (Exception e) {
                Debug.signal(Debug.ERROR, this, e);
                playerImpl.setCurrentChatPrimaryKey(ChatRoom.DEFAULT_CHAT);
            }
        } else {
            sendMessage(new RemPlayerFromChatRoomMessage(player.getPrimaryKey(), ChatRoom.DEFAULT_CHAT));
        }

        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To remove all the players of this group. The default implementation of this method
     *  just removes all the players WITHOUT sending any messages.
     */
    @Override
    public void removeAllPlayers() {
        super.removeAllPlayers();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To find a player by its primary key. We first search in  the local group and then
     *  extend our search to near groups.
     *
     * @param primaryKey player to find
     * @return null if not found, the player otherwise
     */
    @Override
    public Player getPlayer(String primaryKey) {
        Player p = this.players.get(primaryKey);

        if (p != null) {
            return p;
        }

        // we extend the search to near rooms
        for (int i = 0; i < this.nearRooms.length; i++) {
            p = this.nearRooms[i].getMessageRouter().getPlayers().get(primaryKey);

            if (p != null) {
                return p;
            }
        }

        return null; // not found
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To move a player from this group to another. The player location is changed.
     *
     * @param player player to move
     * @return true if the player was moved successfully, false if an error occured.
     */
    @Override
    public boolean movePlayer(Player player, WotlasLocation targetLocation) {

        // 1 - We remove the player from our router...
        if (!super.removePlayer(player)) {
            return false;
        } // non-existent player

        // 2 - Check the target room...
        int targetRoomID = targetLocation.getRoomID();
        Room targetRoom = null;

        for (int i = 0; i < this.nearRooms.length; i++) {
            if (this.nearRooms[i].getRoomID() == targetRoomID) {
                targetRoom = this.nearRooms[i];
                break;
            }
        }

        if (targetRoom == null) {
            Debug.signal(Debug.ERROR, this, "Target room not found !");
            return false;
        }

        // 3 - Send approriate Remove messages
        RemovePlayerFromRoomMessage rMsg = new RemovePlayerFromRoomMessage(player.getPrimaryKey(), player.getLocation());

        for (int i = 0; i < this.nearRooms.length; i++) {
            if (this.nearRooms[i].getRoomID() != targetRoomID) {
                this.nearRooms[i].getMessageRouter().sendMessage(rMsg);
            }
        }

        sendMessage(new RemPlayerFromChatRoomMessage(player.getPrimaryKey(), ChatRoom.DEFAULT_CHAT));

        // 4 - Send appropriate Location changes & Messages
        player.setLocation(targetRoom.getLocation());
        targetRoom.getMessageRouter().getPlayers().put(player.getPrimaryKey(), player);

        LocationChangeMessage lMsg = new LocationChangeMessage(player.getPrimaryKey(), player.getLocation(), 0, 0, 0.0f);

        sendMessage(lMsg); // to this room
        targetRoom.getMessageRouter().sendMessage(lMsg, player); // to the target room

        // 5 - We ask the moved player to clean his ghosts
        player.sendMessage(new CleanGhostsMessage(player.getPrimaryKey(), player.getLocation()));

        // 6 - Send appropriate Add Player Messages
        //     to the neighbours of the target room where we are now
        //     We also send remaining Players & Doors Messages to our player
        AddPlayerToRoomMessage aMsg = new AddPlayerToRoomMessage(null, player);

        if (targetRoom.getRoomLinks() != null) {
            for (int i = 0; i < targetRoom.getRoomLinks().length; i++) {
                Room otherRoom = targetRoom.getRoomLinks()[i].getRoom1();

                if (otherRoom == targetRoom) {
                    otherRoom = targetRoom.getRoomLinks()[i].getRoom2();
                }

                if (otherRoom == this.thisRoom) {
                    continue;
                }

                player.sendMessage(new DoorsStateMessage(otherRoom));
                player.sendMessage(new RoomPlayerDataMessage(otherRoom, player));

                Hashtable<String, Player> otherPlayers = otherRoom.getMessageRouter().getPlayers();

                synchronized (otherPlayers) {
                    Iterator<Player> it = otherPlayers.values().iterator();

                    while (it.hasNext()) {
                        Player p = it.next();
                        aMsg.setOtherPlayer(p); // needed for the LieManager to know who is
                        p.sendMessage(aMsg); // asking for the player's name....
                    }
                }
            }
        }

        // 7 - We send CHAT DATA to the added player
        ((MultiGroupMessageRouter) targetRoom.getMessageRouter()).updateChatInformation((PlayerImpl) player);

        return true;
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
    @Override
    public void sendMessages(NetMessage msg[], Player exceptThisPlayer, byte groupOption) {

        if (groupOption != MessageRouter.EXC_EXTENDED_GROUP) {
            // We send the messages to the local group.
            synchronized (this.players) {
                Iterator<Player> it = this.players.values().iterator();

                while (it.hasNext()) {
                    Player p = it.next();

                    if (p != exceptThisPlayer) {
                        for (int i = 0; i < msg.length; i++) {
                            p.sendMessage(msg[i]);
                        }
                    }
                }
            }
        }

        if (groupOption != MessageRouter.LOCAL_GROUP) {
            // We send the messages to near groups.
            for (int i = 0; i < this.nearRooms.length; i++) {
                this.nearRooms[i].getMessageRouter().sendMessages(msg, exceptThisPlayer, MessageRouter.LOCAL_GROUP);
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
