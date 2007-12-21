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

import java.util.Hashtable;
import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;
import wotlas.common.message.description.RemoveScreenObjectFromTileMapMessage;
import wotlas.common.screenobject.PlayerOnTheScreen;
import wotlas.common.screenobject.ScreenObject;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the RemovePlayerFromTileMapMessage...
 *
 * @author Aldiss, Diego
 */

public class RemoveScreenObjectFromTileMapMsgBehaviour extends RemoveScreenObjectFromTileMapMessage implements NetMessageBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
    */
    public RemoveScreenObjectFromTileMapMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Associated code to this Message...
    *
    * @param sessionContext an object giving specific access to other objects needed to process
    *        this message.
    */
    public void doBehaviour(Object sessionContext) {
        if (DataManager.SHOW_DEBUG)
            System.out.println("REMOVE screenObject MESSAGE p:" + this.primaryKey);

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl myPlayer = dataManager.getMyPlayer();

        // 1 - Control
        if (!myPlayer.getLocation().isTileMap()) {
            Debug.signal(Debug.ERROR, this, "Master player is not on a TileMap");
            return;
        }

        if (myPlayer.getPrimaryKey().equals(this.primaryKey)) {
            Debug.signal(Debug.ERROR, this, "ATTEMPT TO REMOVE MASTER PLAYER !!");
            return;
        }

        // 2 - We remove the player
        Hashtable screenObjects = dataManager.getScreenObjects();
        ScreenObject item = null;

        synchronized (screenObjects) {
            item = (ScreenObject) screenObjects.get(this.primaryKey);

            if (item == null)
                return;

            if (DataManager.SHOW_DEBUG)
                System.out.println("REMOVING screnObject " + this.primaryKey);

            screenObjects.remove(this.primaryKey);
            if (item instanceof PlayerOnTheScreen)
                dataManager.getClientScreen().getChatPanel().removePlayerFromAllchatRooms(this.primaryKey);
        }

        //playerImpl.cleanVisualProperties(dataManager.getGraphicsDirector());
        item.cleanVisualProperties(dataManager.getGraphicsDirector());

        if (dataManager.getSelectedPlayerKey() != null && this.primaryKey.equals(dataManager.getSelectedPlayerKey()))
            dataManager.removeCircle();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}