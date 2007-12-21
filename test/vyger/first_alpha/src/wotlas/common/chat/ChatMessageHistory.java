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
 
package wotlas.common.chat;


/** A chat message history utility.
 *
 * @author Petrus, Aldiss
 */

public class ChatMessageHistory {

 /*------------------------------------------------------------------------------------*/
  
   /** Number of history entries.
    */
    private static final int HISTORY_MAX_ENTRIES = 15;

 /*------------------------------------------------------------------------------------*/

  /** Messages History
   */
    private String history[];

  /** Current index
   */
    private short index;
  
 /*------------------------------------------------------------------------------------*/  

  /** Constructor
   */
   public ChatMessageHistory() {
   	history = new String[HISTORY_MAX_ENTRIES];
   	index=0;
   }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To add a message to the message history. Eventual 'next' messages are erased.
   */
   public void add(String message) {
      // we erase eventual 'next entries'...
   	for( short i=(short)(index+1); i<HISTORY_MAX_ENTRIES; i++ )
             history[i]=null;

        history[index] = message;
        index++;

      // end of history ?
        if(index==HISTORY_MAX_ENTRIES) {
           // we erase the first saved message
              for( short i=0; i<HISTORY_MAX_ENTRIES-1; i++)
                   history[i]=history[i+1];

              index=HISTORY_MAX_ENTRIES-1;
              history[index]=null;
        }
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the next message if there is one. If there is none the current message is
   *  returned.
   */
   public String getNext(String currentMessage) {
   	if(index==HISTORY_MAX_ENTRIES-1 || history[index+1]==null)
   	   return currentMessage;

        index++;
        return history[index];
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the previous message if there is one. If there is none the current message is
   *  returned. The current message must be given and will be saved at the current index
   *  (only if the current index is the last one of the history).
   */
   public String getPrevious(String currentMessage) {
   	if(index==0 || history[index-1]==null) // security (history[index-1]==null) should never occur
   	   return currentMessage;

        if( history[index]==null || (index!=HISTORY_MAX_ENTRIES-1 && history[index+1]==null) )
           history[index]=currentMessage;

        index--;
        return history[index];
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}