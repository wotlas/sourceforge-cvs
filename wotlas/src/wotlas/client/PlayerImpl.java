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


package wotlas.client;

import wotlas.common.character.*;
import wotlas.common.universe.*;
import wotlas.common.*;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;

import wotlas.libs.net.NetMessage;

import wotlas.libs.pathfinding.*;

import wotlas.utils.*;

import java.awt.Point;
import java.awt.Rectangle;


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

  /** Player's PathFollower for movements...
   */
  private MovementComposer movementComposer = (MovementComposer) new PathFollower();

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

  /** True if this player is controlled by the client.
   */
  private boolean isMaster = false;

 /*------------------------------------------------------------------------------------*/

///////////// ALDISS pour se faciliter la vie... la pièce courante

   /** Our current Room ( if we are in a Room, null otherwise )
    */
       transient private Room myRoom;

/////////////////

 /*------------------------------------------------------------------------------------*/

  /** Locks
   */
  private byte trajectoryLock[] = new byte[0];
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
    Debug.signal( Debug.NOTICE, null, "PlayerImpl::init");
    animation = new Animation(wotCharacter.getImage(location));
    sprite = (Sprite) wotCharacter.getDrawable(this);    
    
  }

  /** Called after graphicsDirector's init to add some visual effects to the master player
   * or to show other players
   */
  public void initVisualProperties(GraphicsDirector gDirector) {
    if (isMaster) {
      gDirector.addDrawable(wotCharacter.getShadow());
    } else {
      gDirector.addDrawable(wotCharacter.getDrawable(this));
      gDirector.addDrawable(wotCharacter.getShadow());
    }
  }

  /** To remove player from the screen
   */
  public void cleanVisualProperties(GraphicsDirector gDirector) {
    if (!isMaster) {
      gDirector.removeDrawable(wotCharacter.getDrawable(this));
      gDirector.removeDrawable(wotCharacter.getShadow());
    }    
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

     ///////////// ALDISS mise à jour du champ myRoom
      if( location.isRoom() )
        myRoom = DataManager.getDefaultDataManager().getWorldManager().getRoom( location );
      else
        myRoom = null;
     /////////////// FIN ALDISS
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

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

/*** SpriteDataSupplier implementation ***/

  /** To get the X image position.
   *
   * @return x image coordinate
   */
  public int getX() {
    synchronized( xLock ) {
      return (int) movementComposer.getXPosition();
    }
  }

  /** To get the Y image position.
   *
   * @return y image cordinate
   */
  public int getY() {
    synchronized( yLock ) {
      return (int) movementComposer.getYPosition();
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
      return movementComposer.getOrientationAngle();
    }
  }

  /** To get the X factor for scaling... 1.0 means no X scaling
   *
   * @return X scale factor
   */
  public double getScaleX() {
    return 1.0;
  }

  /** To get the Y factor for scaling... 1.0 means no Y scaling
   *
   * @return Y scale factor
   */
  public double getScaleY() {
    return 1.0;
  }

  /** To get the image's transparency ( 0.0 means invisible, 1.0 means fully visible ).
   *
   * @return alpha
   */
  public float getAlpha() {
    return 1.0f;
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set X.
   * @param x cordinate
   */
  public void setX( int x ){
    synchronized( xLock ) {
         movementComposer.setXPosition( (float)x );
    }
  }

  /** To set Y.
   * @param y cordinate
   */
  public void setY( int y ){
    synchronized( yLock ) {
         movementComposer.setYPosition( (float)y );
    }
  }

  /** To set the angle.
   */
  public void setAngle( double angleRad ) {
    synchronized( angleLock ) {
         movementComposer.setOrientationAngle( angleRad );
    }
  }

  /** To set the position.
   */
  public void setPosition(ScreenPoint p) {
         setX( p.x );
         setY( p.y );
  }

  /** To set player's speed
   */
  public void setSpeed( float speed ) {
     movementComposer.setSpeed( (float)speed );
  }

  /** To get player's speed
   */
  public float getSpeed() {
    return movementComposer.getSpeed();
  }

  /** To set player's angular speed
   */
  public void setAngularSpeed(float angularSpeed) {
    movementComposer.setAngularSpeed( angularSpeed );
  }

  /** To get player's angular speed
   */
  public float getAngularSpeed() {
    return movementComposer.getAngularSpeed();
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get destination of trajectory
   */
   public Point getEndPosition() {
     return movementComposer.getTargetPosition();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the player's movement.
   */
   public void moveTo( Point endPoint ) {
      synchronized( trajectoryLock ) {
         movementComposer.moveTo( endPoint );
      }
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns true if the player is moving
   */
  public boolean isMoving() {
    return movementComposer.isMoving();
  }

  /** To stop the player's movement
   */
  public void stopMovement() {
    movementComposer.stopMovement();
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the player's drawable
   *  @return player sprite
   */
  public Drawable getDrawable() {
    return wotCharacter.getDrawable(this);
  }

  /** To get player's rectangle (to test intersection)
   */
  public Rectangle getCurrentRectangle() {
    return wotCharacter.getDrawable(this).getRectangle();
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tick
   */
  public void tick() {

   // 1 - Movement Update
      synchronized( trajectoryLock ) {
           movementComposer.tick();
      }

   // 2 - Animation Update
      if(!movementComposer.isMoving())
         animation.reset();
      else
         animation.tick();
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Is this player a Master player ? ( directly controlled  by the client )
    * @return true if this is a Master player, false otherwise.
    */
      public boolean isMaster() {
      	return isMaster;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set if this player is controlled by the client.
    * @param isMaster true means controlled by the client.
    */
      public void setIsMaster( boolean isMaster ) {
         this.isMaster = isMaster;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's movement Composer.
    *
    *  @return player MovementComposer
    */
      public MovementComposer getMovementComposer() {
      	  return movementComposer;
      }
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's movement Composer.
    *
    *  @param movement MovementComposer.
    */
      public void setMovementComposer( MovementComposer movementComposer ) {
      	  this.movementComposer = movementComposer;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Use this method to send a NetMessage to the server.
   *
   * @param message message to send to the player.   
   */
     public void sendMessage( NetMessage message ) {
        DataManager.getDefaultDataManager().sendMessage( message );             
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 ///////////////// ALDISS pour récupérer la pièce courante

   /** To get the player's current Room ( if we are in a Room ).
    */
      public Room getMyRoom() {
      	return myRoom;
      }

 //////////////////////// FIN ALDISS

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}