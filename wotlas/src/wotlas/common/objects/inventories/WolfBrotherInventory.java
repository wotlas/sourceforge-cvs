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

import wotlas.common.objects.armors.BodyArmor;
import wotlas.common.objects.armors.HeadArmor;
import wotlas.common.objects.weapons.LightWeapon;

/** 
 * This is the class for WolfBrother's Inventory.<br>
 * All objects are inherited. All methods are inherited excepted those not available to Wolfbrothers
 * @author Elann
 * @see wotlas.common.objects.interfaces.InventoryInterface
 */

public class WolfBrotherInventory extends Inventory {

    /*------------------------------------------------------------------------------------*/

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public WolfBrotherInventory() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the body armor.
     * @return null
     */
    @Override
    public BodyArmor getBodyArmor() {
        return null;
    }

    /** Set the body armor. Ignored.
     * @param bodyArmor the new body armor
     */
    @Override
    public void setBodyArmor(BodyArmor bodyArmor) throws InventoryException {
        throw new InventoryException();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the head armor.
     * @return null
     */
    @Override
    public HeadArmor getHeadArmor() {
        return null;
    }

    /** Set the head armor. Ignored.
     * @param headArmor the new head armor
     */
    @Override
    public void setHeadArmor(HeadArmor headArmor) throws InventoryException {
        throw new InventoryException();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the weapon hidden in right sleeve.
     * @return null
     */
    @Override
    public LightWeapon getRightSleeveWeapon() {
        return null;
    }

    /** Set the weapon hidden in right sleeve. Ignored.
     * @param rightSleeveWeapon the new weapon hidden in right sleeve
     */
    @Override
    public void setRightSleeveWeapon(LightWeapon rightSleeveWeapon) throws InventoryException {
        throw new InventoryException();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the weapon hidden in left sleeve.
     * @return null
     */
    @Override
    public LightWeapon getLeftSleeveWeapon() {
        return null;
    }

    /** Set the weapon hidden in left sleeve. Ignored.
     * @param leftSleeveWeapon the new weapon hidden in left sleeve
     */
    @Override
    public void setLeftSleeveWeapon(LightWeapon leftSleeveWeapon) throws InventoryException {
        throw new InventoryException();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the weapon hidden in right boot.
     * @return null
     */
    @Override
    public LightWeapon getRightBootWeapon() {
        return null;
    }

    /** Set the weapon hidden in right boot. Ignored.
     * @param rightBootWeapon the new weapon hidden in right boot
     */
    @Override
    public void setRightBootWeapon(LightWeapon rightBootWeapon) throws InventoryException {
        throw new InventoryException();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the weapon hidden in left boot.
     * @return null
     */
    @Override
    public LightWeapon getLeftBootWeapon() {
        return null;
    }

    /** Set the weapon hidden in left boot. Ignored.
     * @param leftBootWeapon the new weapon hidden in left boot
     */
    @Override
    public void setLeftBootWeapon(LightWeapon leftBootWeapon) throws InventoryException {
        throw new InventoryException();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}