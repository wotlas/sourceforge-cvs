/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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

package wotlas.common.objects;

import wotlas.libs.persistence.*;

/** used to define and manage differnt item types,
 * for example potions and armors
 *
 * @author Diego
 */

public class ItemType implements BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;
    
    /*
    all the graphics : fakeiso/flat/normal wotlas
    should work with "near" the same item graphics
    tileFlat50
    tileFlat32
    */

    private int id;
    private int[] maskItemPosition;
    private int[] maskItemType;
    private byte[] defaultTileSet;
    private byte[] defaultTileSetImageNr;

    static public final int MISCELANEUS = 0;
    static public final int POTION = 1;
    static public final int SCROLL = 2;
    static public final int WAND   = 3;
    static public final int STAFF  = 4;
    static public final int WEAPON = 5;
    static public final int ARMOR  = 6;
    static public final int CONTAINER        = 7;
    static public final int LIQUID_CONTAINER = 8;
    static public final int FOOD      = 9;
    static public final int KEY       = 10;
    static public final int PEN       = 11;
    static public final int PAPER     = 12;
    static public final int BOOK      = 13;
    static public final int MONEY     = 14;
    static public final int LIGHT     = 15;
    static public final int BOW       = 16;
    static public final int AMMO      = 17;
    static public final int TREASURE  = 18;
    static public final int TRAP      = 19;
    static public final int FOUNTAIN  = 20;

    static public final String[] itemType = {
    "miscelaneus","potion","scroll","wand","staff"
    ,"weapon","armor","container","liquid container"
    ,"food","key","pen","paper","book","money","light"
    ,"bow","ammo","treasure","trap","fountain"};

    public ItemType(int type){
        id = type;
        maskItemType = new int[1];
        defaultTileSet = new byte[1];
        defaultTileSetImageNr = new byte[1];
    }

    public ItemType(int type, byte[] defaultTileSet, byte defaultTileSetImageNg){
        id = type;
        maskItemType = new int[1];
        defaultTileSet = new byte[1];
        defaultTileSetImageNr = new byte[1];
    }
/*
    static public itemMiscelaneus(){
                
    }
    static public int MISCELANEUS = 0;
    static final int POTION = 1;
    static final int SCROLL = 2;
    static final int WAND   = 3;
    static final int STAFF  = 4;
    static final int WEAPON = 5;
    static final int ARMOR  = 6;
    static final int CONTAINER        = 7;
    static final int LIQUID_CONTAINER = 8;
    static final int FOOD      = 9;
    static final int KEY       = 10;
    static final int PEN       = 11;
    static final int PAPER     = 12;
    static final int BOOK      = 13;
    static final int MONEY     = 14;
    static final int LIGHT     = 15;
    static final int BOW       = 16;
    static final int AMMO      = 17;
    static final int TREASURE  = 18;
    static final int TRAP      = 19;
    static final int FOUNTAIN  = 20;
*/
  /** id version of data, used in serialized persistance.
   */
    public int ExternalizeGetVersion(){
        return 1;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeInt( id );
        objectOutput.writeObject( maskItemPosition );
        objectOutput.writeObject( maskItemType );
        objectOutput.writeObject( defaultTileSet );
        objectOutput.writeObject( defaultTileSetImageNr );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            id = objectInput.readInt();
            maskItemPosition = ( int[] ) objectInput.readObject();
            maskItemType = ( int[] ) objectInput.readObject();
            defaultTileSet = ( byte[] ) objectInput.readObject();
            defaultTileSetImageNr = ( byte[] ) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }
}