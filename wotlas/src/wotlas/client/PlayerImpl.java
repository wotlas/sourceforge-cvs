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

// TODO
// - getWotCharacter

package wotlas.client;

import wotlas.common.character.WotCharacter;
import wotlas.common.character.*;
import wotlas.common.ImageLibRef;
import wotlas.common.Player;
import wotlas.common.Tickable;
import wotlas.common.universe.*;

import wotlas.libs.graphics2D.Animation;
import wotlas.libs.graphics2D.ImageLibrary;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.Sprite;
import wotlas.libs.graphics2D.drawable.*;

import wotlas.utils.Debug;
import wotlas.utils.List;

import java.awt.Point;

/** Class of a Wotlas Player.
 *
 * @author Petrus, Aldiss
 * @see wotlas.common.Player
 */

public class PlayerImpl implements Player, SpriteDataSupplier, Tickable
{

 /*------------------------------------------------------------------------------------*/

  /** Player's primary key (the key)
   */
  private String primaryKey;

  /** Player location
   */
  private WotlasLocation location;

  /** Player name
   */
  private String playerName;

  /** Player full name
   */
  private String fullPlayerName;

  /** Wotlas Character
   */
  private WotCharacter wotCharacter;

 /*------------------------------------------------------------------------------------*/

  /** Player's trajectory
   */
  private List trajectory;

  /** Current position in trajectory
   */
  private int indexTrajectory = 0;

  /** Current position
   */
  private Point position;
  
  /** End of the trajectory
   */
  private Point endPosition;

 /*------------------------------------------------------------------------------------*/

  /** Our animation.
   */
  private Animation animation;
      
  /** Our sprite.
   */
  private Sprite sprite;

  /** True if player is moving
   */
  private boolean isMoving = false;

 /*------------------------------------------------------------------------------------*/

  /** X coordinate
   */
  private int x;

  /** Y coordinate
   */
  private int y;

  /** our angle (in rads)
   */
  private double angleRad;

 /*------------------------------------------------------------------------------------*/

  /** Locks
   */
  private byte trajectoryLock[] = new byte[0];
  private byte endLock[] = new byte[0];
  private byte xLock[] = new byte[0];
  private byte yLock[] = new byte[0];
  private byte angleLock[] = new byte[0];
  private byte imageLock[] = new byte[0];

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
  public PlayerImpl() {
  }

 /*------------------------------------------------------------------------------------*/

 /** When this method is called, the player can initialize its own fields safely : all
  *  the game data has been loaded.
  */
  public void init() {    
    System.out.println("PlayerImpl::init");
    System.out.println("\twotCharacter = " + wotCharacter );
    animation = new Animation(wotCharacter.getImage(location));
    sprite = (Sprite) wotCharacter.getDrawable(this);        
    endPosition = new Point();
    trajectory = new List();
    position = new Point(x, y);
  }

