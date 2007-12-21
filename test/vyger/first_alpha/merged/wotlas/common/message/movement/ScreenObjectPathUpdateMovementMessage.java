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
import wotlas.common.movement.MovementComposer;

/** 
 * A message containing movement update along a path. (Message Sent by Server).
 *
 * @author Aldiss, Diego
 */

public class ScreenObjectPathUpdateMovementMessage extends PathUpdateMovementMessage {
    /** Constructor. Just initializes the message category and type.
    */
    public ScreenObjectPathUpdateMovementMessage() {
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
    public ScreenObjectPathUpdateMovementMessage(PathUpdateMovementMessage msg) {
        super();
        this.primaryKey = msg.primaryKey;
        this.syncID = msg.syncID;

        this.srcPoint = msg.srcPoint;
        this.orientationAngle = msg.orientationAngle;
        this.isMoving = msg.isMoving;
        if (this.isMoving) {
            this.dstPoint = msg.dstPoint;
            this.movementDeltaTime = msg.movementDeltaTime;
        }
    }

    /** Constructor with the Player object.
    *
    * @param pFollower player's path follower.
    * @param primaryKey if set we also send this player's primary key so that
    *        the message's destination will be easily identified on the other side.
    * @param syncID the player's synchronization ID. See the player.getSyncID() method
    *        for more details.
    */
    public ScreenObjectPathUpdateMovementMessage(MovementComposer pFollower, String primaryKey, byte syncID) {
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
}