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
 
package wotlas.libs.net;


/** 
 * A NetMessageRegistry simply contains IDs of message categories.
 * There are some restrictions :
 *<br>
 *  - IDs should be the size of a byte to match a message's category ID.<P>
 *  - the message category value should begin at 1. 0 is reserved by the system.<P>
 *  - The word CATEGORY should appear somewhere in the category's name.<P>
 *
 * Example:
 *<br><pre>
 *    interface MyMessageRegistry extends NetMessageRegistry {
 *
 *      public final static byte CHAT_CATEGORY      = 1;
 *      public final static byte FILE_CATEGORY      = 2;
 *    }
 *</pre>
 * Each of these message categories has its own NetMessageCategory Interface.
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetMessageCategory
 * @see wotlas.libs.net.NetMessage
 */

public interface NetMessageRegistry {

  public final static byte SYSTEM_CATEGORY    = 0;
}

