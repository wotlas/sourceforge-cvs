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

package wotlas.server.message.description;

import wotlas.common.message.description.PlayerPastMessage;
import wotlas.common.router.MessageRouter;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.server.PlayerImpl;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the PlayerPastMessage...
 *
 * @author Aldiss
 */

public class PlayerPastMsgBehaviour extends PlayerPastMessage implements NetMessageBehaviour {

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public PlayerPastMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {

        // The sessionContext is here a PlayerImpl.
        PlayerImpl player = (PlayerImpl) sessionContext;

        // is our player the dest of this message
        if (this.primaryKey.equals(player.getPrimaryKey())) {
            // do we have to save the past for our player
            if (player.getPlayerPast() == null || player.getPlayerPast().length() == 0)
                player.setPlayerPast(this.playerPast); // we save the past...

            return;
        }

        // no, it's another player we want...
        if (!player.getLocation().isRoom()) {
            Debug.signal(Debug.ERROR, this, "Location is not a room ! " + player.getLocation());
            return;
        }

        // we search for the player via our MessageRouter
        MessageRouter mRouter = player.getMessageRouter();
        if (mRouter == null)
            return;

        PlayerImpl searchedPlayer = (PlayerImpl) mRouter.getPlayer(this.primaryKey);

        if (searchedPlayer != null) {
            String playerPast = searchedPlayer.getPlayerPast();
            playerPast += "\n\nEncounter info: " + searchedPlayer.getLieManager().getLastMeetPlayer(player);
            player.sendMessage(new PlayerPastMessage(this.primaryKey, playerPast));
            return;
        }

        Debug.signal(Debug.WARNING, this, "Could not find player : " + this.primaryKey);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
