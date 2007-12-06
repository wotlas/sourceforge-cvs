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

package wotlas.common.objects.containers;

import wotlas.common.Player;
import wotlas.common.objects.BaseObject;
import wotlas.common.objects.interfaces.TransportableInterface;
import wotlas.common.objects.valueds.ValuedObject;

/** 
 * The purse. Special Container that may only contain ValuedObjects.
 * 
 * @author Elann
 * @see wotlas.common.objects.containers.ContainerObject
 * @see wotlas.common.objects.valueds.ValuedObject
 * @see wotlas.common.objects.interfaces.TransportableInterface
 */
public class Purse extends ContainerObject implements TransportableInterface {

    /*------------------------------------------------------------------------------------*/

    /** The default capacity of a purse. Used when the default constructor is invoked.
     */
    public final static short defaultPurseCapacity = 50;

    /** The actual content of the purse.
     */
    protected ValuedObject content;

    /*------------------------------------------------------------------------------------*/

    /** The default constructor.<br>
     * Calls ContainerObject's constructor.
     */
    public Purse() {
        super(Purse.defaultPurseCapacity);

        className = "Purse";
        objectName = "standard purse";
    }

    /** The parametric constructor.<br>
     * Calls ContainerObject's constructor.
     * @param capacity the number of objects that can be contained
     */
    public Purse(short capacity) {
        super(capacity);

        className = "Purse";
        objectName = "standard purse";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a valued object to the purse.
     * @param o the object to add
     * @return true if added - false if a problem occurs
     */
    @Override
    public boolean addObject(BaseObject o) {
        if (!"ValuedObject".equals(o.getClassName()))
            return false;

        return super.addObject(o);
    }

    /** Remove a valued object from the purse.
     * @param o the object to remove. Can be found by getObjectByName() or getObjectAt()
     */
    @Override
    public void removeObject(BaseObject o) {
        if (!"ValuedObject".equals(o.getClassName()))
            return; // throw ?

        super.removeObject(o);
    }

    /** Retrieve a valued object from the purse. This method does not check validity.
     * @param pos the position of the valued object in the container
     * @return the valued object required 
     */
    @Override
    public BaseObject getObjectAt(short pos) throws ArrayIndexOutOfBoundsException {
        return super.getObjectAt(pos);
    }

    /** Retrieve a valued object from the purse by name.
     * @param name the name of the object wanted
     * @return the object required 
     */
    @Override
    public BaseObject getObjectByName(String name) { // dnk if useful - Code first C later
        return super.getObjectByName(name);
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

    /*------------------------------------------------------------------------------------*/

}
