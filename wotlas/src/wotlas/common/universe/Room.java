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

package wotlas.common.universe;

import wotlas.common.Player;
import wotlas.utils.Debug;

import java.awt.Point;
import java.util.Hashtable;

 /** Room class
  *
  * @author Petrus
  * @see wotlas.common.universe.RoomLink
  */

public class Room
{
 /*------------------------------------------------------------------------------------*/

  /** ID of the Room (index in the array {@link InteriorMap#rooms InteriorMap.rooms})
   */
   private int roomID;

  /** Full name of the World
   */
   private String fullName;

  /** Short name of the World
   */
   private String shortName;

  /** Point of insertion (teleportation, arrival)
   */
   private Point insertionPoint;

  /** Number maximum of players
   */
   private int maxPlayers;

  /**
   */
   private RoomLink[] roomLinks;

  /**
   */
   private MapExit[] mapExits;

  /** List of players in the Room
   */
   private transient Hashtable players;

  /** List of items in the Room
   */
   private WotlasObject[] wotlasObjects;

 /*------------------------------------------------------------------------------------*/
  
  /**
   * Constructor
   */
   public Room() {}

 /*------------------------------------------------------------------------------------*/
  /*
   * List of setter and getter used for persistence
   */

  public void setRoomID(int myRoomID) {
    this.roomID = myRoomID;
  }
  public int getRoomID() {
    return roomID;
  }
  public void setFullName(String myFullName) {
    this.fullName = myFullName;
  }
  public String getFullName() {
    return fullName;
  }
  public void setShortName(String myShortName) {
    this.shortName = myShortName;
  }
  public String getShortName() {
    return shortName;
  }
  public void setInsertionPoint(Point myInsertionPoint) {
    this.insertionPoint = myInsertionPoint;
  }
  public Point getInsertionPoint() {
    return insertionPoint;
  }
  public void setMaxPlayers(int myMaxPlayers) {
    this.maxPlayers = myMaxPlayers;
  }
  public int getMaxPlayers() {
    return maxPlayers;
  }
  public void setRoomLinks(RoomLink[] myRoomLinks) {
    this.roomLinks = myRoomLinks;
  }
  public RoomLink[] getRoomLinks() {
    return roomLinks;
  }
  public void setMapExits(MapExit[] myMapExits) {
    this.mapExits = myMapExits;
  }
  public MapExit[] getMapExits() {
    return mapExits;
  }
  public void setWotlasObjects(WotlasObject myWotlasObject) {
  }
  public WotlasObject[] getWotlasObjects() {
    return null;
  }
  
  /*------------------------------------------------------------------------------------*/


  /** To get the list of all the players on this map.
   * IMPORTANT: before ANY process on this list synchronize your code on the "players"
   * object :
   *<pre>
   *   Hashtable players = room.getPlayers();
   *   
   *   synchronized( players ) {
   *       ... some SIMPLE and SHORT processes...
   *   }
   *
   * @return player hashtable, player.getPrimaryKey() is the key.
   */
    public Hashtable getPlayers() {
        return players;
    }

 /*------------------------------------------------------------------------------------*/

  /** Add a player to this world. The player must have been previously initialized.
   *  We suppose that the player.getLocation() points out to this Room.
   *
   * @param player player to add
   * @return false if the player already exists on this RoomMap, true otherwise
   */
   public boolean addPlayer( Player player ) {
       if( players.contains( player.getPrimaryKey() ) ) {
           Debug.signal( Debug.CRITICAL, this, "addPlayer failed: key "+player.getPrimaryKey()
                         +" already in this room "+roomID );
           return false;
       }

       players.put( player.getPrimaryKey(), player );
       return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Removes a player from this room.
   *  We suppose that the player.getLocation() points out to this room.
   *
   * @param player player to remove
   * @return false if the player doesn't exists in this Room, true otherwise
   */
   public boolean removePlayer( Player player ) {
       if( !players.contains( player.getPrimaryKey() ) ) {
           Debug.signal( Debug.CRITICAL, this, "removePlayer failed: key "+player.getPrimaryKey()
                         +" not found in this room "+roomID );
           return false;
       }

       players.remove( player.getPrimaryKey() );
       return true;
   }

 /*------------------------------------------------------------------------------------*/

  /** Add a new RoomLink object to the array {@link #roomLinks roomLinks}
   *
   * @return a new RoomLink object
   */
  public RoomLink addRoomLink()
  {
    RoomLink myRoomLink = new RoomLink();
    
    if (roomLinks == null) {
      roomLinks = new RoomLink[1];
      myRoomLink.setRoomLinkID(0);
      roomLinks[0] = myRoomLink;
    } else {
      RoomLink[] myRoomLinks = new RoomLink[roomLinks.length+1];
      myRoomLink.setRoomLinkID(roomLinks.length);
      System.arraycopy(roomLinks, 0, myRoomLinks, 0, roomLinks.length);
      myRoomLinks[roomLinks.length] = myRoomLink;
      roomLinks = myRoomLinks;
    }
    return myRoomLink;
  }

 /*------------------------------------------------------------------------------------*/
  
  /** Add a new MapExit object to the array {@link #mapExits mapExits}
   *
   * @return a new MapExit object
   */
  public MapExit addMapExit()
  {
    MapExit myMapExit = new MapExit();
    
    if (mapExits == null) {
      mapExits = new MapExit[1];
      myMapExit.setMapExitID(0);
      mapExits[0] = myMapExit;
    } else {
      MapExit[] myMapExits = new MapExit[mapExits.length+1];
      myMapExit.setMapExitID(mapExits.length);
      System.arraycopy(mapExits, 0, myMapExits, 0, mapExits.length);
      myMapExits[mapExits.length] = myMapExit;
      mapExits = myMapExits;
    }
    return myMapExit;
  }

 /*------------------------------------------------------------------------------------*/

  /** To init this room ( it rebuilds shortcuts ). DON'T CALL this method directly,
   *  use the init() method of the associated world.
   */
   public void init(){
   }

 /*------------------------------------------------------------------------------------*/

}
