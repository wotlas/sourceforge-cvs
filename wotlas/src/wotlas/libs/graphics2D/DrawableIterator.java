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

package wotlas.libs.graphics2D;

/**
 * An DrawableIterator iterates over a chained list of Drawables. You can use the provided
 * methods to iterate over the drawables : the resetIterator() method resets the iterator.
 *
 * Concurrent accesses ARE NOT supported. Only one thread should use this iterator at the
 * same time.
 *
 * @author aldiss
 */

public class DrawableIterator
{
 /*------------------------------------------------------------------------------------*/

  // First element of the chain ( Element is in an internal class )
     private Element first;

  // current element of the chain
     private Element current;

  // last element of the chain
     private Element last;

 /*------------------------------------------------------------------------------------*/

  /** Inserts the new drawable just before the last drawable you retrieved with the next() call.
   *
   * @param drawable drawable to insert
   */
     public void insert( Drawable drawable )
     {
         Element toAdd = new Element();
         toAdd.drawable = drawable;

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

  /** Adds the given drawable to the end of our list. The Iterator is not updated.
   *
   * @param drawable drawable to add
   */
     public void add( Drawable drawable )
     {
         Element toAdd = new Element();
         toAdd.drawable = drawable;
         
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

  /** Replace the last drawable you retrieved with the next() call by the specified drawable.
   *  
   * @param drawable drawable to set
   */
     public void replace( Drawable drawable )
     {
         if(current==null){
              if(last!=null)
                 last.drawable=drawable;
         }
         else if (current.prev!=null)
              current.prev.drawable = drawable;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We remove the drawable returned by the last next() call.
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
         toRemove.drawable = null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We remove all the drawables.
   */
     public void clear() {
         current = first;

         while( current!=null ) {
          current.drawable = null;
          current.prev = null;
          current = current.next;
         }

         if( last!=null )
             last.next =null;

         first = null;
         last = null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Resets this iterator.
    */
      public void resetIterator() {
          current = first;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
   /** Returns true if there is a next element.
    *  @return true if there is a next element.
    */
      public boolean hasNext() {
          return current!=null;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the next drawable.
    *  @return next drawable
    */
       public Drawable next() {
           if(current==null) return null;
            Drawable drawable = current.drawable;
            current = current.next;
            return drawable;
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Element class
    */
       static class Element{        
          public Drawable drawable;
          public Element prev;
          public Element next;
       }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}


