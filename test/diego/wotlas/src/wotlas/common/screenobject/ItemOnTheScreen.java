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
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.filter.*;
import wotlas.common.environment.*;

import java.awt.Rectangle;

/** 
 *
 * @author Diego
 */
public class ItemOnTheScreen extends ScreenObject {

    public ItemOnTheScreen(int x,int y, String key) {
        this.primaryKey = key;
        this.x = x;
        this.y = y;
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
        , EnvironmentManager.getServerEnvironment().getGraphics(EnvironmentManager.SET_OF_NPC
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
    }
    
    public byte getTargetType() {
        return UserAction.TARGET_TYPE_ITEM;
    }
    
    public CharData getCharData() {
        return null;
    }
    
}