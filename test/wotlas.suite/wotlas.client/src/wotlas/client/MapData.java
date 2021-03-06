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

package wotlas.client;

/** Interface of a map (WorldMap, TownMap or InteriorMap
 *
 * @author Petrus
 * @see wotlas.client.WorldMapData
 * @see wotlas.client.TownMapData
 * @see wotlas.client.InteriorMapData
 */

public interface MapData {

    /*------------------------------------------------------------------------------------*/

    /** Connection timeout
     */
    public static final int CONNECTION_TIMEOUT = 5000;

    /** Set to true to show debug information
     */
    public void showDebug(boolean value);

    /*------------------------------------------------------------------------------------*/

    /** To set isNotMovingToAnotherMap
     */
    public void setIsNotMovingToAnotherMap(boolean value);

    /*------------------------------------------------------------------------------------*/

    /** To init the display<br>
     * - load background and mask images<br>
     * - init the AStar algorithm
     * - init the Graphics Director
     * - show the other images (shadows, buildings, towns...)
     */
    public void initDisplay(PlayerImpl myPlayer, DataManager dataManager);

    /*------------------------------------------------------------------------------------*/

    /** To update the location<br>
     * - test if player is intersecting a screenZone<br>
     * - test if player is entering a new WotlasLocation<br>
     * - change the current MapData
     *
     * @param myPlayer the master player
     */
    public void locationUpdate(PlayerImpl myPlayer);

    /*------------------------------------------------------------------------------------*/

}
