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

package wotlas.common.message.movement;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import wotlas.common.movement.PathFollower;

/** 
 * A message containing movement update along a path. (Message Sent by Server or Client).
 *
 * @author Aldiss
 */

public class PathUpdateMovementMessage extends MovementUpdateMessage {
    /*------------------------------------------------------------------------------------*/

    /** Source Point
     */
    public Point srcPoint;

    /** Destination Point
     */
    public Point dstPoint;

    /** is there a movement ?
     */
    public boolean isMoving;

    /** Time elapsed since movement begun.
     */
    public int movementDeltaTime;

    /** player orientation
     */
    public float orientationAngle;

    /** Eventual player primaryKey.
     */
    public String primaryKey;

    /** SyncID of our player.
     */
    public byte syncID;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. Just initializes the message category and type.
     */
    public PathUpdateMovementMessage() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with the Player object.
     *
     * @param pFollower player's path follower.
     * @param primaryKey if set we also send this player's primary key so that
     *        the message's destination will be easily identified on the other side.
     * @param syncID the player's synchronization ID. See the player.getSyncID() method
     *        for more details.
     */
    public PathUpdateMovementMessage(PathFollower pFollower, String primaryKey, byte syncID) {
        super();
        this.primaryKey = primaryKey;
        this.syncID = syncID;

        this.srcPoint = new Point();
        this.srcPoint.x = (int) pFollower.getXPosition();
        this.srcPoint.y = (int) pFollower.getYPosition();

        this.orientationAngle = (float) pFollower.getOrientationAngle();
        this.isMoving = pFollower.isMoving();

        if (pFollower.getTargetPosition() == null)
            this.isMoving = false;

        if (this.isMoving) {
            this.dstPoint = new Point(pFollower.getTargetPosition());
            this.movementDeltaTime = (int) (System.currentTimeMillis() - pFollower.getMovementTimeStamp());
        }
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

        ostream.writeByte(this.syncID);

        ostream.writeInt(this.srcPoint.x);
        ostream.writeInt(this.srcPoint.y);

        ostream.writeFloat(this.orientationAngle);
        ostream.writeBoolean(this.isMoving);

        if (this.isMoving) {
            ostream.writeInt(this.dstPoint.x);
            ostream.writeInt(this.dstPoint.y);
            ostream.writeInt(this.movementDeltaTime);
        }

        if (this.primaryKey != null) {
            ostream.writeBoolean(true);
            ostream.writeUTF(this.primaryKey);
        } else
            ostream.writeBoolean(false);
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

        this.syncID = istream.readByte();

        this.srcPoint = new Point();

        this.srcPoint.x = istream.readInt();
        this.srcPoint.y = istream.readInt();

        this.orientationAngle = istream.readFloat();
        this.isMoving = istream.readBoolean();

        if (this.isMoving) {
            this.dstPoint = new Point();
            this.dstPoint.x = istream.readInt();
            this.dstPoint.y = istream.readInt();
            this.movementDeltaTime = istream.readInt();
        }

        if (istream.readBoolean())
            this.primaryKey = istream.readUTF();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
