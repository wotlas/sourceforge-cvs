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
import wotlas.common.objects.interfaces.*;
import wotlas.common.Player;
import wotlas.common.objects.valueds.ValuedObject;

/** 
 * The ammunition base class.
 * 
 * @author Elann
 * @see wotlas.common.objects.BaseObject
 * @see wotlas.common.objects.weapons.RemoteWeapon
 */

public class Ammo extends BaseObject implements AmmoInterface, TransportableInterface
{

 /*------------------------------------------------------------------------------------*/
 
  /** Is it equipped = ready to use ?
   */
    protected boolean equipped;
  																						
 /*------------------------------------------------------------------------------------*/
	
 /** Default constructor
  */ 
   public Ammo()
   {
   	super();
	
	this.className="Ammo";
	this.objectName="default ammo";
   }
	 
 /*------------------------------------------------------------------------------------*/

   /** Throw at target.
   * @param target the target
   */
    public void throwAt(Player target)
	{
	 /* no op */
	}

  /** Put in hand. Ready to throw().
   */
    public void equip()
	{
	 equipped=true;
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
	
	
 /*------------------------------------------------------------------------------------*/

  /** The damage inflicted with a bow. -1 if impossible
   */
      private short bowDamage;
 																						 
  /** The damage inflicted throwed by hand. -1 if impossible
   */
      private short handThrowDamage;

  /** The damage inflicted with a siege weapon. -1 if impossible
   */
      private short siegeWeaponDamage;
	  
  
 /*------------------------------------------------------------------------------------*/

  /** Gets the damage inflicted with a bow. -1 if impossible
   * @return bowDamage
   */
    public short getBowDamage() { return bowDamage; }
	
  /** Sets the damage inflicted with a bow. -1 if impossible
   * @param bowDamage the new damage inflicted with a bow
   */
    public void setBowDamage(short bowDamage) { this.bowDamage=bowDamage; }

	
  /** Gets the damage inflicted throwed by hand. -1 if impossible
   * @return handThrowDamage
   */
    public short getHandThrowDamage() { return handThrowDamage; }
	
  /** Sets the damage inflicted throwed by hand. -1 if impossible
   * @param handThrowDamage the new damage inflicted with a hand-throw
   */
    public void setHandThrowDamage(short handThrowDamage) { this.handThrowDamage=handThrowDamage; }

	
  /** Gets the damage inflicted with a siege weapon. -1 if impossible
   * @return siegeWeaponDamage
   */
    public short getSiegeWeaponDamage() { return siegeWeaponDamage; }
	
  /** Sets the damage inflicted throwed by siege weapon. -1 if impossible
   * @param siegeWeaponDamage the new damage inflicted with a siege weapon
   */
    public void setSiegeWeaponDamage(short siegeWeaponDamage) { this.siegeWeaponDamage=siegeWeaponDamage; }
	
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

