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
 
package wotlas.server.setup;

import wotlas.server.ServerDirector;
import wotlas.common.PersistenceManager;
import wotlas.libs.wizard.*;
import wotlas.utils.*;

import wotlas.utils.Debug;
import wotlas.utils.aswing.*;

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

  /** Database Config.
   */
    private final static String DATABASE_CONFIG = "../src/config/server.cfg";

  /** Remote servers config file.
   */
    private final static String REMOTE_SERVER_CONFIG = "../src/config/remote-servers.cfg";

 /*------------------------------------------------------------------------------------*/

   /** Database Relative Path.
    */
     private static String databasePath;

   /** Our serverID
    */
     private static int serverID;

   /** Remote server config properties
    */
     private static Properties serverProperties;

   /** Server Config Address file path.
    */
     private static String serverAddressFile;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public ServerAddressSetup() {
         super("Server Address Setup",470,550);
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
               System.exit(1); // init failed !
          }
    }

 /*------------------------------------------------------------------------------------*/

  /** Called when wizard is finished (after last step's end).
   */
   protected void onFinished(Object context) {
           System.exit(0);
   }

 /*------------------------------------------------------------------------------------*/

  /** Called when wizard is canceled ('cancel' button pressed).
   */
   protected void onCanceled(Object context) {
         System.exit(0);
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

           String lastIP = FileTools.loadTextFromFile( serverAddressFile );

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
                                          +serverProperties.getProperty("REMOTE_SERVER_CONFIG_HOME_URL") );

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
           t_login = new JTextField(serverProperties.getProperty("SERVER_HOME_LOGIN",""));
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
           t_passw = new JPasswordField(serverProperties.getProperty( "SERVER_HOME_PASSW",""));
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
           	   serverProperties.getProperty("FILE_TRANSFER_PROG",""),
           	   "../bin/win32/pscp.exe",
           	   "\"../bin/win32/pscp.exe\"   (for win2000 & XP, don't remove the \" \" )",
           	   "../bin/unix/scp",
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
           	   serverProperties.getProperty("FILE_TRANSFER_OPT",""),
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
           	   serverProperties.getProperty("FILE_TRANSFER_WORKING_DIR",""),
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

       	   // 1 - we retrieve the data and save it to disk.
       	      serverProperties.setProperty( "SERVER_HOME_LOGIN", t_login.getText() );
       	      serverProperties.setProperty( "SERVER_HOME_PASSW", new String(t_passw.getPassword()) );
       	      serverProperties.setProperty( "FILE_TRANSFER_PROG", c_prog.getSelectedItem().toString() );
       	      serverProperties.setProperty( "FILE_TRANSFER_OPT", c_options.getSelectedItem().toString() );
       	      serverProperties.setProperty( "FILE_TRANSFER_WORKING_DIR", c_workdir.getSelectedItem().toString() );

              String oldConfig = FileTools.loadTextFromFile( REMOTE_SERVER_CONFIG );

              if( oldConfig!=null ) {
                  oldConfig = FileTools.updateProperty( "SERVER_HOME_LOGIN", t_login.getText(), oldConfig);
                  oldConfig = FileTools.updateProperty( "FILE_TRANSFER_PROG", c_prog.getSelectedItem().toString(), oldConfig);
                  oldConfig = FileTools.updateProperty( "FILE_TRANSFER_OPT", c_options.getSelectedItem().toString(), oldConfig);
                  oldConfig = FileTools.updateProperty( "FILE_TRANSFER_WORKING_DIR", c_workdir.getSelectedItem().toString(), oldConfig);
                  FileTools.saveTextToFile( REMOTE_SERVER_CONFIG, oldConfig );
              }

              if( !FileTools.saveTextToFile( serverAddressFile, t_ipAddress.getText() ) ) {
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
                                    +serverProperties.getProperty("REMOTE_SERVER_CONFIG_HOME_URL")
                                    +"\n\n      Your '.adr' file will be available at this URL. "
                                    +"This way other servers/client will be able to discover your server and connect to it.\n\n");

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

                    String prog = serverProperties.getProperty("FILE_TRANSFER_PROG","");

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
                    String cmd = serverProperties.getProperty("FILE_TRANSFER_OPT","");
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
                    fullCmd.append( serverProperties.getProperty("SERVER_HOME_LOGIN","") );
                    fullCmd.append( cmd.substring(ind1+7,cmd.length() ) );
                    cmd = fullCmd.toString();
                    fullCmd = new StringBuffer("");

                    ind1 = cmd.indexOf("$PASSW$");

                    fullCmd.append( cmd.substring(0,ind1) );
                    fullCmd.append( serverProperties.getProperty("SERVER_HOME_PASSW","") );
                    fullCmd.append( cmd.substring(ind1+7,cmd.length() ) );

                 // Runtime... we execute the transfert command
                    int result=1;
                    String workingDir = serverProperties.getProperty("FILE_TRANSFER_WORKING_DIR");
                    File workingDirPath = null;
                    
                    if(workingDir.length()!=0)
                       workingDirPath = new File(workingDir);

                    try{
                       System.out.println( "Command run :\n"+cmd );
                       Process pr = Runtime.getRuntime().exec( fullCmd.toString(), null, workingDirPath );
                       result = pr.waitFor();
                    }
                    catch( Exception ex ) {
                        JOptionPane.showMessageDialog( null, "Command Line Failed :\n"+ex.getMessage(),
                                                        "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if(result==0)
                        JOptionPane.showMessageDialog( null, "Transfer done. You should check if it worked.",
                                                       "Success", JOptionPane.INFORMATION_MESSAGE);
                    else
                        JOptionPane.showMessageDialog( null, "Command line seems to have failed.\nCheck transfer destination.",
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

  /** Main. We don't expect any parameters.
   *  @param argv none
   */
    static public void main( String argv[] ) {

        // STEP 1 - We load the database path. Where is the data ?
           Properties properties = FileTools.loadPropertiesFile( DATABASE_CONFIG );

             if( properties==null ) {
                Debug.signal( Debug.FAILURE, null, "No valid server-database.cfg file found !" );
                System.exit(1);
             }

           databasePath = properties.getProperty( "DATABASE_PATH","" );

           if( databasePath.length()==0 ) {
               Debug.signal( Debug.FAILURE, null, "No Database Path specified in config file !" );
               System.exit(1);
           }

           Debug.signal( Debug.NOTICE, null, "DataBase Path Found : "+databasePath );

        // STEP 2 - What is the current server ID ?
           String s_serverID = properties.getProperty( "SERVER_ID","" );

           if( s_serverID.length()==0 ) {
               Debug.signal( Debug.FAILURE, null, "No ServerID specified in config file !" );
               System.exit(1);
           }

           try{
              serverID = Integer.parseInt( s_serverID );
           }catch( Exception e ) {
                Debug.signal( Debug.FAILURE, null, "Bad ServerID specified in config file !" );
                System.exit(1);
           }

           Debug.signal( Debug.NOTICE, null, "Current Default Server ID is : "+serverID );


        // STEP 3 - We get the remote server properties
           serverProperties = FileTools.loadPropertiesFile( REMOTE_SERVER_CONFIG );

             if( serverProperties==null ) {
                Debug.signal( Debug.FAILURE, null, "No valid remote-servers.cfg file found !" );
                System.exit(1);
             }


           serverAddressFile = databasePath+File.separator+PersistenceManager.SERVERS_HOME
                               +File.separator+PersistenceManager.SERVERS_PREFIX
                               +serverID+PersistenceManager.SERVERS_SUFFIX
                               +PersistenceManager.SERVERS_ADDRESS_SUFFIX;

         // STEP 4 - Start the wizard
           new ServerAddressSetup();
    }

 /*------------------------------------------------------------------------------------*/

}

