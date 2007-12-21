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
import wotlas.server.ServerDirector;

/** 
 *
 * @author Diego
 */
public class ItemOnTheScreen extends ScreenObject {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /**
     *  empty constructor
     */
    public ItemOnTheScreen() {
    }

    public ItemOnTheScreen(int x, int y, String name, short[] indexOfImage) {
        this.x = x;
        this.y = y;
        this.primaryKey = "" + ServerDirector.GenUniqueKeyId();
        this.name = name;
        this.loc = null;
        this.indexOfImage = indexOfImage;
        this.color = Color.white;
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
        this.memImage = new FakeSprite(this, ImageLibRef.OBJECT_PRIORITY, EnvironmentManager.getGraphics(EnvironmentManager.SET_OF_ITEM)[this.indexOfImage[0]], this.indexOfImage[1]);
        return this.memImage;
    }

    /** To get the image identifier to use.
     *
     * @return image identifier.
     */
    public ImageIdentifier getImageIdentifier() {
        return new ImageIdentifier();
    }

    @Override
    public boolean isConnectedToGame() {
        return true;
    }

    @Override
    public void setLocation(WotlasLocation loc) {
        this.loc = loc;
    }

    @Override
    public byte getTargetType() {
        return UserAction.TARGET_TYPE_ITEM;
    }

    @Override
    public CharData getCharData() {
        return null;
    }

    @Override
    public void init(GraphicsDirector gDirector) {
        gDirector.addDrawable(getDrawable());

        if (true)
            gDirector.addDrawable(new TextDrawable(getName().toUpperCase(), getDrawable(), getColor(), 10.0f, "Dialog.plain", ImageLibRef.TEXT_PRIORITY, -1));
        //            ,13.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, -1));

    }

    @Override
    public void serverInit(AStarDoubleServer aStarDoubleServer) {
    }

    /* - - - - - - - - - - - SYNC ID MANIPULATION - - - - - - - - - - - - - - - - - - - -*/

    /** To get the synchronization ID. This ID is used to synchronize this player on the
    *  client & server side. The ID is incremented only when the player changes its map.
    *  Movement messages that have a bad syncID are discarded.
    * @return sync ID
    */
    @Override
    public byte getSyncID() {
        synchronized (this.syncID) {
            return this.syncID[0];
        }
    }

    /** To update the synchronization ID. See the getter for an explanation on this ID.
    *  The new updated syncID is (syncID+1)%100.
    */
    public void updateSyncID() {
        synchronized (this.syncID) {
            this.syncID[0] = (byte) ((this.syncID[0] + 1) % 100);
        }
    }

    /** To set the synchronization ID. See the getter for an explanation on this ID.
    *  @param syncID new syncID
    */
    @Override
    public void setSyncID(byte syncID) {
        synchronized (this.syncID) {
            this.syncID[0] = syncID;
        }
    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    public MovementComposer getMovementComposer() {
        return null;
    }

    /** Tick
    */
    @Override
    public void tick() {
    }

    /** To get the X image position.
    *
    * @return x image cordinate
    */
    @Override
    public int getX() {
        return this.x;
    }

    /** To get the Y image position.
    *
    * @return y image cordinate
    */
    @Override
    public int getY() {
        return this.y;
    }
}