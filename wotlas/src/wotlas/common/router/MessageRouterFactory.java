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

package wotlas.common.router;

import wotlas.common.*;
import wotlas.common.universe.*;


/** A factory of MessageRouters. This is an abstract factory. You need to create an
 *  implementation of it and give it to the WorldManager. The server & client sides
 *  of wotlas will use different factories.
 *
 * @author Aldiss
 */

public interface MessageRouterFactory {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Inititializes this MessageRouterFactory.
    */
     public void init();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates or gets a MessageRouter for a WorldMap.
    * @param wMap WorldMap the router is for...
    * @return a MessageRouter to use for this map
    */
     public MessageRouter createMsgRouterForWorldMap( WorldMap wMap, WorldManager wManager );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates or gets a MessageRouter for a TownMap.
    * @param tMap TownMap the router is for...
    * @return a MessageRouter to use for this map
    */
     public MessageRouter createMsgRouterForTownMap( TownMap tMap, WorldManager wManager );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates or gets a MessageRouter for a Room.
    * @param room Room the router is for...
    * @return a MessageRouter to use for this map
    */
     public MessageRouter createMsgRouterForRoom( Room room, WorldManager wManager );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

