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

import wotlas.utils.Debug;
import wotlas.server.*;

import java.util.Properties;
import java.util.Hashtable;
import java.util.Iterator;

/** A bot manager possesses a BotFactory to ease bots creation, a BotChatService
 *  that enables bots to get answers to send to players when they are chatting.
 *  Finally the BotManager is a façade (design pattern) for some methods that
 *  concerns bots.
 *
 *  IMPORTANT : to init this class needs the init.botChatServiceClass server property
 *              See the ServerPropertiesFile for more information.
 *
 * @author Aldiss
 * @see wotlas.server.bots.BotPlayer
 */

public class BotManager {

 /*------------------------------------------------------------------------------------*/

   /** Our Bot Factory
    */
      private BotFactory botFactory;

   /** Our Bot Chat Service
    */
      private BotChatService botChatService;

   /** Link to the account manager where bots are stored just like real players.
    */
      private AccountManager accountManager;

 /*------------------------------------------------------------------------------------*/

   /** Empty Constructor.
    */
      public BotManager( AccountManager accountManager ) {
      	  this.accountManager = accountManager;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Call this method to initialize the factory with the server properties.
    * @param serverProperties server properties as given by the ServerDirector
    * @return true if the initialization succeeded, false if it failed
    */
     public boolean init( Properties serverProperties ) {

       // 1 - We create our Bot Factory
          botFactory = new BotFactory();

          if( !botFactory.init(serverProperties) )
              return false; //failed to init factory

       // 2 - Creation of the Bot Chat Service
          String botChatServiceClass = serverProperties.getProperty("init.botChatServiceClass");

          if(botChatServiceClass==null)
             return false; // property not found

          try{
               Class myClass = Class.forName(botChatServiceClass);
               botChatService = (BotChatService) myClass.newInstance();
          }catch(Exception ex) {
               Debug.signal( Debug.ERROR, null, "Failed to create new instance of "+botChatServiceClass+", "+ex );
               return false;
          }

          if(!botChatService.init( serverProperties ) )
             return false; // init failed

          botChatService.connect(); // we try a first connection
          return true;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To shutdown this bot manager.
    */
     public void shutdown() {
     	// 1 - end of connection with the chat bot service
     	   botChatService.shutdown();

        // 2 - clean up
           botFactory = null;
           accountManager = null;
           botChatService = null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the bot factory.
    */
     public BotFactory getBotFactory() {
        return botFactory;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the bot chat service.
    */
     public BotChatService getBotChatService() {
        return botChatService;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To open a session with the bots of a chat room
    *  @param bot bot with which we want to open the bot chat session...
    *  @param player that asks to open the sessions
    */
     public void openChatBotSession( BotPlayer bot, PlayerImpl player ) {
       // 1 - We don't talk to other bots... (security)
          if(player instanceof BotPlayer) {
             Debug.signal( Debug.WARNING, this, "Bot "+player.getPrimaryKey()
                           +" tried to talk with other bots !!" );
             return;
          }

       // 2 - Check if the bot chat service is running
          if( !botChatService.isAvailable() )
             return; // not running

       // 3 - Try to open a session
          botChatService.openSession( bot, player );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To open a session with the bots of a chat room
    *  @param playerGroup a group of bots & players...
    *  @param player that asks to open the sessions
    */
     public void openChatBotSessions( Hashtable playerGroup, PlayerImpl player ) {
       // 1 - We don't talk to other bots... (security)
          if(player instanceof BotPlayer) {
             Debug.signal( Debug.WARNING, this, "Bot "+player.getPrimaryKey()
                           +" tried to talk with other bots !!" );
             return;
          }

       // 2 - Check if the bot chat service is running
          if( !botChatService.isAvailable() )
             return; // not running

       // 3 - Search for bots and open sessions 	
          synchronized( playerGroup ) {
             Iterator it = playerGroup.values().iterator();

             while( it.hasNext() ) {
          	 PlayerImpl pl = (PlayerImpl) it.next();
          	 if( !(pl instanceof BotPlayer) ) continue;
          	 
          	 botChatService.openSession( (BotPlayer) pl, player );
             }
          }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To close a session with the bots of a chat room.
    *  @param bot bot with which we want to close the bot chat session...
    *  @param player that asks to close the sessions
    */
     public void closeChatBotSession( BotPlayer bot, PlayerImpl player ) {
       // 1 - We don't talk to other bots... (security)
          if(player instanceof BotPlayer) {
             Debug.signal( Debug.WARNING, this, "Bot "+player.getPrimaryKey()
                           +" tried to close a session with players." );
             return;
          }

       // 2 - Check if the bot chat service is running
          if( !botChatService.isAvailable() )
             return; // not running

       // 3 - Try to close the session
          botChatService.closeSession( bot, player );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To close a session with the bots of a chat room.
    *  @param playerGroup a group of bots & players...
    *  @param player that asks to close the sessions
    */
     public void closeChatBotSessions( Hashtable playerGroup, PlayerImpl player ) {
       // 1 - We don't talk to other bots... (security)
          if(player instanceof BotPlayer) {
             Debug.signal( Debug.WARNING, this, "Bot "+player.getPrimaryKey()
                           +" tried to close a session with players." );
             return;
          }

       // 2 - Check if the bot chat service is running
          if( !botChatService.isAvailable() )
             return; // not running

       // 3 - Search for bots and close sessions
          synchronized( playerGroup ) {
             Iterator it = playerGroup.values().iterator();

             while( it.hasNext() ) {
          	 PlayerImpl pl = (PlayerImpl) it.next();
          	 
          	 if( !(pl instanceof BotPlayer) ) continue;

                 botChatService.closeSession( (BotPlayer) pl, player );
             }
          }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the number of online bots.
    */
     public int getOnlineBotsNumber() {

          if( !botChatService.isAvailable() )
               return 0; // service not running

     	return getBotsNumber();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the number of bots.
    */
     public int getBotsNumber() {

          int nb=0;

          synchronized( accountManager ) {
             Iterator it = accountManager.getIterator();

             while( it.hasNext() ) {
          	 GameAccount ac = (GameAccount) it.next();
          	 PlayerImpl pl = ac.getPlayer();
          	 
          	 if( pl instanceof BotPlayer ) nb++;
             }
          }

     	return nb;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To refresh the state of all the bots. This method can be used by the botChatService
    *  to tell the bots that its state has changed.
    */
     public void refreshBotState() {
System.out.println("REFRESHING BOT STATE");
          synchronized( accountManager ) {
             Iterator it = accountManager.getIterator();

             while( it.hasNext() ) {
          	 GameAccount ac = (GameAccount) it.next();
          	 PlayerImpl pl = ac.getPlayer();
          	 
          	 if( pl instanceof BotPlayer )
          	     pl.setIsConnectedToGame( botChatService.isAvailable() );
             }
          }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
