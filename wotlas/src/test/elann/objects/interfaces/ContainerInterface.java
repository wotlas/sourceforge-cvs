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
 
package wotlas.common.objects.interfaces;

import wotlas.common.objects.BaseObject;

/** 
 * The interface implemented by all things that may contain an object.
 * 
 * @author Elann
 */

public interface ContainerInterface
{

 /*------------------------------------------------------------------------------------*/

  /** Empty the container on the ground.
   */
    public void empty();
	
  /** Add an object to the container.
   * @param o the object to add
   */
   	public void addObject(BaseObject o);
	
  /** Remove an object from the container.
   * @param o the object to remove. Can be found by getObjectByName() or getObjectAt()
   */
    public void removeObject(BaseObject o);
	
  /** Retrieve an object from the container.
   * @param pos the position of the object in the container
   * @return the object required 
   */
   	public BaseObject getObjectAt(short pos) throws ArrayIndexOutOfBoundsException;
	
  /** Retrieve an object from the container by name.
   * @param name the name of the object wanted
   * @return the object required 
   */
	public BaseObject getObjectByName(String name); // dnk if useful - C later
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

