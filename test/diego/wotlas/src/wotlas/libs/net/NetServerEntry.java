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
 
package wotlas.libs.net;

/** 
 * A NetServerEntry is the association { NetServer, NetConnection }
 * It is only used in the NetServer and some server messages.
 * You should not be concerned with the use of this class.
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetServer
 */

public class NetServerEntry
{

 /*------------------------------------------------------------------------------------*/

  /** Our Server
   */
      private NetServer server;

  /** Our Connection.
   */
      private NetConnection connection;

 /*------------------------------------------------------------------------------------*/

  /** Constructor of this association
   *
   * @param server our server 
   * @param connection our connection
   */
     public NetServerEntry( NetServer server, NetConnection connection ) {
         this.server = server;
         this.connection = connection;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the NetServer.
   * 
   * @return the NetServer
   */
     public NetServer getServer() {
           return server;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the NetConnection
   * 
   * @return the NetConnection
   */
     public NetConnection getConnection() {
           return connection;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

