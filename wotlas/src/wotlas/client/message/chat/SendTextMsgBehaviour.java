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
import wotlas.common.PlayerState;
import wotlas.common.character.AesSedai;
import wotlas.common.character.DarkOne;
import wotlas.common.character.WotCharacter;
import wotlas.common.chat.ChatRoom;
import wotlas.common.message.chat.SendTextMessage;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the SendTextMessage (on the client side)...
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

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl player = dataManager.getMyPlayer();
        boolean fanfare = false;

        // Return of a command ?
        if (this.message.startsWith("/cmd:")) {
            JChatRoom chatRoom = dataManager.getClientScreen().getChatPanel().getJChatRoom(this.chatRoomPrimaryKey);
            chatRoom.appendText("<font color='purple'>" + this.message.substring(5) + "</font>");
            return;
        } else if (this.message.equals("/BELL")) {
            this.message = "/me rings a bell...";
            SoundLibrary.getSoundPlayer().playSound("bell.wav");
        } else if (this.message.startsWith("/FANFARE")) {
            int index = this.message.indexOf(' ');
            if (index < 0 || index == this.message.length() - 1) {
                return;
            }

            String soundFileName = this.message.substring(index + 1, this.message.length()).toLowerCase();
            SoundLibrary.getSoundPlayer().playSound(soundFileName);
            this.message = "/me sounds the fanfare !";
            fanfare = true;
        } else if (this.message.equals("/KNOCK")) {
            this.message = "/me knocks at the door...";
            SoundLibrary.getSoundPlayer().playSound("knock.wav");
        }

        // We get the sender of this message
        Hashtable<String, PlayerImpl> players = dataManager.getPlayers();
        PlayerImpl sender = null;

        if (players != null) {
            sender = players.get(this.senderPrimaryKey);
        }

        if (sender == null) {
            Debug.signal(Debug.WARNING, this, "Couldnot find the sender of this message : " + this.senderPrimaryKey);
        } else {
            if (sender.getWotCharacter() instanceof DarkOne && fanfare) {
                return;
            }
            dataManager.addWaveDrawable(sender);
            this.senderFullName = sender.getFullPlayerName();
        }

        // Is there a modifier in this message ?
        if (this.message.equals("/BLACKAJAH") && this.voiceSoundLevel == ChatRoom.SHOUTING_VOICE_LEVEL) {
            WotCharacter wotC = sender.getWotCharacter();

            if (wotC instanceof AesSedai) {
                if (((AesSedai) wotC).toggleBlackAjah()) {
                    dataManager.getClientScreen().getChatPanel().getCurrentJChatRoom().appendText("<font color='black'><b>[DARK ONE]<i> NOW YOU ARE MINE " + sender.getPlayerName().toUpperCase() + " !</i></b></font>");
                } else {
                    dataManager.getClientScreen().getChatPanel().getCurrentJChatRoom().appendText("<font color='black'><b>[DARK ONE]<i> YOU CAN'T HIDE FROM ME. YOUR SOUL IS MINE " + sender.getPlayerName().toUpperCase() + ".</i></b></font>");
                }
            }

            return;
        } else if (this.message.startsWith("/me")) {
            this.message = "<font color='blue'><i>" + this.senderFullName + " " + this.message.substring(3) + " </i></font>";
        } else if (this.message.startsWith("/away")) {
            this.message = this.message.substring(5);
            if (this.message.length() == 0) {
                // no parameter : player is no longer away
                sender.getPlayerState().value = PlayerState.CONNECTED;
                this.message = "<font color='green'><i>" + this.senderFullName + " is back</i></font>";
            } else {
                // player is away
                sender.getPlayerState().value = PlayerState.AWAY;
                sender.setPlayerAwayMessage(this.message);
                this.message = "<font color='green'><i>" + this.senderFullName + " is away (" + this.message + " )</i></font>";

            }
            dataManager.getClientScreen().getChatPanel().updateAllChatRooms(sender);
        } else if (this.message.startsWith("/to:")) {
            this.message = this.message.substring(4);
            int index = this.message.indexOf(':');

            if (index >= 0 && index + 1 < this.message.length()) {
                String otherPlayerName = this.message.substring(0, index);
                this.message = "<font color='blue'><i>" + this.senderFullName + " says to " + otherPlayerName + this.message.substring(index) + "</i></font>";
            } else {
                this.message = "/to:" + this.message + " <font color='red'>ERROR: bad format</font>";
            }
        } else if (sender != null && sender.getWotCharacter() instanceof DarkOne) {
            // display the message in the "dark one manner..."
            this.message = "<b>[DARK ONE] " + this.message.toUpperCase() + " </b>";
        } else {
            // We add sender name
            this.message = "[" + this.senderFullName + "] " + this.message;
        }

        // We display the message
        if (this.voiceSoundLevel != ChatRoom.SHOUTING_VOICE_LEVEL) {
            JChatRoom chatRoom = dataManager.getClientScreen().getChatPanel().getJChatRoom(this.chatRoomPrimaryKey);

            if (chatRoom != null) {
                chatRoom.addPlayer(this.senderPrimaryKey, this.senderFullName); // we add the player to the member's list
                switch (this.voiceSoundLevel) {
                    case ChatRoom.WHISPERING_VOICE_LEVEL:
                        chatRoom.appendText("<font color='gray'>" + this.message + "</font>"); // if it wasn't already the case
                        break;
                    case ChatRoom.NORMAL_VOICE_LEVEL:
                        chatRoom.appendText(this.message);
                        break;
                }
            } else {
                Debug.signal(Debug.ERROR, this, "No JChatRoom " + this.chatRoomPrimaryKey + " found !");
            }
        } else {
            JChatRoom chatRoom = dataManager.getClientScreen().getChatPanel().getCurrentJChatRoom();
            chatRoom.appendText("<font color='red'>" + this.message + "</font>"); // if it wasn't already the case              
        }

    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
