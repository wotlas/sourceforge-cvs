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

package wotlas.libs.npc;

import wotlas.common.environment.*;
import wotlas.server.ServerDirector;
import wotlas.common.character.*;
import wotlas.common.character.roguelike.*;
import wotlas.common.*;
import wotlas.utils.*;

import java.io.*;
import java.util.*;

import org.python.util.PythonInterpreter;
import org.python.core.*;

/**  Npc Definitions
  *
  * @author Diego
 */
public class NpcDefinition  {
    
    static protected final String NPC_SCRIPTS_FILE = "npc_def.txt";
    
    transient private String name="";
    transient public String[] triggers;
    transient private BasicChar basicChar;
    
 /*------------------------------------------------------------------------------------*/    
    /** Empty constructor
     */
    public NpcDefinition() {
    }
    
    /** set the race of the npc : a WoT character class
     * or a RLike character race class
     */
    public void setRace(String className) throws Exception {
        // should throw excaption to the script loader
        basicChar = (BasicChar) Class.forName(className).newInstance();
        basicChar.init();
    }

    /** set the Class of the Npc it's used only for RLikeCharacters
     * in RLike environment
     *
     */
    public void setClass(String className) throws Exception {
        // should throw excaption to the script loader
        ((RLikeCharacter)basicChar).setClass( (RLikeClass) Class.forName(className).newInstance() );
    }
    
    public void setLevel(int level) {
        basicChar.setLevel(level);
    }

/*
    public boolean isTrigger(){
        for(int i=0; i< triggers.length;i++)
            SimpleEmbedded.interp.exec( triggers[i] );
        return true;
    }
*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void clone(String npcName) throws Exception {
        NpcDefinition value = (NpcDefinition) NpcManager.npcDef.get(npcName);
        name = new String(value.name);
        basicChar = (BasicChar) value.basicChar.getClass().newInstance();
        basicChar.clone(value.basicChar);
    }

    public BasicChar getBasicChar() {
        return basicChar;
    }
}