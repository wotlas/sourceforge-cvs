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
import java.util.*;
import java.io.*;

import wotlas.utils.Debug;

/** Implementation of a generic WotChanneller. Any character capable of channelling 
 * extends a Channeller class, all of which are subclasses of this.
 *
 * @author Chris
 * @see wotlas.common.Player
 * @see wotlas.common.power.Weave
 * @see wotlas.libs.graphics2D.Drawable
 */

public abstract class Channeller 
{
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Storage for the list of Weaves usable by this Channeller
     */
    private HashMap weaveList = new HashMap();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Storage for the full list of Weaves
     */
    private HashMap fullWeaveList = new HashMap();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Storage for the status of the True Source
     */
    private boolean trueSourceUsable = false;
    private boolean trueSourceInUse = false;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Storage for the status of the Source
     */
    private boolean isChannelling = false;
 
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor
     */
    public Channeller() {

	/** Load the available Weave classes
	 * ASSUMING NOT IN JAR FILE
	 */
	File weavesFiles[] = new File( "wotlas/common/power/Weaves" ).listFiles();

	if( weavesFiles==null || weavesFiles.length==0 ) {
	    Debug.signal( Debug.WARNING, this, "No Weaves found in wotlas/common/power/Weaves!" );
	    return;
	}

	for (int i=0; i< weavesFiles.length; i++ ) {

	    if( !weavesFiles[i].isFile() || !weavesFiles[i].getName().endsWith(".class") )
		continue;

	    // Load the class
	    try{
		String name = weavesFiles[i].getName();
		Class cl = Class.forName("wotlas.common.power.Weaves." 
					 + name.substring( 0, name.lastIndexOf(".class") ) );

		if (cl==null || cl.isInterface())
		    continue;

		Object o = cl.newInstance();

		if( o==null || !(o instanceof Weave) )
		    continue;
	    // Ok, we have a valid Weave class.
		fullWeaveList.put( ((Weave) o).getName(), (Weave) o );
	    }
	    catch( Exception e ) {
		Debug.signal( Debug.WARNING, this, e );
	    }
	}

    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a list of Weaves usable by this Channeller.</P>
     * Not modifiable directly, it is generated each time this method is called.
     *
     * @return an array of the Strings, the names of the weaves.
     */
    public String[] getWeaveList() {
	return (String[] ) weaveList.keySet().toArray();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a weave to the Channeller's list
     *
     * @param weaveName the name of the new weave to be added.
     * @return success status
     */
    public boolean addWeave( String weaveName ) {
	if ( !(fullWeaveList.containsKey(weaveName)) ) {
	     return false;
	}
	else {
	    weaveList.put(weaveName, fullWeaveList.get(weaveName) );
	    return true;
	}
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a Weave
     *
     * @param weaveName the name of the Power (see the list produced by getPowerList())
     * @return the Power
     */
    public Weave getWeave( String weaveName ) {
	return (Weave) weaveList.get(weaveName);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Seize/Embrace the Source
     * This is here for when I implement time spent holding the Source effects
     *
     * @return success status (true if successful)
     */
    public boolean openSource() {
	isChannelling = true;
	return isChannelling;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Release the Source
     *
     * This is here for when I implement time spent holding the source effects
     */
    public boolean releaseSource() {
	isChannelling = false;
	return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the Power Points the Channeller has remaining
     * NOT YET IMPLEMENTED
     *
     * @return the number of Power Points the channeller has remaining
     */
    public int getPowerPoints() {
	return -1;
    }

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
    public int getPowerLevel() {
	return -1;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Test if the True Source is available
     *
     * @return success status
     */
    public boolean isTrueSourceAvailable() {
	return trueSourceUsable;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Toggle whether or not the True Source is in use, or the One Source
     *
     * @return the status of the True Source, true=in use
     */
    public boolean toggleSourcePower() {
	if (trueSourceUsable) {
	    trueSourceInUse = !(trueSourceInUse);
	    return trueSourceInUse;
	}
	else {
	    return false;
	}
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Test if the Channeller can Channel
     *
     * @return success status
     */
    public boolean isChanneller() {
	return true; // Not considering the option of severed Channellers yet
    }

}
