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
 
package wotlas.common.message;

import wotlas.libs.net.NetMessageRegistry;

/** 
 * MessageRegistry where are declared all the messages categories used in wotlas
 * (mainly to avoid collisions between IDs).
 *<br><p>
 *
 * This registry is common to both server and client but not all categories are
 * opened to clients ( for instance the GATEWAY_CATEGORY is only server side ).
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetMessageCategory
 * @see wotlas.libs.net.NetMessageRegistry
 */

public interface MessageRegistry extends NetMessageRegistry {

 /** The zero Category is reserved ( system category )
  */

 /** Messages about moves in the world.
  */
  public final static byte MOVEMENT_CATEGORY     = 1;

 /** Messages about players & entity  states, description, etc...
  */
  public final static byte DESCRIPTION_CATEGORY  = 2;

 /** Messages of the chat category.
  */
  public final static byte CHAT_CATEGORY         = 3;

 /** Messages for account creation & config.
  */
  public final static byte ACCOUNT_CATEGORY      = 4;

 /** Messages for account transfer (Server Side Only) .
  */
  public final static byte GATEWAY_CATEGORY      = 5;

}

