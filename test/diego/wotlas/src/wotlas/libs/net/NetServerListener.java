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
 * A NetServerListener receives information on the server network interface signaling
 * when it's down or up. This informations are sent regularly as the server socket
 * has a SO timeout of 5 seconds.
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetConnection
 */

public interface NetServerListener {
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when the server network interface is down.
   * @param itf the network interface we tried which is NOT available.
   */
     public void serverInterfaceIsDown( String itf );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when the server network interface is Up.
   * @param ipAddress currently used IP address
   * @param stateChanged if true it means the interface has been re-created ( ip changed or
   *        serverSocket has just been created ). If false it means that the interface is Up
   *        and its state has not changed (since last check).
   */
     public void serverInterfaceIsUp( String ipAddress, boolean stateChanged );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

