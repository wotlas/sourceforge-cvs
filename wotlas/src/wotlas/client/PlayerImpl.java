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

package wotlas.client;

import wotlas.common.Player;

import wotlas.common.universe.*;
import wotlas.utils.Debug;

/** Class of a Wotlas Player.
 *
 * @author Petrus
 * @see wotlas.common.Player 
 */

public class PlayerImpl implements Player
{

 /*------------------------------------------------------------------------------------*/

  /** Player's primary key (usually the client account name)
   */
  private String primaryKey;

  /** Player location
   */
  private WotlasLocation location;

  /** Player name
   */
  private String playerName;

  /** Player full name
   */
  private String fullPlayerName;
 
 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
  public PlayerImpl() {
    ;
  }
 
 /*------------------------------------------------------------------------------------*/

 /** When this method is called, the player can intialize its own fields safely : all
  *  the game data has been loaded.
  */
  public void init() {
    // nothing to do here for now...
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the player location.
   *
   *  @return player WotlasLocation
   */
  public WotlasLocation getLocation() {
    return location;
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the player location.
   *
   *  @param new player WotlasLocation
   */
  public void setLocation(WotlasLocation myLocation) {
    location = myLocation;
  }

 /*------------------------------------------------------------------------------------*/

 /** To get the player name ( short name )
  *
  *  @return player name
  */
  public String getPlayerName() {
    return playerName;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the player's full name.
   *
   *  @return player full name ( should contain the player name )
   */
  public String getFullPlayerName() {
    return fullPlayerName;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the player primary Key ( account name )
   *
   *  @return player primary key
   */
  public String getPrimaryKey() {
    return primaryKey;
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the player's name ( short name )
   *
   *  @param player name
   */
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the player's primary key.
   *
   *  @param primary key
   */
  public void setPrimaryKey(String primaryKey) {
    this.primaryKey = primaryKey;
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the player's full name.
   *
   *  @param player full name ( should contain the player name )
   */
  public void setFullPlayerName(String fullPlayerName) {
    this.fullPlayerName = fullPlayerName;
  }
  
 /*------------------------------------------------------------------------------------*/
  
}