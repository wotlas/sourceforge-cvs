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

package wotlas.server;

import wotlas.libs.net.NetServer;
import wotlas.libs.net.NetClient;
import wotlas.libs.net.NetPersonality;

import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigList;

import wotlas.utils.Debug;

import java.io.IOException;


// TO DO : password properties protection



/** Wotlas Gateway Server. This isused for account travel management.
 *  The server awaits for account transferts and also provides a method
 *  to send an account to a remote GatewayServer.
 *
 * @author Aldiss
 */

public class GatewayServer extends NetServer
{
 /*------------------------------------------------------------------------------------*/

   /** Server config list.
    */
     private ServerConfigList configList;

 /*------------------------------------------------------------------------------------*/

  /** Constructor (see wotlas.libs.net.NetServer for details)
   *
   *  @param host the host interface to bind to. Example: wotlas.tower.org
   *  @param server_port port on which the server listens to clients.
   *  @param msg_packages a list of packages where we can find NetMsgBehaviour Classes.
   *  @param nbMaxSockets maximum number of sockets that can be opened on this server
   *  @param configList config of all the servers.
   */
    public GatewayServer( String host, int port, String packages[], int nbMaxSockets,
                          ServerConfigList configList ) {
       super( host, port, packages );
       setMaximumOpenedSockets( nbMaxSockets );

       this.configList = configList;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** This method is called automatically when a new client establishes a connection
    *  with this server ( the client sends a ClientRegisterMessage ).
    *
    * @param personality a previously created personality for this connection.
    * @param key a string given by the client to identify itself. The key should be
    *        equal to "AccountServerPlease!".
    */
    public void accessControl( NetPersonality personality, String key )
    {
       // The key is there to prevent wrong connections

          if( key.equals("GatewayServerPlease!") )
          {
            // ok, let's create an AccountTransaction for the operation.
               AccountTransaction transaction = new AccountTransaction(
                                                AccountTransaction.TRANSACTION_ACCOUNT_RECEIVER );

            // we set his message context to his player...
               personality.setContext( transaction );
               personality.setConnectionListener( transaction );

            // welcome on board...
               acceptClient( personality );
               Debug.signal( Debug.NOTICE, this, "Gateway Server is receiving an account...");
          }
/*        if( key.startsWith("isThereAnAccountNamed:") )
          {
              String primaryKey = key.substring( key.indexOf(':')+1, key.length() );
              
           // does this client exists ?
              AccountManager manager = DataManager.getDefaultDataManager().getAccountManager();

              if( manager.checkAccountName( primaryKey ) )
                  refuseClient( personality, primaryKey+":found" ); // yes the client exists
              else {
                  Debug.signal( Debug.WARNING, "The account searched ("+primaryKey+") was not found." );
                  refuseClient( personality, primaryKey+":not found");
              }
          }
*/        else {
            // NO VALID KEY
               Debug.signal( Debug.NOTICE, this, "Someone tried to connect with a bad key : "+key);
               refuseClient( personality, "Wrong key for this server :"+key );
          }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To transfert an account to a remote server...
    *  @param account account to transfert
    *  @param remoteServerID remote server ID.
    */
    public boolean transfertAccount( String accountPrimaryKey, int remoteServerID ) {

          Debug.signal( Debug.NOTICE, null, "Starting "+accountPrimaryKey+" account transfert to server "+remoteServerID+"." );

       // STEP 1 - Get the remote server config
          ServerConfig remoteServer = configList.getServerConfig( remoteServerID );

          if(remoteServer==null) {
             Debug.signal(Debug.ERROR, this, "Server Config "+remoteServerID+" not found !" );
             return false;
          }

       // STEP 2 - Creation of an AccountTransaction
          AccountTransaction transaction = new AccountTransaction(
                                               AccountTransaction.TRANSACTION_ACCOUNT_SENDER );

       // STEP 3 - Open a connection with the remote server
          NetClient client = new NetClient();
          NetPersonality personality = client.connectToServer(
                                          remoteServer.getServerName(),
                                          remoteServer.getGatewayServerPort(),
                                          "GatewayServerPlease!",
                                          transaction, null );

          if(personality==null) {
             Debug.signal( Debug.ERROR, this, client.getErrorMessage() );
             return false;
          }

          personality.setConnectionListener( transaction );
       
       // STEP 4 - Wait for the result (10s max)
          return transaction.transfertAccount( accountPrimaryKey, 10000, ServerDirector.getServerID() );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
