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

package wotlas.server.message.chat;

import java.io.IOException;
import java.util.*;

import wotlas.libs.net.NetMessageBehaviour;
import wotlas.common.message.chat.*;

import wotlas.common.Player;
import wotlas.common.universe.*;

import wotlas.server.DataManager;
import wotlas.server.PlayerImpl;

import wotlas.utils.Debug;

/**
 * Associated behaviour to the ChatRoomCreationMessage...
 *
 * @author Petrus
 */

public class ChatRoomCreationMsgBehaviour extends ChatRoomCreationMessage implements NetMessageBehaviour
{

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  public ChatRoomCreationMsgBehaviour() {
    super();
  }
  
  /** Associated code to this Message...
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
  public void doBehaviour( Object context ) {
    // The context is here a PlayerImpl.
    PlayerImpl player = (PlayerImpl) context;

    System.out.println("public ChatRoomCreationMsgBehaviour()");
    String primaryKey = name+creatorPrimaryKey;
    System.out.println("primaryKey = " + primaryKey);
    System.out.println("name = " + name);
    System.out.println("creatorPrimaryKey = " + creatorPrimaryKey);
    ChatRoomCreatedMessage crcMsg = new ChatRoomCreatedMessage(primaryKey, name, creatorPrimaryKey);
    player.sendMessage(crcMsg);
    
    // We send the information to all players of the same room or town or world
    WotlasLocation location = player.getLocation();
    
    Hashtable players;
    
    if ( location.isWorld() ) {
      WorldMap world = DataManager.getDefaultDataManager().getWorldManager().getWorldMap(location);
      if (world!=null)
        players = world.getPlayers();
      else
        return;
    } else if ( location.isTown() ) {
      TownMap town = DataManager.getDefaultDataManager().getWorldManager().getTownMap(location);
      if (town!=null)
        players = town.getPlayers();
      else
        return;
    } else if ( location.isRoom() ) {
      Room currentRoom = player.getMyRoom();    
      if (currentRoom==null) {
        Debug.signal( Debug.ERROR, this, "Error could not get current room ! "+player.getLocation() );
        player.sendMessage( new wotlas.common.message.account.WarningMessage("Error could not get current room ! "+player.getLocation()) );
        return;
      }
      players = currentRoom.getPlayers();
    } else {
      return;
    }
    
    synchronized(players) {
      Iterator it = players.values().iterator();
      PlayerImpl p;
              	 
      while ( it.hasNext() ) {
        p = (PlayerImpl)it.next();
        if (p!=player) {
          System.out.println("To player "+p+":");
          p.sendMessage( crcMsg );
        }
      }
    }
    
  }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}
  
