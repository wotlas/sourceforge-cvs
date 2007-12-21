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

package wotlas.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import wotlas.client.ClientDirector;
import wotlas.common.ErrorCodeList;
import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigManager;
import wotlas.libs.aswing.AInfoDialog;
import wotlas.libs.aswing.ALabel;
import wotlas.libs.net.NetClient;
import wotlas.libs.net.NetConnection;
import wotlas.libs.net.NetErrorCodeList;
import wotlas.utils.Debug;
import wotlas.utils.SwingTools;
import wotlas.utils.Tools;

/** A small utility to connect to a server using a JDialog.
 *
 * @author Aldiss, Petrus
 * @see wotlas.libs.net.NetClient
 */

public abstract class JConnectionDialog extends JDialog implements Runnable {

    /*------------------------------------------------------------------------------------*/

    private ALabel l_info; // information label
    private JButton b_cancel; // cancel button

    protected boolean hasSucceeded; // has connection succeeded ?
    private NetConnection connection;
    private NetClient client;

    protected Frame frame;
    private String server, key;
    private int port;
    private Object context;

    // to tell if this JConnectionDialog was canceled
    private boolean connectCanceled;

    // eventual error message & associated error code
    protected String errorMessage;
    protected short errorCode;

    // ID of the server we want to join
    protected int serverID;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. Displays the JDialog and immediately tries to connect to the specified
     *  server. It displays eventual error messages in pop-ups.
     *  The detail of the parameters is the following :
     * 
     * @param frame frame owner of this JDialog
     * @param server server name (DNS or IP address)
     * @param port server port
     * @param serverID Id of the server we want to join
     * @param key server key for this connection ( see the javadoc header of this class ).
     * @param context context to set to messages ( see NetConnection ).
     */

    public JConnectionDialog(Frame frame, String server, int port, int serverID, String key, Object context) {
        super(frame, "Network Connection", true);

        // some inits
        this.frame = frame;
        this.server = server;
        this.port = port;
        this.key = key;
        this.context = context;
        this.serverID = serverID;

        this.hasSucceeded = false;
        this.connectCanceled = false;
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.white);

        // Top Label
        ALabel label1 = new ALabel("Trying to connect to " + server + ":" + port + ", please wait...", SwingConstants.CENTER);
        getContentPane().add(label1, BorderLayout.NORTH);

        // Center label
        this.l_info = new ALabel("Initializing Network Connection...", SwingConstants.CENTER);
        this.l_info.setPreferredSize(new Dimension(200, 80));
        getContentPane().add(this.l_info, BorderLayout.CENTER);

        // Cancel Button
        ImageIcon im_cancelup = ClientDirector.getResourceManager().getImageIcon("cancel-up.gif");
        ImageIcon im_canceldo = ClientDirector.getResourceManager().getImageIcon("cancel-do.gif");
        ImageIcon im_cancelun = ClientDirector.getResourceManager().getImageIcon("cancel-un.gif");
        this.b_cancel = new JButton(im_cancelup);
        this.b_cancel.setRolloverIcon(im_canceldo);
        this.b_cancel.setPressedIcon(im_canceldo);
        this.b_cancel.setDisabledIcon(im_cancelun);
        this.b_cancel.setBorderPainted(false);
        this.b_cancel.setContentAreaFilled(false);
        this.b_cancel.setFocusPainted(false);
        //b_cancel = new JButton("Cancel Server Connection");
        getContentPane().add(this.b_cancel, BorderLayout.SOUTH);

        this.b_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JConnectionDialog.this.connectCanceled = true;

