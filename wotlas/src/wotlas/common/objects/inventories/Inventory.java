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

import wotlas.common.objects.interfaces.InventoryInterface;

/** 
 * This is the base class for all Inventories.<br>
 * All the objects are represented here and all the methods are provided.<br>
 * When an object isn't available to a Character class, the matching Inventory overrides getters and setters.
 * @author Elann
 * @see wotlas.common.objects.interfaces.InventoryInterface
 */

public class Inventory implements InventoryInterface {

 /*------------------------------------------------------------------------------------*/

  /*-------------- Armor ------------*/

  /** The main armor. At least shirt ?
   */
      protected BodyArmor bodyArmor;

  /** The head armor ie helmet, cap, ... or none
   */
      protected HeadArmor headArmor;
	  
  /*-------------- Weapon -----------*/

  /** The heavy weapon. May be sword, axe, ... or none
   */
      protected HeavyWeapon heavyWeapon;
	  
  /** The bow. May be bow, crossbow, ... or none
   */
      protected Bow bow;

  /** The light weapons. May be dagger, knife, ... or none
   */
      protected LightWeapon beltWeapon,
                            rightSleeveWeapon,leftSleeveWeapon,
                            rightBootWeapon,leftBootWeapon;
  
  /*------------ Container ----------*/

  /** The purse.
   */
      protected Purse purse;
 
  /** The bag.
   */
      protected Bag bag;
	  
  /** The belt.
   */
      protected Belt belt;
	
	  
  /*---------- UsefulObject ---------*/

  /** The useful object equipped.
   */
      protected UsefulObject rightObject,leftObject;
  
  /*-------------- Book -------------*/
  
  /** The book owned. Contains lots of things in different chapters.
   */
      protected Book book;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public Inventory() {
        book = new Book();
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the body armor.
   * @return bodyArmor
   */
    public BodyArmor getBodyArmor()
    {
        return bodyArmor;
    }

  /** Set the body armor.
   * @param bodyArmor the new body armor
   */
    public void setBodyArmor(BodyArmor bodyArmor)
    {
        this.bodyArmor=bodyArmor;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the head armor.
   * @return headArmor
   */
    public HeadArmor getHeadArmor()
    {
        return headArmor;
    }

  /** Set the head armor.
   * @param headArmor the new head armor
   */
    public void setHeadArmor(HeadArmor headArmor)
    {
        this.headArmor=headArmor;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the heavy weapon.
   * @return heavyWeapon
   */
    public HeavyWeapon getHeavyWeapon()
    {
        return heavyWeapon;
    }

  /** Set the heavy weapon.
   * @param heavyWeapon the new heavy weapon
   */
    public void setHeavyWeapon(HeavyWeapon heavyWeapon)
    {
        this.heavyWeapon=heavyWeapon;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the bow.
   * @return bow
   */
    public Bow getBow()
    {
        return bow;
    }

  /** Set the bow.
   * @param bow the new bow
   */
    public void setBow(Bow bow)
    {
        this.bow=bow;
    }
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the belt weapon.
   * @return beltWeapon
   */
    public LightWeapon getBeltWeapon()
    {
        return beltWeapon;
    }

  /** Set the belt weapon.
   * @param beltWeapon the new belt weapon
   */
    public void setBeltWeapon(LightWeapon beltWeapon)
    {
        this.beltWeapon=beltWeapon;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in right sleeve.
   * @return rightSleeveWeapon
   */
    public LightWeapon getRightSleeveWeapon()
    {
        return rightSleeveWeapon;
    }

  /** Set the weapon hidden in right sleeve.
   * @param rightSleeveWeapon the new weapon hidden in right sleeve
   */
    public void setRightSleeveWeapon(LightWeapon rightSleeveWeapon)
    {
        this.rightSleeveWeapon=rightSleeveWeapon;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in left sleeve.
   * @return leftSleeveWeapon
   */
    public LightWeapon getLeftSleeveWeapon()
    {
        return leftSleeveWeapon;
    }

  /** Set the weapon hidden in left sleeve.
   * @param leftSleeveWeapon the new weapon hidden in left sleeve
   */
    public void setLeftSleeveWeapon(LightWeapon leftSleeveWeapon)
    {
        this.leftSleeveWeapon=leftSleeveWeapon;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in right boot.
   * @return rightBootWeapon
   */
    public LightWeapon getRightBootWeapon()
    {
        return rightBootWeapon;
    }

  /** Set the weapon hidden in right boot.
   * @param rightBootWeapon the new weapon hidden in right boot
   */
    public void setRightBootWeapon(LightWeapon rightBootWeapon)
    {
        this.rightBootWeapon=rightBootWeapon;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the weapon hidden in left boot.
   * @return leftBootWeapon
   */
    public LightWeapon getLeftBootWeapon()
    {
        return leftBootWeapon;
    }

  /** Set the weapon hidden in left boot.
   * @param leftBootWeapon the new weapon hidden in left boot
   */
    public void setLeftBootWeapon(LightWeapon leftBootWeapon)
    {
        this.leftBootWeapon=leftBootWeapon;
    }
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the purse.
   * @return purse
   */
    public Purse getPurse()
    {
        return purse;
    }

  /** Set the purse.
   * @param purse the new purse
   */
    public void setPurse(Purse purse)
    {
        this.purse=purse;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the bag.
   * @return bag
   */
    public Bag getBag()
    {
        return bag;
    }

  /** Set the bag.
   * @param bag the new bag
   */
    public void setBag(Bag bag)
    {
        this.bag=bag;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the belt.
   * @return belt
   */
    public Belt getBelt()
    {
        return belt;
    }

  /** Set the belt.
   * @param belt the new belt
   */
    public void setBelt(Belt belt)
    {
        this.belt=belt;
    }
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the object ready for right hand.
   * @return rightObject
   */
    public UsefulObject getRightObject()
    {
        return rightObject;
    }

  /** Set the object ready for right hand.
   * @param rightObject the new object ready for right hand
   */
    public void setRightObject(UsefulObject rightObject)
    {
        this.rightObject=rightObject;
    }
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the object ready for left hand.
   * @return leftObject
   */
    public UsefulObject getLeftObject()
    {
        return leftObject;
    }

  /** Set the object ready for left hand.
   * @param leftObject the new object ready for left hand
   */
    public void setLeftObject(UsefulObject leftObject)
    {
        this.leftObject=leftObject;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the book.
   * @return book
   */
    public Book getBook()
    {
        return book;
    }

  /** Set the book.
   * @param book the new book
   */
    public void setBook(Book book)
    {
        this.book=book;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 
 
}

