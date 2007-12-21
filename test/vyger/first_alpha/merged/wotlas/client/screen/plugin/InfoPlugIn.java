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
import javax.swing.SwingConstants;
import wotlas.client.ClientDirector;
import wotlas.client.PlayerImpl;
import wotlas.client.screen.JPanelPlugIn;
import wotlas.common.Player;
import wotlas.common.environment.EnvironmentManager;
import wotlas.common.message.description.PlayerPastMessage;
import wotlas.libs.aswing.AButton;
import wotlas.libs.aswing.ALabel;
import wotlas.libs.aswing.ATextArea;

/** Plug In that shows information on the selected player and enables the
 *  local player to set his/her past.
 *
 * @author MasterBob, Diego
 */

public class InfoPlugIn extends JPanelPlugIn {

    /*------------------------------------------------------------------------------------*/

    /** Player Name Label
     */
    private ALabel infoPlayerLabel = new ALabel();

    /** Information Text Area
     */
    private ATextArea playerTextArea;

    /** Can we show the 'save' button ?
     */
    private boolean savePastButtonDisplayed = false;

    /** Save Button & its panel
     */
    private AButton savePastButton;
    private JPanel whitePanel;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public InfoPlugIn() {
        super();
        setLayout(new BorderLayout());

        this.infoPlayerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.infoPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        this.playerTextArea = new ATextArea(10, 25);
        this.playerTextArea.setLineWrap(true);
        this.playerTextArea.setWrapStyleWord(true);
        this.playerTextArea.setEditable(false);
        this.playerTextArea.setAlignmentX(0.5f);

        setText("Click on a player...");
        setLabelText("No Player Selected");

        add(this.infoPlayerLabel, BorderLayout.NORTH);
        add(new JScrollPane(this.playerTextArea), BorderLayout.CENTER);
    }

    /*------------------------------------------------------------------------------------*/

    /** Called once to initialize the plug-in.
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

        if (this.savePastButtonDisplayed) {
            this.playerTextArea.setEditable(false);
            remove(this.savePastButton);
            remove(this.whitePanel);
            revalidate();
            this.savePastButton = null;
            this.whitePanel = null;
        }

        this.savePastButtonDisplayed = false;
        setText("Click on a player...");
        setLabelText("No Player Selected");
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the information text.
     */
    protected void setText(String text) {
        this.playerTextArea.setText(text);
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the label text content.
     */
    protected void setLabelText(String text) {
        this.infoPlayerLabel.setText(text);
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the player info given a player.
     */
    public void setPlayerInfo(Player player) {
        if (player.getBasicChar().getEnvironment() == EnvironmentManager.ENVIRONMENT_WOT) {
            setWotPlayerInfo(player);
        } else if (player.getBasicChar().getEnvironment() == EnvironmentManager.ENVIRONMENT_ROGUE_LIKE) {
            setRLikePlayerInfo(player);
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the RogueLike player info given a player.
     */
    public void setRLikePlayerInfo(Player player) {
        if (player == ClientDirector.getDataManager().getMyPlayer()) {

            // Is there a valid past ?
            String past = player.getPlayerPast();

            if (past.length() == 0 && !this.savePastButtonDisplayed) {
                // No past set we display the save button
                this.playerTextArea.setEditable(true);
                this.setLabelText("Your player's past:");
                this.setText("...");
                this.savePastButtonDisplayed = true;

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
                        InfoPlugIn.this.savePastButton.setEnabled(false);
                        PlayerImpl p = ClientDirector.getDataManager().getMyPlayer();
                        p.setPlayerPast(InfoPlugIn.this.playerTextArea.getText());
                        p.sendMessage(new PlayerPastMessage(p.getPrimaryKey(), p.getPlayerPast()));
                        InfoPlugIn.this.reset();
                        setPlayerInfo(p);
                    }
                });

                this.whitePanel = new JPanel();
                this.whitePanel.setBackground(Color.white);
                this.whitePanel.add(this.savePastButton);
                add(this.whitePanel, BorderLayout.SOUTH);
                revalidate();

                return;
            } else if (past.length() == 0)
                return;
        } else if (this.savePastButtonDisplayed)
            reset();

        setLabelText(player.getFullPlayerName(null));

        if (player != ClientDirector.getDataManager().getMyPlayer()) {
            setText("Class: " + player.getBasicChar().getCommunityName() + "\n" + "Player Past: " + player.getPlayerPast());
        } else {
            setText("Nickname: " + player.getPlayerName() + "\n" + "Class: " + player.getBasicChar().getCommunityName() + "\n" + "Player Past: " + player.getPlayerPast());
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the Wot player info given a player.
     */
    public void setWotPlayerInfo(Player player) {
        if (player == ClientDirector.getDataManager().getMyPlayer()) {

            // Is there a valid past ?
            String past = player.getPlayerPast();

            if (past.length() == 0 && !this.savePastButtonDisplayed) {
                // No past set we display the save button
                this.playerTextArea.setEditable(true);
                this.setLabelText("Your player's past:");
                this.setText("...");
                this.savePastButtonDisplayed = true;

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
                        InfoPlugIn.this.savePastButton.setEnabled(false);
                        PlayerImpl p = ClientDirector.getDataManager().getMyPlayer();
                        p.setPlayerPast(InfoPlugIn.this.playerTextArea.getText());
                        p.sendMessage(new PlayerPastMessage(p.getPrimaryKey(), p.getPlayerPast()));
                        InfoPlugIn.this.reset();
                        setPlayerInfo(p);
                    }
                });

                this.whitePanel = new JPanel();
                this.whitePanel.setBackground(Color.white);
                this.whitePanel.add(this.savePastButton);
                add(this.whitePanel, BorderLayout.SOUTH);
                revalidate();

                return;
            } else if (past.length() == 0)
                return;
        } else if (this.savePastButtonDisplayed)
            reset();

        setLabelText(player.getFullPlayerName(null));

        if (player != ClientDirector.getDataManager().getMyPlayer())
            setText("Community: " + player.getBasicChar().getCommunityName() + "\n" + "Rank: " + player.getBasicChar().getCharacterRank() + "\n\n" + "Player Past: " + player.getPlayerPast());
        else
            setText("Nickname: " + player.getPlayerName() + "\n" + "Community: " + player.getBasicChar().getCommunityName() + "\n" + "Rank: " + player.getBasicChar().getCharacterRank() + "\n\n" + "Player Past: " + player.getPlayerPast());
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
     * @return a short name for the plug-in
     */
    @Override
    public String getPlugInName() {
        return "Info";
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in's author.
     * @return author name.
     */
    @Override
    public String getPlugInAuthor() {
        return "Wotlas Team (MasterBob)";
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the tool tip text that will be displayed in the JPlayerPanel.
     * @return a short tool tip text
     */
    @Override
    public String getToolTipText() {
        return "Selected Player Information";
    }

    /*------------------------------------------------------------------------------------*/

    /** Eventual index in the list of JPlayerPanels
     * @return -1 if the plug-in has to be added at the end of the plug-in list,
     *         otherwise a positive integer for a precise location.
     */
    @Override
    public int getPlugInIndex() {
        return 0;
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