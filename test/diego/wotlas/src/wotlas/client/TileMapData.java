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

package wotlas.client;

import wotlas.client.screen.JClientScreen;

import wotlas.common.message.description.*;
import wotlas.common.message.movement.*;
import wotlas.common.universe.*;
import wotlas.common.*;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;

import wotlas.libs.pathfinding.AStarDouble;

import wotlas.libs.sound.SoundLibrary;

import wotlas.utils.Debug;
import wotlas.utils.ScreenPoint;
import wotlas.utils.ScreenRectangle;

import wotlas.editor.*;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import java.util.Hashtable;

/**  TileMapData 
  *
  * @author ??? who started TownMapData, Diego
 */
public class TileMapData implements MapData {
    
 /*------------------------------------------------------------------------------------*/

  /** True if we show debug informations
   */
  public static boolean SHOW_DEBUG = false;

  /** if true, the player can change its MapData
   * otherwise, the server didn't send a message to do so => player stay where he is
   */
  public boolean canChangeMap;

  /** Our default dataManager
   */
  private DataManager dataManager;
  
  /** tells if the player is going to another map
   */
  private boolean isNotMovingToAnotherMap = true;

  /** previous location
   */
  private int currentTileMapID=-1;

 /*------------------------------------------------------------------------------------*/

  /** Set to true to show debug information
   */
  public void showDebug(boolean value) {
    SHOW_DEBUG = value;
  }

 /*------------------------------------------------------------------------------------*/

  /** To set isNotMovingToAnotherMap
   */
  public void setIsNotMovingToAnotherMap(boolean value) {
    isNotMovingToAnotherMap = value;
  }

 /*------------------------------------------------------------------------------------*/

  /** To init the display<br>
   * - load background and mask images<br>
   * - init the AStar algorithm
   * - init the Graphics Director
   * - show the other images (shadows, buildings, towns...)
   */
  public void initDisplay(PlayerImpl myPlayer, DataManager dataManager ) {
    this.dataManager = dataManager;
  
    if (DataManager.SHOW_DEBUG)
      System.out.println("-- initDisplay in TileMapData --");
    
    GraphicsDirector gDirector = dataManager.getGraphicsDirector();

    // 0 - Some inits...
    myPlayer.init();

    // 1 - We load the TileMap
    WotlasLocation location = myPlayer.getLocation();

    currentTileMapID = location.getTileMapID();

    TileMap tileMap = dataManager.getWorldManager().getTileMap(location);

      if (SHOW_DEBUG) {
         System.out.println("TileMap");
         System.out.println("\tfullName = "  + tileMap.getFullName());
         System.out.println("\tshortName = " + tileMap.getShortName());
      }

    dataManager.getClientScreen().getChatPanel().changeMainJChatRoom(tileMap.getShortName());

    dataManager.addPlayer(myPlayer);

    // 2 - We set player's position if his position is incorrect

    // 3 - preInit the GraphicsDirector : reset it...
    // ??? WHY DO THIS ??? gDirector.preTileMapInit( new Dimension( JClientScreen.leftWidth, JClientScreen.mapHeight ) );
    gDirector.preTileMapInit( tileMap.getMapFullSize() );

    // 4 - We load the background tile and create the background
    tileMap.initGroupOfGraphics( gDirector );
    tileMap.drawAllLayer( gDirector );

    // 5 - We load the mask

    // 6 - We initialize the AStar algo
    // IMPOSSIBLE : astar cant understand tilemap
/*
    myPlayer.getMovementComposer().setMovementMask( BinaryMask.create( bufIm ), 5, 1 );
    bufIm.flush(); // free image resource
*/
    myPlayer.getMovementComposer().resetMovement();

    gDirector.tileMapInit( tileMap.getMapFullSize() );

    // 7 - We add buildings' images

    // 8 - We add MapExits' images

    // 9 - We show some informations on the screen
    gDirector.addDrawable(myPlayer.getGameScreenFullPlayerName());

    String[] strTemp2 = { tileMap.getFullName() };
    MultiLineText mltLocationName = new MultiLineText(strTemp2, 10, 10, Color.black, 15.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, MultiLineText.RIGHT_ALIGNMENT);
    gDirector.addDrawable(mltLocationName);

    // 10 - We play music
    String midiFile = tileMap.getMusicName();
    if (midiFile != null)
      SoundLibrary.getMusicPlayer().playMusic( midiFile );

    // 11 - We retrieve eventual remaining data
    dataManager.sendMessage(new AllDataLeftPleaseMessage());
  }

  /** To init the display editor<br>
   * - load background and mask images<br>
   * - init the AStar algorithm
   * - init the Graphics Director
   * - show the other images (shadows, buildings, towns...)
   */
    public void initDisplayEditor( EditorDataManager editorDataManager, WotlasLocation location ) {
        TileMap tileMap = editorDataManager.getWorldManager().getTileMap( location );
        System.out.println( ""+location );
        GraphicsDirector gDirector = editorDataManager.getGraphicsDirector();
        EditorPlugIn.rememberTheGDirector( gDirector );
        gDirector.preTileMapInit( tileMap.getMapFullSize() );
        tileMap.initGroupOfGraphics( gDirector );
        tileMap.drawAllLayer( gDirector );
        gDirector.tileMapInit( tileMap.getMapFullSize() );
        String[] strTemp2 = { tileMap.getFullName() };
        MultiLineText mltLocationName = new MultiLineText(strTemp2, 10, 10, Color.black, 15.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, MultiLineText.RIGHT_ALIGNMENT);
        gDirector.addDrawable(mltLocationName);
    }

  /*------------------------------------------------------------------------------------*/

  /** To update the location<br>
   * - test if player is intersecting a screenZone<br>
   * - test if player is entering a new WotlasLocation<br>
   * - change the current MapData
   */
  public void locationUpdate(PlayerImpl myPlayer) {

    if(dataManager==null)
       return;

    // Has the currentLocation changed ?

    if ( (currentTileMapID != myPlayer.getLocation().getTileMapID())
          || (myPlayer.getLocation().getBuildingID()>-1) ) {
      if (DataManager.SHOW_DEBUG)
        System.out.println("LOCATION HAS CHANGED in TileMapData");
        
      Debug.signal( Debug.NOTICE, null, "LOCATION HAS CHANGED in TileMapData");

      dataManager.getPlayers().clear();
      dataManager.cleanInteriorMapData();
      dataManager.getClientScreen().getChatPanel().reset();
      dataManager.changeMapData();
      return;
    }

    TileMap tileMap = dataManager.getWorldManager().getTileMap( myPlayer.getLocation() );

    // I - MAPEXIT INTERSECTION UPDATE ( is the player moving to a world map ? )

    // II - BUILDING INTERSECTION UPDATE ( is the player entering a building ? )

  }
}