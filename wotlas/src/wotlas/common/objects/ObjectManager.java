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
 
package wotlas.common.objects;

//import java.rmi.Remote;
//import java.rmi.RemoteException;

//import wotlas.utils.Tools;

import wotlas.common.objects.inventories.Inventory;	

/** 
 * The ObjectManager. Used to handle all kind of Object and Inventory.<br>
 * Has different implementations in client and server sides.
 * @author Elann
 */

public class ObjectManager
{

 /*------------------------------------------------------------------------------------*/
 
 /** An Inventory object.
  */
  private Inventory inventory;
 
 
 /*------------------------------------------------------------------------------------*/
		
  /* ------- Constructor ----- */
  
  /** Default constructor.
   *
   */
   	public ObjectManager()
	{
	
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
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

