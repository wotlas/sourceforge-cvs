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

import wotlas.common.universe.*;

import wotlas.common.Player;

import wotlas.utils.Debug;

 /** A WorldManager provides all the methods needed to handle & manage the game world
  *  from its root.<p><br>
  *
  *  This class IS NOT directly persistent. The WorldMap instances are made persistent
  *  by calling the PersistenceManager BUT the PersistenceManager save them in separated
  *  files. This is why a WorldManager is not directly persistent : we don't want to save
  *  all its data in one huge file.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.WorlManager
  * @see wotlas.common.universe.WorldMap
  */
 
public class WorldManager extends wotlas.common.WorldManager
{
 /*------------------------------------------------------------------------------------*/
  
  /** Constructor. Attemps to load the local universe data. Any error at this
   * step will stop the program.
   */

   public WorldManager() {
          super();

       // we use the PersistenceManager to load the worlds.
          if( !loadLocalUniverse() ) {
              Debug.signal( Debug.FAILURE, null, "Could not load data correctly ! Exiting..." );
              System.exit(1);
          }

       // We rebuild shortcuts

   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load our local game universe. 
   *
   * @return true in case of success, false otherwise.
   */

   public boolean loadLocalUniverse() {

       // Call to the PersistenceManager to load the worlds from the dataBase.
          PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
          worldMaps = pm.loadLocalUniverse();
          return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To save our local game universe. 
   *
   * @return true in case of success, false otherwise.
   */

   public boolean saveLocalUniverse() {
       // Call to the PersistenceManager to save the worlds to the dataBase.
          PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
          return pm.saveLocalUniverse( worldMaps, false );
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /** Move player from one location to another. VERY IMPORTANT: the destination location can
  *  be on another server. In that case we move the entire client account on the other server
  *  and ask the client to reconnect on the other side... so this method can take huge time
  *  to complete in the worst case.
  *
  *  If something fails a debug information will be printed, this method will return
  *  false and the player's location will not have been changed.
  *
  *  @param player player to move
  *  @param destination destination
  *  @return true in case of success
  */
     public boolean movePlayer( PlayerImpl player, WotlasLocation destination )
     {
         WotlasLocation source = player.getLocation();

      // EASY - is it a room to room move ?
         if( source.isRoom() ) {

             if( destination.isRoom() ) {
                // 1 - let's find the RoomLink between the two rooms

                // 2 - let's check the player is in this RoomLink
                //     and that there are no doors...

                // 3 - move the player
                   editPlayer( player, false );                   
                   player.setLocation( destination );
                   editPlayer( player, true );
             }


         }

      // EASY - is it a xxxx to town move ?

      // EASY - is it a xxxx to world move ?


      // MEDIUM - is it a interiorMap to interiorMap move ?

      // COMPLEX - is it a xxxx to building move ?
     
        return false;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