                if (JConnectionDialog.this.client != null)
                    JConnectionDialog.this.client.stopConnectionProcess();
            }
        });

        pack();

        new Thread(this).start();
        SwingTools.centerComponent(this);
        show();

    }

    /*------------------------------------------------------------------------------------*/

    /** Thread action
     */
    public void run() {

        // 1 - Wait the display of the window
        do {
            Tools.waitTime(500);
        } while (!isShowing());

        boolean retry;
        int searchingOnServerID = 0;

        do {
            retry = false;

            // 2 - We try a connection with the provided parameters...
            tryConnection();

            // 3 - Connection failed ?
            if (!this.connectCanceled && !this.hasSucceeded) {
                // We analyze the error returned
                if (this.errorCode == NetErrorCodeList.ERR_CONNECT_FAILED) {
                    // we report the deadlink and try the eventualy new address
                    this.l_info.setText("Connection failed. Trying to update server address...");
                    ServerConfigManager configList = ClientDirector.getClientManager().getServerConfigManager();

                    if (searchingOnServerID <= 0)
                        this.server = configList.reportDeadServer(this.serverID);
                    else
                        this.server = configList.reportDeadServer(searchingOnServerID);

                    if (this.server != null)
                        retry = true; // we retry a connection
                }

                // If the account was not found we try another server (happens only for the GameServer)
                if (this.errorCode == ErrorCodeList.ERR_UNKNOWN_ACCOUNT || (!retry && searchingOnServerID > 0)) {
                    this.l_info.setText("Account not found on this server. Trying next server...");
                    ServerConfigManager configList = ClientDirector.getClientManager().getServerConfigManager();

                    do {
                        searchingOnServerID = configList.getNextServerID(searchingOnServerID);
                    } while (searchingOnServerID == this.serverID);

                    if (searchingOnServerID > 0) {
                        // we update our server address & port
                        ServerConfig nextServer = configList.getServerConfig(searchingOnServerID);
                        this.server = nextServer.getServerName();
                        this.port = nextServer.getGameServerPort();
                        retry = true;
                    } else
                        displayError("Account not found on running servers. Please retry later.");
                } else if (!retry)
                    displayError("" + this.errorMessage);
            }

        } while (retry);

        // 4 - Prepare to close the dialog the right way (we are not in the AWT thread)
        final JConnectionDialog cDialog = this;

        Runnable runnable = new Runnable() {
            public void run() {
                cDialog.dispose();
            }
        };

        SwingUtilities.invokeLater(runnable);
        Debug.signal(Debug.NOTICE, null, "closing JConnectionDialog");
    }

    /*------------------------------------------------------------------------------------*/

    /** To display en error message in a pop-up.
     */
    protected void displayError(String error) {

        final String ferror = new String(error);

        Runnable runnable = new Runnable() {
            public void run() {
                new AInfoDialog(JConnectionDialog.this.frame, ferror, true, ClientDirector.getResourceManager());
            }
        };

        SwingUtilities.invokeLater(runnable);

        //  JOptionPane.showMessageDialog( frame, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /*------------------------------------------------------------------------------------*/

    /** To try a connection
     */
    private void tryConnection() {
        this.client = new NetClient();

        String packages[] = getPackages();

        this.connection = this.client.connectToServer(this.server, this.port, this.key, this.context, packages);

        if (this.connection == null) {
            if (this.client.getErrorCode() != NetErrorCodeList.ERR_NONE) {
                this.errorCode = this.client.getErrorCode();
                this.errorMessage = this.client.getErrorMessage();
            } else
                this.errorCode = NetErrorCodeList.ERR_CONNECT_FAILED;

            return;
        }

        this.l_info.setText("Connection succeeded...");
        this.hasSucceeded = true;

        Tools.waitTime(1000);
    }

    /*------------------------------------------------------------------------------------*/

    /** To retrieve a list of the NetMessage packages to use with this server.
     */
    abstract protected String[] getPackages();

    /*------------------------------------------------------------------------------------*/

    /** Has the connection succeeded ?
     *
     * @return true if we succesfully connected on the specified server.
     */
    public boolean hasSucceeded() {
        return this.hasSucceeded;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the created connection.
     *
     * @return the created NetConnection.
     */
    public NetConnection getConnection() {
        return this.connection;
    }

    /*------------------------------------------------------------------------------------*/

}