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

package wotlas.common.universe;

import java.awt.Dimension;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import wotlas.libs.persistence.BackupReady;

public class StoreTileMapBackground implements BackupReady {

    /**
     * id used in Serialized interface.
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
        super();
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

    /* (non-Javadoc)
     * @see wotlas.libs.persistence.BackupReady#ExternalizeGetVersion()
     */
    public int ExternalizeGetVersion() {
        // FIXME Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see wotlas.libs.persistence.BackupReady#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        // FIXME Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see wotlas.libs.persistence.BackupReady#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        // FIXME Auto-generated method stub

    }
}