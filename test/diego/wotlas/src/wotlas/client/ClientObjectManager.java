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
 
package wotlas.client;

import wotlas.common.objects.ObjectManager;

import wotlas.common.objects.inventories.Inventory;	

/** 
 * The ClientObjectManager.<br>
 * Used to handle Objects and Inventory client side.
 * @see wotlas.common.objects.ObjectManager
 * @see wotlas.server.ServerObjectManager
 * @author Elann
 */

public class ClientObjectManager implements ObjectManager
{

 /*------------------------------------------------------------------------------------*/
 
 /** The owned Inventory object.
  */
  protected Inventory inventory;
 
 /** The class of WotCharacter who owns me.<br> 
  * Set by the owner Player, depending on the implementor of WotCharacter it owns.
  */
  protected String characterClassName; 
 
 /*------------------------------------------------------------------------------------*/
		
  /* ------- Constructors ----- */
  
  /** Default constructor.
   *
   */
   	public ClientObjectManager()
	{
	 inventory=null;
	 characterClassName="Undefined";
	}		

  /** Parametric constructor.<br>
   * Sets the character class name.
   * @param characterClassName the character class name.
   */
   	public ClientObjectManager(String characterClassName)
	{
	 inventory=null;
	 this.characterClassName=characterClassName;
	}		
		

  /** Parametric constructor.<br>
   * Sets the inventory member.
   * @param inventory the player's inventory.
   */
   	public ClientObjectManager(Inventory inventory)
	{
	 this.inventory=inventory;
	 characterClassName="Undefined";
	}		


  /** Full parametric constructor.<br>
   * Sets the character class name and the inventory member.
   * @param characterClassName the character class name.
   * @param inventory the player's inventory.
   */
   	public ClientObjectManager(String characterClassName,Inventory inventory)
	{
	 this.inventory=inventory;
	 this.characterClassName=characterClassName;
	}		
		
		
  /* ------- Getters / Setters --------- */
	
  /** Get the Inventory object owned by the Manager.
  	  @return the Inventory
   */
    public Inventory getInventory() { return inventory; }

  /** Set the Inventory of the Manager.
  	  @param inventory the new Inventory
   */
    public void setInventory(Inventory inventory) { this.inventory=inventory; }


  /** Get the owning player's character's className
  	  @return characterClassName
   */
    public String getCharacterClassName() { return characterClassName; }
	
  /** Set the owning player's character's className
  	  @param characterClassName the new player's character className
   */
    public void setPlayerClassName(String characterClassName) { this.characterClassName=characterClassName; }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

