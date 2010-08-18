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
package wotlas.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import wotlas.client.gui.JHTMLWindow;
import wotlas.client.gui.JIntroWizard;
import wotlas.client.screen.JAbout;
import wotlas.client.screen.JAccountCreationWizard;
import wotlas.client.screen.JConfigurationDlg;
import wotlas.client.screen.JDeleteAccountDialog;
import wotlas.client.screen.JGameConnectionDialog;
import wotlas.common.ResourceManager;
import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigManager;
import wotlas.common.message.description.AccountRecoverMessage;
import wotlas.libs.aswing.ALabel;
import wotlas.libs.aswing.APasswordField;
import wotlas.libs.aswing.ATableCellRenderer;
import wotlas.libs.aswing.ATextField;
import wotlas.libs.graphics2d.FontFactory;
import wotlas.libs.net.NetConfig;
import wotlas.libs.sound.SoundLibrary;
import wotlas.utils.Debug;

/** The main small windows of the game : welcome, accounts, login, recover, delete.
 *  The ClientManager is here to link the different GUIs between them. It also
 *  possesses the client player profiles and server configs.
 *
 * @author Petrus
 * @see wotlas.client.gui.JIntroWizard
 */
public class ClientManager extends JIntroWizard implements ActionListener {

    /*------------------------------------------------------------------------------------*/
    /** Available steps for this Client Manager
     */
    final static public int FIRST_INIT = -1;
    final static public int MAIN_SCREEN = 0;
    final static public int ACCOUNT_LOGIN_SCREEN = 1;
    final static public int DELETE_ACCOUNT_SCREEN = 2;
    final static public int RECOVER_ACCOUNT_SCREEN = 3;
    final static public int ACCOUNT_CREATION_SCREEN = 10;
    final static public int ACCOUNT_INFO_SCREEN = 11;
    final static public int DATAMANAGER_DISPLAY = 20;

    /*------------------------------------------------------------------------------------*/
    /** Do we have to remember passwords ? i.e. save them to disk. (default is true).
     */
    private static boolean rememberPasswords = true;

    /*------------------------------------------------------------------------------------*/
    /** Our ProfileConfigList file.
     */
    private ProfileConfigList profileConfigList;
    /** Current profileConfig
     */
    private ProfileConfig currentProfileConfig;
    /** Our ServerConfigManager file.
     */
    private ServerConfigManager serverConfigManager;
    /** Current serverConfig
     */
    private ServerConfig currentServerConfig;
    /** Index of current screen shown
     */
    private int indexScreen;
    /** Number of tries to reach wotlas *web* server
     */
    private short nbTry;
    /** pictures of buttons
     */
    private ImageIcon im_okup;
    private ImageIcon im_okdo;
    private ImageIcon im_okun;
    private ImageIcon im_cancelup;
    private ImageIcon im_canceldo;
    private ImageIcon im_cancelun;
    private ImageIcon im_newup;
    private ImageIcon im_newdo;
    private ImageIcon im_loadup;
    private ImageIcon im_loaddo;
    private ImageIcon im_loadun;
    private ImageIcon im_recoverup;
    private ImageIcon im_recoverdo;
    private ImageIcon im_recoverun;
    private ImageIcon im_delup;
    private ImageIcon im_deldo;
    private ImageIcon im_delun;
    private ImageIcon im_exitup;
    private ImageIcon im_exitdo;
    private ImageIcon im_aboutup;
    private ImageIcon im_aboutdo;
    private ImageIcon im_helpup;
    private ImageIcon im_helpdo;
    private ImageIcon im_optionsup;
    private ImageIcon im_optionsdo;
    /** Default font
     */
    private Font f;
    /** Do we have to login the user automatically if his password is in memory ?
     *  Automatic login makes that the password prompt doesn't displays if
     *  there is already a password in memory.
     *
     *  This option is used only when we need to reconnect because the client
     *  account has moved to another server.
     */
    private boolean automaticLogin;

    /*------------------------------------------------------------------------------------*/
    /** To set if we have to remember passwords or not
     */
    static public void setRememberPasswords(boolean remember) {
        ClientManager.rememberPasswords = remember;
    }

    /** do we have to remember passwords ?
     */
    static public boolean getRememberPasswords() {
        return ClientManager.rememberPasswords;
    }

    /*------------------------------------------------------------------------------------*/
    /** Constructor. Loads/create the different config files but does not display anything.
     */
    public ClientManager(ResourceManager rManager) {
        super();
        this.automaticLogin = false;

        // 1 - We load the ProfileConfigList
        this.profileConfigList = ProfileConfigList.load();

        if (this.profileConfigList == null) {
            Debug.signal(Debug.NOTICE, this, "no client's profile found : creating a new one...");
            this.profileConfigList = new ProfileConfigList();
        } else {
            Debug.signal(Debug.NOTICE, null, "Client Configs loaded with success !");
        }

        if (!ClientManager.rememberPasswords) {
            this.profileConfigList.deletePasswords(); // make sure we don't save any password here
            this.profileConfigList.save();
        }

        // 2 - We load the ServerConfigManager
        this.serverConfigManager = new ServerConfigManager(rManager);
        this.serverConfigManager.setRemoteServerConfigHomeURL(ClientDirector.getRemoteServerConfigHomeURL());
        Debug.signal(Debug.NOTICE, null, "Server config Manager started with success !");

        // 3 - We get the font we are going to use...
        this.f = FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter Regular");
    }

