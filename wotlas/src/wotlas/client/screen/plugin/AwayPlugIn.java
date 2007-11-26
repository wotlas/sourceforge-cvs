/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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

package wotlas.client.screen.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import wotlas.client.ClientDirector;
import wotlas.client.PlayerImpl;
import wotlas.client.screen.JPanelPlugIn;
import wotlas.common.message.description.PlayerAwayMessage;
import wotlas.libs.aswing.AButton;
import wotlas.libs.aswing.ATextArea;

/** Plug In where you can enter some text that will be displayed when you are
 *  not connected to the game.
 *
 * @author MasterBob, Aldiss
 */

public class AwayPlugIn extends JPanelPlugIn {

    /*------------------------------------------------------------------------------------*/

    /** Away Message Text Area
     */
    private ATextArea playerTextArea;

    /** Save buttons.
     */
    private AButton savePastButton;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public AwayPlugIn() {
        super();
        setLayout(new BorderLayout());

        ATextArea ta_infos = new ATextArea("You can enter here a message that will be displayed when you are not connected:");
        ta_infos.setLineWrap(true);
        ta_infos.setWrapStyleWord(true);
        ta_infos.setEditable(false);
        ta_infos.setAlignmentX(0.5f);
        ta_infos.setOpaque(false);
        add(ta_infos, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());

        this.playerTextArea = new ATextArea(10, 25);
        this.playerTextArea.setLineWrap(true);
        this.playerTextArea.setWrapStyleWord(true);
        this.playerTextArea.setEditable(true);
        this.playerTextArea.setAlignmentX(0.5f);
        PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();
        this.playerTextArea.setText(player.getPlayerAwayMessage());
        centerPanel.add(new JScrollPane(this.playerTextArea), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        ImageIcon im_saveup = ClientDirector.getResourceManager().getImageIcon("save-up.gif");
        ImageIcon im_savedo = ClientDirector.getResourceManager().getImageIcon("save-do.gif");

        this.savePastButton = new AButton(im_saveup);
        this.savePastButton.setRolloverIcon(im_savedo);
        this.savePastButton.setPressedIcon(im_savedo);
        this.savePastButton.setBorderPainted(false);
        this.savePastButton.setContentAreaFilled(false);
        this.savePastButton.setFocusPainted(false);
        this.savePastButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.savePastButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlayerImpl p = ClientDirector.getDataManager().getMyPlayer();

                String awayMessage = AwayPlugIn.this.playerTextArea.getText();

                // Any macro to process ?
                MacroPlugIn macroPlugIn = (MacroPlugIn) ClientDirector.getDataManager().getClientScreen().getPlayerPanel().getPlugIn("Macro");

                if (macroPlugIn != null) {
                    awayMessage = macroPlugIn.processMacros(awayMessage);
                    AwayPlugIn.this.playerTextArea.setText(awayMessage);
                }

                if (p.getPlayerAwayMessage() != null && p.getPlayerAwayMessage().equals(awayMessage))
                    return;

                if (awayMessage.length() > 400) {
                    awayMessage = awayMessage.substring(0, 399);
                    AwayPlugIn.this.playerTextArea.setText(awayMessage);
                }

                p.setPlayerAwayMessage(awayMessage);
                p.sendMessage(new PlayerAwayMessage(p.getPrimaryKey(), awayMessage));
            }
        });

        JPanel whitePanel = new JPanel();
        whitePanel.setBackground(Color.white);
        whitePanel.add(this.savePastButton);
        add(whitePanel, BorderLayout.SOUTH);
    }

    /*------------------------------------------------------------------------------------*/

    /** Called once to initialize the plug-in.
     *
     *  @return if true we display the plug-in, return false if something fails during
     *          this init(), this way the plug-in won't be displayed.
     */
    @Override
    public boolean init() {
        return true; // nothing special to init...
    }

    /*------------------------------------------------------------------------------------*/

    /** Called when we need to reset the content of this plug-in.
     */
    @Override
    public void reset() {
        PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();
        this.playerTextArea.setText(player.getPlayerAwayMessage());
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
     * @return a short name for the plug-in
     */
    @Override
    public String getPlugInName() {
        return "Away";
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in's author.
     * @return author name.
     */
    @Override
    public String getPlugInAuthor() {
        return "Wotlas Team (MasterBob,Aldiss)";
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the tool tip text that will be displayed in the JPlayerPanel.
     * @return a short tool tip text
     */
    @Override
    public String getToolTipText() {
        return "Message Displayed when Away";
    }

    /*------------------------------------------------------------------------------------*/

    /** Eventual index in the list of JPlayerPanels
     * @return -1 if the plug-in has to be added at the end of the plug-in list,
     *         otherwise a positive integer for a precise location.
     */
    @Override
    public int getPlugInIndex() {
        return 1;
    }

    /*------------------------------------------------------------------------------------*/

    /** Tells if this plug-in is a system plug-in that represents some base
     *  wotlas feature.
     * @return true means system plug-in, false means user plug-in
     */
    @Override
    public boolean isSystemPlugIn() {
        return true;
    }

    /*------------------------------------------------------------------------------------*/
}
