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
import wotlas.common.movement.ScreenObjectPathFollower;
import wotlas.common.router.*;
import wotlas.common.movement.MovementComposer;
import wotlas.common.action.*;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.color.*;

/** 
 *
 * @author Diego
 */
public class NpcOnTheScreen extends ScreenObject {

    transient private ScreenObjectPathFollower movementComposer; //= new ScreenObjectPathFollower();
    transient private MessageRouter routerMsg;
    transient private byte trajectoryLock[] = new byte[0];
    
    public NpcOnTheScreen(int x,int y, String name,short[] indexOfImage,MessageRouter routerMsg) {
        this.x = x;
        this.y = y;
        this.primaryKey = ""+ServerDirector.GenUniqueKeyId();
        this.name = name;
        this.loc = null;
        this.routerMsg = routerMsg;
        this.speed = 35.0f;  // Default human speed ( 60pixel/s = 2m/s )
        this.indexOfImage = indexOfImage;
        this.color = Color.red;
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
        memImage = new FakeSprite( this, ImageLibRef.PLAYER_PRIORITY
        , EnvironmentManager.getServerEnvironment().getGraphics(EnvironmentManager.SET_OF_NPC
        )[indexOfImage[0]], indexOfImage[1] );
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
        return UserAction.TARGET_TYPE_NPC;
    }
        
    public CharData getCharData() {
        // at the moment, then it should send npc data.....
        return null;
    }

    public void init(GraphicsDirector gDirector) {
        gDirector.addDrawable( getDrawable() );

        if(true)
            gDirector.addDrawable( new TextDrawable( getName(), getDrawable(), getColor()
        ,13.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, -1));

        trajectoryLock = new byte[0];
        movementComposer = new ScreenObjectPathFollower(x,y,0);
        movementComposer.init( this );
    }
    
    public MessageRouter getRouter() {
        return routerMsg;
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
    
    public float getSpeed(WotlasLocation loc) {
        return speed;
    }
    
    public MovementComposer getMovementComposer() {
        return movementComposer;
    }
    
    /** Tick
    */
    public void tick() {
        // 1 - Movement Update
        synchronized( trajectoryLock ) {
            movementComposer.tick();
        }
    }
    
    /** To get the X image position.
    *
    * @return x image cordinate
    */
    public int getX() {
        if( movementComposer == null )
            return x;
        else
            return (int)movementComposer.getXPosition();
    }
    
    /** To get the Y image position.
    *
    * @return y image cordinate
    */
    public int getY() {
        if( movementComposer == null )
            return y;
        else
            return (int)movementComposer.getYPosition();
    }
}