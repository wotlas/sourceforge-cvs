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
 
package wotlas.libs.pathfinding;

import wotlas.common.Tickable;
import wotlas.utils.*;

import java.awt.Point;

/** 
 * A path follower... well yes, this class is a path adept :) It follows a given path
 * at a certain speed taking into account angle variations...
 *
 * IMPORTANT : this implementation is NOT synchronized... please avoid the situation
 *             when one thread is invoking the tick() method and the other a setXXX()...
 *
 * @author Petrus, Aldiss
 */

public class PathFollower implements Tickable {

 /*------------------------------------------------------------------------------------*/

  // PATH FIELDS

  /** Player's trajectory
   */
    private List path;

  /** Our current index in the Path ( next point we target )
   */
    private int pathIndex;

  /** Previous point in path.
   */
    private Point prevPoint;

  /** Next point in path.
   */
    private Point nextPoint;

  /** Next Angle in the path.
   */
    private float nextAngle;

  /** Our current movement walking ? turning ? have we the right to turn ?
   */
    private boolean walkingAlongPath, turningAlongPath, noRotations;

  /** Last update time.
   */
    private long lastUpdateTime;

 /*------------------------------------------------------------------------------------*/

  // KINEMATIC FIELDS

  /** Current position
   */
    private float xPosition, yPosition;

  /** Current Orientation (our angle in rads)
   */
    private double orientationAngle;

  /** PathFollower speed in pixel/s ( default 60 pixel/s )
   */
    private float speed = 60;

  /** PathFollower angular speed in radian/s ( default 3 rad/s )
   */
    private float angularSpeed = 3;

