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

package wotlas.libs.net;


/** 
 * A NetPingListener listens to network ping information. Use the NetPersonality methods
 * to register to this listener.
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetPersonality
 */

public interface NetPingListener
{
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To tell that the network connection is no longer available
   */
     public final static int PING_CONNECTION_CLOSED = -2;

  /** To tell that the last ping failed.
   */
     public final static int PING_FAILED = -1;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when some ping information is available.
   *
   * @param ping if >=0 it's a valid ping value, if == PING_FAILED it means the
   *        last ping failed, if == PING_CONNECTION_CLOSED it means the connection
   *        has been closed.
   */
     public void pingComputed( int ping );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

