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

package wotlas.common.movement;

import wotlas.common.message.movement.*;
import wotlas.common.*;
import wotlas.common.screenobject.*;

import java.awt.Point;


/** An interface representing a generic player/ScreenObject (to manage npc) movement.
 *
 * @author Aldiss, Diego
 * @see wotlas.common.Player
 * @see wotlas.common.ScreenObject
 */

public interface MovementComposer extends Tickable {

 /*------------------------------------------------------------------------------------*/

   /** To init the MovementComposer classes with the ground's mask.
    * @param mask two dimension mask representing the zones where the players can go.
    * @param maskTileSize mask tile size (in pixels).
    * @param playerSize represents the average player size ( in maskTileSize unit )
    */
     public void setMovementMask( boolean mask[][], int maskTileSize, int playerSize );

 /*------------------------------------------------------------------------------------*/

    /** To init this MovementComposer.
     * @param player associated player.
     */
     public void init( Player player );

     /** To init this MovementComposer.
     * @param screenObject associated ScreenObject.
     */
     public void init( ScreenObject screenObject );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the current X position.
   * @return x cordinate
   */
     public float getXPosition();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the current Y position.
   * @return y cordinate
   */
     public float getYPosition();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the current X position.
   * @param xPosition x cordinate
   */
     public void setXPosition( float xPosition );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set Y.
   * @param yPosition y cordinate
   */
     public void setYPosition( float yPosition );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the position from a Point.
   * @param p screen point
   */
     public void setPosition(Point p);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the position as a Point object.
   * @return point
   */
     public Point getPosition();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the orientation angle.
   * @return angle in radians.
   */
     public double getOrientationAngle();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the orientation angle.
   * @param angleRad angle in radians.
   */
     public void setOrientationAngle(double orientationAngle);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set player's speed
   * @param speed
   */
     public void setSpeed( float speed );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get player's speed
   * @return speed
   */
     public float getSpeed();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set player's angular speed
   * @param angularSpeed
   */
     public void setAngularSpeed(float angularSpeed);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get player's angular speed
   * @return angularSpeed
   */
     public float getAngularSpeed();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the destination position of the current movement.
   *  Should return an "out of screen" point if there are no current movement.
   * @return new point representing the future end position of the player.
   */
     public Point getTargetPosition();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set a player's movement : movement from current position to the given point.
   */
     public void moveTo( Point endPosition, WorldManager wManager );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To rotate the player on itself.
   *  @param finalOrientation final orientation to reach
   */
     public void rotateTo( double finalOrientation );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns true if the player is moving.
   * @return isMoving
   */
     public boolean isMoving();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To reset the current player's movement. It behaves like stopMovement() but
   *  stopMovement is supposed to publish messages on the network.
   */
     public void resetMovement();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To stop the current player's movement.
   */
     public void stopMovement();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set if we want realistic rotations or not.
   * @param realisticRotations true if you want realistic rotations.
   */
     public void setRealisticRotations( boolean realisticRotations );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tick Method to update the state of this MovementComposer.
   */
    public void tick();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an update message representing the current movement state.
   *
   * @return a MovementUpdateMessage 
   */
     public MovementUpdateMessage getUpdate();

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To update the current movement.
   * @param updateMessage MovementUpdateMessage
   */
     public void setUpdate( MovementUpdateMessage updateMessage );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
     
     public long getMovementTimeStamp();
}