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
import wotlas.common.universe.*;

import java.io.*;
import java.util.Set;

/** Interface of a Chat.
 *
 * @author Petrus
 * @see wotlas.server.ChatImpl
 * @see wotlas.client.ChatImpl
 */

public interface ChatList
{
  /** To add a ChatRoom.
   */
  public boolean addChatRoom(ChatRoom chatRoom);
  
  /** To remove a ChatRoom.
   *
   * @param primaryKey ChatRoom primary key
   */
  public boolean removeChatRoom(String primaryKey);
  
  /** To get a ChatRoom.
   *
   * @param primaryKey primary key of ChatRoom we want to get
   */
  public ChatRoom getChatRoom(String primaryKey);
  
  /** To get current ChatRoom.
   */
  public ChatRoom getCurrentChatRoom();
  
  /** To set the current active window.
   *
   * @param primaryKey primary key of current ChatRoom
   */
  public boolean setCurrentChatRoom(String primaryKey);
  
  /** To add a player to a ChatRoom.
   *
   * @param primaryKey primary key of ChatRoom to modify
   * @param playerPrimaryKey primary key of Player to add
   */
  public boolean addPlayer(String primaryKey, String playerPrimaryKey);
  
  /** To remove a player from a ChatRoom.
   *
   * @param primaryKey primary key of ChatRoom to modify
   * @param playerPrimaryKey primary key of Player to remove
   */
  public boolean removePlayer(String primaryKey, String playerPrimaryKey);
  
  /** To get the list of players of a ChatRoom
   *
   * @param primaryKey primary key of the ChatRoom
   */
  public Set getPlayers(String primaryKey);
  
}
