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

package wotlas.client;

import wotlas.common.character.*;
import wotlas.common.chat.*;
import wotlas.common.universe.*;
import wotlas.common.movement.*;
import wotlas.common.*;

import wotlas.common.message.description.PlayerPastMessage;
import wotlas.common.message.description.PlayerAwayMessage;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.filter.*;
import wotlas.libs.sound.*;

import wotlas.libs.net.NetMessage;

import wotlas.libs.pathfinding.*;

import wotlas.utils.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import java.util.Hashtable;

/** Class of a Wotlas Player.
 *
 * @author Petrus, Aldiss
 * @see wotlas.common.Player
 */

public class PlayerImpl implements Player, SpriteDataSupplier, Tickable {

  /** Period between the display of two away messages for ONE player.
   */
  public final static long AWAY_MSG_DISPLAY_PERIOD = 1000*20;

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

  /** Player character's past
   */
  private String playerPast;

  /** Player away message.
   */
  private String playerAwayMessage;

  /** Wotlas Character
   */
  private WotCharacter wotCharacter;
  
  /** Player state
   */
  transient private PlayerState playerState = new PlayerState();
  
  /** Current Chat PrimaryKey : the chat we are currently connected to.
   */
  private String currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT; // everlasting chat set as default

  /** Time stamp for the away message : we don't display the away messages when they
   *  are asked during the AWAY_MSG_DISPLAY_PERIOD.
   */
  transient private long playerAwayMsgTimeStamp;

 /*------------------------------------------------------------------------------------*/

  /** Player's PathFollower for movements...
   */
  private MovementComposer movementComposer = (MovementComposer) new PathFollower();

 /*------------------------------------------------------------------------------------*/

  /** Our animation.
   */
  private Animation animation;

  /** Our brightness filter.
   */
  private BrightnessFilter brightnessFilter;
  
  /** Our sprite.
   */
  private Sprite sprite;

  /** Our textDrawable
   */
  private TextDrawable textDrawable;

  /** Our wave arc drawable to show our player talking.
   */
  private WaveArcDrawable waveDrawable;

  /** To get the player name to display on the left of the screen
   */
  private MultiLineText gameScreenFullPlayerName;

 /** Our current Room ( if we are in a Room, null otherwise )
  */
  private Room myRoom;

  /** True if player is moving.
   */
  private boolean isMoving = false;

  /** True if this player is controlled by the client.
   */
  private boolean isMaster = false;

  /** Is this player representing a client that is connected to the game ?
   */
  private boolean isConnectedToGame = false; 

