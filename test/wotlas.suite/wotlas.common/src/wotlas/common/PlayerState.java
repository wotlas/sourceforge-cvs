/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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

package wotlas.common;

/** Utility for the chat
 *
 * @author Petrus
 * @see wotlas.client.screen.JChatRoom
 * @see wotlas.client.screen.JChatPanel
 * @see wotlas.common.Player
 */

public class PlayerState {

    public final static byte DISCONNECTED = 0;
    public final static byte CONNECTED = 1;
    public final static byte AWAY = 2;

    public String fullName;

    //public boolean isNotAway=false;  
    public byte value;

    /** Constructor.
     */
    //PlayerState(String fullName, boolean isConnected) {
    public PlayerState(String fullName, byte value) {
        this.fullName = fullName;
        //this.isNotAway = isConnected;
        this.value = value;
    }

    public PlayerState() {
        this.fullName = "";
        this.value = PlayerState.DISCONNECTED;
    }

}