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
 
package wotlas.common.objects.valueds;

import wotlas.common.objects.BaseObject;

/** 
 * The base class for all valued objects.
 * 
 * @author Elann
 * @see wotlas.common.objects.BaseObject
 */

public class ValuedObject extends BaseObject
{

 /*------------------------------------------------------------------------------------*/


  /** The quantity owned.
   */
      private short quantity;
  
 /*------------------------------------------------------------------------------------*/
	
 /** Gets the quantity.
  * @return quantity
  */ 
   public short getQuantity() { return quantity; }
   
 /** Sets the quantity.
  * @param quantity the new quantity
  */
   public void setQuantity() { this.quantity=quantity; }
	
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

