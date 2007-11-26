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

package wotlas.libs.pathfinding;

import java.awt.Point;

/** a Node represents a cell in the matrix associated with a {@link AStar#mask mask}
 * it is used in the A* algorithm.
 *
 * @see wotlas.libs.pathfinding.AStar
 * @author Petrus
 */

public class NodeDouble {
    /*------------------------------------------------------------------------------------*/

    /** coordonate
     */
    Point point;

    /** cost
     */
    double g;

    /** distance to the goal (heuristic)
     */
    double h;

    /** total : f=g+h
     */
    double f;

    /** Node's parent
     */
    NodeDouble parent;

    /*------------------------------------------------------------------------------------*/

    /**
     * Constructor
     */
    NodeDouble() {
        this.parent = null;
    }

}