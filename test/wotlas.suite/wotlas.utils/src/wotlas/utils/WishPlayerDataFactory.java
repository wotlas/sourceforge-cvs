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
package wotlas.utils;

/**
 * The classes implementing WishPlayerDataFactory are used to create data instances used by the current game.
 *
 * @author SleepingOwl
 */
public interface WishPlayerDataFactory extends WishGameExtension {

    /*------------------------------------------------------------------------------------*/
    /** To get an instance of an object from its class name. We assume that the
     *  object has an empty constructor.
     *
     *  @param className a string representing the class name of the filter
     *  @return an instance of the object, null if we cannot get an instance.
     */
    public Object getInstance(String className);
}
