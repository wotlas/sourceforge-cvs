

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

    transient private TileMap tileMap;

  /** array with fake iso data
   */
    private FakeIsoLayers[][] fakeIsoLayers;
    
  /** data of additive layers of map.
   */
    private byte[][][] mapBackgroundData;
    
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
        objectOutput.writeObject( fakeIsoLayers );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            mapBackgroundData = ( byte[][][] ) objectInput.readObject();
            fakeIsoLayers = ( FakeIsoLayers[][] ) objectInput.readObject();
        } else {
            // to do.... when new version
        }
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
    
    public void setMap( int x, int y, int layers, Dimension mapTileDim ){
        this.tileMap.mapTileDim = mapTileDim;
        Dimension mapSize = new Dimension();
        mapSize.width  = x;
        mapSize.height = y;
        tileMap.setMapSize( mapSize );
        mapBackgroundData = new byte[x][y][3];
        for( int xx=0; xx < x; xx++)
            for( int yy=0; yy < y; yy++) {
                mapBackgroundData[xx][yy][0] = 0;
                mapBackgroundData[xx][yy][1] = 0;
                mapBackgroundData[xx][yy][2] = 0;
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
        mapBackgroundData[x][y][2] = 0;
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
                background = (Drawable) new MotionlessSprite( x*tileMap.getMapTileDim().width,  // ground x=0
                                                              y*tileMap.getMapTileDim().height, // ground y=0
                                                              tileMap.getGroupOfGraphics()[0],  // GroupOfGraphics
                                                              getMapBackGroundData()[x][y][1],  // number of internal tile
                                                              ImageLibRef.MAP_PRIORITY          // priority
                                                            );
                gDirector.addDrawable(background);
                if( getMapBackGroundData()[x][y][0] != 0 ) {
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
}