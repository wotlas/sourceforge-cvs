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

import wotlas.client.gui.*;
import wotlas.client.screen.*;

import wotlas.common.message.account.*;
import wotlas.common.message.description.*;
import wotlas.common.*;

import wotlas.libs.net.*;
import wotlas.libs.net.personality.*;

import wotlas.libs.graphics2D.FontFactory;
import wotlas.libs.sound.SoundLibrary;
import wotlas.libs.wizard.*;

import wotlas.utils.*;
import wotlas.utils.aswing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.event.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.*;

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
    final static public int FIRST_INIT              = -1;
    final static public int MAIN_SCREEN             =  0;
    final static public int ACCOUNT_LOGIN_SCREEN    =  1;
    final static public int DELETE_ACCOUNT_SCREEN   =  2;
    final static public int RECOVER_ACCOUNT_SCREEN  =  3;
    final static public int ACCOUNT_CREATION_SCREEN = 10;
    final static public int ACCOUNT_INFO_SCREEN     = 11;
    final static public int DATAMANAGER_DISPLAY     = 20;

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
    private ImageIcon im_okup, im_okdo, im_okun;
    private ImageIcon im_cancelup, im_canceldo, im_cancelun;
    private ImageIcon im_newup, im_newdo;
    private ImageIcon im_loadup, im_loaddo, im_loadun;
    private ImageIcon im_recoverup, im_recoverdo, im_recoverun;
    private ImageIcon im_delup, im_deldo, im_delun;
    private ImageIcon im_exitup, im_exitdo;
    private ImageIcon im_aboutup, im_aboutdo;
    private ImageIcon im_helpup, im_helpdo;
    private ImageIcon im_optionsup, im_optionsdo;

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
    static public void setRememberPasswords( boolean remember ) {
    	rememberPasswords = remember;
    }

  /** do we have to remember passwords ?
   */
    static public boolean getRememberPasswords() {
    	return rememberPasswords;
    }

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Loads/create the different config files but does not display anything.
   */
   public ClientManager( ResourceManager rManager ) {
       super();
       automaticLogin = false;

    // 1 - We load the ProfileConfigList
       profileConfigList = ProfileConfigList.load();

       if ( profileConfigList == null ) {
           Debug.signal( Debug.NOTICE, this, "no client's profile found : creating a new one..." );
           profileConfigList = new ProfileConfigList();
       }
       else
           Debug.signal( Debug.NOTICE, null, "Client Configs loaded with success !" );

       if(!rememberPasswords) {
          profileConfigList.deletePasswords(); // make sure we don't save any password here
          profileConfigList.save();
       }

    // 2 - We load the ServerConfigManager
       serverConfigManager = new ServerConfigManager( rManager );
       serverConfigManager.setRemoteServerConfigHomeURL( ClientDirector.getRemoteServerConfigHomeURL() );
       Debug.signal( Debug.NOTICE, null, "Server config Manager started with success !" );

    // 3 - We get the font we are going to use...
       f = FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter");
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the Profile Config.
   * @return the Profile Config
   */
   public ProfileConfigList getProfileConfigList() {
        return profileConfigList;
   }

  /** To get the current Profile Config.
   * @return the ProfilesConfig
   */
   public ProfileConfig getCurrentProfileConfig() {
        return currentProfileConfig;
   }

 /*------------------------------------------------------------------------------------*/

  /** To get the ServerConfigManager.
   *
   * @return the ServerConfigManager
   */
   public ServerConfigManager getServerConfigManager() {
        return serverConfigManager;
   }

 /*------------------------------------------------------------------------------------*/

  /** To set automatic login or not.
   *  Automatic login makes that the password prompt doesn't displays if
   *  there is already a password in memory.
   *
   *  This option is used only when we need to reconnect because the client
   *  account has moved to another server.
   */
   public void setAutomaticLogin( boolean automaticLogin ) {
      this.automaticLogin = automaticLogin;
   }

  /** Are we using automatic login or not ?
   */
   public boolean getAutomaticLogin() {
      return automaticLogin;
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
    final JTable serversTable ;

    indexScreen = state;

    switch(state) {

      // ********************
      // *** First Screen ***
      // ********************

      case FIRST_INIT:
         // We try to contact the wotlas web server...
        nbTry=1;
        Timer timer = new Timer(5000,this);
        timer.start();

        serverConfigManager.getLatestConfigFiles(this);
        timer.stop();

        if( !serverConfigManager.hasRemoteServersInfo() )
            JOptionPane.showMessageDialog( this, "We failed to contact the wotlas web server. So we could not update\n"+
                                                 "our servers addresses. If this is not the first time you start wotlas on\n"+
                                                 "your computer, you can try to connect with the previous server config\n"+
                                                 "files. Otherwise please restart wotlas later.\n\n"+
                                                 "Note also that wotlas is not firewall/proxy friendly. See our FAQ for\n"+
                                                 "more details ( from the help section or 'wotlas.html' local file ).",
                                                 "Warning", JOptionPane.WARNING_MESSAGE);
        else  // Wotlas News
            new JHTMLWindow( this, "Wotlas News", ClientDirector.getRemoteServerConfigHomeURL()+"news.html",
                             320, 400, false, ClientDirector.getResourceManager().getBase("gui") );

         // Load images of buttons
        im_cancelup    = ClientDirector.getResourceManager().getImageIcon("cancel-up.gif");
        im_canceldo    = ClientDirector.getResourceManager().getImageIcon("cancel-do.gif");
        im_cancelun    = ClientDirector.getResourceManager().getImageIcon("cancel-un.gif");
        im_okup    = ClientDirector.getResourceManager().getImageIcon("ok-up.gif");
        im_okdo    = ClientDirector.getResourceManager().getImageIcon("ok-do.gif");
        im_okun    = ClientDirector.getResourceManager().getImageIcon("ok-un.gif");
        im_recoverup = ClientDirector.getResourceManager().getImageIcon("recover-up.gif");
        im_recoverdo = ClientDirector.getResourceManager().getImageIcon("recover-do.gif");
        im_recoverun = ClientDirector.getResourceManager().getImageIcon("recover-un.gif");
        im_delup    = ClientDirector.getResourceManager().getImageIcon("delete-up.gif");
        im_deldo    = ClientDirector.getResourceManager().getImageIcon("delete-do.gif");
        im_delun    = ClientDirector.getResourceManager().getImageIcon("delete-un.gif");
        im_exitup   = ClientDirector.getResourceManager().getImageIcon("exit-up.gif");
        im_exitdo   = ClientDirector.getResourceManager().getImageIcon("exit-do.gif");      
        im_loadup   = ClientDirector.getResourceManager().getImageIcon("load-up.gif");
        im_loaddo   = ClientDirector.getResourceManager().getImageIcon("load-do.gif");
        im_loadun   = ClientDirector.getResourceManager().getImageIcon("load-un.gif");
        im_newup    = ClientDirector.getResourceManager().getImageIcon("new-up.gif");
        im_newdo    = ClientDirector.getResourceManager().getImageIcon("new-do.gif");
        im_aboutup  = ClientDirector.getResourceManager().getImageIcon("about-up.gif");
        im_aboutdo  = ClientDirector.getResourceManager().getImageIcon("about-do.gif");
        im_helpup  = ClientDirector.getResourceManager().getImageIcon("help-up.gif");
        im_helpdo  = ClientDirector.getResourceManager().getImageIcon("help-do.gif");
        im_optionsup = ClientDirector.getResourceManager().getImageIcon("options-up.gif");
        im_optionsdo  = ClientDirector.getResourceManager().getImageIcon("options-do.gif");

        indexScreen = MAIN_SCREEN;
        state = MAIN_SCREEN;

      // Test if an account exists
       if ( profileConfigList.size()==0 ) {
         start(ACCOUNT_CREATION_SCREEN);
         return;
       }

     case MAIN_SCREEN:

       setTitle("Wotlas - Account selection...");

       if( SoundLibrary.getSoundLibrary()!=null )
          SoundLibrary.getSoundLibrary().stopMusic();

      // Create panels
       leftPanel = new JPanel();
       leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
       rightPanel = new JPanel();
       rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, getRightWidth(), 5));

      // Create buttons
      b_about = new JButton(im_aboutup);
      b_about.setRolloverIcon(im_aboutdo);
      b_about.setPressedIcon(im_aboutdo);
      b_about.setBorderPainted(false);
      b_about.setContentAreaFilled(false);
      b_about.setFocusPainted(false);

      b_option = new JButton(im_optionsup);
      b_option.setRolloverIcon(im_optionsdo);
      b_option.setPressedIcon(im_optionsdo);
      b_option.setBorderPainted(false);
      b_option.setContentAreaFilled(false);
      b_option.setFocusPainted(false);

      b_ok = new JButton(im_okup);
      b_ok.setRolloverIcon(im_okdo);
      b_ok.setPressedIcon(im_okdo);
      b_ok.setDisabledIcon(im_okun);
      b_ok.setBorderPainted(false);
      b_ok.setContentAreaFilled(false);
      b_ok.setFocusPainted(false);

      b_cancel = new JButton(im_cancelup);
      b_cancel.setRolloverIcon(im_canceldo);
      b_cancel.setPressedIcon(im_canceldo);
      b_cancel.setDisabledIcon(im_cancelun);
      b_cancel.setBorderPainted(false);
      b_cancel.setContentAreaFilled(false);
      b_cancel.setFocusPainted(false);

      b_load = new JButton(im_loadup);
      b_load.setRolloverIcon(im_loaddo);
      b_load.setPressedIcon(im_loaddo);
      b_load.setDisabledIcon(im_loadun);
      b_load.setBorderPainted(false);
      b_load.setContentAreaFilled(false);
      b_load.setFocusPainted(false);

      b_help = new JButton(im_helpup);
      b_help.setRolloverIcon(im_helpdo);
      b_help.setPressedIcon(im_helpdo);
      b_help.setBorderPainted(false);
      b_help.setContentAreaFilled(false);
      b_help.setFocusPainted(false);

      b_newProfile = new JButton(im_newup);
      b_newProfile.setRolloverIcon(im_newdo);
      b_newProfile.setPressedIcon(im_newdo);
      b_newProfile.setDisabledIcon(im_newdo);
      b_newProfile.setBorderPainted(false);
      b_newProfile.setContentAreaFilled(false);
      b_newProfile.setFocusPainted(false);

      b_recoverProfile = new JButton(im_recoverup);
      b_recoverProfile.setRolloverIcon(im_recoverdo);
      b_recoverProfile.setPressedIcon(im_recoverdo);
      b_recoverProfile.setDisabledIcon(im_recoverun);
      b_recoverProfile.setBorderPainted(false);
      b_recoverProfile.setContentAreaFilled(false);
      b_recoverProfile.setFocusPainted(false);

      b_delProfile = new JButton(im_delup);
      b_delProfile.setRolloverIcon(im_deldo);
      b_delProfile.setPressedIcon(im_deldo);
      b_delProfile.setDisabledIcon(im_delun);
      b_delProfile.setBorderPainted(false);
      b_delProfile.setContentAreaFilled(false);
      b_delProfile.setFocusPainted(false);

      b_exitProfile = new JButton(im_exitup);
      b_exitProfile.setRolloverIcon(im_exitdo);
      b_exitProfile.setPressedIcon(im_exitdo);      
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

      leftPanel.add(Box.createRigidArea(new Dimension(0,10)));

      // Creates a table of profiles
      ProfileConfigListTableModel profileConfigListTabModel = new ProfileConfigListTableModel(profileConfigList, serverConfigManager);
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
      rowProfilesSM.addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          //Ignore extra messages.
          if (e.getValueIsAdjusting()) return;
          ListSelectionModel lsm = (ListSelectionModel) e.getSource();
          if (lsm.isSelectionEmpty()) {
            //no rows are selected
          } else {
            int selectedRow = lsm.getMinSelectionIndex();
            currentProfileConfig = profileConfigList.getProfiles()[selectedRow];
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
          public void actionPerformed (ActionEvent e) {
            start(ACCOUNT_LOGIN_SCREEN);
          }
        }
      );
      rightPanel.add(b_load);

      b_newProfile.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            start(ACCOUNT_CREATION_SCREEN);
          }
        }
      );
      rightPanel.add(b_newProfile);

      rightPanel.add( new JLabel( ClientDirector.getResourceManager().getImageIcon("separator.gif") ) );  // SEPARATOR

      b_recoverProfile.setEnabled(true);
      b_recoverProfile.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            start(RECOVER_ACCOUNT_SCREEN);
          }
        }
      );
      rightPanel.add(b_recoverProfile);

      b_delProfile.setEnabled(false);

      b_delProfile.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            start(DELETE_ACCOUNT_SCREEN);
          }
        }
      );

      rightPanel.add(b_delProfile);

      rightPanel.add( new JLabel( ClientDirector.getResourceManager().getImageIcon("separator.gif") ) );  // SEPARATOR

      b_option.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
             new JConfigurationDlg( ClientManager.this );
          }
        }
      );
      rightPanel.add(b_option);


      b_help.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
             new JHTMLWindow( ClientManager.this, "Help",
                 ClientDirector.getResourceManager().getHelp("index.html"),
                 640, 340, false,
                 ClientDirector.getResourceManager().getBase("gui") );
          }
        }
      );
      rightPanel.add(b_help);

      rightPanel.add( new JLabel( ClientDirector.getResourceManager().getImageIcon("separator.gif") ) );  // SEPARATOR

      b_about.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            try{
              new JAbout( ClientManager.this );
            }catch(RuntimeException ei ) {
              Debug.signal( Debug.ERROR, this, ei );
            }
          }
        }
      );
      rightPanel.add(b_about);

      b_exitProfile.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             ClientDirector.getDataManager().exit();
          }
        }
      );
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
      b_ok = new JButton(im_okup);
      b_ok.setRolloverIcon(im_okdo);
      b_ok.setPressedIcon(im_okdo);
      b_ok.setDisabledIcon(im_okun);
      b_ok.setBorderPainted(false);
      b_ok.setContentAreaFilled(false);
      b_ok.setFocusPainted(false);

      b_cancel = new JButton(im_cancelup);
      b_cancel.setRolloverIcon(im_canceldo);
      b_cancel.setPressedIcon(im_canceldo);
      b_cancel.setDisabledIcon(im_cancelun);
      b_cancel.setBorderPainted(false);
      b_cancel.setContentAreaFilled(false);
      b_cancel.setFocusPainted(false);

      // *** Left JPanel ***

      label1 = new ALabel("Welcome " + currentProfileConfig.getLogin() + ",");
      label1.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftPanel.add(label1);
      label1.setFont(f.deriveFont(18f));

      leftPanel.add(Box.createRigidArea(new Dimension(0,10)));
      JPanel mainPanel_01 = new JPanel();
        mainPanel_01.setBackground(Color.white);

        JPanel formPanel_01_left = new JPanel(new GridLayout(2,1,5,5));
          formPanel_01_left.setBackground(Color.white);
          formPanel_01_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("enter-password.gif")));
          formPanel_01_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("your-key.gif")));
        mainPanel_01.add(formPanel_01_left);
        JPanel formPanel_01_right = new JPanel(new GridLayout(2,1,5,10));
          formPanel_01_right.setBackground(Color.white);
          pfield1 = new APasswordField(10);
          pfield1.setFont(f.deriveFont(18f));

          if( currentProfileConfig.getPassword()!=null )
              pfield1.setText(currentProfileConfig.getPassword());
          
          pfield1.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
            if ( e.getKeyCode()==KeyEvent.VK_ENTER )
              b_ok.doClick();
            }
          });
          
          formPanel_01_right.add(pfield1);
          ALabel alabel = new ALabel(currentProfileConfig.getLogin()+"-"+currentProfileConfig.getKey());
          alabel.setFont(f.deriveFont(16f));
          formPanel_01_right.add(alabel);
        mainPanel_01.add(formPanel_01_right);
      leftPanel.add(mainPanel_01);

      // *** Right Panel ***

      b_ok.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
          char charPasswd[] = pfield1.getPassword();
          if (charPasswd.length < 4) {
            JOptionPane.showMessageDialog( ClientManager.this, "Password must have at least 5 characters !", "New Password", JOptionPane.ERROR_MESSAGE);
          } else {
            String passwd = new String(charPasswd);

            ClientDirector.getDataManager().setCurrentProfileConfig(currentProfileConfig);

            currentServerConfig = serverConfigManager.getServerConfig(currentProfileConfig.getServerID());

            JGameConnectionDialog jgconnect = new JGameConnectionDialog( ClientManager.this,
                    currentServerConfig.getServerName(), currentServerConfig.getGameServerPort(),
                    currentServerConfig.getServerID(),
                    currentProfileConfig.getLogin(), passwd, currentProfileConfig.getLocalClientID(),
                    currentProfileConfig.getOriginalServerID(), ClientDirector.getDataManager());

            if ( jgconnect.hasSucceeded() ) {
              currentProfileConfig.setPassword(passwd);

              if(rememberPasswords)
                 profileConfigList.save();

              Debug.signal( Debug.NOTICE, null, "ClientManager connected to GameServer");
              start(DATAMANAGER_DISPLAY);
            } else {
              Debug.signal( Debug.ERROR, this, "ClientManager ejected from GameServer");
              start(MAIN_SCREEN);
            }
          }
        }
      }
      );
      rightPanel.add(b_ok);

      b_cancel.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
            start(MAIN_SCREEN);
        }
      }
      );
      rightPanel.add(b_cancel);

      // *** Adding the panels ***
      setLeftPanel(leftPanel);
      setRightPanel(rightPanel);
      showScreen();

      if( automaticLogin && currentProfileConfig.getPassword()!=null ) {
      	 automaticLogin = false;  // works only once...
         b_ok.doClick();          // we launch the connection procedure
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
      b_delProfile = new JButton(im_delup);
      b_delProfile.setRolloverIcon(im_deldo);
      b_delProfile.setPressedIcon(im_deldo);
      b_delProfile.setDisabledIcon(im_delun);
      b_delProfile.setBorderPainted(false);
      b_delProfile.setContentAreaFilled(false);
      b_delProfile.setFocusPainted(false);

      b_cancel = new JButton(im_cancelup);
      b_cancel.setRolloverIcon(im_canceldo);
      b_cancel.setPressedIcon(im_canceldo);
      b_cancel.setDisabledIcon(im_cancelun);
      b_cancel.setBorderPainted(false);
      b_cancel.setContentAreaFilled(false);
      b_cancel.setFocusPainted(false);

      // *** Left JPanel ***

      label1 = new ALabel("Delete " + currentProfileConfig.getPlayerName() + " ?");
      label1.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftPanel.add(label1);
      label1.setFont(f.deriveFont(18f));

      leftPanel.add(Box.createRigidArea(new Dimension(0,10)));

      JPanel mainPanel_02 = new JPanel();
        mainPanel_02.setBackground(Color.white);
        JPanel formPanel_02_left = new JPanel(new GridLayout(2,1,5,5));
          formPanel_02_left.setBackground(Color.white);
          formPanel_02_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("enter-password.gif")));
          formPanel_02_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("your-key.gif")));
        mainPanel_02.add(formPanel_02_left);
        JPanel formPanel_02_right = new JPanel(new GridLayout(2,1,5,10));
          formPanel_02_right.setBackground(Color.white);
          pfield1 = new APasswordField(10);
          pfield1.setFont(f.deriveFont(18f));
          
          pfield1.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
            if ( e.getKeyCode()==KeyEvent.VK_ENTER )
              b_delProfile.doClick();
            }
          });

          if( currentProfileConfig.getPassword()!=null )
              pfield1.setText(currentProfileConfig.getPassword());
          
          formPanel_02_right.add(pfield1);
          ALabel alabel2 = new ALabel(currentProfileConfig.getLogin()+"-"+currentProfileConfig.getKey());
          alabel2.setFont(f.deriveFont(16f));
          formPanel_02_right.add(alabel2);
        mainPanel_02.add(formPanel_02_right);
      leftPanel.add(mainPanel_02);

      // *** Right Panel ***

      b_delProfile.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
          char charPasswd[] = pfield1.getPassword();
          String passwd = "";
          if (charPasswd.length < 4) {
            JOptionPane.showMessageDialog( ClientManager.this, "Password must have at least 5 characters !", "New Password", JOptionPane.ERROR_MESSAGE);
          } else {
            for (int i=0; i<charPasswd.length; i++) {
              passwd += charPasswd[i];
            }

            currentServerConfig = serverConfigManager.getServerConfig(currentProfileConfig.getServerID());

            JDeleteAccountDialog jdconnect = new JDeleteAccountDialog( ClientManager.this,
                    currentServerConfig.getServerName(), currentServerConfig.getAccountServerPort(),
                    currentServerConfig.getServerID(),
                    currentProfileConfig.getLogin()+"-"+
                    currentProfileConfig.getOriginalServerID()+"-"+
                    currentProfileConfig.getLocalClientID(), passwd );

            if ( jdconnect.hasSucceeded() ) {
                 Debug.signal( Debug.NOTICE, this, "Account deleted.");

              // Save accounts informations
                 if( !profileConfigList.removeProfile(currentProfileConfig) )
                    Debug.signal( Debug.ERROR, this, "Failed to delete player profile !" );
                 else
                    profileConfigList.save();
            }

            start(MAIN_SCREEN); // return to main screen
          }
        }
      }
      );
      rightPanel.add(b_delProfile);

      b_cancel.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
            start(MAIN_SCREEN);
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
      b_ok = new JButton(im_okup);
      b_ok.setRolloverIcon(im_okdo);
      b_ok.setPressedIcon(im_okdo);
      b_ok.setDisabledIcon(im_okun);
      b_ok.setBorderPainted(false);
      b_ok.setContentAreaFilled(false);
      b_ok.setFocusPainted(false);

      b_cancel = new JButton(im_cancelup);
      b_cancel.setRolloverIcon(im_canceldo);
      b_cancel.setPressedIcon(im_canceldo);
      b_cancel.setDisabledIcon(im_cancelun);
      b_cancel.setBorderPainted(false);
      b_cancel.setContentAreaFilled(false);
      b_cancel.setFocusPainted(false);
      
      
      // *** Left JPanel ***

      label1 = new ALabel("To recover an existing account, please enter :");
      label1.setAlignmentX(Component.CENTER_ALIGNMENT);
      label1.setFont(f.deriveFont(18f));
      leftPanel.add(label1);

      leftPanel.add(Box.createRigidArea(new Dimension(0,10)));

      JPanel mainPanel_03 = new JPanel();
        mainPanel_03.setBackground(Color.white);
        JPanel formPanel_03_left = new JPanel(new GridLayout(2,1,5,5));
          formPanel_03_left.setBackground(Color.white);
          formPanel_03_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("your-key.gif")));
          formPanel_03_left.add(new JLabel(ClientDirector.getResourceManager().getImageIcon("enter-password.gif")));
        mainPanel_03.add(formPanel_03_left);
        JPanel formPanel_03_right = new JPanel(new GridLayout(2,1,5,10));
          formPanel_03_right.setBackground(Color.white);
          atf_key = new ATextField(10);
          formPanel_03_right.add(atf_key);
          
          pfield1 = new APasswordField(10);
          pfield1.setFont(f.deriveFont(18f));
          pfield1.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
            if ( e.getKeyCode()==KeyEvent.VK_ENTER )
              b_ok.doClick();
            }
          });
          formPanel_03_right.add(pfield1);
          
        mainPanel_03.add(formPanel_03_right);
      leftPanel.add(mainPanel_03);

      // *** Right Panel ***

      b_ok.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
          char charPasswd[] = pfield1.getPassword();

          if (charPasswd.length < 4) {
            JOptionPane.showMessageDialog( ClientManager.this, "Password must have at least 5 characters !", "Password", JOptionPane.ERROR_MESSAGE);
          } else {
                    
            currentProfileConfig = new ProfileConfig();
            
            String tempKey = atf_key.getText();
            int index = tempKey.indexOf('-');
            if (index<0) {
              JOptionPane.showMessageDialog( ClientManager.this, "Your key must have the following format : login-xx-yy.\nExample: bob-1-36", "Bad Format", JOptionPane.ERROR_MESSAGE);
              start(MAIN_SCREEN);
              return;
            }

            currentProfileConfig.setLogin(tempKey.substring(0,index));
            currentProfileConfig.setPlayerName("");
            currentProfileConfig.setPassword(new String(charPasswd));

            tempKey = tempKey.substring(index+1);
            index = tempKey.indexOf('-');

            if (index<0) {
              JOptionPane.showMessageDialog( ClientManager.this, "Your key must have the following format : login-xx-yy.\nExample: bob-1-36", "Bad Format", JOptionPane.ERROR_MESSAGE);
              start(MAIN_SCREEN);   
              return;
            }         

            try {
              currentProfileConfig.setServerID(Integer.parseInt(tempKey.substring(0,index)));
              currentProfileConfig.setOriginalServerID(Integer.parseInt(tempKey.substring(0,index)));
              currentProfileConfig.setLocalClientID(Integer.parseInt(tempKey.substring(index+1)));
            } catch (NumberFormatException nfes) {
              JOptionPane.showMessageDialog( ClientManager.this, "Your key must have the following format : login-xx-yy.\nExample: bob-1-36", "Bad Format", JOptionPane.ERROR_MESSAGE);
              start(MAIN_SCREEN);
              return;
            }
            
            ClientDirector.getDataManager().setCurrentProfileConfig(currentProfileConfig);
            currentServerConfig = serverConfigManager.getServerConfig(currentProfileConfig.getServerID());

            if(currentServerConfig==null) {
              JOptionPane.showMessageDialog( ClientManager.this, "Failed to find the associated server.", "ERROR", JOptionPane.ERROR_MESSAGE);
              start(MAIN_SCREEN);
              return;
            }

            JGameConnectionDialog jgconnect = new JGameConnectionDialog( ClientManager.this,
                    currentServerConfig.getServerName(), currentServerConfig.getGameServerPort(),
                    currentServerConfig.getServerID(),
                    currentProfileConfig.getLogin(), new String(charPasswd), currentProfileConfig.getLocalClientID(),
                    currentProfileConfig.getOriginalServerID(), ClientDirector.getDataManager());

            if ( jgconnect.hasSucceeded() ) {
              Debug.signal( Debug.NOTICE, null, "ClientManager connected to GameServer");              
              jgconnect.getPersonality().queueMessage(new AccountRecoverMessage( atf_key.getText()) );
              
              start(DATAMANAGER_DISPLAY);
            } else {
              Debug.signal( Debug.ERROR, this, "ClientManager ejected from GameServer");
              start(MAIN_SCREEN);
            }
          }
        }
      }
      );
      rightPanel.add(b_ok);

      b_cancel.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
            start(MAIN_SCREEN);
        }
      }
      );
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

      serverConfigManager.getLatestConfigFiles(this);

      // Launching Wizard
      hide();
      JAccountCreationWizard accountCreationWz = new JAccountCreationWizard();
      break;
      
    
    // **************************************
    // *** A new account has been created ***
    // **************************************

    case ACCOUNT_INFO_SCREEN:

      setTitle("Wotlas - Account creation...");

      // Set the appropriate server config.
      currentServerConfig = serverConfigManager.getServerConfig(currentProfileConfig.getServerID());

      // Create panels
      leftPanel = new JPanel();
      //leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
      leftPanel.setLayout(new GridLayout(1,1,5,5));
      rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, getRightWidth(), 10));

      // Create buttons
      b_ok = new JButton(im_okup);
      b_ok.setRolloverIcon(im_okdo);
      b_ok.setPressedIcon(im_okdo);
      b_ok.setDisabledIcon(im_okun);
      b_ok.setBorderPainted(false);
      b_ok.setContentAreaFilled(false);
      b_ok.setFocusPainted(false);

      b_cancel = new JButton(im_cancelup);
      b_cancel.setRolloverIcon(im_canceldo);
      b_cancel.setPressedIcon(im_canceldo);
      b_cancel.setDisabledIcon(im_cancelun);
      b_cancel.setBorderPainted(false);
      b_cancel.setContentAreaFilled(false);
      b_cancel.setFocusPainted(false);

      // *** Left Panel ***/
      JEditorPane editorPane = new JEditorPane("text/html","<html>Your new account has been <br>"
                            + "successfully created! <br>"
                            + "Remember your key to access <br>"
                            + "wotlas from anywhere : <b>" + currentProfileConfig.getLogin()+"-"+currentProfileConfig.getKey()
                            + "</b><br>Click OK to enter WOTLAS....</html>");
      leftPanel.add(editorPane, BorderLayout.CENTER);
      editorPane.setEditable(false);

      // *** Right Panel ***/

      b_ok.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {       
            JGameConnectionDialog jgconnect = new JGameConnectionDialog( ClientManager.this,
              currentServerConfig.getServerName(), currentServerConfig.getGameServerPort(),
              currentServerConfig.getServerID(),
              currentProfileConfig.getLogin(), currentProfileConfig.getPassword(),
              currentProfileConfig.getLocalClientID(), currentProfileConfig.getOriginalServerID(),
              ClientDirector.getDataManager());

            if ( jgconnect.hasSucceeded() ) {
              Debug.signal( Debug.NOTICE, null, "ClientManager connected to GameServer");
              start(DATAMANAGER_DISPLAY);
            } else {
              Debug.signal( Debug.ERROR, this, "ClientManager ejected from GameServer");
            }
          }
        }
      );
      rightPanel.add(b_ok);

      b_cancel.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            start(MAIN_SCREEN);
          }
        }
      );
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
      ClientDirector.getDataManager().showInterface();

      break;

    default:
      // We should never arrive here
      // --> return to main screen
         start(MAIN_SCREEN);
    }
  }

 /*------------------------------------------------------------------------------------*/

   /** To add a new profile to the player's profile list. This method is called by
    *  the AccountCreationEndedMsgBehaviour when an account has been successfully created.
    */
    public void addNewProfile( int clientID, int serverID, String login, String password, String playerName) {

      currentProfileConfig = new ProfileConfig();

      // Set profile data
      currentProfileConfig.setPlayerName(playerName);
      currentProfileConfig.setLogin(login);
      currentProfileConfig.setPassword(password);
      currentProfileConfig.setLocalClientID(clientID);
      currentProfileConfig.setOriginalServerID(serverID);
      currentProfileConfig.setServerID(serverID);
      
      // Save profile informations
      profileConfigList.addProfile(currentProfileConfig);
      profileConfigList.save();

      // Set Data Manager ready
      ClientDirector.getDataManager().setCurrentProfileConfig(currentProfileConfig);
   }

 /*------------------------------------------------------------------------------------*/

 /** Timer Event interception
  * @param e supposed timer event
  */
   public void actionPerformed( ActionEvent e) {
       Debug.signal(Debug.WARNING,null,"Wotlas Web Server unreachable. Trying again... ("+nbTry+"/12)");
       nbTry++;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}