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
 
package wotlas.common.objects.weapons;

import wotlas.common.objects.interfaces.*;

import wotlas.common.objects.valueds.ValuedObject;
import wotlas.common.Player;
import wotlas.common.objects.valueds.Material;

/** 
 * The light weapon class. All the weapons within this class can be hidden in clothes.
 * 
 * @author Elann
 * @see wotlas.common.objects.weapons.Weapon
 * @see wotlas.common.objects.interfaces.RepairInterface
 * @see wotlas.common.objects.interfaces.TransportableInterface
 */

public abstract class LightWeapon extends Weapon implements RepairInterface, TransportableInterface
{

 /*------------------------------------------------------------------------------------*/

 /** The current state of the weapon. Goes from newly-made to broken.
   */
   	  protected short state;  

 /** The knowledges needed to repair this.
   */
	  protected String[] /* Knowledge[] */ repairKnowledge;	  	  

 /*------------------------------------------------------------------------------------*/

  /** Equips the weapon. The weapon is ready to use but plainly visible.
   * @param hand the hand in which it will be put 
   */
    public abstract void equip(String hand);
	
  /** Hide the weapon. The weapon cannot be used but is not visible even to curious look.
   * @param position the place to hide the weapon - can be R/L sleeve, belt, R/L boot
   */
    public abstract void hide(String position);
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Puts on the weapon to enable attack.<br>
   * There is another method with an extra parameter to use. This method should try the first hand available.
   * Take at least one hand. 
   */
    public void equip()
	{
	 /* no op */
	}
	
  /** Attacks the specified target.
   *
   * @param target the Player attacked
   * @return 0 because the damage is not instantly inflicted.
   */
    public short attack(Player target)
	{
	 short damage=0;
	 /* no op */
	 return damage;
	}

  /** Alternative attack on the specified target.
   * 
   * @param target the Player attacked
   * @return the damage inflicted
   */
    public short alternativeAttack(Player target)
	{
	 short damage=0;
	 /* no op */
	 return damage;
	}

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Repair the weapon.
   * @param repairer the Player that repairs the object. May be the owner or not.
   */
    public void repair(Player repairer)
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

 /* ----------------- Getters/Setters ----------------- */	
 
  /** Returns the state of the weapon - string version
    * @return a state string
    */
	public String getStateString() { return stateList[this.state]; } // should check for size violation

  /** Gets the state of the weapon - int version
    * @return state
    */
	public short getState() {  return state; }
	
	
  /** Sets the state of the weapon - int version
    * @param state the state value
    */
	public void setState(short state) { this.state=state; }

	
  /** Get the knowledge needed to repair.
   * @return knowledge needed
   */ 																		
    public String[]/*Knowledge[] */ getRepairKnowledge() { return repairKnowledge; }
	
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
 
	

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

