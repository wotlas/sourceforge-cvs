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

import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.common.Player;
import wotlas.utils.Debug;

import java.util.Hashtable;
import java.awt.Rectangle;

 /** A WorldMap represents the root class of a whole world of our Game Universe.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.ServerProcess
  * @see wotlas.common.universe.TownMap
  */
 
public class WorldMap
{
 /*------------------------------------------------------------------------------------*/
 
  /** ID of the World (index in the array {@link ServerProcess#worldMaps ServerProcess.worldMaps})
   */
    private int worldMapID;
     
  /** Full name of the World
   */
    private String fullName;
   
  /** Short name of the World
   */
    private String shortName;

  /** Full Image (identifier) of this world
   */
    private ImageIdentifier worldImage;

 /*------------------------------------------------------------------------------------*/

  /** Array of TownMap
   */
    private transient TownMap[] townMaps;

  /** List of players in the WorldMap (not players in towns)
   */
    private transient Hashtable players;

 /*------------------------------------------------------------------------------------*/
  
  /**
   * Constructor
   */
    public WorldMap() {
       players = new Hashtable(10);
    }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*
   * List of setter and getter used for persistence
   */

    public void setWorldMapID(int myWorldMapID) {
      this.worldMapID = myWorldMapID;
    }

    public int getWorldMapID() {
      return worldMapID;
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

    public void setWorldImage(ImageIdentifier worldImage) {
      this.worldImage = worldImage;
    }

    public ImageIdentifier getWorldImage() {
      return worldImage;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Transient fields getter & setter
   */
    public void setTownMaps(TownMap[] myTownMaps) {
      this.townMaps = myTownMaps;
    }

    public TownMap[] getTownMaps() {
      return townMaps;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the list of all the players on this map.
   * IMPORTANT: before ANY process on this list synchronize your code on the "players"
   * object :
   *<pre>
   *   Hashtable players = world.getPlayers();
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
   *  We suppose that the player.getLocation() points out to this World.
   *
   * @param player player to add
   * @return false if the player already exists on this WorldMap, true otherwise
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

  /** Removes a player from this world.
   *  We suppose that the player.getLocation() points out to this World.
   *
   * @param player player to remove
   * @return false if the player doesn't exists on this WorldMap, true otherwise
   */
    public boolean removePlayer( Player player ) {
       if( !players.contains( player.getPrimaryKey() ) ) {
           Debug.signal( Debug.CRITICAL, this, "removePlayer failed: key "+player.getPrimaryKey()
                         +" not found in "+this );
           return false;
       }

       players.remove( player.getPrimaryKey() );
       return true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a Town by its ID.
   *
   * @param id townMapID
   * @return corresponding townMap, null if ID does not exist.
   */
    public TownMap getTownMapFromID( int id ) {
   	if(id>=townMaps.length || id<0) {
           Debug.signal( Debug.ERROR, this, "getTownMapFromID : Bad town ID "+id );
   	   return null;
   	}
   	
        return townMaps[id];
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new TownMap object to the array {@link #townMaps townMaps}
   *
   * @param town TownMap object to add
   */
    public void addTownMap( TownMap town ) {
      if (townMaps == null) {
         townMaps = new TownMap[town.getTownMapID()+1];
      }
      else if( townMaps.length <= town.getTownMapID() ) {
         TownMap[] myTownMaps = new TownMap[town.getTownMapID()+1];
         System.arraycopy( townMaps, 0, myTownMaps, 0, townMaps.length );
         townMaps = myTownMaps;
      }

      townMaps[town.getTownMapID()] = town;        
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new TownMap object to the array {@link #townMaps townMaps}
   *
   * @return a new TownMap object
   */
    public TownMap addNewTownMap()
    {
       TownMap myTownMap = new TownMap();

       if (townMaps == null) {
           townMaps = new TownMap[1];
           myTownMap.setTownMapID(0);
           townMaps[0] = myTownMap;
       } else {
    	   TownMap[] myTownMaps = new TownMap[townMaps.length+1];
    	   myTownMap.setTownMapID(townMaps.length);
    	   System.arraycopy(townMaps, 0, myTownMaps, 0, townMaps.length);
    	   myTownMaps[townMaps.length] = myTownMap;
    	   townMaps = myTownMaps;
       }

       return myTownMap;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init this world ( it rebuilds shortcuts ). This method calls the init() method
   *  of the TownMaps. You must only call this method when ALL the world data has been
   *  loaded.
   */
    public void init(){

     // 1 - any data ?
        if(townMaps==null) {
           Debug.signal(Debug.WARNING, this, "WorldMap init failed: No Towns.");
           return;
        }

     // 2 - we transmit the init() call
        for( int i=0; i<townMaps.length; i++ )
            if( townMaps[i]!=null )
                townMaps[i].init( this );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** String Info.
   */
    public String toString(){
      if(townMaps==null)
         return "WorldMap wId:"+worldMapID+" Name:"+fullName+" maxIdTowns: no array";
      else
         return "WorldMap wId:"+worldMapID+" Name:"+fullName+" maxIdTowns:"+townMaps.length;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tests if the given player rectangle has its x,y cordinates in a TownRectangle
   *
   *  Here is an example how to use this method (at each game tick) :
   *  <pre>
   *
   *  WorldMap wMap = worldManager.getWorld( player.getWotlasLocation() );
   *
   *  TownMap tMap = wMap.isEnteringTown( myPlayer.myDestX, myPlayer.myDestY,
   *                                      myPlayer.getCurrentRectangle() );
   *  
   *  if( tMap != null ) {
   *   // intersection with a TownMap, which MapExit are we using ?
   *      MapExit mExit = tMap.findTownMapExit( myPlayer.getCurrentRectangle() );
   *  
   *   // ok, we have the TownMap => we know which image to display, buildings, etc...
   *   //     we have the MapExit => we know where to insert our player on the
   *   //                            TownMap, it's done the following way :
   *
   *      myPlayer.setX( mExit.getX() + mExit.getWidth()/2 );
   *      myPlayer.setY( mExit.getY() + mExit.getHeight()/2 );
   *
   *  </pre>
   *  Yes, we set our player on the middle of the MapExit. Refer to the TownMap
   *  findTownMapExit() javadoc for more details on this method.
   *
   * @param destX destination x position of the player movement ( endPoint of path )
   * @param destY destination y position of the player movement ( endPoint of path )
   * @param rCurrent rectangle containing the player's current position, width & height
   * @return the TownMap the player is heading to (if he has reached it, or if there
   *         are any), null if none.
   */
     public TownMap isEnteringTown( int destX, int destY, Rectangle rCurrent ) {
        if(townMaps==null)
           return null;

        for( int i=0; i<townMaps.length; i++ ){
             Rectangle townRect = townMaps[i].toRectangle();

             if( townRect.contains( destX, destY ) && townRect.intersects( rCurrent ) )
                 return townMaps[i]; // town reached
        }

        return null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
