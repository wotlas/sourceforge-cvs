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

import wotlas.common.character.*;
import wotlas.common.environment.*;

/**  Npc 
  *
  * @author Diego
 */
public class Npc {

    transient public int x,y;
    transient private NpcDefinition npcDef;
    transient private String npcDefName;
    transient private BasicChar basicChar;
    
    public Npc( String name, int x, int y ) {
        try {
            this.npcDefName = name;
            this.npcDef = (NpcDefinition) NpcManager.npcDef.get(npcDefName);
            this.basicChar = (BasicChar) npcDef.getBasicChar().getClass().newInstance();
            this.basicChar.clone(npcDef.getBasicChar());
            this.x = x;
            this.y = y;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getName() {
        return npcDef.getName();
    }
    
    public BasicChar getBasicChar() {
        return basicChar;
    }
}