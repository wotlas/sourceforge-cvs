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

import wotlas.utils.*;

 /** MapExit class
  *
  * @author Petrus, Aldiss
  */

public class MapExit extends ScreenRectangle
{
  /** MapExit type
   */
  public final static byte INTERIOR_MAP_EXIT = 0;
  public final static byte BUILDING_EXIT     = 1;
  public final static byte TOWN_EXIT         = 2;

  /** MapExit Side
   */
  public final static byte NONE  = 0; // one-way MapExit
  public final static byte NORTH = 1;
  public final static byte SOUTH = 2;
  public final static byte WEST  = 3;
  public final static byte EAST  = 4;
  
 /*------------------------------------------------------------------------------------*/

  /** ID of the MapExit (index in the array {@link Room#mapExits Room.mapExits})
   */
   public int mapExitID;
  
  /** one of INTERIOR_MAP_EXIT, BUILDING_EXIT, TOWN_EXIT
   */
   public byte type;
   
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
   }

 /*------------------------------------------------------------------------------------*/

  /** Constructor with ScreenRectangle & type.
   */
   public MapExit(ScreenRectangle r, byte type) {
      super(r);
      this.type = type;
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
}
