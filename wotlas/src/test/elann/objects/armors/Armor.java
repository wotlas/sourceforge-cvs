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
 
package wotlas.common.objects.armors;

import wotlas.common.objects.BaseObject;
import wotlas.common.objects.valueds.ValuedObject;
import wotlas.common.objects.valueds.Material;
import wotlas.common.objects.interfaces.*;

import wotlas.common.Player;

/** 
 * The base class for all pieces of armor.
 * 
 * @author Elann
 * @see wotlas.common.objects.BaseObject
 * @see wotlas.common.objects.interfaces.ArmorInterface
 * @see wotlas.common.objects.interfaces.TransportableInterface
 */

public class Armor extends BaseObject implements ArmorInterface, RepairInterface, TransportableInterface
{

 /*------------------------------------------------------------------------------------*/


  /** The defense of the armor - may be zero.
   */
      protected short defense;

  /** The current state of the armor. Goes from newly-made to broken.
   */
   	  protected short state;  
	  
	  protected static String[] stateList={"Newly-made","Good state","Used","Worned out","Broken"}; 
	  		  // that's just place-holder stuff, OK ?
			  // may be a file or a static list
			  // but better if in a file => internationalization / evolution 
	  	 
  /** Is the armor in a bag or on the char ?
   */
	  protected boolean equipped;
	  	  
 /** The knowledges needed to repair this.
   */
	  protected String[] /* Knowledge[] */ repairKnowledge;
	  
	  
 /*------------------------------------------------------------------------------------*/
 
  /** The only constructor.
	* @param defense the defense of the armor - may be zero
	* @param state the current state of the armor  	
   */			
    public Armor(short defense,short state)
	{
	 this.defense=defense;
	 this.state=state;
	 this.equipped=false;
	 
	 this.className="Armor";
	 this.objectName="default armor";
	}															

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Puts on the armor.
   */
    public void equip()
	{
	 /* no op */
	}

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Gets rid of the object. The object is dropped on the ground.
   */
    public void discard()
	{
	 /* no op */
	}

  /** Sells the object to somebody.
  	  @param buyer The Player who buy the object. 
  	  @return the prize paid.
   */
    public ValuedObject sellTo(Player buyer)
	{
	 /* no op */
	 return new ValuedObject();
	}

  /** Gives the object to somebody.
  	  @param receiver The Player who receive the object.
   */
    public void giveTo(Player receiver)
	{
	 /* no op */
	}

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the state of the armor - string version
    * @return a state string
    */
	public String getStateString() { return stateList[this.state]; } // should check for size violation

	
  /** Get the knowledge needed to repair.
   * @return knowledge needed
   */ 																		
    public String[]/*Knowledge[] */ getRepairKnowledge()
	{
	 return repairKnowledge;
	}
	
  /** Get the materials needed to repair.<br>
   * Get this from the repairer.
   * @return material list
   * @param repairer the Player that repairs the object. May be the owner or not.
   */
    public Material[] getRepairMaterial(Player repairer) 
	{  
	   /* asks the repairer what he needs */
	   return new Material[1];	   
	}
	
 
  /** Repair the object.
   * @param repairer the Player that repairs the object. May be the owner or not.
   */
    public void repair(Player repairer)
	{
	 /* no op */
	}
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Gets the defense of the armor.
    * @return defense
    */
	public short getDefense() { return defense; }
 
  /** Sets the defense of the armor.
    * @param defense the new defense
    */
	public void setDefense(short defense) { this.defense=defense; }

	
  /** Gets the state of the armor.
    * @return state
    */
	public short getState() { return state; }
 
  /** Sets the state of the armor.
    * @param state the new state
    */
	public void setState(short state) { this.state=state; }
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

