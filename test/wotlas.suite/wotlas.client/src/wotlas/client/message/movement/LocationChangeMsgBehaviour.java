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
import wotlas.common.message.movement.LocationChangeMessage;
import wotlas.common.message.movement.WishClientMovementNetMsgBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the LocationChangeMessage...
 *
 * @author Aldiss
 */
public class LocationChangeMsgBehaviour extends LocationChangeMessage implements WishClientMovementNetMsgBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public LocationChangeMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {
        if (DataManager.SHOW_DEBUG) {
            System.out.println("LOCATION CHANGED FOR PLAYER " + this.primaryKey + " !");
        }

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl player = dataManager.getMyPlayer();

        if (this.primaryKey == null) {
            Debug.signal(Debug.ERROR, this, "No primary key to identify player !");
            return;
        }

        if (player.getPrimaryKey().equals(this.primaryKey)) {
            Debug.signal(Debug.ERROR, this, "This message is for our master player !");
            return;
        }

        // We seek for this player
        Hashtable<String, PlayerImpl> players = dataManager.getPlayers();
        Player uPlayer = null;

        if (players != null) {
            uPlayer = players.get(this.primaryKey);
        }

        if (uPlayer == null) {
            Debug.signal(Debug.ERROR, this, "Player " + this.primaryKey + " not found !");
            return;
        }

        // SUCCESS ! we update the player location !
        uPlayer.setLocation(this.location);

        if (!uPlayer.getLocation().equals(player.getLocation())) {
            dataManager.getClientScreen().getChatPanel().removePlayerFromAllchatRooms(this.primaryKey);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
