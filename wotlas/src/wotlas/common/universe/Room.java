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
import wotlas.utils.*;

import java.awt.Rectangle;
import java.util.Hashtable;

 /** A Room of an interiorMap. Here is a sample code on how to use a Room
  *  during the player tick... all the following could be placed in the
  *  client's DataManager :
  *
  *  <pre>
  *
  * // tells if the player could be moving to another room
  *    private boolean couldBeMovingToAnotherRoom = false;
  *
  * // current RoomLink considered for intersection
  *    private RoomLink latestRoomLink;
  *
  *    
  *    public void tick() {
  *         player.tick();
  *         locationUpdate();
  *    }
  *
  *
  *    public void locationUpdate() {
  *
  *      // we call the right method whether the player is on
  *      // a TownMap, a WorldMap or in a Room.
  *
  *         if( player.getWotlasLocation().isRoom() )
  *             roomLocationUpdate();
  *         else if ( player.getWotlasLocation().isTown() )
  *             townLocationUpdate();
  *         else if ( player.getWotlasLocation().isWorld() )
  *             worldLocationUpdate();
  *    }
  *
  *
  *    public void roomLocationUpdate() {
  *    
  *        Room myRoom = worldManager.getRoom( player.getWotlasLocation() );
  *
  *     // I - ROOMLINK INTERSECTION UPDATE ( is the player moving to another room ? )
  *
  *        RoomLink rl = myRoom.isIntersectingRoomLink( player.getCurrentRectangle() );
  *
  *        if( rl!=null && !couldBeMovingToAnotherRoom ) {
  *          // Player is intersecting a RoomLink
  *             latestRoomLink = rl;
  *             couldBeMovingToAnotherRoom = true;
  *
  *          // is there a Door ?
  *             if( rl.getDoor()!=null ) {
  *                 // nothing for now
  *             }
  *        }
  *        else if ( couldBeMovingToAnotherRoom ) {
  *          // ok, no intersection now, are we in an another room ?
  *             couldBeMovingToAnotherRoom = false;
  *
  *             int newRoomID = myRoom.isInOtherRoom( latestRoomLink, player.getCurrentRectangle() );
  *             
  *             if( newRoomID>=0 ) {
  *                // Ok, we move to this new Room
  *                   myRoom.removePlayer( player );
  *                   player.getWotlasLocation().setRoomID( newRoomID );
  *                   myRoom.addPlayer( player );
  *             }
  *        } // End of part I
  *
  *
  *     // II - MAPEXIT INTERSECTION UPDATE ( is the player moving to another map ? )
  *        if( player.isMoving() ) {
  *            MapExit mapExit = myRoom.isIntersectingMapExit( player.getDestX(), player.getDestY(),
  *                                                            player.getCurrentRectangle() );
  *
  *            if( mapExit!=null ) {
  *            	  // Ok, we are going to a new map...
  *                  myRoom.removePlayer( player );
  *                  player.setWotlasLocation( mapExit.getTargetWotlasLocation() );
  *                  cleanInteriorMapData(); // suppress drawables, shadows, data
  *
  *                  switch( mapExit.getType ) {
  *                      case mapExit.INTERIOR_MAP_EXIT :
  *                               initInteriorMapDisplay(); // init new map
  *                               break;
  *
  *                      case mapExit.TOWN_EXIT :
  *                               initTownMapDisplay(); // init new map
  *                               break;
  *                  }
  *            }
  *        } // End of part II
  *
  *    }
  *
  *  
  *    public void initInteriorMapDisplay( PlayerImpl player ) {
  * 
  *      // 1 - we load the images & init the graphicsDirector
  *      // 2 - we init our Player, add a shadow, and start the display
  *    }
  *
  *
  *    public void initTownMapDisplay( PlayerImpl player ) {
  * 
  *      // 1 - we load the images & init the graphicsDirector
  *      // 2 - we init our Player (no shadow drawable) and start the display
  *    }
  *
  *  </pre>
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.RoomLink
  */

