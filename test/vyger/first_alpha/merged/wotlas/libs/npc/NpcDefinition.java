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

import wotlas.common.character.BasicChar;
import wotlas.common.character.RLikeCharacter;
import wotlas.common.character.roguelike.RLikeClass;

/**  Npc Definitions
  *
  * @author Diego
 */
public class NpcDefinition {

    static protected final String NPC_SCRIPTS_FILE = "npc_def.txt";

    transient private String name = "";
    transient public String[] triggers;
    transient private BasicChar basicChar;
    transient private short[] picture = { 2, 2 };

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
        this.basicChar = (BasicChar) Class.forName(className).newInstance();
        this.basicChar.init();
    }

    /** set the Class of the Npc it's used only for RLikeCharacters
     * in RLike environment
     *
     */
    public void setClass(String className) throws Exception {
        // should throw excaption to the script loader
        ((RLikeCharacter) this.basicChar).setClass((RLikeClass) Class.forName(className).newInstance());
    }

    public void setLevel(int level) {
        this.basicChar.setLevel(level);
    }

    /*
        public boolean isTrigger(){
            for(int i=0; i< triggers.length;i++)
                SimpleEmbedded.interp.exec( triggers[i] );
            return true;
        }
    */

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void clone(String npcName) throws Exception {
        NpcDefinition value = (NpcDefinition) NpcManager.npcDef.get(npcName);
        this.name = new String(value.name);
        this.basicChar = value.basicChar.getClass().newInstance();
        this.basicChar.clone(value.basicChar);
    }

    public BasicChar getBasicChar() {
        return this.basicChar;
    }

    public void setPicture(int img, int nr) {
        this.picture = new short[2];
        this.picture[0] = (short) img;
        this.picture[1] = (short) nr;
    }

    public short[] getPicture() {
        return this.picture;
    }
}