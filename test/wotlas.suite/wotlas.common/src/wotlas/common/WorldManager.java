/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
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

package wotlas.common;

import java.io.File;
import wotlas.common.objects.inventories.RoomInventory;
import wotlas.common.router.MessageRouterFactory;
import wotlas.common.universe.Building;
import wotlas.common.universe.InteriorMap;
import wotlas.common.universe.Room;
import wotlas.common.universe.TownMap;
import wotlas.common.universe.WorldMap;
import wotlas.common.universe.WotlasLocation;
import wotlas.utils.Debug;

/** A WorldManager provides all the methods needed to handle & manage the game world
 *  from its root.<p><br>
 *
 *  This class IS NOT directly persistent. The WorldMap instances are made persistent
 *  by calling saveUniverse() & loadUniverse() methods. The files are saved separatly.
 *  This is why a WorldManager is not directly persistent : we don't want to save
 *  all its data in one huge file.
 *
 * @author Petrus, Aldiss
 * @see wotlas.common.universe.WorldMap
 * @see wotlas.libs.persistence.PropertiesConverter
 */

public class WorldManager {

    /*------------------------------------------------------------------------------------*/

    /** Game Universe Name Format
     */
    public final static String DEFAULT_UNIVERSE = "default";
    public final static String UNIVERSE_PREFIX = "universe-save-";
    public final static String UNIVERSE_SUFFIX = "";

    public final static String WORLD_FILE = "world.cfg";
    public final static String TOWN_FILE = "town.cfg";
    public final static String BUILDING_FILE = "building.cfg";
    public final static String MAP_SUFFIX = "-map.cfg";

    public final static String DEFAULT_UNIVERSE_OBJECTS = "objects";
    public final static String INVENTORY_PREFIX = "room-inventory-";
    public final static String INVENTORY_SUFFIX = ".cfg";

    /*------------------------------------------------------------------------------------*/

    /** array of WorldMap
     */
    protected WorldMap[] worldMaps;

    /** Our resource manager.
     */
    protected ResourceManager rManager;

    /*------------------------------------------------------------------------------------*/

