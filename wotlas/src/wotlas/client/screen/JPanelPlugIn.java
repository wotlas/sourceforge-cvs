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

package wotlas.client.screen;

import javax.swing.JPanel;

/** Represents a JPanel plug-in that can be added to the JPlayerPanel.
 *  To create a plug-in just create a class that this class. That's all.
 *  The class will be searched via the classpath and loaded dynamically.
 *
 *  Note also that the plug-in class must have an empty constructor and should
 *  do its main inits in the init() method.
 *
 * @author Aldiss
 */

public abstract class JPanelPlugIn extends JPanel {

    /*------------------------------------------------------------------------------------*/

    /** Constructor
     */
    protected JPanelPlugIn() {
        super();
    }

    /*------------------------------------------------------------------------------------*/

    /** Called once to initialize the plug-in.
     *
     *  @return if true we display the plug-in, return false if something fails during
     *          this init(), this way the plug-in won't be displayed.
     */
    abstract public boolean init();

    /*------------------------------------------------------------------------------------*/

    /** Called when we need to reset the content of this plug-in.
     */
    abstract public void reset();

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
     * @return a short name for the plug-in
     */
    abstract public String getPlugInName();

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in's author.
     * @return author name.
     */
    abstract public String getPlugInAuthor();

    /*------------------------------------------------------------------------------------*/

    /** Returns the tool tip text that will be displayed in the JPlayerPanel.
     * @return a short tool tip text
     */
    @Override
    abstract public String getToolTipText();

    /*------------------------------------------------------------------------------------*/

    /** Eventual index in the list of JPlayerPanels
     * @return -1 if the plug-in has to be added at the end of the plug-in list,
     *         otherwise a positive integer for a precise location.
     */
    abstract public int getPlugInIndex();

    /*------------------------------------------------------------------------------------*/

    /** Tells if this plug-in is a system plug-in that represents some base
     *  wotlas feature.
     * @return true means system plug-in, false means user plug-in
     */
    abstract public boolean isSystemPlugIn();

    /*------------------------------------------------------------------------------------*/
}
