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
import wotlas.libs.wizard.step.*;
import wotlas.libs.log.*;

import wotlas.libs.graphics2D.FontFactory;
import wotlas.libs.net.utils.NetInterface;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Properties;

/** A small utility to register/update the server config.
 *
 * @author Aldiss
 * @see wotlas.common.ServerConfig
 */

public class ServerSetup extends JWizard {

 /*------------------------------------------------------------------------------------*/

  /** Setup Command Line Help
   */
    public final static String SETUP_COMMAND_LINE_HELP =
            "Usage: ServerSetup -[help|base <path>]\n\n"
           +"Examples : \n"
           +"  ServerSetup -base ../base : sets the data location.\n\n"
           +"If the -base option is not set we search for data in "+ResourceManager.DEFAULT_BASE_PATH
           +"\n\n";

 /*------------------------------------------------------------------------------------*/

   /** Towns Name & initial position - TEMP will be replaced by a more oo...
    */
    public final static String TOWNS_NAME[] = {
          "Near Tar Valon",
          "In the Blight",
          "Near Shayol Ghul",
          "Near Caemlyn",
          "Near Cairhien",
          "Near Tear",
          "Near Illian",
          "Near Edou Bar",
          "Near Stedding Shangtai",
          "Near Amador"
    };

    public final static int TOWNS_NEAR_POSITION[][] = {
          { 743, 277 },  // position near Tar Valon on the World Map
          { 778, 135 },  // position near the Blight on the World Map
          { 815, 84  },  // ... etc ...
          { 724, 441 },
          { 811, 360 },
          { 769, 608 },
          { 651, 674 },
          { 489, 443 },
          { 935, 541 },
          { 394, 565 }
    };

 /*------------------------------------------------------------------------------------*/

   /** Our current serverID
    */
     private static int serverID;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public ServerSetup() {
         super("Server Config",
               ServerDirector.getResourceManager(),
               FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter").deriveFont(18f),
               470,450);
         setLocation(200,100);
         serverID = ServerDirector.getServerID(); // we get the current server ID

       // We display first step
          try{
               init( WelcomeWizardStep.getStaticParameters() );
          }
          catch( WizardException we ) {
               we.printStackTrace();
               Debug.exit(); // init failed !
          }
    }

 /*------------------------------------------------------------------------------------*/

  /** Called when wizard is finished (after last step's end).
   */
   protected void onFinished(Object context) {
           dispose();
   }

 /*------------------------------------------------------------------------------------*/

  /** Called when wizard is canceled ('cancel' button pressed).
   */
   protected void onCanceled(Object context) {
         dispose();
   }

 /*------------------------------------------------------------------------------------*/

