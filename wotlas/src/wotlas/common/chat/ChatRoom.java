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
 
package wotlas.common.chat;

import wotlas.common.Player;
import wotlas.common.universe.WotlasLocation;

import wotlas.utils.Debug;

import java.util.Hashtable;
import java.util.Set;

/** A chat room of the Chat Engine
 *
 * @author Petrus
 */

public class ChatRoom
{
  
 /*------------------------------------------------------------------------------------*/

  /** ID of the ChatRoom
   */
  private String primaryKey;
  
  /** Name of the ChatRoom
   */
  private String name;
  
  /** ID of the player who created the ChatRoom
   */
  private String creatorPrimaryKey;
  
  /** Location where the ChatRoom was created
   */
  private WotlasLocation location;
  
  /** Number maximum of players
   */
  private int maxPlayers;
  
  /** List of players' primary key in the ChatRoom
   */
  private transient Set players;
  
 /*------------------------------------------------------------------------------------*/  
 
  /** Constructor
   */
  public ChatRoom() {
  }
  
 /*------------------------------------------------------------------------------------*/  

  /** List of setters and getters
   */
  
  public String getPrimaryKey() {
    return primaryKey;
  }
  
  public void setPrimaryKey(String primaryKey) {
    this.primaryKey = primaryKey;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getPlayerCreatorKey() {
    return creatorPrimaryKey;
  }
  
  public void setCreatorPrimaryKey(String creatorPrimaryKey) {
    this.creatorPrimaryKey = creatorPrimaryKey;
  }
  
  public WotlasLocation getLocation() {
    return location;
  }
  
  public void setLocation(WotlasLocation location) {
    this.location = location;
  }
  
  public Set getPlayers() {
    return players;
  }

 /*------------------------------------------------------------------------------------*/  
  
  /** Add a player to this ChatRoom. The player must have been previously initialized.  
   *
   * @param player player's primary key to add
   * @return false if the player already exists on this ChatRoom, true otherwise
   */
  public boolean addPlayer(String primaryKey) {
    if ( players.contains(primaryKey) ) {
      Debug.signal( Debug.CRITICAL, this, "addPlayer failed: key "+primaryKey
                         +" already in "+this );
      return false;
    }

    players.add(primaryKey);
    return true;
  }
  
  /** Add a player to this ChatRoom. The player must have been previously initialized.  
   *
   * @param player player to add
   * @return false if the player already exists on this ChatRoom, true otherwise
   */
  public boolean addPlayer(Player player) {
    return addPlayer(player.getPrimaryKey());
  }
    
 /*------------------------------------------------------------------------------------*/  

  /** Removes a player from this ChatRoom.   
   *
   * @param player player to remove
   * @return false if the player doesn't exists in this ChatRoom, true otherwise
   */
  public boolean removePlayer(String primaryKey) {
    if ( !players.contains(primaryKey) ) {
      Debug.signal( Debug.CRITICAL, this, "removePlayer failed: key "+primaryKey
                         +" not found in "+this );
      return false;
    }
    players.remove( primaryKey );
    return true;
  }
  
  /** Removes a player from this ChatRoom.   
   *
   * @param player player to remove
   * @return false if the player doesn't exists in this ChatRoom, true otherwise
   */
  public boolean removePlayer(Player player) {
    return removePlayer(player.getPrimaryKey());
  }
    
 /*------------------------------------------------------------------------------------*/  

  /** String Info.
   */
  public String toString(){
    return "ChatRoom : " + primaryKey;
  }

 /*------------------------------------------------------------------------------------*/  
 
}
