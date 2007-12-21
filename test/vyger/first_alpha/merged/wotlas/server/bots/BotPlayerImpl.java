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

package wotlas.server.bots;

import wotlas.common.chat.ChatRoom;
import wotlas.common.message.chat.AddPlayerToChatRoomMessage;
import wotlas.common.message.chat.RemPlayerFromChatRoomMessage;
import wotlas.common.message.chat.SendTextMessage;
import wotlas.common.message.description.PlayerConnectedToGameMessage;
import wotlas.common.router.MessageRouter;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetMessage;
import wotlas.server.PlayerImpl;
import wotlas.server.ServerDirector;
import wotlas.server.message.chat.ChatRoomCreationMsgBehaviour;
import wotlas.server.message.chat.RemPlayerFromChatRoomMsgBehaviour;
import wotlas.utils.Debug;

/** A simple Bot that does not move and only send answers to chat messages.
 *
 * @author Aldiss
 * @see wotlas.server.bots.BotPlayer
 */

public class BotPlayerImpl extends PlayerImpl implements BotPlayer {

    /** character location
    */
    private WotlasLocation location;
    /** primaryKey was removed by Diego, from playerImpl, 'cause it's managed 
     * in another part of the code (characters) so even NPC have their primaryKey
     * so to make this code work, it's created here for the bots*/
    private String primaryKey;

    /*------------------------------------------------------------------------------------*/

