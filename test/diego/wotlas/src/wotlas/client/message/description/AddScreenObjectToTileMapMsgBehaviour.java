/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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
package wotlas.client.message.description;

import java.io.IOException;
import java.util.*;

import wotlas.utils.Debug;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.libs.sound.*;
import wotlas.common.screenobject.*;
import wotlas.common.message.description.*;
import wotlas.common.universe.*;
import wotlas.common.Player;
import wotlas.client.*;


/**
 * Associated behaviour to the AddScreenObjectToTileMapMessage...
 *
 * @author Aldiss, Diego
 */

public class AddScreenObjectToTileMapMsgBehaviour extends AddScreenObjectToTileMapMessage implements NetMessageBehaviour
{
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
    */
    public AddScreenObjectToTileMapMsgBehaviour() {
        super();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Associated code to this Message...
    *
    * @param sessionContext an object giving specific access to other objects needed to process
    *        this message.
    */
    public void doBehaviour( Object sessionContext ) {
        if (DataManager.SHOW_DEBUG)
            System.out.println("ADD ScreenObject TO TILEMAP MESSAGE itemKey: "+item.getPrimaryKey());

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl myPlayer = dataManager.getMyPlayer();

        // 1 - Control
        if( !myPlayer.getLocation().isTileMap() ) {
            Debug.signal( Debug.ERROR, this, "Master player is not on an TileMap" );
            return;
        }

        WotlasLocation myLocation = myPlayer.getLocation();
        TileMap myTileMap = myPlayer.getMyTileMap();

        if( myLocation.getWorldMapID() != item.getLocation().getWorldMapID()
        || myLocation.getTileMapID() != item.getLocation().getTileMapID() ){
               Debug.signal( Debug.WARNING, this, "Received message with far location" );
               return;
        }

        // Search in Current TileMap
        if( myTileMap.getTileMapID() == item.getLocation().getTileMapID() ) {
            Hashtable screenObjects = dataManager.getScreenObjects();
               
            synchronized( screenObjects ) {
                if( !screenObjects.containsKey( item.getPrimaryKey() ) ) {
                    screenObjects.put( item.getPrimaryKey(), item );
                    if( item instanceof PlayerOnTheScreen ) {
                   	// ((PlayerImpl)player).init();
                        // la riga sopra, qui serve solo a fare questo :
                        //  movementComposer.init( this );
                        // va' sistemato.....
                        
                        ;
                    }
                    else if( !(item instanceof ItemOnTheScreen) ) {
                        // done then npc or player come into a map
                        SoundLibrary.getSoundPlayer().playSound("human-steps.wav");
                    }
                    // ((PlayerImpl)player).initVisualProperties(dataManager.getGraphicsDirector());
                    // la riga sopra falsequesto sotto:
                    // gDirector.addDrawable(wotCharacter.getDrawable(this));
                    item.init( dataManager.getGraphicsDirector() );
                    // dataManager.getGraphicsDirector().addDrawable(item.getDrawable());
               	}
           }

           return;  // success
        }

        // not found
        Debug.signal( Debug.NOTICE, this, "Player's TileMap is not near master's" );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}