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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import wotlas.client.ClientDirector;
import wotlas.client.ClientManager;
import wotlas.client.gui.JHTMLWindow;
import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigListTableModel;
import wotlas.common.ServerConfigManager;
import wotlas.common.message.account.AccountCreationMessage;
import wotlas.common.message.account.CancelAccountCreationMessage;
import wotlas.libs.aswing.ATableCellRenderer;
import wotlas.libs.aswing.ATextArea;
import wotlas.libs.graphics2d.FontFactory;
import wotlas.libs.net.NetConfig;
import wotlas.libs.net.NetConnection;
import wotlas.libs.wizard.JWizard;
import wotlas.libs.wizard.JWizardStep;
import wotlas.libs.wizard.JWizardStepParameters;
import wotlas.libs.wizard.WizardException;
import wotlas.utils.Debug;

/** A wizard to create an account
 *
 * @author Petrus
 */
public class JAccountCreationWizard extends JWizard {

    /*------------------------------------------------------------------------------------*/
    /**  The current account wizard
     */
    static protected JAccountCreationWizard wizard;

    /*------------------------------------------------------------------------------------*/
    /** Called when wizard is finished (after last step's end).
     */
    @Override
    protected void onFinished(Object context) {
        Debug.signal(Debug.NOTICE, this, "Wizard finished.");

        if (context != null) {
            NetConnection connection = (NetConnection) context;
            connection.queueMessage(new CancelAccountCreationMessage());
            connection.close();
        }

        ClientManager clientManager = ClientDirector.getClientManager();
        clientManager.start(ClientManager.ACCOUNT_INFO_SCREEN);
    }

    /*------------------------------------------------------------------------------------*/
    /** Called when wizard is canceled ('cancel' button pressed).
     */
    @Override
    protected void onCanceled(Object context) {
        Debug.signal(Debug.NOTICE, this, "Wizard canceled.");

        if (context != null) {
            NetConnection connection = (NetConnection) context;
            connection.queueMessage(new CancelAccountCreationMessage());
            connection.close();
        }

        ClientManager clientManager = ClientDirector.getClientManager();
        clientManager.start(ClientManager.MAIN_SCREEN);
    }

