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
package wotlas.server.action.spell;

import wotlas.common.action.spell.Spell;
import wotlas.common.character.CharData;
import wotlas.common.screenobject.ArrowSpell;
import wotlas.common.screenobject.SpellOnTheScreen;
import wotlas.common.universe.TileMap;
import wotlas.server.ServerDirector;

public class Plasma extends Spell {

    int effectNr;
    int damage;

    public Plasma(int effectNr, int damage) {
        this.effectNr = effectNr;
        this.damage = damage;
    }

    /**
     * cast the spell to the map: it's called only by the server
     */
    @Override
    public void CastToTarget(CharData caster, CharData target) {
        Spell.Validate(caster, target);
        TileMap map = ServerDirector.getDataManager().getWorldManager().getTileMap(caster.getLocation());

        SpellOnTheScreen eff = new ArrowSpell(this.effectNr, caster.getScreenObject().getX(), caster.getScreenObject().getY(), map.getMessageRouter(), target.getScreenObject().getX(), target.getScreenObject().getY());

        map.getMessageRouter().addScreenObject(eff);
    }
}