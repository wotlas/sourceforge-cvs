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

import wotlas.client.DataManager;
import wotlas.common.*;
import wotlas.common.message.movement.*;
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

public class PathFollower implements MovementComposer {

 /*------------------------------------------------------------------------------------*/

  /** To tell that we must reconstruct a trajectory that was not possible
   *  to construct before, due to a AStar not initialized...
   */
    transient private boolean reconstructTrajectory = false;

  /** Saved DeltaTime for trajectory reconstruction.
   */
    transient private int movementDeltaTime;

 /*------------------------------------------------------------------------------------*/

  // PATH FIELDS

  /** Player's trajectory
   */
    transient private List path;

  /** Our current index in the Path ( next point we target )
   */
    transient private int pathIndex;

  /** Previous point in path.
   */
    transient private Point prevPoint;

  /** Next point in path.
   */
    transient private Point nextPoint;

  /** End point of the path (for persistence only).
   */
    private ScreenPoint endPoint;

  /** Next Angle in the path.
   */
    transient private float nextAngle;

  /** what's our current movement  ? walking ? turning ? do we have to provide
   *  realistic rotations ?
   */
    private boolean walkingAlongPath;
    transient private boolean turningAlongPath;
    transient private boolean realisticRotations;

  /** Last update time.
   */
    transient private long lastUpdateTime;

 /*------------------------------------------------------------------------------------*/

  // KINEMATIC FIELDS

  /** Current position
   */
    private float xPosition;
    private float yPosition;

  /** Current Orientation (our angle in rads)
   */
    private double orientationAngle;

  /** PathFollower speed in pixel/s ( default 60 pixel/s )
   */
    transient private float speed = 60;

  /** PathFollower angular speed in radian/s ( default 3 rad/s )
   */
    transient private float angularSpeed = 3;

  /** in which angular direction are we turning ? +1 for positive direction, -1 otherwise.
   */
    transient private byte angularDirection;

  /** Time when we initialized the last movement.
   */
    private long movementTimeStamp;

 /*------------------------------------------------------------------------------------*/
 
  /** Asociated Player.
   */
    transient private Player player;

 /*------------------------------------------------------------------------------------*/

   /** To init the MovementComposer classes with the ground's mask.
    * @param mask two dimension mask representing the zones where the players can go.
    * @param maskTileSize mask tile size (in pixels).
    * @param playerSize represents the average player size ( in maskTileSize unit )
    */
     public void setMovementMask( boolean mask[][], int maskTileSize, int playerSize ) {
         AStarDouble.setMask( mask );
         AStarDouble.setTileSize( maskTileSize );
         AStarDouble.setSpriteSize( playerSize );
     }

 /*------------------------------------------------------------------------------------*/

