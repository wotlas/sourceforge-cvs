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

import wotlas.server.ServerConfig;
import wotlas.server.PersistenceManager;
import wotlas.utils.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/** A small utility to configure the server.
 *
 * @author Aldiss
 * @see wotlas.server.ServerConfig
 */

public class ServerSetup extends JFrame
{
 /*------------------------------------------------------------------------------------*/

    private JTextField t_serverName, t_serverID, t_accountServerPort,
               t_gameServerPort, t_gatewayServerPort, t_maxNumberOfGameConnections,
               t_maxNumberOfAccountConnections, t_maxNumberOfGatewayConnections,
               t_description, t_location, t_adminEmail;
    
    private JButton b_save, b_exit;

    private ServerConfig config;

    private PersistenceManager pm;
 /*------------------------------------------------------------------------------------*/

   public ServerSetup() {
         super("Server Setup");

      // persistence manager
         pm = PersistenceManager.createPersistenceManager( null );

      // load default server config.
         config = pm.loadServerConfig();

         if( config == null )
             config = new ServerConfig();

      // Main JPanel
         JPanel mainPanel = new JPanel(new GridLayout(22,1,10,10));

      // Server Name
         JLabel label1 = new JLabel("Server Name :");
         t_serverName = new JTextField( config.getServerName() );
         mainPanel.add( label1 );
         mainPanel.add( t_serverName );
//         mainPanel.add( new JLabel("") );


      // Server ID ( -1 means standalone )
         JLabel label2 = new JLabel("Server ID (-1 means standalone) :");
         t_serverID = new JTextField( ""+config.getServerID() );
         mainPanel.add( label2 );
         mainPanel.add( t_serverID );
//         mainPanel.add( new JLabel("") );

      // Server description
         JLabel label3 = new JLabel("Server Description :");
         t_description = new JTextField( config.getDescription() );
         mainPanel.add( label3 );
         mainPanel.add( t_description );
//         mainPanel.add( new JLabel("") );

      // Location
         JLabel label4 = new JLabel("Server Location :");
         t_location = new JTextField( config.getLocation() );
         mainPanel.add( label4 );
         mainPanel.add( t_location );
//         mainPanel.add( new JLabel("") );

      // Email
         JLabel label5 = new JLabel("Server Administrator Email :");
         t_adminEmail = new JTextField( config.getAdminEmail() );
         mainPanel.add( label5 );
         mainPanel.add( t_adminEmail );
//         mainPanel.add( new JLabel("") );

      // Game Server Port
         JLabel label6 = new JLabel("Game Server Port :");
         t_gameServerPort = new JTextField( ""+config.getGameServerPort() );
         mainPanel.add( label6 );
         mainPanel.add( t_gameServerPort );
//         mainPanel.add( new JLabel("") );

      // Account Server Port
         JLabel label7 = new JLabel("Account Server Port :");
         t_accountServerPort = new JTextField( ""+config.getAccountServerPort() );
         mainPanel.add( label7 );
         mainPanel.add( t_accountServerPort );
//         mainPanel.add( new JLabel("") );

      // Gateway Server Port
         JLabel label8 = new JLabel("Gateway Server Port :");
         t_gatewayServerPort = new JTextField( ""+config.getGatewayServerPort() );
         mainPanel.add( label8 );
         mainPanel.add( t_gatewayServerPort );
//         mainPanel.add( new JLabel("") );

      // maxNumberOfGameConnections
         JLabel label9 = new JLabel("Maximum Number of Connections on the Game Server:");
         t_maxNumberOfGameConnections = new JTextField( ""+config.getMaxNumberOfGameConnections() );
         mainPanel.add( label9 );
         mainPanel.add( t_maxNumberOfGameConnections );
//         mainPanel.add( new JLabel("") );

      // maxNumberOfAccountConnections
         JLabel label10 = new JLabel("Maximum Number of Connections on the Account Server:");
         t_maxNumberOfAccountConnections = new JTextField( ""+config.getMaxNumberOfAccountConnections() );
         mainPanel.add( label10 );
         mainPanel.add( t_maxNumberOfAccountConnections );
//         mainPanel.add( new JLabel("") );

      // maxNumberOfGatewayConnections
         JLabel label11 = new JLabel("Maximum Number of Connections on the Gateway Server:");
         t_maxNumberOfGatewayConnections = new JTextField( ""+config.getMaxNumberOfGatewayConnections() );
         mainPanel.add( label11 );
         mainPanel.add( t_maxNumberOfGatewayConnections );
//         mainPanel.add( new JLabel("") );


      // *** Button Panel

         JPanel buttonPanel = new JPanel(new GridLayout(1,2,5,5));

         b_save = new JButton("Save Server Config");

         buttonPanel.add(b_save);

          b_save.addActionListener(new ActionListener()
           {
              public void actionPerformed (ActionEvent e)
              {
                // we save the config
                   config.setServerName( t_serverName.getText() );
                   config.setDescription( t_description.getText() );
                   config.setLocation( t_location.getText() );
                   config.setAdminEmail( t_adminEmail.getText() );


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
                   }
                   catch(Exception ee) {
                       Debug.signal( Debug.ERROR, this, ""+ee );
                       JOptionPane.showMessageDialog( ServerSetup.this, "Failed to save : bad number format !",
                                                      "Error", JOptionPane.ERROR_MESSAGE);
                       return;
                   }

                   if( !pm.saveServerConfig(config) )
                       JOptionPane.showMessageDialog( ServerSetup.this, "Failed to save : server.cfg file not found",
                                                      "Error", JOptionPane.ERROR_MESSAGE);
                   else
                       JOptionPane.showMessageDialog( ServerSetup.this, "Config saved: "+config.toString(),
                                                      "Success", JOptionPane.INFORMATION_MESSAGE);
              }
           });

         b_exit = new JButton("Exit Setup");

         buttonPanel.add(b_exit);

          b_exit.addActionListener(new ActionListener()
           {
              public void actionPerformed (ActionEvent e)
              {
                 System.exit(1);
              }
           });

      // *** We add the MainPanel to the Frame
         JScrollPane scrollPane =  new JScrollPane( mainPanel );
         scrollPane.setPreferredSize( new Dimension( 400, 400 ) );
         
         getContentPane().add( scrollPane, BorderLayout.CENTER );

         getContentPane().add( new JLabel( "Edit Your Server Setup and Click Save !" ), BorderLayout.NORTH );

         getContentPane().add( buttonPanel, BorderLayout.SOUTH );

         pack();
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