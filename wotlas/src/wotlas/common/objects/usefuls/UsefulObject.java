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
 
package wotlas.common.objects.usefuls;

import wotlas.common.objects.BaseObject;
import wotlas.common.objects.interfaces.*;

/** 
 * The base class for all useful objects. (equippable objects)
 * 
 * @author Elann
 * @see wotlas.common.objects.BaseObject
 * @see wotlas.common.objects.interfaces.UsefulInterface
 */

public abstract class UsefulObject extends BaseObject implements UsefulInterface
{

 /*------------------------------------------------------------------------------------*/

 /** Is it ready ?
  */
  protected boolean ready;
  
 /*------------------------------------------------------------------------------------*/

  /** The default constructor.
   */			
    public UsefulObject()
	{
	 super();
	
	 className="UsefulObject";
	 objectName="default useful object";
	 
	 ready=false;	 
	}															

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
	
  /** Use the object. Lots of different implementations. 
   */
    public abstract void use();

  /** Ready the object for usage. Lots of different implementations.
   */
    public abstract void ready();

	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

