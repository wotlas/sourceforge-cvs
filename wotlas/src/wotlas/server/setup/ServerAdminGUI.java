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

import wotlas.common.*;
import wotlas.server.*;
import wotlas.utils.*;
import wotlas.libs.aswing.*;

import wotlas.libs.wizard.*;

import wotlas.libs.graphics2D.FontFactory;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Properties;

/** The main class for setups on the server side.
 *
 * @author Aldiss
 */

public class ServerAdminGUI extends JFrame {

 /*------------------------------------------------------------------------------------*/

  /** Our World Manager
   */
    private static WorldManager worldManager;

  /** TextEditorPanel for our transfer script
   */
    private static TextEditorPanel transferScript;

 /*------------------------------------------------------------------------------------*/

  /** Our tabbed pane.
   */
    private JTabbedPane adminTabbedPane;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public ServerAdminGUI() {
         super("Server Administration");

      // 1 - GUI construction
         Font f = FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter");

         JPanel mainPanel = new JPanel();
         mainPanel.setLayout(new BorderLayout());
         mainPanel.setBackground(Color.white);

         adminTabbedPane = new JTabbedPane();
         adminTabbedPane.setPreferredSize( new Dimension(570,400) );
         mainPanel.add( adminTabbedPane, BorderLayout.CENTER );

         JPanel exitPanel = new JPanel();
         exitPanel.setLayout(new GridLayout(1,4));
         exitPanel.setBackground(Color.white);

         exitPanel.add( getWhiteLabel() );
         exitPanel.add( getWhiteLabel() );
         exitPanel.add( getWhiteLabel() );

         ImageIcon im_exitup = ServerDirector.getResourceManager().getImageIcon("exit-up.gif");
         ImageIcon im_exitdo   = ServerDirector.getResourceManager().getImageIcon("exit-do.gif");

         JButton b_exitProfile = new JButton(im_exitup);
         b_exitProfile.setRolloverIcon(im_exitdo);
         b_exitProfile.setPressedIcon(im_exitdo);      
         b_exitProfile.setBorderPainted(false);
         b_exitProfile.setContentAreaFilled(false);
         b_exitProfile.setFocusPainted(false);
         exitPanel.add( b_exitProfile );

           b_exitProfile.addActionListener(new ActionListener() {
              public void actionPerformed (ActionEvent e) {
                  Debug.exit();
              }
           });

         mainPanel.add( exitPanel, BorderLayout.SOUTH );
         getContentPane().add(mainPanel);

      // 2 - Add the tabbed panels
         JPanel setupPanel = new JPanel();
         setupPanel.setLayout(new GridLayout(2,1,10,10));
         setupPanel.setBackground(Color.white);
         setupPanel.setBorder(BorderFactory.createEmptyBorder(5,10,0,10));

           ATextArea taInfo = new ATextArea("\nWelcome to the Wotlas Server Administration Setup !\n\n\n"
                                           +"        You'll find here all the utilities needed to configure your server. "
                                           +"If it's the first time you start this setup you'll probably want to "
                                           +"edit/register your server configuration (click on the button below). "
                                           +"You can also use the Setup Wizard (button below) to update your server's "
                                           +"config.\n");

           taInfo.setLineWrap(true);
           taInfo.setWrapStyleWord(true);
           taInfo.setEditable(false);
           taInfo.setBackground(Color.white);
           taInfo.setFont(f.deriveFont(14f));
           setupPanel.add( taInfo );

           JPanel launchWizardPanel = new JPanel();
           launchWizardPanel.setLayout(new GridLayout(3,3,10,10));
           launchWizardPanel.setBackground(Color.white);
           launchWizardPanel.add(getWhiteLabel());

           AButton b_wizard = new AButton("Click here to launch the Setup Wizard.");
           b_wizard.setPreferredSize(new Dimension(200,30));
           b_wizard.setFont(f.deriveFont(14f));
           launchWizardPanel.add( b_wizard );

           b_wizard.addActionListener(new ActionListener() {
              public void actionPerformed (ActionEvent e) {
                  setGUI();
                  new ServerSetup();
              }
           });

           launchWizardPanel.add(getWhiteLabel());
           setupPanel.add(launchWizardPanel);

         adminTabbedPane.addTab( "Setup",
                                 ServerDirector.getResourceManager().getImageIcon("pin.gif"),
                                 setupPanel,
                                 "Register & Setup your Server Config" );


         transferScript = new TextEditorPanel( ServerDirector.getResourceManager(),
                           ServerDirector.getResourceManager().getExternalTransferScript(),
                           "This is the script we'll use for automatic config transfer :", true );

         adminTabbedPane.addTab( "Transfer Script",
                                 ServerDirector.getResourceManager().getImageIcon("pin.gif"),
                                 transferScript,
                                 "Edit/View your file transfer script:" );

         JPanel manualPanel = new JPanel();
         manualPanel.setLayout(new GridLayout(2,1,10,10));
         manualPanel.setBackground(Color.white);
         manualPanel.setBorder(BorderFactory.createEmptyBorder(5,10,0,10));

           ATextArea taInfo2 = new ATextArea("\n\n        If you chose to never send automatically your new server's IP "
                                              +"when it changes, you'll need to use the wizard below for manual update. "
                                              +"It will perform the same operation as the transfer script.\n");

           taInfo2.setLineWrap(true);
           taInfo2.setWrapStyleWord(true);
           taInfo2.setEditable(false);
           taInfo2.setBackground(Color.white);
           taInfo2.setFont(f.deriveFont(14f));
           manualPanel.add( taInfo2 );

           JPanel launchWizardPanel2 = new JPanel();
           launchWizardPanel2.setLayout(new GridLayout(3,3,10,10));
           launchWizardPanel2.setBackground(Color.white);
           launchWizardPanel2.add(getWhiteLabel());

           AButton b_wizard2 = new AButton("Click here to launch the Manual Transfer Wizard.");
           b_wizard2.setPreferredSize(new Dimension(200,30));
           b_wizard2.setFont(f.deriveFont(14f));
           launchWizardPanel2.add( b_wizard2 );

           b_wizard2.addActionListener(new ActionListener() {
              public void actionPerformed (ActionEvent e) {
                  new ServerAddressSetup();
              }
           });

           launchWizardPanel2.add(getWhiteLabel());
           manualPanel.add(launchWizardPanel2);

         adminTabbedPane.addTab( "Manual Transfer",
                                 ServerDirector.getResourceManager().getImageIcon("pin.gif"),
                                 manualPanel,
                                 "Manual IP File Transfer" );

         adminTabbedPane.addTab( "Maps",
                                 ServerDirector.getResourceManager().getImageIcon("pin.gif"),
                                 new ServerMapSetup(),
                                 "Edit the server IDs of the maps" );

         adminTabbedPane.addTab( "Start-up",
                                 ServerDirector.getResourceManager().getImageIcon("pin.gif"),
                                 new TextEditorPanel( ServerDirector.getResourceManager(),
                                                      ServerDirector.getResourceManager().getExternalConfigsDir()+"server.cfg",
                                                      "Here is your server's start-up config (Press 'Load' to refresh) :",false),
                                 "See your server's start-up config" );

         adminTabbedPane.addTab( "Remote",
                                 ServerDirector.getResourceManager().getImageIcon("pin.gif"),
                                 new TextEditorPanel( ServerDirector.getResourceManager(),
                                                      ServerDirector.getResourceManager().getExternalConfigsDir()+"remote-servers.cfg",
                                                      "Here is your 'remote servers' config here (Press 'Load' to refresh) :",false),
                                 "Edit your remote-servers config" );

      // 3 - Prepare the whole & Show it.
         pack();
         setLocation(200,100);
         show();
    }

