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
 
package wotlas.common.objects;

import wotlas.common.objects.usefuls.*;
import wotlas.common.objects.containers.*;
import wotlas.common.objects.weapons.*;
import wotlas.common.objects.armors.*;

/** 
 * This represents what the player has on. Not what he has in is bag or purse.
 * 
 * @author Elann
 */

public class Inventory
{

 /*------------------------------------------------------------------------------------*/

  /*-------------- Armor ------------*/

  /** The main armor. At least shirt ?
   */
      private BodyArmor bodyArmor;

  /** The head armor ie helmet, cap, ... or none
   */
   	  private HeadArmor headArmor;

  /** The foot armor ie shoes, boots, ... or none ?
   */
   	  private FootArmor footArmor;
	  
  /*-------------- Weapon -----------*/

  /** The heavy weapon. May be sword, axe, ... or none
   */
      private HeavyWeapon heavyWeapon;
	  
  /** The bow. May be bow, crossbow, ... or none
   */
      private Bow bow;

  /** The light weapons. May be dagger, knife, ... or none
   */
      private LightWeapon beltWeapon,
	  		  			  rightSleeveWeapon,leftSleeveWeapon,
						  rightBootWeapon,leftBootWeapon;
  
  /*------------ Container ----------*/

  /** The purse.
   */
      private Purse purse;
 
  /** The bag.
   */
   	  private Bag bag;
	  
  /*---------- UsefulObject ---------*/

  /** The useful object equipped.
   */
      private UsefulObject rightObject,leftObject;
  
  /*-------------- Book -------------*/
  
  /** The book owned. Contains lots of things in different chapters.
   */
      private Book book;
	  
 /*------------------------------------------------------------------------------------*/

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the body armor.
   * @return bodyArmor
   */
    public BodyArmor getBodyArmor()
    {
        return this.bodyArmor;
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
        return this.headArmor;
    }

  /** Set the head armor.
   * @param headArmor the new head armor
   */
    public void setHeadArmor(HeadArmor headArmor)
    {
        this.headArmor=headArmor;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the foot armor.
   * @return footArmor
   */
    public FootArmor getFootArmor()
    {
        return this.footArmor;
    }

  /** Set the foot armor.
   * @param footArmor the new foot armor
   */
    public void setFootArmor(FootArmor footArmor)
    {
        this.footArmor=footArmor;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the heavy weapon.
   * @return heavyWeapon
   */
    public HeavyWeapon getHeavyWeapon()
    {
        return this.heavyWeapon;
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
        return this.bow;
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
        return this.beltWeapon;
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
        return this.rightSleeveWeapon;
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
        return this.leftSleeveWeapon;
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
        return this.rightBootWeapon;
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
        return this.leftBootWeapon;
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
        return this.purse;
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
        return this.bag;
    }

  /** Set the bag.
   * @param bag the new bag
   */
    public void setBag(Bag bag)
    {
        this.bag=bag;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Get the object ready for right hand.
   * @return rightObject
   */
    public UsefulObject getRightObject()
    {
        return this.rightObject;
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
        return this.leftObject;
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
        return this.book;
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