public class Room
{
 /*------------------------------------------------------------------------------------*/

  /** ID of the Room (index in the array {@link InteriorMap#rooms InteriorMap.rooms})
   */
    private int roomID;

  /** Full name of the Room
   */
    private String fullName;

  /** Short name of the World
   */
    private String shortName;

  /** Point of insertion (teleportation, arrival)
   */
    private ScreenPoint insertionPoint;

  /** Number maximum of players
   */
    private int maxPlayers;

  /** Room links...
   */
    private RoomLink[] roomLinks;

  /** Map exits...
   */
    private MapExit[] mapExits;

  /** List of items in the Room
   */
    private WotlasObject[] wotlasObjects;

 /*------------------------------------------------------------------------------------*/

  /** Our interiorMap wher this room is.
   */
    private transient InteriorMap myInteriorMap;

  /** List of players in the Room
   */
    private transient Hashtable players;

 /*------------------------------------------------------------------------------------*/
  
  /** Constructor
   */
    public Room() {
       players = new Hashtable(10);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*
   * List of setter and getter used for persistence
   */
    public void setRoomID(int myRoomID) {
      this.roomID = myRoomID;
    }

    public int getRoomID() {
      return roomID;
    }

    public RoomLink getRoomLink( int id ) {
      return roomLinks[id];
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

    public void setInsertionPoint(ScreenPoint myInsertionPoint) {
      this.insertionPoint = myInsertionPoint;
    }

    public ScreenPoint getInsertionPoint() {
      return new ScreenPoint( insertionPoint );
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
       // none for now
    }

    public WotlasObject[] getWotlasObjects() {
      return null; // none for now
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Transient fields getter & setter
   */
  
    public InteriorMap getMyInteriorMap() {
      return myInteriorMap;
    }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

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

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a player to this world. The player must have been previously initialized.
   *  We suppose that the player.getLocation() points out to this Room.
   *
   * @param player player to add
   * @return false if the player already exists on this RoomMap, true otherwise
   */
    public boolean addPlayer( Player player ) {
       if( players.contains( player.getPrimaryKey() ) ) {
           Debug.signal( Debug.CRITICAL, this, "addPlayer failed: key "+player.getPrimaryKey()
                         +" already in "+this );
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
       if( !players.containsKey( player.getPrimaryKey() ) ) {
           Debug.signal( Debug.CRITICAL, this, "removePlayer failed: key "+player.getPrimaryKey()
                         +" not found in "+this );
           return false;
       }

       players.remove( player.getPrimaryKey() );
       return true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new RoomLink object to the array {@link #roomLinks roomLinks}
   *
   * @return a new RoomLink object
   */
    public RoomLink addRoomLink( ScreenRectangle r )
    {
       RoomLink myRoomLink = new RoomLink(r);

       if(roomLinks == null) {
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

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new RoomLink object to the array {@link #roomLinks roomLinks}
   *
   * @param rl RoomLink object to add
   */
    public void addRoomLink( RoomLink rl )
    {
       if(roomLinks == null) {
         roomLinks = new RoomLink[1];
         roomLinks[0] = rl;
       } else {
         RoomLink[] myRoomLinks = new RoomLink[roomLinks.length+1];
         System.arraycopy(roomLinks, 0, myRoomLinks, 0, roomLinks.length);
         myRoomLinks[roomLinks.length] = rl;
         roomLinks = myRoomLinks;
       }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Add a new MapExit object to the array {@link #mapExits mapExits}
   *
   * @return a new MapExit object
   */
    public MapExit addMapExit(ScreenRectangle r)
    {
      MapExit myMapExit = new MapExit(r);
    
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

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new MapExit object to the array {@link #mapExits mapExits}
   *
   * @param me MapExit object
   */
    public void addMapExit( MapExit me )
    {
      if (mapExits == null) {
         mapExits = new MapExit[1];
         mapExits[0] = me;
      } else {
         MapExit[] myMapExits = new MapExit[mapExits.length+1];
         System.arraycopy(mapExits, 0, myMapExits, 0, mapExits.length);
         myMapExits[mapExits.length] = me;
         mapExits = myMapExits;
      }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init this room ( it rebuilds shortcuts ). DON'T CALL this method directly,
   *  use the init() method of the associated world.
   *
   * @param myInteriorMap our father InteriorMap
   */
    public void init( InteriorMap myInteriorMap ){
       this.myInteriorMap = myInteriorMap;
       
       if( mapExits == null )
           return;
       
       WotlasLocation thisLocation = new WotlasLocation();
       thisLocation.setRoomID( roomID );
       thisLocation.setInteriorMapID( myInteriorMap.getInteriorMapID() );
       thisLocation.setBuildingID( myInteriorMap.getMyBuilding().getBuildingID() );
       thisLocation.setTownMapID( myInteriorMap.getMyBuilding().getMyTownMap().getTownMapID() );
       thisLocation.setWorldMapID( myInteriorMap.getMyBuilding().getMyTownMap().getMyWorldMap().getWorldMapID() );
       
       for( int i=0; i<mapExits.length; i++ )
            mapExits[i].setMapExitLocation(thisLocation);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the eventual RoomLink the given player is intersecting.
   *
   * @param rCurrent rectangle containing the player's current position, width & height
   * @return the Building the player is heading to (if he has reached it, or if there
   *         are any), null if none.
   */
     public RoomLink isIntersectingRoomLink( Rectangle rCurrent ) {
        if(roomLinks==null)
           return null;

        for( int i=0; i<roomLinks.length; i++ )
             if( roomLinks[i].toRectangle().intersects( rCurrent ) )
                 return roomLinks[i]; // RoomLink reached

        return null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the eventual MapExit the given player is intersecting.
   *
   * @param rCurrent rectangle containing the player's current position, width & height
   * @return the Building the player is heading to (if he has reached it, or if there
   *         are any), null if none.
   */
     public MapExit isIntersectingMapExit( int destX, int destY, Rectangle rCurrent ) {
        if(mapExits==null)
           return null;

        for( int i=0; i<mapExits.length; i++ )
             if( mapExits[i].toRectangle().contains(destX,destY)
                 && mapExits[i].toRectangle().intersects( rCurrent ) )
                 return mapExits[i]; // mapExits reached

        return null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the RoomID of the RoomLink's target Room if the given current player
    *  Rectangle is proved to be in this other room. We return -1 if we are still in our
    *  room.
    *
    *  You should call this method
    *
    * @param rlink a RoomLink the player has intersected recently
    * @param rCurrent current player Rectangle.
    * @return -1 if the player is still in this Room, the other RoomID if he has moved to
    *         the other Room pointed by the given RoomLink.
    */
     public int isInOtherRoom( RoomLink rlink, Rectangle rCurrent ) {

             if( rlink.width < rlink.height ) {
                 if( rlink.getRoom1ID()==roomID ){
                   // ok, we are the west Room
                      if( rlink.x + rlink.width <= rCurrent.x )
                          return rlink.getRoom2ID(); // we are in the other room
                 }
                 else {
                  // ok, we are the east Room
                      if( rCurrent.x + rCurrent.width <= rlink.x )
                          return rlink.getRoom1ID(); // we are in the other room
                 }
             }
             else if( rlink.width > rlink.height ) {
                 if( rlink.getRoom1ID()==roomID ){
                   // ok, we are the north Room
                      if( rlink.y + rlink.height <= rCurrent.y )
                          return rlink.getRoom2ID(); // we are in the other room
                 }
                 else {
                  // ok, we are the south Room
                      if( rCurrent.y + rCurrent.height <= rlink.y )
                          return rlink.getRoom1ID(); // we are in the other room
                 }
             }

        return -1; // we are still in this room
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** String Info.
   */
    public String toString(){
         return "Room Id:"+roomID+" Name:"+fullName+" parent:"+myInteriorMap;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
