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
import wotlas.common.*;
import wotlas.common.router.*;
import wotlas.utils.*;

import java.awt.Rectangle;
import java.awt.Point;

 /** A TownMap represents a town in our Game Universe.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.WorldMap
  * @see wotlas.common.universe.Building
  */
 
public class TownMap extends ScreenRectangle implements WotlasMap {

 /*------------------------------------------------------------------------------------*/
 
  /** ID of the TownMap (index in the array of townmaps in the WorldMap)
   */
    private int townMapID;
     
  /** Full name of the Town
   */
    private String fullName;
   
  /** Short name of the Town
   */
    private String shortName;

  /** Small Image (identifier) of this town for WorldMaps.
   */
    private ImageIdentifier smallTownImage;

  /** Full Image (identifier) of this town
   */
    private ImageIdentifier townImage;

  /** Point of insertion (teleportation, arrival)
   */
    private ScreenPoint insertionPoint;

  /** Music Name
   */
    private String musicName;

  /** Map exits...
   */
    private MapExit[] mapExits;

 /*------------------------------------------------------------------------------------*/

  /** Link to the worldMap we belong to...
   */
    private transient WorldMap myWorldMap;

  /** Array of Building
   */
    private transient Building[] buildings;
  
  /** Our message router. Owns the list of players of this map (not in buildings).
   */
    private transient MessageRouter messageRouter;

 /*------------------------------------------------------------------------------------*/