 /**
  * First Step of our JWizard. A welcome message.
  */
  public static class WelcomeWizardStep extends JWizardStepInfo {

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** This is a static JWizardStep, to build it more simply this method
      *  returns the JWizardStepParameters needed for the JWizard.
      */
       public static JWizardStepParameters getStaticParameters() {
          JWizardStepParameters param = new JWizardStepParameters(
                          "wotlas.server.setup.ServerSetup$WelcomeWizardStep",
                          "Welcome !" );

          param.setIsPrevButtonEnabled(false);
          param.setIsDynamic(true);

          param.setProperty("init.info0", "\n\n\n       This wizard will help you configure your wotlas"
                                         +" server. Click on 'next' to continue.");
          return param;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Constructor.
      */
       public WelcomeWizardStep() {
           super();
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called each time the step is shown on screen.
      */
       protected void onShow(Object context, JWizard wizard) {
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when the "Next" button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Next" button action, false to cancel it...
      */
       protected boolean onNext(Object context, JWizard wizard) {
           // we move on to the next step
              wizard.setNextStep(  RegisterChoicesWizardStep.getStaticParameters()  );
              return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when Previous button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Previous" button action, false to cancel it...
      */
       protected boolean onPrevious(Object context, JWizard wizard) {
             return false; // should never been reached
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

    }

 /*------------------------------------------------------------------------------------*/

 /**
  * Second Step of our JWizard. Register choices.
  */
  public static class RegisterChoicesWizardStep extends JWizardStepRadio {

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** This is a static JWizardStep, to build it more simply this method
      *  returns the JWizardStepParameters needed for the JWizard.
      */
       public static JWizardStepParameters getStaticParameters() {
          JWizardStepParameters param = new JWizardStepParameters(
                          "wotlas.server.setup.ServerSetup$RegisterChoicesWizardStep",
                          "Register Choice" );

          param.setIsPrevButtonEnabled(true);
          param.setIsDynamic(false);

          param.setProperty("init.label0", "Here are the options you have :");

          param.setProperty("init.nbChoices", "4");

          param.setProperty("init.choice0", "Create a public wotlas server.");
          param.setProperty("init.info0", "      With this option your server will be added to the list of our public Internet servers.\n");

          param.setProperty("init.choice1", "Create a private wotlas server.");
          param.setProperty("init.info1", "      With this option you can choose which wotlas network you want to join for your server ( "
                                         +"with the wotlas manager package everyone can create his/her own wotlas network ).");

          param.setProperty("init.choice2", "Create a local server.");
          param.setProperty("init.info2", "      With this option your server will remain local to your computer. It will not be published on the Internet. "
                                         +"This is a fast & easy solution for running a server locally and perform some tests.");

          param.setProperty("init.choice3", "Update your server's config.");
          param.setProperty("init.info3", "      Choose this option if you want to edit/view your previously created server config.\n");
          return param;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Constructor.
      */
       public RegisterChoicesWizardStep() {
           super();
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called each time the step is shown on screen.
      */
       protected void onShow(Object context, JWizard wizard) {
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when the "Next" button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Next" button action, false to cancel it...
      */
       protected boolean onNext(Object context, JWizard wizard) {
            switch(getChoice()) {
            	case 0:
                   wizard.setNextStep(  ServerIdWizardStep.getStaticParameters()  );
                   break;

            	case 1:
                   wizard.setNextStep(  PrivateServerWizardStep.getStaticParameters()  );
                   break;

            	case 2:
            	   serverID=0;
                   wizard.setNextStep(  ServerInterfaceWizardStep.getStaticParameters()  );
                   break;

            	case 3:
                   wizard.setNextStep(  ServerInterfaceWizardStep.getStaticParameters()  );
                   break;
            }

            return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when Previous button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Previous" button action, false to cancel it...
      */
       protected boolean onPrevious(Object context, JWizard wizard) {
              wizard.setNextStep(  WelcomeWizardStep.getStaticParameters()  );
              return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

    }

 /*------------------------------------------------------------------------------------*/

 /**
  * Second Step of our JWizard. Register choices.
  */
  public static class ServerIdWizardStep extends JWizardStep1TextField {

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** This is a static JWizardStep, to build it more simply this method
      *  returns the JWizardStepParameters needed for the JWizard.
      */
       public static JWizardStepParameters getStaticParameters() {
          JWizardStepParameters param = new JWizardStepParameters(
                          "wotlas.server.setup.ServerSetup$ServerIdWizardStep",
                          "Server Identifier" );

          param.setIsPrevButtonEnabled(true);
          param.setIsDynamic(false);

          param.setProperty("init.label0", "Enter your server ID :");

          param.setProperty("init.info0", "\n      To obtain a valid server Id you should contact this address : "
                                         +""+ServerDirector.getRemoteServersProperties().getProperty("info.remoteServerAdminEmail","")
                                         +". Just send a mail to that address and ask for an Id."
                                         +" Once you have your Id enter it here and click on 'next'.");
          return param;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Constructor.
      */
       public ServerIdWizardStep() {
           super();
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called each time the step is shown on screen.
      */
       protected void onShow(Object context, JWizard wizard) {
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when the "Next" button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Next" button action, false to cancel it...
      */
       protected boolean onNext(Object context, JWizard wizard) {
            if( !super.onNext(context,wizard) )
       	        return false;

            try{
               serverID = Integer.parseInt(getText0());
            }catch(Exception ex) {
               JOptionPane.showMessageDialog( null, "Bad numeric format !", "Error", JOptionPane.ERROR_MESSAGE );
               return false;
            }

            wizard.setNextStep(  ServerInterfaceWizardStep.getStaticParameters()  );
            return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when Previous button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Previous" button action, false to cancel it...
      */
       protected boolean onPrevious(Object context, JWizard wizard) {
            wizard.setNextStep(  RegisterChoicesWizardStep.getStaticParameters()  );
            return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

    }

 /*------------------------------------------------------------------------------------*/

 /**
  * Second Step of our JWizard. Register choices.
  */
  public static class PrivateServerWizardStep extends JWizardStep2TextField {

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** This is a static JWizardStep, to build it more simply this method
      *  returns the JWizardStepParameters needed for the JWizard.
      */
       public static JWizardStepParameters getStaticParameters() {
          JWizardStepParameters param = new JWizardStepParameters(
                          "wotlas.server.setup.ServerSetup$PrivateServerWizardStep",
                          "Private Wotlas Network" );

          param.setIsPrevButtonEnabled(true);
          param.setIsDynamic(true);

          param.setProperty("init.label0", "Wotlas web server's URL:");
          param.setProperty("init.text0", ServerDirector.getRemoteServersProperties().getProperty("info.remoteServerHomeURL","") );

          param.setProperty("init.label1", "Wotlas manager's email:");
          param.setProperty("init.text1", ServerDirector.getRemoteServersProperties().getProperty("info.remoteServerAdminEmail","") );

          param.setProperty("init.info0", "\n      We need this information to know how to contact a specified"
                                         +" wotlas network. If you don't know them please refer to the"
                                         +" web site where you downloaded this package.");
          return param;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Constructor.
      */
       public PrivateServerWizardStep() {
           super();
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called each time the step is shown on screen.
      */
       protected void onShow(Object context, JWizard wizard) {
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when the "Next" button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Next" button action, false to cancel it...
      */
       protected boolean onNext(Object context, JWizard wizard) {
       	  if( !super.onNext(context,wizard) )
       	      return false;

          int value = JOptionPane.showConfirmDialog(null, "Save this information ? (required for next step)", "Save", JOptionPane.YES_NO_OPTION);
          if( value != JOptionPane.YES_OPTION ) return false;

          ServerDirector.getRemoteServersProperties().setProperty( "info.remoteServerHomeURL", getText0() );
          ServerDirector.getRemoteServersProperties().setProperty( "info.remoteServerAdminEmail", getText1() );

          wizard.setNextStep( ServerIdWizardStep.getStaticParameters()  );
          return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when Previous button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Previous" button action, false to cancel it...
      */
       protected boolean onPrevious(Object context, JWizard wizard) {
            wizard.setNextStep(  RegisterChoicesWizardStep.getStaticParameters() );
            return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

    }

 /*------------------------------------------------------------------------------------*/

 /**
  * Second Step of our JWizard. Server Interface Choice.
  */
  public static class ServerInterfaceWizardStep extends JWizardStep implements ActionListener{

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** This is a static JWizardStep, to build it more simply this method
      *  returns the JWizardStepParameters needed for the JWizard.
      */
       public static JWizardStepParameters getStaticParameters() {
          JWizardStepParameters param = new JWizardStepParameters(
                          "wotlas.server.setup.ServerSetup$ServerInterfaceWizardStep",
                          "Server Network Interface Setup" );

          param.setIsPrevButtonEnabled(true);
          param.setIsDynamic(true);
          return param;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

      /** Swing Components
       */
       private ATextField t_itfName, t_itfPublish;
       private ALabel l_itfDesc,l1,l2,l3,l4;
       private JPanel autoPanel, fixedPanel;

       private JComboBox c_itfName,c_ipName;
       private ARadioButton r_automatic, r_fixed, r_publishSelect, r_publishFixed,
                            r_automaticUpdate, r_manualUpdate;
       private ButtonGroup btGroupItf, btGroupPublish, btGroupUpdate;

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Constructor.
      */
       public ServerInterfaceWizardStep() {
           super();

        // I - Swing components
           setLayout( new BorderLayout() );
           JPanel mainPanel = new JPanel();
           mainPanel.setLayout( new BorderLayout() );
           mainPanel.setBackground(Color.white);
           add(mainPanel,BorderLayout.CENTER);
           mainPanel.setBorder(BorderFactory.createEmptyBorder(0,15,0,10));

         // First Radio section
            r_automatic = new ARadioButton("Automatic Interface Detection & Monitoring");
            r_automatic.setActionCommand("automaticItf");
            r_automatic.addActionListener(this);

            r_fixed = new ARadioButton("Fixed IP/DNS name");
            r_fixed.setActionCommand("fixedItf");
            r_fixed.addActionListener(this);

            btGroupItf = new ButtonGroup();
            btGroupItf.add(r_automatic);
            btGroupItf.add(r_fixed);

         // Automatic Itf Detection components
            autoPanel = new JPanel(new GridLayout(3,2,0,0));
            autoPanel.setBackground(Color.white);
            autoPanel.setBorder(BorderFactory.createEmptyBorder(0,50,0,10));
            
            l1 = new ALabel("Network Interfaces :");
            l1.setBackground(Color.white);
            autoPanel.add( l1 );

            String itf[] = NetInterface.getInterfaceNames();
            c_itfName = new JComboBox( itf );
            c_itfName.setEditable(false);
            if(itf.length!=0) c_itfName.setSelectedIndex(0);
            c_itfName.addActionListener(this);
            c_itfName.setActionCommand("itf");
            autoPanel.add( c_itfName );

            l2 = new ALabel("Interface Description:");
            l2.setBackground(Color.white);
            autoPanel.add( l2 );

            l_itfDesc = new ALabel("-");
            if(itf.length!=0) l_itfDesc.setText( NetInterface.getInterfaceDescription(itf[0]) );
            l_itfDesc.setBackground(Color.white);
            autoPanel.add( l_itfDesc );

            l3 = new ALabel("Interface IP addresses:");
            l3.setBackground(Color.white);
            autoPanel.add( l3 );

            String ip[] = new String[0];
            if(itf.length!=0) ip = NetInterface.getInterfaceAddresses(itf[0]);

            c_ipName = new JComboBox( ip );
            c_ipName.setEditable(false);
            if(ip.length!=0) c_ipName.setSelectedIndex(0);
            autoPanel.add( c_ipName );

         // Fixed Itf components
            fixedPanel = new JPanel(new GridLayout(1,2,0,0));
            fixedPanel.setBackground(Color.white);
            fixedPanel.setBorder(BorderFactory.createEmptyBorder(0,50,0,10));
            
            l4 = new ALabel("Enter the address to use :");
            l4.setBackground(Color.white);
            fixedPanel.add( l4 );

            t_itfName = new ATextField("");
            t_itfName.setBackground(Color.white);
            fixedPanel.add( t_itfName );

         // Creation of the network interface panel
            JPanel netPanel = new JPanel(new BorderLayout());
            netPanel.setBackground(Color.white);
            netPanel.setBorder(BorderFactory.createLineBorder(Color.gray));

            JPanel netPanel1 = new JPanel(new BorderLayout());
            netPanel1.setBackground(Color.white);
            netPanel1.add(r_automatic,BorderLayout.NORTH);
            netPanel1.add(autoPanel,BorderLayout.SOUTH);

            JPanel netPanel2 = new JPanel(new GridLayout(2,1,0,0));
            netPanel2.setBackground(Color.white);
            netPanel2.add(r_fixed);
            netPanel2.add(fixedPanel);
            netPanel2.setBorder(BorderFactory.createEmptyBorder(0,0,1,0));
            
            netPanel.add( netPanel1, BorderLayout.NORTH);
            netPanel.add( netPanel2, BorderLayout.SOUTH);
            mainPanel.add( netPanel, BorderLayout.NORTH );

         // Creation of the publish panel
            r_publishSelect = new ARadioButton("Publish the IP of the interface that was selected above.");
            r_publishSelect.setActionCommand("publishItf");
            r_publishSelect.addActionListener(this);

            r_publishFixed = new ARadioButton("Publish another IP/DNS name:");
            r_publishFixed.setActionCommand("publishOther");
            r_publishFixed.addActionListener(this);

            btGroupPublish = new ButtonGroup();
            btGroupPublish.add(r_publishSelect);
            btGroupPublish.add(r_publishFixed);

            t_itfPublish = new ATextField("");
            t_itfPublish.setBackground(Color.white);

            JPanel otherPanel = new JPanel(new GridLayout(1,2,0,0));
            otherPanel.setBackground(Color.white);
            otherPanel.add(r_publishFixed);
            otherPanel.add(t_itfPublish);
            otherPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
            
            JPanel publishPanel = new JPanel(new GridLayout(3,1,5,5));
            publishPanel.setBackground(Color.white);

            ALabel l5 = new ALabel("IP Address to Publish on the Internet");
            l5.setBackground(Color.white);
            publishPanel.add(l5);

            publishPanel.add(r_publishSelect);
            publishPanel.add(otherPanel);
            publishPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
            mainPanel.add( publishPanel, BorderLayout.CENTER );

         // Third Radio section
            r_automaticUpdate = new ARadioButton("Automatically when the IP changes (please edit the transfer script).");
            r_manualUpdate = new ARadioButton("Never. I will do it manually via the Server Address Wizard.");

            btGroupItf = new ButtonGroup();
            btGroupItf.add(r_automaticUpdate);
            btGroupItf.add(r_manualUpdate);

            JPanel updatePanel = new JPanel(new GridLayout(3,1,5,5));
            updatePanel.setBackground(Color.white);
            updatePanel.setBorder(BorderFactory.createLineBorder(Color.gray));

            ALabel l6 = new ALabel("When do we have to do the IP File Transfer ?");
            l6.setBackground(Color.white);
            updatePanel.add(l6);
            updatePanel.add(r_automaticUpdate);
            updatePanel.add(r_manualUpdate);
            mainPanel.add( updatePanel, BorderLayout.SOUTH );


         // Selection init
            r_automatic.setSelected(true);
            t_itfName.setEnabled(false);
            l4.setEnabled(false);
            r_publishSelect.setSelected(true);
            t_itfPublish.setEnabled(false);
            r_automaticUpdate.setSelected(true);
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

       /** Action Performed when the user clicks a button.
        */
        public void actionPerformed(ActionEvent e) {

           String name = e.getActionCommand();

           if( name.equals("automaticItf") ) {
               c_itfName.setEnabled(true);
               c_ipName.setEnabled(true);
               l_itfDesc.setEnabled(true);
               t_itfName.setEnabled(false);
               l1.setEnabled(true);
               l2.setEnabled(true);
               l3.setEnabled(true);
               l4.setEnabled(false);
           }
           else if( name.equals("fixedItf") ) {
               c_itfName.setEnabled(false);
               c_ipName.setEnabled(false);
               l_itfDesc.setEnabled(false);
               t_itfName.setEnabled(true);
               l1.setEnabled(false);
               l2.setEnabled(false);
               l3.setEnabled(false);
               l4.setEnabled(true);
           }
           else if( name.equals("publishItf") ) {
               t_itfPublish.setEnabled(false);
           }
           else if( name.equals("publishOther") ) {
               t_itfPublish.setEnabled(true);
           }
           else if( name.equals("itf") ) {
               if( c_itfName.getItemCount()==0 || l_itfDesc==null || c_ipName==null)
                   return;
               
               String itfName = (String) c_itfName.getSelectedItem();
               if(itfName==null) return;
               l_itfDesc.setText( NetInterface.getInterfaceDescription(itfName) );

               String ip[] = NetInterface.getInterfaceAddresses( itfName );
               c_ipName.removeAllItems();

               for( int i=0; i<ip.length; i++ )
                    c_ipName.addItem(ip[i]);

               if(ip.length!=0)
                  c_ipName.setSelectedIndex(0);
           }
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called each time the step is shown on screen.
      */
       protected void onShow(Object context, JWizard wizard) {
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when the "Next" button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Next" button action, false to cancel it...
      */
       protected boolean onNext(Object context, JWizard wizard) {

         // we retrieve the setup data
            String itf = "";
            String publish = "";
         
            if( r_automatic.isSelected() ) {
                itf = (String) c_itfName.getSelectedItem();

                if(itf==null || itf.length()==0) {
                   JOptionPane.showMessageDialog( null, "No network interface selected.",
                                                  "Error", JOptionPane.ERROR_MESSAGE);
                   return false;
                }

                int ind = c_ipName.getSelectedIndex();

                if( ind<0 ) {
                   JOptionPane.showMessageDialog( null, "No IP address index selected.",
                                                  "Error", JOptionPane.ERROR_MESSAGE);
                   return false;
                }
                
                itf = itf+","+ind;
            }
            else {
                if( t_itfName.getText().length()==0 ) {
                   JOptionPane.showMessageDialog( null, "No IP/DNS name entered.",
                                                  "Error", JOptionPane.ERROR_MESSAGE);
                   return false;
                }

                itf = t_itfName.getText();
            }

            if( r_publishFixed.isSelected() ) {
                if( t_itfPublish.getText().length()==0 ) {
                   JOptionPane.showMessageDialog( null, "No IP/DNS name entered (publish section).",
                                                  "Error", JOptionPane.ERROR_MESSAGE);
                   return false;
                }

                publish = t_itfPublish.getText();
            }

         // set this config as default ?
            int value = JOptionPane.showConfirmDialog( null, "Set this config as default ?\n  Server Interface : "+itf
                                                       +"\n  Published Interface: "+(publish.length()==0?itf:publish),
                                                       "Server Interface Update", JOptionPane.YES_NO_OPTION);

            if( value == JOptionPane.YES_OPTION ) {
                ServerDirector.getServerProperties().setProperty( "init.serverItf", itf );
                ServerDirector.getServerProperties().setProperty( "init.publishAddress", publish );
                ServerDirector.getServerProperties().setProperty( "init.automaticUpdate",
                                                           ""+r_automaticUpdate.isSelected() );
            }
            else
                return false;

            wizard.setNextStep(  ServerConfigWizardStep.getStaticParameters() );
            return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when Previous button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Previous" button action, false to cancel it...
      */
       protected boolean onPrevious(Object context, JWizard wizard) {
            wizard.setNextStep(  RegisterChoicesWizardStep.getStaticParameters()  );
            return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

    }

 /*------------------------------------------------------------------------------------*/
 /**
  * Third Step of our JWizard. Server config.
  */
  public static class ServerConfigWizardStep extends JWizardStep {

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** This is a static JWizardStep, to build it more simply this method
      *  returns the JWizardStepParameters needed for the JWizard.
      */
       public static JWizardStepParameters getStaticParameters() {
          JWizardStepParameters param = new JWizardStepParameters(
                          "wotlas.server.setup.ServerSetup$ServerConfigWizardStep",
                          "Edit your Server's Config" );

          param.setIsPrevButtonEnabled(true);
          param.setIsDynamic(true);
          return param;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

      /** Swing Components
       */
      private ATextField t_serverSymbolicName, t_serverName, t_serverID, t_accountServerPort,
               t_gameServerPort, t_gatewayServerPort, t_maxNumberOfGameConnections,
               t_maxNumberOfAccountConnections, t_maxNumberOfGatewayConnections,
               t_description, t_location, t_adminEmail;
    
      private JComboBox c_worldStartLocation;

      /** The server config we build.
       */
      private ServerConfig config;

      /** Server config Manager to save/load configs
       */
      private ServerConfigManager serverConfigManager;

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Constructor.
      */
       public ServerConfigWizardStep() {
           super();

        // II - we load the default server config.
           serverConfigManager = new ServerConfigManager( ServerDirector.getResourceManager() );
           config = serverConfigManager.loadServerConfig( serverID );

           if( config == null )
               config = new ServerConfig();  // none ? ok, we build one...

           config.setServerID(serverID);

        // III - Swing components
           JPanel sconfPanel = new JPanel(new GridLayout(24,1,10,10));
           sconfPanel.setBackground(Color.white);
           sconfPanel.setPreferredSize(new Dimension(350,800));
           setLayout( new BorderLayout() );

         // Server Symbolic Name
            ALabel label0 = new ALabel("Server symbolic name (can be any name) :");
            t_serverSymbolicName = new ATextField( config.getServerSymbolicName() );
            sconfPanel.add( label0 );
            sconfPanel.add( t_serverSymbolicName );

         // Server ID ( 0 means standalone )
            ALabel label2 = new ALabel("Server Id :");
            t_serverID = new ATextField( ""+config.getServerID() );
            t_serverID.setEditable(false);
            sconfPanel.add( label2 );
            sconfPanel.add( t_serverID );

         // Server description
            ALabel label3 = new ALabel("Server Description :");
            t_description = new ATextField( config.getDescription() );
            sconfPanel.add( label3 );
            sconfPanel.add( t_description );

         // Players starting location :
            ALabel label12 = new ALabel("Starting location for players :");
            c_worldStartLocation = new JComboBox( TOWNS_NAME );
            c_worldStartLocation.setEditable(false);

            for(int i=0; i<TOWNS_NAME.length; i++ ) {
                if(TOWNS_NEAR_POSITION[i][0]==config.getWorldFirstXPosition()
                   && TOWNS_NEAR_POSITION[i][1]==config.getWorldFirstYPosition()) {
                   // we found the previous location
                      c_worldStartLocation.setSelectedIndex(i);
                      break;
                }
            }

            sconfPanel.add( label12 );
            sconfPanel.add( c_worldStartLocation );

         // Location
            ALabel label4 = new ALabel("Server's country :");
            t_location = new ATextField( config.getLocation() );
            sconfPanel.add( label4 );
            sconfPanel.add( t_location );

         // Email
            ALabel label5 = new ALabel("Server Administrator Email :");
            t_adminEmail = new ATextField( config.getAdminEmail() );
            sconfPanel.add( label5 );
            sconfPanel.add( t_adminEmail );

         // Game Server Port
            ALabel label6 = new ALabel("Game Server Port :");
            t_gameServerPort = new ATextField( ""+config.getGameServerPort() );
            sconfPanel.add( label6 );
            sconfPanel.add( t_gameServerPort );

         // Account Server Port
            ALabel label7 = new ALabel("Account Server Port :");
            t_accountServerPort = new ATextField( ""+config.getAccountServerPort() );
            sconfPanel.add( label7 );
            sconfPanel.add( t_accountServerPort );

         // Gateway Server Port
            ALabel label8 = new ALabel("Gateway Server Port :");
            t_gatewayServerPort = new ATextField( ""+config.getGatewayServerPort() );
            sconfPanel.add( label8 );
            sconfPanel.add( t_gatewayServerPort );

         // maxNumberOfGameConnections
            ALabel label9 = new ALabel("Maximum number of connections on the Game Server:");
            t_maxNumberOfGameConnections = new ATextField( ""+config.getMaxNumberOfGameConnections() );
            sconfPanel.add( label9 );
            sconfPanel.add( t_maxNumberOfGameConnections );

         // maxNumberOfAccountConnections
            ALabel label10 = new ALabel("Maximum number of connections on the Account Server:");
            t_maxNumberOfAccountConnections = new ATextField( ""+config.getMaxNumberOfAccountConnections() );
            sconfPanel.add( label10 );
            sconfPanel.add( t_maxNumberOfAccountConnections );

         // maxNumberOfGatewayConnections
            ALabel label11 = new ALabel("Maximum number of connections on the Gateway Server:");
            t_maxNumberOfGatewayConnections = new ATextField( ""+config.getMaxNumberOfGatewayConnections() );
            sconfPanel.add( label11 );
            sconfPanel.add( t_maxNumberOfGatewayConnections );

         // ScrollPane to wrap the main panel
            JScrollPane scrollPane =  new JScrollPane( sconfPanel );
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10,15,0,15));
            scrollPane.setBackground(Color.white);
            add( scrollPane, BorderLayout.CENTER );
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called each time the step is shown on screen.
      */
       protected void onShow(Object context, JWizard wizard) {
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when the "Next" button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Next" button action, false to cancel it...
      */
       protected boolean onNext(Object context, JWizard wizard) {

         // we retrieve the config's data
            config.setServerSymbolicName( t_serverSymbolicName.getText() );
            config.setServerName( null );
            config.setDescription( t_description.getText() );
            config.setLocation( t_location.getText() );
            config.setAdminEmail( t_adminEmail.getText() );
            config.setConfigVersion();

            try{
               int val1 = Integer.parseInt( t_serverID.getText() );
               int val2 = Integer.parseInt( t_accountServerPort.getText() );
               int val3 = Integer.parseInt( t_gameServerPort.getText() );
               int val4 = Integer.parseInt( t_gatewayServerPort.getText() );
               int val5 = Integer.parseInt( t_maxNumberOfGameConnections.getText() );
               int val6 = Integer.parseInt( t_maxNumberOfAccountConnections.getText() );
               int val7 = Integer.parseInt( t_maxNumberOfGatewayConnections.getText() );

               config.setServerID( val1 );
               config.setAccountServerPort( val2 );
               config.setGameServerPort( val3 );
               config.setGatewayServerPort( val4 );
               config.setMaxNumberOfGameConnections( val5 );
               config.setMaxNumberOfAccountConnections( val6 );
               config.setMaxNumberOfGatewayConnections( val7 );

               config.setWorldFirstXPosition( TOWNS_NEAR_POSITION[c_worldStartLocation.getSelectedIndex()][0] );
               config.setWorldFirstYPosition( TOWNS_NEAR_POSITION[c_worldStartLocation.getSelectedIndex()][1] );
            }
            catch(Exception ee) {
                  Debug.signal( Debug.ERROR, this, ""+ee );
                  JOptionPane.showMessageDialog( null, "Bad number format !","Error", JOptionPane.ERROR_MESSAGE);
                  return false;
            }

         // We save the config
            if( !serverConfigManager.saveServerConfig(config) ) {
                JOptionPane.showMessageDialog( null, "Failed to save server config in database",
                                               "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

         // set this server as default ?
            int value = JOptionPane.showConfirmDialog(null, "Set this server as default ?", "Update Startup Config", JOptionPane.YES_NO_OPTION);

            if( value == JOptionPane.YES_OPTION )
                ServerDirector.getServerProperties().setProperty( "init.serverID", ""+serverID );

            wizard.setNextStep(  FinalWizardStep.getStaticParameters() );
            return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when Previous button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Previous" button action, false to cancel it...
      */
       protected boolean onPrevious(Object context, JWizard wizard) {
            wizard.setNextStep(  ServerInterfaceWizardStep.getStaticParameters()  );
            return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

    }

 /*------------------------------------------------------------------------------------*/
 /**
  * Second Step of our JWizard. Register choices.
  */
  public static class FinalWizardStep extends JWizardStepInfo {

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** This is a static JWizardStep, to build it more simply this method
      *  returns the JWizardStepParameters needed for the JWizard.
      */
       public static JWizardStepParameters getStaticParameters() {
          JWizardStepParameters param = new JWizardStepParameters(
                          "wotlas.server.setup.ServerSetup$FinalWizardStep",
                          "Server Config Saved" );

          param.setIsPrevButtonEnabled(true);
          param.setIsLastStep(true);
          param.setIsDynamic(true);

          if(serverID==0) {
             param.setProperty("init.info0", "\n      Your server config has been successfully saved."
                                           +" You can now start your server. Because your server is"
                                           +" local you don't need to use the setup program.");

             ServerDirector.getResourceManager().saveText(
                 ServerDirector.getResourceManager().getExternalServerConfigsDir()+"server-0.cfg.adr", "localhost" );
          } else {
             param.setProperty("init.info0", "\n      Your server config has been successfully saved.\n\n"
                                           +"      You must now send it to the wotlas manager : just"
                                           +" attach the \""+ServerDirector.getResourceManager().getExternalServerConfigsDir()
                                           +"server-"+serverID+".cfg\" file"
                                           +" to a mail and send it to the address "
                                           +ServerDirector.getServerProperties().getProperty("info.remoteServerAdminEmail","")
                                           +". You will then receive the up- to-date universe data and be"
                                           +" able to start your server." );
          }

          return param;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Constructor.
      */
       public FinalWizardStep() {
           super();
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called each time the step is shown on screen.
      */
       protected void onShow(Object context, JWizard wizard) {
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when the "Next" button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Next" button action, false to cancel it...
      */
       protected boolean onNext(Object context, JWizard wizard) {
       	   return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Called when Previous button is clicked.
      *  Use the wizard's setNextStep() method to set the next step to be displayed.
      *  @return return true to validate the "Previous" button action, false to cancel it...
      */
       protected boolean onPrevious(Object context, JWizard wizard) {
            wizard.setNextStep( ServerConfigWizardStep.getStaticParameters() );
            return true;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

    }

 /*------------------------------------------------------------------------------------*/

}


