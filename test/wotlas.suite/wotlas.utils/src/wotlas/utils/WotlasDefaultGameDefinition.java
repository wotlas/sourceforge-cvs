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
 * @author SleepingOwl
 *
 */
public class WotlasDefaultGameDefinition implements WotlasGameDefinition {

    /** An unique String that makes this definition unique in all games.*/
    private final String definitionId;

    /** List of string that a WishGameExtension must contain in its classname to be instantiated in this game. */
    private final String[] includes;

    /** List of string that a WishGameExtension must not contain in its classname to be instantiated in this game. */
    private final String[] excludes;

    /**
     * @param definitionIdp 
     * @param excludesFilters
     * @param includesFilters
     */
    public WotlasDefaultGameDefinition(String definitionIdp, String[] excludesFilters, String[] includesFilters) {
        super();
        this.definitionId = definitionIdp;
        this.excludes = excludesFilters;
        this.includes = includesFilters;
    }

    /* (non-Javadoc)
     * @see wotlas.utils.WotlasGameDefinition#getDefinitionId()
     */
    public String getDefinitionId() {
        return this.definitionId;
    }

    /* (non-Javadoc)
     * @see wotlas.utils.WotlasGameDefinition#isImplementorClassAllowed(wotlas.utils.WishGameExtension)
     */
    public boolean isImplementorClassAllowed(WishGameExtension wge) {
        String classname = wge.getClass().getName();

        if (classname != null) {
            for (int i = 0; this.includes != null && i < this.includes.length; i++) {
                if (this.includes[i] != null && classname.indexOf(this.includes[i]) != -1) {
                    return true;
                }
            }
            for (int i = 0; this.excludes != null && i < this.excludes.length; i++) {
                if (this.excludes[i] != null && classname.indexOf(this.excludes[i]) != -1) {
                    Debug.signal(Debug.NOTICE, this, "GameDefinition (" + this.definitionId + ") don't use or instantiate this class :" + classname);
                    return false;
                }
            }
        }
        return true;
    }
}
