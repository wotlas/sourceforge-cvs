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
 
package wotlas.common.message.description;

import wotlas.libs.net.NetMessageCategory;

/** 
 * This NetMessageCategory represents the messages linked to the GameServer
 * when a client wants to obtain various descriptions about players, objects ...
 *
 * @author Aldiss
 */

public interface DescriptionMessageCategory extends NetMessageCategory {

       public final static byte PLAYER_DATA_MSG             = 0;
       public final static byte MY_PLAYER_DATA_PLEASE_MSG   = 1;
       public final static byte YOUR_PLAYER_DATA_MSG        = 2;
       public final static byte ROOM_PLAYER_DATA_MSG        = 3;
       public final static byte ALL_DATA_LEFT_MSG           = 4;
       public final static byte REMOVE_PLAYER_FROM_ROOM_MSG = 5;
       public final static byte ADD_PLAYER_TO_ROOM_MSG      = 6;
       public final static byte CLEAN_GHOST_PLAYERS_MSG     = 7;
       public final static byte DOOR_STATE_MSG              = 8;
       public final static byte DOORS_STATE_MSG             = 9;
       public final static byte PLAYER_PAST_MSG             = 10;
       public final static byte PLAYER_CONNECTED_GAME_MSG   = 11;
       public final static byte PLAYER_AWAY_MSG             = 12;
       public final static byte WELCOME_MSG                 = 13;
}

