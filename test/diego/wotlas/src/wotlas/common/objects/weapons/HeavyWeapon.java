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

import wotlas.common.objects.interfaces.HeavyWeaponInterface;
import wotlas.common.objects.interfaces.RepairInterface;
import wotlas.common.objects.interfaces.TransportableInterface;

import wotlas.common.objects.valueds.ValuedObject;
import wotlas.common.Player;
import wotlas.common.objects.valueds.Material;
import wotlas.common.objects.BaseObject;


/** 
 * The heavy weapon class. All the weapons within this class cannot be hidden in the clothes.
 * 
 * @author Elann
 * @see wotlas.common.objects.weapons.Weapon
 * @see wotlas.common.objects.interfaces.HeavyWeaponInterface
 * @see wotlas.common.objects.interfaces.RepairInterface
 * @see wotlas.common.objects.interfaces.TransportableInterface 
 */

public class HeavyWeapon extends Weapon implements HeavyWeaponInterface, RepairInterface, TransportableInterface
{

 /** The weapon visibility status
  */
  private boolean sheathed;

 /** The current state of the weapon. Goes from newly-made to broken.
   */
   	  protected short state;  

 /** The knowledges needed to repair this.
   */
	  protected String[] /* Knowledge[] */ repairKnowledge;	  	  
	  
 /*------------------------------------------------------------------------------------*/

 /** Default constructor
  */ 
   public HeavyWeapon()
   {
   	super();
	
	this.className="HeavyWeapon";
	this.objectName="default heavy weapon";
   }
 
 /*------------------------------------------------------------------------------------*/

  /** Sheathes the weapon. The weapon can no longer be used without being unsheathed first but it'll escape casual look. 
   */
    public void sheathe()
	{
	 this.sheathed=true;
	}

  /** Unsheathes the weapon. The weapon is ready to strike. It is plainly visible.
   */
    public void unsheathe()
	{
	 this.sheathed=false;
	}

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Puts on the weapon to enable attack.<br>
   * Take at least one hand. 
   */
    public void equip()
	{
	 equipped=true;
	 unsheathe();
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
	
  /** Trade the object to somebody.<br>
    * Here the transaction is already accepted.
  	* @param buyer The Player who buy the object. 
  	* @return the object given by the other player.
    */
    public BaseObject tradeTo(Player buyer)
	{
	 /* no op */
	 return new BaseObject();
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

