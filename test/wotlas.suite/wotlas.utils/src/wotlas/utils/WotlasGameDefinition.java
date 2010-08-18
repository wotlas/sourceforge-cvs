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
 * Used by wotlas.utils.Tools to filter which implementor class is allowed to be used in this game settings. 
 * 
 * @author SleepingOwl
 *
 */
public interface WotlasGameDefinition {

    /** Unique id for the game definition of the wotlas client */
    public static final String ID_WOTLAS_CLIENT = "www.wotlas.org/client";

    /** Unique id for the game definition of the wotlas server */
    public static final String ID_WOTLAS_SERVER = "www.wotlas.org/server";

    /**
     * @return an unique String that makes this definition unique in all games.
     */
    public String getDefinitionId();

    /**
     * @param wge a class implementing an extension of the framework.
     * @return true if using this class is allowed in this game settings.
     */
    public boolean isImplementorClassAllowed(WishGameExtension wge);

}
