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

import wotlas.common.objects.usefuls.*;
import wotlas.common.objects.containers.*;
import wotlas.common.objects.weapons.*;
import wotlas.common.objects.armors.*;

/** 
 * The common interface for all inventories. 
 * 
 * @author Elann
 */

public interface InventoryInterface
{

 /*------------------------------------------------------------------------------------*/

 /*------------------------------------------------------------------------------------*/

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the body armor.
   * @return bodyArmor
   */
    public BodyArmor getBodyArmor();

  /** Set the body armor.
   * @param bodyArmor the new body armor
   */
    public void setBodyArmor(BodyArmor bodyArmor);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the head armor.
   * @return headArmor
   */
    public HeadArmor getHeadArmor();

  /** Set the head armor.
   * @param headArmor the new head armor
   */
    public void setHeadArmor(HeadArmor headArmor);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the foot armor.
   * @return footArmor
   */
    public FootArmor getFootArmor();

  /** Set the foot armor.
   * @param footArmor the new foot armor
   */
    public void setFootArmor(FootArmor footArmor);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the heavy weapon.
   * @return heavyWeapon
   */
    public HeavyWeapon getHeavyWeapon();

  /** Set the heavy weapon.
   * @param heavyWeapon the new heavy weapon
   */
    public void setHeavyWeapon(HeavyWeapon heavyWeapon);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the bow.
   * @return bow
   */
    public Bow getBow();

  /** Set the bow.
   * @param bow the new bow
   */
    public void setBow(Bow bow);
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the belt weapon.
   * @return beltWeapon
   */
    public LightWeapon getBeltWeapon();

  /** Set the belt weapon.
   * @param beltWeapon the new belt weapon
   */
    public void setBeltWeapon(LightWeapon beltWeapon);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in right sleeve.
   * @return rightSleeveWeapon
   */
    public LightWeapon getRightSleeveWeapon();

  /** Set the weapon hidden in right sleeve.
   * @param rightSleeveWeapon the new weapon hidden in right sleeve
   */
    public void setRightSleeveWeapon(LightWeapon rightSleeveWeapon);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in left sleeve.
   * @return leftSleeveWeapon
   */
    public LightWeapon getLeftSleeveWeapon();

  /** Set the weapon hidden in left sleeve.
   * @param leftSleeveWeapon the new weapon hidden in left sleeve
   */
    public void setLeftSleeveWeapon(LightWeapon leftSleeveWeapon);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in right boot.
   * @return rightBootWeapon
   */
    public LightWeapon getRightBootWeapon();

  /** Set the weapon hidden in right boot.
   * @param rightBootWeapon the new weapon hidden in right boot
   */
    public void setRightBootWeapon(LightWeapon rightBootWeapon);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in left boot.
   * @return leftBootWeapon
   */
    public LightWeapon getLeftBootWeapon();

  /** Set the weapon hidden in left boot.
   * @param leftBootWeapon the new weapon hidden in left boot
   */
    public void setLeftBootWeapon(LightWeapon leftBootWeapon);
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the purse.
   * @return purse
   */
    public Purse getPurse();

  /** Set the purse.
   * @param purse the new purse
   */
    public void setPurse(Purse purse);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the bag.
   * @return bag
   */
    public Bag getBag();

  /** Set the bag.
   * @param bag the new bag
   */
    public void setBag(Bag bag);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the belt.
   * @return belt
   */
    public Belt getBelt();

  /** Set the belt.
   * @param belt the new belt
   */
    public void setBelt(Belt belt);
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the object ready for right hand.
   * @return rightObject
   */
    public UsefulObject getRightObject();

  /** Set the object ready for right hand.
   * @param rightObject the new object ready for right hand
   */
    public void setRightObject(UsefulObject rightObject);
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the object ready for left hand.
   * @return leftObject
   */
    public UsefulObject getLeftObject();

  /** Set the object ready for left hand.
   * @param leftObject the new object ready for left hand
   */
    public void setLeftObject(UsefulObject leftObject);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the book.
   * @return book
   */
    public Book getBook();

  /** Set the book.
   * @param book the new book
   */
    public void setBook(Book book);

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 
}

