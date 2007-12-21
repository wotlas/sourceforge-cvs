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
 
package wotlas.common.universe;

import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.common.*;
import wotlas.common.router.*;
import wotlas.utils.*;

import java.awt.Rectangle;

 /** A WorldMap represents the root class of a whole world of our Game Universe.
  *
  * @author Petrus, Aldiss, Diego
  * @see wotlas.common.universe.TownMap
  */
 
public class WorldMap implements WotlasMap {

 /*------------------------------------------------------------------------------------*/
    
  /** ID of the World (index in the worldmap array in the worldmanager)
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

  /** Point of insertion (teleportation, arrival)
   */
    private ScreenPoint insertionPoint;

  /** Music Name
   */
    private String musicName;

 /*------------------------------------------------------------------------------------*/

  /** Array of TownMap
   */
    private transient TownMap[] townMaps;

  /** Array of TileMap
   */
    private transient TileMap[] tileMaps;

  /** Our message router. Owns the list of players of this map (not in Towns).
   */
    private transient MessageRouter messageRouter;

 /*------------------------------------------------------------------------------------*/
  
  /**
   * Constructor
   */
    public WorldMap() {
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

    public void setInsertionPoint(ScreenPoint myInsertionPoint) {
      this.insertionPoint = myInsertionPoint;
    }

    public ScreenPoint getInsertionPoint() {
      return new ScreenPoint( insertionPoint );
    }

    public void setWorldImage(ImageIdentifier worldImage) {
      this.worldImage = worldImage;
    }

    public ImageIdentifier getWorldImage() {
      return worldImage;
    }

    public void setMusicName(String musicName) {
      this.musicName = musicName;
    }

    public String getMusicName() {
      return musicName;
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

    public MessageRouter getMessageRouter() {
      return messageRouter;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a TileMap by its ID.
   *
   * @param id tileMapID
   * @return corresponding tileMap, null if ID does not exist.
   */
    public TileMap getTileMapFromID( int id ) {
   	if(id>=tileMaps.length || id<0) {
           Debug.signal( Debug.ERROR, this, "getTileMapFromID : Bad tileMap ID "+id );
   	   return null;
   	}
   	
        return tileMaps[id];
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
    public TownMap addNewTownMap() {
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

  /** To get the wotlas location associated to this Map.
   *  @return associated Wotlas Location
   */
    public WotlasLocation getLocation() {
    	return new WotlasLocation( worldMapID );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init this world ( it rebuilds shortcuts ). This method calls the init() method
   *  of the TownMaps. You must only call this method when ALL the world data has been
   *  loaded.
   */
    public void init() {

     // 1 - any data ?
        if(townMaps==null) {
           Debug.signal(Debug.WARNING, this, "WorldMap init failed: No Towns.");
        }
        if(tileMaps==null) {
           Debug.signal(Debug.WARNING, this, "WorldMap init failed: No TileMap.");
        }
        if(townMaps==null && tileMaps==null) {
            return;
        }

     // 2 - we transmit the init() call
        for( int i=0; i<townMaps.length; i++ )
            if( townMaps[i]!=null )
                townMaps[i].init( this );
        for( int i=0; i<tileMaps.length; i++ )
            if( tileMaps[i]!=null )
                tileMaps[i].init( this );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init this worldmap for message routing. We create an appropriate message router
   *  for the world via the provided factory.
   *
   *  Don't call this method yourself it's called from the WorldManager !
   *
   * @param msgRouterFactory our router factory
   */
    public void initMessageRouting( MessageRouterFactory msgRouterFactory, WorldManager wManager ){
       // build/get our router
          messageRouter = msgRouterFactory.createMsgRouterForWorldMap( this, wManager );

       // we transmit the call to other layers
          for( int i=0; i<townMaps.length; i++ )
             if( townMaps[i]!=null )
                 townMaps[i].initMessageRouting( msgRouterFactory, wManager );
          for( int i=0; i<tileMaps.length; i++ )
             if( tileMaps[i]!=null )
                 tileMaps[i].initMessageRouting( msgRouterFactory, wManager );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** String Info.
   */
    public String toString(){
         return "World - "+fullName;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tests if the given player rectangle has its x,y cordinates in a TileMapRectangle
   *
   * @param destX destination x position of the player movement ( endPoint of path )
   * @param destY destination y position of the player movement ( endPoint of path )
   * @param rCurrent rectangle containing the player's current position, width & height
   * @return the TileMap the player is heading to (if he has reached it, or if there
   *         are any), null if none.
   */
     public TileMap isEnteringTileMap( int destX, int destY, Rectangle rCurrent ) {
        if(tileMaps==null)
           return null;

        for( int i=0; i<tileMaps.length; i++ ){
             Rectangle tileMapRect = tileMaps[i].toRectangle();

             if( tileMapRect.contains( destX, destY ) && tileMapRect.intersects( rCurrent ) )
                 return tileMaps[i]; // ileMap reached
        }

        return null;
     }
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tests if the given player rectangle has its x,y cordinates in a TownRectangle
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

  /** Add a new TileMap object to the array {@link #tileMaps tileMaps}
   *
   * @param tileMap TileMap object to add
   */
    public void addTileMap( TileMap tileMap ) {
      if (tileMaps == null) {
         tileMaps = new TileMap[tileMap.getTileMapID()+1];
      }
      else if( tileMaps.length <= tileMap.getTileMapID() ) {
         TileMap[] myTileMaps = new TileMap[tileMap.getTileMapID()+1];
         System.arraycopy( tileMaps, 0, myTileMaps, 0, tileMaps.length );
         tileMaps = myTileMaps;
      }

      tileMaps[tileMap.getTileMapID()] = tileMap;        
    }

  /** Add a new TileMap object to the array {@link #tileMaps tileMaps}
   *
   * @return a new TileMap object
   */
    public TileMap addNewTileMap() {
       TileMap myTileMap = new TileMap();

       if (tileMaps == null) {
           tileMaps = new TileMap[1];
           myTileMap.setTileMapID(0);
           tileMaps[0] = myTileMap;
       } else {
    	   TileMap[] myTileMaps = new TileMap[tileMaps.length+1];
    	   myTileMap.setTileMapID(tileMaps.length);
    	   System.arraycopy(tileMaps, 0, myTileMaps, 0, tileMaps.length);
    	   myTileMaps[tileMaps.length] = myTileMap;
    	   tileMaps = myTileMaps;
       }

       return myTileMap;
    }
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Transient fields getter & setter
   */
    public void setTileMaps(TileMap[] myTileMaps) {
      this.tileMaps = myTileMaps;
    }

    public TileMap[] getTileMaps() {
      return tileMaps;
    }

}