    /** Our default chat room name. This field is made persistent.
     *  When the bot is created it automatically creates this default chat room.
     */
    private String defaultChatRoomName;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public BotPlayerImpl() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** When this method is called, the bot can intialize its fields safely.
     *  Always call the super.init() method.
     */
    @Override
    public void init() {
        super.init();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** If you call this method all the local data will be replaced by the given
    *  player's one.
    */
    @Override
    public void clone(PlayerImpl playerToClone) {
        super.clone(playerToClone);
        ChatRoom ourChatRoom = null;

        if (this.chatList != null)
            ourChatRoom = this.chatList.getChatRoom(this.currentChatPrimaryKey);
        if (ourChatRoom == null || !ourChatRoom.getCreatorPrimaryKey().equals(this.primaryKey))
            this.defaultChatRoomName = this.playerName; // bot creator forgot to create a default chat for the bot, we create one
        else
            this.defaultChatRoomName = ourChatRoom.getName(); // get chatroom's name
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the bot's default chat room name (for persistence only).
    */
    public String getDefaultChatRoomName() {
        return this.defaultChatRoomName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the bot's default chat room name (for persistence only).
     */
    public void setDefaultChatRoomName(String defaultChatRoomName) {
        this.defaultChatRoomName = defaultChatRoomName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Is this player a Master player ? ( directly controlled  by the client )
     * @return true if this is a Master player, false otherwise.
     */
    @Override
    public boolean isMaster() {
        return false; // a bot has no master player
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Is this player connected to the game ? ( not synchronized )
     * @return true if the player is in the game, false if the client is not connected.
     */
    @Override
    public boolean isConnectedToGame() {

        if (super.isConnectedToGame())
            return true;

        BotChatService chatService = ServerDirector.getDataManager().getBotManager().getBotChatService();

        if (chatService != null && chatService.isAvailable())
            return true;

        return false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set if this player is connected to the game.
     *
     *  BOTS : This method can be called by the BotChatService when it becomes available or
     *  unavailable. This methods advertises the bots state change to near players.
     *
     * @param isConnected this parameter is not used here, it is directly checked at the
     *        BotChatService.isAvailable method...
     */
    @Override
    public void setIsConnectedToGame(boolean isConnected) {

        if (super.isConnectedToGame())
            return;

        // We check our real state (we had to stay compatible with the method signature)
        if (isConnected != isConnectedToGame()) {
            isConnected = !isConnected;
            Debug.signal(Debug.WARNING, this, "Bad 'isConnected' value avoided...");
        }

        // if it's a transition from connected to not connected we
        // reset our state
        if (!isConnected) {
            this.lastDisconnectedTime = System.currentTimeMillis();
            this.movementComposer.resetMovement();
        }

        // we signal our change to our neighbours
        if (this.location.isRoom()) {
            // 1 - myRoom & MessageRouter check
            if (this.myRoom == null) {
                Debug.signal(Debug.ERROR, this, "Bot " + this.primaryKey + " has an incoherent location state");
                return;
            } else if (this.myRoom.getMessageRouter() == null) {
                Debug.signal(Debug.ERROR, this, "Message Router not found for bot " + this.primaryKey);
                return;
            }

            // 2 - We send an update to players near us...
            // ... and players in other rooms
            if (!isConnected)
                this.myRoom.getMessageRouter().sendMessage(this.movementComposer.getUpdate(), this, MessageRouter.EXTENDED_GROUP);

            // 3 - We check that we are a member of the given Message Router
            if (this.myRoom.getMessageRouter().getPlayer(this.primaryKey) != null) {
                // We send an update to players near us...
                PlayerConnectedToGameMessage pMsg = new PlayerConnectedToGameMessage(this.primaryKey, isConnected);
                this.myRoom.getMessageRouter().sendMessage(pMsg, this, MessageRouter.EXTENDED_GROUP);
            }

            // 4 - We create or delete local chat room
            if (isConnected) {

                if (this.defaultChatRoomName == null) {
                    Debug.signal(Debug.ERROR, this, "Bot has no default chat !");
                    this.defaultChatRoomName = this.playerName;
                }

                // we create our bot's default chat room
                ChatRoomCreationMsgBehaviour roomCreation = new ChatRoomCreationMsgBehaviour(this.defaultChatRoomName, this.primaryKey, true);

                try {
                    roomCreation.doBehaviour(this);
                } catch (Exception e) {
                    Debug.signal(Debug.ERROR, this, "Failed to create default chat room for bot...");
                }
            } else if (!this.currentChatPrimaryKey.equals(ChatRoom.DEFAULT_CHAT)) {
                // we quit our current chat
                RemPlayerFromChatRoomMsgBehaviour remPlayerFromChat = new RemPlayerFromChatRoomMsgBehaviour(this.primaryKey, this.currentChatPrimaryKey);

                try {
                    remPlayerFromChat.doBehaviour(this);
                } catch (Exception e) {
                    Debug.signal(Debug.ERROR, this, e);
                    this.currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT;
                }
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method does nothing here. It only produces an error message.
     */
    @Override
    public void connectionCreated(NetConnection connection) {
        super.connectionCreated(connection);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method does nothing here. It only produces an error message.
     */
    @Override
    public void connectionClosed(NetConnection connection) {
        super.connectionClosed(connection);
        setIsConnectedToGame(isConnectedToGame());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Use this method to send a NetMessage to this bot. You can use it directly :
     *  it does not lock, does not wait for the message to be sent before returning.
     *
     *  BOT : we check the type of the message and react to special messages.
     *
     * @param message message to send to the bot.
     */
    @Override
    public void sendMessage(NetMessage message) {
        super.sendMessage(message);

        // 1 - need to react ?
        if (!isConnectedToGame())
            return;

        // 2 - what type of message ?
        //     we use the instanceof operator here because we don't want to modify the code
        //     of the classes that invoke sendMessage(). This way our bot code remains
        //     integrated to the bots package.
        if (message instanceof AddPlayerToChatRoomMessage) {
            AddPlayerToChatRoomMessage msg = (AddPlayerToChatRoomMessage) message;

            if (this.currentChatPrimaryKey.equals(msg.getChatRoomPrimaryKey())) {
                // ok, new player has entered our chat
                // we open a botchatservice session with him
                PlayerImpl player = (PlayerImpl) getMessageRouter().getPlayer(msg.getSenderPrimaryKey());

                if (player == null) {
                    Debug.signal(Debug.ERROR, this, "Player " + msg.getSenderPrimaryKey() + " not found ! can't open session !");
                    return; // player not found
                }

                ServerDirector.getDataManager().getBotManager().openChatBotSession(this, player);

                // we turn toward the new comer
                if (this.location.isRoom()) {
                    this.movementComposer.resetMovement();
                    this.movementComposer.setOrientationAngle(player.getMovementComposer().getOrientationAngle() + Math.PI);

                    if (this.myRoom == null) {
                        Debug.signal(Debug.ERROR, this, "Bot " + this.primaryKey + " has an incoherent location state");
                        return;
                    }

                    this.myRoom.getMessageRouter().sendMessage(this.movementComposer.getUpdate(), this, MessageRouter.EXTENDED_GROUP);
                }
            }
        } else if (message instanceof RemPlayerFromChatRoomMessage) {
            RemPlayerFromChatRoomMessage msg = (RemPlayerFromChatRoomMessage) message;

            if (this.currentChatPrimaryKey.equals(msg.getChatRoomPrimaryKey())) {
                // ok, a player has left our chat
                // we close his botchatservice session
                PlayerImpl player = (PlayerImpl) getMessageRouter().getPlayer(msg.getSenderPrimaryKey());

                if (player == null) {
                    Debug.signal(Debug.ERROR, this, "Player " + msg.getSenderPrimaryKey() + " not found ! can't close session !");
                    return; // player not found
                }

                ServerDirector.getDataManager().getBotManager().closeChatBotSession(this, player);
            }

        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Use this method to send a chat message to this bot. You can use it directly :
     *  it does not lock, does not wait for the message to be sent before returning.
     *
     * @param message message to send to the player.
     * @param otherPlayerKey key of player who sent the message
     */
    @Override
    public void sendChatMessage(SendTextMessage message, PlayerImpl otherPlayer) {
        super.sendMessage(message);

        // 1 - We don't talk to other bots & chat groups... (security)
        if (otherPlayer instanceof BotPlayer) {
            Debug.signal(Debug.WARNING, this, "Bot " + otherPlayer.getPrimaryKey() + " tried to talk to " + this.primaryKey);
            return;
        }

        if (!message.getChatRoomPrimaryKey().equals(this.currentChatPrimaryKey))
            return;

        // 2 - If the BotChatService is available we ask for an answer
        BotChatService chatService = ServerDirector.getDataManager().getBotManager().getBotChatService();

        if (chatService == null || !chatService.isAvailable())
            return;

        chatService.askForAnswer(message.getMessage(), otherPlayer, this);

    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To send an answer from this bot to its local group.
     *  @param message chat message to send.
     */
    public void sendChatAnswer(String message) {

        SendTextMessage tMsg = new SendTextMessage(this.primaryKey, this.playerName, this.currentChatPrimaryKey, message, ChatRoom.NORMAL_VOICE_LEVEL);
        getMessageRouter().sendMessage(tMsg, this);
        super.sendMessage(tMsg);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}