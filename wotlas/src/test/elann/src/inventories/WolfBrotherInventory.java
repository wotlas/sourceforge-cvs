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
 
package wotlas.common.objects.inventories;

import wotlas.common.objects.usefuls.*;
import wotlas.common.objects.containers.*;
import wotlas.common.objects.weapons.*;
import wotlas.common.objects.armors.*;

/** 
 * This is the class for WolfBrother's Inventory.<br>
 * All objects are inherited. All methods are inherited excepted those not available to Wolfbrothers
 * @author Elann
 * @see wotlas.common.objects.interfaces.InventoryInterface
 */

public class WolfBrotherInventory extends Inventory 
{

 /*------------------------------------------------------------------------------------*/

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Get the body armor.
   * @return null
   */
    public BodyArmor getBodyArmor()
    {
        return null;
    }

  /** Set the body armor. Ignored.
   * @param bodyArmor the new body armor
   */
    public void setBodyArmor(BodyArmor bodyArmor) throws InventoryException
    {
	 throw InventoryException;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the head armor.
   * @return null
   */
    public HeadArmor getHeadArmor()
    {
        return null;
    }

  /** Set the head armor. Ignored.
   * @param headArmor the new head armor
   */
    public void setHeadArmor(HeadArmor headArmor) throws InventoryException
    {
	 throw InventoryException;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
 
  /** Get the weapon hidden in right sleeve.
   * @return null
   */
    public LightWeapon getRightSleeveWeapon()
    {
        return null;
    }

  /** Set the weapon hidden in right sleeve. Ignored.
   * @param rightSleeveWeapon the new weapon hidden in right sleeve
   */
    public void setRightSleeveWeapon(LightWeapon rightSleeveWeapon) throws InventoryException
    {
	 throw InventoryException;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in left sleeve.
   * @return null
   */
    public LightWeapon getLeftSleeveWeapon()
    {
        return null;
    }

  /** Set the weapon hidden in left sleeve. Ignored.
   * @param leftSleeveWeapon the new weapon hidden in left sleeve
   */
    public void setLeftSleeveWeapon(LightWeapon leftSleeveWeapon) throws InventoryException
    {
	 throw InventoryException;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in right boot.
   * @return null
   */
    public LightWeapon getRightBootWeapon()
    {
        return null;
    }

  /** Set the weapon hidden in right boot. Ignored.
   * @param rightBootWeapon the new weapon hidden in right boot
   */
    public void setRightBootWeapon(LightWeapon rightBootWeapon) throws InventoryException
    {
	 throw InventoryException;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in left boot.
   * @return null
   */
    public LightWeapon getLeftBootWeapon()
    {
        return null;
    }

  /** Set the weapon hidden in left boot. Ignored.
   * @param leftBootWeapon the new weapon hidden in left boot
   */
    public void setLeftBootWeapon(LightWeapon leftBootWeapon) throws InventoryException
    {
	 throw InventoryException;
    }
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
 
}