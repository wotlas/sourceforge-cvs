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

package wotlas.server.bots.alice.client;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.server.AccountManager;
import wotlas.server.GameAccount;
import wotlas.server.bots.BotPlayer;
import wotlas.server.bots.alice.AliceWotlasMessage;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the AliceWotlasMessage on the Wotlas Server side.
 *
 * @author Aldiss
 */

public class AliceWotlasMsgBehaviour extends AliceWotlasMessage implements NetMessageBehaviour {

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public AliceWotlasMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {

        // The sessionContext is here a AccountManager...
        AccountManager aManager = (AccountManager) sessionContext;

        // We send the answer back to the bot
        GameAccount account = aManager.getAccount(this.botPrimaryKey);

        if (account == null) {
            Debug.signal(Debug.ERROR, this, "Bot not found ! (" + this.botPrimaryKey + ")");
            return;
        }

        if (!(account.getPlayer() instanceof BotPlayer)) {
            Debug.signal(Debug.ERROR, this, "Player is not a bot ! (" + this.botPrimaryKey + ")");
            return;
        }

        ((BotPlayer) account.getPlayer()).sendChatAnswer(this.message);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
