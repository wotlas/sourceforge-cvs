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

package wotlas.server.bots.alice.client;

import wotlas.server.bots.alice.AliceWotlasMessage;
import wotlas.server.bots.*;
import wotlas.server.*;

import wotlas.libs.net.*;
import wotlas.common.Player;

import wotlas.utils.Debug;
import wotlas.utils.PropertiesConfigFile;

import java.util.Properties;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/** A BotChatService which connects to an AliceBot server using the AliceWOTLAS
 *  AliceChatListener.
 *
 * @author Aldiss
 * @see wotlas.server.bots.BotPlayer
 */

public class AliceBotChatService implements BotChatService, ActionListener, NetConnectionListener {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Period between two connection attempts. (beware it's an int !)
    */
      public static final int CONNECT_PERIOD = 1000*60*10;  // 10 minutes

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Alice Host Name
    */
      protected String aliceHost;

   /** Alice Host Port
    */
      protected int alicePort;

   /** NetPersonality that represents the connection with the Alice server
    */
      protected NetPersonality personality;

   /** Timer to retry a connection if the previous attempt failed.
    */
      protected Timer timer;

   /** Personality Lock
    */
      protected byte personalityLock[] = new byte[0];

   /** To tell that we are shutting down...
    */
      protected boolean shutdown;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Empty constructor for dynamic construction.
    */
      public AliceBotChatService() {
           alicePort = -1;
           shutdown = false;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To init this chat service.
    *  @param serverProperties server properties giving some information for this service
    *  @return true if the initialization succeeded, false if it failed
    */
      public boolean init( Properties serverProperties ) {

           PropertiesConfigFile serverProps = (PropertiesConfigFile) serverProperties;
           Debug.signal(Debug.NOTICE, null, "Bot Chat Service used : alicebot");

        // 1 - We retrieve the alicebot server's address & port
           if( !serverProps.isValid("bots.aliceHost") ) {
               Debug.signal( Debug.FAILURE, this, "No alice host property set !" );
               return false;
           }

           if( !serverProps.isValidInteger("bots.alicePort") ) {
               Debug.signal( Debug.FAILURE, this, "The given alice port is not a valid integer !" );
               return false;
           }

           aliceHost = serverProps.getProperty("bots.aliceHost");
           alicePort = serverProps.getIntegerProperty("bots.alicePort");

           shutdown = false;
          return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To initialize the connection with the remote chat service. The first connect()
    *  is called just after the init() call. It's your job to eventually a thread to manage
    *  the state of the connection.<br>
    *
    *  When the connection succeeds or fails you should refresh the bots state :<br>
    *
    *     ServerDirector.getDataManager().getBotManager().refreshBotState();
    *
    *  @return true if the connection was successfully established, false otherwise
    */
      public boolean connect() {

           if(alicePort==-1 || aliceHost==null || shutdown==true)
              return false; // no alice info
        
        // 1 - We try a connection...
           NetClient client = new NetClient();

           String messagePackages[] = { "wotlas.server.bots.alice.client" };

           personality = client.connectToServer(
                                          aliceHost, alicePort,
                                          "alicebot-access:"+ServerDirector.getServerID(),     // access key for that server (password)
                                          ServerDirector.getDataManager().getAccountManager(), // context for NetMessageBehaviour
                                          messagePackages ); // Message packages to load

           synchronized( personalityLock ) {
              if(personality==null) {
                // We failed to connect... we create a timer to retry later
                   Debug.signal(Debug.WARNING, null, "Bot Chat Service connect : failed to reach AliceBot server. We'll retry later.\n"
                                                     +client.getErrorMessage());

                   if(timer==null) {
                   // we create a timer to retry a connection attempt in CONNECT_PERIOD
                      timer = new Timer(CONNECT_PERIOD,this);
                      timer.start();
                   }

                   return false;
              }
              else {
                   personality.setConnectionListener( this ); // WE will monitor the state of the connection

                // Connection succeeded !
                   if(timer!=null) {
                      timer.stop();
                      timer = null;
                   }
              }
           }

           Debug.signal(Debug.NOTICE, null, "Bot Chat Service connect : alice server reached !");

        // 3 - We refresh the bots state
           ServerDirector.getDataManager().getBotManager().refreshBotState();
           return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To shut down the connection with the remote chat service. When this method is called
    *  it means the system is about to shutdown. You should free resources and advertise
    *  the shut.
    *  @return true if the connection was successfully shutdown, false otherwise
    */
      public boolean shutdown() {
      	  Debug.signal(Debug.NOTICE, null, "Bot Chat Service shutting down...");
      	  
          synchronized( personalityLock ) {
             shutdown = true;

             if(personality!=null) {
      	        personality.closeConnection();
                personality = null;
             }

             if(timer!=null) {
                timer.stop();
                timer=null;
             }
          }

          return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the state of this chat service (usually represents the connection state).
   *  @return true if this BotChatService is available, false if it's not working at
   *          the moment.
   */
      public boolean isAvailable() {
          synchronized( personalityLock ) {
             return personality!=null;
          }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Opens a chat session for the given player with the given bot. This method is
    *  called each time a player arrives in the bot's area.
    *
    *  @param bot bot who's the target of the session.
    *  @param player player arriving near our bot.
    *  @return true if the session was opened successfully
    */
      public boolean openSession(  BotPlayer bot, Player player ) {
      	  // nothing special to do, alicebot manages its session itself...
          // We just send a hi! from the client
             synchronized( personalityLock ) {
                if(personality==null)
                   return false;

              //  String message = "Hi, my name is "+((PlayerImpl) player).getFullPlayerName( (PlayerImpl) bot );
                  String message = "Hi!";
              
                personality.queueMessage(
                          new AliceWotlasMessage( player.getPrimaryKey(),
                                                  ((PlayerImpl)bot).getPrimaryKey(),
                                                  message,
                                                  ServerDirector.getServerID() ) );
             }

          return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Player 'fromPlayer' sent a 'message' to 'toBot'. We ask to this service the
    *  answer 'toBot' must send to 'fromPlayer'. This method SHOULD BE ASYNCHRONOUS.
    *  I.E. we ask for an answer and return. Later, when the result is received we
    *  call back the bot's sendChatAnswer method.
    *
    *  @param message message sent by 'fromPlayer'
    *  @param fromPlayer the player that sent the chat 'message'.
    *  @param toBot the bot which is supposed to answer the 'fromPlayer''s message.
    */
      public void askForAnswer( String message, Player fromPlayer, BotPlayer toBot ) {
      	
      	  // We send the request to the Alice Server
             synchronized( personalityLock ) {
                if(personality==null)
                   return;

                personality.queueMessage(
                          new AliceWotlasMessage( fromPlayer.getPrimaryKey(),
                                                  ((PlayerImpl)toBot).getPrimaryKey(),
                                                  message,
                                                  ServerDirector.getServerID() ) );
             }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Closes a chat session for the given player with the given bot. This method is
    *  called each time a player leaves the bot's area.
    *
    *  @param bot bot who's the target of the session.
    *  @param player player leaving our bot.
    *  @return true if the session was closed successfully
    */
      public boolean closeSession(  BotPlayer bot, Player player ) {
      	  // nothing to do, alicebot manages its session itself...
      	  return true;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** This method is called when a new network connection is created on this player.
    *
    * @param personality the NetPersonality object associated to this connection.
    */
      public void connectionCreated( NetPersonality personality ) {
          // nothing to do...
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when the network connection of the client is no longer
   * of this world.
   *
   * @param personality the NetPersonality object associated to this connection.
   */
     public void connectionClosed( NetPersonality personality ) {

         // 1 - no more messages will be sent...
             synchronized( personalityLock ) {
                 personality = null;

                 if(!shutdown)
                    ServerDirector.getDataManager().getBotManager().refreshBotState();

                 if(timer==null) {
                   // we create a timer to retry a connection attempt in CONNECT_PERIOD
                    timer = new Timer(CONNECT_PERIOD,this);
                    timer.start();
                 }
             }

             Debug.signal(Debug.NOTICE, null, "Bot Chat Service : connection closed with alice server.");
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Timer Event interception
   */
   public void actionPerformed( ActionEvent e ) {
        if(e.getSource()!=timer)
           return;

     // New connection attempt
        connect();
   }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
