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

import wotlas.server.*;
import wotlas.common.*;
import wotlas.libs.wizard.*;
import wotlas.libs.graphics2D.FontFactory;
import wotlas.utils.*;

import wotlas.utils.Debug;
import wotlas.libs.aswing.*;

import wotlas.libs.log.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Properties;
import java.io.*;

 /** This a utility to update the server address.
  *
  * @author Aldiss
  */

public class ServerAddressSetup extends JWizard {

 /*------------------------------------------------------------------------------------*/

  /** Setup Command Line Help
   */
    public final static String SETUP_COMMAND_LINE_HELP =
            "Usage: ServerAddressSetup -[help|base <path>]\n\n"
           +"Examples : \n"
           +"  ServerAddressSetup -base ../base : sets the data location.\n\n"
           +"If the -base option is not set we search for data in "
           +ResourceManager.DEFAULT_BASE_PATH
           +"\n\n";

 /*------------------------------------------------------------------------------------*/

   /** Our resource Manager
    */
     private static ResourceManager rManager;

   /** Our base path.
    */
     private static String basePath;

   /** Our serverID
    */
     private static int serverID;

   /** Remote server config properties
    */
     private static ServerPropertiesFile serverProperties;

   /** Remote server config properties
    */
     private static RemoteServersPropertiesFile remoteServersProperties;

   /** Server Config Address file path.
    */
     private static String serverAddressFile;

