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
 
package wotlas.common.universe;

import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.utils.*;

import java.awt.Point;

 /** A Building of a town in our World. A building always belongs to a townMap.
  *
  * @author Petrus, Aldiss
  * @see wotlas.common.universe.TownMap
  * @see wotlas.common.universe.MapExit
  */
 
public class Building extends ScreenRectangle
{
 /*------------------------------------------------------------------------------------*/
   
  /** ID of the Building
   */
    private int buildingID;
     
  /** Full name of the Building
   */
    private String fullName;
   
  /** Short name of the Building
   */
    private String shortName;
  
  /** Server ID of the server that possesses this Building
   */
    private int serverID;

  /** is true if the Building has some TownExit
   */
    private boolean hasTownExits;
  
  /** is true if the Building has some BuildingExit
   */
    private boolean hasBuildingExits;

  /** Small Image (identifier) of this building for TownMaps.
   */
    private ImageIdentifier smallBuildingImage;

 /*------------------------------------------------------------------------------------*/

  /** A link to our father town...
   */
    private transient TownMap myTownMap;
   
  /** Our interior maps.
   */
    private transient InteriorMap[] interiorMaps;
   
  /** Map exits that are building exits...
   */
    private transient MapExit[] buildingExits;
   
  /** Map exits that are town exits.
   */
    private transient MapExit[] townExits;
   
 /*------------------------------------------------------------------------------------*/
  
