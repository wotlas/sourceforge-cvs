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
 
package wotlas.libs.net.message;

import java.io.IOException;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.libs.net.NetServerEntry;
import wotlas.libs.net.NetEngineVersion;
import wotlas.libs.net.NetErrorCodeList;

import wotlas.utils.Debug;


/**
 * Associated behaviour to the ClientRegisterMessage...
 *
 * @author Aldiss
 * @see wotlas.libs.net.message.ClientRegisterMessage
 */

public class ClientRegisterMsgBehaviour extends ClientRegisterMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public ClientRegisterMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code to the ClientRegisterMessage...
   *
   * @param sessionContext an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object sessionContext ) {

        // the sessionContext is here an entry on this server ( server + personality )
           NetServerEntry entry = (NetServerEntry) sessionContext;

     	// this message is the first one sent by a client 
     	// when he establishes a new connection. Arrived on the server
        // we check that we have the same NetEngineVersion.
           if( netEngineVersion != NetEngineVersion.VERSION )
           {
                if( netEngineVersion > NetEngineVersion.VERSION )
                {
                     entry.getPersonality().queueMessage(
                           new ServerErrorMessage( NetErrorCodeList.ERR_BAD_LIB_VERSION,
                                                   "The Server Network Engine Version is old : "
                                                   + NetEngineVersion.VERSION
                                                   + ". Please Signal it ! You have version "
                                                   + netEngineVersion ) );

                     entry.getPersonality().closeConnection();

                     Debug.signal( Debug.WARNING, this,
                                     "Client tried to connect with a more recent network engine version :"
                                      +netEngineVersion+". This server has version "
                                      +NetEngineVersion.VERSION );
                }
                else
                {
                       entry.getPersonality().queueMessage(
                           new ServerErrorMessage( NetErrorCodeList.ERR_BAD_LIB_VERSION,
                                                   "You have an old version of the Wotlas Network Engine (v"
                                                        + netEngineVersion + "). Please update to v"
                                                        + NetEngineVersion.VERSION ) );

                       entry.getPersonality().closeConnection();

                       Debug.signal( Debug.WARNING, this,
                                     "Client tried to connect with an old version of the network engine (v"
                                      +netEngineVersion+")." );
                }

              return;
           }

     	// if the version are the same, we call the server's accessControl method.
           entry.getServer().accessControl( entry.getPersonality(), key );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

