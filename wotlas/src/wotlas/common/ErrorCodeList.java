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

package wotlas.common;

import wotlas.libs.net.NetErrorCodeList;

/** 
 * Defines error codes for the wotlas servers & client.
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetErrorCodeList
 */

public interface ErrorCodeList extends NetErrorCodeList
{
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Net Library Error Codes (Server Side) - reserves code 0 - 99
   *  declared in NetErrorCodeList.
   */

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Net Library Error Codes (Client Side) - reserves code 100 - 199
   *  declared in NetErrorCodeList.
   */

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Wotlas Server Error Codes (AccountServer, GameServer, GatewayServer) - reserves code 200 - 299
   *  declared in NetErrorCodeList.
   */
    public final static short ERR_BAD_REQUEST       = 200;  // some data is missing in the request
    public final static short ERR_UNKNOWN_ACCOUNT   = 201;  // the account does not exist on this server
    public final static short ERR_BAD_PASSWORD      = 202;  // the client entered a wrong password
    public final static short ERR_DELETE_FAILED     = 203;  // the delete operation failed
    public final static short ERR_ACCOUNT_DELETED   = 204;  // the account has been deleted
    public final static short ERR_WRONG_KEY         = 205;  // the client gave a wrong key for this server
    public final static short ERR_ALREADY_CONNECTED = 206;  // someone is already connected to this account
    public final static short ERR_DEAD_ACCOUNT      = 207;  // if the player of the account has been killed in the game

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

