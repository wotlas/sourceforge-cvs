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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import wotlas.client.ClientDirector;
import wotlas.client.PlayerImpl;
import wotlas.client.screen.JPanelPlugIn;
import wotlas.common.message.description.ChangeFakeNameMessage;
import wotlas.common.message.description.CreateFakeNameMessage;
import wotlas.libs.aswing.AButton;
import wotlas.libs.aswing.ALabel;
import wotlas.libs.aswing.ARadioButton;
import wotlas.libs.aswing.ATextArea;
import wotlas.libs.aswing.ATextField;

/** Plug In where you can create fake names for your character and use them.
 *
 * @author Petrus
 */

public class LiePlugIn extends JPanelPlugIn {

    /*------------------------------------------------------------------------------------*/

    /** Maximum number of fake names the player can create.
     */
    private final static int FAKE_NAMES_LENGTH = 5;

    /*------------------------------------------------------------------------------------*/

    /** 'New' button.
     */
    private AButton newNameButton;

    /** Radio buttons for the fake names
     */
    private ARadioButton[] b_fakeNames;

    /** 'New' button panel
     */
    private JPanel whitePanel;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public LiePlugIn() {
        super();
        setLayout(new BorderLayout());

        ATextArea ta_infos = new ATextArea("You can lie on your name: you can create and use up to 5 fake names... but be careful, once you have created a fake name, you cannot change it.");
        ta_infos.setLineWrap(true);
        ta_infos.setWrapStyleWord(true);
        ta_infos.setEditable(false);
        ta_infos.setAlignmentX(0.5f);
        ta_infos.setOpaque(false);
        add(ta_infos, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(LiePlugIn.FAKE_NAMES_LENGTH, 1));
        this.b_fakeNames = new ARadioButton[LiePlugIn.FAKE_NAMES_LENGTH];
        ButtonGroup g_fakeNames = new ButtonGroup();
        RadioListener myListener = new RadioListener();

        for (int i = 0; i < LiePlugIn.FAKE_NAMES_LENGTH; i++) {
            this.b_fakeNames[i] = new ARadioButton();
            this.b_fakeNames[i].setVisible(false);
            this.b_fakeNames[i].setActionCommand("");
            this.b_fakeNames[i].addActionListener(myListener);
            g_fakeNames.add(this.b_fakeNames[i]);
            form.add(this.b_fakeNames[i]);
        }

        centerPanel.add(form);
        add(centerPanel, BorderLayout.CENTER);

        JPanel form2 = new JPanel(new GridLayout(2, 1));
        form2.setBackground(Color.white);
        ALabel a_infoFakeName = new ALabel("Enter a new fake name:");
        form2.add(a_infoFakeName);
        final ATextField a_newFakeName = new ATextField();
        form2.add(a_newFakeName);

        ImageIcon im_newup = ClientDirector.getResourceManager().getImageIcon("new-up.gif");
        ImageIcon im_newdo = ClientDirector.getResourceManager().getImageIcon("new-do.gif");

        this.newNameButton = new AButton(im_newup);
        this.newNameButton.setRolloverIcon(im_newdo);
        this.newNameButton.setPressedIcon(im_newdo);
        this.newNameButton.setBorderPainted(false);
        this.newNameButton.setContentAreaFilled(false);
        this.newNameButton.setFocusPainted(false);

        this.newNameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int size = a_newFakeName.getText().trim().length();

                if (size < 5) {
                    JOptionPane.showMessageDialog(null, "Your fake name should have more than 4 letters !", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (size > 30) {
                    JOptionPane.showMessageDialog(null, "Your fake name should have less than 30 letters !", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();
                player.sendMessage(new CreateFakeNameMessage(a_newFakeName.getText().trim()));
                a_newFakeName.setText("");
            }
        });

        this.whitePanel = new JPanel(new BorderLayout());
        this.whitePanel.add(form2, BorderLayout.NORTH);
        this.whitePanel.add(this.newNameButton, BorderLayout.CENTER);
        this.whitePanel.setBackground(Color.white);
        add(this.whitePanel, BorderLayout.SOUTH);
    }

    /*------------------------------------------------------------------------------------*/

    /** To set a fake name.
     */
    public void setFakeName(int index, String fakeName) {
        if (fakeName.length() == 0)
            return;

        this.b_fakeNames[index].setText(fakeName);
        this.b_fakeNames[index].setActionCommand("" + index);
        this.b_fakeNames[index].setVisible(true);

        // if player has filled all his fake names,
        // we hide the form to create a new fake name
        if (index == (LiePlugIn.FAKE_NAMES_LENGTH - 1))
            this.whitePanel.setVisible(false);
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the current fake name.
     * @param index index of the current fake name
     */
    public void setCurrentFakeName(int index) {
        this.b_fakeNames[index].setSelected(true);
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
        for (int i = 0; i < LiePlugIn.FAKE_NAMES_LENGTH; i++) {
            this.b_fakeNames[i].setVisible(false);
            this.b_fakeNames[i].setActionCommand("");
        }

        this.whitePanel.setVisible(true);
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
     * @return a short name for the plug-in
     */
    @Override
    public String getPlugInName() {
        return "Lie";
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in's author.
     * @return author name.
     */
    @Override
    public String getPlugInAuthor() {
        return "Wotlas Team (Petrus)";
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the tool tip text that will be displayed in the JPlayerPanel.
     * @return a short tool tip text
     */
    @Override
    public String getToolTipText() {
        return "To Lie on your Name";
    }

    /*------------------------------------------------------------------------------------*/

    /** Eventual index in the list of JPlayerPanels
     * @return -1 if the plug-in has to be added at the end of the plug-in list,
     *         otherwise a positive integer for a precise location.
     */
    @Override
    public int getPlugInIndex() {
        return 2;
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

    /** Listen to the radio button selection.
     */
    class RadioListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int currentFakeName = Integer.parseInt(e.getActionCommand());

            PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();

            player.sendMessage(new ChangeFakeNameMessage((short) currentFakeName));
            player.setFullPlayerName(LiePlugIn.this.b_fakeNames[currentFakeName].getText());

            ClientDirector.getDataManager().getClientScreen().getChatPanel().getCurrentJChatRoom().updatePlayer(player.getPrimaryKey(), LiePlugIn.this.b_fakeNames[currentFakeName].getText());
        }
    }

    /*------------------------------------------------------------------------------------*/
}
