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

}
