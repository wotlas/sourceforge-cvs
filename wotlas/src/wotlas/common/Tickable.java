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

/** Interface to signal that a class can work synchronously  : the tick() method can be called
 *  regularly. A root class is the leader and has to invoke the tick() method on all the
 *  tickable classes he knows. Then every tickable class propagate the tick() to their tickable
 *  fields and so on... 
 *
 *  In Wotlas the client side works with a tick implementation but does not have a synchronous
 *  behaviour ( network operations and awt events are still asynchronous ). We use the "tickable"
 *  approach as it is a convenient way to design periodic tasks.
 *
 * @author Aldiss
 */

public interface Tickable
{
 /*------------------------------------------------------------------------------------*/

   /** Method called to signal the new process "tick" to your class.
    */
      public void tick();

 /*------------------------------------------------------------------------------------*/
}