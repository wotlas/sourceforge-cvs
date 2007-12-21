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

import wotlas.common.*;
import wotlas.common.character.*;
import wotlas.common.action.*;
import wotlas.common.universe.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.filter.*;
import wotlas.common.environment.*;
import wotlas.server.ServerDirector;
import wotlas.common.router.*;
import wotlas.common.movement.*;
import wotlas.common.action.*;
import wotlas.client.ClientDirector;
import wotlas.client.DataManager;
import wotlas.libs.pathfinding.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.color.*;

/** 
 *
 * @author Diego
 */
public class AreaSpell extends SpellOnTheScreen {
    
    /**
     *  empty constructor
     */
    public AreaSpell() { }

    public AreaSpell(int imageNr, int x,int y, MessageRouter routerMsg, int endX, int endY) {
        this.x = x;
        this.y = y;
        this.primaryKey = ""+ServerDirector.GenUniqueKeyId();
        this.loc = null;
        this.routerMsg = routerMsg;
        this.indexOfImage = new short[2];
        this.indexOfImage[0] = (short)0;
        this.indexOfImage[1] = (short)imageNr;
        this.endX = endX;
        this.endY = endY;
        this.duration = -1;
        isServerSide = true ;
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
    public Drawable getDrawable() {
        if(memImage!=null)
            return (Drawable) memImage;
        memImage = new FakeSprite( this, ImageLibRef.ONEPOWER_PRIORITY
        , EnvironmentManager.getServerEnvironment().getGraphics(EnvironmentManager.SET_OF_EFFECT
        )[indexOfImage[0]], indexOfImage[1] );
        return (Drawable) memImage;
    }

    public void init(GraphicsDirector gDirector) {
        gDirector.addDrawable( getDrawable() );

        trajectoryLock = new byte[0];
        movementComposer = new ScreenObjectPathFollower(x,y,0);
        movementComposer.init( this );
    }

    public void serverInit(AStarDoubleServer aStarDoubleServer) {
        trajectoryLock = new byte[0];
        movementComposer = new ServerPathFollower(x,y,0,aStarDoubleServer);
        movementComposer.init( this );
    }
        
    public void startMove() {
        movementComposer.moveTo( new Point( endX, endY)
        ,ServerDirector.getDataManager().getWorldManager() );
    }
    
    /** Tick
    */
    public void tick() {
        // 1 - Movement Update
        synchronized( trajectoryLock ) {
            movementComposer.tick();
            if( getX() == endX
            && getY() == endY )
                destroy();
        }
    }
    
    /* - - - - - - - - - - - - - - Send object by the net- - - - - - - - - - - - - - - - -*/
    
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal(objectOutput);
        objectOutput.writeObject( indexOfImage );
        objectOutput.writeInt( endX );
        objectOutput.writeInt( endY );
    }

    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal(objectInput);
            indexOfImage = ( short[] ) objectInput.readObject();
            endX = objectInput.readInt();
            endY = objectInput.readInt();
        } else {
                // to do.... when new version
        }
        isServerSide = false;
    }

    /** id version of data, used in serialized persistance.
    */
    public int ExternalizeGetVersion(){
        return 1;
    }
    
    public void writeObject(java.io.ObjectOutputStream objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeObject(objectOutput);
        objectOutput.writeObject( indexOfImage );
        objectOutput.writeInt( endX );
        objectOutput.writeInt( endY );
    }

    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readObject(objectInput);
            indexOfImage = ( short[] ) objectInput.readObject();
            endX = objectInput.readInt();
            endY = objectInput.readInt();
        } else {
            // to do.... when new version
        }
        isServerSide = false;
    }

    public void destroy() {
        ClientDirector.getDataManager().getGraphicsDirector(
        ).removeDrawable( getDrawable() );
        ClientDirector.getDataManager().removeScreenObject(this);
    }
}