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

import wotlas.common.*;
import wotlas.common.router.*;
import wotlas.utils.*;
import wotlas.libs.persistence.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.schedule.*;
import wotlas.common.environment.*;
import wotlas.editor.*;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.*;

 /** A TileMap represents a TileMap in our Game Universe.
  *
  * @author Petrus, Aldiss, Diego
  * @see wotlas.common.universe.WorldMap
  * @see wotlas.common.universe.Building
  */
public class TileMap extends PreloaderEnabled implements WotlasMap,BackupReady,SendObjectReady {
    
    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    public static byte FAKEISO = 1;
    public static byte FLAT    = 2;
    public static Dimension PIXEL_32 = new Dimension( 32, 32 );
    public static Dimension PIXEL_50 = new Dimension( 50, 50 );
    public static Dimension PIXEL_40X80 = new Dimension( 40, 80 );
    public static Dimension PIXEL_80X40 = new Dimension( 80, 40 );
    public static Dimension PIXEL_70X35 = new Dimension( 70, 35 );

    public static byte TILE   = 1;
    public static byte WALLX  = 2;
    public static byte WALLY  = 3;
    
    public static boolean TILE_FREE      = true;
    public static boolean TILE_NOT_FREE  = false;

 /*------------------------------------------------------------------------------------*/
    
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

 /*------------------------------------------------------------------------------------*/

