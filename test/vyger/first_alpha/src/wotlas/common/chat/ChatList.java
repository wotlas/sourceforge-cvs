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

package wotlas.common.chat;

import wotlas.common.Player;

import java.util.Hashtable;

/** Interface of a Chat.
 *
 * @author Petrus
 * @see wotlas.server.ChatImpl
 * @see wotlas.client.ChatImpl
 */

public interface ChatList
{
  /** To get the number of existing ChatRooms.
   */
  public int getNumberOfChatRooms();

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
  
  /** To add a player to a ChatRoom.
   *
   * @param primaryKey primary key of ChatRoom to modify
   * @param player Player to add
   */
  public boolean addPlayer(String primaryKey, Player player);
  
  /** To remove a player from a ChatRoom.
   *
   * @param primaryKey primary key of ChatRoom to modify
   * @param player Player to remove
   */
  public boolean removePlayer(String primaryKey, Player player);
  
  /** To get the list of players of a ChatRoom
   *
   * @param primaryKey primary key of the ChatRoom
   */
  public Hashtable getPlayers(String primaryKey);

  /** To get all the ChatRooms. Use with : synchronized( ... ) {} please !
   *
   * @param primaryKey primary key of ChatRoom we want to get
   */
  public Hashtable getChatRooms();
  
}
