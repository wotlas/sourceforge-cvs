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

package wotlas.common;

import wotlas.libs.persistence.BackupReady;

/** Utility for the chat
 *
 * @author Petrus, Diego
 * @see wotlas.client.screen.JChatRoom
 * @see wotlas.client.screen.JChatPanel
 * @see wotlas.common.Player
 */

public class PlayerState implements BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

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

    /** write object data with serialize.
     */
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeUTF(this.fullName);
        objectOutput.writeByte(this.value);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.fullName = objectInput.readUTF();
            this.value = objectInput.readByte();
        } else {
            // to do.... when new version
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** id version of data, used in serialized persistance.
     */
    public int ExternalizeGetVersion() {
        return 1;
    }
}