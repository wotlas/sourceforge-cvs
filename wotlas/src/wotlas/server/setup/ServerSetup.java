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

   /** Our base path.
    */
     private static String basePath;

   /** ResourceManager
    */
     private static ResourceManager rManager;

   /** Our serverID
    */
     private static int serverID;

   /** Remote server config properties
    */
     private static ServerPropertiesFile serverProperties;

   /** Remote server config properties
    */
     private static RemoteServersPropertiesFile remoteServersProperties;

   /** Server Config file path.
    */
     private static String serverConfigPrefixPath;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public ServerSetup() {
         super("Server Config",
               rManager,
               FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter").deriveFont(18f),
               470,450);
         setLocation(200,100);
         setGUI();

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
           Debug.exit();
   }

 /*------------------------------------------------------------------------------------*/

  /** Called when wizard is canceled ('cancel' button pressed).
   */
   protected void onCanceled(Object context) {
         Debug.exit();
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
                   wizard.setNextStep(  ServerConfigWizardStep.getStaticParameters()  );
                   break;

            	case 3:
            	   serverID=0;
                   wizard.setNextStep(  ServerConfigWizardStep.getStaticParameters()  );
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
                                         +""+remoteServersProperties.getProperty("info.remoteServerAdminEmail","")
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

            wizard.setNextStep(  ServerConfigWizardStep.getStaticParameters()  );
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
          param.setProperty("init.text0", remoteServersProperties.getProperty("info.remoteServerHomeURL","") );

          param.setProperty("init.label1", "Wotlas manager's email:");
          param.setProperty("init.text1", remoteServersProperties.getProperty("info.remoteServerAdminEmail","") );

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

          remoteServersProperties.setProperty( "info.remoteServerHomeURL", getText0() );
          remoteServersProperties.setProperty( "info.remoteServerAdminEmail", getText1() );

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
  * Second Step of our JWizard. Register choices.
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
           Debug.setLevel( Debug.CRITICAL );
           Debug.displayExceptionStack( false );

           serverConfigManager = new ServerConfigManager( rManager );
           config = serverConfigManager.loadServerConfig( serverID );

           Debug.setLevel( Debug.NOTICE );
           Debug.displayExceptionStack( true );

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
                serverProperties.setProperty( "init.serverID", ""+serverID );

            wizard.setNextStep(  FinalWizardStep.getStaticParameters() );
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

             rManager.saveText( rManager.getExternalServerConfigsDir()+"server-0.cfg.adr", "localhost" );
          } else {
             param.setProperty("init.info0", "\n      Your server config has been successfully saved.\n\n"
                                           +"      You must now send it to the wotlas manager : just"
                                           +" attach the \""+rManager.getExternalServerConfigsDir()
                                           +"server-"+serverID+".cfg\" file"
                                           +" to a mail and send it to the address "
                                           +serverProperties.getProperty("info.remoteServerAdminEmail","")
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

  /** Main. Starts the setup utility.
   * @param argv enter -help to get some help info.
   */
    static public void main( String argv[] ) {

        // STEP 0 - We parse the command line options
           basePath = ResourceManager.DEFAULT_BASE_PATH;
           Debug.displayExceptionStack( true );

           for( int i=0; i<argv.length; i++ ) {

              if( !argv[i].startsWith("-") )
                  continue;

              if(argv[i].equals("-base")) {   // -- TO SET THE CONFIG FILES LOCATION --

                   if(i==argv.length-1) {
                      System.out.println("Location missing.");
                      System.out.println(SETUP_COMMAND_LINE_HELP);
                      return;
                   }

                   basePath = argv[i+1];
              }
              else if(argv[i].equals("-help")) {   // -- TO DISPLAY THE HELP --

                   System.out.println(SETUP_COMMAND_LINE_HELP);
                   return;
              }
           }

        // STEP 1 - Creation of the ResourceManager
           rManager = new ResourceManager();

           if( !rManager.inJar() )
               rManager.setBasePath(basePath);

        // STEP 2 - Log Creation
           try {
              Debug.setPrintStream( new JLogStream( new javax.swing.JFrame(),
                                    rManager.getExternalLogsDir()+"register-setup.log",
                                    "log-title-dark.jpg", rManager ) );
           } catch( java.io.FileNotFoundException e ) {
              e.printStackTrace();
              Debug.exit();
           }

           Debug.signal(Debug.NOTICE,null,"Starting Register Setup...");

           serverProperties = new ServerPropertiesFile(rManager);
           remoteServersProperties = new RemoteServersPropertiesFile(rManager);

           serverID = serverProperties.getIntegerProperty("init.serverID");
           Debug.signal( Debug.NOTICE, null, "Current Default Server ID is : "+serverID );

           serverConfigPrefixPath = rManager.getExternalServerConfigsDir()
                               +ServerConfigManager.SERVERS_PREFIX;

         // STEP 3 - Creation of our Font Factory
           FontFactory.createDefaultFontFactory( rManager );
           Debug.signal( Debug.NOTICE, null, "Font factory created..." );

         // STEP 4 - Start the wizard
           new ServerSetup();
    }

 /*------------------------------------------------------------------------------------*/

  /** Set the colors and fonts
   */
  static public void setGUI() {
    Font f;
    
    f = new Font("Monospaced", Font.PLAIN, 10);
    UIManager.put("Button.font", f);

    f = FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter");

    UIManager.put("ComboBox.font", f.deriveFont(14f));
    UIManager.put("ComboBox.foreground", Color.black);

    UIManager.put("Label.font", f.deriveFont(14f));
    UIManager.put("Label.foreground", Color.black);
    
    UIManager.put("PasswordField.font", f.deriveFont(14f));
    UIManager.put("PasswordField.foreground", Color.black);
    
    UIManager.put("RadioButton.font", f.deriveFont(14f));
    UIManager.put("RadioButton.foreground", Color.black);
            
    UIManager.put("Table.font", f.deriveFont(14f));
    UIManager.put("Table.foreground", Color.black);    
    
    UIManager.put("TableHeader.font", f.deriveFont(16f));
    UIManager.put("TableHeader.foreground", Color.black);
    
    UIManager.put("TextArea.font", f.deriveFont(14f));
    UIManager.put("TextArea.foreground", Color.black);
    
    UIManager.put("TextField.font", f.deriveFont(14f));
    UIManager.put("TextField.foreground", Color.black);    
    
    UIManager.put("CheckBox.font", f.deriveFont(14f));
    UIManager.put("CheckBox.foreground", Color.black);
  }
  
 /*--------------------------------------------------------------------------*/

}


