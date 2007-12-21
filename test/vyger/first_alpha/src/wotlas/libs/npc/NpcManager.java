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

/**  Npc Manager
  *
  * @author Diego
 */
public class NpcManager implements Runnable {
    
    /** Our thread.
    */
    private Thread thread;
    
    private boolean quit;
    
    static protected final String NPC_SCRIPTS_FILE = "npc_def.txt";
    
    public static Hashtable npcDef;
    
 /*------------------------------------------------------------------------------------*/    
    
    private Command[] commands;
    
    private void setCommands() {
        commands = new Command[17];
        // commands to create npc
        commands[0] = new Command( "ADD CREATURE", "pc = NpcDefinition()\npc.setName(\"","\")" );
        commands[1] = new Command( "RLRACE", "pc.setRace(\"wotlas.common.character.roguelike.","\")" );
        commands[2] = new Command( "END", "elencoN.put(pc.name,pc)\nprint \"    "
        +"       <\"+pc.name+\"> done!\"","" );
        commands[3] = new Command( "RLCLASS", "pc.setClass(\"wotlas.common.character.roguelike.","\")" );
        commands[4] = new Command( "WOTCLASS", "pc.setRace(\"wotlas.common.character.","\")" );
        commands[5] = new Command( "LEVEL", "pc.setLevel(",")" );
        commands[6] = new Command( "CLONE CREATURE", "pc = NpcDefinition()\npc.clone(\"","\")");
        commands[7] = new Command( "NAME", "pc.name=\"","\"" );
        // commands about set stats
        commands[8] = new Command( "STR", "pc.getBasicChar().setCharAttr(CharData.ATTR_STR,",")" );
        commands[9] = new Command( "INT", "pc.getBasicChar().setCharAttr(CharData.ATTR_INT,",")" );
        commands[10] = new Command( "WIS", "pc.getBasicChar().setCharAttr(CharData.ATTR_WIS,",")" );
        commands[11] = new Command( "CON", "pc.getBasicChar().setCharAttr(CharData.ATTR_CON,",")" );
        commands[12] = new Command( "DEX", "pc.getBasicChar().setCharAttr(CharData.ATTR_DEX,",")" );
        commands[13] = new Command( "CHA", "pc.getBasicChar().setCharAttr(CharData.ATTR_CHA,",")" );
        commands[14] = new Command( "MANA", "pc.getBasicChar().setCharAttr(CharData.ATTR_MANA,",")" );
        commands[15] = new Command( "MOV", "pc.getBasicChar().setCharAttr(CharData.ATTR_MOV,",")" );
        // .....
        commands[16] = new Command( "PICTURE", "pc.setPicture(",")" );
    }

    class Command {
        
        private String checkValue,replaceValue,stringEnd;
        private int index;
        
        public Command(String checkValue, String replaceValue, String stringEnd){
            this.checkValue = checkValue;
            this.replaceValue = replaceValue;
            this.stringEnd = stringEnd;
            this.index = checkValue.length();
        }
        
        public String controll(String stringToCheck) {
            if(stringToCheck.length() <= 2)
                return stringToCheck;
            if( stringToCheck.length() >= index ) {
                if( stringToCheck.substring(0,
                index).equalsIgnoreCase(checkValue) )
                    stringToCheck = replaceValue+stringToCheck.substring(index).trim()+stringEnd;
            }
            return stringToCheck;
        }
    }
    
 /*------------------------------------------------------------------------*/

    public void init() {
        // throws PyException {
        int rowIndex;
        String parse = "";
        npcDef = new Hashtable(10);
        setCommands();
        while(true) {
            rowIndex = 0;
            try {
                BufferedReader tmp;
                tmp = new BufferedReader( new FileReader( ServerDirector.getResourceManager(
                ).getScriptsDataDir()+NPC_SCRIPTS_FILE ) );
                while( tmp.ready() ){
                    parse = tmp.readLine().trim();
                    rowIndex ++;
                    for(int i=0; i < commands.length; i++)
                        parse = commands[i].controll( parse );
                    if(parse.length() > 2)
                        ServerDirector.interp.exec( parse );

                }
                tmp.close();
                break;
            } catch (Exception e){
                System.out.println("[error at row "+rowIndex+" ] line text : "+parse);
                e.printStackTrace();
            }
            System.out.println("......press enter to reload npc_def.txt");
            npcDef = new Hashtable(10);
            try{
                System.in.read(); // cr
                System.in.read(); // +lf = return 13+10 != 26 :o)
            }catch( Exception e ) {
                e.printStackTrace();
            }
        }
        commands = null;
    }
    
// FIXME ??? -> fin du fichier ???
    
    public void run() {
        Thread me = Thread.currentThread();
        while ( (thread == me )
        && !shouldQuit() ) {
            action();
            try {
                Thread.sleep(1000*2);
            } catch (InterruptedException e){
                // the VM doesn't want us to sleep anymore,
                // so get back to work
                break;
            }
        }
        thread = null;
    }

    /** Start thread.
    */
    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.setName("NpcManager");
        thread.start();
    }
  
    /** Stop thread.
    */
    public synchronized void stop() {
        thread = null;
        notify();
    }
    
    public boolean shouldQuit(){
        return quit;
    }

    public void shouldQuit(boolean quit){
        this.quit = quit;
    }

    public void action(){
        System.out.println("it's time to action for Npc!");
        // check for maps with players
        // check for npc inside map
        // :------------- HOW and WITH HOW DELAY an attack
    }
}