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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.awt.Point;

import wotlas.libs.net.NetMessage;
import wotlas.common.Player;
import wotlas.common.character.WotCharacter;
import wotlas.common.universe.WotlasLocation;
import wotlas.common.movement.*;

import wotlas.libs.pathfinding.*;

import wotlas.utils.Tools;

/** 
 * A message containing movement update along a path. (Message Sent by Server).
 *
 * @author Aldiss, Diego
 */

public class ScreenObjectPathUpdateMovementMessage extends PathUpdateMovementMessage
{
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
    public ScreenObjectPathUpdateMovementMessage( PathUpdateMovementMessage msg ) {
         super();
         this.primaryKey = msg.primaryKey;
         this.syncID = msg.syncID;

         srcPoint = msg.srcPoint;
         orientationAngle = msg.orientationAngle;
         isMoving = msg.isMoving;
         if( isMoving ) {
             dstPoint = msg.dstPoint;
             movementDeltaTime = msg.movementDeltaTime;
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
    public ScreenObjectPathUpdateMovementMessage( ScreenObjectPathFollower pFollower, String primaryKey, byte syncID ) {
         super();
         this.primaryKey = primaryKey;
         this.syncID = syncID;

         srcPoint = new Point();
         srcPoint.x = (int)pFollower.getXPosition();
         srcPoint.y = (int)pFollower.getYPosition();

         orientationAngle = (float)pFollower.getOrientationAngle();
         isMoving = pFollower.isMoving();

         if( pFollower.getTargetPosition()==null )
             isMoving = false;

         if( isMoving ) {
             dstPoint = new Point( pFollower.getTargetPosition() );
             movementDeltaTime = (int)(System.currentTimeMillis()-pFollower.getMovementTimeStamp());
         }
     }
}