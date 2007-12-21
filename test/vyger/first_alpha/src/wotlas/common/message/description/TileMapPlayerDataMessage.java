/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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
import java.io.*;

import java.util.*;

import wotlas.libs.net.NetMessage;
import wotlas.common.Player;
import wotlas.common.universe.*;
import wotlas.common.screenobject.*;


/** 
 * The messages the GameServer sends to give us the players data
 * of a tilemap (Message Sent by Server).
 *
 * @author Aldiss, Diego
 */

public class TileMapPlayerDataMessage extends PlayerDataMessage {

 /*------------------------------------------------------------------------------------*/

  /** SHOULD BE SCREENOBJECTS reference.
   */
     private Player myPlayer;

  /** ScreenObjects
   */
     protected Hashtable screenObjects;

  /** Wotlas Location
   */
     protected WotlasLocation location;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public TileMapPlayerDataMessage() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with the Players object and our Player ( myPlayer ). The 'myPlayer'
   *  parameter is needed as we don't want to send our Player's data with the other
   *  players.
   *
   * @param location WotlasLocation from which the player's list comes from.
   * @param myPlayer our player.
   */
     public TileMapPlayerDataMessage( TileMap tileMap, Player myPlayer ) {
         super();
         this.myPlayer = myPlayer;
         this.location = tileMap.getLocation();
         this.screenObjects = tileMap.getMessageRouter().getScreenObjects();
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
         ostream.writeInt( location.getTileMapID() );

      // screenObjects
         synchronized( screenObjects ) {
            if( screenObjects.containsKey( myPlayer.getPrimaryKey() ) )
                ostream.writeInt( screenObjects.size()-1 );
            else
                ostream.writeInt( screenObjects.size() );

            Iterator it = screenObjects.values().iterator();

            ScreenObject item = null;
            while( it.hasNext() ) {
            	item = (ScreenObject) it.next();

                if( myPlayer.getPrimaryKey() != item.getPrimaryKey() )
                    try{
                        new ObjectOutputStream(ostream).writeObject( item ); 
                    } catch (Exception e) {
                        System.out.println("diego: error, should still decide how to manage this error");
                    }
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
            location.setTileMapID( istream.readInt() );

         // items : screeObjects to show.
            int nbItems = istream.readInt();
            
            if(nbItems>0)
               screenObjects = new Hashtable((int)(nbItems*1.6));
            else {
               screenObjects = new Hashtable();
               return;
            }

            ScreenObject item = null;
            for( int i=0; i<nbItems; i++ ) {
                try {
                    item = (ScreenObject) new ObjectInputStream(istream).readObject();             
                    screenObjects.put( item.getPrimaryKey(), item );
                } catch (Exception e) {
                     System.out.println(" diego: error, should still decide how to manage this error");
                }
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}