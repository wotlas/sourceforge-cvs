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

public class LieMemoryIterator
{
 /*------------------------------------------------------------------------------------*/

  // First element of the chain ( Element is in an internal class )
     private Element first;

  // current element of the chain
     private Element current;

  // last element of the chain
     private Element last;

 /*------------------------------------------------------------------------------------*/

  /** Inserts the new lieMemory just before the last lieMemory you retrieved with the next() call.
   *
   * @param lieMemory lieMemory to insert
   */
     public void insert( LieMemory lieMemory )
     {
         Element toAdd = new Element();
         toAdd.lieMemory = lieMemory;

         // empty list ?
            if(first==null) {
               first = toAdd;
               last = first;
               return;
            }

         // element just after the element we want to add
            Element justAfterAdd = null; 

            if(current==null || current.prev==null)
                   justAfterAdd = last;
            else
                   justAfterAdd= current.prev;
                     
            if(justAfterAdd.prev!=null) {
                  justAfterAdd.prev.next = toAdd;
                  toAdd.prev = justAfterAdd.prev;
                  toAdd.next = justAfterAdd;
                  justAfterAdd.prev = toAdd;
            }
            else {
                  first = toAdd;
                  toAdd.next = justAfterAdd;
                  justAfterAdd.prev = toAdd;
            }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Adds the given lieMemory to the end of our list. The Iterator is not updated.
   *
   * @param lieMemory lieMemory to add
   */
     public void add( LieMemory lieMemory )
     {
         Element toAdd = new Element();
         toAdd.lieMemory = lieMemory;
         
         if(last==null){
            first = toAdd;
            last = first;
         }
         else {
            last.next = toAdd;
            toAdd.prev = last;
            last = toAdd;
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Replace the last lieMemory you retrieved with the next() call by the specified lieMemory.
   *  
   * @param lieMemory lieMemory to set
   */
     public void replace( LieMemory lieMemory )
     {
         if(current==null){
              if(last!=null)
                 last.lieMemory=lieMemory;
         }
         else if (current.prev!=null)
              current.prev.lieMemory = lieMemory;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We remove the lieMemory returned by the last next() call.
   */
     public void remove()
     {
         Element toRemove = null;
               
         if(current==null) {
             if(last!=null)
                  toRemove = last;
             else
                  return; // empty list
         }
         else
             toRemove = current.prev;

         if(toRemove.prev!=null)
             toRemove.prev.next = toRemove.next;
         else
             first = toRemove.next;

         if(toRemove.next!=null)
             toRemove.next.prev = toRemove.prev;
         else
             last = toRemove.prev;

         toRemove.prev = null;
         toRemove.next = null;
         toRemove.lieMemory = null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We remove all the lieMemorys.
   */
     public void clear() {
         current = first;

         while( current!=null ) {
          current.lieMemory = null;
          current.prev = null;
          current = current.next;
         }

         if( last!=null )
             last.next =null;

         first = null;
         last = null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Resets this iterator ( use hasNext() & next() methods ).
    */
      public void resetIterator() {
          current = first;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Resets this iterator to the end of the lieMemory list ( use hasPrev() & prev()
    *  methods ). 
    */
      public void resetIteratorToEnd() {
          current = last;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/ 
 
   /** Returns true if there is a next element.
    *  @return true if there is a next element.
    */
      public boolean hasNext() {
          return current!=null;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns true if there is a previous element.
    *  @return true if there is a previous element.
    */
      public boolean hasPrev() {
          return current!=null;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the next lieMemory.
    *  @return next lieMemory
    */
       public LieMemory next() {
           if(current==null) return null;
            LieMemory lieMemory = current.lieMemory;
            current = current.next;
            return lieMemory;
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the previous lieMemory.
    *  @return previous lieMemory
    */
       public LieMemory prev() {
           if(current==null) return null;
            LieMemory lieMemory = current.lieMemory;
            current = current.prev;
            return lieMemory;
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Element class
    */
       static class Element{        
          public LieMemory lieMemory;
          public Element prev;
          public Element next;
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}


