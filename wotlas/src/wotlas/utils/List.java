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

package wotlas.utils;


/** A list that implements the same methods as a java.utils.Vector
 *  WITH the difference that you can only remove elements from the head
 *  or the tail ( We optimized these operations by implementing defered
 *  destruction ).
 * <br><p>
 * <b>Important</b>: The <i>List</i> methods are not synchronized.
 * <br><p>
 * <b>Known use</b>: A* pathfindig algorithm (see {@link wotlas.libs.pathfinding.AStar A*} )
 *
 * @author Aldiss, Petrus
 */

public class List
{
 /*------------------------------------------------------------------------------------*/

    /** 
     *  Object Array
     */
       private Object array[];

   /**
    *  index of the last valid element of this array
    */
       private int last_element;

   /** index of the first valid element of this array
    */
       private int first_element;

   /**
    *  Array growth
    */
       private int growth;

   /**
    *  Initial capacity
    */
       private int initial_capacity;


 /*------------------------------------------------------------------------------------*/

    /** Constructor with a default initial list capacity of 10 and a
     * default growth of 10.
     */
       public List(){
           last_element=-1;
           first_element=-1;
           array = new Object[10];
           growth = 10;
           initial_capacity = 10;
       }

 /*------------------------------------------------------------------------------------*/

    /** Constructor with initial list capacity and a default growth of 10.
     *
     * @param initial_capacity initial list capacity
     */
       public List( int initial_capacity ){
           last_element=-1;
           first_element=-1;
           array = new Object[initial_capacity];
           growth = 10;
           this.initial_capacity = initial_capacity;
       }

 /*------------------------------------------------------------------------------------*/

    /** Constructor with initial list capacity and list growth.
     *
     * @param initial_capacity initial list capacity
     * @param growth array growth when the capacity is reached.
     */
       public List( int initial_capacity, int growth ){
           last_element=-1;
           first_element=-1;
           array = new Object[initial_capacity];
           this.initial_capacity = initial_capacity;
           this.growth = growth;
       }

 /*------------------------------------------------------------------------------------*/

    /** Adds this element at the end of the list.
     *
     * @param o object to add
     */
       public void addElement( Object o )
       {
        // array to short ?
           if( last_element==array.length-1 ) {
               Object array_tmp[] = new Object[array.length+growth-first_element];
               System.arraycopy( array, first_element, array_tmp, 0, array.length-first_element );

               last_element = array.length-first_element-1;
               first_element = 0;
               array = array_tmp;
           }

        // add of the element
           last_element++;
           array[last_element] = o;

           if(first_element==-1)
                first_element=0;
                
       }

 /*------------------------------------------------------------------------------------*/

    /** Removes the first element of the list.
     */
       public void removeFirstElement()
       {
        // Empty array ?
           if( last_element==-1 )
               return;

        // Last element ?
       	   if( first_element==last_element ) {
               array = new Object[initial_capacity];
               first_element = -1;
       	       last_element = -1;
       	       return;
           }

        // we erase the element WITHOUT shifting the elements...
           array[first_element] = null;
           first_element++;

        // defered destruction...
           if( first_element>=2*growth ) {
               Object array_tmp[] = new Object[array.length-growth];
               System.arraycopy( array, first_element, array_tmp, 0, last_element-first_element+1 );

               last_element = last_element-first_element;
               first_element = 0;
               array = array_tmp;
           }
       }

 /*------------------------------------------------------------------------------------*/

    /** Removes the last element of the list.
     */
       public void removeLastElement()
       {
        // empty array ?
           if( last_element==-1 )
               return;

        // last element ?
       	   if( last_element==first_element ) {
               array = new Object[initial_capacity];
               first_element = -1;
       	       last_element = -1;
       	       return;
           }

        // we erase the element...
           array[last_element] = null;
           last_element--;

        // defered destruction
           if(array.length-last_element>2*growth) {
               Object array_tmp[] = new Object[array.length-growth];
               System.arraycopy( array, first_element, array_tmp, 0, last_element-first_element+1 );

               last_element = last_element-first_element;
               first_element = 0;
               array = array_tmp;
           }
       }

