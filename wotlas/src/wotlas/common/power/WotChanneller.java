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

package wotlas.common.power;

/** Interface of a Wotlas Channeller. Any character capable of channelling extends the Channeller class
 *
 * @author Chris
 * @see wotlas.common.Player
 * @see wotlas.common.power.Weave
 * @see wotlas.libs.graphics2d.Drawable
 */

public interface WotChanneller {
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a list of Weaves usable by this Channeller.</P>
     * Not modifiable directly, it is generated each time this method is called.
     *
     * @return an array of the Strings, the names of the weaves.
     */
    public String[] getWeaveList();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Adds a Weave to this Channeller's list
     *
     * @param weaveName the name of the new Weave to be added.
     * @return success status (true if successful)
     */
    public boolean addWeave(String weaveName);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a Weave
     *
     * @param weaveName the name of the Power (see the list produced by getPowerList())
     * @return the Weave
     */
    public Weave getWeave(String weaveName);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Seize/Embrace the Source
     * This is here for when I implement time spent holding the Source effects
     *
     * @return success status (true if successful)
     */
    public boolean openSource();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Release the Source
     *
     * This is here for when I implement time spent holding the source effects
     */
    public boolean releaseSource();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Power Points the Channeller has remaining
     * NOT YET IMPLEMENTED
     *
     * @return the number of Power Points the channeller has remaining
     */
    public int getPowerPoints();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Power level of the Channeller
     *
     * Power level will control how much of the Source can be handled, this will
     * determine which weaves may be used, and how much power may be put into them.  
     * The return object of this may change to a structure with different values for
     * each type of flow.
     *
     * NOT YET IMPLEMENTED
     * @return the Power level of the Channeller
     */
    public int getPowerLevel();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Test if the True Source is available
     *
     * @return success status
     */
    public boolean isTrueSourceAvailable();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Toggle whether or not the True Source is in use, or the One Source
     *
     * @return the status of the True Source, true=in use
     */
    public boolean toggleTrueSource();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Test if the Channeller can Channel
     *
     * @return success status
     */
    public boolean isChanneller();

}
