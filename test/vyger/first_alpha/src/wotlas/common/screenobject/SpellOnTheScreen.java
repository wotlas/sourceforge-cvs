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
abstract public class SpellOnTheScreen extends ScreenObject {

    /** used by movement composer
     */
    transient protected MessageRouter routerMsg;
    protected byte trajectoryLock[] = new byte[0];
    protected long duration = -1;
    protected int endX,endY;
    
    /**
     *  empty constructor
     */
    public SpellOnTheScreen() { }
    
    /** To get the image identifier to use.
     *
     * @return image identifier.
     */
    public ImageIdentifier getImageIdentifier() {
        return new ImageIdentifier();
    }
   
    public boolean isConnectedToGame() {
        return true;
    }
        
    public void setLocation(WotlasLocation loc) {
        this.loc = loc;
    }
    
    public byte getTargetType() {
        return UserAction.TARGET_TYPE_SPELL;
    }
        
    public CharData getCharData() {
        return null;
    }

    /** called by movement composer during movements
     */
    public MessageRouter getRouter() {
        return routerMsg;
    }

    public MovementComposer getMovementComposer() {
        return movementComposer;
    }
    
 /* - - - - - - - - - - - SYNC ID MANIPULATION - - - - - - - - - - - - - - - - - - - -*/

    /** To get the synchronization ID. This ID is used to synchronize this player on the
    *  client & server side. The ID is incremented only when the player changes its map.
    *  Movement messages that have a bad syncID are discarded.
    * @return sync ID
    */
    public byte getSyncID(){
      	synchronized( syncID ) {
            return syncID[0];
        }
    }

    /** To update the synchronization ID. See the getter for an explanation on this ID.
    *  The new updated syncID is (syncID+1)%100.
    */
    public void updateSyncID(){
      	synchronized( syncID ) {
            syncID[0] = (byte) ( (syncID[0]+1)%100 );
        }
    }

    /** To set the synchronization ID. See the getter for an explanation on this ID.
    *  @param syncID new syncID
    */
    public void setSyncID(byte syncID){
        synchronized( this.syncID ) {
            this.syncID[0] = syncID;
        }
    }
    
    public float getSpeed() {
        return 230.0f;
        //return speed;
    }
    
    /* - - - - - - - - - - - - - - Send object by the net- - - - - - - - - - - - - - - - -*/
    
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeInt( x );
        objectOutput.writeInt( y );
        objectOutput.writeObject( primaryKey );
        objectOutput.writeObject( loc );
    }

    public void readExternal(java.io.ObjectInput objectInput)
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
        isServerSide = false;
    }
}