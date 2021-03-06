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

package wotlas.client.message.chat;

import java.io.IOException;
import java.util.*;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.libs.sound.*;
import wotlas.common.message.chat.*;
import wotlas.common.character.*;
import wotlas.common.PlayerState;

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
public class SendTextMsgBehaviour extends SendTextMessage implements NetMessageBehaviour {

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public SendTextMsgBehaviour() {
        super();
    }

 /*------------------------------------------------------------------------------------*/
  
  /** Associated code to this Message...
   *
   * @param sessionContext an object giving specific access to other objects needed to process
   *        this message.
   */
    public void doBehaviour( Object sessionContext ) {

       // The sessionContext is here a DataManager.
          DataManager dataManager = (DataManager) sessionContext;
          PlayerImpl player = dataManager.getMyPlayer();
          boolean fanfare = false;
          
       // Return of a command ?
          if (message.startsWith("/cmd:")) {
            JChatRoom chatRoom = dataManager.getClientScreen().getChatPanel().getJChatRoom(chatRoomPrimaryKey);
            chatRoom.appendText("<font color='purple'>"+message.substring(5)+"</font>");
            return;
          }
          else if( message.equals("/BELL") ) {
             message = "/me rings a bell...";
             SoundLibrary.getSoundPlayer().playSound("bell.wav");
          }
          else if( message.startsWith("/FANFARE") ) {             
             int index = message.indexOf(' ');
             if(index<0 || index==message.length()-1) return;

             String soundFileName = message.substring(index+1,message.length()).toLowerCase();
             SoundLibrary.getSoundPlayer().playSound(soundFileName);
             message = "/me sounds the fanfare !";
             fanfare=true;
          }
          else if( message.equals("/KNOCK") ) {
             message = "/me knocks at the door...";
             SoundLibrary.getSoundPlayer().playSound("knock.wav");
          }


       // We get the sender of this message
          Hashtable players = dataManager.getPlayers();
          PlayerImpl sender = null;

          if(players!=null)
             sender = (PlayerImpl) players.get( senderPrimaryKey );

          if( sender==null )
              Debug.signal( Debug.WARNING, this, "Couldnot find the sender of this message : "+senderPrimaryKey);
          else {
              if(sender.getBasicChar() instanceof DarkOne && fanfare)
                 return;
              dataManager.addWaveDrawable(sender);
              senderFullName = sender.getFullPlayerName();
          }

       // Is there a modifier in this message ?
          if(message.equals("/BLACKAJAH") && voiceSoundLevel==ChatRoom.SHOUTING_VOICE_LEVEL) {
             BasicChar wotC = sender.getBasicChar();
       
             if( wotC instanceof AesSedai ) {
                 if( ((AesSedai) wotC).toggleBlackAjah() )
                     dataManager.getClientScreen().getChatPanel().getCurrentJChatRoom().appendText("<font color='black'><b>[DARK ONE]<i> NOW YOU ARE MINE "
                         +sender.getPlayerName().toUpperCase()+" !</i></b></font>");
                 else
                     dataManager.getClientScreen().getChatPanel().getCurrentJChatRoom().appendText("<font color='black'><b>[DARK ONE]<i> YOU CAN'T HIDE FROM ME. YOUR SOUL IS MINE "
                         +sender.getPlayerName().toUpperCase()+".</i></b></font>");
             }

             return;
          }
          else if (message.startsWith("/me")) {
            message = "<font color='blue'><i>" + senderFullName + " " + message.substring(3) + " </i></font>";
          }
          else if (message.startsWith("/away")) {
            message = message.substring(5);
            if (message.length()==0) {
              // no parameter : player is no longer away
              sender.getPlayerState().value = PlayerState.CONNECTED;
              message = "<font color='green'><i>" + senderFullName + " is back</i></font>";
            } else {
              // player is away
              sender.getPlayerState().value = PlayerState.AWAY;
              sender.setPlayerAwayMessage(message);
              message = "<font color='green'><i>" + senderFullName + " is away (" + message + " )</i></font>";
              
            }
            dataManager.getClientScreen().getChatPanel().updateAllChatRooms((Player) sender);
          }          
          else if (message.startsWith("/to:")) {
            message = message.substring(4);
            int index = message.indexOf(':');

            if( index>=0 && index+1<message.length() ) {
               String otherPlayerName = message.substring(0,index);
               message = "<font color='blue'><i>" + senderFullName + " says to " + otherPlayerName + message.substring(index) + "</i></font>";
            }
            else
               message = "/to:" +message+" <font color='red'>ERROR: bad format</font>";
          }
          else if( sender!=null && sender.getBasicChar() instanceof DarkOne ) {
             // display the message in the "dark one manner..."
              message = "<b>[DARK ONE] "+message.toUpperCase()+" </b>";
          }          
          else {
             // We add sender name
                message = "["+senderFullName+"] " + message;
          }


       // We display the message
          if( voiceSoundLevel!=ChatRoom.SHOUTING_VOICE_LEVEL ) {
              JChatRoom chatRoom = dataManager.getClientScreen().getChatPanel().getJChatRoom(chatRoomPrimaryKey);
          
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
              JChatRoom chatRoom = dataManager.getClientScreen().getChatPanel().getCurrentJChatRoom();
              chatRoom.appendText("<font color='red'>"+message+"</font>"); // if it wasn't already the case              
          }

    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}
  