  /** Constructor for persistence.
   */
    public Building() {
       hasBuildingExits = false; // default
       hasTownExits = false;     // default
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with x,y positions & width,height dimension on TownMaps.
   * @param x x position of this building on a townMap.
   * @param y y position of this building on a townMap.
   * @param width width dimension of this building on a townMap.
   * @param height height dimension of this building on a townMap.
   */
    public Building(int x, int y, int width, int height) {
       super(x,y,width,height);
       hasBuildingExits = false; // default
       hasTownExits = false;     // default
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /*
   * List of setter and getter used for persistence
   */

    public void setBuildingID(int myBuildingID) {
      this.buildingID = myBuildingID;
    }

    public int getBuildingID() {
      return buildingID;
    }

    public void setServerID(int myServerID) {
      this.serverID = myServerID;
    }

    public int getServerID() {
      return serverID;
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

    public void setHasTownExits(boolean myHasTownExits) {
      this.hasTownExits = myHasTownExits;
    }

    public boolean getHasTownExits() {
      return hasTownExits;
    }

    public void setHasBuildingExits(boolean myHasBuildingExits) {
      this.hasBuildingExits = myHasBuildingExits;
    }

    public boolean getHasBuildingExits() {
      return hasBuildingExits;
    }

    public void setSmallBuildingImage(ImageIdentifier smallBuildingImage) {
      this.smallBuildingImage = smallBuildingImage;
    }

    public ImageIdentifier getSmallBuildingImage() {
      return smallBuildingImage;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Transient fields getter & setter
   */

    public TownMap getMyTownMap() {
      return myTownMap;
    }

    public void setInteriorMaps(InteriorMap[] myInteriorMaps) {
      this.interiorMaps = myInteriorMaps;
    }

    public InteriorMap[] getInteriorMaps() {
      return interiorMaps;
    }

    public MapExit[] getBuidlingExits() {
      return buildingExits;
    }

    public MapExit[] getTownExits() {
      return townExits;
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To Get a interiorMap by its ID.
   *
   * @param id interiorMapID
   * @return corresponding interiorMap, null if ID does not exist.
   */
    public InteriorMap getInteriorMapFromID( int id ) {
   	if(id>=interiorMaps.length || id<0) {
           Debug.signal( Debug.ERROR, this, "getInteriorMapByID : Bad interiorMap ID "+id+". "+this );
   	   return null;
   	}

        return interiorMaps[id];
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a new InteriorMap object to the array interiorMaps
   *
   * @return a new InteriorMap object
   */
    public InteriorMap addNewInteriorMap()
    {
      InteriorMap myInteriorMap = new InteriorMap();
    
      if (interiorMaps == null) {
        interiorMaps = new InteriorMap[1];
        myInteriorMap.setInteriorMapID(0);
        interiorMaps[0] = myInteriorMap;
      } else {
        InteriorMap[] myInteriorMaps = new InteriorMap[interiorMaps.length+1];
        myInteriorMap.setInteriorMapID(interiorMaps.length);
        System.arraycopy(interiorMaps, 0, myInteriorMaps, 0, interiorMaps.length);
        myInteriorMaps[interiorMaps.length] = myInteriorMap;
        interiorMaps = myInteriorMaps;
      }

      return myInteriorMap;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Add a InteriorMap to our array interiorMaps {@link #buildings buildings})
   *
   * @param building Building object to add
   */
    public void addInteriorMap( InteriorMap map )
    {
       if ( interiorMaps == null ) {
            interiorMaps = new InteriorMap[map.getInteriorMapID()+1];
       }
       else if( interiorMaps.length <= map.getInteriorMapID() ) {
          InteriorMap[] myInteriorMap = new InteriorMap[map.getInteriorMapID()+1];
          System.arraycopy( interiorMaps, 0, myInteriorMap, 0, interiorMaps.length );
          interiorMaps = myInteriorMap;
       }

       interiorMaps[map.getInteriorMapID()] = map;        
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To init this building ( it rebuilds shortcuts ). DON'T CALL this method directly, use
   *  the init() method of the associated world.
   *
   * @param townMap our father townMap
   */
    public void init( TownMap myTownMap ){

       this.myTownMap = myTownMap;

    // 1 - any data ?
       if(interiorMaps==null) {
          Debug.signal(Debug.NOTICE, this, "Building has no interior maps: "+this );
          return;
       }

    // 2 - we transmit the init() call
       for( int i=0; i<interiorMaps.length; i++ )
            if( interiorMaps[i]!=null )
                interiorMaps[i].init( this );

    // 3 - we reconstruct the shortcuts (now that interiorMaps shortcuts have been rebuild)
       for( int i=0; i<interiorMaps.length; i++ )
            if( interiorMaps[i]!=null )
            {
               Room rooms[] = interiorMaps[i].getRooms();
               
               if(rooms==null)
                  continue;
               
               for( int j=0; j<rooms.length; j++ )
                    if( rooms[i]!=null )
                    {
                       MapExit exits[] = rooms[i].getMapExits();
                    
                       if(exits==null)
                          continue;
                    
                       for( int k=0; k<exits.length; k++ )
                           if( exits[k]!=null && exits[k].getType()==MapExit.BUILDING_EXIT )
                           {
                               if ( buildingExits == null ){
                                    buildingExits = new MapExit[1];
                                    hasBuildingExits = true;
                               }
                               else {
                                    MapExit tmp[] = new MapExit[buildingExits.length+1];
                                    System.arraycopy( buildingExits, 0, tmp, 0, buildingExits.length );
                                    buildingExits = tmp;
                               }

                               buildingExits[buildingExits.length-1] = exits[k];        
                           }
                           else if( exits[k]!=null && exits[k].getType()==MapExit.TOWN_EXIT )
                           {
                               if ( townExits == null ) {
                                    townExits = new MapExit[1];
                                    hasTownExits = true;
                               }
                               else {
                                    MapExit tmp[] = new MapExit[townExits.length+1];
                                    System.arraycopy( townExits, 0, tmp, 0, townExits.length );
                                    townExits = tmp;
                               }

                               townExits[townExits.length-1] = exits[k];        
                           }                    
                    }
            }
   }


 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the buildingExit (MapExit) that is on the side given by the specified point.
   * @param a point which is out of the MapExit ScreenZone and should represent
   *        the direction by which the player hits this TownMap zone.
   * @return the appropriate MapExit, null if there are no MapExits.
   */
   public MapExit findTownMapExit( Point fromPosition ) {
   	
      if(buildingExits==null)
         return null;

      if(buildingExits.length==1)
         return buildingExits[0];
   
      for(int i=0; i<buildingExits.length; i++ ) {
         if( buildingExits[i].getMapExitSide()==MapExit.WEST && fromPosition.x <= x )
             return buildingExits[i];

         if( buildingExits[i].getMapExitSide()==MapExit.EAST && fromPosition.x >= x+width )
             return buildingExits[i];

         if( buildingExits[i].getMapExitSide()==MapExit.NORTH && fromPosition.y <= y )
             return buildingExits[i];

         if( buildingExits[i].getMapExitSide()==MapExit.SOUTH && fromPosition.y >= y+height )
             return buildingExits[i];
      }
   
      return buildingExits[0]; // default
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** String Info.
   */
    public String toString(){
      if(interiorMaps==null)
         return "Building bId:"+buildingID+" Name:"+fullName+" maxIdTownMap: no array";
      else
         return "Building bId:"+buildingID+" Name:"+fullName+" maxIdTownMap:"+interiorMaps.length;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/


}