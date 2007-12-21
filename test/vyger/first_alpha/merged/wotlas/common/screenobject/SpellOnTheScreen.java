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

import wotlas.common.action.UserAction;
import wotlas.common.character.CharData;
import wotlas.common.movement.MovementComposer;
import wotlas.common.router.MessageRouter;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2d.ImageIdentifier;

/** 
 *
 * @author Diego
 */
abstract public class SpellOnTheScreen extends ScreenObject {

    /** used by movement composer
     */
    transient protected MessageRouter routerMsg;
    protected byte trajectoryLock[] = new byte[0];
    protected long duration = -1;
    protected int endX, endY;

    /**
     *  empty constructor
     */
    public SpellOnTheScreen() {
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
        return UserAction.TARGET_TYPE_SPELL;
    }

    @Override
    public CharData getCharData() {
        return null;
    }

    /** called by movement composer during movements
     */
    public MessageRouter getRouter() {
        return this.routerMsg;
    }

    @Override
    public MovementComposer getMovementComposer() {
        return this.movementComposer;
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
        return 230.0f;
        //return speed;
    }

    /* - - - - - - - - - - - - - - Send object by the net- - - - - - - - - - - - - - - - -*/

    @Override
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeInt(this.x);
        objectOutput.writeInt(this.y);
        objectOutput.writeObject(this.primaryKey);
        objectOutput.writeObject(this.loc);
    }

    @Override
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.x = objectInput.readInt();
            this.y = objectInput.readInt();
            this.primaryKey = (String) objectInput.readObject();
            this.loc = (WotlasLocation) objectInput.readObject();
        } else {
            // to do.... when new version
        }
        this.isServerSide = false;
    }

    /** id version of data, used in serialized persistance.
    */
    @Override
    public int ExternalizeGetVersion() {
        return 1;
    }

    @Override
    public void writeObject(java.io.ObjectOutputStream objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeInt(this.x);
        objectOutput.writeInt(this.y);
        objectOutput.writeObject(this.primaryKey);
        objectOutput.writeObject(this.loc);
    }

    @Override
    public void readObject(java.io.ObjectInputStream objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.x = objectInput.readInt();
            this.y = objectInput.readInt();
            this.primaryKey = (String) objectInput.readObject();
            this.loc = (WotlasLocation) objectInput.readObject();
        } else {
            // to do.... when new version
        }
        this.isServerSide = false;
    }
}