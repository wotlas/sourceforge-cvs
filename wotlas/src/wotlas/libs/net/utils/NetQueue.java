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


package wotlas.libs.net.utils;

import wotlas.libs.net.*;

/** 
 * A simple queue of NetMessageBehaviour for synchronous calls.
 * For general use... This class is not used by other classes of the net packages.
 *
 * @author aldiss
 */

public class NetQueue
{
  /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   // Our table of NetMessageBehaviour
      private NetMessageBehaviour table[];

   // Nb of entries in our table of objects
      private int nbEntries;
      
   // default size
      private int defaultSize;

   // grow factor
      private int growthFactor;

 /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Constructor with default size and growth factor.
    *
    * @param defaultSize default size of queue
    * @param growthFactor number we add to the queue's size when resizing.
    */
      public NetQueue( int defaultSize, int growthFactor ) {
         this.defaultSize = defaultSize;
         this.growthFactor = growthFactor;

         table = new NetMessageBehaviour[defaultSize];
         nbEntries = 0;
      }

 /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To queue a message.
    *
    * @param msg message to add
    */
      public synchronized void queueMessage( NetMessageBehaviour msg ) {
         // re-allocation ?
            if( table.length<=nbEntries ) {
                NetMessageBehaviour tableSwap[] = new NetMessageBehaviour[nbEntries+growthFactor];
                System.arraycopy( table, 0, tableSwap, 0, nbEntries );
                table = tableSwap;
            }

           table[nbEntries] = msg;
           nbEntries++;
     }

 /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To retrieve all the messages from the queue and get a table that has the same
    *  size as the number of entries.
    *
    * @return message table (if it's empty, pullMessages().length = 0 )
    */
     public synchronized NetMessageBehaviour[] pullMessages() {

           if(nbEntries==0) return new NetMessageBehaviour[0];

        // we swap the tables :
           NetMessageBehaviour tableSwap[] = new NetMessageBehaviour[nbEntries];
           System.arraycopy( table, 0, tableSwap, 0, nbEntries );
 
           table =new NetMessageBehaviour[defaultSize];
           nbEntries = 0;
           return tableSwap;
     }

 /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
