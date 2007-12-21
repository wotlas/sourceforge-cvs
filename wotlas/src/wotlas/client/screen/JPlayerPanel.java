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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import wotlas.client.ClientDirector;
import wotlas.utils.Debug;
import wotlas.utils.Tools;

/** JPanel that possesses & display various plugins that implement
 *  the JPanelPlugIn package.
 *
 * @author Petrus, Aldiss
 */
public class JPlayerPanel extends JPanel implements MouseListener {

    /*------------------------------------------------------------------------------------*/
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** Our TabbedPane where are stored the plug-ins.
     */
    private final JTabbedPane playerTabbedPane;

    /*------------------------------------------------------------------------------------*/
    /** Consctructor.
     */
    public JPlayerPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.playerTabbedPane = new JTabbedPane();
        add(this.playerTabbedPane);
    }

    /*------------------------------------------------------------------------------------*/
    /** To init the JPlayerPanel with the available plug-ins.
     */
    protected void init() {

        /** We load the available plug-ins (we search everywhere).
         */
        Class<wotlas.client.screen.JPanelPlugIn> classes[] = null;

        try {
            String packages[] = {"wotlas.client.screen.plugin"};
            classes = Tools.getImplementorsOf("wotlas.client.screen.JPanelPlugIn", packages);
        } catch (ClassNotFoundException e) {
            Debug.signal(Debug.CRITICAL, this, e);
            return;
        } catch (SecurityException e) {
            Debug.signal(Debug.CRITICAL, this, e);
            return;
        } catch (RuntimeException e) {
            Debug.signal(Debug.ERROR, this, e);
            return;
        }

        if (classes == null || classes.length == 0) {
            // we retry in the plug-in package (this is neede dif we are not in a jar file)
            try {
                String packageName[] = {"wotlas.client.screen.plugin"};

                classes = Tools.getImplementorsOf("wotlas.client.screen.JPanelPlugIn", packageName);
            } catch (ClassNotFoundException e) {
                Debug.signal(Debug.CRITICAL, this, e);
                return;
            } catch (SecurityException e) {
                Debug.signal(Debug.CRITICAL, this, e);
                return;
            } catch (RuntimeException e) {
                Debug.signal(Debug.ERROR, this, e);
                return;
            }
        }

        for (int i = 0; i < classes.length; i++) {

            // We get an instance of the plug-in and add it to our JTabbedPane
            try {
                Object o = classes[i].newInstance();

                if (o == null || !(o instanceof JPanelPlugIn)) {
                    continue;
                }

                JPanelPlugIn plugIn = (JPanelPlugIn) o;

                if (!plugIn.init()) {
                    continue; // init failed
                }

                // Ok, we have a valid plug-in
                addPlugIn(plugIn, plugIn.getPlugInIndex());
            } catch (Exception e) {
                Debug.signal(Debug.WARNING, this, e);
            }
        }

        Debug.signal(Debug.NOTICE, null, "Loaded " + this.playerTabbedPane.getTabCount() + " plug-ins...");

    }

    /*------------------------------------------------------------------------------------*/
    /** To add a plugin to our tabbed pane
     *  @param plugIn plug-in to add
     *  @param index index in the list, -1 means at the end.
     */
    public void addPlugIn(JPanelPlugIn plugIn, int index) {
        if (index < 0 || index > this.playerTabbedPane.getTabCount() - 1) {
            this.playerTabbedPane.addTab(plugIn.getPlugInName(), ClientDirector.getResourceManager().getImageIcon("pin.gif"), plugIn, plugIn.getToolTipText());
        } else {
            this.playerTabbedPane.insertTab(plugIn.getPlugInName(), ClientDirector.getResourceManager().getImageIcon("pin.gif"), plugIn, plugIn.getToolTipText(), index);
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To get a PlugIn given its name.
     * @param plugInName plug-in name as returned by plugIn.getPlugInName()
     * @return plugIn, null if not found
     */
    public JPanelPlugIn getPlugIn(String plugInName) {

        for (int i = 0; i < this.playerTabbedPane.getTabCount(); i++) {
            JPanelPlugIn plugIn = (JPanelPlugIn) this.playerTabbedPane.getComponentAt(i);

            if (plugIn.getPlugInName().equals(plugInName)) {
                return plugIn;
            }
        }

        return null; // not found
    }

    /*------------------------------------------------------------------------------------*/
    /** To reset the JPlayerPanel and the state of all its plug-ins.
     */
    public void reset() {
        for (int i = 0; i < this.playerTabbedPane.getTabCount(); i++) {
            JPanelPlugIn plugIn = (JPanelPlugIn) this.playerTabbedPane.getComponentAt(i);
            plugIn.reset();
        }
    }

    /*------------------------------------------------------------------------------------*/
    /**
     * Invoked when the mouse button is clicked
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component
     */
    public void mouseReleased(MouseEvent e) {
    }

    /*------------------------------------------------------------------------------------*/
}
