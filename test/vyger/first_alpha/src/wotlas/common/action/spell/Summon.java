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
package wotlas.common.action.spell;

import wotlas.common.universe.*;
import wotlas.server.*;
import wotlas.common.*;
import wotlas.common.screenobject.*;
import wotlas.common.router.*;
import wotlas.libs.npc.*;
import wotlas.utils.Debug;
import wotlas.common.character.*;

public class Summon extends Spell {

    String name;

    public Summon(String name) {
        if(Spell.loadByServer)
            if(!NpcManager.npcDef.containsKey(name) )
                Debug.signal( Debug.CRITICAL, this, "This npcDefinition ["+name+"] wasnt created!"
                +"So summon spell can't be init with this value!");
        this.name = name;
    }

    /** cast the spell to the map: it's called only by the server
     */
    public void CastToMap(CharData caster, int casterX, int casterY, int targetX, int targetY) {
        Spell.Validate(caster);
        TileMap map = ServerDirector.getDataManager().getWorldManager().getTileMap(caster.getLocation());
        Npc npc = new Npc(name,targetX,targetY,map.getMessageRouter());
        map.addNpc( npc );
    }
}