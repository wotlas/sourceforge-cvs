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
 
package wotlas.common.objects.armors;

import wotlas.common.objects.BaseObject;
import wotlas.common.objects.interfaces.*;

/** 
 * The base class for all pieces of armor.
 * 
 * @author Elann
 * @see wotlas.common.objects.BaseObject
 * @see wotlas.common.objects.interfaces.ArmorInterface
 * @see wotlas.common.objects.interfaces.TransportableInterface
 */

public class Armor extends BaseObject implements ArmorInterface, RepairInterface, TransportableInterface
{

 /*------------------------------------------------------------------------------------*/


  /** The defense of the armor - may be zero.
   */
      private short defense;

  /** The current state of the armor. Goes from newly-made to broken.
   */
   	  private short state;  
	  
	  private static String[] stateList={"Newly-made","Good state","Used","Worned out","Broken"}; 
	  		  // that's just place-holder stuff, OK ?
			  // may be a file or a static list
			  // but better if in a file => internationalization / evolution 
	  	 
  /** Is the armor in a bag or on the char ?
   */
	  private boolean equipped;
	  	  
	  
 /*------------------------------------------------------------------------------------*/
 
  /** The only constructor.
	* @param defense the defense of the armor - may be zero
	* @param state the current state of the armor  	
   */			
    public Armor(short defense,short state)
	{
	 this.defense=defense;
	 this.state=state;
	 this.equipped=false;
	 
	 this.className="Armor";
	 this.objectName="default armor";
	}															

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Gets the defense of the armor.
    * @return defense
    */
	public short getDefense() { return this.defense; }
 
  /** Sets the defense of the armor.
    * @param defense the new defense
    */
	public void setDefense(short defense) { this.defense=defense; }

	
  /** Gets the state of the armor.
    * @return state
    */
	public short getState() { return this.state; }
 
  /** Sets the state of the armor.
    * @param state the new state
    */
	public void setState(short state) { this.state=state; }
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

