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

import wotlas.common.*;
import wotlas.server.PersistenceManager;
import wotlas.utils.*;
import wotlas.utils.aswing.*;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Properties;

/** A small utility to configure the server.
 *
 * @author Aldiss
 * @see wotlas.common.ServerConfig
 */

public class ServerSetup extends JFrame
{
 /*------------------------------------------------------------------------------------*/

   /** Static Link to Database Config File.
    */
    public final static String DATABASE_CONFIG = "../src/config/server.cfg";

   /** Static Link to Remote Servers Config File.
    */
    public final static String REMOTE_SERVER_CONFIG = "../src/config/remote-servers.cfg";

 /*------------------------------------------------------------------------------------*/

   /** Towns Name & initial position
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

    private JTextField t_serverSymbolicName, t_serverName, t_serverID, t_accountServerPort,
               t_gameServerPort, t_gatewayServerPort, t_maxNumberOfGameConnections,
               t_maxNumberOfAccountConnections, t_maxNumberOfGatewayConnections,
               t_description, t_location, t_adminEmail;
    
    private JComboBox c_worldStartLocation;
    
    private JButton b_save, b_exit;

    private ServerConfig config;

    private PersistenceManager pm;

 /*------------------------------------------------------------------------------------*/

   /** Complete Path to the database where are stored the universe and the client
    *  accounts.
    */
      private String databasePath;

   /** Our Server ID
    */
      private int serverID;

   /** Remote server admin email.
    */
      private String remoteServerAdminEmail;

 /*------------------------------------------------------------------------------------*/