    /*------------------------------------------------------------------------------------*/
    /** To get the Profile Config.
     * @return the Profile Config
     */
    public ProfileConfigList getProfileConfigList() {
        return this.profileConfigList;
    }

    /** To get the current Profile Config.
     * @return the ProfilesConfig
     */
    public ProfileConfig getCurrentProfileConfig() {
        return this.currentProfileConfig;
    }

    /*------------------------------------------------------------------------------------*/
    /** To get the ServerConfigManager.
     *
     * @return the ServerConfigManager
     */
    public ServerConfigManager getServerConfigManager() {
        return this.serverConfigManager;
    }

    /*------------------------------------------------------------------------------------*/
    /** To set automatic login or not.
     *  Automatic login makes that the password prompt doesn't displays if
     *  there is already a password in memory.
     *
     *  This option is used only when we need to reconnect because the client
     *  account has moved to another server.
     */
    public void setAutomaticLogin(boolean automaticLogin) {
        this.automaticLogin = automaticLogin;
    }

    /** Are we using automatic login or not ?
     */
    public boolean getAutomaticLogin() {
        return this.automaticLogin;
    }

    /*------------------------------------------------------------------------------------*/
    /** Starts the Wizard at the beginning of the game
     */
    public void start(int state) {

        JPanel leftPanel;
        JPanel rightPanel;

        JScrollPane scrollPane;

        ALabel label1;
        ALabel label2;
        ALabel label3;
        ALabel label4;
        ALabel label5;

        JLabel imgLabel1;
        JLabel imgLabel2;

        final ATextField tfield1;
        final ATextField atf_login;
        final ATextField atf_key;
        final APasswordField pfield1;
        final APasswordField pfield2;

        final JButton b_ok;
        final JButton b_load;
        final JButton b_help;
        final JButton b_about;
        final JButton b_option;
        final JButton b_cancel;
        final JButton b_newProfile;
        final JButton b_delProfile;
        final JButton b_exitProfile;
        final JButton b_recoverProfile;

        final JTable profilesTable;
        final JTable serversTable;

        this.indexScreen = state;

        switch (state) {

            // ********************
            // *** First Screen ***
            // ********************

            case FIRST_INIT:
                // We try to contact the wotlas web server...
                this.nbTry = 1;
                Timer timer = new Timer(5000, this);
                timer.start();

                this.serverConfigManager.getLatestConfigFiles(this);
                timer.stop();

                if (!this.serverConfigManager.hasRemoteServersInfo()) {
                    JOptionPane.showMessageDialog(this, "We failed to contact the wotlas web server. So we could not update\n" + "our servers addresses. If this is not the first time you start wotlas on\n" + "your computer, you can try to connect with the previous server config\n" + "files. Otherwise please restart wotlas later.\n\n" + "Note also that wotlas is not firewall/proxy friendly. See our FAQ for\n" + "more details ( from the help section or 'wotlas.html' local file ).", "Warning", JOptionPane.WARNING_MESSAGE);
                } else // Wotlas News
                {
                    new JHTMLWindow(this, "Wotlas News", ClientDirector.getRemoteServerConfigHomeURL() + "news.html", 320, 400, false, ClientDirector.getResourceManager());
                }

                // Load images of buttons
                this.im_cancelup = ClientDirector.getResourceManager().getImageIcon("cancel-up.gif");
                this.im_canceldo = ClientDirector.getResourceManager().getImageIcon("cancel-do.gif");
                this.im_cancelun = ClientDirector.getResourceManager().getImageIcon("cancel-un.gif");
                this.im_okup = ClientDirector.getResourceManager().getImageIcon("ok-up.gif");
                this.im_okdo = ClientDirector.getResourceManager().getImageIcon("ok-do.gif");
                this.im_okun = ClientDirector.getResourceManager().getImageIcon("ok-un.gif");
                this.im_recoverup = ClientDirector.getResourceManager().getImageIcon("recover-up.gif");
                this.im_recoverdo = ClientDirector.getResourceManager().getImageIcon("recover-do.gif");
                this.im_recoverun = ClientDirector.getResourceManager().getImageIcon("recover-un.gif");
                this.im_delup = ClientDirector.getResourceManager().getImageIcon("delete-up.gif");
                this.im_deldo = ClientDirector.getResourceManager().getImageIcon("delete-do.gif");
                this.im_delun = ClientDirector.getResourceManager().getImageIcon("delete-un.gif");
                this.im_exitup = ClientDirector.getResourceManager().getImageIcon("exit-up.gif");
                this.im_exitdo = ClientDirector.getResourceManager().getImageIcon("exit-do.gif");
                this.im_loadup = ClientDirector.getResourceManager().getImageIcon("load-up.gif");
                this.im_loaddo = ClientDirector.getResourceManager().getImageIcon("load-do.gif");
                this.im_loadun = ClientDirector.getResourceManager().getImageIcon("load-un.gif");
                this.im_newup = ClientDirector.getResourceManager().getImageIcon("new-up.gif");
                this.im_newdo = ClientDirector.getResourceManager().getImageIcon("new-do.gif");
                this.im_aboutup = ClientDirector.getResourceManager().getImageIcon("about-up.gif");
                this.im_aboutdo = ClientDirector.getResourceManager().getImageIcon("about-do.gif");
                this.im_helpup = ClientDirector.getResourceManager().getImageIcon("help-up.gif");
                this.im_helpdo = ClientDirector.getResourceManager().getImageIcon("help-do.gif");
                this.im_optionsup = ClientDirector.getResourceManager().getImageIcon("options-up.gif");
                this.im_optionsdo = ClientDirector.getResourceManager().getImageIcon("options-do.gif");

                this.indexScreen = ClientManager.MAIN_SCREEN;
                state = ClientManager.MAIN_SCREEN;

                // Hide the Log Window ?
                if (!ClientDirector.getClientConfiguration().getDisplayLogWindow()) {
                    // ADD by DIEGO : REMOVE by DIEGO if( !ClientDirector.SHOW_DEBUG )
                    ClientDirector.getLogStream().setVisible(false);
                }

                // Test if an account exists
                if (this.profileConfigList.size() == 0) {
                    start(ClientManager.ACCOUNT_CREATION_SCREEN);
                    return;
                }

            case MAIN_SCREEN:

                setTitle("Wotlas - Account selection...");
                SoundLibrary.getMusicPlayer().stopMusic();

                // Create panels
                leftPanel = new JPanel();
                leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                rightPanel = new JPanel();
                rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, getRightWidth(), 5));