  /** SyncID for client & server. See the getter of this field for explanation.
   * This field is an array and not a byte because we want to be able to
   * synchronize the code that uses it.
   */
  private byte syncID[] = new byte[1];

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
    //Debug.signal( Debug.NOTICE, null, "PlayerImpl::init");
    animation = new Animation( wotCharacter.getImage(location),
                               ClientDirector.getDataManager().getImageLibrary() );
    sprite = (Sprite) wotCharacter.getDrawable(this);
    brightnessFilter = new BrightnessFilter();
    sprite.setDynamicImageFilter(brightnessFilter);
    movementComposer.init( this );
  }

  /** Called after graphicsDirector's init to add some visual effects to the master player
   * or to show other players
   */
  public void initVisualProperties(GraphicsDirector gDirector) {

    if(isMaster)
      gDirector.addDrawable(wotCharacter.getShadow()); // Drawable has already been added
    else {
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

    if ( location.isRoom() )
      myRoom = ClientDirector.getDataManager().getWorldManager().getRoom( location );
    else
      myRoom = null;
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
   *  @param the key of player who requested player's full name
   *  @return player full name ( should contain the player name )
   */
  public String getFullPlayerName(Player otherPlayer) {
    return fullPlayerName;
  }

  public String getFullPlayerName() {
    return fullPlayerName;
  }
  
  /** To set the player's full name.
   *
   *  @param player full name ( should contain the player name )
   */
  public void setFullPlayerName(String fullPlayerName) {
    this.fullPlayerName = fullPlayerName;

       if( textDrawable!=null )
           textDrawable.setText(fullPlayerName);

       if( gameScreenFullPlayerName!=null ) {
           String[] strTemp = { fullPlayerName };
           gameScreenFullPlayerName.setText(strTemp);
       }
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

   /** To get the player character past.
    *
    *  @return player past
    */
      public String getPlayerPast() {
      	 if( playerPast==null ) {
      	    // we ask for the player's past
      	     sendMessage( new PlayerPastMessage( primaryKey, "") );
      	     playerPast="loading...";
      	     return playerPast;
      	 }
      	
         return playerPast;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's past.
    *
    *  @param playerPast past
    */
      public void setPlayerPast( String playerPast ) {
           this.playerPast = playerPast;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player away message.
    *
    *  @return player away Message
    */
      public String getPlayerAwayMessage() {
      	     if(isConnectedToGame && this!=ClientDirector.getDataManager().getMyPlayer())
      	        return null;

             if(playerAwayMessage==null)
                sendMessage( new PlayerAwayMessage( primaryKey, "") );

             return playerAwayMessage;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Can we display the Away Message ? (method for the DataManager)
    */
      public boolean canDisplayAwayMessage() {
             long now = System.currentTimeMillis();

             if( playerAwayMsgTimeStamp+AWAY_MSG_DISPLAY_PERIOD < now ) {
                playerAwayMsgTimeStamp = now;
                return true;
             }

             return false;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's away message.
    *
    *  @param playerAwayMessage msg
    */
      public void setPlayerAwayMessage( String playerAwayMessage ){
      	this.playerAwayMessage = playerAwayMessage;
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
    if( isConnectedToGame )
        return 1.0f;
    else
        return 0.5f;
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
    public void moveTo( Point endPoint, WorldManager worldManager ) {
      synchronized( trajectoryLock ) {
         movementComposer.moveTo( endPoint, worldManager );
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

   // 2 - Dynamic Filter Update
      if (brightnessFilter!=null)
        if (myRoom!=null)
            brightnessFilter.setBrightness(movementComposer.getXPosition(), movementComposer.getYPosition());
   
   // 3 - Animation Update
      if(animation==null)
         return;

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
        ClientDirector.getDataManager().sendMessage( message );             
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the player's current Room ( if we are in a Room ).
    * @return current Room, null if we are not in a room.
   */
    public Room getMyRoom() {
      return myRoom;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a text drawable representing the name of this player.
   */
   public Drawable getTextDrawable() {
       if( textDrawable!=null ) {
           if( textDrawable.isLive() )
               return null;
           //textDrawable.resetTimeLimit();
           //return textDrawable;
       }

       if (isConnectedToGame) {
        textDrawable = new TextDrawable( fullPlayerName, getDrawable(), wotCharacter.getColor(),
                                        13.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, 5000 );
       } else {
        textDrawable = new TextDrawable( fullPlayerName + " (away)", getDrawable(), wotCharacter.getColor(),
                                        13.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, 5000 );
       }                                        
       return textDrawable;
   }
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a wave drawable on this player.
   */
   public Drawable getWaveArcDrawable() {
       if( waveDrawable!=null ) {
           if( waveDrawable.isLive() ) {
               waveDrawable.reset();
               return null;
           }

           waveDrawable.reset();
           return waveDrawable;
       }

       if(sprite==null) return null;

       waveDrawable = new WaveArcDrawable( sprite, 40, Color.white,
                                           ImageLibRef.WAVE_PRIORITY, 1.0f, (byte)3 );
       return waveDrawable;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the player name to display on the left of the screen
   */
     public MultiLineText getGameScreenFullPlayerName() {
          if(gameScreenFullPlayerName==null) {
             String[] strTemp = { fullPlayerName };
             gameScreenFullPlayerName = new MultiLineText(strTemp, 10, 10, Color.black, 15.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, MultiLineText.LEFT_ALIGNMENT);
          }

          return gameScreenFullPlayerName;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the primary key of the chat the player is now using.
    * @return currentChatPrimaryKey
    */
      public String getCurrentChatPrimaryKey() {
          return currentChatPrimaryKey;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the current chat used
    * @param currentChatPrimaryKey
    */
      public void setCurrentChatPrimaryKey( String currentChatPrimaryKey ) {
      	this.currentChatPrimaryKey = currentChatPrimaryKey;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Is this player connected to the game ? ( not synchronized )
    * @return true if the player is in the game, false if the client is not connected.
    */
      public boolean isConnectedToGame() {
            return isConnectedToGame;
      }

    /** To get the player's state (disconnected/connected/away)
     *
     * @return player state
     */        
      public PlayerState getPlayerState() {
        return playerState;
      }      
      
    /** To set the player's state (disconnected/connected/away)
     *
     * @param playerState player state
     */     
      public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
      }      

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set if this player is connected to the game.
    * @param true if the player is in the game, false if the client is not connected.
    */
      public void setIsConnectedToGame( boolean isConnectedToGame ) {
      	 this.isConnectedToGame = isConnectedToGame;
      	 playerState.value = PlayerState.CONNECTED;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the synchronization ID. This ID is used to synchronize this player on the
    *  client & server side. The ID is incremented only when the player changes its map.
    *  Movement messages that have a bad syncID are discarded.
    * @return sync ID
    */
      public byte getSyncID(){
      	synchronized( syncID ) {
          return syncID[0];
        }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the synchronization ID. See the getter for an explanation on this ID.
    *  @param syncID new syncID
    */
      public void setSyncID(byte syncID){
      	synchronized( this.syncID ) {
           this.syncID[0] = syncID;
        }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}