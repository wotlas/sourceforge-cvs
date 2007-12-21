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

import wotlas.common.Player;
import wotlas.common.objects.BaseObject;
import wotlas.common.objects.interfaces.TransportableInterface;
import wotlas.common.objects.valueds.ValuedObject;

/** 
 * The bow class.
 * 
 * @author Elann
 * @see wotlas.common.objects.weapons.RemoteWeapon
 * @see wotlas.common.objects.interfaces.RemoteWeaponInterface
 * @see wotlas.common.objects.interfaces.TransportableInterface
 */

public class Bow extends RemoteWeapon implements TransportableInterface {

    /*------------------------------------------------------------------------------------*/

    /*------------------------------------------------------------------------------------*/

    /** Default constructor
     */
    public Bow() {
        super();

        this.className = "Bow";
        this.objectName = "default bow";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Puts on the weapon to enable attack.<br>
     * Take one hand. When arm is invoked, both hands are taken.
     */
    @Override
    public void equip() {
        this.equipped = true;
    }

    /** Attacks the specified target.
     *
     * @param target the Player attacked
     * @return 0 because the damage is not instantly inflicted.
     */
    @Override
    public short attack(Player target) {
        loose();
        return 0;
    }

    /** Alternative attack on the specified target.<br>
     * No altern attack for bows. Calls attack(target).
     * @param target the Player attacked
     * @return the damage inflicted
     */
    @Override
    public short alternativeAttack(Player target) {
        return attack(target);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Gets rid of the object. The object is dropped on the ground.
    */
    public void discard() {
        /* no op */
    }

    /** Sells the object to somebody.
    	  @param buyer The Player who buy the object. 
    	  @return the prize paid.
     */
    public ValuedObject sellTo(Player buyer) {
        /* no op */
        return new ValuedObject();
    }

    /** Gives the object to somebody.
    	  @param receiver The Player who receive the object.
     */
    public void giveTo(Player receiver) {
        /* no op */
    }

    /** Trade the object to somebody.<br>
     * Here the transaction is already accepted.
    * @param buyer The Player who buy the object. 
    * @return the object given by the other player.
     */
    public BaseObject tradeTo(Player buyer) {
        /* no op */
        return new BaseObject();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
