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
 
package wotlas.common.objects;

import wotlas.common.Player;	// the class used to give or sell. It has to be adapted.
//import wotlas.common.Levels;	// not implemented
//import wotlas.common.Knowledge; // not implemented
import wotlas.common.universe.WotlasLocation;	
import wotlas.libs.graphics2D.Drawable;
import wotlas.libs.graphics2D.ImageIdentifier;

/** 
 * The base class for all game objects that may be encountered in wotlas.
 * 
 * @author Elann
 */

public class BaseObject
{

 /*------------------------------------------------------------------------------------*/

  /** Class name - generic name for all the objects of the class.  
   */
   	 protected String className;
	 
  /** Object name - specific name for the object. Not static because it could change if it is broken for example.  
   */
   	 protected String objectName;
   
  /** Object position in the world.
   */
  	 protected WotlasLocation objectLocation; 
   
  /** Object position in the room.
   */
   	 protected int x,y;
  
  /** Object's weight - used for endurance and max load. Perhaps negative weight should mean unmovable.
   */
     protected short objectWeight;
	  
  /** Object's size - used for max load in Containers.
   */
     protected short objectSize;

  /** The level(s) required to use this object. - THE CLASS LEVEL DOES NOT EXIST ! 
   */
	 protected String[] /*Level[]*/ requiredLevels;													

  /** The knowledge required to use this object. - THE CLASS KNOWLEDGE DOES NOT EXIST !
   */
   	 protected String[] /*Knowledge[]*/ requiredKnowledge;

  /** The owner of this object. - THE CLASS PLAYER HAS TO BE ADAPTED FOR OBJECTS !
   */
   	 protected Player owner;
  	 
  /** The GFX of the object.
   */
	 protected Drawable drawable;
	 
  /** The icon in the Inventory
   */
	 protected ImageIdentifier inventoryPicture;
	 	 
 /*------------------------------------------------------------------------------------*/
		
  /* ------- Constructor ----- */
  		
  /** Default constructor.<br>Just sets className and objectName to default.
   */
   	 public BaseObject()
	 {
	  className="BaseObject";
	  objectName="default object";
	 }
		
  /* ------- Getters / Setters - still missing some : Knowledge & Levels, owner --------- */

  /** Return the ImageIdentifier of the object's inventory representation.
  	  @return inventoryPicture
   */
   	 public ImageIdentifier getInventoryPicture()
	 {
	  return inventoryPicture;
	 }

  /** Set the ImageIdentifier of the object's inventory representation.
  	  @param inventoryPicture the new ID
   */
   	 public void setInventoryPicture(ImageIdentifier inventoryPicture)
	 {
	  this.inventoryPicture=inventoryPicture;
	 }

  /** Return the object's in-game representation.
  	  @return drawable
   */
   	 public Drawable getDrawable()
	 {
	  return drawable;
	 }

  /** Set the object's in-game representation.
  	  @param drawable the new drawable
   */
   	 public void setDrawable(Drawable drawable)
	 {
	  this.drawable=drawable;
	 }
	
  /** Return the name of the class of the object (like Sword, Axe, ...)
  	  @return className
   */
	public String getClassName() { return className; }

  /** Sets the name of the class of the object (like Sword, Axe, ...)
  	  @param className the new name
   */
	public void setClassName(String className) { this.className=className; }

	
  /** Returns the name of the object (like Callandor, Tar Valon Gold Mark, ...)
  	  @return objectName
   */
	public String getObjectName() { return objectName; }

  /** Sets the name of the object (like Callandor, Tar Valon Gold Mark, ...)
  	  @param objectName the new name
   */
	public void setObjectName(String objectName) { this.objectName=objectName; }
		

  /** Returns the location of the object in the world. 
  	  @return objectLocation
   */
	public WotlasLocation getObjectLocation() { return objectLocation; }

  /** Sets the location of the object in the world.
  	  @param objectLocation the new location
   */
	public void setObjectLocation(WotlasLocation objectLocation) { this.objectLocation=objectLocation; }


  /** Returns the x location of the object in the map. 
  	  @return x
   */
	public int getX() { return x; }

  /** Sets the x location of the object in the map.
  	  @param x the new x location
   */
	public void setX(int x) { this.x=x; }

	
  /** Returns the y location of the object in the map. 
  	  @return y
   */
	public int getY() { return y; }

  /** Sets the y location of the object in the map.
  	  @param y the new y location
   */
	public void setY(int y) { this.y=y; }

	
  /** Returns the weight of the object.
  	  @return the object's weight
   */
    public short getObjectWeight() { return objectWeight; }

  /** Sets the weight of the object.
  	  @param weight the object's weight
   */
    public void setObjectWeight(short weight) { this.objectWeight=weight; }

	
  /** Returns the size of the object.
  	  @return the object's size
   */
    public short getObjectSize() { return objectSize; }

  /** Sets the size of the object.
  	  @param size the object's size
   */
    public void setObjectSize(short size) { this.objectSize=size; }
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

