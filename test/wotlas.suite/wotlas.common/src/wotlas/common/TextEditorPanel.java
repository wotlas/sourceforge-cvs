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

package wotlas.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import wotlas.libs.aswing.ALabel;
import wotlas.libs.aswing.ATextArea;
import wotlas.utils.FileTools;

/** To display a text file in a JPanel with Save & Load buttons.
 *
 * @author Aldiss
 */

public class TextEditorPanel extends JPanel {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Our File Path.
     */
    protected String textFilePath;

    /** Original text, not modified...
     */
    protected String originalText;

    /** ATextField
     */
    protected ATextArea a_text;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with full file path.
     * @param rManager resource manager to get the images
     * @param textFilePath text file path
     * @param title title
     * @param canSave can we modify/save the file ?
     */
    public TextEditorPanel(ResourceManager rManager, String textFilePath, String title, boolean canSave) {
        super();
        this.textFilePath = textFilePath;

        // Components
        setLayout(new BorderLayout());
        setBackground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

        ALabel atitle = new ALabel(title);
        atitle.setBackground(Color.white);
        add(atitle, BorderLayout.NORTH);
        atitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        this.a_text = new ATextArea();
        this.a_text.setLineWrap(true);
        this.a_text.setWrapStyleWord(true);
        this.a_text.setEditable(canSave);

        if (canSave)
            this.a_text.setBackground(new Color(235, 235, 235));
        else
            this.a_text.setBackground(new Color(215, 215, 215));

        JScrollPane scroller = new JScrollPane(this.a_text);
        scroller.setPreferredSize(new Dimension(450, 280));
        add(scroller, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        if (canSave)
            buttonPanel.setLayout(new GridLayout(1, 2, 10, 10));
        else
            buttonPanel.setLayout(new GridLayout(1, 1, 10, 10));

        buttonPanel.setBackground(Color.white);

        ImageIcon im_loadup = rManager.getImageIcon("load-up.gif");
        ImageIcon im_loaddo = rManager.getImageIcon("load-do.gif");

        JButton bLoad = new JButton(im_loadup);
        bLoad.setRolloverIcon(im_loaddo);
        bLoad.setPressedIcon(im_loaddo);
        bLoad.setBorderPainted(false);
        bLoad.setContentAreaFilled(false);
        bLoad.setFocusPainted(false);
        buttonPanel.add(bLoad);

        bLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });

        if (canSave) {
            ImageIcon im_saveup = rManager.getImageIcon("save-up.gif");
            ImageIcon im_savedo = rManager.getImageIcon("save-do.gif");

            JButton bSave = new JButton(im_saveup);
            bSave.setRolloverIcon(im_savedo);
            bSave.setPressedIcon(im_savedo);
            bSave.setBorderPainted(false);
            bSave.setContentAreaFilled(false);
            bSave.setFocusPainted(false);

            bSave.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    save();
                }
            });

            buttonPanel.add(bSave);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        // We init the content
        load();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To load the text
     */
    public boolean load() {
        String text = FileTools.loadTextFromFile(this.textFilePath);

        if (text == null) {
            JOptionPane.showMessageDialog(null, "Failed to load " + this.textFilePath, "Error", JOptionPane.ERROR_MESSAGE);

            if (this.originalText == null) {
                text = "[Error: failed To load file : " + this.textFilePath + "]";
                this.a_text.setText(text);
            }

            this.a_text.setEnabled(false);
            return false;
        } else {
            if (this.originalText != null && !this.originalText.equals(this.a_text.getText())) {
                int value = JOptionPane.showConfirmDialog(null, "The current text will be erased.\nDo you want to continue ?", "Load", JOptionPane.YES_NO_OPTION);

                if (value != JOptionPane.YES_OPTION)
                    return false;
            }

            this.originalText = text;
            this.a_text.setText(text);
            this.a_text.setEnabled(true);
            return true;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To save the text
     */
    public boolean save() {
        if (!this.a_text.isEnabled()) {
            JOptionPane.showMessageDialog(null, "Save not allowed.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean res = FileTools.saveTextToFile(this.textFilePath, this.a_text.getText());

        if (res)
            JOptionPane.showMessageDialog(null, "File saved", "Success", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(null, "Failed to save file", "Error", JOptionPane.ERROR_MESSAGE);

        return res;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the current text displayed in this editor.
     */
    public String getText() {
        if (!this.a_text.isEnabled())
            return null;

        return this.a_text.getText();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the current text displayed in this editor.
     */
    public void setText(String text) {
        this.originalText = text;
        this.a_text.setText(text);
        this.a_text.setEnabled(true);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
