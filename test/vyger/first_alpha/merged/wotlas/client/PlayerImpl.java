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
package wotlas.client;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import wotlas.common.ImageLibRef;
import wotlas.common.Player;
import wotlas.common.PlayerState;
import wotlas.common.Tickable;
import wotlas.common.WorldManager;
import wotlas.common.character.BasicChar;
import wotlas.common.chat.ChatRoom;
import wotlas.common.message.description.PlayerAwayMessage;
import wotlas.common.message.description.PlayerPastMessage;
import wotlas.common.movement.MovementComposer;
import wotlas.common.movement.PathFollower;
import wotlas.common.objects.ObjectManager;
import wotlas.common.screenobject.PlayerOnTheScreen;
import wotlas.common.universe.Room;
import wotlas.common.universe.TileMap;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2d.Animation;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.drawable.FakeSpriteDataSupplier;
import wotlas.libs.graphics2d.drawable.MultiLineText;
import wotlas.libs.graphics2d.drawable.Sprite;
import wotlas.libs.graphics2d.drawable.SpriteDataSupplier;
import wotlas.libs.graphics2d.drawable.TextDrawable;
import wotlas.libs.graphics2d.drawable.WaveArcDrawable;
import wotlas.libs.graphics2d.filter.BrightnessFilter;
import wotlas.libs.net.NetMessage;
import wotlas.utils.ScreenPoint;

/** Class of a Wotlas Player.
 *
 * @author Petrus, Aldiss, Elann, Diego
 * @see wotlas.common.Player
 */

public class PlayerImpl implements Player, SpriteDataSupplier, FakeSpriteDataSupplier, Tickable {

    /** Period between the display of two away messages for ONE player.
    */
    public final static long AWAY_MSG_DISPLAY_PERIOD = 1000 * 20;

    /*------------------------------------------------------------------------------------*/

    /** Player name
    */
    private String playerName;

    /** Player full name
    */
    private String fullPlayerName;

    /** Player character's past
    */
    private String playerPast;

    /** Player away message.
    */
    private String playerAwayMessage;

    /** Wotlas Character
    */
    private BasicChar wotCharacter;
    /**
     * Object manager
     */
    private ClientObjectManager objectManager;

    /** Player state
    */
    transient private PlayerState playerState = new PlayerState();

    /** Current Chat PrimaryKey : the chat we are currently connected to.
    */
    private String currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT; // everlasting chat set as default

    /** Time stamp for the away message : we don't display the away messages when they
    *  are asked during the AWAY_MSG_DISPLAY_PERIOD.
    */
    transient private long playerAwayMsgTimeStamp;

    /*------------------------------------------------------------------------------------*/

    /** Player's PathFollower for movements...
    */
    private MovementComposer movementComposer = new PathFollower();

    /*------------------------------------------------------------------------------------*/

    /** Our animation.
    */
    private Animation animation;

    /** Our brightness filter.
    */
    private BrightnessFilter brightnessFilter;

    /** Our sprite.
    */
    private Sprite sprite;

    /** Our textDrawable
    */
    private TextDrawable textDrawable;

    /** Our wave arc drawable to show our player talking.
    */
    private WaveArcDrawable waveDrawable;

    /** To get the player name to display on the left of the screen
    */
    private MultiLineText gameScreenFullPlayerName;

    /** Our current TileMap ( if we are in a TileMap, null otherwise )
    */
    private TileMap myTileMap;

    /** Our current Room ( if we are in a Room, null otherwise )
    */
    private Room myRoom;

    /** True if player is moving.
    */
    private boolean isMoving = false;

    /** True if this player is controlled by the client.
    */
    private boolean isMaster = false;

    /** Is this player representing a client that is connected to the game ?
    */
    private boolean isConnectedToGame = false;

    /** SyncID for client & server. See the getter of this field for explanation.
    * This field is an array and not a byte because we want to be able to
    * synchronize the code that uses it.
    */
    private byte syncID[] = new byte[1];

    /*------------------------------------------------------------------------------------*/

    /** Locks
    */
    private byte trajectoryLock[] = new byte[0];
    private byte xLock[] = new byte[0];
    private byte yLock[] = new byte[0];
    private byte angleLock[] = new byte[0];
    private byte imageLock[] = new byte[0];

