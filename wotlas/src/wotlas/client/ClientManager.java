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

  /** Our ProfilesConfig file.
   */
  private ProfilesConfig profilesConfig;
  
  /** Generic interface of Wotlas client
   */
  public JIntroWizard screenIntro;
  
  /** Index of current screen shown
   */
  private int indexScreen;
  
  /** Current profile
   */
  private Profile currentProfile;
  
  private NetPersonality myNetPersonality;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Attemps to load the config/client-profiles.cfg file...   
   */  
  private ClientManager() {
    
    // 1 - We load the ProfilesConfig file...
    PersistenceManager pm = PersistenceManager.getDefaultPersistenceManager();
    profilesConfig = pm.loadProfilesConfig();
    
    if (profilesConfig == null) {
      Debug.signal( Debug.FAILURE, this, "Can't init client's profile without a ProfilesConfig file !" );
      System.exit(1);
    }
    
    // 2 - We create the wizard to connect Wotlas
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
    if(clientManager == null)
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
  public ProfilesConfig getProfilesConfig() {
    return profilesConfig;
  }

 /*------------------------------------------------------------------------------------*/

  /** Starts the Wizard at the beginning of the game
   */
  public void start(int state)
  {
    JPanel leftPanel;
    JPanel rightPanel;
    
    JLabel label1;
    JLabel label2;
    JLabel label3;
    JLabel label4;
    JLabel label5;
    JTextField tfield1;
    final JPasswordField pfield1;
    final JButton b_ok;
    final JButton b_cancel;
    final JButton b_delProfile;
    
    indexScreen = state;    
    
    switch(state)
    {
      case 0:      

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
      ProfilesTableModel profilesTabModel = new ProfilesTableModel(profilesConfig.getProfiles());      
      JTable profilesTable = new JTable(profilesTabModel);      
      profilesTable.setOpaque(false);
      profilesTable.setPreferredScrollableViewportSize(new Dimension(200, 50)); 
      // selection
      profilesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      ListSelectionModel rowSM = profilesTable.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener()
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
            currentProfile = profilesConfig.getProfiles()[selectedRow];
            b_ok.setEnabled(true);
            b_delProfile.setEnabled(true);
          }
        }
      });
      // show table
      JScrollPane scrollPane = new JScrollPane(profilesTable);    
      scrollPane.setOpaque(false);       
      leftPanel.add(scrollPane);      
      
      // *** Adding the panels ***
      screenIntro.setLeftPanel(leftPanel);
      screenIntro.setRightPanel(rightPanel);      
      screenIntro.showScreen();
      break;
      
    case 1:      
    
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
            wotlas.client.Client.FalseClient falseC = new wotlas.client.Client.FalseClient( currentProfile.getLogin(), passwd );            
            wotlas.client.screen.JGameConnectionDialog jconnect = new wotlas.client.screen.JGameConnectionDialog( new JFrame(), 
                    currentProfile.getServerName(), 26500, currentProfile.getLogin(), passwd, ""+currentProfile.getLocalClientID(),
                    ""+currentProfile.getOriginalServerID(),  falseC );
            if ( jconnect.hasSucceeded() ) {
              System.out.println("connected");
              myNetPersonality = jconnect.getPersonality();
            } else {
              System.out.println("not connected");
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
      
      label1 = new JLabel("Welcome " + currentProfile.getLogin());
      leftPanel.add(label1);
      label2 = new JLabel("type your password to access Wotlas :");
      leftPanel.add(label2);
      
      leftPanel.add(pfield1);
      label3 = new JLabel("your serial : " + currentProfile.getSerial());
      leftPanel.add(label3);            
      
      
      
      // *** Adding the panels ***      
      screenIntro.setLeftPanel(leftPanel);
      screenIntro.setRightPanel(rightPanel);
      screenIntro.showScreen();
      break;
    
    case 2:        
    
    case 3:
    
      
    default:
      
      // end of the wizard      
      screenIntro.closeScreen();      
    }
  }
}