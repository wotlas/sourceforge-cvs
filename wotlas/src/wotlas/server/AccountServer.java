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
import wotlas.libs.net.NetPersonality;

import wotlas.utils.Debug;
import wotlas.utils.FileTools;

import java.io.IOException;
import java.util.Properties;

/** Wotlas Account Server. Its role is to wait clients and connect them to a new
 *  AccountBuilder. An AccountBuilder will help the client to create a GameAccount.<p>
 *
 *  For more information on how it works see {@see AccountBuilder AccountBuilder }.
 *
 * @author Aldiss
 * @see wotlas.server.AccountBuilder
 */

public class AccountServer extends NetServer
{
 /*------------------------------------------------------------------------------------*/

   /** Static Link to Account Server Config File.
    */
    public final static String ACCOUNT_CONFIG = "../src/config/account-server.cfg";

 /*------------------------------------------------------------------------------------*/

  /** Client Counter
   */
      private int clientCounter;      

 /*------------------------------------------------------------------------------------*/

  /** Constructor (see wotlas.libs.net.NetServer for details)
   *
   *  @param host the host interface to bind to. Example: wotlas.tower.org
   *  @param server_port port on which the server listens to clients.
   *  @param msg_packages a list of packages where we can find NetMsgBehaviour Classes.
   *  @param nbMaxSockets maximum number of sockets that can be opened on this server
   */
    public AccountServer( String host, int port, String packages[], int nbMaxSockets ) {
       super( host, port, packages );
       setMaximumOpenedSockets( nbMaxSockets );
     
     
     // we load the clientCounter from the ACCOUNT_CONFIG
        Properties props = FileTools.loadPropertiesFile( ACCOUNT_CONFIG );

        if(props==null) {
           // file not found, we create one...
              Debug.signal( Debug.WARNING, this, "Could not find file: "+ACCOUNT_CONFIG
                            +"\n   Creating a new account-server.cfg file..." );

              props = new Properties();
              props.setProperty( "clientCounter", "0" );
              clientCounter=0;
              
              if( !FileTools.savePropertiesFile( props, ACCOUNT_CONFIG, "Do not remove or modify this file !") ){
                  Debug.signal( Debug.FAILURE, this,"Cannot create or get account-server.cfg file!");
                  Debug.exit();
              }
        }
        else {
             try{
                clientCounter = Integer.parseInt( props.getProperty( "clientCounter" ) )+1;
                Debug.signal( Debug.NOTICE, null, "AccountServer Client Counter set to "+clientCounter+"." );
             }
             catch( Exception e ){
                  Debug.signal( Debug.FAILURE, this,"Bad account-server.cfg clientCounter property!");
                  Debug.exit();
             }
        }
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

          if( key.equals("AccountServerPlease!") )
          {
            // ok, let's create an AccountBuilder for this future client.
               AccountBuilder accountBuilder = new AccountBuilder(this);

            // we set his message context to his player...
               personality.setContext( accountBuilder );
               personality.setConnectionListener( accountBuilder );

            // welcome on board...
               acceptClient( personality );
               Debug.signal( Debug.NOTICE, this, "A new client is building a GameAccount...");
          }
          else {
               Debug.signal( Debug.NOTICE, this, "A client tried to enter the AccountServer with a wrong key :"+key);
               refuseClient( personality, "Wrong key for this server :"+key );
          }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    synchronized public int getNewLocalClientID() {
        clientCounter++;

      // save this state in config/accountServer.cfg
         Properties props = new Properties();
         props.setProperty( "clientCounter", ""+clientCounter );
              
         if( !FileTools.savePropertiesFile( props, ACCOUNT_CONFIG, "Do not remove or modify this file !") ){
             Debug.signal( Debug.CRITICAL, this,"Cannot save clientCounter (="
                           +clientCounter+") to account-server.cfg file!");
         }

      // we return the new value
         return clientCounter;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
