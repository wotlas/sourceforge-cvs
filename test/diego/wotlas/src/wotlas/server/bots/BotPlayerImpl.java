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

package wotlas.server.bots;

import wotlas.server.*;
import wotlas.server.message.chat.*;

import wotlas.utils.Debug;

import wotlas.libs.net.*;
import wotlas.common.chat.*;
import wotlas.common.message.chat.*;
import wotlas.common.message.description.*;
import wotlas.common.router.MessageRouter;

/** A simple Bot that does not move and only send answers to chat messages.
 *
 * @author Aldiss
 * @see wotlas.server.bots.BotPlayer
 */

public class BotPlayerImpl extends PlayerImpl implements BotPlayer {

 /*------------------------------------------------------------------------------------*/

   /** Our default chat room name. This field is made persistent.
    *  When the bot is created it automatically creates this default chat room.
    */
      private String defaultChatRoomName;

 /*------------------------------------------------------------------------------------*/

   /** Constructor.
    */
      public BotPlayerImpl() {
         super();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** When this method is called, the bot can intialize its fields safely.
    *  Always call the super.init() method.
    */
      public void init() {
          super.init();
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** If you call this method all the local data will be replaced by the given
    *  player's one.
    */
      public void clone( PlayerImpl playerToClone ) {
      	  super.clone(playerToClone);

          ChatRoom ourChatRoom = null;

          if(chatList!=null)
             ourChatRoom = chatList.getChatRoom(currentChatPrimaryKey);

          if( ourChatRoom==null || !ourChatRoom.getCreatorPrimaryKey().equals(primaryKey) )
              defaultChatRoomName = playerName;  // bot creator forgot to create a default chat for the bot, we create one
          else
              defaultChatRoomName = ourChatRoom.getName(); // get chatroom's name
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the bot's default chat room name (for persistence only).
     */
      public String getDefaultChatRoomName() {
          return defaultChatRoomName;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the bot's default chat room name (for persistence only).
     */
      public void setDefaultChatRoomName( String defaultChatRoomName ) {
          this.defaultChatRoomName = defaultChatRoomName;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Is this player a Master player ? ( directly controlled  by the client )
    * @return true if this is a Master player, false otherwise.
    */
      public boolean isMaster() {
        return false; // a bot has no master player
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Is this player connected to the game ? ( not synchronized )
    * @return true if the player is in the game, false if the client is not connected.
    */
      public boolean isConnectedToGame() {

         if( super.isConnectedToGame() )
             return true;

         BotChatService chatService = ServerDirector.getDataManager().getBotManager().getBotChatService();
         
         if( chatService!=null && chatService.isAvailable() )
             return true;

         return false;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set if this player is connected to the game.
    *
    *  BOTS : This method can be called by the BotChatService when it becomes available or
    *  unavailable. This methods advertises the bots state change to near players.
    *
    * @param isConnected this parameter is not used here, it is directly checked at the
    *        BotChatService.isAvailable method...
    */
      public void setIsConnectedToGame( boolean isConnected ) {

            if( super.isConnectedToGame() )
                return;

         // We check our real state (we had to stay compatible with the method signature)
            if( isConnected!=isConnectedToGame() ) {
               isConnected = !isConnected;
               Debug.signal(Debug.WARNING, this, "Bad 'isConnected' value avoided...");
            }

         // if it's a transition from connected to not connected we
         // reset our state
            if( !isConnected ) {
                lastDisconnectedTime = System.currentTimeMillis();
                movementComposer.resetMovement();
            }

      	 // we signal our change to our neighbours
            if(location.isRoom()) {
              // 1 - myRoom & MessageRouter check
                 if(myRoom==null) {
                    Debug.signal( Debug.ERROR, this, "Bot "+primaryKey+" has an incoherent location state");
                    return;
                 }
                 else if(myRoom.getMessageRouter()==null) {
                    Debug.signal( Debug.ERROR, this, "Message Router not found for bot "+primaryKey);
                    return;
                 }

              // 2 - We send an update to players near us...
              // ... and players in other rooms
                 if(!isConnected)
                     myRoom.getMessageRouter().sendMessage( (NetMessage) movementComposer.getUpdate(),
                                                           this, MessageRouter.EXTENDED_GROUP );

              // 3 - We check that we are a member of the given Message Router
                 if( myRoom.getMessageRouter().getPlayer(primaryKey)!=null ) {
                   // We send an update to players near us...
                      PlayerConnectedToGameMessage pMsg = new PlayerConnectedToGameMessage(
                                                          primaryKey, isConnected );
                      myRoom.getMessageRouter().sendMessage( pMsg, this,
                                                          MessageRouter.EXTENDED_GROUP );
                 }

              // 4 - We create or delete local chat room
                 if(isConnected) {

                     if( defaultChatRoomName==null ) {
                         Debug.signal(Debug.ERROR, this, "Bot has no default chat !" );
                         defaultChatRoomName = playerName;
                     }

                  // we create our bot's default chat room
                     ChatRoomCreationMsgBehaviour roomCreation = new ChatRoomCreationMsgBehaviour(defaultChatRoomName,primaryKey,true);
 
                     try{
                          roomCreation.doBehaviour( this );
                     }
                     catch( Exception e ) {
                          Debug.signal(Debug.ERROR,this,"Failed to create default chat room for bot...");
                     }
                 }
                 else if( !currentChatPrimaryKey.equals( ChatRoom.DEFAULT_CHAT ) ) {
                    // we quit our current chat
                     RemPlayerFromChatRoomMsgBehaviour remPlayerFromChat
                            = new RemPlayerFromChatRoomMsgBehaviour( primaryKey, currentChatPrimaryKey );

                     try{
                          remPlayerFromChat.doBehaviour( this );
                     }catch( Exception e ) {
                          Debug.signal( Debug.ERROR, this, e );
                          currentChatPrimaryKey = ChatRoom.DEFAULT_CHAT;
                     }
                 }
            }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method does nothing here. It only produces an error message.
   */
     public void connectionCreated( NetConnection connection ) {
            super.connectionCreated(connection);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method does nothing here. It only produces an error message.
   */
     public void connectionClosed( NetConnection connection ) {
            super.connectionClosed( connection );
            setIsConnectedToGame(isConnectedToGame());
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Use this method to send a NetMessage to this bot. You can use it directly :
   *  it does not lock, does not wait for the message to be sent before returning.
   *
   *  BOT : we check the type of the message and react to special messages.
   *
   * @param message message to send to the bot.
   */
     public void sendMessage( NetMessage message ) {
           super.sendMessage(message);

        // 1 - need to react ?
           if( !isConnectedToGame() )
               return;

        // 2 - what type of message ?
        //     we use the instanceof operator here because we don't want to modify the code
        //     of the classes that invoke sendMessage(). This way our bot code remains
        //     integrated to the bots package.
           if( message instanceof AddPlayerToChatRoomMessage ) {
              AddPlayerToChatRoomMessage msg = (AddPlayerToChatRoomMessage) message;

              if( currentChatPrimaryKey.equals( msg.getChatRoomPrimaryKey() ) ) {
                 // ok, new player has entered our chat
                 // we open a botchatservice session with him
                    PlayerImpl player = (PlayerImpl) getMessageRouter().getPlayer( msg.getSenderPrimaryKey() );
                    
                    if(player==null) {
                    	Debug.signal(Debug.ERROR,this,"Player "+msg.getSenderPrimaryKey()+" not found ! can't open session !");
                    	return; // player not found
                    }

                    ServerDirector.getDataManager().getBotManager().openChatBotSession( this, player );

                 // we turn toward the new comer
                    if(location.isRoom()) {
                       movementComposer.resetMovement();
                       movementComposer.setOrientationAngle( player.getMovementComposer().getOrientationAngle()+Math.PI);

                       if(myRoom==null) {
                          Debug.signal( Debug.ERROR, this, "Bot "+primaryKey+" has an incoherent location state");
                          return;
                       }

                       myRoom.getMessageRouter().sendMessage( movementComposer.getUpdate(),
                                                    this, MessageRouter.EXTENDED_GROUP );
                    }
              }
           }
           else if( message instanceof RemPlayerFromChatRoomMessage ) {
              RemPlayerFromChatRoomMessage msg = (RemPlayerFromChatRoomMessage) message;

              if( currentChatPrimaryKey.equals( msg.getChatRoomPrimaryKey() ) ) {
                 // ok, a player has left our chat
                 // we close his botchatservice session
                    PlayerImpl player = (PlayerImpl) getMessageRouter().getPlayer( msg.getSenderPrimaryKey() );
                    
                    if(player==null) {
                    	Debug.signal(Debug.ERROR,this,"Player "+msg.getSenderPrimaryKey()+" not found ! can't close session !");
                    	return; // player not found
                    }

                    ServerDirector.getDataManager().getBotManager().closeChatBotSession( this, player );
              }

           }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Use this method to send a chat message to this bot. You can use it directly :
   *  it does not lock, does not wait for the message to be sent before returning.
   *
   * @param message message to send to the player.
   * @param otherPlayerKey key of player who sent the message
   */
     public void sendChatMessage( SendTextMessage message, PlayerImpl otherPlayer) {
               super.sendMessage( message );

            // 1 - We don't talk to other bots & chat groups... (security)
               if(otherPlayer instanceof BotPlayer) {
               	  Debug.signal( Debug.WARNING, this, "Bot "+otherPlayer.getPrimaryKey()
               	                +" tried to talk to "+primaryKey );
               	  return;
               }

               if( !message.getChatRoomPrimaryKey().equals(currentChatPrimaryKey) )
                  return;

            // 2 - If the BotChatService is available we ask for an answer
               BotChatService chatService = ServerDirector.getDataManager().getBotManager().getBotChatService();

               if( chatService==null || !chatService.isAvailable() )
                   return;

               chatService.askForAnswer( message.getMessage(), otherPlayer, this );

            // 3 - Increment our Lie manager
               if( !primaryKey.equals(otherPlayer.getPrimaryKey()) )
                   lieManager.addMeet(otherPlayer, LieManager.MEET_CHATMESSAGE);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To send an answer from this bot to its local group.
    *  @param message chat message to send.
    */
     public void sendChatAnswer( String message ) {

            SendTextMessage tMsg = new SendTextMessage( primaryKey, playerName,
                                                       currentChatPrimaryKey, message,
                                                       ChatRoom.NORMAL_VOICE_LEVEL );
            getMessageRouter().sendMessage( tMsg, this );
            super.sendMessage( tMsg );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}