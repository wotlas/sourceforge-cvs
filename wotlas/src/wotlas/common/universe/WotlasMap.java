/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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

import wotlas.common.Player;
import java.util.Hashtable;

 /** Represents a Map of the game. It can receive, own players...
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.WorldMap
  * @see wotlas.common.universe.TownMap
  * @see wotlas.common.universe.Room
  */

public interface WotlasMap extends LocationOwner {

 /*------------------------------------------------------------------------------------*/

  /** Add a player to this map. The player must have been previously initialized.
   *  We suppose that the player.getLocation() points out to this map.
   *
   * @param player player to add
   * @return false if the player already exists on this Map, true otherwise
   */
    public boolean addPlayer( Player player );

 /*------------------------------------------------------------------------------------*/

  /** Removes a player from this map.
   *  We suppose that the player.getLocation() points out to this map.
   *
   * @param player player to remove
   * @return false if the player doesn't exists on this map, true otherwise
   */
    public boolean removePlayer( Player player );

 /*------------------------------------------------------------------------------------*/

  /** To get the list of all the players on this map.
   * IMPORTANT: before ANY process on this list synchronize your code on the "players"
   * object :
   *<pre>
   *   Hashtable players = map.getPlayers();
   *   
   *   synchronized( players ) {
   *       ... some SIMPLE and SHORT processes...
   *   }
   *</pre>
   * @return player hashtable, player.getPrimaryKey() is the key.
   */
    public Hashtable getPlayers();

 /*------------------------------------------------------------------------------------*/

  /** To get the full name of the map.
   * @return fullName
   */
    public String getFullName();

 /*------------------------------------------------------------------------------------*/

  /** To get a String representation of this map Object.
   * @return map type + map Full Name
   */
    public String toString();

 /*------------------------------------------------------------------------------------*/

}
