/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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
import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigList;
import wotlas.common.ServerConfigListTableModel;

import wotlas.libs.net.*;
import wotlas.libs.net.personality.*;

import wotlas.utils.ALabel;
import wotlas.utils.APasswordField;
import wotlas.utils.ATableCellRenderer;
import wotlas.utils.ATextField;
import wotlas.utils.Debug;
import wotlas.utils.SwingTools;
import wotlas.utils.Tools;

import java.awt.*;
import java.awt.event.*;

import javax.swing.event.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.*;

public class ClientManager
{

 /*------------------------------------------------------------------------------------*/

  /** Our Default ClientManager.
   */
  private static ClientManager clientManager;

  /** Our ProfileConfigList file.
   */
  private ProfileConfigList profileConfigList;

  /** Current profileConfig
   */
  private ProfileConfig currentProfileConfig;

  /** Our ServerConfigList file.
   */
  private ServerConfigList serverConfigList;

  /** Current serverConfig
   */
  ServerConfig currentServerConfig;

  /** Generic interface of Wotlas client
   */
  public JIntroWizard screenIntro;

  /** Index of current screen shown
   */
  private int indexScreen;

  /** Path to the local server database.
   */
  private String databasePath;

  /** pictures of buttons
   */
  private ImageIcon im_okup, im_okdo, im_okun;
  private ImageIcon im_cancelup, im_canceldo, im_cancelun;
  private ImageIcon im_newup, im_newdo;
  private ImageIcon im_loadup, im_loaddo, im_loadun;
  private ImageIcon im_delup, im_deldo, im_delun;
  private ImageIcon im_exitup, im_exitdo;

  /** Default font
   */
  private Font f;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Attemps to load the config/client-profiles.cfg file...
   */
  private ClientManager(String databasePath) {
    this.databasePath = databasePath;

    PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();

    // 1 - We load the ProfileConfigList
    profileConfigList = pm.loadProfileConfigs();
    if (profileConfigList == null) {
      Debug.signal( Debug.NOTICE, this, "no client's profile found : creating a new one..." );
    } else {
      Debug.signal( Debug.NOTICE, null, "Client Configs loaded with success !" );
    }

    // 2 - We load the ServerConfigList
    serverConfigList = new ServerConfigList(pm);
    if (serverConfigList == null) {
      Debug.signal( Debug.FAILURE, this, "No Server Configs loaded !" );
      Debug.exit();
    } else {
      Debug.signal( Debug.NOTICE, null, "Server Configs loaded with success !" );
    }

    // 3 - We create the wizard to connect Wotlas
    screenIntro = new JIntroWizard();
    screenIntro.setGUI();
    f = SwingTools.loadFont("../base/fonts/Lblack.ttf");

  }

 /*------------------------------------------------------------------------------------*/

  /** Creates a client manager. Attemps to load the config/client.cfg file...
   *
   * @return the created (or previously created) client manager.
   */
  public static ClientManager createClientManager(String databasePath) {
    if (clientManager == null)
      clientManager = new ClientManager(databasePath);
    return clientManager;
   }

 /*------------------------------------------------------------------------------------*/

