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

 /** The value of the object. It is expressed in MK. 
  * Perhaps there should be another field for integer value (expressed in CP).
  *
  * 10 CP = 1 SP  (Copper Penny - Silver Penny)
  * 10 SP = 1 MK  (Silver Penny - silver MarK)
  * 10 MK = 1 GC  (silver MarK  - Gold Coin)
  */
  	protected float value;

  /** The quantity owned.
   */
    protected short quantity;
  
 /*------------------------------------------------------------------------------------*/
	
 /** Default constructor
  */ 
   public ValuedObject()
   {
   	super();
   }
	
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

