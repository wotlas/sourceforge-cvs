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

import wotlas.libs.persistence.*;
import wotlas.common.*;

import java.io.*;
import java.util.*;

 /** A FakeIsoLayers represents the plane of a house, from first floor to roof.
  *
  * @author Diego
  * @see wotlas.common.universe.TileMap
  * @see wotlas.common.universe.TileMapData
  */
 
public class FakeIsoLayers implements BackupReady {
    
    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;
    
    public static final byte NO_FLOOR = -1;
    
    public static final byte HOUSE_FLOOR_DIR            = 1;
    public static final byte CARPET_FLOOR_DIR           = 5;
    public static final byte PRIMARY_WALL_ANGLE_DIR     = 7;
    public static final byte PRIMARY_WALL_POS1_DIR_X    = 9;
    public static final byte PRIMARY_WALL_POS2_DIR_X    = 11;
    public static final byte PRIMARY_WALL_END_DIR_X     = 13;
    public static final byte PRIMARY_WALL_ANGLE_DIR_Y   = 15;
    public static final byte PRIMARY_WALL_POS1_DIR_Y    = 17;
    public static final byte PRIMARY_WALL_POS2_DIR_Y    = 19;
    public static final byte PRIMARY_WALL_END_DIR_Y     = 21;
    public static final byte CENTRAL_FLOOR_DIR          = 23;
    public static final byte SECONDARY_WALL_ANGLE_DIR_X = 25;
    public static final byte SECONDARY_WALL_POS1_DIR_X  = 27;
    public static final byte SECONDARY_WALL_POS2_DIR_X  = 29;
    public static final byte SECONDARY_WALL_END_DIR_X   = 31;
    public static final byte SECONDARY_WALL_ANGLE_DIR_Y = 33;
    public static final byte SECONDARY_WALL_POS1_DIR_Y  = 35;
    public static final byte SECONDARY_WALL_POS2_DIR_Y  = 37;
    public static final byte SECONDARY_WALL_END_DIR_Y   = 39;
    
    FakeIsoLayers next;
    Vector data;

    public FakeIsoLayers(byte imageId, byte tileNr, byte imageDirection) {
        next = null;
        data = new Vector();
        byte[] information = new byte[3]; 
        information[0] = imageId;
        information[1] = tileNr;
        information[2] = imageDirection;
        data.add( information ) ;
    }

    public void Add(byte imageId, byte tileNr, byte imageDirection) {
        byte[] information = new byte[3]; 
        information[0] = imageId;
        information[1] = tileNr;
        information[2] = imageDirection;
        data.add( information ) ;
    }
    
    public FakeIsoLayers() {
        next = null;
    }

    public Vector getData() {
        return data;
    }

    public FakeIsoLayers getNext() {
        return next;
    }
    
    public boolean isNext() {
        if( next == null )
            return false;
        else
            return true;
    }
    
    public void setNext( FakeIsoLayers next ) {
        this.next = next;
    }
    
    public static short getPriority( byte imageDirection ) {
        switch ( imageDirection ){
            case HOUSE_FLOOR_DIR:
                return ImageLibRef.HOUSE_FLOOR_PRIORITY;
            case CARPET_FLOOR_DIR:
                return ImageLibRef.CARPET_FLOOR_PRIORITY;
            case PRIMARY_WALL_ANGLE_DIR:
                return ImageLibRef.PRIMARY_WALL_ANGLE_PRIORITY;
            case PRIMARY_WALL_POS1_DIR_X:
            case PRIMARY_WALL_POS1_DIR_Y:
                return ImageLibRef.PRIMARY_WALL_POS1_PRIORITY;
            case PRIMARY_WALL_POS2_DIR_X:
            case PRIMARY_WALL_POS2_DIR_Y:
                return ImageLibRef.PRIMARY_WALL_POS2_PRIORITY;
            case PRIMARY_WALL_END_DIR_X:
            case PRIMARY_WALL_END_DIR_Y:
                return ImageLibRef.PRIMARY_WALL_END_PRIORITY;
            case CENTRAL_FLOOR_DIR:
                return ImageLibRef.CENTRAL_FLOOR_PRIORITY;
            case SECONDARY_WALL_ANGLE_DIR_X:
            case SECONDARY_WALL_ANGLE_DIR_Y:
                return ImageLibRef.SECONDARY_WALL_ANGLE_PRIORITY;
            case SECONDARY_WALL_POS1_DIR_X:
            case SECONDARY_WALL_POS1_DIR_Y:
                return ImageLibRef.SECONDARY_WALL_POS1_PRIORITY;
            case SECONDARY_WALL_POS2_DIR_X:
            case SECONDARY_WALL_POS2_DIR_Y:
                return ImageLibRef.SECONDARY_WALL_POS2_PRIORITY;
            case SECONDARY_WALL_END_DIR_X:
            case SECONDARY_WALL_END_DIR_Y:
                return ImageLibRef.SECONDARY_WALL_END_PRIORITY;
        }
        return -1;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeObject( data );
        objectOutput.writeObject( next );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            data = ( Vector ) objectInput.readObject();
            next = ( FakeIsoLayers ) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** id version of data, used in serialized persistance.
   */
    public int ExternalizeGetVersion(){
        return 1;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** String Info.
   */
    public String toString(){
         return "FakeIsoLayers ";
    }
}