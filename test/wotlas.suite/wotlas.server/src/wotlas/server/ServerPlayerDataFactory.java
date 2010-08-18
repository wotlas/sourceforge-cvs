/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2009 WOTLAS Team
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
package wotlas.server;

import wotlas.utils.Debug;
import wotlas.utils.WishPlayerDataFactory;

/**
 *
 * @author SleepingOwl
 */
public class ServerPlayerDataFactory implements WishPlayerDataFactory {
    /** To get an instance of an object from its class name. We assume that the
     *  object has an empty constructor.
     *
     *  @param className a string representing the class name of the filter
     *  @return an instance of the object, null if we cannot get an instance.
     */
    public Object getInstance(String className) {
        if (className == null || !className.startsWith("wotlas.server"))
            return null;
        try {
            Class myClass = Class.forName(className);
            return myClass.newInstance();
        } catch (Exception ex) {
            Debug.signal(Debug.ERROR, null, "Failed to create new instance of " + className + ", " + ex);
            return null;
        }
    }
}
