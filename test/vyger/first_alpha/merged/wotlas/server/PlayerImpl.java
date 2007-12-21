/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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
package wotlas.server;

import wotlas.common.Player;
import wotlas.common.PlayerState;
import wotlas.common.ServerConfig;
import wotlas.common.WorldManager;
import wotlas.common.character.BasicChar;
import wotlas.common.chat.ChatList;
import wotlas.common.chat.ChatRoom;
import wotlas.common.message.chat.SendTextMessage;
import wotlas.common.message.description.PlayerConnectedToGameMessage;
import wotlas.common.movement.MovementComposer;
import wotlas.common.movement.ServerPathFollower;
import wotlas.common.router.MessageRouter;
import wotlas.common.screenobject.PlayerOnTheScreen;
import wotlas.common.universe.Room;
import wotlas.common.universe.TileMap;
import wotlas.common.universe.TownMap;
import wotlas.common.universe.WorldMap;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.net.NetMessage;
import wotlas.libs.persistence.BackupReady;
import wotlas.server.message.chat.RemPlayerFromChatRoomMsgBehaviour;
import wotlas.utils.Debug;
import wotlas.utils.Tools;

/** Class of a Wotlas Player. It is the class that, in certain way, a client gets connected to.
 *  All the client messages have a server PlayerImpl context.
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.common.Player
 * @see wotlas.common.NetConnectionListener
 */

