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
 
package wotlas.common.objects.usefuls;

import wotlas.common.objects.interfaces.*;

/** 
 * The keys class.
 * 
 * @author Elann
 * @see wotlas.common.objects.usefuls.UsefulObject
 * @see wotlas.common.objects.interfaces.LockableInterface
 * @see wotlas.common.objects.interfaces.TransportableInterface 
 */

public class Key extends UsefulObject implements TransportableInterface 
{

 /*------------------------------------------------------------------------------------*/

 /** Key identifier.
  */
  	private int iKeyID;
	
 /** Key string identifier.
  */
  	private String sKeyID; 																						
	  
 /*------------------------------------------------------------------------------------*/
 
  /** The default constructor.
   */			
    public Key()
	{
	 this.className="Key";
	 this.objectName="default key";
	}															
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /** Get the key integer identifier.
  * @return iKeyID
  */
  	public int getIKeyID()
	{
	 return this.iKeyID;
	}
	
 /** Set the key integer identifier.
  * @param iKeyID the new key identifier
  */
    public void setIKeyID(int iKeyID)
	{
	 this.iKeyID=iKeyID;
	} 
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /** Get the key string identifier.
  * @return sKeyID
  */
  	public String getSKeyID()
	{
	 return this.sKeyID;
	}
	
 /** Set the key string identifier.
  * @param sKeyID the new key identifier
  */
    public void setSKeyID(String sKeyID)
	{
	 this.sKeyID=sKeyID;
	} 
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

