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
 
package wotlas.common.objects.interfaces;

/** 
 * The ammunition interface. Provides methods common to all throwable objects.
 * 
 * @author Elann
 */

public interface AmmoInterface
{

 /*------------------------------------------------------------------------------------*/

  /** Throw at target.
   * @param target the target
   */
    public void throwAt(Target target);

  /** Put in hand. Ready to throw().
   */
    public void equip();
	
 
  /** Gets the damage inflicted with a bow. -1 if impossible
   * @return bowDamage
   */
    public void getBowDamage();
	
  /** Sets the damage inflicted with a bow. -1 if impossible
   * @param bowDamage the new damage inflicted with a bow
   */
    public void setBowDamage(short bowDamage);

	
  /** Gets the damage inflicted throwed by hand. -1 if impossible
   * @return handThrowDamage
   */
    public void getHandThrowDamage();
	
  /** Sets the damage inflicted throwed by hand. -1 if impossible
   * @param handThrowDamage the new damage inflicted with a hand-throw
   */
    public void setHandThrowDamage(short handThrowDamage);

	
  /** Gets the damage inflicted with a siege weapon. -1 if impossible
   * @return siegeWeaponDamage
   */
    public void getSiegeWeaponDamage();
	
  /** Sets the damage inflicted throwed by siege weapon. -1 if impossible
   * @param siegeWeaponDamage the new damage inflicted with a siege weapon
   */
    public void setSiegeWeaponDamage(short siegeWeaponDamage);

	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

