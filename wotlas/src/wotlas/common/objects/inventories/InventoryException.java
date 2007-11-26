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

package wotlas.common.objects.inventories;

/** An exception that can be thrown at Runtime by the Inventory.<br>
 * It's thrown if the player tries to use an object he shouldn't or he can't.<br>
 * There may be other exceptions for specific misuses.
 *
 * @author Elann
 */

public class InventoryException extends java.lang.RuntimeException {
    /** Exception Constructor without message.<br>
     * Always starts messages by "Invalid Inventory exception ".
     */
    public InventoryException() {
        super("Invalid Inventory command exception ");
    }

    /** Exception Constructor with message.<br>
     * Always starts messages by "Invalid Inventory exception ".
     */
    public InventoryException(String msg) {
        super("Invalid Inventory command exception " + msg);
    }

}
