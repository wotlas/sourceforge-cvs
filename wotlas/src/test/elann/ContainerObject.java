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
 
package wotlas.common.object;

/** 
 * The base class for container objects.
 * 
 * @author Elann
 * @see wotlas.common.object.BaseObject
 * @see wotlas.common.object.ContainerInterface
 */

public class ContainerObject extends BaseObject implements ContainerInterface
{

 /*------------------------------------------------------------------------------------*/


  /** The capacity of the container
   */
      private short capacity;

  /** The quantity contained
   */
      private short quantity;

  /** The content
   */
      private BaseObject[] content;

  
 /*------------------------------------------------------------------------------------*/
	
  /** The unique constructor
   * @param capacity the maximum number of Objects contained. Used to create an array.  
   */
      public ContainerObject(short capacity)
	  {
	   this.capacity=capacity;
	   this.quantity=0;
	   content=new BaseObject[capacity];
	   
	   this.className="ContainerObject";
	   this.objectName="default container";
	  }
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Empty the container on the ground. Equivalent to discard on all contained objects.
   */
    public void empty()
	{
	 for (int i=0;i<quantity;i++)
	 	 content[i].discard();
		 
	 content=null;			  	 		 // must ask if it's correct to write this
	 content=new BaseObject[capacity]; 	 // or if there is another way to do that.
	}
	
  /** Add an object to the container.
   * @param o the object to add
   */
    public void addObject(BaseObject o)
	{
	 // to do
	}
 
}

