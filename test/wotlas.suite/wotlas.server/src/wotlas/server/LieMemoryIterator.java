/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
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

package wotlas.server;

/**
 * An LieMemoryIterator iterates over a chained list of LieMemorys. You can use the provided
 * methods to iterate over the lieMemorys : the resetIterator() method resets the iterator.
 *
 * Concurrent accesses ARE NOT supported. Only one thread should use this iterator at the
 * same time.
 *
 * There are also three methods : resetIteratorToEnd(), hasPrev(), prev() to iterate over
 * the lieMemorys backward... BUT the remove(), replace() and insert() methods only work as
 * they do when you iterate with the hasNext(), next() methods.
 *
 * @author aldiss
 */

public class LieMemoryIterator {
    /*------------------------------------------------------------------------------------*/

    // First element of the chain ( Element is in an internal class )
    private Element first;

    // current element of the chain
    private Element current;

    // last element of the chain
    private Element last;

    private int size;

    public int getSize() {
        return this.size;
    }

    /*------------------------------------------------------------------------------------*/

    /** Inserts the new lieMemory just before the last lieMemory you retrieved with the next() call.
     *
     * @param lieMemory lieMemory to insert
     */
    public void insert(LieMemory lieMemory) {
        Element toAdd = new Element();
        toAdd.lieMemory = lieMemory;

        // empty list ?
        if (this.first == null) {
            this.first = toAdd;
            this.last = this.first;
            this.size = 1;
            return;
        }

        this.size++;

        // element just after the element we want to add
        Element justAfterAdd = null;

        if (this.current == null || this.current.prev == null)
            justAfterAdd = this.last;
        else
            justAfterAdd = this.current.prev;

        if (justAfterAdd.prev != null) {
            justAfterAdd.prev.next = toAdd;
            toAdd.prev = justAfterAdd.prev;
            toAdd.next = justAfterAdd;
            justAfterAdd.prev = toAdd;
        } else {
            this.first = toAdd;
            toAdd.next = justAfterAdd;
            justAfterAdd.prev = toAdd;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Adds the given lieMemory to the end of our list. The Iterator is not updated.
     *
     * @param lieMemory lieMemory to add
     */
    public void add(LieMemory lieMemory) {
        Element toAdd = new Element();
        toAdd.lieMemory = lieMemory;

        if (this.last == null) {
            this.first = toAdd;
            this.last = this.first;
            this.size = 1;
        } else {
            this.last.next = toAdd;
            toAdd.prev = this.last;
            this.last = toAdd;
            this.size++;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Replace the last lieMemory you retrieved with the next() call by the specified lieMemory.
     *  
     * @param lieMemory lieMemory to set
     */
    public void replace(LieMemory lieMemory) {
        if (this.current == null) {
            if (this.last != null)
                this.last.lieMemory = lieMemory;
        } else if (this.current.prev != null)
            this.current.prev.lieMemory = lieMemory;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** We remove the lieMemory returned by the last next() call.
     */
    public void remove() {
        Element toRemove = null;

        if (this.current == null) {
            if (this.last != null)
                toRemove = this.last;
            else
                return; // empty list
        } else
            toRemove = this.current.prev;

        this.size--;

        if (toRemove.prev != null)
            toRemove.prev.next = toRemove.next;
        else
            this.first = toRemove.next;

        if (toRemove.next != null)
            toRemove.next.prev = toRemove.prev;
        else
            this.last = toRemove.prev;

        toRemove.prev = null;
        toRemove.next = null;
        toRemove.lieMemory = null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** We remove all the lieMemorys.
     */
    public void clear() {
        this.current = this.first;

        while (this.current != null) {
            this.current.lieMemory = null;
            this.current.prev = null;
            this.current = this.current.next;
        }

        if (this.last != null)
            this.last.next = null;

        this.first = null;
        this.last = null;
        this.size = 0;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Resets this iterator ( use hasNext() & next() methods ).
     */
    public void resetIterator() {
        this.current = this.first;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Resets this iterator to the end of the lieMemory list ( use hasPrev() & prev()
     *  methods ). 
     */
    public void resetIteratorToEnd() {
        this.current = this.last;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns true if there is a next element.
     *  @return true if there is a next element.
     */
    public boolean hasNext() {
        return this.current != null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns true if there is a previous element.
     *  @return true if there is a previous element.
     */
    public boolean hasPrev() {
        return this.current != null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the next lieMemory.
     *  @return next lieMemory
     */
    public LieMemory next() {
        if (this.current == null)
            return null;
        LieMemory lieMemory = this.current.lieMemory;
        this.current = this.current.next;
        return lieMemory;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the previous lieMemory.
     *  @return previous lieMemory
     */
    public LieMemory prev() {
        if (this.current == null)
            return null;
        LieMemory lieMemory = this.current.lieMemory;
        this.current = this.current.prev;
        return lieMemory;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Element class
     */
    static class Element {
        public LieMemory lieMemory;
        public Element prev;
        public Element next;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
