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
    public void getBowDamage() { return this.bowDamage; }
	
  /** Sets the damage inflicted with a bow. -1 if impossible
   * @param bowDamage the new damage inflicted with a bow
   */
    public void setBowDamage(short bowDamage) { this.bowDamage=bowDamage; }

	
  /** Gets the damage inflicted throwed by hand. -1 if impossible
   * @return handThrowDamage
   */
    public void getHandThrowDamage() { return this.handThrowDamage; }
	
  /** Sets the damage inflicted throwed by hand. -1 if impossible
   * @param handThrowDamage the new damage inflicted with a hand-throw
   */
    public void setHandThrowDamage(short handThrowDamage) { this.handThrowDamage=handThrowDamage; }

	
  /** Gets the damage inflicted with a siege weapon. -1 if impossible
   * @return siegeWeaponDamage
   */
    public void getSiegeWeaponDamage() { return this.siegeWeaponDamage; }
	
  /** Sets the damage inflicted throwed by siege weapon. -1 if impossible
   * @param siegeWeaponDamage the new damage inflicted with a siege weapon
   */
    public void setSiegeWeaponDamage(short siegeWeaponDamage) { this.siegeWeaponDamage=siegeWeaponDamage; }
	
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

