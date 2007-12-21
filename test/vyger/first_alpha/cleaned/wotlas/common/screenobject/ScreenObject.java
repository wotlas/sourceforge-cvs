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

package wotlas.common.screenobject;

import java.awt.Color;
import wotlas.common.action.UserAction;
import wotlas.common.character.CharData;
import wotlas.common.movement.MovementComposer;
import wotlas.common.movement.ScreenObjectPathFollower;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.drawable.FakeSprite;
import wotlas.libs.graphics2d.drawable.FakeSpriteDataSupplier;
import wotlas.libs.pathfinding.AStarDoubleServer;
import wotlas.libs.persistence.BackupReady;
import wotlas.libs.persistence.SendObjectReady;

/**  ScreenObject :
 * This object is created by the server, then it's sended to the client
 * by the MultiGroupMessageRouterForTileMap
 *
 * Every ScreenObject should have all the information so the client can show it
 * without know all of what is behind (player,item and npc data)
 * So the data behind can keept secret!
 *
 * @author Diego
 */
public abstract class ScreenObject implements FakeSpriteDataSupplier, SendObjectReady, BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    transient protected FakeSprite memImage;
    transient protected boolean isServerSide = true;
    transient protected MovementComposer movementComposer;
    protected int x, y;
    protected WotlasLocation loc;
    protected String primaryKey;
    protected String name;
    protected float speed;
    protected Color color;
    protected short[] indexOfImage;

    /** SyncID for client & server. See the getter of this field for explanation.
    * This field is an array and not a byte because we want to be able to
    * synchronize the code that uses it.
    */
    transient protected byte syncID[] = new byte[1];

    /*---------------all the abstract functions to implement------------------------------*/

    abstract public void setLocation(WotlasLocation loc);

    abstract public boolean isConnectedToGame();

    abstract public byte getTargetType();

    abstract public CharData getCharData();

    /** To get the player's drawable
    *  @return player sprite
    */
    abstract public Drawable getDrawable();

    /** To get player's rectangle (to test intersection)
    * it's used in world/town/interior/rooms/tilemaps sprites
    */
    //    abstract public Rectangle getCurrentRectangle();
    abstract public void init(GraphicsDirector gDirector);

    /* - - - - - - - - - - - SYNC ID MANIPULATION - - - - - - - - - - - - - - - - - - - -*/

    /** To get the synchronization ID. This ID is used to synchronize this player on the
    *  client & server side. The ID is incremented only when the player changes its map.
    *  Movement messages that have a bad syncID are discarded.
    * @return sync ID
    */
    abstract public byte getSyncID();

    /** To set the synchronization ID. See the getter for an explanation on this ID.
    *  @param syncID new syncID
    */
    abstract public void setSyncID(byte syncID);

    abstract public float getSpeed();

    abstract public MovementComposer getMovementComposer();

    abstract public void tick();

    abstract public void serverInit(AStarDoubleServer aStarDoubleServer);

    /** Tick on the Server
    * return true if the object should be removed from the hashtable 
    */
    public boolean serverTick() {
        return false;
    }

    /*---------------------All the common functions---------------------------------------*/

    public WotlasLocation getLocation() {
        return this.loc;
    }

    /** To get the player primary Key ( account name or any unique ID )
    *
    *  @return player primary key
    */
    public String getPrimaryKey() {
        return this.primaryKey;
    }

    public String getName() {
        return this.name;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** To get the X image position.
    *
    * @return x image cordinate
    */
    public int getX() {
        /*
        if( getMovementComposer() == null )
        return x;
        else
        */
        return (int) getMovementComposer().getXPosition();
    }

    /** To get the Y image position.
    *
    * @return y image cordinate
    */
    public int getY() {
        /*
        if( getMovementComposer() == null )
        return y;
        else
        */
        return (int) getMovementComposer().getYPosition();
    }

    // Check it.....
    public void cleanVisualProperties(GraphicsDirector gDirector) {
        gDirector.removeDrawable(getDrawable());
    }

    public boolean isTheServerSide() {
        return this.isServerSide;
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /* - - - - - - - - - - - - - - Send object by the net- - - - - - - - - - - - - - - - -*/

    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeInt(this.x);
        objectOutput.writeInt(this.y);
        objectOutput.writeObject(this.primaryKey);
        objectOutput.writeObject(getLocation());
        if (this.getTargetType() != UserAction.TARGET_TYPE_ITEM)
            objectOutput.writeFloat(this.speed);
        objectOutput.writeObject(this.color);
        objectOutput.writeObject(this.name);
        objectOutput.writeObject(this.indexOfImage);
    }

    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.x = objectInput.readInt();
            this.y = objectInput.readInt();
            this.movementComposer = new ScreenObjectPathFollower(this.x, this.y, 0);
            this.primaryKey = (String) objectInput.readObject();
            this.loc = (WotlasLocation) objectInput.readObject();
            if (this.getTargetType() != UserAction.TARGET_TYPE_ITEM)
                this.speed = objectInput.readFloat();
            this.color = (Color) objectInput.readObject();
            this.name = (String) objectInput.readObject();
            this.indexOfImage = (short[]) objectInput.readObject();
        } else {
            // to do.... when new version
        }
        this.isServerSide = false;
    }

    /** id version of data, used in serialized persistance.
    */
    public int ExternalizeGetVersion() {
        return 1;
    }

    public void writeObject(java.io.ObjectOutputStream objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeInt(this.x);
        objectOutput.writeInt(this.y);
        objectOutput.writeObject(this.primaryKey);
        objectOutput.writeObject(getLocation());
        if (this.getTargetType() != UserAction.TARGET_TYPE_ITEM)
            objectOutput.writeFloat(this.speed);
        objectOutput.writeObject(this.color);
        objectOutput.writeObject(this.name);
        objectOutput.writeObject(this.indexOfImage);
    }

    public void readObject(java.io.ObjectInputStream objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.x = objectInput.readInt();
            this.y = objectInput.readInt();
            this.movementComposer = new ScreenObjectPathFollower(this.x, this.y, (float) (Math.PI / 2));
            this.primaryKey = (String) objectInput.readObject();
            this.loc = (WotlasLocation) objectInput.readObject();
            if (this.getTargetType() != UserAction.TARGET_TYPE_ITEM)
                this.speed = objectInput.readFloat();
            this.color = (Color) objectInput.readObject();
            this.name = (String) objectInput.readObject();
            this.indexOfImage = (short[]) objectInput.readObject();
        } else {
            // to do.... when new version
        }
        this.isServerSide = false;
    }
}