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
 
package wotlas.common.objects.magicals;

import wotlas.common.power.*;		// One and True Power - Weaves
import wotlas.common.objects.usefuls.UsefulObject;

/** 
 * The base class for all magical objects.
 * 
 * @author Elann
 * @see wotlas.common.objects.usefuls.UsefulObject
 */

public abstract class MagicalObject extends UsefulObject
{

 /*------------------------------------------------------------------------------------*/

  /** The weave produced by the object. THE CLASS WEAVE DOES NOT EXIST.
   */
      private Weave magicEffect;
	  
  /** The powers involved in the magical action. THE CLASS POWER/POWERLIST DOES NOT EXIST.
   */
   	  private PowerList powersInvolved;
	  
	  
 /*------------------------------------------------------------------------------------*/
 
  /** The only constructor
   * @param magicEffect The Weave produced
   * @param powersInvolved The Powers involved
   */
   public MagicalObject(Weave magicEffect,PowerList powersInvolved)
   {
   	super();
	this.magicEffect=magicEffect;
	this.powersInvolved=powersInvolved;
	
	this.className="MagicalObject";
	this.objectName="default magical object";
   }
 
  /** Get the magical effect caused by the object when used.
   * @return magicEffect
   */  					
   	public Weave getMagicEffect()
	{
	 return this.magicEffect;
	}																	

  /** Get the powers involved in the magical effect.
   * @return powersInvolved
   */  					
   	public PowerList getPowersInvolved()
	{
	 return this.powersInvolved;
	}																	
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

