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
 
package wotlas.server;

import wotlas.common.objects.ObjectManager;

import wotlas.common.objects.inventories.Inventory;	
import wotlas.common.objects.inventories.RoomInventory;	

import wotlas.common.universe.WotlasLocation;

/** 
 * The ServerObjectManager.<br>
 * Used to handle all kinds of Object and Inventory server side.
 * @see wotlas.client.ClientObjectManager 
 * @author Elann
 */

public class ServerObjectManager implements ObjectManager
{

 /*------------------------------------------------------------------------------------*/
  
 
 /*------------------------------------------------------------------------------------*/
		
  /* ------- Constructor ----- */
  
  /** Default constructor.
   *
   */
   	public ServerObjectManager()
	{
	
	}		
		
  /* ------- Methods --------- */
	
  /** Get the Inventory object of the player
   *  @return the player's Inventory
   */
    public Inventory getInventory() 
	{ 
	 Inventory ret=new Inventory();

	 /* no op */
	 /* should read on disk the stored inventory to rebuild one */
	 
	 return ret; 	
	}

  /** Set the Inventory of the Manager.
  	  @param inventory the new Inventory
   */
    public void setInventory(Inventory inventory)
	{
	 /* no op */ 
	}

	
  /** Get the RoomInventory object of the given room
   *  @return the room's RoomInventory
   *  @param roomID the WotlasLocation of the room
   */
    public RoomInventory getInventory(WotlasLocation roomID) 
	{ 
	 RoomInventory ret=new RoomInventory();

	 /* no op */
	 /* should read on disk the stored inventory to rebuild one */
	 
	 return ret; 	
	}

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

