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
 * @author Petrus
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
    // Only one character for now !
    wotCharacter = new AesSedai();

    animation = new Animation(wotCharacter.getImage());
    sprite = (Sprite) wotCharacter.getDrawable(this);
    endPosition = new Point();
    trajectory = new List();

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
    if (indexTrajectory < trajectory.size()) {
      Point newPosition = (Point) trajectory.elementAt(indexTrajectory);
      x = newPosition.x*10;
      y = newPosition.y*10;
      animation.tick();
      sprite.tick();
      indexTrajectory++;
    }
  }

 /*------------------------------------------------------------------------------------*/

}