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
 * A NetMessageCategory simply contains IDs of the message types belonging 
 * to this category. To a NetMessageCategory corresponds ONE category ID
 * declared in the NetMessageRegistry.
 *<br>
 * There are some restrictions :
 *<br>
 *  - IDs should be the size of a byte to match a message's category ID.<P>
 *  - the message type value should begin at zero.<P>
 *  - The word MSG should appear somewhere in the type's name.<P>
 *
 * Example:
 *<br><pre>
 *    interface ChatMessageCategory extends NetMessageCategory {
 *
 *      public final static byte CHAT_CREATE_MSG    = 0;
 *      public final static byte CHAT_STRING_MSG    = 1;
 *      public final static byte CHAT_LEAVE_MSG     = 2;
 *    }
 *</pre>
 * Each of these message types has its own NetMessage and NetMessageBehaviour Class.
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetMessageRegistry
 * @see wotlas.libs.net.NetMessage
 */

public interface NetMessageCategory
{
}