 /*------------------------------------------------------------------------------------*/

    /** Removes all the elements of the list.
     */
       public void removeAllElements(){
           if( last_element==-1 )
               return;

           array = new Object[initial_capacity];
           first_element = -1;
           last_element = -1;
       }

 /*------------------------------------------------------------------------------------*/

    /** To get the element at the specied index.
     *
     * @param index element index
     */
       public Object elementAt( int index ){        
           return array[index+first_element];          
       }

 /*------------------------------------------------------------------------------------*/

    /** Insert this element at the specified position of the list.
     *  The elements of the array beginning at "index" are shifted to
     *  the right.
     *
     * @param o object to add.
     * @param index index of insertion
     */
       public void insertElementAt( Object o, int index )
       {
        // list is empty ?
           if (last_element==-1) {
              addElement( o );
              return;
           }
        
        // simple add ?
           if( index>last_element-first_element ) {
              addElement( o );
              return;
           }

           Object array_tmp[];

        // array length problem for the shift ?
           if( last_element-first_element+1==array.length )
               array_tmp = new Object[array.length+growth];
             else
               array_tmp = new Object[array.length-first_element+1];

       // copy & shift
          if(index>0)
              System.arraycopy( array, first_element, array_tmp, 0, index );
          
          System.arraycopy( array, index+first_element, array_tmp, index+1, last_element-first_element-index+1 );
          
       // insertion
          last_element = last_element-first_element+1;
          first_element = 0;
          array = array_tmp;
          array[index] = o;
       }

 /*------------------------------------------------------------------------------------*/

    /** To get the size of the list.
     *
     * @return the list's size
     */
       public int size(){
           if (last_element==-1)
             return 0;           
           return last_element-first_element+1;
       }

 /*------------------------------------------------------------------------------------*/

    /** To test if the list is empty
     *
     * @return true if the list is empty
     */
       public boolean isEmpty(){
           return (last_element == -1);           
       }


 /*------------------------------------------------------------------------------------*/

    /** To print the content of the list.
     *
     * @return a string representation of the list
     */
       public String toString(){
           StringBuffer s = new StringBuffer(
                                 "List:\n First> "+first_element
                                 +"\n  Last> "+last_element
                                 +"\n   Array Size> "
                                 +array.length+"    Growth> "+growth+"\n     Elements> " );

           if(last_element==-1) {
              s.append( "none.");
              return s.toString();
           }

           s.append( "{ ");


           for( int i=first_element; i<=last_element; i++)
                s.append( "["+(i-first_element)+"]:"+array[i].toString()+", ");

           s.append( "}");
           return s.toString();
       }

 /*------------------------------------------------------------------------------------*/

 /* TEST
   public static void main(String argv[]) {
	
    List list = new List( 3, 2 );

    System.out.println( list );

    list.addElement( (Object) new String("0") );
    list.addElement( (Object) new String("1") );
    list.addElement( (Object) new String("2") );

    System.out.println( "Should be 0, 1, 2\n"+list );

    list.addElement( (Object) new String("3") );
    list.addElement( (Object) new String("5") );
    list.addElement( (Object) new String("6") );

    System.out.println( "Should be 0, 1, 2, 3, 5, 6\n"+list );

    list.insertElementAt( (Object) new String("4"), 4 );

    System.out.println( "Should be 0, 1, 2, 3, 4, 5, 6\n"+list );

    list.insertElementAt( (Object) new String("7"), 7 );

    list.removeFirstElement();
    list.removeFirstElement();
    list.removeLastElement();
    list.removeLastElement();
    list.removeLastElement();
    list.removeLastElement();

    System.out.println( "Should be 2, 3,\n"+list );

    list.insertElementAt( (Object) new String("0"), 0 );
    list.insertElementAt( (Object) new String("1"), 1 );

    System.out.println( "Should be 0, 1, 2, 3,\n"+list );

}
*/
}



