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

import wotlas.client.DataManager;
import wotlas.client.PlayerImpl;

import wotlas.common.chat.*;
import wotlas.common.Player;

import wotlas.utils.*;

/**
 * Associated behaviour to the ChatRoomCreatedMessage...
 *
 * @author Petrus
 */

public class ChatRoomCreatedMsgBehaviour extends ChatRoomCreatedMessage implements NetMessageBehaviour
{

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  public ChatRoomCreatedMsgBehaviour() {
    super();
  }

 /*------------------------------------------------------------------------------------*/

  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
  public void doBehaviour( Object context ) {
       System.out.println("ChatRoomCreatedMsgBehaviour:"+primaryKey);

    // The context is here a DataManager.
       DataManager dataManager = (DataManager) context;
       PlayerImpl player = dataManager.getMyPlayer();

    // We seek for the creator of this chat...
       Hashtable players = dataManager.getPlayers();
       PlayerImpl sender = null;

       if(players!=null)
          sender = (PlayerImpl) players.get( creatorPrimaryKey );

       if( sender==null )
           Debug.signal( Debug.WARNING, this, "Could not find the sender of this message : "+creatorPrimaryKey);

     // We create the new chat
       ChatRoom chatRoom = new ChatRoom();
       chatRoom.setPrimaryKey(primaryKey);
       chatRoom.setName(name);
       chatRoom.setCreatorPrimaryKey(creatorPrimaryKey);

       dataManager.getChatPanel().addJChatRoom(chatRoom);
       
       if( player.getPrimaryKey().equals( creatorPrimaryKey ) ) {
       	 // We created this chat !
            boolean success = dataManager.getChatPanel().setCurrentJChatRoom( primaryKey );

            if( success )
                dataManager.getChatPanel().addPlayer(primaryKey, player);
            else
                Debug.signal( Debug.ERROR, this, "Failed to create owner's new ChatRoom");
       }
       else {
       	 // someone else created the chatroom
            dataManager.getChatPanel().setEnabledAt(primaryKey,false);
       }
       
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