public class PlayerImpl implements Player, NetConnectionListener, BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /** Period between two focus sounds. Focus sounds can be send by players two draw
    *  attention.
    */
    protected static final int FOCUS_SOUND_PERIOD = 1000 * 10; // 10s between two sounds

    /*------------------------------------------------------------------------------------*/

    /** Player name ( in fact it's nickname... )
    */
    protected String playerName;

    /** Player character's past
    */
    protected String playerPast;

    /** Player away message.
    */
    protected String playerAwayMessage;

    /** WotCharacter Class
    */
    protected BasicChar wotCharacter;

    /** Player state
    */
    protected PlayerState playerState = new PlayerState();

    /** Movement Composer
    */
    //    protected MovementComposer movementComposer = (MovementComposer) new PathFollower();
    protected MovementComposer movementComposer = new ServerPathFollower();

    /** Last time player disconnected (in days)
    */
    protected long lastDisconnectedTime;

    /*------------------------------------------------------------------------------------*/

    /** Our NetConnection, useful if we want to send messages !
    */
    transient protected NetConnection connection;

    /** Our current Room ( if we are in a Room, null otherwise )
    */
    transient protected Room myRoom;

    /** Our current TileMap ( if we are in a TileMap, null otherwise )
    */
    transient protected TileMap myTileMap;

    /** SyncID for client & server. See the getter of this field for explanation.
    * This field is an array and not a byte because we want to be able to
    * synchronize the code that uses it.
    */
    transient protected byte syncID[] = new byte[1];

    /*------------------------------------------------------------------------------------*/

    /** Player ChatRooms : is the list of the current chat room.
    */
    transient protected ChatList chatList;

    /** Current Chat PrimaryKey : the chat we are currently looking.
    */
    transient protected String currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT; // everlasting chat set as default

    /** are we a member of this chat ? or just eavesdropping ?
    */
    transient protected boolean isChatMember = true; //always member on default chat.

    /** Last time this player was grant the possibility to send a focus sound to players.
    *
    *  The period between two focus sound is set by the static FOCUS_SOUND_PERIOD.
    */
    transient protected long focusSoundTimeStamp;

    /*------------------------------------------------------------------------------------*/

    /** Connection Lock
    */
    transient protected byte connectionLock[] = new byte[0];

    /** ChatList Lock
    */
    transient protected byte chatListLock[] = new byte[0];

    /*------------------------------------------------------------------------------------*/

    /** Constructor for persistence.
    */
    public PlayerImpl() {
        this.playerAwayMessage = new String("I'm not here for the moment...");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** When this method is called, the player can intialize its own fields safely : all
    *  the game data has been loaded.
    */
    public void init() {
        // done to reinit vars. : set myTileMap or myRoom
        setLocation(getLocation());
        this.movementComposer.init(this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** If you call this method all the local data will be replaced by the given
    *  player's one.
    */
    public void clone(PlayerImpl playerToClone) {
        setBasicChar(playerToClone.getBasicChar());
        setLocation(playerToClone.getLocation());
        setPlayerName(playerToClone.getPlayerName());
        setPrimaryKey(playerToClone.getPrimaryKey());

        setPlayerPast(playerToClone.getPlayerPast());
        setPlayerAwayMessage(playerToClone.getPlayerAwayMessage());

        setMovementComposer(playerToClone.getMovementComposer());
        this.movementComposer.init(this);

        setChatList(playerToClone.getChatList());
        setCurrentChatPrimaryKey(playerToClone.getCurrentChatPrimaryKey());
        setIsChatMember(playerToClone.isChatMember());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To initialize the player location to the first existent world found.
    *  WARNING : the player is NOT moved to the world... thus this method
    *  is for player creation ONLY.
    */
    public void setDefaultPlayerLocation() {
        // 1 - player initial location : a World...
        WorldManager worldManager = ServerDirector.getDataManager().getWorldManager();
        
        int worldID = worldManager.getAValidWorldID();
        
        if (worldID < 0)
            Debug.signal(Debug.CRITICAL, this, "No world data given to initialize player.");
        else if (worldID != 0)
            Debug.signal(Debug.WARNING, this, "The default world isn't the first in the list... hope you are aware of that...");

        setLocation(new WotlasLocation(worldID));

        // we retrieve the default position.
        ServerConfig cfg = ServerDirector.getServerManager().getServerConfig();
        setX(cfg.getWorldFirstXPosition());
        setY(cfg.getWorldFirstYPosition());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Is this player a Master player ? ( directly controlled  by the client )
    * @return true if this is a Master player, false otherwise.
    */
    public boolean isMaster() {
        return false; // Server PlayerImpl is only a slave player implementation
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's X position.
    *
    *  @return x
    */
    public int getX() {
        return (int) this.movementComposer.getXPosition();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's Y position.
    *
    *  @return y
    */
    public int getY() {
        return (int) this.movementComposer.getYPosition();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's X position.
    *
    *  @param x
    */
    public void setX(int x) {
        this.movementComposer.setXPosition(x);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's Y position.
    */
    public void setY(int y) {
        this.movementComposer.setYPosition(y);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's orientation.
    */
    public void setOrientation(float orientation) {
        this.movementComposer.setOrientationAngle(orientation);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's orientation.
    */
    public float getOrientation() {
        return (float) this.movementComposer.getOrientationAngle();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player location.
    *
    *  @return player WotlasLocation
    */
    public WotlasLocation getLocation() {
        return new WotlasLocation(getBasicChar().getLocation());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player location.
     *
     *  @param new player WotlasLocation
     */
    public void setLocation(WotlasLocation myLocation) {
        getBasicChar().setLocation(myLocation);
        
        if (myLocation.isRoom() && ServerDirector.getDataManager() != null)
            this.myRoom = ServerDirector.getDataManager().getWorldManager().getRoom(myLocation);
        else {
            if (myLocation.isRoom())
                Debug.signal(Debug.CRITICAL, this, "Room not found !!! location is:" + myLocation);
            this.myRoom = null;
        }
        
        if (myLocation.isTileMap() && ServerDirector.getDataManager() != null)
            this.myTileMap = ServerDirector.getDataManager().getWorldManager().getTileMap(myLocation);
        else {
            if (myLocation.isTileMap())
                Debug.signal(Debug.CRITICAL, this, "TileMap not found !!! location is:" + myLocation);
            this.myTileMap = null;
        }
        
        // Current Chat set to default
        this.currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT;
        this.isChatMember = false;
        synchronized (this.chatListLock) {
            this.chatList = null;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player name ( short name )
    *
    *  @return player name
    */
    public String getPlayerName() {
        return this.playerName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's full name
    */
    public String getFullPlayerName(String otherPlayerKey) {
        //        return lieManager.getCurrentFakeName();
        return "fullname-should be implremented";
    }

    /** To get the player's full name
    */
    public String getFullPlayerName() {
        return "fullname-should be implremented";
    }

    /** To get the player's full name or fake name
    *
    * @return player full name
    */
    public String getFullPlayerName(PlayerImpl otherPlayer) {
        return getFullPlayerName();
    }

    public String getFullPlayerName(Player otherPlayer) {
        return getFullPlayerName((PlayerImpl) otherPlayer);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /*** Player implementation ***/

    /** To get the player primary Key ( account name )
    *
    *  @return player primary key
    */
    public String getPrimaryKey() {
        return getBasicChar().getPrimaryKey();
    }

    /** To set the player's primary key.
    *
    *  @param primary key
    */
    public void setPrimaryKey(String primaryKey) {
        getBasicChar().setPrimaryKey(primaryKey);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player character past.
    *
    *  @return player past
    */
    public String getPlayerPast() {
        return this.playerPast;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's name ( short name )
     *
     *  @param player name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's past.
    *
    *  @param playerPast past
    */
    public void setPlayerPast(String playerPast) {
        this.playerPast = playerPast;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's full name.
    *
    *  @param player full name ( should contain the player name )
    */
    public void setFullPlayerName(String fullPlayerName) {
        // lieManager.setFullPlayerName(fullPlayerName);          
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's character.
    *
    *  @return player character
    */
    public BasicChar getBasicChar() {
        return this.wotCharacter;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's character.
    *
    *  @param wotCharacter new player character
    */
    public void setBasicChar(BasicChar wotCharacter) {
        this.wotCharacter = wotCharacter;
        this.wotCharacter.init();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's movement Composer.
    *
    *  @return player MovementComposer
    */
    public MovementComposer getMovementComposer() {
        return this.movementComposer;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's movement Composer.
    *
    *  @param movement MovementComposer.
    */
    public void setMovementComposer(MovementComposer movementComposer) {
        this.movementComposer = movementComposer;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get lastDisconnectedTime.
    */
    public long getLastDisconnectedTime() {
        return this.lastDisconnectedTime;
    }

    /** To set lastDisconnectedTime.
    */
    public void setLastDisconnectedTime(long lastTime) {
        this.lastDisconnectedTime = lastTime;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's current Room ( if we are in a Room ).
    * @return current Room, null if we are not in a room.
    */
    public Room getMyRoom() {
        return this.myRoom;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's current TileMap ( if we are in a TileMap ).
    * @return current TileMap, null if we are not in a tileMap.
    */
    public TileMap getMyTileMap() {
        return this.myTileMap;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get our Message router.
     */
    public MessageRouter getMessageRouter() {
        if (this.myRoom != null)
            return this.myRoom.getMessageRouter();
        if (this.myTileMap != null)
            return this.myTileMap.getMessageRouter();

        if (getLocation().isWorld()) {
            WorldMap world = ServerDirector.getDataManager().getWorldManager().getWorldMap(getLocation());
            if (world != null)
                return world.getMessageRouter();
        } else if (getLocation().isTown()) {
            TownMap town = ServerDirector.getDataManager().getWorldManager().getTownMap(getLocation());
            if (town != null)
                return town.getMessageRouter();
        } else if (getLocation().isRoom()) {
            Room room = ServerDirector.getDataManager().getWorldManager().getRoom(getLocation());
            if (room != null)
                return room.getMessageRouter();
        } else if (getLocation().isTileMap()) {
            TileMap tileMap = ServerDirector.getDataManager().getWorldManager().getTileMap(getLocation());
            if (tileMap != null)
                return tileMap.getMessageRouter();
        }

        Debug.signal(Debug.ERROR, this, "MessageRouter not found ! bad location :" + getLocation() + " player: " + getPrimaryKey());
        return null; // not found
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player away message.
    *
    *  @return player away Message
    */
    public String getPlayerAwayMessage() {
        return this.playerAwayMessage;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's away message.
    *
    *  @param playerAwayMessage msg
    */
    public void setPlayerAwayMessage(String playerAwayMessage) {
        this.playerAwayMessage = playerAwayMessage;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the synchronization ID. This ID is used to synchronize this player on the
    *  client & server side. The ID is incremented only when the player changes its map.
    *  Movement messages that have a bad syncID are discarded.
    * @return sync ID
    */
    public byte getSyncID() {
        synchronized (this.syncID) {
            return this.syncID[0];
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To update the synchronization ID. See the getter for an explanation on this ID.
    *  The new updated syncID is (syncID+1)%100.
    */
    public void updateSyncID() {
        synchronized (this.syncID) {
            this.syncID[0] = (byte) ((this.syncID[0] + 1) % 100);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the synchronization ID. See the getter for an explanation on this ID.
    *  @param syncID new syncID
    */
    public void setSyncID(byte syncID) {
        synchronized (this.syncID) {
            this.syncID[0] = syncID;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's chatList.
    */
    public void setChatList(ChatList chatList) {
        synchronized (this.chatListLock) {
            this.chatList = chatList;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's chatList.
    */
    public ChatList getChatList() {
        synchronized (this.chatListLock) {
            return this.chatList;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the primary key of the chat the player is now using.
    * @return currentChatPrimaryKey
    */
    public String getCurrentChatPrimaryKey() {
        return this.currentChatPrimaryKey;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the current chat used
    * @param currentChatPrimaryKey
    */
    public void setCurrentChatPrimaryKey(String currentChatPrimaryKey) {
        this.currentChatPrimaryKey = currentChatPrimaryKey;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** are we a member of this chat ? or just eavesdropping ?
    * @return true if we are a real member of the current chat
    */
    public boolean isChatMember() {
        return this.isChatMember;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** are we a member of this chat ? or just eavesdropping ?
    * @param isChatMember if we are a real member of the current chat set it to true.
    */
    public void setIsChatMember(boolean isChatMember) {
        this.isChatMember = isChatMember;
    }

    /*------------------------------------------------------------------------------------*/

    /** Is this player connected to the game ? ( not synchronized )
    * @return true if the player is in the game, false if the client is not connected.
    */
    public boolean isConnectedToGame() {
        if (this.connection == null)
            return false;
        return true;
    }

    /** To get the player's state (disconnected/connected/away)
    *
    * @return player state
    */
    public PlayerState getPlayerState() {
        return this.playerState;
    }

    /** To set the player's state (disconnected/connected/away)
    *
    * @param playerState player state
    */
    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set if this player is connected to the game. (not used on this server side )
    * @param isConnected true if the player is in the game, false if the client is not connected.
    */
    public void setIsConnectedToGame(boolean isConnected) {
        // no external update possible on the server side !
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called when a new network connection is created on this player.
     *
     * @param connection the NetConnection object associated to this connection.
     */
    public void connectionCreated(NetConnection connection) {

        synchronized (this.connectionLock) {
            this.connection = connection;
        }

        // We update our state
        this.playerState.value = PlayerState.CONNECTED;

        // We signal our connection to players in the game
        // ... and players in the rooms near us
        if (getLocation().isRoom()) {
            if (this.myRoom == null) {
                Debug.signal(Debug.ERROR, this, "Player " + getPrimaryKey() + " has an incoherent location state");
                return;
            }

            // are we present in this room already ?
            if (this.myRoom.getMessageRouter().getPlayer(getPrimaryKey()) != null) {
                // We send an update to players near us...                      
                PlayerConnectedToGameMessage pMsg = new PlayerConnectedToGameMessage(getPrimaryKey(), true);
                this.myRoom.getMessageRouter().sendMessage(pMsg, this, MessageRouter.EXTENDED_GROUP);
            }
        }

        // We signal our connection to players in the game
        if (getLocation().isTileMap()) {
            if (this.myTileMap == null) {
                Debug.signal(Debug.ERROR, this, "Player " + getPrimaryKey() + " has an incoherent location state");
                return;
            }

            // are we present in this TileMap already ?
            if (this.myTileMap.getMessageRouter().getPlayer(getPrimaryKey()) != null) {
                // We send an update to players near us...                      
                PlayerConnectedToGameMessage pMsg = new PlayerConnectedToGameMessage(getPrimaryKey(), true);
                this.myTileMap.getMessageRouter().sendMessage(pMsg, this, MessageRouter.EXTENDED_GROUP);
            }
        }

        Debug.signal(Debug.NOTICE, null, "Connection opened for player " + this.playerName + " at " + Tools.getLexicalTime());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** This method is called when the network connection of the client is no longer
     * of this world.
     *
     * @param connection the NetConnection object associated to this connection.
     */
    public void connectionClosed(NetConnection connection) {

        // 0 - no more messages will be sent...
        synchronized (this.connectionLock) {
            this.connection = null;
        }
        this.lastDisconnectedTime = System.currentTimeMillis();

        // 0.1 - We update our state
        this.playerState.value = PlayerState.DISCONNECTED;

        Debug.signal(Debug.NOTICE, null, "Connection closed on player: " + this.playerName + " at " + Tools.getLexicalTime());

        // 1 - Leave any current chat...
        if (!this.currentChatPrimaryKey.equals(ChatRoom.DEFAULT_CHAT)) {
            RemPlayerFromChatRoomMsgBehaviour remPlayerFromChat = new RemPlayerFromChatRoomMsgBehaviour(getPrimaryKey(), this.currentChatPrimaryKey);

            try {
                remPlayerFromChat.doBehaviour(this);
            } catch (Exception e) {
                Debug.signal(Debug.ERROR, this, e);
                this.currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT;
            }
        }

        synchronized (this.chatListLock) {
            this.chatList = null;
        }

        // 2 - Stop any current movement
        if (getLocation().isRoom()) {

            // no movement saved on rooms...
            this.movementComposer.resetMovement();

            // We send an update to players near us...
            // ... and players in other rooms
            NetMessage msg[] = new NetMessage[2];
            msg[0] = this.movementComposer.getUpdate();
            msg[1] = new PlayerConnectedToGameMessage(getPrimaryKey(), false);

            if (this.myRoom == null) {
                Debug.signal(Debug.ERROR, this, "Player " + getPrimaryKey() + " has an incoherent location state");
                return;
            }

            this.myRoom.getMessageRouter().sendMessages(msg, this, MessageRouter.EXTENDED_GROUP);
            return;
        } else if (getLocation().isTown()) {
            // no movement saved on towns...
            this.movementComposer.resetMovement();
            return;
        } else if (getLocation().isTileMap()) {

            // no movement saved on tileMap...
            this.movementComposer.resetMovement();

            // We send an update to players near us...
            NetMessage msg[] = new NetMessage[2];
            msg[0] = this.movementComposer.getUpdate();
            msg[1] = new PlayerConnectedToGameMessage(getPrimaryKey(), false);

            if (this.myTileMap == null) {
                Debug.signal(Debug.ERROR, this, "Player " + getPrimaryKey() + " has an incoherent location state");
                return;
            }

            this.myTileMap.getMessageRouter().sendMessages(msg, this, MessageRouter.EXTENDED_GROUP);
            return;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Use this method to send a NetMessage to this player. You can use it directly :
    *  it does not lock, does not wait for the message to be sent before returning
    *  AND checks that the player is connected.
    *
    * @param message message to send to the player.
    */
    public void sendMessage(NetMessage message) {
        synchronized (this.connectionLock) {
            if (this.connection != null) {
                if (ServerDirector.SHOW_DEBUG)
                    System.out.println("Player " + getPrimaryKey() + " sending msg: " + message);
                this.connection.queueMessage(message);
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Use this method to send a SendTextMessage to this player. You can use it directly :
    *  it does not lock, does not wait for the message to be sent before returning
    *  AND checks that the player is connected.
    *
    * @param message message to send to the player.
    * @param otherPlayerKey key of player who sent the message
    */
    public void sendChatMessage(SendTextMessage message, PlayerImpl otherPlayer) {
        synchronized (this.connectionLock) {
            if (this.connection != null) {
                if (ServerDirector.SHOW_DEBUG)
                    System.out.println("Player " + getPrimaryKey() + " sending to:" + otherPlayer.getPrimaryKey() + " msg: " + message);
                this.connection.queueMessage(message);
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To close the network connection if any.
    */
    public void closeConnection() {
        synchronized (this.connectionLock) {
            if (this.connection != null)
                this.connection.close();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To ask the grant to send a focus sound... see FOCUS_SOUND_PERIOD definition for
    *  more information.
    *
    *  @return true if you can send the sound, false otherwise.
    */
    public boolean askGrantAccessToSendFocusSound() {
        long now = System.currentTimeMillis();
        if (this.focusSoundTimeStamp + PlayerImpl.FOCUS_SOUND_PERIOD < now) {
            this.focusSoundTimeStamp = now;
            return true; // grant accepted
        }
        return false; // grant rejected
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Redirects the network listener. USE WITH CARE !! should be only used by the
    *  BotFactory.
    */
    public void removeConnectionListener() {
        synchronized (this.connectionLock) {
            if (this.connection != null)
                this.connection.removeConnectionListener(this);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** write object data with serialize.
    */
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeUTF(this.playerName);
        objectOutput.writeUTF(this.playerPast);
        objectOutput.writeUTF(this.playerAwayMessage);
        objectOutput.writeObject(this.wotCharacter);
        objectOutput.writeLong(this.lastDisconnectedTime);

        objectOutput.writeObject(this.playerState);
        objectOutput.writeObject(this.movementComposer);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
    */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.playerName = objectInput.readUTF();
            this.playerPast = objectInput.readUTF();
            this.playerAwayMessage = objectInput.readUTF();
            this.wotCharacter = (BasicChar) objectInput.readObject();
            this.lastDisconnectedTime = objectInput.readLong();
            ;

            this.playerState = (PlayerState) objectInput.readObject();
            this.movementComposer = (MovementComposer) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** id version of data, used in serialized persistance.
    */
    public int ExternalizeGetVersion() {
        return 1;
    }

    private PlayerOnTheScreen playerOnTheScreen;

    /** called by router or 
    *
    */
    public PlayerOnTheScreen getScreenObject() {
        short[] indexOfImage = { (short) 0, (short) 0 };
        if (this.playerOnTheScreen == null)
            this.playerOnTheScreen = new PlayerOnTheScreen(this, indexOfImage);
        return this.playerOnTheScreen;
    }
}