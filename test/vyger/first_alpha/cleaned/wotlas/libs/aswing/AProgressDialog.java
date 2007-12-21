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

package wotlas.libs.aswing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/** A small utility to display a progress bar in a JDialog
 *
 * @author Aldiss
 */

public class AProgressDialog extends JDialog {

    /*------------------------------------------------------------------------------------*/

    /** Our label ...
     */
    protected ALabel label;

    /** Our progress bar.
     */
    protected JProgressBar progressBar;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     * 
     * @param frame frame owner of this JDialog
     * @param message msg to display
     * @param modal if the dialog is modal or not
     */
    public AProgressDialog(Frame frame, String title) {
        super(frame, title, false);

        // some inits
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.white);

        // Top Label
        this.label = new ALabel("  ", SwingConstants.CENTER);
        getContentPane().add(this.label, BorderLayout.CENTER);
        this.label.setPreferredSize(new Dimension(300, 30));
        this.label.setMinimumSize(new Dimension(300, 30));

        // Progress Bar
        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setValue(0);
        this.progressBar.setStringPainted(true);
        getContentPane().add(this.progressBar, BorderLayout.SOUTH);
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) ((screenSize.getWidth() - getWidth()) / 2), (int) ((screenSize.getHeight() - getHeight()) / 2));
        show();
    }

    /*------------------------------------------------------------------------------------*/

    /** String message for the progress monitor...
     */
    public void setNote(String note) {
        this.label.setText(note);
        AProgressDialog.this.repaint();
    }

    /*-------------------------------------------------------------------------------*/

    /** Value for the progress monitor, ranges from one to 100.
     */
    public void setProgress(final int value) {
        this.progressBar.setValue(value);
        AProgressDialog.this.repaint();
    }

    /*-------------------------------------------------------------------------------*/

}