   /** User Password
    */
     private static String password="";

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public ServerAddressSetup() {
         super("Server Address Setup",
               rManager,
               FontFactory.getDefaultFontFactory().getFont("Lucida Blackletter").deriveFont(18f),
               470,550);

         setLocation(200,100);

         if(serverID==0)
            JOptionPane.showMessageDialog( null, "Your server ID is 0 ('localhost'). This setup program is only"
                                                +"\nfor servers that need to publish their IP on the Internet.",
                                                "Warning", JOptionPane.WARNING_MESSAGE);

       // We display first step
          try{
               init( AddressWizardStep.getStaticParameters() );
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
  * First Step of our JWizard. Address config.
  */

  public static class AddressWizardStep extends JWizardStep {

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Swing Fields
      */
       private JTextField t_ipAddress;
       private JTextField t_login;
       private JPasswordField t_passw;
       private JComboBox c_prog, c_options, c_workdir;

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** This is a static JWizardStep, to build it more simply this method
      *  returns the JWizardStepParameters needed for the JWizard.
      */
       public static JWizardStepParameters getStaticParameters() {
          JWizardStepParameters param = new JWizardStepParameters( 
                          "wotlas.server.setup.ServerAddressSetup$AddressWizardStep",
                          "Server Address Setup" );

          param.setIsPrevButtonEnabled(false);
          param.setIsDynamic(true); // we don't want the step to be buffered, we want its data
          return param;             // to be updated each time the step is displayed
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** Constructor.
      */
       public AddressWizardStep() {
           super();

        // JPanel inits
           JPanel stepPanel = new JPanel( new GridLayout(8,1,5,5) );
           stepPanel.setAlignmentX(LEFT_ALIGNMENT);
           stepPanel.setBackground(Color.white);
           stepPanel.setPreferredSize( new Dimension(430,630) );

           setAlignmentX(LEFT_ALIGNMENT);
           JScrollPane scroll = new JScrollPane( stepPanel );
           scroll.setAlignmentX(LEFT_ALIGNMENT);
           scroll.setPreferredSize( new Dimension(450,430) );
           add( scroll );

        // Info on this Step
           JPanel group0 = new JPanel(new GridLayout(1,1,0,0));
           group0.setAlignmentX(LEFT_ALIGNMENT);
           group0.setBackground(Color.white);

           JTextArea text0 = new JTextArea("\n    Welcome to your Server Address Setup."
                                          +" This is where you declare and export your server's"
                                          +" IP address (or DNS name). Make sure the information below is correct"
                                          +" and move on to the next step. Your server ID is "+serverID+".");
           text0.setLineWrap(true);
           text0.setWrapStyleWord(true);
           text0.setEditable(false);
           text0.setAlignmentX(LEFT_ALIGNMENT);

           group0.add( text0 );
           stepPanel.add(group0);

        // IP Address Combo Box
           JPanel group1 = new JPanel(new GridLayout(3,1,0,0));
           group1.setAlignmentX(LEFT_ALIGNMENT);
           group1.setBackground(Color.white);

           JLabel label1 = new JLabel("Select/Enter your Internet address or DNS name (do not enter both) :");
           label1.setAlignmentX(LEFT_ALIGNMENT);

           String lastIP = rManager.loadText( serverAddressFile );

           if(lastIP==null) lastIP="0.0.0.0";

           t_ipAddress = new JTextField( lastIP );
           t_ipAddress.setAlignmentX(LEFT_ALIGNMENT);

           JLabel label1bis = new JLabel(" ");
           label1bis.setAlignmentX(LEFT_ALIGNMENT);

           group1.add( label1 );
           group1.add( t_ipAddress );
           group1.add( label1bis );
           stepPanel.add(group1);

        // File Transfer Info
           JTextArea text2 = new JTextArea("\n      We need to know how to transfer your server's IP"
                                          +" address to the wotlas central web server : "
                                          +remoteServersProperties.getProperty("info.remoteServerHomeURL") );

           text2.setAlignmentX(LEFT_ALIGNMENT);
           text2.setLineWrap(true);
           text2.setWrapStyleWord(true);
           text2.setEditable(false);
           stepPanel.add(text2);
           
        // Login
           JPanel group2 = new JPanel(new GridLayout(2,1,0,0));
           group2.setAlignmentX(LEFT_ALIGNMENT);
           group2.setBackground(Color.white);

           JLabel label2 = new JLabel("Enter your login :");
           label2.setAlignmentX(LEFT_ALIGNMENT);
           t_login = new JTextField(remoteServersProperties.getProperty("transfer.serverHomeLogin",""));
           t_login.setAlignmentX(LEFT_ALIGNMENT);

           group2.add( label2 );
           group2.add( t_login );
           stepPanel.add(group2);

        // Password
           JPanel group3 = new JPanel(new GridLayout(2,1,0,0));
           group3.setAlignmentX(LEFT_ALIGNMENT);
           group3.setBackground(Color.white);

           JLabel label3 = new JLabel("Enter your password :");
           label3.setAlignmentX(LEFT_ALIGNMENT);
           t_passw = new JPasswordField(password);
           t_passw.setAlignmentX(LEFT_ALIGNMENT);

           group3.add( label3 );
           group3.add( t_passw );
           stepPanel.add(group3);


        // File Tranfer Program
           JPanel group4 = new JPanel(new GridLayout(2,1,0,0));
           group4.setAlignmentX(LEFT_ALIGNMENT);
           group4.setBackground(Color.white);

           JLabel label4 = new JLabel("Enter the program to use for file transfer :");
           label4.setAlignmentX(LEFT_ALIGNMENT);

           String cmdProgList[] = {
           	   remoteServersProperties.getProperty("transfer.fileTransferProgram",""),
           	   "../bin/win32/pscp.exe",
           	   "\"../bin/win32/pscp.exe\"   (for win2000 & XP, don't remove the \" \" )",
           	   "scp",
           	   "ftp",
           	   };

           c_prog = new JComboBox(cmdProgList);
           c_prog.setEditable(true);
           c_prog.setAlignmentX(LEFT_ALIGNMENT);

           group4.add( label4 );
           group4.add( c_prog );
           stepPanel.add(group4);

        // File Tranfer Options
           JPanel group5 = new JPanel(new GridLayout(2,1,0,0));
           group5.setAlignmentX(LEFT_ALIGNMENT);
           group5.setBackground(Color.white);

           JTextArea text5 = new JTextArea("Enter the program's command line options. "
                                          +"It should contain $FILE$, $LOGIN$ and $PASSW$ tags "
                                          +"that we'll replace by their value.");
           text5.setAlignmentX(LEFT_ALIGNMENT);

           text5.setLineWrap(true);
           text5.setWrapStyleWord(true);
           text5.setEditable(false);

           String cmdOptList[] = {
           	   remoteServersProperties.getProperty("tranfer.fileTransferOptions",""),
           	   "-pw $PASSW$ $FILE$ $LOGIN$@shell.sf.net:/home/groups/w/wo/wotlas/htdocs/game",
           	   };

           c_options = new JComboBox(cmdOptList);
           c_options.setEditable(true);
           c_options.setAlignmentX(LEFT_ALIGNMENT);
           group5.add( text5 );
           group5.add( c_options );
           stepPanel.add(group5);

        // File Tranfer Working Directory :
           JPanel group6 = new JPanel(new GridLayout(2,1,0,0));
           group6.setAlignmentX(LEFT_ALIGNMENT);
           group6.setBackground(Color.white);

           JLabel text6 = new JLabel("Enter your command line's working directory : ");
           text6.setAlignmentX(LEFT_ALIGNMENT);

           String cmdWorkDirList[] = {
           	   remoteServersProperties.getProperty("transfer.fileTransferWorkingDir",""),
           	   "../bin/win32",
           	   "../bin/unix"
           	   };

           c_workdir = new JComboBox(cmdWorkDirList);
           c_workdir.setEditable(true);
           c_workdir.setAlignmentX(LEFT_ALIGNMENT);
           group6.add( text6 );
           group6.add( c_workdir );
           stepPanel.add( group6 );
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

              int value = JOptionPane.showConfirmDialog(null, "Save this config ? (required for transfer)", "Server Address Config", JOptionPane.YES_NO_OPTION);

              if( value != JOptionPane.YES_OPTION ) {
                  wizard.setNextStep(  TransferWizardStep.getStaticParameters()  );
                  return true;
              }

       	      password = new String(t_passw.getPassword());

       	   // 1 - we retrieve the data and save it to disk.
       	      remoteServersProperties.setProperty( "transfer.serverHomeLogin", t_login.getText() );
       	      remoteServersProperties.setProperty( "transfer.fileTransferProgram", c_prog.getSelectedItem().toString() );
       	      remoteServersProperties.setProperty( "tranfer.fileTransferOptions", c_options.getSelectedItem().toString() );
       	      remoteServersProperties.setProperty( "transfer.fileTransferWorkingDir", c_workdir.getSelectedItem().toString() );

              if( !rManager.saveText( serverAddressFile, t_ipAddress.getText() ) ) {
                  JOptionPane.showMessageDialog( null, "Failed to save IP address to\n"+serverAddressFile,
                                                        "Error", JOptionPane.ERROR_MESSAGE);
                  return false;
              }

           // 2 - we move on to the next step
              wizard.setNextStep(  TransferWizardStep.getStaticParameters()  );
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

    public static class TransferWizardStep extends JWizardStep {
     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

      /** Button To launch transfer
       */
        private JButton b_transfer;

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */

     /** This is a static JWizardStep, to build it more simply this method
      *  returns the JWizardStepParameters needed for the JWizard.
      */
       public static JWizardStepParameters getStaticParameters() {
          JWizardStepParameters param = new JWizardStepParameters( 
                          "wotlas.server.setup.ServerAddressSetup$TransferWizardStep",
                          "Server Config Transfer" );

          param.setIsLastStep(true);
          return param;
       }

     /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  */
     
     /** Constructor.
      */
       public TransferWizardStep() {
           super();

           JPanel group1 = new JPanel(new BorderLayout());
           group1.setAlignmentX(LEFT_ALIGNMENT);
           group1.setBackground(Color.white);
           JTextArea taInfo = new JTextArea("\n\n\n      We will now run the command line you entered in the previous step. "
                                    +"It will send your 'server-"+serverID+".cfg.adr' file to the "
                                    +"web server hosting the following URL : "
                                    +remoteServersProperties.getProperty("info.remoteServerHomeURL")
                                    +"\n\n      Your '.adr' file will be available at this URL. "
                                    +"This way other servers/client will be able to discover your server and connect to it.\n\n"
                                    +"If the transfer doesn't work take a look at server-side questions in our FAQ.\n\n");

           taInfo.setAlignmentX(LEFT_ALIGNMENT);
           taInfo.setLineWrap(true);
           taInfo.setWrapStyleWord(true);
           taInfo.setEditable(false);
           taInfo.setBackground(Color.white);
           group1.add( taInfo, BorderLayout.NORTH );

           b_transfer = new JButton("Click here to launch file tranfer !");
           b_transfer.setPreferredSize(new Dimension(250,30));
           group1.add( b_transfer, BorderLayout.SOUTH );
           add(group1);

           b_transfer.addActionListener(new ActionListener()
           {
              public void actionPerformed (ActionEvent e)
              {
                 // We get the full program path
                    StringBuffer fullCmd = new StringBuffer("");
                    boolean wrapFilePath = false; // need to wrap file path between " " ? (winXP needs it)

                    String prog = remoteServersProperties.getProperty("transfer.fileTransferProgram","");

                    if( prog.startsWith("\"") && prog.endsWith("\"") ) {
                        wrapFilePath = true;
                        fullCmd.append("\"");
                        prog = prog.substring(1,prog.length()-1);
                    }

                    try{
                      fullCmd.append( new File( prog ).getCanonicalPath() );
                    }catch(Exception ex ) {
                      ex.printStackTrace();
                      fullCmd.append( prog );
                    }

                    if(wrapFilePath)
                        fullCmd.append("\" ");
                    else
                        fullCmd.append(" "); // separator between program name & options

                 // We check the options command
                    String cmd = remoteServersProperties.getProperty("tranfer.fileTransferOptions","");
                    cmd += " "; // makes our job easier
                 
                    if( cmd.indexOf("$FILE$")<0 ) {
                        JOptionPane.showMessageDialog( null, "No $FILE$ tag found in command line.",
                                                        "Error", JOptionPane.ERROR_MESSAGE);
                    	return;
                    }

                    if( cmd.indexOf("$LOGIN$")<0 ) {
                        JOptionPane.showMessageDialog( null, "No $LOGIN$ tag found in command line.",
                                                        "Error", JOptionPane.ERROR_MESSAGE);
                    	return;
                    }

                    if( cmd.indexOf("$PASSW$")<0 ) {
                        JOptionPane.showMessageDialog( null, "No $PASSW$ tag found in command line.",
                                                        "Error", JOptionPane.ERROR_MESSAGE);
                    	return;
                    }

                 // We complete the options command
                    int ind1 = cmd.indexOf("$FILE$");

                    fullCmd.append( cmd.substring(0,ind1) );

                    if(wrapFilePath)
                       fullCmd.append("\""); // beginning " wrapper

                    try{
                       fullCmd.append( new File(serverAddressFile).getCanonicalPath() );
                    }catch(Exception ex ) {
                       ex.printStackTrace();
                       fullCmd.append( new File(serverAddressFile).getAbsolutePath() );
                    }

                    if(wrapFilePath)
                       fullCmd.append("\""); // ending " wrapper

                    fullCmd.append( cmd.substring(ind1+6,cmd.length()) );
                    cmd = fullCmd.toString();
                    fullCmd = new StringBuffer("");

                    ind1 = cmd.indexOf("$LOGIN$");

                    fullCmd.append( cmd.substring(0,ind1) );
                    fullCmd.append( remoteServersProperties.getProperty("transfer.serverHomeLogin","") );
                    fullCmd.append( cmd.substring(ind1+7,cmd.length() ) );
                    cmd = fullCmd.toString();
                    fullCmd = new StringBuffer("");

                    ind1 = cmd.indexOf("$PASSW$");

                    fullCmd.append( cmd.substring(0,ind1) );
                    fullCmd.append( password );
                    fullCmd.append( cmd.substring(ind1+7,cmd.length() ) );

                 // Runtime... we execute the transfert command
                    int result=1;
                    String workingDir = remoteServersProperties.getProperty("transfer.fileTransferWorkingDir");
                    File workingDirPath = null;
                    
                    if(workingDir.length()!=0)
                       workingDirPath = new File(workingDir);

                    Debug.signal(Debug.NOTICE,null,"Command run :\n"+cmd);
                    System.out.println("\nThis is the command we are trying to run (the $PASSW$ has been replaced by your password) :\n\n");
                    System.out.println(""+cmd);

                    try{
                       Process pr = Runtime.getRuntime().exec( fullCmd.toString(), null, workingDirPath );
                       result = pr.waitFor();
                    }
                    catch( Exception ex ) {
                        JOptionPane.showMessageDialog( null, "Command Line Failed :\n"+ex.getMessage(),
                                                        "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if(result==0)
                        JOptionPane.showMessageDialog( null, "Transfer done ! You can\nnow start your server!",
                                                       "Success", JOptionPane.INFORMATION_MESSAGE);
                    else
                        JOptionPane.showMessageDialog( null, "Transfer has failed. Please check (1) if the destination\n"
                                                            +"web server is running, (2) if you have setup your firewall\n"
                                                            +"properly, (3) if the command line is correct, (4) for pscp\n"
                                                            +"users the encryption key of your target web server may have\n"
                                                            +"changed, try to use pscp in a shell window to connect to\n"
                                                            +"your web server, (5) ask the wotlas manager to check your\n"
                                                            +"rights on the web server.",
                                                        "Error", JOptionPane.ERROR_MESSAGE);
              }
           });
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
           // we return to the previous step
              wizard.setNextStep(  AddressWizardStep.getStaticParameters()  );
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
                                    rManager.getExternalLogsDir()+"server-setup.log",
                                    "log-title-dark.jpg", rManager ) );
           } catch( java.io.FileNotFoundException e ) {
              e.printStackTrace();
              Debug.exit();
           }

           Debug.signal(Debug.NOTICE,null,"Starting Server Address Setup...");

           serverProperties = new ServerPropertiesFile(rManager);
           remoteServersProperties = new RemoteServersPropertiesFile(rManager);

           serverID = serverProperties.getIntegerProperty("init.serverID");
           Debug.signal( Debug.NOTICE, null, "Current Default Server ID is : "+serverID );

           serverAddressFile = rManager.getExternalServerConfigsDir()
                               +ServerConfigManager.SERVERS_PREFIX
                               +serverID+ServerConfigManager.SERVERS_SUFFIX
                               +ServerConfigManager.SERVERS_ADDRESS_SUFFIX;

         // STEP 3 - Creation of our Font Factory
           FontFactory.createDefaultFontFactory( rManager );
           Debug.signal( Debug.NOTICE, null, "Font factory created..." );

         // STEP 4 - Start the wizard
           new ServerAddressSetup();
    }

 /*------------------------------------------------------------------------------------*/

}

