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
<COMPLETE>
<UPDATE POSSIBILITY>

import wotlas.libs.net.NetServer;
import wotlas.libs.net.NetPersonality;

import wotlas.utils.Debug;


/** Wotlas Game Server. Its role is to wait client and connect them to the
 *  game. A client must have previously created a GameAccount with the AccountServer.<br>
 *
 * @author Aldiss
 */

public class GameServer extends NetServer
{

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor (see wotlas.libs.net.NetServer for details)
   *
   *  @param host the host interface to bind to. Example: wotlas.tower.org
   *  @param server_port port on which the server listens to clients.
   *  @param msg_packages a list of packages where we can find NetMsgBehaviour Classes.
   */
    public GameServer( String host, int port, String packages[] ) {
       super( host, port, packages );
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
    public void accessControl( NetPersonality personality, String key )
    {
      try{
         // we retrieve the key data "accountName:password"
            String accountName = key.substring( 0, key.indexOf(':') );
            String password = key.substring( key.indexOf(':')+1, key.length );

         // does this client exists ?
            AccountManager manager = DataManager.getDefaultDataManager().getAccountManager();
            GameAccount account = manager.getAccount( accountName );

            if( account==null ) {
                 Debug.signal( Debug.NOTICE, this, "A client tried to connect on a non-existent account.");
                 refuseClient( personality, "This account doesnot exist on this server" );
            }

         // Password Crack Detection ( dictionnay attack )
            if( account.tooMuchBadPasswordEntered() ) {
                 Debug.signal( Debug.WARNING, this, "A client has entered 3 bad passwords.! account locked for 30s");
                 refuseClient( personality, "Sorry, you entered 3 bad passwords ! your account is locked for 30s." );
            }

         // The account exists... but do we have the right password ?
            if( account.isRightPassword( password ) )
            {
              // ok, client accepted...
                 account.setLastConnectionTimeNow();

              // we set his message context to his player...
                 personality.setContext( account.getPlayer() );
                 personality.setConnectionListener( account.getPlayer() );

              // welcome on board...
                 acceptClient( personality );
                 Debug.signal( Debug.NOTICE, this, "A new player entered the game...");
            }
            else {
                 Debug.signal( Debug.NOTICE, this, "A client entered a bad password");
                 refuseClient( personality, "mauvais mot de passe :"+key );
            }
      }
      catch(IOException e) {
             Debug.signal( Debug.WARNING, this, e );

          // just to be absolutely sure the connection is closed...
             try{
                 refuseClient( personality, "Access Control Failed." );
             }
             catch( IOException e )
             { /* expected */ }
      }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
