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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.util.*;

import wotlas.libs.net.NetMessage;
import wotlas.common.Player;
import wotlas.common.universe.WotlasLocation;


/** 
 * The messages the GameServer sends to give us the players data
 * of a room (Message Sent by Server).
 *
 * @author Aldiss
 */

public class RoomPlayerDataMessage extends PlayerDataMessage
{
 /*------------------------------------------------------------------------------------*/

  /** Player reference.
   */
     private Player myPlayer;

  /** Players.
   */
     protected Hashtable players;

  /** Wotlas Location
   */
     protected WotlasLocation location;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public RoomPlayerDataMessage() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with the Players object and our Player ( myPlayer ). The 'myPlayer'
   *  parameter is needed as we don't want to send our Player's data with the other
   *  players.
   *
   * @param location WotlasLocation from which the player's list comes from.
   * @param myPlayer our player.
   * @param players our players.
   */
     public RoomPlayerDataMessage( WotlasLocation location, Player myPlayer,
                                   Hashtable players ) {
         super();
         this.myPlayer = myPlayer;
         this.otherPlayer = myPlayer;
         this.location = location;
         this.players = players;
         this.publicInfoOnly = true;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void encode( DataOutputStream ostream ) throws IOException {

      // Wotlas Location
         ostream.writeInt( location.getWorldMapID() );
         ostream.writeInt( location.getTownMapID() );
         ostream.writeInt( location.getBuildingID() );
         ostream.writeInt( location.getInteriorMapID() );
         ostream.writeInt( location.getRoomID() );

      // Players
         synchronized( players ) {
            if( players.containsKey( myPlayer.getPrimaryKey() ) )
                ostream.writeInt( players.size()-1 );
            else
                ostream.writeInt( players.size() );

            Iterator it = players.values().iterator();

            while( it.hasNext() ) {
            	player = (Player) it.next();

                if( myPlayer!=player )
                    super.encode( ostream );
            }
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
     public void decode( DataInputStream istream ) throws IOException {

         // Wotlas Location
            location = new WotlasLocation();

            location.setWorldMapID( istream.readInt() );
            location.setTownMapID( istream.readInt() );
            location.setBuildingID( istream.readInt() );
            location.setInteriorMapID( istream.readInt() );
            location.setRoomID( istream.readInt() );

         // Players
            int nbPlayers = istream.readInt();
            
            if(nbPlayers>0)
               players = new Hashtable((int)(nbPlayers*1.6));
            else {
               players = new Hashtable();
               return;
            }

            for( int i=0; i<nbPlayers; i++ ) {
                 super.decode( istream );
                 players.put( player.getPrimaryKey(), player );
            }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

