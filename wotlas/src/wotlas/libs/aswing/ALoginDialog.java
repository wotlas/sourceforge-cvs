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
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** A small utility to display a dialog asking for login+password
 *
 * @author Aldiss
 */

public class ALoginDialog extends JDialog {
    /*------------------------------------------------------------------------------------*/

    /** Textfields for login & Password
     */
    private ATextField f_login;
    private APasswordField f_password;

    /** Results of the dialog
     */
    private String login;
    private String password;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     * 
     * @param frame frame owner of this JDialog
     * @param message msg to display
     * @param modal if the dialog is modal or not
     * @param rLocator to locate resources
     */
    public ALoginDialog(Frame frame, String message, String defaultLogin, ASwingResourceLocator rLocator) {
        super(frame, "Login", true);

        // some inits
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.white);

        // Top Label
        ALabel label1 = new ALabel(message);
        label1.setBackground(Color.white);
        getContentPane().add(label1, BorderLayout.NORTH);

        // Fields Panel
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setBackground(Color.white);
        fieldsPanel.setLayout(new GridLayout(2, 2, 5, 5));
        getContentPane().add(fieldsPanel, BorderLayout.CENTER);

        // Fields
        ALabel l1 = new ALabel("Login :");
        l1.setBackground(Color.white);
        fieldsPanel.add(l1);

        this.f_login = new ATextField(defaultLogin);
        this.f_login.setBackground(new Color(235, 235, 235));
        fieldsPanel.add(this.f_login);

        ALabel l2 = new ALabel("Password :");
        l2.setBackground(Color.white);
        fieldsPanel.add(l2);

        this.f_password = new APasswordField(10);
        this.f_password.setBackground(new Color(235, 235, 235));
        fieldsPanel.add(this.f_password);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.white);
        buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Ok Button
        ImageIcon im_okup = rLocator.getImageIcon("ok-up.gif");
        ImageIcon im_okdo = rLocator.getImageIcon("ok-do.gif");

        JButton b_ok = new JButton(im_okup);
        b_ok.setRolloverIcon(im_okdo);
        b_ok.setPressedIcon(im_okdo);
        b_ok.setDisabledIcon(im_okup);
        b_ok.setBorderPainted(false);
        b_ok.setContentAreaFilled(false);
        b_ok.setFocusPainted(false);
        buttonPanel.add(b_ok);

        b_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ALoginDialog.this.login = ALoginDialog.this.f_login.getText();
                ALoginDialog.this.password = new String(ALoginDialog.this.f_password.getPassword());

                if (ALoginDialog.this.login.length() == 0) {
                    JOptionPane.showMessageDialog(ALoginDialog.this, "Login not set !", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (ALoginDialog.this.password.length() == 0) {
                    JOptionPane.showMessageDialog(ALoginDialog.this, "Password not set !", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                dispose();
            }
        });

        ImageIcon im_cancelup = rLocator.getImageIcon("cancel-up.gif");
        ImageIcon im_canceldo = rLocator.getImageIcon("cancel-do.gif");

        JButton b_cancel = new JButton(im_cancelup);
        b_cancel.setRolloverIcon(im_canceldo);
        b_cancel.setPressedIcon(im_canceldo);
        b_cancel.setDisabledIcon(im_cancelup);
        b_cancel.setBorderPainted(false);
        b_cancel.setContentAreaFilled(false);
        b_cancel.setFocusPainted(false);
        buttonPanel.add(b_cancel);

        b_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Pack & Display
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) ((screenSize.getWidth() - getWidth()) / 2), (int) ((screenSize.getHeight() - getHeight()) / 2));
        show();
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the login entered.
     */
    public String getLogin() {
        return this.login;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the password entered.
     */
    public String getPassword() {
        return this.password;
    }

    /*------------------------------------------------------------------------------------*/

    /** Has the user clicked on 'Ok' ?
     * @return true if OK was clicked
     */
    public boolean okWasClicked() {
        return (this.login != null);
    }

    /*------------------------------------------------------------------------------------*/
}