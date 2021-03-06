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

import java.io.*;

public interface SendObjectReady extends Serializable{
    
	/** return Version of the Serialized object to
	 * fullfill auto-serialize upgrade
	 * @return number of version for this class to be used
	 * in serialize auto-upgrade
	 */
	public int ExternalizeGetVersion();
        
	public void writeObject(java.io.ObjectOutputStream objectOutput)
	throws java.io.IOException;

	public void readObject(java.io.ObjectInputStream objectInput)
	throws java.io.IOException, java.lang.ClassNotFoundException;
}