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

import wotlas.utils.*;

 /** MapExit class
  *
  * @author Petrus, Aldiss, Diego
  */

public class MapExit extends ScreenRectangle
{
  /** id used in Serialized interface.
   */
  private static final long serialVersionUID = 556565L;

  /** MapExit type
   */
  public final static byte INTERIOR_MAP_EXIT = 0;
  public final static byte BUILDING_EXIT     = 1;
  public final static byte TOWN_EXIT         = 2;
  public final static byte TILEMAP_EXIT      = 3;

  /** MapExit Side
   */
  public final static byte NONE  = 0; // one-way MapExit
  public final static byte NORTH = 1;
  public final static byte SOUTH = 2;
  public final static byte WEST  = 3;
  public final static byte EAST  = 4;
  
 /*------------------------------------------------------------------------------------*/

  /** used to name an exit, used by the Editor to connect mapexit each other
   */
   public String name;
   
  /** ID of the MapExit (index in the array {@link Room#mapExits Room.mapExits})
   */
   public int mapExitID;
  
  /** one of INTERIOR_MAP_EXIT, BUILDING_EXIT, TOWN_EXIT,TILEMAP_EXIT
   */
   public byte type;

  /** 
   */
   public int z; // (z only used by fakeIso maps, it's the third point after x,y,<z>)
   
  /** location of the target
   */
   public WotlasLocation targetWotlasLocation;
   
  /** MapExit Side
   * 0 if none (one-way MapExit)
   */
   public byte mapExitSide;

  /** Position on the target map.
   */
   public ScreenPoint targetPosition;

  /** Target player orientation
   */
   public float targetOrientation;
   
  /** knowledge required to use MapExit
   * -1 if no knowledge required
   */
   public int requiredKnowledgeID;

 /*------------------------------------------------------------------------------------*/

  /** MapExit Location : the Map
   */
   transient private WotlasLocation mapExitLocation;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
   public MapExit() {}

 /*------------------------------------------------------------------------------------*/

    /** Constructor with ScreenRectangle.
    */
    public MapExit(ScreenRectangle r) {
        super(r);
        this.name = "";
    }

 /*------------------------------------------------------------------------------------*/

    /** Constructor with ScreenRectangle and Name of the Exit
    */
    public MapExit(ScreenRectangle r, String name) {
        super(r);
        this.name = name;
    }
   
 /*------------------------------------------------------------------------------------*/

  /** Constructor with ScreenRectangle & type.
   */
   public MapExit(ScreenRectangle r, byte type) {
      super(r);
      this.type = type;
      this.name = "";
   }

 /*------------------------------------------------------------------------------------*/

  /*
   * List of setter and getter used for persistence
   */
  
  public void setMapExitID(int myMapExitID) {
    this.mapExitID = myMapExitID;
  }
  public int getMapExitID() {
    return mapExitID;
  }
  public void setType(byte myType) {
    this.type = myType;
  }
  public byte getType() {
    return type;
  }
  public void setTargetWotlasLocation(WotlasLocation myTargetWotlasLocation) {
    this.targetWotlasLocation = myTargetWotlasLocation;
  }
  public WotlasLocation getTargetWotlasLocation() {
    return new WotlasLocation( targetWotlasLocation );
  }
  public void setTargetPosition(ScreenPoint myTargetPosition) {
    this.targetPosition = myTargetPosition;
  }
  public ScreenPoint getTargetPosition() {
    return targetPosition;
  }
  public void setTargetOrientation(float targetOrientation) {
    this.targetOrientation = targetOrientation;
  }
  public float getTargetOrientation() {
    return targetOrientation;
  }
  public void setMapExitSide(byte mapExitSide) {
    this.mapExitSide = mapExitSide;
  }
  public byte getMapExitSide() {
    return mapExitSide;
  }
  public void setRequiredKnowledgeID(int myRequiredKnowledgeID) {
    this.requiredKnowledgeID = myRequiredKnowledgeID;
  }
  public int getRequiredKnowledgeID() {
    return requiredKnowledgeID;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the insertion point associated to this MapExit.
   */
    public ScreenPoint getInsertionPoint() {
        ScreenPoint p = new ScreenPoint();

        switch( mapExitSide ) {
          case MapExit.SOUTH:
            p.x = x + width/2;
            p.y = y - 20;
            break;
          case MapExit.EAST:
            p.x = x - 20;
            p.y = y + height/2;
            break;
          default:
            p.x = x + width/2;
            p.y = y + height/2;            
            break;
        }

        return p;
    }

 /*------------------------------------------------------------------------------------*/

  /** To get the local orientation when arriving on this MapExit.
   */
    public float getLocalOrientation() {
        switch( mapExitSide ) {

          case MapExit.NORTH:
            return (float)(Math.PI/2);

          case MapExit.SOUTH:
            return (float)(-Math.PI/2);

          case MapExit.EAST:
            return (float)(Math.PI);

          case MapExit.WEST:
          default:
            return 0.0f;
        }
    }

 /*------------------------------------------------------------------------------------*/

  /** To set this MapExit's location : the Map this MapExit belongs to.
   * @param mapExitLocation WotlasLocation of this MapExit.
   */
   public void setMapExitLocation(WotlasLocation mapExitLocation) {
     this.mapExitLocation = mapExitLocation;
   }

 /*------------------------------------------------------------------------------------*/
 
  /** To get the WotlasLocation of this MapExit (i.e. the Map possessing this
   *  MapExit ). To get the location where this MapExit sends to use the
   *  getTargetWotlasLocation method.
   *
   *  @return WotlasLocation of this MapExit.
   */
   public WotlasLocation getMapExitLocation() {
     return new WotlasLocation( mapExitLocation );
   }
  
 /*------------------------------------------------------------------------------------*/

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
        super.writeExternal(objectOutput);
        objectOutput.writeInt( z );
        objectOutput.writeInt( mapExitID );
        objectOutput.writeByte( mapExitSide );
        objectOutput.writeInt( requiredKnowledgeID );
        objectOutput.writeFloat( targetOrientation );
        objectOutput.writeObject( targetPosition );
        objectOutput.writeObject( targetWotlasLocation );
        objectOutput.writeByte( type );
        objectOutput.writeUTF( name );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal(objectInput);
            z = objectInput.readInt();
            mapExitID = objectInput.readInt();
            mapExitSide = objectInput.readByte();
            requiredKnowledgeID = objectInput.readInt();
            targetOrientation = objectInput.readFloat();
            targetPosition = ( ScreenPoint ) objectInput.readObject();
            targetWotlasLocation = ( WotlasLocation ) objectInput.readObject();
            type = objectInput.readByte();
            name = objectInput.readUTF();
       } else {
            // to do.... when new version
        }
    }
    
    public String toString(){
        String tmpLoc="";
        if( targetWotlasLocation == null )
            tmpLoc = "need destination";
        if( name == null )
            return "No Name"+tmpLoc;
        if( name.length()==0 )
            return "No Name"+tmpLoc;
        return name+tmpLoc;
    }
    
    public ScreenPoint getScreenPointFromExitLocation(){
        ScreenPoint p = new ScreenPoint();
        p.x = x;
        p.y = y;
        return p;
    }
    
    public void setName(String name){
        this.name = name;
    }
}