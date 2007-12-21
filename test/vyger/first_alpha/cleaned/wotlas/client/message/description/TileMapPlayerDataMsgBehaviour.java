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
import java.util.Iterator;
import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;
import wotlas.common.message.description.TileMapPlayerDataMessage;
import wotlas.common.screenobject.ScreenObject;
import wotlas.common.universe.TileMap;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the PathUpdateMovementMessage...
 *
 * @author Aldiss, Diego
 */

public class TileMapPlayerDataMsgBehaviour extends TileMapPlayerDataMessage implements NetMessageBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public TileMapPlayerDataMsgBehaviour() {
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
            System.out.println("TILEMAP PLAYER DATA MESSAGE " + this.location);

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl myPlayer = dataManager.getMyPlayer();

        if (myPlayer.getLocation() == null) {
            Debug.signal(Debug.ERROR, this, "No location set !");
            return;
        }

        // We search for the location specified...
        if (myPlayer.getLocation().isTileMap()) {
            TileMap myTileMap = myPlayer.getMyTileMap();
            if (myTileMap == null) {
                Debug.signal(Debug.ERROR, this, "Null TileMap for " + myPlayer.getPrimaryKey());
                return;
            }

            // is this TileMap on the same map as ours ?
            WotlasLocation myLocation = myPlayer.getLocation();

            if (myLocation.getWorldMapID() != this.location.getWorldMapID() || myLocation.getTileMapID() != this.location.getTileMapID()) {
                Debug.signal(Debug.WARNING, this, "Received message with far location");
                return;
            }

            // Search in Current TileMap
            if (myTileMap.getTileMapID() == this.location.getTileMapID()) {
                merge(dataManager);
                return; // success
            }

            return; // the tilemap was not found near us...
        }

    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To merge our screenObjects the DataManager's hashtable...
     */
    private void merge(DataManager dataManager) {
        Hashtable dest = dataManager.getScreenObjects();
        synchronized (dest) {
            Iterator it = this.screenObjects.values().iterator();

            while (it.hasNext()) {
                ScreenObject item = (ScreenObject) it.next();

                if (dest.containsKey(item.getPrimaryKey()))
                    continue;

                dest.put(item.getPrimaryKey(), item);
                item.init(dataManager.getGraphicsDirector());
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}