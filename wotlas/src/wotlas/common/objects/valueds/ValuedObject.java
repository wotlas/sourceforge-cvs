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

 /** The value of the object. It is expressed in MK.<br>
  * The lowest value is 0.01. (1 CP)<br> 
  *<br><br>
  * <table border=1>
  * <tr><th>Value</th><th>Equals</th><th>Names</th></tr>
  * <tr><td>10 CP</td><td>1 SP</td><td>(Copper Penny - Silver Penny)</td></tr>
  * <tr><td>10 SP</td><td>1 MK</td><td>(Silver Penny - silver MarK)</td></tr>
  * <tr><td>10 MK</td><td>1 GC</td><td>(silver MarK  - Gold Coin)</td></tr>
  * </table>
  */
    protected double value;

  /** The quantity owned.
   */
    protected short quantity;
  
 /*------------------------------------------------------------------------------------*/
	
 /** Default constructor. <br>
  * Sets value and quantity to 0.
  */ 
   public ValuedObject()
   {
   	super();
	
	value=0.0;
	quantity=0;
	
	className="ValuedObject";
	objectName="default valued object";
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
	
 /** Gets the value of the object. 
  * @return value
  */
   public double getValue() { return value; }
   
 /** Sets the value of the object.
  * @param value the new value
  */
   public void setValue(float value) { this.value=value; }
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

