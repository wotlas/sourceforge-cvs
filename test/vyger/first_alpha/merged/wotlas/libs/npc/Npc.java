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
package wotlas.libs.npc;

// package wotlas.libs.npc;

import wotlas.common.PrimaryKeyGenerator;
import wotlas.common.character.BasicChar;
import wotlas.common.router.MessageRouter;
import wotlas.common.screenobject.NpcOnTheScreen;

/**  Npc 
  *
  * @author Diego
 */
public class Npc {

    transient public int x, y;
    transient private NpcDefinition npcDef;
    transient private String npcDefName;
    transient private BasicChar basicChar;
    //    transient private short[] picture = {2,2};

    transient private NpcOnTheScreen npcOnTheScreen;

    /** Our current TileMap ( if we are in a TileMap, null otherwise )
    */
    //    private TileMap myTileMap;
    public Npc(String name, int x, int y, MessageRouter msgRouter) {
        try {
            this.npcDefName = name;
            this.npcDef = (NpcDefinition) NpcManager.npcDef.get(this.npcDefName);
            this.basicChar = this.npcDef.getBasicChar().getClass().newInstance();
            this.basicChar.clone(this.npcDef.getBasicChar());
            this.basicChar.setPrimaryKey(PrimaryKeyGenerator.GenUniqueKeyId());
            this.npcOnTheScreen = new NpcOnTheScreen(x, y, name, this.getBasicChar(), this.npcDef.getPicture(), msgRouter);
            msgRouter.addScreenObject(this.npcOnTheScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.npcDef.getName();
    }

    public BasicChar getBasicChar() {
        return this.basicChar;
    }

    public NpcOnTheScreen getScreenObject() {
        return this.npcOnTheScreen;
    }
}