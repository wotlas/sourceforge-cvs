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

package wotlas.server.chat;

import wotlas.server.*;
import wotlas.common.message.chat.SendTextMessage;
import wotlas.common.chat.ChatRoom;
import wotlas.common.universe.*;

/** "/lie" chat command. To get lieManager informations.
 *
 * @author Petrus
 */

public class LieChatCommand implements ChatCommand
{
 /*------------------------------------------------------------------------------------*/

   /** Returns the first part of the chat command. For example if your chat command
    *  has the following format '/msg playerId message' the prefix is '/msg'.
    *  Other example : if your command is '/who' the prefix is '/who'.
    *
    * @return the chat command prefix that will help identify the command.
    */
      public String getChatCommandPrefix() {
      	 return "/lie";
      }

 /*------------------------------------------------------------------------------------*/

   /** Voice sound level needed to exec this command. While most commands only need to be
    *  be spoken, others need to be shouted or whispered.
    *
    *  @return ChatRoom.WHISPERING_VOICE_LEVEL if the command is to be whispered,
    *          ChatRoom.NORMAL_VOICE_LEVEL  if the command just need to be spoken,
    *          ChatRoom.SHOUTING_VOICE_LEVEL if the command needs to be shout.
    */
      public byte getChatCommandVoiceSoundLevel() {
         return ChatRoom.NORMAL_VOICE_LEVEL;
      }

 /*------------------------------------------------------------------------------------*/

   /** Is this a secret command that musn't be displayed in public commands list ?
    * @return true if secret, false if public...
    */
      public boolean isHidden() {
      	 return true;
      }

 /*------------------------------------------------------------------------------------*/

   /** To get information on this command.
    * @return command full documentation.
    */
      public String getCommandDocumentation() {
      	return "<font size='4'>Command 'lie'</font>" +
      	       "<br><b> Syntax :</b> /lie [playerKey] " +
      	       "<br><b> Voice  :</b> normal voice level " +
      	       "<br><b> Descr  :</b> returns informations on player's fake names." +
      	       "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Use the /who command to find the player key." +
      	       "<br><b> Example:</b> /lie alice-1-34";
      }

 /*------------------------------------------------------------------------------------*/

   /** Method called to execute the command. Just use the response.setMessage() before
    *  sending it (if you have to).
    *
    *  @param message the string containing the chat command.
    *  @param player the player on which the command is executed
    *  @param response to use to send the result of the command to the client
    *  @return true if the message process is finished, false if this command was
    *          a 'modifier' to modify the rest of the message process.
    */
      public boolean exec( String message, PlayerImpl player, SendTextMessage response ) {

              if(message.indexOf(' ')!=4)
                 return true; // no parameters

              message = message.substring(5);

              if(message.length()==0) {
                 response.setMessage("/cmd:/lie command error:<font color='red'> no key entered</font>");
                 player.sendMessage(response);
                 return true;
              }

              GameAccount account = ServerDirector.getDataManager().getAccountManager().getAccount(message);

              if(account==null) {
                 response.setMessage("/cmd:/lie command error:<font color='red'> unknown player</font>");
                 player.sendMessage(response);
                 return true;
              }

              message = "/cmd:";

              LieMemoryIterator memories = account.getPlayer().getLieManager().getMemoriesIterator();
              if (memories==null) {
                message += "memories null";
              } else {
                int k=0;
                LieMemory memory;
                synchronized(memories) {
                  memories.resetIterator();
                  while ( memories.hasNext() ) {
                    memory = memories.next();
                    message += "k=" + k + " otherPlayerKey=" + memory.otherPlayerKey + " meetsNumber=" + memory.meetsNumber +  " otherPlayerFakeNameIndex=" + memory.otherPlayerFakeNameIndex + " lastMeetTime=" + memory.lastMeetTime + "<br>";
                    k++;
                  }
                }
              }

              response.setMessage(message);
              player.sendMessage(response);

          return true;
      }

 /*------------------------------------------------------------------------------------*/
}