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

 /** A WorldManager provides all the methods needed to handle & manage the game world
  *  from its root.<p><br>
  *
  *  This class IS NOT directly persistent. The WorldMap instances are made persistent
  *  by calling the PersistenceManager BUT the PersistenceManager save them in separated
  *  files. This is why a WorldManager is not directly persistent : we don't want to save
  *  all its data in one huge file.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.WorlManager
  * @see wotlas.common.universe.WorldMap
  */

public class WorldManager extends wotlas.common.WorldManager
{

 /*------------------------------------------------------------------------------------*/
  
  /** Constructor. Attemps to load the local universe data. Any error at this
   * step will stop the program.
   */
  WorldManager() {
    super();
  }
}