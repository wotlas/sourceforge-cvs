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

import java.util.Iterator;
import wotlas.common.chat.ChatList;
import wotlas.common.chat.ChatRoom;
import wotlas.common.message.account.WarningMessage;
import wotlas.common.message.chat.ChatRoomCreatedMessage;
import wotlas.common.message.chat.ChatRoomCreationMessage;
import wotlas.common.message.chat.WishServerChatNetMsgBehaviour;
import wotlas.common.router.MessageRouter;
import wotlas.common.universe.WotlasLocation;
import wotlas.server.ChatListImpl;
import wotlas.server.PlayerImpl;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the ChatRoomCreationMessage...
 *
 * @author Petrus
 */
public class ChatRoomCreationMsgBehaviour extends ChatRoomCreationMessage implements WishServerChatNetMsgBehaviour {

    /*------------------------------------------------------------------------------------*/
    /** Is it a bot's default chat room we want to create ?
     */
    private boolean isBotChatRoom;

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     */
    public ChatRoomCreationMsgBehaviour() {
        super();
    }

    /*------------------------------------------------------------------------------------*/
    /** Constructor with parameters for bots. The isBotChatRoom parameter tells if this
     *  chat room is the default chat room of a bot.
     */
    public ChatRoomCreationMsgBehaviour(String name, String creatorPrimaryKey, boolean isBotChatRoom) {
        super(name, creatorPrimaryKey);
        this.isBotChatRoom = isBotChatRoom;
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
        WotlasLocation location = player.getLocation();

        // 0 - We check the length of the chat room name
        if (this.name.length() > ChatRoom.MAXIMUM_NAME_SIZE) {
            this.name = this.name.substring(0, ChatRoom.MAXIMUM_NAME_SIZE - 1);
        }

        // 1 - We get the message router
        MessageRouter mRouter = player.getMessageRouter();

        if (mRouter == null) {
            Debug.signal(Debug.ERROR, this, "No Message Router !");
            player.sendMessage(new WarningMessage("Error #ChCreMsgRtr while performing creation !\nPlease report the bug !"));
            return; // rare internal error occured !
        }

        // 2 - Do we have to delete the previous chatroom ?
        if (!player.getCurrentChatPrimaryKey().equals(ChatRoom.DEFAULT_CHAT)) {
            // The following message behaviour does this job...
            RemPlayerFromChatRoomMsgBehaviour remPlayerFromChat = new RemPlayerFromChatRoomMsgBehaviour(player.getPrimaryKey(), player.getCurrentChatPrimaryKey());

            try {
                remPlayerFromChat.doBehaviour(player);
            } catch (Exception e) {
                Debug.signal(Debug.ERROR, this, e);
                player.setCurrentChatPrimaryKey(ChatRoom.DEFAULT_CHAT);
            }
        }

        // 3 - We try to create the new chatroom
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setPrimaryKey(ChatRoom.getNewChatPrimaryKey());
        chatRoom.setName(this.name);
        chatRoom.setCreatorPrimaryKey(this.creatorPrimaryKey);
        chatRoom.addPlayer(player);

        synchronized (mRouter.getPlayers()) {
            ChatList chatList = player.getChatList();

            if (chatList == null) {
                chatList = new ChatListImpl();

                // We set the chatList to all the players in the chat room...
                Iterator it = mRouter.getPlayers().values().iterator();

                while (it.hasNext()) {
                    PlayerImpl p = (PlayerImpl) it.next();
                    p.setChatList(chatList);
                }
            }

            if (chatList.getNumberOfChatRooms() > ChatRoom.MAXIMUM_CHATROOMS_PER_ROOM && !this.isBotChatRoom) {
                return;
            } // can't add ChatRoom : too many already !

            chatList.addChatRoom(chatRoom);
        }

        player.setCurrentChatPrimaryKey(chatRoom.getPrimaryKey());
        player.setIsChatMember(true);

        // 4 - We advertise the newly created chatroom
        // We send the information to all players of the same room or town or world
        mRouter.sendMessage(new ChatRoomCreatedMessage(chatRoom.getPrimaryKey(), this.name, this.creatorPrimaryKey));
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
