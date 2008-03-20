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
import wotlas.common.Player;
import wotlas.common.character.BasicChar;
import wotlas.common.message.movement.MovementUpdateMessage;
import wotlas.common.movement.MovementComposer;
import wotlas.libs.net.NetMessage;
import wotlas.utils.Tools;

/** 
 * To send player data (Message Sent by Server).
 *
 * @author Aldiss, Petrus, Diego
 */

public class PlayerDataMessage extends NetMessage {
    /*------------------------------------------------------------------------------------*/

    /** Player interface.
     */
    protected Player player;

    /** key of destinated player.
     */
    protected Player otherPlayer;

    /** Do we have to write/read public info or all the player's data ?
     */
    protected boolean publicInfoOnly;

    /** Player Class to use when building the Player object.
     *  Default is client implementation. Use the appropriate constructor to change that.
     */
    private String playerClass = "wotlas.client.PlayerImpl";

    /*------------------------------------------------------------------------------------*/

    /** Constructor. Just initializes the message category and type.
     */
    public PlayerDataMessage() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with the Player object.
     *
     * @param player Player object to send.
     * @param publicInfoOnly tells if we have to write/read public info or all the player's data
     */
    public PlayerDataMessage(Player player, boolean publicInfoOnly) {
        super();
        this.player = player;
        this.publicInfoOnly = publicInfoOnly;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with the Player object and playerClass to use.
     *
     * @param player Player object to send.
     * @param publicInfoOnly tells if we have to write/read public info or all the player's data
     * @param playerClass to use when building the Player object.
     */
    public PlayerDataMessage(Player player, boolean publicInfoOnly, String playerClass) {
        super();
        this.player = player;
        this.publicInfoOnly = publicInfoOnly;
        this.playerClass = playerClass;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set otherPlayerKey
     *
     * @param otherPlayerKey key of player this message is sent to
     */
    public void setOtherPlayer(Player otherPlayer) {
        this.otherPlayer = otherPlayer;
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
        ostream.writeBoolean(this.publicInfoOnly);

        // Player Data
        ostream.writeUTF(this.player.getPlayerName());
        ostream.writeUTF(this.player.getFullPlayerName(this.otherPlayer));

        if (!this.publicInfoOnly) {
            ostream.writeUTF(this.player.getPlayerPast());
            ostream.writeUTF(this.player.getPlayerAwayMessage());
        }

        ostream.writeBoolean(this.player.isConnectedToGame());
        ostream.writeByte(this.player.getPlayerState().value);

        // Sync ID
        if (!this.publicInfoOnly)
            ostream.writeByte(this.player.getSyncID());

        // Movement Composer
        // FIXME blocked 'cause it cant be saved : on serverside is ServerPathFollower
        // but on clientside should be only PathFollower
        //        ostream.writeUTF( player.getMovementComposer().getClass().getName() );
        ostream.writeUTF("wotlas.common.movement.PathFollower");

        MovementUpdateMessage updateMsg = this.player.getMovementComposer().getUpdate();

        ostream.writeUTF(updateMsg.getClass().getName());
        updateMsg.encode(ostream);

        // Wotlas Character Data
        //  if(!publicInfoOnly){
        ostream.writeUTF(this.player.getBasicChar().getClass().getName());
        try {
            new ObjectOutputStream(ostream).writeObject(this.player.getBasicChar()); // call to encode character's data
        } catch (Exception e) {
            System.out.println(" diego: error, should still decide how to manage this error");
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
        this.publicInfoOnly = istream.readBoolean();

        // Player Client Instance creation ( no direct call to "server"
        // or "client" packages are issued from the "common" package )
        this.player = (Player) Tools.getInstance(this.playerClass);

        // Player Data
        this.player.setPlayerName(istream.readUTF());
        this.player.setFullPlayerName(istream.readUTF());

        if (!this.publicInfoOnly) {
            this.player.setPlayerPast(istream.readUTF());
            this.player.setPlayerAwayMessage(istream.readUTF());
        }

        this.player.setIsConnectedToGame(istream.readBoolean());
        this.player.getPlayerState().value = istream.readByte();

        // Sync ID
        if (!this.publicInfoOnly)
            this.player.setSyncID(istream.readByte());

        // Movement Composer
        MovementComposer mvComposer = (MovementComposer) Tools.getInstance(istream.readUTF());
        MovementUpdateMessage uMsg = (MovementUpdateMessage) Tools.getInstance(istream.readUTF());
        uMsg.decode(istream);

        // Wotlas Character
        BasicChar wotChar = (BasicChar) Tools.getInstance(istream.readUTF());
        try {
            this.player.setBasicChar((BasicChar) new ObjectInputStream(istream).readObject());
        } catch (Exception e) {
            System.out.println(" diego: error, should still decide how to manage this error");
        }
        this.player.setLocation(this.player.getLocation());

        // Movement Composer init
        mvComposer.init(this.player);
        mvComposer.setUpdate(uMsg); // in this order, because the player must have been fully initialized
        this.player.setMovementComposer(mvComposer);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player subject of this message.
    */
    public Player getPlayer() {
        return this.player;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}