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

// package wotlas.libs.npc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import wotlas.server.ServerDirector;

/**  Npc Manager
  *
  * @author Diego
 */
public class NpcManager implements Runnable {

    /**
     * Our thread.
     */
    private Thread thread;

    private boolean quit;

    static protected final String NPC_SCRIPTS_FILE = "npc_def.txt";

    public static Hashtable npcDef;

    /*------------------------------------------------------------------------------------*/

    private Command[] commands;

    private void setCommands() {
        this.commands = new Command[17];
        // commands to create npc
        this.commands[0] = new Command("ADD CREATURE", "pc = NpcDefinition()\npc.setName(\"", "\")");
        this.commands[1] = new Command("RLRACE", "pc.setRace(\"wotlas.common.character.roguelike.", "\")");
        this.commands[2] = new Command("END", "elencoN.put(pc.name,pc)\nprint \"    " + "       <\"+pc.name+\"> done!\"", "");
        this.commands[3] = new Command("RLCLASS", "pc.setClass(\"wotlas.common.character.roguelike.", "\")");
        this.commands[4] = new Command("WOTCLASS", "pc.setRace(\"wotlas.common.character.", "\")");
        this.commands[5] = new Command("LEVEL", "pc.setLevel(", ")");
        this.commands[6] = new Command("CLONE CREATURE", "pc = NpcDefinition()\npc.clone(\"", "\")");
        this.commands[7] = new Command("NAME", "pc.name=\"", "\"");
        // commands about set stats
        this.commands[8] = new Command("STR", "pc.getBasicChar().setCharAttr(CharData.ATTR_STR,", ")");
        this.commands[9] = new Command("INT", "pc.getBasicChar().setCharAttr(CharData.ATTR_INT,", ")");
        this.commands[10] = new Command("WIS", "pc.getBasicChar().setCharAttr(CharData.ATTR_WIS,", ")");
        this.commands[11] = new Command("CON", "pc.getBasicChar().setCharAttr(CharData.ATTR_CON,", ")");
        this.commands[12] = new Command("DEX", "pc.getBasicChar().setCharAttr(CharData.ATTR_DEX,", ")");
        this.commands[13] = new Command("CHA", "pc.getBasicChar().setCharAttr(CharData.ATTR_CHA,", ")");
        this.commands[14] = new Command("MANA", "pc.getBasicChar().setCharAttr(CharData.ATTR_MANA,", ")");
        this.commands[15] = new Command("MOV", "pc.getBasicChar().setCharAttr(CharData.ATTR_MOV,", ")");
        // .....
        this.commands[16] = new Command("PICTURE", "pc.setPicture(", ")");
    }

    class Command {

        private String checkValue, replaceValue, stringEnd;
        private int index;

        public Command(String checkValue, String replaceValue, String stringEnd) {
            this.checkValue = checkValue;
            this.replaceValue = replaceValue;
            this.stringEnd = stringEnd;
            this.index = checkValue.length();
        }

        public String controll(String stringToCheck) {
            if (stringToCheck.length() <= 2)
                return stringToCheck;
            if (stringToCheck.length() >= this.index) {
                if (stringToCheck.substring(0, this.index).equalsIgnoreCase(this.checkValue))
                    stringToCheck = this.replaceValue + stringToCheck.substring(this.index).trim() + this.stringEnd;
            }
            return stringToCheck;
        }
    }

    /*------------------------------------------------------------------------*/

    public void init() {
        // throws PyException {
        int rowIndex;
        String parse = "";
        NpcManager.npcDef = new Hashtable(10);
        setCommands();
        while (true) {
            rowIndex = 0;
            try {
                BufferedReader tmp;
                tmp = new BufferedReader(new FileReader(ServerDirector.getResourceManager().getScriptsDataDir() + NpcManager.NPC_SCRIPTS_FILE));
                while (tmp.ready()) {
                    parse = tmp.readLine().trim();
                    rowIndex++;
                    for (int i = 0; i < this.commands.length; i++)
                        parse = this.commands[i].controll(parse);
                    if (parse.length() > 2)
                        ServerDirector.getPythonInterp().exec(parse);

                }
                tmp.close();
                break;
            } catch (Exception e) {
                System.out.println("[error at row " + rowIndex + " ] line text : " + parse);
                e.printStackTrace();
            }
            System.out.println("......press enter to reload npc_def.txt");
            NpcManager.npcDef = new Hashtable(10);
            try {
                System.in.read(); // cr
                System.in.read(); // +lf = return 13+10 != 26 :o)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.commands = null;
    }

    // FIXME ??? -> fin du fichier ???

    public void run() {
        Thread me = Thread.currentThread();
        while (this.thread == me && !shouldQuit()) {
            action();
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                // the VM doesn't want us to sleep anymore,
                // so get back to work
                break;
            }
        }
        this.thread = null;
    }

    /**
     * Start thread.
     */
    public void start() {
        this.thread = new Thread(this);
        this.thread.setPriority(Thread.NORM_PRIORITY);
        this.thread.setName("NpcManager");
        this.thread.start();
    }

    /**
     * Stop thread.
     */
    public synchronized void stop() {
        this.thread = null;
        notify();
    }

    public boolean shouldQuit() {
        return this.quit;
    }

    public void shouldQuit(boolean quit) {
        this.quit = quit;
    }

    public void action() {
        System.out.println("it's time to action for Npc!");
        // check for maps with players
        // check for npc inside map
        // :------------- HOW and WITH HOW DELAY an attack
    }
}