package wotlas.common.action.spell;

import wotlas.common.universe.*;
import wotlas.server.*;
import wotlas.common.*;
import wotlas.common.screenobject.*;
import wotlas.common.router.*;
import wotlas.libs.npc.*;
import wotlas.utils.Debug;

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
    public void CastToMap(WotlasLocation loc, int x, int y) {
        TileMap map = ServerDirector.getDataManager().getWorldManager().getTileMap(loc);
        Npc npc = new Npc(name,x,y);
        map.addNpc( npc );
    }
}
