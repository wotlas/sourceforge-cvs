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
import wotlas.common.movement.ScreenObjectPathFollower;
import wotlas.common.movement.MovementComposer;
import wotlas.common.action.*;
import wotlas.libs.pathfinding.*;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.color.*;

/** 
 *
 * @author Diego
 */
public class PlayerOnTheScreen extends ScreenObject {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    transient private Player player;
// FIXME ???
//    transient private ScreenObjectPathFollower movementComposer;        
    private byte trajectoryLock[] = new byte[0];
    
    /**
     *  empty constructor
     */
    public PlayerOnTheScreen() { }

    public PlayerOnTheScreen(Player player, short[] indexOfImage) {
        this.player = player;
        this.x = player.getX();
        this.y = player.getY();
        this.primaryKey = player.getPrimaryKey();
        this.name = player.getPlayerName();
        this.speed = player.getBasicChar().getSpeed();
        this.loc = player.getBasicChar().getLocation();
        this.indexOfImage = indexOfImage;
        this.color = Color.blue;
        player.getBasicChar().setScreenObject(this);
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
 
    public Player getPlayer(){
        return player;
    }
   
    public boolean isConnectedToGame() {
        return player.isConnectedToGame();
    }

    public void setLocation(WotlasLocation loc) {
        player.setLocation(loc);
    }

    public WotlasLocation getLocation() {
        if(player == null)
            return loc;
        else
            return getCharData().getLocation();
    }
    
    public byte getTargetType() {
        return UserAction.TARGET_TYPE_PLAYER;
    }    

    public CharData getCharData() {
        if(player == null)
            return null;
        else
            return player.getBasicChar();
    }

    public void init(GraphicsDirector gDirector) {
        gDirector.addDrawable( getDrawable() );

        if(true)
            gDirector.addDrawable( new TextDrawable( getName().toUpperCase(), getDrawable(), getColor()
            ,10.0f, "Dialog.plain", ImageLibRef.TEXT_PRIORITY, -1));
//            ,13.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, -1));

        trajectoryLock = new byte[0];
// FIXME ???       movementComposer = new ScreenObjectPathFollower(x,y,0);
        movementComposer.init( this );
    }

    public void serverInit(AStarDoubleServer aStarDoubleServer) {
        trajectoryLock = new byte[0];
        getMovementComposer().init( this );
        getMovementComposer().resetMovement();
    }
    
  /* - - - - - - - - - - - SYNC ID MANIPULATION - - - - - - - - - - - - - - - - - - - -*/

    /** To get the synchronization ID. This ID is used to synchronize this player on the
    *  client & server side. The ID is incremented only when the player changes its map.
    *  Movement messages that have a bad syncID are discarded.
    * @return sync ID
    */
    public byte getSyncID(){
        if( player == null)
            synchronized( syncID ) {
                return syncID[0];
            }
        else
            return player.getSyncID();
    }

    /** To set the synchronization ID. See the getter for an explanation on this ID.
    *  @param syncID new syncID
    */
    public void setSyncID(byte syncID){
        if( player == null)
            synchronized( this.syncID ) {
                this.syncID[0] = syncID;
            }
        else
            player.setSyncID(syncID);
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public MovementComposer getMovementComposer() {
        if( player == null )
            return movementComposer;
        else
            return player.getMovementComposer();
    }
    
    /** Tick
    */
    public void tick() {
        // 1 - Movement Update
        synchronized( trajectoryLock ) {
            getMovementComposer().tick();
        }
    }

    /** Tick on the Server
    * return true if the object should be removed from the hashtable 
    */
    public boolean serverTick() {
        // 1 - Movement Update
        // System.out.print("path-> x,y:["+getX()+","+getY()+"]  - ");
        synchronized( trajectoryLock ) {
            getMovementComposer().tick();
        }
        // System.out.println("end-> x,y:["+getX()+","+getY()+"] ");
        return false;
    }
  
// FIXME ???    
    /** To get the X image position.
    *
    * @return x image cordinate
    */
//    public int getX() {
//        if( movementComposer == null )
//            return x;
//        else
//            return (int)movementComposer.getXPosition();
//    }
    
    /** To get the Y image position.
    *
    * @return y image cordinate
    */
//    public int getY() {
//        if( movementComposer == null )
//            return y;
//        else
//            return (int)movementComposer.getYPosition();
//    }    
}