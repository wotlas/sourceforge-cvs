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

package wotlas.client.message.chat;

import java.io.IOException;
import java.util.*;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.chat.*;

import wotlas.client.*;
import wotlas.client.screen.*;

import wotlas.common.chat.*;
import wotlas.common.Player;

import wotlas.utils.*;

/**
 * Associated behaviour to the SendTextMessage (on the client side)...
 *
 * @author Petrus
 */

public class SendTextMsgBehaviour extends SendTextMessage implements NetMessageBehaviour
{

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public SendTextMsgBehaviour() {
      super();
    }

 /*------------------------------------------------------------------------------------*/
  
  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
    public void doBehaviour( Object context ) {
       // The context is here a DataManager.
          DataManager dataManager = (DataManager) context;
          PlayerImpl player = dataManager.getMyPlayer();          
          
       // Private message
          if (message.startsWith("/msg:")) {
            JChatRoom chatRoom = dataManager.getChatPanel().getJChatRoom(chatRoomPrimaryKey);
            chatRoom.appendText("<font color='brown'><i> ** [" + senderFullName + "] " + message.substring(5) + " ** </i></font>");
            return;
          }
            
       // We get the sender of this message
          Hashtable players = dataManager.getPlayers();
          PlayerImpl sender = null;
          
          if(players!=null)
             sender = (PlayerImpl) players.get( senderPrimaryKey );

          if( sender==null )
              Debug.signal( Debug.WARNING, this, "Couldnot find the sender of this message : "+senderPrimaryKey);

          if (message.startsWith("/me")) {
            message = "<font color='blue'><i>" + senderFullName + " " + message.substring(3) + "</i></font>";
          } else {
            // We add sender name
            message = "["+senderFullName+"] " + message;          
          }
           
       // We display the message
          if( voiceSoundLevel!=ChatRoom.SHOUTING_VOICE_LEVEL ) {
              JChatRoom chatRoom = dataManager.getChatPanel().getJChatRoom(chatRoomPrimaryKey);
          
              if(chatRoom!=null) {
                 chatRoom.addPlayer(senderPrimaryKey,senderFullName);   // we add the player to the member's list
                switch(voiceSoundLevel) {                  
                  case ChatRoom.WHISPERING_VOICE_LEVEL:
                    chatRoom.appendText("<font color='gray'>"+message+"</font>"); // if it wasn't already the case
                    break;
                  case ChatRoom.NORMAL_VOICE_LEVEL:
                    chatRoom.appendText(message);
                    break;
                 }
              }
              else
                 Debug.signal( Debug.ERROR, this, "No JChatRoom "+chatRoomPrimaryKey+" found !");
          }
          else {
              JChatRoom chatRoom = dataManager.getChatPanel().getCurrentJChatRoom();
              chatRoom.appendText("<font color='red'>"+message+"</font>"); // if it wasn't already the case              
          }

    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}
  
