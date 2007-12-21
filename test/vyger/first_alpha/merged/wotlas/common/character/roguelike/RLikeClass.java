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

package wotlas.common.character.roguelike;

import wotlas.common.character.CharData;
import wotlas.common.character.RLikeCharacter;
import wotlas.libs.persistence.BackupReady;
import wotlas.server.ServerDirector;

/** Super class of Rogue Like Class character, it's added into a RLikeCharacter
 *
 * @author Diego
 */
public abstract class RLikeClass implements BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    transient protected RLikeCharacter myChar;

    public void init(RLikeCharacter myChar) {
        this.myChar = myChar;
    }

    abstract public void RollStat();

    abstract public int getHitDice();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** write object data with serialize.
     */

    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
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

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    public void writeObject(java.io.ObjectOutputStream objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
    }

    public void readObject(java.io.ObjectInputStream objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
        } else {
            // to do.... when new version
        }
    }

    static protected short[] rollStat() {
        // ServerDirector.initRoll();

        short[] stats = new short[6];
        short[] prevStats = new short[6];
        int prevTotal = 0, total = 0;
        short stat;
        for (int a = 0; a < 6; a++) {
            stat = ServerDirector.roll(3, 6);
            prevStats[a] = stat;
            prevTotal += stat;
        }
        for (int b = 0; b < 4; b++) {
            for (int i = 0; i < 6; i++) {
                stat = ServerDirector.roll(3, 6);
                stats[i] = stat;
                total += stat;
            }
            if (prevTotal < total)
                prevStats = stats;
        }

        // order 'em
        stats = prevStats.clone();
        int bestIndex;
        for (int a = 0; a < 6; a++) {
            bestIndex = 0;
            for (int thisIndex = 0; thisIndex < 6; thisIndex++) {
                if (stats[bestIndex] < stats[thisIndex]) {
                    bestIndex = thisIndex;
                    prevStats[a] = stats[bestIndex];
                }
            }
            stats[bestIndex] = -1;
        }
        return prevStats;
    }

    public void setMyChar(RLikeCharacter myChar) {
        this.myChar = myChar;
    }

    /** used to manage level gain
     * to add hp, mana ; to add skills and spells and knowledge
     */
    public void gainLevel() {
        short value = ServerDirector.roll(1, getHitDice());
        this.myChar.addCharAttr(CharData.ATTR_HP, value);
    }

    public void clone(RLikeClass value) throws Exception {
    }
}