  /** Called after graphicsDirector's init
   * to add some visual effects to the player
   */
  public void initVisualProperties(GraphicsDirector gDirector) {
    gDirector.addDrawable(wotCharacter.getShadow());
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the player location.
   *
   *  @return player WotlasLocation
   */
  public WotlasLocation getLocation() {
    return location;
  }

  /** To set the player location.
   *
   *  @param new player WotlasLocation
   */
  public void setLocation(WotlasLocation myLocation) {
    location = myLocation;
  }

 /*------------------------------------------------------------------------------------*/

 /** To get the player name ( short name )
  *
  *  @return player name
  */
  public String getPlayerName() {
    return playerName;
  }

  /** To set the player's name ( short name )
   *
   *  @param player name
   */
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the player's full name.
   *
   *  @return player full name ( should contain the player name )
   */
  public String getFullPlayerName() {
    return fullPlayerName;
  }

  /** To set the player's full name.
   *
   *  @param player full name ( should contain the player name )
   */
  public void setFullPlayerName(String fullPlayerName) {
    this.fullPlayerName = fullPlayerName;
  }

 /*------------------------------------------------------------------------------------*/

/*** Player implementation ***/

  /** To get the player primary Key ( account name )
   *
   *  @return player primary key
   */
  public String getPrimaryKey() {
    return primaryKey;
  }

  /** To set the player's primary key.
   *
   *  @param primary key
   */
  public void setPrimaryKey(String primaryKey) {
    this.primaryKey = primaryKey;
  }

  /** To get the player's character.
   *
   *  @return player character
   */
  public WotCharacter getWotCharacter() {
    return wotCharacter;
  }
  
  /** To set the player's character.
   *
   *  @return WotCharacter player character
   */
  public void setWotCharacter(WotCharacter wotCharacter) {
    this.wotCharacter = wotCharacter;
  }

 /*------------------------------------------------------------------------------------*/

/*** SpriteDataSupplier implementation ***/

  /** To get the X image position.
   *
   * @return x image coordinate
   */
  public int getX() {
    synchronized( xLock ) {
      return x;
    }
  }

  /** To get the Y image position.
   *
   * @return y image cordinate
   */
  public int getY() {
    synchronized( yLock ) {
      return y;
    }
  }

  /** To get the image identifier to use.
   *
   * @return image identifier.
   */
  public ImageIdentifier getImageIdentifier() {
    synchronized( imageLock ) {
      return animation.getCurrentImage();
    }
  }

  /** To get the eventual rotation angle. 0 means no rotation.
   *
   * @return angle in radians.
   */
  public double getAngle() {
    synchronized( angleLock ) {
      return angleRad;
    }
  };

  /** To get the X factor for scaling... 1.0 means no X scaling
   *
   * @return X scale factor
   */
  public double getScaleX() {
    return 1.0;
  };

  /** To get the Y factor for scaling... 1.0 means no Y scaling
   *
   * @return Y scale factor
   */
  public double getScaleY() {
    return 1.0;
  };

  /** To get the image's transparency ( 0.0 means invisible, 1.0 means fully visible ).
   *
   * @return alpha
   */
  public float getAlpha() {
    return 1.0f;
  };

 /*------------------------------------------------------------------------------------*/

  /** To set X.
   * @param x cordinate
   */
  public void setX( int x ){
    synchronized( xLock ) {
      this.x = x;
      
    }
  }

  /** To set Y.
   * @param y cordinate
   */
  public void setY( int y ){
    synchronized( yLock ) {
      this.y = y;
      
    }
  }
  
  public void setAngle( double angleRad ) {
    synchronized( angleLock ) {
      this.angleRad = angleRad;
    }
  }

  public void setPosition(wotlas.utils.ScreenPoint p) {
    if (position==null) {
      position = new Point(p.x, p.y);
    } else {
      position.x = p.x;
      position.y = p.y;
    }
  }
  
 /*------------------------------------------------------------------------------------*/

  /** To set endPosition of trajectory.
   */
  public void setEndPosition(int x, int y) {
    synchronized( endLock ) {
      this.endPosition.x = x;
      this.endPosition.y = y;      
    }
  }

  /** To set endPosition of trajectory.
   */
  public void setEndPosition(Point endPoint) {
    synchronized( endLock ) {
      this.endPosition = endPoint;
    }
  }

  /** To set the trajectory
   */
  public void setTrajectory(List list) {
    if (list != null) {
      synchronized( trajectoryLock ) {
        this.trajectory = list;
        indexTrajectory = 0;
      }
    }
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the player's drawable
   *  @return player sprite
   */
  public Drawable getDrawable() {
    return wotCharacter.getDrawable(this);
  }

 /*------------------------------------------------------------------------------------*/

  /** Tick
   */
  public void tick() {    
    if (turningAlongPath || walkingAlongPath) {
      updatePathMovement();
      x = position.x;
      y = position.y; 
      animation.tick();     
      sprite.tick();      
      return;
    }
    
    /*if (indexTrajectory < trajectory.size()) {    
      Point newPosition = (Point) trajectory.elementAt(indexTrajectory);
      x = newPosition.x*DataManager.TILE_SIZE;
      y = newPosition.y*DataManager.TILE_SIZE;              
      animation.tick();     
      sprite.tick();      
      indexTrajectory++;
    }*/
  }

 /*------------------------------------------------------------------------------------*/
 
//******************************************************************
//******* AStar with angle and velocity ****************************
//******************************************************************

  /** Player speed : 40 pixel/s
   */
  private int speed = 40;

  /** Player default angular speed : 3 rad/s
   */
  public float angularSpeed = 3;

  //---------------------------------------------------------------------------//

   /** Previous point in path.
    */
    private Point prevPoint;

   /** Next point in path & next angle.
    */
    private Point nextPoint;
    private float nextAngle;

   /** Our current index in the Path ( next point we target )
    */
    private int pathIndex;

   /** The computed Astar path
    */
    private List path;

   /** Our current movement walking ? turning ?
    */
    private boolean walkingAlongPath, turningAlongPath;

   /** Last update time.
    */
    private long lastUpdateTime;

   /** Angular Direction : +1 for positive direction, -1 otherwise.
    */
    private byte angularDirection;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Update Movement along path.
   *  Method to call each tick to update the entity's position.
   *  This method does nothing if there is no current move.
   */
    void updatePathMovement() {

       if(!turningAlongPath && !walkingAlongPath)
          return;

       long now = System.currentTimeMillis();
       float deltaT = ( now-lastUpdateTime )/1000.0f;
       lastUpdateTime = now;

       if(turningAlongPath) {
        // Orientation update
           setAngle( getAngle() + angularDirection*deltaT*angularSpeed );

        // End of turn ?
           float deltaA = (float)( (nextAngle-getAngle())*angularDirection);

           if( deltaA<=0 ) {
                turningAlongPath = false;
                setAngle( nextAngle );
           }
           else if(deltaA>Math.PI/4)
                return; // no footsteps, the angle is to great, we just turn...
       }

    // 1 - Position Update
       position.x += (int)( speed*deltaT*Math.cos( getAngle() ) );
       position.y += (int)( speed*deltaT*Math.sin( getAngle() ) );

    // 2 - Have we reached the next Path Point ?
       float deltaD = distance( new Point( (int)position.x, (int)position.y ), prevPoint ) - distance( nextPoint, prevPoint );

       if( deltaD >= 0 ) {
           pathIndex++;
        
         // 2.1 - Path Over ?
            if( pathIndex >= path.size() ) {
                position.x = nextPoint.x;
                position.y = nextPoint.y;
                setAngle( nextAngle );

                walkingAlongPath=false;
                turningAlongPath = false;
                path=null;
                nextPoint=null;
                prevPoint=null;
                return;
            }
         
         // 2.2 - Next Point + position correction
            prevPoint = nextPoint;
            nextPoint = (Point) path.elementAt( pathIndex );
            setAngle( nextAngle );
            updateAngularNode();

            position.x = (int)( prevPoint.x + deltaD*Math.cos( getAngle() ) );
            position.y = (int)( prevPoint.y + deltaD*Math.sin( getAngle() ) );
       }

    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Initialize a path movement.
    * @param path a valid returned by the Astar algorithm
    */
    public void initMovement( List path ) {
         if( path==null || path.size()<2 ) {// invalid path
             System.out.println( "Invalid Path !!!! "+path);
             return;
         }

         this.path = path;
         pathIndex = 1;
         lastUpdateTime = System.currentTimeMillis();

         //prevPoint =  new Point( (int)position.x, (int)position.y );
         
         prevPoint = (Point) path.elementAt(0);
         nextPoint = (Point) path.elementAt(1);

         walkingAlongPath =true;
         updateAngularNode();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the distance between two points
    * @return distance between the two points.
    */
    private float distance( Point a, Point b ) {
         return (float) Math.sqrt( (b.y-a.y)*(b.y-a.y)+(b.x-a.x)*(b.x-a.x) );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the angle between the given line and the horizontal.
    *
    * @param first point of the line
    * @param second point of the line
    * @return angle in radian in the [-pi,pi] range
    */
    private float angle( Point a, Point b ) {
         if(b.x==a.x) {
            if(b.y>a.y) return (float) Math.PI/2;
            else if (b.y<a.y) return (float) -Math.PI/2;

            return 0.0f;
         }

         float angle = (float) Math.atan( (double)(b.y-a.y)/(b.x-a.x) );

         if(b.x<a.x) {
            if(angle>=0) return (float)( angle-Math.PI );
            if(angle<0)  return (float) ( angle+Math.PI );
         }

         return angle;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To update the angular movement at a Path node.
    */
    private void updateAngularNode() {
        nextAngle = angle( prevPoint, nextPoint );
        //System.out.print("updateAngularNode::angle = ");
        //System.out.println(getAngle()*180/Math.PI);
        //System.out.print("updateAngularNode::nextAngle = ");
        //System.out.println(nextAngle*180/Math.PI);
        angularDirection = 1;
        turningAlongPath = true;

        while( nextAngle-getAngle() > Math.PI )
           nextAngle = (float)(nextAngle-2*Math.PI);

        while( nextAngle-getAngle() < -Math.PI )
           nextAngle = (float)(nextAngle+2*Math.PI);

        if( getAngle() > nextAngle )
            angularDirection = -1;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/




}