/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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

package wotlas.common.screenobject;

import java.awt.Color;
import wotlas.common.ImageLibRef;
import wotlas.common.Player;
import wotlas.common.action.UserAction;
import wotlas.common.character.CharData;
import wotlas.common.environment.EnvironmentManager;
import wotlas.common.movement.MovementComposer;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.drawable.FakeSprite;
import wotlas.libs.graphics2d.drawable.TextDrawable;
import wotlas.libs.pathfinding.AStarDoubleServer;

/** 
 *
 * @author Diego
 */
public class PlayerOnTheScreen extends ScreenObject {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    transient private Player player;
    // FIXME ???
    // transient private ScreenObjectPathFollower movementComposer;
    private byte trajectoryLock[] = new byte[0];

    /**
     *  empty constructor
     */
    public PlayerOnTheScreen() {
    }

    public PlayerOnTheScreen(Player player, short[] indexOfImage) {
        this.player = player;
        this.x = player.getX();
        this.y = player.getY();
        this.primaryKey = player.getPrimaryKey();
        this.name = player.getPlayerName();
        this.speed = player.getBasicChar().getSpeed();
        this.loc = player.getBasicChar().getLocation();
        this.indexOfImage = indexOfImage;
        this.color = Color.blue;
        player.getBasicChar().setScreenObject(this);
    }

    /** To get a Drawable for this character. This should not be used on the
    *  server side.
    *
    *  The returned Drawable is unique : we always return the same drawable per
    *  AesSedai instance.
    *
    * @param player the player to chain the drawable to. If a XXXDataSupplier is needed
    *               we sets it to this player object.
    * @return a Drawable for this character.
    */
    @Override
    public Drawable getDrawable() {
        if (this.memImage != null)
            return this.memImage;
        this.memImage = new FakeSprite(this, ImageLibRef.PLAYER_PRIORITY, EnvironmentManager.getGraphics(EnvironmentManager.SET_OF_NPC)[this.indexOfImage[0]], this.indexOfImage[1]);
        return this.memImage;
    }

    /** To get the image identifier to use.
     *
     * @return image identifier.
     */
    public ImageIdentifier getImageIdentifier() {
        return new ImageIdentifier();
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean isConnectedToGame() {
        return this.player.isConnectedToGame();
    }

    @Override
    public void setLocation(WotlasLocation loc) {
        this.player.setLocation(loc);
    }

    @Override
    public WotlasLocation getLocation() {
        if (this.player == null)
            return this.loc;
        else
            return getCharData().getLocation();
    }

    @Override
    public byte getTargetType() {
        return UserAction.TARGET_TYPE_PLAYER;
    }

    @Override
    public CharData getCharData() {
        if (this.player == null)
            return null;
        else
            return this.player.getBasicChar();
    }

    @Override
    public void init(GraphicsDirector gDirector) {
        gDirector.addDrawable(getDrawable());

        if (true)
            gDirector.addDrawable(new TextDrawable(getName().toUpperCase(), getDrawable(), getColor(), 10.0f, "Dialog.plain", ImageLibRef.TEXT_PRIORITY, -1));
        //            ,13.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, -1));

        this.trajectoryLock = new byte[0];
		// FIXME ??? movementComposer = new ScreenObjectPathFollower(x,y,0);
        this.movementComposer.init(this);
    }

    @Override
    public void serverInit(AStarDoubleServer aStarDoubleServer) {
        this.trajectoryLock = new byte[0];
        getMovementComposer().init(this);
        getMovementComposer().resetMovement();
    }

    /* - - - - - - - - - - - SYNC ID MANIPULATION - - - - - - - - - - - - - - - - - - - -*/

    /** To get the synchronization ID. This ID is used to synchronize this player on the
    *  client & server side. The ID is incremented only when the player changes its map.
    *  Movement messages that have a bad syncID are discarded.
    * @return sync ID
    */
    @Override
    public byte getSyncID() {
        if (this.player == null)
            synchronized (this.syncID) {
                return this.syncID[0];
            }
        else
            return this.player.getSyncID();
    }

    /** To set the synchronization ID. See the getter for an explanation on this ID.
    *  @param syncID new syncID
    */
    @Override
    public void setSyncID(byte syncID) {
        if (this.player == null)
            synchronized (this.syncID) {
                this.syncID[0] = syncID;
            }
        else
            this.player.setSyncID(syncID);
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public MovementComposer getMovementComposer() {
        if (this.player == null)
            return this.movementComposer;
        else
            return this.player.getMovementComposer();
    }

    /** Tick
    */
    @Override
    public void tick() {
        // 1 - Movement Update
        synchronized (this.trajectoryLock) {
            getMovementComposer().tick();
        }
    }

    /** Tick on the Server
    * return true if the object should be removed from the hashtable 
    */
    @Override
    public boolean serverTick() {
        // 1 - Movement Update
        // System.out.print("path-> x,y:["+getX()+","+getY()+"]  - ");
        synchronized (this.trajectoryLock) {
            getMovementComposer().tick();
        }
        // System.out.println("end-> x,y:["+getX()+","+getY()+"] ");
        return false;
    }

    // FIXME ???
    /**
     * To get the X image position.
     * 
     * @return x image cordinate
     */
    // public int getX() {
    // if( movementComposer == null )
    // return x;
    // else
    // return (int)movementComposer.getXPosition();
    // }
    /**
     * To get the Y image position.
     * 
     * @return y image cordinate
     */
    // public int getY() {
    // if( movementComposer == null )
    // return y;
    // else
    // return (int)movementComposer.getYPosition();
    // }
}