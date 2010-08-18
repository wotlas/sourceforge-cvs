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
package wotlas.client.message.chat;

import java.util.Hashtable;
import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;
import wotlas.common.chat.ChatRoom;
import wotlas.common.message.chat.ChatRoomCreatedMessage;
import wotlas.common.message.chat.WishClientChatNetMsgBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the ChatRoomCreatedMessage...
 *
 * @author Petrus
 */
public class ChatRoomCreatedMsgBehaviour extends ChatRoomCreatedMessage implements WishClientChatNetMsgBehaviour {

    /*------------------------------------------------------------------------------------*/
    /** To tell if this message is to be invoked later or not.
     */
    private boolean invokeLater = true;

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     */
    public ChatRoomCreatedMsgBehaviour() {
        super();
    }

    /*------------------------------------------------------------------------------------*/
    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {

        if (DataManager.SHOW_DEBUG) {
            System.out.println("ChatRoomCreatedMsgBehaviour:" + this.primaryKey);
        }

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl player = dataManager.getMyPlayer();

        if (this.invokeLater) {
            this.invokeLater = false;
            dataManager.invokeLater(this);
            return;
        }

        // We seek for the creator of this chat...
        Hashtable<String, PlayerImpl> players = dataManager.getPlayers();
        PlayerImpl sender = null;

        if (players != null) {
            sender = players.get(this.creatorPrimaryKey);
        }

        if (sender == null) {
            Debug.signal(Debug.WARNING, this, "Could not find the sender of this message : " + this.creatorPrimaryKey);
        }

        // We create the new chat
        if (this.primaryKey == null || this.name == null || this.creatorPrimaryKey == null) {
            Debug.signal(Debug.ERROR, this, "Created new chat with bad parameters !");
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setPrimaryKey(this.primaryKey);
        chatRoom.setName(this.name);
        chatRoom.setCreatorPrimaryKey(this.creatorPrimaryKey);

        dataManager.getClientScreen().getChatPanel().addJChatRoom(chatRoom);

        if (player.getPrimaryKey().equals(this.creatorPrimaryKey)) {
            // We created this chat !
            boolean success = dataManager.getClientScreen().getChatPanel().setCurrentJChatRoom(this.primaryKey);

            if (success) {
                dataManager.getClientScreen().getChatPanel().addPlayer(this.primaryKey, player);
            } else {
                Debug.signal(Debug.ERROR, this, "Failed to create owner's new ChatRoom");
            }
        } else {
            // someone else created the chatroom
            dataManager.getClientScreen().getChatPanel().setEnabledAt(this.primaryKey, false);
        }

    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
