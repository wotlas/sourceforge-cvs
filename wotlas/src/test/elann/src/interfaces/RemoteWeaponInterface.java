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

import wotlas.common.Target;
import wotlas.common.objects.weapons.Ammo;

/** 
 * The remote weapon interface. Provides methods common to all remote weapons.
 * 
 * @author Elann
 * @see wotlas.common.objects.weapons.RemoteWeapon 
 */

public interface RemoteWeaponInterface
{

 /*------------------------------------------------------------------------------------*/


  /** Arms the weapon. The weapon is ready to Aim()/Loose().
   * @param ammo the ammo used 
   */
    public void arm(Ammo ammo);

  /** Aims to the specified target. Needs to be armed.
   * @param target the target to aim at. CLASS NOT IMPLEMENTED - may be char or build
   */
    public void aim(Target target);
	
  /** Looses. Launch the ammo on the target.
   */
    public void loose();


	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

