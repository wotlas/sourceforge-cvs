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

import java.awt.Rectangle;

/** 
 *
 * @author Diego
 */
public class PlayerOnTheScreen extends ScreenObject {

    transient private Player player;
    transient private ScreenObjectPathFollower movementComposer;
    
    public PlayerOnTheScreen(Player player) {
        this.player = player;
        this.x = player.getX();
        this.y = player.getY();
        this.loc = player.getLocation();
        this.primaryKey = player.getPrimaryKey();
        this.speed = player.getBasicChar().getSpeed( player.getLocation() );
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
                imageNr = 7;
                break;
            default:
                imageNr = EnvironmentManager.getDefaultNpcImageNr();
        }
        memImage = new FakeSprite( this, ImageLibRef.PLAYER_PRIORITY
        , EnvironmentManager.getServerEnvironment().getGraphics(EnvironmentManager.SET_OF_NPC
        )[2], imageNr  );
        return (Drawable) memImage;
        // cant do this if i dont know the player data : and i dont know it on the client side
        // return player.getBasicChar().getDrawable(player);
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
    
    public byte getTargetType() {
        return UserAction.TARGET_TYPE_PLAYER;
    }    

    public CharData getCharData() {
        return player.getBasicChar();
    }

    public void init(GraphicsDirector gDirector) {
        gDirector.addDrawable( getDrawable() );
        movementComposer = new ScreenObjectPathFollower();
        movementComposer.init( this );
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
    
}