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

package wotlas.server.chat;

import wotlas.server.*;
import wotlas.common.message.chat.SendTextMessage;
import wotlas.common.chat.ChatRoom;
import wotlas.common.universe.*;

import java.util.HashMap;
import java.util.Iterator;

/** "/who" chat command. To get the list of connected players.
 *
 * @author Aldiss
 */

public class WhoChatCommand implements ChatCommand
{
 /*------------------------------------------------------------------------------------*/

   /** Returns the first part of the chat command. For example if your chat command
    *  has the following format '/msg:playerId:message' the prefix is '/msg' ( note
    *  that there is ':' at the end).
    *  Other example : if your command is '/who' the prefix is '/who'. 
    *
    * @return the chat command prefix that will help identify the command.
    */
      public String getChatCommandPrefix() {
      	 return "/who";
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
      	 return false;
      }

 /*------------------------------------------------------------------------------------*/

   /** To get information on this command.
    * @return command full documentation.
    */
      public String getCommandDocumentation() {
      	return "<font size='4'> Command 'who'</font>" +
      	       "<br><b> Syntax :</b> /who " +
      	       "<br><b> Voice  :</b> normal voice level " +
      	       "<br><b> Descr  :</b> displays the list of connected players." +
      	       "<br><b> Example:</b> none." ;
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

          HashMap onlinePlayers = DataManager.getDefaultDataManager().getAccountManager().getOnlinePlayers();

          Iterator it = onlinePlayers.values().iterator();
          StringBuffer result = new StringBuffer("/cmd:There are <b>"+onlinePlayers.size() + "</b> online players on this server :");
          result.append("<table border='0' bgcolor='#EDE4FF'>");

          boolean keys = message.equals("/who keys");

          PlayerImpl onlinePlayer;

          while ( it.hasNext() ) {
             onlinePlayer = (PlayerImpl) it.next();
             if(keys)
                result.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp; - " + onlinePlayer.getPlayerName() + " &nbsp; <i> ( " + onlinePlayer.getPrimaryKey() + " )</i></td></tr>" );
             else {
                String slocation = null;
                WotlasLocation flocation = player.getLocation();

                if( flocation.isRoom() )
                    flocation.setBuildingID(-1);

                if( flocation.isTown() ) {
                    TownMap t = DataManager.getDefaultDataManager().getWorldManager().getTownMap(flocation);
                    if(t!=null)
                       slocation = t.getFullName();
                }
                else if( flocation.isWorld() ) {
                    WorldMap w = DataManager.getDefaultDataManager().getWorldManager().getWorldMap(flocation);
                    if(w!=null)
                       slocation = w.getFullName();
                }
                else
              	  slocation = "bad location";

                result.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp; - " + onlinePlayer.getPlayerName() + " &nbsp; <i> ( in " +slocation+ " )</i></td></tr>" );
             }
          }

          result.append("</tr></table>");
          response.setMessage( result.toString() );
          player.sendMessage( response );
          return true;
      }

 /*------------------------------------------------------------------------------------*/
}