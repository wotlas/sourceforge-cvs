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
package wotlas.server.message.chat;

import java.util.Hashtable;
import java.util.Iterator;
import wotlas.common.Player;
import wotlas.common.chat.ChatRoom;
import wotlas.common.message.account.WarningMessage;
import wotlas.common.message.chat.SendTextMessage;
import wotlas.common.router.MessageRouter;
import wotlas.common.universe.Room;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.server.PlayerImpl;
import wotlas.server.ServerDirector;
import wotlas.server.chat.ChatCommandProcessor;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the SendTextMsgBehaviour...
 *
 * @author Petrus
 */
public class SendTextMsgBehaviour extends SendTextMessage implements NetMessageBehaviour {

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     */
    public SendTextMsgBehaviour() {
        super();
    }

    /*------------------------------------------------------------------------------------*/
    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {
        // The sessionContext is here a PlayerImpl.
        PlayerImpl player = (PlayerImpl) sessionContext;

        // 0 - big messages are truncated
        if (this.message.length() > ChatRoom.MAXIMUM_MESSAGE_SIZE) {
            this.message = this.message.substring(0, ChatRoom.MAXIMUM_MESSAGE_SIZE - 4) + "...";
        }

        Hashtable<String, Player> players = null;
        WotlasLocation myLocation = player.getLocation();

        // 0.1 - test shortcut/commands...
        if (this.message.charAt(0) == '/') {
            ChatCommandProcessor processor = ServerDirector.getDataManager().getChatCommandProcessor();

            if (processor.processCommand(this.message, player, this)) {
                return;
            } // end of message process if the command returns true
        // if the command returns false we continue the message process
        }

        // 1 - We send the message back to the user.
        if (this.chatRoomPrimaryKey.equals(player.getCurrentChatPrimaryKey())) {
            if (this.voiceSoundLevel == ChatRoom.SHOUTING_VOICE_LEVEL) {
                this.message = this.message.toUpperCase();
            }
            player.sendMessage(this);
        } else if (this.voiceSoundLevel != ChatRoom.SHOUTING_VOICE_LEVEL) {
            // player is trying to speak in a ChatRoom not near to him.
            this.message = "<i>No one can hear you !</i>";
            player.sendMessage(this);
            return;
        }

        // 2 - We analyze who we must receive this message... it depends on the location...

        // 2.1 - ROOM CASE
        if (myLocation.isRoom()) {
            // 2.1.1 - Get Current Room
            Room myRoom = player.getMyRoom();
            if (myRoom == null) {
                Debug.signal(Debug.ERROR, this, "Error could not get current room ! " + player.getLocation());
                player.sendMessage(new WarningMessage("Your player has a bad location on Server ! Please report this bug !\nLocation:" + player.getLocation()));
                return;
            }

            // 2.1.2 - Voice Level
            switch (this.voiceSoundLevel) {
                case ChatRoom.WHISPERING_VOICE_LEVEL:
                    // is it the default chat ? or another ?
                    boolean isDefaultChat = this.chatRoomPrimaryKey.equals(ChatRoom.DEFAULT_CHAT);

                    if (isDefaultChat) {
                        players = myRoom.getMessageRouter().getPlayers();
                    } else {
                        player.setIsChatMember(true);

                        if (player.getChatList() == null) {
                            Debug.signal(Debug.ERROR, this, "No Chat List for player: " + player.getPrimaryKey());
                            return;
                        }

                        players = player.getChatList().getPlayers(this.chatRoomPrimaryKey);
                    }

                    if (players == null) {
                        Debug.signal(Debug.ERROR, this, "No players found for chat: " + this.chatRoomPrimaryKey);
                        return;
                    }

                    // send the message
                    synchronized (players) {
                        Iterator<Player> it = players.values().iterator();
                        PlayerImpl p = null;

                        while (it.hasNext()) {
                            p = (PlayerImpl) it.next();
                            if (p != player && (p.isChatMember() || isDefaultChat)) {
                                p.sendChatMessage(this, player);
                            }
                        }
                    }

                    return;

                case ChatRoom.NORMAL_VOICE_LEVEL:
                    // is it the default chat ? or another ?
                    if (this.chatRoomPrimaryKey.equals(ChatRoom.DEFAULT_CHAT)) {
                        players = myRoom.getMessageRouter().getPlayers();
                    } else {
                        player.setIsChatMember(true);

                        if (player.getChatList() == null) {
                            Debug.signal(Debug.ERROR, this, "No Chat List for player: " + player.getPrimaryKey());
                            return;
                        }

                        players = player.getChatList().getPlayers(this.chatRoomPrimaryKey);
                    }

                    if (players == null) {
                        Debug.signal(Debug.ERROR, this, "No players found for chat: " + this.chatRoomPrimaryKey);
                        return;
                    }

                    // send the message
                    synchronized (players) {
                        Iterator<Player> it = players.values().iterator();
                        PlayerImpl p = null;

                        while (it.hasNext()) {
                            p = (PlayerImpl) it.next();
                            if (p != player) {
                                p.sendChatMessage(this, player);
                            }
                        }
                    }

                    return;

                case ChatRoom.SHOUTING_VOICE_LEVEL:
                    players = myRoom.getMessageRouter().getPlayers();

                    if (players == null) {
                        Debug.signal(Debug.ERROR, this, "No players found for room: " + myRoom);
                        return;
                    }

                    this.message = this.message.toUpperCase();

                    // send the message to the players of the room
                    synchronized (players) {
                        Iterator<Player> it = players.values().iterator();
                        PlayerImpl p = null;

                        while (it.hasNext()) {
                            p = (PlayerImpl) it.next();
                            if (p != player) {
                                p.sendChatMessage(this, player);
                            }
                        }
                    }

                    // And players in other rooms
                    if (myRoom.getRoomLinks() == null) {
                        return;
                    }

                    for (int j = 0; j < myRoom.getRoomLinks().length; j++) {
                        Room otherRoom = myRoom.getRoomLinks()[j].getRoom1();

                        if (otherRoom == myRoom) {
                            otherRoom = myRoom.getRoomLinks()[j].getRoom2();
                        }

                        players = otherRoom.getMessageRouter().getPlayers();

                        synchronized (players) {
                            Iterator<Player> it = players.values().iterator();

                            while (it.hasNext()) {
                                PlayerImpl p = (PlayerImpl) it.next();
                                p.sendChatMessage(this, player);
                            }
                        }
                    }

                    return;
            } // end of switch

            return; // should never be reached
        }

        // 2.2 - TOWN & WORLD CASE

        MessageRouter mRouter = player.getMessageRouter();

        if (mRouter == null) {
            Debug.signal(Debug.ERROR, this, "No MessageRouter found for location: " + player.getLocation());
            return;
        }

        players = mRouter.getPlayers();

        synchronized (players) {
            Iterator<Player> it = players.values().iterator();

            while (it.hasNext()) {
                PlayerImpl p = (PlayerImpl) it.next();

                if (p != player) {
                    p.sendChatMessage(this, player);
                }
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