 /*------------------------------------------------------------------------------------*/

  /** To get a white empty label
   */
    private JLabel getWhiteLabel() {
        JLabel l = new JLabel(" ");
        l.setBackground(Color.white);
        return l;
    }

 /*------------------------------------------------------------------------------------*/

  /** Inits and starts the admin utility.
   */
    static public void create() {

        // STEP 1 - Some inits...
           Debug.signal(Debug.NOTICE,null,"Starting Admin GUI...");

           FontFactory.createDefaultFontFactory( ServerDirector.getResourceManager() );
           Debug.signal( Debug.NOTICE, null, "Font factory created..." );

           worldManager = new WorldManager( ServerDirector.getResourceManager(), true, false );

         // STEP 2 - Start the admin GUI
           new ServerAdminGUI();
    }

 /*------------------------------------------------------------------------------------*/

  /** To get our world Manager
   */
    static public WorldManager getWorldManager() {
        return worldManager;
    }

 /*------------------------------------------------------------------------------------*/

  /** To get our transfer script.
   */
    static public TextEditorPanel getTransferScript() {
        return transferScript;
    }

 /*------------------------------------------------------------------------------------*/

  /** Set the colors and fonts
   */
  static public void setGUI() {
    Font f;
    
    f = new Font("Monospaced", Font.PLAIN, 12);
    UIManager.put("Button.font", f);

    UIManager.put("ComboBox.font", f.deriveFont(14f));
    UIManager.put("ComboBox.foreground", Color.black);

    f = FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter");

    UIManager.put("Label.font", f.deriveFont(14f));
    UIManager.put("Label.foreground", Color.black);

    UIManager.put("TextArea.font", f.deriveFont(14f));
    UIManager.put("TextArea.foreground", Color.black);

    UIManager.put("TextField.font", f.deriveFont(14f));
    UIManager.put("TextField.foreground", Color.black);
    
    UIManager.put("PasswordField.font", f.deriveFont(14f));
    UIManager.put("PasswordField.foreground", Color.black);
    
    UIManager.put("RadioButton.font", f.deriveFont(14f));
    UIManager.put("RadioButton.foreground", Color.black);
            
    UIManager.put("Table.font", f.deriveFont(14f));
    UIManager.put("Table.foreground", Color.black);    
    
    UIManager.put("TableHeader.font", f.deriveFont(16f));
    UIManager.put("TableHeader.foreground", Color.black);
    
    UIManager.put("CheckBox.font", f.deriveFont(14f));
    UIManager.put("CheckBox.foreground", Color.black);
  }
  
