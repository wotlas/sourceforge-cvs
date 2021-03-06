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

import wotlas.common.objects.BaseObject;
import wotlas.common.objects.interfaces.ContainerInterface;
import wotlas.common.objects.interfaces.TransportableInterface;

/** 
 * The base class for container objects.
 * 
 * @author Elann
 * @see wotlas.common.objects.BaseObject
 * @see wotlas.common.objects.interfaces.ContainerInterface
 */
public class ContainerObject extends BaseObject implements ContainerInterface {

    /*------------------------------------------------------------------------------------*/

    /** The default capacity of a container. Used when the default constructor is invoked.
     */
    public final static short defaultContainerCapacity = 50;

    /** The capacity of the container
     */
    protected transient short capacity;

    /** The quantity contained
     */
    protected transient short quantity;

    /** The content
     */
    protected BaseObject[] content;

    /*------------------------------------------------------------------------------------*/

    /** The default constructor
     */
    public ContainerObject() {
        this.capacity = ContainerObject.defaultContainerCapacity;
        this.quantity = 0;
        this.content = new BaseObject[this.capacity];

        this.className = "ContainerObject";
        this.objectName = "default container";
    }

    /** The parametric constructor
     * @param capacity the maximum number of Objects contained. Used to create an array.  
     */
    public ContainerObject(short capacity) {
        this.capacity = capacity;
        this.quantity = 0;
        this.content = new BaseObject[capacity];

        this.className = "ContainerObject";
        this.objectName = "default container";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Empty the container on the ground. Equivalent to discard on all contained objects.<br>
     * Use a typecast to an interface to achieve this. May be a bit slow.
     */
    public void empty() {
        for (int i = 0; i < this.quantity; i++)
            ((TransportableInterface) this.content[i]).discard();

        this.content = new BaseObject[this.capacity];
    }

    /** Returns the first available cell in an array. Warning : may be out of bounds. The caller must check.
     */
    private short findFirstFree(Object[] tab) {
        short i;

        for (i = 0; i < tab.length && tab[i] != null; i++)
            ;

        return i;
    }

    /** Add an object to the container.
     * @param o the object to add
     * @return false if max capacity is already reached ; true if the object was added
     */
    public boolean addObject(BaseObject o) {
        if (this.quantity < this.capacity)
            this.content[findFirstFree(this.content)] = o;
        else
            // implementors should check return value
            return false;

        this.quantity++;
        return true;
    }

    /** Remove an object from the container.
     * @param o the object to remove. Can be found by getObjectByName() or getObjectAt()
     */
    public void removeObject(BaseObject o) {
        int i = 0;
        while (i < this.capacity && this.content[i] != o)
            i++;

        if (i >= this.capacity) // may throw something. Must see with implementors
            return;

        this.content[i] = null;
    }

    /** Retrieve an object from the container. This method does not check validity.
     * @param pos the position of the object in the container
     * @return the object required 
     */
    public BaseObject getObjectAt(short pos) throws ArrayIndexOutOfBoundsException {
        return this.content[pos];
    }

    /** Retrieve an object from the container by name.
     * @param name the name of the object wanted
     * @return the object required 
     */
    public BaseObject getObjectByName(String name) // dnk if useful - Code first C later
    {
        int i = 0;
        while (i < this.capacity && this.content[i].getObjectName() != name)
            i++;

        if (i >= this.capacity)
            return null;

        return this.content[i];
    }

    /** Get the raw content.
     * @return content 
     */
    public BaseObject[] getContent() {
        return this.content;
    }

    /** Set the content.
     * @param content the content array 
     */
    public void getContent(BaseObject[] content) {
        this.content = content;

        if (content != null) {
            this.capacity = (short) content.length;

            for (int i = 0; i < content.length; i++)
                if (content[i] == null) {
                    this.quantity = (short) i;
                    return;
                }

            this.quantity = this.capacity;
        }
    }

}
