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

import wotlas.common.character.CharData;
import wotlas.common.screenobject.ItemOnTheScreen;
import wotlas.common.universe.TileMap;
import wotlas.server.ServerDirector;

public class Create extends Spell {

    String name;

    public Create(String name) {
        this.name = name;
    }

    /** cast the spell to the map: it's called only by the server
     */
    @Override
    public void CastToMap(CharData caster, int casterX, int casterY, int targetX, int targetY) {
        Spell.Validate(caster);
        TileMap map = ServerDirector.getDataManager().getWorldManager().getTileMap(caster.getLocation());
        // System.out.println("create :"+itemKey);
        short[] picture = { 2, 2 };
        map.getMessageRouter().addScreenObject(new ItemOnTheScreen(targetX, targetY, this.name, picture));
    }
}