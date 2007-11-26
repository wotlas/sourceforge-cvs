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

package wotlas.client.message.movement;

import java.util.Hashtable;
import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;
import wotlas.common.Player;
import wotlas.common.message.movement.PathUpdateMovementMessage;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the PathUpdateMovementMessage...
 *
 * @author Aldiss
 */

public class PathUpdateMovementMsgBehaviour extends PathUpdateMovementMessage implements NetMessageBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public PathUpdateMovementMsgBehaviour() {
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
            System.out.println("PATH UPDATE MESSAGE FOR PLAYER " + this.primaryKey);

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl player = dataManager.getMyPlayer();

        if (this.primaryKey == null) {
            Debug.signal(Debug.ERROR, this, "No primary key to identify player !");
            return;
        }

        if (player.getPrimaryKey().equals(this.primaryKey)) {
            Debug.signal(Debug.ERROR, this, "Can't set data for master player !");
            return;
        }

        // We search for the "primaryKey" owner among the players around the master player's rooms
        if (player.getLocation().isRoom()) {
            Player playerToUpdate = null;

            // Search in Current Room
            Hashtable players = dataManager.getPlayers();

            synchronized (players) {
                playerToUpdate = (Player) players.get(this.primaryKey);
            }

            if (playerToUpdate != null && playerToUpdate.getPrimaryKey().equals(this.primaryKey)) {
                if (DataManager.SHOW_DEBUG)
                    System.out.println("Movement successfully updated for " + this.primaryKey);
                playerToUpdate.getMovementComposer().setUpdate(this);
                return; // success !
            } else {
                if (DataManager.SHOW_DEBUG)
                    System.out.println("Movement NOOOT updated for " + this.primaryKey);
            }
        }

    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
