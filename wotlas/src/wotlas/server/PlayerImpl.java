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

package wotlas.server;

import wotlas.common.Player;

import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.net.NetPersonality;
import wotlas.libs.net.NetMessage;


/** Class of a Wotlas Player. It is the class that, in certain way, a client gets connected to.
 *  All the client messages have a server PlayerImpl context.
 *
 * @author Aldiss
 * @see wotlas.common.Player
 * @see wotlas.common.NetConnectionListener
 */

class PlayerImpl implements Player, NetConnectionListener
{
 /*------------------------------------------------------------------------------------*/

   /** No real fields for now. This field "test" is just what's its name suggests...
    */
       String test = new String("it's working."); // TO REMOVE

 /*------------------------------------------------------------------------------------*/

   /** Our NetPersonality, useful if we want to send messages !
    */
       transient private NetPersonality personality;

 /*------------------------------------------------------------------------------------*/

   /** When this method is called, the player can intialize its own fields safely : all
    *  the game data has been loaded.
    */
      public void init() {
         // ah, I get it , it's a joke... nothing to do here for now...
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when a new network connection is created on this player.
   *
   * @param personality the NetPersonality object associated to this connection.
   */
     public void connectionCreated( NetPersonality personality ) {

             synchronized( this.personality ) {
                 this.personality = personality;
             }

          // great we do nothing
             System.out.println("Connection opened on this player");
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when the network connection of the client is no longer
   * of this world.
   *
   * @param personality the NetPersonality object associated to this connection.
   */
     public void connectionClosed( NetPersonality personality ) {

             synchronized( this.personality ) {
                 this.personality = null;
             }

          // great we do nothing
             System.out.println("Connection closed on this player");
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Use this method to send a NetMessage to this player. You can use it directly :
   *  it does not lock, does not wait for the message to be sent before returning
   *  AND checks that the player is connected.
   *
   * @param message message to send to the player.
   * @return true if the message was sent, false if the client was not connected.
   */
     public boolean sendMessage( NetMessage message ) {

             synchronized( personality ) {
             	if( personality!=null ) {
                    personality.queueMessage( message );
                    return true;
                }
             }

         return false;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}