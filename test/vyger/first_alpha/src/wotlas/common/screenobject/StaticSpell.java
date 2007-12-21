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
public class StaticSpell extends SpellOnTheScreen {
    
    private String text;
    
    /**
     *  empty constructor
     */
    public StaticSpell() { }

    public StaticSpell(int imageNr, String text) {
//    public StaticSpell(int imageNr, int x,int y, MessageRouter routerMsg, int duration) {
        this.x = x;
        this.y = y;
        this.primaryKey = ""+ServerDirector.GenUniqueKeyId();
        this.loc = null;
//        this.routerMsg = routerMsg;
        this.text = text;
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
        return null;
    }
    
    /** To get the image identifier to use.
     *
     * @return image identifier.
     */
    public ImageIdentifier getImageIdentifier() {
        return new ImageIdentifier();
    }
   
    public void init(GraphicsDirector gDirector) {
        gDirector.addDrawable( getDrawable() );
    }

    public void serverInit(AStarDoubleServer aStarDoubleServer) {
    }
    
    /** Tick
    */
    public void tick() {
    }
    
    /* - - - -  - - - - - Send object by the net- - - -- - - - - - - -*/
    
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal(objectOutput);
        objectOutput.writeObject( text );
    }

    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal(objectInput);
            text = ( String ) objectInput.readObject();
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
        objectOutput.writeObject( text );
    }

    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readObject(objectInput);
            text = ( String ) objectInput.readObject();
        } else {
                // to do.... when new version
        }
        isServerSide = false;
    }
}