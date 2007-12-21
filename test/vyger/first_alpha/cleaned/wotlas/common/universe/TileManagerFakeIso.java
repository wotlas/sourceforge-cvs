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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Iterator;
import wotlas.common.ImageLibRef;
import wotlas.common.environment.EnvironmentManager;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.drawable.MotionlessSprite;
import wotlas.utils.Debug;
import wotlas.utils.ScreenRectangle;

/** Group of graphics represents an Id+ the size of the Tiles inside the image, and the name of the image
 *
 * @author Diego
 * @see wotlas.common.universe.TileMap
 * @see wotlas.client.TileMapData
 */

public class TileManagerFakeIso extends TileMapManager {

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
    public TileManagerFakeIso() {
    }

    /** create the object
     */
    public TileManagerFakeIso(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    /*------------------------------------------------------------------------------------*/

    /** String Info.
     */
    @Override
    public String toString() {
        return "FakeIso - ";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** id version of data, used in serialized persistance.
     */
    public int ExternalizeGetVersion() {
        return 2;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** write object data with serialize.
     */
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeObject(this.mapBackgroundData);
        objectOutput.writeObject(this.mapBackgroundDataMask);
        objectOutput.writeObject(this.fakeIsoLayers);
        objectOutput.writeByte(this.basicFloorId);
        objectOutput.writeByte(this.basicFloorIdTileNr);
        objectOutput.writeObject(this.mapExits);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.mapBackgroundData = (byte[][][]) objectInput.readObject();
            this.mapBackgroundDataMask = (boolean[][]) objectInput.readObject();
            this.fakeIsoLayers = (FakeIsoLayers[][]) objectInput.readObject();
            this.basicFloorId = objectInput.readByte();
            this.basicFloorIdTileNr = objectInput.readByte();
            this.mapExits = (MapExit[]) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }

    public void setMapExits(MapExit[] myMapExits) {
        this.mapExits = myMapExits;
    }

    @Override
    public MapExit[] getMapExits() {
        return this.mapExits;
    }

    public void setFakeIsoLayers(int x, int y, byte layer, byte imageId, byte tileNr, byte imageDirection) {
        FakeIsoLayers theNext;
        if (this.fakeIsoLayers[x][y] == null)
            this.fakeIsoLayers[x][y] = new FakeIsoLayers(imageId, tileNr, imageDirection);
        else
            this.fakeIsoLayers[x][y].Add(imageId, tileNr, imageDirection);
        theNext = this.fakeIsoLayers[x][y].getNext();
        for (int floor = 1; floor < layer; floor++) {
            if (theNext == null)
                theNext = new FakeIsoLayers(imageId, tileNr, imageDirection);
            else
                theNext.Add(imageId, tileNr, imageDirection);
            theNext = theNext.getNext();
        }
        if (layer != 0) {
            theNext.setNext(new FakeIsoLayers(imageId, tileNr, imageDirection));
        }
    }

    public void setMap(int x, int y, Dimension mapTileDim, byte basicFloorId, byte basicFloorIdTileNr) {
        this.basicFloorId = basicFloorId;
        this.basicFloorIdTileNr = basicFloorIdTileNr;
        this.tileMap.mapTileDim = mapTileDim;
        Dimension mapSize = new Dimension();
        mapSize.width = x;
        mapSize.height = y;
        this.tileMap.setMapSize(mapSize);
        this.mapBackgroundData = new byte[x][y][2];
        this.mapBackgroundDataMask = new boolean[x][y];
        for (int xx = 0; xx < x; xx++)
            for (int yy = 0; yy < y; yy++) {
                this.mapBackgroundData[xx][yy][0] = basicFloorId;
                this.mapBackgroundData[xx][yy][1] = basicFloorIdTileNr;
                this.mapBackgroundDataMask[xx][yy] = TileMap.TILE_FREE;
            }
        this.fakeIsoLayers = new FakeIsoLayers[x][y];
        for (int xx = 0; xx < x; xx++)
            for (int yy = 0; yy < y; yy++)
                this.fakeIsoLayers[xx][yy] = null;
    }

    public void setMapPoint(int x, int y, int map, int tileNr) {
        if (x >= this.tileMap.getMapSize().width || y >= this.tileMap.getMapSize().height) {
            Debug.signal(Debug.WARNING, null, "Tried to change a point inside of map, with coordinates over mapSize");
            return;
        }
        /*
        if( map > (.length) ) {
            Debug.signal( Debug.WARNING, null, "Tried to change a point inside of map, with a layer of declared layers " );
            return;
        }
         */
        this.mapBackgroundData[x][y][0] = (byte) map;
        this.mapBackgroundData[x][y][1] = (byte) tileNr;
        this.mapBackgroundDataMask[x][y] = TileMap.TILE_FREE;
    }

    @Override
    public byte[][][] getMapBackGroundData() {
        return this.mapBackgroundData;
    }

    @Override
    public byte getMapType() {
        return TileManagerFakeIso.MAPTYPE;
    }

    @Override
    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    @Override
    public void drawAllLayer(GraphicsDirector gDirector) {

        Drawable background = null; // background image

        for (int y = 0; y < this.tileMap.getMapSize().height; y++) {
            for (int x = 0; x < this.tileMap.getMapSize().width; x++) {
                if (getMapBackGroundData()[x][y][0] != this.basicFloorId) {
                    background = new MotionlessSprite(x * this.tileMap.getMapTileDim().width, // ground x=0
                    y * this.tileMap.getMapTileDim().height, // ground y=0
                    EnvironmentManager.getGraphicsForMaps(this.tileMap.getGraphicSet())[this.basicFloorId], // GroupOfGraphics
                    this.basicFloorIdTileNr, // number of internal tile
                    ImageLibRef.MAP_PRIORITY // priority
                    );
                    gDirector.addDrawable(background);
                    background = new MotionlessSprite(x * this.tileMap.getMapTileDim().width, // ground x=0
                    y * this.tileMap.getMapTileDim().height, // ground y=0
                    EnvironmentManager.getGraphicsForMaps(this.tileMap.getGraphicSet())[getMapBackGroundData()[x][y][0]], // GroupOfGraphics
                    getMapBackGroundData()[x][y][1], // number of internal tile
                    ImageLibRef.SECONDARY_MAP_PRIORITY // priority
                    );
                    gDirector.addDrawable(background);
                } else {
                    background = new MotionlessSprite(x * this.tileMap.getMapTileDim().width, // ground x=0
                    y * this.tileMap.getMapTileDim().height, // ground y=0
                    EnvironmentManager.getGraphicsForMaps(this.tileMap.getGraphicSet())[getMapBackGroundData()[x][y][0]], // GroupOfGraphics
                    getMapBackGroundData()[x][y][1], // number of internal tile
                    ImageLibRef.SECONDARY_MAP_PRIORITY // priority
                    );
                    gDirector.addDrawable(background);
                }
                if (getMapType() == TileMap.FAKEISO)
                    if (this.fakeIsoLayers[x][y] != null) {
                        Iterator singleData = this.fakeIsoLayers[x][y].getData().iterator();
                        while (singleData.hasNext()) {
                            byte[] singleByteData = (byte[]) singleData.next();
                            background = new MotionlessSprite(x * this.tileMap.getMapTileDim().width, // ground x=0
                            y * this.tileMap.getMapTileDim().height, // ground y=0
                            EnvironmentManager.getGraphicsForMaps(this.tileMap.getGraphicSet())[singleByteData[0]], // GroupOfGraphics
                            singleByteData[1], // number of internal tile
                            FakeIsoLayers.getPriority(singleByteData[2]) // priority
                            );
                            gDirector.addDrawable(background);
                        }
                    }
            }
        }
    }

    @Override
    public byte getBasicFloorId() {
        return this.basicFloorId;
    }

    @Override
    public void setBasicFloorId(byte value) {
        this.basicFloorId = value;
    }

    @Override
    public byte getBasicFloorNr() {
        return this.basicFloorIdTileNr;
    }

    @Override
    public void setBasicFloorNr(byte value) {
        this.basicFloorIdTileNr = value;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new MapExit object to the array {@link #mapExits mapExits}
    *
    * @return a new MapExit object
    */
    @Override
    public MapExit addMapExit(ScreenRectangle r) {
        return addMapExit(r, "");
    }

    @Override
    public MapExit addMapExit(ScreenRectangle r, String name) {
        MapExit myMapExit = new MapExit(r, name);

        if (this.mapExits == null) {
            this.mapExits = new MapExit[1];
            myMapExit.setMapExitID(0);
            this.mapExits[0] = myMapExit;
        } else {
            MapExit[] myMapExits = new MapExit[this.mapExits.length + 1];
            myMapExit.setMapExitID(this.mapExits.length);
            System.arraycopy(this.mapExits, 0, myMapExits, 0, this.mapExits.length);
            myMapExits[this.mapExits.length] = myMapExit;
            this.mapExits = myMapExits;
        }
        return myMapExit;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new MapExit object to the array {@link #mapExits mapExits}
     *
     * @param me MapExit object
     */
    @Override
    public void addMapExit(MapExit me) {
        if (this.mapExits == null) {
            this.mapExits = new MapExit[1];
            this.mapExits[0] = me;
        } else {
            MapExit[] myMapExits = new MapExit[this.mapExits.length + 1];
            System.arraycopy(this.mapExits, 0, myMapExits, 0, this.mapExits.length);
            myMapExits[this.mapExits.length] = me;
            this.mapExits = myMapExits;
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
    @Override
    public MapExit findTileMapExit(Rectangle fromPosition) {

        if (this.mapExits == null) {
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

        if (this.mapExits.length == 1)
            return this.mapExits[0];

        for (int i = 0; i < this.mapExits.length; i++) {
            if (this.mapExits[i].getMapExitSide() == MapExit.WEST && fromPosition.x <= this.tileMap.x + this.tileMap.width / 2)
                return this.mapExits[i];

            if (this.mapExits[i].getMapExitSide() == MapExit.EAST && fromPosition.x >= this.tileMap.x + this.tileMap.width / 2)
                return this.mapExits[i];

            if (this.mapExits[i].getMapExitSide() == MapExit.NORTH && fromPosition.y <= this.tileMap.y + this.tileMap.height / 2)
                return this.mapExits[i];

            if (this.mapExits[i].getMapExitSide() == MapExit.SOUTH && fromPosition.y >= this.tileMap.y + this.tileMap.height / 2)
                return this.mapExits[i];
        }

        return this.mapExits[0]; // default
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the eventual MapExit the given player is intersecting.
     *
     * @param rCurrent rectangle containing the player's current position, width & height
     * @return the ~Building:others tilemap the player is heading to (if he has reached it, or if there
     *         are any), null if none.
     */
    @Override
    public MapExit isIntersectingMapExit(int destX, int destY, Rectangle rCurrent) {
        if (this.mapExits == null)
            return null;

        for (int i = 0; i < this.mapExits.length; i++)
            if (this.mapExits[i].toRectangle().contains(destX, destY) && this.mapExits[i].toRectangle().intersects(rCurrent))
                return this.mapExits[i]; // mapExits reached

        return null;
    }

    @Override
    public boolean[][] getMapMask() {
        return this.mapBackgroundDataMask;
    }

    @Override
    public void freeMapBackGroundData() {
        this.mapBackgroundData = null;
    }

    /*  USED by Editor */
    @Override
    public void replaceGraphics(byte[][][] graphic) {
        this.mapBackgroundData = graphic;
    }

    @Override
    public void replaceMask(boolean[][] mask) {
        this.mapBackgroundDataMask = mask;
    }

    @Override
    public void replaceExits(MapExit[] exits) {
        this.mapExits = exits;
    }
}