                // Create buttons
                b_about = new JButton(this.im_aboutup);
                b_about.setRolloverIcon(this.im_aboutdo);
                b_about.setPressedIcon(this.im_aboutdo);
                b_about.setBorderPainted(false);
                b_about.setContentAreaFilled(false);
                b_about.setFocusPainted(false);

                b_option = new JButton(this.im_optionsup);
                b_option.setRolloverIcon(this.im_optionsdo);
                b_option.setPressedIcon(this.im_optionsdo);
                b_option.setBorderPainted(false);
                b_option.setContentAreaFilled(false);
                b_option.setFocusPainted(false);

                b_ok = new JButton(this.im_okup);
                b_ok.setRolloverIcon(this.im_okdo);
                b_ok.setPressedIcon(this.im_okdo);
                b_ok.setDisabledIcon(this.im_okun);
                b_ok.setBorderPainted(false);
                b_ok.setContentAreaFilled(false);
                b_ok.setFocusPainted(false);

                b_cancel = new JButton(this.im_cancelup);
                b_cancel.setRolloverIcon(this.im_canceldo);
                b_cancel.setPressedIcon(this.im_canceldo);
                b_cancel.setDisabledIcon(this.im_cancelun);
                b_cancel.setBorderPainted(false);
                b_cancel.setContentAreaFilled(false);
                b_cancel.setFocusPainted(false);

                b_load = new JButton(this.im_loadup);
                b_load.setRolloverIcon(this.im_loaddo);
                b_load.setPressedIcon(this.im_loaddo);
                b_load.setDisabledIcon(this.im_loadun);
                b_load.setBorderPainted(false);
                b_load.setContentAreaFilled(false);
                b_load.setFocusPainted(false);

                b_help = new JButton(this.im_helpup);
                b_help.setRolloverIcon(this.im_helpdo);
                b_help.setPressedIcon(this.im_helpdo);
                b_help.setBorderPainted(false);
                b_help.setContentAreaFilled(false);
                b_help.setFocusPainted(false);

                b_newProfile = new JButton(this.im_newup);
                b_newProfile.setRolloverIcon(this.im_newdo);
                b_newProfile.setPressedIcon(this.im_newdo);
                b_newProfile.setDisabledIcon(this.im_newdo);
                b_newProfile.setBorderPainted(false);
                b_newProfile.setContentAreaFilled(false);
                b_newProfile.setFocusPainted(false);

                b_recoverProfile = new JButton(this.im_recoverup);
                b_recoverProfile.setRolloverIcon(this.im_recoverdo);
                b_recoverProfile.setPressedIcon(this.im_recoverdo);
                b_recoverProfile.setDisabledIcon(this.im_recoverun);
                b_recoverProfile.setBorderPainted(false);
                b_recoverProfile.setContentAreaFilled(false);
                b_recoverProfile.setFocusPainted(false);

                b_delProfile = new JButton(this.im_delup);
                b_delProfile.setRolloverIcon(this.im_deldo);
                b_delProfile.setPressedIcon(this.im_deldo);
                b_delProfile.setDisabledIcon(this.im_delun);
                b_delProfile.setBorderPainted(false);
                b_delProfile.setContentAreaFilled(false);
                b_delProfile.setFocusPainted(false);

                b_exitProfile = new JButton(this.im_exitup);
                b_exitProfile.setRolloverIcon(this.im_exitdo);
                b_exitProfile.setPressedIcon(this.im_exitdo);
                b_exitProfile.setBorderPainted(false);
                b_exitProfile.setContentAreaFilled(false);
                b_exitProfile.setFocusPainted(false);

