/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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

package wotlas.common.universe;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Vector;
import wotlas.common.WorldManager;
import wotlas.common.environment.EnvironmentManager;
import wotlas.common.router.MessageRouter;
import wotlas.common.router.MessageRouterFactory;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.ImageIdentifier;
// FIXME ??? import wotlas.libs.npc.Npc;
import wotlas.libs.pathfinding.AStarDoubleServer;
import wotlas.libs.persistence.BackupReady;
import wotlas.libs.persistence.SendObjectReady;
import wotlas.libs.schedule.EncounterSchedule;
import wotlas.utils.ScreenPoint;

/** A TileMap represents a TileMap in our Game Universe.
 *
 * @author Petrus, Aldiss, Diego
 * @see wotlas.common.universe.WorldMap
 * @see wotlas.common.universe.Building
 */
public class TileMap extends PreloaderEnabled implements WotlasMap, BackupReady, SendObjectReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    public static byte FAKEISO = 1;
    public static byte FLAT = 2;
    public static Dimension PIXEL_32 = new Dimension(32, 32);
    public static Dimension PIXEL_50 = new Dimension(50, 50);
    public static Dimension PIXEL_40X80 = new Dimension(40, 80);
    public static Dimension PIXEL_80X40 = new Dimension(80, 40);
    public static Dimension PIXEL_70X35 = new Dimension(70, 35);

    public static byte TILE = 1;
    public static byte WALLX = 2;
    public static byte WALLY = 3;

    public static boolean TILE_FREE = true;
    public static boolean TILE_NOT_FREE = false;

    /*------------------------------------------------------------------------------------*/

    /** Movement Composer
    */
    transient private AStarDoubleServer aStarDoubleServer;

    private TileMapManager manager;

    private EncounterSchedule[] encounterSchedules;

    public Dimension mapTileDim;

    /** ID of the TileMap (index in the array of tilemaps in the WorldMap)
    */
    public int tileMapID;

    /** Name of the area where the map should be saved 
    * (it's a directory under the first tilemap)
    */
    private String areaName;

    /** Full name of the TileMap
    */
    private String fullName;

    /** Short name of the TileMap
    */
    private String shortName;

    /** Small Image (identifier) of this TileMap for WorldMaps.
    */
    private ImageIdentifier smallTileMapImage;

    /** value between FLAT | FAKEISO
    */
    private byte mapType;

    /** dimension of map
    */
    private Dimension mapSize;

    /** Point of insertion (teleportation, arrival)
    * this should be in a free tile
    */
    private ScreenPoint insertionPoint;

    /** Music Name
    */
    private String musicName;

    private byte graphicSet;
    //    private GroupOfGraphics[] groupOfGraphics;

    /** Link to the worldMap we belong to...
    */
    private transient WorldMap myWorldMap;

    /** Our message router. Owns the list of players of this map (not in buildings).
    */
    private transient MessageRouter messageRouter;

    /** holds the npc on this map
     */
    private transient Vector npcs;

    /*------------------------------------------------------------------------------------*/

    /** Constructor for persistence.
     */
    public TileMap() {
        this.loadStatus = PreloaderEnabled.LOAD_ALL;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with x,y positions & width,height dimension on WorldMap.
     * @param x x position of this building on a WorldMap.
     * @param y y position of this building on a WorldMap.
     * @param width width dimension of this building on a WorldMap.
     * @param height height dimension of this building on a WorldMap.
     */
    public TileMap(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.loadStatus = PreloaderEnabled.LOAD_ALL;
        this.npcs = new Vector();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /*
     * List of setter and getter used for persistence
     */
    public void setTileMapID(int myTileMapID) {
        this.tileMapID = myTileMapID;
    }

    public int getTileMapID() {
        return this.tileMapID;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaName() {
        return this.areaName;
    }

    public void setFullName(String myFullName) {
        this.fullName = myFullName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setShortName(String myShortName) {
        this.shortName = myShortName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setSmallTileMapImage(ImageIdentifier smallTileMapImage) {
        this.smallTileMapImage = smallTileMapImage;
    }

    public ImageIdentifier getSmallTileMapImage() {
        return this.smallTileMapImage;
    }

    public Dimension getMapSize() {
        return this.mapSize;
    }

    public void setMapSize(Dimension mapSize) {
        this.mapSize = mapSize;
    }

    // this should be in a free tile
    public void setInsertionPoint(ScreenPoint myInsertionPoint) {
        this.insertionPoint = myInsertionPoint;
    }

    // this should be in a free tile
    public ScreenPoint getInsertionPoint() {
        return new ScreenPoint(this.insertionPoint);
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicName() {
        return this.musicName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Transient fields getter & setter
     */

    public WorldMap getMyWorldMap() {
        return this.myWorldMap;
    }

    public MessageRouter getMessageRouter() {
        return this.messageRouter;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the wotlas location associated to this Map.
     *  @return associated Wotlas Location
     */
    public WotlasLocation getLocation() {
        WotlasLocation thisLocation = new WotlasLocation(this.myWorldMap.getWorldMapID());
        thisLocation.WotlasLocationChangeToTileMap(this.tileMapID);
        return thisLocation;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this tileMap ( it rebuilds shortcuts ). DON'T CALL this method directly, use
     *  the init() method of the associated world.
     *
     * @param myWorldMap our parent WorldMap.
     */
    public void init(WorldMap myWorldMap) {

        this.myWorldMap = myWorldMap;

        // 1 - any data ?

        // 2 - we transmit the init() call

        // 3 - MapExit inits
        if (this.manager.getMapExits() == null)
            return;

        WotlasLocation thisLocation = new WotlasLocation(myWorldMap.getWorldMapID());
        thisLocation.WotlasLocationChangeToTileMap(this.tileMapID);
        for (int i = 0; i < this.manager.getMapExits().length; i++)
            this.manager.getMapExits()[i].setMapExitLocation(thisLocation);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this tilemap for message routing. We create an appropriate message router
     *  for the tilemap via the provided factory.
     *
     *  Don't call this method yourself it's called via the WorldManager !
     *
     * @param msgRouterFactory our router factory
     */
    public void initMessageRouting(MessageRouterFactory msgRouterFactory, WorldManager wManager) {
        // build/get our router
        this.messageRouter = msgRouterFactory.createMsgRouterForTileMap(this, wManager);

        // we transmit the call to other layers
        // diego:actually no others layers
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** String Info.
     */
    @Override
    public String toString() {
        return "TileMap - " + this.fullName;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** id version of data, used in serialized persistance.
     */
    @Override
    public int ExternalizeGetVersion() {
        return 1;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** write object data with serialize.
     */
    @Override
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        if (this.loadStatus != PreloaderEnabled.LOAD_ALL) {
            System.out.println("Map [" + this.fullName + "] damaged, cause status of data before saving : " + this.loadStatus);
            return;
            //            throw new PreloaderException(loadStatus);
        }
        objectOutput.writeInt(ExternalizeGetVersion());
        super.writeExternal(objectOutput);
        objectOutput.writeInt(this.tileMapID);
        objectOutput.writeObject(this.areaName);
        objectOutput.writeObject(this.fullName);
        objectOutput.writeObject(this.shortName);
        objectOutput.writeObject(this.insertionPoint);
        objectOutput.writeByte(this.manager.getMapType());
        objectOutput.writeObject(this.smallTileMapImage);
        objectOutput.writeObject(this.musicName);
        objectOutput.writeObject(this.mapTileDim);
        objectOutput.writeByte(this.graphicSet);
        objectOutput.writeObject(this.mapSize);
        objectOutput.writeObject(this.manager);
        objectOutput.writeObject(this.encounterSchedules);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    @Override
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        this.loadStatus = PreloaderEnabled.setClassPreloader;
        if (IdTmp == ExternalizeGetVersion()) {
            super.readExternal(objectInput);
            this.tileMapID = objectInput.readInt();
            this.areaName = (String) objectInput.readObject();
            this.fullName = (String) objectInput.readObject();
            this.shortName = (String) objectInput.readObject();
            this.insertionPoint = (ScreenPoint) objectInput.readObject();
            this.mapType = objectInput.readByte();
            this.smallTileMapImage = (ImageIdentifier) objectInput.readObject();
            this.musicName = (String) objectInput.readObject();
            if (PreloaderEnabled.setClassPreloader == PreloaderEnabled.LOAD_MINIMUM_DATA)
                return;
            this.mapTileDim = (Dimension) objectInput.readObject();
            this.graphicSet = objectInput.readByte();
            this.mapSize = (Dimension) objectInput.readObject();
            this.manager = (TileMapManager) objectInput.readObject();
            this.manager.setTileMap(this);
            if (PreloaderEnabled.setClassPreloader == PreloaderEnabled.LOAD_CLIENT_DATA)
                return;
            if (PreloaderEnabled.setClassPreloader == PreloaderEnabled.LOAD_SERVER_DATA) {
                this.manager.freeMapBackGroundData();
                this.npcs = new Vector();
            }
            this.encounterSchedules = (EncounterSchedule[]) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }

    public byte getMapType() {
        return this.mapType;
    }

    public void setManager(TileMapManager manager) {
        this.manager = manager;
        this.manager.setTileMap(this);
        this.mapType = manager.getMapType();
    }

    public TileMapManager getManager() {
        return this.manager;
    }

    public Dimension getMapTileDim() {
        return this.mapTileDim;
    }

    public Dimension getMapFullSize() {
        Dimension mapFullSize = new Dimension();
        mapFullSize.width = this.mapSize.width * this.mapTileDim.width;
        mapFullSize.height = this.mapSize.height * this.mapTileDim.height;
        return mapFullSize;
    }

    public void selectGraphicSet(byte graphicSet) {
        this.graphicSet = graphicSet;
    }

    public void initGraphicSet(GraphicsDirector gDirector) {
        EnvironmentManager.initGraphics(gDirector, this.graphicSet);
        return;
    }

    public byte getGraphicSet() {
        return this.graphicSet;
    }

    public void drawAllLayer(GraphicsDirector gDirector) {
        this.manager.drawAllLayer(gDirector);
    }

    public void initNewTileMap(WorldMap myWorldMap) {
        this.myWorldMap = myWorldMap;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the eventual MapExit the given player is intersecting.
     *
     * @param rCurrent rectangle containing the player's current position, width & height
     * @return the Building the player is heading to (if he has reached it, or if there
     *         are any), null if none.
     */
    public MapExit isIntersectingMapExit(int destX, int destY, Rectangle rCurrent) {
        return this.manager.isIntersectingMapExit(destX, destY, rCurrent);
    }

    public MapExit[] getMapExits() {
        return this.manager.getMapExits();
    }

    public EncounterSchedule[] getEncounterSchedule() {
        return this.encounterSchedules;
    }

    public EncounterSchedule addEncounterSchedule() {
        EncounterSchedule my = new EncounterSchedule();

        if (this.encounterSchedules == null) {
            this.encounterSchedules = new EncounterSchedule[1];
            my.setId(0);
            this.encounterSchedules[0] = my;
        } else {
            EncounterSchedule[] myEncounterSchedules = new EncounterSchedule[this.encounterSchedules.length + 1];
            my.setId(this.encounterSchedules.length);
            System.arraycopy(this.encounterSchedules, 0, myEncounterSchedules, 0, this.encounterSchedules.length);
            myEncounterSchedules[this.encounterSchedules.length] = my;
            this.encounterSchedules = myEncounterSchedules;
        }
        return my;
    }

    @Override
    public void readObject(java.io.ObjectInputStream objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.loadStatus = objectInput.readByte();
            super.readObject(objectInput);
            this.tileMapID = objectInput.readInt();
            this.areaName = (String) objectInput.readObject();
            this.fullName = (String) objectInput.readObject();
            this.shortName = (String) objectInput.readObject();
            this.insertionPoint = (ScreenPoint) objectInput.readObject();
            this.mapType = objectInput.readByte();
            this.smallTileMapImage = (ImageIdentifier) objectInput.readObject();
            this.musicName = (String) objectInput.readObject();
            if (this.loadStatus == PreloaderEnabled.LOAD_MINIMUM_DATA)
                return;
            this.mapTileDim = (Dimension) objectInput.readObject();
            this.graphicSet = objectInput.readByte();
            // groupOfGraphics = ( GroupOfGraphics[] ) objectInput.readObject();
            this.mapSize = (Dimension) objectInput.readObject();
            this.manager = (TileMapManager) objectInput.readObject();
            this.manager.setTileMap(this);
            if (this.loadStatus == PreloaderEnabled.LOAD_CLIENT_DATA)
                return;
            if (this.loadStatus == PreloaderEnabled.LOAD_SERVER_DATA) {
                this.manager.freeMapBackGroundData();
                this.npcs = new Vector();
            }
            this.encounterSchedules = (EncounterSchedule[]) objectInput.readObject();
        } else {
            // ERORR IN THE STREAM : DIFFERENT VERSION OF CLIENT/SERVER
            // to do.... when new version
        }
    }

    @Override
    public void writeObject(java.io.ObjectOutputStream objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeByte(this.loadStatus);
        super.writeExternal(objectOutput);
        objectOutput.writeInt(this.tileMapID);
        objectOutput.writeObject(this.areaName);
        objectOutput.writeObject(this.fullName);
        objectOutput.writeObject(this.shortName);
        objectOutput.writeObject(this.insertionPoint);
        objectOutput.writeByte(this.manager.getMapType());
        objectOutput.writeObject(this.smallTileMapImage);
        objectOutput.writeObject(this.musicName);
        if (this.loadStatus == PreloaderEnabled.LOAD_MINIMUM_DATA)
            return;
        objectOutput.writeObject(this.mapTileDim);
        objectOutput.writeByte(this.graphicSet);
        //        objectOutput.writeObject( groupOfGraphics );
        objectOutput.writeObject(this.mapSize);
        objectOutput.writeObject(this.manager);
        if (this.loadStatus == PreloaderEnabled.LOAD_CLIENT_DATA)
            return;
        objectOutput.writeObject(this.encounterSchedules);
    }

    public StoreTileMapBackground getStoreBackground() {
        return (new StoreTileMapBackground(this.manager.getMapBackGroundData(), this.manager.getMapMask(), this.manager.getMapExits(), this.mapSize, this.fileName, this.areaName, this.fullName, this.shortName));
    }

    public void setStoreBackground(StoreTileMapBackground data) {
        this.manager.replaceMask(data.mask);
        this.manager.replaceGraphics(data.graphic);
        this.manager.replaceExits(data.exits);
        this.mapSize = data.mapSize;
        this.fileName = data.fileName;
        this.areaName = data.areaName;
        this.shortName = data.shortName;
        this.fullName = data.fullName;
    }

    /**
     * add an npc to this map, so the map can manage it actions and movement
     * 
     * 
     */
    public void addNpc(Object npc) {// FIXME ??? Npc npc) {
        this.npcs.add(npc);
        // FIXME ???
        // short[] picture = {2,2};
        // getMessageRouter().addScreenObject( new
        // NpcOnTheScreen(npc.x,npc.y,npc.getName()
        // ,picture,getMessageRouter()) );
    }


    /** Return the AstarDouble for this map. The class is AStarDoubleServer
     * 'cause there isnt a static AStartDouble for all the maps, but one
     * for every map, shared by all on the same map.
     * To manage memory, astar is init here : if it's null it's inited.
     */
    public AStarDoubleServer getAStar() {
        if (this.aStarDoubleServer == null) {
            this.aStarDoubleServer = new AStarDoubleServer();
            this.aStarDoubleServer.setMask(this.manager.getMapMask());
            this.aStarDoubleServer.setTileSize(32);
            this.aStarDoubleServer.setSpriteSize(1);
        }
        return this.aStarDoubleServer;
    }

    /* --------------SPELL AREA------------------------------------------- */

    transient private String timeStopOwner;
    transient private long timeStopDur;
    transient private boolean isTimeStopPossible = true;

    public boolean isBlockedByTimeStop(String primaryKey) {
        if (this.timeStopDur > System.currentTimeMillis())
            return !this.timeStopOwner.equals(primaryKey);
        return false;
    }

    public boolean setTimeStop(String primaryKey) {
        if (!this.isTimeStopPossible)
            return false;
        if (this.timeStopDur > System.currentTimeMillis())
            return false;
        this.timeStopOwner = primaryKey;
        this.timeStopDur = System.currentTimeMillis() + (1000 * 4);
        return true;
    }
}