

/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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

import wotlas.common.*;
import wotlas.utils.*;
import wotlas.libs.persistence.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.utils.*;

import java.awt.*;
import java.util.*;

 /** Group of graphics represents an Id+ the size of the Tiles inside the image, and the name of the image
  *
  * @author Diego
  * @see wotlas.common.universe.TileMap
  * @see wotlas.client.TileMapData
  */
 
public class TileManagerFakeIso extends TileMapManager{
    
    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

 /*------------------------------------------------------------------------------------*/
    
    static final byte MAPTYPE = TileMap.FAKEISO;

        /** wht's the basic floor id, eventually used for overlay tiles?
     *  it's basicFloorId
     */
    private byte basicFloorId;
    private byte basicFloorIdTileNr;

  /** Map exits...
   */
    private MapExit[] mapExits;

    transient private TileMap tileMap;

  /** array with fake iso data
   */
    private FakeIsoLayers[][] fakeIsoLayers;
    
  /** data of additive layers of map.
   */
    private byte[][][] mapBackgroundData;
    private boolean[][] mapBackgroundDataMask;
    
  /** only for persistence
   */
    public TileManagerFakeIso( ) {
    }

  /** create the object
   */
    public TileManagerFakeIso( TileMap tileMap) {
        this.tileMap = tileMap;
    }
    
 /*------------------------------------------------------------------------------------*/

