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

/** 
 * The base class for all weapons.
 * 
 * @author Elann
 * @see wotlas.common.object.BaseObject
 */

public abstract class Weapon extends BaseObject
{

 /*------------------------------------------------------------------------------------*/


  /** The minimum damage inflicted by the weapon - may be zero.
   */
      private short damageMin;

  /** The maximum damage inflicted by the weapon - may not be zero or it wouldn't be a weapon, eh ?
   */
      private short damageMax;
	  
  /** The current state of the weapon. Goes from newly-made to broken.
   */
   	  private short state;  
	  
	  private static String[] stateList={"Newly-made","Good state","Used","Worned out","Broken"}; 
	  		  // that's just place-holder stuff, OK ?
			  // may be a file or a static list
			  // but better if in a file => internationalization 
	  	 
  /** Is the weapon in a bag or on the char ?
   */
	  private boolean equipped;
	  
	  
	  
 /*------------------------------------------------------------------------------------*/
 
  /** The only constructor.
	* @param damageMin the minimum damage inflicted by the weapon - may be zero
    * @param damageMax the maximum damage inflicted by the weapon - should not be zero
	* @param state the current state of the weapon  	
   */			
    public Weapon(short damageMin,short damageMax,short state)
	{
	 this.damageMin=damageMin;
	 this.damagemax=damageMax;
	 this.state=state;
	 this.equipped=false;
	 
	 this.className="Weapon";
	 this.objectName="default weapon";
	}															


 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Puts on the weapon to enable attack.
   */
    public void equip();
	
  /** Attacks the specified target. Abstract method.
   *
   * @param target the Character attacked
   * @return the damage inflicted
   */
    public abstract short attack(Character target);

  /** Alternative attack on the specified target. Abstract method.
   *
   * @param target the Character attacked
   * @return the damage inflicted
   */
    public abstract short alternativeAttack(Character target);

	
 /* ---------------------- Getters ----------------- */	

  /** Returns the state of the weapon - string version
    * @return a state string
    */
	public String getState() { return stateList[this.state]; } // should check for size violation

  /** Returns the minimum damage inflicted by the weapon
    * @return damageMin
    */
	public short getDamageMin() { return this.damageMin; }

  /** Returns the maximum damage inflicted by the weapon
    * @return damageMax
    */
	public short getDamageMax() { return this.damageMax; }

	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

