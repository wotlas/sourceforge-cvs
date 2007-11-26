/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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

package wotlas.common.objects.interfaces;

//import wotlas.common.Knowledge;
import wotlas.common.Player;
import wotlas.common.objects.valueds.Material;

/** 
 * The base interface for all repairable objects.
 * 
 * @author Elann
 * @see wotlas.common.objects.BaseObject
 */

public interface RepairInterface {

    public static final String[] stateList = { "Newly-made", "Good state", "Used", "Worned out", "Broken" };

    // that's just place-holder stuff, OK ?
    // may be a file or a static list
    // but better if in a file => internationalization 

    /*------------------------------------------------------------------------------------*/

    /** Returns the state of the object - string version
      * @return a state string
      */
    public String getStateString();

    /** Get the object's state
     * @return state
     */
    public short getState();

    /** Sets the object's state
     * @param state the new state
     */
    public void setState(short state);

    /** Get the knowledge needed to repair.
     * @return knowledge needed
     */
    public String[]/*Knowledge[] */getRepairKnowledge();

    /** Get the materials needed to repair.<br>
     * Get this from the repairer.
     * @return material list
     * @param repairer the Player that repairs the object. May be the owner or not.
     */
    public Material[] getRepairMaterial(Player repairer);

    /** Repair the object.
     * @param repairer the Player that repairs the object. May be the owner or not.
     */
    public void repair(Player repairer);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
