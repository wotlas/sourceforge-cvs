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

import wotlas.common.objects.BaseObject;

import wotlas.common.Player;

/** 
 * The base class for all weapons.
 * 
 * @author Elann
 * @see wotlas.common.objects.BaseObject
 */

public abstract class Weapon extends BaseObject
{

 /*------------------------------------------------------------------------------------*/


  /** The minimum damage inflicted by the weapon - may be zero.
   */
      protected short damageMin;

  /** The maximum damage inflicted by the weapon - may not be zero or it wouldn't be a weapon, eh ?
   */
      protected short damageMax;	    
	  	 
  /** Is the weapon in a bag or on the char ?
   */
	  protected boolean equipped;
	  
	  
	  
 /*------------------------------------------------------------------------------------*/
 
  /** The default constructor.<br>
	* damageMin is set to -1<br>
    * damageMax is set to -1
   */			
    public Weapon()
	{
	 this.damageMin=-1;
	 this.damageMax=-1;
	 this.equipped=false;
	 
	 this.className="Weapon";
	 this.objectName="default weapon";
	}															
 
 
  /** The parametric constructor.
	* @param damageMin the minimum damage inflicted by the weapon - may be zero
    * @param damageMax the maximum damage inflicted by the weapon - should not be zero
   */			
    public Weapon(short damageMin,short damageMax)
	{
	 this.damageMin=damageMin;
	 this.damageMax=damageMax;
	 this.equipped=false;
	 
	 this.className="Weapon";
	 this.objectName="default weapon";
	}															


 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Puts on the weapon to enable attack.
   */
    public abstract void equip();
	
  /** Attacks the specified target. Abstract method.
   *
   * @param target the Player attacked
   * @return the damage inflicted
   */
    public abstract short attack(Player target);

  /** Alternative attack on the specified target. Abstract method.
   *
   * @param target the Player attacked
   * @return the damage inflicted
   */
    public abstract short alternativeAttack(Player target);

	
 /* ----------------- Getters/Setters ----------------- */	
	
	
  /** Returns the minimum damage inflicted by the weapon
    * @return damageMin
    */
	public short getDamageMin() { return damageMin; }

  /** Sets the minimum damage inflicted by the weapon
    * @param damageMin the new damage min
    */
	public void setDamageMin(short damageMin) { this.damageMin=damageMin; }

	
  /** Returns the maximum damage inflicted by the weapon
    * @return damageMax
    */
	public short getDamageMax() { return damageMax; }

  /** Sets the maximum damage inflicted by the weapon
    * @param damageMax the new damage max
    */
	public void setDamageMax(short damageMax) { this.damageMax=damageMax; }
	
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

