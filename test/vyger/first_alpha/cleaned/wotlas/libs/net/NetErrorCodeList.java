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
 * Defines error codes for the network library. If you want to add error codes for your
 * server's refuseClient() method just extend this interface.
 * 
 * @author Aldiss
 */

public interface NetErrorCodeList {
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Net Library Error Codes (Server Side) - reserves code 0 - 99
     */
    public final static short ERR_NONE = 0; // no error occured
    public final static short ERR_MAX_CONN_REACHED = 1; // server max number of connections reached
    public final static short ERR_ACCESS_LOCKED = 2; // server access is temporary locked
    public final static short ERR_BAD_LIB_VERSION = 3; // server has not the same library version

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Net Library Error Codes (Client Side) - reserves code 100 - 199
     */
    public final static short ERR_CONNECT_FAILED = 100; // server has unknown address
    public final static short ERR_CONNECT_CANCELED = 101; // connect operation was canceled

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
