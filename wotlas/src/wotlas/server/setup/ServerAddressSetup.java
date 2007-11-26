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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import wotlas.common.ServerConfigManager;
import wotlas.libs.graphics2D.FontFactory;
import wotlas.libs.wizard.JWizard;
import wotlas.libs.wizard.JWizardStep;
import wotlas.libs.wizard.JWizardStepParameters;
import wotlas.libs.wizard.WizardException;
import wotlas.server.ServerDirector;
import wotlas.utils.Debug;

/** This a utility to update the server address.
 *
 * @author Aldiss
 */

public class ServerAddressSetup extends JWizard {

    /*------------------------------------------------------------------------------------*/

    /** Our serverID
     */
    private static int serverID;

    /** Server Config Address file path.
     */
    private static String serverAddressFile;

    /** User Password
     */
    private static String password = "";

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public ServerAddressSetup() {
        super("Server Address Setup", ServerDirector.getResourceManager(), FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter Regular").deriveFont(18f), 470, 550);

        setLocation(200, 100);
        ServerAddressSetup.serverID = ServerDirector.getServerID();

        ServerAddressSetup.serverAddressFile = ServerDirector.getResourceManager().getExternalServerConfigsDir() + ServerConfigManager.SERVERS_PREFIX + ServerAddressSetup.serverID + ServerConfigManager.SERVERS_SUFFIX + ServerConfigManager.SERVERS_ADDRESS_SUFFIX;

        if (ServerAddressSetup.serverID == 0)
            JOptionPane.showMessageDialog(null, "Your server ID is 0 ('localhost'). This setup program is only" + "\nfor servers that need to publish their IP on the Internet.", "Warning", JOptionPane.WARNING_MESSAGE);

        // We display first step
        try {
            init(AddressWizardStep.getStaticParameters());
        } catch (WizardException we) {
            we.printStackTrace();
            dispose(); // init failed !
            ServerAdminGUI.setAdminGUI();
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** Called when wizard is finished (after last step's end).
     */
    @Override
    protected void onFinished(Object context) {
        dispose();
        ServerAdminGUI.setAdminGUI();
    }

    /*------------------------------------------------------------------------------------*/

    /** Called when wizard is canceled ('cancel' button pressed).
     */
    @Override
    protected void onCanceled(Object context) {
        dispose();
        ServerAdminGUI.setAdminGUI();
    }

    /*------------------------------------------------------------------------------------*/

    /**
     * First Step of our JWizard. Address config.
     */

    public static class AddressWizardStep extends JWizardStep {

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** Swing Fields
         */
        private JTextField t_ipAddress;
        private JTextField t_login;
        private JPasswordField t_passw;
        private JComboBox c_prog, c_options, c_workdir;

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** This is a static JWizardStep, to build it more simply this method
         *  returns the JWizardStepParameters needed for the JWizard.
         */
        public static JWizardStepParameters getStaticParameters() {
            JWizardStepParameters param = new JWizardStepParameters("wotlas.server.setup.ServerAddressSetup$AddressWizardStep", "Server Address Setup");

            param.setIsPrevButtonEnabled(false);
            param.setIsDynamic(true); // we don't want the step to be buffered, we want its data
            return param; // to be updated each time the step is displayed
        }

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** Constructor.
         */
        public AddressWizardStep() {
            super();

            // JPanel inits
            JPanel stepPanel = new JPanel(new GridLayout(8, 1, 5, 5));
            stepPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            stepPanel.setBackground(Color.white);
            stepPanel.setPreferredSize(new Dimension(430, 630));

            setAlignmentX(Component.LEFT_ALIGNMENT);
            JScrollPane scroll = new JScrollPane(stepPanel);
            scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            scroll.setPreferredSize(new Dimension(450, 430));
            add(scroll);

            // Info on this Step
            JPanel group0 = new JPanel(new GridLayout(1, 1, 0, 0));
            group0.setAlignmentX(Component.LEFT_ALIGNMENT);
            group0.setBackground(Color.white);

            JTextArea text0 = new JTextArea("\n    Welcome to your Server Address Setup." + " This is where you declare and export your server's" + " IP address (or DNS name). Make sure the information below is correct" + " and move on to the next step. Your server ID is " + ServerAddressSetup.serverID + ".");
            text0.setLineWrap(true);
            text0.setWrapStyleWord(true);
            text0.setEditable(false);
            text0.setAlignmentX(Component.LEFT_ALIGNMENT);

            group0.add(text0);
            stepPanel.add(group0);

            // IP Address Combo Box
            JPanel group1 = new JPanel(new GridLayout(3, 1, 0, 0));
            group1.setAlignmentX(Component.LEFT_ALIGNMENT);
            group1.setBackground(Color.white);

            JLabel label1 = new JLabel("Select/Enter your Internet address or DNS name (do not enter both) :");
            label1.setAlignmentX(Component.LEFT_ALIGNMENT);

            String lastIP = ServerDirector.getResourceManager().loadText(ServerAddressSetup.serverAddressFile);

            if (lastIP == null)
                lastIP = "0.0.0.0";

            this.t_ipAddress = new JTextField(lastIP);
            this.t_ipAddress.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel label1bis = new JLabel(" ");
            label1bis.setAlignmentX(Component.LEFT_ALIGNMENT);

            group1.add(label1);
            group1.add(this.t_ipAddress);
            group1.add(label1bis);
            stepPanel.add(group1);

            // File Transfer Info
            JTextArea text2 = new JTextArea("\n      We need to know how to transfer your server's IP" + " address to the wotlas central web server : " + ServerDirector.getRemoteServersProperties().getProperty("info.remoteServerHomeURL"));

            text2.setAlignmentX(Component.LEFT_ALIGNMENT);
            text2.setLineWrap(true);
            text2.setWrapStyleWord(true);
            text2.setEditable(false);
            stepPanel.add(text2);

            // Login
            JPanel group2 = new JPanel(new GridLayout(2, 1, 0, 0));
            group2.setAlignmentX(Component.LEFT_ALIGNMENT);
            group2.setBackground(Color.white);

            JLabel label2 = new JLabel("Enter your login :");
            label2.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.t_login = new JTextField(ServerDirector.getRemoteServersProperties().getProperty("transfer.serverHomeLogin", ""));
            this.t_login.setAlignmentX(Component.LEFT_ALIGNMENT);

            group2.add(label2);
            group2.add(this.t_login);
            stepPanel.add(group2);

            // Password
            JPanel group3 = new JPanel(new GridLayout(2, 1, 0, 0));
            group3.setAlignmentX(Component.LEFT_ALIGNMENT);
            group3.setBackground(Color.white);

            JLabel label3 = new JLabel("Enter your password :");
            label3.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.t_passw = new JPasswordField(ServerAddressSetup.password);
            this.t_passw.setAlignmentX(Component.LEFT_ALIGNMENT);

            group3.add(label3);
            group3.add(this.t_passw);
            stepPanel.add(group3);

            // File Tranfer Program
            JPanel group4 = new JPanel(new GridLayout(2, 1, 0, 0));
            group4.setAlignmentX(Component.LEFT_ALIGNMENT);
            group4.setBackground(Color.white);

            JLabel label4 = new JLabel("Enter the program to use for file transfer :");
            label4.setAlignmentX(Component.LEFT_ALIGNMENT);

            String cmdProgList[] = { ServerDirector.getRemoteServersProperties().getProperty("transfer.fileTransferProgram", ""), "../bin/win32/pscp.exe", "\"../bin/win32/pscp.exe\"   (for win2000 & XP, don't remove the \" \" )", "scp", "ftp", };

            this.c_prog = new JComboBox(cmdProgList);
            this.c_prog.setEditable(true);
            this.c_prog.setAlignmentX(Component.LEFT_ALIGNMENT);

            group4.add(label4);
            group4.add(this.c_prog);
            stepPanel.add(group4);

            // File Tranfer Options
            JPanel group5 = new JPanel(new GridLayout(2, 1, 0, 0));
            group5.setAlignmentX(Component.LEFT_ALIGNMENT);
            group5.setBackground(Color.white);

            JTextArea text5 = new JTextArea("Enter the program's command line options. " + "It should contain $FILE$, $LOGIN$ and $PASSW$ tags " + "that we'll replace by their value.");
            text5.setAlignmentX(Component.LEFT_ALIGNMENT);

            text5.setLineWrap(true);
            text5.setWrapStyleWord(true);
            text5.setEditable(false);

            String cmdOptList[] = { ServerDirector.getRemoteServersProperties().getProperty("tranfer.fileTransferOptions", ""), "-pw $PASSW$ $FILE$ $LOGIN$@shell.sf.net:/home/groups/w/wo/wotlas/htdocs/game", };

            this.c_options = new JComboBox(cmdOptList);
            this.c_options.setEditable(true);
            this.c_options.setAlignmentX(Component.LEFT_ALIGNMENT);
            group5.add(text5);
            group5.add(this.c_options);
            stepPanel.add(group5);

            // File Tranfer Working Directory :
            JPanel group6 = new JPanel(new GridLayout(2, 1, 0, 0));
            group6.setAlignmentX(Component.LEFT_ALIGNMENT);
            group6.setBackground(Color.white);

            JLabel text6 = new JLabel("Enter your command line's working directory : ");
            text6.setAlignmentX(Component.LEFT_ALIGNMENT);

            String cmdWorkDirList[] = { ServerDirector.getRemoteServersProperties().getProperty("transfer.fileTransferWorkingDir", ""), "../bin/win32", "../bin/unix" };

            this.c_workdir = new JComboBox(cmdWorkDirList);
            this.c_workdir.setEditable(true);
            this.c_workdir.setAlignmentX(Component.LEFT_ALIGNMENT);
            group6.add(text6);
            group6.add(this.c_workdir);
            stepPanel.add(group6);
        }

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** Called each time the step is shown on screen.
         */
        @Override
        protected void onShow(Object context, JWizard wizard) {
        }

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** Called when the "Next" button is clicked.
         *  Use the wizard's setNextStep() method to set the next step to be displayed.
         *  @return return true to validate the "Next" button action, false to cancel it...
         */
        @Override
        protected boolean onNext(Object context, JWizard wizard) {

            int value = JOptionPane.showConfirmDialog(null, "Save this config ? (required for transfer)", "Server Address Config", JOptionPane.YES_NO_OPTION);

            if (value != JOptionPane.YES_OPTION) {
                wizard.setNextStep(TransferWizardStep.getStaticParameters());
                return true;
            }

            ServerAddressSetup.password = new String(this.t_passw.getPassword());

            // 1 - we retrieve the data and save it to disk.
            ServerDirector.getRemoteServersProperties().setProperty("transfer.serverHomeLogin", this.t_login.getText());
            ServerDirector.getRemoteServersProperties().setProperty("transfer.fileTransferProgram", this.c_prog.getSelectedItem().toString());
            ServerDirector.getRemoteServersProperties().setProperty("tranfer.fileTransferOptions", this.c_options.getSelectedItem().toString());
            ServerDirector.getRemoteServersProperties().setProperty("transfer.fileTransferWorkingDir", this.c_workdir.getSelectedItem().toString());

            if (!ServerDirector.getResourceManager().saveText(ServerAddressSetup.serverAddressFile, this.t_ipAddress.getText())) {
                JOptionPane.showMessageDialog(null, "Failed to save IP address to\n" + ServerAddressSetup.serverAddressFile, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // 2 - we move on to the next step
            wizard.setNextStep(TransferWizardStep.getStaticParameters());
            return true;
        }

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** Called when Previous button is clicked.
         *  Use the wizard's setNextStep() method to set the next step to be displayed.
         *  @return return true to validate the "Previous" button action, false to cancel it...
         */
        @Override
        protected boolean onPrevious(Object context, JWizard wizard) {
            return false; // should never been reached
        }

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

    }

    /*------------------------------------------------------------------------------------*/

    public static class TransferWizardStep extends JWizardStep {
        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** Button To launch transfer
         */
        private JButton b_transfer;

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** This is a static JWizardStep, to build it more simply this method
         *  returns the JWizardStepParameters needed for the JWizard.
         */
        public static JWizardStepParameters getStaticParameters() {
            JWizardStepParameters param = new JWizardStepParameters("wotlas.server.setup.ServerAddressSetup$TransferWizardStep", "Server Config Transfer");

            param.setIsLastStep(true);
            return param;
        }

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** Constructor.
         */
        public TransferWizardStep() {
            super();

            JPanel group1 = new JPanel(new BorderLayout());
            group1.setAlignmentX(Component.LEFT_ALIGNMENT);
            group1.setBackground(Color.white);
            JTextArea taInfo = new JTextArea("\n\n\n      We will now run the command line you entered in the previous step. " + "It will send your 'server-" + ServerAddressSetup.serverID + ".cfg.adr' file to the " + "web server hosting the following URL : " + ServerDirector.getRemoteServersProperties().getProperty("info.remoteServerHomeURL") + "\n\n      Your '.adr' file will be available at this URL. " + "This way other servers/client will be able to discover your server and connect to it.\n\n" + "If the transfer doesn't work take a look at server-side questions in our FAQ.\n\n");

            taInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
            taInfo.setLineWrap(true);
            taInfo.setWrapStyleWord(true);
            taInfo.setEditable(false);
            taInfo.setBackground(Color.white);
            group1.add(taInfo, BorderLayout.NORTH);

            this.b_transfer = new JButton("Click here to launch file tranfer !");
            this.b_transfer.setPreferredSize(new Dimension(250, 30));
            group1.add(this.b_transfer, BorderLayout.SOUTH);
            add(group1);

            this.b_transfer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // We get the full program path
                    StringBuffer fullCmd = new StringBuffer("");
                    boolean wrapFilePath = false; // need to wrap file path between " " ? (winXP needs it)

                    String prog = ServerDirector.getRemoteServersProperties().getProperty("transfer.fileTransferProgram", "");

                    if (prog.startsWith("\"") && prog.endsWith("\"")) {
                        wrapFilePath = true;
                        fullCmd.append("\"");
                        prog = prog.substring(1, prog.length() - 1);
                    }

                    try {
                        fullCmd.append(new File(prog).getCanonicalPath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        fullCmd.append(prog);
                    }

                    if (wrapFilePath)
                        fullCmd.append("\" ");
                    else
                        fullCmd.append(" "); // separator between program name & options

                    // We check the options command
                    String cmd = ServerDirector.getRemoteServersProperties().getProperty("tranfer.fileTransferOptions", "");
                    cmd += " "; // makes our job easier

                    if (cmd.indexOf("$FILE$") < 0) {
                        JOptionPane.showMessageDialog(null, "No $FILE$ tag found in command line.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (cmd.indexOf("$LOGIN$") < 0) {
                        JOptionPane.showMessageDialog(null, "No $LOGIN$ tag found in command line.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (cmd.indexOf("$PASSW$") < 0) {
                        JOptionPane.showMessageDialog(null, "No $PASSW$ tag found in command line.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // We complete the options command
                    int ind1 = cmd.indexOf("$FILE$");

                    fullCmd.append(cmd.substring(0, ind1));

                    if (wrapFilePath)
                        fullCmd.append("\""); // beginning " wrapper

                    try {
                        fullCmd.append(new File(ServerAddressSetup.serverAddressFile).getCanonicalPath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        fullCmd.append(new File(ServerAddressSetup.serverAddressFile).getAbsolutePath());
                    }

                    if (wrapFilePath)
                        fullCmd.append("\""); // ending " wrapper

                    fullCmd.append(cmd.substring(ind1 + 6, cmd.length()));
                    cmd = fullCmd.toString();
                    fullCmd = new StringBuffer("");

                    ind1 = cmd.indexOf("$LOGIN$");

                    fullCmd.append(cmd.substring(0, ind1));
                    fullCmd.append(ServerDirector.getRemoteServersProperties().getProperty("transfer.serverHomeLogin", ""));
                    fullCmd.append(cmd.substring(ind1 + 7, cmd.length()));
                    cmd = fullCmd.toString();
                    fullCmd = new StringBuffer("");

                    ind1 = cmd.indexOf("$PASSW$");

                    fullCmd.append(cmd.substring(0, ind1));
                    fullCmd.append(ServerAddressSetup.password);
                    fullCmd.append(cmd.substring(ind1 + 7, cmd.length()));

                    // Runtime... we execute the transfert command
                    int result = 1;
                    String workingDir = ServerDirector.getRemoteServersProperties().getProperty("transfer.fileTransferWorkingDir");
                    File workingDirPath = null;

                    if (workingDir.length() != 0)
                        workingDirPath = new File(workingDir);

                    Debug.signal(Debug.NOTICE, null, "Command run :\n" + cmd);
                    System.out.println("\nThis is the command we are trying to run (the $PASSW$ has been replaced by your password) :\n\n");
                    System.out.println("" + cmd);

                    try {
                        Process pr = Runtime.getRuntime().exec(fullCmd.toString(), null, workingDirPath);
                        result = pr.waitFor();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Command Line Failed :\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (result == 0)
                        JOptionPane.showMessageDialog(null, "Transfer done ! You can\nnow start your server!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(null, "Transfer has failed. Please check (1) if the destination\n" + "web server is running, (2) if you have setup your firewall\n" + "properly, (3) if the command line is correct, (4) for pscp\n" + "users the encryption key of your target web server may have\n" + "changed, try to use pscp in a shell window to connect to\n" + "your web server, (5) ask the wotlas manager to check your\n" + "rights on the web server.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** Called each time the step is shown on screen.
         */
        @Override
        protected void onShow(Object context, JWizard wizard) {
        }

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** Called when the "Next" button is clicked.
         *  Use the wizard's setNextStep() method to set the next step to be displayed.
         *  @return return true to validate the "Next" button action, false to cancel it...
         */
        @Override
        protected boolean onNext(Object context, JWizard wizard) {
            return true;
        }

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

        /** Called when Previous button is clicked.
         *  Use the wizard's setNextStep() method to set the next step to be displayed.
         *  @return return true to validate the "Previous" button action, false to cancel it...
         */
        @Override
        protected boolean onPrevious(Object context, JWizard wizard) {
            // we return to the previous step
            wizard.setNextStep(AddressWizardStep.getStaticParameters());
            return true;
        }

        /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

    }

    /*------------------------------------------------------------------------------------*/

}
