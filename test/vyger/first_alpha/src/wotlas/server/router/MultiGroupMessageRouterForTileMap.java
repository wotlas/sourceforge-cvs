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

package wotlas.server.router;

import wotlas.server.PlayerImpl;
import wotlas.server.message.chat.*;

import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.common.router.*;
import wotlas.common.chat.*;
import wotlas.common.screenobject.*;

import wotlas.common.message.description.*;
import wotlas.common.message.movement.*;
import wotlas.common.message.chat.*;

import wotlas.libs.net.NetMessage;
import wotlas.utils.Debug;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;

/** A message router for TileMaps which follows a 1-near step policy.
 * 
 *
 * @author Aldiss, Diego
 */

public class MultiGroupMessageRouterForTileMap extends MessageRouter {

// FIXME ???       player.setPrimaryKey( account.getAccountName() );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Our Map.
    */
    protected TileMap thisTileMap;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor. Just creates internals.
    */
    public MultiGroupMessageRouterForTileMap() {
        super();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Inititializes this MessageRouter.
    *
    * @param location location this MessageRouter is linked to.
    * @param wManager WorldManager of the application.
    */
     public void init(WotlasLocation location, WorldManager wManager) {

         // 1 - We get our tileMap
            if( !location.isTileMap()) {
               Debug.signal(Debug.FAILURE, this, "Location is not a TileMap ! Can't init router !" );
               return;
            }

            thisTileMap = wManager.getTileMap( location );

            if( thisTileMap==null ) {
               Debug.signal(Debug.FAILURE, this, "TileMap not found ! Can't init router !" );
               return;
            }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To add a player to this group. We update its location.
    *
    *  Call this method when a player is added to the map. If the player is arriving from
    *  another map call movePlayer.
    *
    * @param player player to add
    * @return true if the player was added successfully, false if an error occured.
    */
     public boolean addPlayer( Player player ) {
        return addScreenObject(player.getScreenObject());
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To update the local chat information for a player. We update the state of the
    *  player
    */
    protected void updateChatInformation( PlayerImpl player ) {

        // 1 - and signal our player to default chat room...
        sendMessage( new AddPlayerToChatRoomMessage( player.getPrimaryKey(), ChatRoom.DEFAULT_CHAT ),
                       player );

        // 2 - We send CHAT data to the added player
        //     ( list of the players of the default chat room )
        SetCurrentChatRoomMessage msg = new SetCurrentChatRoomMessage();
        msg.SetToUseScreenObjects(ChatRoom.DEFAULT_CHAT, players );
        player.sendMessage( msg );

        // 3 - We seek for a valid chatList if any...
        synchronized( screenObjects ) {
            Iterator it = screenObjects.values().iterator();

            Object tmpObject;
            while( it.hasNext() ) {
                tmpObject = it.next();
                if( tmpObject instanceof PlayerOnTheScreen ){
                    PlayerImpl p = (PlayerImpl)( (PlayerOnTheScreen) tmpObject ).getPlayer();
                    if(tmpObject != player && p.isConnectedToGame() ) {
                        ChatList chatList = p.getChatList();

                        if( chatList!=null ) {
                            player.setChatList( chatList );
                            break;
                        }
                    }
                }
            }
        }

        ChatList myChatList = player.getChatList();

        if( myChatList == null )
            return; // no chats in the room

        // 4 - We send the CHAT ROOMS available to our client...
        Hashtable chatRooms = myChatList.getChatRooms();

        synchronized( chatRooms ) {
            Iterator it = chatRooms.values().iterator();

            while( it.hasNext() ) {
                ChatRoom cRoom = (ChatRoom) it.next();
                player.sendMessage( new ChatRoomCreatedMessage( cRoom.getPrimaryKey(),
                                           cRoom.getName(), cRoom.getCreatorPrimaryKey() ) );
            }
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To remove a player from this group.
    *
    *  Call this method when a player is removed from the map. If the player is arriving
    *  from another room call movePlayer.
    *
    * @param player player to remove
    * @return true if the player was removed successfully, false if an error occured.
    */
     public boolean removePlayer( Player player ) {
          return removeScreenObject( player.getScreenObject() );
     }

   /** To remove a player from this group.
    *
    *  Call this method when a player is removed from the map. If the player is arriving
    *  from another room call movePlayer.
    *
    * @param player player to remove
    * @return true if the player was removed successfully, false if an error occured.
    */
    public boolean removeScreenObject( ScreenObject item ) {

        // 1 - We remove this item from our list
        if( !super.removeScreenObject(item) )
            return false; // non-existent player

        // 2 - We send remove messages to local & near players
        sendMessage( new RemoveScreenObjectFromTileMapMessage( item.getPrimaryKey(), thisTileMap.getLocation() ),
                        null,
                        EXTENDED_GROUP );

        // 3 - Remove from the chat if player
        if( item instanceof PlayerOnTheScreen ){
            PlayerImpl p = (PlayerImpl)( (PlayerOnTheScreen) item ).getPlayer();
            Player player = ( (PlayerOnTheScreen) item ).getPlayer();
            if( !p.getCurrentChatPrimaryKey().equals( ChatRoom.DEFAULT_CHAT ) ) {
                RemPlayerFromChatRoomMsgBehaviour remPlayerFromChat
                    = new RemPlayerFromChatRoomMsgBehaviour( player.getPrimaryKey(),
                                        p.getCurrentChatPrimaryKey() );

                try{
                    remPlayerFromChat.doBehaviour( player );
                }catch( Exception e ) {
                    Debug.signal( Debug.ERROR, this, e );
                    p.setCurrentChatPrimaryKey( ChatRoom.DEFAULT_CHAT );
                }
            }
            else
                sendMessage( new RemPlayerFromChatRoomMessage( player.getPrimaryKey(), ChatRoom.DEFAULT_CHAT ) );
        }
        return true;
     }
     
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To remove all the players of this group. The default implementation of this method
    *  just removes all the players WITHOUT sending any messages.
    */
     public void removeAllPlayers() {
         super.removeAllScreenObjects();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To find a player by its primary key. We first search in  the local group and then
    *  extend our search to near groups.
    *
    * @param primaryKey player to find
    * @return null if not found, the player otherwise
    */
     public Player getPlayer( String primaryKey ) {
           // Player p = (Player) players.get( primaryKey );
           return null; // not found
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To move a player from this group to another. The player location is changed.
    *
    * @param player player to move
    * @return true if the player was moved successfully, false if an error occured.
    */
     public boolean movePlayer(Player player, WotlasLocation targetLocation) {
        return false;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To send a list of messages to the specified group with the exception of a player.
    *  @param msg message to send to the group
    *  @param exceptThisPlayer player to except from the send of messages, if the
    *         given player is null the message will be sent to everyone in the selected
    *         groups.
    *  @param groupOption gives the groups to send the message to. See the constants
    *         defined in this class : LOCAL_GROUP, EXTENDED_GROUP, EXC_EXTENDED_GROUP
    */
     public void sendMessages(NetMessage[] msg, Player exceptThisPlayer, byte groupOption) {
        if( groupOption!=EXC_EXTENDED_GROUP ) {
        // We send the messages to the local group.
            synchronized( screenObjects ) {
                Iterator it = screenObjects.values().iterator();

                Object tmpObject;
                while( it.hasNext() ) {
                    tmpObject = it.next();
                    if( tmpObject instanceof PlayerOnTheScreen ){
                        Player p = ( (PlayerOnTheScreen) tmpObject ).getPlayer();
                        if(p!=exceptThisPlayer) {
                            for( int i=0; i< msg.length; i++ )
                                p.sendMessage( msg[i] );
                        }
                    }
                }
            }
        }
    }

 /* - - - - - - - - - - -screen object manipulation - - - - - - - - - - - - - - -*/

    /** To add a screen object to this map to this group. We update its location.
    *
    *  Call this method when a player/npc/item is added to the map. 
    *  
    * @param item to add
    * @return true if it was added successfully, false if an error occured.
    */
    public boolean addScreenObject( ScreenObject item ) {

        // 1 - We add this player to our list & don't care if it's already in there
        screenObjects.put( item.getPrimaryKey(), item );
        item.setLocation( thisTileMap.getLocation() ); // update player location
        item.serverInit( thisTileMap.getAStar() );
        
        if(!item.isConnectedToGame())
            return true; // no need to advertise if the player is not connected

        // 2 - We advertise our presence to the other players in the LOCAL map
        //   and to the npc-> but not with message for npc.
        AddScreenObjectToTileMapMessage aMsg = new AddScreenObjectToTileMapMessage( item );
        synchronized( screenObjects ) {
            Iterator it = screenObjects.values().iterator();
            
            Object tmpObject;
            while( it.hasNext() ) {
                tmpObject = it.next();
                if( tmpObject instanceof PlayerOnTheScreen ){
                    Player p = ( (PlayerOnTheScreen) tmpObject ).getPlayer();
                    if(tmpObject!=item) {
                        // needed for the LieManager to know who is
                        //aMsg.setOtherPlayer(p);
                        // asking for the player's name....
                        p.sendMessage( aMsg );
                    }
                }
                else if( tmpObject instanceof NpcOnTheScreen )
                    ; // add code to inform npc. (npc that looks for items.....
                else if( tmpObject instanceof ItemOnTheScreen )
                    ; // items are not interested in other items.....
                else if( tmpObject instanceof SpellOnTheScreen )
                    ; // items are not interested in other items.....
                else
                    continue;
            }
        }
        
        if( item instanceof PlayerOnTheScreen ) {
            Player p = ( (PlayerOnTheScreen) item ).getPlayer();
            
            // 3 - We send PLAYER data to the added player
            p.sendMessage( new TileMapPlayerDataMessage( thisTileMap, p ) );

            // 4 - We send CHAT DATA to the added player
            updateChatInformation( (PlayerImpl) p );
        }
          
        if(item instanceof AreaSpell)
            ((AreaSpell)item).startMove();
        if(item instanceof ArrowSpell)
            ((ArrowSpell)item).startMove();

        return true;
    }
}