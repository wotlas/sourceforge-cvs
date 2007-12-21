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
package wotlas.libs.persistence;

import java.io.Externalizable;
import java.io.IOException;

/** Means of this Implementation is to complete DATA maniupulation
 * Externalizable let us save classes in a java.io.ObjectInput/ObjectOutput
 * but we still have a problem : what can I do, when I should upgrade data?
 * To survive to this problem, we have 2 main operations to do :
 * 1) find if data structure is old
 * 2) upgrade data
 *
 * the 2nd problem we follow is to know what's the ID for this data.
 * 3) Know ID field in this Record.
 */
public interface BackupReady extends Externalizable {

    /** used to Serialize
     * @param objectOutput as in serialize
     * @exception IOException as in serialize
     */
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException;

    /** used to Serialize
     * @param objectInput as in serialize
     * @exception IOException as in serialize
     * @exception ClassNotFoundException as in serialize
     */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException;

    /** return Version of the Serialized object to
     * fullfill auto-serialize upgrade
     * @return number of version for this class to be used
     * in serialize auto-upgrade
     */
    public int ExternalizeGetVersion();

}
