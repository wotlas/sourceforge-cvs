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

package wotlas.server.setup;

import wotlas.common.ResourceManager;
import wotlas.common.WorldManager;
import wotlas.common.universe.Building;
import wotlas.common.universe.Door;
import wotlas.common.universe.InteriorMap;
import wotlas.common.universe.MapExit;
import wotlas.common.universe.Room;
import wotlas.common.universe.RoomLink;
import wotlas.common.universe.TownMap;
import wotlas.common.universe.WorldMap;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.drawable.DoorDrawable;
import wotlas.utils.Debug;
import wotlas.utils.ScreenPoint;
import wotlas.utils.ScreenRectangle;
import wotlas.utils.WotlasGameDefinition;

/** A small utility to generate <<Wotlas release 1>>'s default world.
 *
 * @author Aldiss
 */

public class WorldGenerator {

    /*------------------------------------------------------------------------------------*/
    /**
     *  WORLD CREATION : RANDLAND
     * @return Randland World.
     */
    public static final WorldMap createRandlandWorld() {

        float halfPI = (float) (Math.PI / 2);

        WorldMap worldMap = new WorldMap();

        worldMap.setWorldMapID(0);
        worldMap.setFullName("RandLand");
        worldMap.setShortName("randland");
        worldMap.setInsertionPoint(new ScreenPoint(680, 455));
        worldMap.setWorldImage(new ImageIdentifier("maps-1/universe-2/randland-0"));
        worldMap.setMusicName("stedding.mid");

        // STEP 3 - TOWNS
        TownMap townMaps[] = new TownMap[5];
        worldMap.setTownMaps(townMaps);

        // Tar Valon Creation
        TownMap townMap = new TownMap(755, 277, 17, 17);
        townMaps[0] = townMap;

        townMap.setTownMapID(0);
        townMap.setFullName("Tar Valon");
        townMap.setShortName("tarvalon");
        townMap.setInsertionPoint(new ScreenPoint(70, 340));

        townMap.setSmallTownImage(new ImageIdentifier("maps-1/town-small-1/tar-valon-small-0"));

        townMap.setTownImage(new ImageIdentifier("maps-1/universe-2/tar-valon-1"));
        townMap.setMusicName("tar-valon.mid");

        MapExit mapExit = null;

        mapExit = townMap.addMapExit(new ScreenRectangle(30, 300, 20, 40));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.WEST);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(745, 280));

        mapExit = townMap.addMapExit(new ScreenRectangle(30, 710, 20, 30));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(763, 300));

        mapExit = townMap.addMapExit(new ScreenRectangle(30, 740, 20, 50));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.SOUTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(763, 300));

        mapExit = townMap.addMapExit(new ScreenRectangle(550, 700, 20, 50));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.EAST);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(774, 284));

        mapExit = townMap.addMapExit(new ScreenRectangle(550, 370, 20, 50));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(769, 273));

        mapExit = townMap.addMapExit(new ScreenRectangle(550, 170, 20, 50));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.NORTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(758, 264));

        // border mapExits
        mapExit = townMap.addMapExit(new ScreenRectangle(30, 130, 20, 170));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(745, 280));

        mapExit = townMap.addMapExit(new ScreenRectangle(30, 340, 20, 370));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(763, 300));

        mapExit = townMap.addMapExit(new ScreenRectangle(30, 790, 20, 120));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(763, 300));

        mapExit = townMap.addMapExit(new ScreenRectangle(550, 420, 20, 280));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(774, 284));

        mapExit = townMap.addMapExit(new ScreenRectangle(550, 220, 20, 150));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(769, 273));

        mapExit = townMap.addMapExit(new ScreenRectangle(0, 0, 600, 130));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(758, 264));

        mapExit = townMap.addMapExit(new ScreenRectangle(0, 920, 600, 80));
        mapExit.setType(MapExit.TOWN_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(763, 300));

        // Blight Refuge 'Town'
        townMap = new TownMap(774, 115, 16, 15);
        townMaps[1] = townMap;

        townMap.setTownMapID(1);
        townMap.setFullName("Blight Refuge");
        townMap.setShortName("blightrefuge");
        townMap.setInsertionPoint(new ScreenPoint(0, 0));

        townMap.setSmallTownImage(new ImageIdentifier("maps-1/town-small-1/blight-refuge-small-1"));

        townMap.setTownImage(new ImageIdentifier()); // no town image
        townMap.setMusicName("blight-refuge.mid");

        // Shayol Ghul 'Town'
        townMap = new TownMap(800, 70, 12, 11);
        townMaps[2] = townMap;

        townMap.setTownMapID(2);
        townMap.setFullName("Shayol Ghul");
        townMap.setShortName("shayolghul");
        townMap.setInsertionPoint(new ScreenPoint(0, 0));

        townMap.setSmallTownImage(new ImageIdentifier("maps-1/town-small-1/shayol-ghul-small-2"));

        townMap.setTownImage(new ImageIdentifier()); // no town image
        townMap.setMusicName("blight-refuge.mid");

        // Braem Wood 'Town'
        townMap = new TownMap(706, 377, 15, 15);
        townMaps[3] = townMap;

        townMap.setTownMapID(3);
        townMap.setFullName("Braem Wood");
        townMap.setShortName("braemwood");
        townMap.setInsertionPoint(new ScreenPoint(0, 0));

        townMap.setSmallTownImage(new ImageIdentifier("maps-1/town-small-1/braem-small-3"));

        townMap.setTownImage(new ImageIdentifier()); // no town image
        townMap.setMusicName("blight-refuge.mid");

        // Two Rivers 'Town'
        townMap = new TownMap(428, 410, 15, 15);
        townMaps[4] = townMap;

        townMap.setTownMapID(4);
        townMap.setFullName("Two Rivers");
        townMap.setShortName("tworivers");
        townMap.setInsertionPoint(new ScreenPoint(0, 0));

        townMap.setSmallTownImage(new ImageIdentifier("maps-1/town-small-1/two-small-4"));

        townMap.setTownImage(new ImageIdentifier()); // no town image
        townMap.setMusicName("blight-refuge.mid");

        // STEP 4 - Tar Valon West Gate Building
        Building buildings[] = new Building[5];
        townMaps[0].setBuildings(buildings);

        buildings[0] = new Building(208, 493, 10, 18);
        buildings[0].setBuildingID(0);
        buildings[0].setFullName("Tar Valon - West Gate");
        buildings[0].setShortName("WestGate");
        buildings[0].setServerID(0);
        buildings[0].setHasTownExits(false);
        buildings[0].setHasBuildingExits(true);
        buildings[0].setSmallBuildingImage(new ImageIdentifier("maps-1/building-small-0/tar-valon-wegate-small-1"));

        // Tar Valon - North-West Clearing (Building)
        buildings[1] = new Building(55, 232, 18, 18);
        buildings[1].setBuildingID(1);
        buildings[1].setFullName("Tar Valon - Forest - North West Clearing");
        buildings[1].setShortName("NorthClearing");
        buildings[1].setServerID(0);
        buildings[1].setHasTownExits(false);
        buildings[1].setHasBuildingExits(true);
        buildings[1].setSmallBuildingImage(new ImageIdentifier("maps-1/building-small-0/tar-valon-nwclearing-small-2"));

        // Tar Valon - North-West Gate (Building)
        buildings[2] = new Building(210, 400, 13, 21);
        buildings[2].setBuildingID(2);
        buildings[2].setFullName("Tar Valon - North West Gate");
        buildings[2].setShortName("NorthWestGate");
        buildings[2].setServerID(0);
        buildings[2].setHasTownExits(false);
        buildings[2].setHasBuildingExits(true);
        buildings[2].setSmallBuildingImage(new ImageIdentifier("maps-1/building-small-0/tar-valon-nwgate-small-3"));

        // Tar Valon - White Tower South Gate (Building)
        buildings[3] = new Building(276, 503, 14, 15);
        buildings[3].setBuildingID(3);
        buildings[3].setFullName("Tar Valon - White Tower South Gate");
        buildings[3].setShortName("WhiteTowerGate");
        buildings[3].setServerID(0);
        buildings[3].setHasTownExits(false);
        buildings[3].setHasBuildingExits(true);
        buildings[3].setSmallBuildingImage(new ImageIdentifier("maps-1/building-small-0/tar-valon-swhitower-small-0"));

        // Tar Valon - White Tower (Building)
        buildings[4] = new Building(-10, -10, 1, 1);
        buildings[4].setBuildingID(4);
        buildings[4].setFullName("Tar Valon - White Tower");
        buildings[4].setShortName("WhiteTower");
        buildings[4].setServerID(0);
        buildings[4].setHasTownExits(false);
        buildings[4].setHasBuildingExits(true);
        buildings[4].setSmallBuildingImage(new ImageIdentifier());

        // STEP 5 - Tar Valon West Gate InteriorMap
        InteriorMap maps[] = new InteriorMap[1];
        InteriorMap map = new InteriorMap();
        maps[0] = map;

        buildings[0].setInteriorMaps(maps);

        map.setInteriorMapID(0);
        map.setFullName("Tar Valon - West Gate");
        map.setShortName("westgate");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/tar-valon-wegate-3"));
        map.setImageWidth(1050);
        map.setImageHeight(1200);
        map.setImageRegionWidth(350);
        map.setImageRegionHeight(400);

        map.setMusicName("tv-bridge.mid");

        // STEP 6 - Rooms of TarValon WestGate InteriorMap
        Room rooms[] = new Room[18];
        map.setRooms(rooms);

        RoomLink roomLink = null;

        for (int i = 0; i < 18; i++) {
            rooms[i] = new Room();
            rooms[i].setRoomID(i);
            rooms[i].setMaxPlayers(30);
        }

        rooms[0].setFullName("West Bridge Middle");
        rooms[0].setShortName("bridge1");
        rooms[0].setInsertionPoint(new ScreenPoint(70, 640));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(170, 450, 30, 400));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(1);

        mapExit = rooms[0].addMapExit(new ScreenRectangle(0, 450, 30, 400));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.WEST);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(200, 500));

        rooms[1].setFullName("West Bridge");
        rooms[1].setShortName("bridge2");
        rooms[1].setInsertionPoint(new ScreenPoint(320, 640));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(450, 450, 30, 400));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(2);

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        rooms[2].setFullName("West Gate Entry");
        rooms[2].setShortName("entry");
        rooms[2].setInsertionPoint(new ScreenPoint(640, 640));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(780, 520, 30, 260));
        roomLink.setRoom1ID(2);
        roomLink.setRoom2ID(3);

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(695, 770, 60, 25));
        roomLink.setRoom1ID(2);
        roomLink.setRoom2ID(14);
        roomLink.setDoor(new Door(705, 777, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-40len-8th-2/hor-right-pivot-3.gif")));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(700, 505, 50, 25));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(705, 514, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-40len-8th-2/hor-right-pivot-3.gif")));

        rooms[2].addRoomLink(rooms[1].getRoomLinks()[0]);

        rooms[3].setFullName("Tar Valon West Entry");
        rooms[3].setShortName("tarval-entry");
        rooms[3].setInsertionPoint(new ScreenPoint(960, 640));

        roomLink = rooms[3].addRoomLink(new ScreenRectangle(800, 460, 250, 25));
        roomLink.setRoom1ID(4);
        roomLink.setRoom2ID(3);

        roomLink = rooms[3].addRoomLink(new ScreenRectangle(800, 815, 250, 25));
        roomLink.setRoom1ID(3);
        roomLink.setRoom2ID(10);

        rooms[3].addRoomLink(rooms[2].getRoomLinks()[0]);

        mapExit = rooms[3].addMapExit(new ScreenRectangle(1020, 485, 30, 330));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.EAST);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(230, 500));

        rooms[4].setFullName("North-West Street");
        rooms[4].setShortName("nw-street");
        rooms[4].setInsertionPoint(new ScreenPoint(930, 40));

        roomLink = rooms[4].addRoomLink(new ScreenRectangle(780, 150, 20, 40));
        roomLink.setRoom1ID(8);
        roomLink.setRoom2ID(4);
        roomLink.setDoor(new Door(786, 154, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/vert-top-pivot-0.gif")));

        rooms[4].addRoomLink(rooms[3].getRoomLinks()[0]);

        mapExit = rooms[4].addMapExit(new ScreenRectangle(1020, 0, 30, 460));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NORTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(220, 485));

        mapExit = rooms[4].addMapExit(new ScreenRectangle(790, 0, 230, 25));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(220, 485));

        rooms[5].setFullName("West Gate - North Tower");
        rooms[5].setShortName("build-north-entry");
        rooms[5].setInsertionPoint(new ScreenPoint(620, 450));

        roomLink = rooms[5].addRoomLink(new ScreenRectangle(450, 385, 125, 20));
        roomLink.setRoom1ID(6);
        roomLink.setRoom2ID(5);

        rooms[5].addRoomLink(rooms[2].getRoomLinks()[2]);

        rooms[6].setFullName("West Gate - North Tower");
        rooms[6].setShortName("build-north-middle");
        rooms[6].setInsertionPoint(new ScreenPoint(480, 310));

        roomLink = rooms[6].addRoomLink(new ScreenRectangle(540, 270, 20, 65));
        roomLink.setRoom1ID(6);
        roomLink.setRoom2ID(7);

        roomLink = rooms[6].addRoomLink(new ScreenRectangle(460, 200, 120, 20));
        roomLink.setRoom1ID(8);
        roomLink.setRoom2ID(6);

        rooms[6].addRoomLink(rooms[5].getRoomLinks()[0]);

        rooms[7].setFullName("West Gate - North Tower - Meeting Room");
        rooms[7].setShortName("build-north-central");
        rooms[7].setInsertionPoint(new ScreenPoint(670, 290));

        rooms[7].addRoomLink(rooms[6].getRoomLinks()[0]);

        rooms[8].setFullName("West Gate - North Tower");
        rooms[8].setShortName("build-north-corridor");
        rooms[8].setInsertionPoint(new ScreenPoint(660, 170));

        roomLink = rooms[8].addRoomLink(new ScreenRectangle(680, 120, 40, 20));
        roomLink.setRoom1ID(9);
        roomLink.setRoom2ID(8);
        roomLink.setDoor(new Door(685, 127, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-right-pivot-3.gif")));

        rooms[8].addRoomLink(rooms[6].getRoomLinks()[1]);
        rooms[8].addRoomLink(rooms[4].getRoomLinks()[0]);

        rooms[9].setFullName("West Gate - North Tower's Store");
        rooms[9].setShortName("build-north-store");
        rooms[9].setInsertionPoint(new ScreenPoint(630, 80));

        rooms[9].addRoomLink(rooms[8].getRoomLinks()[0]);

        rooms[10].setFullName("South-West Street");
        rooms[10].setShortName("sw-street");
        rooms[10].setInsertionPoint(new ScreenPoint(960, 1150));

        roomLink = rooms[10].addRoomLink(new ScreenRectangle(780, 1110, 25, 40));
        roomLink.setRoom1ID(14);
        roomLink.setRoom2ID(10);
        roomLink.setDoor(new Door(786, 1114, halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/vert-bottom-pivot-1.gif")));

        rooms[10].addRoomLink(rooms[3].getRoomLinks()[1]);

        mapExit = rooms[10].addMapExit(new ScreenRectangle(1020, 840, 30, 360));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.SOUTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(223, 510));

        mapExit = rooms[10].addMapExit(new ScreenRectangle(790, 1170, 230, 30));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(223, 510));

        rooms[11].setFullName("West Gate - South Tower - Room");
        rooms[11].setShortName("bsouth-room2");
        rooms[11].setInsertionPoint(new ScreenPoint(580, 1140));

        roomLink = rooms[11].addRoomLink(new ScreenRectangle(570, 1110, 40, 20));
        roomLink.setRoom1ID(16);
        roomLink.setRoom2ID(11);
        roomLink.setDoor(new Door(575, 1118, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-right-pivot-3.gif")));

        rooms[12].setFullName("West Gate - South Tower - Room");
        rooms[12].setShortName("bsouth-room1");
        rooms[12].setInsertionPoint(new ScreenPoint(600, 1020));

        roomLink = rooms[12].addRoomLink(new ScreenRectangle(670, 990, 20, 40));
        roomLink.setRoom1ID(12);
        roomLink.setRoom2ID(14);
        roomLink.setDoor(new Door(676, 995, halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-bottom-pivot-1.gif")));

        rooms[13].setFullName("West Gate - South Tower's Store");
        rooms[13].setShortName("bsouth-store");
        rooms[13].setInsertionPoint(new ScreenPoint(600, 850));

        roomLink = rooms[13].addRoomLink(new ScreenRectangle(670, 850, 20, 40));
        roomLink.setRoom1ID(13);
        roomLink.setRoom2ID(14);
        roomLink.setDoor(new Door(677, 854, -halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-bottom-pivot-1.gif")));

        rooms[14].setFullName("West Gate - South Tower Hall");
        rooms[14].setShortName("bsouth-entry");
        rooms[14].setInsertionPoint(new ScreenPoint(730, 980));

        roomLink = rooms[14].addRoomLink(new ScreenRectangle(670, 920, 20, 40));
        roomLink.setRoom1ID(15);
        roomLink.setRoom2ID(14);

        roomLink = rooms[14].addRoomLink(new ScreenRectangle(670, 1070, 20, 40));
        roomLink.setRoom1ID(16);
        roomLink.setRoom2ID(14);

        rooms[14].addRoomLink(rooms[2].getRoomLinks()[1]);
        rooms[14].addRoomLink(rooms[10].getRoomLinks()[0]);
        rooms[14].addRoomLink(rooms[12].getRoomLinks()[0]);
        rooms[14].addRoomLink(rooms[13].getRoomLinks()[0]);

        rooms[15].setFullName("West Gate - South Tower");
        rooms[15].setShortName("bsouth-corridor1");
        rooms[15].setInsertionPoint(new ScreenPoint(600, 940));

        roomLink = rooms[15].addRoomLink(new ScreenRectangle(520, 910, 20, 40));
        roomLink.setRoom1ID(17);
        roomLink.setRoom2ID(15);
        roomLink.setDoor(new Door(528, 915, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        rooms[15].addRoomLink(rooms[14].getRoomLinks()[0]);

        rooms[16].setFullName("West Gate - South Tower");
        rooms[16].setShortName("bsouth-corridor2");
        rooms[16].setInsertionPoint(new ScreenPoint(600, 1080));

        roomLink = rooms[16].addRoomLink(new ScreenRectangle(520, 1060, 20, 40));
        roomLink.setRoom1ID(17);
        roomLink.setRoom2ID(16);
        roomLink.setDoor(new Door(528, 1065, -halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-bottom-pivot-1.gif")));

        rooms[16].addRoomLink(rooms[11].getRoomLinks()[0]);
        rooms[16].addRoomLink(rooms[14].getRoomLinks()[1]);

        rooms[17].setFullName("West Gate - South Tower's Office");
        rooms[17].setShortName("bsouth-office");
        rooms[17].setInsertionPoint(new ScreenPoint(480, 980));

        rooms[17].addRoomLink(rooms[15].getRoomLinks()[0]);
        rooms[17].addRoomLink(rooms[16].getRoomLinks()[0]);

        // STEP 7 - Tar Valon NW Clearing InteriorMap
        maps = new InteriorMap[1];
        maps[0] = new InteriorMap();

        maps[0].setInteriorMapID(0);
        maps[0].setFullName("Tar Valon - North West Clearing");
        maps[0].setShortName("nw-clearing");
        maps[0].setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/tv-nw-clearing-4"));
        maps[0].setImageWidth(550);
        maps[0].setImageHeight(350);
        maps[0].setImageRegionWidth(550);
        maps[0].setImageRegionHeight(350);

        maps[0].setMusicName("tv-clearing.mid");

        buildings[1].setInteriorMaps(maps);

        // STEP 8 - Rooms of Tar Valon NW Clearing InteriorMap
        rooms = new Room[1];
        maps[0].setRooms(rooms);
        rooms[0] = new Room();
        rooms[0].setRoomID(0);
        rooms[0].setMaxPlayers(30);

        rooms[0].setFullName("Tar Valon - North West Clearing");
        rooms[0].setShortName("nw-clearing");
        rooms[0].setInsertionPoint(new ScreenPoint(270, 200));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(0, 260, 20, 90));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.WEST);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(60, 250));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(20, 325, 400, 25));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.SOUTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(60, 250));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(430, 0, 120, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NORTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(65, 225));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(525, 20, 25, 240));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.EAST);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(65, 225));

        // STEP 9 - Tar Valon NW Gate InteriorMap
        maps = new InteriorMap[2];
        maps[0] = new InteriorMap();

        maps[0].setInteriorMapID(0);
        maps[0].setFullName("Tar Valon - North West Gate");
        maps[0].setShortName("nw-gate-lv0");
        maps[0].setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/tv-nw-gate-lv0-5"));
        maps[0].setImageWidth(640);
        maps[0].setImageHeight(460);
        maps[0].setImageRegionWidth(640);
        maps[0].setImageRegionHeight(460);

        maps[0].setMusicName("tv-bridge.mid");

        maps[1] = new InteriorMap();
        maps[1].setInteriorMapID(1);
        maps[1].setFullName("Tar Valon - North West Terrace");
        maps[1].setShortName("nw-gate-lv1");
        maps[1].setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/tv-nw-gate-lv1-6"));
        maps[1].setImageWidth(600);
        maps[1].setImageHeight(460);
        maps[1].setImageRegionWidth(600);
        maps[1].setImageRegionHeight(460);

        maps[1].setMusicName("tv-bridge.mid");

        buildings[2].setInteriorMaps(maps);

        // STEP 10 - Rooms of Tar Valon NW Gate InteriorMap Level 0
        rooms = new Room[1];
        maps[0].setRooms(rooms);
        rooms[0] = new Room();
        rooms[0].setRoomID(0);
        rooms[0].setMaxPlayers(30);

        rooms[0].setFullName("Tar Valon - North West Gate");
        rooms[0].setShortName("nw-gate-bridge");
        rooms[0].setInsertionPoint(new ScreenPoint(260, 240));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(0, 25, 25, 245));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.WEST);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(207, 400));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(330, 435, 310, 25));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.SOUTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(224, 420));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(0, 0, 230, 25));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NORTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(207, 400));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(615, 335, 25, 100));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.EAST);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(224, 420));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(485, 200, 40, 25));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 2, 1, 0));
        mapExit.setTargetPosition(new ScreenPoint(400, 40));
        mapExit.setTargetOrientation((float) (Math.PI / 4));

        // STEP 11 - Rooms of Tar Valon NW Terrace InteriorMap Level 1
        rooms = new Room[1];
        maps[1].setRooms(rooms);
        rooms[0] = new Room();
        rooms[0].setRoomID(0);
        rooms[0].setMaxPlayers(30);

        rooms[0].setFullName("Tar Valon - North West Terrace");
        rooms[0].setShortName("nw-terrace");
        rooms[0].setInsertionPoint(new ScreenPoint(400, 300));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(400, 25, 25, 40));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 2, 0, 0));
        mapExit.setTargetPosition(new ScreenPoint(500, 205));
        mapExit.setTargetOrientation((float) (Math.PI / 4));

        // STEP 11 bis - Tar Valon White Tower South Gate InteriorMap
        maps = new InteriorMap[2];
        map = new InteriorMap();
        maps[0] = map;

        buildings[3].setInteriorMaps(maps);

        map.setInteriorMapID(0);
        map.setFullName("Tar Valon - White Tower - South Gate");
        map.setShortName("whitetower-south-gate");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/tv-s-whitetower-gate-2"));
        map.setImageWidth(600);
        map.setImageHeight(1000);
        map.setImageRegionWidth(600);
        map.setImageRegionHeight(200);

        map.setMusicName("tv-white-tower.mid");

        map = new InteriorMap();
        maps[1] = map;

        map.setInteriorMapID(1);
        map.setFullName("Tar Valon - White Tower - Front");
        map.setShortName("whitetower-front");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/whitetower-entrance-11"));
        map.setImageWidth(760);
        map.setImageHeight(800);
        map.setImageRegionWidth(380);
        map.setImageRegionHeight(200);

        map.setMusicName("tv-white-tower.mid");

        // STEP 11bis2 - Rooms of TarValon White Tower South Gate InteriorMap
        rooms = new Room[13];
        maps[0].setRooms(rooms);

        for (int i = 0; i < 13; i++) {
            rooms[i] = new Room();
            rooms[i].setRoomID(i);
            rooms[i].setMaxPlayers(30);
        }

        rooms[0].setFullName("White Tower - South Gate");
        rooms[0].setShortName("south-gate");
        rooms[0].setInsertionPoint(new ScreenPoint(200, 350));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(340, 650, 10, 60));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(1);

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(400, 20, 10, 50));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(1);

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(270, 830, 10, 40));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(272, 830, halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-40len-8th-2/vert-bottom-pivot-1.gif")));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(300, 570, 10, 30));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(4);
        roomLink.setDoor(new Door(302, 570, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/vert-top-pivot-0.gif")));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(300, 110, 10, 30));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(5);
        roomLink.setDoor(new Door(302, 110, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/vert-top-pivot-0.gif")));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(300, 900, 10, 30));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(1);

        mapExit = rooms[0].addMapExit(new ScreenRectangle(0, 975, 250, 25));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.SOUTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(282, 520));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(250, 975, 350, 25));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0));
        mapExit.setTargetPosition(new ScreenPoint(282, 520));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(40, 0, 360, 25));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NORTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 3, 1, 0));
        mapExit.setTargetPosition(new ScreenPoint(280, 750));
        mapExit.setTargetOrientation((float) (-Math.PI / 2));

        rooms[1].setFullName("White Tower - South Gate - Back");
        rooms[1].setShortName("south-gate-back");
        rooms[1].setInsertionPoint(new ScreenPoint(500, 670));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(530, 850, 10, 30));
        roomLink.setRoom1ID(3);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(532, 850, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/vert-top-pivot-0.gif")));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(530, 360, 10, 30));
        roomLink.setRoom1ID(9);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(532, 360, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/vert-top-pivot-0.gif")));

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);
        rooms[1].addRoomLink(rooms[0].getRoomLinks()[1]);
        rooms[1].addRoomLink(rooms[0].getRoomLinks()[5]);

        rooms[2].setFullName("White Tower - South Gate - Guard Room");
        rooms[2].setShortName("guard-room");
        rooms[2].setInsertionPoint(new ScreenPoint(340, 740));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(430, 770, 30, 10));
        roomLink.setRoom1ID(2);
        roomLink.setRoom2ID(3);
        roomLink.setDoor(new Door(430, 772, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-right-pivot-3.gif")));

        rooms[2].addRoomLink(rooms[0].getRoomLinks()[2]);

        rooms[3].setFullName("White Tower - South Gate - Waiting Room");
        rooms[3].setShortName("waiting-room");
        rooms[3].setInsertionPoint(new ScreenPoint(450, 820));

        rooms[3].addRoomLink(rooms[1].getRoomLinks()[0]);
        rooms[3].addRoomLink(rooms[2].getRoomLinks()[0]);

        rooms[4].setFullName("Novice Quarters A");
        rooms[4].setShortName("quartersA");
        rooms[4].setInsertionPoint(new ScreenPoint(330, 430));

        roomLink = rooms[4].addRoomLink(new ScreenRectangle(330, 390, 40, 10));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(4);

        roomLink = rooms[4].addRoomLink(new ScreenRectangle(400, 590, 10, 30));
        roomLink.setRoom1ID(4);
        roomLink.setRoom2ID(6);
        roomLink.setDoor(new Door(402, 590, halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-bottom-pivot-1.gif")));

        roomLink = rooms[4].addRoomLink(new ScreenRectangle(400, 510, 10, 30));
        roomLink.setRoom1ID(4);
        roomLink.setRoom2ID(7);
        roomLink.setDoor(new Door(402, 510, halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-bottom-pivot-1.gif")));

        roomLink = rooms[4].addRoomLink(new ScreenRectangle(400, 430, 10, 30));
        roomLink.setRoom1ID(4);
        roomLink.setRoom2ID(8);
        roomLink.setDoor(new Door(402, 430, halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-bottom-pivot-1.gif")));

        rooms[4].addRoomLink(rooms[0].getRoomLinks()[3]);

        rooms[5].setFullName("Novice Quarters B");
        rooms[5].setShortName("QuartersB");
        rooms[5].setInsertionPoint(new ScreenPoint(330, 350));

        roomLink = rooms[5].addRoomLink(new ScreenRectangle(400, 330, 10, 30));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(9);
        roomLink.setDoor(new Door(402, 330, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        roomLink = rooms[5].addRoomLink(new ScreenRectangle(400, 250, 10, 30));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(10);
        roomLink.setDoor(new Door(402, 250, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        roomLink = rooms[5].addRoomLink(new ScreenRectangle(400, 170, 10, 30));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(11);
        roomLink.setDoor(new Door(402, 170, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        roomLink = rooms[5].addRoomLink(new ScreenRectangle(400, 90, 10, 30));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(12);
        roomLink.setDoor(new Door(402, 90, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        rooms[5].addRoomLink(rooms[0].getRoomLinks()[4]);
        rooms[5].addRoomLink(rooms[4].getRoomLinks()[0]);

        rooms[6].setFullName("White Tower - South Gate - Room 1");
        rooms[6].setShortName("room1");
        rooms[6].setInsertionPoint(new ScreenPoint(470, 600));

        rooms[6].addRoomLink(rooms[4].getRoomLinks()[1]);

        rooms[7].setFullName("White Tower - South Gate - Room 2");
        rooms[7].setShortName("room2");
        rooms[7].setInsertionPoint(new ScreenPoint(470, 520));

        rooms[7].addRoomLink(rooms[4].getRoomLinks()[2]);

        rooms[8].setFullName("White Tower - South Gate - Room 3");
        rooms[8].setShortName("room3");
        rooms[8].setInsertionPoint(new ScreenPoint(470, 440));

        rooms[8].addRoomLink(rooms[4].getRoomLinks()[3]);

        rooms[9].setFullName("White Tower - South Gate - Room 1");
        rooms[9].setShortName("room1");
        rooms[9].setInsertionPoint(new ScreenPoint(470, 360));

        rooms[9].addRoomLink(rooms[1].getRoomLinks()[1]);
        rooms[9].addRoomLink(rooms[5].getRoomLinks()[0]);

        rooms[10].setFullName("White Tower - South Gate - Room 2");
        rooms[10].setShortName("room2");
        rooms[10].setInsertionPoint(new ScreenPoint(470, 260));

        rooms[10].addRoomLink(rooms[5].getRoomLinks()[1]);

        rooms[11].setFullName("White Tower - South Gate - Room 3");
        rooms[11].setShortName("room3");
        rooms[11].setInsertionPoint(new ScreenPoint(470, 180));

        rooms[11].addRoomLink(rooms[5].getRoomLinks()[2]);

        rooms[12].setFullName("White Tower - South Gate - Room 4");
        rooms[12].setShortName("room4");
        rooms[12].setInsertionPoint(new ScreenPoint(470, 90));

        rooms[12].addRoomLink(rooms[5].getRoomLinks()[3]);

        // STEP 11bisc - Rooms of TarValon White Tower Front InteriorMap
        rooms = new Room[2];

        maps[1].setRooms(rooms);

        for (int i = 0; i < 2; i++) {
            rooms[i] = new Room();
            rooms[i].setRoomID(i);
            rooms[i].setMaxPlayers(30);
        }

        rooms[0].setFullName("White Tower - Front");
        rooms[0].setShortName("white-tower");
        rooms[0].setInsertionPoint(new ScreenPoint(380, 400));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(590, 730, 10, 30));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(592, 730, halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-5th-3/vert-bottom-pivot-1.gif")));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(110, 770, 360, 30));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.SOUTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 3, 0, 0));
        mapExit.setTargetPosition(new ScreenPoint(185, 25));
        mapExit.setTargetOrientation((float) (Math.PI / 2));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(320, 0, 130, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NORTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 0, 0));
        mapExit.setTargetPosition(new ScreenPoint(375, 1450));
        mapExit.setTargetOrientation((float) (-Math.PI / 2));

        rooms[1].setFullName("White Tower - Front - Storehouse");
        rooms[1].setShortName("storehouse");
        rooms[1].setInsertionPoint(new ScreenPoint(640, 750));

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        // STEP 11bis d - Tar Valon White Tower - Hall
        maps = new InteriorMap[5];
        buildings[4].setInteriorMaps(maps);

        map = new InteriorMap();
        maps[0] = map;

        map.setInteriorMapID(0);
        map.setFullName("White Tower - Hall");
        map.setShortName("white-tower-hall");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/wt-hall-14"));
        map.setImageWidth(760);
        map.setImageHeight(1500);
        map.setImageRegionWidth(190);
        map.setImageRegionHeight(250);
        map.setMusicName("tv-white-tower-hall.mid");

        // STEP 11 bis e - Rooms of White Tower Hall
        rooms = new Room[26];
        map = maps[0];
        map.setRooms(rooms);
        roomLink = null;

        for (int i = 0; i < 26; i++) {
            rooms[i] = new Room();
            rooms[i].setRoomID(i);
            rooms[i].setMaxPlayers(30);
        }

        rooms[0].setFullName("White Tower - Hall Entrance");
        rooms[0].setShortName("hall-entrance");
        rooms[0].setInsertionPoint(new ScreenPoint(390, 1400));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(605, 1190, 10, 40));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(2);

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(155, 1190, 10, 40));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(0);

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(150, 930, 470, 10));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(0);

        mapExit = rooms[0].addMapExit(new ScreenRectangle(320, 1480, 130, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.SOUTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 3, 1, 0));
        mapExit.setTargetPosition(new ScreenPoint(370, 40));
        mapExit.setTargetOrientation((float) (Math.PI / 2));

        rooms[1].setFullName("White Tower - Hall");
        rooms[1].setShortName("hall");
        rooms[1].setInsertionPoint(new ScreenPoint(370, 800));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(560, 830, 10, 30));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(6);
        roomLink.setDoor(new Door(562, 830, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-8th-4/vert-top-pivot-0.gif")));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(200, 830, 10, 30));
        roomLink.setRoom1ID(7);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(200, 830, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-8th-4/vert-top-pivot-0.gif")));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(190, 390, 40, 10));
        roomLink.setRoom1ID(10);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(190, 391, -halfPI, DoorDrawable.HORIZONTAL_LEFT_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-40len-8th-5/hor-left-pivot-2.gif")));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(540, 390, 40, 10));
        roomLink.setRoom1ID(19);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(540, 391, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-40len-8th-5/hor-right-pivot-3.gif")));

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[2]);

        mapExit = rooms[1].addMapExit(new ScreenRectangle(70, 500, 50, 140));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 4, 0));
        mapExit.setTargetPosition(new ScreenPoint(70, 400));
        mapExit.setTargetOrientation(-halfPI);

        mapExit = rooms[1].addMapExit(new ScreenRectangle(630, 510, 50, 140));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 4, 0));
        mapExit.setTargetPosition(new ScreenPoint(830, 400));
        mapExit.setTargetOrientation(-halfPI);

        rooms[2].setFullName("White Tower - Post Office");
        rooms[2].setShortName("post-office");
        rooms[2].setInsertionPoint(new ScreenPoint(650, 1220));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(660, 1340, 30, 10));
        roomLink.setRoom1ID(2);
        roomLink.setRoom2ID(3);
        roomLink.setDoor(new Door(660, 1342, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-5th-3/hor-right-pivot-3.gif")));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(680, 1130, 30, 10));
        roomLink.setRoom1ID(4);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(680, 1132, -halfPI, DoorDrawable.HORIZONTAL_LEFT_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-5th-3/hor-left-pivot-2.gif")));

        rooms[2].addRoomLink(rooms[0].getRoomLinks()[0]);

        rooms[3].setFullName("White Tower - Post Store");
        rooms[3].setShortName("post-store");
        rooms[3].setInsertionPoint(new ScreenPoint(670, 1390));

        rooms[3].addRoomLink(rooms[2].getRoomLinks()[0]);

        rooms[4].setFullName("White Tower - Post Archive");
        rooms[4].setShortName("post-archive");
        rooms[4].setInsertionPoint(new ScreenPoint(700, 1080));

        rooms[4].addRoomLink(rooms[2].getRoomLinks()[1]);

        rooms[5].setFullName("White Tower - Waiting Room");
        rooms[5].setShortName("waiting-room");
        rooms[5].setInsertionPoint(new ScreenPoint(90, 1300));

        rooms[5].addRoomLink(rooms[0].getRoomLinks()[1]);

        rooms[6].setFullName("White Tower - Common Room");
        rooms[6].setShortName("common");
        rooms[6].setInsertionPoint(new ScreenPoint(630, 800));

        rooms[6].addRoomLink(rooms[1].getRoomLinks()[0]);

        rooms[7].setFullName("White Tower - Administration");
        rooms[7].setShortName("admin");
        rooms[7].setInsertionPoint(new ScreenPoint(140, 840));

        roomLink = rooms[7].addRoomLink(new ScreenRectangle(30, 750, 30, 10));
        roomLink.setRoom1ID(8);
        roomLink.setRoom2ID(7);
        roomLink.setDoor(new Door(30, 752, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-5th-3/hor-right-pivot-3.gif")));

        roomLink = rooms[7].addRoomLink(new ScreenRectangle(60, 950, 30, 10));
        roomLink.setRoom1ID(7);
        roomLink.setRoom2ID(9);
        roomLink.setDoor(new Door(60, 950, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-8th-4/hor-right-pivot-3.gif")));

        rooms[7].addRoomLink(rooms[1].getRoomLinks()[1]);

        rooms[8].setFullName("White Tower - Recent Archive");
        rooms[8].setShortName("archive");
        rooms[8].setInsertionPoint(new ScreenPoint(30, 710));

        rooms[8].addRoomLink(rooms[7].getRoomLinks()[0]);

        rooms[9].setFullName("White Tower - Basement Passage");
        rooms[9].setShortName("passage");
        rooms[9].setInsertionPoint(new ScreenPoint(50, 990));

        rooms[9].addRoomLink(rooms[7].getRoomLinks()[1]);

        mapExit = rooms[9].addMapExit(new ScreenRectangle(30, 1110, 20, 70));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 1, 0));
        mapExit.setTargetPosition(new ScreenPoint(100, 350));
        mapExit.setTargetOrientation((float) Math.PI);

        rooms[10].setFullName("Accepted Quarters A");
        rooms[10].setShortName("accepted-A");
        rooms[10].setInsertionPoint(new ScreenPoint(250, 300));

        roomLink = rooms[10].addRoomLink(new ScreenRectangle(310, 210, 60, 10));
        roomLink.setRoom1ID(11);
        roomLink.setRoom2ID(10);

        roomLink = rooms[10].addRoomLink(new ScreenRectangle(70, 260, 30, 10));
        roomLink.setRoom1ID(12);
        roomLink.setRoom2ID(10);
        roomLink.setDoor(new Door(70, 262, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-right-pivot-3.gif")));

        roomLink = rooms[10].addRoomLink(new ScreenRectangle(160, 260, 30, 10));
        roomLink.setRoom1ID(13);
        roomLink.setRoom2ID(10);
        roomLink.setDoor(new Door(160, 262, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-right-pivot-3.gif")));

        roomLink = rooms[10].addRoomLink(new ScreenRectangle(250, 260, 30, 10));
        roomLink.setRoom1ID(14);
        roomLink.setRoom2ID(10);
        roomLink.setDoor(new Door(250, 262, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-right-pivot-3.gif")));

        roomLink = rooms[10].addRoomLink(new ScreenRectangle(370, 290, 10, 30));
        roomLink.setRoom1ID(10);
        roomLink.setRoom2ID(18);
        roomLink.setDoor(new Door(372, 290, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        rooms[10].addRoomLink(rooms[1].getRoomLinks()[2]);

        rooms[11].setFullName("Accepted Quarters A - Corridor");
        rooms[11].setShortName("accepted-A");
        rooms[11].setInsertionPoint(new ScreenPoint(330, 115));

        roomLink = rooms[11].addRoomLink(new ScreenRectangle(250, 60, 30, 10));
        roomLink.setRoom1ID(11);
        roomLink.setRoom2ID(15);
        roomLink.setDoor(new Door(250, 62, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-right-pivot-3.gif")));

        roomLink = rooms[11].addRoomLink(new ScreenRectangle(160, 60, 30, 10));
        roomLink.setRoom1ID(11);
        roomLink.setRoom2ID(16);
        roomLink.setDoor(new Door(160, 62, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-right-pivot-3.gif")));

        roomLink = rooms[11].addRoomLink(new ScreenRectangle(70, 60, 30, 10));
        roomLink.setRoom1ID(11);
        roomLink.setRoom2ID(17);
        roomLink.setDoor(new Door(70, 62, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-right-pivot-3.gif")));

        rooms[11].addRoomLink(rooms[10].getRoomLinks()[0]);

        rooms[12].setFullName("Accepted Quarters A - Room 1");
        rooms[12].setShortName("room1");
        rooms[12].setInsertionPoint(new ScreenPoint(70, 220));

        rooms[12].addRoomLink(rooms[10].getRoomLinks()[1]);

        rooms[13].setFullName("Accepted Quarters A - Room 2");
        rooms[13].setShortName("room2");
        rooms[13].setInsertionPoint(new ScreenPoint(160, 220));

        rooms[13].addRoomLink(rooms[10].getRoomLinks()[2]);

        rooms[14].setFullName("Accepted Quarters A - Room 3");
        rooms[14].setShortName("room3");
        rooms[14].setInsertionPoint(new ScreenPoint(260, 220));

        rooms[14].addRoomLink(rooms[10].getRoomLinks()[3]);

        rooms[15].setFullName("Accepted Quarters A - Room 4");
        rooms[15].setShortName("room4");
        rooms[15].setInsertionPoint(new ScreenPoint(260, 90));

        rooms[15].addRoomLink(rooms[11].getRoomLinks()[0]);

        rooms[16].setFullName("Accepted Quarters A - Room 5");
        rooms[16].setShortName("room5");
        rooms[16].setInsertionPoint(new ScreenPoint(160, 90));

        rooms[16].addRoomLink(rooms[11].getRoomLinks()[1]);

        rooms[17].setFullName("Accepted Quarters A - Room 6");
        rooms[17].setShortName("room6");
        rooms[17].setInsertionPoint(new ScreenPoint(70, 90));

        rooms[17].addRoomLink(rooms[11].getRoomLinks()[2]);

        rooms[18].setFullName("White Tower - Accepted Office");
        rooms[18].setShortName("office");
        rooms[18].setInsertionPoint(new ScreenPoint(400, 300));

        roomLink = rooms[18].addRoomLink(new ScreenRectangle(490, 290, 10, 30));
        roomLink.setRoom1ID(18);
        roomLink.setRoom2ID(19);
        roomLink.setDoor(new Door(492, 290, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        rooms[18].addRoomLink(rooms[10].getRoomLinks()[4]);

        rooms[19].setFullName("Accepted Quarters Entrance");
        rooms[19].setShortName("quarters");
        rooms[19].setInsertionPoint(new ScreenPoint(540, 310));

        roomLink = rooms[19].addRoomLink(new ScreenRectangle(650, 290, 10, 30));
        roomLink.setRoom1ID(19);
        roomLink.setRoom2ID(20);
        roomLink.setDoor(new Door(650, 290, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-8th-4/vert-top-pivot-0.gif")));

        roomLink = rooms[19].addRoomLink(new ScreenRectangle(600, 260, 30, 10));
        roomLink.setRoom1ID(21);
        roomLink.setRoom2ID(19);

        rooms[19].addRoomLink(rooms[1].getRoomLinks()[3]);
        rooms[19].addRoomLink(rooms[18].getRoomLinks()[0]);

        rooms[20].setFullName("White Tower - Basement Passage");
        rooms[20].setShortName("passage");
        rooms[20].setInsertionPoint(new ScreenPoint(680, 250));

        rooms[20].addRoomLink(rooms[19].getRoomLinks()[0]);

        mapExit = rooms[20].addMapExit(new ScreenRectangle(600, 10, 30, 80));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 2, 0));
        mapExit.setTargetPosition(new ScreenPoint(400, 50));
        mapExit.setTargetOrientation((float) Math.PI);

        rooms[21].setFullName("Accepted Quarters B");
        rooms[21].setShortName("accepted-B");
        rooms[21].setInsertionPoint(new ScreenPoint(530, 210));

        roomLink = rooms[21].addRoomLink(new ScreenRectangle(480, 190, 10, 30));
        roomLink.setRoom1ID(22);
        roomLink.setRoom2ID(21);
        roomLink.setDoor(new Door(482, 190, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        roomLink = rooms[21].addRoomLink(new ScreenRectangle(480, 110, 10, 30));
        roomLink.setRoom1ID(23);
        roomLink.setRoom2ID(21);
        roomLink.setDoor(new Door(482, 110, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        roomLink = rooms[21].addRoomLink(new ScreenRectangle(480, 30, 10, 30));
        roomLink.setRoom1ID(24);
        roomLink.setRoom2ID(21);
        roomLink.setDoor(new Door(482, 30, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        roomLink = rooms[21].addRoomLink(new ScreenRectangle(550, 120, 10, 30));
        roomLink.setRoom1ID(21);
        roomLink.setRoom2ID(25);
        roomLink.setDoor(new Door(552, 120, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        rooms[21].addRoomLink(rooms[19].getRoomLinks()[1]);

        rooms[22].setFullName("Accepted Quarters B - Room 1");
        rooms[22].setShortName("room1");
        rooms[22].setInsertionPoint(new ScreenPoint(440, 200));

        rooms[22].addRoomLink(rooms[21].getRoomLinks()[0]);

        rooms[23].setFullName("Accepted Quarters B - Room 2");
        rooms[23].setShortName("room2");
        rooms[23].setInsertionPoint(new ScreenPoint(440, 120));

        rooms[23].addRoomLink(rooms[21].getRoomLinks()[1]);

        rooms[24].setFullName("Accepted Quarters B - Room 4");
        rooms[24].setShortName("room4");
        rooms[24].setInsertionPoint(new ScreenPoint(440, 40));

        rooms[24].addRoomLink(rooms[21].getRoomLinks()[2]);

        rooms[25].setFullName("Accepted Quarters B - Room 3");
        rooms[25].setShortName("room3");
        rooms[25].setInsertionPoint(new ScreenPoint(570, 130));

        rooms[25].addRoomLink(rooms[21].getRoomLinks()[3]);

        // STEP 11 bis f - Tar Valon White Tower - Basement 0
        map = new InteriorMap();
        maps[1] = map;

        map.setInteriorMapID(1);
        map.setFullName("White Tower - South Basement");
        map.setShortName("wt-basement-0");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/basement-15"));
        map.setImageWidth(780);
        map.setImageHeight(400);
        map.setImageRegionWidth(195);
        map.setImageRegionHeight(400);
        map.setMusicName("dark-basement.mid");

        // STEP 11 bis g - Rooms of White Tower Basement 0
        rooms = new Room[11];
        map.setRooms(rooms);
        roomLink = null;

        for (int i = 0; i < 11; i++) {
            rooms[i] = new Room();
            rooms[i].setRoomID(i);
            rooms[i].setMaxPlayers(30);
        }

        rooms[0].setFullName("White Tower - Basement Stairs");
        rooms[0].setShortName("basement-stairs");
        rooms[0].setInsertionPoint(new ScreenPoint(70, 340));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(10, 230, 70, 10));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(0);

        mapExit = rooms[0].addMapExit(new ScreenRectangle(140, 320, 20, 70));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 0, 9));
        mapExit.setTargetPosition(new ScreenPoint(60, 1140));
        mapExit.setTargetOrientation(0.0f);

        rooms[1].setFullName("White Tower - South Basement");
        rooms[1].setShortName("basement-south");
        rooms[1].setInsertionPoint(new ScreenPoint(330, 190));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(130, 230, 30, 10));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(130, 232, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-5th-3/hor-right-pivot-3.gif")));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(300, 110, 10, 30));
        roomLink.setRoom1ID(3);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(302, 110, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-5th-3/vert-top-pivot-0.gif")));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(300, 350, 10, 30));
        roomLink.setRoom1ID(4);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(302, 350, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-5th-3/vert-top-pivot-0.gif")));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(510, 150, 30, 10));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(1);

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(510, 230, 30, 10));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(6);

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(610, 85, 30, 10));
        roomLink.setRoom1ID(7);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(611, 87, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/jail-6/hor-0.gif")));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(610, 230, 30, 10));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(8);

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(710, 85, 30, 10));
        roomLink.setRoom1ID(9);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(711, 87, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/jail-6/hor-0.gif")));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(710, 230, 30, 10));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(10);

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        mapExit = rooms[1].addMapExit(new ScreenRectangle(310, 0, 160, 20));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 2, 1));
        mapExit.setTargetPosition(new ScreenPoint(90, 700));
        mapExit.setTargetOrientation(-halfPI);

        rooms[2].setFullName("White Tower - Basement Store S1");
        rooms[2].setShortName("basement-store");
        rooms[2].setInsertionPoint(new ScreenPoint(130, 260));

        rooms[2].addRoomLink(rooms[1].getRoomLinks()[0]);

        rooms[3].setFullName("White Tower - Basement Corridor");
        rooms[3].setShortName("basement-store");
        rooms[3].setInsertionPoint(new ScreenPoint(220, 110));

        rooms[3].addRoomLink(rooms[1].getRoomLinks()[1]);

        rooms[4].setFullName("White Tower - Basement Store S2");
        rooms[4].setShortName("basement-store");
        rooms[4].setInsertionPoint(new ScreenPoint(230, 330));

        rooms[4].addRoomLink(rooms[1].getRoomLinks()[2]);

        rooms[5].setFullName("White Tower - Basement Store S3");
        rooms[5].setShortName("basement-store");
        rooms[5].setInsertionPoint(new ScreenPoint(520, 90));

        rooms[5].addRoomLink(rooms[1].getRoomLinks()[3]);

        rooms[6].setFullName("White Tower - Basement Store S4");
        rooms[6].setShortName("basement-store");
        rooms[6].setInsertionPoint(new ScreenPoint(530, 300));

        rooms[6].addRoomLink(rooms[1].getRoomLinks()[4]);

        rooms[7].setFullName("White Tower - Basement Cell");
        rooms[7].setShortName("basement-cell");
        rooms[7].setInsertionPoint(new ScreenPoint(610, 40));

        rooms[7].addRoomLink(rooms[1].getRoomLinks()[5]);

        rooms[8].setFullName("White Tower - Basement Store S5");
        rooms[8].setShortName("basement-store");
        rooms[8].setInsertionPoint(new ScreenPoint(600, 280));

        rooms[8].addRoomLink(rooms[1].getRoomLinks()[6]);

        rooms[9].setFullName("White Tower - Basement Cell");
        rooms[9].setShortName("basement-cell");
        rooms[9].setInsertionPoint(new ScreenPoint(710, 40));

        rooms[9].addRoomLink(rooms[1].getRoomLinks()[7]);

        rooms[10].setFullName("White Tower - Basement Store S6");
        rooms[10].setShortName("basement-store");
        rooms[10].setInsertionPoint(new ScreenPoint(700, 280));

        rooms[10].addRoomLink(rooms[1].getRoomLinks()[8]);

        // STEP 11 bis h - Tar Valon White Tower - Basement 1
        map = new InteriorMap();
        maps[2] = map;

        map.setInteriorMapID(2);
        map.setFullName("White Tower - North Basement");
        map.setShortName("wt-basement-1");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/basement-16"));
        map.setImageWidth(500);
        map.setImageHeight(760);
        map.setImageRegionWidth(500);
        map.setImageRegionHeight(190);
        map.setMusicName("dark-basement.mid");

        // STEP 11 bis i - Rooms of White Tower Basement 1
        rooms = new Room[6];
        map.setRooms(rooms);
        roomLink = null;

        for (int i = 0; i < 6; i++) {
            rooms[i] = new Room();
            rooms[i].setRoomID(i);
            rooms[i].setMaxPlayers(30);
        }

        rooms[0].setFullName("White Tower - Basement Stairs");
        rooms[0].setShortName("basement-stairs");
        rooms[0].setInsertionPoint(new ScreenPoint(340, 50));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(190, 20, 10, 80));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(0);

        mapExit = rooms[0].addMapExit(new ScreenRectangle(450, 20, 20, 80));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 0, 20));
        mapExit.setTargetPosition(new ScreenPoint(650, 40));
        mapExit.setTargetOrientation(0.0f);

        rooms[1].setFullName("White Tower - North Basement");
        rooms[1].setShortName("basement-north");
        rooms[1].setInsertionPoint(new ScreenPoint(100, 140));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(170, 120, 10, 40));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(171, 120, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-40len-8th-5/vert-top-pivot-0.gif")));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(170, 680, 10, 40));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(3);
        roomLink.setDoor(new Door(171, 680, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-40len-8th-5/vert-top-pivot-0.gif")));

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        mapExit = rooms[1].addMapExit(new ScreenRectangle(10, 740, 160, 20));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 1, 1));
        mapExit.setTargetPosition(new ScreenPoint(380, 30));
        mapExit.setTargetOrientation(halfPI);

        rooms[2].setFullName("White Tower - Basement Store");
        rooms[2].setShortName("basement-store");
        rooms[2].setInsertionPoint(new ScreenPoint(300, 130));

        rooms[2].addRoomLink(rooms[1].getRoomLinks()[0]);

        rooms[3].setFullName("White Tower - Basement Store");
        rooms[3].setShortName("basement-store");
        rooms[3].setInsertionPoint(new ScreenPoint(230, 690));

        roomLink = rooms[3].addRoomLink(new ScreenRectangle(320, 660, 30, 10));
        roomLink.setRoom1ID(4);
        roomLink.setRoom2ID(3);
        roomLink.setDoor(new Door(320, 660, -halfPI, DoorDrawable.HORIZONTAL_LEFT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-left-pivot-2.gif")));

        rooms[3].addRoomLink(rooms[1].getRoomLinks()[1]);

        rooms[4].setFullName("White Tower - Restricted Access Store");
        rooms[4].setShortName("basement-store");
        rooms[4].setInsertionPoint(new ScreenPoint(330, 600));

        roomLink = rooms[4].addRoomLink(new ScreenRectangle(320, 550, 30, 10));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(4);
        roomLink.setDoor(new Door(320, 550, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wall-7/hor-0.gif")));

        rooms[4].addRoomLink(rooms[3].getRoomLinks()[0]);

        rooms[5].setFullName("White Tower - Secret Store");
        rooms[5].setShortName("basement-store");
        rooms[5].setInsertionPoint(new ScreenPoint(320, 400));

        rooms[5].addRoomLink(rooms[4].getRoomLinks()[0]);

        mapExit = rooms[5].addMapExit(new ScreenRectangle(320, 330, 50, 20));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 3, 0));
        mapExit.setTargetPosition(new ScreenPoint(375, 330));
        mapExit.setTargetOrientation(-halfPI);

        // STEP 11 bis j - Tar Valon White Tower - Ways
        map = new InteriorMap();
        maps[3] = map;

        map.setInteriorMapID(3);
        map.setFullName("White Tower - The Ways");
        map.setShortName("ways-wt");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/ways-17"));
        map.setImageWidth(700);
        map.setImageHeight(400);
        map.setImageRegionWidth(700);
        map.setImageRegionHeight(400);
        map.setMusicName("ways.mid");

        // STEP 11 bis k - Rooms of White Tower - Ways
        rooms = new Room[1];
        map.setRooms(rooms);

        rooms[0] = new Room();
        rooms[0].setRoomID(0);
        rooms[0].setMaxPlayers(30);

        rooms[0].setFullName("The Ways - White Tower Entrance");
        rooms[0].setShortName("ways");
        rooms[0].setInsertionPoint(new ScreenPoint(370, 300));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(360, 350, 50, 20));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 2, 5));
        mapExit.setTargetPosition(new ScreenPoint(335, 355));
        mapExit.setTargetOrientation(halfPI);

        // STEP 11 bis l - Tar Valon White Tower - Ways
        map = new InteriorMap();
        maps[4] = map;

        map.setInteriorMapID(4);
        map.setFullName("White Tower - Library");
        map.setShortName("wt-library");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/library-18"));
        map.setImageWidth(920);
        map.setImageHeight(500);
        map.setImageRegionWidth(184);
        map.setImageRegionHeight(500);
        map.setMusicName("tv-white-tower-hall.mid");

        // STEP 11 bis m - Rooms of White Tower - Ways
        rooms = new Room[8];
        map.setRooms(rooms);

        for (int i = 0; i < 8; i++) {
            rooms[i] = new Room();
            rooms[i].setRoomID(i);
            rooms[i].setMaxPlayers(30);
        }

        rooms[0].setFullName("White Tower - 5th Floor");
        rooms[0].setShortName("Floor5");
        rooms[0].setInsertionPoint(new ScreenPoint(340, 370));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(430, 330, 60, 10));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(0);

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(160, 280, 30, 10));
        roomLink.setRoom1ID(2);
        roomLink.setRoom2ID(0);
        roomLink.setDoor(new Door(160, 282, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-5th-3/hor-right-pivot-3.gif")));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(730, 280, 30, 10));
        roomLink.setRoom1ID(6);
        roomLink.setRoom2ID(0);
        roomLink.setDoor(new Door(730, 282, halfPI, DoorDrawable.HORIZONTAL_LEFT_PIVOT, new ImageIdentifier("objects-2/doors-0/stone-30len-5th-3/hor-left-pivot-2.gif")));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(20, 410, 130, 30));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 0, 1));
        mapExit.setTargetPosition(new ScreenPoint(120, 570));
        mapExit.setTargetOrientation(halfPI / 3);

        mapExit = rooms[0].addMapExit(new ScreenRectangle(770, 410, 130, 30));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 0, 4, 0, 1));
        mapExit.setTargetPosition(new ScreenPoint(610, 570));
        mapExit.setTargetOrientation(halfPI * 4 / 3);

        rooms[1].setFullName("White Tower - Library Hall");
        rooms[1].setShortName("library-hall");
        rooms[1].setInsertionPoint(new ScreenPoint(450, 140));

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        rooms[2].setFullName("Gray Ajah Quarters");
        rooms[2].setShortName("quarters");
        rooms[2].setInsertionPoint(new ScreenPoint(170, 170));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(130, 230, 10, 30));
        roomLink.setRoom1ID(3);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(132, 230, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(130, 140, 10, 30));
        roomLink.setRoom1ID(4);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(132, 140, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(130, 50, 10, 30));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(132, 50, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        rooms[2].addRoomLink(rooms[0].getRoomLinks()[1]);

        rooms[3].setFullName("Gray Ajah - Room 1");
        rooms[3].setShortName("room1");
        rooms[3].setInsertionPoint(new ScreenPoint(60, 240));

        rooms[3].addRoomLink(rooms[2].getRoomLinks()[0]);

        rooms[4].setFullName("Gray Ajah - Room 2");
        rooms[4].setShortName("room2");
        rooms[4].setInsertionPoint(new ScreenPoint(60, 140));

        rooms[4].addRoomLink(rooms[2].getRoomLinks()[1]);

        rooms[5].setFullName("Gray Ajah - Room 3");
        rooms[5].setShortName("room3");
        rooms[5].setInsertionPoint(new ScreenPoint(60, 60));

        rooms[5].addRoomLink(rooms[2].getRoomLinks()[2]);

        rooms[6].setFullName("White Tower - Corridor");
        rooms[6].setShortName("corridor");
        rooms[6].setInsertionPoint(new ScreenPoint(73, 200));

        roomLink = rooms[6].addRoomLink(new ScreenRectangle(780, 230, 10, 30));
        roomLink.setRoom1ID(6);
        roomLink.setRoom2ID(7);
        roomLink.setDoor(new Door(782, 230, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        roomLink = rooms[6].addRoomLink(new ScreenRectangle(780, 50, 10, 30));
        roomLink.setRoom1ID(6);
        roomLink.setRoom2ID(7);
        roomLink.setDoor(new Door(782, 50, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        rooms[6].addRoomLink(rooms[0].getRoomLinks()[2]);

        rooms[7].setFullName("White Tower - Meeting Room");
        rooms[7].setShortName("meeting-room");
        rooms[7].setInsertionPoint(new ScreenPoint(840, 230));

        rooms[7].addRoomLink(rooms[6].getRoomLinks()[0]);
        rooms[7].addRoomLink(rooms[6].getRoomLinks()[1]);

        // STEP 12 - Blight Refuge Building
        buildings = new Building[1];
        townMaps[1].setBuildings(buildings);

        buildings[0] = new Building(0, 0, 10, 10);
        buildings[0].setBuildingID(0);
        buildings[0].setFullName("Blight Refuge");
        buildings[0].setShortName("blightrefuge");
        buildings[0].setServerID(0);
        buildings[0].setHasTownExits(true);
        buildings[0].setHasBuildingExits(true);
        buildings[0].setSmallBuildingImage(new ImageIdentifier()); // no image

        // STEP 13 - Blight Refuge Exterior InteriorMap
        maps = new InteriorMap[3];
        buildings[0].setInteriorMaps(maps);

        map = new InteriorMap();
        maps[0] = map;

        map.setInteriorMapID(0);
        map.setFullName("Blight Refuge - Outside");
        map.setShortName("blight-refuge-outside");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/blight-refuge-ext-7"));
        map.setImageWidth(720);
        map.setImageHeight(400);
        map.setImageRegionWidth(720);
        map.setImageRegionHeight(400);

        map.setMusicName("blight-refuge.mid");

        // STEP 14 - Rooms of Blight Refuge Ext InteriorMap
        rooms = new Room[2];
        map.setRooms(rooms);

        rooms[0] = new Room();
        rooms[0].setRoomID(0);
        rooms[0].setMaxPlayers(30);

        rooms[0].setFullName("Blight Refuge - Outside");
        rooms[0].setShortName("refuge-ouside");
        rooms[0].setInsertionPoint(new ScreenPoint(610, 200));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(400, 350, 5, 10));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(0);

        mapExit = rooms[0].addMapExit(new ScreenRectangle(400, 170, 20, 80));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 1, 0, 1, 0));
        mapExit.setTargetPosition(new ScreenPoint(370, 142));
        mapExit.setTargetOrientation((float) (Math.PI));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(700, 60, 20, 300));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.EAST);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(779, 133));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(630, 380, 70, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(779, 133));

        rooms[1] = new Room();
        rooms[1].setRoomID(1);
        rooms[1].setMaxPlayers(30);

        rooms[1].setFullName("Blight Refuge - Terrace");
        rooms[1].setShortName("refuge-terrace");
        rooms[1].setInsertionPoint(new ScreenPoint(250, 200));

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        mapExit = rooms[1].addMapExit(new ScreenRectangle(160, 50, 20, 310));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 1, 0, 1, 2));
        mapExit.setTargetPosition(new ScreenPoint(350, 360));
        mapExit.setTargetOrientation((float) (Math.PI));

        // STEP 15 - Blight Refuge Int0 InteriorMap
        map = new InteriorMap();
        maps[1] = map;

        map.setInteriorMapID(1);
        map.setFullName("Blight Refuge - Entrance");
        map.setShortName("blight-refuge-entrance");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/blight-refuge-int0-8"));
        map.setImageWidth(400);
        map.setImageHeight(500);
        map.setImageRegionWidth(400);
        map.setImageRegionHeight(500);

        map.setMusicName("blight-hall.mid");

        // STEP 16 - Rooms of Blight Refuge Int0 InteriorMap
        rooms = new Room[4];
        map.setRooms(rooms);

        rooms[0] = new Room();
        rooms[0].setRoomID(0);
        rooms[0].setMaxPlayers(30);

        rooms[0].setFullName("Blight Refuge - Hall");
        rooms[0].setShortName("refuge-hall");
        rooms[0].setInsertionPoint(new ScreenPoint(246, 147));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(330, 20, 10, 30));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(1);
        roomLink.setDoor(new Door(331, 20, halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/vert-top-pivot-0.gif")));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(90, 290, 30, 10));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(90, 291, halfPI, DoorDrawable.HORIZONTAL_LEFT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/hor-left-pivot-2.gif")));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(375, 130, 30, 50));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 1, 0, 0, 0));
        mapExit.setTargetPosition(new ScreenPoint(410, 200));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(0, 70, 20, 160));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 1, 0, 2, 0));
        mapExit.setTargetPosition(new ScreenPoint(480, 260));
        mapExit.setTargetOrientation((float) (Math.PI));

        rooms[1] = new Room();
        rooms[1].setRoomID(1);
        rooms[1].setMaxPlayers(30);

        rooms[1].setFullName("Blight Refuge - Room");
        rooms[1].setShortName("refuge-room");
        rooms[1].setInsertionPoint(new ScreenPoint(354, 24));

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        rooms[2] = new Room();
        rooms[2].setRoomID(2);
        rooms[2].setMaxPlayers(30);

        rooms[2].setFullName("Blight Refuge - Meeting Room");
        rooms[2].setShortName("refuge-meeting");
        rooms[2].setInsertionPoint(new ScreenPoint(300, 380));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(350, 290, 30, 10));
        roomLink.setRoom1ID(3);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(350, 291, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/hor-right-pivot-3.gif")));

        rooms[2].addRoomLink(rooms[0].getRoomLinks()[1]);

        mapExit = rooms[2].addMapExit(new ScreenRectangle(355, 355, 25, 55));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 1, 0, 0, 1));
        mapExit.setTargetPosition(new ScreenPoint(170, 150));

        rooms[3] = new Room();
        rooms[3].setRoomID(3);
        rooms[3].setMaxPlayers(30);

        rooms[3].setFullName("Blight Refuge - Store");
        rooms[3].setShortName("refuge-store");
        rooms[3].setInsertionPoint(new ScreenPoint(360, 260));

        rooms[3].addRoomLink(rooms[2].getRoomLinks()[0]);

        // STEP 17 - Blight Refuge Int1 InteriorMap
        map = new InteriorMap();
        maps[2] = map;

        map.setInteriorMapID(2);
        map.setFullName("Blight Refuge - Hall");
        map.setShortName("blight-refuge-hall");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/blight-refuge-int1-9"));
        map.setImageWidth(525);
        map.setImageHeight(530);
        map.setImageRegionWidth(525);
        map.setImageRegionHeight(530);

        map.setMusicName("blight-hall.mid");

        // STEP 18 - Rooms of Blight Refuge Int0 InteriorMap
        rooms = new Room[1];
        map.setRooms(rooms);

        rooms[0] = new Room();
        rooms[0].setRoomID(0);
        rooms[0].setMaxPlayers(30);

        rooms[0].setFullName("Blight Refuge - Main Hall");
        rooms[0].setShortName("refuge-mainhall");
        rooms[0].setInsertionPoint(new ScreenPoint(450, 230));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(500, 230, 30, 70));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 1, 0, 1, 0));
        mapExit.setTargetPosition(new ScreenPoint(10, 140));

        // STEP 19 - Shayol Ghul Building
        buildings = new Building[1];
        townMaps[2].setBuildings(buildings);

        buildings[0] = new Building(0, 0, 10, 10);
        buildings[0].setBuildingID(0);
        buildings[0].setFullName("Shayol Ghul");
        buildings[0].setShortName("shayolghul");
        buildings[0].setServerID(0);
        buildings[0].setHasTownExits(true);
        buildings[0].setHasBuildingExits(true);
        buildings[0].setSmallBuildingImage(new ImageIdentifier()); // no image

        // STEP 20 - Shayol Ghul Entrance InteriorMap
        maps = new InteriorMap[2];
        buildings[0].setInteriorMaps(maps);

        map = new InteriorMap();
        maps[0] = map;

        map.setInteriorMapID(0);
        map.setFullName("Shayol Ghul - Entrance");
        map.setShortName("shayol-entrance");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/shayol-ghul-entrance-13"));
        map.setImageWidth(600);
        map.setImageHeight(350);
        map.setImageRegionWidth(600);
        map.setImageRegionHeight(350);

        map.setMusicName("blight-refuge.mid");

        // STEP 21 - Rooms of Shayol Ghul Entrance InteriorMap
        rooms = new Room[2];
        map.setRooms(rooms);

        rooms[0] = new Room();
        rooms[0].setRoomID(0);
        rooms[0].setMaxPlayers(30);

        rooms[0].setFullName("Shayol Ghul - Entrance");
        rooms[0].setShortName("shayol-ghul");
        rooms[0].setInsertionPoint(new ScreenPoint(500, 240));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(380, 270, 20, 40));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(0);

        mapExit = rooms[0].addMapExit(new ScreenRectangle(410, 0, 170, 20));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 2, 0, 1, 2));
        mapExit.setTargetPosition(new ScreenPoint(1300, 300));
        mapExit.setTargetOrientation((float) (-Math.PI / 2));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(570, 200, 30, 130));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.EAST);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(806, 84));

        rooms[1] = new Room();
        rooms[1].setRoomID(1);
        rooms[1].setMaxPlayers(30);

        rooms[1].setFullName("Shayol Ghul - Cavern");
        rooms[1].setShortName("shayol-ghul");
        rooms[1].setInsertionPoint(new ScreenPoint(200, 90));

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        mapExit = rooms[1].addMapExit(new ScreenRectangle(140, 0, 170, 20));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 2, 0, 1, 1));
        mapExit.setTargetPosition(new ScreenPoint(1020, 300));
        mapExit.setTargetOrientation((float) (-Math.PI / 2));

        // STEP 22 - Shayol Ghul Center InteriorMap
        map = new InteriorMap();
        maps[1] = map;

        map.setInteriorMapID(1);
        map.setFullName("Shayol Ghul - Prison");
        map.setShortName("shayol-prison");
        map.setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/shayol-ghul-dark-lord-12"));
        map.setImageWidth(1400);
        map.setImageHeight(350);
        map.setImageRegionWidth(200);
        map.setImageRegionHeight(350);

        map.setMusicName("blight-refuge.mid");

        // STEP 23 - Rooms of Shayol Ghul Center InteriorMap
        rooms = new Room[3];
        map.setRooms(rooms);

        rooms[0] = new Room();
        rooms[0].setRoomID(0);
        rooms[0].setMaxPlayers(30);

        rooms[0].setFullName("Shayol Ghul - Prison");
        rooms[0].setShortName("shayol-prison");
        rooms[0].setInsertionPoint(new ScreenPoint(450, 150));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(900, 160, 25, 125));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(1);

        rooms[1] = new Room();
        rooms[1].setRoomID(1);
        rooms[1].setMaxPlayers(30);

        rooms[1].setFullName("Shayol Ghul - Tunnels");
        rooms[1].setShortName("shayol-tunnels");
        rooms[1].setInsertionPoint(new ScreenPoint(1020, 220));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(1160, 10, 25, 130));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(2);

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        mapExit = rooms[1].addMapExit(new ScreenRectangle(935, 325, 220, 25));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 2, 0, 0, 1));
        mapExit.setTargetPosition(new ScreenPoint(210, 25));
        mapExit.setTargetOrientation((float) (Math.PI / 2));

        rooms[2] = new Room();
        rooms[2].setRoomID(2);
        rooms[2].setMaxPlayers(30);

        rooms[2].setFullName("Shayol Ghul - Tunnel");
        rooms[2].setShortName("shayol-ghul");
        rooms[2].setInsertionPoint(new ScreenPoint(1300, 275));

        rooms[2].addRoomLink(rooms[1].getRoomLinks()[0]);

        mapExit = rooms[2].addMapExit(new ScreenRectangle(1200, 325, 200, 25));
        mapExit.setType(MapExit.INTERIOR_MAP_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0, 2, 0, 0, 0));
        mapExit.setTargetPosition(new ScreenPoint(480, 25));
        mapExit.setTargetOrientation((float) (Math.PI / 2));

        // STEP 24 - Braem Wood Building
        buildings = new Building[1];
        townMaps[3].setBuildings(buildings);

        buildings[0] = new Building(0, 0, 10, 10);
        buildings[0].setBuildingID(0);
        buildings[0].setFullName("Braem Wood Road");
        buildings[0].setShortName("braem-road");
        buildings[0].setServerID(0);
        buildings[0].setHasTownExits(true);
        buildings[0].setHasBuildingExits(true);
        buildings[0].setSmallBuildingImage(new ImageIdentifier()); // no image

        // STEP 25 - Braem Wood Road InteriorMap
        maps = new InteriorMap[1];
        maps[0] = new InteriorMap();
        buildings[0].setInteriorMaps(maps);

        maps[0].setInteriorMapID(0);
        maps[0].setFullName("Braem Wood - Road");
        maps[0].setShortName("braem-road");
        maps[0].setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/braem-wood-10"));
        maps[0].setImageWidth(1800);
        maps[0].setImageHeight(350);
        maps[0].setImageRegionWidth(200);
        maps[0].setImageRegionHeight(350);

        maps[0].setMusicName("tv-clearing.mid");

        // STEP 26 - Rooms of Braem Wood Road InteriorMap
        rooms = new Room[4];
        maps[0].setRooms(rooms);

        for (int i = 0; i < 4; i++) {
            rooms[i] = new Room();
            rooms[i].setRoomID(i);
            rooms[i].setMaxPlayers(30);
        }

        rooms[0].setFullName("Braem Wood - Ruins");
        rooms[0].setShortName("bw-ruins");
        rooms[0].setInsertionPoint(new ScreenPoint(260, 160));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(640, 230, 10, 40));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(1);

        mapExit = rooms[0].addMapExit(new ScreenRectangle(320, 0, 140, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(713, 370));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(190, 330, 470, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(707, 393));

        rooms[1].setFullName("Braem Wood - Road");
        rooms[1].setShortName("bw-road");
        rooms[1].setInsertionPoint(new ScreenPoint(880, 100));

        roomLink = rooms[1].addRoomLink(new ScreenRectangle(1245, 200, 10, 120));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(2);

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        mapExit = rooms[1].addMapExit(new ScreenRectangle(770, 0, 180, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NORTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(713, 370));

        mapExit = rooms[1].addMapExit(new ScreenRectangle(700, 330, 520, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.SOUTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(707, 393));

        rooms[2].setFullName("Braem Wood");
        rooms[2].setShortName("wood");
        rooms[2].setInsertionPoint(new ScreenPoint(1400, 140));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(1510, 40, 10, 30));
        roomLink.setRoom1ID(2);
        roomLink.setRoom2ID(3);
        roomLink.setDoor(new Door(1512, 40, halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-bottom-pivot-1.gif")));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(1740, 40, 10, 30));
        roomLink.setRoom1ID(3);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(1742, 40, -halfPI, DoorDrawable.VERTICAL_BOTTOM_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-bottom-pivot-1.gif")));

        rooms[2].addRoomLink(rooms[1].getRoomLinks()[0]);

        mapExit = rooms[2].addMapExit(new ScreenRectangle(1360, 0, 150, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(713, 370));

        mapExit = rooms[2].addMapExit(new ScreenRectangle(1750, 0, 20, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(713, 370));

        mapExit = rooms[2].addMapExit(new ScreenRectangle(1460, 330, 330, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NONE);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(707, 393));

        rooms[3].setFullName("Braem Wood - Deserted House");
        rooms[3].setShortName("bw-house");
        rooms[3].setInsertionPoint(new ScreenPoint(1600, 100));

        rooms[3].addRoomLink(rooms[2].getRoomLinks()[0]);
        rooms[3].addRoomLink(rooms[2].getRoomLinks()[1]);

        // STEP 27 - Two Rivers Building
        buildings = new Building[1];
        townMaps[4].setBuildings(buildings);

        buildings[0] = new Building(0, 0, 10, 10);
        buildings[0].setBuildingID(0);
        buildings[0].setFullName("Two Rivers Road");
        buildings[0].setShortName("two-rivers-road");
        buildings[0].setServerID(0);
        buildings[0].setHasTownExits(true);
        buildings[0].setHasBuildingExits(true);
        buildings[0].setSmallBuildingImage(new ImageIdentifier()); // no image

        // STEP 28 - Two Rivers Road InteriorMap
        maps = new InteriorMap[1];
        maps[0] = new InteriorMap();
        buildings[0].setInteriorMaps(maps);

        maps[0].setInteriorMapID(0);
        maps[0].setFullName("Two Rivers - Road");
        maps[0].setShortName("trivers-road");
        maps[0].setInteriorMapImage(new ImageIdentifier("maps-1/universe-2/two-rivers-19"));
        maps[0].setImageWidth(800);
        maps[0].setImageHeight(700);
        maps[0].setImageRegionWidth(200);
        maps[0].setImageRegionHeight(700);

        maps[0].setMusicName("two-rivers.mid");

        // STEP 29 - Rooms of Two Rivers Road InteriorMap
        rooms = new Room[9];
        maps[0].setRooms(rooms);

        for (int i = 0; i < 9; i++) {
            rooms[i] = new Room();
            rooms[i].setRoomID(i);
            rooms[i].setMaxPlayers(30);
        }

        rooms[0].setFullName("Emond's Field - North Road");
        rooms[0].setShortName("north-road");
        rooms[0].setInsertionPoint(new ScreenPoint(300, 200));

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(160, 230, 10, 90));
        roomLink.setRoom1ID(1);
        roomLink.setRoom2ID(0);

        roomLink = rooms[0].addRoomLink(new ScreenRectangle(400, 380, 10, 90));
        roomLink.setRoom1ID(0);
        roomLink.setRoom2ID(2);

        mapExit = rooms[0].addMapExit(new ScreenRectangle(190, 0, 210, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.NORTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(437, 404));

        mapExit = rooms[0].addMapExit(new ScreenRectangle(170, 680, 180, 20));
        mapExit.setType(MapExit.BUILDING_EXIT);
        mapExit.setMapExitSide(MapExit.SOUTH);
        mapExit.setTargetWotlasLocation(new WotlasLocation(0));
        mapExit.setTargetPosition(new ScreenPoint(442, 424));

        rooms[1].setFullName("Two Rivers - West Forest");
        rooms[1].setShortName("west-forest");
        rooms[1].setInsertionPoint(new ScreenPoint(70, 270));

        rooms[1].addRoomLink(rooms[0].getRoomLinks()[0]);

        rooms[2].setFullName("Emond's Field - Aybara Domain");
        rooms[2].setShortName("aybara");
        rooms[2].setInsertionPoint(new ScreenPoint(500, 440));

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(410, 90, 70, 10));
        roomLink.setRoom1ID(3);
        roomLink.setRoom2ID(2);

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(740, 360, 40, 10));
        roomLink.setRoom1ID(3);
        roomLink.setRoom2ID(2);

        roomLink = rooms[2].addRoomLink(new ScreenRectangle(570, 370, 30, 10));
        roomLink.setRoom1ID(4);
        roomLink.setRoom2ID(2);
        roomLink.setDoor(new Door(570, 372, halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/hor-right-pivot-3.gif")));

        rooms[2].addRoomLink(rooms[0].getRoomLinks()[1]);

        rooms[3].setFullName("Aybara Domain - Back");
        rooms[3].setShortName("back");
        rooms[3].setInsertionPoint(new ScreenPoint(700, 50));

        roomLink = rooms[3].addRoomLink(new ScreenRectangle(750, 210, 10, 30));
        roomLink.setRoom1ID(7);
        roomLink.setRoom2ID(3);
        roomLink.setDoor(new Door(752, 210, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-8th-1/vert-top-pivot-0.gif")));

        rooms[3].addRoomLink(rooms[2].getRoomLinks()[0]);
        rooms[3].addRoomLink(rooms[2].getRoomLinks()[1]);

        rooms[4].setFullName("Aybara House - Hall");
        rooms[4].setShortName("hall");
        rooms[4].setInsertionPoint(new ScreenPoint(570, 300));

        roomLink = rooms[4].addRoomLink(new ScreenRectangle(550, 270, 30, 10));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(4);
        roomLink.setDoor(new Door(550, 272, halfPI, DoorDrawable.HORIZONTAL_LEFT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-left-pivot-2.gif")));

        roomLink = rooms[4].addRoomLink(new ScreenRectangle(620, 300, 10, 30));
        roomLink.setRoom1ID(4);
        roomLink.setRoom2ID(6);
        roomLink.setDoor(new Door(622, 300, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        rooms[4].addRoomLink(rooms[2].getRoomLinks()[2]);

        rooms[5].setFullName("Aybara House - Dining Room");
        rooms[5].setShortName("dining-room");
        rooms[5].setInsertionPoint(new ScreenPoint(510, 210));

        roomLink = rooms[5].addRoomLink(new ScreenRectangle(620, 90, 10, 40));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(8);

        roomLink = rooms[5].addRoomLink(new ScreenRectangle(620, 210, 10, 30));
        roomLink.setRoom1ID(5);
        roomLink.setRoom2ID(7);
        roomLink.setDoor(new Door(622, 210, -halfPI, DoorDrawable.VERTICAL_TOP_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/vert-top-pivot-0.gif")));

        rooms[5].addRoomLink(rooms[4].getRoomLinks()[0]);

        rooms[6].setFullName("Aybara House - Lounge");
        rooms[6].setShortName("lounge");
        rooms[6].setInsertionPoint(new ScreenPoint(660, 300));

        roomLink = rooms[6].addRoomLink(new ScreenRectangle(690, 270, 30, 10));
        roomLink.setRoom1ID(7);
        roomLink.setRoom2ID(6);
        roomLink.setDoor(new Door(690, 272, -halfPI, DoorDrawable.HORIZONTAL_RIGHT_PIVOT, new ImageIdentifier("objects-2/doors-0/wood-30len-5th-0/hor-right-pivot-3.gif")));

        rooms[6].addRoomLink(rooms[4].getRoomLinks()[1]);

        rooms[7].setFullName("Aybara House - Office");
        rooms[7].setShortName("office");
        rooms[7].setInsertionPoint(new ScreenPoint(670, 200));

        rooms[7].addRoomLink(rooms[3].getRoomLinks()[0]);
        rooms[7].addRoomLink(rooms[5].getRoomLinks()[1]);
        rooms[7].addRoomLink(rooms[6].getRoomLinks()[0]);

        rooms[8].setFullName("Aybara House - Kitchen");
        rooms[8].setShortName("kitchen");
        rooms[8].setInsertionPoint(new ScreenPoint(660, 120));

        rooms[8].addRoomLink(rooms[5].getRoomLinks()[0]);

        return worldMap;
    }

    /*------------------------------------------------------------------------------------*/

    /** Main method.
     *  @param argv : -debug or -base=xxx
     */
    public static void main(String argv[]) {
        String basePath = null;
        for (int i = 0; i < argv.length; i++) {

            if (!argv[i].startsWith("-"))
                continue;

            if (argv[i].equals("-debug")) {

                // -- TO SET THE DEBUG MODE --
                System.out.println("mode DEBUG on");
                Debug.displayExceptionStack(true);

            } else if (argv[i].equals("-base")) {

                // -- TO SET THE CONFIG FILES LOCATION --
                if (i == argv.length - 1) {
                    System.out.println("Location missing.");
                    return;
                }
                basePath = argv[i + 1];
            }
        }

        // STEP 2 - WORLD CREATION : RANDLAND
        // TODO Define the game context. 
        WotlasGameDefinition wgd = null;
        WorldMap worldMaps[] = new WorldMap[1];
        worldMaps[0] = WorldGenerator.createRandlandWorld();

        // STEP XX - We save this simple universe.
        ResourceManager rManager = new ResourceManager(basePath, true, wgd);
        WorldManager wManager = new WorldManager(worldMaps, rManager);

        if (wManager.saveUniverse(true))
            Debug.signal(Debug.NOTICE, null, "World Save Succeeded...");
        else
            Debug.signal(Debug.NOTICE, null, "World Save Failed...");
    }

    /*------------------------------------------------------------------------------------*/
}