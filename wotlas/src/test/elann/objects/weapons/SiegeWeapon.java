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

import wotlas.common.Player;

/** 
 * The siege weapon base class.
 * Siege weapons cannot be moved by lone Player without Power. 
 *
 * @author Elann
 * @see wotlas.common.objects.weapons.RemoteWeapon
 * @see wotlas.common.objects.interfaces.RemoteWeaponInterface
 * @see wotlas.common.objects.interfaces.SiegeWeaponInterface
 */

public class SiegeWeapon extends RemoteWeapon
{

 /*------------------------------------------------------------------------------------*/

 
 /*------------------------------------------------------------------------------------*/

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Puts on the weapon to enable attack.<br>
   * Impossible on SiegeWeapons !
   */
    public void equip()
	{
	 /* no op - have you ever seen someone CARRYING a catapult ? */
	}
	
  /** Attacks the specified target.
   *
   * @param target the Player attacked
   * @return 0 because the damage is not instantly inflicted.
   */
    public short attack(Player target)
	{
	 loose();
	 return 0;
	}

  /** Alternative attack on the specified target.<br>
   * No altern attack for SiegeWeapons. Calls attack(target).
   * @param target the Player attacked
   * @return the damage inflicted
   */
    public short alternativeAttack(Player target)
	{
	 return attack(target);
	}

 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}

