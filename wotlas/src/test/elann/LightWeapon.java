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
 * The light weapon class. All the weapons within this class can be hidden in clothes.
 * 
 * @author Elann
 * @see wotlas.common.object.Weapon
 * @see wotlas.common.object.RepairInterface
 * @see wotlas.common.object.TransportableInterface
 */

public class LightWeapon extends Weapon implements RepairInterface, TransportableInterface
{

 /** The weapon's position
  */
  private boolean equipped;

 /*------------------------------------------------------------------------------------*/


  /** Equips the weapon. The weapon is ready to use but plainly visible.
   * @param hand the hand in which it will be put 
   */
    public void equip(String hand);
	
  /** Hide the weapon. The weapon cannot be used but is not visible even to curious look.
   * @param position the place to hide the weapon - can be R/L sleeve, belt, R/L boot
   */
    public void hide(String position);
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