  /** Constructor for persistence.
   */
    public TileMap() {
        loadStatus = PreloaderEnabled.LOAD_ALL;
    }
   
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with x,y positions & width,height dimension on WorldMap.
   * @param x x position of this building on a WorldMap.
   * @param y y position of this building on a WorldMap.
   * @param width width dimension of this building on a WorldMap.
   * @param height height dimension of this building on a WorldMap.
   */
    public TileMap(int x, int y, int width, int height) {
        super(x,y,width,height);
        loadStatus = PreloaderEnabled.LOAD_ALL;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*
   * List of setter and getter used for persistence
   */
    public void setTileMapID(int myTileMapID) {
        this.tileMapID = myTileMapID;
    }

    public int getTileMapID() {
        return tileMapID;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setFullName(String myFullName) {
        this.fullName = myFullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setShortName(String myShortName) {
        this.shortName = myShortName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setSmallTileMapImage(ImageIdentifier smallTileMapImage) {
        this.smallTileMapImage = smallTileMapImage;
    }

    public ImageIdentifier getSmallTileMapImage() {
        return smallTileMapImage;
    }
        
    public Dimension getMapSize() {
        return mapSize;
    }
        
    public void setMapSize( Dimension mapSize) {
        this.mapSize = mapSize;
    }

    // this should be in a free tile
    public void setInsertionPoint(ScreenPoint myInsertionPoint) {
        this.insertionPoint = myInsertionPoint;
    }

    // this should be in a free tile
    public ScreenPoint getInsertionPoint() {
        return new ScreenPoint( insertionPoint );
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicName() {
        return musicName;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Transient fields getter & setter
   */

    public WorldMap getMyWorldMap() {
        return myWorldMap;
    }

    public MessageRouter getMessageRouter() {
        return messageRouter;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the wotlas location associated to this Map.
   *  @return associated Wotlas Location
   */
    public WotlasLocation getLocation() {
        WotlasLocation thisLocation = new WotlasLocation( myWorldMap.getWorldMapID() );
        thisLocation.WotlasLocationChangeToTileMap(tileMapID);
        return thisLocation;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init this tileMap ( it rebuilds shortcuts ). DON'T CALL this method directly, use
   *  the init() method of the associated world.
   *
   * @param myWorldMap our parent WorldMap.
   */
    public void init( WorldMap myWorldMap ) {

       this.myWorldMap = myWorldMap;

       // 1 - any data ?

       // 2 - we transmit the init() call

       // 3 - MapExit inits
       if( manager.getMapExits()==null ) return;
       
       WotlasLocation thisLocation = new WotlasLocation( myWorldMap.getWorldMapID() );
       thisLocation.WotlasLocationChangeToTileMap(tileMapID);
       for( int i=0; i<manager.getMapExits().length; i++ )
            manager.getMapExits()[i].setMapExitLocation(thisLocation);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init this tilemap for message routing. We create an appropriate message router
   *  for the tilemap via the provided factory.
   *
   *  Don't call this method yourself it's called via the WorldManager !
   *
   * @param msgRouterFactory our router factory
   */
    public void initMessageRouting( MessageRouterFactory msgRouterFactory, WorldManager wManager ){
       // build/get our router
          messageRouter = msgRouterFactory.createMsgRouterForTileMap( this, wManager );

       // we transmit the call to other layers
       // diego:actually no others layers
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** String Info.
   */
    public String toString(){
         return "TileMap - "+fullName;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** id version of data, used in serialized persistance.
   */
    public int ExternalizeGetVersion(){
        return 1;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        if( loadStatus != LOAD_ALL){
            System.out.println( "Map ["+this.fullName+"] damaged, cause status of data before saving : "+ this.loadStatus );
            return;
//            throw new PreloaderException(loadStatus);
        }
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal( objectOutput );
        objectOutput.writeInt( tileMapID );
        objectOutput.writeObject( areaName );
        objectOutput.writeObject( fullName );
        objectOutput.writeObject( shortName );
        objectOutput.writeObject( insertionPoint );
        objectOutput.writeByte( manager.getMapType() );
        objectOutput.writeObject( smallTileMapImage );
        objectOutput.writeObject( musicName );
        objectOutput.writeObject( mapTileDim );
        objectOutput.writeByte( graphicSet );
        objectOutput.writeObject( mapSize );
        objectOutput.writeObject( manager );
        objectOutput.writeObject( encounterSchedules ); 
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        loadStatus = setClassPreloader;
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal(objectInput);
            tileMapID= objectInput.readInt();
            areaName = ( String ) objectInput.readObject();
            fullName = ( String ) objectInput.readObject();
            shortName = ( String ) objectInput.readObject();
            insertionPoint = ( ScreenPoint ) objectInput.readObject();
            mapType = objectInput.readByte();
            smallTileMapImage = ( ImageIdentifier ) objectInput.readObject();
            musicName = ( String ) objectInput.readObject();
            if( setClassPreloader == LOAD_MINIMUM_DATA)
                return;
            mapTileDim = ( Dimension ) objectInput.readObject();
            graphicSet = objectInput.readByte();
            mapSize = ( Dimension ) objectInput.readObject();
            manager = ( TileMapManager ) objectInput.readObject();
            manager.setTileMap(this);
            if( setClassPreloader == LOAD_CLIENT_DATA)
                return;
            if( setClassPreloader == LOAD_SERVER_DATA){
                manager.freeMapBackGroundData();
            }
            encounterSchedules = ( EncounterSchedule[] ) objectInput.readObject();
       } else {
            // to do.... when new version
        }
    }

    public byte getMapType(){
        return mapType;
    }

    public void setManager( TileMapManager manager ){
        this.manager = manager;
        this.manager.setTileMap(this);
        mapType = manager.getMapType();
    }

    public TileMapManager getManager(){
        return manager;
    }

    public Dimension getMapTileDim(){
        return mapTileDim;
    }

    public Dimension getMapFullSize() {
        Dimension mapFullSize = new Dimension();
        mapFullSize.width  = mapSize.width * mapTileDim.width;
        mapFullSize.height = mapSize.height * mapTileDim.height;
        return mapFullSize;
    }
    
    public void selectGraphicSet( byte graphicSet ) {
        this.graphicSet = graphicSet;
    }

    public void initGraphicSet( GraphicsDirector gDirector ) {
        EnvironmentManager.initGraphics( gDirector, graphicSet );
        return ;
    }

    public byte getGraphicSet() {
        return graphicSet;
    }

    public void drawAllLayer( GraphicsDirector gDirector ) {
        manager.drawAllLayer( gDirector );
    }
    
    public void initNewTileMap( WorldMap myWorldMap ) {
        this.myWorldMap = myWorldMap;
    }
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the eventual MapExit the given player is intersecting.
   *
   * @param rCurrent rectangle containing the player's current position, width & height
   * @return the Building the player is heading to (if he has reached it, or if there
   *         are any), null if none.
   */
    public MapExit isIntersectingMapExit( int destX, int destY, Rectangle rCurrent ) {
        return manager.isIntersectingMapExit( destX, destY, rCurrent );
    }

    public MapExit[] getMapExits(){
        return manager.getMapExits();
    }
    
    public EncounterSchedule[] getEncounterSchedule(){
        return encounterSchedules;
    }   

    public EncounterSchedule addEncounterSchedule() {
        EncounterSchedule my = new EncounterSchedule();

        if (encounterSchedules == null) {
            encounterSchedules = new EncounterSchedule[1];
            my.setId(0);
            encounterSchedules[0] = my;
        } else {
            EncounterSchedule[] myEncounterSchedules = new EncounterSchedule[encounterSchedules.length+1];
            my.setId(encounterSchedules.length);
            System.arraycopy(encounterSchedules, 0, myEncounterSchedules, 0, encounterSchedules.length);
            myEncounterSchedules[encounterSchedules.length] = my;
            encounterSchedules = myEncounterSchedules;
        }
        return my;
    }
    
    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            loadStatus = objectInput.readByte();
            super.readObject(objectInput);
            tileMapID= objectInput.readInt();
            areaName = ( String ) objectInput.readObject();
            fullName = ( String ) objectInput.readObject();
            shortName = ( String ) objectInput.readObject();
            insertionPoint = ( ScreenPoint ) objectInput.readObject();
            mapType = objectInput.readByte();
            smallTileMapImage = ( ImageIdentifier ) objectInput.readObject();
            musicName = ( String ) objectInput.readObject();
            if( loadStatus == LOAD_MINIMUM_DATA)
                return;
            mapTileDim = ( Dimension ) objectInput.readObject();
            graphicSet = objectInput.readByte();
            // groupOfGraphics = ( GroupOfGraphics[] ) objectInput.readObject();
            mapSize = ( Dimension ) objectInput.readObject();
            manager = ( TileMapManager ) objectInput.readObject();
            manager.setTileMap(this);
            if( loadStatus == LOAD_CLIENT_DATA)
                return;
            if( loadStatus == LOAD_SERVER_DATA){
                manager.freeMapBackGroundData();
            }
            encounterSchedules = ( EncounterSchedule[] ) objectInput.readObject();
        } else {
            // ERORR IN THE STREAM : DIFFERENT VERSION OF CLIENT/SERVER
            // to do.... when new version
        }
    }
    
    public void writeObject(java.io.ObjectOutputStream objectOutput) 
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeByte( loadStatus );
        super.writeExternal( objectOutput );
        objectOutput.writeInt( tileMapID );
        objectOutput.writeObject( areaName );
        objectOutput.writeObject( fullName );
        objectOutput.writeObject( shortName );
        objectOutput.writeObject( insertionPoint );
        objectOutput.writeByte( manager.getMapType() );
        objectOutput.writeObject( smallTileMapImage );
        objectOutput.writeObject( musicName );
        if( loadStatus == LOAD_MINIMUM_DATA)
            return;
        objectOutput.writeObject( mapTileDim );
        objectOutput.writeByte( graphicSet );
//        objectOutput.writeObject( groupOfGraphics );
        objectOutput.writeObject( mapSize );
        objectOutput.writeObject( manager );
        if( loadStatus == LOAD_CLIENT_DATA)
            return;
        objectOutput.writeObject( encounterSchedules ); 
    }
    
    public StoreTileMapBackground getStoreBackground(){
        return (new StoreTileMapBackground( manager.getMapBackGroundData()
        , manager.getMapMask()
        , manager.getMapExits()
        , mapSize
        , fileName
        , areaName
        , fullName        
        , shortName
        ));
    }

    public void setStoreBackground( StoreTileMapBackground data ){
        manager.replaceMask(data.mask);
        manager.replaceGraphics(data.graphic);
        manager.replaceExits(data.exits);
        this.mapSize = data.mapSize;
        this.fileName = data.fileName;
        this.areaName = data.areaName;
        this.shortName = data.shortName;
        this.fullName = data.fullName;
    }
}