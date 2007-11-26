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

package wotlas.server.setup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import wotlas.common.WorldManager;
import wotlas.common.WorldTree;
import wotlas.common.universe.Building;
import wotlas.libs.aswing.ALabel;
import wotlas.server.ServerDirector;
import wotlas.utils.Debug;

/** This a utility for server management: it displays all the buildings and let you
 * choose the server owner of each building.
 *
 * @author Aldiss
 */

public class ServerMapSetup extends JPanel {

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     * @param wManager worldManager to use to get the world data.
     */
    public ServerMapSetup() {
        super();

        setLayout(new BorderLayout());
        setBackground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

        ALabel ltitle = new ALabel("Double-click on a building to change the server that owns it (serverID):");
        ltitle.setBackground(Color.white);
        add(ltitle, BorderLayout.NORTH);

        BuildingTree buildingTree = new BuildingTree(ServerAdminGUI.getWorldManager()); // internal class
        buildingTree.setBackground(Color.white);
        add(buildingTree, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.white);
        add(buttonPanel, BorderLayout.SOUTH);

        ImageIcon im_saveup = ServerDirector.getResourceManager().getImageIcon("save-up.gif");
        ImageIcon im_savedo = ServerDirector.getResourceManager().getImageIcon("save-do.gif");

        JButton bSave = new JButton(im_saveup);
        bSave.setRolloverIcon(im_savedo);
        bSave.setPressedIcon(im_savedo);
        bSave.setBorderPainted(false);
        bSave.setContentAreaFilled(false);
        bSave.setFocusPainted(false);

        bSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ServerAdminGUI.getWorldManager().getWorldMaps() == null) {
                    JOptionPane.showMessageDialog(null, " No world data to save.", "INFORMATION", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (ServerAdminGUI.getWorldManager().saveUniverse(true))
                    JOptionPane.showMessageDialog(null, " World data saved", "Success", JOptionPane.INFORMATION_MESSAGE);
                else
                    JOptionPane.showMessageDialog(null, " Failed to save world data.", "INFORMATION", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(bSave, BorderLayout.EAST);
    }

    /*------------------------------------------------------------------------------------*/

    /** A tree composed of the buildings of the game.
     */
    public class BuildingTree extends WorldTree {

        /** Constructor.
         */
        public BuildingTree(WorldManager wManager) {
            super(wManager, WorldTree.BUILDING_LEVEL_ONLY);
        }

        /** Method called when an element is clicked.
         *  Extend this method to provide here the process you want on the selected object.
         *  Note that the 'selected' is either a WorldMap, TownMap, Building, InteriorMap or Room.
         *
         *  By default here we don't do anything.
         *
         * @param selected object in the hierarchy.  You can cast it into a WorldMap, etc...
         */
        @Override
        public void elementDoubleClicked(Object selectedObject) {
            if (!(selectedObject instanceof Building))
                return;

            String newID = JOptionPane.showInputDialog("Enter a new serverID for this building:");

            if (newID == null || newID.length() == 0)
                return;

            try {
                int nID = Integer.parseInt(newID);

                Building b = (Building) selectedObject;
                b.setServerID(nID);
            } catch (Exception e) {
                Debug.signal(Debug.ERROR, this, "ERROR: " + e.getMessage() + "\n  -> ServerID not saved.");
                return;
            }

            ServerMapSetup.this.repaint();
        }
    }

    /*------------------------------------------------------------------------------------*/

}
