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
<PERSISTENCE CORRECT>
<WORLD SHORTCUTS TO DO>

 /** A WorldManager provides all the methods needed to handle & manage the game world
  *  from its root.<p><br>
  *
  *  This class IS NOT directly persistent. The WorldMap instances are made persistent
  *  by calling the PersistenceManager BUT the PersistenceManager save them in separated
  *  files. This is why a WorldManager is not directly persistent : we don't want to save
  *  all its data in one huge file.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.WorldMap
  */
 
public class WorldManager
{
 /*------------------------------------------------------------------------------------*/
 
  /** array of WorldMap
   */
   private WorldMap[] worldMaps;

 /*------------------------------------------------------------------------------------*/
  
  /** Constructor. Attemps to load the local universe data. Any error at this
   * step will stop the program.
   */

   public WorldManager() {

       // we use the PersistenceManager to load the worlds.
          if( !loadLocalUniverse() ) {
              Debug.signal( Debug.FAILURE, null, "Could not load Data ! Exiting..." );
              System.exit(1);
          }

       // We rebuild shortcuts

   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Get World by ID.
   *
   * @param id worldMapID
   * @return corresponding worldMap, null if ID does not exist.
   */
   public WorldMap getWorldMapByID( int id ) {
   	if(id>=worldMaps.length) {
           Debug.signal( Debug.ERROR, this, "getWorldMapByID : Bad world ID "+id );
   	   return null;
   	}
   	
        return worldMaps[id];
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load our local game universe. 
   *
   * @return true in case of success, false otherwise.
   */

   public boolean loadLocalUniverse() {

       // Call to the PersistenceManager to load the worlds from the dataBase.
          try{
              PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
              worldMaps = pm.loadLocalUniverse();
          }
          catch( PersistenceException pe ) {
              Debug.signal( Debug.FAILURE, this, pe );
              return false;
          }

      return true;
   }
  
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To save our local game universe. 
   *
   * @return true in case of success, false otherwise.
   */

   public boolean saveLocalUniverse() {
       // Call to the PersistenceManager to save the worlds to the dataBase.
          try{
              PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
              pm.saveLocalUniverse( worldMaps );
          }
          catch( PersistenceException pe ) {
              Debug.signal( Debug.FAILURE, this, pe );
              return false;
          }

      return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Add a new WorldMap object to the array {@link #worldMaps worldMaps}
   *
   * IMPORTANT: WorldManager methods are not synchronized. Handle this method
   * with care ( Server locked and no connected clients ).
   *
   * @return a new WorldMap object
   */
   public WorldMap addWorldMap()
   {
     WorldMap myWorldMap = new WorldMap();
    
     if (worldMaps == null) {
       worldMaps = new WorldMap[1];      
       myWorldMap.setWorldMapID(0);
       worldMaps[0] = myWorldMap;
     } else {
       WorldMap[] myWorldMaps = new WorldMap[worldMaps.length+1];
       myWorldMap.setWorldMapID(worldMaps.length);
       System.arraycopy(worldMaps, 0, myWorldMaps, 0, worldMaps.length);
       myWorldMaps[worldMaps.length] = myWorldMap; 
       worldMaps = myWorldMaps;      
     }    
     return myWorldMap;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Add a player to this universe. This method is called at system init and should not
   *  be used in any other cases. Use movePlayer instead.
   *
   * @param player player to add to this world.
   */
   public void addNewPlayer( PlayerImpl player ) {
       addPlayer( player ); // no control on server location, we assume locality
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Add a player to this universe. The player must have been previously initialized.
   *  IMPORTANT: if the location point out a room we assume that the room is LOCAL, i.e.
   *             local to this server.
   *
   * @param player player to add
   */
   private void addPlayer( PlayerImpl player )
   {
      // Get Location & location type
         WotlasLocation location = player.getLocation();
      
      // add player
         if( location.isWorld() )
         {
          // does this world exists ?
             WorldMap world = getWorldMapByID( location.getWorldMapID() );

             if( world!=null ) {
             	 world.addPlayer( player );
                 return;
             }
         }
         else if( location.isTown() ) {

             return;
         }
         else if( location.isRoom() ) {
         
             return;
         }

      Debug.signal( Debug.ERROR, this, "Player "+player.toString()+" has bad location." );
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
     public boolean movePlayer( PlayerImpl player, WotlasLocation destination ){

      // EASY - is it a room to room move ?


      // EASY - is it a xxxx to town move ?

      // EASY - is it a xxxx to world move ?


      // MEDIUM - is it a interiorMap to interiorMap move ?

      // COMPLEX - is it a xxxx to building move ?
     
        return false;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
