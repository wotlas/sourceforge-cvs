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

import wotlas.common.objects.interfaces.*;

/** 
 * The foot armor class.
 * 
 * @author Elann
 * @see wotlas.common.objects.BaseObject
 * @see wotlas.common.objects.interfaces.ArmorInterface
 */

public class FootArmor extends Armor implements ArmorInterface
{

 /*------------------------------------------------------------------------------------*/

 
 /*------------------------------------------------------------------------------------*/

  /** The only constructor.
  	* Invoques Armor's constructor.
	* @param defense the defense of the armor - may be zero
	* @param state the current state of the armor  	
   */			
    public FootArmor(short defense,short state)
	{
	 super(defense,state);
	 
	 this.className="FootArmor";
	 this.objectName="default foot armor";
	}															

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}
