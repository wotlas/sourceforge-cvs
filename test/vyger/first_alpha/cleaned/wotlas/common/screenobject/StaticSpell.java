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

import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.pathfinding.AStarDoubleServer;
import wotlas.server.ServerDirector;

/** 
 *
 * @author Diego
 */
public class StaticSpell extends SpellOnTheScreen {

    private String text;

    /**
     *  empty constructor
     */
    public StaticSpell() {
    }

    public StaticSpell(int imageNr, String text) {
        //    public StaticSpell(int imageNr, int x,int y, MessageRouter routerMsg, int duration) {
        //this.x = x;
        //this.y = y;
        this.primaryKey = "" + ServerDirector.GenUniqueKeyId();
        this.loc = null;
        //        this.routerMsg = routerMsg;
        this.text = text;
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
        return null;
    }

    /** To get the image identifier to use.
     *
     * @return image identifier.
     */
    @Override
    public ImageIdentifier getImageIdentifier() {
        return new ImageIdentifier();
    }

    @Override
    public void init(GraphicsDirector gDirector) {
        gDirector.addDrawable(getDrawable());
    }

    @Override
    public void serverInit(AStarDoubleServer aStarDoubleServer) {
    }

    /** Tick
    */
    @Override
    public void tick() {
    }

    /* - - - -  - - - - - Send object by the net- - - -- - - - - - - -*/

    @Override
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        super.writeExternal(objectOutput);
        objectOutput.writeObject(this.text);
    }

    @Override
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            super.readExternal(objectInput);
            this.text = (String) objectInput.readObject();
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
        objectOutput.writeObject(this.text);
    }

    @Override
    public void readObject(java.io.ObjectInputStream objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            super.readObject(objectInput);
            this.text = (String) objectInput.readObject();
        } else {
            // to do.... when new version
        }
        this.isServerSide = false;
    }
}