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
 * The purse. Special Container that may only contain ValuedObjects.
 * 
 * @author Elann
 * @see wotlas.common.object.ContainerObject
 * @see wotlas.common.object.ValuedObject
 * @see wotlas.common.object.TransportableInterface
 */

public class Purse extends ContainerObject implements TransportableInterface
{

 /*------------------------------------------------------------------------------------*/

  private ValuedObject content;
 
 /*------------------------------------------------------------------------------------*/

 /** The only constructor. Calls ContainerObject's constructor.
  * @param capacity the number of objects that can be contained
  */
  public Purse(short capacity)
  {
   super(capacity);  
   this.className="Purse";
   this.objectName="standard purse";	  // to modify -> player name ?
  }
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a valued object to the purse.
   * @param o the object to add
   */
    public void addObject(ValuedObject o)
	{
	 super.addObject(o);
	}

  /** Remove a valued object from the purse.
   * @param o the object to remove. Can be found by getObjectByName() or getObjectAt()
   */
    public void removeObject(ValuedObject o)
	{
	 super.removeObject(o);
	}	
	
  /** Retrieve a valued object from the purse. This method does not check validity.
   * @param pos the position of the valued object in the container
   * @return the valued object required 
   */
   	public ValuedObject getObjectAt(short pos) throws ArrayIndexOutOfBoundsException
	{
	 return (ValuedObject)super.getObjectAt(pos);
	}

  /** Retrieve a valued object from the purse by name.
   * @param name the name of the object wanted
   * @return the object required 
   */
	public ValuedObject getObjectByName(String name) // dnk if useful - Code first C later
	{
	 return (ValuedObject)super.getObjectByName(name);
	}

	
 /*------------------------------------------------------------------------------------*/
	
 
}