    /** Constructor with resource Manager. We do not load universe data.
     * @param rManager resource manager to get the data from.
     */
    public WorldManager(ResourceManager rManager) {
        this.rManager = rManager;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with resource manager. We attempt to load the local universe data.
     * Any error at this step will stop the program.
     *
     * @param rManager resource manager to get the data from.
     * @param loadDefault do we have to load the default universe data (true) or the
     *                    current one ?
     */
    public WorldManager(ResourceManager rManager, boolean loadDefault) {
        this(rManager, loadDefault, true);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with resource manager. We attempt to load the local universe data.
     * Any error at this step will stop the program if failureIfNoData==true.
     *
     * @param rManager resource manager to get the data from.
     * @param loadDefault do we have to load the default universe data (true) or the
     *                    current one ?
     * @param failureIfNoData if true we'll produce a Debug.FAILURE+Debug.exit if no universe
     *        data is found, if false we'll just print a FAILURE message.
     */
    public WorldManager(ResourceManager rManager, boolean loadDefault, boolean failureIfNoData) {
        this.rManager = rManager;
        loadUniverse(loadDefault);

        if (this.worldMaps == null) {
            Debug.signal(Debug.FAILURE, this, "No universe data available !");

            if (failureIfNoData)
                Debug.exit();
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with a pre-loaded list of World Maps.
     *  @param worldMaps list of worlds...
     */
    public WorldManager(WorldMap worldMaps[], ResourceManager rManager) {
        this.rManager = rManager;
        this.worldMaps = worldMaps;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To initialize this whole universe ( it rebuilds shortcuts ). This method calls
     *  recursively the init() method of the WorldMaps, TownMaps, buildings, interiorMaps
     *  and rooms.
     *
     *  IMPORTANT: You must ONLY call this method ONE time when ALL the world data has been
     *  loaded...
     */
    protected void init() {

        // 1 - any data ?
        if (this.worldMaps == null) {
            Debug.signal(Debug.WARNING, this, "Universe inits failed: No WorldMaps.");
            return;
        }

        // 2 - we transmit the init() call
        for (int i = 0; i < this.worldMaps.length; i++)
            if (this.worldMaps[i] != null)
                this.worldMaps[i].init();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this worldmap for message routing. We create an appropriate message router
     *  for the world/towns/rooms via the provided factory. This method should be called once
     *  on the WorldManager at start-up. It's your job to create the factory.
     *
     *  If you don't call this method you won't be able to manage message routing among
     *  the different locations.
     *
     * @param msgRouterFactory our router factory for MessageRouter creation.
     */
    public void initMessageRouting(MessageRouterFactory msgRouterFactory) {
        // 1 - any data ?
        if (this.worldMaps == null) {
            Debug.signal(Debug.WARNING, this, "Universe routing inits failed: No WorldMaps.");
            return;
        }

        // 2 - we transmit the initMessageRouting() call
        for (int i = 0; i < this.worldMaps.length; i++)
            if (this.worldMaps[i] != null)
                this.worldMaps[i].initMessageRouting(msgRouterFactory, this);
    }

    /*------------------------------------------------------------------------------------*/

    /** To Get a World by its ID.
     *
     * @param id worldMapID
     * @return corresponding worldMap, null if ID does not exist.
     */
    public WorldMap getWorldMapFromID(int id) {
        if (this.worldMaps == null) {
            Debug.signal(Debug.ERROR, this, "No World data available.");
            return null;
        }

        if (id >= this.worldMaps.length || id < 0) {
            Debug.signal(Debug.ERROR, this, "getWorldMapFromID : Bad world ID " + id);
            return null;
        }

        return this.worldMaps[id];
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get a World list (some entries might be null).
     *  @return worldMaps possessed by this WorldManager.
     */
    public WorldMap[] getWorldMaps() {
        return this.worldMaps;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get a World from a Wotlas location.
     *
     * @param location wotlas location
     * @return corresponding worldMap, null if ID does not exist.
     */
    public WorldMap getWorldMap(WotlasLocation location) {
        return getWorldMapFromID(location.getWorldMapID());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get a Town from a WotlasLocation. IMPORTANT: we assume the WotlasLocation object
     *  points out at least a townMap...
     *
     * @param location wotlas location
     * @return corresponding townMap, null if the map does not exist.
     */
    public TownMap getTownMap(WotlasLocation location) {

        WorldMap wMap = getWorldMapFromID(location.getWorldMapID());

        if (wMap == null)
            return null;

        return wMap.getTownMapFromID(location.getTownMapID());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get a Building from a WotlasLocation. IMPORTANT: we assume the
     *  WotlasLocation object points out at least a building...
     *
     * @param location wotlas location
     * @return corresponding building, null if the map does not exist.
     */
    public Building getBuilding(WotlasLocation location) {

        TownMap tMap = getTownMap(location);

        if (tMap == null)
            return null;

        return tMap.getBuildingFromID(location.getBuildingID());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get an InteriorMap from a WotlasLocation. IMPORTANT: we assume the
     *  WotlasLocation object points out at least an interiorMap...
     *
     * @param location wotlas location
     * @return corresponding interiorMap, null if the map does not exist.
     */
    public InteriorMap getInteriorMap(WotlasLocation location) {

        Building bMap = getBuilding(location);

        if (bMap == null)
            return null;

        return bMap.getInteriorMapFromID(location.getInteriorMapID());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get a Room from a WotlasLocation. IMPORTANT: we assume the
     *  WotlasLocation object points out a room...
     *
     * @param location wotlas location
     * @return corresponding Room, null if the map does not exist.
     */
    public Room getRoom(WotlasLocation location) {

        InteriorMap iMap = getInteriorMap(location);

        if (iMap == null)
            return null;

        return iMap.getRoomFromID(location.getRoomID());
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new WorldMap object to the array {@link #worldMaps worldMaps}
     *
     * IMPORTANT: WorldManager methods are not synchronized. Handle this method
     * with care ( Server locked and no connected clients ).
     *
     * @return a new WorldMap object
     */
    public WorldMap addWorldMap() {

        WorldMap myWorldMap = new WorldMap();

        if (this.worldMaps == null) {
            this.worldMaps = new WorldMap[1];
            myWorldMap.setWorldMapID(0);
            this.worldMaps[0] = myWorldMap;
        } else {
            WorldMap[] myWorldMaps = new WorldMap[this.worldMaps.length + 1];
            myWorldMap.setWorldMapID(this.worldMaps.length);
            System.arraycopy(this.worldMaps, 0, myWorldMaps, 0, this.worldMaps.length);
            myWorldMaps[this.worldMaps.length] = myWorldMap;
            this.worldMaps = myWorldMaps;
        }
        return myWorldMap;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a player to this universe.
     * @param player player to add to this world.
     */
    public void addPlayerToUniverse(Player player) {
        editPlayer(player, true); // no control on server location, we assume locality
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Remove a player from the universe.
     * @param player the player to remove.
     */
    public void removePlayerFromUniverse(Player player) {
        editPlayer(player, false);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a player to this universe (addButNotRemove=true) or removes a player from
     *  this universe (addButNotRemove=false). The player must have been previously initialized.
     *  IMPORTANT: if the location points out a room we assume that the room is LOCAL, i.e.
     *             local to this server.
     *
     * @param player player to add/remove
     * @param addButNotRemove set to true if tou want to add this player, set to false
     *        if you want to remove the player.
     */
    protected void editPlayer(Player player, boolean addButNotRemove) {

        // Get Location & location type
        WotlasLocation location = player.getLocation();

        if (location == null) {
            Debug.signal(Debug.ERROR, this, "Player " + player.toString() + " has no WotlasLocation.");
            return;
        }

        // does this world exists ?
        WorldMap world = getWorldMapFromID(location.getWorldMapID());

        if (world == null) {
            Debug.signal(Debug.ERROR, this, "Player " + player.toString() + " has bad location.");
            return;
        }

        // add/remove player
        if (location.isWorld()) {
            if (addButNotRemove)
                world.getMessageRouter().addPlayer(player);
            else
                world.getMessageRouter().removePlayer(player);
        } else {
            // does this town exists ?
            TownMap town = world.getTownMapFromID(location.getTownMapID());

            if (town == null) {
                Debug.signal(Debug.ERROR, this, "Player " + player.toString() + " has bad location.");
                return;
            }

            if (location.isTown()) {
                if (addButNotRemove)
                    town.getMessageRouter().addPlayer(player);
                else
                    town.getMessageRouter().removePlayer(player);
            } else if (location.isRoom()) {
                // does this building exists ?
                Building building = town.getBuildingFromID(location.getBuildingID());

                if (building == null) {
                    Debug.signal(Debug.ERROR, this, "Player " + player.toString() + " has bad location.");
                    return;
                }

                // does this interiorMap exists ?
                InteriorMap map = building.getInteriorMapFromID(location.getInteriorMapID());

                if (map == null) {
                    Debug.signal(Debug.ERROR, this, "Player " + player.toString() + " has bad location.");
                    return;
                }

                // does this room exists ?
                Room room = map.getRoomFromID(location.getRoomID());

                if (room == null) {
                    Debug.signal(Debug.ERROR, this, "Player " + player.toString() + " has bad location.");
                    return;
                }

                // pheewww... ok, we add/remove this player...
                if (addButNotRemove)
                    room.getMessageRouter().addPlayer(player);
                else
                    room.getMessageRouter().removePlayer(player);
            } else
                Debug.signal(Debug.ERROR, this, "Player " + player.toString() + " has strange location.");
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To Get a valid WorldID ( for player inits ).
     *
     * @return a valid worldMap ID, -1 if there are none
     */
    public int getAValidWorldID() {
        if (this.worldMaps == null)
            return -1;

        for (int i = 0; i < this.worldMaps.length; i++)
            if (this.worldMaps[i] != null)
                return i;

        return -1;
    }

    /*------------------------------------------------------------------------------------*/

    /** To load the local game universe.
     *
     *  @param loadDefault do we have to load default data ?
     */
    public void loadUniverse(boolean loadDefault) {
        int worldCount = 0, townCount = 0, buildingCount = 0, mapCount = 0;

        String universeHome = this.rManager.getUniverseDataDir() + WorldManager.DEFAULT_UNIVERSE + "/";

        /*** STEP 1 - WE LOAD LOCATIONS (default data) ***/

        String worldList[] = this.rManager.listUniverseDirectories(universeHome);
        Debug.signal(Debug.NOTICE, null, "Loading Universe Data from :" + universeHome);
        this.worldMaps = null;

        // ok, here we go ! we load all the worlds we can find...
        for (int w = 0; w < worldList.length; w++) {

            // we load the world object
            if (worldList[w].endsWith("/CVS/") || worldList[w].endsWith("\\CVS\\"))
                continue; // this is a CVS directory

            WorldMap world = (WorldMap) this.rManager.loadObject(worldList[w] + WorldManager.WORLD_FILE);

            if (world == null) {
                Debug.signal(Debug.WARNING, this, "Failed to load World : " + worldList[w]);
                continue;
            }

            if (this.worldMaps == null) {
                this.worldMaps = new WorldMap[world.getWorldMapID() + 1];
            } else if (this.worldMaps.length <= world.getWorldMapID()) {
                WorldMap[] myWorldMaps = new WorldMap[world.getWorldMapID() + 1];
                System.arraycopy(this.worldMaps, 0, myWorldMaps, 0, this.worldMaps.length);
                this.worldMaps = myWorldMaps;
            }

            this.worldMaps[world.getWorldMapID()] = world;
            worldCount++;

            // we load all the towns of this world
            String townList[] = this.rManager.listUniverseDirectories(worldList[w]);

            for (int t = 0; t < townList.length; t++) {

                // we load the town objects
                TownMap town = (TownMap) this.rManager.loadObject(townList[t] + WorldManager.TOWN_FILE);

                if (town == null) {
                    Debug.signal(Debug.WARNING, this, "Failed to load Town : " + townList[t]);
                    continue;
                }

                world.addTownMap(town);
                townCount++;

                // we load all this town's buildings
                String buildingList[] = this.rManager.listUniverseDirectories(townList[t]);

                for (int b = 0; b < buildingList.length; b++) {

                    // we load the building objects
                    Building building = (Building) this.rManager.loadObject(buildingList[b] + WorldManager.BUILDING_FILE);

                    if (building == null) {
                        Debug.signal(Debug.WARNING, this, "Failed to load building : " + buildingList[b]);
                        continue;
                    }

                    town.addBuilding(building);
                    buildingCount++;

                    // we load all this building's maps
                    String mapList[] = this.rManager.listUniverseFiles(buildingList[b], WorldManager.MAP_SUFFIX);

                    for (int m = 0; m < mapList.length; m++) {
                        if (mapList[m].equals(buildingList[b] + WorldManager.BUILDING_FILE))
                            continue;

                        // we load the map objects
                        InteriorMap map = (InteriorMap) this.rManager.loadObject(mapList[m]);

                        if (map == null) {
                            Debug.signal(Debug.WARNING, this, "Failed to load map : " + mapList[m]);
                            continue;
                        }

                        building.addInteriorMap(map);
                        mapCount++;
                    }
                }
            }
        }

        Debug.signal(Debug.NOTICE, null, "World Manager loaded " + worldCount + " worlds, " + townCount + " towns, " + buildingCount + " buildings, " + mapCount + " maps.");

        /*** STEP 2 - WE LOAD OBJECTS (latest data) ***/
        int roomInventoryCount = 0;

        if (!loadDefault) { // if loaddefault==true we only want to save default location data, not objects

            String objectsHome = this.rManager.getUniverseDataDir() + WorldManager.DEFAULT_UNIVERSE_OBJECTS + "/";

            worldList = this.rManager.listUniverseDirectories(objectsHome);

            // ok, here we go ! we load all the objects we can find...
            for (int w = 0; w < worldList.length; w++) {

                // we load the world that contain objects
                if (worldList[w].endsWith("/CVS/") || worldList[w].endsWith("\\CVS\\"))
                    continue; // this is a CVS directory

                // we load all the towns of this world
                String townList[] = this.rManager.listUniverseDirectories(worldList[w]);

                for (int t = 0; t < townList.length; t++) {

                    // we load all this town's buildings
                    String buildingList[] = this.rManager.listUniverseDirectories(townList[t]);

                    for (int b = 0; b < buildingList.length; b++) {

                        // we load all this building's maps
                        String mapList[] = this.rManager.listUniverseFiles(buildingList[b], WorldManager.MAP_SUFFIX);

                        for (int m = 0; m < mapList.length; m++) {

                            // we load the map objects
                            RoomInventory roomInv = (RoomInventory) this.rManager.loadObject(mapList[m]);

                            if (roomInv == null) {
                                Debug.signal(Debug.WARNING, this, "Failed to load inventory : " + mapList[m]);
                                continue;
                            }

                            // we associate the RoomInventory to its Room
                            Room associatedRoom = getRoom(roomInv.getLocation());

                            if (associatedRoom == null) {
                                Debug.signal(Debug.ERROR, this, "Failed to find room for roomInventory : " + mapList[m]);
                                continue;
                            }

                            associatedRoom.setInventory(roomInv);
                            roomInventoryCount++;
                        }
                    }
                }
            }
        }

        Debug.signal(Debug.NOTICE, null, "World Manager loaded " + roomInventoryCount + " room inventories.");

        /*** STEP 3 - WE INIT THE WORLD ***/
        init();
    }

    /*------------------------------------------------------------------------------------*/

    /** To save the local game universe.
     *
     *  @param isDefault if true we save all data in their DEFAULT directory.
     *  @return true in case of success, false otherwise
     */
    public boolean saveUniverse(boolean isDefault) {

        int worldCount = 0, townCount = 0, buildingCount = 0, mapCount = 0;

        /*** STEP 1 - We only have to save location data if they are to be saved as default ***/

        if (isDefault) {

            String universeHome = this.rManager.getUniverseDataDir() + WorldManager.DEFAULT_UNIVERSE + "/";

            // We create this directory... (we expect that the directory is outside the JAR)
            new File(universeHome).mkdirs();

            // ok, here we go ! we save all our world data...
            Debug.signal(Debug.NOTICE, this, "Saving Universe Data to :" + universeHome);

            for (int w = 0; w < this.worldMaps.length; w++) {
                if (this.worldMaps[w] == null)
                    continue;

                // we create the world directory
                String worldHome = universeHome + this.worldMaps[w].getShortName() + "/";
                new File(worldHome).mkdir();

                // we save the world object
                if (!this.rManager.saveObject(this.worldMaps[w], worldHome + WorldManager.WORLD_FILE)) {
                    Debug.signal(Debug.ERROR, this, "Failed to save world : " + worldHome);
                    continue;
                }

                worldCount++;

                // we save all the towns of this world
                TownMap towns[] = this.worldMaps[w].getTownMaps();
                if (towns == null)
                    continue;

                for (int t = 0; t < towns.length; t++) {
                    if (towns[t] == null)
                        continue;

                    // we save the town object
                    String townHome = worldHome + towns[t].getShortName() + "/";
                    new File(townHome).mkdir();

                    if (!this.rManager.saveObject(towns[t], townHome + WorldManager.TOWN_FILE)) {
                        Debug.signal(Debug.ERROR, this, "Failed to save town : " + townHome);
                        continue;
                    }

                    townCount++;

                    // we save all this town's buildings
                    Building buildings[] = towns[t].getBuildings();
                    if (buildings == null)
                        continue;

                    for (int b = 0; b < buildings.length; b++) {
                        if (buildings[b] == null)
                            continue;

                        // we save the building objects
                        String buildingHome = townHome + buildings[b].getShortName() + "/";
                        new File(buildingHome).mkdir();

                        if (!this.rManager.saveObject(buildings[b], buildingHome + WorldManager.BUILDING_FILE)) {
                            Debug.signal(Debug.ERROR, this, "Failed to save building : " + buildingHome);
                            continue;
                        }

                        buildingCount++;

                        // we save all this building's maps
                        InteriorMap interiorMaps[] = buildings[b].getInteriorMaps();
                        if (interiorMaps == null)
                            continue;

                        for (int m = 0; m < interiorMaps.length; m++) {
                            if (interiorMaps[m] == null)
                                continue;

                            // we save the maps
                            String mapName = buildingHome + interiorMaps[m].getShortName() + WorldManager.MAP_SUFFIX;

                            if (!this.rManager.saveObject(interiorMaps[m], mapName)) {
                                Debug.signal(Debug.ERROR, this, "Failed to save map : " + mapName);
                                continue;
                            }

                            mapCount++;
                        }
                    }
                }
            }
        }

        /*** STEP 2 - WE SAVE OBJECTS DATA
         ***
         *** Note that objects are only stored in Rooms (RoomInventory).
         ***/
        int inventoryCount = 0;

        if (!isDefault) { // we only save objects if default==false ( default means location data only )

            String objectsHome = this.rManager.getUniverseDataDir() + WorldManager.DEFAULT_UNIVERSE_OBJECTS + "/";

            // We create this directory... (we expect that the directory is outside the JAR)
            new File(objectsHome).mkdirs();

            // ok, here we go ! we save all our world data...
            Debug.signal(Debug.NOTICE, this, "Saving Universe's Objects to :" + objectsHome);

            for (int w = 0; w < this.worldMaps.length; w++) {
                if (this.worldMaps[w] == null)
                    continue;

                // we create the world directory
                String worldHome = objectsHome + this.worldMaps[w].getShortName() + "/";
                new File(worldHome).mkdir();

                // we save all the town objects of this world
                TownMap towns[] = this.worldMaps[w].getTownMaps();
                if (towns == null)
                    continue;

                for (int t = 0; t < towns.length; t++) {
                    if (towns[t] == null)
                        continue;

                    // we save the town object
                    String townHome = worldHome + towns[t].getShortName() + "/";
                    new File(townHome).mkdir();

                    // we save all this town's buildings
                    Building buildings[] = towns[t].getBuildings();
                    if (buildings == null)
                        continue;

                    for (int b = 0; b < buildings.length; b++) {
                        if (buildings[b] == null)
                            continue;

                        // we save the building's objects
                        String buildingHome = townHome + buildings[b].getShortName() + "/";
                        new File(buildingHome).mkdir();

                        // we save all this building's maps
                        InteriorMap interiorMaps[] = buildings[b].getInteriorMaps();
                        if (interiorMaps == null)
                            continue;

                        for (int m = 0; m < interiorMaps.length; m++) {
                            if (interiorMaps[m] == null)
                                continue;

                            // we save all the RoomInventories
                            Room rooms[] = interiorMaps[m].getRooms();
                            if (rooms == null)
                                continue;

                            for (int r = 0; r < rooms.length; r++) {
                                if (rooms[r] == null || rooms[r].getInventory() == null)
                                    continue;

                                String inventoryName = buildingHome + WorldManager.INVENTORY_PREFIX + rooms[r].getMyInteriorMap().getInteriorMapID() + "-" + rooms[r].getRoomID() + WorldManager.INVENTORY_SUFFIX;

                                if (!this.rManager.saveObject(rooms[r].getInventory(), inventoryName)) {
                                    Debug.signal(Debug.ERROR, this, "Failed to save room inventory : " + inventoryName);
                                    continue;
                                }

                                inventoryCount++;
                            }
                        }
                    }
                }
            }
        }

        /*** STEP 3 - Print some stats for control ***/
        if (isDefault)
            Debug.signal(Debug.NOTICE, this, "Saved " + worldCount + " worlds, " + townCount + " towns, " + buildingCount + " buildings, " + mapCount + " maps.");
        else
            Debug.signal(Debug.NOTICE, this, "Saved " + inventoryCount + " room inventories.");

        return true;
    }

    /*------------------------------------------------------------------------------------*/

}