   /** To get a path between two points via Astar.
    * @param a first point
    * @param b second point
    * @return path
    */
     public static List findPath(  Point a, Point b ) {
           int tileSize = AStarDouble.getTileSize();
        
           List path = AStarDouble.findPath( new Point( a.x/tileSize, a.y/tileSize ),
                                       new Point( b.x/tileSize, b.y/tileSize ) );
           path = AStarDouble.smoothPath(path);
                     
           if( path==null || path.size()<2 )
               return null; // no movement

           if(path!=null)
              for (int i=0; i<path.size(); i++) {
                   Point p = (Point) path.elementAt(i);
                   p.x *= tileSize;
                   p.y *= tileSize;
              }

           return path;
     }

 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor.
   */
    public PathFollower() {
        walkingAlongPath = false;
        turningAlongPath = false;
        realisticRotations = false;
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

   /** To init this MovementComposer.
    * @param player associated player.
    */
     public void init( Player player ) {
        this.player = player;
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

  /** To set if the player is walking along the path
   */
    public void setWalkingAlongPath(boolean walkingAlongPath) {
      this.walkingAlongPath = walkingAlongPath;
    }

  /** is the player moving ( same as isMoving(), this method is for persistence only )
   */
    public boolean getWalkingAlongPath() {
      return walkingAlongPath;
    }

  /** To set the player end position ( for persistence only )
   */
    public void setEndPoint(ScreenPoint endPoint) {
      this.endPoint= endPoint;
    }

  /** To get the end position of the current movement ( for persistence only, prefer
   *  getTargetPosition() )
   */
    public ScreenPoint getEndPoint() {
      return endPoint;
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
        if(path!=null && path.size()>0)
           return (Point)path.elementAt( path.size()-1 );

        if(endPoint!=null)
           return endPoint.toPoint();

        return new Point( -100, -100 ); // out of screen point
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns true if we are moving
   */
    public boolean isMoving() {
       return walkingAlongPath;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To reset our movement along the path.
   */
    public void resetMovement() {
      walkingAlongPath = false;
      turningAlongPath = false;
      path = null;
      nextPoint = null;
      prevPoint = null;
      endPoint = null;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To stop our movement along the path.
   */
    public void stopMovement() {
      resetMovement();
      
      if(player.isMaster())
         player.sendMessage( new PathUpdateMovementMessage( this, player.getPrimaryKey() ) );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set if we want realistic rotations or not.
   * @param realisticRotations true if you want realistic rotations.
   */
     public void setRealisticRotations( boolean realisticRotations ) {
        this.realisticRotations = realisticRotations;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the movement's timeStamp.
   */
     public long getMovementTimeStamp() {
         return movementTimeStamp;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the movement's timeStamp.
   */
     public void setMovementTimeStamp( long movementTimeStamp ) {
         this.movementTimeStamp = movementTimeStamp;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an update message representing the current movement state.
   *  IMPORTANT : We don't set any primaryKey.
   *
   * @return a MovementUpdateMessage 
   */
     public MovementUpdateMessage getUpdate() {
         if(AStarDouble.isInitialized())
            return (MovementUpdateMessage) new PathUpdateMovementMessage( this, null );
         else
            return (MovementUpdateMessage) new PathUpdateMovementMessage( this, player.getPrimaryKey() );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To update the current movement.
   * @param updateMessage MovementUpdateMessage
   */
     public synchronized void setUpdate( MovementUpdateMessage updateMessage ) {
           if( !(updateMessage instanceof PathUpdateMovementMessage) ) {
               Debug.signal( Debug.ERROR, this, "Received bad update message :"+updateMessage.getClass());
               return;
           }

           PathUpdateMovementMessage msg = (PathUpdateMovementMessage) updateMessage;

/* METHOD 1 : SIMPLE UPDATE : WE FORCE THE NEW POSITION

           xPosition = (float)msg.srcPoint.x;
           yPosition = (float)msg.srcPoint.y;
           orientationAngle = msg.orientationAngle;

           walkingAlongPath = msg.isMoving;

           if( walkingAlongPath ) {
               Point pDst = new Point( msg.dstPoint.x, msg.dstPoint.y );

               if (AStarDouble.isInitialized()) {
                  // Astar initialized, re-creating path
                     recreateTrajectory( pDst, msg.movementDeltaTime );
               }
               else {
                  // Astar not initialized, just saving data
                     endPoint = new ScreenPoint( pDst );
                     movementTimeStamp = System.currentTimeMillis();
                     reconstructTrajectory = true;
                     movementDeltaTime = msg.movementDeltaTime;
               }
           }
           else {
             // No movement
               resetMovement();
           }
*/

/* METHOD 2 : ADVANCED UPDATE : WE TEST THE NEW POSITION */

           if (!AStarDouble.isInitialized()) {
             // We just save the data
                xPosition = (float)msg.srcPoint.x;
                yPosition = (float)msg.srcPoint.y;
                orientationAngle = msg.orientationAngle;

                walkingAlongPath = msg.isMoving;

                if( walkingAlongPath ) {
                    endPoint = new ScreenPoint( msg.dstPoint.x, msg.dstPoint.y );
                    movementTimeStamp = System.currentTimeMillis();
                    reconstructTrajectory = true;
                    movementDeltaTime = msg.movementDeltaTime;
                }
                else
                    resetMovement(); // No movement
           }
           else
           {
             // AStar initialized
             // Do we have to consider this update ?
                boolean takeUpdate = false;

                if( walkingAlongPath ) {
                    Point target = getTargetPosition();

                    if( msg.isMoving ) {
                        if( distance( msg.srcPoint, getPosition() )>200 ||
                            findPath( getPosition(), msg.dstPoint )==null )
                            takeUpdate = true;
                        else
                            recreateTrajectory( msg.dstPoint, 0 );
                    }
                    else {
                        if( target.x!=msg.srcPoint.x || target.y!=msg.srcPoint.y
                    	    || distance( target, getPosition() )>200 )
                    	    takeUpdate = true;
                    }
                }
                else
                    takeUpdate = true;

                if(!takeUpdate)
                   return;

             // Our update...
                xPosition = (float)msg.srcPoint.x;
                yPosition = (float)msg.srcPoint.y;
                orientationAngle = msg.orientationAngle;

                walkingAlongPath = msg.isMoving;

                if( walkingAlongPath )
                    recreateTrajectory( msg.dstPoint, msg.movementDeltaTime );
                else
                    resetMovement(); // No movement
           }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To update speed & rotations
   */
      private void updateMovementAspect() {
         realisticRotations = false; // default
         speed = 1.0f;             // default : very slow speed

         if( player==null || player.getLocation()==null )
             return;

         if ( player.getLocation().isRoom() )
              realisticRotations = true;

         speed = player.getWotCharacter().getSpeed( player.getLocation() );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Our Tick method. Call this method regularly to update the position along the path.
   */
    public void tick() {
       if (AStarDouble.isInitialized()) {
          if(reconstructTrajectory) {
              if(endPoint!=null) 
                 recreateTrajectory( endPoint.toPoint(), movementDeltaTime );
              reconstructTrajectory = false;
          }
          else
              updatePathMovement();
       }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Updates our movement along path.
   *  Method to call each tick to update the entity's position.
   *  This method does nothing if there is no current move.
   */
    private synchronized void updatePathMovement() {
       if(!turningAlongPath && !walkingAlongPath)
          return;

    // 1 - Time Update - Delta T 
       long now = System.currentTimeMillis();
       double deltaT = ( now-lastUpdateTime )/1000.0f;
       lastUpdateTime = now;

       if(deltaT>=0.8f) return; // SECURITY if a slow is encountered
       if(deltaT<0) return; // Date has been advanced

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

  // MOVEMENT CONTROL

  /** To set a player's movement : movement from current position to the given point.
   */
     public void moveTo( Point endPosition ) {                       
            if (DataManager.SHOW_DEBUG)
              System.out.println("PathFollower::moveTo");
            // Test if xPosition,yPosition is a valid point
            Point startPt = new Point( (int)xPosition, (int)yPosition );            
            if ( !AStarDouble.isValidStart(startPt) ) {
              if (DataManager.SHOW_DEBUG)
                System.out.println("PathFollower : invalid start point");
              return;
              // Faire un reset de la position
            } else {
              if (DataManager.SHOW_DEBUG)
                System.out.println("PathFollower : valid start point");
            }
            
            path = findPath( startPt, new Point( endPosition.x, endPosition.y ) );

            if( path==null ) {
            	if( walkingAlongPath )
                    stopMovement(); // a message is sent : we were moving...
                else
                    resetMovement(); // no message sent : we were already still...

                return; // no movement
            }

            updateMovementAspect();
            initMovement( path );

            if(player.isMaster())
               player.sendMessage( new PathUpdateMovementMessage( this, player.getPrimaryKey() ) );
     }


 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To recreate a trajectory from a dest. point & a DeltaTime.
   */
     public void recreateTrajectory( Point pDst, int movementDeltaTime ) {
            path = findPath( new Point( (int)xPosition, (int)yPosition ),
                             new Point( pDst.x, pDst.y ) );
                     
            if( path==null ) {
                Debug.signal( Debug.ERROR, this, "Failed to re-create path !" );
                
                if(player.isMaster())
                   stopMovement();
                else
                   resetMovement();
                return;
            }

            updateMovementAspect();
            initMovement( path, movementDeltaTime );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Initialize a path movement from the start. This is the most method you should
    *  use the most. This method modifies the entire state of the PathFollower.
    *  To test if the movement is finished, call the isMoving method after each tick.
    *
    * @param path a valid path returned by the Astar algorithm.
    */
    synchronized public void initMovement( List path ) {

      // 1 - Control
         if( path==null || path.size()<1 ) {
             Debug.signal(Debug.ERROR, this, "Invalid Path !!!! "+path);
             return;
         }

      // 2 - Path Inits
         this.path = path;
         pathIndex = 1;
         lastUpdateTime = System.currentTimeMillis();
         movementTimeStamp = lastUpdateTime;

         prevPoint =  getPosition();
         nextPoint = (Point) path.elementAt(pathIndex);

         updateAngularNode();

      // 3 - We validate the movement...
         walkingAlongPath = true;
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
   */
    synchronized public void initMovement( List path, int deltaTime ) {

     // 1 - Control
        if(path==null || path.size()<1 ) {
           resetMovement();
           Debug.signal(Debug.ERROR, this, "Invalid Path !!!! "+path);
           return;
         }

        if(path.size()==1) {
           resetMovement();
           return; // no movement
        }

        this.path = path;

     // 2 - Position evaluation
        float totalDistance = (deltaTime/1000.0f)*speed;
        double d = 0.0f;
        Point a0 = null;
        Point a1 = (Point)path.elementAt(0);
        
        for( int i=0; i<path.size()-1; i++ ) {
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
                 nextPoint = a1; //(Point) path.elementAt(pathIndex);
                 lastUpdateTime = System.currentTimeMillis();
                 movementTimeStamp = lastUpdateTime;

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

        if(player.isMaster())
           stopMovement();     
        else
           resetMovement();
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

        if (realisticRotations)
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

