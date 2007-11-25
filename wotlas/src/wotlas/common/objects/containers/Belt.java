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
 
package wotlas.common.objects.containers;

import wotlas.common.objects.interfaces.*;
import wotlas.common.objects.BaseObject;
import wotlas.common.objects.valueds.ValuedObject;
import wotlas.common.Player;

/** 
 * The belt. Special Container that is also a piece of Armor.
 * 
 * @author Elann
 * @see wotlas.common.objects.containers.ContainerObject
 * @see wotlas.common.objects.interfaces.ArmorInterface
 * @see wotlas.common.objects.interfaces.TransportableInterface
 */
public class Belt extends ContainerObject implements TransportableInterface, ArmorInterface
{

 /*------------------------------------------------------------------------------------*/
  
  protected short defense;
  
  protected boolean equipped;
 
 /*------------------------------------------------------------------------------------*/

 /** The default constructor. Calls ContainerObject's constructor.
  */
  public Belt()
  {
   super();
     
   className="Belt";
   objectName="standard belt";	  // to modify
  }

 /** The parametric constructor. Calls ContainerObject's constructor.
  * @param capacity the number of objects that can be contained
  */
  public Belt(short capacity)
  {
   super(capacity);
     
   className="Belt";
   objectName="standard belt";	  // to modify
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
	
  /** Get the armor's defense points
   * @return defense
   */ 				
   	public short getDefense()
	{
	 return defense;
	} 																		

  /** Sets the defense of the armor.
    * @param defense the new defense
    */
	public void setDefense(short defense)
	{
	 this.defense=defense;
	}
 
  /** Puts on the armor.
   */
    public void equip()
	{
	 equipped=true;
	}

	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Gets rid of the object. The object is dropped on the ground.
   */
    public void discard()
	{
	 /* no op */
	}

  /** Sells the object to somebody.
  	  @param buyer The Player who buy the object. 
  	  @return the prize paid.
   */
    public ValuedObject sellTo(Player buyer)
	{
	 /* no op */
	 return new ValuedObject();
	}

  /** Gives the object to somebody.
  	  @param receiver The Player who receive the object.
   */
    public void giveTo(Player receiver)
	{
	 /* no op */
	}
	
  /** Trade the object to somebody.<br>
    * Here the transaction is already accepted.
  	* @param buyer The Player who buy the object. 
  	* @return the object given by the other player.
    */
    public BaseObject tradeTo(Player buyer)
	{
	 /* no op */
	 return new BaseObject();
	}
	
 /*------------------------------------------------------------------------------------*/
	
 
}

