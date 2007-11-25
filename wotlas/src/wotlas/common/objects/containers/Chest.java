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
import wotlas.common.objects.valueds.ValuedObject;
import wotlas.common.Player;
import wotlas.common.objects.usefuls.Key;

/** 
 * A chest. Used to securely store objects.
 * 
 * @author Elann
 * @see wotlas.common.objects.containers.ContainerObject
 * @see wotlas.common.objects.interfaces.LockableInterface
 */
public class Chest extends ContainerObject implements LockableInterface
{

 /*------------------------------------------------------------------------------------*/

 /** The Key associated with the chest.
  */
  protected Key key;
 
 
 /*------------------------------------------------------------------------------------*/

  /** The default constructor. Calls ContainerObject's constructor.
   */
      public Chest()
	  {
	   super();
	   	   
	   className="Chest";
	   objectName="default chest";
	   key=null;
	  }
 
 /** Parametric constructor. Calls ContainerObject's constructor.
  * @param key the key working with the chest
  */
  public Chest(Key key)
  {
   super();
     
   className="Chest";
   objectName=key.getSKeyID()+"-chest";
   this.key=key;
  }

 
 /** Parametric constructor. Calls ContainerObject's constructor.
  * @param capacity the number of objects that can be contained
  */
  public Chest(short capacity)
  {
   super(capacity);
     
   className="Chest";
   objectName="default chest";
   key=null;
  }

 /** Full parametric constructor. Calls ContainerObject's constructor.
  * @param key the key working with the chest
  */
  public Chest(Key key,short capacity)
  {
   super(capacity);
     
   className="Chest";
   objectName=key.getSKeyID()+"-chest";
   this.key=key;
  }

  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
	
  /** Try to lock the object.
   * @param key the key used 
   */
    public void tryLock(Key key)
	{
	
	}

  /** Try to unlock the object.
   * @param key the key used 
   */
    public void tryUnlock(Key key)
	{
	
	}

 /*------------------------------------------------------------------------------------*/

  /** Get the associated Key
   * @return key the key for this chest 
   */
    public Key getKey()
	{
	 return key;
	}

  /** Set the associated Key
   * @param key the new key for this chest 
   */
    public void setKey(Key key)
	{
	 this.key=key;
	}
	
 /*------------------------------------------------------------------------------------*/

}

