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

package wotlas.common;

import wotlas.common.universe.*;


/** Interface of a Wotlas Player.
 *
 * @author Aldiss
 * @see wotlas.server.PlayerImpl
 * @see wotlas.client.PlayerImpl
 */

public interface Player
{
 /*------------------------------------------------------------------------------------*/

   /** When this method is called, the player can intialize its own fields safely : all
    *  the game data has been loaded.
    */
      public void init();

 /*------------------------------------------------------------------------------------*/

   /** To get the player location
    *
    *  @return player WotlasLocation
    */
      public WotlasLocation getLocation();

 /*------------------------------------------------------------------------------------*/

   /** To get the player name ( short name )
    *
    *  @return player name
    */
      public String getPlayerName();

 /*------------------------------------------------------------------------------------*/

   /** To get the player's full name.
    *
    *  @return player full name ( should contain the player name )
    */
      public String getFullPlayerName();

 /*------------------------------------------------------------------------------------*/

   /** To get the player primary Key ( account name or any unique ID )
    *
    *  @return player primary key
    */
      public String getPrimaryKey();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}