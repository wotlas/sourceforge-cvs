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
 
package wotlas.common.object;

//import java.rmi.Remote;
//import java.rmi.RemoteException;

//import wotlas.utils.Tools;

import wotlas.common.Character;	// the class used to give or sell. Is it implemented as I need ?
import wotlas.common.Levels;	// not implemented
import wotlas.common.Knowledge; // not implemented


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
   	 private String className;
	 
  /** Object name - specific name for the object. Not static because it could change if it is broken for example.  
   */
   	 private String objectName;
   
  /** Object's weight - used for endurance and max load. Perhaps negative weight should mean unmovable.
   */
     private short weight;
	  
  /** The level(s) required to use this object. - THE CLASS LEVEL/LEVELLIST DOES NOT EXIST ! 
   */
	 private LevelList requiredLevels;													

  /** The knowledge required to use this object. - THE CLASS KNOWLEDGE/KNOWLEDGELIST DOES NOT EXIST !
   */
   	 private KnowledgeList requiredKnowledge;

  /** The owner of this object. - THE CLASS CHARACTER HAS TO BE ADAPTED FOR OBJECTS !
   */
   	 private Character owner;
  	 
	 
 /*------------------------------------------------------------------------------------*/

  /** Gets rid of the object. The object is dropped on the ground.
   */
    public void discard();
	
  /** Sells the object to somebody.
  	  @param buyer The Character who buy the object. 
  	  @return the prize paid.
   */
    public ValuedObject sellTo(Character buyer);

  /** Gives the object to somebody.
  	  @param receiver The Character who receive the object.
   */
    public void giveTo(Character receiver);
	
  /* ------- Getters / Setters --------- */
	
  /** Return the name of the class of the object (like Sword, Axe, ...)
  	  @return className
   */
	public String getClassName() { return this.className; }

  /** Sets the name of the class of the object (like Sword, Axe, ...)
  	  @param className the new name
   */
	public void setClassName(String className) { this.className=className; }

	
  /** Returns the name of the object (like Callandor, Tar Valon Gold Mark, ...)
  	  @return objectName
   */
	public String getObjectName() { return this.className; }

  /** Sets the name of the object (like Callandor, Tar Valon Gold Mark, ...)
  	  @param objectName the new name
   */
	public void setObjectName(String objectName) { this.objectName=objectName; }
		
	
  /** Returns the weight of the object.
  	  @return the object's weight
   */
    public short getObjectWeight() { return this.weight; }

  /** Sets the weight of the object.
  	  @param weight the object's weight
   */
    public void setObjectWeight(short weight) { this.weight=weight; }

	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

