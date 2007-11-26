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

import wotlas.common.objects.BaseObject;
import wotlas.common.objects.armors.BodyArmor;
import wotlas.common.objects.armors.HeadArmor;
import wotlas.common.objects.containers.Bag;
import wotlas.common.objects.containers.Belt;
import wotlas.common.objects.containers.Purse;
import wotlas.common.objects.interfaces.InventoryInterface;
import wotlas.common.objects.usefuls.Book;
import wotlas.common.objects.weapons.Bow;
import wotlas.common.objects.weapons.HeavyWeapon;
import wotlas.common.objects.weapons.LightWeapon;

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
    protected LightWeapon beltWeapon, rightSleeveWeapon, leftSleeveWeapon, rightBootWeapon, leftBootWeapon;

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

    /*---------- Objects in hands ---------*/

    /** The object equipped. <br>
     * May be an object from the slots or any transportable object.
     */
    protected BaseObject rightObject, leftObject;

    /*-------------- Book -------------*/

    /** The book owned. Contains lots of things in different chapters.
     */
    protected Book book;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public Inventory() {
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** The player has received an object.<br>
     * First, this method try to find an available slot to put it.<br>
     * If none is available, the object should go in the bag.<br>
     * If the bag hasn't got enough space, failure.<br>
     *
     * @param object the object the player received
     * @return true if the object is put in the inventory ; false if it is refused 
     */
    public boolean receiveObject(BaseObject object) {
        String clName = object.getClassName();

        if (clName == "Bag") {
            if (this.bag == null) {
                this.bag = (Bag) object;
                return true;
            }
        } else if (clName == "Belt") {
            if (this.belt == null) {
                this.belt = (Belt) object;
                return true;
            }
        } else if (clName == "BodyArmor") {
            if (this.bodyArmor == null) {
                this.bodyArmor = (BodyArmor) object;
                return true;
            }
        } else if (clName == "Book") {
            if (this.book == null) {
                this.book = (Book) object;
                return true;
            }
        } else if (clName == "Bow") {
            if (this.bow == null) {
                this.bow = (Bow) object;
                return true;
            }
        } else if (clName == "HeadArmor") {
            if (this.headArmor == null) {
                this.headArmor = (HeadArmor) object;
                return true;
            }
        } else if (clName == "HeavyWeapon") {
            if (this.heavyWeapon == null) {
                this.heavyWeapon = (HeavyWeapon) object;
                return true;
            }
        } else if (clName == "Purse") {
            if (this.purse == null) {
                this.purse = (Purse) object;
                return true;
            }
        }

        return this.bag.addObject(object);
    }

    /* - - - - - - - - - - - - - - Getters / Setters - - - - - - - - - - - - - - - - - - -*/

    /** Get the body armor.
     * @return bodyArmor
     */
    public BodyArmor getBodyArmor() {
        return this.bodyArmor;
    }

    /** Set the body armor.
     * @param bodyArmor the new body armor
     */
    public void setBodyArmor(BodyArmor bodyArmor) {
        this.bodyArmor = bodyArmor;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the head armor.
     * @return headArmor
     */
    public HeadArmor getHeadArmor() {
        return this.headArmor;
    }

    /** Set the head armor.
     * @param headArmor the new head armor
     */
    public void setHeadArmor(HeadArmor headArmor) {
        this.headArmor = headArmor;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the heavy weapon.
     * @return heavyWeapon
     */
    public HeavyWeapon getHeavyWeapon() {
        return this.heavyWeapon;
    }

    /** Set the heavy weapon.
     * @param heavyWeapon the new heavy weapon
     */
    public void setHeavyWeapon(HeavyWeapon heavyWeapon) {
        this.heavyWeapon = heavyWeapon;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the bow.
     * @return bow
     */
    public Bow getBow() {
        return this.bow;
    }

    /** Set the bow.
     * @param bow the new bow
     */
    public void setBow(Bow bow) {
        this.bow = bow;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the belt weapon.
     * @return beltWeapon
     */
    public LightWeapon getBeltWeapon() {
        return this.beltWeapon;
    }

    /** Set the belt weapon.
     * @param beltWeapon the new belt weapon
     */
    public void setBeltWeapon(LightWeapon beltWeapon) {
        this.beltWeapon = beltWeapon;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the weapon hidden in right sleeve.
     * @return rightSleeveWeapon
     */
    public LightWeapon getRightSleeveWeapon() {
        return this.rightSleeveWeapon;
    }

    /** Set the weapon hidden in right sleeve.
     * @param rightSleeveWeapon the new weapon hidden in right sleeve
     */
    public void setRightSleeveWeapon(LightWeapon rightSleeveWeapon) {
        this.rightSleeveWeapon = rightSleeveWeapon;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the weapon hidden in left sleeve.
     * @return leftSleeveWeapon
     */
    public LightWeapon getLeftSleeveWeapon() {
        return this.leftSleeveWeapon;
    }

    /** Set the weapon hidden in left sleeve.
     * @param leftSleeveWeapon the new weapon hidden in left sleeve
     */
    public void setLeftSleeveWeapon(LightWeapon leftSleeveWeapon) {
        this.leftSleeveWeapon = leftSleeveWeapon;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the weapon hidden in right boot.
     * @return rightBootWeapon
     */
    public LightWeapon getRightBootWeapon() {
        return this.rightBootWeapon;
    }

    /** Set the weapon hidden in right boot.
     * @param rightBootWeapon the new weapon hidden in right boot
     */
    public void setRightBootWeapon(LightWeapon rightBootWeapon) {
        this.rightBootWeapon = rightBootWeapon;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the weapon hidden in left boot.
     * @return leftBootWeapon
     */
    public LightWeapon getLeftBootWeapon() {
        return this.leftBootWeapon;
    }

    /** Set the weapon hidden in left boot.
     * @param leftBootWeapon the new weapon hidden in left boot
     */
    public void setLeftBootWeapon(LightWeapon leftBootWeapon) {
        this.leftBootWeapon = leftBootWeapon;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the purse.
     * @return purse
     */
    public Purse getPurse() {
        return this.purse;
    }

    /** Set the purse.
     * @param purse the new purse
     */
    public void setPurse(Purse purse) {
        this.purse = purse;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the bag.
     * @return bag
     */
    public Bag getBag() {
        return this.bag;
    }

    /** Set the bag.
     * @param bag the new bag
     */
    public void setBag(Bag bag) {
        this.bag = bag;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the belt.
     * @return belt
     */
    public Belt getBelt() {
        return this.belt;
    }

    /** Set the belt.
     * @param belt the new belt
     */
    public void setBelt(Belt belt) {
        this.belt = belt;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the object ready for right hand.
     * @return rightObject
     */
    public BaseObject getRightObject() {
        return this.rightObject;
    }

    /** Set the object ready for right hand.<br>
     * This method does not check for validity - you can put anything in hand.
     * @param rightObject the new object ready for right hand
     */
    public void setRightObject(BaseObject rightObject) {
        this.rightObject = rightObject;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the object ready for left hand.
     * @return leftObject
     */
    public BaseObject getLeftObject() {
        return this.leftObject;
    }

    /** Set the object ready for left hand.
     * This method does not check for validity - you can put anything in hand.
     * @param leftObject the new object ready for left hand
     */
    public void setLeftObject(BaseObject leftObject) {
        this.leftObject = leftObject;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get the book.
     * @return book
     */
    public Book getBook() {
        return this.book;
    }

    /** Set the book.
     * @param book the new book
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
