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
import wotlas.common.message.chat.AddPlayerToChatRoomMessage;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the AddPlayerToChatRoomMessage...
 *
 * @author Aldiss
 */
public class AddPlayerToChatRoomMsgBehaviour extends AddPlayerToChatRoomMessage implements NetMessageBehaviour {

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     */
    public AddPlayerToChatRoomMsgBehaviour() {
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
            System.out.println("AddPlayerToChatRoomMsgBehaviour::doBehaviour: " + this.chatRoomPrimaryKey);
        }

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl player = dataManager.getMyPlayer();

        // We seek for the player to add
        Hashtable<String, PlayerImpl> players = dataManager.getPlayers();
        PlayerImpl sender = null;

        if (players != null) {
            sender = players.get(this.senderPrimaryKey);
        }

        if (sender == null) {
            Debug.signal(Debug.ERROR, this, "Could not find the subject player of this message : " + this.senderPrimaryKey);
            return;
        }

        // We add the player
        dataManager.getClientScreen().getChatPanel().addPlayer(this.chatRoomPrimaryKey, sender);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
