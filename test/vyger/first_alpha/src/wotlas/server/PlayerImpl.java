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
package wotlas.server;

import wotlas.common.screenobject.*;

import wotlas.common.character.*;
import wotlas.common.chat.*;
import wotlas.common.movement.*;
import wotlas.common.PlayerState;
import wotlas.common.router.*;
import wotlas.common.message.chat.SendTextMessage;
import wotlas.server.message.chat.*;


import wotlas.libs.net.NetConnectionListener;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetMessage;

import wotlas.libs.pathfinding.*;
import wotlas.libs.persistence.*;

import wotlas.common.*;
import wotlas.common.chat.*;
import wotlas.common.message.movement.*;
import wotlas.common.message.description.*;
import wotlas.common.universe.*;
import wotlas.common.objects.*;
import wotlas.utils.*;

import wotlas.libs.persistence.*;

import java.util.*;
import java.io.*;

/** Class of a Wotlas Player. It is the class that, in certain way, a client gets connected to.
 *  All the client messages have a server PlayerImpl context.
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.common.Player
 * @see wotlas.common.NetConnectionListener
 */

public class PlayerImpl implements Player, NetConnectionListener,BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /** Period between two focus sounds. Focus sounds can be send by players two draw
    *  attention.
    */
    protected static final int FOCUS_SOUND_PERIOD = 1000*10; // 10s between two sounds

 /*------------------------------------------------------------------------------------*/

    /** Player name ( in fact it's nickname... )
    */
    protected String playerName;

    /** Player character's past
    */
    protected String playerPast;

    /** Player away message.
    */
    protected String playerAwayMessage;

    /** WotCharacter Class
    */
    protected BasicChar wotCharacter;
      
    /** Player state
    */
    protected PlayerState playerState = new PlayerState();

    /** Movement Composer
    */
//    protected MovementComposer movementComposer = (MovementComposer) new PathFollower();
    protected MovementComposer movementComposer = (MovementComposer) new ServerPathFollower();

   /** Lie Manager
    */
    protected LieManager lieManager = new LieManager();
    
    /** Object manager
     */
    protected ServerObjectManager objectManager;    
