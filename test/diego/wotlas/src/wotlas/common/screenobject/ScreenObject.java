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

import wotlas.common.*;
import wotlas.common.character.*;
import wotlas.common.universe.*;
import wotlas.libs.persistence.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.filter.*;
import wotlas.common.environment.*;

import java.awt.Rectangle;

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
public abstract class ScreenObject implements FakeSpriteDataSupplier, SendObjectReady{
    
    transient protected FakeSprite memImage;
    protected int x,y;
    protected WotlasLocation loc;
    protected String primaryKey;
    protected String name;

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
     
 /*---------------------All the common functions---------------------------------------*/

    public WotlasLocation getLocation() {
        return loc;
    }
    
    /** To get the player primary Key ( account name or any unique ID )
    *
    *  @return player primary key
    */
    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getName() {
        return name;
    }

    /** To get the X image position.
    *
    * @return x image cordinate
    */
    public int getX() {
        return x;
    }
    
    /** To get the Y image position.
    *
    * @return y image cordinate
    */
    public int getY() {
        return y;
    }
    
    public void setPos(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    // Check it.....
    public void cleanVisualProperties(GraphicsDirector gDirector) {
        gDirector.removeDrawable( getDrawable() );
    }
    
    /* - - - - - - - - - - - - - - Send object by the net- - - - - - - - - - - - - - - - -*/
    
    /** id version of data, used in serialized persistance.
    */
    public int ExternalizeGetVersion(){
        return 1;
    }

    public void writeObject(java.io.ObjectOutputStream objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeInt( x );
        objectOutput.writeInt( y );
        objectOutput.writeObject( primaryKey );
        objectOutput.writeObject( loc );
    }

    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            x = objectInput.readInt();
            y = objectInput.readInt();
            primaryKey = ( String ) objectInput.readObject();
            loc = ( WotlasLocation ) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }
}