 /*--------------------------------------------------------------------------*/

  /** Set the colors and fonts
   */
  static public void setAdminGUI() {
    Font f;
    
    f = new Font("Dialog", Font.PLAIN, 12);
    UIManager.put("Button.font", f);

    UIManager.put("Label.font", f.deriveFont(14f));
    UIManager.put("Label.foreground", Color.black);

    UIManager.put("TextArea.font", f.deriveFont(14f));
    UIManager.put("TextArea.foreground", Color.black);

    UIManager.put("TextField.font", f.deriveFont(14f));
    UIManager.put("TextField.foreground", Color.black);

//    f = FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter");

    UIManager.put("ComboBox.font", f.deriveFont(14f));
    UIManager.put("ComboBox.foreground", Color.black);
    
    UIManager.put("PasswordField.font", f.deriveFont(14f));
    UIManager.put("PasswordField.foreground", Color.black);
    
    UIManager.put("RadioButton.font", f.deriveFont(14f));
    UIManager.put("RadioButton.foreground", Color.black);
            
    UIManager.put("Table.font", f.deriveFont(14f));
    UIManager.put("Table.foreground", Color.black);    
    
    UIManager.put("TableHeader.font", f.deriveFont(16f));
    UIManager.put("TableHeader.foreground", Color.black);
    
    UIManager.put("CheckBox.font", f.deriveFont(14f));
    UIManager.put("CheckBox.foreground", Color.black);
  }
  
 /*--------------------------------------------------------------------------*/

}


