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

package wotlas.server;

import java.util.Enumeration;
import java.util.Hashtable;
import wotlas.common.character.CharData;
import wotlas.common.screenobject.NpcOnTheScreen;
import wotlas.common.screenobject.PlayerOnTheScreen;
import wotlas.common.screenobject.ScreenObject;
import wotlas.common.screenobject.SpellOnTheScreen;
import wotlas.common.universe.TileMap;

/**  Action Manager
 * manages movement and actions of player over a TileMap
 * to be sure people cant cast while moving
 *
 * @author Diego
 */
public class ActionManager implements Runnable {

    /** Our thread.
    */
    transient private Thread thread;

    transient private boolean quit;

    transient private TileMap[] tileMaps;

    /*------------------------------------------------------------------------*/

    public ActionManager() {
        this.tileMaps = ServerDirector.getDataManager().getWorldManager().getWorldMaps()[0].getTileMaps();
    }

    public void run() {
        Thread me = Thread.currentThread();
        while ((this.thread == me) && !shouldQuit()) {
            action();
            try {
                Thread.sleep(1000 * 1);
            } catch (InterruptedException e) {
                // the VM doesn't want us to sleep anymore,
                // so get back to work
                break;
            }
        }
        this.thread = null;
    }

    /** Start thread.
    */
    public void start() {
        this.thread = new Thread(this);
        this.thread.setPriority(Thread.NORM_PRIORITY);
        this.thread.setName("ActionManager");
        this.thread.start();
    }

    /** Stop thread.
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
        // System.out.println("it's time to action for Npc!");
        // check for maps with players
        // check for npc inside map
        // :------------- HOW and WITH HOW DELAY an attack
        Hashtable screenObjects;
        ScreenObject obj;
        for (int i = 0; i < this.tileMaps.length; i++) {
            screenObjects = this.tileMaps[i].getMessageRouter().getScreenObjects();
            if (screenObjects.size() <= 0)
                continue;
            for (Enumeration e = screenObjects.elements(); e.hasMoreElements();) {
                obj = (ScreenObject) e.nextElement();
                if (obj instanceof PlayerOnTheScreen) {
                    if (((PlayerOnTheScreen) obj).isConnectedToGame())
                        ((PlayerOnTheScreen) obj).serverTick();
                    /*
                    System.out.println("Compare : x vs x "
                    +((PlayerOnTheScreen)obj).getPlayer().getX()
                    +" vs "
                    +obj.getX() );
                    */
                } else if (obj instanceof NpcOnTheScreen) {
                    NpcOnTheScreen zrachu = ((NpcOnTheScreen) obj);
                    zrachu.serverTick();
                    if (zrachu.getMovementComposer().isMoving())
                        System.out.println("moving x,y " + debugThis(zrachu.getMovementComposer().getXPosition()) + "," + debugThis(zrachu.getMovementComposer().getYPosition()));
                } else if (obj instanceof SpellOnTheScreen) {
                    if (((SpellOnTheScreen) obj).serverTick()) {
                        System.out.println("Removing Object!");
                        screenObjects.remove(obj.getPrimaryKey());
                    }
                }
            }
        }
    }

    /** valid my movement :
     * Cant move while : 
     * 1) timestop and i'm not the caster
     * 2) Frozen/paralized/holded
     * Should advise : 
     * 1) if not casting : nothing
     * 2) if casting/action : abort action
     *
     * As much as it's possible try to do nothing to data while here.
     *
     */
    public boolean checkForMovement(PlayerImpl player) {
        TileMap zone = player.getMyTileMap();
        String key = player.getPrimaryKey();
        CharData charData = player.getBasicChar();

        if (!charData.getLocation().isTileMap())
            return true;

        // check for timestop
        // and check if the player is immune to timestop
        if (zone.isBlockedByTimeStop(key) && !charData.isFlagSet(CharData.FLAG_TIME_ANCHOR)) {
            return false;
        }

        // check if the player is blocked by paralize, hold and so on
        if (charData.isFlagSet(CharData.FLAG_PARALIZED)) {
            return false;
        }

        // abort action or casting, causing delay
        // add bonus attack for enemy near ( AN EXTRA ATTACK )
        return true;
    }

    public int debugThis(int value) {
        return new Integer(value / 32).intValue();
    }

    public int debugThis(float value) {
        return new Integer((int) (value / 32)).intValue();
    }
}