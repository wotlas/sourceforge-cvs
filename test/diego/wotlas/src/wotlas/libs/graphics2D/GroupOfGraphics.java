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
 
package wotlas.libs.graphics2D;

import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.libs.graphics2D.GraphicsDirector;
import wotlas.common.*;
import wotlas.libs.persistence.*;
import wotlas.common.universe.*;

import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;

 /** Group of graphics represents an Id+ the size of the Tiles inside the image, and the name of the image
  *
  * WHT'S THE USE of Group of graphics?
  *
  * well let's explain:
  *
  * Every TileMap, from Flat to FakeIso, needs to load images, with ImageIdentifier.
  * TileMap need many ImageIdentifier, but it need even information about 'em, 
  * beacause inside every image, there are at least 5 tiles.
  * To use the tiles, you need to know the dimension of 'em width*height this value
  * is saved inside <singleTileSize>
  *
  * How do you create an object? With this function:
  *     public GroupOfGraphics( byte Id, byte[] singleTileSize, String tileImageSet ) 
  *
  *  <Id> this Id it's used from the TileMap data
  *  <singleTileSize> was just explained
  *  <tileImageSet> is the name of the image with inside at least 5 tiles
  *
  * @author Diego
  * @see wotlas.common.universe.TileMap
  * @see wotlas.common.universe.TilePosition
  * @see wotlas.client.TileMapData
  */
 
public class GroupOfGraphics implements BackupReady {
    
    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;
    
    /**  Pre-definies set of data array[7] of GroupOfGraphics
     *
     */
    static public GroupOfGraphics[] DEMO_SET = {
    new  GroupOfGraphics( (byte)0, TileMap.PIXEL_50, "tilemaps-cat-3/openmap-set-1/basic_tile-0/grass-0.png" )
    ,new GroupOfGraphics( (byte)1, TileMap.PIXEL_50, "tilemaps-cat-3/openmap-set-1/add_tile-1/little_road-10.png" )
    ,new GroupOfGraphics( (byte)2, TileMap.PIXEL_50, "tilemaps-cat-3/openmap-set-1/add_tile-1/water-4.png" )
    ,new GroupOfGraphics( (byte)3, TileMap.PIXEL_50, "tilemaps-cat-3/fakeiso-set-0/floor-set-0/fix-5.png" )
    ,new GroupOfGraphics( (byte)4, TileMap.PIXEL_50, "tilemaps-cat-3/fakeiso-set-0/carpet-set-2/carpet-2.png" )
    ,new GroupOfGraphics( (byte)5, TileMap.PIXEL_80X40, "tilemaps-cat-3/fakeiso-set-0/wall-set-1/fix_wallwoodX9-23.png" )
    ,new GroupOfGraphics( (byte)6, TileMap.PIXEL_40X80, "tilemaps-cat-3/fakeiso-set-0/wall-set-1/fix_wallwoodY9-34.png" )
    };

 /*------------------------------------------------------------------------------------*/

    /**  <Id> this Id it's used from the TileMap data
     */
    private byte Id;
    /** read class description
     */
    private Dimension tileDim;
    /**  <tileImageSet> is the name of the image with inside at least 5 tiles
     */
    private String tileImageSet;

    transient private TilePosition posInsideTile;
    transient private ImageIdentifier image;
    
    transient private int Xlen;
    transient private int Ylen;
    transient protected int[][] Pos;

    public GroupOfGraphics() {
    }

    /** create the object, read class description
     */
    public GroupOfGraphics( byte Id, Dimension tileDim, String tileImageSet ) {
        this.Id = Id;
        this.tileDim = tileDim;
        this.tileImageSet = tileImageSet;
    }
    
    public void init( GraphicsDirector gDirector ) {
        image = new ImageIdentifier(tileImageSet);
        gDirector.getImageLibrary().loadImage( image );
    }
    
 /*------------------------------------------------------------------------------------*/

  /** String Info.
   */
    public String toString(){
         return "GroupOfGraphics - ";
    }

 /*------------------------------------------------------------------------------------*/

  /** id version of data, used in serialized persistance.
   */
    public int ExternalizeGetVersion(){
        return 1;
    }

 /*------------------------------------------------------------------------------------*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeByte( Id );
        objectOutput.writeObject( tileDim );
        objectOutput.writeObject( tileImageSet );
    }
    
 /*------------------------------------------------------------------------------------*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            Id =  objectInput.readByte();
            tileDim = ( Dimension ) objectInput.readObject();
            tileImageSet = ( String ) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }

    protected void ComputePos(BufferedImage allTile){
        Xlen = allTile.getWidth() / tileDim.width;
        Ylen = allTile.getHeight() / tileDim.height;
        Pos = new int[ Xlen * Ylen ][ 4 ];
        for(int b=0;b<Ylen;b++){
            for(int a=0;a<Xlen;a++){
                Pos[a+(b*Xlen)][0] = tileDim.width * a;
                Pos[a+(b*Xlen)][1] = tileDim.height * b;
                Pos[a+(b*Xlen)][2] = tileDim.width + ( tileDim.width * a );
                Pos[a+(b*Xlen)][3] = tileDim.height + ( tileDim.height * b );
            }
        }
    }

    public void drawMe( java.awt.Graphics2D gc, int myX, int myY, int internalTile, BufferedImage theTile ) {
       if(Pos==null)
           ComputePos(theTile);
       gc.drawImage( theTile, myX, myY, myX+tileDim.width, myY+tileDim.height
       ,Pos[internalTile][0],Pos[internalTile][1]
       ,Pos[internalTile][2],Pos[internalTile][3]
       ,null);
    }

    public ImageIdentifier getImage() {
        return image;
    }
}