  /** String Info.
   */
    public String toString(){
         return "GroupOfGraphics - ";
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** id version of data, used in serialized persistance.
   */
    public int ExternalizeGetVersion(){
        return 2;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeObject( mapBackgroundData );
        objectOutput.writeObject( mapBackgroundDataMask );
        objectOutput.writeObject( fakeIsoLayers );
        objectOutput.writeByte( basicFloorId );
        objectOutput.writeByte( basicFloorIdTileNr );
        objectOutput.writeObject( mapExits );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            mapBackgroundData = ( byte[][][] ) objectInput.readObject();
            mapBackgroundDataMask = ( boolean[][] ) objectInput.readObject();
            fakeIsoLayers = ( FakeIsoLayers[][] ) objectInput.readObject();
            basicFloorId = objectInput.readByte();
            basicFloorIdTileNr = objectInput.readByte();
            mapExits = ( MapExit[] ) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }

    public void setMapExits(MapExit[] myMapExits) {
      this.mapExits = myMapExits;
    }

    public MapExit[] getMapExits() {
      return mapExits;
    }
 
    public void setFakeIsoLayers( int x, int y, byte layer, byte imageId, byte tileNr, byte imageDirection) {
        FakeIsoLayers theNext;
        if( fakeIsoLayers[x][y] == null )
            fakeIsoLayers[x][y] = new FakeIsoLayers( imageId, tileNr, imageDirection );
        else
            fakeIsoLayers[x][y].Add( imageId, tileNr, imageDirection );
        theNext = fakeIsoLayers[x][y].getNext();
        for( int floor=1; floor < layer; floor++){
            if( theNext == null)
                theNext = new FakeIsoLayers( imageId, tileNr, imageDirection );
            else
                theNext.Add( imageId, tileNr, imageDirection );
            theNext = theNext.getNext();
        }
        if(layer != 0){
            theNext.setNext(  new FakeIsoLayers( imageId, tileNr, imageDirection ) );
        }
    }
    
    public void setMap( int x, int y, Dimension mapTileDim, byte basicFloorId, byte basicFloorIdTileNr ){
        this.basicFloorId = basicFloorId;
        this.basicFloorIdTileNr = basicFloorIdTileNr;
        this.tileMap.mapTileDim = mapTileDim;
        Dimension mapSize = new Dimension();
        mapSize.width  = x;
        mapSize.height = y;
        tileMap.setMapSize( mapSize );
        mapBackgroundData = new byte[x][y][2];
        mapBackgroundDataMask = new boolean[x][y];
        for( int xx=0; xx < x; xx++)
            for( int yy=0; yy < y; yy++) {
                mapBackgroundData[xx][yy][0] = basicFloorId;
                mapBackgroundData[xx][yy][1] = basicFloorIdTileNr;
                mapBackgroundDataMask[xx][yy] = TileMap.TILE_FREE;
            }
        fakeIsoLayers = new FakeIsoLayers[x][y];
        for( int xx=0; xx < x; xx++)
            for( int yy=0; yy < y; yy++)
                fakeIsoLayers[xx][yy] = null;
    }
    
    public void setMapPoint(int x, int y, int map, int tileNr) {
        if( x >= tileMap.getMapSize().width 
        || y >= tileMap.getMapSize().height) {
            Debug.signal( Debug.WARNING, null, "Tried to change a point inside of map, with coordinates over mapSize" );
            return;
        }
        /*
        if( map > (.length) ) {
            Debug.signal( Debug.WARNING, null, "Tried to change a point inside of map, with a layer of declared layers " );
            return;
        }
         */
        mapBackgroundData[x][y][0] = (byte) map;
        mapBackgroundData[x][y][1] = (byte) tileNr;
        mapBackgroundDataMask[x][y] = TileMap.TILE_FREE;
    }

    public byte[][][] getMapBackGroundData() {
        return mapBackgroundData;
    }
    
    public byte getMapType(){
        return MAPTYPE;
    }
    
    public void setTileMap(TileMap tileMap){
        this.tileMap = tileMap;
    }

    public void drawAllLayer( GraphicsDirector gDirector ) {
        
        Drawable background = null;                 // background image
        
        for( int y=0; y<tileMap.getMapSize().height; y++ ) {
            for( int x=0; x<tileMap.getMapSize().width; x++ ) {
                if( getMapBackGroundData()[x][y][0] != basicFloorId ) {
                    background = (Drawable) new MotionlessSprite( x*tileMap.getMapTileDim().width,  // ground x=0
                                                              y*tileMap.getMapTileDim().height, // ground y=0
                                                              tileMap.getGroupOfGraphics()[basicFloorId],  // GroupOfGraphics
                                                              basicFloorIdTileNr,  // number of internal tile
                                                              ImageLibRef.MAP_PRIORITY          // priority
                                                            );
                    gDirector.addDrawable(background);
                    background = (Drawable) new MotionlessSprite( x*tileMap.getMapTileDim().width,          // ground x=0
                                                                  y*tileMap.getMapTileDim().height,         // ground y=0
                                                                  tileMap.getGroupOfGraphics()[getMapBackGroundData()[x][y][0]],  // GroupOfGraphics
                                                                  getMapBackGroundData()[x][y][1],          // number of internal tile
                                                                  ImageLibRef.SECONDARY_MAP_PRIORITY        // priority
                                                                  );
                    gDirector.addDrawable( background );
                } 
                else {
                    background = (Drawable) new MotionlessSprite( x*tileMap.getMapTileDim().width,          // ground x=0
                                                                  y*tileMap.getMapTileDim().height,         // ground y=0
                                                                  tileMap.getGroupOfGraphics()[getMapBackGroundData()[x][y][0]],  // GroupOfGraphics
                                                                  getMapBackGroundData()[x][y][1],          // number of internal tile
                                                                  ImageLibRef.SECONDARY_MAP_PRIORITY        // priority
                                                                  );
                    gDirector.addDrawable( background );
                } 
                if( getMapType() == tileMap.FAKEISO )
                    if( fakeIsoLayers[x][y] !=  null ) {
                        Iterator singleData = fakeIsoLayers[x][y].getData().iterator();
                        while( singleData.hasNext() ){
                            byte[] singleByteData = (byte[]) singleData.next();
                            background = (Drawable) new MotionlessSprite( x*tileMap.getMapTileDim().width,        // ground x=0
                                                                        y*tileMap.getMapTileDim().height,           // ground y=0
                                                                        tileMap.getGroupOfGraphics()[ singleByteData[0] ], // GroupOfGraphics
                                                                        singleByteData[1],  // number of internal tile
                                                                        FakeIsoLayers.getPriority( singleByteData[2] )     // priority
                                                                        );
                            gDirector.addDrawable( background );
                        }
                    }
            }
        }
    }

    public byte getBasicFloorId(){
        return basicFloorId;
    }

    public void setBasicFloorId( byte value ){
        this.basicFloorId = value;
    }

    public byte getBasicFloorNr(){
        return basicFloorIdTileNr;
    }

    public void setBasicFloorNr( byte value ){
        this.basicFloorIdTileNr = value;
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

  /** Returns the MapExit which is on the side given by the specified rectangle.
   *  It's an helper for you : if your player is on a WorldMap and wants to go inside
   *  a TileMap use this method to retrieve a valid MapExit and get an insertion point.
   *
   *  The MapExit is in fact a ScreenRectangle and the so called "insertion point"
   *  should be the center of this ScreenRectangle.
   * 
   * @param rCurrent rectangle containing the player's current position, width & height
   *        the rectangle position can be anything BUT it should represent in some
   *        way the direction by which the player hits this TileMap zone.
   * @return the appropriate MapExit, null if there are no MapExits.
   */
   public MapExit findTileMapExit( Rectangle fromPosition ) {

      if(mapExits==null) {
         return null; // no position to analyze
      }  
/*

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
*/

      if(mapExits.length==1)
         return mapExits[0];

      for(int i=0; i<mapExits.length; i++ ) {
         if( mapExits[i].getMapExitSide()==MapExit.WEST && fromPosition.x <= tileMap.x+tileMap.width/2 )
             return mapExits[i];

         if( mapExits[i].getMapExitSide()==MapExit.EAST && fromPosition.x >= tileMap.x+tileMap.width/2 )
             return mapExits[i];

         if( mapExits[i].getMapExitSide()==MapExit.NORTH && fromPosition.y <= tileMap.y+tileMap.height/2 )
             return mapExits[i];

         if( mapExits[i].getMapExitSide()==MapExit.SOUTH && fromPosition.y >= tileMap.y+tileMap.height/2 )
             return mapExits[i];
      }
   
      return mapExits[0]; // default
   }
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the eventual MapExit the given player is intersecting.
   *
   * @param rCurrent rectangle containing the player's current position, width & height
   * @return the ~Building:others tilemap the player is heading to (if he has reached it, or if there
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

     public boolean[][] getMapMask() {
         return mapBackgroundDataMask;
     }
}