                // *** Left JPanel ***

                imgLabel1 = new JLabel(ClientDirector.getResourceManager().getImageIcon("welcome-title.jpg"));
                imgLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
                leftPanel.add(imgLabel1);

                imgLabel2 = new JLabel(ClientDirector.getResourceManager().getImageIcon("choose.gif"));
                imgLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
                leftPanel.add(imgLabel2);

                leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                // Creates a table of profiles
                ProfileConfigListTableModel profileConfigListTabModel = new ProfileConfigListTableModel(this.profileConfigList, this.serverConfigManager);
                profilesTable = new JTable(profileConfigListTabModel);
                profilesTable.setDefaultRenderer(Object.class, new ATableCellRenderer());
                profilesTable.setBackground(Color.white);
                profilesTable.setForeground(Color.black);
                profilesTable.setSelectionBackground(Color.lightGray);
                profilesTable.setSelectionForeground(Color.white);
                profilesTable.setRowHeight(24);
                profilesTable.getColumnModel().getColumn(2).setPreferredWidth(-1);

                // selection
                profilesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                ListSelectionModel rowProfilesSM = profilesTable.getSelectionModel();
                rowProfilesSM.addListSelectionListener(new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        //Ignore extra messages.
                        if (e.getValueIsAdjusting()) {
                            return;
                        }
                        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                        if (lsm.isSelectionEmpty()) {
                            //no rows are selected
                        } else {
                            int selectedRow = lsm.getMinSelectionIndex();
                            ClientManager.this.currentProfileConfig = ClientManager.this.profileConfigList.getProfiles()[selectedRow];
                            b_load.setEnabled(true);
                            b_delProfile.setEnabled(true);
                        }
                    }
                });

                // show table
                scrollPane = new JScrollPane(profilesTable);
                profilesTable.setPreferredScrollableViewportSize(new Dimension(0, 170));
                scrollPane.getViewport().setBackground(Color.white);
                JScrollBar jsb_01 = scrollPane.getVerticalScrollBar();
                leftPanel.add(scrollPane);

                // *** Right Panel ***

                b_load.setEnabled(false);
                b_load.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        start(ClientManager.ACCOUNT_LOGIN_SCREEN);
                    }
                });
                rightPanel.add(b_load);

                b_newProfile.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        start(ClientManager.ACCOUNT_CREATION_SCREEN);
                    }
                });
                rightPanel.add(b_newProfile);

                rightPanel.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("separator.gif"))); // SEPARATOR

                b_recoverProfile.setEnabled(true);
                b_recoverProfile.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        start(ClientManager.RECOVER_ACCOUNT_SCREEN);
                    }
                });
                rightPanel.add(b_recoverProfile);

                b_delProfile.setEnabled(false);

                b_delProfile.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        start(ClientManager.DELETE_ACCOUNT_SCREEN);
                    }
                });

                rightPanel.add(b_delProfile);

                rightPanel.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("separator.gif"))); // SEPARATOR

                b_option.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        new JConfigurationDlg(ClientManager.this);
                    }
                });
                rightPanel.add(b_option);

                b_help.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        new JHTMLWindow(ClientManager.this, "Help", ClientDirector.getResourceManager().getHelpDocsDir() + "index.html", 640, 340, false, ClientDirector.getResourceManager());
                    }
                });
                rightPanel.add(b_help);

                rightPanel.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("separator.gif"))); // SEPARATOR

                b_about.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        try {
                            new JAbout(ClientManager.this);
                        } catch (RuntimeException ei) {
                            Debug.signal(Debug.ERROR, this, ei);
                        }
                    }
                });
                rightPanel.add(b_about);

                b_exitProfile.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        ClientDirector.getDataManager().exit();
                    }
                });
                rightPanel.add(b_exitProfile);

                // *** Adding the panels ***

                setLeftPanel(leftPanel);
                setRightPanel(rightPanel);
                showScreen();
                break;

            // ********************************
            // *** Connection to GameServer ***
            // ********************************

            case ACCOUNT_LOGIN_SCREEN:
                setTitle("Wotlas - Login...");

                // Create panels
                leftPanel = new JPanel();
                leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                rightPanel = new JPanel();

                // Create buttons
                b_ok = new JButton(this.im_okup);
                b_ok.setRolloverIcon(this.im_okdo);
                b_ok.setPressedIcon(this.im_okdo);
                b_ok.setDisabledIcon(this.im_okun);
                b_ok.setBorderPainted(false);
                b_ok.setContentAreaFilled(false);
                b_ok.setFocusPainted(false);

                b_cancel = new JButton(this.im_cancelup);
                b_cancel.setRolloverIcon(this.im_canceldo);
                b_cancel.setPressedIcon(this.im_canceldo);
                b_cancel.setDisabledIcon(this.im_cancelun);
                b_cancel.setBorderPainted(false);
                b_cancel.setContentAreaFilled(false);
                b_cancel.setFocusPainted(false);

                // *** Left JPanel ***

                label1 = new ALabel("Welcome " + this.currentProfileConfig.getLogin() + ",");
                label1.setAlignmentX(Component.CENTER_ALIGNMENT);
                leftPanel.add(label1);
                label1.setFont(this.f.deriveFont(18f));

                leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                JPanel mainPanel_01 = new JPanel();
                mainPanel_01.setBackground(Color.white);

                JPanel formPanel_01_left = new JPanel(new GridLayout(2, 1, 5, 5));
                formPanel_01_left.setBackground(Color.white);
                formPanel_01_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("enter-password.gif")));
                formPanel_01_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("your-key.gif")));
                mainPanel_01.add(formPanel_01_left);
                JPanel formPanel_01_right = new JPanel(new GridLayout(2, 1, 5, 10));
                formPanel_01_right.setBackground(Color.white);
                pfield1 = new APasswordField(10);
                pfield1.setFont(this.f.deriveFont(18f));

                if (this.currentProfileConfig.getPassword() != null) {
                    pfield1.setText(this.currentProfileConfig.getPassword());
                }

                pfield1.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            b_ok.doClick();
                        }
                    }
                });

                formPanel_01_right.add(pfield1);
                ALabel alabel = new ALabel(this.currentProfileConfig.getLogin() + "-" + this.currentProfileConfig.getKey());
                alabel.setFont(this.f.deriveFont(16f));
                formPanel_01_right.add(alabel);
                mainPanel_01.add(formPanel_01_right);
                leftPanel.add(mainPanel_01);

                // *** Right Panel ***

                b_ok.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        char charPasswd[] = pfield1.getPassword();
                        if (charPasswd.length < 4) {
                            JOptionPane.showMessageDialog(ClientManager.this, "Password must have at least 5 characters !", "New Password", JOptionPane.ERROR_MESSAGE);
                        } else {
                            String passwd = new String(charPasswd);

                            ClientDirector.getDataManager().setCurrentProfileConfig(ClientManager.this.currentProfileConfig);

                            ClientManager.this.currentServerConfig = ClientManager.this.serverConfigManager.getServerConfig(ClientManager.this.currentProfileConfig.getServerID());
                            NetConfig netCfg = new NetConfig(ClientManager.this.currentServerConfig.getServerName(), ClientManager.this.currentServerConfig.getGameServerPort());
                            netCfg.setServerId(ClientManager.this.currentServerConfig.getServerID());
                            netCfg.setStandaloneBasePath(ClientDirector.getResourceManager().getResourceDir(null));

                            JGameConnectionDialog jgconnect = new JGameConnectionDialog(ClientManager.this, netCfg, ClientManager.this.currentProfileConfig.getLogin(), passwd, ClientManager.this.currentProfileConfig.getLocalClientID(), ClientManager.this.currentProfileConfig.getOriginalServerID(), ClientDirector.getDataManager(), ClientDirector.getResourceManager().getGameDefinition());

                            if (jgconnect.hasSucceeded()) {
                                ClientManager.this.currentProfileConfig.setPassword(passwd);

                                if (ClientManager.rememberPasswords) {
                                    ClientManager.this.profileConfigList.save();
                                }

                                Debug.signal(Debug.NOTICE, null, "ClientManager connected to GameServer");
                                start(ClientManager.DATAMANAGER_DISPLAY);
                            } else {
                                Debug.signal(Debug.ERROR, this, "ClientManager ejected from GameServer");
                                start(ClientManager.MAIN_SCREEN);
                            }
                        }
                    }
                });
                rightPanel.add(b_ok);

                b_cancel.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        start(ClientManager.MAIN_SCREEN);
                    }
                });
                rightPanel.add(b_cancel);

                // *** Adding the panels ***
                setLeftPanel(leftPanel);
                setRightPanel(rightPanel);
                showScreen();

                if (this.automaticLogin && this.currentProfileConfig.getPassword() != null) {
                    this.automaticLogin = false; // works only once...
                    b_ok.doClick(); // we launch the connection procedure
                }

                break;

            // ********************************
            // ***   To Delete An Account   ***
            // ********************************

            case DELETE_ACCOUNT_SCREEN:
                setTitle("Wotlas - Delete Account...");

                // Create panels
                leftPanel = new JPanel();
                leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                rightPanel = new JPanel();

                // Create buttons
                b_delProfile = new JButton(this.im_delup);
                b_delProfile.setRolloverIcon(this.im_deldo);
                b_delProfile.setPressedIcon(this.im_deldo);
                b_delProfile.setDisabledIcon(this.im_delun);
                b_delProfile.setBorderPainted(false);
                b_delProfile.setContentAreaFilled(false);
                b_delProfile.setFocusPainted(false);

                b_cancel = new JButton(this.im_cancelup);
                b_cancel.setRolloverIcon(this.im_canceldo);
                b_cancel.setPressedIcon(this.im_canceldo);
                b_cancel.setDisabledIcon(this.im_cancelun);
                b_cancel.setBorderPainted(false);
                b_cancel.setContentAreaFilled(false);
                b_cancel.setFocusPainted(false);

                // *** Left JPanel ***

                label1 = new ALabel("Delete " + this.currentProfileConfig.getPlayerName() + " ?");
                label1.setAlignmentX(Component.CENTER_ALIGNMENT);
                leftPanel.add(label1);
                label1.setFont(this.f.deriveFont(18f));

                leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                JPanel mainPanel_02 = new JPanel();
                mainPanel_02.setBackground(Color.white);
                JPanel formPanel_02_left = new JPanel(new GridLayout(2, 1, 5, 5));
                formPanel_02_left.setBackground(Color.white);
                formPanel_02_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("enter-password.gif")));
                formPanel_02_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("your-key.gif")));
                mainPanel_02.add(formPanel_02_left);
                JPanel formPanel_02_right = new JPanel(new GridLayout(2, 1, 5, 10));
                formPanel_02_right.setBackground(Color.white);
                pfield1 = new APasswordField(10);
                pfield1.setFont(this.f.deriveFont(18f));

                pfield1.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            b_delProfile.doClick();
                        }
                    }
                });

                if (this.currentProfileConfig.getPassword() != null) {
                    pfield1.setText(this.currentProfileConfig.getPassword());
                }

                formPanel_02_right.add(pfield1);
                ALabel alabel2 = new ALabel(this.currentProfileConfig.getLogin() + "-" + this.currentProfileConfig.getKey());
                alabel2.setFont(this.f.deriveFont(16f));
                formPanel_02_right.add(alabel2);
                mainPanel_02.add(formPanel_02_right);
                leftPanel.add(mainPanel_02);

                // *** Right Panel ***

                b_delProfile.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        char charPasswd[] = pfield1.getPassword();
                        String passwd = "";
                        if (charPasswd.length < 4) {
                            JOptionPane.showMessageDialog(ClientManager.this, "Password must have at least 5 characters !", "New Password", JOptionPane.ERROR_MESSAGE);
                        } else {
                            for (int i = 0; i < charPasswd.length; i++) {
                                passwd += charPasswd[i];
                            }

                            ClientManager.this.currentServerConfig = ClientManager.this.serverConfigManager.getServerConfig(ClientManager.this.currentProfileConfig.getServerID());
                            NetConfig netCfg = new NetConfig(ClientManager.this.currentServerConfig.getServerName(), ClientManager.this.currentServerConfig.getAccountServerPort());
                            netCfg.setServerId(ClientManager.this.currentServerConfig.getServerID());
                            netCfg.setStandaloneBasePath(ClientDirector.getResourceManager().getResourceDir(null));

                            JDeleteAccountDialog jdconnect = new JDeleteAccountDialog(ClientManager.this, netCfg, ClientManager.this.currentProfileConfig.getLogin() + "-" + ClientManager.this.currentProfileConfig.getOriginalServerID() + "-" + ClientManager.this.currentProfileConfig.getLocalClientID(), passwd, ClientDirector.getResourceManager().getGameDefinition());

                            if (jdconnect.hasSucceeded()) {
                                Debug.signal(Debug.NOTICE, this, "Account deleted.");

                                // Save accounts informations
                                if (!ClientManager.this.profileConfigList.removeProfile(ClientManager.this.currentProfileConfig)) {
                                    Debug.signal(Debug.ERROR, this, "Failed to delete player profile !");
                                } else {
                                    ClientManager.this.profileConfigList.save();
                                }
                            }

                            start(ClientManager.MAIN_SCREEN); // return to main screen
                        }
                    }
                });
                rightPanel.add(b_delProfile);

                b_cancel.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        start(ClientManager.MAIN_SCREEN);
                    }
                });
                rightPanel.add(b_cancel);

                // *** Adding the panels ***
                setLeftPanel(leftPanel);
                setRightPanel(rightPanel);
                showScreen();
                break;

            // *********************************
            // *** Recover an existing account ****
            // *********************************

            case RECOVER_ACCOUNT_SCREEN:

                setTitle("Wotlas - Recover Login");

                // Create panels
                leftPanel = new JPanel();
                leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                rightPanel = new JPanel();

                // Create buttons
                b_ok = new JButton(this.im_okup);
                b_ok.setRolloverIcon(this.im_okdo);
                b_ok.setPressedIcon(this.im_okdo);
                b_ok.setDisabledIcon(this.im_okun);
                b_ok.setBorderPainted(false);
                b_ok.setContentAreaFilled(false);
                b_ok.setFocusPainted(false);

                b_cancel = new JButton(this.im_cancelup);
                b_cancel.setRolloverIcon(this.im_canceldo);
                b_cancel.setPressedIcon(this.im_canceldo);
                b_cancel.setDisabledIcon(this.im_cancelun);
                b_cancel.setBorderPainted(false);
                b_cancel.setContentAreaFilled(false);
                b_cancel.setFocusPainted(false);

                // *** Left JPanel ***

                label1 = new ALabel("To recover an existing account, please enter :");
                label1.setAlignmentX(Component.CENTER_ALIGNMENT);
                label1.setFont(this.f.deriveFont(18f));
                leftPanel.add(label1);

                leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                JPanel mainPanel_03 = new JPanel();
                mainPanel_03.setBackground(Color.white);
                JPanel formPanel_03_left = new JPanel(new GridLayout(2, 1, 5, 5));
                formPanel_03_left.setBackground(Color.white);
                formPanel_03_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("your-key.gif")));
                formPanel_03_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("enter-password.gif")));
                mainPanel_03.add(formPanel_03_left);
                JPanel formPanel_03_right = new JPanel(new GridLayout(2, 1, 5, 10));
                formPanel_03_right.setBackground(Color.white);
                atf_key = new ATextField(10);
                formPanel_03_right.add(atf_key);

                pfield1 = new APasswordField(10);
                pfield1.setFont(this.f.deriveFont(18f));
                pfield1.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            b_ok.doClick();
                        }
                    }
                });
                formPanel_03_right.add(pfield1);

                mainPanel_03.add(formPanel_03_right);
                leftPanel.add(mainPanel_03);

                // *** Right Panel ***

                b_ok.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        char charPasswd[] = pfield1.getPassword();

                        if (charPasswd.length < 4) {
                            JOptionPane.showMessageDialog(ClientManager.this, "Password must have at least 5 characters !", "Password", JOptionPane.ERROR_MESSAGE);
                        } else {

                            ClientManager.this.currentProfileConfig = new ProfileConfig();

                            String tempKey = atf_key.getText();
                            int index = tempKey.indexOf('-');
                            if (index < 0) {
                                JOptionPane.showMessageDialog(ClientManager.this, "Your key must have the following format : login-xx-yy.\nExample: bob-1-36", "Bad Format", JOptionPane.ERROR_MESSAGE);
                                start(ClientManager.MAIN_SCREEN);
                                return;
                            }

                            ClientManager.this.currentProfileConfig.setLogin(tempKey.substring(0, index));
                            ClientManager.this.currentProfileConfig.setPlayerName("");
                            ClientManager.this.currentProfileConfig.setPassword(new String(charPasswd));

                            tempKey = tempKey.substring(index + 1);
                            index = tempKey.indexOf('-');

                            if (index < 0) {
                                JOptionPane.showMessageDialog(ClientManager.this, "Your key must have the following format : login-xx-yy.\nExample: bob-1-36", "Bad Format", JOptionPane.ERROR_MESSAGE);
                                start(ClientManager.MAIN_SCREEN);
                                return;
                            }

                            try {
                                ClientManager.this.currentProfileConfig.setServerID(Integer.parseInt(tempKey.substring(0, index)));
                                ClientManager.this.currentProfileConfig.setOriginalServerID(Integer.parseInt(tempKey.substring(0, index)));
                                ClientManager.this.currentProfileConfig.setLocalClientID(Integer.parseInt(tempKey.substring(index + 1)));
                            } catch (NumberFormatException nfes) {
                                JOptionPane.showMessageDialog(ClientManager.this, "Your key must have the following format : login-xx-yy.\nExample: bob-1-36", "Bad Format", JOptionPane.ERROR_MESSAGE);
                                start(ClientManager.MAIN_SCREEN);
                                return;
                            }

                            ClientDirector.getDataManager().setCurrentProfileConfig(ClientManager.this.currentProfileConfig);
                            ClientManager.this.currentServerConfig = ClientManager.this.serverConfigManager.getServerConfig(ClientManager.this.currentProfileConfig.getServerID());

                            if (ClientManager.this.currentServerConfig == null) {
                                JOptionPane.showMessageDialog(ClientManager.this, "Failed to find the associated server.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                start(ClientManager.MAIN_SCREEN);
                                return;
                            }

                            NetConfig netCfg = new NetConfig(ClientManager.this.currentServerConfig.getServerName(), ClientManager.this.currentServerConfig.getGameServerPort());
                            netCfg.setServerId(ClientManager.this.currentServerConfig.getServerID());
                            netCfg.setStandaloneBasePath(ClientDirector.getResourceManager().getResourceDir(null));

                            JGameConnectionDialog jgconnect = new JGameConnectionDialog(ClientManager.this, netCfg, ClientManager.this.currentProfileConfig.getLogin(), new String(charPasswd), ClientManager.this.currentProfileConfig.getLocalClientID(), ClientManager.this.currentProfileConfig.getOriginalServerID(), ClientDirector.getDataManager(), ClientDirector.getResourceManager().getGameDefinition());

                            if (jgconnect.hasSucceeded()) {
                                Debug.signal(Debug.NOTICE, null, "ClientManager connected to GameServer");
                                jgconnect.getConnection().queueMessage(new AccountRecoverMessage(atf_key.getText()));

                                start(ClientManager.DATAMANAGER_DISPLAY);
                            } else {
                                Debug.signal(Debug.ERROR, this, "ClientManager ejected from GameServer");
                                start(ClientManager.MAIN_SCREEN);
                            }
                        }
                    }
                });
                rightPanel.add(b_ok);

                b_cancel.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        start(ClientManager.MAIN_SCREEN);
                    }
                });
                rightPanel.add(b_cancel);

                // *** Adding the panels ***

                setLeftPanel(leftPanel);
                setRightPanel(rightPanel);
                showScreen();
                break;

            // ***********************************
            // *** Connection to AccountServer ***
            // ***********************************

            case ACCOUNT_CREATION_SCREEN:

                this.serverConfigManager.getLatestConfigFiles(this);

                // Launching Wizard
                hide();
                JAccountCreationWizard accountCreationWz = new JAccountCreationWizard();
                break;

            // **************************************
            // *** A new account has been created ***
            // **************************************

            case ACCOUNT_INFO_SCREEN:

                setTitle("Wotlas - Account creation...");

                if (this.currentProfileConfig == null) {
                    start(ClientManager.MAIN_SCREEN);
                    break;
                }

                // Set the appropriate server config.
                this.currentServerConfig = this.serverConfigManager.getServerConfig(this.currentProfileConfig.getServerID());

                // Create panels
                leftPanel = new JPanel();
                //leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                leftPanel.setLayout(new GridLayout(1, 1, 5, 5));
                rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, getRightWidth(), 10));

                // Create buttons
                b_ok = new JButton(this.im_okup);
                b_ok.setRolloverIcon(this.im_okdo);
                b_ok.setPressedIcon(this.im_okdo);
                b_ok.setDisabledIcon(this.im_okun);
                b_ok.setBorderPainted(false);
                b_ok.setContentAreaFilled(false);
                b_ok.setFocusPainted(false);

                b_cancel = new JButton(this.im_cancelup);
                b_cancel.setRolloverIcon(this.im_canceldo);
                b_cancel.setPressedIcon(this.im_canceldo);
                b_cancel.setDisabledIcon(this.im_cancelun);
                b_cancel.setBorderPainted(false);
                b_cancel.setContentAreaFilled(false);
                b_cancel.setFocusPainted(false);

                // *** Left Panel ***/
                JEditorPane editorPane = new JEditorPane("text/html", "<html>Your new account has been <br>" + "successfully created! <br>" + "Remember your key to access <br>" + "wotlas from anywhere : <b>" + this.currentProfileConfig.getLogin() + "-" + this.currentProfileConfig.getKey() + "</b><br>Click OK to enter WOTLAS....</html>");
                leftPanel.add(editorPane, BorderLayout.CENTER);
                editorPane.setEditable(false);

                // *** Right Panel ***/

                b_ok.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        NetConfig netCfg = new NetConfig(ClientManager.this.currentServerConfig.getServerName(), ClientManager.this.currentServerConfig.getGameServerPort());
                        netCfg.setServerId(ClientManager.this.currentServerConfig.getServerID());
                        netCfg.setStandaloneBasePath(ClientDirector.getResourceManager().getResourceDir(null));

                        JGameConnectionDialog jgconnect = new JGameConnectionDialog(ClientManager.this, netCfg, ClientManager.this.currentProfileConfig.getLogin(), ClientManager.this.currentProfileConfig.getPassword(), ClientManager.this.currentProfileConfig.getLocalClientID(), ClientManager.this.currentProfileConfig.getOriginalServerID(), ClientDirector.getDataManager(), ClientDirector.getResourceManager().getGameDefinition());

                        if (jgconnect.hasSucceeded()) {
                            Debug.signal(Debug.NOTICE, null, "ClientManager connected to GameServer");
                            start(ClientManager.DATAMANAGER_DISPLAY);
                        } else {
                            Debug.signal(Debug.ERROR, this, "ClientManager ejected from GameServer");
                        }
                    }
                });
                rightPanel.add(b_ok);

                b_cancel.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        start(ClientManager.MAIN_SCREEN);
                    }
                });
                rightPanel.add(b_cancel);

                // *** Adding the panels ***

                setLeftPanel(leftPanel);
                setRightPanel(rightPanel);
                showScreen();
                break;

            // ********************
            // *** Final screen ***
            // ********************

            case DATAMANAGER_DISPLAY:

                hide();

                Thread heavyProcessThread = new Thread() {

                    @Override
                    public void run() {
                        ClientDirector.getDataManager().showInterface();
                    }
                };

                heavyProcessThread.start();
                break;

            default:
                // We should never arrive here
                // --> return to main screen
                start(ClientManager.MAIN_SCREEN);
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To add a new profile to the player's profile list. This method is called by
     *  the AccountCreationEndedMsgBehaviour when an account has been successfully created.
     */
    public void addNewProfile(int clientID, int serverID, String login, String password, String playerName) {

        this.currentProfileConfig = new ProfileConfig();

        // Set profile data
        this.currentProfileConfig.setPlayerName(playerName);
        this.currentProfileConfig.setLogin(login);
        this.currentProfileConfig.setPassword(password);
        this.currentProfileConfig.setLocalClientID(clientID);
        this.currentProfileConfig.setOriginalServerID(serverID);
        // FIXME must be current server id in config files ???
        // ??? int serverCurrentId = this.serverConfigManager.getServerConfig(serverID).getServerID();
        this.currentProfileConfig.setServerID(serverID); //serverCurrentId);

        // Save profile informations
        this.profileConfigList.addProfile(this.currentProfileConfig);
        this.profileConfigList.save();

        // Set Data Manager ready
        ClientDirector.getDataManager().setCurrentProfileConfig(this.currentProfileConfig);
    }

    /*------------------------------------------------------------------------------------*/
    /** Timer Event interception
     * @param e supposed timer event
     */
    public void actionPerformed(ActionEvent e) {
        Debug.signal(Debug.WARNING, null, "Wotlas Web Server unreachable. Trying again... (" + this.nbTry + "/12)");
        this.nbTry++;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}
