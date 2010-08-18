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
import wotlas.client.screen.JChatRoom;
import wotlas.common.message.chat.SetCurrentChatRoomMessage;
import wotlas.common.message.chat.WishClientChatNetMsgBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the SetCurrentChatRoomMessage...
 *
 * @author Aldiss
 */
public class SetCurrentChatRoomMsgBehaviour extends SetCurrentChatRoomMessage implements WishClientChatNetMsgBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** To tell if this message is to be invoked later or not.
     */
    private boolean invokeLater = true;

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     */
    public SetCurrentChatRoomMsgBehaviour() {
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
            System.out.println("SetCurrentChatRoomMsgBehaviour::doBehaviour: " + this.chatRoomPrimaryKey);
        }

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl player = dataManager.getMyPlayer();

        if (this.invokeLater) {
            // We set the current chat for our player
            if (dataManager.getClientScreen().getChatPanel().setCurrentJChatRoom(this.chatRoomPrimaryKey)) {
                dataManager.getClientScreen().getChatPanel().addPlayer(this.chatRoomPrimaryKey, player);
            } else {
                Debug.signal(Debug.ERROR, this, "Failed to set current chat for player... " + this.chatRoomPrimaryKey);
                return;
            }

            if (!player.getLocation().isRoom()) {
                return;
            }

            this.invokeLater = false;
            dataManager.invokeLater(this);
            return;
        }

        // We seek the players to add
        JChatRoom chatRoom = dataManager.getClientScreen().getChatPanel().getJChatRoom(this.chatRoomPrimaryKey);

        if (chatRoom == null) {
            Debug.signal(Debug.ERROR, this, "Failed to find chat : " + this.chatRoomPrimaryKey);
            return;
        }

        Hashtable<String, PlayerImpl> players = dataManager.getPlayers();
        PlayerImpl sender = null;

        for (int i = 0; i < this.playersPrimaryKey.length; i++) {
            if (players != null) {
                sender = players.get(this.playersPrimaryKey[i]);
            }

            if (sender == null) {
                Debug.signal(Debug.WARNING, this, "Could not find the player for chat... " + this.playersPrimaryKey[i]);
            } else {
                this.fullPlayerNames[i] = sender.getFullPlayerName();
            }

            // We add the player
            if (!this.playersPrimaryKey[i].equals(player.getPrimaryKey())) {
                chatRoom.addPlayer(this.playersPrimaryKey[i], this.fullPlayerNames[i]);
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
