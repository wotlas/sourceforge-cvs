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

import wotlas.utils.Debug;

import java.util.Hashtable;
import java.util.Iterator;

/** This class processes the available chat commands. In wotlas there is only one
 * instance of this class held by the default server DataManager.
 *
 * @author Aldiss
 */

public class ChatCommandProcessor
{
 /*------------------------------------------------------------------------------------*/

  /** Our chat commands table where we store commands by their prefix name.
   */
    private Hashtable commands;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public ChatCommandProcessor() {
    	commands = new Hashtable(20);
    }

 /*------------------------------------------------------------------------------------*/

  /** To init the processor with available commands.
   */
    public void init() {

       /**
        **  DECLARE HERE YOUR COMMANDS.
        **
        **  Note : this manual load is temporary, when I'll have time I'll implement
        **  a simple dynamic class download method and suppress manual declaration.
        **/
           addChatCommand( new WhoChatCommand() );
           addChatCommand( new BlackAjahChatCommand() );
           addChatCommand( new MsgChatCommand() );
           addChatCommand( new FindChatCommand() );
           addChatCommand( new ServerChatCommand() );
           addChatCommand( new LogChatCommand() );
           addChatCommand( new InfoChatCommand() );
           addChatCommand( new HelpChatCommand() );
           addChatCommand( new BellChatCommand() );
           addChatCommand( new FanfareChatCommand() );
           addChatCommand( new KnockChatCommand() );
    }

 /*------------------------------------------------------------------------------------*/

   /** Adds a new chat command to our table.
    */
    private void addChatCommand( ChatCommand newCommand ) {

          if( commands.containsKey( newCommand.getChatCommandPrefix() ) ) {
              Debug.signal( Debug.ERROR, this, "Command already exists ! "+newCommand );
              return;
          }

          commands.put( newCommand.getChatCommandPrefix(), newCommand );
    }

 /*------------------------------------------------------------------------------------*/

   /** Method called to execute the given command.
    *
    *  @param commandName command name as it was given to you by the getCommandName() method.
    *  @param message the string containing the chat command.
    *  @param player the player on which the command is executed
    *  @param response to use to send the result of the command to the client
    *  @return true if the message process is finished, false if this command was
    *          a 'modifier' to modify the rest of the message process.
    */
      public boolean processCommand( String message, PlayerImpl player, SendTextMessage response ) {

    	// 1 - Search for an eventual separator ':'
           int separator = message.indexOf(':');
           String commandName;

           if(separator<0)
              commandName = message.trim();
           else
              commandName = message.substring(0,separator).trim();

        // 2 - test existence
           if( !commands.containsKey(commandName) ) {
               //response.setMessage("/cmd:Command error :<font color='red'> '"+message+"' not found</font>");
               //player.sendMessage(response);
               return false;
           }
      	
      	 // 3 - we run the command
           ChatCommand command = (ChatCommand) commands.get(commandName);

           if( response.getVoiceSoundLevel()!=command.getChatCommandVoiceSoundLevel() )
               return false; // not the right sound level for this command !

           return command.exec(message,player,response);
      }

 /*------------------------------------------------------------------------------------*/

    /** To get the list of public commands
     *  @param seeHidden set to true to display all commands
     */
      public String getCommandList( boolean seeHidden ) {

         StringBuffer list = new StringBuffer("<div align='center'><table width='60%' border='0' bgcolor='#B4D0EF'>");
         int cellCounter = 0;
      	 Iterator it = commands.values().iterator();
      	 
      	 synchronized( commands ) {

             while( it.hasNext() ) {
             	ChatCommand command = (ChatCommand) it.next();

                if( command.isHidden() && !seeHidden ) continue;

                if(cellCounter%4==0) list.append("<tr>");

                list.append("<td><div align='center'>"+command.getChatCommandPrefix()+"</div></td>");

                if(cellCounter%4==3) list.append("</tr>");
             	cellCounter++;
             }
      	 }

         if(cellCounter%4==1) list.append("<td></td><td></td><td></td></tr></table></div>");
         else if(cellCounter%4==2) list.append("<td></td><td></td></tr></table></div>");
         else if(cellCounter%4==3) list.append("<td></td></tr></table></div>");
         else list.append("</table></div>");

         return list.toString();
      }

 /*------------------------------------------------------------------------------------*/

    /** To get the documentation of a command.
     * @param commandName command name
     * @return null if the command doesn't exist, the documentation otherwise.
     */
      public String getCommandDocumentation( String commandName ) {

           if( !commands.containsKey(commandName) )
               return null;

           ChatCommand command = (ChatCommand) commands.get(commandName);
           return command.getCommandDocumentation();
      }

 /*------------------------------------------------------------------------------------*/

}