// FIXME ???
//    transient protected ServerObjectManager objectManager; 

    /** Last time player disconnected (in days)
    */
    protected long lastDisconnectedTime;

 /*------------------------------------------------------------------------------------*/


    /** Our NetConnection, useful if we want to send messages !
    */
    transient protected NetConnection connection;

    /** Our current Room ( if we are in a Room, null otherwise )
    */
    transient protected Room myRoom;

    /** Our current TileMap ( if we are in a TileMap, null otherwise )
    */
    transient protected TileMap myTileMap;

    /** SyncID for client & server. See the getter of this field for explanation.
    * This field is an array and not a byte because we want to be able to
    * synchronize the code that uses it.
    */
    transient protected byte syncID[] = new byte[1];

 /*------------------------------------------------------------------------------------*/

    /** Player ChatRooms : is the list of the current chat room.
    */
    transient protected ChatList chatList;

    /** Current Chat PrimaryKey : the chat we are currently looking.
    */
    transient protected String currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT; // everlasting chat set as default

    /** are we a member of this chat ? or just eavesdropping ?
    */
    transient protected boolean isChatMember = true; //always member on default chat.

    /** Last time this player was grant the possibility to send a focus sound to players.
    *
    *  The period between two focus sound is set by the static FOCUS_SOUND_PERIOD.
    */
    transient protected long focusSoundTimeStamp;

 /*------------------------------------------------------------------------------------*/

    /** Connection Lock
    */
    transient protected byte connectionLock[] = new byte[0];

    /** ChatList Lock
    */
    transient protected byte chatListLock[] = new byte[0];
    


 /*------------------------------------------------------------------------------------*/

    /** Constructor for persistence.
    */
    public PlayerImpl() {
        playerAwayMessage=new String("I'm not here for the moment...");
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** When this method is called, the player can intialize its own fields safely : all
    *  the game data has been loaded.
    */
    public void init() {
        // done to reinit vars. : set myTileMap or myRoom
        setLocation( getLocation() );
        movementComposer.init( this );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** If you call this method all the local data will be replaced by the given
    *  player's one.
    */
    public void clone( PlayerImpl playerToClone ) {
        setBasicChar( playerToClone.getBasicChar() );
        setLocation( playerToClone.getLocation() );
        setPlayerName( playerToClone.getPlayerName() );
        setPrimaryKey( playerToClone.getPrimaryKey() );

        setPlayerPast( playerToClone.getPlayerPast() );
        setPlayerAwayMessage( playerToClone.getPlayerAwayMessage() );

        setMovementComposer( playerToClone.getMovementComposer() );
        movementComposer.init( this );

        setLieManager( playerToClone.getLieManager() );
         
        setChatList( playerToClone.getChatList() );
        setCurrentChatPrimaryKey( playerToClone.getCurrentChatPrimaryKey() );
        setIsChatMember( playerToClone.isChatMember() );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To initialize the player location to the first existent world found.
    *  WARNING : the player is NOT moved to the world... thus this method
    *  is for player creation ONLY.
    */
    public void setDefaultPlayerLocation() {
        // 1 - player initial location : a World...
        WorldManager worldManager = ServerDirector.getDataManager().getWorldManager();
        int worldID = worldManager.getAValidWorldID();
        if( worldID<0 )
            Debug.signal( Debug.CRITICAL, this, "No world data given to initialize player." );
        else if(worldID!=0)
            Debug.signal( Debug.WARNING, this, "The default world isn't the first in the list... hope you are aware of that..." );
        setLocation( new WotlasLocation(worldID) );
        // we retrieve the default position.
        ServerConfig cfg = ServerDirector.getServerManager().getServerConfig();
        setX( cfg.getWorldFirstXPosition() );
        setY( cfg.getWorldFirstYPosition() );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Is this player a Master player ? ( directly controlled  by the client )
    * @return true if this is a Master player, false otherwise.
    */
    public boolean isMaster() {
        return false; // Server PlayerImpl is only a slave player implementation
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's X position.
    *
    *  @return x
    */
    public int getX() {
        return (int)movementComposer.getXPosition();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's Y position.
    *
    *  @return y
    */
    public int getY() {
        return (int)movementComposer.getYPosition();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's X position.
    *
    *  @param x
    */
    public void setX( int x ) {
        movementComposer.setXPosition( (float)x );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's Y position.
    */
    public void setY( int y ) {
        movementComposer.setYPosition( (float)y );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's orientation.
    */
    public void setOrientation( float orientation ) {
        movementComposer.setOrientationAngle( orientation );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's orientation.
    */
    public float getOrientation() {
        return (float) movementComposer.getOrientationAngle();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player location.
    *
    *  @return player WotlasLocation
    */
    public WotlasLocation getLocation(){                      
        return new WotlasLocation( getBasicChar().getLocation() );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player location.
    *
    *  @param new player WotlasLocation
    */
      public void setLocation( WotlasLocation myLocation ){
             getBasicChar().setLocation(myLocation);
             if( myLocation.isRoom() && ServerDirector.getDataManager()!=null )
                 myRoom = ServerDirector.getDataManager().getWorldManager().getRoom( myLocation );
             else {
             	 if( myLocation.isRoom() )
              	     Debug.signal( Debug.CRITICAL, this, "Room not found !!! location is:"+myLocation );
                 myRoom = null;
             }
             if( myLocation.isTileMap() && ServerDirector.getDataManager()!=null )
                 myTileMap = ServerDirector.getDataManager().getWorldManager().getTileMap( myLocation );
             else {
             	 if( myLocation.isTileMap() )
              	     Debug.signal( Debug.CRITICAL, this, "TileMap not found !!! location is:"+myLocation );
                 myTileMap = null;
             }
          // Current Chat set to default
             currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT;
             isChatMember = false;
             synchronized( chatListLock ) {
             	chatList = null;
             }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player name ( short name )
    *
    *  @return player name
    */
    public String getPlayerName() {
        return playerName;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
    /** To get the player's full name
    */
    public String getFullPlayerName(String otherPlayerKey) {
        return lieManager.getCurrentFakeName();
// FIXME        return "fullname-should be implremented";
    }
      
    /** To get the player's full name
    */
    public String getFullPlayerName() {
        return lieManager.getCurrentFakeName();
//FIXME        return "fullname-should be implremented";
    }
    
    /** To get the player's full name or fake name
    *
    * @return player full name
    */
    public String getFullPlayerName(PlayerImpl otherPlayer) {
        if (otherPlayer==null)
          return lieManager.getCurrentFakeName();
         if (otherPlayer.getPrimaryKey().equals(getPrimaryKey())) {
           return lieManager.getCurrentFakeName();
         } else {
           //return lieManager.getFakeName(otherPlayer);
           return otherPlayer.getLieManager().getFakeName(this);
         }
    }
       
    public String getFullPlayerName(Player otherPlayer) {
        return getFullPlayerName((PlayerImpl) otherPlayer);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*** Player implementation ***/

    /** To get the player primary Key ( account name )
    *
    *  @return player primary key
    */
    public String getPrimaryKey() {
        return getBasicChar().getPrimaryKey();
    }

    /** To set the player's primary key.
    *
    *  @param primary key
    */
    public void setPrimaryKey(String primaryKey) {
        getBasicChar().setPrimaryKey(primaryKey);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player character past.
    *
    *  @return player past
    */
    public String getPlayerPast() {
        return playerPast;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the player's name ( short name )
    *
    *  @param player name
    */
      public void setPlayerName( String playerName ) {
         this.playerName = playerName;           
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

    /** To set the player's full name.
    *
    *  @param player full name ( should contain the player name )
    */
    public void setFullPlayerName( String fullPlayerName ) {
          lieManager.setFullPlayerName(fullPlayerName);          
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's character.
    *
    *  @return player character
    */
    public BasicChar getBasicChar() {
        return wotCharacter; 
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's character.
    *
    *  @param wotCharacter new player character
    */
    public void setBasicChar( BasicChar wotCharacter ) {
        this.wotCharacter = wotCharacter;
        this.wotCharacter.init();
    }
	  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the player's object manager
    *
    *  @return player object manager
    */
      public ObjectManager getObjectManager()
	  {
	   return objectManager;
	  }

   /** To set the player's object manager.
    *
    *  @param objectManager player object manager
    */
      public void setObjectManager( ObjectManager objectManager )
	  {
	   this.objectManager=(ServerObjectManager)objectManager;
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

   /** To get the Lie Manager.
    */
      public LieManager getLieManager() {
        return lieManager;
      }
   
   /** To set the Lie Manager.
    */
      public void setLieManager(LieManager lieManager) {
        this.lieManager = lieManager;
      }
   
    /** To get lastDisconnectedTime.
    */
    public long getLastDisconnectedTime() {
        return lastDisconnectedTime;
    }
      
    /** To set lastDisconnectedTime.
    */
    public void setLastDisconnectedTime(long lastTime) {
        this.lastDisconnectedTime = lastTime;
    }
      
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's current Room ( if we are in a Room ).
    * @return current Room, null if we are not in a room.
    */
    public Room getMyRoom() {
        return myRoom;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player's current TileMap ( if we are in a TileMap ).
    * @return current TileMap, null if we are not in a tileMap.
    */
    public TileMap getMyTileMap() {
        return myTileMap;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get our Message router.
    */
      public MessageRouter getMessageRouter() {
          if(myRoom!=null)
             return myRoom.getMessageRouter();
          if(myTileMap!=null)
             return myTileMap.getMessageRouter();

          if( getLocation().isWorld() ) {
              WorldMap world = ServerDirector.getDataManager().getWorldManager().getWorldMap(getLocation());
              if(world!=null)
                 return world.getMessageRouter();
          }
          else if( getLocation().isTown() ) {
              TownMap town = ServerDirector.getDataManager().getWorldManager().getTownMap(getLocation());
              if(town!=null)
                 return town.getMessageRouter();
          }
          else if( getLocation().isRoom() ) {
              Room room = ServerDirector.getDataManager().getWorldManager().getRoom(getLocation());
              if(room!=null)
                 return room.getMessageRouter();
          }
          else if( getLocation().isTileMap() ) {
              TileMap tileMap = ServerDirector.getDataManager().getWorldManager().getTileMap(getLocation());
              if(tileMap!=null)
                 return tileMap.getMessageRouter();
          }

         Debug.signal(Debug.ERROR, this, "MessageRouter not found ! bad location :"+getLocation()+" player: "+getPrimaryKey());
         return null; // not found
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the player away message.
    *
    *  @return player away Message
    */
    public String getPlayerAwayMessage() {
        return playerAwayMessage;
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

    /** To update the synchronization ID. See the getter for an explanation on this ID.
    *  The new updated syncID is (syncID+1)%100.
    */
    public void updateSyncID(){
      	synchronized( syncID ) {
            syncID[0] = (byte) ( (syncID[0]+1)%100 );
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

    /** To set the player's chatList.
    */
    public void setChatList( ChatList chatList ) {
        synchronized( chatListLock ) {
            this.chatList = chatList;
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the player's chatList.
    */
    public ChatList getChatList() {
      	synchronized( chatListLock ) {
            return chatList;
        }
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
      
    /** are we a member of this chat ? or just eavesdropping ?
    * @return true if we are a real member of the current chat
    */
    public boolean isChatMember() {
        return isChatMember;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** are we a member of this chat ? or just eavesdropping ?
    * @param isChatMember if we are a real member of the current chat set it to true.
    */
    public void setIsChatMember( boolean isChatMember ) {
        this.isChatMember = isChatMember;
    }

 /*------------------------------------------------------------------------------------*/

    /** Is this player connected to the game ? ( not synchronized )
    * @return true if the player is in the game, false if the client is not connected.
    */
    public boolean isConnectedToGame() {
        if(connection==null)
            return false;
        return true;
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

    /** To set if this player is connected to the game. (not used on this server side )
    * @param isConnected true if the player is in the game, false if the client is not connected.
    */
    public void setIsConnectedToGame( boolean isConnected ) {
        // no external update possible on the server side !
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when a new network connection is created on this player.
   *
   * @param connection the NetConnection object associated to this connection.
   */
     public void connectionCreated( NetConnection connection ) {

             synchronized( connectionLock ) {
                 this.connection = connection;
             }

          // We forget a little about players we met if we last connected 5 days ago
             if (lastDisconnectedTime-System.currentTimeMillis()>(5*86400000))
                lieManager.removeMeet(LieManager.FORGET_RECONNECT_LONG);
             else
                lieManager.removeMeet(LieManager.FORGET_RECONNECT);

          // We update our state
             playerState.value = PlayerState.CONNECTED;
             
          // We signal our connection to players in the game
          // ... and players in the rooms near us
             if(getLocation().isRoom()) {
                 if(myRoom==null) {
                    Debug.signal( Debug.ERROR, this, "Player "+getPrimaryKey()+" has an incoherent location state");
                    return;
                 }

               // are we present in this room already ?
                 if( myRoom.getMessageRouter().getPlayer(getPrimaryKey())!=null ) {
                   // We send an update to players near us...                      
                      PlayerConnectedToGameMessage pMsg = new PlayerConnectedToGameMessage(getPrimaryKey(),true);
                      myRoom.getMessageRouter().sendMessage( pMsg, this, MessageRouter.EXTENDED_GROUP );
                 }
             }

             // We signal our connection to players in the game
             if(getLocation().isTileMap()) {
                 if(myTileMap==null) {
                    Debug.signal( Debug.ERROR, this, "Player "+getPrimaryKey()+" has an incoherent location state");
                    return;
                 }

               // are we present in this TileMap already ?
                 if( myTileMap.getMessageRouter().getPlayer(getPrimaryKey())!=null ) {
                   // We send an update to players near us...                      
                      PlayerConnectedToGameMessage pMsg = new PlayerConnectedToGameMessage(getPrimaryKey(),true);
                      myTileMap.getMessageRouter().sendMessage( pMsg, this, MessageRouter.EXTENDED_GROUP );
                 }
             }

             Debug.signal(Debug.NOTICE,null,"Connection opened for player "+playerName+" at "+Tools.getLexicalTime());
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when the network connection of the client is no longer
   * of this world.
   *
   * @param connection the NetConnection object associated to this connection.
   */
     public void connectionClosed( NetConnection connection ) {

         // 0 - no more messages will be sent...
             synchronized( connectionLock ) {
                 this.connection = null;
             }
             lastDisconnectedTime = System.currentTimeMillis();
             
         // 0.1 - We update our state
             playerState.value = PlayerState.DISCONNECTED;

             Debug.signal(Debug.NOTICE, null, "Connection closed on player: "+playerName+" at "+Tools.getLexicalTime());

         // 1 - Leave any current chat...
            if( !currentChatPrimaryKey.equals( ChatRoom.DEFAULT_CHAT ) ) {
                RemPlayerFromChatRoomMsgBehaviour remPlayerFromChat
                     = new RemPlayerFromChatRoomMsgBehaviour( getPrimaryKey(), currentChatPrimaryKey );

                try{
                   remPlayerFromChat.doBehaviour( this );
                }catch( Exception e ) {
                   Debug.signal( Debug.ERROR, this, e );
                   currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT;
                }
            }

            synchronized( chatListLock ) {
                chatList = null;
            }

         // 2 - Stop any current movement
            if(getLocation().isRoom()) {

              // no movement saved on rooms...
                 movementComposer.resetMovement();

              // We send an update to players near us...
              // ... and players in other rooms
                 NetMessage msg[] = new NetMessage[2];
                 msg[0] = (NetMessage) movementComposer.getUpdate();
                 msg[1] = (NetMessage) new PlayerConnectedToGameMessage(getPrimaryKey(),false);

                 if(myRoom==null) {
                    Debug.signal( Debug.ERROR, this, "Player "+getPrimaryKey()+" has an incoherent location state");
                    return;
                 }

                 myRoom.getMessageRouter().sendMessages( msg, this, MessageRouter.EXTENDED_GROUP );
                 return;
            }
            else if(getLocation().isTown()) {
              // no movement saved on towns...
                 movementComposer.resetMovement();
                 return;
            }
            else if(getLocation().isTileMap()) {

              // no movement saved on tileMap...
                 movementComposer.resetMovement();

              // We send an update to players near us...
                 NetMessage msg[] = new NetMessage[2];
                 msg[0] = (NetMessage) movementComposer.getUpdate();
                 msg[1] = (NetMessage) new PlayerConnectedToGameMessage(getPrimaryKey(),false);

                 if(myTileMap==null) {
                    Debug.signal( Debug.ERROR, this, "Player "+getPrimaryKey()+" has an incoherent location state");
                    return;
                 }

                 myTileMap.getMessageRouter().sendMessages( msg, this, MessageRouter.EXTENDED_GROUP );
                 return;
            }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Use this method to send a NetMessage to this player. You can use it directly :
    *  it does not lock, does not wait for the message to be sent before returning
    *  AND checks that the player is connected.
    *
    * @param message message to send to the player.
    */
    public void sendMessage( NetMessage message ) {
        synchronized( connectionLock ) {
            if( connection!=null ) {
                if( ServerDirector.SHOW_DEBUG )
                    System.out.println("Player "+getPrimaryKey()+" sending msg: "+message);
                connection.queueMessage( message );
            }
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
     
    /** Use this method to send a SendTextMessage to this player. You can use it directly :
    *  it does not lock, does not wait for the message to be sent before returning
    *  AND checks that the player is connected.
    *
    * @param message message to send to the player.
    * @param otherPlayerKey key of player who sent the message
    */
    public void sendChatMessage( SendTextMessage message, PlayerImpl otherPlayer) {
        synchronized( connectionLock ) {
            if( connection!=null ) {
                if( ServerDirector.SHOW_DEBUG )
                    System.out.println("Player "+getPrimaryKey()+" sending to:"+otherPlayer.getPrimaryKey()+" msg: "+message);
                connection.queueMessage( message );

                  if( !getPrimaryKey().equals(otherPlayer.getPrimaryKey()) )
                     lieManager.addMeet(otherPlayer, LieManager.MEET_CHATMESSAGE);
            }
        }
    }    

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To close the network connection if any.
    */
     public void closeConnection() {
        synchronized( connectionLock ) {
            if( connection!=null )
                connection.close();
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To ask the grant to send a focus sound... see FOCUS_SOUND_PERIOD definition for
    *  more information.
    *
    *  @return true if you can send the sound, false otherwise.
    */
    public boolean askGrantAccessToSendFocusSound() {
    	long now = System.currentTimeMillis();
        if( focusSoundTimeStamp+FOCUS_SOUND_PERIOD < now ) {
            focusSoundTimeStamp=now;
            return true; // grant accepted
        }
        return false; // grant rejected
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Redirects the network listener. USE WITH CARE !! should be only used by the
    *  BotFactory.
    */
    public void removeConnectionListener() {
        synchronized( connectionLock ) {
            if( connection!=null )
                connection.removeConnectionListener(this);
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    
    /** write object data with serialize.
    */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeUTF(playerName);
        objectOutput.writeUTF(playerPast);
        objectOutput.writeUTF(playerAwayMessage);
        objectOutput.writeObject(wotCharacter);
        objectOutput.writeLong(lastDisconnectedTime);
 
        objectOutput.writeObject(lieManager);
        objectOutput.writeObject(playerState);
        objectOutput.writeObject(movementComposer);
        /* FIXME ???
        PlayerState playerState = new PlayerState());
        MovementComposer movementComposer = (MovementComposer) new PathFollower());
        LieManager lieManager = new LieManager());
         */
   }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
    */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            playerName = objectInput.readUTF();
            playerPast = objectInput.readUTF();
            playerAwayMessage = objectInput.readUTF();
            wotCharacter = ( BasicChar ) objectInput.readObject();
            lastDisconnectedTime = objectInput.readLong();;

            lieManager = ( LieManager ) objectInput.readObject();
            playerState = ( PlayerState ) objectInput.readObject();
            movementComposer = ( MovementComposer ) objectInput.readObject();
            /* FIXME ???
            PlayerState playerState = new PlayerState();
            MovementComposer movementComposer = (MovementComposer) new PathFollower();
            LieManager lieManager = new LieManager();
            */
       } else {
            // to do.... when new version
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** id version of data, used in serialized persistance.
    */
    public int ExternalizeGetVersion(){
        return 1;
    }

    private PlayerOnTheScreen playerOnTheScreen;

    /** called by router or 
    *
    */
    public PlayerOnTheScreen getScreenObject() {
        short[] indexOfImage = { (short)0 , (short)0 };
        if( playerOnTheScreen == null )
            playerOnTheScreen = new PlayerOnTheScreen( this, indexOfImage );
        return playerOnTheScreen;
    }
}