    /*------------------------------------------------------------------------------------*/

    /** Constructor
    */
    public PlayerImpl() {
    }

    /*------------------------------------------------------------------------------------*/

    /** When this method is called, the player can initialize its own fields safely : all
    *  the game data has been loaded.
    */
    public void init() {
        if (!getLocation().isTileMap()) {
            //Debug.signal( Debug.NOTICE, null, "PlayerImpl::init");
            this.animation = new Animation(this.wotCharacter.getImage(), ClientDirector.getDataManager().getImageLibrary());
            this.sprite = (Sprite) this.wotCharacter.getDrawable(this);
            this.brightnessFilter = new BrightnessFilter();
            this.sprite.setDynamicImageFilter(this.brightnessFilter);
        }
        this.movementComposer.init(this);
    }

    /** Called after graphicsDirector's init to add some visual effects to the master player
    * or to show other players
    */
    public void initVisualProperties(GraphicsDirector gDirector) {

        if (!getLocation().isTileMap()) {
            if (this.isMaster)
                gDirector.addDrawable(this.wotCharacter.getShadow()); // Drawable has already been added
            else {
                gDirector.addDrawable(this.wotCharacter.getDrawable(this));
                gDirector.addDrawable(this.wotCharacter.getShadow());
            }
        } else {
            gDirector.addDrawable(this.wotCharacter.getDrawable(this));
        }
    }

