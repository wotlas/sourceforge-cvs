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

package wotlas.common.power;

import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.libs.graphics2D.*;

import java.io.*;
import java.awt.Color;

/** Generic Wotlas Power. Each Channeller object possess a collection of Power objects for their amusement.
 *
 * @author Chris
 * @see wotlas.common.power.Power
 * @see wotlas.libs.graphics2D.Drawable
 */

public abstract class Power {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor
     */
    public Power() {
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** to get the name of the power
     */
    public String getName() {
	return this.name;
    }

    private String name;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Where all the magic happens, this version is for simple powers that require
     * no target
     * @param Playter the channeller
     * @return success status
     */
    public abstract boolean channel( Player channeller );

    /** Alternative channel() method - this one requires a Character as a target
     *
     * @param channellerr the channeller Player
     * @param target the target Player
     * @return success status
     */
    public abstract boolean channel( Player channeller, Player target );

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a Drawable for the effect of this power, returns null on the server-side
     *  or if no visual effect is required.
     * 
     * Can this be animated?
     * Should channel really deal with this?
     *
     * @param playerLocation player current location
     * @return image identifier of the pwwer in action
     */
    public abstract Drawable getEffect( WotlasLocation playerLocation );

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/


}
