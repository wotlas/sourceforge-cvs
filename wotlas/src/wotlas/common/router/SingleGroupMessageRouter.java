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

package wotlas.common.router;

import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.common.chat.*;

import wotlas.libs.net.NetMessage;
import wotlas.utils.Debug;

import java.util.Hashtable;
import java.util.Iterator;

/** A message router that manages only a single group of players. Useful for WorldMaps
 *  TownMaps. We don't advertise people's presence...
 *
 * @author Aldiss
 */

public class SingleGroupMessageRouter extends MessageRouter {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Constructor. Just creates internals.
    */
     public SingleGroupMessageRouter() {
         super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** This method does nothing for this router.
    *
    * @param location location this MessageRouter is linked to.
    * @param wManager WorldManager of the application.
    */
     public void init( WotlasLocation location, WorldManager wManager ) {
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To add a player to this group. The default implementation of this method just
    *  add the player to our list WITHOUT sending any messages.
    *
    * @param player player to add
    * @return true if the player was added successfully, false if an error occured.
    */
     public boolean addPlayer( Player player ) {
         players.put( player.getPrimaryKey(), player );
         return true;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** This method does nothing for this router.
    *
    * @param player player to move
    * @return true if the player was moved successfully, false if an error occured.
    */
     public boolean movePlayer( Player player, WotlasLocation targetLocation ) {
        return false;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To send a list of messages to the specified group with the exception of a player.
    *  for this router messages are only sent to the LOCAL group we manage. Other
    *  groupOption values are not considered. If you use the EXC_EXTENDED_GROUP option
    *  no message will be sent.
    *
    *  @param msg message to send to the group
    *  @param exceptThisPlayer player to except from the send of messages, if the
    *         given player is null the message will be sent to everyone in the selected
    *         groups.
    *  @param groupOption gives the groups to send the message to. See the constants
    *         defined in this class : LOCAL_GROUP, EXTENDED_GROUP, EXC_EXTENDED_GROUP
    */
     public void sendMessages( NetMessage msg[], Player exceptThisPlayer, byte groupOption ) {

           if( groupOption==EXC_EXTENDED_GROUP )
               return;
        
        // We send the messages to the local group.
           synchronized( players ) {
              Iterator it = players.values().iterator();

              while( it.hasNext() ) {
                  Player p = (Player) it.next();

                  if( p!=exceptThisPlayer )
                      for( int i=0; i< msg.length; i++ )
                           p.sendMessage( msg[i] );
              }
           }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

