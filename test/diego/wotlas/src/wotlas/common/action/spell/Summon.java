package wotlas.common.action.spell;

import wotlas.common.universe.*;
import wotlas.server.*;
import wotlas.common.*;
import wotlas.common.screenobject.*;
import wotlas.common.router.*;

public class Summon extends Spell {

    String name;

    public Summon(String name) {
        this.name = name;
    }
        
    public void CastToMap(WotlasLocation loc, int x, int y) {
        TileMap map = ServerDirector.getDataManager().getWorldManager().getTileMap(loc);
        // System.out.println("summon :"+npcKey);
        map.getMessageRouter().addScreenObject( new NpcOnTheScreen(x,y,name) );
    }
}