    /*------------------------------------------------------------------------------------*/
    /** Constructor
     */
    public JAccountCreationWizard() {
        super("Account creation wizard", ClientDirector.getResourceManager(), ClientDirector.getResourceManager().getGameDefinition(), FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter Regular").deriveFont(18f), 460, 460);

        // We display first step
        try {
            init(ServerSelectionStep.getStepParameters());
        } catch (WizardException we) {
            we.printStackTrace();
            Debug.signal(Debug.ERROR, this, "Wizard initialisation failed");
        }

        ((ServerSelectionStep) this.currentStep).setWizard(this);
    }

    /*------------------------------------------------------------------------------------*/
    /**
     * First Step of our JWizard.
     * Choose a server.
     */
    public static class ServerSelectionStep extends JWizardStep {

        /** Our ServerConfigManager file.
         */
        private ServerConfigManager serverConfigManager;
        /** Current serverConfig
         */
        private ServerConfig currentServerConfig;
        /** Our current wizard...
         */
        private JWizard wizard;

        /*------------------------------------------------------------------------------------*/
        /** Creation of the steps paramters
         */
        public static JWizardStepParameters getStepParameters() {
            // Creation of the parameters for the first step
            JWizardStepParameters param = new JWizardStepParameters("wotlas.client.screen.JAccountCreationWizard$ServerSelectionStep", "Server Selection");
            param.setIsNextButtonEnabled(false);
            param.setIsPrevButtonEnabled(false);
            param.setIsDynamic(false); // we want the step to be buffered
            return param;
        }

        /*------------------------------------------------------------------------------------*/
        /** Constructor.
         */
        public ServerSelectionStep() {
            super();

            ClientManager clientManager = ClientDirector.getClientManager();

            // JPanel inits
            setLayout(new BorderLayout());

            setAlignmentX(Component.LEFT_ALIGNMENT);
            setBackground(Color.white);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            // Info on this Step
            JPanel group0 = new JPanel(new GridLayout(1, 1, 0, 0));
            group0.setAlignmentX(Component.LEFT_ALIGNMENT);
            group0.setBackground(Color.white);
            ATextArea text0 = new ATextArea("         Welcome to the Account Creation Wizard." + " Please, choose the server where you want to create your new account." + " If the list below is empty it probably means that the wotlas web server is not running" + " and that you'll have to restart this wizard later.\n");
            text0.setLineWrap(true);
            text0.setWrapStyleWord(true);
            text0.setEditable(false);
            text0.setAlignmentX(Component.LEFT_ALIGNMENT);
            group0.add(text0);
            add(group0, BorderLayout.NORTH);

            // Loading Server Configs
            this.serverConfigManager = clientManager.getServerConfigManager();
            this.serverConfigManager.getLatestConfigFiles(this.wizard);

            ServerConfigListTableModel serverConfigListTabModel = new ServerConfigListTableModel(this.serverConfigManager);
            JTable serversTable = new JTable(serverConfigListTabModel);
            serversTable.setDefaultRenderer(Object.class, new ATableCellRenderer());
            serversTable.setBackground(Color.white);
            serversTable.setForeground(Color.black);
            serversTable.setSelectionBackground(Color.lightGray);
            serversTable.setSelectionForeground(Color.white);
            serversTable.setRowHeight(24);
            serversTable.getColumnModel().getColumn(2).setPreferredWidth(-1);
            // selection
            serversTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            ListSelectionModel rowServerSM = serversTable.getSelectionModel();
            rowServerSM.addListSelectionListener(new ListSelectionListener() {

                private JHTMLWindow htmlDescr;

                public void valueChanged(ListSelectionEvent e) {

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if (lsm.isSelectionEmpty()) {
                        return; //no rows were selected
                    }

                    int selectedRow = lsm.getMinSelectionIndex();

                    //selectedRow is selected
                    ServerSelectionStep.this.currentServerConfig = ServerSelectionStep.this.serverConfigManager.serverConfigAt(selectedRow);

                    if (this.htmlDescr == null) {
                        this.htmlDescr = new JHTMLWindow(ClientDirector.getClientManager(), "Wotlas Server", "text:" + ServerSelectionStep.this.currentServerConfig.toHTML(), 350, 250, false, ClientDirector.getResourceManager());
                    } else {
                        this.htmlDescr.setText(ServerSelectionStep.this.currentServerConfig.toHTML());
                        if (!this.htmlDescr.isShowing()) {
                            this.htmlDescr.show();
                        }
                    }

                    //Ignore extra messages.
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    //selectedRow is selected
                    ServerSelectionStep.this.currentServerConfig = ServerSelectionStep.this.serverConfigManager.serverConfigAt(selectedRow);
                    ServerSelectionStep.this.wizard.setIsNextButtonEnabled(true);
                }
            });

            // show table
            JScrollPane scrollPane = new JScrollPane(serversTable);
            serversTable.setAlignmentX(Component.LEFT_ALIGNMENT);
            //serversTable.setPreferredScrollableViewportSize(new Dimension(0, 200));
            scrollPane.getViewport().setBackground(Color.white);
            //scrollPane.setPreferredSize(new Dimension(100,220));
            scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            scrollPane.setPreferredSize(new Dimension(430, 300));
            add(scrollPane, BorderLayout.CENTER);
        }

        /*------------------------------------------------------------------------------------*/
        /** To set our wizard.
         */
        protected void setWizard(JWizard wizard) {
            this.wizard = wizard;
        }

        /*------------------------------------------------------------------------------------*/
        /** Called each time the step is shown on screen.
         */
        @Override
        protected void onShow(Object context, JWizard wizard) {
        }

        /*------------------------------------------------------------------------------------*/
        /** Called when the "Next" button is clicked.
         *  Use the wizard's setNextStep() method to set the next step to be displayed.
         *  @return return true to validate the "Next" button action, false to cancel it...
         */
        @Override
        protected boolean onNext(Object context, JWizard wizard) {
            NetConfig netCfg = new NetConfig(this.currentServerConfig.getServerName(), this.currentServerConfig.getAccountServerPort());
            netCfg.setServerId(this.currentServerConfig.getServerID());
            netCfg.setStandaloneBasePath(ClientDirector.getResourceManager().getResourceDir(null));

            JAccountConnectionDialog jaconnect = new JAccountConnectionDialog(null, netCfg, wizard, wizard.getGameDefinition());

            if (jaconnect.hasSucceeded()) {
                wizard.setContext(jaconnect.getConnection());
                Debug.signal(Debug.NOTICE, null, "ClientManager connected to AccountServer");
                jaconnect.getConnection().queueMessage(new AccountCreationMessage());
                await();

                return true;
            } else {
                Debug.signal(Debug.NOTICE, null, "ClientManager ejected from AccountServer");
                return false;
            }
        }

        /*------------------------------------------------------------------------------------*/
        /** Called when Previous button is clicked.
         *  Use the wizard's setNextStep() method to set the next step to be displayed.
         *  @return return true to validate the "Previous" button action, false to cancel it...
         */
        @Override
        protected boolean onPrevious(Object context, JWizard wizard) {
            return false; // should never been reached
        }

        /*------------------------------------------------------------------------------------*/
    }

    /*------------------------------------------------------------------------------------*/
}
