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

import java.awt.Point;
import java.awt.Rectangle;

 /** MapExit class
  *
  * @author Petrus
  */

public class MapExit extends ScreenZone
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
   public int mapExitSide;

  /** Eventual position on the target map (null if none)
   */
   public Point targetPosition;
   
  /** knowledge required to use MapExit
   * -1 if no knowledge required
   */
   public int requiredKnowledgeID;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
   public MapExit() {}

 /*------------------------------------------------------------------------------------*/

  /** Constructor with Rectangle.
   */
   public MapExit(Rectangle r) {
      super(r);
   }

 /*------------------------------------------------------------------------------------*/

  /** Constructor with Rectangle & type.
   */
   public MapExit(Rectangle r, byte type) {
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
    return targetWotlasLocation;
  }
  public void setTargetPosition(Point myTargetPosition) {
    this.targetPosition = myTargetPosition;
  }
  public Point getTargetPosition() {
    return targetPosition;
  }
  public void setMapExitSide(int mapExitSide) {
    this.mapExitSide = mapExitSide;
  }
  public int getMapExitSide() {
    return mapExitSide;
  }
  public void setRequiredKnowledgeID(int myRequiredKnowledgeID) {
    this.requiredKnowledgeID = myRequiredKnowledgeID;
  }
  public int getRequiredKnowledgeID() {
    return requiredKnowledgeID;
  }

 /*------------------------------------------------------------------------------------*/
  
}
