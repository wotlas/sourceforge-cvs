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

import wotlas.common.Character;
import wotlas.common.objects.valueds.ValuedObject;

/** 
 * The objects that can be taken and carried.
 * 
 * @author Elann
 */

public interface TransportableInterface
{

 /*------------------------------------------------------------------------------------*/

   /** Gets rid of the object. The object is dropped on the ground.
   */
    public void discard();

  /** Sells the object to somebody.
  	  @param buyer The Character who buy the object. 
  	  @return the prize paid.
   */
    public ValuedObject sellTo(Character buyer);

  /** Gives the object to somebody.
  	  @param receiver The Character who receive the object.
   */
    public void giveTo(Character receiver);
	
		
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

