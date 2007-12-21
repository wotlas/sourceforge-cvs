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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import wotlas.common.Player;
import wotlas.common.screenobject.ScreenObject;
import wotlas.common.universe.TileMap;
import wotlas.common.universe.WotlasLocation;

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
    public TileMapPlayerDataMessage(TileMap tileMap, Player myPlayer) {
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
    @Override
    public void encode(DataOutputStream ostream) throws IOException {

        // Wotlas Location
        ostream.writeInt(this.location.getWorldMapID());
        ostream.writeInt(this.location.getTownMapID());
        ostream.writeInt(this.location.getBuildingID());
        ostream.writeInt(this.location.getInteriorMapID());
        ostream.writeInt(this.location.getRoomID());
        ostream.writeInt(this.location.getTileMapID());

        // screenObjects
        synchronized (this.screenObjects) {
            if (this.screenObjects.containsKey(this.myPlayer.getPrimaryKey()))
                ostream.writeInt(this.screenObjects.size() - 1);
            else
                ostream.writeInt(this.screenObjects.size());

            Iterator it = this.screenObjects.values().iterator();

            ScreenObject item = null;
            while (it.hasNext()) {
                item = (ScreenObject) it.next();

                if (this.myPlayer.getPrimaryKey() != item.getPrimaryKey())
                    try {
                        new ObjectOutputStream(ostream).writeObject(item);
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
    @Override
    public void decode(DataInputStream istream) throws IOException {

        // Wotlas Location
        this.location = new WotlasLocation();

        this.location.setWorldMapID(istream.readInt());
        this.location.setTownMapID(istream.readInt());
        this.location.setBuildingID(istream.readInt());
        this.location.setInteriorMapID(istream.readInt());
        this.location.setRoomID(istream.readInt());
        this.location.setTileMapID(istream.readInt());

        // items : screeObjects to show.
        int nbItems = istream.readInt();

        if (nbItems > 0)
            this.screenObjects = new Hashtable((int) (nbItems * 1.6));
        else {
            this.screenObjects = new Hashtable();
            return;
        }

        ScreenObject item = null;
        for (int i = 0; i < nbItems; i++) {
            try {
                item = (ScreenObject) new ObjectInputStream(istream).readObject();
                this.screenObjects.put(item.getPrimaryKey(), item);
            } catch (Exception e) {
                System.out.println(" diego: error, should still decide how to manage this error");
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}