  /** Constructor for persistence.
   */
    public TownMap() {
    }
   
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with x,y positions & width,height dimension on WorldMap.
   * @param x x position of this building on a WorldMap.
   * @param y y position of this building on a WorldMap.
   * @param width width dimension of this building on a WorldMap.
   * @param height height dimension of this building on a WorldMap.
   */
    public TownMap(int x, int y, int width, int height) {
       super(x,y,width,height);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*
   * List of setter and getter used for persistence
   */
    public void setTownMapID(int myTownMapID) {
      this.townMapID = myTownMapID;
    }

    public int getTownMapID() {
      return townMapID;
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

    public void setSmallTownImage(ImageIdentifier smallTownImage) {
      this.smallTownImage = smallTownImage;
    }

    public ImageIdentifier getSmallTownImage() {
      return smallTownImage;
    }

    public void setTownImage(ImageIdentifier townImage) {
      this.townImage = townImage;
    }

    public ImageIdentifier getTownImage() {
      return townImage;
    }

    public void setMapExits(MapExit[] myMapExits) {
      this.mapExits = myMapExits;
    }

    public MapExit[] getMapExits() {
      return mapExits;
    }

    public void setInsertionPoint(ScreenPoint myInsertionPoint) {
      this.insertionPoint = myInsertionPoint;
    }

    public ScreenPoint getInsertionPoint() {
      return new ScreenPoint( insertionPoint );
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

    public WorldMap getMyWorldMap() {
      return myWorldMap;
    }

    public void setBuildings(Building[] myBuildings) {
      this.buildings = myBuildings;
    }

    public Building[] getBuildings() {
      return buildings;
    }

    public MessageRouter getMessageRouter() {
      return messageRouter;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a building by its ID.
   *
   * @param id buildingID
   * @return corresponding building, null if ID does not exist.
   */
    public Building getBuildingFromID( int id ) {
   	if(id>=buildings.length || id<0) {
           Debug.signal( Debug.ERROR, this, "getBuildingFromID : Bad building ID "+id );
   	   return null;
   	}
   	
        return buildings[id];
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the wotlas location associated to this Map.
   *  @return associated Wotlas Location
   */
    public WotlasLocation getLocation() {
       return new WotlasLocation( myWorldMap.getWorldMapID(),townMapID );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new Building object to our list {@link #buildings buildings})
   *
   * @param building Building object to add
   */
    public void addBuilding( Building building ) {
        if ( buildings == null ) {
             buildings = new Building[building.getBuildingID()+1];
        }
        else if( buildings.length <= building.getBuildingID() ) {
           Building[] myBuildings = new Building[building.getBuildingID()+1];
           System.arraycopy( buildings, 0, myBuildings, 0, buildings.length );
           buildings = myBuildings;
        }

        buildings[building.getBuildingID()] = building;        
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new Building object to the array {@@link #buildings buildings})
   *
   * @return a new Building object
   */
    public Building addNewBuilding() {
       Building myBuilding = new Building();
    
       if (buildings == null) {
           buildings = new Building[1];      
           myBuilding.setBuildingID(0);
           buildings[0] = myBuilding;
       } else {
           Building[] myBuildings = new Building[buildings.length+1];
           myBuilding.setBuildingID(buildings.length);
           System.arraycopy(buildings, 0, myBuildings, 0, buildings.length);
           myBuildings[buildings.length] = myBuilding; 
           buildings = myBuildings;      
       }

       return myBuilding;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new MapExit object to the array {@link #mapExits mapExits}
   *
   * @return a new MapExit object
   */
    public MapExit addMapExit(ScreenRectangle r) {
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
    public void addMapExit( MapExit me ) {
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

  /** To init this town ( it rebuilds shortcuts ). DON'T CALL this method directly, use
   *  the init() method of the associated world.
   *
   * @param myWorldMap our parent WorldMap.
   */
    public void init( WorldMap myWorldMap ) {

       this.myWorldMap = myWorldMap;

    // 1 - any data ?
       if(buildings==null) {
          Debug.signal(Debug.WARNING, this, "Town has no buildings ! "+this);
          return;
       }

    // 2 - we transmit the init() call
       for( int i=0; i<buildings.length; i++ )
            if( buildings[i]!=null )
                buildings[i].init( this );

    // 3 - MapExit inits
       if( mapExits==null ) return;
       
       WotlasLocation thisLocation = new WotlasLocation( myWorldMap.getWorldMapID(),townMapID );
       
       for( int i=0; i<mapExits.length; i++ )
            mapExits[i].setMapExitLocation(thisLocation);

    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init this townmap for message routing. We create an appropriate message router
   *  for the town via the provided factory.
   *
   *  Don't call this method yourself it's called via the WorldManager !
   *
   * @param msgRouterFactory our router factory
   */
    public void initMessageRouting( MessageRouterFactory msgRouterFactory, WorldManager wManager ){
       // build/get our router
          messageRouter = msgRouterFactory.createMsgRouterForTownMap( this, wManager );

       // we transmit the call to other layers
          for( int i=0; i<buildings.length; i++ )
             if( buildings[i]!=null && buildings[i].getInteriorMaps()!=null) {
             	 InteriorMap imaps[] = buildings[i].getInteriorMaps();

                 for( int j=0; j<imaps.length; j++ )
                    if(imaps[j]!=null && imaps[j].getRooms()!=null) {

                       Room rooms[] = imaps[j].getRooms();
                       
                       for( int k=0; k<rooms.length; k++ )
                            rooms[k].initMessageRouting( msgRouterFactory, wManager );
                    }
             }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the MapExit which is on the side given by the specified rectangle.
   *  It's an helper for you : if your player is on a WorldMap and wants to go inside
   *  a TownMap use this method to retrieve a valid MapExit and get an insertion point.
   *
   *  The MapExit is in fact a ScreenRectangle and the so called "insertion point"
   *  should be the center of this ScreenRectangle.
   * 
   * @param rCurrent rectangle containing the player's current position, width & height
   *        the rectangle position can be anything BUT it should represent in some
   *        way the direction by which the player hits this TownMap zone.
   * @return the appropriate MapExit, null if there are no MapExits.
   */
   public MapExit findTownMapExit( Rectangle fromPosition ) {

      if(mapExits==null) {
      	// Ok, this town has no MapExit, we suppose it's just a building
      	// Is there ONE building ?
      	   if(buildings==null || buildings.length==0 || buildings[0]==null
      	      || buildings[0].getBuildingExits()==null || buildings[0].getBuildingExits().length==0) {
              Debug.signal(Debug.ERROR,this,"Failed to find town map exit !");
              return null; // nope ! error
           }

           if(fromPosition==null)
              return null; // no position to analyze

        // We search on the first map exit
           MapExit bExits[] = buildings[0].getBuildingExits();

           for(int i=0; i<bExits.length; i++ ) {
             if( bExits[i].getMapExitSide()==MapExit.WEST && fromPosition.x <= x+width/2 )
                 return bExits[i];

             if( bExits[i].getMapExitSide()==MapExit.EAST && fromPosition.x >= x+width/2 )
                 return bExits[i];

             if( bExits[i].getMapExitSide()==MapExit.NORTH && fromPosition.y <= y+height/2 )
                 return bExits[i];

             if( bExits[i].getMapExitSide()==MapExit.SOUTH && fromPosition.y >= y+height/2 )
                 return bExits[i];
           }
   
          return bExits[0]; // default
      }

      if(mapExits.length==1)
         return mapExits[0];

      for(int i=0; i<mapExits.length; i++ ) {
         if( mapExits[i].getMapExitSide()==MapExit.WEST && fromPosition.x <= x+width/2 )
             return mapExits[i];

         if( mapExits[i].getMapExitSide()==MapExit.EAST && fromPosition.x >= x+width/2 )
             return mapExits[i];

         if( mapExits[i].getMapExitSide()==MapExit.NORTH && fromPosition.y <= y+height/2 )
             return mapExits[i];

         if( mapExits[i].getMapExitSide()==MapExit.SOUTH && fromPosition.y >= y+height/2 )
             return mapExits[i];
      }
   
      return mapExits[0]; // default
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tests if the given player rectangle has its x,y cordinates in a Building.
   *
   * @param destX destination x position of the player movement ( endPoint of path )
   * @param destY destination y position of the player movement ( endPoint of path )
   * @param rCurrent rectangle containing the player's current position, width & height
   * @return the Building the player is heading to (if he has reached it, or if there
   *         are any), null if none.
   */
     public Building isEnteringBuilding( int destX, int destY, Rectangle rCurrent ) {
        if(buildings==null)
           return null;

        for( int i=0; i<buildings.length; i++ ){
             Rectangle buildRect = buildings[i].toRectangle();

             if( buildRect.contains( destX, destY ) && buildRect.intersects( rCurrent ) )
                 return buildings[i]; // building reached
        }

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

  /** String Info.
   */
    public String toString(){
         return "TownMap - "+fullName;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

        
        
 