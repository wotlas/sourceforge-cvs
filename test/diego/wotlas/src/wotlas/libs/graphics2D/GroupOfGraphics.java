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
    new  GroupOfGraphics( (byte)0, TileMap.PIXEL_50, TileMap.TILE, "tilemaps-cat-3/openmap-set-1/basic_tile-0/grass-0.png" )
    ,new GroupOfGraphics( (byte)1, TileMap.PIXEL_50, TileMap.TILE, "tilemaps-cat-3/openmap-set-1/add_tile-1/little_road-10.png" )
    ,new GroupOfGraphics( (byte)2, TileMap.PIXEL_50, TileMap.TILE, "tilemaps-cat-3/openmap-set-1/add_tile-1/water-4.png" )
    ,new GroupOfGraphics( (byte)3, TileMap.PIXEL_50, TileMap.TILE, "tilemaps-cat-3/fakeiso-set-0/floor-set-0/fix-5.png" )
    ,new GroupOfGraphics( (byte)4, TileMap.PIXEL_50, TileMap.TILE, "tilemaps-cat-3/fakeiso-set-0/carpet-set-2/carpet-2.png" )
    ,new GroupOfGraphics( (byte)5, TileMap.PIXEL_80X40, TileMap.WALLX, "tilemaps-cat-3/fakeiso-set-0/wall-set-1/fix_wallwoodX6-20.png" )
    ,new GroupOfGraphics( (byte)6, TileMap.PIXEL_40X80, TileMap.WALLY, "tilemaps-cat-3/fakeiso-set-0/wall-set-1/fix_wallwoodY6-31.png" )
    ,new GroupOfGraphics( (byte)7, TileMap.PIXEL_80X40, TileMap.WALLX, "tilemaps-cat-3/fakeiso-set-0/wall-set-1/fix_wallwoodX3-3.png" )
    ,new GroupOfGraphics( (byte)8, TileMap.PIXEL_40X80, TileMap.WALLY, "tilemaps-cat-3/fakeiso-set-0/wall-set-1/fix_wallwoodY3-11.png" )
    ,new GroupOfGraphics( (byte)9, TileMap.PIXEL_70X35, TileMap.TILE, "tilemaps-cat-3/fakeiso-set-0/door-set-3/door-x-0.png" )
    };

    static public GroupOfGraphics[] ROGUE_SET = {
    new  GroupOfGraphics( (byte)0, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_armor32-0.png" )
    ,new GroupOfGraphics( (byte)1, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_classm32-1.png" )
    ,new GroupOfGraphics( (byte)2, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_dragon32-2.png" )
    ,new GroupOfGraphics( (byte)3, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg-dungeon32-3.png" )
    ,new GroupOfGraphics( (byte)4, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_edging132-4.png" )
    ,new GroupOfGraphics( (byte)5, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_edging232-5.png" )
    ,new GroupOfGraphics( (byte)6, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_edging332-6.png" )
    ,new GroupOfGraphics( (byte)7, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_effects32-7.png" )
    ,new GroupOfGraphics( (byte)8, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_extra132-8.png" )
    ,new GroupOfGraphics( (byte)9, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_features32-9.png" )
    ,new GroupOfGraphics( (byte)10, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_grounds32-11.png" )
    ,new GroupOfGraphics( (byte)11, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_humans32-12.png" )
    ,new GroupOfGraphics( (byte)12, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_jewls32-14.png" )
    ,new GroupOfGraphics( (byte)13, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_magic32-15.png" )
    ,new GroupOfGraphics( (byte)14, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_monster132-17.png" )
    ,new GroupOfGraphics( (byte)15, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_monster232-18.png" )
    ,new GroupOfGraphics( (byte)16, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_monster332-19.png" )
    ,new GroupOfGraphics( (byte)17, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_monster432-20.png" )
    ,new GroupOfGraphics( (byte)18, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_monster532-21.png" )
    ,new GroupOfGraphics( (byte)19, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_monster632-22.png" )
    ,new GroupOfGraphics( (byte)20, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_monster732-23.png" )
    ,new GroupOfGraphics( (byte)21, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_people32-24.png" )
    ,new GroupOfGraphics( (byte)22, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_potions32-25.png" )
    ,new GroupOfGraphics( (byte)23, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_undead32-26.png" )
    ,new GroupOfGraphics( (byte)24, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_uniques32-27.png" )
    ,new GroupOfGraphics( (byte)25, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_wands32-28.png" )
    ,new GroupOfGraphics( (byte)26, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_weapons32-29.png" )
    ,new GroupOfGraphics( (byte)27, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_food32-10.png" )
    ,new GroupOfGraphics( (byte)28, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_iso32-13.png" )
    ,new GroupOfGraphics( (byte)29, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/dg_misc32-16.png" )
    }; 

    /*------------------------------------------------------------------------------------*/

    /**  <Id> this Id it's used from the TileMap data
     */
    private byte Id;
    /**  ...............
     */
    private byte tileType;
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
    transient protected short[][] Offset;

    public GroupOfGraphics() {
    }

    /** create the object, read class description
     */
    public GroupOfGraphics( byte Id, Dimension tileDim, byte tileType, String tileImageSet ) {
        this.Id = Id;
        this.tileDim = tileDim;
        this.tileType = tileType;
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
        objectOutput.writeByte( tileType );
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
            tileType =  objectInput.readByte();
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
        if(Pos==null){
            ComputePos(theTile);
            if( tileType == TileMap.WALLX )
                ComputePosWall( true );
            if( tileType == TileMap.WALLY )
                ComputePosWall( false );
        }
        if( tileType == TileMap.WALLX )
            gc.drawImage( theTile
            , myX+Offset[internalTile][0]-30
            , myY+Offset[internalTile][1]+10
            , myX+tileDim.width-30, myY+tileDim.height+10
            ,Pos[internalTile][0],Pos[internalTile][1]
            ,Pos[internalTile][2],Pos[internalTile][3]
            ,null);
        else if( tileType == TileMap.WALLY )
            gc.drawImage( theTile
            , myX+Offset[internalTile][0]+10
            , myY+Offset[internalTile][1]-30
            , myX+tileDim.width+10, myY+tileDim.height-30
            ,Pos[internalTile][0],Pos[internalTile][1]
            ,Pos[internalTile][2],Pos[internalTile][3]
            ,null);
        else 
            gc.drawImage( theTile, myX, myY, myX+tileDim.width, myY+tileDim.height
            ,Pos[internalTile][0],Pos[internalTile][1]
            ,Pos[internalTile][2],Pos[internalTile][3]
            ,null);
    }

    public ImageIdentifier getImage() {
        return image;
    }

    public void ComputePosWall(boolean isOrizzontal){
        Offset = new short[ Xlen * Ylen ][ 2 ];
        /* THIS OPERATIONS ARE DONE TO CHECK THAT THE LESS POSSIBLE TRANSPARENT AREA
         * IS COPIED DURING VIDEO OPERATIONS */
        try{
            if(isOrizzontal){
                Pos[1][1] += 20 ;
                Offset[1][1] = 20;
                for(int index=2;index<6;index++){
                    Offset[index][0] = new Integer(10*(index-1)).shortValue();
                    Pos[index][0] += 10*(index-1);
                }
            } 
            else {
                Pos[1][0] += 20 ;
                Offset[1][0] = 20;
                for(int index=2;index<6;index++){
                    Offset[index][1] = new Integer(10*(index-1)).shortValue();
                    Pos[index][1] += 10*(index-1);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.err.println( "GraphicWall ComputeposWall Filename" + tileImageSet );
        }
    }
    
    /*
    public void ComputePosDoor(boolean isOrizzontal){
        try{
            Xlen = new Integer(icon.getIconWidth() / XSinglePic).shortValue();
            Ylen = new Integer(icon.getIconHeight() / YSinglePic).shortValue();
            Images = new Integer(Xlen*Ylen).shortValue();
            Offset = null;
            Pos = new short[Images][4];
            for(int b=0;b<Ylen;b++){
                for(int a=0;a<Xlen;a++){
                    Pos[a+(b*Xlen)][0] = new Integer(0+(XSinglePic*a)).shortValue();
                    Pos[a+(b*Xlen)][1] = new Integer(0+(YSinglePic*b)).shortValue();
                    Pos[a+(b*Xlen)][2] = new Integer(XSinglePic+(XSinglePic*a)).shortValue();
                    Pos[a+(b*Xlen)][3] = new Integer(YSinglePic+(YSinglePic*b)).shortValue();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("GraphicWall ComputeposWall Filename"+FileName);
        }
    }
     public void externalPaint(Graphics g,int startX,int startY
    ,int imageNumber,JComponent here) {
        switch (Type){
            case XWALL_THIN:
                g.drawImage(icon.getImage()
                ,startX+Offset[imageNumber][0]-30,startY+Offset[imageNumber][1]+10
                ,startX+XSinglePic-30,startY+YSinglePic+10
                ,Pos[imageNumber][0],Pos[imageNumber][1]
                ,Pos[imageNumber][2],Pos[imageNumber][3]
                ,here);
                break;
            case YWALL_THIN:
                g.drawImage(icon.getImage()
                ,startX+Offset[imageNumber][0]+10,startY+Offset[imageNumber][1]-30
                ,startX+XSinglePic+10,startY+YSinglePic-30
                ,Pos[imageNumber][0],Pos[imageNumber][1]
                ,Pos[imageNumber][2],Pos[imageNumber][3]
                ,here);
                break;
            case DOOR_X:
                g.drawImage(icon.getImage()
                ,startX-20,startY+15
                ,startX+XSinglePic-20,startY+YSinglePic+15
                ,Pos[imageNumber][0],Pos[imageNumber][1]
                ,Pos[imageNumber][2],Pos[imageNumber][3]
                ,here);
                break;
        }
    }
   */
}