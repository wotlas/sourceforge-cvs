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
import wotlas.common.movement.MovementComposer;

import java.awt.Rectangle;

/** 
 *
 * @author Diego
 */
public class ItemOnTheScreen extends ScreenObject {

    public ItemOnTheScreen(int x,int y, String name) {
        this.x = x;
        this.y = y;
        this.primaryKey = ""+ServerDirector.GenUniqueKeyId();
        this.name = name;
        this.loc = null;
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
        int imageNr = 0;
        switch( EnvironmentManager.whtGraphicSetIs() ){
            case EnvironmentManager.GRAPHICS_SET_ROGUE:
                imageNr = 2;
                break;
            default:
                imageNr = EnvironmentManager.getDefaultNpcImageNr();
        }
        memImage = new FakeSprite( this, ImageLibRef.PLAYER_PRIORITY
        , EnvironmentManager.getServerEnvironment().getGraphics(EnvironmentManager.SET_OF_ITEM
        )[2], imageNr  );
        return (Drawable) memImage;
    }
    
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
        return UserAction.TARGET_TYPE_ITEM;
    }
    
    public CharData getCharData() {
        return null;
    }
    
    public void init(GraphicsDirector gDirector) {
        gDirector.addDrawable( getDrawable() );
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
    
    public float getSpeed(WotlasLocation loc) {
        return 0;
    }    
    
    public MovementComposer getMovementComposer() {
        return null;
    }
    
    /** Tick
    */
    public void tick() {
    }
}