  /** To get the default client manager.
   *
   * @return the default client manager.
   */
  public static ClientManager getDefaultClientManager() {
    return clientManager;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the ProfilesConfig.
   *
   * @return the ProfilesConfig
   */
  public ProfileConfigList getProfileConfigList() {
    return profileConfigList;
  }

 /*------------------------------------------------------------------------------------*/

  /** Starts the Wizard at the beginning of the game
   */
  public void start(int state)
  {
    JPanel leftPanel;
    JPanel rightPanel;

    JPanel tempPanel;
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
    final APasswordField pfield1;
    final APasswordField pfield2;

    final JButton b_ok;
    final JButton b_cancel;
    final JButton b_delProfile;
    final JButton b_exitProfile;

    final JTable profilesTable;
    final JTable serversTable ;

    indexScreen = state;

    switch(state)
    {

      // ********************
      // *** First Screen ***
      // ********************

      case 0:
      new JHTMLWindow( screenIntro, "Wotlas News", "http://wotlas.sf.net/game/news.html", 320, 400, false );

      screenIntro.setTitle("Wotlas - Account selection...");

      // Load images of buttons
      im_cancelup = new ImageIcon("..\\base\\gui\\cancel-up.gif");
      im_canceldo = new ImageIcon("..\\base\\gui\\cancel-do.gif");
      im_cancelun = new ImageIcon("..\\base\\gui\\cancel-un.gif");
      im_delup    = new ImageIcon("..\\base\\gui\\delete-up.gif");
      im_deldo    = new ImageIcon("..\\base\\gui\\delete-do.gif");
      im_delun    = new ImageIcon("..\\base\\gui\\delete-un.gif");
      im_exitup   = new ImageIcon("..\\base\\gui\\exit-up.gif");
      im_exitdo   = new ImageIcon("..\\base\\gui\\exit-do.gif");      
      im_loadup   = new ImageIcon("..\\base\\gui\\load-up.gif");
      im_loaddo   = new ImageIcon("..\\base\\gui\\load-do.gif");
      im_loadun   = new ImageIcon("..\\base\\gui\\load-un.gif");
      im_newup    = new ImageIcon("..\\base\\gui\\new-up.gif");
      im_newdo    = new ImageIcon("..\\base\\gui\\new-do.gif");
      im_okup     = new ImageIcon("..\\base\\gui\\ok-up.gif");
      im_okdo     = new ImageIcon("..\\base\\gui\\ok-do.gif");
      im_okun     = new ImageIcon("..\\base\\gui\\ok-un.gif");      

      // Test if an account exists
      if (profileConfigList==null) {
        profileConfigList = new ProfileConfigList();
        start(10);
        return;
      }

      // Create panels
      leftPanel = new JPanel();
      leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
      rightPanel = new JPanel();
      rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, screenIntro.getRightWidth(), 10));

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

      JButton b_newProfile = new JButton(im_newup);
      b_newProfile.setRolloverIcon(im_newdo);
      b_newProfile.setPressedIcon(im_newdo);
      b_newProfile.setDisabledIcon(im_newdo);
      b_newProfile.setBorderPainted(false);
      b_newProfile.setContentAreaFilled(false);
      b_newProfile.setFocusPainted(false);

      JButton b_loadProfile = new JButton(im_loadup);
      b_loadProfile.setRolloverIcon(im_loaddo);
      b_loadProfile.setPressedIcon(im_loaddo);
      b_loadProfile.setDisabledIcon(im_loadun);
      b_loadProfile.setBorderPainted(false);
      b_loadProfile.setContentAreaFilled(false);
      b_loadProfile.setFocusPainted(false);

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

      imgLabel1 = new JLabel(new ImageIcon("..\\base\\gui\\welcome-title.jpg"));
      imgLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftPanel.add(imgLabel1);

      imgLabel2 = new JLabel(new ImageIcon("..\\base\\gui\\choose.gif"));
      imgLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftPanel.add(imgLabel2);

      leftPanel.add(Box.createRigidArea(new Dimension(0,10)));

      // Creates a table of profiles
      // data
      ProfileConfigListTableModel profileConfigListTabModel = new ProfileConfigListTableModel(profileConfigList, serverConfigList);
      /*profilesTable = new JTable(profileConfigListTabModel) {
        public String getToolTipText(MouseEvent event) {
          if (getSelectedRow() > -1) {
            currentServerConfig = serverConfigList.ServerConfigAt(getSelectedRow());
            String str = currentServerConfig.getServerName() + " : " + currentServerConfig.getDescription() + " .";
            return str;
          } else {
            return null;
          }
        }
      };*/
      profilesTable = new JTable(profileConfigListTabModel);
      profilesTable.setDefaultRenderer(Object.class, new ATableCellRenderer());
      profilesTable.setBackground(Color.white);
      profilesTable.setForeground(Color.black);
      profilesTable.setSelectionBackground(Color.lightGray);
      profilesTable.setSelectionForeground(Color.white);
      profilesTable.setRowHeight(24);
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
            //selectedRow is selected
            //currentServerConfig = serverConfigList.ServerConfigAt(selectedRow);
            //profilesTable.setToolTipText(currentServerConfig.getDescription());
            currentProfileConfig = profileConfigList.getProfiles()[selectedRow];
            b_ok.setEnabled(true);
            b_delProfile.setEnabled(true);
          }
        }
      });
      // show table
      scrollPane = new JScrollPane(profilesTable);
      profilesTable.setPreferredScrollableViewportSize(new Dimension(0, 100));
      scrollPane.getViewport().setBackground(Color.white);
      JScrollBar jsb_01 = scrollPane.getVerticalScrollBar();
      leftPanel.add(scrollPane);

      // *** Right Panel ***

      b_ok.setEnabled(false);
      b_ok.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            start(indexScreen+1);
          }
        }
      );
      rightPanel.add(b_ok);

      b_cancel.setEnabled(false);
      rightPanel.add(b_cancel);

      b_newProfile.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            start(10);
          }
        }
      );
      rightPanel.add(b_newProfile);

      rightPanel.add(b_loadProfile);

      b_delProfile.setEnabled(false);
      rightPanel.add(b_delProfile);

      b_exitProfile.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            DataManager.getDefaultDataManager().exit();
          }
        }
      );
      rightPanel.add(b_exitProfile);
      
      // *** Adding the panels ***

      screenIntro.setLeftPanel(leftPanel);
      screenIntro.setRightPanel(rightPanel);
      screenIntro.showScreen();
      break;

    // ********************************
    // *** Connection to GameServer ***
    // ********************************

    case 1:
      screenIntro.setTitle("Wotlas - Account selection...");

      // Create panels
      leftPanel = new JPanel();
      leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
      rightPanel = new JPanel();
      //rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, screenIntro.getRightWidth(), 10));

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

      leftPanel.add(Box.createRigidArea(new Dimension(0,10)));

      JPanel mainPanel_01 = new JPanel();
        mainPanel_01.setBackground(Color.white);
        JPanel formPanel_01_left = new JPanel(new GridLayout(2,1,5,5));
          formPanel_01_left.setBackground(Color.white);
          formPanel_01_left.add(new JLabel(new ImageIcon("..\\base\\gui\\enter-password.gif")));
          formPanel_01_left.add(new JLabel(new ImageIcon("..\\base\\gui\\your-key.gif")));
        mainPanel_01.add(formPanel_01_left);
        JPanel formPanel_01_right = new JPanel(new GridLayout(2,1,5,10));
          formPanel_01_right.setBackground(Color.white);
          pfield1 = new APasswordField(10);
          pfield1.setFont(f.deriveFont(18f));
          formPanel_01_right.add(pfield1);
          formPanel_01_right.add(new ALabel(currentProfileConfig.getKey()));
        mainPanel_01.add(formPanel_01_right);
      leftPanel.add(mainPanel_01);

      // *** Right Panel ***

      b_ok.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
          char charPasswd[] = pfield1.getPassword();
          String passwd = "";
          if (charPasswd.length < 4) {
            JOptionPane.showMessageDialog( screenIntro, "Password mut have at least 5 characters !", "New Password", JOptionPane.ERROR_MESSAGE);                          
          } else {
            for (int i=0; i<charPasswd.length; i++) {
              passwd += charPasswd[i];
            }

            DataManager.getDefaultDataManager().setCurrentProfileConfig(currentProfileConfig);

            currentServerConfig = serverConfigList.getServerConfig(currentProfileConfig.getServerID());

            JGameConnectionDialog jgconnect = new JGameConnectionDialog( screenIntro,
                    currentServerConfig.getServerName(), currentServerConfig.getGameServerPort(),
                    currentProfileConfig.getLogin(), passwd, currentProfileConfig.getLocalClientID(),
                    currentProfileConfig.getOriginalServerID(), DataManager.getDefaultDataManager());

            if ( jgconnect.hasSucceeded() ) {
              Debug.signal( Debug.NOTICE, null, "ClientManager connected to GameServer");
              start(100);
            } else {
              Debug.signal( Debug.ERROR, this, "ClientManager ejected from GameServer");
            }
          }
        }
      }
      );
      rightPanel.add(b_ok);

      b_cancel.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
            start(0);
        }
      }
      );
      rightPanel.add(b_cancel);

      // *** Adding the panels ***

      screenIntro.setLeftPanel(leftPanel);
      screenIntro.setRightPanel(rightPanel);
      screenIntro.showScreen();
      break;

    // ***********************************
    // *** Connection to AccountServer ***
    // ***********************************

    case 10:
      screenIntro.setTitle("Wotlas - Account creation...");

      // Account creation
      currentProfileConfig = new ProfileConfig();

      // Create panels
      leftPanel = new JPanel();
      leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
      rightPanel = new JPanel();
      rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, screenIntro.getRightWidth(), 10));

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

      imgLabel1 = new JLabel(new ImageIcon("..\\base\\gui\\complete-info.gif"));
      imgLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftPanel.add(imgLabel1);

      leftPanel.add(Box.createRigidArea(new Dimension(0,10)));

      JPanel mainPanel_10 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel_10.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel_10.setBackground(Color.white);
        JPanel formPanel_10 = new JPanel(new GridLayout(3,2,5,5));
          formPanel_10.setBackground(Color.white);
          formPanel_10.add(new JLabel(new ImageIcon("..\\base\\gui\\login.gif")));
          atf_login = new ATextField(10);
          atf_login.setSelectionColor(Color.lightGray);
          atf_login.setSelectedTextColor(Color.white);
          formPanel_10.add(atf_login);
          formPanel_10.add(new JLabel(new ImageIcon("..\\base\\gui\\password.gif")));
          pfield1 = new APasswordField(10);
          pfield1.setFont(f.deriveFont(18f));
          pfield1.setSelectionColor(Color.lightGray);
          pfield1.setSelectedTextColor(Color.white);
          formPanel_10.add(pfield1);
          formPanel_10.add(new JLabel(new ImageIcon("..\\base\\gui\\password.gif")));
          pfield2 = new APasswordField(10);
          pfield2.setFont(f.deriveFont(18f));
          pfield2.setSelectionColor(Color.lightGray);
          pfield2.setSelectedTextColor(Color.white);
          formPanel_10.add(pfield2);
        mainPanel_10.add(formPanel_10);
      leftPanel.add(mainPanel_10);

      leftPanel.add(Box.createRigidArea(new Dimension(0,10)));

      imgLabel2 = new JLabel(new ImageIcon("..\\base\\gui\\choose-server.gif"));
      imgLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftPanel.add(imgLabel2);

      // Creates a table of servers
      // data
      ServerConfigListTableModel serverConfigListTabModel = new ServerConfigListTableModel(serverConfigList);
      serversTable = new JTable(serverConfigListTabModel);
      serversTable.setDefaultRenderer(Object.class, new ATableCellRenderer());
      serversTable.setBackground(Color.white);
      serversTable.setForeground(Color.black);
      serversTable.setSelectionBackground(Color.lightGray);
      serversTable.setSelectionForeground(Color.white);
      serversTable.setRowHeight(24);
      // selection
      serversTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      ListSelectionModel rowServerSM = serversTable.getSelectionModel();
      rowServerSM.addListSelectionListener(new ListSelectionListener()
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
            //selectedRow is selected
            currentServerConfig = serverConfigList.ServerConfigAt(selectedRow);
            //serversTable.setToolTipText(currentServerConfig.getDescription());
            currentProfileConfig.setOriginalServerID(currentServerConfig.getServerID());
            currentProfileConfig.setServerID(currentServerConfig.getServerID());
            b_ok.setEnabled(true);
          }
        }
      });
      // show table
      scrollPane = new JScrollPane(serversTable);
      serversTable.setPreferredScrollableViewportSize(new Dimension(0, 100));
      scrollPane.getViewport().setBackground(Color.white);
      leftPanel.add(scrollPane);

      // *** Right Panel ***

      b_ok.setEnabled(false);
      b_ok.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
          // Verify the fields
          if (atf_login.getText().length() == 0) {
            JOptionPane.showMessageDialog( screenIntro, "Login cannot be empty !", "New Login", JOptionPane.ERROR_MESSAGE);
            return;
          }          
          char charPasswd1[] = pfield1.getPassword();          
          char charPasswd2[] = pfield2.getPassword();          
          if ( (charPasswd1.length < 4) || (charPasswd2.length < 4) ) {
            JOptionPane.showMessageDialog( screenIntro, "Password mut have at least 5 characters !", "New Password", JOptionPane.ERROR_MESSAGE);
            return;
          } else {
            //b_ok.setEnabled(false);
            String passwd = "";            
            if (charPasswd1.length != charPasswd2.length) {
              JOptionPane.showMessageDialog( screenIntro, "Password are not identical", "New Password", JOptionPane.ERROR_MESSAGE);
              pfield1.setText("");
              pfield2.setText("");
              return;
            }            
            
            for (int i=0; i<charPasswd1.length; i++) {
              if (charPasswd1[i] != charPasswd2[i]) {
                JOptionPane.showMessageDialog( screenIntro, "Password are not identical", "New Password", JOptionPane.ERROR_MESSAGE);
                pfield1.setText("");
                pfield2.setText("");
                return;
              }   
              passwd += charPasswd1[i];
            }

            // Account creation

            currentProfileConfig.setLogin(atf_login.getText());
            currentProfileConfig.setPassword(passwd);

            DataManager dataManager = DataManager.getDefaultDataManager();
            dataManager.setCurrentProfileConfig(currentProfileConfig);

            screenIntro.hide();
            
            JAccountConnectionDialog jaconnect = new JAccountConnectionDialog( screenIntro,
                       currentServerConfig.getServerName(), currentServerConfig.getAccountServerPort(),
                       dataManager);

            if ( jaconnect.hasSucceeded() ) {
              Debug.signal( Debug.NOTICE, null, "ClientManager connected to AccountServer");
            } else {
              Debug.signal( Debug.NOTICE, null, "ClientManager ejected from AccountServer");
              return;
            }

          }
        }
      });
      rightPanel.add(b_ok);


      b_cancel.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            start(0);
          }
        }
      );
      rightPanel.add(b_cancel);

      if (profileConfigList.size() == 0) {
        b_cancel.setEnabled(false);
      }

      // *** Adding the panels ***

      screenIntro.setLeftPanel(leftPanel);
      screenIntro.setRightPanel(rightPanel);
      screenIntro.showScreen();
      break;

    // **************************************
    // *** A new account has been created ***
    // **************************************

    case 11:
      screenIntro.setTitle("Wotlas - Account creation...");      
      
      // Save accounts informations
      profileConfigList.addProfile(currentProfileConfig);
      PersistenceManager.getDefaultPersistenceManager().saveProfilesConfig(profileConfigList);

      // Create panels
      leftPanel = new JPanel();
      //leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
      rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, screenIntro.getRightWidth(), 10));

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
                            + "wotlas from anywhere : " + currentProfileConfig.getKey()
                            + "<br>Click OK to enter WOTLAS....</html>");
      leftPanel.add(editorPane, BorderLayout.CENTER);
     
      // *** Right Panel ***/

      b_ok.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
            JGameConnectionDialog jgconnect = new JGameConnectionDialog( screenIntro,
              currentServerConfig.getServerName(), currentServerConfig.getGameServerPort(),
              currentProfileConfig.getLogin(), currentProfileConfig.getPassword(),
              currentProfileConfig.getLocalClientID(), currentProfileConfig.getOriginalServerID(),
              DataManager.getDefaultDataManager());

            if ( jgconnect.hasSucceeded() ) {
              Debug.signal( Debug.NOTICE, null, "ClientManager connected to GameServer");
              start(100);
            } else {
              Debug.signal( Debug.ERROR, this, "ClientManager ejected from GameServer");
            }
          }
        }
      );
      rightPanel.add(b_ok);

      b_cancel.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            start(0);
          }
        }
      );
      rightPanel.add(b_cancel);

      // *** Adding the panels ***

      screenIntro.setLeftPanel(leftPanel);
      screenIntro.setRightPanel(rightPanel);
      screenIntro.showScreen();
      break;

    // ********************
    // *** Final screen ***
    // ********************

    case 100:

      screenIntro.dispose();
      DataManager dataManager = DataManager.getDefaultDataManager();
      dataManager.showInterface();
      break;

    default:

      // end of the wizard
      screenIntro.closeScreen();
    }
  }
}