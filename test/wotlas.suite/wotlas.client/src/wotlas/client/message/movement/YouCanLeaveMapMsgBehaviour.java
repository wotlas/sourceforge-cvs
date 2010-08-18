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

import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;
import wotlas.common.message.movement.WishClientMovementNetMsgBehaviour;
import wotlas.common.message.movement.YouCanLeaveMapMessage;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the YouCanLeaveMapMessage...
 *
 * @author Aldiss
 */
public class YouCanLeaveMapMsgBehaviour extends YouCanLeaveMapMessage implements WishClientMovementNetMsgBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** To tell if this message is to be invoked later or not.
     */
    private boolean invokeLater = true;

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     */
    public YouCanLeaveMapMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {

        // The sessionContext is here a DataManager.
        DataManager dataManager = (DataManager) sessionContext;
        PlayerImpl myPlayer = dataManager.getMyPlayer();

        // Direct Change
        if (this.invokeLater) {
            if (DataManager.SHOW_DEBUG) {
                System.out.println("YOU CAN LEAVE MAP MESSAGE");
            }

            if (this.primaryKey == null) {
                Debug.signal(Debug.ERROR, this, "No primary key to identify player !");
                return;
            }

            if (!myPlayer.getPrimaryKey().equals(this.primaryKey)) {
                Debug.signal(Debug.ERROR, this, "This message is not for master player !");
                return;
            }

            this.invokeLater = false;
            dataManager.invokeLater(this);
            return;
        }

        // Code to invoke after the current tick :
        Debug.signal(Debug.NOTICE, this, "Location update sent by server !");
        myPlayer.getMovementComposer().resetMovement();
        myPlayer.setX(this.x);
        myPlayer.setY(this.y);
        myPlayer.setAngle(this.orientation);
        myPlayer.setLocation(this.location);
        myPlayer.setSyncID(this.syncID);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
