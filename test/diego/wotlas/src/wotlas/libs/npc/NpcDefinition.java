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
import wotlas.common.*;

import java.io.*;
import java.util.*;

import org.python.util.PythonInterpreter;
import org.python.core.*;

/**  Npc Definitions
  *
  * @author Diego
 */
public class NpcDefinition implements Cloneable {
    
    static protected final String NPC_SCRIPTS_FILE = "npc_def.txt";
    
    public static Hashtable npcDef;
    
    
    transient public String name;
    transient public String[] triggers;
    transient public int str=0;
    transient public BasicChar basicChar;
    
 /*------------------------------------------------------------------------------------*/    

    static public void LoadNpcDef() {
    // throws PyException {
        npcDef = new Hashtable(10);
        PythonInterpreter interp = ServerDirector.interp;
        while(true) {
            try {
                BufferedReader tmp;
                tmp = new BufferedReader( new FileReader( ServerDirector.getResourceManager(
                ).getScriptsDataDir()+NPC_SCRIPTS_FILE ) );
                String parse = null;
                while( tmp.ready() ){
                    parse = tmp.readLine();
                    if(parse.length() > 1)
                        interp.exec( parse );
                }
                tmp.close();
                break;
            } catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("......press enter to reload npc_def.txt .");
            npcDef = new Hashtable(10);
            try{
                System.in.read();
                System.in.read();
            }catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    public NpcDefinition() {
    }
/*
    public boolean isTrigger(){
        for(int i=0; i< triggers.length;i++)
            SimpleEmbedded.interp.exec( triggers[i] );
        return true;
    }
*/
    public NpcDefinition Clone(){
        NpcDefinition clone = new NpcDefinition();
        clone.name = new String(name);
        if( triggers != null ){
            clone.triggers = new String[triggers.length];
            for(int i=0; i< triggers.length;i++)
                clone.triggers[i] = new String(triggers[i]);
        } else
            clone.triggers = null;
        clone.str = str;
        return clone;
    }
}