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

package wotlas.server.bots.alice;

import wotlas.server.bots.BotPlayer;

import wotlas.libs.net.NetServer;
import wotlas.libs.net.NetPersonality;
import wotlas.libs.net.NetConnectionListener;

import wotlas.common.ErrorCodeList;

import wotlas.utils.Debug;

import java.util.Hashtable;
import java.util.Iterator;


/** Alice Wotlas Server. Its role is to wait alice message request from remote
 *  wotlas servers and transfer them to the the AliceWOTLAS listener.
 *
 * @author Aldiss
 */

public class AliceWotlasServer extends NetServer implements NetConnectionListener, ErrorCodeList {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Our WOTLAS alice chat listener. We use this class to handle our alicebot answer
    *  requests.
    */
     protected AliceWOTLAS aliceWotlas;

   /** List of the remote wotlas servers we are connected to. The key of the hashtable
    *  is the server's id.
    */
     protected Hashtable serverLinks;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor (see wotlas.libs.net.NetServer for details)
   *
   *  @param host the host interface to bind to. Example: wotlas.tower.org
   *  @param server_port port on which the server listens to clients.
   *  @param msg_packages a list of packages where we can find NetMsgBehaviour Classes.
   *  @param nbMaxSockets maximum number of sockets that can be opened on this server
   *  @param aliceWotlas wotlas alice chat listener
   */
    public AliceWotlasServer( String host, int port, String packages[],
                              int nbMaxSockets, AliceWOTLAS aliceWotlas ) {
       super( host, port, packages );
       this.aliceWotlas = aliceWotlas;
       serverLinks = new Hashtable(10);

       setMaximumOpenedSockets( nbMaxSockets );
       Debug.displayExceptionStack( false );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** This method is called automatically when a new client establishes a connection
    *  with this server ( the client sends a ClientRegisterMessage ).
    *
    * @param personality a previously created personality for this connection.
    * @param key a string given by the client to identify itself. The key structure
    *        is the following "accountName:password". See wotlas.server.GameAccount
    *        for the accountName structure.
    */
    public void accessControl( NetPersonality personality, String key ) {

       // 1 - We check the key
          if( !key.startsWith("alicebot-access:") || key.endsWith(":") ) {
               Debug.signal( Debug.NOTICE, this, "A client tried to connect with a bad key format : "+key);
               refuseClient( personality, ERR_WRONG_KEY, "You are trying to connect with a bad key !!!" );
               return;
          }

       // 2 - We extract the server ID and add its NetPersonality to our list
          try {
               int serverID = Integer.parseInt( key.substring(key.indexOf(':')+1) );
               serverLinks.put(""+serverID,personality);
               Debug.signal(Debug.NOTICE,null,"AliceWotlasServer connection created for server "+serverID);
          }
          catch( Exception e ) {
               Debug.signal( Debug.NOTICE, this, "A client tried to connect with a bad key : "+key);
               refuseClient( personality, ERR_WRONG_KEY, "The key you sent is not correct !!!" );
               return;
          }

       // 3 - we set the message context to our alice chat listener...
          personality.setContext( aliceWotlas );     // requests will go to our AliceWOTLAS class
          personality.setConnectionListener( this ); // we will listen to the connection's state

       // 4 - Final step, all inits have been done, we welcome our new client...
          acceptClient( personality );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To send back Alice's answer to the 'botPrimaryKey' player on server 'serverID'.
    *
    *  @param playerPrimaryKey pk of the player whose the answer is for
    *  @param botPrimaryKey pk of the bot
    *  @param answer sent by alice
    *  @param serverID wotlas server to send the answer to
    */
     public void sendAnswer( String playerPrimaryKey, String botPrimaryKey, String answer, int serverID ) {

       // 1 - Search for the server's connection
          NetPersonality personality = (NetPersonality) serverLinks.get(""+serverID);
       
       // 2 - Send the message...
          if(personality!=null)
             personality.queueMessage(
                 new AliceWotlasMessage( playerPrimaryKey, botPrimaryKey, answer, serverID ) );
          else
             Debug.signal(Debug.ERROR,this,"AliceWotlasServer server "+serverID+" connection not found...");
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when a new network connection is created.
   *
   * @param personality the NetPersonality object associated to this connection.
   */
     public void connectionCreated( NetPersonality personality ) {
        // well nothing to do here...
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when a network connection is no longer of this world.
   *
   * @param personality the NetPersonality object associated to this connection.
   */
     public void connectionClosed( NetPersonality personality ) {

       // We perform some cleaning...
       // We search the server NetPersonality entry in our table
          synchronized( serverLinks ) {
              Iterator it = serverLinks.values().iterator();
          
              while( it.hasNext() ) {
                  NetPersonality np = (NetPersonality) it.next();
                  
                  if( np==personality ) {
                    // ok, we found it... we can remove it...
                      it.remove();
                      Debug.signal(Debug.NOTICE,null,"AliceWotlasServer asked to close a connection.");
                      return;
                  }
              }
          }

          Debug.signal(Debug.WARNING,this,"AliceWotlasServer server connection not found... "
                        +"this message could mean that the connection was just shut and re-opened.");
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     *  Shuts down the process.
     */
      public void shutdown() {
         // 1 - Stop Server
            stopServer();

         // 2 - Shuts down all connections
            synchronized( serverLinks ) {
                Iterator it = serverLinks.values().iterator();
          
                while( it.hasNext() ) {
                   NetPersonality np = (NetPersonality) it.next();
                   np.closeConnection();
                }
            }
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
