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
 * The base interface for all repairable objects.
 * 
 * @author Elann
 * @see wotlas.common.object.BaseObject
 */

public interface RepairInterface
{

 /*------------------------------------------------------------------------------------*/

  /** Get the object's state
   * @return state
   */ 				
   	public short getState();
	
  /** Get the knowledge needed to repair.
   * @return knowledge needed
   */ 																		
    public KnowledgeList getKnowledge();
	
  /** Get the materials needed to repair.
   * @ return material list
   */
    public MaterialList getMaterial();
 
  /** Repair the object.
   * @param repairer the Character that repairs the object. May be the owner or not.
   */
    public void repair(Character repairer);
	
	 
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

