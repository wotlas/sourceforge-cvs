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

package wotlas.editor;

import java.awt.Dimension;
import wotlas.common.universe.MapExit;
import wotlas.libs.persistence.BackupReady;

public class StoreTileMapBackground implements BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    public byte[][][] graphic;
    public boolean[][] mask;
    public MapExit[] exits;
    public Dimension mapSize;
    public String fileName;
    public String areaName;
    public String shortName;
    public String fullName;

    public StoreTileMapBackground() {
    }

    public StoreTileMapBackground(byte[][][] graphic, boolean[][] mask, MapExit[] exits, Dimension mapSize, String fileName,
            String areaName, String shortName, String fullName) {
        this.graphic = graphic;
        this.mask = mask;
        this.exits = exits;
        this.mapSize = mapSize;
        this.fileName = fileName;
        this.areaName = shortName;
        this.shortName = shortName;
        this.fullName = fullName;
    }

    /** return Version of the Serialized object to
     * fullfill auto-serialize upgrade
     * @return number of version for this class to be used
     * in serialize auto-upgrade
     */
    public int ExternalizeGetVersion() {
        return 1;
    }

    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.graphic = (byte[][][]) objectInput.readObject();
            this.mask = (boolean[][]) objectInput.readObject();
            this.exits = (MapExit[]) objectInput.readObject();
            this.mapSize = (Dimension) objectInput.readObject();
            this.fileName = (String) objectInput.readObject();
            this.areaName = (String) objectInput.readObject();
            this.shortName = (String) objectInput.readObject();
            this.fullName = (String) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }

    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeObject(this.graphic);
        objectOutput.writeObject(this.mask);
        objectOutput.writeObject(this.exits);
        objectOutput.writeObject(this.mapSize);
        objectOutput.writeObject(this.fileName);
        objectOutput.writeObject(this.areaName);
        objectOutput.writeObject(this.shortName);
        objectOutput.writeObject(this.fullName);
    }
}