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

import wotlas.common.ServerConfigList;
import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigListTableModel;

import wotlas.client.gui.*;
import wotlas.client.screen.*;
import wotlas.utils.Tools;
import wotlas.utils.Debug;

import wotlas.libs.net.*;
import wotlas.libs.net.personality.*;
import wotlas.common.message.account.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

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

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Attemps to load the config/client-profiles.cfg file...
   */
  private ClientManager() {
    
    PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
    
    // 1 - We load the ProfileConfigList    
    profileConfigList = pm.loadProfileConfigs();
    if (profileConfigList == null) {
      Debug.signal( Debug.FAILURE, this, "Can't init client's profile without a ProfileConfigList file !" );
      //System.exit(1);
    } else {
      Debug.signal( Debug.NOTICE, null, "Client Configs loaded with success !" );      
    }

    // 2 - We load the ServerConfigList
    serverConfigList = new ServerConfigList(pm);    
    if (serverConfigList == null) {
      Debug.signal( Debug.FAILURE, this, "No Server Configs loaded !" );
      System.exit(1);
    } else {
      Debug.signal( Debug.NOTICE, null, "Server Configs loaded with success !" );      
    }
    
    // 3 - We create the wizard to connect Wotlas
    screenIntro = new JIntroWizard();
    // set the different colors of fonts and buttons
    screenIntro.setGUI();
  }

 /*------------------------------------------------------------------------------------*/

  /** Creates a client manager. Attemps to load the config/client.cfg file...
   *
   * @return the created (or previously created) client manager.
   */
  public static ClientManager createClientManager() {
    if (clientManager == null)
      clientManager = new ClientManager();
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
    
    JLabel label1;
    JLabel label2;
    JLabel label3;
    JLabel label4;
    JLabel label5;
    
    final JTextField tfield1;
    final JPasswordField pfield1;

    final JButton b_ok;
    final JButton b_cancel;
    final JButton b_delProfile;

    indexScreen = state;

    switch(state)
    {
      
      // ********************
      // *** First Screen *** 
      // ********************
      
      case 0:
      screenIntro.setTitle("Wotlas - Account selection...");
      
      // Test if an account exits
      if (profileConfigList==null) {
        profileConfigList = new ProfileConfigList();
        start(10);
        return;
      }      
      
      // *** Right Panel ***
      rightPanel = new JPanel();
      rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, screenIntro.getRightWidth(), 10));

      b_ok = new JButton("ok");
      b_ok.setEnabled(false);
      b_ok.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            start(indexScreen+1);
          }
        }
      );
      rightPanel.add(b_ok);

      b_cancel = new JButton("cancel");
      b_cancel.setEnabled(false);
      rightPanel.add(b_cancel);

      JButton b_newProfile = new JButton("New ");
      b_newProfile.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            start(10);
          }
        }
      );
      rightPanel.add(b_newProfile);

      JButton b_loadProfile = new JButton("Load ");
      rightPanel.add(b_loadProfile);

      b_delProfile = new JButton("Delete ");
      b_delProfile.setEnabled(false);
      rightPanel.add(b_delProfile);

      // *** Left JPanel ***
      leftPanel = new JPanel();
      leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, screenIntro.getLeftWidth(), 20));

      label1 = new JLabel("Welcome to WOTLAS");
      leftPanel.add(label1);
      label2 = new JLabel("Choose your profile :");
      leftPanel.add(label2);

      // Creates a table of profiles
      // data
      ProfileConfigListTableModel profileConfigListTabModel = new ProfileConfigListTableModel(profileConfigList, serverConfigList);
      JTable profilesTable = new JTable(profileConfigListTabModel);
      profilesTable.setOpaque(false);
      profilesTable.setPreferredScrollableViewportSize(new Dimension(200, 100));
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
            currentProfileConfig = profileConfigList.getProfiles()[selectedRow];
            b_ok.setEnabled(true);
            b_delProfile.setEnabled(true);
          }
        }
      });
      // show table
      scrollPane = new JScrollPane(profilesTable);
      scrollPane.setOpaque(false);
      leftPanel.add(scrollPane);

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
      
      pfield1 = new JPasswordField(15);

      // *** Right Panel ***
      rightPanel = new JPanel();
      rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, screenIntro.getRightWidth(), 10));

      b_ok = new JButton("ok");
      b_ok.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
          char charPasswd[] = pfield1.getPassword();
          String passwd = "";
          if (charPasswd.length < 6) {
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

      b_cancel = new JButton("cancel");
      b_cancel.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
            start(0);
        }
      }
      );
      rightPanel.add(b_cancel);

      // *** Left JPanel ***
      leftPanel = new JPanel();
      leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, screenIntro.getLeftWidth(), 20));

      label1 = new JLabel("Welcome " + currentProfileConfig.getLogin());
      leftPanel.add(label1);
      label2 = new JLabel("type your password to access Wotlas :");
      leftPanel.add(label2);

      leftPanel.add(pfield1);
      label3 = new JLabel("your key : " + currentProfileConfig.getKey());
      leftPanel.add(label3);

      // *** Adding the panels ***
      screenIntro.setLeftPanel(leftPanel);
      screenIntro.setRightPanel(rightPanel);
      screenIntro.showScreen();
      break;

    case 2:
    case 3:
      break;
    
    // ***********************************
    // *** Connection to AccountServer ***
    // ***********************************

    case 10:
      screenIntro.setTitle("Wotlas - Account creation...");
    
      // Account creation
      currentProfileConfig = new ProfileConfig();

      // *** Right Panel ***
      rightPanel = new JPanel();
      rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, screenIntro.getRightWidth(), 10));

      b_ok = new JButton("ok");
      b_ok.setEnabled(false);

      // *** Left JPanel ***
      leftPanel = new JPanel();
      leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, screenIntro.getRightWidth(), 20));

      label3 = new JLabel("Complete the informations:");
      leftPanel.add(label3);
      
      tempPanel = new JPanel(new GridLayout(2,2,5,5));
      
      label1 = new JLabel("login");
      tempPanel.add(label1);
      tfield1 = new JTextField(10);
      tempPanel.add(tfield1);
      label2 = new JLabel("password:");
      tempPanel.add(label2);
      pfield1 = new JPasswordField(10);
      tempPanel.add(pfield1);

      tempPanel.setOpaque(false);
      leftPanel.add(tempPanel);

      label4 = new JLabel("Choose a server for your account:");
      leftPanel.add(label4);

      // Creates a table of servers
      // data
      ServerConfigListTableModel serverConfigListTabModel = new ServerConfigListTableModel(serverConfigList);
      JTable serversTable = new JTable(serverConfigListTabModel);
      
      serversTable.setPreferredScrollableViewportSize(new Dimension(250, 80));
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
            currentProfileConfig.setOriginalServerID(currentServerConfig.getServerID());
            currentProfileConfig.setServerID(currentServerConfig.getServerID());            
            b_ok.setEnabled(true);            
          }
        }
      });
      // show table
      serversTable.setOpaque(false);      
      scrollPane = new JScrollPane(serversTable);
      scrollPane.setOpaque(false);
      leftPanel.add(scrollPane);

      
      
      b_ok.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
          b_ok.setEnabled(false);
          char charPasswd[] = pfield1.getPassword();
          String passwd = "";
          if (charPasswd.length < 6) {
            JOptionPane.showMessageDialog( screenIntro, "Password mut have at least 5 characters !", "New Password", JOptionPane.ERROR_MESSAGE);
          } else {
            for (int i=0; i<charPasswd.length; i++) {
              passwd += charPasswd[i];
            }

            // Account creation
           
            currentProfileConfig.setLogin(tfield1.getText());
            currentProfileConfig.setPassword(passwd);
                        
            DataManager dataManager = DataManager.getDefaultDataManager();
            dataManager.setCurrentProfileConfig(currentProfileConfig);            

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

      b_cancel = new JButton("cancel");
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
      
      // *** Left Panel ***/
            
      leftPanel = new JPanel();
      leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, screenIntro.getLeftWidth(), 20));
      
      label1 = new JLabel();
      label1.setFont(new Font("Dialog", Font.BOLD, 16));
      label1.setText("<html>Your new account has been"
                            + "<br>successfully created!<br>" 
                            + "Remember your key to access wotlas<br>"
                            + "from anywhere : " + currentProfileConfig.getKey()
                            + "</center><br>Click OK to enter WOTLAS....</html>");      
      
      leftPanel.add(label1);
      
      // *** Right Panel ***/      
      rightPanel = new JPanel();
      
      b_ok = new JButton("ok");
      b_ok.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {                    
            JGameConnectionDialog jgconnect = new JGameConnectionDialog( screenIntro,
              currentServerConfig.getServerName(), currentServerConfig.getGameServerPort(),
              currentProfileConfig.getLogin(), currentProfileConfig.getPassword(),
              currentProfileConfig.getLocalClientID(), currentProfileConfig.getOriginalServerID(),
              DataManager.getDefaultDataManager());

            if ( jgconnect.hasSucceeded() ) {
              Debug.signal( Debug.NOTICE, null, "client connected to GameServer");
              start(100);
            } else {
              Debug.signal( Debug.ERROR, this, "client ejected from GameServer");
            }
          }
        }     
      );
      rightPanel.add(b_ok);
      
      b_cancel = new JButton("cancel");
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
      screenIntro.setTitle("Wotlas - Welcome !");
      
      JOptionPane.showMessageDialog( screenIntro, "Wotlas client", "Welcome !", JOptionPane.INFORMATION_MESSAGE);
      break;      
      
    default:

      // end of the wizard
      screenIntro.closeScreen();
    }
  }
}