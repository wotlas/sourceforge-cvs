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

import wotlas.common.ImageLibRef;
import wotlas.common.PrimaryKeyGenerator;
import wotlas.common.environment.EnvironmentManager;
import wotlas.common.movement.ScreenObjectPathFollower; 
// FIXME ???? import wotlas.server.movement.ServerPathFollower;
import wotlas.common.router.MessageRouter; 
// FIXME ???? import wotlas.client.ClientDirector;
// FIXME ???? import wotlas.server.ServerDirector;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.drawable.FakeSprite;
import wotlas.libs.pathfinding.AStarDoubleServer;

/** 
 *
 * @author Diego
 */
public class AreaSpell extends SpellOnTheScreen {

    /**
     *  empty constructor
     */
    public AreaSpell() {
    }

    public AreaSpell(int imageNr, int x, int y, MessageRouter routerMsg, int endX, int endY) {
        this.x = x;
        this.y = y;
	this.primaryKey = "" + PrimaryKeyGenerator.GenUniqueKeyId();
        this.loc = null;
        this.routerMsg = routerMsg;
        this.indexOfImage = new short[2];
        this.indexOfImage[0] = (short) 0;
        this.indexOfImage[1] = (short) imageNr;
        this.endX = endX;
        this.endY = endY;
        this.duration = -1;
        this.isServerSide = true;
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
        this.memImage = new FakeSprite(this, ImageLibRef.ONEPOWER_PRIORITY, EnvironmentManager.getGraphics(EnvironmentManager.SET_OF_EFFECT)[this.indexOfImage[0]], this.indexOfImage[1]);
        return this.memImage;
    }

    @Override
    public void init(GraphicsDirector gDirector) {
        gDirector.addDrawable(getDrawable());

        this.trajectoryLock = new byte[0];
        this.movementComposer = new ScreenObjectPathFollower(this.x, this.y, 0);
        this.movementComposer.init(this);
    }

    @Override
    public void serverInit(AStarDoubleServer aStarDoubleServer) {
        this.trajectoryLock = new byte[0];
        // FIXME ??? this.movementComposer = new ServerPathFollower(this.x, this.y, 0, aStarDoubleServer);
        // FIXME ??? this.movementComposer.init(this);
    }

    public void startMove() {
        // FIXME ??? this.movementComposer.moveTo(new Point(this.endX, this.endY), ServerDirector.getDataManager().getWorldManager());
    }

    /** Tick
    */
    @Override
    public void tick() {
        // 1 - Movement Update
        synchronized (this.trajectoryLock) {
            this.movementComposer.tick();
            if (getX() == this.endX && getY() == this.endY)
                destroy();
        }
    }

    /* - - - - - - - - - - - - - - Send object by the net- - - - - - - - - - - - - - - - -*/

    @Override
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        super.writeExternal(objectOutput);
        objectOutput.writeObject(this.indexOfImage);
        objectOutput.writeInt(this.endX);
        objectOutput.writeInt(this.endY);
    }

    @Override
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            super.readExternal(objectInput);
            this.indexOfImage = (short[]) objectInput.readObject();
            this.endX = objectInput.readInt();
            this.endY = objectInput.readInt();
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
        super.writeObject(objectOutput);
        objectOutput.writeObject(this.indexOfImage);
        objectOutput.writeInt(this.endX);
        objectOutput.writeInt(this.endY);
    }

    @Override
    public void readObject(java.io.ObjectInputStream objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            super.readObject(objectInput);
            this.indexOfImage = (short[]) objectInput.readObject();
            this.endX = objectInput.readInt();
            this.endY = objectInput.readInt();
        } else {
            // to do.... when new version
        }
        this.isServerSide = false;
    }

    public void destroy() {
        // FIXME ??? ClientDirector.getDataManager().getGraphicsDirector().removeDrawable(getDrawable());
        // FIXME ??? ClientDirector.getDataManager().removeScreenObject(this);
    }
}