   public ServerSetup() {
         super("Server Setup");

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

      // persistence manager
         pm = PersistenceManager.createPersistenceManager( databasePath );

      // load default server config.
         Debug.setLevel( Debug.CRITICAL );
         Debug.displayExceptionStack( false );

         config = pm.loadServerConfig( serverID );

         Debug.setLevel( Debug.NOTICE );
         Debug.displayExceptionStack( true );


         if( config == null )
             config = new ServerConfig();


      // STEP 2 - We load the remote servers config file to get the admin email.
         Properties remoteProps = FileTools.loadPropertiesFile( REMOTE_SERVER_CONFIG );

         if( remoteProps==null ) {
             Debug.signal( Debug.ERROR, null, "No valid remote-servers.cfg file found !" );
             remoteServerAdminEmail = "<no remote-servers.cfg file!>";
         }
         else {
           remoteServerAdminEmail = remoteProps.getProperty( "REMOTE_SERVER_ADMIN_EMAIL","" );

           if( remoteServerAdminEmail.length()==0 ) {
               Debug.signal( Debug.ERROR, null, "No admin email set !" );
               remoteServerAdminEmail = "<no email set!>";
           }
         }

      // Main JPanel
         JPanel mainPanel = new JPanel(new GridLayout(26,1,10,10));

      // Server Symbolic Name
         JLabel label0 = new JLabel("Server Symbolic Name :");
         t_serverSymbolicName = new JTextField( config.getServerSymbolicName() );
         mainPanel.add( label0 );
         mainPanel.add( t_serverSymbolicName );

      // Server Name
         JLabel label1 = new JLabel("Server Name (IP address or DNS name):");
         t_serverName = new JTextField( config.getServerName() );
         mainPanel.add( label1 );
         mainPanel.add( t_serverName );


      // Server ID ( 0 means standalone )
         JLabel label2 = new JLabel("Server ID (0 means standalone) :");
         t_serverID = new JTextField( ""+config.getServerID() );
         mainPanel.add( label2 );
         mainPanel.add( t_serverID );

      // Server description
         JLabel label3 = new JLabel("Server Description :");
         t_description = new JTextField( config.getDescription() );
         mainPanel.add( label3 );
         mainPanel.add( t_description );

      // Players starting location :
         JLabel label12 = new JLabel("Players starting location :");
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

         mainPanel.add( label12 );
         mainPanel.add( c_worldStartLocation );

      // Location
         JLabel label4 = new JLabel("Server's Country :");
         t_location = new JTextField( config.getLocation() );
         mainPanel.add( label4 );
         mainPanel.add( t_location );

      // Email
         JLabel label5 = new JLabel("Server Administrator Email :");
         t_adminEmail = new JTextField( config.getAdminEmail() );
         mainPanel.add( label5 );
         mainPanel.add( t_adminEmail );

      // Game Server Port
         JLabel label6 = new JLabel("Game Server Port :");
         t_gameServerPort = new JTextField( ""+config.getGameServerPort() );
         mainPanel.add( label6 );
         mainPanel.add( t_gameServerPort );

      // Account Server Port
         JLabel label7 = new JLabel("Account Server Port :");
         t_accountServerPort = new JTextField( ""+config.getAccountServerPort() );
         mainPanel.add( label7 );
         mainPanel.add( t_accountServerPort );

      // Gateway Server Port
         JLabel label8 = new JLabel("Gateway Server Port :");
         t_gatewayServerPort = new JTextField( ""+config.getGatewayServerPort() );
         mainPanel.add( label8 );
         mainPanel.add( t_gatewayServerPort );

      // maxNumberOfGameConnections
         JLabel label9 = new JLabel("Maximum Number of Connections on the Game Server:");
         t_maxNumberOfGameConnections = new JTextField( ""+config.getMaxNumberOfGameConnections() );
         mainPanel.add( label9 );
         mainPanel.add( t_maxNumberOfGameConnections );

      // maxNumberOfAccountConnections
         JLabel label10 = new JLabel("Maximum Number of Connections on the Account Server:");
         t_maxNumberOfAccountConnections = new JTextField( ""+config.getMaxNumberOfAccountConnections() );
         mainPanel.add( label10 );
         mainPanel.add( t_maxNumberOfAccountConnections );

      // maxNumberOfGatewayConnections
         JLabel label11 = new JLabel("Maximum Number of Connections on the Gateway Server:");
         t_maxNumberOfGatewayConnections = new JTextField( ""+config.getMaxNumberOfGatewayConnections() );
         mainPanel.add( label11 );
         mainPanel.add( t_maxNumberOfGatewayConnections );


      // *** Button Panel
         JPanel buttonPanel = new JPanel(new GridLayout(1,2,5,5));

         b_save = new JButton("Save Server Config");

         buttonPanel.add(b_save);

          b_save.addActionListener(new ActionListener()
          {
              public void actionPerformed (ActionEvent e)
              {
                // we save the config
                   config.setServerSymbolicName( t_serverSymbolicName.getText() );
                   config.setServerName( t_serverName.getText() );
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
                       JOptionPane.showMessageDialog( ServerSetup.this, "Failed to save : bad number format !",
                                                      "Error", JOptionPane.ERROR_MESSAGE);
                       return;
                   }

                   if( !pm.saveServerConfig(config) )
                       JOptionPane.showMessageDialog( ServerSetup.this, "Failed to save server config in database",
                                                      "Error", JOptionPane.ERROR_MESSAGE);
                   else {
                       new JHTMLWindow( ServerSetup.this, "<b>Your server configuration has been successfully saved.</b>"
                                        +"<p>If you want to make available your server on the Internet, you should"
                                        +" mail the config below to <i>"+remoteServerAdminEmail+" .</i></p>"
                                        +"<p>We will then send you a valid serverID and add your server config "
                                        +"to our server's list. This way clients will be able to see your server from "
                                        +"the account panel.</p><br><pre>", "Success",
                                        databasePath+File.separator+PersistenceManager.SERVERS_HOME
                                        +File.separator+PersistenceManager.SERVERS_PREFIX
                                        +config.getServerID()+PersistenceManager.SERVERS_SUFFIX,
                                        500, 400, true, true );

                       if(serverID==config.getServerID())
                          return;

                   // set this server as default ?
                      int value = JOptionPane.showConfirmDialog(null, "Set this server as default ?", "Update Startup Config", JOptionPane.YES_NO_OPTION);

                      if( value == JOptionPane.YES_OPTION ) {
                        // we load the server config file
                         String oldConfig = FileTools.loadTextFromFile( DATABASE_CONFIG );      	

                        // config loaded ?
                         if(oldConfig==null || oldConfig.length()==0 ) {
                              JOptionPane.showMessageDialog( ServerSetup.this, "Failed to load file : "+DATABASE_CONFIG,
                                                             "Error", JOptionPane.ERROR_MESSAGE);
                              return;
                         }

                      // search for property
                         StringBuffer newConfig = new StringBuffer("");

                         int pos = oldConfig.lastIndexOf( "SERVER_ID" );

                         if(pos<0) {
                              JOptionPane.showMessageDialog( ServerSetup.this, "Failed to find SERVER_ID property in file : "+DATABASE_CONFIG,
                                                             "Error", JOptionPane.ERROR_MESSAGE);
                              return;
                         }

                         pos = oldConfig.indexOf( "=", pos );

                         if(pos<0) {
                              JOptionPane.showMessageDialog( ServerSetup.this, "Failed to find correct SERVER_ID property in file : "+DATABASE_CONFIG,
                                                             "Error", JOptionPane.ERROR_MESSAGE);
                              return;
                         }

                          newConfig.append( oldConfig.substring(0,pos+1) );
                          newConfig.append( " "+config.getServerID() );
                          
                          pos = oldConfig.indexOf( "\n", pos );
                          
                          if(pos>0 && pos<oldConfig.length())
                            newConfig.append( oldConfig.substring( pos, oldConfig.length() ) );
                          else 
                            newConfig.append( "\n" );

                          if( !FileTools.saveTextToFile( DATABASE_CONFIG, newConfig.toString() ) )
                               JOptionPane.showMessageDialog( ServerSetup.this, "Failed to update "+DATABASE_CONFIG,
                                                              "Error", JOptionPane.ERROR_MESSAGE );
                          else
                               JOptionPane.showMessageDialog( ServerSetup.this, "Config successfully updated !",
                                                      "Success", JOptionPane.INFORMATION_MESSAGE);
                      }

                   }
              }
           });

         b_exit = new JButton("Exit Setup");

         buttonPanel.add(b_exit);

          b_exit.addActionListener(new ActionListener()
           {
              public void actionPerformed (ActionEvent e) {
                 System.exit(0);
              }
           });

      // *** We add the MainPanel to the Frame
         JScrollPane scrollPane =  new JScrollPane( mainPanel );
         scrollPane.setPreferredSize( new Dimension( 400, 400 ) );
         
         getContentPane().add( scrollPane, BorderLayout.CENTER );

         getContentPane().add( new JLabel( "Edit Your Server Setup and Click Save !" ), BorderLayout.NORTH );

         getContentPane().add( buttonPanel, BorderLayout.SOUTH );

         pack();
         SwingTools.centerComponent( this );
         show();
   }

 /*------------------------------------------------------------------------------------*/

   /** Main. Launches the Server Setup utility.
    *
    * @param argv this default param is not used.
    */
    static public void main( String argv[] ) {
        new ServerSetup();	
    }

 /*------------------------------------------------------------------------------------*/

}