    /** To remove player from the screen
    */
    public void cleanVisualProperties(GraphicsDirector gDirector) {
        if (!this.isMaster) {
            gDirector.removeDrawable(this.wotCharacter.getDrawable(this));
            if (!getLocation().isTileMap()) {
                gDirector.removeDrawable(this.wotCharacter.getShadow());
            }
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the player location.
    *
    *  @return player WotlasLocation
    */
    public WotlasLocation getLocation() {
        return this.getBasicChar().getLocation();
    }

    /** To set the player location.
    *
    *  @param new player WotlasLocation
    */
    public void setLocation(WotlasLocation myLocation) {
        getBasicChar().setLocation(myLocation);

        if (myLocation.isRoom()) {
            this.myRoom = ClientDirector.getDataManager().getWorldManager().getRoom(myLocation);
        } else {
            this.myRoom = null;
        }
        if (myLocation.isTileMap()) {
            this.myTileMap = ClientDirector.getDataManager().getWorldManager().getTileMap(myLocation);
        } else {
            this.myTileMap = null;
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the player name ( short name )
    *
    *  @return player name
    */
    public String getPlayerName() {
        return this.playerName;
    }

    /** To set the player's name ( short name )
    *
    *  @param player name
    */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the player's full name.
    *
    *  @param the key of player who requested player's full name
    *  @return player full name ( should contain the player name )
    */
    public String getFullPlayerName(Player otherPlayer) {
        return this.fullPlayerName;
    }

    public String getFullPlayerName() {
        return this.fullPlayerName;
    }

    /** To set the player's full name.
    *
    *  @param player full name ( should contain the player name )
    */
    public void setFullPlayerName(String fullPlayerName) {
        this.fullPlayerName = fullPlayerName;

        if (this.textDrawable != null)
            this.textDrawable.setText(fullPlayerName);

        if (this.gameScreenFullPlayerName != null) {
            String[] strTemp = { fullPlayerName };
            this.gameScreenFullPlayerName.setText(strTemp);
        }
    }

    /*------------------------------------------------------------------------------------*/

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

    /** To get the player's character.
    *
    *  @return player character
    */
    public BasicChar getBasicChar() {
        return this.wotCharacter;
    }

    /** To set the player's character.
    *
    *  @return WotCharacter player character
    */
    public void setBasicChar(BasicChar wotCharacter) {
        this.wotCharacter = wotCharacter;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * To get the player's object manager
     * 
     * @return player object manager
     */
    public ObjectManager getObjectManager() {
	return this.objectManager;
    }

    /**
     * To set the player's object manager.
     * 
     * @param objectManager
     *                player object manager
     */
    public void setObjectManager(ObjectManager objectManager) {
	this.objectManager = (ClientObjectManager) objectManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    /**
     * To get the player character past.
    *
    *  @return player past
    */
    public String getPlayerPast() {
        if (this.playerPast == null) {
            // we ask for the player's past
            sendMessage(new PlayerPastMessage(getPrimaryKey(), ""));
            this.playerPast = "loading...";
            return this.playerPast;
        }

        return this.playerPast;
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

    /** To get the player away message.
    *
    *  @return player away Message
    */
    public String getPlayerAwayMessage() {
        if (this.isConnectedToGame && this != ClientDirector.getDataManager().getMyPlayer())
            return null;

        if (this.playerAwayMessage == null) {
            sendMessage(new PlayerAwayMessage(getPrimaryKey(), ""));
        }

        return this.playerAwayMessage;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Can we display the Away Message ? (method for the DataManager)
    */
    public boolean canDisplayAwayMessage() {
        long now = System.currentTimeMillis();

        if (this.playerAwayMsgTimeStamp + PlayerImpl.AWAY_MSG_DISPLAY_PERIOD < now) {
            this.playerAwayMsgTimeStamp = now;
            return true;
        }

        return false;
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

    /*** SpriteDataSupplier implementation ***/

    /** To get the X image position.
    *
    * @return x image coordinate
    */
    public int getX() {
        synchronized (this.xLock) {
            return (int) this.movementComposer.getXPosition();
        }
    }

    /** To get the Y image position.
    *
    * @return y image cordinate
    */
    public int getY() {
        synchronized (this.yLock) {
            return (int) this.movementComposer.getYPosition();
        }
    }

    /** To get the image identifier to use.
    *
    * @return image identifier.
    */
    public ImageIdentifier getImageIdentifier() {
        synchronized (this.imageLock) {
            return this.animation.getCurrentImage();
        }
    }

    /** To get the eventual rotation angle. 0 means no rotation.
    *
    * @return angle in radians.
    */
    public double getAngle() {
        synchronized (this.angleLock) {
            return this.movementComposer.getOrientationAngle();
        }
    }

    /** To get the X factor for scaling... 1.0 means no X scaling
    *
    * @return X scale factor
    */
    public double getScaleX() {
        return 1.0;
    }

    /** To get the Y factor for scaling... 1.0 means no Y scaling
    *
    * @return Y scale factor
    */
    public double getScaleY() {
        return 1.0;
    }

    /** To get the image's transparency ( 0.0 means invisible, 1.0 means fully visible ).
    *
    * @return alpha
    */
    public float getAlpha() {
        if (this.isConnectedToGame)
            return 1.0f;
        else
            return 0.5f;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set X.
    * @param x cordinate
    */
    public void setX(int x) {
        synchronized (this.xLock) {
            this.movementComposer.setXPosition(x);
        }
    }

    /** To set Y.
    * @param y cordinate
    */
    public void setY(int y) {
        synchronized (this.yLock) {
            this.movementComposer.setYPosition(y);
        }
    }

    /** To set the angle.
    */
    public void setAngle(double angleRad) {
        synchronized (this.angleLock) {
            this.movementComposer.setOrientationAngle(angleRad);
        }
    }

    /** To set the position.
     */
    public void setPosition(ScreenPoint p) {
        setX(p.x);
        setY(p.y);
    }

    /** To set player's speed
     */
    public void setSpeed(float speed) {
        this.movementComposer.setSpeed(speed);
    }

    /** To get player's speed
     */
    public float getSpeed() {
        return this.movementComposer.getSpeed();
    }

    /** To set player's angular speed
     */
    public void setAngularSpeed(float angularSpeed) {
        this.movementComposer.setAngularSpeed(angularSpeed);
    }

    /** To get player's angular speed
     */
    public float getAngularSpeed() {
        return this.movementComposer.getAngularSpeed();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get destination of trajectory
     */
    public Point getEndPosition() {
        return this.movementComposer.getTargetPosition();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's movement.
     */
    public void moveTo(Point endPoint, WorldManager worldManager) {
        synchronized (this.trajectoryLock) {
            this.movementComposer.moveTo(endPoint, worldManager);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns true if the player is moving
     */
    public boolean isMoving() {
        return this.movementComposer.isMoving();
    }

    /** To stop the player's movement
     */
    public void stopMovement() {
        this.movementComposer.stopMovement();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's drawable
     *  @return player sprite
     */
    public Drawable getDrawable() {
        return this.wotCharacter.getDrawable(this);
    }

    /** To get player's rectangle (to test intersection)
     * it's used in world/town/interior/rooms/tilemaps sprites
     */
    public Rectangle getCurrentRectangle() {
        return this.wotCharacter.getDrawable(this).getRectangle();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Tick
     */
    public void tick() {

        // 1 - Movement Update
        synchronized (this.trajectoryLock) {
            this.movementComposer.tick();
        }

        // 2 - Dynamic Filter Update
        if (this.brightnessFilter != null)
            if (this.myRoom != null)
                this.brightnessFilter.setBrightness(this.movementComposer.getXPosition(), this.movementComposer.getYPosition());

        // 3 - Animation Update
        if (this.animation == null)
            return;

        if (!this.movementComposer.isMoving())
            this.animation.reset();
        else
            this.animation.tick();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Is this player a Master player ? ( directly controlled  by the client )
     * @return true if this is a Master player, false otherwise.
     */
    public boolean isMaster() {
        return this.isMaster;
    }

    /** To set if this player is controlled by the client.
     * @param isMaster true means controlled by the client.
     */
    public void setIsMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's movement Composer.
     *
     *  @return player MovementComposer
     */
    public MovementComposer getMovementComposer() {
        return this.movementComposer;
    }

    /** To set the player's movement Composer.
     *
     *  @param movement MovementComposer.
     */
    public void setMovementComposer(MovementComposer movementComposer) {
        this.movementComposer = movementComposer;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Use this method to send a NetMessage to the server.
     *
     * @param message message to send to the player.   
     */
    public void sendMessage(NetMessage message) {
        ClientDirector.getDataManager().sendMessage(message);
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

    /** To get a text drawable representing the name of this player.
     */
    public Drawable getTextDrawable() {
        if (this.textDrawable != null) {
            if (this.textDrawable.isLive())
                return null;
            //textDrawable.resetTimeLimit();
            //return textDrawable;
        }

        if (this.isConnectedToGame) {
            this.textDrawable = new TextDrawable(this.fullPlayerName, getDrawable(), this.wotCharacter.getColor(), 13.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, 5000);
        } else {
            this.textDrawable = new TextDrawable(this.fullPlayerName + " (away)", getDrawable(), this.wotCharacter.getColor(), 13.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, 5000);
        }
        return this.textDrawable;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a wave drawable on this player.
     */
    public Drawable getWaveArcDrawable() {
        if (this.waveDrawable != null) {
            if (this.waveDrawable.isLive()) {
                this.waveDrawable.reset();
                return null;
            }

            this.waveDrawable.reset();
            return this.waveDrawable;
        }

        if (this.sprite == null)
            return null;

        this.waveDrawable = new WaveArcDrawable(this.sprite, 40, Color.white, ImageLibRef.WAVE_PRIORITY, 1.0f, (byte) 3);
        return this.waveDrawable;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player name to display on the left of the screen
     */
    public MultiLineText getGameScreenFullPlayerName() {
        if (this.gameScreenFullPlayerName == null) {
            String[] strTemp = { this.fullPlayerName };
            this.gameScreenFullPlayerName = new MultiLineText(strTemp, 10, 10, Color.black, 15.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, MultiLineText.LEFT_ALIGNMENT);
        }

        return this.gameScreenFullPlayerName;
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

    /** Is this player connected to the game ? ( not synchronized )
     * @return true if the player is in the game, false if the client is not connected.
     */
    public boolean isConnectedToGame() {
        return this.isConnectedToGame;
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

    /** To set if this player is connected to the game.
     * @param true if the player is in the game, false if the client is not connected.
     */
    public void setIsConnectedToGame(boolean isConnectedToGame) {
        this.isConnectedToGame = isConnectedToGame;
        this.playerState.value = PlayerState.CONNECTED;
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

    /** To set the synchronization ID. See the getter for an explanation on this ID.
    *  @param syncID new syncID
    */
    public void setSyncID(byte syncID) {
        synchronized (this.syncID) {
            this.syncID[0] = syncID;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    public PlayerOnTheScreen getScreenObject() {
        return null;
    }
}