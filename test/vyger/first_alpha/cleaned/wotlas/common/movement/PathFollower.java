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

package wotlas.common.movement;

import java.awt.Point;
import wotlas.common.Player;
import wotlas.common.WorldManager;
import wotlas.common.message.movement.MovementUpdateMessage;
import wotlas.common.message.movement.PathUpdateMovementMessage;
import wotlas.common.screenobject.ScreenObject;
import wotlas.common.universe.TownMap;
import wotlas.common.universe.WorldMap;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.pathfinding.AStarDouble;
import wotlas.libs.persistence.BackupReady;
import wotlas.utils.Debug;
import wotlas.utils.List;
import wotlas.utils.ScreenPoint;

/** 
 * A path follower... well yes, this class is a path adept :) It follows a given path
 * at a certain speed taking into account angle variations...
 *
 * IMPORTANT : this implementation is NOT synchronized... please avoid the situation
 *             when one thread is invoking the tick() method and the other a setXXX()...
 *
 * @author Petrus, Aldiss, Diego
 */

public class PathFollower implements MovementComposer, BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /*------------------------------------------------------------------------------------*/

    /** Distance in pixels before we consider the slave replica too far from its
     *  master replica : we then make slave replica jump to its final position.
     */
    public final static int MAX_DISTANCE_DELAY = 200;

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

    /** Do we have to set a special orientation of the player at the end of his movement.
     *  This field is useful when recreating a trajectory : we don't want the final
     *  orientation of the player to differ from the original replica.
     */
    transient private boolean useEndingOrientationValue;

    /** The ending orientation for the slave replica of the player.
     */
    transient private double endingOrientation;

    /** Asociated Player.
     */
    transient private Player player;

    /*------------------------------------------------------------------------------------*/

    /** To init the MovementComposer classes with the ground's mask.
     * @param mask two dimension mask representing the zones where the players can go.
     * @param maskTileSize mask tile size (in pixels).
     * @param playerSize represents the average player size ( in maskTileSize unit )
     */
    public void setMovementMask(boolean mask[][], int maskTileSize, int playerSize) {
        AStarDouble.setMask(mask);
        AStarDouble.setTileSize(maskTileSize);
        AStarDouble.setSpriteSize(playerSize);
    }

    /*------------------------------------------------------------------------------------*/

    /** To get a path between two points via Astar.
     * @param a first point
     * @param b second point
     * @return path
     */
    public static List findPath(Point a, Point b, boolean pathInRoom) {
        int tileSize = AStarDouble.getTileSize();

        List path = AStarDouble.findPath(new Point(a.x / tileSize, a.y / tileSize), new Point(b.x / tileSize, b.y / tileSize));
        path = AStarDouble.smoothPath(path);

        if (path == null || path.size() < 2)
            return null; // no movement

        if (path != null)
            for (int i = 0; i < path.size(); i++) {
                Point p = (Point) path.elementAt(i);
                if (pathInRoom && i != path.size() - 1) {
                    p.x = p.x * tileSize - 5;
                    p.y = p.y * tileSize - 5;
                } else {
                    p.x *= tileSize;
                    p.y *= tileSize;
                }
            }

        return path;
    }

    /*------------------------------------------------------------------------------------*/

    /** Empty Constructor.
     */
    public PathFollower() {
        this.walkingAlongPath = false;
        this.turningAlongPath = false;
        this.realisticRotations = false;
        this.useEndingOrientationValue = false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with initial position & orientation
     *
     *  @param xPosition x position in pixels.
     *  @param yPosition y position in pixels.
     *  @param orientationAngle orientation angle in radians.
     */
    public PathFollower(float xPosition, float yPosition, double orientationAngle) {
        this();
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.orientationAngle = orientationAngle;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this MovementComposer.
    * @param player associated player.
    */
    public void init(Player player) {
        this.player = player;
    }

    public void init(ScreenObject screenObject) {
        Debug.signal(Debug.ERROR, null, "This should never be called : init with ScreenObject on PathFollower!");
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    // KINEMATIC GETTERS & SETTERS
    /** To get the X position.
     * @return x position
     */
    public float getXPosition() {
        return this.xPosition;
    }

    /** To set the X Position.
     * @param x x cordinate
     */
    public void setXPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    /** To get the Y position.
     * @return y position
     */
    public float getYPosition() {
        return this.yPosition;
    }

    /** To set the Y Position.
     * @param y y cordinate
     */
    public void setYPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    /** To get the orientation angle.
     * @return angle in radians.
     */
    public double getOrientationAngle() {
        return this.orientationAngle;
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
        return this.speed;
    }

    /** To set player's angular speed
     */
    public void setAngularSpeed(float angularSpeed) {
        this.angularSpeed = angularSpeed;
    }

    /** To get player's angular speed
     */
    public float getAngularSpeed() {
        return this.angularSpeed;
    }

    /** To set if the player is walking along the path
     */
    public void setWalkingAlongPath(boolean walkingAlongPath) {
        this.walkingAlongPath = walkingAlongPath;
    }

    /** is the player moving ( same as isMoving(), this method is for persistence only )
     */
    public boolean getWalkingAlongPath() {
        return this.walkingAlongPath;
    }

    /** To set the player end position ( for persistence only )
     */
    public void setEndPoint(ScreenPoint endPoint) {
        this.endPoint = endPoint;
    }

    /** To get the end position of the current movement ( for persistence only, prefer
     *  getTargetPosition() )
     */
    public ScreenPoint getEndPoint() {
        return this.endPoint;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the position from a Point.
     * @param p Point
     */
    public void setPosition(Point p) {
        this.xPosition = p.x;
        this.yPosition = p.y;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the current position as a Point.
     * @return current position
     */
    public Point getPosition() {
        return new Point((int) this.xPosition, (int) this.yPosition);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the target position of the current movement ( last point in the path ).
     * @return returns the target position, can be the current position if we are not moving...
     */
    public Point getTargetPosition() {
        if (this.path != null && this.path.size() > 0)
            return (Point) this.path.elementAt(this.path.size() - 1);

        if (this.endPoint != null)
            return this.endPoint.toPoint();

        return new Point(-100, -100); // out of screen point
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns true if we are moving
     */
    public boolean isMoving() {
        return this.walkingAlongPath;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To reset our movement along the path.
     */
    public void resetMovement() {
        this.walkingAlongPath = false;
        this.turningAlongPath = false;
        this.path = null;
        this.nextPoint = null;
        this.prevPoint = null;
        this.endPoint = null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To stop our movement along the path.
     */
    public void stopMovement() {
        resetMovement();

        if (this.player.isMaster())
            this.player.sendMessage(new PathUpdateMovementMessage(this, this.player.getPrimaryKey(), this.player.getSyncID()));
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set if we want realistic rotations or not.
     * @param realisticRotations true if you want realistic rotations.
     */
    public void setRealisticRotations(boolean realisticRotations) {
        this.realisticRotations = realisticRotations;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the movement's timeStamp.
     */
    public long getMovementTimeStamp() {
        return this.movementTimeStamp;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the movement's timeStamp.
     */
    public void setMovementTimeStamp(long movementTimeStamp) {
        this.movementTimeStamp = movementTimeStamp;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get an update message representing the current movement state.
     *  IMPORTANT : We don't set any primaryKey.
     *
     * @return a MovementUpdateMessage 
     */
    public MovementUpdateMessage getUpdate() {
        if (AStarDouble.isInitialized())
            return new PathUpdateMovementMessage(this, null, this.player.getSyncID());
        else
            return new PathUpdateMovementMessage(this, this.player.getPrimaryKey(), this.player.getSyncID());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To update the current movement.
     * @param updateMessage MovementUpdateMessage
     */
    public synchronized void setUpdate(MovementUpdateMessage updateMessage) {
        if (!(updateMessage instanceof PathUpdateMovementMessage)) {
            Debug.signal(Debug.ERROR, this, "Received bad update message :" + updateMessage.getClass());
            return;
        }

        PathUpdateMovementMessage msg = (PathUpdateMovementMessage) updateMessage;
        this.useEndingOrientationValue = false;

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
            this.xPosition = msg.srcPoint.x;
            this.yPosition = msg.srcPoint.y;
            this.orientationAngle = msg.orientationAngle;

            this.walkingAlongPath = msg.isMoving;

            if (this.walkingAlongPath) {
                this.endPoint = new ScreenPoint(msg.dstPoint.x, msg.dstPoint.y);
                this.movementTimeStamp = System.currentTimeMillis();
                this.reconstructTrajectory = true;
                this.movementDeltaTime = msg.movementDeltaTime;
            } else
                resetMovement(); // No movement
        } else {
            // AStar initialized
            // Do we have to consider this update ?
            boolean takeUpdate = false;

            if (this.walkingAlongPath) {
                Point target = getTargetPosition();

                if (msg.isMoving) {
                    if (PathFollower.distance(msg.srcPoint, getPosition()) > PathFollower.MAX_DISTANCE_DELAY ||
                    // findPath( getPosition(), msg.dstPoint, player.getLocation().isRoom() )==null )
                    PathFollower.findPath(getPosition(), msg.dstPoint, this.player.getLocation().isRoom()) == null)
                        takeUpdate = true;
                    else
                        recreateTrajectory(msg.dstPoint, 0);
                } else {
                    // no movement, we received the master replica's ending trajectory position
                    if (target.x != msg.srcPoint.x || target.y != msg.srcPoint.y || PathFollower.distance(target, getPosition()) > PathFollower.MAX_DISTANCE_DELAY) {
                        takeUpdate = true;
                    } else {
                        // save the final orientation for later
                        this.useEndingOrientationValue = true;
                        this.endingOrientation = msg.orientationAngle;
                    }
                }
            } else if (!msg.isMoving && Math.abs(this.xPosition - msg.srcPoint.x) <= 1 && Math.abs(this.yPosition - msg.srcPoint.y) <= 1) {
                // we turn on ourself
                this.turningAlongPath = true;
                this.useEndingOrientationValue = true;
                this.nextAngle = msg.orientationAngle;
                this.angularDirection = 1;

                while (this.nextAngle - this.orientationAngle > Math.PI)
                    this.nextAngle = (float) (this.nextAngle - 2 * Math.PI);

                while (this.nextAngle - this.orientationAngle < -Math.PI)
                    this.nextAngle = (float) (this.nextAngle + 2 * Math.PI);

                if (this.orientationAngle > this.nextAngle)
                    this.angularDirection = -1;

                this.path = null;
            } else
                takeUpdate = true;

            if (!takeUpdate)
                return;

            // Our update...
            this.xPosition = msg.srcPoint.x;
            this.yPosition = msg.srcPoint.y;
            this.orientationAngle = msg.orientationAngle;

            this.walkingAlongPath = msg.isMoving;

            if (this.walkingAlongPath)
                recreateTrajectory(msg.dstPoint, msg.movementDeltaTime);
            else
                resetMovement(); // No movement
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To update speed & rotations
     */
    private void updateMovementAspect() {
        this.realisticRotations = false; // default
        this.speed = 1.0f; // default : very slow speed

        if (this.player == null || this.player.getLocation() == null)
            return;

        if (this.player.getLocation().isRoom())
            this.realisticRotations = true;

        this.speed = this.player.getBasicChar().getSpeed();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Our Tick method. Call this method regularly to update the position along the path.
     */
    public void tick() {
        if (AStarDouble.isInitialized()) {
            if (this.reconstructTrajectory) {
                if (this.endPoint != null)
                    recreateTrajectory(this.endPoint.toPoint(), this.movementDeltaTime);
                this.reconstructTrajectory = false;
            } else
                updatePathMovement();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Updates our movement along path.
     *  Method to call each tick to update the entity's position.
     *  This method does nothing if there is no current move.
     */
    private synchronized void updatePathMovement() {
        if (!this.turningAlongPath && !this.walkingAlongPath)
            return;

        // 1 - Time Update - Delta T 
        long now = System.currentTimeMillis();
        double deltaT = (now - this.lastUpdateTime) / 1000.0f;
        this.lastUpdateTime = now;

        if (deltaT >= 0.8f)
            return; // SECURITY if a slow is encountered
        if (deltaT < 0)
            return; // Date has been advanced

        // 2 - Orientation update
        if (this.turningAlongPath) {

            // Orientation update
            this.orientationAngle += this.angularDirection * deltaT * this.angularSpeed;

            // End of turn ?
            double deltaA = (this.nextAngle - this.orientationAngle) * this.angularDirection;

            if (deltaA <= 0) {
                if (this.useEndingOrientationValue && (this.path == null || this.pathIndex >= this.path.size())) {
                    resetMovement(); // recreated trajectory ending by a rotation...
                    return;
                }

                this.turningAlongPath = false;
                this.orientationAngle = PathFollower.angle(getPosition(), this.nextPoint);
            } else if (deltaA > Math.PI / 8)
                return; // no footsteps, the angle is to great, we just turn...
        }

        // 3 - Position Update
        if (this.path == null)
            return; // no path to follow we just have to turn on ourself

        this.xPosition = (float) (this.xPosition + this.speed * deltaT * Math.cos(this.orientationAngle));
        this.yPosition = (float) (this.yPosition + this.speed * deltaT * Math.sin(this.orientationAngle));

        // 4 - Have we reached the next point in the path ?
        float deltaD = PathFollower.distance(getPosition(), this.prevPoint) - PathFollower.distance(this.nextPoint, this.prevPoint);

        if (deltaD >= 0) {
            this.pathIndex++;

            // Path end point reached ?
            if (this.pathIndex >= this.path.size()) {
                this.xPosition = this.nextPoint.x;
                this.yPosition = this.nextPoint.y;
                this.orientationAngle = this.nextAngle;

                // if this is a recreated trajectory do we have to turn to get the right
                // final orientation ?
                if (this.useEndingOrientationValue && this.realisticRotations) {
                    this.nextAngle = (float) this.endingOrientation;
                    this.turningAlongPath = true;
                    return;
                }

                this.useEndingOrientationValue = false;
                stopMovement();
                return;
            }

            // Next Point reached...
            this.prevPoint = getPosition();
            this.nextPoint = (Point) this.path.elementAt(this.pathIndex);
            updateAngularNode();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    // MOVEMENT CONTROL
    /** To set a player's movement : movement from current position to the given point.
    */
    public void moveTo(Point endPosition, WorldManager wManager) {
        // Test if xPosition,yPosition is a valid point
        Point startPt = new Point((int) this.xPosition, (int) this.yPosition);
        if (!AStarDouble.isValidStart(startPt)) {
            Debug.signal(Debug.WARNING, this, "PathFollower : invalid start point");
            // We reset the position
            WotlasLocation location = this.player.getLocation();
            // We search for a valid insertion point
            ScreenPoint pReset = null;
            if (location.isRoom())
                pReset = this.player.getMyRoom().getInsertionPoint();
            else {
                if (location.isTown()) {
                    TownMap myTown = wManager.getTownMap(location);
                    if (myTown != null)
                        pReset = myTown.getInsertionPoint();
                } else if (location.isWorld()) {
                    WorldMap myWorld = wManager.getWorldMap(location);
                    if (myWorld != null)
                        pReset = myWorld.getInsertionPoint();
                }
            }

            if (pReset == null) {
                pReset = new ScreenPoint(0, 0);
                Debug.signal(Debug.CRITICAL, this, "Could not find a valid start point !");
            } else
                Debug.signal(Debug.NOTICE, this, "Found a new valid start point...");
            this.player.setX(pReset.x);
            this.player.setY(pReset.y);
            startPt.x = pReset.x;
            startPt.y = pReset.y;
        }
        // path = findPath( startPt, new Point( endPosition.x, endPosition.y ),player.getLocation().isRoom() );
        this.path = PathFollower.findPath(startPt, new Point(endPosition.x, endPosition.y), this.player.getLocation().isRoom());
        if (this.path == null) {
            if (this.walkingAlongPath)
                stopMovement(); // a message is sent : we were moving...
            else
                resetMovement(); // no message sent : we were already still...
            return; // no movement
        }
        updateMovementAspect();
        initMovement(this.path);
        if (this.player.isMaster())
            this.player.sendMessage(new PathUpdateMovementMessage(this, this.player.getPrimaryKey(), this.player.getSyncID()));
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To rotate the player on itself.
     *  @param finalOrientation final orientation to reach
     */
    public void rotateTo(double finalOrientation) {

        this.orientationAngle = finalOrientation;

        if (this.player.isMaster())
            this.player.sendMessage(new PathUpdateMovementMessage(this, this.player.getPrimaryKey(), this.player.getSyncID()));
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To recreate a trajectory from a dest. point & a DeltaTime.
     */
    public void recreateTrajectory(Point pDst, int movementDeltaTime) {
        this.path = PathFollower.findPath(new Point((int) this.xPosition, (int) this.yPosition), new Point(pDst.x, pDst.y), this.player.getLocation().isRoom());
        //                            new Point( pDst.x, pDst.y ), player.getLocation().isRoom() );

        if (this.path == null) {
            Debug.signal(Debug.ERROR, this, "Failed to re-create path !");

            if (this.player.isMaster())
                stopMovement();
            else
                resetMovement();
            return;
        }

        updateMovementAspect();

        if (movementDeltaTime > 500)
            initMovement(this.path, movementDeltaTime);
        else
            initMovement(this.path);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Initialize a path movement from the start. This is the most method you should
     *  use the most. This method modifies the entire state of the PathFollower.
     *  To test if the movement is finished, call the isMoving method after each tick.
     *
     * @param path a valid path returned by the Astar algorithm.
     */
    synchronized public void initMovement(List path) {

        // 1 - Control
        if (path == null || path.size() < 1) {
            Debug.signal(Debug.ERROR, this, "Invalid Path !!!! " + path);
            return;
        }

        // 2 - Path Inits
        this.path = path;
        this.pathIndex = 1;
        this.lastUpdateTime = System.currentTimeMillis();
        this.movementTimeStamp = this.lastUpdateTime;

        this.prevPoint = getPosition();
        this.nextPoint = (Point) path.elementAt(this.pathIndex);

        updateAngularNode();

        // 3 - We validate the movement...
        this.walkingAlongPath = true;
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
    synchronized public void initMovement(List path, int deltaTime) {

        // 1 - Control
        if (path == null || path.size() < 1) {
            resetMovement();
            Debug.signal(Debug.ERROR, this, "Invalid Path !!!! " + path);
            return;
        }

        if (path.size() == 1) {
            resetMovement();
            return; // no movement
        }

        this.path = path;

        // 2 - Position evaluation
        float totalDistance = (deltaTime / 1000.0f) * this.speed;
        double d = 0.0f;
        Point a0 = null;
        Point a1 = (Point) path.elementAt(0);

        for (int i = 0; i < path.size() - 1; i++) {
            a0 = a1;
            a1 = (Point) path.elementAt(i + 1);
            d += PathFollower.distance(a0, a1);

            // have we found the last point crossed by our entity ?
            if (d >= totalDistance) {
                // Path approved !
                this.path = path;
                this.pathIndex = i;

                this.orientationAngle = PathFollower.angle(a0, a1);
                this.xPosition = (float) (a1.x - (d - totalDistance) * Math.cos(this.orientationAngle));
                this.yPosition = (float) (a1.y - (d - totalDistance) * Math.sin(this.orientationAngle));

                this.prevPoint = a0;
                this.nextPoint = a1; //(Point) path.elementAt(pathIndex);
                this.lastUpdateTime = System.currentTimeMillis();
                this.movementTimeStamp = this.lastUpdateTime;

                updateAngularNode();

                // We validate the movement
                this.walkingAlongPath = true;
                return;
            }
        }

        // 3 - If we arrive here it means that the movement is over !
        //     We set the PathFollower state to the end of the path
        a0 = (Point) path.elementAt(path.size() - 2);
        a1 = (Point) path.elementAt(path.size() - 1);

        this.orientationAngle = PathFollower.angle(a0, a1);
        this.xPosition = a1.x;
        this.yPosition = a1.y;

        if (this.player.isMaster())
            stopMovement();
        else
            resetMovement();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To update the angular movement at a Path node.
    */
    private void updateAngularNode() {
        this.nextAngle = PathFollower.angle(this.prevPoint, this.nextPoint);
        this.angularDirection = 1;
        while (this.nextAngle - this.orientationAngle > Math.PI)
            this.nextAngle = (float) (this.nextAngle - 2 * Math.PI);
        while (this.nextAngle - this.orientationAngle < -Math.PI)
            this.nextAngle = (float) (this.nextAngle + 2 * Math.PI);
        if (this.orientationAngle > this.nextAngle)
            this.angularDirection = -1;
        if (this.realisticRotations)
            this.turningAlongPath = true; // we will turn progressively, using the angularSpeed
        else {
            this.turningAlongPath = false;
            this.orientationAngle = this.nextAngle; // We update the angle right now
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** Helper. Returns the distance between two points
     * @param a first point
     * @param b second point
     * @return distance between the two points.
     */
    public static float distance(Point a, Point b) {
        if (a == null || b == null)
            return 0f;

        return (float) Math.sqrt((b.y - a.y) * (b.y - a.y) + (b.x - a.x) * (b.x - a.x));
    }

    /*------------------------------------------------------------------------------------*/

    /** Helper. Returns the angle between the given line and the horizontal.
     * @param first point of the line
     * @param second point of the line
     * @return angle in radian in the [-pi,pi] range.
     */
    public static float angle(Point a, Point b) {
        if (b.x == a.x) {
            if (b.y > a.y)
                return (float) Math.PI / 2;
            else if (b.y < a.y)
                return (float) -Math.PI / 2;

            return 0.0f;
        }

        float angle = (float) Math.atan((double) (b.y - a.y) / (b.x - a.x));

        if (b.x < a.x) {
            if (angle >= 0)
                return (float) (angle - Math.PI);
            if (angle < 0)
                return (float) (angle + Math.PI);
        }

        return angle;
    }

    /*------------------------------------------------------------------------------------*/
    /** write object data with serialize.
     */
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeFloat(this.xPosition);
        objectOutput.writeFloat(this.yPosition);
        objectOutput.writeLong(this.movementTimeStamp);
        objectOutput.writeBoolean(this.walkingAlongPath);
        objectOutput.writeDouble(this.orientationAngle);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.xPosition = objectInput.readFloat();
            this.yPosition = objectInput.readFloat();
            this.movementTimeStamp = objectInput.readLong();
            this.walkingAlongPath = objectInput.readBoolean();
            this.orientationAngle = objectInput.readDouble();
        } else {
            // to do.... when new version
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** id version of data, used in serialized persistance.
     */
    public int ExternalizeGetVersion() {
        return 1;
    }
}