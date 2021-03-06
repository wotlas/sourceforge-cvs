/*
 *
 *
 *
 * GRAN CASINO IN CORSO:
 * IL SALVATAGGIO DEI GROUP OF GRAPHICS DENTRO LE TILEMAPS
 * CREA NUOVE DISTINTE ISTANZE DEGLI STESSI IN MEMORIA.
 *
 * IO INVECE VOGLIO UNA SOLA ISTANZA DI QUESTI COSI :)
 *
 *
 *
 *
 *
 *
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

package wotlas.libs.graphics2d;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import wotlas.common.universe.TileMap;
import wotlas.libs.persistence.BackupReady;

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
    static public GroupOfGraphics[] DEMO_SET = { new GroupOfGraphics((byte) 0, TileMap.PIXEL_50, TileMap.TILE, "tilemaps-cat-3/openmap-set-1/basic_tile-0/grass-0.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 1, TileMap.PIXEL_50, TileMap.TILE, "tilemaps-cat-3/openmap-set-1/add_tile-1/little_road-10.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 2, TileMap.PIXEL_50, TileMap.TILE, "tilemaps-cat-3/openmap-set-1/add_tile-1/water-4.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 3, TileMap.PIXEL_50, TileMap.TILE, "tilemaps-cat-3/fakeiso-set-0/floor-set-0/fix-5.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 4, TileMap.PIXEL_50, TileMap.TILE, "tilemaps-cat-3/fakeiso-set-0/carpet-set-2/carpet-2.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 5, TileMap.PIXEL_80X40, TileMap.WALLX, "tilemaps-cat-3/fakeiso-set-0/wall-set-1/fix_wallwoodX6-20.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 6, TileMap.PIXEL_40X80, TileMap.WALLY, "tilemaps-cat-3/fakeiso-set-0/wall-set-1/fix_wallwoodY6-31.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 7, TileMap.PIXEL_80X40, TileMap.WALLX, "tilemaps-cat-3/fakeiso-set-0/wall-set-1/fix_wallwoodX3-3.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 8, TileMap.PIXEL_40X80, TileMap.WALLY, "tilemaps-cat-3/fakeiso-set-0/wall-set-1/fix_wallwoodY3-11.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 9, TileMap.PIXEL_70X35, TileMap.TILE, "tilemaps-cat-3/fakeiso-set-0/door-set-3/door-x-0.png", TileMap.TILE_FREE) };

    static public GroupOfGraphics[] ROGUE_SET = { new GroupOfGraphics((byte) 0, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/ground-0/onlygrounds-0.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 1, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/ground-0/tree-1.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 2, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/ground-0/roads-2.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 3, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/ground-0/roads2-3.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 4, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/ground-0/mixwater-4.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 5, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/ground-0/dungeons-5.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 6, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/ground-0/strange-6.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 7, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/ground-0/wall-7.png", TileMap.TILE_NOT_FREE) };
    /*
        static public GroupOfGraphics[] ROGUE_SET_ISO = {
        new GroupOfGraphics( (byte)0, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/ground/dg_iso32-13.png" )
        }; 
    */
    static public GroupOfGraphics[] ROGUE_NPC_SET = { new GroupOfGraphics((byte) 0, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/classes-0.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 1, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_dragon32-1.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 2, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_humans32-2.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 3, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_monster132-3.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 4, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_monster232-4.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 5, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_monster332-5.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 6, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_monster432-6.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 7, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_monster532-7.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 8, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_monster632-8.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 9, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_monster732-9.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 10, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_people32-10.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 11, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_undead32-11.png", TileMap.TILE_NOT_FREE), new GroupOfGraphics((byte) 12, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/npc-1/dg_uniques32-12.png", TileMap.TILE_NOT_FREE) };

    static public GroupOfGraphics[] ROGUE_EFFECT_SET = { new GroupOfGraphics((byte) 0, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/effects-2/dg_effects32-0.png", TileMap.TILE_FREE) };

    static public GroupOfGraphics[] ROGUE_ITEM_SET = { new GroupOfGraphics((byte) 0, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/dg_armor32-0.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 1, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/dg_weapons32-1.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 2, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/dg_wands32-2.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 3, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/dg_potions32-3.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 4, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/dg_books32-4.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 5, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/dg_jewls32-5.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 6, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/dg_food32-6.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 7, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/dg_misc32-7.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 8, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/fountains-8.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 9, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/doors-9.png", TileMap.TILE_FREE), new GroupOfGraphics((byte) 10, TileMap.PIXEL_32, TileMap.TILE, "tilemaps-cat-3/angband-rougue-library-2/item-3/system-10.png", TileMap.TILE_FREE) };

    /*------------------------------------------------------------------------------------*/

    /**  <Id> this Id it's used from the TileMap data
     */
    private byte Id;
    /**  ...............
     */
    private boolean freeWalk;
    /**  ...............
     */
    private byte tileType;
    /** read class description
     */
    private Dimension tileDim;
    /**  <tileImageSet> is the name of the image with inside at least 5 tiles
     */
    private String tileImageSet;

    transient private ImageIdentifier image;

    transient private int Xlen;
    transient private int Ylen;
    transient protected int[][] Pos;
    transient protected short[][] Offset;

    public GroupOfGraphics() {
    }

    /** create the object, read class description
     */
    public GroupOfGraphics(byte Id, Dimension tileDim, byte tileType, String tileImageSet, boolean freeWalk) {
        this.Id = Id;
        this.tileDim = tileDim;
        this.tileType = tileType;
        this.freeWalk = freeWalk;
        this.tileImageSet = tileImageSet;
    }

    public void init(GraphicsDirector gDirector) {
        this.image = new ImageIdentifier(this.tileImageSet);
        gDirector.getImageLibrary().loadImage(this.image);
    }

    /*------------------------------------------------------------------------------------*/

    /** String Info.
     */
    @Override
    public String toString() {
        return "GroupOfGraphics - ";
    }

    /*------------------------------------------------------------------------------------*/

    /** id version of data, used in serialized persistance.
     */
    public int ExternalizeGetVersion() {
        return 1;
    }

    /*------------------------------------------------------------------------------------*/

    /** write object data with serialize.
     */
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeByte(this.Id);
        objectOutput.writeObject(this.tileDim);
        objectOutput.writeByte(this.tileType);
        objectOutput.writeObject(this.tileImageSet);
        objectOutput.writeBoolean(this.freeWalk);
    }

    /*------------------------------------------------------------------------------------*/

    /** read object data with serialize.
     */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.Id = objectInput.readByte();
            this.tileDim = (Dimension) objectInput.readObject();
            this.tileType = objectInput.readByte();
            this.tileImageSet = (String) objectInput.readObject();
            this.freeWalk = objectInput.readBoolean();
        } else {
            // to do.... when new version
        }
    }

    protected void ComputePos(BufferedImage allTile) {
        this.Xlen = allTile.getWidth() / this.tileDim.width;
        this.Ylen = allTile.getHeight() / this.tileDim.height;
        this.Pos = new int[this.Xlen * this.Ylen][4];
        for (int b = 0; b < this.Ylen; b++) {
            for (int a = 0; a < this.Xlen; a++) {
                this.Pos[a + (b * this.Xlen)][0] = this.tileDim.width * a;
                this.Pos[a + (b * this.Xlen)][1] = this.tileDim.height * b;
                this.Pos[a + (b * this.Xlen)][2] = this.tileDim.width + (this.tileDim.width * a);
                this.Pos[a + (b * this.Xlen)][3] = this.tileDim.height + (this.tileDim.height * b);
            }
        }
    }

    public void drawMe(java.awt.Graphics2D gc, int myX, int myY, int internalTile, BufferedImage theTile) {
        if (this.Pos == null) {
            ComputePos(theTile);
            // if( tileType == TileMap.TILE )
            // do nothing;
            if (this.tileType == TileMap.WALLX)
                ComputePosWall(true);
            else if (this.tileType == TileMap.WALLY)
                ComputePosWall(false);
        }
        if (this.tileType == TileMap.WALLX)
            gc.drawImage(theTile, myX + this.Offset[internalTile][0] - 30, myY + this.Offset[internalTile][1] + 10, myX + this.tileDim.width - 30, myY + this.tileDim.height + 10, this.Pos[internalTile][0], this.Pos[internalTile][1], this.Pos[internalTile][2], this.Pos[internalTile][3], null);
        else if (this.tileType == TileMap.WALLY)
            gc.drawImage(theTile, myX + this.Offset[internalTile][0] + 10, myY + this.Offset[internalTile][1] - 30, myX + this.tileDim.width + 10, myY + this.tileDim.height - 30, this.Pos[internalTile][0], this.Pos[internalTile][1], this.Pos[internalTile][2], this.Pos[internalTile][3], null);
        else
            gc.drawImage(theTile, myX, myY, myX + this.tileDim.width, myY + this.tileDim.height, this.Pos[internalTile][0], this.Pos[internalTile][1], this.Pos[internalTile][2], this.Pos[internalTile][3], null);
    }

    public ImageIdentifier getImage() {
        return this.image;
    }

    public void ComputePosWall(boolean isOrizzontal) {
        this.Offset = new short[this.Xlen * this.Ylen][2];
        /* THIS OPERATIONS ARE DONE TO CHECK THAT THE LESS POSSIBLE TRANSPARENT AREA
         * IS COPIED DURING VIDEO OPERATIONS */
        try {
            if (isOrizzontal) {
                this.Pos[1][1] += 20;
                this.Offset[1][1] = 20;
                for (int index = 2; index < 6; index++) {
                    this.Offset[index][0] = new Integer(10 * (index - 1)).shortValue();
                    this.Pos[index][0] += 10 * (index - 1);
                }
            } else {
                this.Pos[1][0] += 20;
                this.Offset[1][0] = 20;
                for (int index = 2; index < 6; index++) {
                    this.Offset[index][1] = new Integer(10 * (index - 1)).shortValue();
                    this.Pos[index][1] += 10 * (index - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("GraphicWall ComputeposWall Filename" + this.tileImageSet);
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

    public ImageIcon getAsIcon(int internalTile, ImageLibrary imageLib) {
        if (this.image == null)
            this.image = new ImageIdentifier(this.tileImageSet);
        imageLib.loadImage(this.image);
        BufferedImage theTile = imageLib.getImage(this.image);
        if (this.Pos == null)
            ComputePos(theTile);
        BufferedImage subImage;
        subImage = theTile.getSubimage(this.Pos[internalTile][0], this.Pos[internalTile][1], this.tileDim.width, this.tileDim.height);
        return new ImageIcon(subImage);
    }

    public int totalImage() {
        return this.Xlen * this.Ylen;
    }

    static public void initGroupOfGraphics(GraphicsDirector gDirector, GroupOfGraphics[] groupOfGraphics) {
        for (int index = 0; index < groupOfGraphics.length; index++)
            groupOfGraphics[index].init(gDirector);
        return;
    }

    public boolean getFreeStatus() {
        return this.freeWalk;
    }
}