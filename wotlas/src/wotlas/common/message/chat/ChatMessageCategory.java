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
 
package wotlas.common.message.chat;

import wotlas.libs.net.NetMessageCategory;

/** 
 * This NetMessageCategory represents the messages linked to the Chat Engine.
 *
 * @author Petrus
 */

public interface ChatMessageCategory extends NetMessageCategory {

       public final static byte CHATROOM_CREATION_MSG       = 0;
       public final static byte CHATROOM_CREATED_MSG        = 1;
       public final static byte CHATROOM_DELETED_MSG        = 2;
       public final static byte REM_PLAYER_FROM_CHATROOM_MSG= 3;
       public final static byte ADD_PLAYER_TO_CHATROOM_MSG  = 4;
       public final static byte SET_CURRENT_CHATROOM_MSG    = 5;
       public final static byte SEND_TEXT_MSG               = 6;
}