  /** in which angular direction are we turning ? +1 for positive direction, -1 otherwise.
   */
    private byte angularDirection;

 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor.
   */
    public PathFollower() {
        walkingAlongPath = false;
        turningAlongPath = false;
        noRotations = false;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with initial position & orientation
   *
   *  @param xPosition x position in pixels.
   *  @param yPosition y position in pixels.
   *  @param orientationAngle orientation angle in radians.
   */
    public PathFollower( float xPosition, float yPosition, double orientationAngle ) {
    	this();
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.orientationAngle = orientationAngle;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  // KINEMATIC GETTERS & SETTERS

  /** To get the X position.
   * @return x position
   */
    public float getXPosition() {
      return xPosition;
    }

  /** To set the X Position.
   * @param x x cordinate
   */
    public void setXPosition( float xPosition ){
      this.xPosition = xPosition;
    }

  /** To get the Y position.
   * @return y position
   */
    public float getYPosition() {
      return yPosition;
    }

  /** To set the Y Position.
   * @param y y cordinate
   */
    public void setYPosition( float yPosition ){
      this.yPosition = yPosition;
    }

  /** To get the orientation angle.
   * @return angle in radians.
   */
    public double getOrientationAngle() {
      return orientationAngle;
    }

  /** To set the orientation angle.
   * @param orientationAngle angle in radians.
   */
    public void setOrientationAngle(double orientationAngle) {
      this.orientationAngle = orientationAngle;
    }

  /** To set player's speed
   */
    public void setSpeed(float speed) {
      this.speed = speed;
    }

  /** To get player's speed
   */
    public float getSpeed() {
      return speed;
    }

  /** To set player's angular speed
   */
    public void setAngularSpeed(float angularSpeed) {
      this.angularSpeed = angularSpeed;
    }

  /** To get player's angular speed
   */
    public float getAngularSpeed() {
      return angularSpeed;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the position from a screenPoint.
    * @param p ScreenPoint
    */
     public void setPosition( ScreenPoint p ) {
        xPosition = (float) p.x;
        yPosition = (float) p.y;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the position from a Point.
    * @param p Point
    */
     public void setPosition( Point p ) {
        xPosition = (float) p.x;
        yPosition = (float) p.y;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the current position as a Point.
    * @return current position
    */
     public Point getPosition() {
     	return new Point( (int)xPosition, (int)yPosition );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the target position of the current movement ( last point in the path ).
    * @return returns the target position, can be the current position if we are not moving...
    */
     public Point getTargetPosition() {
     	if(path!=null)
           return (Point)path.elementAt( path.size()-1 );
     	return new Point( (int)xPosition, (int)yPosition );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns true if we are moving
   */
    public boolean isMoving() {
       return walkingAlongPath;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To stop our movement along the path.
   */
    public void stopMovement() {
      walkingAlongPath = false;
      turningAlongPath = false;
      path = null;
      nextPoint = null;
      prevPoint = null;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Our Tick method. Call this method regularly to update the position along the path.
   */
    public void tick() {
       updatePathMovement();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Updates our movement along path.
   *  Method to call each tick to update the entity's position.
   *  This method does nothing if there is no current move.
   */
    private void updatePathMovement() {
       if(!turningAlongPath && !walkingAlongPath)
          return;

    // 1 - Time Update - Delta T 
       long now = System.currentTimeMillis();
       double deltaT = ( now-lastUpdateTime )/1000.0f;
       lastUpdateTime = now;

    // 2 - Orientation update
       if (turningAlongPath) {
        // Orientation update
           orientationAngle += angularDirection*deltaT*angularSpeed;

        // End of turn ?
           double deltaA = (nextAngle-orientationAngle)*angularDirection;

           if( deltaA<=0 ) {
                turningAlongPath = false;
                orientationAngle = angle( getPosition(), nextPoint );
           }
           else if(deltaA>Math.PI/8)
                return; // no footsteps, the angle is to great, we just turn...
       }

    // 3 - Position Update
       xPosition = (float)(xPosition + speed*deltaT*Math.cos( orientationAngle ) );
       yPosition = (float)(yPosition + speed*deltaT*Math.sin( orientationAngle ) );

    // 4 - Have we reached the next point in the path ?
       float deltaD = distance( getPosition(), prevPoint ) - distance( nextPoint, prevPoint );

        if( deltaD >= 0 ) {
            pathIndex++;

         // Path end point reached ?
            if( pathIndex >= path.size() ) {
                xPosition = (float) nextPoint.x;
                yPosition = (float) nextPoint.y;
                orientationAngle = nextAngle;
                stopMovement();
                return;
            }

         // Next Point reached...
            prevPoint = getPosition();
            nextPoint = (Point) path.elementAt( pathIndex );
            updateAngularNode();
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Initialize a path movement from the start. This is the most method you should
    *  use the most. This method modifies the entire state of the PathFollower.
    *  To test if the movement is finished, call the isMoving method after each tick.
    *
    * @param path a valid path returned by the Astar algorithm.
    * @param noRotations if true we take orientation variations into consideration,
    *        if false we only follow the path without turning on ourself.
    */
    public void initMovement( List path, boolean noRotations ) {

      // 1 - Control
         if( path==null || path.size()<1 ) {
             Debug.signal(Debug.ERROR, this, "Invalid Path !!!! "+path);
             return;
         }

      // 2 - Path Inits
         this.path = path;
         pathIndex = 1;
         lastUpdateTime = System.currentTimeMillis();

         prevPoint =  getPosition();
         nextPoint = (Point) path.elementAt(pathIndex);

         this.noRotations = noRotations;
         updateAngularNode();

      // 3 - We validate the movement...
         walkingAlongPath = true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Initialize a path movement from the start. This is the most method you should
    *  use the most. This method modifies the entire state of the PathFollower.
    *  To test if the movement is finished, call the isMoving method after each tick.
    *
    *  By default we take into consideration orientation variations.
    *
    * @param path a valid path returned by the Astar algorithm.
    * @param noRotations if true we take angle variations into consideration, if
    *        false we only follow the path without turning on ourself.
    */
    public void initMovement( List path ) {
         initMovement( path, false );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Given a path followed by a player (1) and an elapsed time since the movement begun (2)
   *  we initialize our path movement at the computed "current" position.
   *
   *  The "current" position is totaly dependent from the speed at which we move. So be
   *  sure to correctly initialize the PathFollower's speed BEFORE calling this method.
   *
   * @param path current path followed by a player.
   * @param deltaTime deltaTime in ms since the player begun to follow the path (starting
   *        at the first point given in the path parameter)
   * @param noRotations if true we take orientation variations into consideration,
   *        if false we only follow the path without turning on ourself.
   */
    public void initMovement( List path, int deltaTime, boolean noRotations ) {

     // 1 - Control
   	if(path==null || path.size()<1 ) {
           Debug.signal(Debug.ERROR, this, "Invalid Path !!!! "+path);
           return;
         }

        if(path.size()==1)
           return; // no movement

     // 2 - Position evaluation
   	float totalDistance = (deltaTime/1000.0f)*speed;
   	double d = 0.0f;
   	Point a0 = null;
   	Point a1 = (Point)path.elementAt(0);
   	
        for( int i=0; i<path.size(); i++ ) {
            a0 = a1;
            a1 = (Point)path.elementAt(i+1);            
            d += distance( a0, a1 );

         // have we found the last point crossed by our entity ?
            if( d >= totalDistance ) {
              // Path approved !
                 this.path = path;
                 pathIndex = i;

                 orientationAngle = angle( a0, a1 );                
                 xPosition = (float)(a1.x - (d-totalDistance)*Math.cos(orientationAngle) );
                 yPosition = (float)(a1.y - (d-totalDistance)*Math.sin(orientationAngle) );

                 prevPoint =  a0;
                 nextPoint = (Point) path.elementAt(pathIndex);
                 lastUpdateTime = System.currentTimeMillis();

                 this.noRotations = noRotations;
                 updateAngularNode();

              // We validate the movement
                 walkingAlongPath = true;
                 return;
            }
        }

     // 3 - If we arrive here it means that the movement is over !
     //     We set the PathFollower state to the end of the path
        a0 = (Point)path.elementAt(path.size()-2);
        a1 = (Point)path.elementAt(path.size()-1);

        orientationAngle = angle( a0, a1 );
        xPosition = (float)a1.x;
        yPosition = (float)a1.y;
        stopMovement();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Given a path followed by a player (1) and an elapsed time since the movement begun (2)
   *  we initialize our path movement at the computed "current" position.
   *
   *  The "current" position is totaly dependent from the speed at which we move. So be
   *  sure to correctly initialize the PathFollower's speed BEFORE calling this method.
   *
   *  By default we take into consideration orientation variations.
   *
   * @param path current path followed by a player.
   * @param deltaTime deltaTime in ms since the player begun to follow the path (starting
   *        at the first point given in the path parameter)
   */
    public void initMovement( List path, int deltaTime ) {
          initMovement( path, deltaTime, false );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To update the angular movement at a Path node.
   */
    private void updateAngularNode() {
        nextAngle = angle( prevPoint, nextPoint );

        angularDirection = 1;

        while( nextAngle-orientationAngle > Math.PI )
           nextAngle = (float)(nextAngle-2*Math.PI);

        while( nextAngle-orientationAngle < -Math.PI )
           nextAngle = (float)(nextAngle+2*Math.PI);

        if( orientationAngle > nextAngle )
            angularDirection = -1;

        if (noRotations)
          turningAlongPath = true;   // we will turn progressively, using the angularSpeed
        else {
          turningAlongPath = false;
          orientationAngle = nextAngle; // We update the angle right now
        }
    }

 /*------------------------------------------------------------------------------------*/

   /** Helper. Returns the distance between two points
    * @param a first point
    * @param b second point
    * @return distance between the two points.
    */
    public static float distance( Point a, Point b ) {
         return (float) Math.sqrt( (b.y-a.y)*(b.y-a.y)+(b.x-a.x)*(b.x-a.x) );
    }

 /*------------------------------------------------------------------------------------*/

   /** Helper. Returns the angle between the given line and the horizontal.
    * @param first point of the line
    * @param second point of the line
    * @return angle in radian in the [-pi,pi] range.
    */
    public static float angle( Point a, Point b ) {
         if(b.x==a.x) {
            if(b.y>a.y) return (float) Math.PI/2;
            else if (b.y<a.y) return (float) -Math.PI/2;

            return 0.0f;
         }

         float angle = (float) Math.atan( (double)(b.y-a.y)/(b.x-a.x) );

         if(b.x<a.x) {
            if(angle>=0) return (float) ( angle-Math.PI );
            if(angle<0)  return (float) ( angle+Math.PI );
         }

         return angle;
    }

 /*------------------------------------------------------------------------------------*/
}

